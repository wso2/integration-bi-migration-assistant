package converter;

import ballerina.BallerinaModel;
import ballerina.CodeGenerator;
import dataweave.DWReader;
import dataweave.converter.DWUtils;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import mule.Constants;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static ballerina.BallerinaModel.BallerinaExpression;
import static ballerina.BallerinaModel.BallerinaStatement;
import static ballerina.BallerinaModel.DefaultPackage;
import static ballerina.BallerinaModel.ElseIfClause;
import static ballerina.BallerinaModel.Function;
import static ballerina.BallerinaModel.IfElseStatement;
import static ballerina.BallerinaModel.Import;
import static ballerina.BallerinaModel.Listener;
import static ballerina.BallerinaModel.ListenerType;
import static ballerina.BallerinaModel.Module;
import static ballerina.BallerinaModel.ModuleTypeDef;
import static ballerina.BallerinaModel.ModuleVar;
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
import static mule.MuleModel.Choice;
import static mule.MuleModel.DbInParam;
import static mule.MuleModel.DbMSQLConfig;
import static mule.MuleModel.DbTemplateQuery;
import static mule.MuleModel.Database;
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
import static mule.MuleModel.SubFlow;
import static mule.MuleModel.TransformMessage;
import static mule.MuleModel.Type;
import static mule.MuleModel.WhenInChoice;
import static mule.MuleModel.UnsupportedBlock;

public class MuleToBalConverter {

    public static class Data {
        // Mule global elements
        HashMap<String, HTTPListenerConfig> globalHttpListenerConfigsMap = new HashMap<>();
        HashMap<String, DbMSQLConfig> globalDbMySQLConfigsMap = new HashMap<>();
        HashMap<String, DbTemplateQuery> globalDbTemplateQueryMap = new HashMap<>();
        List<UnsupportedBlock> globalUnsupportedBlocks = new ArrayList<>();

        // Ballerina global elements
        HashMap<String, ModuleTypeDef> typeDef = new HashMap<>();
        HashSet<Import> imports = new HashSet<>();
        HashSet<String> queryParams = new HashSet<>();
        public List<Function> functions = new ArrayList<>();

        // Internal variable/method count
        public int dwMethodCount = 0;
        public int dbQueryVarCount = 0;
        public int dbStreamVarCount = 0;
        public int dbSelectVarCount = 0;
        public int objectToJsonVarCount = 0;
        public int objectToStringVarCount = 0;

        // Temporary payload info
        public CurrentPayloadVarInfo payload = null;
    }

    public record CurrentPayloadVarInfo (String type, String nameReference) {
    }

    public static SyntaxTree convertToBallerina(String xmlFilePath) {
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

        BallerinaModel ballerinaModel = generateBallerinaModel(data, flows, subFlows);
        return new CodeGenerator(ballerinaModel).generateBalCode();
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

            // Create a service from the flow
            Service service = genBalService(data, (HttpListener) src, flow.flowBlocks());
            services.add(service);
        }

        List<Function> functions = new ArrayList<>();

        // Create functions for private flows
        List<Function> privateFlowFuncs = genBalFuncsFromPrivateFlows(data, privateFlows);

        // Create functions for sub-flows
        List<Function> subFlowFuncs = genBalFuncsFromSubFlows(data, subFlows);

        functions.addAll(privateFlowFuncs);
        functions.addAll(subFlowFuncs);

        // Add imports
        List<Import> imports = new ArrayList<>();
        for (Import imp : data.imports) {
            imports.add(new Import(imp.orgName(), imp.moduleName(), imp.importPrefix()));
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
            var balExpr = new BallerinaExpression(String.format("check new (\"%s\", \"%s\", \"%s\", \"%s\", %s)",
                    dbMSQLConfig.host(), dbMSQLConfig.user(), dbMSQLConfig.password(), dbMSQLConfig.database(),
                    dbMSQLConfig.port()));
            moduleVars.add(new ModuleVar(dbMSQLConfig.name(), Constants.MYSQL_CLIENT_TYPE, balExpr));
        }

        for (DbTemplateQuery dbTemplateQuery : data.globalDbTemplateQueryMap.values()) {
            var balExpr = new BallerinaExpression(String.format("`%s`", dbTemplateQuery.parameterizedQuery()));
            moduleVars.add(new ModuleVar(dbTemplateQuery.name(), Constants.SQL_PARAMETERIZED_QUERY_TYPE, balExpr));
        }

        List<String> comments = new ArrayList<>();
        for (UnsupportedBlock unsupportedBlock : data.globalUnsupportedBlocks) {
            String comment = ConversionUtils.wrapElementInUnsupportedBlockComment(unsupportedBlock.xmlBlock());
            comments.add(comment);
        }

