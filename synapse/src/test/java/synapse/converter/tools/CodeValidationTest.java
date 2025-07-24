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

import common.LoggingUtils;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import synapse.converter.ConversionContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

class TestConversionContextWithTarget extends ConversionContext {

    public TestConversionContextWithTarget(String projectPath, String targetPath) {
        super(projectPath, targetPath);
    }

    @Override
    public void log(LoggingUtils.Level level, String message) {
        // No-op for testing
    }

    @Override
    public void logState(String message) {
        // No-op for testing
    }
}

public class CodeValidationTest {

    private Path tempDir;
    private CodeValidation codeValidation;

    @BeforeMethod
    public void setUp() throws IOException {
        tempDir = Files.createTempDirectory("codevalidation-test");
        codeValidation = new CodeValidation();
    }

    @AfterMethod
    public void tearDown() throws IOException {
        if (tempDir != null && Files.exists(tempDir)) {
            Files.walk(tempDir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            // Ignore deletion errors in tests
                        }
                    });
        }
    }

    @Test
    public void testToolMetadata() {
        Assert.assertEquals(codeValidation.name(), "validate_code", "Tool name should be 'validate_code'");
        Assert.assertEquals(codeValidation.description(), "Validate generated Ballerina code",
                "Tool description should match expected value");

        String inputSchema = codeValidation.inputSchema();
        Assert.assertTrue(inputSchema.contains("\"properties\""), "Input schema should have properties");
        Assert.assertTrue(inputSchema.contains("{}"), "Input schema should have empty properties");
    }

    @Test
    public void testValidateCodeWithValidTargetPath() throws IOException {
        // Create a valid Ballerina project structure
        Path projectDir = tempDir.resolve("valid-project");
        Files.createDirectories(projectDir);

        // Create Ballerina.toml
        Files.writeString(projectDir.resolve("Ballerina.toml"),
                """
                        [package]
                        org = "testorg"
                        name = "test_project"
                        version = "1.0.0"
                        """);

        // Create main.bal with valid content
        Files.writeString(projectDir.resolve("main.bal"),
                """
                        import ballerina/io;

                        public function main() {
                            io:println("Hello World");
                        }
                        """);

        TestConversionContextWithTarget conversionContext =
                new TestConversionContextWithTarget("", projectDir.toString());
        ToolContext toolContext = new ToolContext(conversionContext);

        String request = "{}";

        String result = codeValidation.execute(toolContext, request);

        // Should successfully validate valid Ballerina code
        Assert.assertTrue(result.contains("Success"), "Should return success for valid Ballerina code");
    }

    @Test
    public void testValidateCodeWithMissingFiles() throws IOException {
        // Create empty directory without Ballerina.toml or main.bal
        Path projectDir = tempDir.resolve("empty-project");
        Files.createDirectories(projectDir);

        TestConversionContextWithTarget conversionContext =
                new TestConversionContextWithTarget("", projectDir.toString());
        ToolContext toolContext = new ToolContext(conversionContext);

        String request = "{}";

        String result = codeValidation.execute(toolContext, request);

        // Should successfully initialize and validate empty project
        Assert.assertTrue(result.contains("Success"), "Should initialize project and return success");
    }

    @Test
    public void testValidateCodeWithInvalidBalCode() throws IOException {
        // Create project with invalid Ballerina code
        Path projectDir = tempDir.resolve("invalid-project");
        Files.createDirectories(projectDir);

        // Create Ballerina.toml
        Files.writeString(projectDir.resolve("Ballerina.toml"),
                """
                        [package]
                        org = "testorg"
                        name = "test_project"
                        version = "1.0.0"
                        """);

        // Create main.bal with invalid syntax
        Files.writeString(projectDir.resolve("main.bal"),
                """
                        import ballerina/io;

                        public function main() {
                            invalid syntax here!!!
                        }
                        """);

        TestConversionContextWithTarget conversionContext =
                new TestConversionContextWithTarget("", projectDir.toString());
        ToolContext toolContext = new ToolContext(conversionContext);

        String request = "{}";

        String result = codeValidation.execute(toolContext, request);

        // Should detect compilation errors
        Assert.assertEquals(result.trim(), """
                Build failed with compilation errors:
                ERROR [main.bal:(4:5,4:12)] unknown type 'invalid'
                ERROR [main.bal:(4:20,4:20)] missing equal token
                ERROR [main.bal:(4:20,4:24)] undefined symbol 'here'
                ERROR [main.bal:(4:24,4:27)] invalid expression statement
                ERROR [main.bal:(4:24,4:24)] missing semicolon token
                ERROR [main.bal:(5:1,5:1)] missing expression
                ERROR [main.bal:(5:1,5:1)] missing semicolon token
                error: compilation contains errors
                """.trim());
    }

    @Test
    public void testValidateCodeWithNonExistentPath() {
        TestConversionContextWithTarget conversionContext =
                new TestConversionContextWithTarget("", "/non/existent/path");
        ToolContext toolContext = new ToolContext(conversionContext);

        String request = "{}";

        String result = codeValidation.execute(toolContext, request);

        Assert.assertTrue(result.equals("Validation ignored"),
                "Should ignore validation for non-existent directory");
    }

    @Test
    public void testValidateCodeWithFileAsPath() throws IOException {
        // Create a file instead of directory
        Path file = tempDir.resolve("not-a-directory.txt");
        Files.writeString(file, "This is a file, not a directory");

        TestConversionContextWithTarget conversionContext = new TestConversionContextWithTarget("", file.toString());
        ToolContext toolContext = new ToolContext(conversionContext);

        String request = "{}";

        String result = codeValidation.execute(toolContext, request);

        Assert.assertTrue(result.equals("Validation ignored"),
                "Should ignore validation when path is not a directory");
    }

    @Test
    public void testValidateCodeWithEmptyTargetPath() {
        TestConversionContextWithTarget conversionContext = new TestConversionContextWithTarget("", "");
        ToolContext toolContext = new ToolContext(conversionContext);

        String request = "{}";

        String result = codeValidation.execute(toolContext, request);

        Assert.assertTrue(result.startsWith("Error: No target path available in context"),
                "Should indicate empty target path error");
    }

    @Test
    public void testValidateCodeWithWhitespaceTargetPath() {
        TestConversionContextWithTarget conversionContext = new TestConversionContextWithTarget("", "   ");
        ToolContext toolContext = new ToolContext(conversionContext);

        String request = "{}";

        String result = codeValidation.execute(toolContext, request);

        Assert.assertTrue(result.startsWith("Error: No target path available in context"),
                "Should indicate whitespace-only target path error");
    }

    @Test
    public void testValidateCodeWithNoTargetPathAndNoContext() {
        TestConversionContextWithTarget conversionContext = new TestConversionContextWithTarget("", null);
        ToolContext toolContext = new ToolContext(conversionContext);

        String request = "{}";

        String result = codeValidation.execute(toolContext, request);

        Assert.assertTrue(result.startsWith("Error: No target path available in context"),
                "Should indicate no target path available");
    }

    @Test
    public void testValidateCodeWithOnlyMainBal() throws IOException {
        // Create project with only main.bal, missing Ballerina.toml
        Path projectDir = tempDir.resolve("main-only-project");
        Files.createDirectories(projectDir);

        Files.writeString(projectDir.resolve("main.bal"),
                """
                        import ballerina/io;

                        public function main() {
                            io:println("Hello World");
                        }
                        """);

        TestConversionContextWithTarget conversionContext =
                new TestConversionContextWithTarget("", projectDir.toString());
        ToolContext toolContext = new ToolContext(conversionContext);

        String request = "{}";

        String result = codeValidation.execute(toolContext, request);

        // Should successfully initialize and validate project with only main.bal
        Assert.assertTrue(result.contains("Success"), "Should initialize and validate project with only main.bal");
    }

    @Test
    public void testInputSchemaStructure() {
        String schema = codeValidation.inputSchema();

        Assert.assertTrue(schema.contains("\"type\": \"object\""), "Schema should define object type");
        Assert.assertTrue(schema.contains("\"properties\""), "Schema should have properties");
        Assert.assertTrue(schema.contains("{}"), "Schema should have empty properties object");
    }

}
