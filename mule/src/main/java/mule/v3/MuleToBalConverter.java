/*
 *  Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com).
 *
 *  WSO2 LLC. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package mule.v3;

import common.BallerinaModel.Statement.BallerinaStatement;
import common.BallerinaModel.TypeDesc.RecordTypeDesc;
import common.BallerinaModel.TypeDesc.RecordTypeDesc.RecordField;
import common.CodeGenerator;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import mule.common.MuleXMLNavigator;
import mule.common.MuleXMLNavigator.MuleElement;
import mule.v3.converter.MuleConfigConverter;
import mule.v3.model.MuleModel;
import mule.v3.model.MuleXMLTag;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static common.BallerinaModel.BlockFunctionBody;
import static common.BallerinaModel.ClassDef;
import static common.BallerinaModel.Expression.BallerinaExpression;
import static common.BallerinaModel.Function;
import static common.BallerinaModel.Import;
import static common.BallerinaModel.Listener;
import static common.BallerinaModel.ModuleTypeDef;
import static common.BallerinaModel.ModuleVar;
import static common.BallerinaModel.Parameter;
import static common.BallerinaModel.Resource;
import static common.BallerinaModel.Service;
import static common.BallerinaModel.Statement;
import static common.BallerinaModel.Statement.Comment;
import static common.BallerinaModel.TextDocument;
import static common.BallerinaModel.TypeDesc;
import static mule.v3.Constants.BAL_ANYDATA_TYPE;
import static mule.v3.Constants.BAL_ERROR_TYPE;
import static common.ConversionUtils.exprFrom;
import static mule.v3.ConversionUtils.getAttrVal;
import static common.ConversionUtils.stmtFrom;
import static common.ConversionUtils.typeFrom;
import static mule.v3.ConversionUtils.getBallerinaAbsolutePath;
import static mule.v3.ConversionUtils.getBallerinaResourcePath;
import static mule.v3.ConversionUtils.getAttrValInt;
import static mule.v3.ConversionUtils.insertLeadingSlash;
import static mule.v3.converter.MuleConfigConverter.convertTopLevelMuleBlocks;
import static mule.v3.converter.MuleConfigConverter.getCatchExceptionBody;
import static mule.v3.converter.MuleConfigConverter.getChoiceExceptionBody;
import static mule.v3.model.MuleModel.QuartzInboundEndpoint;
import static mule.v3.model.MuleModel.Poll;
import static mule.v3.reader.MuleConfigReader.readMuleConfigFromRoot;
import static mule.v3.model.MuleModel.CatchExceptionStrategy;
import static mule.v3.model.MuleModel.ChoiceExceptionStrategy;
import static mule.v3.model.MuleModel.DbMSQLConfig;
import static mule.v3.model.MuleModel.DbOracleConfig;
import static mule.v3.model.MuleModel.DbGenericConfig;
import static mule.v3.model.MuleModel.DbTemplateQuery;
import static mule.v3.model.MuleModel.Flow;
import static mule.v3.model.MuleModel.HTTPListenerConfig;
import static mule.v3.model.MuleModel.HttpListener;
import static mule.v3.model.MuleModel.Kind;
import static mule.v3.model.MuleModel.MuleRecord;
import static mule.v3.model.MuleModel.SubFlow;
import static mule.v3.model.MuleModel.VMInboundEndpoint;
import static mule.v3.model.MuleModel.UnsupportedBlock;

public class MuleToBalConverter {

    public static SyntaxTree convertStandaloneXMLFileToBallerina(String xmlFilePath) {
        Context ctx = new Context();
        ctx.startStandaloneFile(xmlFilePath);
        MuleXMLNavigator muleXMLNavigator = new MuleXMLNavigator(ctx.migrationMetrics, MuleXMLTag::isCompatible);
        TextDocument txtDoc = convertXMLFileToBir(ctx, muleXMLNavigator, xmlFilePath, "internal");
        return new CodeGenerator(txtDoc).generateSyntaxTree();
    }

    public static TextDocument convertXMLFileToBir(Context ctx, MuleXMLNavigator muleXMLNavigator, String xmlFilePath,
                                                   String balFileName) {
        return getTextDocument(muleXMLNavigator, ctx, xmlFilePath, balFileName);
    }

    private static TextDocument getTextDocument(MuleXMLNavigator muleXMLNavigator, Context ctx, String xmlFilePath,
                                                String balFileName) {
        Element root;
        try {
            root = parseMuleXMLConfigurationFile(xmlFilePath);
        } catch (Exception e) {
            throw new RuntimeException("Error while parsing the mule XML configuration file: ", e);
        }

        MuleElement muleRootElement = muleXMLNavigator.createRootMuleElement(root);
        List<Flow> flows = new ArrayList<>();
        List<SubFlow> subFlows = new ArrayList<>();
        readMuleConfigFromRoot(ctx, muleRootElement, flows, subFlows);

        return generateTextDocument(ctx, balFileName, flows, subFlows);
    }

    private static Element parseMuleXMLConfigurationFile(String uri) throws ParserConfigurationException, SAXException,
            IOException {
        // Load the Mule XML file
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(uri);

        // Normalize the XML structure
        document.getDocumentElement().normalize();

        return document.getDocumentElement();
    }

    private static TextDocument generateTextDocument(Context ctx, String balFileName,
                                                     List<Flow> flows, List<SubFlow> subFlows) {
        List<Service> services = new ArrayList<>();
        Set<Function> functions = new HashSet<>();
        List<ClassDef> classDefs = new ArrayList<>();
        List<Flow> privateFlows = new ArrayList<>();

        for (Flow flow : flows) {
            Optional<MuleRecord> source = flow.source();
            if (source.isEmpty()) {
                privateFlows.add(flow);
                continue;
            }

            MuleRecord src = source.get();
            if (src.kind() == Kind.VM_INBOUND_ENDPOINT) {
                genVMInboundEndpointSource(ctx, flow, (VMInboundEndpoint) src, functions);
            } else if (src.kind() == Kind.QUARTZ_INBOUND_ENDPOINT) {
                genSchedulerSource(ctx, flow, (QuartzInboundEndpoint) src, functions, classDefs);
            } else if (src.kind() == Kind.POLL) {
                genPollSource(ctx, flow, (Poll) src, functions, classDefs);
            } else {
                assert src.kind() == Kind.HTTP_LISTENER;
                genHttpSource(ctx, flow, (HttpListener) src, services, functions);
            }
        }

        // Create functions for private flows
        genBalFuncsFromPrivateFlows(ctx, privateFlows, functions);

        // Create functions for sub-flows
        genBalFuncsFromSubFlows(ctx, subFlows, functions);
        functions.addAll(ctx.currentFileCtx.balConstructs.functions);

        // Create functions for global exception strategies
        for (MuleRecord exceptionStrategy : ctx.currentFileCtx.configs.globalExceptionStrategies) {
            genBalFuncForGlobalExceptionStrategy(ctx, exceptionStrategy, functions);
        }

        // Add global listeners
        List<Listener> listeners = new ArrayList<>();
        for (HTTPListenerConfig httpListenerConfig : ctx.currentFileCtx.configs.httpListenerConfigs.values()) {
            listeners.add(new Listener.HTTPListener(httpListenerConfig.name(),
                    getAttrValInt(ctx, httpListenerConfig.port()), httpListenerConfig.host()));
        }

        // Add module vars
        List<ModuleVar> moduleVars = new ArrayList<>();

        for (DbMSQLConfig dbMSQLConfig : ctx.currentFileCtx.configs.dbMySQLConfigs.values()) {
            var balExpr = exprFrom("check new (%s, %s, %s, %s, %s)".formatted(
                    getAttrVal(ctx, dbMSQLConfig.host()), getAttrVal(ctx, dbMSQLConfig.user()),
                    getAttrVal(ctx, dbMSQLConfig.password()), getAttrVal(ctx, dbMSQLConfig.database()),
                    getAttrValInt(ctx, dbMSQLConfig.port())));
            moduleVars.add(new ModuleVar(dbMSQLConfig.name(), typeFrom(Constants.MYSQL_CLIENT_TYPE), balExpr));
        }

        for (DbOracleConfig dbOracleConfig : ctx.currentFileCtx.configs.dbOracleConfigs.values()) {
            var balExpr = exprFrom(String.format("check new (%s, %s, %s, %s, %s)",
                    getAttrVal(ctx, dbOracleConfig.host()), getAttrVal(ctx, dbOracleConfig.user()),
                    getAttrVal(ctx, dbOracleConfig.password()), getAttrVal(ctx, dbOracleConfig.instance()),
                    getAttrValInt(ctx, dbOracleConfig.port())));
            moduleVars.add(new ModuleVar(dbOracleConfig.name(), typeFrom(Constants.ORACLEDB_CLIENT_TYPE), balExpr));
        }

        for (DbGenericConfig dbGenericConfig : ctx.currentFileCtx.configs.dbGenericConfigs.values()) {
            String url = getAttrVal(ctx, dbGenericConfig.url());
            JavaDependencies javaDependencies = determineJdbcDependencyFromUrl(url);
            ctx.projectCtx.addJavaDependency(javaDependencies);
            BallerinaExpression balExpr;
            if (dbGenericConfig.user().isEmpty()) {
                balExpr = exprFrom(String.format("check new (%s)", url));
            } else {
                balExpr = exprFrom(String.format("check new (%s, %s, %s)",
                        url, getAttrVal(ctx, dbGenericConfig.user()),
                        getAttrVal(ctx, dbGenericConfig.password())));
            }
            moduleVars.add(new ModuleVar(dbGenericConfig.name(), typeFrom(Constants.JDBC_CLIENT_TYPE), balExpr));
        }

        for (DbTemplateQuery dbTemplateQuery : ctx.currentFileCtx.configs.dbTemplateQueries.values()) {
            var balExpr = exprFrom(String.format("`%s`", dbTemplateQuery.parameterizedQuery()));
            moduleVars.add(new ModuleVar(dbTemplateQuery.name(), typeFrom(Constants.SQL_PARAMETERIZED_QUERY_TYPE),
                    balExpr));
        }

        moduleVars.addAll(ctx.currentFileCtx.balConstructs.moduleVars.values());

        // Global comments at the end of file
        List<String> comments = new ArrayList<>();
        for (UnsupportedBlock unsupportedBlock : ctx.currentFileCtx.configs.unsupportedBlocks) {
            ctx.migrationMetrics.failedBlocks.add(unsupportedBlock.xmlBlock());
            String comment = ConversionUtils.wrapElementInUnsupportedBlockComment(unsupportedBlock.xmlBlock());
            comments.add(comment);
        }

        List<ModuleTypeDef> typeDefs;
        if (ctx.isStandaloneBalFile()) {
            List<ModuleTypeDef> contextTypeDefns = createContextTypeDefns(ctx);
            contextTypeDefns.addAll(ctx.currentFileCtx.balConstructs.typeDefs.values());
            typeDefs = contextTypeDefns;
        } else {
            typeDefs = ctx.currentFileCtx.balConstructs.typeDefs.values().stream().toList();
        }

        ArrayList<ModuleVar> orderedModuleVars = new ArrayList<>(ctx.currentFileCtx.configs.configurableVars.values());
        orderedModuleVars.addAll(moduleVars);
        return createTextDocument(balFileName + ".bal", new ArrayList<>(ctx.currentFileCtx.balConstructs.imports),
                typeDefs, orderedModuleVars, listeners, services, classDefs, functions.stream().toList(), comments);
    }

    private static void genVMInboundEndpointSource(Context ctx, Flow flow, VMInboundEndpoint vmInboundEndpoint,
                                                   Set<Function> functions) {
        String path = vmInboundEndpoint.path();
        String funcName = ctx.projectCtx.vmPathToBalFuncMap.get(path);
        if (funcName == null) {
            funcName = ConversionUtils.convertToBalIdentifier(flow.name());
            ctx.projectCtx.vmPathToBalFuncMap.put(path, funcName);
            genBalFunc(ctx, functions, funcName, flow.flowBlocks());
        } else {
            genBalFunc(ctx, functions, funcName, flow.flowBlocks());
        }
    }

    private static void genSchedulerSource(Context ctx, Flow flow, QuartzInboundEndpoint quartzEp,
                                           Set<Function> functions, List<ClassDef> classDefs) {
        String jobClassName = quartzEp.jobName().isEmpty() ? "Job" : quartzEp.jobName();
        ClassDef classDef = genBalJobClass(ctx, jobClassName, flow.flowBlocks());
        classDefs.add(classDef);

        List<Statement> stmts = new ArrayList<>();
        if (!quartzEp.cronExpression().isEmpty()) {
            // TODO: Cron expression conversion is complex and may need manual adjustment
            stmts.add(stmtFrom("// TODO: Convert cron expression to Ballerina task scheduling"));
            stmts.add(stmtFrom("// Original cron expression: " + quartzEp.cronExpression()));
        }

        if (!quartzEp.startDelay().isEmpty()) {
            ctx.currentFileCtx.balConstructs.imports.add(new Import(Constants.ORG_BALLERINA, Constants.MODULE_RUNTIME));
            double startDelayInSeconds = convertIntervalToSeconds(quartzEp.startDelay());
            BallerinaStatement stmt = stmtFrom("runtime:sleep(%s);".formatted(startDelayInSeconds));
            stmts.add(stmt);
        }

        // Convert time unit to seconds for Ballerina's task:IntervalTimer;
        double intervalInSeconds = convertIntervalToSeconds(quartzEp.repeatInterval());

        BallerinaStatement stmt = stmtFrom("task:JobId id = check task:scheduleJobRecurByFrequency(new %s(), %s);"
                        .formatted(jobClassName, intervalInSeconds));
        stmts.add(stmt);
        Function mainFunc = Function.publicFunction("main", List.of(), typeFrom("error?"), stmts);
        functions.add(mainFunc);
    }

    private static void genPollSource(Context ctx, Flow flow, Poll poll, Set<Function> functions,
                                      List<ClassDef> classDefs) {
        String jobClassName = "PollJob";
        ClassDef classDef = genBalJobClass(ctx, jobClassName, flow.flowBlocks());
        classDefs.add(classDef);

        // Convert poll frequency and time unit to seconds for Ballerina's task scheduling
        double intervalInSeconds = convertPollToSeconds(poll.frequency(), poll.timeUnit());

        List<Statement> stmts = new ArrayList<>();
        String startDelay = poll.startDelay();
        if (!startDelay.isEmpty()) {
            ctx.currentFileCtx.balConstructs.imports.add(new Import(Constants.ORG_BALLERINA, Constants.MODULE_RUNTIME));
            double startDelayInSeconds = convertPollToSeconds(startDelay, poll.timeUnit());
            BallerinaStatement stmt = stmtFrom("runtime:sleep(%s);".formatted(startDelayInSeconds));
            stmts.add(stmt);
        }

        BallerinaStatement stmt = stmtFrom("task:JobId id = check task:scheduleJobRecurByFrequency(new %s(), %s);"
                        .formatted(jobClassName, intervalInSeconds));
        stmts.add(stmt);
        Function mainFunc = Function.publicFunction("main", List.of(), typeFrom("error?"), stmts);
        functions.add(mainFunc);
    }

    private static double convertIntervalToSeconds(String interval) {
        if (interval.isEmpty()) {
            return 5.0; // Default 5 seconds
        }

        try {
            // Try to parse as milliseconds (common in Mule 3.x)
            long millis = Long.parseLong(interval);
            return millis / 1000.0;
        } catch (NumberFormatException e) {
            // If parsing fails, return default
            return 5.0;
        }
    }

    private static double convertPollToSeconds(String frequency, String timeUnit) {
        if (frequency.isEmpty()) {
            return 5.0; // Default 5 seconds
        }

        try {
            double freq = Double.parseDouble(frequency);
            return switch (timeUnit.toLowerCase()) {
                case "millisecond", "milliseconds" -> freq / 1000.0;
                case "second", "seconds" -> freq;
                case "minute", "minutes" -> freq * 60;
                case "hour", "hours" -> freq * 3600;
                default -> 5.0; // Unknown unit, fallback to default
            };
        } catch (NumberFormatException e) {
            // If parsing fails, return default
            return 5.0;
        }
    }

    private static ClassDef genBalJobClass(mule.v3.Context ctx, String jobName, List<MuleModel.MuleRecord> flowBlocks) {
        List<Statement> bodyCoreStmts = MuleConfigConverter.convertTopLevelMuleBlocks(ctx, flowBlocks);
        Function executeFunc = Function.publicFunction("execute", List.of(), bodyCoreStmts);
        return new ClassDef(jobName, List.of(typeFrom("task:Job")), List.of(), List.of(executeFunc));
    }

    private static void genHttpSource(Context ctx, Flow flow, HttpListener src, List<Service> services,
                                      Set<Function> functions) {
        ctx.projectCtx.inboundProperties.put(Constants.HTTP_REQUEST_REF, Constants.HTTP_REQUEST_TYPE);
        ctx.projectCtx.inboundProperties.put(Constants.HTTP_RESPONSE_REF, Constants.HTTP_RESPONSE_TYPE);
        ctx.projectCtx.inboundProperties.put(Constants.URI_PARAMS_REF, "map<string>");

        // Create a service from the flow
        Service service = genBalService(ctx, src, flow.flowBlocks(), functions);
        services.add(service);
    }

    public static List<ModuleTypeDef> createContextTypeDefns(Context ctx) {
        List<ModuleTypeDef> contextTypeDefns = new ArrayList<>();

        List<RecordField> contextRecFields = new ArrayList<>();
        contextRecFields.add(new RecordField(Constants.PAYLOAD_REF, BAL_ANYDATA_TYPE, exprFrom("()")));

        if (!ctx.projectCtx.flowVars.isEmpty()) {
            contextRecFields.add(new RecordField(Constants.FLOW_VARS_REF, typeFrom(Constants.FLOW_VARS_TYPE),
                    exprFrom("{}")));
            List<RecordField> flowVarRecFields = new ArrayList<>();
            for (Map.Entry<String, String> entry : ctx.projectCtx.flowVars.entrySet()) {
                flowVarRecFields.add(new RecordField(entry.getKey(), typeFrom(entry.getValue()), true));
            }

            RecordTypeDesc flowVarsRecord = RecordTypeDesc.closedRecord(flowVarRecFields);
            contextTypeDefns.add(new ModuleTypeDef(Constants.FLOW_VARS_TYPE, flowVarsRecord));
        }

        if (!ctx.projectCtx.sessionVars.isEmpty()) {
            contextRecFields.add(new RecordField(Constants.SESSION_VARS_REF, typeFrom(Constants.SESSION_VARS_TYPE),
                    exprFrom("{}")));
            List<RecordField> sessionVarRecFields = new ArrayList<>();
            for (Map.Entry<String, String> entry : ctx.projectCtx.sessionVars.entrySet()) {
                sessionVarRecFields.add(new RecordField(entry.getKey(), typeFrom(entry.getValue()), true));
            }
            RecordTypeDesc sessionVarsRecord = RecordTypeDesc.closedRecord(sessionVarRecFields);
            contextTypeDefns.add(new ModuleTypeDef(Constants.SESSION_VARS_TYPE, sessionVarsRecord));
        }

        if (!ctx.projectCtx.inboundProperties.isEmpty()) {
            contextRecFields.add(new RecordField("inboundProperties", typeFrom(Constants.INBOUND_PROPERTIES_TYPE),
                    false));
            List<RecordField> inboundPropRecordFields = new ArrayList<>();
            for (Map.Entry<String, String> entry : ctx.projectCtx.inboundProperties.entrySet()) {
                String name = entry.getKey();
                String type = entry.getValue();
                RecordField inboundPropField;
                if (type.startsWith("map<") && type.endsWith(">")) {
                    inboundPropField = new RecordField(name, typeFrom(type), exprFrom("{}"));
                } else {
                    inboundPropField = new RecordField(name, typeFrom(type), false);
                }
                inboundPropRecordFields.add(inboundPropField);
            }
            RecordTypeDesc inboundPropertiesRecord = RecordTypeDesc.closedRecord(inboundPropRecordFields);
            contextTypeDefns.add(new ModuleTypeDef(Constants.INBOUND_PROPERTIES_TYPE, inboundPropertiesRecord));
        }

        RecordTypeDesc contextRecord = RecordTypeDesc.closedRecord(contextRecFields);
        contextTypeDefns.add(new ModuleTypeDef(Constants.CONTEXT_RECORD_TYPE, contextRecord));
        return contextTypeDefns;
    }

    private static void genBalFuncForGlobalExceptionStrategy(Context ctx, MuleRecord muleRecord,
                                                             Set<Function> functions) {
        List<Statement> body;
        String name;
        if (muleRecord instanceof CatchExceptionStrategy catchExceptionStrategy) {
            name = catchExceptionStrategy.name();
            body = getCatchExceptionBody(ctx, catchExceptionStrategy);
        } else if (muleRecord instanceof ChoiceExceptionStrategy choiceExceptionStrategy) {
            name = choiceExceptionStrategy.name();
            body = getChoiceExceptionBody(ctx, choiceExceptionStrategy);
        } else {
            throw new UnsupportedOperationException("exception strategy not supported");
        }

        String methodName = ConversionUtils.convertToBalIdentifier(name);
        List<Parameter> parameters = new ArrayList<>();
        parameters.add(Constants.CONTEXT_FUNC_PARAM);
        parameters.add(new Parameter("e", BAL_ERROR_TYPE));
        Function function = Function.publicFunction(methodName, parameters.stream().toList(), body);
        functions.add(function);
    }

    private static void genBalFuncsFromSubFlows(Context ctx, List<SubFlow> subFlows, Set<Function> functions) {
        for (SubFlow subFlow : subFlows) {
            genBalFuncForPrivateOrSubFlow(ctx, functions, subFlow.name(), subFlow.flowBlocks());
        }
    }

    private static void genBalFuncForPrivateOrSubFlow(Context ctx, Set<Function> functions, String flowName,
                                                      List<MuleRecord> flowBlocks) {
        List<Statement> body = convertTopLevelMuleBlocks(ctx, flowBlocks);

        String methodName = ConversionUtils.convertToBalIdentifier(flowName);
        Function function = Function.publicFunction(methodName, Constants.FUNC_PARAMS_WITH_CONTEXT, body);
        functions.add(function);
    }

    private static void genBalFunc(Context ctx, Set<Function> functions, String funcName, List<MuleRecord> flowBlocks) {
        List<Statement> body = convertTopLevelMuleBlocks(ctx, flowBlocks);
        Function function = Function.publicFunction(funcName, Constants.FUNC_PARAMS_WITH_CONTEXT, body);
        functions.add(function);
    }

    private static void genBalFuncsFromPrivateFlows(Context ctx, List<Flow> privateFlows, Set<Function> functions) {
        for (Flow privateFlow : privateFlows) {
            genBalFuncForPrivateOrSubFlow(ctx, functions, privateFlow.name(), privateFlow.flowBlocks());
        }
    }

    private static Service genBalService(Context ctx, HttpListener httpListener, List<MuleRecord> flowBlocks,
                                         Set<Function> functions) {
        List<String> pathParams = new ArrayList<>();
        String resourcePath = getBallerinaResourcePath(ctx, httpListener.resourcePath(), pathParams);
        String[] resourceMethodNames = httpListener.allowedMethods();
        String listenerRef = httpListener.configRef();
        HTTPListenerConfig httpListenerConfig = ctx.projectCtx.getHttpListenerConfig(httpListener.configRef());

        Comment comment = null;
        String basePath;
        if (httpListenerConfig == null) {
            basePath = "/";
            comment = new Comment("TODO: listener config ref '%s' not found. Using default base path '/'"
                    .formatted(listenerRef));
        } else {
            String muleBasePath = insertLeadingSlash(httpListenerConfig.basePath());
            basePath = getBallerinaAbsolutePath(muleBasePath);
        }

        // Add services
        List<Parameter> queryPrams = new ArrayList<>();
        queryPrams.add(new Parameter(Constants.HTTP_REQUEST_REF, typeFrom(Constants.HTTP_REQUEST_TYPE)));

        List<Statement> bodyStmts = new ArrayList<>();
        String inboundPropInitValue = getInboundPropInitValue(ctx, pathParams);
        bodyStmts.add(stmtFrom("Context %s = {%s: %s};".formatted(Constants.CONTEXT_REFERENCE,
                Constants.INBOUND_PROPERTIES_REF, inboundPropInitValue)));

        List<Statement> bodyCoreStmts = convertTopLevelMuleBlocks(ctx, flowBlocks);
        bodyStmts.addAll(bodyCoreStmts);

        // Add return statement
        bodyStmts.add(stmtFrom("\n\n%s.%s.setPayload(%s.payload);".formatted(Constants.INBOUND_PROPERTIES_FIELD_ACCESS,
                        Constants.HTTP_RESPONSE_REF, Constants.CONTEXT_REFERENCE)));
        bodyStmts.add(stmtFrom("return %s.%s;".formatted(Constants.INBOUND_PROPERTIES_FIELD_ACCESS,
                Constants.HTTP_RESPONSE_REF)));

        // Add service resources
        List<Resource> resources = new ArrayList<>();
        TypeDesc returnType = typeFrom(Constants.HTTP_RESOURCE_RETURN_TYPE_DEFAULT);
        ctx.currentFileCtx.balConstructs.imports.add(Constants.HTTP_MODULE_IMPORT);

        if (resourceMethodNames.length > 1) {
            // same logic is shared, thus extracting it to a function
            String invokeEndPointMethodName = String.format(Constants.FUNC_NAME_HTTP_ENDPOINT_TEMPLATE,
                    ctx.projectCtx.counters.invokeEndPointMethodCount++);
            List<Statement> body = Collections.singletonList(stmtFrom("return %s(%s);"
                    .formatted(invokeEndPointMethodName, Constants.HTTP_REQUEST_REF)));

            for (String resourceMethodName : resourceMethodNames) {
                resourceMethodName = resourceMethodName.toLowerCase();
                Resource resource = new Resource(resourceMethodName, resourcePath, queryPrams, Optional.of(returnType),
                        body);
                resources.add(resource);
            }

            // Add body as a top level function
            functions.add(new Function(Optional.of("public"), invokeEndPointMethodName, Collections.singletonList(
                    new Parameter(Constants.HTTP_REQUEST_REF, typeFrom(Constants.HTTP_REQUEST_TYPE))),
                    Optional.of(returnType), new BlockFunctionBody(bodyStmts)));
        } else if (resourceMethodNames.length == 1) {
            String resourceMethodName = resourceMethodNames[0].toLowerCase();
            Resource resource = new Resource(resourceMethodName, resourcePath, queryPrams, Optional.of(returnType),
                    bodyStmts);
            resources.add(resource);
        } else {
            throw new IllegalStateException();
        }

        return new Service(basePath, listenerRef, resources, comment);
    }

    private static String getInboundPropInitValue(Context ctx, List<String> pathParams) {
        Map<String, String> inboundPropMap = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : ctx.projectCtx.inboundProperties.entrySet()) {
            switch (entry.getKey()) {
                case Constants.HTTP_REQUEST_REF ->
                        inboundPropMap.put(Constants.HTTP_REQUEST_REF, Constants.HTTP_REQUEST_REF);
                case Constants.HTTP_RESPONSE_REF -> inboundPropMap.put(Constants.HTTP_RESPONSE_REF, "new");
                case Constants.URI_PARAMS_REF -> {
                    if (!pathParams.isEmpty()) {
                        String pathParamValue = String.join(",", pathParams);
                        inboundPropMap.put(Constants.URI_PARAMS_REF, "{%s}".formatted(pathParamValue));
                    }
                }
                default -> throw new IllegalStateException();
            }
        }

        List<String> fields = new ArrayList<>();
        inboundPropMap.forEach((key, value) -> {
            if (key.equals(value)) {
                fields.add(key);
            } else {
                fields.add("%s: %s".formatted(key, value));
            }
        });

        String recordBody = String.join(", ", fields);
        return String.format("{%s}", recordBody);
    }

    public static TextDocument createTextDocument(String docName, List<Import> imports,
                                                  List<ModuleTypeDef> moduleTypeDefs, List<ModuleVar> moduleVars,
                                                  List<Listener> listeners, List<Service> services,
                                                  List<ClassDef> classDefs, List<Function> functions,
                                                  List<String> comments) {
        return new TextDocument(docName, imports, moduleTypeDefs, moduleVars, listeners,
                services, classDefs, functions, comments);
    }

    private static JavaDependencies determineJdbcDependencyFromUrl(String dbUrl) {
        String url = dbUrl.trim();
        if (url.startsWith("jdbc:h2:")) {
            return JavaDependencies.JDBC_H2;
        } else if (url.startsWith("jdbc:mysql:")) {
            return JavaDependencies.JDBC_MYSQL;
        } else if (url.startsWith("jdbc:postgresql:")) {
            return JavaDependencies.JDBC_POSTGRESQL;
        } else if (url.startsWith("jdbc:oracle:")) {
            return JavaDependencies.JDBC_ORACLE;
        } else if (url.startsWith("jdbc:mariadb:")) {
            return JavaDependencies.JDBC_MARIADB;
        }
        // Default to H2 for unknown JDBC URLs
        return JavaDependencies.JDBC_H2;
    }

    public enum JavaDependencies {
        JDBC_H2("""
                [[platform.java17.dependency]]
                artifactId = "h2"
                version = "2.0.206"
                groupId = "com.h2database"
                """),
        JDBC_POSTGRESQL("""
                [[platform.java17.dependency]]
                artifactId = "postgresql"
                version = "42.7.2"
                groupId = "org.postgresql"
                """),
        JDBC_MYSQL("""
                [[platform.java17.dependency]]
                artifactId = "mysql-connector-java"
                version = "8.0.33"
                groupId = "mysql"
                """),
        JDBC_ORACLE("""
                [[platform.java17.dependency]]
                artifactId = "ojdbc8"
                version = "21.9.0.0"
                groupId = "com.oracle.database.jdbc"
                """),
        JDBC_MARIADB("""
                [[platform.java17.dependency]]
                artifactId = "mariadb-java-client"
                version = "3.1.4"
                groupId = "org.mariadb.jdbc"
                """);

        public final String dependencyParam;

        JavaDependencies(String dependencyParam) {
            this.dependencyParam = dependencyParam;
        }
    }
}
