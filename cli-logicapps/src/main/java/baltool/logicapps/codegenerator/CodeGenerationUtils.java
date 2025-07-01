package baltool.logicapps.codegenerator;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.ballerina.projects.BuildOptions;
import io.ballerina.projects.DiagnosticResult;
import io.ballerina.projects.ModuleDescriptor;
import io.ballerina.projects.ModuleName;
import io.ballerina.projects.PackageCompilation;
import io.ballerina.projects.PackageDescriptor;
import io.ballerina.projects.PackageName;
import io.ballerina.projects.PackageOrg;
import io.ballerina.projects.PackageVersion;
import io.ballerina.projects.ProjectEnvironmentBuilder;
import io.ballerina.projects.directory.BuildProject;
import io.ballerina.projects.environment.Environment;
import io.ballerina.projects.environment.EnvironmentBuilder;
import io.ballerina.projects.util.ProjectUtils;
import io.ballerina.tools.diagnostics.Diagnostic;
import io.ballerina.tools.diagnostics.DiagnosticInfo;
import io.ballerina.tools.diagnostics.DiagnosticSeverity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static baltool.logicapps.Constants.BALLERINA_TOML_FILE;
import static baltool.logicapps.Constants.CONTENT;
import static baltool.logicapps.Constants.COPILOT_BACKEND_URL;
import static baltool.logicapps.Constants.DEFAULT_ORG_NAME;
import static baltool.logicapps.Constants.DEFAULT_PROJECT_VERSION;
import static baltool.logicapps.Constants.FILE_PATH;
import static baltool.logicapps.Constants.TRIPLE_BACKTICK;
import static baltool.logicapps.Constants.TRIPLE_BACKTICK_BALLERINA;

/**
 * Methods to generate code at compile-time.
 *
 * @since 0.4.0
 */
public class CodeGenerationUtils {
    private static final PrintStream errStream = System.err;
    private static final String TEMP_DIR_PREFIX = "logicapps-migration-tool-codegen-diagnostics-dir-";

    public static JsonArray generateCodeForLogicApp(String copilotAccessToken, Path logicAppFilePath, Path projectPath,
                                                    String packageName, String additionalInstructions,
                                                    ModuleDescriptor moduleDescriptor) {
        try {
            String logicAppContent = Files.readString(logicAppFilePath, StandardCharsets.UTF_8);
            JsonArray fileAttachmentContents = getFileAttachmentContents(logicAppFilePath.getFileName().toString(),
                    logicAppContent);
            JsonArray sourceFiles = createSourceFilesArray();
            String executionPlan = CodeGenerationUtils.generateLogicAppExecutionPlan(COPILOT_BACKEND_URL,
                    copilotAccessToken, getHttpClient(), sourceFiles, fileAttachmentContents, packageName,
                    additionalInstructions);
            String generatedPrompt = constructMigrateUserPrompt(additionalInstructions, executionPlan);

            // Generate code
            GeneratedCode generatedCode = generateCode(COPILOT_BACKEND_URL, copilotAccessToken, getHttpClient(),
                    sourceFiles, fileAttachmentContents, packageName, generatedPrompt);
            updateSourceFilesWithGeneratedContent(sourceFiles, generatedCode.codeMap);

            // Repair code
            GeneratedCode repairedCode = repairCode(COPILOT_BACKEND_URL, copilotAccessToken, getHttpClient(),
                    sourceFiles, fileAttachmentContents, packageName, generatedPrompt, moduleDescriptor,
                    generatedCode);
            updateSourceFilesWithGeneratedContent(sourceFiles, repairedCode.codeMap);

            return sourceFiles;

        } catch (URISyntaxException e) {
            throw new RuntimeException("Failed to generate code, invalid URI for Copilot");
        } catch (ConnectException e) {
            throw new RuntimeException("Failed to connect to Copilot META-INF.services");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to generate code: " + e.getMessage());
        }
    }

