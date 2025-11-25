package baltool.mirth.auth;
/*
 *  Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com).
 *
 *  WSO2 LLC. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

import baltool.mirth.codegenerator.VerboseLogger;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.awt.Desktop;
import java.io.IOException;
import java.io.OutputStream;
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

import static baltool.mirth.Constants.AUTHENTICATION_TIMEOUT_SECONDS;
import static baltool.mirth.Constants.AUTH_CLIENT_ID;
import static baltool.mirth.Constants.AUTH_ORG;
import static baltool.mirth.Constants.AUTH_REDIRECT_URL;
import static baltool.mirth.Constants.BALLERINA_USER_HOME_NAME;
import static baltool.mirth.Constants.CONFIG_FILE_PATH;
import static baltool.mirth.Constants.DEV_AUTH_CLIENT_ID;
import static baltool.mirth.Constants.DEV_AUTH_ORG;
import static baltool.mirth.Constants.DEV_AUTH_REDIRECT_URL;

/**
 * Handles CLI authentication for the Ballerina MirthConnect Channel migration tool.
 * This class manages the OAuth flow to obtain access and refresh tokens.
 */
public class CLIAuthenticator {
    public static final boolean BALLERINA_DEV_UPDATE = Boolean.parseBoolean(
            System.getenv("BALLERINA_DEV_UPDATE"));

    private HttpServer server;
    private CountDownLatch callbackReceived;
    private String authorizationCode;
    private String receivedState;
    private String error;

    /**
     * Retrieves a valid access token, either from the config file or by performing authentication.
     *
     * @param logger Logger to log messages during the process
     * @return Valid access token if available, otherwise performs authentication
     * @throws Exception If authentication fails or network issues occur
     */
    public static String getValidAccessToken(VerboseLogger logger) throws Exception {
        String existingToken = loadTokenFromConfig("accessToken", logger);
        if (existingToken != null) {
            if (isTokenValid(existingToken, logger)) {
                return existingToken;
            } else {
                String refreshToken = loadTokenFromConfig("refreshToken", logger);
                if (refreshToken != null) {
                    String newAccessToken = refreshAccessToken(refreshToken, logger);
                    if (newAccessToken != null) {
                        return newAccessToken;
                    }
                }
            }
        }

        logger.printInfo("Performing new authentication");
        return performFullAuthentication(logger);
    }

    /**
     * Performs full authentication by opening a browser for user login and exchanging the authorization code for
     * tokens.
     *
     * @param logger Logger to log messages during the process
     * @return Access token if successful, null otherwise
     * @throws Exception If network issues occur or response parsing fails
     */
    private static String performFullAuthentication(VerboseLogger logger) throws Exception {
        CLIAuthenticator authInstance = new CLIAuthenticator();
        String authCode = authInstance.authenticate(logger);
        if (authCode != null) {
            return exchangeCodeForTokens(authCode, logger);
        }
        return null;
    }

    /**
     * Refreshes the access token using the provided refresh token.
     *
     * @param refreshToken The refresh token to use for obtaining a new access token
     * @param logger       Logger to log messages during the process
     * @return New access token if successful, null otherwise
     */
    private static String refreshAccessToken(String refreshToken, VerboseLogger logger) {
        try {
            String tokenUrl = String.format("https://api.asgardeo.io/t/%s/oauth2/token", getAuthOrg());

            String requestBody = String.format(
                    "grant_type=refresh_token&refresh_token=%s&client_id=%s",
                    URLEncoder.encode(refreshToken, StandardCharsets.UTF_8),
                    URLEncoder.encode(getAuthClientID(), StandardCharsets.UTF_8)
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

                String newAccessToken = extractTokenFromResponse(responseBody, "access_token", logger);
                String newRefreshToken = extractTokenFromResponse(responseBody, "refresh_token", logger);

                if (newAccessToken != null) {
                    saveTokenToConfig("accessToken", newAccessToken, logger);
                    // Update refresh token if a new one is provided
                    if (newRefreshToken != null) {
                        saveTokenToConfig("refreshToken", newRefreshToken, logger);
                    }
                    return newAccessToken;
                } else {
                    return null;
                }
            } else {
                logger.printError("Token refresh failed with status: " + response.statusCode());
                if (response.body() != null) {
                    logger.printError("Error response: " + response.body());
                }
                return null;
            }
        } catch (IOException e) {
            logger.printError("Network error while refreshing token: " + e.getMessage());
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.printError("Token refresh interrupted");
            return null;
        } catch (Exception e) {
            logger.printError("Error refreshing token: " + e.getMessage());
            return null;
        }
    }

