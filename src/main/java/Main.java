import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        try {
            // Load the Mule XML file
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse("src/main/resources/muledemo.xml");

            // Normalize the XML structure
            document.getDocumentElement().normalize();

            // Print root element
            Element root = document.getDocumentElement();

            Data data = new Data();
            // Traverse child nodes of the root element
            NodeList childNodes = root.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = childNodes.item(i);
                if (node.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }

                Element element = (Element) node;
                if (element.getTagName().equals("http:listener-config")) {
                    Listener listener = readHttpListenerConfig(data, element);
                    data.globalListenersMap.put(listener.name(), listener);
                } else if (element.getTagName().equals("flow")) {
                    BallerinaModel ballerinaModel = readFlow(data, element);
                    new BalCodeGen(ballerinaModel).generateBalCode();
                } else {
                    throw new UnsupportedOperationException();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static Listener readHttpListenerConfig(Data data, Element element) {
        data.imports.add(new Import("ballerina", "http"));
        // capture: name, host, and port
        String listenerName = element.getAttribute("name");
        Element listenerConnection = (Element) element.getElementsByTagName("http:listener-connection").item(0);
        String host = listenerConnection.getAttribute("host");
        String port = listenerConnection.getAttribute("port");
        // TODO: add base_path
        HashMap<String, String> config = new HashMap<>(Collections.singletonMap("host", host));
        return new Listener("http:Listener", listenerName, port, config);
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

            Element elmt2 = (Element) child;
            switch (elmt2.getTagName()) {
                case "http:listener" -> {
                    HttpListener hl = readHttpListener(data, elmt2);
                    flow.add(hl);
                }
                case "logger" -> {
                    Logger lg = readLogger(data, elmt2);
                    flow.add(lg);
                }
                case "set-payload" -> {
                    Payload payload = readSetPayload(data, elmt2);
                    flow.add(payload);
                }
                default -> throw new UnsupportedOperationException();
            }
        }

        // To create service we need, basePath, listeners, resources
        String basePath = "/";
        List<BallerinaModel.Import> imports = new ArrayList<>();
        List<BallerinaModel.Listener> listeners = new ArrayList<>();
        List<BallerinaModel.Resource> resources = new ArrayList<>();
        List<BallerinaModel.BodyStatement> body = new ArrayList<>();
        String returnType = "http:Created"; // TODO: consider POST, etc
        String resourcePath = null;
        String resourceMethodName = null;

        for (Import anImport : data.imports) {
            imports.add(new BallerinaModel.Import(anImport.org(), anImport.module()));
        }
        for (Listener listener : data.globalListenersMap.values()) {
            listeners.add(new BallerinaModel.Listener(listener.type(), listener.name(), listener.port(), listener.config()));
        }

        int i = 0;
        for (Record record : flow) {
            if (record instanceof HttpListener hl) {
                resourcePath = normalizedResourcePath(hl.path);
                resourceMethodName = hl.allowedMethods;
            } else if (record instanceof Logger lg) {
                var s = new BallerinaModel.BodyStatement("log:printInfo(\"" + lg.message + "\");");
                body.add(s);
            } else if (record instanceof Payload payload) {
                var s = new BallerinaModel.BodyStatement("json payload" + ++i + " = " + payload.expr + ";");
                body.add(s);
                returnType = "json";
            } else {
                throw new UnsupportedOperationException();
            }
        }
        if (i > 0) {
            var s = new BallerinaModel.BodyStatement("return payload" + i + ";");
            body.add(s);
        }

        BallerinaModel.Resource resource = new BallerinaModel.Resource(resourceMethodName, resourcePath, Collections.emptyList(), returnType, body);
        resources.add(resource);
        BallerinaModel.Service service = new BallerinaModel.Service(basePath, listeners, resources, Collections.emptyList(), Collections.emptyList());
        BallerinaModel.Module module = new BallerinaModel.Module("muleDemo", imports, Collections.emptyList(),
                Collections.singletonList(service));
        return new BallerinaModel(new BallerinaModel.DefaultPackage("muleDemo", "muleDemo", "1.0.0"), Collections.singletonList(module));
    }

    private static HttpListener readHttpListener(Data data, Element element) {
        String configRef = element.getAttribute("config-ref");
        String path = element.getAttribute("path");
        String allowedMethods = element.getAttribute("allowedMethods"); // TODO: support multiple methods
        return new HttpListener(0, configRef, path, allowedMethods);
    }

    record HttpListener(int tag, String configRef, String path, String allowedMethods) {
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
        String expr = extractExprFromMuleExpr(muleExpr);
        return new Payload(2, expr);
    }

    private static String extractExprFromMuleExpr(String muleExpr) {
        assert muleExpr.startsWith("#[") && muleExpr.endsWith("]");
        return muleExpr.substring(2, muleExpr.length() - 1);
    }

    private static String normalizedResourcePath(String resourcePath) {
        return resourcePath.startsWith("/") ? resourcePath.substring(1) : resourcePath;
    }

    static class Data {
        HashMap<String, Listener> globalListenersMap = new HashMap<>();
        HashSet<Import> imports = new HashSet<>();
    }

    public record Import(String org, String module) { }

    public record Listener(String type, String name, String port, Map<String, String> config) {}
}
