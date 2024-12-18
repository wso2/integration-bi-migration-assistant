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

public class Main {

    static class Data {
        HashMap<String, ListenerConfig> globalListenerConfigsMap = new HashMap<>();
        HashSet<Import> imports = new HashSet<>();
        HashSet<String> queryParams = new HashSet<>();
        boolean hasPayload;
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
                new BalCodeGen(ballerinaModel).generateBalCode();
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

    private static Record readComponent(Data data, Element element) {
        switch (element.getTagName()) {
            case Constants.LOGGER -> {
                return readLogger(data, element);
            }
            case Constants.SET_VARIABLE -> {
                return readSetVariable(data, element);
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
                case Constants.LOGGER , Constants.SET_VARIABLE -> {
                    Record r = readComponent(data, elmt2);
                    flow.add(r);
                }
                case Constants.SET_PAYLOAD -> {
                    Payload payload = readSetPayload(data, elmt2);
                    flow.add(payload);
                }
                case Constants.CHOICE -> {
                    Choice choice = readChoice(data, elmt2);
                    flow.add(choice);
                }
                case Constants.HTTP_REQUEST -> {
                    HttpRequest httpRequest = readHttpRequest(data, elmt2);
                    flow.add(httpRequest);
                }
                default -> throw new UnsupportedOperationException();
            }
        }

        // To create service we need, basePath, listeners, resources
        String basePath = null;
        List<BallerinaModel.Import> imports = new ArrayList<>();
        List<BallerinaModel.Listener> listeners = new ArrayList<>();
        List<BallerinaModel.Resource> resources = new ArrayList<>();
        List<BallerinaModel.Statement> body = new ArrayList<>();
        List<BallerinaModel.Parameter> queryPrams = new ArrayList<>();

        // TODO: narrow down return type
        String returnType = "anydata|http:Response|http:StatusCodeResponse|" +
                "stream<http:SseEvent, error?>|stream<http:SseEvent, error>|error";
        String resourcePath = null;
        String[] resourceMethodNames = null;
        List<String> listenerRefs = new ArrayList<>();

        for (Import anImport : data.imports) {
            imports.add(new BallerinaModel.Import(anImport.org(), anImport.module()));
        }

        for (ListenerConfig listenerConfig : data.globalListenerConfigsMap.values()) {
            listeners.add(new BallerinaModel.Listener(listenerConfig.type(), listenerConfig.name(), listenerConfig.port(), listenerConfig.config()));
        }

        for (Record record : flow) {
            if (record instanceof HttpListener hl) {
                resourcePath = removeLeadingSlash(hl.resourcePath);
                resourceMethodNames = hl.allowedMethods;
                listenerRefs.add(hl.configRef);
                String muleBasePath = data.globalListenerConfigsMap.get(hl.configRef).basePath;
                basePath = muleBasePath.isEmpty() ? "/" : muleBasePath;
            } else if (record instanceof Logger lg) {
                List<BallerinaModel.Statement> s = convertToStatements(data, lg);
                body.addAll(s);
            } else if (record instanceof Payload payload) {
                data.hasPayload = true;
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
        if (data.hasPayload) {
            body.add(new BallerinaModel.BallerinaStatement("return " + "\\$payload\\$;"));
        }

        for (String qp : data.queryParams) {
            queryPrams.add(new BallerinaModel.Parameter(qp, "string",
                    Optional.of(new BallerinaModel.BallerinaExpression("\"null\""))));
        }

        for (String resourceMethodName : resourceMethodNames) {
            resourceMethodName = resourceMethodName.toLowerCase();
            BallerinaModel.Resource resource = new BallerinaModel.Resource(resourceMethodName,
                    resourcePath, queryPrams, returnType == null ? "http:Created": returnType, body);
            resources.add(resource);
        }

        BallerinaModel.Service service = new BallerinaModel.Service(basePath, listenerRefs, resources,
                Collections.emptyList(), Collections.emptyList());
        BallerinaModel.Module module = new BallerinaModel.Module("muleDemo", imports, Collections.emptyList(),
                listeners, Collections.singletonList(service));
        return new BallerinaModel(new BallerinaModel.DefaultPackage("muleDemo", "muleDemo", "1.0.0"), Collections.singletonList(module));
    }

    private static List<BallerinaModel.Statement> convertToStatements(Data data, Record r) {
        List<BallerinaModel.Statement> ls = new ArrayList<>();
        if (r instanceof Logger lg) {
            ls.add(new BallerinaModel.BallerinaStatement(
                    "log:printInfo(" + normalizedFromMuleExpr(data, lg.message, true) + ");"));
        } else if (r instanceof Payload payload) {
            // TODO: Add multiple payload support with i.
//            return new BallerinaModel.BallerinaStatement("json payload" + ++i + " = " + payload.expr + ";");
            ls.add(new BallerinaModel.BallerinaStatement(
                    "json \\$payload\\$" + " = " + normalizedFromMuleExpr(data, payload.expr, false) + ";"));
        } else if (r instanceof Choice choice){
            List<BallerinaModel.Statement> x = new ArrayList<>(choice.whenProcess.size());
            for (Record r2: choice.whenProcess) {
                List<BallerinaModel.Statement> statements = convertToStatements(data, r2);
                x.addAll(statements);
            }

            List<BallerinaModel.Statement> y = new ArrayList<>(choice.otherwiseProcess.size());
            for (Record r2: choice.otherwiseProcess) {
                List<BallerinaModel.Statement> statements = convertToStatements(data, r2);
                y.addAll(statements);
            }
            // TODO: fix properly e.g. vars.bar == "10"
            String condition = getVariable(data, choice.condition);
            ls.add(new BallerinaModel.IfElseStatement(new BallerinaModel.BallerinaExpression(condition), x, y));
        } else if (r instanceof SetVariable setVariable) {
            String varValue = normalizedFromMuleExpr(data, setVariable.value, true);
            ls.add(new BallerinaModel.BallerinaStatement("string " + setVariable.variableName + " = " + varValue + ";"));
        } else if (r instanceof HttpRequest httpRequest) {
            List<BallerinaModel.Statement> statements = new ArrayList<>();
            String path = httpRequest.path();
            String method = httpRequest.method();
            String url = httpRequest.url();
            Map<String, String> queryParams = httpRequest.queryParams();

            statements.add(new BallerinaModel.BallerinaStatement(String.format(
                    "http:Client \\$client\\$ = check new(\"%s\");", url)));
            statements.add(new BallerinaModel.BallerinaStatement(
                    String.format(
                            "http:Response|anydata|stream<http:SseEvent, error?> \\$client\\$get = check " +
                                    "\\$client\\$->%s.%s(%s);",
                    path, method.toLowerCase(), genQueryParam(queryParams))));
            statements.add(new BallerinaModel.BallerinaStatement("http:Response \\$client\\$response = check " +
                    "\\$client\\$get.ensureType(http:Response);"));
            // TODO: revisit
//            statements.add(new BallerinaModel.BallerinaStatement("return \\$client\\$response;"));
            ls.addAll(statements);
        } else {
            throw new IllegalStateException();
        }

        return ls;
    }

    private static String genQueryParam(Map<String, String> queryParams) {
        return queryParams.entrySet().stream()
                .map(e -> String.format("%s = \"%s\"", e.getKey(), e.getValue())).reduce((a, b) -> a + ", " + b).orElse("");
    }

    private static String getVariable(Data data, String value) {
        String queryParamPrefix = "attributes.queryParams.";
        String varPrefix = "vars.";

        String v;
        if (value.startsWith(queryParamPrefix)) {
            v = value.substring(queryParamPrefix.length());
            data.queryParams.add(v);
        } else if (value.startsWith(varPrefix)) {
            v = value.substring(varPrefix.length());
        } else {
            v = value;
        }
        return v;
    }

    private static HttpListener readHttpListener(Data data, Element element) {
        String configRef = element.getAttribute("config-ref");
        String resourcePath = element.getAttribute("path");
        String[] allowedMethods = Arrays.stream(getAllowedMethods(element.getAttribute("allowedMethods")))
                .map(String::toLowerCase).toArray(String[]::new);
        return new HttpListener(0, configRef, resourcePath, allowedMethods);
    }

    record HttpListener(int tag, String configRef, String resourcePath, String[] allowedMethods) {
    }

    record Logger(int tag, String message, String level) { }

    record Payload(int tag, String expr) {
    }

    record HttpRequest(int tag, String method, String url, String path, Map<String, String> queryParams) { }

    private static Logger readLogger(Data data, Element element) {
        data.imports.add(new Import("ballerina", "log"));
        String message = element.getAttribute("message");
        return new Logger(1, message, "INFO");
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

    private static Map<String, String> processQueryParams(String queryParams) {
        assert queryParams.endsWith("}]");
        String regex = "#\\[output .*\\n---\\n\\{\\n|\\n}]";
        String trimmed = queryParams.replaceAll(regex, "").trim();
        String[] pairs = trimmed.split(",\\n\\t");
        Map<String, String> keyValues = new HashMap<>(pairs.length);
        for (String pair : pairs) {
            String[] kv = pair.split(":");
            keyValues.put(kv[0].trim().replace("\"", ""), kv[1].trim().replace("\"", ""));
        }
        return keyValues;
    }

    private static Payload readSetPayload(Data data, Element element) {
        String muleExpr = element.getAttribute("value");
        return new Payload(2, muleExpr);
    }

    private static SetVariable readSetVariable(Data data, Element element) {
        String varName = element.getAttribute("variableName");
        String val = element.getAttribute("value");
        return new SetVariable(varName, val);
    }

    record SetVariable(String variableName, String value) { }

    private static Choice readChoice(Data data, Element element) {
        Node whenNode = element.getElementsByTagName("when").item(0);
        NodeList whenProcesses = whenNode.getChildNodes();
        String condition = normalizedFromMuleExpr(data, ((Element) whenNode).getAttribute("expression"), false);
        List<Record> whenProcess = new ArrayList<>();
        for (int i = 0; i < whenProcesses.getLength(); i++) {
            Node child = whenProcesses.item(i);
            if (child.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Record r = readComponent(data, (Element) child);
            whenProcess.add(r);
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
        return new Choice(condition, whenProcess, OtherwiseProcess);
    }

    record Choice(String kind, String condition, List<Record> whenProcess, List<Record> otherwiseProcess) {
        Choice(String condition, List<Record> whenProcess, List<Record> otherwiseProcess) {
            this("Choice", condition, whenProcess, otherwiseProcess);
        }
    }

    private static String normalizedFromMuleExpr(Data data, String muleExpr, boolean encloseInDoubleQuotes) {
        if (muleExpr.startsWith("#[") && muleExpr.endsWith("]")) {
            var innerExpr = muleExpr.substring(2, muleExpr.length() - 1);
            return getVariable(data, innerExpr);
        }
        return encloseInDoubleQuotes? "\"" + muleExpr + "\"" : muleExpr;
    }

    private static String removeLeadingSlash(String resourcePath) {
        return resourcePath.startsWith("/") ? resourcePath.substring(1) : resourcePath;
    }

    private static String insertLeadingSlash(String basePath) {
        return basePath.startsWith("/") ? basePath : "/" + basePath;
    }

    private static String[] getAllowedMethods(String allowedMethods) {
        if (allowedMethods.isEmpty()) {
            // Leaving empty will allow all
            // TODO: check and support other methods
            return new String[]{"GET", "POST"};
        }
        return allowedMethods.split(",\\s*");
    }

    public record Import(String org, String module) { }

    // TODO: rename type to something meaningful like tag
    public record ListenerConfig(String type, String name, String basePath, String port, Map<String, String> config) {}
}
