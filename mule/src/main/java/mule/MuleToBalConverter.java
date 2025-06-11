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
package mule;

import common.BallerinaModel.TypeDesc.RecordTypeDesc;
import common.BallerinaModel.TypeDesc.RecordTypeDesc.RecordField;
import common.CodeGenerator;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import mule.reader.MuleXMLNavigator;
import mule.reader.MuleXMLNavigator.MuleElement;
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
import static common.BallerinaModel.Function;
import static common.BallerinaModel.Import;
import static common.BallerinaModel.Listener;
import static common.BallerinaModel.ListenerType;
import static common.BallerinaModel.ModuleTypeDef;
import static common.BallerinaModel.ModuleVar;
import static common.BallerinaModel.Parameter;
import static common.BallerinaModel.Resource;
import static common.BallerinaModel.Service;
import static common.BallerinaModel.Statement;
import static common.BallerinaModel.TextDocument;
import static common.BallerinaModel.TypeDesc;
import static mule.Constants.BAL_ANYDATA_TYPE;
import static mule.Constants.BAL_ERROR_TYPE;
import static common.ConversionUtils.exprFrom;
import static mule.ConversionUtils.getAttrVal;
import static common.ConversionUtils.stmtFrom;
import static common.ConversionUtils.typeFrom;
import static mule.ConversionUtils.getBallerinaAbsolutePath;
import static mule.ConversionUtils.getBallerinaResourcePath;
import static mule.ConversionUtils.getAttrValInt;
import static mule.ConversionUtils.insertLeadingSlash;
import static mule.converter.MuleConfigConverter.convertTopLevelMuleBlocks;
import static mule.converter.MuleConfigConverter.getCatchExceptionBody;
import static mule.converter.MuleConfigConverter.getChoiceExceptionBody;
import static mule.reader.MuleConfigReader.readMuleConfigFromRoot;
import static mule.model.MuleModel.CatchExceptionStrategy;
import static mule.model.MuleModel.ChoiceExceptionStrategy;
import static mule.model.MuleModel.DbMSQLConfig;
import static mule.model.MuleModel.DbOracleConfig;
import static mule.model.MuleModel.DbTemplateQuery;
import static mule.model.MuleModel.Flow;
import static mule.model.MuleModel.HTTPListenerConfig;
import static mule.model.MuleModel.HttpListener;
import static mule.model.MuleModel.Kind;
import static mule.model.MuleModel.MuleRecord;
import static mule.model.MuleModel.SubFlow;
import static mule.model.MuleModel.VMInboundEndpoint;
import static mule.model.MuleModel.UnsupportedBlock;

public class MuleToBalConverter {

    public static SyntaxTree convertStandaloneXMLFileToBallerina(String xmlFilePath) {
        Context ctx = new Context();
        ctx.startStandaloneFile(xmlFilePath);
        MuleXMLNavigator muleXMLNavigator = new MuleXMLNavigator(ctx.migrationMetrics);
        return convertXMLFileToBallerina(muleXMLNavigator, xmlFilePath, ctx);
    }

    public static SyntaxTree convertProjectXMLFileToBallerina(MuleXMLNavigator muleXMLNavigator, Context ctx,
                                                              String xmlFilePath) {
        return convertXMLFileToBallerina(muleXMLNavigator, xmlFilePath, ctx);
    }

    private static SyntaxTree convertXMLFileToBallerina(MuleXMLNavigator muleXMLNavigator, String xmlFilePath,
                                                        Context ctx) {
        TextDocument textDocument = getTextDocument(muleXMLNavigator, ctx, xmlFilePath);
        return new CodeGenerator(textDocument).generateSyntaxTree();
    }

    private static TextDocument getTextDocument(MuleXMLNavigator muleXMLNavigator, Context ctx, String xmlFilePath) {
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

        return generateTextDocument(ctx, flows, subFlows);
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

    private static TextDocument generateTextDocument(Context ctx, List<Flow> flows, List<SubFlow> subFlows) {
        List<Service> services = new ArrayList<>();
        Set<Function> functions = new HashSet<>();
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
            listeners.add(new Listener(ListenerType.HTTP, httpListenerConfig.name(),
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

        for (DbTemplateQuery dbTemplateQuery : ctx.currentFileCtx.configs.dbTemplateQueries.values()) {
            var balExpr = exprFrom(String.format("`%s`", dbTemplateQuery.parameterizedQuery()));
            moduleVars.add(new ModuleVar(dbTemplateQuery.name(), typeFrom(Constants.SQL_PARAMETERIZED_QUERY_TYPE),
                    balExpr));
        }

        moduleVars.addAll(ctx.currentFileCtx.balConstructs.moduleVars.values());

        // Global comments at the end of file
        List<String> comments = new ArrayList<>();
        for (UnsupportedBlock unsupportedBlock : ctx.currentFileCtx.configs.unsupportedBlocks) {
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
        return createTextDocument("internal", new ArrayList<>(ctx.currentFileCtx.balConstructs.imports), typeDefs,
                orderedModuleVars, listeners, services, functions.stream().toList(), comments);
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
        String muleBasePath = insertLeadingSlash(
                ctx.projectCtx.getHttpListenerConfig(httpListener.configRef()).basePath());
        String basePath = getBallerinaAbsolutePath(muleBasePath);

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

        return new Service(basePath, listenerRef, resources);
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
                                                  List<ModuleTypeDef> moduleTypeDefs,
                                                    List<ModuleVar> moduleVars, List<Listener> listeners,
                                                    List<Service> services, List<Function> functions,
                                                    List<String> comments) {
        return new TextDocument(docName, imports, moduleTypeDefs, moduleVars, listeners,
                services, functions, comments);
    }
}