    public static String generateLogicAppExecutionPlan(String copilotUrl, String copilotAccessToken, HttpClient client,
                                                       JsonArray sourceFiles, JsonArray fileAttachmentContents,
                                                       String packageName, String additionalInstructions)
            throws URISyntaxException, IOException, InterruptedException {

        JsonObject executionPlanGenerationPayload = constructExecPlanGenerationPayload(additionalInstructions,
                sourceFiles, fileAttachmentContents, packageName);

        URI uri = new URI(copilotUrl + "/logicapps/executionplan");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + copilotAccessToken)
                .POST(HttpRequest.BodyPublishers.ofString(executionPlanGenerationPayload.toString()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            // Pattern to match "text":"content" where content can span multiple lines
            Pattern pattern = Pattern.compile("\"text\"\\s*:\\s*\"(.*?)\"\\s*,\\s*\"type\"", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(response.body());

            if (matcher.find()) {
                String text = matcher.group(1);
                return text;
            }
            return "";
        } else {
            throw new IOException("Failed to generate execution plan. Status: " +
                    response.statusCode() + ", Body: " + response.body());
        }
    }

    private static GeneratedCode generateCode(String copilotUrl, String copilotAccessToken, HttpClient client,
                                              JsonArray sourceFiles, JsonArray fileAttachmentContents,
                                              String packageName, String generatedPrompt)
            throws URISyntaxException, IOException, InterruptedException {
        JsonObject codeGenerationPayload = constructCodeGenerationPayload(generatedPrompt, sourceFiles,
                fileAttachmentContents, packageName);

        HttpRequest codeGenerationRequest = HttpRequest.newBuilder()
                .uri(new URI(copilotUrl + "/code"))
                .header("Authorization", "Bearer " + copilotAccessToken)
                .POST(HttpRequest.BodyPublishers.ofString(codeGenerationPayload.toString())).build();
        Stream<String> lines = client.send(codeGenerationRequest, HttpResponse.BodyHandlers.ofLines()).body();
        return extractGeneratedCode(lines);
    }

    private static GeneratedCode repairCode(String copilotUrl, String copilotAccessToken, HttpClient client,
                                     JsonArray sourceFiles, JsonArray fileAttachmentContents, String packageName,
                                     String generatedPrompt, ModuleDescriptor moduleDescriptor,
                                     GeneratedCode generatedCodeMap)
            throws IOException, URISyntaxException, InterruptedException {
        return repairIfDiagnosticsExist(copilotUrl, copilotAccessToken, client, sourceFiles,
                fileAttachmentContents, packageName, generatedPrompt, moduleDescriptor, generatedCodeMap);
    }

    private static GeneratedCode repairIfDiagnosticsExist(String copilotUrl, String copilotAccessToken,
                                                                HttpClient client, JsonArray sourceFiles,
                                                                JsonArray fileAttachmentContents, String packageName,
                                                                String generatedPrompt,
                                                                ModuleDescriptor moduleDescriptor,
                                                                GeneratedCode generatedCode)
            throws IOException, URISyntaxException, InterruptedException {
        Optional<JsonArray> diagnostics = getDiagnostics(sourceFiles, moduleDescriptor);
        if (diagnostics.isEmpty()) {
            return generatedCode;
        }
        JsonArray convertedDiagnostics = convertDiagnosticsToMessageObjects(diagnostics);
        JsonObject diagnosticRequest = getDiagnosticsRequest(convertedDiagnostics, generatedCode);

        String repairResponse = repairCode(copilotUrl, copilotAccessToken, client, sourceFiles, fileAttachmentContents,
                packageName, generatedPrompt, generatedCode, diagnosticRequest);

        if (hasBallerinaCodeSnippet(repairResponse)) {
            return new GeneratedCode(extractGeneratedCodeFromResponse(repairResponse), generatedCode.functions);
        }
        return generatedCode;
    }

    private static String repairCode(String copilotUrl, String copilotAccessToken, HttpClient client,
                                     JsonArray sourceFiles, JsonArray fileAttachmentContents, String packageName,
                                     String generatedPrompt, GeneratedCode generatedCodeMap,
                                     JsonObject diagnosticsRequest)
            throws URISyntaxException, IOException, InterruptedException {
        JsonObject codeReparationPayload = constructCodeReparationPayload(generatedPrompt, sourceFiles,
                fileAttachmentContents, packageName, generatedCodeMap.functions, diagnosticsRequest);
        HttpRequest codeReparationRequest = HttpRequest.newBuilder()
                .uri(new URI(copilotUrl + "/code/repair"))
                .header("Authorization", "Bearer " + copilotAccessToken)
                .POST(HttpRequest.BodyPublishers.ofString(codeReparationPayload.toString())).build();
        HttpResponse<String> response = client.send(codeReparationRequest, HttpResponse.BodyHandlers.ofString());
        return JsonParser.parseString(response.body()).getAsJsonObject()
                .getAsJsonPrimitive("repairResponse").getAsString();
    }

    private static Optional<JsonArray> getDiagnostics(JsonArray sourceFiles, ModuleDescriptor moduleDescriptor)
            throws IOException {
        BuildProject project = createProject(sourceFiles, moduleDescriptor);
        PackageCompilation compilation = project.currentPackage().getCompilation();
        DiagnosticResult diagnosticResult = compilation.diagnosticResult();

        if (diagnosticResult.errorCount() == 0) {
            return Optional.empty();
        }

        JsonArray diagnostics = new JsonArray();
        for (Diagnostic diagnostic : diagnosticResult.diagnostics()) {
            DiagnosticInfo diagnosticInfo = diagnostic.diagnosticInfo();
            if (diagnosticInfo.severity() != DiagnosticSeverity.ERROR) {
                continue;
            }
            diagnostics.add(diagnostic.toString());
        }

        return Optional.of(diagnostics);
    }

    private static JsonArray convertDiagnosticsToMessageObjects(Optional<JsonArray> diagnostics) {
        JsonArray result = new JsonArray();

        if (diagnostics.isPresent()) {
            JsonArray diagnosticsArray = diagnostics.get();

            for (JsonElement element : diagnosticsArray) {
                JsonObject messageObject = new JsonObject();
                messageObject.addProperty("message", element.getAsString());
                result.add(messageObject);
            }
        }

        return result;
    }

    private static JsonObject getDiagnosticsRequest(JsonArray diagnostics, GeneratedCode generatedCode) {
        JsonObject diagnosticRequest = new JsonObject();
        diagnosticRequest.add("diagnostics", diagnostics);
        diagnosticRequest.addProperty("response", generatedCode.codeMap.toString());
        return diagnosticRequest;
    }

    private static BuildProject createProject(JsonArray sourceFiles, ModuleDescriptor moduleDescriptor)
            throws IOException {
        Path tempProjectDir = Files.createTempDirectory(TEMP_DIR_PREFIX + System.currentTimeMillis());
        tempProjectDir.toFile().deleteOnExit();

        Path tempGeneratedDir = Files.createDirectory(tempProjectDir.resolve("generated"));
        tempGeneratedDir.toFile().deleteOnExit();

        for (JsonElement sourceFile : sourceFiles) {
            JsonObject sourceFileObj = sourceFile.getAsJsonObject();
            File file = Files.createFile(
                    tempProjectDir.resolve(Path.of(sourceFileObj.get(FILE_PATH).getAsString()))).toFile();
            file.deleteOnExit();

            try (FileWriter fileWriter = new FileWriter(file, StandardCharsets.UTF_8)) {
                fileWriter.write(sourceFileObj.get(CONTENT).getAsString());
            }
        }

        Path ballerinaTomlPath = tempProjectDir.resolve(BALLERINA_TOML_FILE);
        File balTomlFile = Files.createFile(ballerinaTomlPath).toFile();
        balTomlFile.deleteOnExit();

        try (FileWriter fileWriter = new FileWriter(balTomlFile, StandardCharsets.UTF_8)) {
            fileWriter.write(String.format("""
                [package]
                org = "%s"
                name = "%s"
                version = "%s"
                """,
                    moduleDescriptor.org().value(),
                    moduleDescriptor.packageName().value(),
                    moduleDescriptor.version().value()));
        }

        Path ballerinaHomePath = Path.of(Objects.requireNonNull(getBallerinaHome()));
        Environment environment = EnvironmentBuilder.getBuilder().setBallerinaHome(ballerinaHomePath).build();
        ProjectEnvironmentBuilder projectEnvironmentBuilder = ProjectEnvironmentBuilder.getBuilder(environment);
        BuildOptions buildOptions = BuildOptions.builder().targetDir(ProjectUtils.getTemporaryTargetPath()).build();
        return BuildProject.load(projectEnvironmentBuilder, tempProjectDir, buildOptions);
    }

    private static Map<String, String> extractGeneratedCodeFromResponse(String generatedResponseBody) {
        Map<String, String> generatedCodeMap = new HashMap<>();

        // Pattern to match the code blocks with filename and content
        // Captures: filename and the code content between triple backticks
        Pattern pattern = Pattern.compile(
                "<code filename=\"([^\"]+)\">\\s*```ballerina[^\\n]*\\n(.*?)\\n```\\s*</code>",
                Pattern.DOTALL
        );

        Matcher matcher = pattern.matcher(generatedResponseBody);

        while (matcher.find()) {
            String filename = matcher.group(1);  // Extract filename
            String codeContent = matcher.group(2);  // Extract code content

            codeContent = codeContent.trim();

            generatedCodeMap.put(filename, codeContent);
        }

        return generatedCodeMap;
    }

    private static GeneratedCode extractGeneratedCode(Stream<String> lines) {
        String[] linesArr = lines.toArray(String[]::new);
        int length = linesArr.length;

        if (length == 1) {
            JsonObject jsonObject = JsonParser.parseString(linesArr[0]).getAsJsonObject();
            if (jsonObject.has("error_message")) {
                throw new RuntimeException(jsonObject.get("error_message").getAsString());
            }
        }

        StringBuilder responseBody = new StringBuilder();
        JsonArray functions = null;

        int index = 0;
        while (index < length) {
            String line = linesArr[index];

            if (line.isBlank()) {
                index++;
                continue;
            }

            if ("event: content_block_delta".equals(line)) {
                line = linesArr[++index].substring(6);
                responseBody.append(JsonParser.parseString(line).getAsJsonObject()
                        .getAsJsonPrimitive("text").getAsString());
                continue;
            }

            if ("event: functions".equals(line)) {
                line = linesArr[++index].substring(6);
                functions = JsonParser.parseString(line).getAsJsonArray();
                continue;
            }

            index++;
        }

        String responseBodyString = responseBody.toString();
        return new GeneratedCode(extractGeneratedCodeFromResponse(responseBodyString), functions);
    }

    private static boolean hasBallerinaCodeSnippet(String responseBodyString) {
        return responseBodyString.contains(TRIPLE_BACKTICK_BALLERINA) && responseBodyString.contains(TRIPLE_BACKTICK);
    }

    private record GeneratedCode(Map<String, String> codeMap, JsonArray functions) { }

    private static void updateSourceFilesWithGeneratedContent(JsonArray sourceFiles,
                                                              Map<String, String> generatedCodeMap) {
        for (String fileName: generatedCodeMap.keySet()) {
            for (JsonElement sourceFile: sourceFiles) {
                JsonObject sourceFileObj = sourceFile.getAsJsonObject();
                if (sourceFileObj.get(FILE_PATH).getAsString().endsWith(fileName)) {
                    sourceFileObj.addProperty(CONTENT, generatedCodeMap.get(fileName));
                    break;
                }
            }
        }
    }

    private static String constructMigrateUserPrompt(String additionalInstructions, String executionPlan) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("# Role\n")
                .append("You are an expert in migrating azure Logic Apps to Ballerina integration ")
                .append("with given logic app json file.\n\n");

        if (executionPlan != null && !executionPlan.isEmpty()) {
            prompt.append(executionPlan).append("\n\n");
        }

        prompt.append("# Additional Instructions\n");
        if (additionalInstructions != null && !additionalInstructions.isEmpty()) {
            prompt.append(additionalInstructions).append("\n\n");
        }

        prompt.append("# Instructions\n\n")
                .append("Refer the following Ballerina language equivalent for Azure Logic App actions ")
                .append("with examples.\n\n")
                .append("| Azure Logic App Action | Ballerina Language Equivalent | Example |\n")
                .append("|--------|------|---------------|\n")
                .append("| Condition (If-Then-Else) | if-else statement | ")
                .append("if (condition) { ... } else { ... }\n")
                .append("| Switch (Case) | match statement | ")
                .append("match expression { case value1: { ... } case value2: { ... } }\n")
                .append("| For each | foreach statement | foreach var item in collection { ... }\n")
                .append("| Until | while loop | while (!condition) { ... }\n")
                .append("| HTTP | HTTP client | import ballerina/http; http:Client client = ")
                .append("check new(\"<BASE_URL>\"); var response = check client->get(\"/<PATH>\");\n")
                .append("| Request/Response | HTTP service | ")
                .append("resource function get path(http:Request req) returns http:Response { ... }\n")
                .append("| ServiceProvider (SQL) | SQL client | import ballerina/sql; ")
                .append("import ballerinax/mysql; mysql:Client dbClient = ")
                .append("check new(host, user, password, dbName, port);\n")
                .append("| Query | Filter operation | ")
                .append("var filtered = array.filter(item => item.property == value);\n\n")
                .append("When you are converting the logic app processing phase in the execution plan ")
                .append("into Ballerina integration, follow the instructions given below.\n\n")
                .append("1. Use existing file structure in the Ballerina integration project to avoid ")
                .append("bal files with large numbers of lines as well as to write user readable code.\n")
                .append("2. Identify action hierarchy and group sub-actions into functions. ")
                .append("(Usually scope, foreach action types have sub-actions).\n")
                .append("3. Use variable names in the Ballerina integration similar to the variables ")
                .append("initialized in the Logic Apps.\n")
                .append("4. Add comments in the Ballerina integration code describing that ")
                .append("\"this line execution related to the that logic app action\".\n")
                .append("5. Do not use reserved keywords such as \"resource\" as identifiers ")
                .append("in the Ballerina integration code.\n\n")
                .append("# Action Plan\n")
                .append("1. Analyze the Logic App in the attached json file with its logic app ")
                .append("action types and their tasks.\n")
                .append("2. Identify the execution order accurately by refering the json file ")
                .append("with the field 'runAfter'.\n")
                .append("3. Generate completed code for all the logic actions listed in the ")
                .append("given execution plan.\n")
                .append("4. **Crucially:** Output the entire Ballerina equivalent code implementation ")
                .append("for each logic app action (Do not put comments in the generated code to let ")
                .append("user know that he needs to complete the implementation manually).\n")
                .append("5. Give the complete implementation for each function you define in the code.\n");

        return prompt.toString();
    }

