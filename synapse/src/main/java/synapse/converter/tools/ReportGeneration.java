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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import synapse.converter.report.Mediator;
import synapse.converter.report.Project;

public class ReportGeneration implements SynapseConversionTool {

    @Override
    public String name() {
        return "report_generation";
    }

    @Override
    public String description() {
        return "Generate a migration report based on provided confidence and mediator data.";
    }

    @Override
    public String inputSchema() {
        return """
                    {
                        "type": "object",
                        "properties": {
                            "overall_confidence": {
                                "type": "number",
                                "minimum": 0,
                                "maximum": 1,
                                "description": "Overall confidence score (0-1)"
                            },
                            "mediators": {
                                "type": "array",
                                "items": {
                                    "type": "object",
                                    "properties": {
                                        "name": { "type": "string" },
                                        "instances": { "type": "integer", "minimum": 1 },
                                        "confidence_score": { "type": "number", "minimum": 0, "maximum": 1 },
                                        "complexity_score": { "type": "number", "minimum": 0, "maximum": 1 }
                                    },
                                    "required": ["name", "instances", "confidence_score", "complexity_score"]
                                },
                                "description": "List of mediator details"
                            }
                        },
                        "required": ["overall_confidence", "mediators"]
                    }
                """;
    }

    @Override
    public String execute(ToolContext cx, String request) {
        JsonObject requestNode;
        try {
            requestNode = JsonParser.parseString(request).getAsJsonObject();
        } catch (Exception e) {
            cx.log(common.LoggingUtils.Level.DEBUG, "Invalid JSON input: " + e.getMessage());
            return "Error: Invalid JSON input - " + e.getMessage();
        }
        String validationError = validateInput(requestNode);
        if (validationError != null) {
            return validationError;
        }
        var report = parseRequest(requestNode);
        // For now, return a string representation of the report
        return "Report generated successfully";
    }

    static Project parseRequest(JsonObject requestNode) {
        // Parse to Project record
        double overallConfidence = requestNode.get("overall_confidence").getAsDouble();
        JsonArray mediatorsArray = requestNode.getAsJsonArray("mediators");
        Mediator[] mediators = new Mediator[mediatorsArray.size()];
        for (int i = 0; i < mediatorsArray.size(); i++) {
            JsonObject mediatorObj = mediatorsArray.get(i).getAsJsonObject();
            String name = mediatorObj.get("name").getAsString();
            int instances = mediatorObj.get("instances").getAsInt();
            double confidenceScore = mediatorObj.get("confidence_score").getAsDouble();
            double complexityScore = mediatorObj.get("complexity_score").getAsDouble();
            mediators[i] = new Mediator(name, instances, confidenceScore, complexityScore);
        }
        return new Project(overallConfidence, mediators);
    }

    /**
     * Validates the input JSON for required fields and value constraints.
     *
     * @param requestNode the parsed input JSON
     * @return error message if invalid, or null if valid
     */
    private String validateInput(JsonObject requestNode) {
        // Validate overall_confidence
        if (!requestNode.has("overall_confidence")) {
            return "Error: 'overall_confidence' is required";
        }
        double overallConfidence;
        try {
            overallConfidence = requestNode.get("overall_confidence").getAsDouble();
        } catch (Exception e) {
            return "Error: 'overall_confidence' must be a number";
        }
        if (overallConfidence < 0 || overallConfidence > 1) {
            return "Error: 'overall_confidence' must be between 0 and 1";
        }
        // Validate mediators
        if (!requestNode.has("mediators")) {
            return "Error: 'mediators' is required";
        }
        JsonArray mediators;
        try {
            mediators = requestNode.getAsJsonArray("mediators");
        } catch (Exception e) {
            return "Error: 'mediators' must be an array";
        }
        for (int i = 0; i < mediators.size(); i++) {
            JsonObject mediator;
            try {
                mediator = mediators.get(i).getAsJsonObject();
            } catch (Exception e) {
                return "Error: Each mediator must be an object";
            }
            // name
            if (!mediator.has("name") || mediator.get("name").getAsString().isBlank()) {
                return "Error: Mediator at index " + i + " missing or empty 'name'";
            }
            // instances
            if (!mediator.has("instances")) {
                return "Error: Mediator at index " + i + " missing 'instances'";
            }
            int instances;
            try {
                instances = mediator.get("instances").getAsInt();
            } catch (Exception e) {
                return "Error: Mediator at index " + i + " has non-integer 'instances'";
            }
            if (instances < 1) {
                return "Error: Mediator at index " + i + " must have 'instances' > 0";
            }
            // confidence_score
            if (!mediator.has("confidence_score")) {
                return "Error: Mediator at index " + i + " missing 'confidence_score'";
            }
            double confidenceScore;
            try {
                confidenceScore = mediator.get("confidence_score").getAsDouble();
            } catch (Exception e) {
                return "Error: Mediator at index " + i + " has non-numeric 'confidence_score'";
            }
            if (confidenceScore < 0 || confidenceScore > 1) {
                return "Error: Mediator at index " + i + " must have 'confidence_score' between 0 and 1";
            }
            // complexity_score
            if (!mediator.has("complexity_score")) {
                return "Error: Mediator at index " + i + " missing 'complexity_score'";
            }
            double complexityScore;
            try {
                complexityScore = mediator.get("complexity_score").getAsDouble();
            } catch (Exception e) {
                return "Error: Mediator at index " + i + " has non-numeric 'complexity_score'";
            }
            if (complexityScore < 0 || complexityScore > 1) {
                return "Error: Mediator at index " + i + " must have 'complexity_score' between 0 and 1";
            }
        }
        return null;
    }
}
