package converter;

import ballerina.BallerinaModel;
import ballerina.CodeGenerator;
import mule.Constants;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import static ballerina.BallerinaModel.Parameter;
import static ballerina.BallerinaModel.Resource;
import static ballerina.BallerinaModel.Service;
import static ballerina.BallerinaModel.Statement;
import static converter.Utils.genQueryParam;
import static converter.Utils.getAllowedMethods;
import static converter.Utils.insertLeadingSlash;
import static converter.Utils.normalizedFromMuleExpr;
import static converter.Utils.normalizedResourcePath;
import static converter.Utils.processQueryParams;
import static mule.MuleModel.Choice;
import static mule.MuleModel.Flow;
import static mule.MuleModel.HttpListener;
import static mule.MuleModel.HttpRequest;
import static mule.MuleModel.Kind;
import static mule.MuleModel.ListenerConfig;
import static mule.MuleModel.Logger;
import static mule.MuleModel.MuleRecord;
import static mule.MuleModel.Payload;
import static mule.MuleModel.SetVariable;
import static mule.MuleModel.WhenInChoice;

public class Main {

    static class Data {
        HashMap<String, ListenerConfig> globalListenerConfigsMap = new HashMap<>();
        HashSet<Import> imports = new HashSet<>();
        HashSet<String> queryParams = new HashSet<>();
    }

