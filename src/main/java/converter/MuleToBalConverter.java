package converter;

import ballerina.BallerinaModel;
import ballerina.CodeGenerator;
import dataweave.converter.DWReader;
import dataweave.converter.DWUtils;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import mule.Constants;
import mule.MuleModel;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
import static ballerina.BallerinaModel.OnFailClause;
import static ballerina.BallerinaModel.Parameter;
import static ballerina.BallerinaModel.Resource;
import static ballerina.BallerinaModel.Service;
import static ballerina.BallerinaModel.Statement;
import static ballerina.BallerinaModel.TextDocument;
import static converter.ConversionUtils.convertToBallerinaExpression;
import static converter.ConversionUtils.genQueryParam;
import static converter.ConversionUtils.getAllowedMethods;
import static converter.ConversionUtils.getBallerinaAbsolutePath;
import static converter.ConversionUtils.getBallerinaResourcePath;
import static converter.ConversionUtils.insertLeadingSlash;
import static converter.ConversionUtils.processQueryParams;
import static mule.MuleModel.CatchExceptionStrategy;
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
import static mule.MuleModel.SetVariable;
import static mule.MuleModel.SetSessionVariable;
import static mule.MuleModel.SubFlow;
import static mule.MuleModel.TransformMessage;
import static mule.MuleModel.Type;
import static mule.MuleModel.WhenInChoice;
import static mule.MuleModel.UnsupportedBlock;

public class MuleToBalConverter {

    public static class Data {
        // Mule global elements
        HashMap<String, HTTPListenerConfig> globalHttpListenerConfigsMap = new LinkedHashMap<>();
        HashMap<String, DbMSQLConfig> globalDbMySQLConfigsMap = new LinkedHashMap<>();
        HashMap<String, DbTemplateQuery> globalDbTemplateQueryMap = new LinkedHashMap<>();
        List<UnsupportedBlock> globalUnsupportedBlocks = new ArrayList<>();

        // Ballerina global elements
        public HashMap<String, ModuleTypeDef> typeDef = new HashMap<>();
        public HashSet<Import> imports = new HashSet<>();
        HashSet<String> queryParams = new HashSet<>();
        HashMap<String, ModuleTypeDef> typeDefMap = new LinkedHashMap<>();
        HashMap<String, ModuleVar> moduleVarMap = new LinkedHashMap<>();
        public List<Function> functions = new ArrayList<>();

        // Internal variable/method count
        public int dwMethodCount = 0;
        public int dbQueryVarCount = 0;
        public int dbStreamVarCount = 0;
        public int dbSelectVarCount = 0;
        public int objectToJsonVarCount = 0;
        public int objectToStringVarCount = 0;
        public int enricherMethodCount = 0;
        public int payloadVarCount = 0;

        // We don't know all the params passing down to funcs until we fully read the mule config.
        // Therefore, we need to keep a track of the params needed.
        private final HashMap<String, HashSet<Parameter>> functionParamMap = new HashMap<>();

        public void addFuncParam(String funcName, Parameter param) {
            HashSet<Parameter> parameters = functionParamMap.get(funcName);
            if (parameters == null) {
                functionParamMap.put(funcName, new HashSet<>(Collections.singletonList(param)));
            } else {
                parameters.add(param);
            }
        }

        // Data analyze attributes
        Map<String, FlowInfo> flowInfoMap = new HashMap<>();
        Map<String, Function> flowToGenMethodMap = new HashMap<>();
        FlowInfo currentFlowInfo = null;

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
        data.flowInfoMap.putIfAbsent(flowName, new Data.FlowInfo(flowName));
    }

    public enum Context {
        DEFAULT,
        HTTP_LISTENER
    }

    static final PayloadVarInfo DEFAULT_PAYLOAD = new PayloadVarInfo("()", "null");

    public record PayloadVarInfo(String type, String nameReference) {
    }

    public static SyntaxTree convertToBallerina(String xmlFilePath) {
        BallerinaModel ballerinaModel = getBallerinaModel(xmlFilePath);
        return new CodeGenerator(ballerinaModel).generateBalCode();
    }

