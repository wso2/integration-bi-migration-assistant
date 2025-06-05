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

import common.BallerinaModel;
import common.BallerinaModel.TypeDesc.RecordTypeDesc;
import common.BallerinaModel.TypeDesc.RecordTypeDesc.RecordField;
import common.CodeGenerator;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import mule.MuleXMLNavigator.MuleElement;
import mule.dataweave.converter.DWConversionStats;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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
import static mule.MuleConfigConverter.convertTopLevelMuleBlocks;
import static mule.MuleConfigConverter.getCatchExceptionBody;
import static mule.MuleConfigConverter.getChoiceExceptionBody;
import static mule.MuleConfigReader.readMuleConfigFromRoot;
import static mule.MuleModel.CatchExceptionStrategy;
import static mule.MuleModel.ChoiceExceptionStrategy;
import static mule.MuleModel.DbMSQLConfig;
import static mule.MuleModel.DbOracleConfig;
import static mule.MuleModel.DbTemplateQuery;
import static mule.MuleModel.Flow;
import static mule.MuleModel.HTTPListenerConfig;
import static mule.MuleModel.HttpListener;
import static mule.MuleModel.Kind;
import static mule.MuleModel.HTTPRequestConfig;
import static mule.MuleModel.MuleRecord;
import static mule.MuleModel.SubFlow;
import static mule.MuleModel.VMInboundEndpoint;
import static mule.MuleModel.UnsupportedBlock;

public class MuleToBalConverter {

    public static class Data {
        public final SharedProjectData sharedProjectData;

        // Following are per bal file properties

        // Mule global elements
        HashMap<String, HTTPListenerConfig> globalHttpListenerConfigsMap = new LinkedHashMap<>();
        HashMap<String, HTTPRequestConfig> globalHttpRequestConfigsMap = new LinkedHashMap<>();
        HashMap<String, DbMSQLConfig> globalDbMySQLConfigsMap = new LinkedHashMap<>();
        HashMap<String, DbOracleConfig> globalDbOracleConfigsMap = new LinkedHashMap<>();
        HashMap<String, DbTemplateQuery> globalDbTemplateQueryMap = new LinkedHashMap<>();
        private final HashMap<String, ModuleVar> globalConfigVarMap = new LinkedHashMap<>();
        List<UnsupportedBlock> globalUnsupportedBlocks = new ArrayList<>();
        List<MuleRecord> globalExceptionStrategies = new ArrayList<>();

        // Ballerina global elements
        public HashSet<Import> imports = new LinkedHashSet<>();
        HashMap<String, ModuleTypeDef> typeDefMap = new LinkedHashMap<>();
        HashMap<String, ModuleVar> moduleVarMap = new LinkedHashMap<>();
        public List<Function> functions = new ArrayList<>();
        public List<String> utilFunctions = new ArrayList<>();

        public Data(SharedProjectData sharedProjectData) {
            this.sharedProjectData = sharedProjectData;
        }

        public void putConfigVarIfNotExists(String configVarName) {
            if (sharedProjectData.sharedConfigVarMap.containsKey(configVarName)) {
                return;
            }

            BallerinaModel.ModuleVar configVarDecl = new BallerinaModel.ModuleVar(configVarName, "string",
                    Optional.of(exprFrom("?")), false, true);
            sharedProjectData.sharedConfigVarMap.put(configVarName, configVarDecl);
            globalConfigVarMap.put(configVarName, configVarDecl);
        }
    }

    public static class SharedProjectData {
        boolean isStandaloneBalFile = false;
        HashMap<String, ModuleTypeDef> contextTypeDefMap = new LinkedHashMap<>();
        HashSet<Import> contextTypeDefImports = new LinkedHashSet<>();
        HashMap<String, HTTPListenerConfig> sharedHttpListenerConfigsMap = new LinkedHashMap<>();
        HashMap<String, HTTPRequestConfig> sharedHttpRequestConfigsMap = new LinkedHashMap<>();
        HashMap<String, DbMSQLConfig> sharedDbMySQLConfigsMap = new LinkedHashMap<>();
        HashMap<String, DbOracleConfig> sharedDbOracleConfigsMap = new LinkedHashMap<>();
        HashMap<String, DbTemplateQuery> sharedDbTemplateQueryMap = new LinkedHashMap<>();
        private final HashMap<String, ModuleVar> sharedConfigVarMap = new LinkedHashMap<>();
        HashMap<String, String> vmPathToBalFuncMap = new LinkedHashMap<>();

