package converter;

import ballerina.BallerinaModel;
import ballerina.CodeGenerator;
import dataweave.DWReader;
import dataweave.converter.DWConstants;
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
import static converter.ConversionUtils.convertToBallerinaExpression;
import static converter.ConversionUtils.genQueryParam;
import static converter.ConversionUtils.getAllowedMethods;
import static converter.ConversionUtils.getBallerinaAbsolutePath;
import static converter.ConversionUtils.getBallerinaResourcePath;
import static converter.ConversionUtils.insertLeadingSlash;
import static converter.ConversionUtils.processQueryParams;
import static mule.MuleModel.Choice;
import static mule.MuleModel.DbMSQLConfig;
import static mule.MuleModel.DbSelect;
import static mule.MuleModel.Flow;
import static mule.MuleModel.FlowReference;
import static mule.MuleModel.HttpListener;
import static mule.MuleModel.HttpRequest;
import static mule.MuleModel.Kind;
import static mule.MuleModel.HTTPListenerConfig;
import static mule.MuleModel.LogLevel;
import static mule.MuleModel.Logger;
import static mule.MuleModel.MuleRecord;
import static mule.MuleModel.Payload;
import static mule.MuleModel.SetVariable;
import static mule.MuleModel.SubFlow;
import static mule.MuleModel.TransformMessage;
import static mule.MuleModel.WhenInChoice;
import static mule.MuleModel.UnsupportedBlock;

public class MuleToBalConverter {

    public static class Data {
        HashMap<String, HTTPListenerConfig> globalHttpListenerConfigsMap = new HashMap<>();
        HashMap<String, DbMSQLConfig> globalDbMySQLConfigsMap = new HashMap<>();
        HashMap<String, ModuleTypeDef> typeDef = new HashMap<>();
        HashSet<Import> imports = new HashSet<>();
        HashSet<String> queryParams = new HashSet<>();
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
            if (Constants.HTTP_LISTENER_CONFIG.equals(elementTagName) ||
                    Constants.DB_MYSQL_CONFIG.equals(elementTagName)) {
                readGlobalConfigElement(data, element);
                continue;
            }

            if (Constants.FLOW.equals(elementTagName)) {
                Flow flow = readFlow(data, element);
                flows.add(flow);
            } else if (Constants.SUB_FLOW.equals(elementTagName)) {
                SubFlow subFlow = readSubFlow(data, element);
                subFlows.add(subFlow);
            }
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
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private static BallerinaModel generateBallerinaModel(Data data, List<Flow> flows, List<SubFlow> subFlows) {
        // TODO: Support multiple flows
        Flow flow = flows.getFirst();

        MuleRecord source = flow.source();
        if (source.kind() != Kind.HTTP_LISTENER) {
            // Only http listener source is supported at the moment
            throw new UnsupportedOperationException();
        }

        // Create a service from the flow
        Service service = genBalService(data, (HttpListener) source, flow.flowBlocks());

        // Create functions for sub-flows
        List<Function> functions = genBalFunctions(data, subFlows);

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
            moduleVars.add(new ModuleVar(dbMSQLConfig.name(), Constants.MYSQL_CLIENT, balExpr));
        }

        return createBallerinaModel(imports, data.typeDef.values().stream().toList(), moduleVars, listeners,
                Collections.singletonList(service), functions);
    }

