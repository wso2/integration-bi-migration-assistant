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
import com.google.gson.reflect.TypeToken;
import common.LoggingUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import synapse.converter.ConversionContext;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;

public class ToolChainTest {

    private final Gson gson = new Gson();
    private ConversionContext mockContext;
    private ToolChain toolChain;

    @BeforeMethod
    public void setUp() {
        mockContext = new MockConversionContext();
        
        List<Tool> tools = List.of(
            new MockWeatherTool(),
            new MockTimeTool(),
            new MockClaudeEditTool()
        );
        
        toolChain = new ToolChain(mockContext, tools);
    }

    @Test
    public void testGetToolDescriptors() {
        String result = toolChain.getToolDescriptors();
        
        Type listType = new TypeToken<List<String>>() { }.getType();
        List<String> descriptorStrings = gson.fromJson(result, listType);
        
        assertEquals(descriptorStrings.size(), 3);
        
        Map<String, Object> weatherDescriptor = gson.fromJson(descriptorStrings.get(0), Map.class);
        assertEquals(weatherDescriptor.get("name"), "get_weather");
        assertEquals(weatherDescriptor.get("description"), "Get the current weather in a given location");
        
        Map<String, Object> timeDescriptor = gson.fromJson(descriptorStrings.get(1), Map.class);
        assertEquals(timeDescriptor.get("name"), "get_time");
        assertEquals(timeDescriptor.get("description"), "Get the current time in a given timezone");
        
        Map<String, Object> claudeDescriptor = gson.fromJson(descriptorStrings.get(2), Map.class);
        assertEquals(claudeDescriptor.get("type"), "text_editor_20250429");
        assertEquals(claudeDescriptor.get("name"), "str_replace_based_edit_tool");
    }

    @Test
    public void testHandleRequestForWeatherTool() {
        String input = "{\"location\": \"San Francisco, CA\"}";
        String result = toolChain.handleRequest("toolu_01", "get_weather", input);
        
        Map<String, String> response = gson.fromJson(result, Map.class);
        assertEquals(response.get("type"), "tool_result");
        assertEquals(response.get("tool_use_id"), "toolu_01");
        assertEquals(response.get("content"), "San Francisco: 68°F, partly cloudy");
    }

    @Test
    public void testHandleRequestForTimeTool() {
        String input = "{\"timezone\": \"America/Los_Angeles\"}";
        String result = toolChain.handleRequest("toolu_03", "get_time", input);
        
        Map<String, String> response = gson.fromJson(result, Map.class);
        assertEquals(response.get("type"), "tool_result");
        assertEquals(response.get("tool_use_id"), "toolu_03");
        assertEquals(response.get("content"), "2:30 PM PST");
    }

    @Test
    public void testHandleRequestForClaudeTool() {
        String input = "{\"command\": \"str_replace\", \"path\": \"test.py\"}";
        String result = toolChain.handleRequest("toolu_02", "str_replace_based_edit_tool", input);
        
        Map<String, String> response = gson.fromJson(result, Map.class);
        assertEquals(response.get("type"), "tool_result");
        assertEquals(response.get("tool_use_id"), "toolu_02");
        assertEquals(response.get("content"), "Edit completed successfully");
    }

    @Test
    public void testHandleRequestForUnknownTool() {
        String input = "{\"query\": \"test\"}";
        String result = toolChain.handleRequest("toolu_99", "unknown_tool", input);
        
        Map<String, String> response = gson.fromJson(result, Map.class);
        assertEquals(response.get("type"), "tool_result");
        assertEquals(response.get("tool_use_id"), "toolu_99");
        assertEquals(response.get("content"), "Invalid tool request for unknown_tool no such tool");
    }

    @Test
    public void testHandleRequestWithToolException() {
        String input = "{\"location\": \"error_location\"}";
        String result = toolChain.handleRequest("toolu_error", "get_weather", input);
        
        Map<String, String> response = gson.fromJson(result, Map.class);
        assertEquals(response.get("type"), "tool_result");
        assertEquals(response.get("tool_use_id"), "toolu_error");
        assertEquals(response.get("content"), "Unhandled error while executing tool: get_weather");
    }

    @Test
    public void testMultipleToolCallsScenario() {
        String weatherInput = "{\"location\": \"New York, NY\"}";
        String timeInput = "{\"timezone\": \"America/New_York\"}";
        
        String weatherResult = toolChain.handleRequest("toolu_01", "get_weather", weatherInput);
        String timeResult = toolChain.handleRequest("toolu_02", "get_time", timeInput);
        
        Map<String, String> weatherResponse = gson.fromJson(weatherResult, Map.class);
        Map<String, String> timeResponse = gson.fromJson(timeResult, Map.class);
        
        assertEquals(weatherResponse.get("tool_use_id"), "toolu_01");
        assertEquals(weatherResponse.get("content"), "New York: 45°F, clear skies");
        
        assertEquals(timeResponse.get("tool_use_id"), "toolu_02");
        assertEquals(timeResponse.get("content"), "5:30 PM EST");
    }

    private static class MockWeatherTool implements SynapseConversionTool {
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
                    + "\"description\":\"The city and state, e.g. San Francisco, CA\"}},\"required\":[\"location\"]}";
        }

        @Override
        public String execute(ToolContext cx, String request) {
            Map<String, Object> input = new Gson().fromJson(request, Map.class);
            String location = (String) input.get("location");
            
            if ("error_location".equals(location)) {
                throw new RuntimeException("Weather service error");
            }
            
            if (location.contains("San Francisco")) {
                return "San Francisco: 68°F, partly cloudy";
            } else if (location.contains("New York")) {
                return "New York: 45°F, clear skies";
            }
            return "Weather data not available";
        }
    }

    private static class MockTimeTool implements SynapseConversionTool {
        @Override
        public String name() {
            return "get_time";
        }

        @Override
        public String description() {
            return "Get the current time in a given timezone";
        }

        @Override
        public String inputSchema() {
            return "{\"type\":\"object\",\"properties\":{\"timezone\":{\"type\":\"string\","
                    + "\"description\":\"The timezone, e.g. America/New_York\"}},\"required\":[\"timezone\"]}";
        }

        @Override
        public String execute(ToolContext cx, String request) {
            Map<String, Object> input = new Gson().fromJson(request, Map.class);
            String timezone = (String) input.get("timezone");
            
            if ("America/Los_Angeles".equals(timezone)) {
                return "2:30 PM PST";
            } else if ("America/New_York".equals(timezone)) {
                return "5:30 PM EST";
            }
            return "Time not available for timezone";
        }
    }

    private static class MockClaudeEditTool implements ClaudeTool {
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
            return "Edit completed successfully";
        }
    }

    private static class MockConversionContext extends ConversionContext {

        public MockConversionContext() {
            super("");
        }
        @Override
        public void log(LoggingUtils.Level level, String message) {
            // Mock implementation - no actual logging for tests
        }

        @Override
        public void logState(String message) {
            // Mock implementation - no actual logging for tests
        }
    }
}
