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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static baltool.logicapps.Constants.BALLERINA_TOML_FILE;
import static baltool.logicapps.Constants.CONTENT;
import static baltool.logicapps.Constants.COPILOT_BACKEND_URL;
import static baltool.logicapps.Constants.DEV_COPILOT_BACKEND_URL;
import static baltool.logicapps.Constants.FILE_PATH;
import static baltool.logicapps.Constants.MAXIMUM_RETRY_COUNT;
import static baltool.logicapps.Constants.TRIPLE_BACKTICK;
import static baltool.logicapps.Constants.TRIPLE_BACKTICK_BALLERINA;

/**
 * Utility class for code generation related operations.
 */
public class CodeGenerationUtils {
    private static final String TEMP_DIR_PREFIX = "logicapps-migration-tool-codegen-diagnostics-dir-";
    public static final boolean BALLERINA_DEV_UPDATE = Boolean.parseBoolean(
            System.getenv("BALLERINA_DEV_UPDATE"));

    /**
     * Generates Ballerina code for a Logic App file using the Copilot service.
     *
     * @param copilotAccessToken Access token for Copilot service
     * @param logicAppFilePath Path to the Logic App JSON file
     * @param packageName Name of the Ballerina package to generate
     * @param additionalInstructions Additional instructions for code generation
     * @param moduleDescriptor Module descriptor for the Ballerina project
     * @param logger Logger for verbose output
     * @param fileName Name of the file being processed
     *
     * @return JsonArray containing the generated source files
     */
    public static JsonArray generateCodeForLogicApp(String copilotAccessToken, Path logicAppFilePath,
                                                    String packageName, String additionalInstructions,
                                                    ModuleDescriptor moduleDescriptor, VerboseLoggerFactory logger,
                                                    String fileName) {
        try {
            String logicAppContent = Files.readString(logicAppFilePath, StandardCharsets.UTF_8);
            try {
                JsonParser.parseString(logicAppContent);
                logger.printVerboseInfo(fileName, "Logic App JSON validation: SUCCESS");
            } catch (Exception e) {
                logger.printVerboseError(fileName, "Logic App JSON validation: FAILED - " + e.getMessage());
                logger.printStackTrace(fileName, e.getStackTrace());
                throw new RuntimeException("Invalid JSON content in Logic App file", e);
            }

            JsonArray fileAttachmentContents = getFileAttachmentContents(logicAppFilePath.getFileName().toString(),
                    logicAppContent);
            JsonArray sourceFiles = createSourceFilesArray();
            logger.printVerboseInfo(fileName, "Source files structure created: " + sourceFiles.size() + " files");

            logger.printInfo(fileName, "Starting Ballerina code generation for Logic App file: " +
                    logicAppFilePath.getFileName());

            // Step 1: Generate execution plan
            logger.startProgress(fileName, "Generating execution plan");
            String executionPlan = CodeGenerationUtils.generateLogicAppExecutionPlan(copilotAccessToken, sourceFiles,
                    fileAttachmentContents, packageName, additionalInstructions, logger, fileName);
            String generatedPrompt = constructMigrateUserPrompt(additionalInstructions, executionPlan);
            logger.printInfo(fileName, "✓ Execution plan generated");

            // Step 2: Generate code
            logger.updateProgress(fileName, 2, "Generating code");
            GeneratedCode generatedCode = generateCode(copilotAccessToken, sourceFiles, fileAttachmentContents,
                    packageName, generatedPrompt, logger, fileName);
            logger.printVerboseInfo(fileName, "Generated files count: " + generatedCode.codeMap.size());
            logger.printInfo(fileName, "✓ Code generation completed");

            // Step 3: Repair code
            logger.updateProgress(fileName, 3, "Repairing code");
            GeneratedCode repairedCode = repairCode(copilotAccessToken, sourceFiles, fileAttachmentContents,
                    packageName, generatedPrompt, moduleDescriptor, generatedCode, logger, fileName);
            logger.printVerboseInfo(fileName, "Repaired files count: " + repairedCode.codeMap.size());
            logger.printInfo(fileName, "✓ Code repair completed");

            return sourceFiles;
        } catch (URISyntaxException e) {
            String errorMsg = "Failed to generate code, invalid URI for Copilot";
            logger.printError(fileName, errorMsg);
            logger.printStackTrace(fileName, e.getStackTrace());
            throw new RuntimeException(errorMsg, e);
        } catch (ConnectException e) {
            String errorMsg = "Failed to connect to Copilot services";
            logger.printError(fileName, errorMsg);
            logger.printStackTrace(fileName, e.getStackTrace());
            throw new RuntimeException(errorMsg, e);
        } catch (IOException | InterruptedException e) {
            String errorMsg = "Failed to generate code: " + e.getMessage();
            logger.printError(fileName, errorMsg);
            logger.printStackTrace(fileName, e.getStackTrace());
            throw new RuntimeException(errorMsg, e);
        } catch (Exception e) {
            String errorMsg = "Unexpected error during code generation: " + e.getMessage();
            logger.printError(fileName, errorMsg);
            logger.printStackTrace(fileName, e.getStackTrace());
            throw new RuntimeException(errorMsg, e);
        }
    }