        // Internal variable/method count
        public int invokeEndPointMethodCount = 0;
        public int dwMethodCount = 0;
        public int dbQueryVarCount = 0;
        public int dbStreamVarCount = 0;
        public int dbSelectVarCount = 0;
        public int enricherFuncCount = 0;
        public int asyncFuncCount = 0;
        public int payloadVarCount = 0;
        public int clientResultVarCount = 0;
        public int vmReceiveFuncCount = 0;

        private final DWConversionStats dwConversionStats;

        public SharedProjectData(MuleXMLNavigator muleXMLNavigator) {
            this.dwConversionStats = muleXMLNavigator.getDwConversionStats();
        }

        LinkedHashSet<TypeAndNamePair> flowVars = new LinkedHashSet<>();
        LinkedHashSet<TypeAndNamePair> sessionVars = new LinkedHashSet<>();
        LinkedHashSet<TypeAndNamePair> inboundProperties = new LinkedHashSet<>();

        record TypeAndNamePair(String type, String name) {
        }

        boolean existingFlowVar(String name) {
            return existingVar(flowVars, name);
        }

        boolean existingSessionVar(String name) {
            return existingVar(sessionVars, name);
        }

        private boolean existingVar(LinkedHashSet<TypeAndNamePair> vars, String name) {
            for (TypeAndNamePair var : vars) {
                if (var.name.equals(name)) {
                    return true;
                }
            }
            return false;
        }

        public DWConversionStats getDwConversionStats() {
            return dwConversionStats;
        }
    }

    public static SyntaxTree convertStandaloneXMLFileToBallerina(String xmlFilePath) {
        MuleXMLNavigator muleXMLNavigator = new MuleXMLNavigator();
        SharedProjectData sharedProjectData = new SharedProjectData(muleXMLNavigator);
        sharedProjectData.isStandaloneBalFile = true;
        Data data = new Data(sharedProjectData);
        return convertXMLFileToBallerina(muleXMLNavigator, xmlFilePath, data);
    }

    public static SyntaxTree convertProjectXMLFileToBallerina(MuleXMLNavigator muleXMLNavigator,
            SharedProjectData sharedProjectData,
            String xmlFilePath) {
        MuleToBalConverter.Data data = new MuleToBalConverter.Data(sharedProjectData);
        SyntaxTree syntaxTree = convertXMLFileToBallerina(muleXMLNavigator, xmlFilePath, data);
        sharedProjectData.sharedHttpListenerConfigsMap.putAll(data.globalHttpListenerConfigsMap);
        sharedProjectData.sharedHttpRequestConfigsMap.putAll(data.globalHttpRequestConfigsMap);
        sharedProjectData.sharedDbMySQLConfigsMap.putAll(data.globalDbMySQLConfigsMap);
        sharedProjectData.sharedDbOracleConfigsMap.putAll(data.globalDbOracleConfigsMap);
        sharedProjectData.sharedDbTemplateQueryMap.putAll(data.globalDbTemplateQueryMap);
        return syntaxTree;
    }

    private static SyntaxTree convertXMLFileToBallerina(MuleXMLNavigator muleXMLNavigator, String xmlFilePath,
            Data data) {
        TextDocument textDocument = getTextDocument(muleXMLNavigator, data, xmlFilePath);
        return new CodeGenerator(textDocument).generateSyntaxTree();
    }

    private static TextDocument getTextDocument(MuleXMLNavigator muleXMLNavigator, Data data, String xmlFilePath) {
        Element root;
        try {
            root = parseMuleXMLConfigurationFile(xmlFilePath);
        } catch (Exception e) {
            throw new RuntimeException("Error while parsing the mule XML configuration file: ", e);
        }

        MuleElement muleRootElement = muleXMLNavigator.createRootMuleElement(root);
        List<Flow> flows = new ArrayList<>();
        List<SubFlow> subFlows = new ArrayList<>();
        readMuleConfigFromRoot(data, muleRootElement, flows, subFlows);

        return generateTextDocument(data, flows, subFlows);
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

    private static TextDocument generateTextDocument(Data data, List<Flow> flows, List<SubFlow> subFlows) {
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
                genVMInboundEndpointSource(data, flow, (VMInboundEndpoint) src, functions);
            } else {
                assert src.kind() == Kind.HTTP_LISTENER;
                genHttpSource(data, flow, (HttpListener) src, services, functions);
            }
        }

        // Create functions for private flows
        genBalFuncsFromPrivateFlows(data, privateFlows, functions);

        // Create functions for sub-flows
        genBalFuncsFromSubFlows(data, subFlows, functions);
        functions.addAll(data.functions);

