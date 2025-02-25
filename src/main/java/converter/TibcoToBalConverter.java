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

package converter;

import io.ballerina.compiler.syntax.tree.SyntaxTree;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import tibco.TibcoModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class TibcoToBalConverter {

    private TibcoToBalConverter() {
    }

    public static SyntaxTree convertToBallerina(String xmlFilePath) {
        Element root;
        try {
            root = parseXmlFile(xmlFilePath);
            assert root != null;
        } catch (Exception e) {
            throw new RuntimeException("Error while parsing the XML file: " + xmlFilePath, e);
        }
        TibcoModel.Process process = parseProcess(root);
        assert process != null;
        return null;
    }

    private static Element parseXmlFile(String xmlFilePath)
            throws IOException, SAXException, ParserConfigurationException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlFilePath);

        document.getDocumentElement().normalize();

        return document.getDocumentElement();
    }

    private static TibcoModel.Process parseProcess(Element root) {
        String name = root.getAttribute("name");
        Collection<TibcoModel.Type> types = null;
        for (Element element : new ElementIterable(root)) {
            String tag = getTagNameWithoutNameSpace(element);
            switch (tag) {
                case "Types" -> {
                    if (types != null) {
                        throw new IllegalArgumentException("Multiple Types elements found in the XML");
                    }
                    types = parseTypes(element);
                }
                default -> {
                    throw new UnsupportedOperationException("Unsupported process member tag: " + tag);
                }
            }
        }
        return new TibcoModel.Process(name, types);
    }

    private static String getTagNameWithoutNameSpace(Element element) {
        String[] parts = element.getTagName().split(":");
        if (parts.length == 1) {
            return parts[0];
        }
        assert parts.length == 2;
        return parts[1];
    }

    private static Collection<TibcoModel.Type> parseTypes(Element element) {
        return ElementIterable.of(element).stream()
                .map(each -> expectTag(each, "schema"))
                .flatMap(schema -> ElementIterable.of(schema).stream())
                .filter(each -> getTagNameWithoutNameSpace(each).equals("complexType"))
                .map(TibcoToBalConverter::parseType)
                .toList();
    }

    private static Element expectTag(Element element, String tag) {
        if (!getTagNameWithoutNameSpace(element).equals(tag)) {
            throw new IllegalArgumentException("Expected tag: " + tag + ", but found: " + element.getTagName());
        }
        return element;
    }

    private static TibcoModel.Type parseType(Element element) {
        String tag = getTagNameWithoutNameSpace(element);
        switch (tag) {
            case "complexType" -> {
                return parseComplexType(element);
            }
            default -> {
                throw new UnsupportedOperationException("Unsupported type tag: " + tag);
            }
        }
    }

    private static TibcoModel.ComplexType parseComplexType(Element element) {
        String name = element.getAttribute("name");
        Element sequence = getFistMatchingChild(element, e -> getTagNameWithoutNameSpace(e).equals("sequence"))
                .orElseThrow(() -> new IllegalArgumentException("Sequence element not found in the complex type"));
        Optional<TibcoModel.ComplexType.Rest> rest = Optional.empty();
        List<TibcoModel.ComplexType.Element> elements = new ArrayList<>();
        for (Element child : ElementIterable.of(sequence)) {
            String tag = getTagNameWithoutNameSpace(child);
            switch (tag) {
                case "element" -> elements.add(parseComplexTypeElement(child));
                case "any" -> {
                    if (rest.isPresent()) {
                        throw new IllegalArgumentException("Multiple any elements found in the complex type");
                    }
                    boolean isLax = Boolean.parseBoolean(child.getAttribute("processContents"));
                    rest = Optional.of(new TibcoModel.ComplexType.Rest(isLax));
                }
                default -> throw new UnsupportedOperationException("Unsupported complex type element tag: " + tag);
            }
        }
        return new TibcoModel.ComplexType(name, elements, rest);
    }

    private static Optional<Element> getFistMatchingChild(Element element, Predicate<Element> predicate) {
        return ElementIterable.of(element).stream().filter(predicate).findFirst();
    }

    private static TibcoModel.ComplexType.Element parseComplexTypeElement(Element element) {
        String elementName = element.getAttribute("name");
        String typeName = element.getAttribute("type");
        return new TibcoModel.ComplexType.Element(elementName, TibcoModel.TibcoType.of(typeName));
    }

    private record ElementIterable(Element root) implements Iterable<Element> {

        public static ElementIterable of(Element element) {
            return new ElementIterable(element);
        }

        @Override
        public Iterator<Element> iterator() {
            return new ElementIterator(root.getChildNodes());
        }

        public Stream<Element> stream() {
            return StreamSupport.stream(spliterator(), false);
        }

        private static class ElementIterator implements Iterator<Element> {

            private final NodeList nodeList;
            private int index = 0;

            private ElementIterator(NodeList nodeList) {
                this.nodeList = nodeList;
            }

            @Override
            public boolean hasNext() {
                if (index >= nodeList.getLength()) {
                    return false;
                }
                int localIndex = index;
                while (localIndex < nodeList.getLength()) {
                    Node node = nodeList.item(localIndex);
                    localIndex++;
                    if (node instanceof Element) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public Element next() {
                return advanceToNextElement().orElseThrow();
            }

            private Optional<Element> advanceToNextElement() {
                while (index < nodeList.getLength()) {
                    Node node = nodeList.item(index);
                    index++;
                    if (node instanceof Element element) {
                        return Optional.of(element);
                    }
                }
                return Optional.empty();
            }
        }
    }

}