    /**
     * Generates an execution plan for a Logic App using the Copilot service.
     *
     * @param copilotAccessToken Access token for Copilot service
     * @param sourceFiles Array of source files to include in the execution plan
     * @param fileAttachmentContents Array of file attachment contents
     * @param packageName Name of the Ballerina package
     * @param additionalInstructions Additional instructions for execution plan generation
     * @param logger Logger for verbose output
     * @param fileName Name of the file being processed
     *
     * @return The generated execution plan as a String
     */
    public static String generateLogicAppExecutionPlan(String copilotAccessToken, JsonArray sourceFiles,
                                                       JsonArray fileAttachmentContents, String packageName,
                                                       String additionalInstructions, VerboseLoggerFactory logger,
                                                       String fileName)
            throws URISyntaxException, IOException, InterruptedException {
        URI uri = new URI(getCopilotBackendURL() + "/logicapps/executionplan");

        logger.printVerboseInfo(fileName, "Preparing execution plan generation payload");
        JsonObject executionPlanGenerationPayload = constructExecPlanGenerationPayload(additionalInstructions,
                sourceFiles, fileAttachmentContents, packageName);
        logger.printVerboseInfo(fileName, "Payload size: " +
                executionPlanGenerationPayload.toString().length() + " characters");

        logger.printVerboseInfo(fileName, "Sending HTTP request to get LogicApp execution plan");
        long startTime = System.currentTimeMillis();
        HttpResponse<String> response = sendRequestAsync(uri, executionPlanGenerationPayload, copilotAccessToken,
                logger, fileName);
        long duration = System.currentTimeMillis() - startTime;

        logger.printVerboseInfo(fileName, "HTTP response received");
        logger.printVerboseInfo(fileName, "Response time: " + duration + "ms");
        logger.printVerboseInfo(fileName, "Response status: " + response.statusCode());

        if (response.statusCode() >= 300) {
            String errorMsg = "Execution plan generation failed with status: " + response.statusCode();
            logger.printVerboseError(fileName, errorMsg);
            logger.printVerboseError(fileName, "Response body: " + response.body());
            throw new RuntimeException(errorMsg + ". Response: " + response.body());
        }

        JsonObject responseJson = JsonParser.parseString(response.body()).getAsJsonObject();
        String executionPlan = responseJson.getAsJsonPrimitive("executionPlan").getAsString();

        logger.printVerboseInfo(fileName, "Execution plan extracted successfully");
        logger.printVerboseInfo(fileName, "Execution plan length: " + executionPlan.length() + " characters");

        return executionPlan;
    }

