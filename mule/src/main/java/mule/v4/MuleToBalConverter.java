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
package mule.v4;

import common.BallerinaModel.Statement.BallerinaStatement;
import common.BallerinaModel.TypeDesc.RecordTypeDesc;
import common.BallerinaModel.TypeDesc.RecordTypeDesc.RecordField;
import common.CodeGenerator;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import mule.v4.converter.ScriptConversionException;
import mule.v4.model.MuleModel.AnypointMqSubscriber;
import mule.v4.model.MuleModel.ApiKitConfig;
import mule.v4.model.MuleModel.DbConfig;
import mule.v4.model.MuleModel.DbGenericConnection;
import mule.v4.model.MuleModel.Scheduler;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static common.BallerinaModel.BlockFunctionBody;
import static common.BallerinaModel.ClassDef;
import static common.BallerinaModel.Expression;
import static common.BallerinaModel.Function;
import static common.BallerinaModel.Import;
import static common.BallerinaModel.Listener;
import static common.BallerinaModel.Remote;
import static common.BallerinaModel.ModuleTypeDef;
import static common.BallerinaModel.ModuleVar;
import static common.BallerinaModel.Parameter;
import static common.BallerinaModel.Resource;
import static common.BallerinaModel.Service;
import static common.BallerinaModel.Statement;
import static common.BallerinaModel.TextDocument;
import static common.BallerinaModel.TypeDesc;
import static common.ConversionUtils.escapeIdentifier;
import static common.ConversionUtils.exprFrom;
import static common.ConversionUtils.stmtFrom;
import static common.ConversionUtils.typeFrom;
import static mule.v4.Constants.BAL_ANYDATA_TYPE;
import static mule.v4.ConversionUtils.getAttrVal;
import static mule.v4.ConversionUtils.getAttrValInt;
import static mule.v4.ConversionUtils.getBallerinaAbsolutePath;
import static mule.v4.ConversionUtils.getBallerinaResourcePath;
import static mule.v4.ConversionUtils.insertLeadingSlash;
import static mule.v4.converter.MuleConfigConverter.convertErrorHandlerRecords;
import static mule.v4.converter.MuleConfigConverter.convertTopLevelMuleBlocks;
import static mule.v4.model.MuleModel.DbConnection;
import static mule.v4.model.MuleModel.DbMySqlConnection;
import static mule.v4.model.MuleModel.DbOracleConnection;
import static mule.v4.model.MuleModel.ErrorHandler;
import static mule.v4.model.MuleModel.Flow;
import static mule.v4.model.MuleModel.GlobalProperty;
import static mule.v4.model.MuleModel.HTTPListenerConfig;
import static mule.v4.model.MuleModel.HttpListener;
import static mule.v4.model.MuleModel.Kind;
import static mule.v4.model.MuleModel.MuleRecord;
import static mule.v4.model.MuleModel.SubFlow;
import static mule.v4.model.MuleModel.UnsupportedBlock;
import static mule.v4.model.MuleModel.VMListener;

public class MuleToBalConverter {

    public static SyntaxTree convertStandaloneXMLFileToBallerina(String xmlFilePath, mule.common.MuleLogger logger) {
        Context ctx = new Context(List.of(Path.of(xmlFilePath).toFile()), List.of(), logger);
        ctx.parseAllFiles();
        TextDocument txtDoc = ctx.codeGen().getFirst();
        return new CodeGenerator(txtDoc).generateSyntaxTree();
    }

