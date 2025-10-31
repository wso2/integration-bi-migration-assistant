package baltool.mirth.codegenerator;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.*;
import java.util.stream.Stream;

/**
 * Utility class for sending HTTP requests and handling responses.
 *
 */
public class HttpUtils {

    static HttpResponse<Stream<String>> sendStreamRequestAsync(URI uri, JsonObject payload, String accessToken,
                                                               VerboseLoggerFactory logger, String fileName)
            throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Authorization", "Bearer " + accessToken)
                .header("User-Agent", "PostmanRuntime/7.32.3")
                .header("Accept", "*/*")  // Add explicit Accept header
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                .timeout(Duration.ofMinutes(8))
                .build();

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        CompletableFuture<HttpResponse<Stream<String>>> future = getHttpClient().sendAsync(
                request, HttpResponse.BodyHandlers.ofLines());

        HttpResponse<Stream<String>> response;
        try {
            long startTime = System.currentTimeMillis();
            response = future.get(5, TimeUnit.MINUTES);
            long duration = System.currentTimeMillis() - startTime;

            logger.printVerboseInfo(fileName, "Response time: " + duration + "ms");
            logger.printVerboseInfo(fileName, "Response status: " + response.statusCode());
        } catch (TimeoutException e) {
            future.cancel(true);
            logger.printVerboseError(fileName, "Request timed out after 5 minutes");
            throw new IOException("Request timed out", e);
        } catch (ExecutionException e) {
            logger.printVerboseError(fileName, "Request failed: " + e.getCause().getMessage());
            throw new IOException("Request failed", e.getCause());
        } finally {
            scheduler.shutdown();
        }

        return response;
    }

    static HttpResponse<String> sendRequestAsync(URI uri, JsonObject payload, String accessToken,
                                                 VerboseLoggerFactory logger, String fileName)
            throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .header("User-Agent", "PostmanRuntime/7.32.3")
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                .timeout(Duration.ofMinutes(5))
                .build();

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        CompletableFuture<HttpResponse<String>> future = getHttpClient().sendAsync(
                request, HttpResponse.BodyHandlers.ofString());

        HttpResponse<String> response;
        try {
            long startTime = System.currentTimeMillis();
            response = future.get(5, TimeUnit.MINUTES);
            long duration = System.currentTimeMillis() - startTime;

            logger.printVerboseInfo(fileName, "Response time: " + duration + "ms");
            logger.printVerboseInfo(fileName, "Response status: " + response.statusCode());
        } catch (TimeoutException e) {
            future.cancel(true);
            logger.printVerboseError(fileName, "Request timed out after 5 minutes");
            throw new IOException("Request timed out", e);
        } catch (ExecutionException e) {
            logger.printVerboseError(fileName, "Request failed: " + e.getCause().getMessage());
            throw new IOException("Request failed", e.getCause());
        } finally {
            scheduler.shutdown();
        }

        return response;
    }

    static HttpClient getHttpClient() {
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofMinutes(5))
                .executor(Executors.newCachedThreadPool())
                .build();
    }
}