    /**
     * Generates Ballerina code using the Copilot service.
     *
     * @param copilotAccessToken Access token for Copilot service
     * @param sourceFiles Array of source files to include in code generation
     * @param fileAttachmentContents Array of file attachment contents
     * @param packageName Name of the Ballerina package
     * @param generatedPrompt The prompt used for code generation
     * @param logger Logger for verbose output
     * @param fileName Name of the file being processed
     *
     * @return GeneratedCode object containing the generated code and functions
     */
    private static GeneratedCode generateCode(String copilotAccessToken, JsonArray sourceFiles,
                                              JsonArray fileAttachmentContents, String packageName,
                                              String generatedPrompt, VerboseLoggerFactory logger, String fileName)
            throws URISyntaxException, IOException, InterruptedException {

        logger.printVerboseInfo(fileName, "Preparing code generation payload");
        JsonObject codeGenerationPayload = constructCodeGenerationPayload(generatedPrompt, sourceFiles,
                fileAttachmentContents, packageName);
        logger.printVerboseInfo(fileName, "Payload size: " + codeGenerationPayload.toString().length() + " characters");

        URI uri = new URI(getCopilotBackendURL() + "/code");
        HttpRequest codeGenerationRequest = HttpRequest.newBuilder()
                .uri(uri)
                .header("Authorization", "Bearer " + copilotAccessToken)
                .POST(HttpRequest.BodyPublishers.ofString(codeGenerationPayload.toString()))
                .timeout(Duration.ofMinutes(8))
                .build();

        logger.printVerboseInfo(fileName, "Sending HTTP request to get generated code");
        HttpResponse<Stream<String>> response = getHttpClient().send(codeGenerationRequest,
                HttpResponse.BodyHandlers.ofLines());
        logger.printVerboseInfo(fileName, "Code generation response received");
        logger.printVerboseInfo(fileName, "Response status: " + response.statusCode());

        if (response.statusCode() >= 300) {
            String errorMsg = "Code generation failed with status: " + response.statusCode();
            logger.printVerboseError(fileName, errorMsg);
            throw new RuntimeException(errorMsg);
        }

        logger.printVerboseInfo(fileName, "Processing streamed response for code generation");

        GeneratedCode generatedCode = extractGeneratedCode(response.body(), logger, fileName);
        updateSourceFilesWithGeneratedContent(sourceFiles, generatedCode.codeMap, logger, fileName);
        return generatedCode;
    }

    /**
     * Repairs the generated code if diagnostics are found.
     *
     * @param copilotAccessToken Access token for Copilot service
     * @param sourceFiles Array of source files to include in code repair
     * @param fileAttachmentContents Array of file attachment contents
     * @param packageName Name of the Ballerina package
     * @param generatedPrompt The prompt used for code generation
     * @param moduleDescriptor Module descriptor for the Ballerina project
     * @param generatedCode Map of generated code
     * @param logger Logger for verbose output
     * @param fileName Name of the file being processed
     *
     * @return GeneratedCode object containing the repaired code and functions
     */
    private static GeneratedCode repairCode(String copilotAccessToken, JsonArray sourceFiles,
                                            JsonArray fileAttachmentContents, String packageName,
                                            String generatedPrompt, ModuleDescriptor moduleDescriptor,
                                            GeneratedCode generatedCode, VerboseLoggerFactory logger,
                                            String fileName)
            throws IOException, URISyntaxException, InterruptedException {

        logger.printVerboseInfo(fileName, "Starting code repair process...");
        GeneratedCode repairedCode = generatedCode;
        for (int iteration = 0; iteration < MAXIMUM_RETRY_COUNT; iteration++) {
            logger.printVerboseInfo(fileName, "Iteration " + (iteration + 1) + " of code repair");
            GeneratedCode repairedCodeIteration = repairIfDiagnosticsExist(copilotAccessToken, sourceFiles,
                    fileAttachmentContents, packageName, generatedPrompt, moduleDescriptor, generatedCode, logger,
                    fileName);
            if (repairedCodeIteration == null) {
                logger.printVerboseInfo(fileName, "No code generated in iteration " + (iteration + 1));
                break;
            } else {
                repairedCode = repairedCodeIteration;
                logger.printVerboseInfo(fileName, "Code repaired successfully in iteration " + (iteration + 1));
            }
        }
        return repairedCode;
    }

