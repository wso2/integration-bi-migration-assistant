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

package common;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.jetbrains.annotations.NotNull;

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

/**
 * Utility class for handling OAuth authentication flows. This class manages token lifecycle, validation, and refresh
 * operations.
 */
public class AuthenticateUtils {

    // Common constants
    private static final String AUTH_HOST = "https://98c70105-822c-4359-8579-4da58f0ab4b7.";
    private static final String BALLERINA_USER_HOME_NAME = ".ballerina";
    private static final String DEFAULT_CONFIG_FILE_PATH = "migrate-tool.toml";
    private static final int AUTHENTICATION_TIMEOUT_SECONDS = 180;

    // Production environment constants
    private static final String PROD_AUTH_ORG = "ballerinacopilot";
    private static final String PROD_AUTH_CLIENT_ID = "9rKng8hSZd0VkeA45Lt4LOfCp9Aa";
    private static final String PROD_AUTH_REDIRECT_URL = AUTH_HOST + "e1-us-east-azure.choreoapps.dev";

    // Development environment constants
    private static final String DEV_AUTH_ORG = "ballerinacopilotdev";
    private static final String DEV_AUTH_CLIENT_ID = "XpQ6lphi7kjKkWzumYyqqNf7CjIa";
    private static final String DEV_AUTH_REDIRECT_URL = AUTH_HOST + "e1-us-east-azure.choreoapps.dev";

    /**
     * Configuration class for authentication parameters.
     *
     * @param isDevelopment whether to use development or production environment
     * @param toolName the name of the tool for display in HTML templates
     */
    public record Config(boolean isDevelopment, String toolName) {

        @NotNull
        public String authOrg() {
            return isDevelopment ? DEV_AUTH_ORG : PROD_AUTH_ORG;
        }

        @NotNull
        public String authClientId() {
            return isDevelopment ? DEV_AUTH_CLIENT_ID : PROD_AUTH_CLIENT_ID;
        }

        @NotNull
        public String authRedirectUrl() {
            return isDevelopment ? DEV_AUTH_REDIRECT_URL : PROD_AUTH_REDIRECT_URL;
        }

        @NotNull
        public String ballerinaUserHomeName() {
            return BALLERINA_USER_HOME_NAME;
        }

        public int authenticationTimeoutSeconds() {
            return AUTHENTICATION_TIMEOUT_SECONDS;
        }

        @NotNull
        public String configFilePath() {
            return DEFAULT_CONFIG_FILE_PATH;
        }
    }

    /**
     * Internal class for managing OAuth server state.
     */
    private static class AuthenticationState {

        private HttpServer server;
        private CountDownLatch callbackReceived;
        private String authorizationCode;
        private String receivedState;
        private String error;
    }

    /**
     * Retrieves a valid access token, either from the config file or by performing authentication.
     *
     * @param config Authentication configuration
     * @param logger Logger to log messages during the process
     * @return Valid access token if available, otherwise performs authentication
     * @throws Exception If authentication fails or network issues occur
     */
    public static String getValidAccessToken(Config config, LoggingContext logger) throws Exception {
        String existingToken = loadTokenFromConfig("accessToken", config, logger);
        if (existingToken != null) {
            logger.log(LoggingUtils.Level.DEBUG, "Found existing access token in config file");
            if (isTokenValid(existingToken, config, logger)) {
                logger.log(LoggingUtils.Level.DEBUG, "Token is valid");
                return existingToken;
            }
            logger.log(LoggingUtils.Level.DEBUG, "Token is invalid, trying to refresh");
            String refreshToken = loadTokenFromConfig("refreshToken", config, logger);
            if (refreshToken != null) {
                logger.log(LoggingUtils.Level.DEBUG, "Found existing refresh token in config file");
                String newAccessToken = refreshAccessToken(refreshToken, config, logger);
                if (newAccessToken != null) {
                    logger.log(LoggingUtils.Level.DEBUG, "Successfully refreshed access token");
                    return newAccessToken;
                }
            }
        }

        logger.log(LoggingUtils.Level.DEBUG, "No valid access token found, performing full authentication");
        return performFullAuthentication(config, logger);
    }

