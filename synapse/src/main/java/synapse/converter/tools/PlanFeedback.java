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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import static common.LoggingUtils.Level.DEBUG;

public class PlanFeedback implements SynapseConversionTool {

    @Override
    public String name() {
        return "validate_plan";
    }

    @Override
    public String description() {
        return "validate code migration plan";
    }

    @Override
    public String inputSchema() {
        return """
                {
                    "type": "object",
                    "properties": {
                        "plan": {
                            "type": "string",
                            "description": "Step by step migration plan"
                        }
                    },
                    "required": ["plan"]
                }
                """;
    }

    @Override
    public String execute(ToolContext cx, String request) {
        JsonObject requestNode;
        try {
            requestNode = JsonParser.parseString(request).getAsJsonObject();
        } catch (Exception e) {
            cx.log(DEBUG, "Invalid JSON input: " + e.getMessage());
            return "Error: Invalid JSON input - " + e.getMessage();
        }

        String validationError = validateInput(requestNode);
        if (validationError != null) {
            return validationError;
        }

        String plan = requestNode.get("plan").getAsString();

        if (plan.trim().isEmpty()) {
            return "Error: Plan cannot be empty";
        }

        return "Plan is valid fallow it";
    }

    private String validateInput(JsonObject requestNode) {
        if (!requestNode.has("plan")) {
            return "Error: 'plan' parameter is required";
        }
        return null;
    }
}