    public static void main(String[] args) {
        Element root;
        try {
            root = parseMuleXMLConfigurationFile();
        } catch (Exception e) {
            throw new RuntimeException("Error while parsing the mule XML configuration file", e);
        }

        Data data = new Data();
        NodeList childNodes = root.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            Element element = (Element) node;
            String elementTagName = element.getTagName();
            if (Constants.HTTP_LISTENER_CONFIG.equals(elementTagName)) {
                readGlobalConfigElement(data, element);
                continue;
            }

            if (Constants.FLOW.equals(elementTagName)) {
                Flow flow = readFlow(data, element);
                BallerinaModel ballerinaModel = generateBallerinaModel(data, flow);
                new CodeGenerator(ballerinaModel).generateBalCode();
            } else {
                throw new UnsupportedOperationException();
            }
        }
    }

    private static Element parseMuleXMLConfigurationFile() throws ParserConfigurationException, SAXException, IOException {
        // Load the Mule XML file
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse("src/main/resources/muledemo.xml");

        // Normalize the XML structure
        document.getDocumentElement().normalize();

        return document.getDocumentElement();
    }

    private static void readGlobalConfigElement(Data data, Element element) {
        String elementTagName = element.getTagName();
        if (Constants.HTTP_LISTENER_CONFIG.equals(elementTagName)) {
            ListenerConfig listenerConfig = readHttpListenerConfig(data, element);
            data.globalListenerConfigsMap.put(listenerConfig.name(), listenerConfig);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private static BallerinaModel generateBallerinaModel(Data data, Flow flow) {
        List<MuleRecord> flowElements = flow.flowElements();
        // Starting point has to be a source
        MuleRecord source = flowElements.getFirst();

        if (source.kind() != Kind.HTTP_LISTENER) {
            // Only http listener source is supported at the moment
            throw new UnsupportedOperationException();
        }

        return genBalModelForHttpSourcedFlow(data, (HttpListener) source, flow);
    }

    private static BallerinaModel genBalModelForHttpSourcedFlow(Data data, HttpListener httpListener, Flow flow) {
        String resourcePath = normalizedResourcePath(httpListener.resourcePath());
        String[] resourceMethodNames = httpListener.allowedMethods();
        List<String> listenerRefs = Collections.singletonList(httpListener.configRef());
        String muleBasePath = data.globalListenerConfigsMap.get(httpListener.configRef()).basePath();
        String basePath = muleBasePath.isEmpty() ? "/" : muleBasePath;

        // Add imports
        List<Import> imports = new ArrayList<>();
        for (Import imp : data.imports) {
            imports.add(new Import(imp.org(), imp.module()));
        }

        // Add global listeners
        List<Listener> listeners = new ArrayList<>();
        for (ListenerConfig listenerConfig : data.globalListenerConfigsMap.values()) {
            listeners.add(new Listener(ListenerType.HTTP, listenerConfig.name(), listenerConfig.port(),
                    listenerConfig.config()));
        }

        // Add services
        List<Parameter> queryPrams = new ArrayList<>();
        for (String qp : data.queryParams) {
            queryPrams.add(new Parameter(qp, "string", Optional.of(new BallerinaExpression("\"null\""))));
        }

        // resource method return statement
        int invokeEndPointCount = 0; // TODO: support different body resources
        String functionArgs = String.join(",", queryPrams.stream()
                .map(p -> String.format("%s", p.name())).toList());
        String invokeEndPointMethodName = Constants.HTTP_ENDPOINT_METHOD_NAME + invokeEndPointCount++;
        var resourceReturnStmt = new BallerinaStatement(String.format("return self.%s(%s);",
                invokeEndPointMethodName, functionArgs));

        // Add service resources
        List<Resource> resources = new ArrayList<>();
        String returnType = Constants.HTTP_RESOURCE_RETURN_TYPE_DEFAULT;
        for (String resourceMethodName : resourceMethodNames) {
            resourceMethodName = resourceMethodName.toLowerCase();
            Resource resource = new Resource(resourceMethodName,
                    resourcePath, queryPrams, returnType, Collections.singletonList(resourceReturnStmt));
            resources.add(resource);
        }

        // Add method body statements
        List<Statement> body = new ArrayList<>();
        // Add default response
        body.add(new BallerinaStatement(String.format("http:Response %s = new;", Constants.VAR_RESPONSE)));

        // Read flow elements
        List<MuleRecord> flowElements = flow.flowElements();
        for (int i = 1; i < flowElements.size(); i++) {
            MuleRecord record = flowElements.get(i);
            switch (record.kind()) {
                case LOGGER, PAYLOAD, CHOICE, SET_VARIABLE, HTTP_REQUEST -> {
                    List<Statement> s = convertToStatements(data, record);
                    body.addAll(s);
                }
                case null -> throw new IllegalStateException();
                default -> throw new UnsupportedOperationException();
            }
        }

        // Add return statement
        body.add(new BallerinaStatement(String.format("return %s;", Constants.VAR_RESPONSE)));

        // Add service functions
        List<Function> functions = new ArrayList<>();
        functions.add(new Function("private", invokeEndPointMethodName, queryPrams, returnType, body));

        Service service = new Service(basePath, listenerRefs, resources, functions, Collections.emptyList(),
                Collections.emptyList());
        return createBallerinaModel(imports, listeners, Collections.singletonList(service));
    }

    private static BallerinaModel createBallerinaModel(List<Import> imports, List<Listener> listeners,
                                                       List<Service> services) {
        String moduleName = "muleDemo";
        Module module = new Module(moduleName, imports, Collections.emptyList(), listeners, services);
        return new BallerinaModel(new DefaultPackage(moduleName, moduleName, "1.0.0"),
                Collections.singletonList(module));
    }

    private static MuleRecord readComponent(Data data, Element element) {
        switch (element.getTagName()) {
            // Source
            case Constants.HTTP_LISTENER -> {
                return readHttpListener(data, element);
            }
            // Processes
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
            default -> throw new UnsupportedOperationException();
        }
    }

    private static List<Statement> convertToStatements(Data data, MuleRecord muleRec) {
        List<Statement> statementList = new ArrayList<>();
        switch (muleRec) {
            case Logger lg -> statementList.add(new BallerinaStatement(
                    "log:printInfo(" + normalizedFromMuleExpr(data, lg.message(), true) + ");"));
            case Payload payload -> statementList.add(new BallerinaStatement(String.format("%s.setPayload(%s);",
                    Constants.VAR_RESPONSE, normalizedFromMuleExpr(data, payload.expr(), false))));
            case Choice choice -> {
                List<WhenInChoice> whens = choice.whens();
                assert whens.size() > 1; // For valid mule config, there is at least one when

                WhenInChoice firstWhen = whens.getFirst();
                String ifCondition = normalizedFromMuleExpr(data, firstWhen.condition(), false);
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
                    ElseIfClause elseIfClause = new ElseIfClause(
                            new BallerinaExpression(normalizedFromMuleExpr(data, when.condition(), false)), elseIfBody);
                    elseIfClauses.add(elseIfClause);
                }

                List<Statement> elseBody = new ArrayList<>(choice.otherwiseProcess().size());
                for (MuleRecord r2 : choice.otherwiseProcess()) {
                    List<Statement> statements = convertToStatements(data, r2);
                    elseBody.addAll(statements);
                }
                // TODO: fix properly e.g. vars.bar == "10"
//            String condition = getVariable(data, choice.condition);
                statementList.add(new IfElseStatement(new BallerinaExpression(ifCondition), ifBody, elseIfClauses, elseBody));
            }
            case SetVariable setVariable -> {
                String varValue = normalizedFromMuleExpr(data, setVariable.value(), true);
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
            case null, default -> throw new IllegalStateException();
        }

        return statementList;
    }

    // Components
    private static Logger readLogger(Data data, Element element) {
        data.imports.add(new Import("ballerina", "log"));
        String message = element.getAttribute("message");
        return new Logger(message, "INFO");
    }

    // Flow Control
    private static Choice readChoice(Data data, Element element) {
        NodeList when = element.getElementsByTagName("when");
        List<WhenInChoice> whens = new ArrayList<>();
        for (int i = 0; i < when.getLength(); i++) {
            Node whenNode = when.item(i);
            NodeList whenProcesses = whenNode.getChildNodes();
            String condition = normalizedFromMuleExpr(data, ((Element) whenNode).getAttribute("expression"), false);
            List<MuleRecord> whenProcess = new ArrayList<>();
            for (int j = 0; j < whenProcesses.getLength(); j++) {
                Node child = whenProcesses.item(j);
                if (child.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                MuleRecord r = readComponent(data, (Element) child);
                whenProcess.add(r);
            }
            WhenInChoice whenInChoice = new WhenInChoice(condition, whenProcess);
            whens.add(whenInChoice);
        }

        NodeList otherwiseProcesses = element.getElementsByTagName("otherwise").item(0).getChildNodes();
        List<MuleRecord> OtherwiseProcess = new ArrayList<>();
        for (int i = 0; i < otherwiseProcesses.getLength(); i++) {
            Node child = otherwiseProcesses.item(i);
            if (child.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            MuleRecord r = readComponent(data, (Element) child);
            OtherwiseProcess.add(r);
        }
        return new Choice(whens, OtherwiseProcess);
    }

    // Scopes
    private static Flow readFlow(Data data, Element flowElement) {
        String flowName = flowElement.getAttribute("name");
        NodeList children = flowElement.getChildNodes();

        List<MuleRecord> flow = new ArrayList<>();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            Element element = (Element) child;
            switch (element.getTagName()) {
                case Constants.HTTP_LISTENER, Constants.LOGGER,
                        Constants.SET_VARIABLE, Constants.HTTP_REQUEST,
                        Constants.SET_PAYLOAD, Constants.CHOICE -> {
                    MuleRecord muleRec = readComponent(data, element);
                    flow.add(muleRec);
                }
                default -> throw new UnsupportedOperationException();
            }
        }

        return new Flow(flowName, flow);
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
    private static ListenerConfig readHttpListenerConfig(Data data, Element element) {
        data.imports.add(new Import(Constants.ORG_BALLERINA, Constants.MODULE_HTTP));
        String listenerName = element.getAttribute("name");
        Element listenerConnection = (Element) element.getElementsByTagName(Constants.HTTP_LISTENER_CONNECTION).item(0);
        String host = listenerConnection.getAttribute("host");
        String port = listenerConnection.getAttribute("port");
        String basePath = insertLeadingSlash(element.getAttribute("basePath"));
        HashMap<String, String> config = new HashMap<>(Collections.singletonMap("host", host));
        return new ListenerConfig(listenerName, basePath, port, config);
    }

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
}
