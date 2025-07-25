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

package synapse.converter.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static common.LoggingUtils.Level.DEBUG;
import static common.LoggingUtils.Level.INFO;

public class CodeValidation implements SynapseConversionTool {

    private record ProcessResult(String output, String error, int exitCode, boolean timedOut) {

    }

    @Override
    public String name() {
        return "validate_code";
    }

    @Override
    public String description() {
        return "Validate generated Ballerina code";
    }

    @Override
    public String inputSchema() {
        return """
                {
                    "type": "object",
                    "properties": {}
                }
                """;
    }

    @Override
    public String execute(ToolContext cx, String request) {
        String targetPath = cx.targetPath();
        if (targetPath == null || targetPath.trim().isEmpty()) {
            return "Error: No target path available in context";
        }

        cx.log(INFO, "Validating generated code");

        Path targetDir = Paths.get(targetPath);
        // I don't think any of these things can happen
        try {
            if (!Files.exists(targetDir)) {
                cx.log(DEBUG, "Target directory does not exist: " + targetPath);
                return "Validation ignored";
            }

            if (!Files.isDirectory(targetDir)) {
                cx.log(DEBUG, "Target path is not a directory: " + targetPath);
                return "Validation ignored";
            }
        } catch (Exception e) {
            cx.log(DEBUG, "Exception while accessing target directory: " + e.getMessage());
            return "Validation ignored";
        }

        Path ballerinaToml = targetDir.resolve("Ballerina.toml");

        if (!Files.exists(ballerinaToml)) {
            cx.log(INFO, "Initializing Ballerina project");
            String initResult = initializeBallerinaProject(targetPath, cx);
            if (initResult.startsWith("Error:")) {
                return initResult;
            }
        } else {
            cx.log(INFO, "Ballerina project already exists, skipping initialization");
        }

        return validateBallerinaCode(targetPath, cx);
    }

    private String initializeBallerinaProject(String targetPath, ToolContext cx) {
        try {
            cx.log(INFO, "Initializing new Ballerina project");
            ProcessResult result = executeProcess("bal", "new", targetPath);

            if (result.timedOut()) {
                cx.log(DEBUG, "bal new command timed out");
                return "Error: bal new command timed out";
            }

            if (result.exitCode() != 0) {
                cx.log(DEBUG, "bal new failed with exit code: " + result.exitCode() + ", output: " + result.output());
                return "Error: Failed to initialize Ballerina project. Exit code: " + result.exitCode()
                        + ", error: " + result.error();
            }

            cx.log(INFO, "Successfully initialized Ballerina project");
            return "Success: Ballerina project initialized";

        } catch (Exception e) {
            cx.log(DEBUG, "Exception during bal new: " + e.getMessage());
            return "Error: Failed to execute bal new command - " + e.getMessage();
        }
    }

    private String validateBallerinaCode(String targetPath, ToolContext cx) {
        try {
            cx.log(INFO, "Running bal build to validate code");
            ProcessResult result = executeProcess("bal", "build", targetPath);

            if (result.timedOut()) {
                cx.log(DEBUG, "bal build command timed out");
                return "Error: bal build command timed out";
            }

            if (result.exitCode() == 0 && result.error().isEmpty()) {
                cx.log(INFO, "Code validation successful");
                return "Success: Code builds successfully without errors";
            } else {
                cx.log(DEBUG, "Build failed with exit code: " + result.exitCode() + ", errors: " + result.error());
                if (!result.error().isEmpty()) {
                    return "Build failed with compilation errors:\n" + result.error();
                } else {
                    return "Build failed with exit code: " + result.exitCode() + "\nOutput: " + result.output().trim();
                }
            }

        } catch (Exception e) {
            cx.log(DEBUG, "Exception during bal build: " + e.getMessage());
            return "Error: Failed to execute bal build command - " + e.getMessage();
        }
    }

    private ProcessResult executeProcess(String... command) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(false);
        Process process = pb.start();

        StringBuilder errorOutput = new StringBuilder();
        try (BufferedReader errorReader = new BufferedReader(
                new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = errorReader.readLine()) != null) {
                errorOutput.append(line).append("\n");
            }
        }

        StringBuilder standardOutput = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                standardOutput.append(line).append("\n");
            }
        }

        boolean finished = process.waitFor(5, TimeUnit.MINUTES);
        if (!finished) {
            process.destroyForcibly();
            return new ProcessResult(standardOutput.toString(), errorOutput.toString(), -1, true);
        }

        int exitCode = process.exitValue();
        return new ProcessResult(standardOutput.toString(), errorOutput.toString(), exitCode, false);
    }
}
