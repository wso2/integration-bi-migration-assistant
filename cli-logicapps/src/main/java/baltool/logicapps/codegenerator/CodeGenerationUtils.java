package baltool.logicapps.codegenerator;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.ballerina.projects.BuildOptions;
import io.ballerina.projects.DiagnosticResult;
import io.ballerina.projects.ModuleDescriptor;
import io.ballerina.projects.PackageCompilation;
import io.ballerina.projects.directory.BuildProject;
import io.ballerina.projects.util.ProjectUtils;
import io.ballerina.tools.diagnostics.Diagnostic;
import io.ballerina.tools.diagnostics.DiagnosticInfo;
import io.ballerina.tools.diagnostics.DiagnosticSeverity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;
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
import static baltool.logicapps.Constants.DEV_COPILOT_BACKEND_URL;
import static baltool.logicapps.Constants.FILE_PATH;
import static baltool.logicapps.Constants.TRIPLE_BACKTICK;
import static baltool.logicapps.Constants.TRIPLE_BACKTICK_BALLERINA;

public class CodeGenerationUtils {
    private static final String TEMP_DIR_PREFIX = "logicapps-migration-tool-codegen-diagnostics-dir-";
    public static final boolean BALLERINA_DEV_UPDATE = Boolean.parseBoolean(
            System.getenv("BALLERINA_DEV_UPDATE"));

    public static JsonArray generateCodeForLogicApp(String copilotAccessToken, Path logicAppFilePath,
                                                    String packageName, String additionalInstructions,
                                                    ModuleDescriptor moduleDescriptor,
                                                    ProgressCallback progressCallback, VerboseLogger logger) {
        try {
            String logicAppContent = Files.readString(logicAppFilePath, StandardCharsets.UTF_8);
            try {
                JsonParser.parseString(logicAppContent);
                logger.printVerboseInfo("Logic App JSON validation: SUCCESS");
            } catch (Exception e) {
                logger.printVerboseError("Logic App JSON validation: FAILED - " + e.getMessage());
                logger.printVerboseError("Stack trace: \n" + Arrays.toString(e.getStackTrace()));
                throw new RuntimeException("Invalid JSON content in Logic App file", e);
            }

            JsonArray fileAttachmentContents = getFileAttachmentContents(logicAppFilePath.getFileName().toString(),
                    logicAppContent);
            JsonArray sourceFiles = createSourceFilesArray();
            logger.printVerboseInfo("Source files structure created: " + sourceFiles.size() + " files");

            if (progressCallback == null) {
                logger.printInfo("Starting Ballerina code generation for Logic App file: " +
                        logicAppFilePath.getFileName());
            }

            // Step 1: Generate execution plan
            if (progressCallback == null) {
                logger.updateProgressBar(1, "Generating execution plan");
            } else {
                progressCallback.updateProgress(1, "Generating execution plan");
            }

            String executionPlan = CodeGenerationUtils.generateLogicAppExecutionPlan(getCopilotBackendURL(),
                    copilotAccessToken, getHttpClient(), sourceFiles, fileAttachmentContents, packageName,
                    additionalInstructions, logger);
            String generatedPrompt = constructMigrateUserPrompt(additionalInstructions, executionPlan);

            if (progressCallback == null) {
                logger.printInfo("✓ Execution plan generated");
            }

            // Step 2: Generate code
            if (progressCallback == null) {
                logger.updateProgressBar(2, "Generating code");
            } else {
                progressCallback.updateProgress(2, "Generating code");
            }

            GeneratedCode generatedCode = generateCode(getCopilotBackendURL(), copilotAccessToken, getHttpClient(),
                    sourceFiles, fileAttachmentContents, packageName, generatedPrompt, logger);
            logger.printVerboseInfo("Generated files count: " + generatedCode.codeMap.size());

            updateSourceFilesWithGeneratedContent(sourceFiles, generatedCode.codeMap);
            logger.printVerboseInfo("Source files updated with generated content");

            if (progressCallback == null) {
                logger.printInfo("✓ Code generation completed");
            }

            // Step 3: Repair code
            if (progressCallback == null) {
                logger.updateProgressBar(3, "Repairing code");
            } else {
                progressCallback.updateProgress(3, "Repairing code");
            }

            GeneratedCode repairedCode = repairCode(getCopilotBackendURL(), copilotAccessToken, getHttpClient(),
                    sourceFiles, fileAttachmentContents, packageName, generatedPrompt, moduleDescriptor,
                    generatedCode, logger);

            logger.printVerboseInfo("Repaired files count: " + repairedCode.codeMap.size());
            updateSourceFilesWithGeneratedContent(sourceFiles, repairedCode.codeMap);
            logger.printVerboseInfo("Source files updated with repaired content");

            if (progressCallback == null) {
                logger.setProgressBarActive(false);
                logger.printInfo("✓ Code repair completed");
                logger.printInfo("\nBallerina Integration generated successfully!\n");
            }

            return sourceFiles;
        } catch (URISyntaxException e) {
            String errorMsg = "Failed to generate code, invalid URI for Copilot";
            logger.printError(errorMsg);
            logger.printVerboseError("Invalid URI configuration: \n" + Arrays.toString(e.getStackTrace()));
            throw new RuntimeException(errorMsg, e);
        } catch (ConnectException e) {
            String errorMsg = "Failed to connect to Copilot services";
            logger.printError(errorMsg);
            logger.printVerboseError("Network connection failure: \n" + Arrays.toString(e.getStackTrace()));
            throw new RuntimeException(errorMsg, e);
        } catch (IOException | InterruptedException e) {
            String errorMsg = "Failed to generate code: " + e.getMessage();
            logger.printError(errorMsg);
            logger.printVerboseError("File I/O or process interruption: \n" + Arrays.toString(e.getStackTrace()));
            throw new RuntimeException(errorMsg, e);
        } catch (Exception e) {
            String errorMsg = "Unexpected error during code generation: " + e.getMessage();
            logger.printError(errorMsg);
            logger.printVerboseError("Unexpected error during code generation: " + Arrays.toString(e.getStackTrace()));
            throw new RuntimeException(errorMsg, e);
        }
    }