    private static JsonObject constructExecPlanGenerationPayload(String additionalInstructions, JsonArray sourceFiles,
                                                                 JsonArray fileAttachmentContents, String packageName) {
        JsonObject payload = new JsonObject();
        payload.addProperty("usecase", additionalInstructions);
        payload.add("chatHistory", new JsonArray());
        payload.add("sourceFiles", sourceFiles);
        payload.addProperty("operationType", "MIGRATION");
        payload.addProperty("packageName", packageName);
        payload.add("fileAttachmentContents", fileAttachmentContents);
        return payload;
    }

    private static JsonObject constructCodeGenerationPayload(String generatedPrompt, JsonArray sourceFiles,
                                                             JsonArray fileAttachmentContents, String packageName) {
        JsonObject payload = new JsonObject();
        payload.addProperty("usecase", generatedPrompt);
        payload.add("chatHistory", new JsonArray());
        payload.add("sourceFiles", sourceFiles);
        payload.addProperty("operationType", "MIGRATION");
        payload.addProperty("packageName", packageName);
        payload.add("fileAttachmentContents", fileAttachmentContents);
        return payload;
    }

    private static JsonObject constructCodeReparationPayload(String generatedPrompt, JsonArray sourceFiles,
                                                             JsonArray fileAttachmentContents, String packageName,
                                                             JsonArray functions, JsonObject diagnosticsRequest) {
        JsonObject payload = new JsonObject();
        payload.addProperty("usecase", generatedPrompt);
        payload.add("chatHistory", new JsonArray());
        payload.add("sourceFiles", sourceFiles);
        payload.addProperty("operationType", "MIGRATION");
        payload.addProperty("packageName", packageName);
        payload.add("fileAttachmentContents", fileAttachmentContents);
        payload.add("functions", functions);
        payload.add("diagnosticRequest", diagnosticsRequest);
        return payload;
    }

