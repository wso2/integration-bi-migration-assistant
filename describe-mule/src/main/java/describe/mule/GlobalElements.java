/*
 *  Copyright (c) 2026, WSO2 LLC. (http://www.wso2.com).
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
package describe.mule;

import describe.model.DescriptionModel.Connection;
import describe.model.DescriptionModel.Import;
import describe.model.DescriptionModel.PropertyRef;
import mule.v4.model.MuleXMLTag;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reads the top-level elements of the main config files: global configs (connections), imports, and the property
 * placeholders used anywhere. Only names are kept, never values.
 */
final class GlobalElements {

    // ${name} in attributes and text, and p('name') or Mule::p("name") in DataWeave
    private static final Pattern PLACEHOLDER = Pattern.compile("\\$\\{([^}]+)}");
    private static final Pattern P_FUNCTION = Pattern.compile("\\bp\\(\\s*['\"]([^'\"]+)['\"]\\s*\\)");
    private static final Set<String> NOT_CONNECTIONS =
            Set.of("flow", "sub-flow", "import", "error-handler", "global-property");

    private final List<Connection> connections = new ArrayList<>();
    private final List<Import> imports = new ArrayList<>();
    private final Set<String> propertyNames = new TreeSet<>();

    List<Connection> connections() {
        return connections;
    }

    List<Import> imports() {
        return imports;
    }

    List<PropertyRef> properties() {
        List<PropertyRef> properties = new ArrayList<>();
        for (String name : propertyNames) {
            properties.add(new PropertyRef(name, name.startsWith("secure::")));
        }
        return properties;
    }

    // Files must come in their final (sorted) order, so the lists are in a stable order
    void read(Element root, Path projectDir) {
        collectPlaceholders(root, propertyNames);
        for (Element element : XmlElements.children(root)) {
            String tag = element.getTagName();
            if (tag.equals("import")) {
                String file = element.getAttribute("file");
                imports.add(new Import(file, isInProject(projectDir, file)));
            } else if (tag.equals("global-property")) {
                // It defines a property; only its name is kept, never its value
                addName(element.getAttribute("name"), propertyNames);
            } else if (!NOT_CONNECTIONS.contains(tag)) {
                Set<String> used = new TreeSet<>();
                collectPlaceholders(element, used);
                connections.add(new Connection(XmlElements.attribute(element, "name"), XmlElements.connector(tag),
                        tag, MuleXMLTag.isCompatible(tag), new ArrayList<>(used)));
            }
        }
    }

    // Mule finds an imported file on the classpath: src/main/mule or src/main/resources
    private static boolean isInProject(Path projectDir, String file) {
        Path main = projectDir.resolve("src").resolve("main");
        return Files.exists(main.resolve("mule").resolve(file))
                || Files.exists(main.resolve("resources").resolve(file));
    }

    private static void collectPlaceholders(Node node, Set<String> names) {
        if (node.getNodeType() == Node.TEXT_NODE || node.getNodeType() == Node.CDATA_SECTION_NODE) {
            addNames(node.getNodeValue(), names);
        }
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); i++) {
                addNames(attributes.item(i).getNodeValue(), names);
            }
        }
        Node child = node.getFirstChild();
        while (child != null) {
            collectPlaceholders(child, names);
            child = child.getNextSibling();
        }
    }

    private static void addNames(String text, Set<String> names) {
        Matcher placeholder = PLACEHOLDER.matcher(text);
        while (placeholder.find()) {
            addName(placeholder.group(1), names);
        }
        Matcher function = P_FUNCTION.matcher(text);
        while (function.find()) {
            addName(function.group(1), names);
        }
    }

    private static void addName(String name, Set<String> names) {
        if (!name.isBlank()) {
            names.add(name.strip());
        }
    }
}
