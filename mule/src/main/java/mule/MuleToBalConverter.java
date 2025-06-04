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
import common.BallerinaModel.OnFailClause;
import common.BallerinaModel.Statement.DoStatement;
import common.BallerinaModel.Statement.ElseIfClause;
import common.BallerinaModel.Statement.NamedWorkerDecl;
import common.BallerinaModel.TypeBindingPattern;
import common.BallerinaModel.TypeDesc.RecordTypeDesc;
import common.BallerinaModel.TypeDesc.RecordTypeDesc.RecordField;
import common.CodeGenerator;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import mule.MuleXMLNavigator.MuleElement;
import mule.dataweave.converter.DWConversionStats;
import mule.dataweave.converter.DWReader;
import mule.dataweave.converter.DWUtils;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

import static common.BallerinaModel.Expression.BallerinaExpression;
import static common.BallerinaModel.Statement.BallerinaStatement;
import static common.BallerinaModel.BlockFunctionBody;
import static common.BallerinaModel.Function;
import static common.BallerinaModel.Statement.IfElseStatement;
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
import static mule.Constants.FUNC_NAME_ASYC_TEMPLATE;
import static common.ConversionUtils.exprFrom;
import static mule.ConversionUtils.getAttrVal;
import static mule.ConversionUtils.getBallerinaClientResourcePath;
import static common.ConversionUtils.stmtFrom;
import static common.ConversionUtils.typeFrom;
import static mule.ConversionUtils.convertMuleExprToBal;
import static mule.ConversionUtils.convertMuleExprToBalStringLiteral;
import static mule.ConversionUtils.genQueryParam;
import static mule.ConversionUtils.getAllowedMethods;
import static mule.ConversionUtils.getBallerinaAbsolutePath;
import static mule.ConversionUtils.getBallerinaResourcePath;
import static mule.ConversionUtils.getAttrValInt;
import static mule.ConversionUtils.inferTypeFromBalExpr;
import static mule.ConversionUtils.insertLeadingSlash;
import static mule.MuleModel.Async;
import static mule.MuleModel.CatchExceptionStrategy;
import static mule.MuleModel.ChoiceExceptionStrategy;
import static mule.MuleModel.Choice;
import static mule.MuleModel.Database;
import static mule.MuleModel.DbInParam;
import static mule.MuleModel.DbMSQLConfig;
import static mule.MuleModel.DbOracleConfig;
import static mule.MuleModel.DbTemplateQuery;
import static mule.MuleModel.Enricher;
import static mule.MuleModel.ExpressionComponent;
import static mule.MuleModel.Flow;
import static mule.MuleModel.FlowReference;
import static mule.MuleModel.HTTPListenerConfig;
import static mule.MuleModel.HttpListener;
import static mule.MuleModel.HttpRequest;
import static mule.MuleModel.Kind;
import static mule.MuleModel.HTTPRequestConfig;
import static mule.MuleModel.LogLevel;
import static mule.MuleModel.Logger;
import static mule.MuleModel.MuleRecord;
import static mule.MuleModel.ObjectToJson;
import static mule.MuleModel.ObjectToString;
import static mule.MuleModel.Payload;
import static mule.MuleModel.QueryType;
import static mule.MuleModel.ReferenceExceptionStrategy;
import static mule.MuleModel.RemoveVariable;
import static mule.MuleModel.SetVariable;
import static mule.MuleModel.SetSessionVariable;
import static mule.MuleModel.SubFlow;
import static mule.MuleModel.TransformMessage;
import static mule.MuleModel.Type;
import static mule.MuleModel.VMInboundEndpoint;
import static mule.MuleModel.VMOutboundEndpoint;
import static mule.MuleModel.WhenInChoice;
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
        public int objectToJsonVarCount = 0;
        public int objectToStringVarCount = 0;
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

        // Data analyze attributes
        Map<String, FlowInfo> flowInfoMap = new HashMap<>();
        Map<String, Function> flowToGenMethodMap = new HashMap<>();
        FlowInfo currentFlowInfo = null;

        public DWConversionStats getDwConversionStats() {
            return dwConversionStats;
        }

        static class FlowInfo {

            final String flowName;
            private Context context;
            private PayloadVarInfo currentPayload;

            FlowInfo(String flowName) {
                this.flowName = flowName;
                this.context = Context.DEFAULT;
                this.currentPayload = DEFAULT_PAYLOAD;
            }

            FlowInfo(String flowName, Context context) {
                this.flowName = flowName;
                this.context = context;
                this.currentPayload = DEFAULT_PAYLOAD;
            }
        }
    }

    static void putFlowInfoIfAbsent(Data data, String flowName) {
        data.sharedProjectData.flowInfoMap.putIfAbsent(flowName, new SharedProjectData.FlowInfo(flowName));
    }

    public enum Context {
        DEFAULT,
        HTTP_LISTENER
    }

    static final PayloadVarInfo DEFAULT_PAYLOAD = new PayloadVarInfo("()", "null");

    public record PayloadVarInfo(String type, String nameReference) {

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

        MuleElement muleElement = muleXMLNavigator.createRootMuleElement(root);

        List<Flow> flows = new ArrayList<>();
        List<SubFlow> subFlows = new ArrayList<>();

        while (muleElement.peekChild() != null) {
            MuleElement child = muleElement.consumeChild();
            Element element = child.getElement();

            String elementTagName = element.getTagName();
            if (MuleXMLTag.FLOW.tag().equals(elementTagName)) {
                Flow flow = readFlow(data, child);
                flows.add(flow);
                continue;
            } else if (MuleXMLTag.SUB_FLOW.tag().equals(elementTagName)) {
                SubFlow subFlow = readSubFlow(data, child);
                subFlows.add(subFlow);
                continue;
            }

            readGlobalConfigElement(data, child);
        }

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

    private static void readGlobalConfigElement(Data data, MuleElement muleElement) {
        String elementTagName = muleElement.getElement().getTagName();
        if (MuleXMLTag.HTTP_LISTENER_CONFIG.tag().equals(elementTagName)) {
            HTTPListenerConfig httpListenerConfig = readHttpListenerConfig(data, muleElement);
            data.globalHttpListenerConfigsMap.put(httpListenerConfig.name(), httpListenerConfig);
            data.sharedProjectData.sharedHttpListenerConfigsMap.put(httpListenerConfig.name(), httpListenerConfig);
        } else if (MuleXMLTag.HTTP_REQUEST_CONFIG.tag().equals(elementTagName)) {
            HTTPRequestConfig httpRequestConfig = readHttpRequestConfig(data, muleElement);
            data.globalHttpRequestConfigsMap.put(httpRequestConfig.name(), httpRequestConfig);
            data.sharedProjectData.sharedHttpRequestConfigsMap.put(httpRequestConfig.name(), httpRequestConfig);
        } else if (MuleXMLTag.DB_MYSQL_CONFIG.tag().equals(elementTagName)) {
            DbMSQLConfig dbMSQLConfig = readDbMySQLConfig(data, muleElement);
            data.globalDbMySQLConfigsMap.put(dbMSQLConfig.name(), dbMSQLConfig);
            data.sharedProjectData.sharedDbMySQLConfigsMap.put(dbMSQLConfig.name(), dbMSQLConfig);
        } else if (MuleXMLTag.DB_ORACLE_CONFIG.tag().equals(elementTagName)) {
            DbOracleConfig dbOracleConfig = readDbOracleConfig(data, muleElement);
            data.globalDbOracleConfigsMap.put(dbOracleConfig.name(), dbOracleConfig);
            data.sharedProjectData.sharedDbOracleConfigsMap.put(dbOracleConfig.name(), dbOracleConfig);
        } else if (MuleXMLTag.DB_TEMPLATE_QUERY.tag().equals(elementTagName)) {
            DbTemplateQuery dbTemplateQuery = readDbTemplateQuery(data, muleElement);
            data.globalDbTemplateQueryMap.put(dbTemplateQuery.name(), dbTemplateQuery);
            data.sharedProjectData.sharedDbTemplateQueryMap.put(dbTemplateQuery.name(), dbTemplateQuery);
        } else if (MuleXMLTag.CATCH_EXCEPTION_STRATEGY.tag().equals(elementTagName)) {
            CatchExceptionStrategy catchExceptionStrategy = readCatchExceptionStrategy(data, muleElement);
            data.globalExceptionStrategies.add(catchExceptionStrategy);
        } else if (MuleXMLTag.CHOICE_EXCEPTION_STRATEGY.tag().equals(elementTagName)) {
            ChoiceExceptionStrategy choiceExceptionStrategy = readChoiceExceptionStrategy(data, muleElement);
            data.globalExceptionStrategies.add(choiceExceptionStrategy);
        } else {
            UnsupportedBlock unsupportedBlock = readUnsupportedBlock(data, muleElement);
            data.globalUnsupportedBlocks.add(unsupportedBlock);
        }
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
        SharedProjectData.FlowInfo flowInfo = new SharedProjectData.FlowInfo(flow.name(), Context.HTTP_LISTENER);
        data.sharedProjectData.flowInfoMap.put(flow.name(), flowInfo);
        data.sharedProjectData.currentFlowInfo = flowInfo;

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
        putFlowInfoIfAbsent(data, flowName);
        data.sharedProjectData.currentFlowInfo = data.sharedProjectData.flowInfoMap.get(flowName);

        List<Statement> body = genFuncBodyStatements(data, flowBlocks);

        String methodName = ConversionUtils.convertToBalIdentifier(flowName);
        Function function = Function.publicFunction(methodName, Constants.FUNC_PARAMS_WITH_CONTEXT, body);
        functions.add(function);
        data.sharedProjectData.flowToGenMethodMap.put(flowName, function);
    }

    private static void genBalFunc(Data data, Set<Function> functions, String funcName, List<MuleRecord> flowBlocks) {
        List<Statement> body = genFuncBodyStatements(data, flowBlocks);
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

        List<Statement> bodyCoreStmts = genFuncBodyStatements(data, flowBlocks);
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
                    // TODO: add test
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

    private static List<Statement> genFuncBodyStatements(Data data, List<MuleRecord> flowBlocks) {
        // Add function body statements
        List<Statement> body = new ArrayList<>();
        List<Statement> workers = new ArrayList<>();

        // Read flow blocks
        for (MuleRecord record : flowBlocks) {
            List<Statement> s = convertToStatements(data, record);
            // TODO: handle these properly
            if (s.size() > 1 && s.getFirst() instanceof NamedWorkerDecl namedWorkerDecl) {
                workers.add(namedWorkerDecl);
                s.remove(namedWorkerDecl);
            }

            if (s.size() == 1 && s.getFirst() instanceof DoStatement doStatement) {
                body = new ArrayList<>(Collections.singletonList(new DoStatement(body, doStatement.onFailClause())));
                continue;
            }
            body.addAll(s);
        }

        workers.addAll(body);
        return workers;
    }

    public static TextDocument createTextDocument(String docName, List<Import> imports,
                                                  List<ModuleTypeDef> moduleTypeDefs,
                                                    List<ModuleVar> moduleVars, List<Listener> listeners,
                                                    List<Service> services, List<Function> functions,
                                                    List<String> comments) {
        return new TextDocument(docName, imports, moduleTypeDefs, moduleVars, listeners,
                services, functions, comments);
    }

    private static MuleRecord readBlock(Data data, MuleElement muleElement) {
        MuleXMLTag muleXMLTag = MuleXMLTag.fromTag(muleElement.getElement().getTagName());
        switch (muleXMLTag) {
            // Source
            case MuleXMLTag.HTTP_LISTENER -> {
                return readHttpListener(data, muleElement);
            }

            case MuleXMLTag.VM_INBOUND_ENDPOINT -> {
                return readVMInboundEndpoint(data, muleElement);
            }

            // Process Items
            case MuleXMLTag.LOGGER -> {
                return readLogger(data, muleElement);
            }
            case MuleXMLTag.EXPRESSION_COMPONENT -> {
                return readExpressionComponent(data, muleElement);
            }
            case MuleXMLTag.SET_VARIABLE -> {
                return readSetVariable(data, muleElement);
            }
            case MuleXMLTag.SET_SESSION_VARIABLE -> {
                return readSetSessionVariable(data, muleElement);
            }
            case MuleXMLTag.REMOVE_VARIABLE -> {
                return readRemoveVariable(data, muleElement);
            }
            case MuleXMLTag.REMOVE_SESSION_VARIABLE -> {
                return readRemoveSessionVariable(data, muleElement);
            }
            case MuleXMLTag.HTTP_REQUEST -> {
                return readHttpRequest(data, muleElement);
            }
            case MuleXMLTag.SET_PAYLOAD -> {
                return readSetPayload(data, muleElement);
            }
            case MuleXMLTag.CHOICE -> {
                return readChoice(data, muleElement);
            }
            case MuleXMLTag.FLOW_REFERENCE -> {
                return readFlowReference(data, muleElement);
            }
            case MuleXMLTag.TRANSFORM_MESSAGE -> {
                return readTransformMessage(data, muleElement);
            }
            case MuleXMLTag.DB_INSERT, MuleXMLTag.DB_SELECT, MuleXMLTag.DB_UPDATE, MuleXMLTag.DB_DELETE -> {
                return readDatabase(data, muleElement);
            }
            case MuleXMLTag.OBJECT_TO_JSON -> {
                return readObjectToJson(data, muleElement);
            }
            case MuleXMLTag.OBJECT_TO_STRING -> {
                return readObjectToString(data, muleElement);
            }
            case MuleXMLTag.ENRICHER -> {
                return readEnricher(data, muleElement);
            }
            case MuleXMLTag.ASYNC -> {
                return readAsync(data, muleElement);
            }
            case MuleXMLTag.CATCH_EXCEPTION_STRATEGY -> {
                return readCatchExceptionStrategy(data, muleElement);
            }
            case MuleXMLTag.CHOICE_EXCEPTION_STRATEGY -> {
                return readChoiceExceptionStrategy(data, muleElement);
            }
            case MuleXMLTag.REFERENCE_EXCEPTION_STRATEGY -> {
                return readReferenceExceptionStrategy(data, muleElement);
            }
            case MuleXMLTag.VM_OUTBOUND_ENDPOINT -> {
                return readVMOutboundEndpoint(data, muleElement);
            }
            default -> {
                return readUnsupportedBlock(data, muleElement);
            }
        }
    }

    private static List<Statement> convertToStatements(Data data, MuleRecord muleRec) {
        List<Statement> statementList = new ArrayList<>();
        switch (muleRec) {
            case Logger lg -> statementList.add(stmtFrom(String.format("log:%s(%s);",
                    getBallerinaLogFunction(lg.level()), convertMuleExprToBalStringLiteral(data, lg.message()))));
            case VMOutboundEndpoint vmOutboundEndpoint -> {
                String path = vmOutboundEndpoint.path();
                String funcName = data.sharedProjectData.vmPathToBalFuncMap.get(path);
                if (funcName == null) {
                    funcName = String.format(Constants.FUNC_NAME_VM_RECEIVE_TEMPLATE,
                            data.sharedProjectData.vmReceiveFuncCount++);
                    data.sharedProjectData.vmPathToBalFuncMap.put(path, funcName);
                }

                List<Statement> body = new ArrayList<>();
                body.add(stmtFrom("\n// VM Inbound Endpoint\n"));
                body.add(stmtFrom("anydata receivedPayload = <- function;"));
                body.add(stmtFrom("ctx.payload = receivedPayload;"));
                body.add(stmtFrom(String.format("%s(ctx);", funcName)));

                statementList.add(new NamedWorkerDecl("W", Optional.of(typeFrom("error?")), body));
                statementList.add(stmtFrom("\n\n// VM Outbound Endpoint\n"));
                statementList.add(stmtFrom(String.format("%s.payload -> W;", Constants.CONTEXT_REFERENCE)));
            }
            case ExpressionComponent exprComponent -> {
                String convertedExpr = convertMuleExprToBal(data, String.format("#[%s]",
                        exprComponent.exprCompContent()));
                ConversionUtils.processExprCompContent(data.sharedProjectData, convertedExpr);
                statementList.add(stmtFrom(convertedExpr));
            }
            case Payload payload -> {
                statementList.add(stmtFrom("\n\n// set payload\n"));
                String pyld = convertMuleExprToBal(data, payload.expr());
                String type = inferTypeFromBalExpr(pyld);
                String payloadVar = String.format(Constants.VAR_PAYLOAD_TEMPLATE,
                        data.sharedProjectData.payloadVarCount++);
                statementList.add(stmtFrom(String.format("%s %s = %s;", type, payloadVar, pyld)));
                statementList.add(stmtFrom(String.format("%s.payload = %s;", Constants.CONTEXT_REFERENCE,
                        payloadVar)));
                data.sharedProjectData.currentFlowInfo.currentPayload = new PayloadVarInfo("string", payloadVar);
            }
            case Choice choice -> {
                List<WhenInChoice> whens = choice.whens();
                assert !whens.isEmpty(); // For valid mule config, there is at least one when

                WhenInChoice firstWhen = whens.getFirst();
                String ifCondition = convertMuleExprToBal(data, firstWhen.condition());
                List<Statement> ifBody = new ArrayList<>();
                for (MuleRecord r2 : firstWhen.process()) {
                    List<Statement> statements = convertToStatements(data, r2);
                    ifBody.addAll(statements);
                }

                List<ElseIfClause> elseIfClauses = new ArrayList<>(whens.size() - 1);
                for (int i = 1; i < whens.size(); i++) {
                    WhenInChoice when = whens.get(i);
                    List<Statement> elseIfBody = new ArrayList<>();
                    for (MuleRecord r2 : when.process()) {
                        List<Statement> statements = convertToStatements(data, r2);
                        elseIfBody.addAll(statements);
                    }
                    ElseIfClause elseIfClause = new ElseIfClause(exprFrom(convertMuleExprToBal(data, when.condition())),
                            elseIfBody);
                    elseIfClauses.add(elseIfClause);
                }

                List<Statement> elseBody = new ArrayList<>(choice.otherwiseProcess().size());
                for (MuleRecord r2 : choice.otherwiseProcess()) {
                    List<Statement> statements = convertToStatements(data, r2);
                    elseBody.addAll(statements);
                }
                statementList.add(new IfElseStatement(exprFrom(ifCondition), ifBody, elseIfClauses, elseBody));
            }
            case SetVariable setVariable -> {
                String varName = ConversionUtils.convertToBalIdentifier(setVariable.variableName());
                String balExpr = convertMuleExprToBal(data, setVariable.value());
                String type = inferTypeFromBalExpr(balExpr);

                if (!data.sharedProjectData.existingFlowVar(varName)) {
                    data.sharedProjectData.flowVars.add(new SharedProjectData.TypeAndNamePair(type, varName));
                }
                statementList.add(stmtFrom(String.format("%s.%s = %s;", Constants.FLOW_VARS_FIELD_ACCESS,
                        varName, balExpr)));
            }
            case RemoveVariable removeVariable -> {
                String varName = ConversionUtils.convertToBalIdentifier(removeVariable.variableName());
                if (removeVariable.kind() == Kind.REMOVE_VARIABLE && data.sharedProjectData.existingFlowVar(varName)) {
                    statementList.add(stmtFrom(String.format("%s.%s = %s;", Constants.FLOW_VARS_FIELD_ACCESS, varName,
                            "()")));
                } else if (removeVariable.kind() == Kind.REMOVE_SESSION_VARIABLE &&
                        data.sharedProjectData.existingSessionVar(varName)) {
                    statementList.add(stmtFrom(String.format("%s.%s = %s;", Constants.SESSION_VARS_FIELD_ACCESS,
                            varName, "()")));
                }
            }
            case SetSessionVariable setSessionVariable -> {
                String varName = ConversionUtils.convertToBalIdentifier(setSessionVariable.variableName());
                String balExpr = convertMuleExprToBal(data, setSessionVariable.value());
                String type = inferTypeFromBalExpr(balExpr);

                if (!data.sharedProjectData.existingSessionVar(varName)) {
                    data.sharedProjectData.sessionVars.add(new SharedProjectData.TypeAndNamePair(type, varName));
                }
                statementList.add(stmtFrom(String.format("%s.%s = %s;", Constants.SESSION_VARS_FIELD_ACCESS, varName,
                        balExpr)));
            }
            case ObjectToJson objectToJson -> {
                statementList.add(stmtFrom("\n\n// json transformation\n"));
                String objToJsonVarName = String.format(Constants.VAR_OBJ_TO_JSON_TEMPLATE,
                        data.sharedProjectData.objectToJsonVarCount++);
                statementList.add(stmtFrom(String.format("json %s = %s.toJson();", objToJsonVarName,
                        data.sharedProjectData.currentFlowInfo.currentPayload.nameReference())));

                // object to json transformer implicitly sets the payload
                statementList.add(stmtFrom("%s.payload = %s;".formatted(Constants.CONTEXT_REFERENCE,
                        objToJsonVarName)));
                data.sharedProjectData.currentFlowInfo.currentPayload = new PayloadVarInfo("json", objToJsonVarName);
            }
            case ObjectToString objectToString -> {
                statementList.add(stmtFrom("\n\n// string transformation\n"));
                String objToStringVarName = String.format(Constants.VAR_OBJ_TO_STRING_TEMPLATE,
                        data.sharedProjectData.objectToStringVarCount++);
                statementList.add(stmtFrom(String.format("string %s = %s.toString();", objToStringVarName,
                        data.sharedProjectData.currentFlowInfo.currentPayload.nameReference())));

                // object to string transformer implicitly sets the payload
                statementList.add(stmtFrom("%s.payload = %s;".formatted(Constants.CONTEXT_REFERENCE,
                        objToStringVarName)));
                data.sharedProjectData.currentFlowInfo.currentPayload = new PayloadVarInfo("string",
                        objToStringVarName);
            }
            case HttpRequest httpRequest -> {
                List<Statement> statements = new ArrayList<>();
                String path = getBallerinaClientResourcePath(data, httpRequest.path());
                String method = httpRequest.method();
                String url = httpRequest.url();
                Map<String, String> queryParams = httpRequest.queryParams();

                statementList.add(stmtFrom("\n\n// http client request\n"));
                statements.add(stmtFrom(String.format("http:Client %s = check new(\"%s\");", httpRequest.configRef(),
                        url)));
                String clientResultVar = String.format(Constants.VAR_CLIENT_RESULT_TEMPLATE,
                        data.sharedProjectData.clientResultVarCount++);
                statements.add(stmtFrom("%s %s = check %s->%s.%s(%s);".formatted(Constants.HTTP_RESPONSE_TYPE,
                        clientResultVar, httpRequest.configRef(), path, method.toLowerCase(),
                        genQueryParam(data, queryParams))));
                statements.add(stmtFrom(String.format("%s.payload = check %s.getJsonPayload();",
                        Constants.CONTEXT_REFERENCE, clientResultVar)));
                statementList.addAll(statements);
            }
            case FlowReference flowReference -> {
                String flowName = flowReference.flowName();
                String funcRef = ConversionUtils.convertToBalIdentifier(flowName);

                if (data.sharedProjectData.currentFlowInfo.context == Context.HTTP_LISTENER) {
                    Function method = data.sharedProjectData.flowToGenMethodMap.get(flowName);
                    // TODO: revisit
                    if (method == null) {
                        // Set the flow context to Http listener
                        SharedProjectData.FlowInfo flowInfo = new SharedProjectData.FlowInfo(flowName,
                                Context.HTTP_LISTENER);
                        data.sharedProjectData.flowInfoMap.put(flowName, flowInfo);
                    } else {
                        // Means we have analyzed the flow already
                        SharedProjectData.FlowInfo flowInfo = data.sharedProjectData.flowInfoMap.get(flowName);
                        flowInfo.context = Context.HTTP_LISTENER;
                    }
                }

                statementList.add(stmtFrom(String.format("%s(%s);", funcRef, Constants.CONTEXT_REFERENCE)));
            }
            case Async async -> {
                List<Statement> body = genFuncBodyStatements(data, async.flowBlocks());
                int asyncFuncId = data.sharedProjectData.asyncFuncCount++;
                String funcName = String.format(FUNC_NAME_ASYC_TEMPLATE, asyncFuncId);
                Function function = Function.publicFunction(funcName, Constants.FUNC_PARAMS_WITH_CONTEXT, body);
                data.functions.add(function);

                statementList.add(stmtFrom("\n\n// async operation\n"));
                statementList.add(stmtFrom(String.format("_ = start %s(%s);", funcName, Constants.CONTEXT_REFERENCE)));
            }
            case Enricher enricher -> {
                // TODO: support no source
                String source = convertMuleExprToBal(data, enricher.source());
                String target = convertMuleExprToBal(data, enricher.target());

                if (target.startsWith(Constants.FLOW_VARS_FIELD_ACCESS + ".")) {
                    String var = target.replace(Constants.FLOW_VARS_FIELD_ACCESS + ".", "");
                    if (!data.sharedProjectData.existingFlowVar(var)) {
                        data.sharedProjectData.flowVars.add(new SharedProjectData.TypeAndNamePair("string", var));
                    }
                } else if (target.startsWith(Constants.SESSION_VARS_FIELD_ACCESS + ".")) {
                    String var = target.replace(Constants.SESSION_VARS_FIELD_ACCESS + ".", "");
                    if (!data.sharedProjectData.existingSessionVar(var)) {
                        data.sharedProjectData.sessionVars.add(new SharedProjectData.TypeAndNamePair("string", var));
                    }
                }

                if (enricher.innerBlock().isEmpty()) {
                    statementList.add(stmtFrom(String.format("%s = %s;", target, source)));
                } else {
                    List<Statement> enricherStmts = convertToStatements(data, enricher.innerBlock().get());

                    String methodName = String.format(Constants.FUNC_NAME_ENRICHER_TEMPLATE,
                            data.sharedProjectData.enricherFuncCount);
                    Function func = new Function(Optional.of("public"), methodName, Constants.FUNC_PARAMS_WITH_CONTEXT,
                            Optional.of(typeFrom("string?")), new BlockFunctionBody(enricherStmts));
                    data.functions.add(func);

                    enricherStmts.add(stmtFrom(String.format("return %s;", source)));
                    statementList.add(stmtFrom(String.format("%s = %s(%s.clone());", target,
                            String.format(Constants.FUNC_NAME_ENRICHER_TEMPLATE,
                                    data.sharedProjectData.enricherFuncCount++),
                            Constants.CONTEXT_REFERENCE)));
                }
            }
            case CatchExceptionStrategy catchExceptionStrategy -> {
                List<Statement> onFailBody = getCatchExceptionBody(data, catchExceptionStrategy);
                OnFailClause onFailClause = new OnFailClause(onFailBody);
                DoStatement doStatement = new DoStatement(Collections.emptyList(), onFailClause);
                statementList.add(doStatement);
            }
            case ChoiceExceptionStrategy choiceExceptionStrategy -> {
                List<Statement> onFailBody = getChoiceExceptionBody(data, choiceExceptionStrategy);
                TypeBindingPattern typeBindingPattern = new TypeBindingPattern(BAL_ERROR_TYPE,
                        Constants.ON_FAIL_ERROR_VAR_REF);
                OnFailClause onFailClause = new OnFailClause(onFailBody, typeBindingPattern);
                DoStatement doStatement = new DoStatement(Collections.emptyList(), onFailClause);
                statementList.add(doStatement);
            }
            case ReferenceExceptionStrategy referenceExceptionStrategy -> {
                String refName = referenceExceptionStrategy.refName();
                String funcRef = ConversionUtils.convertToBalIdentifier(refName);
                BallerinaStatement funcCallStmt = stmtFrom(String.format("%s(%s, %s);", funcRef,
                        Constants.CONTEXT_REFERENCE, Constants.ON_FAIL_ERROR_VAR_REF));
                List<Statement> onFailBody = Collections.singletonList(funcCallStmt);
                TypeBindingPattern typeBindingPattern = new TypeBindingPattern(BAL_ERROR_TYPE,
                        Constants.ON_FAIL_ERROR_VAR_REF);
                OnFailClause onFailClause = new OnFailClause(onFailBody, typeBindingPattern);
                DoStatement doStatement = new DoStatement(Collections.emptyList(), onFailClause);
                statementList.add(doStatement);
            }
            case Database database -> {
                data.imports.add(new Import(Constants.ORG_BALLERINA, Constants.MODULE_SQL, Optional.empty()));
                String streamConstraintType = Constants.GENERIC_RECORD_TYPE_REF;
                data.typeDefMap.put(streamConstraintType,
                        new ModuleTypeDef(streamConstraintType, typeFrom(Constants.GENERIC_RECORD_TYPE)));

                statementList.add(stmtFrom("\n\n// database operation\n"));
                String dbQueryVarName = Constants.VAR_DB_QUERY_TEMPLATE
                        .formatted(data.sharedProjectData.dbQueryVarCount++);
                statementList.add(stmtFrom("%s %s = %s;".formatted(Constants.SQL_PARAMETERIZED_QUERY_TYPE,
                        dbQueryVarName, database.queryType() == QueryType.TEMPLATE_QUERY_REF ? database.query()
                                : String.format("`%s`", database.query()))));

                String dbStreamVarName = Constants.VAR_DB_STREAM_TEMPLATE
                        .formatted(data.sharedProjectData.dbStreamVarCount++);
                statementList.add(stmtFrom("%s %s= %s->query(%s);"
                        .formatted(Constants.DB_QUERY_DEFAULT_TEMPLATE.formatted(streamConstraintType),
                                dbStreamVarName, database.configRef(), dbQueryVarName)));

                if (database.kind() == Kind.DB_SELECT) {
                    String dbSelectVarName = Constants.VAR_DB_SELECT_TEMPLATE
                            .formatted(data.sharedProjectData.dbSelectVarCount++);
                    statementList.add(stmtFrom(
                            String.format("%s[] %s = check from %s %s in %s select %s;", streamConstraintType,
                                    dbSelectVarName, streamConstraintType, Constants.VAR_ITERATOR, dbStreamVarName,
                                    Constants.VAR_ITERATOR)));
                    // db:select implicitly sets the payload
                    statementList.add(stmtFrom("%s.payload = %s;".formatted(Constants.CONTEXT_REFERENCE,
                            dbSelectVarName)));

                    data.sharedProjectData.currentFlowInfo.currentPayload = new PayloadVarInfo(String.format("%s[]",
                            streamConstraintType),
                            dbSelectVarName);
                }
            }
            case TransformMessage transformMessage -> {
                DWReader.processDWElements(transformMessage.children(), data, statementList);
                statementList.add(stmtFrom("%s.payload = %s;"
                        .formatted(Constants.CONTEXT_REFERENCE, DWUtils.DATAWEAVE_OUTPUT_VARIABLE_NAME)));
            }
            case UnsupportedBlock unsupportedBlock -> {
                String comment = ConversionUtils.wrapElementInUnsupportedBlockComment(unsupportedBlock.xmlBlock());
                // TODO: comment is not a statement. Find a better way to handle this
                // This works for now because we concatenate and create a body block `{ stmts }`
                // before parsing.
                statementList.add(stmtFrom(comment));
            }
            case null -> throw new IllegalStateException();
            default -> throw new UnsupportedOperationException();
        }

        return statementList;
    }

    private static List<Statement> getCatchExceptionBody(Data data, CatchExceptionStrategy catchExceptionStrategy) {
        return convertMuleRecToBalStatements(data, catchExceptionStrategy.catchBlocks());
    }

    private static List<Statement> getChoiceExceptionBody(Data data, ChoiceExceptionStrategy choiceExceptionStrategy) {
        List<CatchExceptionStrategy> catchExceptionStrategies = choiceExceptionStrategy.catchExceptionStrategyList();
        assert !catchExceptionStrategies.isEmpty();

        CatchExceptionStrategy firstCatch = catchExceptionStrategies.getFirst();
        BallerinaExpression ifCondition = exprFrom(convertMuleExprToBal(data, firstCatch.when()));
        List<Statement> ifBody = convertMuleRecToBalStatements(data, firstCatch.catchBlocks());

        List<ElseIfClause> elseIfClauses = new ArrayList<>();
        for (int i = 1; i < catchExceptionStrategies.size() - 1; i++) {
            CatchExceptionStrategy catchExpStrgy = catchExceptionStrategies.get(i);
            List<Statement> elseIfBody = convertMuleRecToBalStatements(data, catchExpStrgy.catchBlocks());
            ElseIfClause elseIfClause = new ElseIfClause(exprFrom(convertMuleExprToBal(data, catchExpStrgy.when())),
                    elseIfBody);
            elseIfClauses.add(elseIfClause);
        }

        List<Statement> elseBody;
        if (catchExceptionStrategies.size() > 1) {
            CatchExceptionStrategy lastCatch = catchExceptionStrategies.getLast();
            elseBody = convertMuleRecToBalStatements(data, lastCatch.catchBlocks());
        } else {
            elseBody = Collections.emptyList();
        }

        IfElseStatement ifElseStmt = new IfElseStatement(ifCondition, ifBody, elseIfClauses, elseBody);

        List<Statement> statementList = new ArrayList<>();
        statementList.add(stmtFrom("\n// TODO: if conditions may require some manual adjustments\n"));
        statementList.add(ifElseStmt);

        return statementList;
    }

    private static List<Statement> convertMuleRecToBalStatements(Data data, List<MuleRecord> muleRecords) {
        List<Statement> statements = new ArrayList<>();
        for (MuleRecord muleRecord : muleRecords) {
            List<Statement> stmts = convertToStatements(data, muleRecord);
            statements.addAll(stmts);
        }
        return statements;
    }

    private static String getBallerinaLogFunction(LogLevel logLevel) {
        return switch (logLevel) {
            case DEBUG -> "printDebug";
            case ERROR -> "printError";
            case INFO, TRACE -> "printInfo";
            case WARN -> "printWarn";
        };
    }

    // Components
    private static Logger readLogger(Data data, MuleElement muleElement) {
        Element element = muleElement.getElement();
        data.imports.add(new Import(Constants.ORG_BALLERINA, Constants.MODULE_LOG));
        String message = element.getAttribute("message");
        String level = element.getAttribute("level");
        LogLevel logLevel = switch (level) {
            case "DEBUG" -> LogLevel.DEBUG;
            case "ERROR" -> LogLevel.ERROR;
            case "INFO" -> LogLevel.INFO;
            case "TRACE" -> LogLevel.TRACE;
            case "WARN" -> LogLevel.WARN;
            default -> throw new IllegalStateException();
        };
        return new Logger(message, logLevel);
    }

    private static MuleRecord readExpressionComponent(Data data, MuleElement muleElement) {
        return new ExpressionComponent(muleElement.getElement().getTextContent());
    }

    // Flow Control
    private static Choice readChoice(Data data, MuleElement muleElement) {
        List<WhenInChoice> whens = new ArrayList<>();
        List<MuleRecord> otherwiseProcess = new ArrayList<>();
        while (muleElement.peekChild() != null) {
            MuleElement child = muleElement.consumeChild();
            Element childElement = child.getElement();
            if (childElement.getTagName().equals(MuleXMLTag.WHEN.tag())) {
                String condition = childElement.getAttribute("expression");
                List<MuleRecord> whenProcess = new ArrayList<>();

                while (child.peekChild() != null) {
                    MuleElement whenChild = child.consumeChild();
                    MuleRecord r = readBlock(data, whenChild);
                    whenProcess.add(r);
                }

                WhenInChoice whenInChoice = new WhenInChoice(condition, whenProcess);
                whens.add(whenInChoice);
            } else {
                assert childElement.getTagName().equals(MuleXMLTag.OTHERWISE.tag());
                assert otherwiseProcess.isEmpty();
                while (child.peekChild() != null) {
                    MuleElement otherwiseChild = child.consumeChild();
                    MuleRecord r = readBlock(data, otherwiseChild);
                    otherwiseProcess.add(r);
                }
            }
        }
        return new Choice(whens, otherwiseProcess);
    }

    // Scopes
    private static Flow readFlow(Data data, MuleElement mFlowElement) {
        Element flowElement = mFlowElement.getElement();
        String flowName = flowElement.getAttribute("name");

        MuleRecord source = null;
        List<MuleRecord> flowBlocks = new ArrayList<>();

        while (mFlowElement.peekChild() != null) {
            MuleElement child = mFlowElement.consumeChild();
            Element element = child.getElement();
            if (element.getTagName().equals(MuleXMLTag.HTTP_LISTENER.tag())) {
                assert source == null;
                source = readBlock(data, child);
            } else if (element.getTagName().equals(MuleXMLTag.VM_INBOUND_ENDPOINT.tag())) {
                assert source == null;
                source = readBlock(data, child);
            } else {
                MuleRecord muleRec = readBlock(data, child);
                flowBlocks.add(muleRec);
            }
        }

        Optional<MuleRecord> optSource;
        if (source == null) {
            optSource = Optional.empty();
        } else {
            optSource = Optional.of(source);
        }
        return new Flow(flowName, optSource, flowBlocks);
    }

    private static SubFlow readSubFlow(Data data, MuleElement mFlowElement) {
        Element flowElement = mFlowElement.getElement();
        String flowName = flowElement.getAttribute("name");

        List<MuleRecord> flowBlocks = new ArrayList<>();
        while (mFlowElement.peekChild() != null) {
            MuleElement muleElement = mFlowElement.consumeChild();
            MuleRecord muleRec = readBlock(data, muleElement);
            flowBlocks.add(muleRec);
        }

        return new SubFlow(flowName, flowBlocks);
    }

    private static Enricher readEnricher(Data data, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String source = element.getAttribute("source");
        String target = element.getAttribute("target");

        MuleRecord block = null;
        while (muleElement.peekChild() != null) {
            MuleElement muleChild = muleElement.consumeChild();
            assert block == null;
            block = readBlock(data, muleChild);
        }

        Optional<MuleRecord> innerBlock = block != null ? Optional.of(block) : Optional.empty();
        return new Enricher(source, target, innerBlock);
    }

    private static Async readAsync(Data data, MuleElement muleElement) {
        List<MuleRecord> flowBlocks = new ArrayList<>();
        while (muleElement.peekChild() != null) {
            MuleElement child = muleElement.consumeChild();
            MuleRecord muleRec = readBlock(data, child);
            flowBlocks.add(muleRec);
        }

        return new Async(flowBlocks);
    }

    // Transformers
    private static Payload readSetPayload(Data data, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String muleExpr = element.getAttribute("value");
        return new Payload(muleExpr);
    }

    private static SetVariable readSetVariable(Data data, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String varName = element.getAttribute("variableName");
        String val = element.getAttribute("value");
        return new SetVariable(varName, val);
    }

    private static SetSessionVariable readSetSessionVariable(Data data, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String varName = element.getAttribute("variableName");
        String val = element.getAttribute("value");
        return new SetSessionVariable(varName, val);
    }

    private static RemoveVariable readRemoveVariable(Data data, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String varName = element.getAttribute("variableName");
        return new RemoveVariable(Kind.REMOVE_VARIABLE, varName);
    }

    private static RemoveVariable readRemoveSessionVariable(Data data, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String varName = element.getAttribute("variableName");
        return new RemoveVariable(Kind.REMOVE_SESSION_VARIABLE, varName);
    }

    private static ObjectToJson readObjectToJson(Data data, MuleElement muleElement) {
        return new ObjectToJson();
    }

    private static ObjectToString readObjectToString(Data data, MuleElement muleElement) {
        return new ObjectToString();
    }

    // Error handling
    private static CatchExceptionStrategy readCatchExceptionStrategy(Data data, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String name = element.getAttribute("name");
        String when = element.getAttribute("when");

        List<MuleRecord> catchBlocks = new ArrayList<>();
        while (muleElement.peekChild() != null) {
            MuleElement muleChild = muleElement.consumeChild();
            MuleRecord muleRec = readBlock(data, muleChild);
            catchBlocks.add(muleRec);
        }

        return new CatchExceptionStrategy(catchBlocks, when, name);
    }

    private static ChoiceExceptionStrategy readChoiceExceptionStrategy(Data data, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String name = element.getAttribute("name");

        List<CatchExceptionStrategy> catchExceptionStrategyList = new ArrayList<>();
        while (muleElement.peekChild() != null) {
            MuleElement muleChild = muleElement.consumeChild();
            assert muleChild.getElement().getTagName().equals(MuleXMLTag.CATCH_EXCEPTION_STRATEGY.tag());
            // TODO: only catch-exp-strategy is supported for now
            CatchExceptionStrategy catchExceptionStrategy = readCatchExceptionStrategy(data, muleChild);
            catchExceptionStrategyList.add(catchExceptionStrategy);
        }

        return new ChoiceExceptionStrategy(catchExceptionStrategyList, name);
    }

    private static ReferenceExceptionStrategy readReferenceExceptionStrategy(Data data, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String refName = element.getAttribute("ref");
        return new ReferenceExceptionStrategy(refName);
    }

    // HTTP Module
    private static HttpListener readHttpListener(Data data, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String configRef = element.getAttribute("config-ref");
        String resourcePath = element.getAttribute("path");
        String[] allowedMethods = Arrays.stream(getAllowedMethods(element.getAttribute("allowedMethods")))
                .map(String::toLowerCase).toArray(String[]::new);
        return new HttpListener(configRef, resourcePath, allowedMethods);
    }

    private static HttpRequest readHttpRequest(Data data, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String configRef = element.getAttribute("config-ref");
        HTTPRequestConfig httpRequestConfig = data.sharedProjectData.sharedHttpRequestConfigsMap.get(configRef);
        String host = httpRequestConfig.host();
        String port = httpRequestConfig.port();
        String url = String.format("%s:%s", host, port);

        String protocol = httpRequestConfig.protocol();
        if (!protocol.isEmpty()) {
            url = protocol.toLowerCase() + "://" + url;
        }

        String method = element.getAttribute("method").toLowerCase();
        String path = element.getAttribute("path");

        Map<String, String> queryParams = new HashMap<>();
        while (muleElement.peekChild() != null) {
            MuleElement child = muleElement.consumeChild();
            if (child.getElement().getTagName().equals(MuleXMLTag.HTTP_REQEUST_BUILDER.tag())) {
                processQueryParams(queryParams, child);
            } else {
                // TODO: handle all other scenarios
                throw new UnsupportedOperationException();
            }
        }

        return new HttpRequest(configRef, method, url, path, queryParams);
    }

    private static void processQueryParams(Map<String, String> queryParams, MuleElement muleElement) {
        while (muleElement.peekChild() != null) {
            MuleElement child = muleElement.consumeChild();
            Element element = child.getElement();
            assert element.getTagName().equals(MuleXMLTag.HTTP_QUERY_PARAM.tag());
            String paramName = element.getAttribute("paramName");
            String value = element.getAttribute("value");
            queryParams.put(paramName, value);
        }
    }

    private static FlowReference readFlowReference(Data data, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String flowName = element.getAttribute("name");
        return new FlowReference(flowName);
    }

    // VM Connector
    private static VMInboundEndpoint readVMInboundEndpoint(Data data, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String path = element.getAttribute("path");
        String exchangePattern = element.getAttribute("exchange-pattern");
        return new VMInboundEndpoint(path, exchangePattern);
    }

    private static VMOutboundEndpoint readVMOutboundEndpoint(Data data, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String path = element.getAttribute("path");
        String exchangePattern = element.getAttribute("exchange-pattern");
        return new VMOutboundEndpoint(path, exchangePattern);
    }

    // Database Connector
    private static Database readDatabase(Data data, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String configRef = element.getAttribute("config-ref");

        QueryType queryType = null;
        String query = null;

        while (muleElement.peekChild() != null) {
            MuleElement child = muleElement.consumeChild();
            assert queryType == null;

            queryType = getQueryType(child.getElement().getTagName());
            query = readQuery(data, child, queryType);
        }

        if (queryType == null) {
            throw new IllegalStateException("No valid query found in the database block");
        }

        MuleXMLTag muleXMLTag = MuleXMLTag.fromTag(element.getTagName());
        Kind kind = switch (muleXMLTag) {
            case MuleXMLTag.DB_INSERT -> Kind.DB_INSERT;
            case MuleXMLTag.DB_SELECT -> Kind.DB_SELECT;
            case MuleXMLTag.DB_UPDATE -> Kind.DB_UPDATE;
            case MuleXMLTag.DB_DELETE -> Kind.DB_DELETE;
            default -> throw new UnsupportedOperationException();
        };

        return new Database(kind, configRef, queryType, query);
    }

    private static QueryType getQueryType(String tagName) {
        MuleXMLTag muleXMLTag = MuleXMLTag.fromTag(tagName);
        return switch (muleXMLTag) {
            case MuleXMLTag.DB_PARAMETERIZED_QUERY -> QueryType.PARAMETERIZED_QUERY;
            case MuleXMLTag.DB_DYNAMIC_QUERY -> QueryType.DYNAMIC_QUERY;
            case MuleXMLTag.DB_TEMPLATE_QUERY_REF -> QueryType.TEMPLATE_QUERY_REF;
            default -> throw new IllegalStateException("Invalid query type");
        };
    }

    private static String readQuery(Data data, MuleElement muleElement, QueryType queryType) {
        return switch (queryType) {
            case PARAMETERIZED_QUERY -> readDbParameterizedQuery(data, muleElement);
            case DYNAMIC_QUERY -> readDbDynamicQuery(data, muleElement);
            case TEMPLATE_QUERY_REF -> readDbTemplateQueryRef(data, muleElement);
        };
    }

    private static String readDbParameterizedQuery(Data data, MuleElement muleElement) {
        return muleElement.getElement().getTextContent();
    }

    private static String readDbDynamicQuery(Data data, MuleElement muleElement) {
        return muleElement.getElement().getTextContent();
    }

    private static String readDbTemplateQueryRef(Data data, MuleElement muleElement) {
        return muleElement.getElement().getAttribute("name");
    }

    private static UnsupportedBlock readUnsupportedBlock(Data data, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String xmlBlock = ConversionUtils.elementToString(element);
        return new UnsupportedBlock(xmlBlock);
    }

    // Global Elements
    private static HTTPListenerConfig readHttpListenerConfig(Data data, MuleElement muleElement) {
        Element element = muleElement.getElement();
        data.imports.add(new Import(Constants.ORG_BALLERINA, Constants.MODULE_HTTP));
        String listenerName = element.getAttribute("name");
        String host = element.getAttribute("host");
        String port = element.getAttribute("port");
        String basePath = element.getAttribute("basePath");
        return new HTTPListenerConfig(listenerName, basePath, port, host);
    }

    private static HTTPRequestConfig readHttpRequestConfig(Data data, MuleElement muleElement) {
        Element element = muleElement.getElement();
        data.imports.add(new Import(Constants.ORG_BALLERINA, Constants.MODULE_HTTP));
        String configName = element.getAttribute("name");
        String host = element.getAttribute("host");
        String port = element.getAttribute("port");
        String protocol = element.getAttribute("protocol");
        return new HTTPRequestConfig(configName, host, port, protocol);
    }

    private static DbMSQLConfig readDbMySQLConfig(Data data, MuleElement muleElement) {
        Element element = muleElement.getElement();
        data.imports.add(new Import(Constants.ORG_BALLERINAX, Constants.MODULE_MYSQL));
        data.imports.add(new Import(Constants.ORG_BALLERINAX, Constants.MODULE_MYSQL_DRIVER, Optional.of("_")));
        String name = element.getAttribute("name");
        String host = element.getAttribute("host");
        String port = element.getAttribute("port");
        String user = element.getAttribute("user");
        String password = element.getAttribute("password");
        String database = element.getAttribute("database");
        return new DbMSQLConfig(name, host, port, user, password, database);
    }

    private static DbOracleConfig readDbOracleConfig(Data data, MuleElement muleElement) {
        Element element = muleElement.getElement();
        data.imports.add(new Import(Constants.ORG_BALLERINAX, Constants.MODULE_ORACLEDB));
        data.imports.add(new Import(Constants.ORG_BALLERINAX, Constants.MODULE_ORACLEDB_DRIVER, Optional.of("_")));
        String name = element.getAttribute("name");
        String host = element.getAttribute("host");
        String port = element.getAttribute("port");
        String user = element.getAttribute("user");
        String password = element.getAttribute("password");
        String instance = element.getAttribute("instance");
        return new DbOracleConfig(name, host, port, user, password, instance);
    }

    private static DbTemplateQuery readDbTemplateQuery(Data data, MuleElement muleElement) {
        String name = muleElement.getElement().getAttribute("name");
        String query = null;
        List<DbInParam> dbInParams = new ArrayList<>();

        while (muleElement.peekChild() != null) {
            MuleElement child = muleElement.consumeChild();
            Element childElement = child.getElement();

            if (childElement.getTagName().equals("db:parameterized-query")) {
                query = readDbParameterizedQuery(data, child);
            } else if (childElement.getTagName().equals("db:in-param")) {
                DbInParam dbInParam = readDbInParam(data, child);
                dbInParams.add(dbInParam);
            } else {
                throw new UnsupportedOperationException();
            }
        }

        if (query == null) {
            throw new IllegalStateException("No query found in the db:template-query block");
        }
        return new DbTemplateQuery(name, query, dbInParams);
    }

    private static DbInParam readDbInParam(Data data, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String name = element.getAttribute("name");
        String type = element.getAttribute("type");
        Type ty = Type.from(type);
        String defaultValue = element.getAttribute("defaultValue");
        return new DbInParam(name, ty, defaultValue);
    }

    private static TransformMessage readTransformMessage(Data data, MuleElement muleElement) {
        List<MuleModel.TransformMessageElement> transformMessageElements = new ArrayList<>();
        while (muleElement.peekChild() != null) {
            MuleElement child = muleElement.consumeChild();
            Element element = child.getElement();

            MuleXMLTag muleXMLTag = MuleXMLTag.fromTag(child.getElement().getTagName());
            switch (muleXMLTag) {
                case MuleXMLTag.DW_SET_PAYLOAD -> {
                    String resource = element.getAttribute("resource");
                    if (resource.isEmpty()) {
                        transformMessageElements.add(new MuleModel.SetPayloadElement(null, element.getTextContent()));
                    } else {
                        transformMessageElements.add(new MuleModel.SetPayloadElement(resource, null));
                    }
                }
                case MuleXMLTag.DW_INPUT_PAYLOAD -> {
                    String mimeType = element.getAttribute("mimeType");
                    String docSamplePath = element.getAttribute("doc:sample");
                    transformMessageElements.add(new MuleModel.InputPayloadElement(mimeType, docSamplePath));
                }
                case MuleXMLTag.DW_SET_VARIABLE -> {
                    String variableName = element.getAttribute("variableName");
                    String resource = element.getAttribute("resource");
                    String script = null;
                    if (resource.isEmpty()) {
                        script = ((CDATASection) element.getChildNodes().item(0)).getData();
                    }
                    transformMessageElements.add(new MuleModel.SetVariableElement(resource, script, variableName));
                }
                case MuleXMLTag.DW_SET_SESSION_VARIABLE -> {
                    String variableName = element.getAttribute("variableName");
                    String resource = element.getAttribute("resource");
                    String script = null;
                    if (resource.isEmpty()) {
                        script = ((CDATASection) element.getChildNodes().item(0)).getData(); // TODO: fix CDATA cast
                    }
                    transformMessageElements.add(new MuleModel.SetSessionVariableElement(
                            resource, script, variableName));
                }

                default -> throw new UnsupportedOperationException();
            }
        }
        return new TransformMessage(transformMessageElements);
    }
}