    /**
     * Repairs the generated code if diagnostics exist.
     *
     * @param copilotAccessToken Access token for Copilot service
     * @param sourceFiles Array of source files to include in code repair
     * @param fileAttachmentContents Array of file attachment contents
     * @param packageName Name of the Ballerina package
     * @param generatedPrompt The prompt used for code generation
     * @param moduleDescriptor Module descriptor for the Ballerina project
     * @param generatedCode Generated code to be repaired
     * @param logger Logger for verbose output
     * @param fileName Name of the file being processed
     *
     * @return GeneratedCode object containing the repaired code and functions
     */
    private static GeneratedCode repairIfDiagnosticsExist(String copilotAccessToken, JsonArray sourceFiles,
                                                          JsonArray fileAttachmentContents, String packageName,
                                                          String generatedPrompt,
                                                          ModuleDescriptor moduleDescriptor,
                                                          GeneratedCode generatedCode, VerboseLoggerFactory logger,
                                                          String fileName)
            throws IOException, URISyntaxException, InterruptedException {

        logger.printVerboseInfo(fileName, "Running diagnostics on generated code...");
        Optional<JsonArray> diagnostics = getDiagnostics(sourceFiles, moduleDescriptor, logger, fileName);

        if (diagnostics.isEmpty()) {
            logger.printVerboseInfo(fileName, "No diagnostics found - code repair not needed");
            return null;
        }

        logger.printVerboseInfo(fileName, "Proceeding with code repair");
        JsonArray convertedDiagnostics = convertDiagnosticsToMessageObjects(diagnostics);
        logger.printVerboseInfo(fileName, "Diagnostics converted to message objects");

        JsonObject diagnosticRequest = getDiagnosticsRequest(convertedDiagnostics, generatedCode);
        logger.printVerboseInfo(fileName, "Diagnostic request prepared");

        GeneratedCode repairedCode = getRepairedCodeFromStream(copilotAccessToken, sourceFiles, fileAttachmentContents,
                packageName, generatedPrompt, generatedCode, diagnosticRequest, logger, fileName);
        updateSourceFilesWithGeneratedContent(sourceFiles, repairedCode.codeMap, logger, fileName);
        return repairedCode;
    }

    /**
     * Repairs the code using the Copilot service.
     *
     * @param copilotAccessToken Access token for Copilot service
     * @param sourceFiles Array of source files to include in code repair
     * @param fileAttachmentContents Array of file attachment contents
     * @param packageName Name of the Ballerina package
     * @param generatedPrompt The prompt used for code generation
     * @param generatedCodeMap Map of generated code
     * @param diagnosticsRequest Request object containing diagnostics information
     * @param logger Logger for verbose output
     * @param fileName Name of the file being processed
     *
     * @return The repaired code as a String
     */
    private static GeneratedCode getRepairedCode(String copilotAccessToken, JsonArray sourceFiles,
                                     JsonArray fileAttachmentContents, String packageName, String generatedPrompt,
                                     GeneratedCode generatedCodeMap, JsonObject diagnosticsRequest,
                                     VerboseLoggerFactory logger, String fileName)
            throws URISyntaxException, IOException, InterruptedException {
        URI uri = new URI(getCopilotBackendURL() + "/code/repair");

        logger.printVerboseInfo(fileName, "Preparing code repair payload...");
        JsonObject codeReparationPayload = constructCodeReparationPayload(generatedPrompt, sourceFiles,
                fileAttachmentContents, packageName, generatedCodeMap.functions, diagnosticsRequest);
        logger.printVerboseInfo(fileName, "Repair payload size: " +
                codeReparationPayload.toString().length() + " characters");

        logger.printVerboseInfo(fileName, "Sending HTTP request to get the repaired code");
        long startTime = System.currentTimeMillis();
        HttpResponse<String> response = sendRequestAsync(uri, codeReparationPayload, copilotAccessToken, logger,
                fileName);
        long duration = System.currentTimeMillis() - startTime;

        logger.printVerboseInfo(fileName, "Code repair response received");
        logger.printVerboseInfo(fileName, "Response time: " + duration + "ms");
        logger.printVerboseInfo(fileName, "Response status: " + response.statusCode());

        if (response.statusCode() >= 300) {
            String errorMsg = "Code repair failed with status: " + response.statusCode();
            logger.printVerboseError(fileName, errorMsg);
            logger.printVerboseError(fileName, "Response body: " + response.body());
            throw new RuntimeException(errorMsg + ". Response: " + response.body());
        }

        JsonObject responseJson = JsonParser.parseString(response.body()).getAsJsonObject();
        String repairResponse = responseJson.getAsJsonPrimitive("repairResponse").getAsString();
        logger.printVerboseInfo(fileName, "Repair response extracted successfully");

        GeneratedCode repairedCode = new GeneratedCode(extractGeneratedCodeFromResponse(repairResponse),
                generatedCodeMap.functions);
        updateSourceFilesWithGeneratedContent(sourceFiles, repairedCode.codeMap, logger, fileName);
        return repairedCode;
    }