    public static TextDocument generateTextDocument(Context ctx, String balFileName,
            List<Flow> flows, List<SubFlow> subFlows)
            throws ScriptConversionException {
        List<Service> services = new ArrayList<>();
        Set<Function> functions = new HashSet<>();
        List<ClassDef> classDefs = new ArrayList<>();
        List<Flow> privateFlows = new ArrayList<>();

        flows.stream()
                .sorted(Comparator.comparing(flow -> {
                    Optional<MuleRecord> source = flow.source();
                    return !(source.isPresent() && source.get() instanceof HttpListener);
                }))
                .forEachOrdered(flow -> {
                    Optional<MuleRecord> source = flow.source();
                    if (source.isEmpty()) {
                        privateFlows.add(flow);
                        return;
                    }

                    MuleRecord src = source.get();
                    try {
                        switch (src) {
                            case VMListener vmListener -> genVMListenerSource(ctx, flow, vmListener, functions);
                            case Scheduler scheduler -> genSchedulerSource(ctx, flow, scheduler, functions, classDefs);
                            case HttpListener httpListener -> ctx.projectCtx.lastHttpService = genHttpSource(ctx, flow,
                                    httpListener, services, functions);
                            case ApiKitConfig apiKit ->
                                genApiKitSource(ctx, flow, apiKit, services, ctx.projectCtx.lastHttpService);
                            case AnypointMqSubscriber mqSubscriber ->
                                genAnypointMqSource(ctx, flow, mqSubscriber, services);
                            default ->
                                throw new IllegalStateException("Unsupported source kind: %s".formatted(src.kind()));
                        }
                    } catch (ScriptConversionException e) {
                        throw new RuntimeException(e);
                    }
                });

        List<Service> normalizeServices = normalizeServices(services);

        // Create functions for private flows
        genBalFuncsFromPrivateFlows(ctx, privateFlows, functions);

        // Create functions for sub-flows
        genBalFuncsFromSubFlows(ctx, subFlows, functions);
        functions.addAll(ctx.currentFileCtx.balConstructs.functions);
        functions.addAll(ctx.currentFileCtx.balConstructs.commonFunctions.values());

        // Create functions for global exception strategies
        for (ErrorHandler errorHandler : ctx.currentFileCtx.configs.globalErrorHandlers) {
            genBalFuncForGlobalErrorHandler(ctx, errorHandler, functions);
            functions.addAll(ctx.currentFileCtx.balConstructs.functions); // TODO: this is a  hack.
        }

        // Add global listeners
        List<Listener> listeners = new ArrayList<>();
        for (HTTPListenerConfig httpListenerConfig : ctx.currentFileCtx.configs.httpListenerConfigs.values()) {
            listeners.add(new Listener.HTTPListener(ConversionUtils.convertToBalIdentifier(httpListenerConfig.name()),
                    getAttrValInt(ctx, httpListenerConfig.port()), httpListenerConfig.host()));
        }

        // Add module vars
        List<ModuleVar> moduleVars = new ArrayList<>();
        for (DbConfig dbConfig : ctx.currentFileCtx.configs.dbConfigs.values()) {
            DbConnection dbConnection = dbConfig.dbConnection();
            TypeDesc dbClientType;
            Expression balExpr;
            if (dbConnection.kind() == Kind.DB_MYSQL_CONNECTION) {
                DbMySqlConnection con = (DbMySqlConnection) dbConnection;
                dbClientType = typeFrom(Constants.MYSQL_CLIENT_TYPE);
                balExpr = exprFrom("check new (%s, %s, %s, %s, %s)".formatted(
                        getAttrVal(ctx, con.host()), getAttrVal(ctx, con.user()),
                        getAttrVal(ctx, con.password()), getAttrVal(ctx, con.database()),
                        getAttrValInt(ctx, con.port())));
            } else if (dbConnection.kind() == Kind.DB_ORACLE_CONNECTION) {
                DbOracleConnection con = (DbOracleConnection) dbConnection;
                dbClientType = typeFrom(Constants.ORACLEDB_CLIENT_TYPE);
                String database = con.instance().isEmpty() ? con.serviceName() : con.instance();
                String port = con.port().isEmpty() ? "1521" : con.port();
                balExpr = exprFrom(String.format("check new (%s, %s, %s, %s, %s)",
                        getAttrVal(ctx, con.host()), getAttrVal(ctx, con.user()),
                        getAttrVal(ctx, con.password()), getAttrVal(ctx, database),
                        getAttrValInt(ctx, port)));
            } else if (dbConnection.kind() == Kind.DB_GENERIC_CONNECTION) {
                DbGenericConnection con = (DbGenericConnection) dbConnection;
                dbClientType = typeFrom(Constants.JDBC_CLIENT_TYPE);
                String url = getAttrVal(ctx, con.url());
                JavaDependencies javaDependencies = determineJdbcDependencyFromUrl(url);
                ctx.projectCtx.addJavaDependency(javaDependencies);
                balExpr = exprFrom(String.format("check new (%s, %s, %s)",
                        url, getAttrVal(ctx, con.user()), getAttrVal(ctx, con.password())));
            } else {
                throw new IllegalStateException("Unsupported DB connection type: " + dbConnection.kind());
            }

            moduleVars
                    .add(new ModuleVar(ConversionUtils.convertToBalIdentifier(dbConfig.name()), dbClientType, balExpr));
        }

        moduleVars.addAll(ctx.currentFileCtx.balConstructs.moduleVars.values());

        // Global comments at the end of file
        List<String> comments = new ArrayList<>();
        for (UnsupportedBlock unsupportedBlock : ctx.currentFileCtx.configs.unsupportedBlocks) {
            String comment = ConversionUtils.convertToUnsupportedTODO(ctx, unsupportedBlock);
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

        for (GlobalProperty globalProperty : ctx.currentFileCtx.configs.globalProperties) {
            String configVarName = globalProperty.name().replace('.', '_');
            ConversionUtils.addConfigVarEntry(ctx, configVarName, globalProperty.value());
        }

        ArrayList<ModuleVar> orderedModuleVars = new ArrayList<>(
                ctx.getCurrentFileConfigurableVars()
        );
        orderedModuleVars.addAll(moduleVars);
        return createTextDocument(balFileName + ".bal", new ArrayList<>(ctx.currentFileCtx.balConstructs.imports),
                typeDefs, orderedModuleVars, listeners, normalizeServices, classDefs, functions.stream().toList(),
                comments);
    }

    private static void genApiKitSource(Context ctx, Flow flow, ApiKitConfig apiKit, Collection<Service> services,
                                        Service lastHttpService) {
        // TODO: Common with httpSource refactor
        ctx.projectCtx.attributes.put(Constants.HTTP_REQUEST_REF, Constants.HTTP_REQUEST_TYPE);
        ctx.projectCtx.attributes.put(Constants.HTTP_RESPONSE_REF, Constants.HTTP_RESPONSE_TYPE);
        ctx.projectCtx.attributes.put(Constants.URI_PARAMS_REF, "map<string>");

        if (lastHttpService == null) {
            throw new IllegalStateException("API Kit flow %s requires an HTTP listener to be processed first"
                    .formatted(flow.name()));
        }

        ApiKitConfig.HTTPResourceData resourceData = apiKit.resourcePathData(flow);
        List<String> pathParams = resourceData.pathParams();
        String apiKitResourcePath = resourceData.resourcePath();
        String resourceMethod = resourceData.method();

        // Get paths: serviceBasePath + apiKitBasePath + resourcePath
        String httpBasePath = lastHttpService.basePath();
        String apiKitBasePath = ctx.getApiKitBasePath(apiKit);
        String combinedResourcePath = concatenatePaths(
                concatenatePaths(httpBasePath, apiKitBasePath),
                apiKitResourcePath);

        // Store paths in context for apikit:router redirect logic
        ctx.currentServiceBasePath = httpBasePath;
        ctx.currentApiKitBasePath = apiKitBasePath;
        ctx.currentResourcePath = combinedResourcePath;
        HTTPListenerConfig listenerConfig = ctx.getDefaultHttpListenerConfig();
        ctx.currentListenerPort = listenerConfig.port();

        List<Parameter> queryPrams = new ArrayList<>();
        queryPrams.add(new Parameter(Constants.HTTP_REQUEST_REF, typeFrom(Constants.HTTP_REQUEST_TYPE)));

        List<Statement> bodyStmts = new ArrayList<>();
        String attributesInitValue = getAttributesInitValue(ctx, pathParams);
        bodyStmts.add(stmtFrom("Context %s = {%s: %s};".formatted(Constants.CONTEXT_REFERENCE,
                Constants.ATTRIBUTES_REF, attributesInitValue)));

        List<Statement> bodyCoreStmts = convertTopLevelMuleBlocks(ctx, flow.flowBlocks());
        bodyStmts.addAll(bodyCoreStmts);

        // Add return statement
        bodyStmts.add(stmtFrom("\n\n%s.%s.setPayload(%s.payload);".formatted(Constants.ATTRIBUTES_FIELD_ACCESS,
                Constants.HTTP_RESPONSE_REF, Constants.CONTEXT_REFERENCE)));
        bodyStmts.add(stmtFrom("return %s.%s;".formatted(Constants.ATTRIBUTES_FIELD_ACCESS,
                Constants.HTTP_RESPONSE_REF)));

        // Add service resources
        TypeDesc returnType = typeFrom(Constants.HTTP_RESOURCE_RETURN_TYPE_DEFAULT);
        ctx.currentFileCtx.balConstructs.imports.add(Constants.HTTP_MODULE_IMPORT);

        Resource resource = new Resource(resourceMethod, combinedResourcePath, queryPrams, Optional.of(returnType),
                bodyStmts);
        lastHttpService.resources().add(resource);
    }

    private static void genAnypointMqSource(Context ctx, Flow flow, AnypointMqSubscriber mqSubscriber,
                                            Collection<Service> services) {
        ctx.projectCtx.attributes.put(Constants.URI_PARAMS_REF, "map<string>");

        // Add JMS import
        ctx.currentFileCtx.balConstructs.imports.add(new Import(Constants.ORG_BALLERINAX, Constants.MODULE_JMS));

        // Add configurable variable for JMS provider URL (null value generates "?")
        String jmsProviderUrlVar = "JMS_PROVIDER_URL";
        ConversionUtils.addConfigVarEntry(ctx, jmsProviderUrlVar, null);

        String serviceName = escapeIdentifier(mqSubscriber.configRef());

        // Create listener expression with hardcoded JMS configuration
        String listenerExpr = """
                new jms:Listener(
                    connectionConfig = {
                        initialContextFactory: "org.apache.activemq.jndi.ActiveMQInitialContextFactory",
                        providerUrl: JMS_PROVIDER_URL
                    }
                )""";

        // Convert flow blocks to statements
        List<Statement> bodyStmts = convertTopLevelMuleBlocks(ctx, flow.flowBlocks());

        // Create remote function for onMessage
        List<Parameter> params = new ArrayList<>();
        params.add(new Parameter("message", typeFrom(Constants.JMS_MESSAGE_TYPE)));

        Function onMessageFunction = new Function("onMessage", params, bodyStmts);
        Remote remoteFunction = new Remote(onMessageFunction);

        // Create service with remote function
        Service service = new Service(serviceName, List.of(listenerExpr), Optional.empty(),
                List.of(), List.of(), List.of(), List.of(remoteFunction), Optional.empty());
        services.add(service);
    }

    private static String concatenatePaths(String basePath, String resourcePath) {
        // Normalize paths by removing trailing slashes from basePath and leading slashes from resourcePath
        String normalizedBasePath = basePath.endsWith("/") ? basePath.substring(0, basePath.length() - 1) : basePath;
        String normalizedResourcePath = resourcePath.startsWith("/") ? resourcePath : "/" + resourcePath;

        // Handle root basePath case
        if (normalizedBasePath.equals("/") || normalizedBasePath.isEmpty()) {
            return normalizedResourcePath;
        }

        String normaizedPath = normalizedBasePath + normalizedResourcePath;
        while (normaizedPath.startsWith("/")) {
            normaizedPath = normaizedPath.substring(1);
        }

        return normaizedPath;
    }

    private static List<Service> normalizeServices(List<Service> services) {
        Map<String, Service> serviceMap = new LinkedHashMap<>();

        for (Service service : services) {
            String key = service.basePath() + "|" + service.listenerRefs();
            serviceMap.merge(key, service, (existing, current) -> {
                existing.resources().addAll(current.resources());
                return existing;
            });
        }

        return new ArrayList<>(serviceMap.values());
    }

    private static void genVMListenerSource(Context ctx, Flow flow, VMListener vmListener, Set<Function> functions) {
        // TODO: Consider config ref usage
        String queueName = vmListener.queueName();
        String funcName = ctx.projectCtx.vmQueueNameToBalFuncMap.get(queueName);
        if (funcName == null) {
            funcName = ConversionUtils.convertToBalIdentifier(flow.name());
            ctx.projectCtx.vmQueueNameToBalFuncMap.put(queueName, funcName);
        }
        genBalFunc(ctx, functions, funcName, flow.flowBlocks());
    }

    private static void genSchedulerSource(Context ctx, Flow flow, Scheduler scheduler, Set<Function> functions,
                                           List<ClassDef> classDefs) {
        String jobClassName = "Job";
        ClassDef classDef = genBalJobClass(ctx, jobClassName, flow.flowBlocks());
        classDefs.add(classDef);

        List<Statement> stmts = new ArrayList<>(2);
        if (!scheduler.startDelay().isEmpty()) {
            ctx.currentFileCtx.balConstructs.imports.add(new Import(Constants.ORG_BALLERINA, Constants.MODULE_RUNTIME));
            double startDelayInSeconds = convertToSeconds(scheduler.startDelay(), scheduler.timeUnit());
            BallerinaStatement stmt = stmtFrom("runtime:sleep(%s);".formatted(startDelayInSeconds));
            stmts.add(stmt);
        }

        // Convert time unit to seconds for Ballerina's task:IntervalTimer
        double intervalInSeconds = convertToSeconds(scheduler.frequency(), scheduler.timeUnit());
        BallerinaStatement stmt = stmtFrom("task:JobId id = check task:scheduleJobRecurByFrequency(new %s(), %s);"
                .formatted(jobClassName, intervalInSeconds));
        stmts.add(stmt);
        Function mainFunc = Function.publicFunction("main", List.of(), typeFrom("error?"), stmts);
        functions.add(mainFunc);
    }

    private static double convertToSeconds(String frequency, String timeUnit) {
        double freq = Double.parseDouble(frequency);
        return switch (timeUnit.toLowerCase()) {
            case "milliseconds", "millis" -> freq / 1000.0;
            case "seconds", "s" -> freq;
            case "minutes", "mins", "m" -> freq * 60.0;
            case "hours", "h" -> freq * 3600.0;
            case "days", "d" -> freq * 86400.0;
            default -> freq; // Default to seconds if unknown
        };
    }

    private static ClassDef genBalJobClass(Context ctx, String jobName, List<MuleRecord> flowBlocks) {
        List<Statement> bodyCoreStmts = convertTopLevelMuleBlocks(ctx, flowBlocks);
        Function executeFunc = Function.publicFunction("execute", List.of(), bodyCoreStmts);
        return new ClassDef(jobName, List.of(typeFrom("task:Job")), List.of(), List.of(executeFunc));
    }

    private static Service genHttpSource(Context ctx, Flow flow, HttpListener src, List<Service> services,
            Set<Function> functions)
            throws ScriptConversionException {
        ctx.projectCtx.attributes.put(Constants.HTTP_REQUEST_REF, Constants.HTTP_REQUEST_TYPE);
        ctx.projectCtx.attributes.put(Constants.HTTP_RESPONSE_REF, Constants.HTTP_RESPONSE_TYPE);
        ctx.projectCtx.attributes.put(Constants.URI_PARAMS_REF, "map<string>");

        // Create a service from the flow
        Service service = genBalService(ctx, src, flow.flowBlocks(), functions);
        services.add(service);
        return service;
    }

    public static List<ModuleTypeDef> createContextTypeDefns(Context ctx) {
        List<ModuleTypeDef> contextTypeDefns = new ArrayList<>();

        List<RecordField> contextRecFields = new ArrayList<>();
        contextRecFields.add(new RecordField(Constants.PAYLOAD_REF, BAL_ANYDATA_TYPE, exprFrom("()")));

        if (!ctx.projectCtx.vars.isEmpty()) {
            contextRecFields.add(new RecordField(Constants.VARS_REF, typeFrom(Constants.VARS_TYPE),
                    exprFrom("{}")));
            List<RecordField> varRecFields = new ArrayList<>();
            for (Map.Entry<String, String> entry : ctx.projectCtx.vars.entrySet()) {
                varRecFields.add(new RecordField(entry.getKey(), typeFrom(entry.getValue()), true));
            }

            RecordTypeDesc varsRecord = RecordTypeDesc.closedRecord(varRecFields);
            contextTypeDefns.add(new ModuleTypeDef(Constants.VARS_TYPE, varsRecord));
        }

        if (!ctx.projectCtx.attributes.isEmpty()) {
            contextRecFields.add(new RecordField(Constants.ATTRIBUTES_REF, typeFrom(Constants.ATTRIBUTES_TYPE),
                    false));
            List<RecordField> attributesRecordFields = new ArrayList<>();
            for (Map.Entry<String, String> entry : ctx.projectCtx.attributes.entrySet()) {
                String name = entry.getKey();
                String type = entry.getValue();
                RecordField attributesField;
                if (type.startsWith("map<") && type.endsWith(">")) {
                    attributesField = new RecordField(name, typeFrom(type), exprFrom("{}"));
                } else {
                    attributesField = new RecordField(name, typeFrom(type), false);
                }
                attributesRecordFields.add(attributesField);
            }
            RecordTypeDesc attributesRecord = RecordTypeDesc.closedRecord(attributesRecordFields);
            contextTypeDefns.add(new ModuleTypeDef(Constants.ATTRIBUTES_TYPE, attributesRecord));
        }

        RecordTypeDesc contextRecord = RecordTypeDesc.closedRecord(contextRecFields);
        contextTypeDefns.add(new ModuleTypeDef(Constants.CONTEXT_RECORD_TYPE, contextRecord));
        return contextTypeDefns;
    }

    private static void genBalFuncForGlobalErrorHandler(Context ctx, ErrorHandler errorHandler,
                                                        Set<Function> functions) {
        String name = errorHandler.name();
        String methodName = errorHandler.name().isEmpty() ? "errorHandler" :
                ConversionUtils.convertToBalIdentifier(name); // Ideally field will not be empty

        List<Parameter> parameters = new ArrayList<>();
        parameters.add(Constants.CONTEXT_FUNC_PARAM);
        parameters.add(new Parameter(Constants.ON_FAIL_ERROR_VAR_REF, Constants.BAL_ERROR_TYPE));

        List<Statement> body = convertErrorHandlerRecords(ctx, errorHandler.errorHandlers());
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
            Set<Function> functions)
            throws ScriptConversionException {
        List<String> pathParams = new ArrayList<>();
        String resourcePath = getBallerinaResourcePath(ctx, httpListener.resourcePath(), pathParams);
        String[] resourceMethodNames = httpListener.allowedMethods();
        String listenerRef = ConversionUtils.convertToBalIdentifier(httpListener.configRef());
        HTTPListenerConfig listenerConfig = ctx.projectCtx.getHttpListenerConfig(httpListener.configRef());
        String muleBasePath = insertLeadingSlash(listenerConfig.basePath());
        String basePath = getBallerinaAbsolutePath(muleBasePath);

        // Add services
        List<Parameter> queryPrams = new ArrayList<>();
        queryPrams.add(new Parameter(Constants.HTTP_REQUEST_REF, typeFrom(Constants.HTTP_REQUEST_TYPE)));

        List<Statement> bodyStmts = new ArrayList<>();
        String attributesInitValue = getAttributesInitValue(ctx, pathParams);
        bodyStmts.add(stmtFrom("Context %s = {%s: %s};".formatted(Constants.CONTEXT_REFERENCE,
                Constants.ATTRIBUTES_REF, attributesInitValue)));

        // Set context for apikit-router comment generation
        ctx.currentServiceBasePath = basePath;
        ctx.currentResourcePath = resourcePath;
        ctx.currentListenerPort = listenerConfig.port();

        List<Statement> bodyCoreStmts = convertTopLevelMuleBlocks(ctx, flowBlocks);
        bodyStmts.addAll(bodyCoreStmts);

        // Add return statement
        bodyStmts.add(stmtFrom("\n\n%s.%s.setPayload(%s.payload);".formatted(Constants.ATTRIBUTES_FIELD_ACCESS,
                        Constants.HTTP_RESPONSE_REF, Constants.CONTEXT_REFERENCE)));
        bodyStmts.add(stmtFrom("return %s.%s;".formatted(Constants.ATTRIBUTES_FIELD_ACCESS,
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

        return new Service(basePath, listenerRef, resources);
    }

    private static String getAttributesInitValue(Context ctx, List<String> pathParams) {
        Map<String, String> attributesPropMap = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : ctx.projectCtx.attributes.entrySet()) {
            switch (entry.getKey()) {
                case Constants.HTTP_REQUEST_REF ->
                        attributesPropMap.put(Constants.HTTP_REQUEST_REF, Constants.HTTP_REQUEST_REF);
                case Constants.HTTP_RESPONSE_REF -> attributesPropMap.put(Constants.HTTP_RESPONSE_REF, "new");
                case Constants.URI_PARAMS_REF -> {
                    if (!pathParams.isEmpty()) {
                        String pathParamValue = String.join(",", pathParams);
                        attributesPropMap.put(Constants.URI_PARAMS_REF, "{%s}".formatted(pathParamValue));
                    }
                }
                default -> throw new IllegalStateException();
            }
        }

        List<String> fields = new ArrayList<>();
        attributesPropMap.forEach((key, value) -> {
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
                                                  List<ModuleTypeDef> moduleTypeDefs,
                                                  List<ModuleVar> moduleVars, List<Listener> listeners,
                                                  List<Service> services, List<ClassDef> classDefs,
                                                  List<Function> functions, List<String> comments) {
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