    /**
     * Performs full authentication by opening a browser for user login and exchanging the authorization code for
     * tokens.
     *
     * @param config Authentication configuration
     * @param logger Logger to log messages during the process
     * @return Access token if successful, null otherwise
     * @throws Exception If network issues occur or response parsing fails
     */
    private static String performFullAuthentication(Config config, LoggingContext logger) throws Exception {
        AuthenticationState state = new AuthenticationState();
        String authCode = authenticate(state, config, logger);
        logger.log(LoggingUtils.Level.DEBUG, "Authentication successful");
        if (authCode != null) {
            logger.log(LoggingUtils.Level.DEBUG, "Exchanging authorization code for tokens");
            return exchangeCodeForTokens(authCode, config, logger);
        }
        return null;
    }

    /**
     * Refreshes the access token using the provided refresh token.
     *
     * @param refreshToken The refresh token to use for obtaining a new access token
     * @param config       Authentication configuration
     * @param logger       Logger to log messages during the process
     * @return New access token if successful, null otherwise
     */
    private static String refreshAccessToken(String refreshToken, Config config, LoggingContext logger) {
        try {
            String requestBody = String.format("grant_type=refresh_token&refresh_token=%s&client_id=%s",
                    URLEncoder.encode(refreshToken, StandardCharsets.UTF_8),
                    URLEncoder.encode(config.authClientId(), StandardCharsets.UTF_8));
            HttpResponse<String> response = getAuthTokenInner(requestBody, config);

            if (response.statusCode() == 200) {
                logger.log(LoggingUtils.Level.INFO, "Token refresh successful");
                String responseBody = response.body();

                String newAccessToken = extractTokenFromResponse(responseBody, "access_token", logger);
                String newRefreshToken = extractTokenFromResponse(responseBody, "refresh_token", logger);

                if (newAccessToken != null) {
                    logger.log(LoggingUtils.Level.INFO, "Saving tokens to config file");
                    saveTokenToConfig("accessToken", newAccessToken, config, logger);
                    if (newRefreshToken != null) {
                        saveTokenToConfig("refreshToken", newRefreshToken, config, logger);
                    }
                    return newAccessToken;
                } else {
                    return null;
                }
            } else {
                logger.log(LoggingUtils.Level.ERROR, "Token refresh failed with status: " + response.statusCode());
                if (response.body() != null) {
                    logger.log(LoggingUtils.Level.ERROR, "Error response: " + response.body());
                }
                return null;
            }
        } catch (IOException e) {
            logger.log(LoggingUtils.Level.ERROR, "Network error while refreshing token: " + e.getMessage());
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.log(LoggingUtils.Level.ERROR, "Token refresh interrupted");
            return null;
        } catch (Exception e) {
            logger.log(LoggingUtils.Level.ERROR, "Error refreshing token: " + e.getMessage());
            return null;
        }
    }

