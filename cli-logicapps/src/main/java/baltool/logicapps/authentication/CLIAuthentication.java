package baltool.logicapps.authentication;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.awt.Desktop;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static baltool.logicapps.Constants.AUTHENTICATION_TIMEOUT_SECONDS;
import static baltool.logicapps.Constants.AUTH_CLIENT_ID;
import static baltool.logicapps.Constants.AUTH_ORG;
import static baltool.logicapps.Constants.AUTH_REDIRECT_URL;
import static baltool.logicapps.Constants.BALLERINA_USER_HOME_NAME;
import static baltool.logicapps.Constants.CONFIG_FILE_PATH;

public class CLIAuthentication {
    private static final PrintStream errStream = System.err;
    private static final PrintStream outStream = System.out;
    private static final String USER_HOME = System.getProperty("user.home");

    // Instance variables for the authentication flow
    private HttpServer server;
    private CountDownLatch callbackReceived;
    private String authorizationCode;
    private String receivedState;
    private String error;

    /**
     * Retrieves a valid access token, refreshing it if necessary.
     *
     * @return Valid access token
     * @throws Exception If authentication fails or network issues occur
     */
    public static String getValidAccessToken() throws Exception {
        String existingToken = loadTokenFromConfig("accessToken");
        if (existingToken != null) {
            if (isTokenValid(existingToken)) {
                return existingToken;
            } else {
                String refreshToken = loadTokenFromConfig("refreshToken");
                if (refreshToken != null) {
                    String newAccessToken = refreshAccessToken(refreshToken);
                    if (newAccessToken != null) {
                        return newAccessToken;
                    }
                }
            }
        }

        outStream.println("Performing new authentication...");
        return performFullAuthentication();
    }

    /**
     * Performs full authentication flow to obtain access token.
     *
     * @return Access token if successful, null otherwise
     * @throws Exception If authentication fails or network issues occur
     */
    private static String performFullAuthentication() throws Exception {
        CLIAuthentication authInstance = new CLIAuthentication();
        String authCode = authInstance.authenticate();
        if (authCode != null) {
            return exchangeCodeForTokens(authCode);
        }
        return null;
    }

