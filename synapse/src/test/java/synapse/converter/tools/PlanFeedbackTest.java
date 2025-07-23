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
import org.testng.annotations.Test;

import synapse.converter.ConversionContext;

import java.nio.file.Paths;

class TestConversionContextForPlanFeedback extends ConversionContext {

    public TestConversionContextForPlanFeedback(String projectPath) {
        super(projectPath);
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

public class PlanFeedbackTest {

    @Test
    public void testToolMetadata() {
        PlanFeedback planFeedback = new PlanFeedback();

        Assert.assertEquals(planFeedback.name(), "validate_plan", "Tool name should be 'validate_plan'");
        Assert.assertEquals(planFeedback.description(), "validate code migration plan",
                "Tool description should match expected value");

        String inputSchema = planFeedback.inputSchema();
        Assert.assertTrue(inputSchema.contains("\"plan\""), "Input schema should contain 'plan' property");
        Assert.assertTrue(inputSchema.contains("\"required\""), "Input schema should specify required fields");
        Assert.assertTrue(inputSchema.contains("Step by step migration plan"),
                "Input schema should contain plan description");
    }

    @Test
    public void testValidPlan() {
        PlanFeedback planFeedback = new PlanFeedback();

        String projectPath = Paths.get("src/test/resources/projects/ContentBasedRouting").toAbsolutePath().toString();
        TestConversionContextForPlanFeedback conversionContext =
                new TestConversionContextForPlanFeedback(projectPath);
        ToolContext toolContext = new ToolContext(conversionContext);

        String request = """
                {
                    "plan": "1. Analyze source code\\n2. Convert APIs\\n3. Generate Ballerina code"
                }
                """;

        String result = planFeedback.execute(toolContext, request);

        Assert.assertEquals(result, "Plan is valid fallow it", "Should return validation success message");
    }

    @Test
    public void testValidPlanWithComplexContent() {
        PlanFeedback planFeedback = new PlanFeedback();

        String projectPath = Paths.get("src/test/resources/projects/ContentBasedRouting").toAbsolutePath().toString();
        TestConversionContextForPlanFeedback conversionContext =
                new TestConversionContextForPlanFeedback(projectPath);
        ToolContext toolContext = new ToolContext(conversionContext);

        String request = """
                {
                    "plan": "Migration Plan: Parse XML, Convert endpoints, Transform sequences"
                }
                """;

        String result = planFeedback.execute(toolContext, request);

        Assert.assertEquals(result, "Plan is valid fallow it", "Should return validation success message");
    }

    @Test
    public void testMissingPlanParameter() {
        PlanFeedback planFeedback = new PlanFeedback();

        String projectPath = Paths.get("src/test/resources/projects/ContentBasedRouting").toAbsolutePath().toString();
        TestConversionContextForPlanFeedback conversionContext =
                new TestConversionContextForPlanFeedback(projectPath);
        ToolContext toolContext = new ToolContext(conversionContext);

        String request = """
                {
                    "steps": "some steps"
                }
                """;

        String result = planFeedback.execute(toolContext, request);

        Assert.assertTrue(result.startsWith("Error: 'plan' parameter is required"),
                "Should indicate missing plan parameter");
    }

    @Test
    public void testEmptyPlanParameter() {
        PlanFeedback planFeedback = new PlanFeedback();

        String projectPath = Paths.get("src/test/resources/projects/ContentBasedRouting").toAbsolutePath().toString();
        TestConversionContextForPlanFeedback conversionContext =
                new TestConversionContextForPlanFeedback(projectPath);
        ToolContext toolContext = new ToolContext(conversionContext);

        String request = """
                {
                    "plan": ""
                }
                """;

        String result = planFeedback.execute(toolContext, request);

        Assert.assertTrue(result.startsWith("Error: Plan cannot be empty"),
                "Should indicate empty plan parameter");
    }

    @Test
    public void testWhitespaceOnlyPlanParameter() {
        PlanFeedback planFeedback = new PlanFeedback();

        String projectPath = Paths.get("src/test/resources/projects/ContentBasedRouting").toAbsolutePath().toString();
        TestConversionContextForPlanFeedback conversionContext =
                new TestConversionContextForPlanFeedback(projectPath);
        ToolContext toolContext = new ToolContext(conversionContext);

        String request = """
                {
                    "plan": "   \\n\\t  "
                }
                """;

        String result = planFeedback.execute(toolContext, request);

        Assert.assertTrue(result.startsWith("Error: Plan cannot be empty"),
                "Should indicate empty plan parameter when only whitespace");
    }

    @Test
    public void testInvalidJson() {
        PlanFeedback planFeedback = new PlanFeedback();

        String projectPath = Paths.get("src/test/resources/projects/ContentBasedRouting").toAbsolutePath().toString();
        TestConversionContextForPlanFeedback conversionContext =
                new TestConversionContextForPlanFeedback(projectPath);
        ToolContext toolContext = new ToolContext(conversionContext);

        String request = "{ invalid json }";

        String result = planFeedback.execute(toolContext, request);

        Assert.assertTrue(result.startsWith("Error: Invalid JSON input"),
                "Should indicate JSON parsing error");
    }

    @Test
    public void testPlanWithSpecialCharacters() {
        PlanFeedback planFeedback = new PlanFeedback();

        String projectPath = Paths.get("src/test/resources/projects/ContentBasedRouting").toAbsolutePath().toString();
        TestConversionContextForPlanFeedback conversionContext =
                new TestConversionContextForPlanFeedback(projectPath);
        ToolContext toolContext = new ToolContext(conversionContext);

        String request = """
                {
                    "plan": "Plan with special chars: @#$%^&*(){}[]|\\\\:;\\\"'<>,.?/~`"
                }
                """;

        String result = planFeedback.execute(toolContext, request);

        Assert.assertEquals(result, "Plan is valid fallow it", "Should handle special characters in plan");
    }

    @Test
    public void testPlanWithUnicodeCharacters() {
        PlanFeedback planFeedback = new PlanFeedback();

        String projectPath = Paths.get("src/test/resources/projects/ContentBasedRouting").toAbsolutePath().toString();
        TestConversionContextForPlanFeedback conversionContext =
                new TestConversionContextForPlanFeedback(projectPath);
        ToolContext toolContext = new ToolContext(conversionContext);

        String request = """
                {
                    "plan": "Plan with unicode: 测试 العربية русский 한국어 日本語"
                }
                """;

        String result = planFeedback.execute(toolContext, request);

        Assert.assertEquals(result, "Plan is valid fallow it", "Should handle unicode characters in plan");
    }
}