        // Create functions for global exception strategies
        for (MuleRecord exceptionStrategy : data.globalExceptionStrategies) {
            genBalFuncForGlobalExceptionStrategy(data, exceptionStrategy, functions);
        }

        // Add global listeners
        List<Listener> listeners = new ArrayList<>();
        for (HTTPListenerConfig httpListenerConfig : data.globalHttpListenerConfigsMap.values()) {
            listeners.add(new Listener(ListenerType.HTTP, httpListenerConfig.name(),
                    getAttrValInt(data, httpListenerConfig.port()), httpListenerConfig.host()));
        }

        // Add module vars
        List<ModuleVar> moduleVars = new ArrayList<>();

        for (DbMSQLConfig dbMSQLConfig : data.globalDbMySQLConfigsMap.values()) {
            var balExpr = exprFrom("check new (%s, %s, %s, %s, %s)".formatted(
                    getAttrVal(data, dbMSQLConfig.host()), getAttrVal(data, dbMSQLConfig.user()),
                    getAttrVal(data, dbMSQLConfig.password()), getAttrVal(data, dbMSQLConfig.database()),
                    getAttrValInt(data, dbMSQLConfig.port())));
            moduleVars.add(new ModuleVar(dbMSQLConfig.name(), typeFrom(Constants.MYSQL_CLIENT_TYPE), balExpr));
        }

        for (DbOracleConfig dbOracleConfig : data.globalDbOracleConfigsMap.values()) {
            var balExpr = exprFrom(String.format("check new (%s, %s, %s, %s, %s)",
                    getAttrVal(data, dbOracleConfig.host()), getAttrVal(data, dbOracleConfig.user()),
                    getAttrVal(data, dbOracleConfig.password()), getAttrVal(data, dbOracleConfig.instance()),
                    getAttrValInt(data, dbOracleConfig.port())));
            moduleVars.add(new ModuleVar(dbOracleConfig.name(), typeFrom(Constants.ORACLEDB_CLIENT_TYPE), balExpr));
        }

        for (DbTemplateQuery dbTemplateQuery : data.globalDbTemplateQueryMap.values()) {
            var balExpr = exprFrom(String.format("`%s`", dbTemplateQuery.parameterizedQuery()));
            moduleVars.add(new ModuleVar(dbTemplateQuery.name(), typeFrom(Constants.SQL_PARAMETERIZED_QUERY_TYPE),
                    balExpr));
        }

        moduleVars.addAll(data.moduleVarMap.values());

        // Global comments at the end of file
        List<String> comments = new ArrayList<>();
        for (UnsupportedBlock unsupportedBlock : data.globalUnsupportedBlocks) {
            String comment = ConversionUtils.wrapElementInUnsupportedBlockComment(unsupportedBlock.xmlBlock());
            comments.add(comment);
        }

        List<ModuleTypeDef> typeDefs;
        if (data.sharedProjectData.isStandaloneBalFile) {
            createContextTypeDefns(data.sharedProjectData);
            data.sharedProjectData.contextTypeDefMap.putAll(data.typeDefMap);
            typeDefs = data.sharedProjectData.contextTypeDefMap.values().stream().toList();
            data.imports.addAll(data.sharedProjectData.contextTypeDefImports);
        } else {
            typeDefs = data.typeDefMap.values().stream().toList();
        }

