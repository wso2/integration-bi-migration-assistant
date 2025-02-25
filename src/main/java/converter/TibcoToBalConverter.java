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
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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

    private static TibcoModel.Process parseProcess(Element root) {
        String name = root.getAttribute("name");
        TibcoModel.Types types = null;
        for (Element element : new ElementIterable(root)) {
            String tag = getTagNameWithoutNameSpace(element);
            switch (tag) {
                case "Types" -> {
                    if (types != null) {
                        throw new ParserException("Multiple Types elements found in the XML", root);
                    }
                    types = parseTypes(element);
                }
                default -> {
                    throw new ParserException("Unsupported process member tag: " + tag, root);
                }
            }
        }
        return new TibcoModel.Process(name, types);
    }

    private static TibcoModel.Types parseTypes(Element element) {
        List<TibcoModel.Types.Members> members = new ArrayList<>();
        for (Element child : ElementIterable.of(element)) {
            String tag = getTagNameWithoutNameSpace(child);
            switch (tag) {
                case "schema" -> members.add(parseSchema(child));
                case "definitions" -> members.add(parseWSDLDefinition(child));
                default -> throw new ParserException("Unsupported types member tag: " + tag, element);
            }
        }
        return new TibcoModel.Types(members);
    }

    private static TibcoModel.Types.Schema parseSchema(Element element) {
        List<TibcoModel.Types.Schema.Type> types = new ArrayList<>();
        List<TibcoModel.Types.Schema.Element> elements = new ArrayList<>();
        List<TibcoModel.NameSpace> imports = new ArrayList<>();
        for (Element each : ElementIterable.of(element)) {
            String tag = getTagNameWithoutNameSpace(each);
            switch (tag) {
                case "element" -> elements.add(parseSchemaElement(each));
                case "complexType" -> types.add(parseType(each));
                case "import" -> imports.add(parseImport(each));
                default -> throw new ParserException("Unsupported schema element tag: " + tag, element);
            }
        }
        return new TibcoModel.Types.Schema(types, elements, imports);
    }

    private static TibcoModel.NameSpace parseImport(Element each) {
        return new TibcoModel.NameSpace(each.getAttribute("namespace"));
    }

    private static TibcoModel.Types.Schema.Element parseSchemaElement(Element each) {
        String name = each.getAttribute("name");
        String typeName = each.getAttribute("type");
        return new TibcoModel.Types.Schema.Element(name, TibcoModel.Types.Schema.TibcoType.of(typeName));
    }

    private static TibcoModel.Types.Schema.Type parseType(Element element) {
        String tag = getTagNameWithoutNameSpace(element);
        switch (tag) {
            case "complexType" -> {
                return parseComplexType(element);
            }
            default -> throw new ParserException("Unsupported type tag: " + tag, element);
        }
    }

    private static TibcoModel.Types.Schema.ComplexType parseComplexType(Element element) {
        String name = element.getAttribute("name");
        Element child = expectNChildren(element, 1).iterator().next();
        TibcoModel.Types.Schema.ComplexType.Body body = switch (getTagNameWithoutNameSpace(child)) {
            case "sequence" -> parseComplexTypeSequence(child);
            case "complexContent" -> parseComplexContent(child);
            case "choice" -> parseComplexTypeChoice(child);
            default -> throw new ParserException(
                    "Unsupported complex type body tag: " + getTagNameWithoutNameSpace(child), element);
        };
        return new TibcoModel.Types.Schema.ComplexType(name, body);
    }

    private static TibcoModel.Types.Schema.ComplexType.Body parseComplexTypeSequence(Element sequence) {
        // FIXME: extract method
        Collection<TibcoModel.Types.Schema.ComplexType.SequenceBody.Element> elements =
                parseSequence(sequence, (child) -> {
                    String tag = getTagNameWithoutNameSpace(child);
                    return switch (tag) {
                        case "element" -> parseComplexTypeElement(child);
                        case "any" -> {
                            boolean isLax = Boolean.parseBoolean(child.getAttribute("processContents"));
                            yield new TibcoModel.Types.Schema.ComplexType.Rest(isLax);
                        }
                        default -> throw new ParserException("Unsupported complex type element tag: " + tag, sequence);
                    };
                });
        return new TibcoModel.Types.Schema.ComplexType.SequenceBody(elements);
    }

    private static TibcoModel.Types.Schema.ComplexType.Element parseComplexTypeElement(Element element) {
        String elementName = element.getAttribute("name");
        String typeName = element.getAttribute("type");
        return new TibcoModel.Types.Schema.ComplexType.Element(elementName,
                TibcoModel.Types.Schema.TibcoType.of(typeName));
    }

    private static TibcoModel.Types.Schema.ComplexType.ComplexContent parseComplexContent(Element element) {
        return new TibcoModel.Types.Schema.ComplexType.ComplexContent(
                parseComplexContentExtension(expectNChildren(element, 1).iterator().next()));
    }

    private static TibcoModel.Types.Schema.ComplexType.Choice parseComplexTypeChoice(Element element) {
        Collection<TibcoModel.Types.Schema.ComplexType.Choice.Element> elements = parseSequence(element, (child) -> {
            String typeName = child.getAttribute("ref");
            int minOccurs = getIntAttribute(child, "minOccurs");
            int maxOccurs = getIntAttribute(child, "maxOccurs");
            return new TibcoModel.Types.Schema.ComplexType.Choice.Element(maxOccurs, minOccurs,
                    TibcoModel.Types.Schema.TibcoType.of(typeName));
        });
        return new TibcoModel.Types.Schema.ComplexType.Choice(elements);
    }

    private static TibcoModel.Types.WSDLDefinition parseWSDLDefinition(Element element) {
        Map<String, String> namespaces = new HashMap<>();
        TibcoModel.Types.WSDLDefinition.PartnerLinkType partnerLinkType = null;
        List<TibcoModel.NameSpace> imports = new ArrayList<>();
        List<TibcoModel.Types.WSDLDefinition.Message> messages = new ArrayList<>();
        List<TibcoModel.Types.WSDLDefinition.PortType> portTypes = new ArrayList<>();
        for (Element child : ElementIterable.of(element)) {
            String tag = getTagNameWithoutNameSpace(child);
            switch (tag) {
                case "partnerLinkType" -> {
                    if (partnerLinkType != null) {
                        throw new ParserException("Multiple partnerLinkType elements found in the XML", element);
                    }
                    partnerLinkType = parsePartnerLinkType(child);
                }
                case "import" -> imports.add(parseImport(child));
                case "message" -> messages.add(parseMessage(child));
                case "portType" -> portTypes.add(parsePortType(child));
                default -> throw new ParserException("Unsupported WSDL definition tag: " + tag, element);
            }
        }
        assert partnerLinkType != null;
        return new TibcoModel.Types.WSDLDefinition(namespaces, partnerLinkType, imports, messages, portTypes);
    }

    private static TibcoModel.Types.WSDLDefinition.PortType parsePortType(Element element) {
        String name = element.getAttribute("name");
        // FIXME:
        String apiPath = "";
        Element operation =
                getFistMatchingChild(element, (child) -> getTagNameWithoutNameSpace(child).equals("operation"))
                        .orElseThrow(() -> new ParserException("Operation not found in portType", element));
        TibcoModel.Types.WSDLDefinition.PortType.Operation portOperation = parsePortOperation(operation);
        return new TibcoModel.Types.WSDLDefinition.PortType(name, apiPath, portOperation);
    }

    private static TibcoModel.Types.WSDLDefinition.PortType.Operation parsePortOperation(Element operation) {

        record MessageNamePair(TibcoModel.NameSpaceValue message, String name) {

            static MessageNamePair from(Element element) {
                return new MessageNamePair(TibcoModel.NameSpaceValue.from(element.getAttribute("message")),
                        element.getAttribute("name"));
            }
        }
        String name = operation.getAttribute("name");
        TibcoModel.Types.WSDLDefinition.PortType.Operation.Input input = null;
        TibcoModel.Types.WSDLDefinition.PortType.Operation.Output output = null;
        List<TibcoModel.Types.WSDLDefinition.PortType.Operation.Fault> faults = new ArrayList<>();
        for (Element child : ElementIterable.of(operation)) {
            MessageNamePair messageNamePair = MessageNamePair.from(child);
            String tag = getTagNameWithoutNameSpace(child);
            switch (tag) {
                case "input" -> {
                    input = new TibcoModel.Types.WSDLDefinition.PortType.Operation.Input(messageNamePair.message,
                            messageNamePair.name);
                }
                case "output" -> {
                    output = new TibcoModel.Types.WSDLDefinition.PortType.Operation.Output(messageNamePair.message,
                            messageNamePair.name);
                }
                case "fault" -> {
                    faults.add(new TibcoModel.Types.WSDLDefinition.PortType.Operation.Fault(messageNamePair.message,
                            messageNamePair.name));
                }
                default -> throw new ParserException("Unsupported port operation tag: " + tag, operation);
            }
        }
        if (input == null) {
            throw new ParserException("Input not found in port operation", operation);
        }
        if (output == null) {
            throw new ParserException("Output not found in port operation", operation);
        }
        return new TibcoModel.Types.WSDLDefinition.PortType.Operation(name, input, output, faults);
    }

    private static TibcoModel.Types.WSDLDefinition.Message parseMessage(Element element) {
        String name = element.getAttribute("name");
        List<TibcoModel.Types.WSDLDefinition.Message.Part> parts =
                parseSequence(element, TibcoToBalConverter::parseMessagePart);
        return new TibcoModel.Types.WSDLDefinition.Message(name, parts);
    }

    private static TibcoModel.Types.WSDLDefinition.Message.Part parseMessagePart(Element node) {
        String name = node.getAttribute("name");
        Optional<TibcoModel.NameSpaceValue> element = node.hasAttribute("element")
                ? Optional.of(TibcoModel.NameSpaceValue.from(node.getAttribute("element")))
                : Optional.empty();
        // TODO: is this correct?
        boolean multipleNamespaces = node.hasAttribute("multipleNamespaces");
        return new TibcoModel.Types.WSDLDefinition.Message.Part(name, element, multipleNamespaces);
    }

    private static TibcoModel.Types.WSDLDefinition.PartnerLinkType parsePartnerLinkType(Element element) {
        String name = element.getAttribute("name");
        Element role = expectNChildren(element, 1).iterator().next();
        String roleName = role.getAttribute("name");
        String portTypeName = role.getAttribute("portType");
        return new TibcoModel.Types.WSDLDefinition.PartnerLinkType(name,
                new TibcoModel.Types.WSDLDefinition.PartnerLinkType.Role(roleName,
                        TibcoModel.NameSpaceValue.from(portTypeName)));
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

    private static String getTagNameWithoutNameSpace(Element element) {
        String[] parts = element.getTagName().split(":");
        if (parts.length == 1) {
            return parts[0];
        }
        assert parts.length == 2;
        return parts[1];
    }

    private static Collection<Element> expectNChildren(Element element, int n) {
        List<Element> children = ElementIterable.of(element).stream().toList();
        if (children.size() != n) {
            throw new ParserException("Expected " + n + " children, but found: " + children.size(), element);
        }
        return children;
    }

    private static <E> List<E> parseSequence(Element sequenceNode, Function<Element, E> parseFn) {
        return ElementIterable.of(sequenceNode).stream().map(parseFn).toList();
    }

    private static TibcoModel.Types.Schema.ComplexType.ComplexContent.Extension parseComplexContentExtension(
            Element element) {
        TibcoModel.Types.Schema.TibcoType base = TibcoModel.Types.Schema.TibcoType.of(element.getAttribute("base"));
        Collection<TibcoModel.Types.Schema.ComplexType.Element> elements = new ArrayList<>();
        for (Element child : ElementIterable.of(element)) {
            String tag = getTagNameWithoutNameSpace(child);
            if (tag.equals("sequence")) {
                elements = parseSequence(child, TibcoToBalConverter::parseComplexTypeElement);
                break;
            } else {
                throw new ParserException("Unsupported complex content extension tag: " + tag, element);
            }
        }
        return new TibcoModel.Types.Schema.ComplexType.ComplexContent.Extension(base, elements);
    }

    private static int getIntAttribute(Element element, String attributeName) {
        try {
            return Integer.parseInt(element.getAttribute(attributeName));
        } catch (NumberFormatException e) {
            throw new ParserException("Invalid integer value for attribute: " + attributeName, element);
        }
    }

    private static Element expectTag(Element element, String tag) {
        if (!getTagNameWithoutNameSpace(element).equals(tag)) {
            throw new ParserException("Expected tag: " + tag + ", but found: " + element.getTagName(), element);
        }
        return element;
    }

    private static Optional<Element> getFistMatchingChild(Element element, Predicate<Element> predicate) {
        return ElementIterable.of(element).stream().filter(predicate).findFirst();
    }

    private static String elementToString(Element element) {
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            // Configure the transformer for clean output
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(element);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);

            transformer.transform(source, result);
            return writer.toString();
        } catch (TransformerException e) {
            throw new RuntimeException("Failed to convert element to string", e);
        }
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

    static class ParserException extends RuntimeException {

        public ParserException(String message, Element element) {
            super("[ParseError] : " + message + "\n" + elementToString(element));
        }

    }
}