    /**
      * Repairs the code using the Copilot service.
      *
      * @param copilotAccessToken Access token for Copilot service
      * @param sourceFiles Array of source files to include in code repair
      * @param fileAttachmentContents Array of file attachment contents
      * @param packageName Name of the Ballerina package
      * @param generatedPrompt The prompt used for code generation
      * @param generatedCode Map of generated code
      * @param diagnosticsRequest Request object containing diagnostics information
      * @param logger Logger for verbose output
      * @param fileName Name of the file being processed
      *
      * @return The repaired code as a String
      */
    private static GeneratedCode getRepairedCodeFromStream(String copilotAccessToken, JsonArray sourceFiles,
                                     JsonArray fileAttachmentContents, String packageName, String generatedPrompt,
                                     GeneratedCode generatedCode, JsonObject diagnosticsRequest,
                                     VerboseLoggerFactory logger, String fileName)
            throws URISyntaxException, IOException, InterruptedException {
        URI uri = new URI(getCopilotBackendURL() + "/code/repair?isStream=true");

        logger.printVerboseInfo(fileName, "Preparing code repair payload");
        JsonObject codeReparationPayload = constructCodeReparationPayload(generatedPrompt, sourceFiles,
                fileAttachmentContents, packageName, generatedCode.functions, diagnosticsRequest);
        logger.printVerboseInfo(fileName, "Repair payload size: " +
                codeReparationPayload.toString().length() + " characters");

        logger.printVerboseInfo(fileName, "Sending HTTP request to get the repaired code");
        HttpResponse<Stream<String>> response = sendStreamRequestAsync(uri, codeReparationPayload, copilotAccessToken,
                logger, fileName);
        logger.printVerboseInfo(fileName, "Code repair response received");
        logger.printVerboseInfo(fileName, "Response status: " + response.statusCode());

        logger.printVerboseInfo(fileName, "Processing streamed response for code reparation");

        return extractGeneratedCode(response.body(), logger, fileName);
    }

