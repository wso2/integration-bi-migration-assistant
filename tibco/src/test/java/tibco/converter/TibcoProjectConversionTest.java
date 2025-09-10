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

package tibco.converter;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import tibco.ConversionContext;
import tibco.ProjectConversionContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class TibcoProjectConversionTest {

    @Test(groups = {"tibco", "converter"}, dataProvider = "projectTestCaseProvider")
    public void testProjectConversion(Path tibcoProject, Path expectedBallerinaProject) throws Exception {
        // Create a temporary directory for the output
        Path tempDir = Files.createTempDirectory("tibco-conversion-test");
        // Create a ProjectConversionContext with verbose logger for test
        ProjectConversionContext cx = TestUtils.createTestProjectConversionContext(
                "testOrg", expectedBallerinaProject.getFileName().toString());
        try {
            if ("true".equalsIgnoreCase(System.getenv("BLESS"))) {
                TibcoConverter.migrateTibcoProject(cx, tibcoProject.toString(), expectedBallerinaProject.toString()
                );
            }
            // Run the conversion
            TibcoConverter.migrateTibcoProject(cx, tibcoProject.toString(), tempDir.toString());

            // Compare the directories
            TestUtils.compareDirectories(tempDir, expectedBallerinaProject);
        } finally {
            // Clean up temporary directory
            TestUtils.deleteDirectory(tempDir);
        }
    }

    @Test(groups = {"tibco", "converter"}, dataProvider = "projectTestCaseProvider")
    public void testProjectConversionByAPI(Path tibcoProject, Path expectedBallerinaProject) throws IOException {
        // Create parameter map for the API
        java.util.Map<String, Object> parameters = java.util.Map.of(
                "orgName", "testOrg",
                "projectName", expectedBallerinaProject.getFileName().toString(),
                "sourcePath", tibcoProject.toString(),
                "stateCallback", (java.util.function.Consumer<String>) s -> {
                },
                "logCallback", (java.util.function.Consumer<String>) s -> {
                }
        );

        // Run the conversion using the new public API
        var result = tibco.TibcoToBalConverter.migrateTIBCO(parameters);
        Assert.assertNotNull(result, "migrateTIBCO returned null");
        Assert.assertFalse(result.containsKey("error"), "Conversion failed with error: " + result.get("error"));
        Assert.assertTrue(result.containsKey("textEdits"), "Result does not contain 'textEdits'");
        Assert.assertTrue(result.containsKey("report-json"), "Result does not contain 'report-json'");
        @SuppressWarnings("unchecked")
        var textEdits = (java.util.Map<String, String>) result.get("textEdits");
        Assert.assertNotNull(textEdits, "textEdits is null");

        // Validate the report-json against expected JSON file
        String reportJson = (String) result.get("report-json");
        Assert.assertNotNull(reportJson, "report-json is null");
        Assert.assertFalse(reportJson.isBlank(), "report-json should not be empty");

        Path expectedJsonFile = Path.of("src/test/resources/tibco.projects.converted")
                .resolve(expectedBallerinaProject.getFileName())
                .resolve("expected-report.json");

        if ("true".equalsIgnoreCase(System.getenv("BLESS"))) {
            // Create parent directory if it doesn't exist
            Files.createDirectories(expectedJsonFile.getParent());
            // Write the generated JSON to expected file
            Files.writeString(expectedJsonFile, reportJson);
            
            // Write all textEdits to the expected directory (like testProjectConversion does)
            for (Map.Entry<String, String> entry : textEdits.entrySet()) {
                Path filePath = expectedBallerinaProject.resolve(entry.getKey());
                Files.createDirectories(filePath.getParent());
                Files.writeString(filePath, entry.getValue());
            }
        }

        Assert.assertTrue(Files.exists(expectedJsonFile),
                "Expected JSON file must exist for project: " + expectedBallerinaProject.getFileName() +
                        " at path: " + expectedJsonFile);

        String expectedJson = Files.readString(expectedJsonFile);
        // Normalize whitespace for comparison by removing all whitespace and comparing structure
        String normalizedReportJson = reportJson.replaceAll("\\s+", "");
        String normalizedExpectedJson = expectedJson.replaceAll("\\s+", "");
        Assert.assertEquals(normalizedReportJson, normalizedExpectedJson,
                "Report JSON does not match expected JSON for project: " + expectedBallerinaProject.getFileName());

        // Collect expected .bal files (except types.bal)
        try (Stream<Path> expectedFiles = Files.walk(expectedBallerinaProject)) {
            var expectedPaths = expectedFiles
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".bal") || path.toString().endsWith(".toml"))
                    .map(expectedBallerinaProject::relativize)
                    .toList();

            // Check all expected .bal files exist in textEdits and match content
            for (Path relativePath : expectedPaths) {
                if (relativePath.endsWith("types.bal")) {
                    continue;
                }
                String key = relativePath.toString().replace('\\', '/');
                Assert.assertTrue(textEdits.containsKey(key), "Missing .bal file in textEdits: " + key);
                String actualContent = textEdits.get(key);
                String expectedContent = Files.readString(expectedBallerinaProject.resolve(relativePath));
                Assert.assertEquals(actualContent, expectedContent,
                        "File contents do not match for: " + key);
            }

            // Check for extra .bal files in textEdits
            for (String key : textEdits.keySet()) {
                if (key.endsWith("types.bal")) {
                    continue;
                }
                Path relPath = Path.of(key);
                if (!expectedPaths.contains(relPath)) {
                    Assert.fail("Extra .bal file in textEdits: " + key);
                }
            }
        }
    }

    @Test(groups = {"tibco", "converter"})
    public void testMultiRootConversion() throws IOException {
        // Create temporary directories for input and output
        Path tempInputDir = Files.createTempDirectory("tibco-multiroot-input");
        Path tempOutputDir = Files.createTempDirectory("tibco-multiroot-output");

        // Setup multi-root source directory structure
        Path multiRootSource = Path.of("src", "test", "resources", "multi-root");
        Path expectedMultiRootOutput = Path.of("src", "test", "resources", "multi-root-converted");

        try {
            // Copy source structure to temp directory if needed for modification
            // For this test, we'll use the existing test resources directly

            // Create test conversion context
            ConversionContext cx = TestUtils.createTestConversionContext("testOrg", false, false);

            if ("true".equalsIgnoreCase(System.getenv("BLESS"))) {
                // Update expected results
                TibcoConverter.migrateTibcoMultiRoot(cx, multiRootSource, expectedMultiRootOutput.toString(),
                        Optional.empty());
            }

            // Run multi-root conversion
            TibcoConverter.migrateTibcoMultiRoot(cx, multiRootSource, tempOutputDir.toString(), Optional.empty()
            );

            // Verify the structure - should have two converted projects
            Assert.assertTrue(Files.exists(tempOutputDir.resolve("helloWorld_converted")),
                    "helloWorld_converted directory should exist");
            Assert.assertTrue(Files.exists(tempOutputDir.resolve("lib_converted")),
                    "lib_converted directory should exist");
            Assert.assertTrue(Files.exists(tempOutputDir.resolve("combined_summary_report.html")),
                    "Combined summary report should exist");

            // Compare each converted project with expected results
            TestUtils.compareDirectories(tempOutputDir.resolve("helloWorld_converted"),
                    expectedMultiRootOutput.resolve("helloWorld_converted"));
            TestUtils.compareDirectories(tempOutputDir.resolve("lib_converted"),
                    expectedMultiRootOutput.resolve("lib_converted"));

            // Verify combined summary report exists (content comparison skipped as it may vary)
            Assert.assertTrue(Files.exists(tempOutputDir.resolve("combined_summary_report.html")));

        } finally {
            // Clean up temporary directories
            TestUtils.deleteDirectory(tempInputDir);
            TestUtils.deleteDirectory(tempOutputDir);
        }
    }

    @Test(groups = {"tibco", "converter"})
    public void testMigrateTIBCOAPIErrorHandling() {
        // Test missing parameter
        Map<String, Object> emptyParams = new HashMap<>();
        var result1 = tibco.TibcoToBalConverter.migrateTIBCO(emptyParams);
        Assert.assertTrue(result1.containsKey("error"), "Should return error for missing parameters");
        Assert.assertTrue(result1.get("error").toString().contains("Missing required parameter"),
                "Error message should mention missing parameter");

        // Test wrong type parameter
        Map<String, Object> wrongTypeParams = new HashMap<>();
        wrongTypeParams.put("orgName", 123); // Should be String, not Integer
        var result2 = tibco.TibcoToBalConverter.migrateTIBCO(wrongTypeParams);
        Assert.assertTrue(result2.containsKey("error"), "Should return error for wrong parameter type");
        Assert.assertTrue(result2.get("error").toString().contains("must be a String"),
                "Error message should mention type mismatch");

        // Test missing some parameters
        Map<String, Object> partialParams = new HashMap<>();
        partialParams.put("orgName", "testOrg");
        partialParams.put("projectName", "testProject");
        // Missing sourcePath, stateCallback, logCallback
        var result3 = tibco.TibcoToBalConverter.migrateTIBCO(partialParams);
        Assert.assertTrue(result3.containsKey("error"), "Should return error for missing parameters");
    }

    @DataProvider
    public Object[][] projectTestCaseProvider() throws IOException {
        Path projectTestCaseDir = Path.of("src", "test", "resources", "tibco.projects");
        Path expectedConvertedResultsDir = Path.of("src", "test", "resources", "tibco.projects.converted");

        // Get only the immediate directories (non-recursive)
        return Files.list(projectTestCaseDir)
                .filter(Files::isDirectory)
                .map(dir -> new Object[]{
                        dir,
                        expectedConvertedResultsDir.resolve(dir.getFileName())
                })
                .toArray(Object[][]::new);
    }
}