        return createBallerinaModel(imports, data.typeDef.values().stream().toList(), moduleVars, listeners, services,
                functions, comments);
    }

    private static List<Function> genBalFuncsFromSubFlows(Data data, List<SubFlow> subFlows) {
        List<Function> functions = new ArrayList<>(subFlows.size());
        for (SubFlow subFlow : subFlows) {
            List<Statement> body = genFuncBodyStatements(data, subFlow.flowBlocks());
            // TODO: assumed source is always a http listener
            List<Parameter> parameterList = List.of(new Parameter(Constants.VAR_RESPONSE, "http:Response",
                    Optional.empty()));
            Function function = new Function(Optional.empty(), subFlow.name(), parameterList, Optional.empty(), body);
            functions.add(function);
        }
        functions.addAll(data.functions);
        return functions;
    }

    private static List<Function> genBalFuncsFromPrivateFlows(Data data, List<Flow> privateFlows) {
        List<Function> functions = new ArrayList<>(privateFlows.size());
        for (Flow privateFlow : privateFlows) {
            assert privateFlow.source().isEmpty();
            // TODO: assumed source is always a http listener
            List<Parameter> parameterList = List.of(new Parameter(Constants.VAR_RESPONSE, "http:Response",
                    Optional.empty()));
            List<Statement> body = genFuncBodyStatements(data, privateFlow.flowBlocks());
            Function function = new Function(Optional.empty(), privateFlow.name(), parameterList, Optional.empty(),
                    body);
            functions.add(function);
        }
        functions.addAll(data.functions);
        return functions;
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

        // Add return statement
        body.add(new BallerinaStatement(String.format("return %s;", Constants.VAR_RESPONSE)));

        // Add service functions
        List<Function> functions = new ArrayList<>();
        functions.add(new Function(Optional.of("private"), invokeEndPointMethodName, queryPrams,
                Optional.of(returnType), body));

        return new Service(basePath, listenerRefs, resources, functions, Collections.emptyList(),
                Collections.emptyList());
    }

    private static String getSetPayloadArg(CurrentPayloadVarInfo currentPayloadVarInfo) {
        if (isSetPayloadAllowedType(currentPayloadVarInfo.type())) {
            return currentPayloadVarInfo.nameReference();
        } else {
            return String.format("%s.toString()", currentPayloadVarInfo.nameReference());
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
            case Payload payload -> statementList.add(new BallerinaStatement(String.format("%s.setPayload(%s);",
                    Constants.VAR_RESPONSE, convertToBallerinaExpression(data, payload.expr(), true))));
            case Choice choice -> {
                List<WhenInChoice> whens = choice.whens();
                assert whens.size() > 1; // For valid mule config, there is at least one when

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
                statementList.add(new BallerinaStatement(
                        "string " + setVariable.variableName() + " = " + varValue + ";"));
            }
            case ObjectToJson objectToJson -> {
                if (data.payload == null) {
                    throw new UnsupportedOperationException();
                }

                statementList.add(new BallerinaStatement("\n\n// json transformation\n"));
                String objToJsonVarName = String.format(Constants.VAR_OBJ_TO_JSON_TEMPLATE,
                        data.objectToJsonVarCount++);
                statementList.add(new BallerinaStatement(String.format("json %s = %s.toJson();", objToJsonVarName,
                        data.payload.nameReference())));

                // object to json transformer implicitly sets the payload
                data.payload = new CurrentPayloadVarInfo("json", objToJsonVarName);
                statementList.add(new BallerinaStatement(String.format("%s.setPayload(%s);", Constants.VAR_RESPONSE,
                        getSetPayloadArg(data.payload))));
            }
            case ObjectToString objectToString -> {
                if (data.payload == null) {
                    throw new UnsupportedOperationException();
                }

                statementList.add(new BallerinaStatement("\n\n// string transformation\n"));
                String objToStringVarName = String.format(Constants.VAR_OBJ_TO_STRING_TEMPLATE,
                        data.objectToStringVarCount++);
                statementList.add(new BallerinaStatement(String.format("string %s = %s.toString();", objToStringVarName,
                        data.payload.nameReference())));

                // object to string transformer implicitly sets the payload
                data.payload = new CurrentPayloadVarInfo("string", objToStringVarName);
                statementList.add(new BallerinaStatement(String.format("%s.setPayload(%s);", Constants.VAR_RESPONSE,
                        getSetPayloadArg(data.payload))));
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
                // TODO: assumed source is always a http listener
                statementList.add(new BallerinaStatement(String.format("%s(%s);", flowReference.flowName(),
                        Constants.VAR_RESPONSE)));
            }
            case Database database -> {
                data.imports.add(new Import(Constants.ORG_BALLERINA, Constants.MODULE_SQL, Optional.empty()));
                String streamConstraintType = "Record";
                data.typeDef.put(streamConstraintType, new ModuleTypeDef(streamConstraintType, Constants.RECORD_TYPE));

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
                    data.payload = new CurrentPayloadVarInfo(String.format("%s[]", streamConstraintType),
                            dbSelectVarName);
                    // TODO: assumed source is always a http listener
                    statementList.add(new BallerinaStatement(String.format("%s.setPayload(%s);", Constants.VAR_RESPONSE,
                            getSetPayloadArg(data.payload))));
                }
            }
            case TransformMessage transformMessage -> {
                String mimeType = transformMessage.mimeType();
                String script = transformMessage.script();
                DWReader.processDWScript(script, mimeType, data, statementList);
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
            String condition = convertToBallerinaExpression(data, ((Element) whenNode).getAttribute("expression"),
                    false);
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

    private static ObjectToJson readObjectToJson(Data data, Element element) {
        return new ObjectToJson();
    }

    private static ObjectToString readObjectToString(Data data, Element element) {
        return new ObjectToString();
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
        Element firstElement = (Element) element.getChildNodes().item(1);
        String mimeType = null;
        CDATASection cdataSection;
        switch (firstElement.getLocalName()) {
            case Constants.SET_PAYLOAD -> {
                cdataSection = (CDATASection) firstElement.getChildNodes().item(0);
            }
            case Constants.INPUT_PAYLOAD -> {
                Element secondElement = (Element) element.getChildNodes().item(3);
                cdataSection = (CDATASection) secondElement.getChildNodes().item(0);
                mimeType = firstElement.getAttribute("mimeType");
            }
            default -> throw new UnsupportedOperationException();
        }
        return new TransformMessage(mimeType, cdataSection.getData());
    }
}
