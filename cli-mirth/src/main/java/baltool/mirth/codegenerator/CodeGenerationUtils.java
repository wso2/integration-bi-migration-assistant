package baltool.mirth.codegenerator;

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
import org.xml.sax.InputSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.stream.Collectors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
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
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static baltool.mirth.Constants.BALLERINA_TOML_FILE;
import static baltool.mirth.Constants.CONTENT;
import static baltool.mirth.Constants.COPILOT_BACKEND_URL;
import static baltool.mirth.Constants.DEV_COPILOT_BACKEND_URL;
import static baltool.mirth.Constants.FILE_PATH;
import static baltool.mirth.Constants.MAXIMUM_RETRY_COUNT;
import static baltool.mirth.codegenerator.HttpUtils.getHttpClient;
import static baltool.mirth.codegenerator.HttpUtils.sendRequestAsync;
import static baltool.mirth.codegenerator.HttpUtils.sendStreamRequestAsync;

public class CodeGenerationUtils {

    public static final boolean BALLERINA_DEV_UPDATE = Boolean.parseBoolean(
            System.getenv("BALLERINA_DEV_UPDATE"));
    private static final String TEMP_DIR_PREFIX = "logicapps-migration-tool-codegen-diagnostics-dir-";

    private record GeneratedCode(Map<String, String> codeMap, JsonArray functions) {
    }

    public static String generateMirthChannelExecPlan(String channelXmlContent) {
        // Placeholder implementation

        return "Execution plan for the provided Mirth channel XML content.";
    }

    public static JsonArray generateCodeForMirthChannel(String copilotAccessToken, Path mirthChannelFilePath,
                                                        String packageName, String additionalInstructions,
                                                        ModuleDescriptor moduleDescriptor, VerboseLoggerFactory logger,
                                                        String fileName) {
        try {
            String mirthChannelContent = Files.readString(mirthChannelFilePath, StandardCharsets.UTF_8);

            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

                // Enable additional validation features here, if needed
                DocumentBuilder builder = factory.newDocumentBuilder();

                // Parse XML from string
                builder.parse(new InputSource(new StringReader(mirthChannelContent)));
                logger.printVerboseInfo(fileName, "XML validation: SUCCESS");
            } catch (Exception e) {
                logger.printVerboseError(fileName, "XML validation: FAILED - " + e.getMessage());
                logger.printStackTrace(fileName, e.getStackTrace());
                throw new RuntimeException("Invalid XML content in Mirth channel file", e);
            }

            JsonArray fileAttachmentContents = getFileAttachmentContents(mirthChannelFilePath.getFileName().toString(),
                    mirthChannelContent);
            JsonArray sourceFiles = createSourceFilesArray();
            logger.printVerboseInfo(fileName, "Source files structure created: " + sourceFiles.size() + " files");

            logger.printInfo(fileName, "Starting Ballerina code generation for Logic App file: " +
                    mirthChannelFilePath.getFileName());

            // Step 1: Generate execution plan
            logger.startProgress(fileName, "Generating execution plan");
            String executionPlan = CodeGenerationUtils.generateMirthChannelExecutionPlan(copilotAccessToken, sourceFiles,
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

        for (String fileName : ballerinaProjectFileList) {
            JsonObject sourceFileObj = new JsonObject();
            sourceFileObj.addProperty(FILE_PATH, fileName);
            sourceFileObj.addProperty(CONTENT, "");
            sourceFilesArray.add(sourceFileObj);
        }

        return sourceFilesArray;
    }

    public static String generateMirthChannelExecutionPlan(String copilotAccessToken, JsonArray sourceFiles,
                                                           JsonArray fileAttachmentContents, String packageName,
                                                           String additionalInstructions, VerboseLoggerFactory logger,
                                                           String fileName)
            throws URISyntaxException, IOException, InterruptedException {

        //Replace this with Mirth Channel specific execution plan generation endpoint
        URI uri = new URI(getCopilotBackendURL() + "/mirthchannel/executionplan");

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

    private static String getCopilotBackendURL() {
        return BALLERINA_DEV_UPDATE ? DEV_COPILOT_BACKEND_URL : COPILOT_BACKEND_URL;
    }

    private static String constructMigrateUserPrompt(String additionalInstructions, String executionPlan) throws IOException {
        StringBuilder prompt = new StringBuilder();

        prompt.append("# Role\n")
                .append("You are an expert in migrating Mirth Connect Channel to Ballerina integration ")
                .append("with given channel xml file.\n\n");

        if (executionPlan != null && !executionPlan.isEmpty()) {
            prompt.append(executionPlan).append("\n\n");
        }

        prompt.append("# Additional Instructions\n");
        if (additionalInstructions != null && !additionalInstructions.isEmpty()) {
            prompt.append(additionalInstructions).append("\n\n");
        }
        // Append the detailed instructions from the resource file
        String instructions = Optional.ofNullable(CodeGenerationUtils.class.getResourceAsStream("/mirth_ballerina_mappings.md"))
                .map(inputStream -> new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                        .lines()
                        .collect(Collectors.joining(System.lineSeparator())))
                .orElseThrow(() -> new RuntimeException("Failed to load instructions_v2.md from resources"));
        prompt.append(instructions);
        return prompt.toString();
    }

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
                .header("User-Agent", "PostmanRuntime/7.32.3")
                .POST(HttpRequest.BodyPublishers.ofString(codeGenerationPayload.toString()))
                .timeout(Duration.ofMinutes(10))
                .build();

        logger.printVerboseInfo(fileName, "Sending HTTP request to get generated code");
        HttpResponse<InputStream> response = getHttpClient().send(codeGenerationRequest,
                HttpResponse.BodyHandlers.ofInputStream());

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
    private static GeneratedCode extractGeneratedCode(InputStream lines, VerboseLoggerFactory logger,
                                                      String fileName) {
        logger.printVerboseInfo(fileName, "Processing streamed response lines");
        StringBuilder responseContent = new StringBuilder();
        long totalBytesRead = 0;

        try (InputStream inputStream = lines;
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String line;
            while (true) {
                try {
                    line = reader.readLine();
                    if (line == null) {
                        break; // normal end of stream
                    }
                } catch (IOException e) {
                    // treat premature close as end-of-stream
                    logger.printVerboseInfo(fileName, "Stream ended unexpectedly, treating as EOF");
                    System.out.println("Stream ended unexpectedly, treating as EOF");
                    break;
                }

                responseContent.append(line).append(System.lineSeparator());
                totalBytesRead += line.getBytes(StandardCharsets.UTF_8).length;

                if (totalBytesRead % (1024 * 1024) == 0) {
                    logger.printVerboseInfo(fileName,
                            "Read " + (totalBytesRead / 1024 / 1024) + " MB so far...");
                }
            }
        } catch (IOException e) {
            logger.printVerboseError(fileName, "Error reading response stream: " + e.getMessage());
        }

        logger.printVerboseInfo(fileName,
                "Successfully read " + totalBytesRead + " bytes from response");

        String[] linesArr = responseContent.toString().split("\\r?\\n");
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

    private static void updateSourceFilesWithGeneratedContent(JsonArray sourceFiles,
                                                              Map<String, String> generatedCodeMap,
                                                              VerboseLoggerFactory logger, String fileName) {
        for (String sourceFileName : generatedCodeMap.keySet()) {
            for (JsonElement sourceFile : sourceFiles) {
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
}
