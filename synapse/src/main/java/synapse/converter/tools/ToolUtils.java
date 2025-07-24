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

import java.util.HashMap;
import java.util.Map;

public final class ToolUtils {

    private static final Gson serializer = new Gson();

    private ToolUtils() {
    }

    /**
     * Get the tool descriptor for the given tool.
     *
     * @param tool Tool instance
     * @return {@code json} string representing the tool descriptor
     */
    public static String getToolDescriptor(Tool tool) {
        Map<String, String> content;
        if (tool instanceof ClaudeTool claudeTool) {
            content = Map.of(
                    "type", claudeTool.type(),
                    "name", claudeTool.name()
            );
        } else {
            var conversionTool = (SynapseConversionTool) tool;
            content = Map.of(
                    "name", conversionTool.name(),
                    "description", conversionTool.description(),
                    "input_schema", conversionTool.inputSchema()
            );
        }
        return serializer.toJson(content);
    }

    public static String getToolResponse(ToolContext cx, String id, String request, Tool tool) {
        String response;
        try {
            response = tool.execute(cx, request);
        } catch (Exception e) {
            response = "Unhandled error while executing tool: %s".formatted(tool.name());
            cx.log(LoggingUtils.Level.ERROR, response);
            cx.log(LoggingUtils.Level.DEBUG, e.getMessage());
        }
        Map<String, String> content = new HashMap<>();
        content.put("type", "tool_result");
        content.put("tool_use_id", id);
        content.put("content", response);
        return serializer.toJson(content);
    }

}