    public static String generateLogicAppExecutionPlan(String copilotUrl, String copilotAccessToken, HttpClient client,
                                                       JsonArray sourceFiles, JsonArray fileAttachmentContents,
                                                       String packageName, String additionalInstructions,
                                                       VerboseLogger logger)
            throws URISyntaxException, IOException, InterruptedException {

        logger.printVerboseInfo("Preparing execution plan generation payload");
        JsonObject executionPlanGenerationPayload = constructExecPlanGenerationPayload(additionalInstructions,
                sourceFiles, fileAttachmentContents, packageName);
        logger.printVerboseInfo("Payload size: " + executionPlanGenerationPayload.toString().length() + " characters");

        URI uri = new URI(copilotUrl + "/logicapps/executionplan");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + copilotAccessToken)
                .POST(HttpRequest.BodyPublishers.ofString(executionPlanGenerationPayload.toString()))
                .build();

        logger.printVerboseInfo("Sending HTTP request to get LogicApp execution plan");
        long startTime = System.currentTimeMillis();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        long duration = System.currentTimeMillis() - startTime;

        logger.printVerboseInfo("HTTP response received");
        logger.printVerboseInfo("Response time: " + duration + "ms");
        logger.printVerboseInfo("Response status: " + response.statusCode());

        if (response.statusCode() >= 300) {
            String errorMsg = "Execution plan generation failed with status: " + response.statusCode();
            logger.printVerboseError(errorMsg);
            logger.printVerboseError("Response body: " + response.body());
            throw new RuntimeException(errorMsg + ". Response: " + response.body());
        }

        JsonObject responseJson = JsonParser.parseString(response.body()).getAsJsonObject();
        String executionPlan = responseJson.getAsJsonPrimitive("executionPlan").getAsString();

        logger.printVerboseInfo("Execution plan extracted successfully");
        logger.printVerboseInfo("Execution plan length: " + executionPlan.length() + " characters");