    private static List<Function> genBalFunctions(Data data, List<SubFlow> subFlows) {
        List<Function> functions = new ArrayList<>(subFlows.size());
        for (SubFlow subFlow : subFlows) {
            List<Statement> body = genFuncBodyStatements(data, subFlow.flowBlocks());
            // TODO: assumed source is always a http listener
            List<Parameter> parameterList = List.of(new Parameter(Constants.VAR_RESPONSE, "http:Response",
                    Optional.empty()));
            Function function = new Function(Optional.empty(), subFlow.name(), parameterList, Optional.empty(), body);
            functions.add(function);
        }
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

    private static List<Statement> genFuncBodyStatements(Data data, List<MuleRecord> flowBlocks) {
        // Add function body statements
        List<Statement> body = new ArrayList<>();

        // Read flow blocks
        for (MuleRecord record : flowBlocks) {
            switch (record.kind()) {
                case LOGGER, PAYLOAD, CHOICE, SET_VARIABLE, HTTP_REQUEST, FLOW_REFERENCE, TRANSFORM_MESSAGE
                , UNSUPPORTED_BLOCK, DB_SELECT -> {
                    List<Statement> s = convertToStatements(data, record);
                    body.addAll(s);
                }
                case null -> throw new IllegalStateException();
                default -> throw new UnsupportedOperationException();
            }
        }
        return body;
    }

    private static BallerinaModel createBallerinaModel(List<Import> imports, List<ModuleTypeDef> moduleTypeDefs,
                                                       List<ModuleVar> moduleVars, List<Listener> listeners,
                                                       List<Service> services, List<Function> functions) {
        String moduleName = "muleDemo";
        Module module = new Module(moduleName, imports, moduleTypeDefs, moduleVars, listeners, services, functions);
        return new BallerinaModel(new DefaultPackage(moduleName, moduleName, "1.0.0"),
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
            case Constants.DB_SELECT -> {
                return readDbSelect(data, element);
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
            case DbSelect dbSelect -> {
                // TODO: assumed source is always a http listener
                data.imports.add(new Import(Constants.ORG_BALLERINA, Constants.MODULE_SQL, Optional.empty()));
                String streamConstraintType = "Record";
                data.typeDef.put(streamConstraintType, new ModuleTypeDef(streamConstraintType, "record {}"));
                statementList.add(new BallerinaStatement(String.format("%s %s= %s->query(`%s`);",
                        String.format(Constants.DB_QUERY_DEFAULT_TEMPLATE, streamConstraintType),
                        String.format(Constants.VAR_DB_STREAM_TEMPLATE, 0),
                        dbSelect.configRef(), dbSelect.query())));
                // Record[] payload = check from Record r in _dbStream0_ select r;
                statementList.add(new BallerinaStatement(
                        String.format("%s[] payload = check from %s %s in %s select %s;", streamConstraintType,
                                streamConstraintType, "r", String.format(Constants.VAR_DB_STREAM_TEMPLATE, 0), "r")));
            }
            case TransformMessage transformMessage -> {
                String mimeType = transformMessage.mimeType();
                String script = transformMessage.script();
                DWReader.processDWScript(script, mimeType, data, statementList);
                statementList.add(new BallerinaStatement(String.format("%s.setPayload(%s);",
                        Constants.VAR_RESPONSE, DWConstants.DATAWEAVE_OUTPUT_VARIABLE_NAME)));
            }
            case UnsupportedBlock unsupportedBlock -> {
                String comment = ConversionUtils.wrapElementInUnsupportedBlockComment(unsupportedBlock.xmlBlock());
                // TODO: comment is not a statement. Find a better way to handle this
                // This works for now because we concatenate and create a body block `{ stmts }` before parsing.
                statementList.add(new BallerinaStatement(comment));
            }
            case null, default -> throw new IllegalStateException();
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
                source = readHttpListener(data, element);
            } else {
                MuleRecord muleRec = readBlock(data, element);
                flowBlocks.add(muleRec);
            }
        }

        assert source != null : "A flow should have a source";
        return new Flow(flowName, source, flowBlocks);
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

    private static DbSelect readDbSelect(Data data, Element element) {
        String configRef = element.getAttribute("config-ref");
        String query = element.getElementsByTagName("db:parameterized-query").item(0).getTextContent();
        return new DbSelect(configRef, query);
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
        return new MuleModel.TransformMessage(mimeType, cdataSection.getData());
    }
}