    /**
     * Retrieves the diagnostics for the given source files and module descriptor.
     *
     * @param sourceFiles Array of source files to analyze
     * @param moduleDescriptor Module descriptor for the Ballerina project
     * @param logger Logger for verbose output
     * @param fileName Name of the file being processed
     *
     * @return Optional<JsonArray> containing diagnostics if any errors are found, otherwise empty
     */
    private static Optional<JsonArray> getDiagnostics(JsonArray sourceFiles, ModuleDescriptor moduleDescriptor,
                                                      VerboseLoggerFactory logger, String fileName) throws IOException {

        logger.printVerboseInfo(fileName, "Creating temporary project for diagnostics");
        BuildProject project = createProject(sourceFiles, moduleDescriptor, logger, fileName);

        logger.printVerboseInfo(fileName, "Compiling project for diagnostics");
        PackageCompilation compilation = project.currentPackage().getCompilation();
        DiagnosticResult diagnosticResult = compilation.diagnosticResult();

        logger.printVerboseInfo(fileName, "Diagnostic compilation completed");
        logger.printVerboseInfo(fileName, "Total diagnostics: " + diagnosticResult.diagnostics().size());
        logger.printVerboseInfo(fileName, "Error count: " + diagnosticResult.errorCount());
        logger.printVerboseInfo(fileName, "Warning count: " + diagnosticResult.warningCount());

        if (diagnosticResult.errorCount() == 0) {
            logger.printVerboseInfo(fileName, "No errors found - diagnostics successful");
            return Optional.empty();
        }

        logger.printVerboseInfo(fileName, "Processing error diagnostics");
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

        logger.printVerboseInfo(fileName, "Processed " + errorCount + " error diagnostics");
        return Optional.of(diagnostics);
    }

    /**
     * Converts diagnostics to a JsonArray of message objects.
     *
     * @param diagnostics Optional<JsonArray> containing diagnostics messages
     * @return JsonArray of message objects
     */
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

    /**
     * Constructs the request payload for diagnostics.
     *
     * @param diagnostics JsonArray of diagnostics messages
     * @param generatedCode GeneratedCode object containing code map
     * @return JsonObject representing the diagnostics request
     */
    private static JsonObject getDiagnosticsRequest(JsonArray diagnostics, GeneratedCode generatedCode) {
        JsonObject diagnosticRequest = new JsonObject();
        diagnosticRequest.add("diagnostics", diagnostics);
        diagnosticRequest.addProperty("response", generatedCode.codeMap.toString());
        return diagnosticRequest;
    }

    /**
     * Creates a temporary Ballerina project with the provided source files and module descriptor.
     *
     * @param sourceFiles Array of source files to include in the project
     * @param moduleDescriptor Module descriptor for the Ballerina project
     * @param logger Logger for verbose output
     * @param fileName Name of the file being processed
     * @return BuildProject representing the temporary Ballerina project
     */
    private static BuildProject createProject(JsonArray sourceFiles, ModuleDescriptor moduleDescriptor,
                                              VerboseLoggerFactory logger, String fileName) throws IOException {

        Path tempProjectDir = Files.createTempDirectory(TEMP_DIR_PREFIX + System.currentTimeMillis());
        tempProjectDir.toFile().deleteOnExit();
        logger.printVerboseInfo(fileName, "Created Temporary project directory: " + tempProjectDir.toAbsolutePath());

        logger.printVerboseInfo(fileName, "Writing source files to temporary directory");
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
        logger.printVerboseInfo(fileName, "Total files written: " + fileCount);

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
        logger.printVerboseInfo(fileName, "Ballerina.toml created successfully");

        Path ballerinaHomePath = Path.of(Objects.requireNonNull(getBallerinaHome(logger, fileName)));
        System.setProperty("ballerina.home", ballerinaHomePath.toString());
        logger.printVerboseInfo(fileName, "Ballerina home set to: " + ballerinaHomePath);

        BuildOptions buildOptions = BuildOptions.builder().targetDir(ProjectUtils.getTemporaryTargetPath()).build();
        BuildProject project = BuildProject.load(tempProjectDir, buildOptions);
        logger.printVerboseInfo(fileName, "Temporary project created successfully");

        return project;
    }

    /**
     * Extracts generated code from the response body.
     *
     * @param generatedResponseBody String containing the response body from the Copilot service
     * @return Map<String, String> containing filenames and their corresponding code content
     */
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

