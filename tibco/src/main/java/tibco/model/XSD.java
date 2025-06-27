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

package tibco.model;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public record XSD(Element type, org.w3c.dom.Element element) {

    public Type.Schema toSchema() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();
        org.w3c.dom.Element wrapped = wrapElement(doc, element, "schema");
        String elementName = this.type.name()
                .orElseThrow(() -> new IllegalStateException("XSD element must have a name to create schema"));
        return new Type.Schema(wrapped, List.of(new Type.Schema.SchemaXsdType(elementName, this.type.type())));
    }

    private static org.w3c.dom.Element wrapElement(Document doc, org.w3c.dom.Element originalElement,
                                                   String wrapperTagName) {
        org.w3c.dom.Element wrapper = doc.createElement(wrapperTagName);
        Node importedOriginal = doc.importNode(originalElement, true);
        wrapper.appendChild(importedOriginal);
        return wrapper;
    }

    public record Element(Optional<String> name, XSDType type, Optional<Integer> minOccur, Optional<Integer> maxOccur) {

        // Constructor for elements with a name
        public Element(String name, XSDType type, Optional<Integer> minOccur, Optional<Integer> maxOccur) {
            this(Optional.of(name), type, minOccur, maxOccur);
        }

        // Constructor for elements without a name (like xs:any)
        public Element(XSDType type, Optional<Integer> minOccur, Optional<Integer> maxOccur) {
            this(Optional.empty(), type, minOccur, maxOccur);
        }

        Stream<String> names() {
            return Stream.concat(name.stream(), type.names().stream());
        }
    }

    public sealed interface XSDType {

        Collection<String> names();

        enum BasicXSDType implements XSDType {
            STRING("string"),
            INTEGER("integer"),
            INT("int"),
            LONG("long"),
            SHORT("short"),
            DECIMAL("decimal"),
            FLOAT("float"),
            DOUBLE("double"),
            BOOLEAN("boolean"),
            ANYDATA("anydata"),
            ANYTYPE("anyType");

            private final String value;

            BasicXSDType(String value) {
                this.value = value;
            }

            public String getValue() {
                return value;
            }

            public static BasicXSDType parse(String typeStr) {
                if (typeStr == null || typeStr.isEmpty()) {
                    throw new IllegalArgumentException("XSD type string cannot be null or empty");
                }

                String type = typeStr;
                if (type.contains(":")) {
                    type = type.substring(type.indexOf(":") + 1);
                }

                String finalType = type;
                return Arrays.stream(BasicXSDType.values())
                        .filter(basicType -> basicType.getValue().equalsIgnoreCase(finalType))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Unsupported XSD type: " + typeStr));
            }

            @Override
            public Collection<String> names() {
                return List.of(value);
            }
        }

        record ComplexType(XSDType.ComplexType.ComplexTypeBody body) implements XSDType {

            @Override
            public Collection<String> names() {
                return body.elements().stream().flatMap(Element::names).collect(Collectors.toSet());
            }

            public sealed interface ComplexTypeBody {

                List<Element> elements();

                record Sequence(List<Element> elements) implements ComplexType.ComplexTypeBody {

                }
            }

        }
    }
}