    private static HttpClient getHttpClient() {
        return HttpClient.newHttpClient();
    }

    private static ModuleDescriptor getModuleDescriptor(String packageName) throws UnsupportedEncodingException {
        ModuleName moduleName = ModuleName.from(PackageName.from(packageName));
        PackageDescriptor packageDescriptor = PackageDescriptor.from(PackageOrg.from(DEFAULT_ORG_NAME),
                moduleName.packageName(), PackageVersion.from(DEFAULT_PROJECT_VERSION));
        return ModuleDescriptor.from(moduleName, packageDescriptor);
    }

    private static JsonArray getFileAttachmentContents(String fileName, String content) {
        JsonArray fileAttachmentContents = new JsonArray();
        JsonObject fileObj = new JsonObject();
        fileObj.addProperty("fileName", fileName);
        fileObj.addProperty("content", content);
        fileAttachmentContents.add(fileObj);
        return fileAttachmentContents;
    }

    private static JsonArray createSourceFilesArray() {
        JsonArray sourceFilesArray = new JsonArray();

        String[] ballerinaProjectFileList = {"agents.bal", "config.bal", "connections.bal", "data_mappings.bal",
                "functions.bal", "main.bal", "types.bal"};

        for (String fileName: ballerinaProjectFileList) {
            JsonObject sourceFileObj = new JsonObject();
            sourceFileObj.addProperty(FILE_PATH, fileName);
            sourceFileObj.addProperty(CONTENT, "");
            sourceFilesArray.add(sourceFileObj);
        }

        return sourceFilesArray;
    }

    /**
     * Gets the Ballerina home directory by executing 'bal home' command.
     *
     * @return Ballerina home directory path, or null if command fails or times out
     */
    private static String getBallerinaHome() {
        ProcessBuilder processBuilder = new ProcessBuilder("bal", "home");
        processBuilder.redirectErrorStream(true);

        try {
            Process process = processBuilder.start();
            boolean finished = process.waitFor(5, TimeUnit.SECONDS);

            if (!finished) {
                process.destroyForcibly();
                errStream.println("Ballerina home cannot be determined: 'bal home' command timed out.");
                return null;
            }

            int exitCode = process.exitValue();

            if (exitCode == 0) {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {

                    String line = reader.readLine();
                    if (line != null && !line.trim().isEmpty()) {
                        return line.trim();
                    }
                }
            } else {
                errStream.println("Ballerina home cannot be determined: bal come command failed.");
            }

        } catch (IOException e) {
            errStream.println("Ballerina home cannot be determined: Error executing 'bal home' command: " +
                    e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            errStream.println("Ballerina home cannot be determined: Command 'bal home' was interrupted");
        }

        return null;
    }
}
