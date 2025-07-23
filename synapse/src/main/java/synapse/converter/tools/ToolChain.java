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
import synapse.converter.ConversionContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToolChain {

    private record ToolData(String name, ToolContext cx, Tool tool) {

        static ToolData from(ConversionContext cx, Tool tool) {
            return new ToolData(tool.name(), new ToolContext(cx), tool);
        }
    }

    private final List<ToolData> chain;
    private final ConversionContext context;

    public ToolChain(ConversionContext cx, List<Tool> tools) {
        this.context = cx;
        this.chain = tools.stream().map(tool -> ToolData.from(cx, tool)).toList();
    }

    public String getToolDescriptors() {
        return new Gson().toJson(
                chain.stream()
                        .map(toolData -> ToolUtils.getToolDescriptor(toolData.tool))
                        .toList()
        );
    }

    public String handleRequest(String id, String name, String input) {
        for (ToolData toolData : chain) {
            if (toolData.name.equals(name)) {
                return ToolUtils.getToolResponse(toolData.cx, id, input, toolData.tool);
            }
        }
        context.log(LoggingUtils.Level.DEBUG, "Request to unknown tool :" + name);

        Map<String, String> content = new HashMap<>();
        content.put("type", "tool_result");
        content.put("tool_use_id", id);
        content.put("content", "Invalid tool request for %s no such tool".formatted(name));
        return new Gson().toJson(content);
    }

}
