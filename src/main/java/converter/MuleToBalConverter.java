package converter;

import ballerina.BallerinaModel;
import ballerina.CodeGenerator;
import converter.MuleXMLNavigator.MuleElement;
import dataweave.converter.DWConversionStats;
import dataweave.converter.DWReader;
import dataweave.converter.DWUtils;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import mule.MuleModel;
import mule.MuleXMLTag;
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

import static ballerina.BallerinaModel.BallerinaExpression;
import static ballerina.BallerinaModel.BallerinaStatement;
import static ballerina.BallerinaModel.BlockFunctionBody;
import static ballerina.BallerinaModel.ClosedRecordType;
import static ballerina.BallerinaModel.DefaultPackage;
import static ballerina.BallerinaModel.DoStatement;
import static ballerina.BallerinaModel.ElseIfClause;
import static ballerina.BallerinaModel.Function;
import static ballerina.BallerinaModel.IfElseStatement;
import static ballerina.BallerinaModel.Import;
import static ballerina.BallerinaModel.Listener;
import static ballerina.BallerinaModel.ListenerType;
import static ballerina.BallerinaModel.Module;
import static ballerina.BallerinaModel.ModuleTypeDef;
import static ballerina.BallerinaModel.ModuleVar;
import static ballerina.BallerinaModel.ObjectField;
import static ballerina.BallerinaModel.OnFailClause;
import static ballerina.BallerinaModel.Parameter;
import static ballerina.BallerinaModel.RecordField;
import static ballerina.BallerinaModel.Resource;
import static ballerina.BallerinaModel.Service;
import static ballerina.BallerinaModel.Statement;
import static ballerina.BallerinaModel.TextDocument;
import static ballerina.BallerinaModel.TypeBindingPattern;
import static converter.Constants.BAL_ANYDATA_TYPE;
import static converter.Constants.BAL_ERROR_TYPE;
import static converter.Constants.BAL_STRING_TYPE;
import static converter.ConversionUtils.exprFrom;
import static converter.ConversionUtils.stmtFrom;
import static converter.ConversionUtils.typeFrom;
import static converter.ConversionUtils.convertMuleExprToBal;
import static converter.ConversionUtils.convertMuleExprToBalStringLiteral;
import static converter.ConversionUtils.genQueryParam;
import static converter.ConversionUtils.getAllowedMethods;
import static converter.ConversionUtils.getBallerinaAbsolutePath;
import static converter.ConversionUtils.getBallerinaResourcePath;
import static converter.ConversionUtils.getRecordInitValue;
import static converter.ConversionUtils.inferTypeFromBalExpr;
import static converter.ConversionUtils.insertLeadingSlash;
import static converter.ConversionUtils.processQueryParams;
import static mule.MuleModel.CatchExceptionStrategy;
import static mule.MuleModel.ChoiceExceptionStrategy;
import static mule.MuleModel.Choice;
import static mule.MuleModel.DbInParam;
import static mule.MuleModel.DbMSQLConfig;
import static mule.MuleModel.DbTemplateQuery;
import static mule.MuleModel.Database;
import static mule.MuleModel.Enricher;
import static mule.MuleModel.Flow;
import static mule.MuleModel.FlowReference;
import static mule.MuleModel.HttpListener;
import static mule.MuleModel.HttpRequest;
import static mule.MuleModel.Kind;
import static mule.MuleModel.HTTPListenerConfig;
import static mule.MuleModel.LogLevel;
import static mule.MuleModel.Logger;
import static mule.MuleModel.MuleRecord;
import static mule.MuleModel.ObjectToJson;
import static mule.MuleModel.ObjectToString;
import static mule.MuleModel.Payload;
import static mule.MuleModel.QueryType;
import static mule.MuleModel.ReferenceExceptionStrategy;
import static mule.MuleModel.SetVariable;
import static mule.MuleModel.SetSessionVariable;
import static mule.MuleModel.SubFlow;
import static mule.MuleModel.TransformMessage;
import static mule.MuleModel.Type;
import static mule.MuleModel.WhenInChoice;
import static mule.MuleModel.UnsupportedBlock;

public class MuleToBalConverter {

    public static class Data {
        public final SharedProjectData sharedProjectData;

        // Following are per bal file properties