    /**
     * Extracts generated code from the streamed response lines.
     *
     * @param lines Stream of response lines from the Copilot service
     * @param logger Logger for verbose output
     * @param fileName Name of the file being processed
     * @return GeneratedCode object containing the extracted code and functions
     */
    private static GeneratedCode extractGeneratedCode(Stream<String> lines, VerboseLoggerFactory logger,
                                                      String fileName) {
        logger.printVerboseInfo(fileName, "Processing streamed response lines");
        String[] linesArr = lines.toArray(String[]::new);
        int length = linesArr.length;

        if (length == 1) {
            JsonObject jsonObject = JsonParser.parseString(linesArr[0]).getAsJsonObject();
            if (jsonObject.has("error_message")) {
                String errorMsg = jsonObject.get("error_message").getAsString();
                logger.printVerboseError(fileName, errorMsg);
                throw new RuntimeException(errorMsg);
            }
        }

        StringBuilder responseBody = new StringBuilder();
        JsonArray functions = null;
        int contentBlocks = 0;
        int functionBlocks = 0;

        logger.printVerboseInfo(fileName, "Parsing response stream");
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

        logger.printVerboseInfo(fileName, "Response parsing completed");
        logger.printVerboseInfo(fileName, "Content blocks processed: " + contentBlocks);
        logger.printVerboseInfo(fileName, "Function blocks processed: " + functionBlocks);

        String responseBodyString = responseBody.toString();
        Map<String, String> codeMap = extractGeneratedCodeFromResponse(responseBodyString);

        logger.printVerboseInfo(fileName, "Code extraction completed");
        logger.printVerboseInfo(fileName, "Extracted files: " + codeMap.size());

        return new GeneratedCode(codeMap, functions);
    }

    /**
     * Checks if the response body contains Ballerina code snippets.
     *
     * @param responseBodyString String containing the response body
     * @return boolean indicating whether Ballerina code snippets are present
     */
    private static boolean hasBallerinaCodeSnippet(String responseBodyString) {
        return responseBodyString.contains(TRIPLE_BACKTICK_BALLERINA) && responseBodyString.contains(TRIPLE_BACKTICK);
    }

    /**
     * Represents the generated code and functions from the code generation process.
     *
     * @param codeMap
     * @param functions
     */
    private record GeneratedCode(Map<String, String> codeMap, JsonArray functions) { }

    /**
     * Updates the source files with the generated content from the code generation process.
     *
     * @param sourceFiles Array of source files to update
     * @param generatedCodeMap Map containing the generated code content
     */
    private static void updateSourceFilesWithGeneratedContent(JsonArray sourceFiles,
                                                              Map<String, String> generatedCodeMap,
                                                              VerboseLoggerFactory logger, String fileName) {
        for (String sourceFileName: generatedCodeMap.keySet()) {
            for (JsonElement sourceFile: sourceFiles) {
                JsonObject sourceFileObj = sourceFile.getAsJsonObject();
                if (sourceFileObj.get(FILE_PATH).getAsString().endsWith(sourceFileName)) {
                    sourceFileObj.addProperty(CONTENT, generatedCodeMap.get(sourceFileName));
                    break;
                }
            }
        }
        logger.printVerboseInfo(fileName, "Source files updated with generated content: " +
                generatedCodeMap.size() + " files");
    }

    /**
     * Constructs the user prompt for migrating a Logic App to Ballerina integration.
     *
     * @param additionalInstructions Additional instructions for the migration
     * @param executionPlan Execution plan for the Logic App migration
     * @return String containing the constructed user prompt
     */
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

    /**
     * Constructs the payload for execution plan generation.
     *
     * @param additionalInstructions Additional instructions for execution plan generation
     * @param sourceFiles Array of source files to include in execution plan generation
     * @param fileAttachmentContents Array of file attachment contents
     * @param packageName Name of the Ballerina package
     *
     * @return JsonObject containing the execution plan generation payload
     */
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

