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

import com.google.gson.Gson;
import common.LoggingUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.Assert.assertEquals;

public class ToolUtilsTest {

    private final Gson gson = new Gson();
    private ToolContext mockContext;

    @BeforeMethod
    public void setUp() {
        mockContext = new ToolContext(new MockLoggingContext());
    }

    @Test
    public void testGetToolDescriptorForClaudeTool() {
        ClaudeTool mockClaudeTool = new MockClaudeTool();
        String result = ToolUtils.getToolDescriptor(mockClaudeTool);
        
        Map<String, String> parsedResult = gson.fromJson(result, Map.class);
        assertEquals(parsedResult.get("type"), "text_editor_20250429");
        assertEquals(parsedResult.get("name"), "str_replace_based_edit_tool");
    }

    @Test
    public void testGetToolDescriptorForSynapseConversionTool() {
        SynapseConversionTool mockSynapseTool = new MockSynapseConversionTool();
        String result = ToolUtils.getToolDescriptor(mockSynapseTool);
        
        Map<String, Object> parsedResult = gson.fromJson(result, Map.class);
        assertEquals(parsedResult.get("name"), "get_weather");
        assertEquals(parsedResult.get("description"), "Get the current weather in a given location");
        assertEquals(parsedResult.get("input_schema"),
                "{\"type\":\"object\",\"properties\":{\"location\":{\"type\":\"string\","
                        + "\"description\":\"The city and state, e.g. San Francisco, CA\"},"
                        + "\"unit\":{\"type\":\"string\",\"enum\":[\"celsius\",\"fahrenheit\"],"
                        + "\"description\":\"The unit of temperature, either \\\"celsius\\\" or \\\"fahrenheit\\\"\"}},"
                        + "\"required\":[\"location\"]}");
    }

    @Test
    public void testGetToolResponseSuccess() {
        Tool mockTool = new MockSuccessfulTool();
        String result = ToolUtils.getToolResponse(mockContext, "toolu_01A09q90qw90lq917835lq9",
                "{\"location\":\"San Francisco, CA\"}", mockTool);
        
        Map<String, String> parsedResult = gson.fromJson(result, Map.class);
        assertEquals(parsedResult.get("type"), "tool_result");
        assertEquals(parsedResult.get("tool_use_id"), "toolu_01A09q90qw90lq917835lq9");
        assertEquals(parsedResult.get("content"), "15 degrees");
    }

    @Test
    public void testGetToolResponseWithException() {
        Tool mockTool = new MockFailingTool();
        String result = ToolUtils.getToolResponse(mockContext, "toolu_01A09q90qw90lq917835lq9",
                "{\"location\":\"San Francisco, CA\"}", mockTool);
        
        Map<String, String> parsedResult = gson.fromJson(result, Map.class);
        assertEquals(parsedResult.get("type"), "tool_result");
        assertEquals(parsedResult.get("tool_use_id"), "toolu_01A09q90qw90lq917835lq9");
        assertEquals(parsedResult.get("content"), "Unhandled error while executing tool: failing_tool");
    }

    private static class MockClaudeTool implements ClaudeTool {
        @Override
        public String type() {
            return "text_editor_20250429";
        }

        @Override
        public String name() {
            return "str_replace_based_edit_tool";
        }

        @Override
        public String execute(ToolContext cx, String request) {
            return "Mock execution result";
        }
    }

    private static class MockSynapseConversionTool implements SynapseConversionTool {
        @Override
        public String name() {
            return "get_weather";
        }

        @Override
        public String description() {
            return "Get the current weather in a given location";
        }

        @Override
        public String inputSchema() {
            return "{\"type\":\"object\",\"properties\":{\"location\":{\"type\":\"string\","
                    + "\"description\":\"The city and state, e.g. San Francisco, CA\"},"
                    + "\"unit\":{\"type\":\"string\",\"enum\":[\"celsius\",\"fahrenheit\"],"
                    + "\"description\":\"The unit of temperature, either \\\"celsius\\\" or \\\"fahrenheit\\\"\"}},"
                    + "\"required\":[\"location\"]}";
        }

        @Override
        public String execute(ToolContext cx, String request) {
            return "Mock weather result";
        }
    }

    private static class MockSuccessfulTool implements Tool {
        @Override
        public String name() {
            return "successful_tool";
        }

        @Override
        public String execute(ToolContext cx, String request) {
            return "15 degrees";
        }
    }

    private static class MockFailingTool implements Tool {
        @Override
        public String name() {
            return "failing_tool";
        }

        @Override
        public String execute(ToolContext cx, String request) {
            throw new RuntimeException("Tool execution failed");
        }
    }

    private static class MockLoggingContext implements common.LoggingContext {
        @Override
        public void log(LoggingUtils.Level level, String message) {
            // Mock implementation - no actual logging
        }

        @Override
        public void logState(String message) {
            // Mock implementation - no actual logging
        }
    }
}