        // Mule global elements
        HashMap<String, HTTPListenerConfig> globalHttpListenerConfigsMap = new LinkedHashMap<>();
        HashMap<String, DbMSQLConfig> globalDbMySQLConfigsMap = new LinkedHashMap<>();
        HashMap<String, DbTemplateQuery> globalDbTemplateQueryMap = new LinkedHashMap<>();
        List<UnsupportedBlock> globalUnsupportedBlocks = new ArrayList<>();
        List<MuleRecord> globalExceptionStrategies = new ArrayList<>();

        // Ballerina global elements
        public HashSet<Import> imports = new LinkedHashSet<>();
        HashSet<String> queryParams = new LinkedHashSet<>();
        HashMap<String, ModuleTypeDef> typeDefMap = new LinkedHashMap<>();
        HashMap<String, ModuleVar> moduleVarMap = new LinkedHashMap<>();
        public List<Function> functions = new ArrayList<>();
        public List<String> utilFunctions = new ArrayList<>();

        public Data(SharedProjectData sharedProjectData) {
            this.sharedProjectData = sharedProjectData;
        }
    }

    public static class SharedProjectData {
        boolean isStandaloneBalFile = false;
        HashMap<String, ModuleTypeDef> contextTypeDefMap = new LinkedHashMap<>();
        HashSet<Import> contextTypeDefImports = new LinkedHashSet<>();
        HashMap<String, HTTPListenerConfig> sharedHttpListenerConfigsMap = new LinkedHashMap<>();
        HashMap<String, DbMSQLConfig> sharedDbMySQLConfigsMap = new LinkedHashMap<>();
        HashMap<String, DbTemplateQuery> sharedDbTemplateQueryMap = new LinkedHashMap<>();

        // Internal variable/method count
        public int invokeEndPointMethodCount = 0;
        public int dwMethodCount = 0;
        public int dbQueryVarCount = 0;
        public int dbStreamVarCount = 0;
        public int dbSelectVarCount = 0;
        public int objectToJsonVarCount = 0;
        public int objectToStringVarCount = 0;
        public int enricherMethodCount = 0;
        public int payloadVarCount = 0;

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
        sharedProjectData.sharedDbMySQLConfigsMap.putAll(data.globalDbMySQLConfigsMap);
        sharedProjectData.sharedDbTemplateQueryMap.putAll(data.globalDbTemplateQueryMap);
        return syntaxTree;
    }

    private static SyntaxTree convertXMLFileToBallerina(MuleXMLNavigator muleXMLNavigator, String xmlFilePath,
                                                        Data data) {
        BallerinaModel ballerinaModel = getBallerinaModel(muleXMLNavigator, data, xmlFilePath);
        return new CodeGenerator(ballerinaModel).generateBalCode();
    }

    public static BallerinaModel getBallerinaModel(String xmlFilePath) {
        MuleXMLNavigator muleXMLNavigator = new MuleXMLNavigator();
        Data data = new Data(new SharedProjectData(muleXMLNavigator));
        return getBallerinaModel(muleXMLNavigator, data, xmlFilePath);
    }

