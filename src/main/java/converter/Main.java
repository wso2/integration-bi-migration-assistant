package converter;

import ballerina.CodeGenerator;
import ballerina.BallerinaModel;
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

import static converter.Utils.genQueryParam;
import static converter.Utils.getAllowedMethods;
import static converter.Utils.insertLeadingSlash;
import static converter.Utils.normalizedFromMuleExpr;
import static converter.Utils.normalizedResourcePath;
import static converter.Utils.processQueryParams;
import static mule.MuleModel.*; // TODO
import static ballerina.BallerinaModel.*; // TODO

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
                ListenerConfig listenerConfig = readHttpListenerConfig(data, element);
                data.globalListenerConfigsMap.put(listenerConfig.name(), listenerConfig);
            } else if (Constants.FLOW.equals(elementTagName)) {
                BallerinaModel ballerinaModel = readFlow(data, element);
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

    private static Record readComponent(Data data, Element element) {
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
            default -> throw new UnsupportedOperationException();
        }
    }

    private static BallerinaModel readFlow(Data data, Element element) {
        String flowName = element.getAttribute("name");
        NodeList children = element.getChildNodes();
        List<Record> flow = new ArrayList<>();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            // TODO: divide into source and process
            Element elmt2 = (Element) child;
            switch (elmt2.getTagName()) {
                case Constants.HTTP_LISTENER -> {
                    HttpListener hl = readHttpListener(data, elmt2);
                    flow.add(hl);
                }
                case Constants.LOGGER , Constants.SET_VARIABLE, Constants.HTTP_REQUEST,
                        Constants.SET_PAYLOAD, Constants.CHOICE -> {
                    Record r = readComponent(data, elmt2);
                    flow.add(r);
                }
                default -> throw new UnsupportedOperationException();
            }
        }

        List<Import> imports = new ArrayList<>();
        List<Listener> listeners = new ArrayList<>();
        List<Resource> resources = new ArrayList<>();
        List<Function> functions = new ArrayList<>();
        List<Statement> body = new ArrayList<>();
        List<Parameter> queryPrams = new ArrayList<>();

        String returnType = Constants.HTTP_RESOURCE_RETURN_TYPE_DEFAULT;
        List<String> listenerRefs = new ArrayList<>();

        String basePath = null;
        String resourcePath = null;
        String[] resourceMethodNames = null;

        for (Import anImport : data.imports) {
            imports.add(new Import(anImport.org(), anImport.module()));
        }

        for (ListenerConfig listenerConfig : data.globalListenerConfigsMap.values()) {
            listeners.add(new BallerinaModel.Listener(listenerConfig.type(), listenerConfig.name(), listenerConfig.port(), listenerConfig.config()));
        }

        // TODO: make internal names unique
        body.add(new BallerinaModel.BallerinaStatement(
                String.format("http:Response %s = new;", Constants.VAR_RESPONSE)));
        for (Record record : flow) {
            if (record instanceof HttpListener hl) {
                resourcePath = normalizedResourcePath(hl.resourcePath());
                resourceMethodNames = hl.allowedMethods();
                listenerRefs.add(hl.configRef());
                String muleBasePath = data.globalListenerConfigsMap.get(hl.configRef()).basePath();
                basePath = muleBasePath.isEmpty() ? "/" : muleBasePath;
            } else if (record instanceof Logger lg) {
                List<BallerinaModel.Statement> s = convertToStatements(data, lg);
                body.addAll(s);
            } else if (record instanceof Payload payload) {
                List<BallerinaModel.Statement> s = convertToStatements(data, payload);
                body.addAll(s);
            } else if (record instanceof Choice choice) {
                List<BallerinaModel.Statement>  s = convertToStatements(data, choice); // TODO: merge similar logic
                body.addAll(s);
            } else if (record instanceof SetVariable setVariable) {
                List<BallerinaModel.Statement>  s = convertToStatements(data, setVariable);
                body.addAll(s);
            } else if (record instanceof HttpRequest httpRequest) {
                List<BallerinaModel.Statement>  s = convertToStatements(data, httpRequest);
                body.addAll(s);
            } else {
                throw new UnsupportedOperationException();
            }
        }

        body.add(new BallerinaModel.BallerinaStatement(String.format("return %s;", Constants.VAR_RESPONSE)));

        for (String qp : data.queryParams) {
            queryPrams.add(new BallerinaModel.Parameter(qp, "string",
                    Optional.of(new BallerinaModel.BallerinaExpression("\"null\""))));
        }

        int invokeEndPointCount = 0; // TODO: support different body resources
        String functionArgs = String.join(",", queryPrams.stream().map(p -> String.format("%s", p.name())).toList());
        String invokeEndPointMethodName = Constants.HTTP_ENDPOINT_METHOD_NAME + invokeEndPointCount++;

        var resourceReturnStmt = new BallerinaStatement(String.format("return self.%s(%s);",
                invokeEndPointMethodName, functionArgs));

        for (String resourceMethodName : resourceMethodNames) {
            resourceMethodName = resourceMethodName.toLowerCase();
            BallerinaModel.Resource resource = new BallerinaModel.Resource(resourceMethodName,
                    resourcePath, queryPrams, returnType, Collections.singletonList(resourceReturnStmt));
            resources.add(resource);
        }

        functions.add(new Function("private",invokeEndPointMethodName, queryPrams, returnType, body));

        BallerinaModel.Service service = new Service(basePath, listenerRefs, resources,
                functions, Collections.emptyList(), Collections.emptyList());
        BallerinaModel.Module module = new BallerinaModel.Module("muleDemo", imports, Collections.emptyList(),
                listeners, Collections.singletonList(service));
        return new BallerinaModel(new BallerinaModel.DefaultPackage("muleDemo", "muleDemo", "1.0.0"), Collections.singletonList(module));
    }

    private static List<BallerinaModel.Statement> convertToStatements(Data data, Record r) {
        List<BallerinaModel.Statement> ls = new ArrayList<>();
        if (r instanceof Logger lg) {
            ls.add(new BallerinaModel.BallerinaStatement(
                    "log:printInfo(" + normalizedFromMuleExpr(data, lg.message(), true) + ");"));
        } else if (r instanceof Payload payload) {
            ls.add(new BallerinaModel.BallerinaStatement(String.format("%s.setPayload(%s);", Constants.VAR_RESPONSE,
                    normalizedFromMuleExpr(data, payload.expr(), false))));
        } else if (r instanceof Choice choice){
            List<WhenInChoice> whens = choice.whens();
            assert whens.size() > 1; // For valid mule config, there is at least one when

            WhenInChoice firstWhen = whens.getFirst();
            String ifCondition = normalizedFromMuleExpr(data, firstWhen.condition(), false);
            List<BallerinaModel.Statement> ifBody = new ArrayList<>();
            for (Record r2: firstWhen.process()) {
                List<BallerinaModel.Statement> statements = convertToStatements(data, r2);
                ifBody.addAll(statements);
            }

            List<BallerinaModel.ElseIfClause> elseIfClauses = new ArrayList<>(whens.size()-1);
            for (int i = 1; i < whens.size(); i++) {
                WhenInChoice when = whens.get(i);
                List<BallerinaModel.Statement> elseIfBody = new ArrayList<>();
                for (Record r2: when.process()) {
                    List<BallerinaModel.Statement> statements = convertToStatements(data, r2);
                    elseIfBody.addAll(statements);
                }
                BallerinaModel.ElseIfClause elseIfClause = new BallerinaModel.ElseIfClause(
                        new BallerinaModel.BallerinaExpression(normalizedFromMuleExpr(data, when.condition(), false)),
                        elseIfBody);
                elseIfClauses.add(elseIfClause);
            }

            List<BallerinaModel.Statement> elseBody = new ArrayList<>(choice.otherwiseProcess().size());
            for (Record r2: choice.otherwiseProcess()) {
                List<BallerinaModel.Statement> statements = convertToStatements(data, r2);
                elseBody.addAll(statements);
            }
            // TODO: fix properly e.g. vars.bar == "10"
//            String condition = getVariable(data, choice.condition);
            ls.add(new BallerinaModel.IfElseStatement(new BallerinaModel.BallerinaExpression(ifCondition), ifBody, elseIfClauses,
                    elseBody));
        } else if (r instanceof SetVariable setVariable) {
            String varValue = normalizedFromMuleExpr(data, setVariable.value(), true);
            ls.add(new BallerinaModel.BallerinaStatement("string " + setVariable.variableName() + " = " + varValue +
                    ";"));
        } else if (r instanceof HttpRequest httpRequest) {
            List<BallerinaModel.Statement> statements = new ArrayList<>();
            String path = httpRequest.path();
            String method = httpRequest.method();
            String url = httpRequest.url();
            Map<String, String> queryParams = httpRequest.queryParams();

            statements.add(new BallerinaModel.BallerinaStatement(String.format(
                    "http:Client %s = check new(\"%s\");", Constants.VAR_CLIENT, url)));
            statements.add(new BallerinaModel.BallerinaStatement(
                    String.format("http:Response %s = check %s->%s/.%s(%s);",
                    Constants.VAR_CLIENT_GET, Constants.VAR_CLIENT, path, method.toLowerCase(),
                            genQueryParam(queryParams))));
//            statements.add(new ballerina.BallerinaModel.BallerinaStatement("http:Response \\$client\\$response = check " +
//                    "\\$client\\$get.ensureType(http:Response);"));
            // TODO: revisit
//            statements.add(new ballerina.BallerinaModel.BallerinaStatement("return \\$client\\$response;"));
            ls.addAll(statements);
        } else {
            throw new IllegalStateException();
        }

        return ls;
    }

    // Components
    private static Logger readLogger(Data data, Element element) {
        data.imports.add(new Import("ballerina", "log"));
        String message = element.getAttribute("message");
        return new Logger(1, message, "INFO");
    }

    // Flow Control
    private static Choice readChoice(Data data, Element element) {
        NodeList when = element.getElementsByTagName("when");
        List<WhenInChoice> whens = new ArrayList<>();
        for (int i = 0; i < when.getLength(); i++) {
            Node whenNode = when.item(i);
            NodeList whenProcesses = whenNode.getChildNodes();
            String condition = normalizedFromMuleExpr(data, ((Element) whenNode).getAttribute("expression"), false);
            List<Record> whenProcess = new ArrayList<>();
            for (int j = 0; j < whenProcesses.getLength(); j++) {
                Node child = whenProcesses.item(j);
                if (child.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                Record r = readComponent(data, (Element) child);
                whenProcess.add(r);
            }
            WhenInChoice whenInChoice = new WhenInChoice(condition, whenProcess);
            whens.add(whenInChoice);
        }

        NodeList otherwiseProcesses = element.getElementsByTagName("otherwise").item(0).getChildNodes();
        List<Record> OtherwiseProcess = new ArrayList<>();
        for (int i = 0; i < otherwiseProcesses.getLength(); i++) {
            Node child = otherwiseProcesses.item(i);
            if (child.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Record r = readComponent(data, (Element) child);
            OtherwiseProcess.add(r);
        }
        return new Choice(whens, OtherwiseProcess);
    }

    // Scopes


    // Transformers
    private static Payload readSetPayload(Data data, Element element) {
        String muleExpr = element.getAttribute("value");
        return new Payload(2, muleExpr);
    }

    private static SetVariable readSetVariable(Data data, Element element) {
        String varName = element.getAttribute("variableName");
        String val = element.getAttribute("value");
        return new SetVariable(varName, val);
    }

    // HTTP Module
    private static ListenerConfig readHttpListenerConfig(Data data, Element element) {
        data.imports.add(new Import(Constants.ORG_BALLERINA, Constants.MODULE_HTTP));
        // capture: name, host, and port
        String listenerName = element.getAttribute("name");
        Element listenerConnection = (Element) element.getElementsByTagName(Constants.HTTP_LISTENER_CONNECTION).item(0);
        String host = listenerConnection.getAttribute("host");
        String port = listenerConnection.getAttribute("port");
        String basePath = insertLeadingSlash(element.getAttribute("basePath"));
        HashMap<String, String> config = new HashMap<>(Collections.singletonMap("host", host));
        return new ListenerConfig("http:Listener", listenerName, basePath, port, config);
    }

    private static HttpListener readHttpListener(Data data, Element element) {
        String configRef = element.getAttribute("config-ref");
        String resourcePath = element.getAttribute("path");
        String[] allowedMethods = Arrays.stream(getAllowedMethods(element.getAttribute("allowedMethods")))
                .map(String::toLowerCase).toArray(String[]::new);
        return new HttpListener(0, configRef, resourcePath, allowedMethods);
    }

    private static HttpRequest readHttpRequest(Data data, Element element) {
        String method = element.getAttribute("method").toLowerCase();
        String url = element.getAttribute("url").toLowerCase();
        String path = element.getAttribute("path").toLowerCase();
        Element queryParamsElement = (Element) element.getElementsByTagName(Constants.HTTP_QUERY_PARAMS).item(0);
        CDATASection cdataSection = (CDATASection) queryParamsElement.getChildNodes().item(0);
        Map<String, String> queryParams = processQueryParams(cdataSection.getData().trim());
        return new HttpRequest(0, method, url, path, queryParams);
    }
}
