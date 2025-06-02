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
package dw.parser.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import mule.dataweave.parser.DataWeaveLexer;
import mule.dataweave.parser.DataWeaveParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ParserTestUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static void compareJson(String dWScript, String expectedJsonFilePath) {
        ParseTree tree = getParseTree(dWScript);
        JsonNode actualJson = getJsonFromTree(tree);

        try {
            JsonNode expectedJson = mapper.readTree(Files.readString(Paths.get(expectedJsonFilePath)));
            List<String> differences = new ArrayList<>();
            compareJsonNodes(expectedJson, actualJson, "", differences);
            if (!differences.isEmpty()) {
                String errorMessage = "JSON output mismatch!\nDifferences:\n" + String.join("\n", differences) +
                        "\nExpected:\n" + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(expectedJson) +
                        "\nActual:\n" + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(actualJson);
                throw new AssertionError(errorMessage);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading expected JSON file", e);
        }
    }

    private static ParseTree getParseTree(String dwScript) {
        CharStream input = CharStreams.fromString(dwScript);
        DataWeaveLexer lexer = new DataWeaveLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        DataWeaveParser parser = new DataWeaveParser(tokens);
        return parser.script();
    }

    private static JsonNode getJsonFromTree(ParseTree tree) {
        JsonVisitor visitor = new JsonVisitor();
        return visitor.visit(tree);
    }


    private static void compareJsonNodes(JsonNode expected, JsonNode actual, String path, List<String> differences) {
        if (expected.equals(actual)) {
            return;
        }

        if (expected.isObject() && actual.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = expected.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String currentPath = path.isEmpty() ? entry.getKey() : path + "." + entry.getKey();

                if (!actual.has(entry.getKey())) {
                    differences.add("Missing key: " + currentPath);
                } else {
                    compareJsonNodes(entry.getValue(), actual.get(entry.getKey()), currentPath, differences);
                }
            }

            // Check for extra keys in the actual JSON
            Iterator<String> actualFields = actual.fieldNames();
            while (actualFields.hasNext()) {
                String key = actualFields.next();
                if (!expected.has(key)) {
                    String currentPath = path.isEmpty() ? key : path + "." + key;
                    differences.add("Unexpected key: " + currentPath);
                }
            }

        } else if (expected.isArray() && actual.isArray()) {
            int expectedSize = expected.size();
            int actualSize = actual.size();
            if (expectedSize != actualSize) {
                differences.add("Array size mismatch at " + path + ": expected " + expectedSize + ", found "
                        + actualSize);
            }

            for (int i = 0; i < Math.min(expectedSize, actualSize); i++) {
                compareJsonNodes(expected.get(i), actual.get(i), path + "[" + i + "]", differences);
            }
        } else {
            differences.add("Value mismatch at " + path + ": expected " + expected + ", found " + actual);
        }
    }

}
