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
package mule;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class MuleProjectConversionTest {

    @Test(groups = {"mule", "converter"})
    public void testSingleProjectConversionByAPI() throws IOException {
        // Use existing test project
        Path muleProject = Path.of("src", "test", "resources", "mule", "v3", "projects", "demo_project_bi");
        Path expectedBallerinaProject = Path.of("src", "test", "resources", "mule", "v3", "projects",
                "demo_project_bi", "demo_project_bi_ballerina");

        // Create parameter map for the API
        Map<String, Object> parameters = Map.of(
                "orgName", "testOrg",
                "projectName", expectedBallerinaProject.getFileName().toString(),
                "sourcePath", muleProject.toString(),
                "stateCallback", (java.util.function.Consumer<String>) s -> {
                },
                "logCallback", (java.util.function.Consumer<String>) s -> {
                }
        );

        // Run the conversion using the new public API
        var result = MuleMigrator.migrateMule(parameters);
        Assert.assertNotNull(result, "migrateMule returned null");
        Assert.assertFalse(result.containsKey("error"), "Conversion failed with error: " + result.get("error"));
        Assert.assertTrue(result.containsKey("textEdits"), "Result does not contain 'textEdits'");
        Assert.assertTrue(result.containsKey("report"), "Result does not contain 'report'");
        Assert.assertTrue(result.containsKey("report-json"), "Result does not contain 'report-json'");

        @SuppressWarnings("unchecked")
        var textEdits = (Map<String, String>) result.get("textEdits");
        Assert.assertNotNull(textEdits, "textEdits is null");

        // Validate the report-json
        String reportJson = (String) result.get("report-json");
        Assert.assertNotNull(reportJson, "report-json is null");
        Assert.assertFalse(reportJson.isBlank(), "report-json should not be empty");
    }

    @Test(groups = {"mule", "converter"})
    public void testMultiRootConversionByAPI() throws IOException {
        // Setup multi-root source directory structure
        Path multiRootSource = Path.of("src", "test", "resources", "mule", "v3", "projects");
        Path expectedMultiRootOutput = Path.of("src", "test", "resources", "mule", "v3", "misc", "multi_root_output");

        // Create parameter map for the API
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("orgName", "testOrg");
        parameters.put("projectName", "testProject");
        parameters.put("sourcePath", multiRootSource.toString());
        parameters.put("multiRoot", true);
        parameters.put("stateCallback", (java.util.function.Consumer<String>) s -> {
        });
        parameters.put("logCallback", (java.util.function.Consumer<String>) s -> {
        });

        // Run the conversion using the new public API
        var result = MuleMigrator.migrateMule(parameters);
        Assert.assertNotNull(result, "migrateMule returned null");
        Assert.assertFalse(result.containsKey("error") && result.get("error") != null,
                "Conversion failed with error: " + result.get("error"));
        Assert.assertTrue(result.containsKey("textEdits"), "Result does not contain 'textEdits'");
        Assert.assertTrue(result.containsKey("report"), "Result does not contain 'report'");
        Assert.assertTrue(result.containsKey("report-json"), "Result does not contain 'report-json'");

        @SuppressWarnings("unchecked")
        var textEdits = (Map<String, String>) result.get("textEdits");
        Assert.assertNotNull(textEdits, "textEdits is null");

        // Validate HTML report
        String report = (String) result.get("report");
        Assert.assertNotNull(report, "report is null");
        Assert.assertFalse(report.isBlank(), "report should not be empty");
        Assert.assertTrue(report.contains("Aggregate Migration Assessment") ||
                        report.contains("Aggregate Migration Summary"),
                "Report should contain aggregate title");

        // Validate aggregate report in textEdits matches the report field
        Assert.assertTrue(textEdits.containsKey("aggregate_migration_report.html"),
                "Should contain aggregate migration report");
        Assert.assertEquals(textEdits.get("aggregate_migration_report.html"), report,
                "Aggregate report in textEdits should match report field");

        // Validate JSON report structure
        @SuppressWarnings("unchecked")
        var jsonReport = (Map<String, Object>) result.get("report-json");
        Assert.assertNotNull(jsonReport, "report-json is null");
        Assert.assertTrue(jsonReport.containsKey("coverageOverview"), "report-json should contain coverageOverview");

        @SuppressWarnings("unchecked")
        var coverageOverview = (Map<String, Object>) jsonReport.get("coverageOverview");
        Assert.assertNotNull(coverageOverview, "coverageOverview is null");
        Assert.assertTrue(coverageOverview.containsKey("projects"), "coverageOverview should contain projects");
        Assert.assertTrue(coverageOverview.containsKey("unitName"), "coverageOverview should contain unitName");
        Assert.assertEquals(coverageOverview.get("unitName"), "code lines", "unitName should be 'code lines'");
        Assert.assertTrue(
                coverageOverview.containsKey("coveragePercentage"),
                "coverageOverview should contain coveragePercentage");
        Assert.assertTrue(
                coverageOverview.containsKey("coverageLevel"),
                "coverageOverview should contain coverageLevel");
        Assert.assertTrue(
                coverageOverview.containsKey("totalElements"),
                "coverageOverview should contain totalElements");
        Assert.assertTrue(
                coverageOverview.containsKey("migratableElements"),
                "coverageOverview should contain migratableElements");
        Assert.assertTrue(
                coverageOverview.containsKey("nonMigratableElements"),
                "coverageOverview should contain nonMigratableElements");

        // Validate root Ballerina.toml
        Assert.assertTrue(textEdits.containsKey("Ballerina.toml"), "Should contain root Ballerina.toml");
        String rootToml = textEdits.get("Ballerina.toml");
        Assert.assertNotNull(rootToml, "Root Ballerina.toml should not be null");
        Assert.assertFalse(rootToml.isBlank(), "Root Ballerina.toml should not be empty");
        Assert.assertTrue(rootToml.contains("[workspace]"), "Root Ballerina.toml should contain [workspace]");
        Assert.assertTrue(rootToml.contains("packages ="), "Root Ballerina.toml should contain packages");

        // Validate expected root Ballerina.toml if it exists
        if (Files.exists(expectedMultiRootOutput.resolve("Ballerina.toml"))) {
            String expectedRootToml = Files.readString(expectedMultiRootOutput.resolve("Ballerina.toml"));
            Assert.assertEquals(rootToml, expectedRootToml, "Root Ballerina.toml should match expected");
        }

        // Validate that we have project-prefixed files
        boolean hasProjectFiles = textEdits.keySet().stream()
                .anyMatch(key -> key.contains("/") && !key.equals("aggregate_migration_report.html")
                        && !key.equals("Ballerina.toml"));
        Assert.assertTrue(hasProjectFiles, "Should contain project-prefixed files");

        // Validate individual project reports exist
        boolean hasIndividualReports = textEdits.keySet().stream()
                .anyMatch(key -> key.endsWith("/migration_report.html"));
        Assert.assertTrue(hasIndividualReports, "Should contain individual project migration reports");
    }

    @Test(groups = {"mule", "converter"})
    public void testMigrateMuleAPIErrorHandling() {
        // Test missing parameter
        Map<String, Object> emptyParams = new HashMap<>();
        var result1 = MuleMigrator.migrateMule(emptyParams);
        Assert.assertTrue(result1.containsKey("error"), "Should return error for missing parameters");
        Assert.assertTrue(result1.get("error").toString().contains("Missing required parameter"),
                "Error message should mention missing parameter");

        // Test wrong type parameter
        Map<String, Object> wrongTypeParams = new HashMap<>();
        wrongTypeParams.put("orgName", 123); // Should be String, not Integer
        var result2 = MuleMigrator.migrateMule(wrongTypeParams);
        Assert.assertTrue(result2.containsKey("error"), "Should return error for wrong parameter type");
        Assert.assertTrue(result2.get("error").toString().contains("must be a String"),
                "Error message should mention type mismatch");

        // Test missing some parameters
        Map<String, Object> partialParams = new HashMap<>();
        partialParams.put("orgName", "testOrg");
        partialParams.put("projectName", "testProject");
        // Missing sourcePath, stateCallback, logCallback
        var result3 = MuleMigrator.migrateMule(partialParams);
        Assert.assertTrue(result3.containsKey("error"), "Should return error for missing parameters");

        // Test invalid path for multi-root
        Map<String, Object> invalidMultiRootParams = new HashMap<>();
        invalidMultiRootParams.put("orgName", "testOrg");
        invalidMultiRootParams.put("projectName", "testProject");
        invalidMultiRootParams.put("sourcePath", "/nonexistent/path");
        invalidMultiRootParams.put("multiRoot", true);
        invalidMultiRootParams.put("stateCallback", (java.util.function.Consumer<String>) s -> {
        });
        invalidMultiRootParams.put("logCallback", (java.util.function.Consumer<String>) s -> {
        });
        var result4 = MuleMigrator.migrateMule(invalidMultiRootParams);
        Assert.assertTrue(result4.containsKey("error"), "Should return error for invalid path");
    }
}

