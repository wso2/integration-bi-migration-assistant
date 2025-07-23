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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static common.LoggingUtils.Level.DEBUG;
import static common.LoggingUtils.Level.INFO;

public class ResourceFinder implements SynapseConversionTool {

    private static final Set<String> SUPPORTED_KINDS =
            Set.of("api", "endpoint", "sequence", "inboundEndpoint", "localEntry", "messageProcessor", "messageStore",
                    "proxy", "task");

    @Override
    public String name() {
        return "get_resource";
    }

    @Override
    public String description() {
        return "Find resources such as sequences, endpoints by name";
    }

    @Override
    public String inputSchema() {
        return """
                {
                    "type": "object",
                    "properties": {
                        "name": {
                            "type": "string",
                            "description": "The name of the resource"
                        },
                        "kind": {
                            "type": "string",
                            "description": "The kind of the resource",
                            "enum": [%s]
                        }
                    },
                    "required": ["name", "kind"]
                }
                """.formatted(
                        SUPPORTED_KINDS.stream().map(kind -> "\"" + kind + "\"")
                                .collect(Collectors.joining(",")));
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

        String resourceName = requestNode.get("name").getAsString();
        String resourceKind = requestNode.get("kind").getAsString();

        cx.log(INFO, "Looking for %s named %s".formatted(resourceKind, resourceName));
        String projectPath = cx.projectPath();
        Path projectDir = Paths.get(projectPath);

        try {
            if (!Files.exists(projectDir) || !Files.isDirectory(projectDir)) {
                cx.log(DEBUG, "Project directory does not exist or is not accessible: " + projectPath);
                return "Error: Project directory does not exist or is not accessible: " + projectPath;
            }
        } catch (Exception e) {
            cx.log(DEBUG, "Exception while trying to check for file: %s".formatted(projectPath));
            return "Error: Project directory does not exist or is not accessible: " + projectPath;
        }

        List<Path> xmlFiles;
        try (Stream<Path> paths = Files.walk(projectDir)) {
            xmlFiles =
                    paths.filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".xml"))
                            .filter(path -> !path.getFileName().toString().equals("pom.xml")).filter(path -> {
                                String pathStr = path.toString();
                                String projectPathStr = projectDir.toString();
                                String relativePath = pathStr.substring(projectPathStr.length());
                                return !relativePath.contains("/src/test/") &&
                        !relativePath.contains("/mock-services/");
                            }).toList();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        for (Path xmlFile : xmlFiles) {
            if (matchesResource(xmlFile, resourceName, resourceKind, cx)) {
                cx.log(INFO, "Found %s named %s at %s".formatted(resourceKind, resourceName, xmlFile));
                try {
                    return Files.readString(xmlFile);
                } catch (Exception e) {
                    cx.log(DEBUG, "Error reading file content: " + e.getMessage());
                    return "Error reading file content: " + e.getMessage();
                }
            }
        }
        cx.log(INFO, "Couldn't find %s named %s using a placeholder".formatted(resourceKind, resourceName));
        return "Couldn't find %s named %s use a appropriate placeholder".formatted(resourceKind, resourceName);
    }

    /**
     * Validates the input JSON for required fields and values.
     * @param requestNode the parsed input JSON
     * @return error message if invalid, or null if valid
     */
    private String validateInput(JsonObject requestNode) {
        if (!requestNode.has("name")) {
            return "Error: 'name' parameter is required";
        }
        if (!requestNode.has("kind")) {
            return "Error: 'kind' parameter is required";
        }
        String resourceName = requestNode.get("name").getAsString();
        String resourceKind = requestNode.get("kind").getAsString();
        if (resourceName.trim().isEmpty()) {
            return "Error: Resource name cannot be empty";
        }
        if (SUPPORTED_KINDS.stream().noneMatch(kind -> kind.equalsIgnoreCase(resourceKind))) {
            return "Error: Unsupported resource kind '" + resourceKind + "'. Supported kinds: " + SUPPORTED_KINDS;
        }
        return null;
    }

    private boolean matchesResource(Path xmlFile, String resourceName, String resourceKind, ToolContext cx) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();

            org.w3c.dom.Document doc = builder.parse(xmlFile.toFile());
            org.w3c.dom.Element root = doc.getDocumentElement();

            if (root == null) {
                return false;
            }

            String rootElementName = root.getLocalName();
            if (rootElementName == null) {
                rootElementName = root.getNodeName();
            }

            if (!resourceKind.equalsIgnoreCase(rootElementName)) {
                return false;
            }

            String nameAttribute = getNameAttribute(root, resourceKind);
            return resourceName.equalsIgnoreCase(nameAttribute);

        } catch (Exception e) {
            cx.log(DEBUG, "Error parsing XML file " + xmlFile + ": " + e.getMessage());
            return false;
        }
    }

    private String getNameAttribute(org.w3c.dom.Element element, String resourceKind) {
        if ("localEntry".equals(resourceKind)) {
            return element.getAttribute("key");
        } else {
            return element.getAttribute("name");
        }
    }
}
