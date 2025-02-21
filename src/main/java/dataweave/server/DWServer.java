package dataweave.server;

import ballerina.BallerinaModel;
import ballerina.CodeGenerator;
import converter.MuleToBalConverter;
import dataweave.converter.BallerinaVisitor;
import dataweave.converter.DWContext;
import dataweave.converter.DWReader;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DWServer {

    public static void main(String[] args) throws IOException {
        int port = 8080;
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(port));

        while (true) {
            Socket clientSocket = serverSocket.accept();
            new Thread(() -> handleClient(clientSocket)).start();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(),
                StandardCharsets.UTF_8)); OutputStream outputStream = clientSocket.getOutputStream()) {

            String requestLine = reader.readLine();
            if (requestLine == null) {
                return;
            }

            String[] requestParts = requestLine.split(" ");
            if (requestParts.length < 3) {
                return;
            }

            String method = requestParts[0];
            String uri = requestParts[1];

            if ("POST".equals(method) && "/convert".equals(uri)) {
                Map<String, String> headers = new HashMap<>();
                String line;
                while ((line = reader.readLine()) != null && !line.isEmpty()) {
                    String[] headerParts = line.split(": ", 2);
                    if (headerParts.length == 2) {
                        headers.put(headerParts[0], headerParts[1]);
                    }
                }

                int contentLength = Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
                char[] buffer = new char[contentLength];
                reader.read(buffer, 0, contentLength);
                String requestBody = new String(buffer);

                // Extract and decode the DataWeave script
                String dataWeaveScript = extractAndDecode(requestBody);

                String convertedText = processConversion(dataWeaveScript);
                byte[] responseBytes = convertedText.getBytes(StandardCharsets.UTF_8); // Get bytes
                String response = "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: text/plain\r\n" +
                        "Content-Length: " + responseBytes.length + "\r\n" + // Correct length
                        "Access-Control-Allow-Origin: http://localhost:63342\r\n" + "\r\n" + convertedText;

                outputStream.write(response.getBytes(StandardCharsets.UTF_8)); // Write bytes
                outputStream.flush();

            } else {
                String response = "HTTP/1.1 404 Not Found\r\n\r\nNot Found\r\n" +
                        "Access-Control-Allow-Origin: http://localhost:63342\r\n";
                outputStream.write(response.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static String extractAndDecode(String requestBody) {
        String dataWeaveScript = "";
        if (requestBody.startsWith("postData=")) {
            dataWeaveScript = requestBody.substring("postData=".length());
        }
        return URLDecoder.decode(dataWeaveScript, StandardCharsets.UTF_8);
    }

    private static String processConversion(String input) {
        List<BallerinaModel.Statement> statementList = new ArrayList<>();
        List<BallerinaModel.ModuleVar> moduleVars = new ArrayList<>();
        DWContext context = new DWContext(statementList);
        MuleToBalConverter.Data data = new MuleToBalConverter.Data();
        BallerinaVisitor visitor = new BallerinaVisitor(context, data);
        ParseTree parseTree = DWReader.readDWScript(input);
        visitor.visit(parseTree);
        List<BallerinaModel.Listener> listeners = new ArrayList<>();
        List<BallerinaModel.Service> services = new ArrayList<>();
        List<String> comments = new ArrayList<>();
        BallerinaModel.TextDocument textDocument = new BallerinaModel.TextDocument("dw_sample",
                data.imports.stream().toList(), data.typeDef.values().stream().toList(), moduleVars, listeners,
                services, data.functions, comments);
        BallerinaModel.Module module = new BallerinaModel.Module("dw_sample",
                Collections.singletonList(textDocument));

        BallerinaModel ballerinaModel = new BallerinaModel(new BallerinaModel.DefaultPackage("dw_sample",
                "dw_sample", "0.1.0"), Collections.singletonList(module));
        SyntaxTree syntaxTree = new CodeGenerator(ballerinaModel).generateBalCode();
        return syntaxTree.toSourceCode();
    }
}