        return executionPlan;
    }

    private static GeneratedCode generateCode(String copilotUrl, String copilotAccessToken, HttpClient client,
                                              JsonArray sourceFiles, JsonArray fileAttachmentContents,
                                              String packageName, String generatedPrompt, VerboseLogger logger)
            throws URISyntaxException, IOException, InterruptedException {

        logger.printVerboseInfo("Preparing code generation payload");
        JsonObject codeGenerationPayload = constructCodeGenerationPayload(generatedPrompt, sourceFiles,
                fileAttachmentContents, packageName);
        logger.printVerboseInfo("Payload size: " + codeGenerationPayload.toString().length() + " characters");

        URI uri = new URI(copilotUrl + "/code");
        HttpRequest codeGenerationRequest = HttpRequest.newBuilder()
                .uri(uri)
                .header("Authorization", "Bearer " + copilotAccessToken)
                .POST(HttpRequest.BodyPublishers.ofString(codeGenerationPayload.toString())).build();

        logger.printVerboseInfo("Sending HTTP request to get generated code");
        long startTime = System.currentTimeMillis();
        HttpResponse<Stream<String>> response = client.send(codeGenerationRequest, HttpResponse.BodyHandlers.ofLines());
        long duration = System.currentTimeMillis() - startTime;

        if (response.statusCode() >= 300) {
            String errorMsg = "Code generation failed with status: " + response.statusCode();
            logger.printVerboseError(errorMsg);
            throw new RuntimeException(errorMsg);
        }

        logger.printVerboseInfo("Processing streamed response for code generation");
        GeneratedCode result = extractGeneratedCode(response.body(), logger);

        return result;
    }

    private static GeneratedCode repairCode(String copilotUrl, String copilotAccessToken, HttpClient client,
                                            JsonArray sourceFiles, JsonArray fileAttachmentContents, String packageName,
                                            String generatedPrompt, ModuleDescriptor moduleDescriptor,
                                            GeneratedCode generatedCodeMap, VerboseLogger logger)
            throws IOException, URISyntaxException, InterruptedException {

        logger.printVerboseInfo("Starting code repair process...");
        return repairIfDiagnosticsExist(copilotUrl, copilotAccessToken, client, sourceFiles,
                fileAttachmentContents, packageName, generatedPrompt, moduleDescriptor, generatedCodeMap, logger);
    }

    private static GeneratedCode repairIfDiagnosticsExist(String copilotUrl, String copilotAccessToken,
                                                          HttpClient client, JsonArray sourceFiles,
                                                          JsonArray fileAttachmentContents, String packageName,
                                                          String generatedPrompt,
                                                          ModuleDescriptor moduleDescriptor,
                                                          GeneratedCode generatedCode, VerboseLogger logger)
            throws IOException, URISyntaxException, InterruptedException {

        logger.printVerboseInfo("Running diagnostics on generated code...");
        Optional<JsonArray> diagnostics = getDiagnostics(sourceFiles, moduleDescriptor, logger);

        if (diagnostics.isEmpty()) {
            logger.printVerboseInfo("No diagnostics found - code repair not needed");
            return generatedCode;
        }

        logger.printVerboseInfo("Proceeding with code repair");
        JsonArray convertedDiagnostics = convertDiagnosticsToMessageObjects(diagnostics);
        logger.printVerboseInfo("Diagnostics converted to message objects");

        JsonObject diagnosticRequest = getDiagnosticsRequest(convertedDiagnostics, generatedCode);
        logger.printVerboseInfo("Diagnostic request prepared");

        String repairResponse = repairCode(copilotUrl, copilotAccessToken, client, sourceFiles, fileAttachmentContents,
                packageName, generatedPrompt, generatedCode, diagnosticRequest, logger);

        if (hasBallerinaCodeSnippet(repairResponse)) {
            Map<String, String> repairedCodeMap = extractGeneratedCodeFromResponse(repairResponse);
            logger.printVerboseInfo("Repaired code extracted: " + repairedCodeMap.size() + " files");
            return new GeneratedCode(repairedCodeMap, generatedCode.functions);
        } else {
            logger.printVerboseError("No Ballerina code snippets found in repair response - using original code");
        }

        return generatedCode;
    }

    private static String repairCode(String copilotUrl, String copilotAccessToken, HttpClient client,
                                     JsonArray sourceFiles, JsonArray fileAttachmentContents, String packageName,
                                     String generatedPrompt, GeneratedCode generatedCodeMap,
                                     JsonObject diagnosticsRequest, VerboseLogger logger)
            throws URISyntaxException, IOException, InterruptedException {

        logger.printVerboseInfo("Preparing code repair payload...");
        JsonObject codeReparationPayload = constructCodeReparationPayload(generatedPrompt, sourceFiles,
                fileAttachmentContents, packageName, generatedCodeMap.functions, diagnosticsRequest);
        logger.printVerboseInfo("Repair payload size: " + codeReparationPayload.toString().length() + " characters");

        URI uri = new URI(copilotUrl + "/code/repair");
        HttpRequest codeReparationRequest = HttpRequest.newBuilder()
                .uri(uri)
                .header("Authorization", "Bearer " + copilotAccessToken)
                .POST(HttpRequest.BodyPublishers.ofString(codeReparationPayload.toString())).build();

        logger.printVerboseInfo("Sending HTTP request to get the repaired code");
        long startTime = System.currentTimeMillis();
        HttpResponse<String> response = client.send(codeReparationRequest, HttpResponse.BodyHandlers.ofString());
        long duration = System.currentTimeMillis() - startTime;

        logger.printVerboseInfo("Code repair response received");
        logger.printVerboseInfo("Response time: " + duration + "ms");
        logger.printVerboseInfo("Response status: " + response.statusCode());

        if (response.statusCode() >= 300) {
            String errorMsg = "Code repair failed with status: " + response.statusCode();
            logger.printVerboseError(errorMsg);
            logger.printVerboseError("Response body: " + response.body());
            throw new RuntimeException(errorMsg + ". Response: " + response.body());
        }

        JsonObject responseJson = JsonParser.parseString(response.body()).getAsJsonObject();
        String repairResponse = responseJson.getAsJsonPrimitive("repairResponse").getAsString();
        logger.printVerboseInfo("Repair response extracted successfully");
        return repairResponse;
    }

    private static Optional<JsonArray> getDiagnostics(JsonArray sourceFiles, ModuleDescriptor moduleDescriptor,
                                                      VerboseLogger logger) throws IOException {

        logger.printVerboseInfo("Creating temporary project for diagnostics");
        BuildProject project = createProject(sourceFiles, moduleDescriptor, logger);

        logger.printVerboseInfo("Compiling project for diagnostics");
        PackageCompilation compilation = project.currentPackage().getCompilation();
        DiagnosticResult diagnosticResult = compilation.diagnosticResult();

        logger.printVerboseInfo("Diagnostic compilation completed");
        logger.printVerboseInfo("Total diagnostics: " + diagnosticResult.diagnostics().size());
        logger.printVerboseInfo("Error count: " + diagnosticResult.errorCount());
        logger.printVerboseInfo("Warning count: " + diagnosticResult.warningCount());

        if (diagnosticResult.errorCount() == 0) {
            logger.printVerboseInfo("No errors found - diagnostics successful");
            return Optional.empty();
        }

        logger.printVerboseInfo("Processing error diagnostics");
        JsonArray diagnostics = new JsonArray();
        int errorCount = 0;

        for (Diagnostic diagnostic : diagnosticResult.diagnostics()) {
            DiagnosticInfo diagnosticInfo = diagnostic.diagnosticInfo();
            if (diagnosticInfo.severity() != DiagnosticSeverity.ERROR) {
                continue;
            }
            diagnostics.add(diagnostic.toString());
            errorCount++;
        }

        logger.printVerboseInfo("Processed " + errorCount + " error diagnostics");
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

    private static BuildProject createProject(JsonArray sourceFiles, ModuleDescriptor moduleDescriptor,
                                              VerboseLogger logger) throws IOException {

        Path tempProjectDir = Files.createTempDirectory(TEMP_DIR_PREFIX + System.currentTimeMillis());
        tempProjectDir.toFile().deleteOnExit();
        logger.printVerboseInfo("Created Temporary project directory: " + tempProjectDir.toAbsolutePath());

        logger.printVerboseInfo("Writing source files to temporary directory");
        int fileCount = 0;
        for (JsonElement sourceFile : sourceFiles) {
            JsonObject sourceFileObj = sourceFile.getAsJsonObject();
            String filePath = sourceFileObj.get(FILE_PATH).getAsString();
            String content = sourceFileObj.get(CONTENT).getAsString();

            File file = Files.createFile(tempProjectDir.resolve(Path.of(filePath))).toFile();
            file.deleteOnExit();

            try (FileWriter fileWriter = new FileWriter(file, StandardCharsets.UTF_8)) {
                fileWriter.write(content);
            }

            fileCount++;
        }
        logger.printVerboseInfo("Total files written: " + fileCount);

        Path ballerinaTomlPath = tempProjectDir.resolve(BALLERINA_TOML_FILE);
        File balTomlFile = Files.createFile(ballerinaTomlPath).toFile();
        balTomlFile.deleteOnExit();

        String tomlContent = String.format("""
                [package]
                org = "%s"
                name = "%s"
                version = "%s"
                """,
                moduleDescriptor.org().value(),
                moduleDescriptor.packageName().value(),
                moduleDescriptor.version().value());

        try (FileWriter fileWriter = new FileWriter(balTomlFile, StandardCharsets.UTF_8)) {
            fileWriter.write(tomlContent);
        }
        logger.printVerboseInfo("Ballerina.toml created successfully");

        Path ballerinaHomePath = Path.of(Objects.requireNonNull(getBallerinaHome(logger)));
        System.setProperty("ballerina.home", ballerinaHomePath.toString());
        logger.printVerboseInfo("Ballerina home set to: " + ballerinaHomePath);

        BuildOptions buildOptions = BuildOptions.builder().targetDir(ProjectUtils.getTemporaryTargetPath()).build();
        BuildProject project = BuildProject.load(tempProjectDir, buildOptions);
        logger.printVerboseInfo("Temporary project created successfully");

        return project;
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

    private static GeneratedCode extractGeneratedCode(Stream<String> lines, VerboseLogger logger) {
        logger.printVerboseInfo("Processing streamed response lines");
        String[] linesArr = lines.toArray(String[]::new);
        int length = linesArr.length;

        if (length == 1) {
            JsonObject jsonObject = JsonParser.parseString(linesArr[0]).getAsJsonObject();
            if (jsonObject.has("error_message")) {
                String errorMsg = jsonObject.get("error_message").getAsString();
                logger.printVerboseError(errorMsg);
                throw new RuntimeException(errorMsg);
            }
        }

        StringBuilder responseBody = new StringBuilder();
        JsonArray functions = null;
        int contentBlocks = 0;
        int functionBlocks = 0;

        logger.printVerboseInfo("Parsing response stream");
        int index = 0;
        while (index < length) {
            String line = linesArr[index];

            if (line.isBlank()) {
                index++;
                continue;
            }

            if ("event: content_block_delta".equals(line)) {
                line = linesArr[++index].substring(6);
                String textContent = JsonParser.parseString(line).getAsJsonObject()
                        .getAsJsonPrimitive("text").getAsString();
                responseBody.append(textContent);
                contentBlocks++;
                continue;
            }

            if ("event: functions".equals(line)) {
                line = linesArr[++index].substring(6);
                functions = JsonParser.parseString(line).getAsJsonArray();
                functionBlocks++;
                continue;
            }

            index++;
        }

        logger.printVerboseInfo("Response parsing completed");
        logger.printVerboseInfo("Content blocks processed: " + contentBlocks);
        logger.printVerboseInfo("Function blocks processed: " + functionBlocks);

        String responseBodyString = responseBody.toString();
        Map<String, String> codeMap = extractGeneratedCodeFromResponse(responseBodyString);

        logger.printVerboseInfo("Code extraction completed");
        logger.printVerboseInfo("Extracted files: " + codeMap.size());

        return new GeneratedCode(codeMap, functions);
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
        return HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(300)).build();
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
    private static String getBallerinaHome(VerboseLogger logger) {
        logger.printVerboseInfo("Determining Ballerina home directory...");
        ProcessBuilder processBuilder = new ProcessBuilder("bal", "home");
        processBuilder.redirectErrorStream(true);

        try {
            logger.printVerboseInfo("Executing 'bal home' command...");
            Process process = processBuilder.start();
            boolean finished = process.waitFor(5, TimeUnit.SECONDS);
            if (!finished) {
                logger.printVerboseError("Command timed out, destroying process");
                process.destroyForcibly();
                logger.printError("Ballerina home cannot be determined: 'bal home' command timed out.");
                return null;
            }

            if (process.exitValue() == 0) {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {

                    String line = reader.readLine();
                    if (line != null && !line.trim().isEmpty()) {
                        String ballerinaHome = line.trim();
                        return ballerinaHome;
                    }
                }
            } else {
                logger.printVerboseError("Command failed with exit code: " + process.exitValue());
                logger.printError("Ballerina home cannot be determined: bal home command failed.");
            }

        } catch (IOException e) {
            String errorMsg = "Error executing 'bal home' command: " + e.getMessage();
            logger.printError(errorMsg);
            logger.printVerboseError("Failed to execute 'bal home' command: " + Arrays.toString(e.getStackTrace()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            String errorMsg = "Command 'bal home' was interrupted";
            logger.printError(errorMsg);
            logger.printVerboseError("'bal home' command was interrupted: " + Arrays.toString(e.getStackTrace()));
        }

        return null;
    }

    private static String getCopilotBackendURL() {
        return BALLERINA_DEV_UPDATE ? DEV_COPILOT_BACKEND_URL : COPILOT_BACKEND_URL;
    }
}