    public static BallerinaModel getBallerinaModel(String xmlFilePath) {
        Element root;
        try {
            root = parseMuleXMLConfigurationFile(xmlFilePath);
        } catch (Exception e) {
            throw new RuntimeException("Error while parsing the mule XML configuration file", e);
        }

        List<Flow> flows = new ArrayList<>();
        List<SubFlow> subFlows = new ArrayList<>();

        Data data = new Data();
        NodeList childNodes = root.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            Element element = (Element) node;
            String elementTagName = element.getTagName();

            if (Constants.FLOW.equals(elementTagName)) {
                Flow flow = readFlow(data, element);
                flows.add(flow);
                continue;
            } else if (Constants.SUB_FLOW.equals(elementTagName)) {
                SubFlow subFlow = readSubFlow(data, element);
                subFlows.add(subFlow);
                continue;
            }

            readGlobalConfigElement(data, element);
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

    private static void readGlobalConfigElement(Data data, Element element) {
        String elementTagName = element.getTagName();
        if (Constants.HTTP_LISTENER_CONFIG.equals(elementTagName)) {
            HTTPListenerConfig httpListenerConfig = readHttpListenerConfig(data, element);
            data.globalHttpListenerConfigsMap.put(httpListenerConfig.name(), httpListenerConfig);
        } else if (Constants.DB_MYSQL_CONFIG.equals(elementTagName)) {
            DbMSQLConfig dbMSQLConfig = readDbMySQLConfig(data, element);
            data.globalDbMySQLConfigsMap.put(dbMSQLConfig.name(), dbMSQLConfig);
        } else if (Constants.DB_TEMPLATE_QUERY.equals(elementTagName)) {
            DbTemplateQuery dbTemplateQuery = readDbTemplateQuery(data, element);
            data.globalDbTemplateQueryMap.put(dbTemplateQuery.name(), dbTemplateQuery);
        } else {
            UnsupportedBlock unsupportedBlock = readUnsupportedBlock(data, element);
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

            Data.FlowInfo flowInfo = new Data.FlowInfo(flow.name(), Context.HTTP_LISTENER);
            data.flowInfoMap.put(flow.name(), flowInfo);
            data.currentFlowInfo = flowInfo;

            // Create a service from the flow
            Service service = genBalService(data, (HttpListener) src, flow.flowBlocks());
            services.add(service);
        }

        Set<Function> functions = new HashSet<>();

        // Create functions for private flows
        genBalFuncsFromPrivateFlows(data, privateFlows, functions);

        // Create functions for sub-flows
        genBalFuncsFromSubFlows(data, subFlows, functions);
        functions.addAll(data.functions);

        // Add global listeners
        List<Listener> listeners = new ArrayList<>();
        for (HTTPListenerConfig httpListenerConfig : data.globalHttpListenerConfigsMap.values()) {
            listeners.add(new Listener(ListenerType.HTTP, httpListenerConfig.name(), httpListenerConfig.port(),
                    httpListenerConfig.config()));
        }

        // Add module vars
        List<ModuleVar> moduleVars = new ArrayList<>();
        for (DbMSQLConfig dbMSQLConfig : data.globalDbMySQLConfigsMap.values()) {
            var balExpr = new BallerinaExpression(String.format("check new (\"%s\", \"%s\", \"%s\", \"%s\", %s)",
                    dbMSQLConfig.host(), dbMSQLConfig.user(), dbMSQLConfig.password(), dbMSQLConfig.database(),
                    dbMSQLConfig.port()));
            moduleVars.add(new ModuleVar(dbMSQLConfig.name(), Constants.MYSQL_CLIENT_TYPE, balExpr));
        }

        for (DbTemplateQuery dbTemplateQuery : data.globalDbTemplateQueryMap.values()) {
            var balExpr = new BallerinaExpression(String.format("`%s`", dbTemplateQuery.parameterizedQuery()));
            moduleVars.add(new ModuleVar(dbTemplateQuery.name(), Constants.SQL_PARAMETERIZED_QUERY_TYPE, balExpr));
        }

        moduleVars.addAll(data.moduleVarMap.values());

        List<String> comments = new ArrayList<>();
        for (UnsupportedBlock unsupportedBlock : data.globalUnsupportedBlocks) {
            String comment = ConversionUtils.wrapElementInUnsupportedBlockComment(unsupportedBlock.xmlBlock());
            comments.add(comment);
        }

        // Update function params
        List<Function> funcs = new ArrayList<>(functions.size());
        for (Function function : functions) {
            HashSet<Parameter> parameters = data.functionParamMap.get(function.methodName());
            if (parameters == null) {
                funcs.add(function);
            } else {
                funcs.add(new Function(function.visibilityQualifier(), function.methodName(),
                        parameters.stream().toList(), function.returnType(), function.body()));
            }
        }

        return createBallerinaModel(new ArrayList<>(data.imports), data.typeDefMap.values().stream().toList(),
                moduleVars, listeners, services, funcs, comments);
    }

    private static void genBalFuncsFromSubFlows(Data data, List<SubFlow> subFlows, Set<Function> functions) {
        for (SubFlow subFlow : subFlows) {
            genBalFuncForPrivateOrSubFlow(data, functions, subFlow.name(), subFlow.flowBlocks());
        }
    }

    private static void genBalFuncForPrivateOrSubFlow(Data data, Set<Function> functions, String flowName,
                                                      List<MuleRecord> flowBlocks) {
        putFlowInfoIfAbsent(data, flowName);
        data.currentFlowInfo = data.flowInfoMap.get(flowName);

        List<Statement> body = genFuncBodyStatements(data, flowBlocks);
        addEndOfMethodStatements(data.currentFlowInfo, body);

        String methodName = ConversionUtils.escapeSpecialCharacters(flowName);
        HashSet<Parameter> parameters = data.functionParamMap.computeIfAbsent(methodName, k -> new HashSet<>());
        Function function = new Function(Optional.empty(), methodName, parameters.stream().toList(),
                Optional.empty(), body);
        functions.add(function);
        data.flowToGenMethodMap.put(flowName, function);
    }

    private static void addEndOfMethodStatements(Data.FlowInfo flowInfo, List<Statement> body) {
        if (flowInfo.context == Context.HTTP_LISTENER) {
            if (flowInfo.currentPayload != DEFAULT_PAYLOAD) {
                // the payload has been updated
                body.add(new BallerinaStatement(String.format("%s.setPayload(%s);", Constants.VAR_RESPONSE,
                        getSetPayloadArg(flowInfo.currentPayload))));

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
        String muleBasePath = data.globalHttpListenerConfigsMap.get(httpListener.configRef()).basePath();
        String basePath = getBallerinaAbsolutePath(muleBasePath);

        // Add services
        List<Parameter> queryPrams = new ArrayList<>();
        for (String qp : data.queryParams) {
            queryPrams.add(new Parameter(qp, "string", Optional.of(new BallerinaExpression("\"null\""))));
        }

        // resource method return statement
        int invokeEndPointCount = 0; // TODO: support different body resources
        String functionArgs = String.join(",", queryPrams.stream()
                .map(p -> String.format("%s", p.name())).toList());
        String invokeEndPointMethodName = String.format(Constants.METHOD_NAME_HTTP_ENDPOINT_TEMPLATE,
                invokeEndPointCount++);
        var resourceReturnStmt = new BallerinaStatement(String.format("return self.%s(%s);",
                invokeEndPointMethodName, functionArgs));

        // Add service resources
        List<Resource> resources = new ArrayList<>();
        String returnType = Constants.HTTP_RESOURCE_RETURN_TYPE_DEFAULT;
        for (String resourceMethodName : resourceMethodNames) {
            resourceMethodName = resourceMethodName.toLowerCase();
            Resource resource = new Resource(resourceMethodName,
                    resourcePath, queryPrams, Optional.of(returnType), Collections.singletonList(resourceReturnStmt));
            resources.add(resource);
        }

        List<Statement> body = genFuncBodyStatements(data, flowBlocks);
        // Add default response
        body.addFirst(new BallerinaStatement(String.format("http:Response %s = new;", Constants.VAR_RESPONSE)));

        if (data.currentFlowInfo.currentPayload != DEFAULT_PAYLOAD) {
            // the payload has been updated
            body.add(new BallerinaStatement(String.format("%s.setPayload(%s);", Constants.VAR_RESPONSE,
                    getSetPayloadArg(data.currentFlowInfo.currentPayload))));
        }

        // Add return statement
        body.add(new BallerinaStatement(String.format("return %s;", Constants.VAR_RESPONSE)));

        // Add service functions
        List<Function> functions = new ArrayList<>();
        functions.add(new Function(Optional.of("private"), invokeEndPointMethodName, queryPrams,
                Optional.of(returnType), body));

        return new Service(basePath, listenerRefs, resources, functions, Collections.emptyList(),
                Collections.emptyList());
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

    private static BallerinaModel createBallerinaModel(List<Import> imports, List<ModuleTypeDef> moduleTypeDefs,
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

    private static MuleRecord readBlock(Data data, Element element) {
        switch (element.getTagName()) {
            case Constants.LOGGER -> {
                return readLogger(data, element);
            }
            case Constants.SET_VARIABLE -> {
                return readSetVariable(data, element);
            }
            case Constants.SET_SESSION_VARIABLE -> {
                return readSetSessionVariable(data, element);
            }
            case Constants.HTTP_REQUEST -> {
                return readHttpRequest(data, element);
            }
            case Constants.SET_PAYLOAD -> {
                return readSetPayload(data, element);
            }
            case Constants.CHOICE -> {
                return readChoice(data, element);
            }
            case Constants.FLOW_REFERENCE -> {
                return readFlowReference(data, element);
            }
            case Constants.TRANSFORM_MESSAGE -> {
                return readTransformMessage(data, element);
            }
            case Constants.DB_INSERT, Constants.DB_SELECT, Constants.DB_UPDATE, Constants.DB_DELETE -> {
                return readDatabase(data, element);
            }
            case Constants.OBJECT_TO_JSON -> {
                return readObjectToJson(data, element);
            }
            case Constants.OBJECT_TO_STRING -> {
                return readObjectToString(data, element);
            }
            case Constants.ENRICHER -> {
                return readEnricher(data, element);
            }
            case Constants.CATCH_EXCEPTION_STRATEGY -> {
                return readCatchExceptionStrategy(data, element);
            }
            default -> {
                return readUnsupportedBlock(data, element);
            }
        }
    }

    private static List<Statement> convertToStatements(Data data, MuleRecord muleRec) {
        List<Statement> statementList = new ArrayList<>();
        switch (muleRec) {
            case Logger lg -> statementList.add(new BallerinaStatement(String.format("log:%s(%s);",
                    getBallerinaLogFunction(lg.level()), convertToBallerinaExpression(data, lg.message(), true))));
            case Payload payload -> {
                statementList.add(new BallerinaStatement("\n\n// set payload\n"));
                String pyld = convertToBallerinaExpression(data, payload.expr(), true);
                String payloadVar = String.format(Constants.VAR_PAYLOAD_TEMPLATE, data.payloadVarCount++);
                statementList.add(new BallerinaStatement(String.format("string %s = %s;", payloadVar, pyld)));
                data.currentFlowInfo.currentPayload = new PayloadVarInfo("string", payloadVar);
            }
            case Choice choice -> {
                List<WhenInChoice> whens = choice.whens();
                assert !whens.isEmpty(); // For valid mule config, there is at least one when

                WhenInChoice firstWhen = whens.getFirst();
                String ifCondition = convertToBallerinaExpression(data, firstWhen.condition(), false);
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
                    ElseIfClause elseIfClause = new ElseIfClause(new BallerinaExpression(
                            convertToBallerinaExpression(data, when.condition(), false)), elseIfBody);
                    elseIfClauses.add(elseIfClause);
                }

                List<Statement> elseBody = new ArrayList<>(choice.otherwiseProcess().size());
                for (MuleRecord r2 : choice.otherwiseProcess()) {
                    List<Statement> statements = convertToStatements(data, r2);
                    elseBody.addAll(statements);
                }
                // TODO: fix properly e.g. vars.bar == "10"
//            String condition = getVariable(data, choice.condition);
                statementList.add(new IfElseStatement(new BallerinaExpression(ifCondition), ifBody, elseIfClauses,
                        elseBody));
            }
            case SetVariable setVariable -> {
                String varValue = convertToBallerinaExpression(data, setVariable.value(), true);
                String varName = ConversionUtils.escapeSpecialCharacters(setVariable.variableName());
                statementList.add(new BallerinaStatement("string " + varName + " = " + varValue + ";"));
            }
            case SetSessionVariable setSessionVariable -> {
                String varValue = convertToBallerinaExpression(data, setSessionVariable.value(), true);
                String varName = ConversionUtils.escapeSpecialCharacters(setSessionVariable.variableName());
                ModuleVar moduleVar = data.moduleVarMap.get(varName);
                if (moduleVar == null) {
                    data.moduleVarMap.put(varName, new ModuleVar(varName, "string", new BallerinaExpression(varValue)));
                } else {
                    statementList.add(new BallerinaStatement(String.format("%s = %s;", varName, varValue)));
                }
            }
            case ObjectToJson objectToJson -> {
                statementList.add(new BallerinaStatement("\n\n// json transformation\n"));
                String objToJsonVarName = String.format(Constants.VAR_OBJ_TO_JSON_TEMPLATE,
                        data.objectToJsonVarCount++);
                statementList.add(new BallerinaStatement(String.format("json %s = %s.toJson();", objToJsonVarName,
                        data.currentFlowInfo.currentPayload.nameReference())));

                // object to json transformer implicitly sets the payload
                data.currentFlowInfo.currentPayload = new PayloadVarInfo("json", objToJsonVarName);
            }
            case ObjectToString objectToString -> {
                statementList.add(new BallerinaStatement("\n\n// string transformation\n"));
                String objToStringVarName = String.format(Constants.VAR_OBJ_TO_STRING_TEMPLATE,
                        data.objectToStringVarCount++);
                statementList.add(new BallerinaStatement(String.format("string %s = %s.toString();", objToStringVarName,
                        data.currentFlowInfo.currentPayload.nameReference())));

                // object to string transformer implicitly sets the payload
                data.currentFlowInfo.currentPayload = new PayloadVarInfo("string", objToStringVarName);
            }
            case HttpRequest httpRequest -> {
                List<Statement> statements = new ArrayList<>();
                String path = httpRequest.path();
                String method = httpRequest.method();
                String url = httpRequest.url();
                Map<String, String> queryParams = httpRequest.queryParams();

                statements.add(new BallerinaStatement(String.format("http:Client %s = check new(\"%s\");",
                        Constants.VAR_CLIENT, url)));
                statements.add(new BallerinaStatement(String.format("http:Response %s = check %s->%s/.%s(%s);",
                        Constants.VAR_CLIENT_GET, Constants.VAR_CLIENT, path, method.toLowerCase(),
                        genQueryParam(queryParams))));
                statementList.addAll(statements);
            }
            case FlowReference flowReference -> {
                String flowName = flowReference.flowName();
                String funcRef = ConversionUtils.escapeSpecialCharacters(flowName);

                String params = "";
                if (data.currentFlowInfo.context == Context.HTTP_LISTENER) {
                    Parameter param = new Parameter(Constants.VAR_RESPONSE, "http:Response", Optional.empty());
                    data.addFuncParam(funcRef, param);
                    params = Constants.VAR_RESPONSE;

                    Function method = data.flowToGenMethodMap.get(flowName);
                    if (method == null) {
                        // Set the flow context to Http listener
                        Data.FlowInfo flowInfo = new Data.FlowInfo(flowName, Context.HTTP_LISTENER);
                        data.flowInfoMap.put(flowName, flowInfo);
                    } else {
                        // Means we have analyzed the flow already
                        Data.FlowInfo flowInfo = data.flowInfoMap.get(flowName);
                        flowInfo.context = Context.HTTP_LISTENER;
                        addEndOfMethodStatements(flowInfo, data.flowToGenMethodMap.get(flowName).body());
                    }
                }

                statementList.add(new BallerinaStatement(String.format("%s(%s);", funcRef, params)));
            }
            case Enricher enricher -> {
                // TODO: support source and target vars properly
                String targetVarName = ConversionUtils.getSimpleMuleFlowVar(enricher.target());
                String sourceArgName = ConversionUtils.getSimpleMuleFlowVar(enricher.source());
                if (enricher.innerBlock().isEmpty()) {
                    statementList.add(new BallerinaStatement(String.format("%s = %s;", targetVarName, sourceArgName)));
                    // TODO: revisit special casing flow reference
//                } else if (enricher.innerBlock().get().kind() == Kind.FLOW_REFERENCE) {
//                    FlowReference flowReference = (FlowReference) enricher.innerBlock().get();
//
//                    String methodName = ConversionUtils.escapeSpecialCharacters(flowReference.flowName());
//                    Parameter sourceAsParam = new Parameter(sourceArgName, "string", Optional.empty());
//                    data.addFuncParam(methodName, sourceAsParam);
//
//                    statementList.add(new BallerinaStatement(String.format("%s = %s(%s);", targetVarName,
//                            methodName, sourceArgName)));
                } else {
                    String methodName = String.format(Constants.METHOD_NAME_ENRICHER_TEMPLATE,
                            data.enricherMethodCount);
                    Parameter sourceAsParam = new Parameter(sourceArgName, "string", Optional.empty());
                    data.addFuncParam(methodName, sourceAsParam);

                    List<Statement> enricherStmts = convertToStatements(data, enricher.innerBlock().get());
                    Function func = new Function(Optional.empty(), methodName, Collections.emptyList(),
                            Optional.of("string"), enricherStmts);
                    data.functions.add(func);

                    enricherStmts.add(new BallerinaStatement(String.format("return %s;", sourceArgName)));
                    statementList.add(new BallerinaStatement(String.format("%s = %s(%s);", targetVarName,
                            String.format(Constants.METHOD_NAME_ENRICHER_TEMPLATE, data.enricherMethodCount++),
                            sourceArgName)));
                }
            }
            case CatchExceptionStrategy catchExceptionStrategy -> {
                List<Statement> onFailBody = new ArrayList<>();

                for (MuleRecord catchBlock : catchExceptionStrategy.catchBlocks()) {
                    List<Statement> s = convertToStatements(data, catchBlock);
                    onFailBody.addAll(s);
                }

                OnFailClause onFailClause = new OnFailClause(onFailBody);
                DoStatement doStatement = new DoStatement(Collections.emptyList(), Optional.of(onFailClause));
                statementList.add(doStatement);
            }
            case Database database -> {
                data.imports.add(new Import(Constants.ORG_BALLERINA, Constants.MODULE_SQL, Optional.empty()));
                String streamConstraintType = "Record";
                data.typeDefMap.put(streamConstraintType, new ModuleTypeDef(streamConstraintType,
                        Constants.RECORD_TYPE));

                statementList.add(new BallerinaStatement("\n\n// database operation\n"));
                String dbQueryVarName = String.format(Constants.VAR_DB_QUERY_TEMPLATE, data.dbQueryVarCount++);
                statementList.add(new BallerinaStatement(String.format("%s %s = %s;",
                        Constants.SQL_PARAMETERIZED_QUERY_TYPE, dbQueryVarName,
                        database.queryType() == QueryType.TEMPLATE_QUERY_REF ? database.query() :
                                String.format("`%s`", database.query()))));

                String dbStreamVarName = String.format(Constants.VAR_DB_STREAM_TEMPLATE, data.dbStreamVarCount++);
                statementList.add(new BallerinaStatement(String.format("%s %s= %s->query(%s);",
                        String.format(Constants.DB_QUERY_DEFAULT_TEMPLATE, streamConstraintType),
                        dbStreamVarName, database.configRef(), dbQueryVarName)));

                if (database.kind() == Kind.DB_SELECT) {
                    String dbSelectVarName = String.format(Constants.VAR_DB_SELECT_TEMPLATE, data.dbSelectVarCount++);
                    statementList.add(new BallerinaStatement(
                            String.format("%s[] %s = check from %s %s in %s select %s;", streamConstraintType,
                                    dbSelectVarName, streamConstraintType, Constants.VAR_ITERATOR, dbStreamVarName,
                                    Constants.VAR_ITERATOR)));

                    // db:select implicitly sets the payload
                    data.currentFlowInfo.currentPayload = new PayloadVarInfo(String.format("%s[]",
                            streamConstraintType),
                            dbSelectVarName);
                }
            }
            case TransformMessage transformMessage -> {
                DWReader.processDWElements(transformMessage.children(), data, statementList);
                statementList.add(new BallerinaStatement(String.format("%s.setPayload(%s);",
                        Constants.VAR_RESPONSE, DWUtils.DATAWEAVE_OUTPUT_VARIABLE_NAME)));
            }
            case UnsupportedBlock unsupportedBlock -> {
                String comment = ConversionUtils.wrapElementInUnsupportedBlockComment(unsupportedBlock.xmlBlock());
                // TODO: comment is not a statement. Find a better way to handle this
                // This works for now because we concatenate and create a body block `{ stmts }` before parsing.
                statementList.add(new BallerinaStatement(comment));
            }
            case null -> throw new IllegalStateException();
            default -> throw new UnsupportedOperationException();
        }

        return statementList;
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
    private static Logger readLogger(Data data, Element element) {
        data.imports.add(new Import("ballerina", "log", Optional.empty()));
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
    private static Choice readChoice(Data data, Element element) {
        NodeList when = element.getElementsByTagName("when");
        List<WhenInChoice> whens = new ArrayList<>();
        for (int i = 0; i < when.getLength(); i++) {
            Node whenNode = when.item(i);
            NodeList whenProcesses = whenNode.getChildNodes();
            String condition = ((Element) whenNode).getAttribute("expression");
            List<MuleRecord> whenProcess = new ArrayList<>();
            for (int j = 0; j < whenProcesses.getLength(); j++) {
                Node child = whenProcesses.item(j);
                if (child.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                MuleRecord r = readBlock(data, (Element) child);
                whenProcess.add(r);
            }
            WhenInChoice whenInChoice = new WhenInChoice(condition, whenProcess);
            whens.add(whenInChoice);
        }

        NodeList otherwiseProcesses = element.getElementsByTagName("otherwise").item(0).getChildNodes();
        List<MuleRecord> otherwiseProcess = new ArrayList<>();
        for (int i = 0; i < otherwiseProcesses.getLength(); i++) {
            Node child = otherwiseProcesses.item(i);
            if (child.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            MuleRecord r = readBlock(data, (Element) child);
            otherwiseProcess.add(r);
        }
        return new Choice(whens, otherwiseProcess);
    }

    // Scopes
    private static Flow readFlow(Data data, Element flowElement) {
        String flowName = flowElement.getAttribute("name");
        NodeList children = flowElement.getChildNodes();

        MuleRecord source = null;
        List<MuleRecord> flowBlocks = new ArrayList<>();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            Element element = (Element) child;
            if (element.getTagName().equals(Constants.HTTP_LISTENER)) {
                assert source == null;
                source = readHttpListener(data, element);
            } else {
                MuleRecord muleRec = readBlock(data, element);
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

    private static SubFlow readSubFlow(Data data, Element flowElement) {
        String flowName = flowElement.getAttribute("name");
        NodeList children = flowElement.getChildNodes();

        List<MuleRecord> flowBlocks = new ArrayList<>();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            Element element = (Element) child;
            MuleRecord muleRec = readBlock(data, element);
            flowBlocks.add(muleRec);
        }

        return new SubFlow(flowName, flowBlocks);
    }

    private static Enricher readEnricher(Data data, Element element) {
        String source = element.getAttribute("source");
        String target = element.getAttribute("target");

        NodeList children = element.getChildNodes();


        MuleRecord block = null;
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            Element e = (Element) child;
            assert block == null;
            block = readBlock(data, e);
        }

        Optional<MuleRecord> innerBlock = block != null ? Optional.of(block) : Optional.empty();
        return new Enricher(source, target, innerBlock);
    }

    // Transformers
    private static Payload readSetPayload(Data data, Element element) {
        String muleExpr = element.getAttribute("value");
        return new Payload(muleExpr);
    }

    private static SetVariable readSetVariable(Data data, Element element) {
        String varName = element.getAttribute("variableName");
        String val = element.getAttribute("value");
        return new SetVariable(varName, val);
    }

    private static SetSessionVariable readSetSessionVariable(Data data, Element element) {
        String varName = element.getAttribute("variableName");
        String val = element.getAttribute("value");
        return new SetSessionVariable(varName, val);
    }

    private static ObjectToJson readObjectToJson(Data data, Element element) {
        return new ObjectToJson();
    }

    private static ObjectToString readObjectToString(Data data, Element element) {
        return new ObjectToString();
    }

    // Error handling
    private static CatchExceptionStrategy readCatchExceptionStrategy(Data data, Element element) {
        NodeList children = element.getChildNodes();

        List<MuleRecord> catchBlocks = new ArrayList<>();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            Element e = (Element) child;
            MuleRecord muleRec = readBlock(data, e);
            catchBlocks.add(muleRec);
        }

        return new CatchExceptionStrategy(catchBlocks);
    }

    // HTTP Module
    private static HttpListener readHttpListener(Data data, Element element) {
        String configRef = element.getAttribute("config-ref");
        String resourcePath = element.getAttribute("path");
        String[] allowedMethods = Arrays.stream(getAllowedMethods(element.getAttribute("allowedMethods")))
                .map(String::toLowerCase).toArray(String[]::new);
        return new HttpListener(configRef, resourcePath, allowedMethods);
    }

    private static HttpRequest readHttpRequest(Data data, Element element) {
        String method = element.getAttribute("method").toLowerCase();
        String url = element.getAttribute("url").toLowerCase();
        String path = element.getAttribute("path").toLowerCase();
        Element queryParamsElement = (Element) element.getElementsByTagName(Constants.HTTP_QUERY_PARAMS).item(0);
        CDATASection cdataSection = (CDATASection) queryParamsElement.getChildNodes().item(0);
        Map<String, String> queryParams = processQueryParams(cdataSection.getData().trim());
        return new HttpRequest(method, url, path, queryParams);
    }

    private static FlowReference readFlowReference(Data data, Element element) {
        String flowName = element.getAttribute("name");
        return new FlowReference(flowName);
    }

    // Database Connector
    private static Database readDatabase(Data data, Element element) {
        String configRef = element.getAttribute("config-ref");

        QueryType queryType = null;
        String query = null;

        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            assert queryType == null;
            Element childElement = (Element) node;
            queryType = getQueryType(childElement.getTagName());
            query = readQuery(data, childElement, queryType);
        }

        if (queryType == null) {
            throw new IllegalStateException("No valid query found in the database block");
        }

        Kind kind = switch (element.getTagName()) {
            case Constants.DB_INSERT -> Kind.DB_INSERT;
            case Constants.DB_SELECT -> Kind.DB_SELECT;
            case Constants.DB_UPDATE -> Kind.DB_UPDATE;
            case Constants.DB_DELETE -> Kind.DB_DELETE;
            default -> throw new UnsupportedOperationException();
        };

        return new Database(kind, configRef, queryType, query);
    }

    private static QueryType getQueryType(String tagName) {
        return switch (tagName) {
            case Constants.DB_PARAMETERIZED_QUERY -> QueryType.PARAMETERIZED_QUERY;
            case Constants.DB_DYNAMIC_QUERY -> QueryType.DYNAMIC_QUERY;
            case Constants.DB_TEMPLATE_QUERY_REF -> QueryType.TEMPLATE_QUERY_REF;
            default -> throw new IllegalStateException("Invalid query type");
        };
    }

    private static String readQuery(Data data, Element element, QueryType queryType) {
        return switch (queryType) {
            case PARAMETERIZED_QUERY -> readDbParameterizedQuery(data, element);
            case DYNAMIC_QUERY -> readDbDynamicQuery(data, element);
            case TEMPLATE_QUERY_REF -> readDbTemplateQueryRef(data, element);
        };
    }

    private static String readDbParameterizedQuery(Data data, Element element) {
        return element.getTextContent();
    }

    private static String readDbDynamicQuery(Data data, Element element) {
        return element.getTextContent();
    }

    private static String readDbTemplateQueryRef(Data data, Element element) {
        return element.getAttribute("name");
    }

    private static UnsupportedBlock readUnsupportedBlock(Data data, Element element) {
        String xmlBlock = ConversionUtils.elementToString(element);
        return new UnsupportedBlock(xmlBlock);
    }

    // Global Elements
    private static HTTPListenerConfig readHttpListenerConfig(Data data, Element element) {
        data.imports.add(new Import(Constants.ORG_BALLERINA, Constants.MODULE_HTTP, Optional.empty()));
        String listenerName = element.getAttribute("name");
        String host = element.getAttribute("host");
        String port = element.getAttribute("port");
        String basePath = insertLeadingSlash(element.getAttribute("basePath"));
        HashMap<String, String> config = new HashMap<>(Collections.singletonMap("host", host));
        return new HTTPListenerConfig(listenerName, basePath, port, config);
    }

    private static DbMSQLConfig readDbMySQLConfig(Data data, Element element) {
        data.imports.add(new Import(Constants.ORG_BALLERINAX, Constants.MODULE_MYSQL, Optional.empty()));
        data.imports.add(new Import(Constants.ORG_BALLERINAX, Constants.MODULE_MYSQL_DRIVER, Optional.of("_")));
        String name = element.getAttribute("name");
        String host = element.getAttribute("host");
        String port = element.getAttribute("port");
        String user = element.getAttribute("user");
        String password = element.getAttribute("password");
        String database = element.getAttribute("database");
        return new DbMSQLConfig(name, host, port, user, password, database);
    }

    private static DbTemplateQuery readDbTemplateQuery(Data data, Element element) {
        String name = element.getAttribute("name");
        String query = null;
        List<DbInParam> dbInParams = new ArrayList<>();
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            Element childElement = (Element) node;
            if (childElement.getTagName().equals("db:parameterized-query")) {
                query = readDbParameterizedQuery(data, childElement);
            } else if (childElement.getTagName().equals("db:in-param")) {
                DbInParam dbInParam = readDbInParam(data, childElement);
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

    private static DbInParam readDbInParam(Data data, Element element) {
        String name = element.getAttribute("name");
        String type = element.getAttribute("type");
        Type ty = Type.from(type);
        String defaultValue = element.getAttribute("defaultValue");
        return new DbInParam(name, ty, defaultValue);
    }

    private static TransformMessage readTransformMessage(Data data, Element element) {
        List<MuleModel.TransformMessageElement> transformMessageElements = new ArrayList<>();
        for (int i = 1; i < element.getChildNodes().getLength(); i += 2) {
            Element node = (Element) element.getChildNodes().item(i);
            switch (node.getLocalName()) {
                case Constants.SET_PAYLOAD -> {
                    String resource = node.getAttribute("resource");
                    if (resource.isEmpty()) {
                        transformMessageElements.add(new MuleModel.SetPayloadElement(null, node.getTextContent()));
                    } else {
                        transformMessageElements.add(new MuleModel.SetPayloadElement(resource, null));
                    }
                }
                case Constants.INPUT_PAYLOAD -> {
                    String mimeType = node.getAttribute("mimeType");
                    String docSamplePath = node.getAttribute("doc:sample");
                    transformMessageElements.add(new MuleModel.InputPayloadElement(mimeType, docSamplePath));
                }
                case Constants.SET_VARIABLE -> {
                    String variableName = node.getAttribute("variableName");
                    String resource = node.getAttribute("resource");
                    String script = null;
                    if (resource.isEmpty()) {
                        script = ((CDATASection) node.getChildNodes().item(0)).getData();
                    }
                    transformMessageElements.add(new MuleModel.SetVariableElement(resource, script, variableName));
                }
                case Constants.SET_SESSION_VARIABLE -> {
                    String variableName = node.getAttribute("variableName");
                    String resource = node.getAttribute("resource");
                    String script = null;
                    if (resource.isEmpty()) {
                        script = ((CDATASection) node.getChildNodes().item(0)).getData();
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