        ArrayList<ModuleVar> orderedModuleVars = new ArrayList<>(data.globalConfigVarMap.values());
        orderedModuleVars.addAll(moduleVars);
        return createTextDocument("internal", new ArrayList<>(data.imports), typeDefs,
                orderedModuleVars, listeners, services, functions.stream().toList(), comments);
    }

    private static void genVMInboundEndpointSource(Data data, Flow flow, VMInboundEndpoint vmInboundEndpoint,
            Set<Function> functions) {
        String path = vmInboundEndpoint.path();
        String funcName = data.sharedProjectData.vmPathToBalFuncMap.get(path);
        if (funcName == null) {
            funcName = ConversionUtils.convertToBalIdentifier(flow.name());
            data.sharedProjectData.vmPathToBalFuncMap.put(path, funcName);
            genBalFunc(data, functions, funcName, flow.flowBlocks());
        } else {
            genBalFunc(data, functions, funcName, flow.flowBlocks());
        }
    }

    private static void genHttpSource(Data data, Flow flow, HttpListener src, List<Service> services,
                                      Set<Function> functions) {
        data.sharedProjectData.inboundProperties.add(new SharedProjectData.TypeAndNamePair(
                Constants.HTTP_REQUEST_TYPE, Constants.HTTP_REQUEST_REF));
        data.sharedProjectData.inboundProperties.add(new SharedProjectData.TypeAndNamePair(
                Constants.HTTP_RESPONSE_TYPE, Constants.HTTP_RESPONSE_REF));
        data.sharedProjectData.inboundProperties.add(new SharedProjectData.TypeAndNamePair(
                "map<string>", Constants.URI_PARAMS_REF));
        data.sharedProjectData.contextTypeDefImports.add(Constants.HTTP_MODULE_IMPORT);

        // Create a service from the flow
        Service service = genBalService(data, src, flow.flowBlocks(), functions);
        services.add(service);
    }

    public static void createContextTypeDefns(SharedProjectData sharedProjectData) {
        List<RecordField> contextRecFields = new ArrayList<>();
        contextRecFields.add(new RecordField(Constants.PAYLOAD_REF, BAL_ANYDATA_TYPE, exprFrom("()")));

        if (!sharedProjectData.flowVars.isEmpty()) {
            contextRecFields.add(new RecordField(Constants.FLOW_VARS_REF, typeFrom(Constants.FLOW_VARS_TYPE),
                    exprFrom("{}")));
            List<RecordField> flowVarRecFields = new ArrayList<>();
            for (SharedProjectData.TypeAndNamePair tnp : sharedProjectData.flowVars) {
                flowVarRecFields.add(new RecordField(tnp.name, typeFrom(tnp.type), true));
            }
            RecordTypeDesc flowVarsRecord = RecordTypeDesc.closedRecord(flowVarRecFields);
            sharedProjectData.contextTypeDefMap.put(Constants.FLOW_VARS_TYPE,
                    new ModuleTypeDef(Constants.FLOW_VARS_TYPE, flowVarsRecord));
        }

        if (!sharedProjectData.sessionVars.isEmpty()) {
            contextRecFields.add(new RecordField(Constants.SESSION_VARS_REF, typeFrom(Constants.SESSION_VARS_TYPE),
                    exprFrom("{}")));
            List<RecordField> sessionVarRecFields = new ArrayList<>();
            for (SharedProjectData.TypeAndNamePair tnp : sharedProjectData.sessionVars) {
                sessionVarRecFields.add(new RecordField(tnp.name, typeFrom(tnp.type), true));
            }
            RecordTypeDesc sessionVarsRecord = RecordTypeDesc.closedRecord(sessionVarRecFields);
            sharedProjectData.contextTypeDefMap.put("SessionVars", new ModuleTypeDef("SessionVars", sessionVarsRecord));
        }

        if (!sharedProjectData.inboundProperties.isEmpty()) {
            contextRecFields.add(new RecordField("inboundProperties", typeFrom(Constants.INBOUND_PROPERTIES_TYPE),
                    false));
            List<RecordField> inboundPropRecordFields = new ArrayList<>();
            for (SharedProjectData.TypeAndNamePair tnp : sharedProjectData.inboundProperties) {
                RecordField inboundPropField;
                if (tnp.type.startsWith("map<") && tnp.type.endsWith(">")) {
                    inboundPropField = new RecordField(tnp.name, typeFrom(tnp.type), exprFrom("{}"));
                } else {
                    inboundPropField = new RecordField(tnp.name, typeFrom(tnp.type), false);
                }
                inboundPropRecordFields.add(inboundPropField);
            }
            RecordTypeDesc inboundPropertiesRecord = RecordTypeDesc.closedRecord(inboundPropRecordFields);
            sharedProjectData.contextTypeDefMap.put(Constants.INBOUND_PROPERTIES_TYPE,
                    new ModuleTypeDef(Constants.INBOUND_PROPERTIES_TYPE, inboundPropertiesRecord));
        }

        RecordTypeDesc contextRecord = RecordTypeDesc.closedRecord(contextRecFields);
        sharedProjectData.contextTypeDefMap.put(Constants.CONTEXT_RECORD_TYPE, new ModuleTypeDef(
                Constants.CONTEXT_RECORD_TYPE, contextRecord));
    }

    private static void genBalFuncForGlobalExceptionStrategy(Data data, MuleRecord muleRecord,
            Set<Function> functions) {
        List<Statement> body;
        String name;
        if (muleRecord instanceof CatchExceptionStrategy catchExceptionStrategy) {
            name = catchExceptionStrategy.name();
            body = getCatchExceptionBody(data, catchExceptionStrategy);
        } else if (muleRecord instanceof ChoiceExceptionStrategy choiceExceptionStrategy) {
            name = choiceExceptionStrategy.name();
            body = getChoiceExceptionBody(data, choiceExceptionStrategy);
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

    private static void genBalFuncsFromSubFlows(Data data, List<SubFlow> subFlows, Set<Function> functions) {
        for (SubFlow subFlow : subFlows) {
            genBalFuncForPrivateOrSubFlow(data, functions, subFlow.name(), subFlow.flowBlocks());
        }
    }

    private static void genBalFuncForPrivateOrSubFlow(Data data, Set<Function> functions, String flowName,
            List<MuleRecord> flowBlocks) {
        List<Statement> body = convertTopLevelMuleBlocks(data, flowBlocks);

        String methodName = ConversionUtils.convertToBalIdentifier(flowName);
        Function function = Function.publicFunction(methodName, Constants.FUNC_PARAMS_WITH_CONTEXT, body);
        functions.add(function);
    }

    private static void genBalFunc(Data data, Set<Function> functions, String funcName, List<MuleRecord> flowBlocks) {
        List<Statement> body = convertTopLevelMuleBlocks(data, flowBlocks);
        Function function = Function.publicFunction(funcName, Constants.FUNC_PARAMS_WITH_CONTEXT, body);
        functions.add(function);
    }

    private static void genBalFuncsFromPrivateFlows(Data data, List<Flow> privateFlows, Set<Function> functions) {
        for (Flow privateFlow : privateFlows) {
            genBalFuncForPrivateOrSubFlow(data, functions, privateFlow.name(), privateFlow.flowBlocks());
        }
    }

    private static Service genBalService(Data data, HttpListener httpListener, List<MuleRecord> flowBlocks,
            Set<Function> functions) {
        List<String> pathParams = new ArrayList<>();
        String resourcePath = getBallerinaResourcePath(data, httpListener.resourcePath(), pathParams);
        String[] resourceMethodNames = httpListener.allowedMethods();
        String listenerRef = httpListener.configRef();
        String muleBasePath = insertLeadingSlash(
                data.sharedProjectData.sharedHttpListenerConfigsMap.get(httpListener.configRef()).basePath());
        String basePath = getBallerinaAbsolutePath(muleBasePath);

        // Add services
        List<Parameter> queryPrams = new ArrayList<>();
        queryPrams.add(new Parameter(Constants.HTTP_REQUEST_REF, typeFrom(Constants.HTTP_REQUEST_TYPE)));

        List<Statement> bodyStmts = new ArrayList<>();
        String inboundPropInitValue = getInboundPropInitValue(data, pathParams);
        bodyStmts.add(stmtFrom("Context %s = {%s: %s};".formatted(Constants.CONTEXT_REFERENCE,
                Constants.INBOUND_PROPERTIES_REF, inboundPropInitValue)));

        List<Statement> bodyCoreStmts = convertTopLevelMuleBlocks(data, flowBlocks);
        bodyStmts.addAll(bodyCoreStmts);

        // Add return statement
        bodyStmts.add(stmtFrom("\n\n%s.%s.setPayload(%s.payload);".formatted(Constants.INBOUND_PROPERTIES_FIELD_ACCESS,
                        Constants.HTTP_RESPONSE_REF, Constants.CONTEXT_REFERENCE)));
        bodyStmts.add(stmtFrom("return %s.%s;".formatted(Constants.INBOUND_PROPERTIES_FIELD_ACCESS,
                Constants.HTTP_RESPONSE_REF)));

        // Add service resources
        List<Resource> resources = new ArrayList<>();
        TypeDesc returnType = typeFrom(Constants.HTTP_RESOURCE_RETURN_TYPE_DEFAULT);
        data.imports.add(Constants.HTTP_MODULE_IMPORT);

        if (resourceMethodNames.length > 1) {
            // same logic is shared, thus extracting it to a function
            String invokeEndPointMethodName = String.format(Constants.FUNC_NAME_HTTP_ENDPOINT_TEMPLATE,
                    data.sharedProjectData.invokeEndPointMethodCount++);
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

    private static String getInboundPropInitValue(Data data, List<String> pathParams) {
        Map<String, String> inboundPropMap = new LinkedHashMap<>();
        for (SharedProjectData.TypeAndNamePair tnp : data.sharedProjectData.inboundProperties) {
            switch (tnp.name) {
                case Constants.HTTP_REQUEST_REF -> {
                    inboundPropMap.put(Constants.HTTP_REQUEST_REF, Constants.HTTP_REQUEST_REF);
                }
                case Constants.HTTP_RESPONSE_REF -> {
                    inboundPropMap.put(Constants.HTTP_RESPONSE_REF, "new");
                }
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