    private static BallerinaModel getBallerinaModel(MuleXMLNavigator muleXMLNavigator, Data data, String xmlFilePath) {
        Element root;
        try {
            root = parseMuleXMLConfigurationFile(xmlFilePath);
        } catch (Exception e) {
            throw new RuntimeException("Error while parsing the mule XML configuration file", e);
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

        return generateBallerinaModel(data, flows, subFlows);
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
        } else if (MuleXMLTag.DB_MYSQL_CONFIG.tag().equals(elementTagName)) {
            DbMSQLConfig dbMSQLConfig = readDbMySQLConfig(data, muleElement);
            data.globalDbMySQLConfigsMap.put(dbMSQLConfig.name(), dbMSQLConfig);
            data.sharedProjectData.sharedDbMySQLConfigsMap.put(dbMSQLConfig.name(), dbMSQLConfig);
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

    private static BallerinaModel generateBallerinaModel(Data data, List<Flow> flows, List<SubFlow> subFlows) {
        List<Service> services = new ArrayList<>();
        List<Flow> privateFlows = new ArrayList<>();

        for (Flow flow : flows) {
            Optional<MuleRecord> source = flow.source();
            if (source.isEmpty()) {
                privateFlows.add(flow);
                continue;
            }

            MuleRecord src = source.get();
            assert src.kind() == Kind.HTTP_LISTENER;

            SharedProjectData.FlowInfo flowInfo = new SharedProjectData.FlowInfo(flow.name(), Context.HTTP_LISTENER);
            data.sharedProjectData.flowInfoMap.put(flow.name(), flowInfo);
            data.sharedProjectData.currentFlowInfo = flowInfo;

            data.sharedProjectData.inboundProperties.add(
                    new SharedProjectData.TypeAndNamePair(Constants.HTTP_RESPONSE_TYPE, "response"));
            data.sharedProjectData.contextTypeDefImports.add(Constants.HTTP_MODULE_IMPORT);

            // Create a service from the flow
            Service service = genBalService(data, (HttpListener) src, flow.flowBlocks());

            createContextInfoHoldingDataStructures(data.sharedProjectData);

            // gen init function
            ModuleTypeDef moduleTypeDef = data.sharedProjectData.contextTypeDefMap.get(Constants.CONTEXT_RECORD_TYPE);
            ClosedRecordType contextRecord = (ClosedRecordType) moduleTypeDef.type();
            String recordInitValue = getRecordInitValue(contextRecord);
            List<Statement> initBody = Collections.singletonList(stmtFrom(
                    String.format("self.%s = %s;", Constants.CONTEXT_REFERENCE, recordInitValue)));
            Function initFunc = new Function("init", Collections.emptyList(), initBody);

            // Modify service with init function
            service = new Service(service.basePath(), service.listenerRefs(), Optional.of(initFunc),
                    service.resources(), service.functions(), service.pathParams(), service.queryParams(),
                    service.fields());
            services.add(service);
        }

        Set<Function> functions = new HashSet<>();

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
            listeners.add(new Listener(ListenerType.HTTP, httpListenerConfig.name(), httpListenerConfig.port(),
                    httpListenerConfig.config()));
        }

        // Add module vars
        List<ModuleVar> moduleVars = new ArrayList<>();
        for (DbMSQLConfig dbMSQLConfig : data.globalDbMySQLConfigsMap.values()) {
            var balExpr = exprFrom(String.format("check new (\"%s\", \"%s\", \"%s\", \"%s\", %s)",
                    dbMSQLConfig.host(), dbMSQLConfig.user(), dbMSQLConfig.password(), dbMSQLConfig.database(),
                    dbMSQLConfig.port()));
            moduleVars.add(new ModuleVar(dbMSQLConfig.name(), typeFrom(Constants.MYSQL_CLIENT_TYPE), balExpr));
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

        if (data.sharedProjectData.contextTypeDefMap.get(Constants.CONTEXT_RECORD_TYPE) == null) {
            // We reach here, when there is no source
            createContextInfoHoldingDataStructures(data.sharedProjectData);
        }

        List<ModuleTypeDef> typeDefs;
        if (data.sharedProjectData.isStandaloneBalFile) {
            data.sharedProjectData.contextTypeDefMap.putAll(data.typeDefMap);
            typeDefs = data.sharedProjectData.contextTypeDefMap.values().stream().toList();
            data.imports.addAll(data.sharedProjectData.contextTypeDefImports);
        } else {
            typeDefs = data.typeDefMap.values().stream().toList();
        }
        return createBallerinaModel(new ArrayList<>(data.imports), typeDefs,
                moduleVars, listeners, services, functions.stream().toList(), comments);
    }

    static void createContextInfoHoldingDataStructures(SharedProjectData sharedProjectData) {
        List<RecordField> contextRecFields = new ArrayList<>();
        contextRecFields.add(new RecordField(BAL_ANYDATA_TYPE, "payload", false));

        if (!sharedProjectData.flowVars.isEmpty()) {
            contextRecFields.add(new RecordField(typeFrom(Constants.FLOW_VARS_TYPE), Constants.FLOW_VARS_REF, false));
            List<RecordField> flowVarRecFields = new ArrayList<>();
            for (SharedProjectData.TypeAndNamePair tnp : sharedProjectData.flowVars) {
                flowVarRecFields.add(new RecordField(typeFrom(tnp.type), tnp.name, true));
            }
            ClosedRecordType flowVarsRecord = new ClosedRecordType(flowVarRecFields);
            sharedProjectData.contextTypeDefMap.put("FlowVars", new ModuleTypeDef(flowVarsRecord, "FlowVars"));
        }

        if (!sharedProjectData.sessionVars.isEmpty()) {
            contextRecFields.add(new RecordField(typeFrom(Constants.SESSION_VARS_TYPE), Constants.SESSION_VARS_REF,
                    false));
            List<RecordField> sessionVarRecFields = new ArrayList<>();
            for (SharedProjectData.TypeAndNamePair tnp : sharedProjectData.sessionVars) {
                sessionVarRecFields.add(new RecordField(typeFrom(tnp.type), tnp.name, true));
            }
            ClosedRecordType sessionVarsRecord = new ClosedRecordType(sessionVarRecFields);
            sharedProjectData.contextTypeDefMap.put("SessionVars", new ModuleTypeDef(sessionVarsRecord, "SessionVars"));
        }

        if (!sharedProjectData.inboundProperties.isEmpty()) {
            contextRecFields.add(new RecordField(typeFrom(Constants.INBOUND_PROPERTIES_TYPE), "inboundProperties",
                    false));
            List<RecordField> inboundPropRecordFields = new ArrayList<>();
            for (SharedProjectData.TypeAndNamePair tnp : sharedProjectData.inboundProperties) {
                inboundPropRecordFields.add(new RecordField(typeFrom(tnp.type), tnp.name, false));
            }
            ClosedRecordType inboundPropertiesRecord = new ClosedRecordType(inboundPropRecordFields);
            sharedProjectData.contextTypeDefMap.put(Constants.INBOUND_PROPERTIES_TYPE,
                    new ModuleTypeDef(inboundPropertiesRecord, Constants.INBOUND_PROPERTIES_TYPE));
        }

        ClosedRecordType contextRecord = new ClosedRecordType(contextRecFields);
        sharedProjectData.contextTypeDefMap.put(Constants.CONTEXT_RECORD_TYPE, new ModuleTypeDef(contextRecord,
                Constants.CONTEXT_RECORD_TYPE));
    }

    private static void genBalFuncForGlobalExceptionStrategy(Data data, MuleRecord muleRecord,
                                                             Set<Function> functions) {
        List<Statement> body;
        String name;
        if (muleRecord instanceof CatchExceptionStrategy catchExceptionStrategy) {
            name = catchExceptionStrategy.name();
            body = getCatchExceptionBody(data, (CatchExceptionStrategy) muleRecord);
        } else if (muleRecord instanceof ChoiceExceptionStrategy choiceExceptionStrategy) {
            name = choiceExceptionStrategy.name();
            body = getChoiceExceptionBody(data, (ChoiceExceptionStrategy) muleRecord);
        } else {
            throw new UnsupportedOperationException("exception strategy not supported");
        }

        String methodName = ConversionUtils.escapeSpecialCharacters(name);
        // TODO: consider passing down context
        List<Parameter> parameters = new ArrayList<>();
        parameters.add(new Parameter("e", BAL_ERROR_TYPE));
        Function function = new Function(methodName, parameters.stream().toList(), body);
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
        addEndOfMethodStatements(data.sharedProjectData.currentFlowInfo,  new BlockFunctionBody(body));

        String methodName = ConversionUtils.escapeSpecialCharacters(flowName);
        Function function = new Function(methodName, Constants.FUNC_PARAMS_WITH_CONTEXT, body);
        functions.add(function);
        data.sharedProjectData.flowToGenMethodMap.put(flowName, function);
    }

    private static void addEndOfMethodStatements(SharedProjectData.FlowInfo flowInfo, BlockFunctionBody body) {
        if (flowInfo.context == Context.HTTP_LISTENER) {
            if (flowInfo.currentPayload != DEFAULT_PAYLOAD) {
                // the payload has been updated
                body.statements().add(stmtFrom(String.format("%s.response.setPayload(%s);",
                        Constants.INBOUND_PROPERTIES_FIELD_ACCESS, getSetPayloadArg(flowInfo.currentPayload))));
            }
        }
    }

    private static void genBalFuncsFromPrivateFlows(Data data, List<Flow> privateFlows, Set<Function> functions) {
        for (Flow privateFlow : privateFlows) {
            genBalFuncForPrivateOrSubFlow(data, functions, privateFlow.name(), privateFlow.flowBlocks());
        }
    }

    private static Service genBalService(Data data, HttpListener httpListener, List<MuleRecord> flowBlocks) {
        String resourcePath = getBallerinaResourcePath(httpListener.resourcePath());
        String[] resourceMethodNames = httpListener.allowedMethods();
        List<String> listenerRefs = Collections.singletonList(httpListener.configRef());
        String muleBasePath = data.sharedProjectData.sharedHttpListenerConfigsMap.get(httpListener.configRef())
                .basePath();
        String basePath = getBallerinaAbsolutePath(muleBasePath);

        // Add services
        List<Parameter> queryPrams = new ArrayList<>();
        for (String qp : data.queryParams) {
            // TODO: revisit
            queryPrams.add(new Parameter(qp, BAL_STRING_TYPE, Optional.of(exprFrom("\"null\""))));
        }

        // resource method return statement
        // TODO: add test case for query params
//        String functionArgs = String.join(",", queryPrams.stream()
//                .map(p -> String.format("%s", p.name())).toList());
        String invokeEndPointMethodName = String.format(Constants.METHOD_NAME_HTTP_ENDPOINT_TEMPLATE,
                data.sharedProjectData.invokeEndPointMethodCount++);
        var resourceReturnStmt = stmtFrom(String.format("return self.%s(self.%s);", invokeEndPointMethodName,
                Constants.CONTEXT_REFERENCE));

        // Add service resources
        List<Resource> resources = new ArrayList<>();
        String returnType = Constants.HTTP_RESOURCE_RETURN_TYPE_DEFAULT;
        data.imports.add(Constants.HTTP_MODULE_IMPORT);
        for (String resourceMethodName : resourceMethodNames) {
            resourceMethodName = resourceMethodName.toLowerCase();
            Resource resource = new Resource(resourceMethodName,
                    resourcePath, queryPrams, Optional.of(returnType), Collections.singletonList(resourceReturnStmt));
            resources.add(resource);
        }

        List<Statement> body = genFuncBodyStatements(data, flowBlocks);

        if (data.sharedProjectData.currentFlowInfo.currentPayload != DEFAULT_PAYLOAD) {
            // the payload has been updated
            body.add(stmtFrom(String.format("%s.response.setPayload(%s);", Constants.INBOUND_PROPERTIES_FIELD_ACCESS,
                    getSetPayloadArg(data.sharedProjectData.currentFlowInfo.currentPayload))));
        }

        // Add return statement
        body.add(stmtFrom(String.format("return %s.response;", Constants.INBOUND_PROPERTIES_FIELD_ACCESS)));

        // Add service functions
        List<Function> functions = new ArrayList<>();
        functions.add(new Function(Optional.of("private"), invokeEndPointMethodName, Collections.singletonList(
                new Parameter(Constants.CONTEXT_REFERENCE, typeFrom(Constants.CONTEXT_RECORD_TYPE))),
                Optional.of(returnType),  new BlockFunctionBody(body)));

        // Add service fields
        List<ObjectField> fields = Collections.singletonList(
                new ObjectField(typeFrom(Constants.CONTEXT_RECORD_TYPE), Constants.CONTEXT_REFERENCE));
        return new Service(basePath, listenerRefs, Optional.empty(), resources, functions, Collections.emptyList(),
                Collections.emptyList(), fields);
    }

    private static String getSetPayloadArg(PayloadVarInfo payloadVarInfo) {
        if (isSetPayloadAllowedType(payloadVarInfo.type())) {
            return payloadVarInfo.nameReference();
        } else {
            return String.format("%s.toString()", payloadVarInfo.nameReference());
        }
    }

    private static boolean isSetPayloadAllowedType(String type) {
        return switch (type) {
            case "string", "xml", "json", "byte[]" -> true;
            default -> false;
        };
    }

    private static List<Statement> genFuncBodyStatements(Data data, List<MuleRecord> flowBlocks) {
        // Add function body statements
        List<Statement> body = new ArrayList<>();

        // Read flow blocks
        for (MuleRecord record : flowBlocks) {
            List<Statement> s = convertToStatements(data, record);
            // TODO: handle properly
            if (s.size() == 1 && s.getFirst() instanceof DoStatement doStatement) {
                body = new ArrayList<>(Collections.singletonList(new DoStatement(body, doStatement.onFailClause())));
                continue;
            }
            body.addAll(s);
        }
        return body;
    }

    protected static BallerinaModel createBallerinaModel(List<Import> imports, List<ModuleTypeDef> moduleTypeDefs,
                                                         List<ModuleVar> moduleVars, List<Listener> listeners,
                                                         List<Service> services, List<Function> functions,
                                                         List<String> comments) {
        // TODO: figure out package, module names properly
        String projectName = "muleDemoProject";
        String moduleName = "muleDemoModule";
        String textDocumentName = "muleDemoTextDocument";
        TextDocument textDocument = new TextDocument(textDocumentName, imports, moduleTypeDefs, moduleVars, listeners,
                services, functions, comments);
        Module module = new Module(moduleName, Collections.singletonList(textDocument));
        return new BallerinaModel(new DefaultPackage(projectName, projectName, "0.1.0"),
                Collections.singletonList(module));
    }

    private static MuleRecord readBlock(Data data, MuleElement muleElement) {
        MuleXMLTag muleXMLTag = MuleXMLTag.fromTag(muleElement.getElement().getTagName());
        switch (muleXMLTag) {
            // Source
            case MuleXMLTag.HTTP_LISTENER -> {
                return readHttpListener(data, muleElement);
            }
            // Process Items
            case MuleXMLTag.LOGGER -> {
                return readLogger(data, muleElement);
            }
            case MuleXMLTag.SET_VARIABLE -> {
                return readSetVariable(data, muleElement);
            }
            case MuleXMLTag.SET_SESSION_VARIABLE -> {
                return readSetSessionVariable(data, muleElement);
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
            case MuleXMLTag.CATCH_EXCEPTION_STRATEGY -> {
                return readCatchExceptionStrategy(data, muleElement);
            }
            case MuleXMLTag.CHOICE_EXCEPTION_STRATEGY -> {
                return readChoiceExceptionStrategy(data, muleElement);
            }
            case MuleXMLTag.REFERENCE_EXCEPTION_STRATEGY -> {
                return readReferenceExceptionStrategy(data, muleElement);
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
                    getBallerinaLogFunction(lg.level()), convertMuleExprToBalStringLiteral(lg.message()))));
            case Payload payload -> {
                statementList.add(stmtFrom("\n\n// set payload\n"));
                String pyld = convertMuleExprToBal(payload.expr());
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
                String ifCondition = convertMuleExprToBal(firstWhen.condition());
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
                    ElseIfClause elseIfClause = new ElseIfClause(exprFrom(convertMuleExprToBal(when.condition())),
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
                String varName = ConversionUtils.escapeSpecialCharacters(setVariable.variableName());
                String balExpr = convertMuleExprToBal(setVariable.value());
                String type = inferTypeFromBalExpr(balExpr);

                data.sharedProjectData.flowVars.add(new SharedProjectData.TypeAndNamePair(type, varName));
                statementList.add(stmtFrom(String.format("%s.%s = %s;", Constants.FLOW_VARS_FIELD_ACCESS,
                        varName, balExpr)));
            }
            case SetSessionVariable setSessionVariable -> {
                String varName = ConversionUtils.escapeSpecialCharacters(setSessionVariable.variableName());
                String balExpr = convertMuleExprToBal(setSessionVariable.value());
                String type = inferTypeFromBalExpr(balExpr);

                data.sharedProjectData.sessionVars.add(new SharedProjectData.TypeAndNamePair(type, varName));
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
                data.sharedProjectData.currentFlowInfo.currentPayload = new PayloadVarInfo("json", objToJsonVarName);
            }
            case ObjectToString objectToString -> {
                statementList.add(stmtFrom("\n\n// string transformation\n"));
                String objToStringVarName = String.format(Constants.VAR_OBJ_TO_STRING_TEMPLATE,
                        data.sharedProjectData.objectToStringVarCount++);
                statementList.add(stmtFrom(String.format("string %s = %s.toString();", objToStringVarName,
                        data.sharedProjectData.currentFlowInfo.currentPayload.nameReference())));

                // object to string transformer implicitly sets the payload
                data.sharedProjectData.currentFlowInfo.currentPayload = new PayloadVarInfo("string",
                        objToStringVarName);
            }
            case HttpRequest httpRequest -> {
                List<Statement> statements = new ArrayList<>();
                String path = httpRequest.path();
                String method = httpRequest.method();
                String url = httpRequest.url();
                Map<String, String> queryParams = httpRequest.queryParams();

                statements.add(stmtFrom(String.format("http:Client %s = check new(\"%s\");", Constants.VAR_CLIENT,
                        url)));
                statements.add(stmtFrom(String.format("http:Response %s = check %s->%s/.%s(%s);",
                        Constants.VAR_CLIENT_GET, Constants.VAR_CLIENT, path, method.toLowerCase(),
                        genQueryParam(queryParams))));
                statementList.addAll(statements);
            }
            case FlowReference flowReference -> {
                String flowName = flowReference.flowName();
                String funcRef = ConversionUtils.escapeSpecialCharacters(flowName);

                if (data.sharedProjectData.currentFlowInfo.context == Context.HTTP_LISTENER) {
                    Function method = data.sharedProjectData.flowToGenMethodMap.get(flowName);
                    if (method == null) {
                        // Set the flow context to Http listener
                        SharedProjectData.FlowInfo flowInfo = new SharedProjectData.FlowInfo(flowName,
                                Context.HTTP_LISTENER);
                        data.sharedProjectData.flowInfoMap.put(flowName, flowInfo);
                    } else {
                        // Means we have analyzed the flow already
                        SharedProjectData.FlowInfo flowInfo = data.sharedProjectData.flowInfoMap.get(flowName);
                        flowInfo.context = Context.HTTP_LISTENER;
                        addEndOfMethodStatements(flowInfo, (BlockFunctionBody)
                                data.sharedProjectData.flowToGenMethodMap.get(flowName).body());
                    }
                }

                statementList.add(stmtFrom(String.format("%s(%s);", funcRef, Constants.CONTEXT_REFERENCE)));
            }
            case Enricher enricher -> {
                // TODO: support no source
                String source = convertMuleExprToBal(enricher.source());
                String target = convertMuleExprToBal(enricher.target());

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

                    String methodName = String.format(Constants.METHOD_NAME_ENRICHER_TEMPLATE,
                            data.sharedProjectData.enricherMethodCount);
                    Function func = new Function(Optional.empty(), methodName, Constants.FUNC_PARAMS_WITH_CONTEXT,
                            Optional.of("string?"),  new BlockFunctionBody(enricherStmts));
                    data.functions.add(func);

                    enricherStmts.add(stmtFrom(String.format("return %s;", source)));
                    statementList.add(stmtFrom(String.format("%s = %s(%s.clone());", target,
                            String.format(Constants.METHOD_NAME_ENRICHER_TEMPLATE,
                                    data.sharedProjectData.enricherMethodCount++), Constants.CONTEXT_REFERENCE)));
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
                TypeBindingPattern typeBindingPattern = new BallerinaModel.TypeBindingPattern(BAL_ERROR_TYPE, "e");
                OnFailClause onFailClause = new OnFailClause(onFailBody, typeBindingPattern);
                DoStatement doStatement = new DoStatement(Collections.emptyList(), onFailClause);
                statementList.add(doStatement);
            }
            case ReferenceExceptionStrategy referenceExceptionStrategy -> {
                String refName = referenceExceptionStrategy.refName();
                String funcRef = ConversionUtils.escapeSpecialCharacters(refName);
                BallerinaStatement funcCallStmt = stmtFrom(String.format("%s(%s);", funcRef, "e"));
                List<Statement> onFailBody = Collections.singletonList(funcCallStmt);
                TypeBindingPattern typeBindingPattern = new TypeBindingPattern(BAL_ERROR_TYPE, "e");
                OnFailClause onFailClause = new OnFailClause(onFailBody, typeBindingPattern);
                DoStatement doStatement = new DoStatement(Collections.emptyList(), onFailClause);
                statementList.add(doStatement);
            }
            case Database database -> {
                data.imports.add(new Import(Constants.ORG_BALLERINA, Constants.MODULE_SQL, Optional.empty()));
                String streamConstraintType = Constants.GENERIC_RECORD_TYPE_REF;
                data.typeDefMap.put(streamConstraintType, new ModuleTypeDef(typeFrom(Constants.GENERIC_RECORD_TYPE),
                        streamConstraintType));

                statementList.add(stmtFrom("\n\n// database operation\n"));
                String dbQueryVarName = String.format(Constants.VAR_DB_QUERY_TEMPLATE,
                        data.sharedProjectData.dbQueryVarCount++);
                statementList.add(stmtFrom(String.format("%s %s = %s;",
                        Constants.SQL_PARAMETERIZED_QUERY_TYPE, dbQueryVarName,
                        database.queryType() == QueryType.TEMPLATE_QUERY_REF ? database.query() :
                                String.format("`%s`", database.query()))));

                String dbStreamVarName = String.format(Constants.VAR_DB_STREAM_TEMPLATE,
                        data.sharedProjectData.dbStreamVarCount++);
                statementList.add(stmtFrom(String.format("%s %s= %s->query(%s);",
                        String.format(Constants.DB_QUERY_DEFAULT_TEMPLATE, streamConstraintType),
                        dbStreamVarName, database.configRef(), dbQueryVarName)));

                if (database.kind() == Kind.DB_SELECT) {
                    String dbSelectVarName = String.format(Constants.VAR_DB_SELECT_TEMPLATE,
                            data.sharedProjectData.dbSelectVarCount++);
                    statementList.add(stmtFrom(
                            String.format("%s[] %s = check from %s %s in %s select %s;", streamConstraintType,
                                    dbSelectVarName, streamConstraintType, Constants.VAR_ITERATOR, dbStreamVarName,
                                    Constants.VAR_ITERATOR)));

                    // db:select implicitly sets the payload
                    data.sharedProjectData.currentFlowInfo.currentPayload = new PayloadVarInfo(String.format("%s[]",
                            streamConstraintType),
                            dbSelectVarName);
                }
            }
            case TransformMessage transformMessage -> {
                DWReader.processDWElements(transformMessage.children(), data, statementList);
                if (!data.sharedProjectData.currentFlowInfo.context.equals(Context.DEFAULT)) {
                    statementList.add(stmtFrom(String.format("%s.response.setPayload(%s);",
                            Constants.INBOUND_PROPERTIES_FIELD_ACCESS, DWUtils.DATAWEAVE_OUTPUT_VARIABLE_NAME)));
                }
            }
            case UnsupportedBlock unsupportedBlock -> {
                String comment = ConversionUtils.wrapElementInUnsupportedBlockComment(unsupportedBlock.xmlBlock());
                // TODO: comment is not a statement. Find a better way to handle this
                // This works for now because we concatenate and create a body block `{ stmts }` before parsing.
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
        List<CatchExceptionStrategy> catchExceptionStrategies =
                choiceExceptionStrategy.catchExceptionStrategyList();
        assert !catchExceptionStrategies.isEmpty();

        CatchExceptionStrategy firstCatch = catchExceptionStrategies.getFirst();
        BallerinaExpression ifCondition = exprFrom(convertMuleExprToBal(firstCatch.when()));
        List<Statement> ifBody = convertMuleRecToBalStatements(data, firstCatch.catchBlocks());

        List<ElseIfClause> elseIfClauses = new ArrayList<>();
        for (int i = 1; i < catchExceptionStrategies.size() - 1; i++) {
            CatchExceptionStrategy catchExpStrgy = catchExceptionStrategies.get(i);
            List<Statement> elseIfBody = convertMuleRecToBalStatements(data, catchExpStrgy.catchBlocks());
            ElseIfClause elseIfClause = new ElseIfClause(exprFrom(convertMuleExprToBal(catchExpStrgy.when())),
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
        return Collections.singletonList(ifElseStmt);
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
        LogLevel logLevel;
        switch (level) {
            case "DEBUG" -> {
                logLevel = LogLevel.DEBUG;
            }
            case "ERROR" -> {
                logLevel = LogLevel.ERROR;
            }
            case "INFO" -> {
                logLevel = LogLevel.INFO;
            }
            case "TRACE" -> {
                logLevel = LogLevel.TRACE;
            }
            case "WARN" -> {
                logLevel = LogLevel.WARN;
            }
            default -> throw new IllegalStateException();
        }
        return new Logger(message, logLevel);
    }

    // Flow Control
    private static Choice readChoice(Data data, MuleElement muleElement) {
        List<WhenInChoice> whens = new ArrayList<>();
        List<MuleRecord> otherwiseProcess = null;
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
                assert otherwiseProcess == null;
                otherwiseProcess = new ArrayList<>();
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
        String method = element.getAttribute("method").toLowerCase();
        String url = element.getAttribute("url").toLowerCase();
        String path = element.getAttribute("path").toLowerCase();

        Map<String, String> queryParams = new HashMap<>();
        while (muleElement.peekChild() != null) {
            MuleElement child = muleElement.consumeChild();
            if (child.getElement().getTagName().equals(MuleXMLTag.HTTP_QUERY_PARAMS.tag())) {
                Element queryParamsElement = child.getElement();
                CDATASection cdataSection = (CDATASection) queryParamsElement.getChildNodes().item(0);
                queryParams = processQueryParams(cdataSection.getData().trim());
            } else {
                // TODO: handle all other scenarios
                throw new UnsupportedOperationException();
            }
        }

        return new HttpRequest(method, url, path, queryParams);
    }

    private static FlowReference readFlowReference(Data data, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String flowName = element.getAttribute("name");
        return new FlowReference(flowName);
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
        data.imports.add(new Import(Constants.ORG_BALLERINA, Constants.MODULE_HTTP, Optional.empty()));
        String listenerName = element.getAttribute("name");
        String host = element.getAttribute("host");
        String port = element.getAttribute("port");
        String basePath = insertLeadingSlash(element.getAttribute("basePath"));
        HashMap<String, String> config = new HashMap<>(Collections.singletonMap("host", host));
        return new HTTPListenerConfig(listenerName, basePath, port, config);
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