    private static HttpResponse<String> getAuthTokenInner(String requestBody, Config config)
            throws IOException, InterruptedException {
        String tokenUrl = String.format("https://api.asgardeo.io/t/%s/oauth2/token", config.authOrg());
        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(tokenUrl))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody)).timeout(Duration.ofSeconds(30)).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Exchanges the authorization code for access and refresh tokens.
     *
     * @param authCode The authorization code received from the OAuth server
     * @param config   Authentication configuration
     * @param logger   Logger to log messages during the process
     * @return Access token if successful, null otherwise
     * @throws Exception If network issues occur or response parsing fails
     */
    private static String exchangeCodeForTokens(String authCode, Config config, LoggingContext logger)
            throws Exception {
        String requestBody = String.format("grant_type=authorization_code&code=%s&redirect_uri=%s&client_id=%s",
                URLEncoder.encode(authCode, StandardCharsets.UTF_8),
                URLEncoder.encode(config.authRedirectUrl(), StandardCharsets.UTF_8),
                URLEncoder.encode(config.authClientId(), StandardCharsets.UTF_8));

        HttpResponse<String> response = getAuthTokenInner(requestBody, config);

        if (response.statusCode() == 200) {
            String responseBody = response.body();
            logger.logState("Authentication successful");

            String accessToken = extractTokenFromResponse(responseBody, "access_token", logger);
            String refreshToken = extractTokenFromResponse(responseBody, "refresh_token", logger);

            if (accessToken != null) {
                saveTokenToConfig("accessToken", accessToken, config, logger);
                if (refreshToken != null) {
                    saveTokenToConfig("refreshToken", refreshToken, config, logger);
                }
                return accessToken;
            } else {
                logger.log(LoggingUtils.Level.ERROR, "No access token found in response");
                throw new RuntimeException("No access token found in response");
            }
        } else {
            String errorMsg = "Token exchange failed with status: " + response.statusCode();
            if (response.body() != null) {
                errorMsg += "\nError response: " + response.body();
            }
            logger.log(LoggingUtils.Level.ERROR, errorMsg);
            throw new RuntimeException(errorMsg);
        }
    }

    /**
     * Extracts a specific token type from the response body.
     *
     * @param responseBody The JSON response body as a String
     * @param tokenType    The type of token to extract (e.g., "access_token", "refresh_token")
     * @param logger       Logger to log messages during the process
     * @return The extracted token or null if not found
     */
    private static String extractTokenFromResponse(String responseBody, String tokenType, LoggingContext logger) {
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
            logger.log(LoggingUtils.Level.ERROR, "Error extracting " + tokenType + " from response: " + e.getMessage());
        }
        return null;
    }

    /**
     * Validates the access token by checking its status via the introspection endpoint.
     *
     * @param accessToken The access token to validate
     * @param config      Authentication configuration
     * @param logger      Logger to log messages during the process
     * @return true if the token is valid, false otherwise
     */
    private static boolean isTokenValid(String accessToken, Config config, LoggingContext logger) {
        String introspectUrl = String.format("https://api.asgardeo.io/t/%s/oauth2/introspect", config.authOrg());

        String requestBody = String.format("redirect_uri=%s&token=%s&client_id=%s",
                URLEncoder.encode(config.authRedirectUrl(), StandardCharsets.UTF_8),
                URLEncoder.encode(accessToken, StandardCharsets.UTF_8),
                URLEncoder.encode(config.authClientId(), StandardCharsets.UTF_8));
        try (HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build()) {

            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(introspectUrl))
                    .header("Content-Type", "application/x-www-form-urlencoded").header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody)).timeout(Duration.ofSeconds(30)).build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String responseBody = response.body();
                return responseBody != null && responseBody.contains("\"active\":true");
            } else {
                return false;
            }
        } catch (Exception e) {
            logger.log(LoggingUtils.Level.ERROR, "Error validating access token: " + e.getMessage());
            return false;
        }
    }

    /**
     * Gets the path to the configuration file where tokens are stored.
     *
     * @param config Authentication configuration
     * @return Path to the config file
     */
    private static Path getConfigFilePath(Config config) {
        String userHome = System.getProperty("user.home");
        return Paths.get(userHome, config.ballerinaUserHomeName(), config.configFilePath());
    }

    /**
     * Loads a specific token from the config file.
     *
     * @param tokenKey The key of the token to load (e.g., "accessToken", "refreshToken")
     * @param config   Authentication configuration
     * @param logger   Logger to log messages during the process
     * @return The token value if found, null otherwise
     */
    private static String loadTokenFromConfig(String tokenKey, Config config, LoggingContext logger) {
        try {
            Path filePath = getConfigFilePath(config);
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
            logger.log(LoggingUtils.Level.ERROR, "Error reading config file: " + e.getMessage());
        }
        return null;
    }

    /**
     * Saves a token to the configuration file, updating it if it already exists.
     *
     * @param tokenKey   The key of the token to save (e.g., "accessToken", "refreshToken")
     * @param tokenValue The value of the token to save
     * @param config     Authentication configuration
     * @param logger     Logger to log messages during the process
     */
    private static void saveTokenToConfig(String tokenKey, String tokenValue, Config config,
                                          LoggingContext logger) {
        try {
            logger.log(LoggingUtils.Level.INFO, "Saving token key to config file");
            Path filePath = getConfigFilePath(config);
            String content = "";

            if (Files.exists(filePath)) {
                content = Files.readString(filePath, StandardCharsets.UTF_8);
            }

            String tokenLine = tokenKey + "=\"" + tokenValue + "\"";
            String searchPattern = tokenKey + "=\"";

            int startIndex = content.indexOf(searchPattern);
            if (startIndex != -1) {
                int lineStart = content.lastIndexOf('\n', startIndex) + 1;
                int lineEnd = content.indexOf('\n', startIndex);
                if (lineEnd == -1) {
                    lineEnd = content.length();
                }

                content = content.substring(0, lineStart) + tokenLine +
                        (lineEnd < content.length() ? content.substring(lineEnd) : "");
            } else {
                if (!content.isEmpty() && !content.endsWith("\n")) {
                    content += "\n";
                }
                content += tokenLine + "\n";
            }

            Files.writeString(filePath, content, StandardCharsets.UTF_8);
            logger.log(LoggingUtils.Level.INFO, "Saved token key to config file");
        } catch (IOException e) {
            logger.log(LoggingUtils.Level.ERROR, "Error saving " + tokenKey + " to config file: " + e.getMessage());
        }
    }

    /**
     * Initiates the OAuth authentication process by opening a browser and waiting for the callback.
     *
     * @param state  Authentication state holder
     * @param config Authentication configuration
     * @param logger Logger to log messages during the process
     * @return The authorization code received from the OAuth server
     * @throws Exception If an error occurs during authentication
     */
    private static String authenticate(AuthenticationState state, Config config, LoggingContext logger)
            throws Exception {
        int port = findAvailablePort();
        String callbackUri = "http://localhost:" + port + "/callback";
        String stateParam = generateState(callbackUri);
        startServer(state, port, config);
        try {
            openBrowser(stateParam, config, logger);
            boolean received = state.callbackReceived.await(config.authenticationTimeoutSeconds(), TimeUnit.SECONDS);
            if (!received) {
                logger.log(LoggingUtils.Level.ERROR, "Authentication timed out after " +
                        config.authenticationTimeoutSeconds() + " seconds");
                throw new RuntimeException("Authentication timed out");
            }
            if (state.error != null) {
                logger.log(LoggingUtils.Level.ERROR, "Authentication failed: " + state.error);
                throw new RuntimeException("Authentication failed: " + state.error);
            }
            return state.authorizationCode;
        } finally {
            if (state.server != null) {
                state.server.stop(0);
            }
        }
    }

    /**
     * Finds an available port on the local machine for the HTTP server.
     *
     * @return An available port number
     * @throws IOException If an error occurs while creating the server socket
     */
    private static int findAvailablePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    /**
     * Starts an HTTP server to handle the OAuth callback.
     *
     * @param state  Authentication state holder
     * @param port   The port on which the server will listen
     * @param config Authentication configuration
     * @throws IOException If an error occurs while starting the server
     */
    private static void startServer(AuthenticationState state, int port, Config config) throws IOException {
        state.callbackReceived = new CountDownLatch(1);
        state.server = HttpServer.create(new InetSocketAddress(port), 0);
        state.server.createContext("/callback", new CallbackHandler(state, config.toolName()));
        state.server.setExecutor(null);
        state.server.start();
    }

    /**
     * Opens the default web browser to the authentication URL.
     *
     * @param state  The state parameter to include in the URL
     * @param config Authentication configuration
     * @param logger Logger to log messages during the process
     * @throws Exception If an error occurs while opening the browser
     */
    private static void openBrowser(String state, Config config, LoggingContext logger) throws Exception {
        String authUrl = String.format(
                "https://api.asgardeo.io/t/%s/oauth2/authorize?response_type=code&redirect_uri=%s&client_id=%s&" +
                        "scope=openid%%20email&state=%s", config.authOrg(), config.authRedirectUrl(),
                config.authClientId(), state);

        logger.logState("Opening browser for authentication...");
        logger.log(LoggingUtils.Level.INFO, "If browser doesn't open automatically, visit: " + authUrl);

        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI(authUrl));
        } else {
            logger.log(LoggingUtils.Level.WARN, "Failed to open browser automatically. " +
                    "Please visit the following URL manually: " + authUrl);
        }
    }

    /**
     * Generates a state parameter for the OAuth flow, which includes the callback URI.
     *
     * @param callbackUri The URI to redirect to after authentication
     * @return Base64 encoded state string
     */
    private static String generateState(String callbackUri) {
        Gson gson = new Gson();
        Map<String, String> data = Map.of("callbackUri", callbackUri);
        String json = gson.toJson(data);
        String base64Encoded = Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));

        return URLEncoder.encode(base64Encoded, StandardCharsets.UTF_8);
    }

    /**
     * Callback handler for the HTTP server that processes the OAuth response.
     *
     * @param state the authentication state to update with the OAuth response
     * @param toolName the name of the tool for display in HTML templates
     */
    private record CallbackHandler(AuthenticationState state, String toolName) implements HttpHandler {

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
                                        state.authorizationCode = value;
                                        break;
                                    case "state":
                                        state.receivedState = value;
                                        break;
                                    case "error":
                                        state.error = value;
                                        break;
                                    case "error_description":
                                        if (state.error != null) {
                                            state.error += ": " + value;
                                        }
                                        break;
                                }
                            }
                        }
                    }

                    String response;
                    if (state.authorizationCode != null) {
                        response = createSuccessPage(toolName);
                    } else {
                        response = createErrorPage(toolName, state.error != null ? state.error : "Unknown error");
                    }

                    exchange.getResponseHeaders().set("Content-Type", "text/html");
                    exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);

                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes(StandardCharsets.UTF_8));
                    }

                } finally {
                    state.callbackReceived.countDown();

                    new Thread(() -> {
                        try {
                            Thread.sleep(1000);
                            state.server.stop(0);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }).start();
                }
            }

            private String createSuccessPage(String toolName) {
                return String.format("""
                        <!DOCTYPE html>
                        <html>
                        <head>
                            <title>Ballerina %s Authentication</title>
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
                                <div class="title">Ballerina %s Authentication</div>
                                <div class="success-title">Success</div>
                                <div class="message">Authorization was successful.</div>
                                <div class="instructions">
                                    You can now close this window and return to your terminal.
                                </div>
                            </div>
                        </body>
                        </html>
                        """, toolName, toolName);
            }

            private String createErrorPage(String toolName, String errorMessage) {
                return String.format("""
                        <!DOCTYPE html>
                        <html>
                        <head>
                            <title>Ballerina %s Authentication</title>
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
                                <div class="title">Ballerina %s Authentication</div>
                                <div class="error-icon">✗</div>
                                <div class="error-title">Authentication Failed</div>
                                <div class="message">An error occurred during authorization.</div>
                                <div class="error-details">Error: %s</div>
                                <div class="instructions">Please try again or check your configuration.</div>
                            </div>
                        </body>
                        </html>
                        """, toolName, toolName, errorMessage);
            }
        }
}
