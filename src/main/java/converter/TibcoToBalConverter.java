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
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
        Collection<TibcoModel.Type> types = null;
        TibcoModel.ProcessInfo processInfo = null;
        Collection<TibcoModel.PartnerLink> partnerLinks = null;
        Collection<TibcoModel.Variable> variables = null;
        TibcoModel.Scope scope = null;
        for (Element element : new ElementIterable(root)) {
            String tag = getTagNameWithoutNameSpace(element);
            switch (tag) {
                case "Types" -> {
                    if (types != null) {
                        throw new ParserException("Multiple Types elements found in the XML", root);
                    }
                    types = parseTypes(element);
                }
                case "ProcessInfo" -> {
                    if (processInfo != null) {
                        throw new ParserException("Multiple ProcessInfo elements found in the XML", root);
                    }
                    processInfo = parseProcessInfo(element);
                }
                case "partnerLinks" -> {
                    if (partnerLinks != null) {
                        throw new ParserException("Multiple partnerLinks elements found in the XML", root);
                    }
                    partnerLinks = parsePartnerLinks(element);
                }
                case "variables" -> {
                    if (variables != null) {
                        throw new ParserException("Multiple variables elements found in the XML", root);
                    }
                    variables = parseVariables(element);
                }
                case "scope" -> {
                    if (scope != null) {
                        throw new ParserException("Multiple scope elements found in the XML", root);
                    }
                    scope = parseScope(element);
                }
                default -> throw new ParserException("Unsupported process member tag: " + tag, root);
            }
        }
        return new TibcoModel.Process(name, types, processInfo, partnerLinks, variables, scope);
    }

    private static TibcoModel.Scope parseScope(Element element) {
        String name = element.getAttribute("name");
        return new TibcoModel.Scope(name,
                ElementIterable.of(element).stream().map(TibcoToBalConverter::parseFlow).toList());
    }

    private static TibcoModel.Scope.Flow parseFlow(Element flow) {
        String name = flow.getAttribute("name");
        Collection<TibcoModel.Scope.Flow.Link> links = null;
        List<TibcoModel.Scope.Flow.Activity> activities = new ArrayList<>();
        for (Element element : ElementIterable.of(flow)) {
            String tag = getTagNameWithoutNameSpace(element);
            switch (tag) {
                case "links" -> {
                    if (links != null) {
                        throw new ParserException("Multiple links elements found in the XML", flow);
                    }
                    links = parseLinks(element);
                }
                case "extensionActivity" -> activities.add(parseExtensionActivity(element));
                case "invoke" -> activities.add(parseInvoke(element));
                default -> throw new ParserException("Unsupported flow member tag: " + tag, flow);
            }
        }
        return new TibcoModel.Scope.Flow(name, links, activities);
    }

    private static TibcoModel.Scope.Flow.Activity.Invoke parseInvoke(Element element) {
        String inputVariable = element.getAttribute("inputVariable");
        String outputVariable = element.getAttribute("outputVariable");
        TibcoModel.Scope.Flow.Activity.Invoke.Operation operation = switch (element.getAttribute("operation")) {
            case "post" -> TibcoModel.Scope.Flow.Activity.Invoke.Operation.POST;
            default -> throw new ParserException("Unsupported invoke operation: " + element.getAttribute("operation"),
                    element);
        };
        String partnerLink = element.getAttribute("partnerLink");
        List<TibcoModel.Scope.Flow.Activity.Invoke.InputBinding> inputBindings = new ArrayList<>();
        Collection<TibcoModel.Scope.Flow.Activity.Invoke.Target> targets = new ArrayList<>();
        Collection<TibcoModel.Scope.Flow.Activity.Invoke.Source> sources = new ArrayList<>();
        for (Element each : ElementIterable.of(element)) {
            String tag = getTagNameWithoutNameSpace(each);
            switch (tag) {
                case "inputBinding" -> inputBindings.add(
                        new TibcoModel.Scope.Flow.Activity.Invoke.InputBinding(parseExpressionNode(each)));
                case "inputBindings" -> {
                    // TODO: what is the difference in partBindings
                    for (Element partBinding : ElementIterable.of(each)) {
                        inputBindings.add(new TibcoModel.Scope.Flow.Activity.Invoke.InputBinding(
                                parseExpressionNode(partBinding)));
                    }
                }
                case "targets" -> {
                    for (Element target : ElementIterable.of(each)) {
                        targets.add(new TibcoModel.Scope.Flow.Activity.Invoke.Target(target.getAttribute("linkName")));
                    }
                }
                case "sources" -> {
                    for (Element source : ElementIterable.of(each)) {
                        sources.add(new TibcoModel.Scope.Flow.Activity.Invoke.Source(source.getAttribute("linkName")));
                    }
                }
                default -> throw new ParserException("Unsupported invoke element tag: " + tag, element);
            }
        }
        return new TibcoModel.Scope.Flow.Activity.Invoke(inputVariable, outputVariable, operation, partnerLink,
                inputBindings, targets, sources);
    }

    private static TibcoModel.Scope.Flow.Activity.Expression parseExpressionNode(Element node) {
        String language = node.getAttribute("expressionLanguage");
        if (language.contains("xslt")) {
            return parseXSLTExpression(node);
        }
        throw new ParserException("Unsupported expression language: " + language, node);
    }

    private static TibcoModel.Scope.Flow.Activity.Expression.XSLT parseXSLTExpression(Element node) {
        String expression;
        if (node.hasAttribute("expression")) {
            expression = node.getAttribute("expression");
        } else {
            expression = node.getTextContent();
        }
        expression = unEscapeXml(expression);
        return new TibcoModel.Scope.Flow.Activity.Expression.XSLT(expression);
    }

    private static String unEscapeXml(String escapedXml) {
        return escapedXml.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "&")
                .replaceAll("&quot;", "\"").replaceAll("&apos;", "'");
    }

    private static TibcoModel.Scope.Flow.Activity parseExtensionActivity(Element element) {
        Element activity = expectNChildren(element, 1).iterator().next();
        switch (getTagNameWithoutNameSpace(activity)) {
            case "receiveEvent" -> {
                return parseReceiveEvent(activity);
            }
            case "activityExtension" -> {
                return parseActivityExtension(activity);
            }
            default -> throw new ParserException(
                    "Unsupported extension activity tag: " + getTagNameWithoutNameSpace(activity), element);
        }
    }

    private static TibcoModel.Scope.Flow.Activity.ActivityExtension parseActivityExtension(Element activity) {
        TibcoModel.Scope.Flow.Activity.Expression expression = parseExpressionNode(activity);
        String inputVariable = activity.getAttribute("inputVariable");
        Collection<TibcoModel.Scope.Flow.Activity.Target> targets =
                ElementIterable.of(getFirstChildWithTag(activity, "targets")).stream()
                        .map(each -> new TibcoModel.Scope.Flow.Activity.Invoke.Target(each.getAttribute("linkName")))
                        .toList();
        Collection<TibcoModel.Scope.Flow.Activity.InputBinding> inputBindings =
                ElementIterable.of(getFirstChildWithTag(activity, "inputBindings")).stream()
                        .map(each -> new TibcoModel.Scope.Flow.Activity.Invoke.InputBinding(parseExpressionNode(each)))
                        .toList();
        TibcoModel.Scope.Flow.Activity.ActivityExtension.Config config =
                parseActivityExtensionConfig(getFirstChildWithTag(activity, "config"));
        return new TibcoModel.Scope.Flow.Activity.ActivityExtension(expression, inputVariable, targets, inputBindings,
                config);
    }

    private static TibcoModel.Scope.Flow.Activity.ActivityExtension.Config parseActivityExtensionConfig(
            Element config) {
        return new TibcoModel.Scope.Flow.Activity.ActivityExtension.Config();
    }

    private static TibcoModel.Scope.Flow.Activity.ReceiveEvent parseReceiveEvent(Element activity) {
        boolean createInstance = activity.getAttribute("createInstance").equals("yes");
        float eventTimeout = expectFloatAttribute(activity, "eventTimeout");
        String variable = activity.getAttribute("variable");
        Collection<TibcoModel.Scope.Flow.Activity.ReceiveEvent.Source> sources =
                ElementIterable.of(getFirstChildWithTag(activity, "sources")).stream()
                        .map(each -> new TibcoModel.Scope.Flow.Activity.ReceiveEvent.Source(
                                each.getAttribute("linkName"))).toList();

        return new TibcoModel.Scope.Flow.Activity.ReceiveEvent(createInstance, eventTimeout, variable, sources);
    }

    private static Collection<TibcoModel.Scope.Flow.Link> parseLinks(Element element) {
        return ElementIterable.of(element).stream()
                .map(link -> new TibcoModel.Scope.Flow.Link(link.getAttribute("name"))).toList();
    }

    private static Collection<TibcoModel.Variable> parseVariables(Element element) {
        return ElementIterable.of(element).stream().map(TibcoToBalConverter::parseVariable).toList();
    }

    private static TibcoModel.Variable parseVariable(Element element) {
        String name = element.getAttribute("name");
        boolean isInternal = getAttributeIgnoringNamespace(element, "internal").equals("true");
        return new TibcoModel.Variable(name, isInternal);
    }

    private static String getAttributeIgnoringNamespace(Element element, String attributeName) {
        var attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            var attribute = attributes.item(i);
            String nameWithoutNamespace = getTagNameWithoutNameSpaceInner(attribute.getNodeName());
            if (nameWithoutNamespace.equals(attributeName)) {
                return attribute.getNodeValue();
            }
        }
        throw new ParserException("Attribute not found: " + attributeName, element);
    }

    private static Collection<TibcoModel.PartnerLink> parsePartnerLinks(Element element) {
        return ElementIterable.of(element).stream().map(TibcoToBalConverter::parsePartnerLink).toList();
    }

    private static TibcoModel.PartnerLink parsePartnerLink(Element element) {
        String name = element.getAttribute("name");
        return finishParsingTibexReferenceBinding(getFirstChildWithTag(element, "ReferenceBinding"), name);
    }

    private static TibcoModel.PartnerLink finishParsingTibexReferenceBinding(Element referenceBinding, String name) {
        return finishParsingTibexBinding(getFirstChildWithTag(referenceBinding, "binding"), name);
    }

    private static TibcoModel.PartnerLink finishParsingTibexBinding(Element binding, String name) {
        return finishParsingBWBaseBinding(getFirstChildWithTag(binding, "BWBaseBinding"), name);
    }

    private static TibcoModel.PartnerLink finishParsingBWBaseBinding(Element bwBaseBinding, String name) {
        return finishParsingReferenceBinding(getFirstChildWithTag(bwBaseBinding, "referenceBinding"), name);
    }

    private static TibcoModel.PartnerLink finishParsingReferenceBinding(Element referenceBinding, String name) {
        return finishParsingBinding(getFirstChildWithTag(referenceBinding, "binding"), name);
    }

    private static TibcoModel.PartnerLink finishParsingBinding(Element binding, String name) {
        String basePath = binding.getAttribute("basePath");
        String path = binding.getAttribute("path");
        TibcoModel.PartnerLink.Binding.Connector connector =
                TibcoModel.PartnerLink.Binding.Connector.from(binding.getAttribute("connector"));
        TibcoModel.PartnerLink.Binding.Operation operation =
                parseBindingOperation(getFirstChildWithTag(binding, "operation"));
        return new TibcoModel.PartnerLink(name,
                new TibcoModel.PartnerLink.Binding(new TibcoModel.PartnerLink.Binding.Path(basePath, path), connector,
                        operation));
    }

    private static TibcoModel.PartnerLink.Binding.Operation parseBindingOperation(Element operation) {
        TibcoModel.PartnerLink.Binding.Operation.Method method =
                TibcoModel.PartnerLink.Binding.Operation.Method.from(operation.getAttribute("httpMethod"));

        TibcoModel.PartnerLink.Binding.Operation.RequestEntityProcessing requestEntityProcessing =
                TibcoModel.PartnerLink.Binding.Operation.RequestEntityProcessing.from(
                        operation.getAttribute("requestEntityProcessing"));
        TibcoModel.PartnerLink.Binding.Operation.MessageStyle requestStyle =
                TibcoModel.PartnerLink.Binding.Operation.MessageStyle.from(operation.getAttribute("requestStyle"));
        TibcoModel.PartnerLink.Binding.Operation.MessageStyle responseStyle =
                TibcoModel.PartnerLink.Binding.Operation.MessageStyle.from(operation.getAttribute("responseStyle"));
        TibcoModel.PartnerLink.Binding.Operation.Format clientFormat =
                parseFormat(getFirstChildWithTag(operation, "clientFormat"));

        TibcoModel.PartnerLink.Binding.Operation.Format clientRequestFormat =
                parseFormat(getFirstChildWithTag(operation, "clientRequestFormat"));
        List<TibcoModel.PartnerLink.Binding.Operation.Parameter> parameters =
                tryGetFirstChildWithTag(operation, "parameters").map(TibcoToBalConverter::parseParameters)
                        .orElseGet(Collections::emptyList);
        return new TibcoModel.PartnerLink.Binding.Operation(method, requestEntityProcessing, requestStyle,
                responseStyle, clientFormat, clientRequestFormat, parameters);
    }

    private static List<TibcoModel.PartnerLink.Binding.Operation.Parameter> parseParameters(Element parameters) {
        return ElementIterable.of(parameters).stream().map(TibcoToBalConverter::parseParameter).toList();
    }

    private static TibcoModel.PartnerLink.Binding.Operation.Parameter parseParameter(Element element) {
        throw new UnsupportedOperationException("unimplemented");
    }

    private static TibcoModel.PartnerLink.Binding.Operation.Format parseFormat(Element clientFormat) {
        String body = clientFormat.getTextContent();
        if (body.equals("json")) {
            return TibcoModel.PartnerLink.Binding.Operation.Format.JSON;
        } else {
            throw new ParserException("Unsupported format: " + body, clientFormat);
        }
    }

    private static Element getFirstChildWithTag(Element element, String tag) {
        return tryGetFirstChildWithTag(element, tag).orElseThrow(
                () -> new ParserException("Child with tag: " + tag + " not found", element));
    }

    private static Optional<Element> tryGetFirstChildWithTag(Element element, String tag) {
        // TODO: use predicate things we have already implemented
        return ElementIterable.of(element).stream().filter(child -> getTagNameWithoutNameSpace(child).equals(tag))
                .findFirst();
    }

    private static TibcoModel.ProcessInfo parseProcessInfo(Element element) {
        boolean callable = expectBooleanAttribute(element, "callable");
        boolean extraErrorVars = expectBooleanAttribute(element, "extraErrorVars");
        // TODO: how multiple modifiers represented?
        Set<TibcoModel.ProcessInfo.Modifier> modifiers = EnumSet.noneOf(TibcoModel.ProcessInfo.Modifier.class);
        String modifiersStr = element.getAttribute("modifiers");
        if (modifiersStr.contains("public")) {
            modifiers.add(TibcoModel.ProcessInfo.Modifier.PUBLIC);
        }
        boolean scalable = expectBooleanAttribute(element, "scalable");
        boolean singleton = expectBooleanAttribute(element, "singleton");
        boolean stateless = expectBooleanAttribute(element, "stateless");
        TibcoModel.ProcessInfo.Type type = switch (element.getAttribute("type")) {
            case "IT" -> TibcoModel.ProcessInfo.Type.IT;
            default -> throw new ParserException("Unsupported process type: " + element.getAttribute("type"), element);
        };
        return new TibcoModel.ProcessInfo(callable, extraErrorVars, modifiers, scalable, singleton, stateless, type);
    }

    private static Collection<TibcoModel.Type> parseTypes(Element element) {
        List<TibcoModel.Type> members = new ArrayList<>();
        for (Element child : ElementIterable.of(element)) {
            String tag = getTagNameWithoutNameSpace(child);
            switch (tag) {
                case "schema" -> members.add(parseSchema(child));
                case "definitions" -> members.add(parseWSDLDefinition(child));
                default -> throw new ParserException("Unsupported types member tag: " + tag, element);
            }
        }
        return members;
    }

    private static TibcoModel.Type.Schema parseSchema(Element element) {
        List<TibcoModel.Type.Schema.ComplexType> types = new ArrayList<>();
        List<TibcoModel.Type.Schema.Element> elements = new ArrayList<>();
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
        return new TibcoModel.Type.Schema(types, elements, imports);
    }

    private static TibcoModel.NameSpace parseImport(Element each) {
        return new TibcoModel.NameSpace(each.getAttribute("namespace"));
    }

    private static TibcoModel.Type.Schema.Element parseSchemaElement(Element each) {
        String name = each.getAttribute("name");
        String typeName = each.getAttribute("type");
        return new TibcoModel.Type.Schema.Element(name, TibcoModel.Type.Schema.TibcoType.of(typeName));
    }

    private static TibcoModel.Type.Schema.ComplexType parseType(Element element) {
        Element complexType = expectTag(element, "complexType");
        return parseComplexType(complexType);
    }

    private static TibcoModel.Type.Schema.ComplexType parseComplexType(Element element) {
        String name = element.getAttribute("name");
        Element child = expectNChildren(element, 1).iterator().next();
        TibcoModel.Type.Schema.ComplexType.Body body = switch (getTagNameWithoutNameSpace(child)) {
            case "sequence" -> parseComplexTypeSequence(child);
            case "complexContent" -> parseComplexContent(child);
            case "choice" -> parseComplexTypeChoice(child);
            default ->
                    throw new ParserException("Unsupported complex type body tag: " + getTagNameWithoutNameSpace(child),
                            element);
        };
        return new TibcoModel.Type.Schema.ComplexType(name, body);
    }

    private static TibcoModel.Type.Schema.ComplexType.Body parseComplexTypeSequence(Element sequence) {
        Collection<TibcoModel.Type.Schema.ComplexType.SequenceBody.Member> elements =
                parseSequence(sequence, TibcoToBalConverter::parseComplexTypeSequenceElement);
        return new TibcoModel.Type.Schema.ComplexType.SequenceBody(elements);
    }

    private static TibcoModel.Type.Schema.ComplexType.SequenceBody.Member parseComplexTypeSequenceElement(
            Element element) {
        String tag = getTagNameWithoutNameSpace(element);
        return switch (tag) {
            case "element" -> parseComplexTypeElement(element);
            case "any" -> {
                boolean isLax = Boolean.parseBoolean(element.getAttribute("processContents"));
                yield new TibcoModel.Type.Schema.ComplexType.SequenceBody.Member.Rest(isLax);
            }
            default -> throw new ParserException("Unsupported complex type element tag: " + tag, element);
        };
    }

    private static TibcoModel.Type.Schema.ComplexType.SequenceBody.Member.Element parseComplexTypeElement(
            Element element) {
        String elementName = element.getAttribute("name");
        String typeName = element.getAttribute("type");
        return new TibcoModel.Type.Schema.ComplexType.SequenceBody.Member.Element(elementName,
                TibcoModel.Type.Schema.TibcoType.of(typeName));
    }

    private static TibcoModel.Type.Schema.ComplexType.ComplexContent parseComplexContent(Element element) {
        return new TibcoModel.Type.Schema.ComplexType.ComplexContent(
                parseComplexContentExtension(expectNChildren(element, 1).iterator().next()));
    }

    private static TibcoModel.Type.Schema.ComplexType.Choice parseComplexTypeChoice(Element element) {
        Collection<TibcoModel.Type.Schema.ComplexType.Choice.Element> elements =
                parseSequence(element, TibcoToBalConverter::parseComplexTypeChoiceElement);
        return new TibcoModel.Type.Schema.ComplexType.Choice(elements);
    }

    private static TibcoModel.Type.Schema.ComplexType.Choice.Element parseComplexTypeChoiceElement(Element element) {
        String typeName = element.getAttribute("ref");
        int minOccurs = expectIntAttribute(element, "minOccurs");
        int maxOccurs = expectIntAttribute(element, "maxOccurs");
        return new TibcoModel.Type.Schema.ComplexType.Choice.Element(maxOccurs, minOccurs,
                TibcoModel.Type.Schema.TibcoType.of(typeName));
    }

    private static TibcoModel.Type.WSDLDefinition parseWSDLDefinition(Element element) {
        Map<String, String> namespaces = new HashMap<>();
        TibcoModel.Type.WSDLDefinition.PartnerLinkType partnerLinkType = null;
        List<TibcoModel.NameSpace> imports = new ArrayList<>();
        List<TibcoModel.Type.WSDLDefinition.Message> messages = new ArrayList<>();
        List<TibcoModel.Type.WSDLDefinition.PortType> portTypes = new ArrayList<>();
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
        return new TibcoModel.Type.WSDLDefinition(namespaces, partnerLinkType, imports, messages, portTypes);
    }

    private static TibcoModel.Type.WSDLDefinition.PortType parsePortType(Element element) {
        String name = element.getAttribute("name");
        // FIXME:
        String apiPath = "";
        Element operation = getFistMatchingChild(element,
                (child) -> getTagNameWithoutNameSpace(child).equals("operation")).orElseThrow(
                () -> new ParserException("Operation not found in portType", element));
        TibcoModel.Type.WSDLDefinition.PortType.Operation portOperation = parsePortOperation(operation);
        return new TibcoModel.Type.WSDLDefinition.PortType(name, apiPath, portOperation);
    }

    private static TibcoModel.Type.WSDLDefinition.PortType.Operation parsePortOperation(Element operation) {

        record MessageNamePair(TibcoModel.NameSpaceValue message, String name) {

            static MessageNamePair from(Element element) {
                return new MessageNamePair(TibcoModel.NameSpaceValue.from(element.getAttribute("message")),
                        element.getAttribute("name"));
            }
        }
        String name = operation.getAttribute("name");
        TibcoModel.Type.WSDLDefinition.PortType.Operation.Input input = null;
        TibcoModel.Type.WSDLDefinition.PortType.Operation.Output output = null;
        List<TibcoModel.Type.WSDLDefinition.PortType.Operation.Fault> faults = new ArrayList<>();
        for (Element child : ElementIterable.of(operation)) {
            MessageNamePair messageNamePair = MessageNamePair.from(child);
            String tag = getTagNameWithoutNameSpace(child);
            switch (tag) {
                case "input" -> {
                    input = new TibcoModel.Type.WSDLDefinition.PortType.Operation.Input(messageNamePair.message,
                            messageNamePair.name);
                }
                case "output" -> {
                    output = new TibcoModel.Type.WSDLDefinition.PortType.Operation.Output(messageNamePair.message,
                            messageNamePair.name);
                }
                case "fault" -> {
                    faults.add(new TibcoModel.Type.WSDLDefinition.PortType.Operation.Fault(messageNamePair.message,
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
        return new TibcoModel.Type.WSDLDefinition.PortType.Operation(name, input, output, faults);
    }

    private static TibcoModel.Type.WSDLDefinition.Message parseMessage(Element element) {
        String name = element.getAttribute("name");
        List<TibcoModel.Type.WSDLDefinition.Message.Part> parts =
                parseSequence(element, TibcoToBalConverter::parseMessagePart);
        return new TibcoModel.Type.WSDLDefinition.Message(name, parts);
    }

    private static TibcoModel.Type.WSDLDefinition.Message.Part parseMessagePart(Element node) {
        String name = node.getAttribute("name");
        Optional<TibcoModel.NameSpaceValue> element = node.hasAttribute("element") ?
                Optional.of(TibcoModel.NameSpaceValue.from(node.getAttribute("element"))) : Optional.empty();
        // TODO: is this correct?
        boolean multipleNamespaces = node.hasAttribute("multipleNamespaces");
        return new TibcoModel.Type.WSDLDefinition.Message.Part(name, element, multipleNamespaces);
    }

    private static TibcoModel.Type.WSDLDefinition.PartnerLinkType parsePartnerLinkType(Element element) {
        String name = element.getAttribute("name");
        Element role = expectNChildren(element, 1).iterator().next();
        String roleName = role.getAttribute("name");
        String portTypeName = role.getAttribute("portType");
        return new TibcoModel.Type.WSDLDefinition.PartnerLinkType(name,
                new TibcoModel.Type.WSDLDefinition.PartnerLinkType.Role(roleName,
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
        String tagName = element.getTagName();
        return getTagNameWithoutNameSpaceInner(tagName);
    }

    private static String getTagNameWithoutNameSpaceInner(String tagName) {
        String[] parts = tagName.split(":");
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

    private static TibcoModel.Type.Schema.ComplexType.ComplexContent.Extension parseComplexContentExtension(
            Element element) {
        TibcoModel.Type.Schema.TibcoType base = TibcoModel.Type.Schema.TibcoType.of(element.getAttribute("base"));
        Collection<TibcoModel.Type.Schema.ComplexType.SequenceBody.Member.Element> elements = new ArrayList<>();
        for (Element child : ElementIterable.of(element)) {
            String tag = getTagNameWithoutNameSpace(child);
            if (tag.equals("sequence")) {
                elements = parseSequence(child, TibcoToBalConverter::parseComplexTypeElement);
                break;
            } else {
                throw new ParserException("Unsupported complex content extension tag: " + tag, element);
            }
        }
        return new TibcoModel.Type.Schema.ComplexType.ComplexContent.Extension(base, elements);
    }

    private static int expectIntAttribute(Element element, String attributeName) {
        try {
            return Integer.parseInt(element.getAttribute(attributeName));
        } catch (NumberFormatException e) {
            throw new ParserException("Invalid integer value for attribute: " + attributeName, element);
        }
    }

    private static float expectFloatAttribute(Element element, String attributeName) {
        try {
            return Float.parseFloat(element.getAttribute(attributeName));
        } catch (NumberFormatException e) {
            throw new ParserException("Invalid float value for attribute: " + attributeName, element);
        }
    }

    private static boolean expectBooleanAttribute(Element element, String attributeName) {
        String attribute = element.getAttribute(attributeName).toLowerCase();
        return switch (attribute) {
            case "true" -> true;
            case "false" -> false;
            default -> throw new ParserException("Invalid boolean value for attribute: " + attributeName, element);
        };
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