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
        String basePath = normalizedBasePath(element.getAttribute("basePath"));
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

        int i = 0;
        for (Record record : flow) {
            if (record instanceof HttpListener hl) {
                resourcePath = normalizedResourcePath(hl.resourcePath);
                resourceMethodNames = hl.allowedMethods;
                listenerRefs.add(hl.configRef);
                String muleBasePath = data.globalListenerConfigsMap.get(hl.configRef).basePath;
                basePath = muleBasePath.isEmpty() ? "/" : muleBasePath;
            } else if (record instanceof Logger lg) {
                BallerinaModel.Statement s = convertToStatement(data, lg);
                body.add(s);
            } else if (record instanceof Payload payload) {
                BallerinaModel.Statement s = convertToStatement(data, payload);
                body.add(s);
            } else if (record instanceof Choice choice) {
                BallerinaModel.Statement s = convertToStatement(data, choice);
                body.add(s);
            } else if (record instanceof SetVariable setVariable) {
                BallerinaModel.Statement s = convertToStatement(data, setVariable);
                body.add(s);
            } else {
                throw new UnsupportedOperationException();
            }
        }
        if (i > 0) {
            var s = new BallerinaModel.BallerinaStatement("return payload" + i + ";");
            body.add(s);
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

    private static BallerinaModel.Statement convertToStatement(Data data, Record r) {
        if (r instanceof Logger lg) {
            return new BallerinaModel.BallerinaStatement("log:printInfo(\"" + lg.message + "\");");
        } else if (r instanceof Payload payload) {
            // TODO: Add multiple payload support with i.
//            return new BallerinaModel.BallerinaStatement("json payload" + ++i + " = " + payload.expr + ";");
            return new BallerinaModel.BallerinaStatement("json payload" + " = " + payload.expr + ";");
        } else if (r instanceof Choice choice){
            List<BallerinaModel.Statement> x = new ArrayList<>(choice.whenProcess.size());
            for (Record r2: choice.whenProcess) {
                BallerinaModel.Statement statement = convertToStatement(data, r2);
                x.add(statement);
            }

            List<BallerinaModel.Statement> y = new ArrayList<>(choice.otherwiseProcess.size());
            for (Record r2: choice.otherwiseProcess) {
                BallerinaModel.Statement statement = convertToStatement(data, r2);
                y.add(statement);
            }
            // TODO: fix properly e.g. vars.bar == "10"
            String condition = getVariable(data, choice.condition);
            return new BallerinaModel.IfElseStatement(new BallerinaModel.BallerinaExpression(condition), x, y);
        } else if (r instanceof SetVariable setVariable) {
            String varValue = getVariable(data, setVariable.value);
            return new BallerinaModel.BallerinaStatement("string " + setVariable.variableName + " = " + varValue + ";");
        } else {
            throw new IllegalStateException();
        }
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

    record Payload(int tag, String expr) { }

    private static Logger readLogger(Data data, Element element) {
        data.imports.add(new Import("ballerina", "log"));
        String message = element.getAttribute("message");
        return new Logger(1, message, "INFO");
    }

    private static Payload readSetPayload(Data data, Element element) {
        String muleExpr = element.getAttribute("value");
        String expr = normalizedFromMuleExpr(muleExpr);
        return new Payload(2, expr);
    }

    private static SetVariable readSetVariable(Data data, Element element) {
        String varName = element.getAttribute("variableName");
        String val = normalizedFromMuleExpr(element.getAttribute("value"));
        return new SetVariable(varName, val);
    }

    record SetVariable(String variableName, String value) { }

    private static Choice readChoice(Data data, Element element) {
        Node whenNode = element.getElementsByTagName("when").item(0);
        NodeList whenProcesses = whenNode.getChildNodes();
        String condition = normalizedFromMuleExpr(((Element) whenNode).getAttribute("expression"));
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

    private static String normalizedFromMuleExpr(String muleExpr) {
        if (muleExpr.startsWith("#[") && muleExpr.endsWith("]")) {
            return muleExpr.substring(2, muleExpr.length() - 1);
        }
        return muleExpr;
    }

    private static String normalizedResourcePath(String resourcePath) {
        return resourcePath.startsWith("/") ? resourcePath.substring(1) : resourcePath;
    }

    private static String normalizedBasePath(String basePath) {
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