    /**
     * Refreshes the access token using the refresh token.
     *
     * @param refreshToken The refresh token to use for obtaining a new access token
     * @return New access token if successful, null otherwise
     */
    private static String refreshAccessToken(String refreshToken) {
        try {
            String tokenUrl = String.format("https://api.asgardeo.io/t/%s/oauth2/token", AUTH_ORG);

            String requestBody = String.format(
                    "grant_type=refresh_token&refresh_token=%s&client_id=%s",
                    URLEncoder.encode(refreshToken, StandardCharsets.UTF_8),
                    URLEncoder.encode(AUTH_CLIENT_ID, StandardCharsets.UTF_8)
            );

            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(tokenUrl))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .timeout(Duration.ofSeconds(30))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String responseBody = response.body();

                String newAccessToken = extractTokenFromResponse(responseBody, "access_token");
                String newRefreshToken = extractTokenFromResponse(responseBody, "refresh_token");

                if (newAccessToken != null) {
                    saveTokenToConfig("accessToken", newAccessToken);
                    // Update refresh token if a new one is provided
                    if (newRefreshToken != null) {
                        saveTokenToConfig("refreshToken", newRefreshToken);
                    }
                    return newAccessToken;
                } else {
                    return null;
                }
            } else {
                errStream.println("Token refresh failed with status: " + response.statusCode());
                if (response.body() != null) {
                    errStream.println("Error response: " + response.body());
                }
                return null;
            }
        } catch (IOException e) {
            errStream.println("Network error while refreshing token: " + e.getMessage());
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            errStream.println("Token refresh interrupted");
            return null;
        } catch (Exception e) {
            errStream.println("Error refreshing token: " + e.getMessage());
            return null;
        }
    }

    /**
     * Exchanges the authorization code for access and refresh tokens.
     *
     * @param authCode The authorization code received from the OAuth server
     * @return Access token if successful, null otherwise
     * @throws Exception If network issues occur or response parsing fails
     */
    private static String exchangeCodeForTokens(String authCode) throws Exception {
        String tokenUrl = String.format("https://api.asgardeo.io/t/%s/oauth2/token", AUTH_ORG);

        String requestBody = String.format(
                "grant_type=authorization_code&code=%s&redirect_uri=%s&client_id=%s",
                URLEncoder.encode(authCode, StandardCharsets.UTF_8),
                URLEncoder.encode(AUTH_REDIRECT_URL, StandardCharsets.UTF_8),
                URLEncoder.encode(AUTH_CLIENT_ID, StandardCharsets.UTF_8)
        );

        // Create HTTP client with timeout
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        // Build the request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(tokenUrl))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .timeout(Duration.ofSeconds(30))
                .build();

        // Send the request
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            String responseBody = response.body();
            outStream.println("Authentication successful!");

            // Parse the response to extract tokens
            String accessToken = extractTokenFromResponse(responseBody, "access_token");
            String refreshToken = extractTokenFromResponse(responseBody, "refresh_token");

            if (accessToken != null) {
                saveTokenToConfig("accessToken", accessToken);
                if (refreshToken != null) {
                    saveTokenToConfig("refreshToken", refreshToken);
                }
                return accessToken;
            } else {
                throw new RuntimeException("No access token found in response");
            }
        } else {
            String errorMsg = "Token exchange failed with status: " + response.statusCode();
            if (response.body() != null) {
                errorMsg += "\nError response: " + response.body();
            }
            throw new RuntimeException(errorMsg);
        }
    }

    /**
     * Extracts a specific token type from the response body.
     *
     * @param responseBody The JSON response body as a String
     * @param tokenType    The type of token to extract (e.g., "access_token", "refresh_token")
     * @return The extracted token or null if not found
     */
    private static String extractTokenFromResponse(String responseBody, String tokenType) {
        try {
            String searchPattern = "\"" + tokenType + "\":\"";
            int startIndex = responseBody.indexOf(searchPattern);
            if (startIndex != -1) {
                startIndex += searchPattern.length();
                int endIndex = responseBody.indexOf("\"", startIndex);
                if (endIndex != -1) {
                    return responseBody.substring(startIndex, endIndex);
                }
            }
        } catch (Exception e) {
            errStream.println("Error extracting " + tokenType + " from response: " + e.getMessage());
        }
        return null;
    }

    /**
     * Validates the access token by checking its status via the introspection endpoint.
     *
     * @param accessToken The access token to validate
     * @return true if the token is valid, false otherwise
     */
    private static boolean isTokenValid(String accessToken) {
        try {
            // Use the introspection endpoint to validate the token
            String introspectUrl = String.format("https://api.asgardeo.io/t/%s/oauth2/introspect", AUTH_ORG);

            String requestBody = String.format(
                    "redirect_uri=%s&token=%s&client_id=%s",
                    URLEncoder.encode(AUTH_REDIRECT_URL, StandardCharsets.UTF_8),
                    URLEncoder.encode(accessToken, StandardCharsets.UTF_8),
                    URLEncoder.encode(AUTH_CLIENT_ID, StandardCharsets.UTF_8)
            );

            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(introspectUrl))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .timeout(Duration.ofSeconds(30))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String responseBody = response.body();
                // Check if the token is active by looking for "active":true in the response
                if (responseBody != null && responseBody.contains("\"active\":true")) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            errStream.println("Error validating access token: " + e.getMessage());
            return false;
        }
    }

    /**
     * Gets the path to the configuration file where tokens are stored.
     *
     * @return Path to the config file
     */
    private static Path getConfigFilePath() {
        return Paths.get(USER_HOME, BALLERINA_USER_HOME_NAME, CONFIG_FILE_PATH);
    }

    /**
     * Loads a specific token from the config file.
     *
     * @param tokenKey The key of the token to load (e.g., "accessToken", "refreshToken")
     * @return The token value if found, null otherwise
     */
    private static String loadTokenFromConfig(String tokenKey) {
        try {
            Path filePath = getConfigFilePath();
            if (Files.exists(filePath)) {
                String content = Files.readString(filePath, StandardCharsets.UTF_8);
                String searchPattern = tokenKey + "=\"";
                int startIndex = content.indexOf(searchPattern);
                if (startIndex != -1) {
                    startIndex += searchPattern.length();
                    int endIndex = content.indexOf("\"", startIndex);
                    if (endIndex != -1) {
                        String token = content.substring(startIndex, endIndex);
                        return token.isEmpty() ? null : token;
                    }
                }
            }
        } catch (IOException e) {
            errStream.println("Error reading config file: " + e.getMessage());
        }
        return null;
    }

    /**
     * Saves a token to the configuration file.
     *
     * @param tokenKey   The key for the token (e.g., "accessToken", "refreshToken")
     * @param tokenValue The value of the token to save
     */
    private static void saveTokenToConfig(String tokenKey, String tokenValue) {
        try {
            Path filePath = getConfigFilePath();
            String content = "";

            // Read existing content if file exists
            if (Files.exists(filePath)) {
                content = Files.readString(filePath, StandardCharsets.UTF_8);
            }

            String tokenLine = tokenKey + "=\"" + tokenValue + "\"";
            String searchPattern = tokenKey + "=\"";

            // Check if the token already exists in the file
            int startIndex = content.indexOf(searchPattern);
            if (startIndex != -1) {
                int lineStart = content.lastIndexOf('\n', startIndex) + 1;
                if (lineStart == 0) {
                    lineStart = 0;
                }
                int lineEnd = content.indexOf('\n', startIndex);
                if (lineEnd == -1) {
                    lineEnd = content.length(); // Handle last line
                }

                String newContent = content.substring(0, lineStart) +
                        tokenLine +
                        (lineEnd < content.length() ? content.substring(lineEnd) : "");
                content = newContent;
            } else {
                if (!content.isEmpty() && !content.endsWith("\n")) {
                    content += "\n";
                }
                content += tokenLine + "\n";
            }

            Files.writeString(filePath, content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            errStream.println("Error saving " + tokenKey + " to config file: " + e.getMessage());
        }
    }

    /**
     * Starts the authentication process and returns the authorization code.
     *
     * @return Authorization code if successful
     * @throws Exception If authentication fails or network issues occur
     */
    private String authenticate() throws Exception {
        int port = findAvailablePort();
        String callbackUri = "http://localhost:" + port + "/callback";
        String state = generateState(callbackUri);
        startServer(port);
        try {
            openBrowser(state);
            boolean received = callbackReceived.await(AUTHENTICATION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!received) {
                throw new RuntimeException("Authentication timed out");
            }
            if (error != null) {
                throw new RuntimeException("Authentication failed: " + error);
            }
            return authorizationCode;
        } finally {
            if (server != null) {
                server.stop(0);
            }
        }
    }

    /**
     * Finds an available port on the local machine.
     *
     * @return An available port number
     * @throws IOException If an error occurs while creating the server socket
     */
    private int findAvailablePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    /**
     * Starts an HTTP server to handle the OAuth callback.
     *
     * @param port The port on which the server will listen
     * @throws IOException If an error occurs while starting the server
     */
    private void startServer(int port) throws IOException {
        callbackReceived = new CountDownLatch(1);
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/callback", new CallbackHandler());
        server.setExecutor(null);
        server.start();
    }

    /**
     * Opens the default web browser to the authentication URL.
     *
     * @param state The state parameter to include in the URL
     * @throws Exception If an error occurs while opening the browser
     */
    private void openBrowser(String state) throws Exception {
        String authUrl = String.format(
                "https://api.asgardeo.io/t/%s/oauth2/authorize?response_type=code&redirect_uri=%s&client_id=%s&" +
                        "scope=openid%%20email&state=%s",
                AUTH_ORG, AUTH_REDIRECT_URL, AUTH_CLIENT_ID, state
        );

        outStream.println("Opening browser for authentication...");
        outStream.println("If browser doesn't open automatically, visit: " + authUrl);

        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI(authUrl));
        } else {
            outStream.println("Please open the above URL in your browser manually.");
        }
    }

    /**
     * Generates a state parameter for the OAuth flow, which includes the callback URI.
     *
     * @param callbackUri The URI to redirect to after authentication
     * @return Base64 encoded state string
     */
    private String generateState(String callbackUri) {
        Gson gson = new Gson();
        Map<String, String> data = Map.of("callbackUri", callbackUri);
        String json = gson.toJson(data);
        String base64Encoded = Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        String state = URLEncoder.encode(base64Encoded, StandardCharsets.UTF_8);

        return state;
    }

    /**
     * Callback handler for the HTTP server that processes the OAuth response.
     */
    private class CallbackHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();

            try {
                if (query != null) {
                    String[] params = query.split("&");
                    for (String param : params) {
                        String[] keyValue = param.split("=", 2);
                        if (keyValue.length == 2) {
                            String key = keyValue[0];
                            String value = keyValue[1];

                            switch (key) {
                                case "code":
                                    authorizationCode = value;
                                    break;
                                case "state":
                                    receivedState = value;
                                    break;
                                case "error":
                                    error = value;
                                    break;
                                case "error_description":
                                    if (error != null) {
                                        error += ": " + value;
                                    }
                                    break;
                            }
                        }
                    }
                }

                // Send response to browser
                String response;
                if (authorizationCode != null) {
                    response = createSuccessPage();
                } else {
                    response = createErrorPage(error != null ? error : "Unknown error");
                }

                exchange.getResponseHeaders().set("Content-Type", "text/html");
                exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);

                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                }

            } finally {
                // Signal that callback was received
                callbackReceived.countDown();

                // Shutdown server after a brief delay to ensure response is sent
                new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                        server.stop(0);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start();
            }
        }

        private String createSuccessPage() {
            return """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Ballerina LogicApps Migration Tool Authentication</title>
                    <style>
                        body { 
                            font-family: Arial, sans-serif; 
                            text-align: center; 
                            margin: 0; 
                            padding: 50px 20px;
                            background: white;
                            color: #333;
                            min-height: 80vh;
                            display: flex;
                            flex-direction: column;
                            justify-content: center;
                            align-items: center;
                        }
                        .container {
                            max-width: 600px;
                            padding: 40px;
                        }
                        .title {
                            font-size: 28px;
                            margin-bottom: 40px;
                            font-weight: 400;
                            color: #6c7ae0;
                        }
                        .success-icon {
                            font-size: 100px;
                            color: #4CAF50;
                            margin-bottom: 30px;
                        }
                        .success-title {
                            font-size: 72px;
                            font-weight: bold;
                            margin-bottom: 30px;
                            color: #333;
                        }
                        .message {
                            font-size: 20px;
                            margin-bottom: 40px;
                            color: #666;
                        }
                        .instructions {
                            font-size: 16px;
                            color: #888;
                            font-style: italic;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="title">Ballerina LogicApps Migration Tool Authentication</div>
                        <div class="success-title">Success!</div>
                        <div class="message">Authorization was successful.</div>
                        <div class="instructions">You can now close this window and return to your terminal.</div>
                    </div>
                </body>
                </html>
                """;
        }

        private String createErrorPage(String errorMessage) {
            return String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Ballerina LogicApps Migration Tool Authentication</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            text-align: center; 
                            margin: 0; 
                            padding: 50px 20px;
                            background: white;
                            color: #333;
                            min-height: 80vh;
                            display: flex;
                            flex-direction: column;
                            justify-content: center;
                            align-items: center;
                        }
                        .container {
                            max-width: 600px;
                            padding: 40px;
                        }
                        .title {
                            font-size: 28px;
                            margin-bottom: 40px;
                            font-weight: 400;
                            color: #6c7ae0;
                        }
                        .error-icon {
                            font-size: 100px;
                            color: #f44336;
                            margin-bottom: 30px;
                        }
                        .error-title {
                            font-size: 72px;
                            font-weight: bold;
                            margin-bottom: 30px;
                            color: #333;
                        }
                        .message {
                            font-size: 20px;
                            margin-bottom: 20px;
                            color: #666;
                        }
                        .error-details {
                            font-size: 16px;
                            margin-bottom: 40px;
                            color: #f44336;
                            background: #ffebee;
                            padding: 15px;
                            border-radius: 8px;
                            border-left: 4px solid #f44336;
                            word-break: break-word;
                        }
                        .instructions {
                            font-size: 16px;
                            color: #888;
                            font-style: italic;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="title">Ballerina LogicApps Migration Tool Authentication</div>
                        <div class="error-icon">✗</div>
                        <div class="error-title">Authentication Failed</div>
                        <div class="message">An error occurred during authorization.</div>
                        <div class="error-details">Error: %s</div>
                        <div class="instructions">Please try again or check your configuration.</div>
                    </div>
                </body>
                </html>
                """, errorMessage);
        }
    }
}