    /**
     * Constructs the payload for code generation.
     *
     * @param generatedPrompt The prompt used for code generation
     * @param sourceFiles Array of source files to include in code generation
     * @param fileAttachmentContents Array of file attachment contents
     * @param packageName Name of the Ballerina package
     *
     * @return JsonObject containing the code generation payload
     */
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

    /**
     * Constructs the payload for code reparation.
     *
     * @param generatedPrompt The prompt used for code generation
     * @param sourceFiles Array of source files to include in code reparation
     * @param fileAttachmentContents Array of file attachment contents
     * @param packageName Name of the Ballerina package
     * @param functions Array of functions to include in code reparation
     * @param diagnosticsRequest Request object containing diagnostics information
     *
     * @return JsonObject containing the code reparation payload
     */
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

    /**
     * Returns a configured HttpClient with a 5-minute connection timeout.
     *
     * @return Configured HttpClient instance
     */
    private static HttpClient getHttpClient() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofMinutes(5))
                .executor(Executors.newCachedThreadPool())
                .build();
    }

    private static HttpResponse<Stream<String>> sendStreamRequestAsync(URI uri, JsonObject payload, String accessToken,
                                                                       VerboseLoggerFactory logger, String fileName)
            throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Authorization", "Bearer " + accessToken)
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

    private static HttpResponse<String> sendRequestAsync(URI uri, JsonObject payload, String accessToken,
                                                         VerboseLoggerFactory logger, String fileName)
            throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
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

    /**
     * Creates a JsonArray containing file attachment contents.
     *
     * @param fileName Name of the file
     * @param content Content of the file
     *
     * @return JsonArray containing the file attachment contents
     */
    private static JsonArray getFileAttachmentContents(String fileName, String content) {
        JsonArray fileAttachmentContents = new JsonArray();
        JsonObject fileObj = new JsonObject();
        fileObj.addProperty("fileName", fileName);
        fileObj.addProperty("content", content);
        fileAttachmentContents.add(fileObj);
        return fileAttachmentContents;
    }

    /**
     * Creates a default source files array for a Ballerina project.
     *
     * @return JsonArray containing default source files
     */
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
     * Retrieves the Ballerina home directory by executing the 'bal home' command.
     *
     * @param logger Logger for verbose output
     * @param fileName Name of the file being processed
     *
     * @return The Ballerina home directory as a String, or null if it cannot be determined
     */
    private static String getBallerinaHome(VerboseLoggerFactory logger, String fileName) {
        logger.printVerboseInfo(fileName, "Determining Ballerina home directory...");
        ProcessBuilder processBuilder = new ProcessBuilder("bal", "home");
        processBuilder.redirectErrorStream(true);

        try {
            logger.printVerboseInfo(fileName, "Executing 'bal home' command...");
            Process process = processBuilder.start();
            boolean finished = process.waitFor(5, TimeUnit.SECONDS);
            if (!finished) {
                logger.printVerboseError(fileName, "Command timed out, destroying process");
                process.destroyForcibly();
                logger.printError(fileName, "Ballerina home cannot be determined: 'bal home' command timed out.");
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
                logger.printVerboseError(fileName, "Command failed with exit code: " + process.exitValue());
                logger.printError(fileName, "Ballerina home cannot be determined: bal home command failed.");
            }

        } catch (IOException e) {
            String errorMsg = "Error executing 'bal home' command: " + e.getMessage();
            logger.printError(fileName, errorMsg);
            logger.printStackTrace(fileName, e.getStackTrace());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            String errorMsg = "Command 'bal home' was interrupted";
            logger.printError(fileName, errorMsg);
            logger.printStackTrace(fileName, e.getStackTrace());
        }

        return null;
    }

    /**
     * Returns the Copilot backend URL based on the environment.
     *
     * @return Copilot backend URL
     */
    private static String getCopilotBackendURL() {
        return BALLERINA_DEV_UPDATE ? DEV_COPILOT_BACKEND_URL : COPILOT_BACKEND_URL;
    }
}
