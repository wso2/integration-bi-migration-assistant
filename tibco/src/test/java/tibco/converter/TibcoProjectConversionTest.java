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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import tibco.ConversionContext;
import tibco.ProjectConversionContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class TibcoProjectConversionTest {

    /**
     * Recursively compares two JSON Maps, handling nested Maps, Lists, and
     * primitive values.
     * Throws AssertionError with detailed differences if maps don't match.
     *
     * @param actual   The actual Map to compare
     * @param expected The expected Map to compare against
     * @param path     The current path in the JSON structure (for error messages)
     */
    private void compareJsonMaps(Map<String, Object> actual, Map<String, Object> expected, String path) {
        List<String> differences = new ArrayList<>();

        // Check all keys in expected map
        for (Map.Entry<String, Object> expectedEntry : expected.entrySet()) {
            String key = expectedEntry.getKey();
            String currentPath = path.isEmpty() ? key : path + "." + key;

            if (!actual.containsKey(key)) {
                differences.add("Missing key: " + currentPath);
                continue;
            }

            Object actualValue = actual.get(key);
            Object expectedValue = expectedEntry.getValue();
            compareValues(actualValue, expectedValue, currentPath, differences);
        }

        // Check for extra keys in actual map
        for (String key : actual.keySet()) {
            if (!expected.containsKey(key)) {
                String currentPath = path.isEmpty() ? key : path + "." + key;
                differences.add("Unexpected key: " + currentPath);
            }
        }

        if (!differences.isEmpty()) {
            Assert.fail("JSON comparison failed:\n" + String.join("\n", differences));
        }
    }

    /**
     * Recursively compares two values, handling Maps, Lists, and primitives.
     */
    @SuppressWarnings("unchecked")
    private void compareValues(Object actual, Object expected, String path, List<String> differences) {
        // Handle null values
        if (actual == null && expected == null) {
            return;
        }
        if (actual == null || expected == null) {
            differences.add("Value mismatch at " + path + ": expected " + expected + ", found " + actual);
            return;
        }

        // Handle Maps
        if (actual instanceof Map && expected instanceof Map) {
            compareJsonMaps((Map<String, Object>) actual, (Map<String, Object>) expected, path);
            return;
        }

        // Handle Lists
        if (actual instanceof List && expected instanceof List) {
            List<Object> actualList = (List<Object>) actual;
            List<Object> expectedList = (List<Object>) expected;
            if (actualList.size() != expectedList.size()) {
                differences.add("Array size mismatch at " + path + ": expected " + expectedList.size() +
                        ", found " + actualList.size());
                return;
            }
            for (int i = 0; i < actualList.size(); i++) {
                compareValues(actualList.get(i), expectedList.get(i), path + "[" + i + "]", differences);
            }
            return;
        }

        // Handle Numbers (compare numeric values, not types)
        if (actual instanceof Number && expected instanceof Number) {
            double actualValue = ((Number) actual).doubleValue();
            double expectedValue = ((Number) expected).doubleValue();
            if (Math.abs(actualValue - expectedValue) > 0.0001) {
                differences.add("Value mismatch at " + path + ": expected " + expected + ", found " + actual);
            }
            return;
        }

        // Handle primitives (use equals for comparison)
        if (!actual.equals(expected)) {
            differences.add("Value mismatch at " + path + ": expected " + expected + ", found " + actual);
        }
    }

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
        @SuppressWarnings("unchecked")
        var reportJsonMap = (java.util.Map<String, Object>) result.get("report-json");
        Assert.assertNotNull(reportJsonMap, "report-json is null");
        Assert.assertFalse(reportJsonMap.isEmpty(), "report-json should not be empty");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String reportJson = gson.toJson(reportJsonMap);

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

        // Deserialize both JSONs to Maps and compare key by key (order-independent)
        String expectedJson = Files.readString(expectedJsonFile);
        @SuppressWarnings("unchecked")
        Map<String, Object> expectedJsonMap = gson.fromJson(expectedJson, Map.class);
        compareJsonMaps(reportJsonMap, expectedJsonMap, "");

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
            Assert.assertTrue(Files.exists(tempOutputDir.resolve("Ballerina.toml")),
                    "Workspace Ballerina.toml should exist");

            // Compare each converted project with expected results
            TestUtils.compareDirectories(tempOutputDir.resolve("helloWorld_converted"),
                    expectedMultiRootOutput.resolve("helloWorld_converted"));
            TestUtils.compareDirectories(tempOutputDir.resolve("lib_converted"),
                    expectedMultiRootOutput.resolve("lib_converted"));

            // Verify combined summary report exists (content comparison skipped as it may vary)
            Assert.assertTrue(Files.exists(tempOutputDir.resolve("combined_summary_report.html")));

            // Compare workspace Ballerina.toml content
            TestUtils.compareFiles(tempOutputDir.resolve("Ballerina.toml"),
                    expectedMultiRootOutput.resolve("Ballerina.toml"));

        } finally {
            // Clean up temporary directories
            TestUtils.deleteDirectory(tempInputDir);
            TestUtils.deleteDirectory(tempOutputDir);
        }
    }

    @Test(groups = { "tibco", "converter" })
    public void testMultiRootConversionByAPI() throws IOException {
        // Setup multi-root source directory structure
        Path multiRootSource = Path.of("src", "test", "resources", "multi-root");
        Path expectedMultiRootOutput = Path.of("src", "test", "resources", "multi-root-converted");

        // Create parameter map for the API
        java.util.Map<String, Object> parameters = new java.util.HashMap<>();
        parameters.put("orgName", "testOrg");
        parameters.put("projectName", "testProject");
        parameters.put("sourcePath", multiRootSource.toString());
        parameters.put("multiRoot", true);
        parameters.put("stateCallback", (java.util.function.Consumer<String>) s -> {
        });
        parameters.put("logCallback", (java.util.function.Consumer<String>) s -> {
        });

        // Run the conversion using the new public API
        var result = tibco.TibcoToBalConverter.migrateTIBCO(parameters);
        Assert.assertNotNull(result, "migrateTIBCO returned null");
        Assert.assertFalse(result.containsKey("error") && result.get("error") != null,
                "Conversion failed with error: " + result.get("error"));
        Assert.assertTrue(result.containsKey("textEdits"), "Result does not contain 'textEdits'");
        Assert.assertTrue(result.containsKey("report"), "Result does not contain 'report'");
        Assert.assertTrue(result.containsKey("report-json"), "Result does not contain 'report-json'");

        @SuppressWarnings("unchecked")
        var textEdits = (java.util.Map<String, String>) result.get("textEdits");
        Assert.assertNotNull(textEdits, "textEdits is null");

        // Validate HTML report
        String report = (String) result.get("report");
        Assert.assertNotNull(report, "report is null");
        Assert.assertFalse(report.isBlank(), "report should not be empty");
        Assert.assertTrue(report.contains("Combined Migration Assessment"), "Report should contain title");

        // Validate aggregate report in textEdits matches the report field
        Assert.assertTrue(textEdits.containsKey("aggregate_migration_report.html"),
                "Should contain aggregate migration report");
        Assert.assertEquals(textEdits.get("aggregate_migration_report.html"), report,
                "Aggregate report in textEdits should match report field");

        // Validate JSON report structure using Map-based comparison
        @SuppressWarnings("unchecked")
        var jsonReport = (java.util.Map<String, Object>) result.get("report-json");
        Assert.assertNotNull(jsonReport, "report-json is null");
        Assert.assertTrue(jsonReport.containsKey("coverageOverview"),
                "report-json should contain coverageOverview");

        // Validate coverageOverview structure explicitly
        @SuppressWarnings("unchecked")
        var coverageOverview = (java.util.Map<String, Object>) jsonReport.get("coverageOverview");
        Assert.assertNotNull(coverageOverview, "coverageOverview is null");

        // Validate required keys exist
        Assert.assertTrue(coverageOverview.containsKey("projects"),
                "coverageOverview should contain projects");
        Assert.assertTrue(coverageOverview.containsKey("unitName"),
                "coverageOverview should contain unitName");

        // Build expected structure for known values (order-independent comparison)
        Map<String, Object> expectedCoverageOverview = new HashMap<>();
        expectedCoverageOverview.put("unitName", "activity");
        // Note: projects value is validated for existence but not compared
        // since we don't have an expected value file for multi-root case

        // Compare known keys using helper method (order-independent)
        // This validates unitName matches and checks for any unexpected keys in the
        // subset we validate
        Map<String, Object> actualSubset = new HashMap<>();
        actualSubset.put("unitName", coverageOverview.get("unitName"));
        compareJsonMaps(actualSubset, expectedCoverageOverview, "coverageOverview");

        // Validate root Ballerina.toml
        Assert.assertTrue(textEdits.containsKey("Ballerina.toml"), "Should contain root Ballerina.toml");
        String expectedRootToml = Files.readString(expectedMultiRootOutput.resolve("Ballerina.toml"));
        Assert.assertEquals(textEdits.get("Ballerina.toml"), expectedRootToml,
                "Root Ballerina.toml should match expected");

        // Validate each project's files
        // Map from API project prefix to expected directory name
        java.util.Map<String, String> projectMapping = java.util.Map.of(
                "helloWorld", "helloWorld_converted",
                "lib", "lib_converted");

        for (var entry : projectMapping.entrySet()) {
            String projectPrefix = entry.getKey();
            String expectedDirName = entry.getValue();
            Path expectedProjectDir = expectedMultiRootOutput.resolve(expectedDirName);

            // Walk through expected project directory and validate files
            try (Stream<Path> expectedFiles = Files.walk(expectedProjectDir)) {
                var expectedPaths = expectedFiles
                        .filter(Files::isRegularFile)
                        .filter(path -> path.toString().endsWith(".bal") || path.toString().endsWith(".toml")
                                || path.toString().endsWith(".html"))
                        .map(expectedProjectDir::relativize)
                        .toList();

                // Check all expected files exist in textEdits with project prefix and match
                // content
                for (Path relativePath : expectedPaths) {
                    if (relativePath.endsWith("types.bal")) {
                        continue;
                    }
                    // Skip report.html as it's stored as migration_report.html in API
                    if (relativePath.endsWith("report.html")) {
                        String migrationReportKey = projectPrefix + "/migration_report.html";
                        Assert.assertTrue(textEdits.containsKey(migrationReportKey),
                                "Missing migration_report.html in textEdits for " + projectPrefix);
                        String expectedReportContent = Files.readString(expectedProjectDir.resolve(relativePath));
                        Assert.assertEquals(textEdits.get(migrationReportKey), expectedReportContent,
                                "Migration report content should match for " + projectPrefix);
                        continue;
                    }

                    String key = projectPrefix + "/" + relativePath.toString().replace('\\', '/');
                    Assert.assertTrue(textEdits.containsKey(key), "Missing file in textEdits: " + key);
                    String actualContent = textEdits.get(key);
                    String expectedContent = Files.readString(expectedProjectDir.resolve(relativePath));
                    Assert.assertEquals(actualContent, expectedContent,
                            "File contents do not match for: " + key);
                }

                // Check for extra files in textEdits for this project
                for (String key : textEdits.keySet()) {
                    if (key.startsWith(projectPrefix + "/") && !key.equals(projectPrefix + "/migration_report.html")) {
                        String relativeKey = key.substring(projectPrefix.length() + 1);
                        if (relativeKey.endsWith("types.bal")) {
                            continue;
                        }
                        Path relPath = Path.of(relativeKey);
                        if (!expectedPaths.contains(relPath) && !relPath.endsWith("migration_report.html")) {
                            Assert.fail("Extra file in textEdits: " + key);
                        }
                    }
                }
            }
        }

        // Validate aggregate report exists (content comparison skipped as it may vary
        // with timestamps/paths)
        Assert.assertTrue(textEdits.containsKey("aggregate_migration_report.html"),
                "Should contain aggregate migration report");
        String aggregateReport = textEdits.get("aggregate_migration_report.html");
        Assert.assertNotNull(aggregateReport, "Aggregate report should not be null");
        Assert.assertFalse(aggregateReport.isBlank(), "Aggregate report should not be empty");
        Assert.assertTrue(aggregateReport.contains("Combined Migration Assessment"),
                "Aggregate report should contain title");
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