    /**
     * Exchanges the authorization code for access and refresh tokens.
     *
     * @param authCode The authorization code received from the OAuth server
     * @param logger   Logger to log messages during the process
     * @return Access token if successful, null otherwise
     * @throws Exception If network issues occur or response parsing fails
     */
    private static String exchangeCodeForTokens(String authCode, VerboseLogger logger) throws Exception {
        String tokenUrl = String.format("https://api.asgardeo.io/t/%s/oauth2/token", getAuthOrg());

        String requestBody = String.format(
                "grant_type=authorization_code&code=%s&redirect_uri=%s&client_id=%s",
                URLEncoder.encode(authCode, StandardCharsets.UTF_8),
                URLEncoder.encode(getAuthRedirectURL(), StandardCharsets.UTF_8),
                URLEncoder.encode(getAuthClientID(), StandardCharsets.UTF_8)
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
            logger.printInfo("Authentication successful");

            // Parse the response to extract tokens
            String accessToken = extractTokenFromResponse(responseBody, "access_token", logger);
            String refreshToken = extractTokenFromResponse(responseBody, "refresh_token", logger);

            if (accessToken != null) {
                saveTokenToConfig("accessToken", accessToken, logger);
                if (refreshToken != null) {
                    saveTokenToConfig("refreshToken", refreshToken, logger);
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
    private static String extractTokenFromResponse(String responseBody, String tokenType, VerboseLogger logger) {
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
            logger.printError("Error extracting " + tokenType + " from response: " + e.getMessage());
        }
        return null;
    }

    /**
     * Validates the access token by checking its status via the introspection endpoint.
     *
     * @param accessToken The access token to validate
     * @param logger      Logger to log messages during the process
     * @return true if the token is valid, false otherwise
     */
    private static boolean isTokenValid(String accessToken, VerboseLogger logger) {
        try {
            // Use the introspection endpoint to validate the token
            String introspectUrl = String.format("https://api.asgardeo.io/t/%s/oauth2/introspect", getAuthOrg());

            String requestBody = String.format(
                    "redirect_uri=%s&token=%s&client_id=%s",
                    URLEncoder.encode(getAuthRedirectURL(), StandardCharsets.UTF_8),
                    URLEncoder.encode(accessToken, StandardCharsets.UTF_8),
                    URLEncoder.encode(getAuthClientID(), StandardCharsets.UTF_8)
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
            logger.printError("Error validating access token: " + e.getMessage());
            return false;
        }
    }

    /**
     * Gets the path to the configuration file where tokens are stored.
     *
     * @return Path to the config file
     */
    private static Path getConfigFilePath() {
        String userHome = System.getProperty("user.home");
        return Paths.get(userHome, BALLERINA_USER_HOME_NAME, CONFIG_FILE_PATH);
    }

    /**
     * Loads a specific token from the config file.
     *
     * @param tokenKey The key of the token to load (e.g., "accessToken", "refreshToken")
     * @param logger   Logger to log messages during the process
     * @return The token value if found, null otherwise
     */
    private static String loadTokenFromConfig(String tokenKey, VerboseLogger logger) {
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
            logger.printError("Error reading config file: " + e.getMessage());
        }
        return null;
    }

    /**
     * Saves a token to the configuration file, updating it if it already exists.
     *
     * @param tokenKey   The key of the token to save (e.g., "accessToken", "refreshToken")
     * @param tokenValue The value of the token to save
     * @param logger     Logger to log messages during the process
     */
    private static void saveTokenToConfig(String tokenKey, String tokenValue, VerboseLogger logger) {
        try {
            Path filePath = getConfigFilePath();
            Files.createDirectories(filePath.getParent());
            String content = "";

            if (Files.exists(filePath)) {
                content = Files.readString(filePath, StandardCharsets.UTF_8);
            }

            String tokenLine = tokenKey + "=\"" + tokenValue + "\"";
            String searchPattern = tokenKey + "=\"";

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
            logger.printError("Error saving " + tokenKey + " to config file: " + e.getMessage());
        }
    }

    /**
     * Initiates the OAuth authentication process by opening a browser and waiting for the callback.
     *
     * @param logger Logger to log messages during the process
     * @return The authorization code received from the OAuth server
     * @throws Exception If an error occurs during authentication
     */
    private String authenticate(VerboseLogger logger) throws Exception {
        int port = findAvailablePort();
        String callbackUri = "http://localhost:" + port + "/callback";
        String state = generateState(callbackUri);
        startServer(port);
        try {
            openBrowser(state, logger);
            boolean received = callbackReceived.await(AUTHENTICATION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!received) {
                throw new RuntimeException("Authentication timed out");
            }
            if (receivedState == null || !receivedState.equals(state)) {
                throw new RuntimeException("Authentication failed: state mismatch");
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
     * Finds an available port on the local machine for the HTTP server.
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
     * @param state  The state parameter to include in the URL
     * @param logger Logger to log messages during the process
     * @throws Exception If an error occurs while opening the browser
     */
    private void openBrowser(String state, VerboseLogger logger) throws Exception {
        String authUrl = String.format(
                "https://api.asgardeo.io/t/%s/oauth2/authorize?response_type=code&redirect_uri=%s&client_id=%s&" +
                        "scope=openid%%20email&state=%s",
                getAuthOrg(), getAuthRedirectURL(), getAuthClientID(), state
        );

        logger.printInfo("Opening browser for authentication...");
        logger.printInfo("If browser doesn't open automatically, visit: " + authUrl);

        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI(authUrl));
        } else {
            logger.printInfo("Please open the above URL in your browser manually.");
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
     * Retrieves the OAuth client ID, organization, and redirect URL based on the environment.
     *
     * @return The respective values for client ID, organization, and redirect URL
     */
    private static String getAuthClientID() {
        return BALLERINA_DEV_UPDATE ? DEV_AUTH_CLIENT_ID : AUTH_CLIENT_ID;
    }

    /**
     * Retrieves the organization name for the OAuth flow based on the environment.
     *
     * @return The organization name for the OAuth flow
     */
    private static String getAuthOrg() {
        return BALLERINA_DEV_UPDATE ? DEV_AUTH_ORG : AUTH_ORG;
    }

    /**
     * Retrieves the redirect URL for the OAuth flow based on the environment.
     *
     * @return The redirect URL for the OAuth flow
     */
    private static String getAuthRedirectURL() {
        return BALLERINA_DEV_UPDATE ? DEV_AUTH_REDIRECT_URL : AUTH_REDIRECT_URL;
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
                        <title>Ballerina MirthConnect Channel Migration Tool Authentication</title>
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
                            <div class="title">Ballerina MirthConnect Channel Migration Tool Authentication</div>
                            <div class="success-title">Success</div>
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
                        <title>Ballerina MirthConnect Channel Migration Tool Authentication</title>
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
                            <div class="title">Ballerina MirthConnect Channel Migration Tool Authentication</div>
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
