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

package converter.tibco;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tibco.TibcoModel;

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
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public final class XmlToTibcoModelConverter {

    private static final Logger logger = Logger.getLogger(XmlToTibcoModelConverter.class.getName());
    private static int anonFieldCount = 0;
    private XmlToTibcoModelConverter() {
    }

    public static TibcoModel.Process parseProcess(Element root) {
        String name = root.getAttribute("name");
        Collection<TibcoModel.Type> types = null;
        TibcoModel.ProcessInfo processInfo = null;
        Collection<TibcoModel.PartnerLink> partnerLinks = null;
        Collection<TibcoModel.Variable> variables = null;
        TibcoModel.Scope scope = null;
        Optional<TibcoModel.ProcessInterface> processInterface = Optional.empty();
        Optional<TibcoModel.ProcessTemplateConfigurations> processTemplateConfigurations = Optional.empty();
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
                case "ProcessInterface" -> {
                    if (processInterface.isPresent()) {
                        throw new ParserException("Multiple ProcessInterface elements found in the XML", root);
                    }
                    processInterface = Optional.of(parseProcessInterface(element));
                }
                case "ProcessTemplateConfigurations" -> {
                    if (processTemplateConfigurations.isPresent()) {
                        throw new ParserException("Multiple ProcessTemplateConfigurations elements found in the XML",
                                root);
                    }
                    processTemplateConfigurations = Optional.of(parseProcessTemplateConfigurations(element));
                }
                case "NamespaceRegistry", "Diagram", "import", "extensions" -> {
                    // ignore
                }
                default -> throw new ParserException("Unsupported process member tag: " + tag, root);
            }
        }
        return new TibcoModel.Process(name, types, processInfo, processInterface, processTemplateConfigurations,
                partnerLinks, variables, Optional.ofNullable(scope));
    }

    // TODO: fill this
    private static TibcoModel.ProcessTemplateConfigurations parseProcessTemplateConfigurations(Element element) {
        return new TibcoModel.ProcessTemplateConfigurations();
    }

    private static TibcoModel.ProcessInterface parseProcessInterface(Element element) {
        String context = element.getAttribute("context");
        String input = element.getAttribute("input");
        String output = element.getAttribute("output");
        return new TibcoModel.ProcessInterface(context, input, output);
    }

    private static TibcoModel.Scope parseScope(Element element) {
        String name = element.getAttribute("name");
        return new TibcoModel.Scope(name,
                ElementIterable.of(element).stream().map(XmlToTibcoModelConverter::parseFlow).toList());
    }

    private static TibcoModel.Scope.Flow parseFlow(Element flow) {
        String name = flow.getAttribute("name");
        Collection<TibcoModel.Scope.Flow.Link> links = null;
        List<TibcoModel.Scope.Flow.Activity> activities = new ArrayList<>();
        for (Element element : ElementIterable.of(flow)) {
            String tag = getTagNameWithoutNameSpace(element);
            if (tag.equals("links")) {
                if (links != null) {
                    throw new ParserException("Multiple links elements found in the XML", flow);
                }
                links = parseLinks(element);
            } else {
                activities.add(tryParseActivity(element));
            }
        }
        if (links == null) {
            logger.warning(
                    String.format("Failed to find any links in the flow: %s, maybe failed to parse all activities",
                            name));
            links = List.of();
        }
        return new TibcoModel.Scope.Flow(name, links, activities);
    }

    private static TibcoModel.Scope.Flow.Activity tryParseActivity(Element element) {
        try {
            return parseActivity(element);
        } catch (Exception ex) {
            return parseUnhandledActivity(element, ex.getMessage());
        }
    }

    private static TibcoModel.Scope.Flow.Activity parseActivity(Element element) {
        String tag = getTagNameWithoutNameSpace(element);
        return switch (tag) {
            case "extensionActivity" -> parseExtensionActivity(element);
            case "invoke" -> parseInvoke(element);
            case "pick" -> parsePick(element);
            case "empty" -> parseEmpty(element);
            case "reply" -> parseReply(element);
            default -> throw new ParserException("Unsupported activity tag: " + tag, element);
        };
    }

    private static TibcoModel.Scope.Flow.Activity parseUnhandledActivity(Element element, String reason) {
        Collection<TibcoModel.Scope.Flow.Activity.Target> targets = new ArrayList<>();
        Collection<TibcoModel.Scope.Flow.Activity.Source> sources = new ArrayList<>();

        for (Element child : ElementIterable.of(element)) {
            String tag = getTagNameWithoutNameSpace(child);
            switch (tag) {
                case "targets" -> targets.addAll(parseTargets(child));
                case "sources" -> sources.addAll(ElementIterable.of(child).stream()
                        .map(XmlToTibcoModelConverter::parseSource).toList());
                default -> {
                }
            }
        }

        return new TibcoModel.Scope.Flow.Activity.UnhandledActivity(reason, elementToString(element), sources, targets);
    }

    private static TibcoModel.Scope.Flow.Activity.Reply parseReply(Element element) {
        String name = element.getAttribute("name");
        var operation = TibcoModel.PartnerLink.Binding.Operation.Method.from(element.getAttribute("operation"));
        String partnerLink = element.getAttribute("partnerLink");
        String portType = element.getAttribute("portType");
        List<TibcoModel.Scope.Flow.Activity.InputBinding> inputBindings = new ArrayList<>();
        List<TibcoModel.Scope.Flow.Activity.Target> targets = new ArrayList<>();
        for (Element each : ElementIterable.of(element)) {
            String tag = getTagNameWithoutNameSpace(each);
            switch (tag) {
                case "inputBindings", "inputBinding" -> inputBindings.addAll(parseInputBindings(each));
                case "targets" -> targets.addAll(parseTargets(each));
                default -> throw new ParserException("Unsupported reply element tag: " + tag, element);
            }
        }
        return new TibcoModel.Scope.Flow.Activity.Reply(name, operation, partnerLink, portType, inputBindings, targets);
    }

    private static TibcoModel.Scope.Flow.Activity.Empty parseEmpty(Element element) {
        String name = element.getAttribute("name");
        return new TibcoModel.Scope.Flow.Activity.Empty(name);
    }

    private static TibcoModel.Scope.Flow.Activity.Pick parsePick(Element element) {
        boolean createInstance = expectBooleanAttribute(element, "createInstance");
        TibcoModel.Scope.Flow.Activity.Pick.OnMessage onMessage =
                parseOnMessage(getFirstChildWithTag(element, "onMessage"));
        return new TibcoModel.Scope.Flow.Activity.Pick(createInstance, onMessage);
    }

    private static TibcoModel.Scope.Flow.Activity.Pick.OnMessage parseOnMessage(Element element) {
        var operation = TibcoModel.PartnerLink.Binding.Operation.Method.from(element.getAttribute("operation"));
        String partnerLink = element.getAttribute("partnerLink");
        String portType = element.getAttribute("portType");
        String variable = element.getAttribute("variable");
        TibcoModel.Scope scope = parseScope(getFirstChildWithTag(element, "scope"));
        return new TibcoModel.Scope.Flow.Activity.Pick.OnMessage(operation, partnerLink, portType, variable, scope);
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
        List<TibcoModel.Scope.Flow.Activity.InputBinding> inputBindings = new ArrayList<>();
        Collection<TibcoModel.Scope.Flow.Activity.Target> targets = new ArrayList<>();
        Collection<TibcoModel.Scope.Flow.Activity.Source> sources = new ArrayList<>();
        for (Element each : ElementIterable.of(element)) {
            String tag = getTagNameWithoutNameSpace(each);
            switch (tag) {
                case "inputBinding", "inputBindings" -> inputBindings.addAll(parseInputBindings(each));
                case "targets" -> {
                    for (Element target : ElementIterable.of(each)) {
                        targets.add(parseTarget(target));
                    }
                }
                case "sources" -> {
                    for (Element source : ElementIterable.of(each)) {
                        sources.add(new TibcoModel.Scope.Flow.Activity.Source(source.getAttribute("linkName")));
                    }
                }
                default -> throw new ParserException("Unsupported invoke element tag: " + tag, element);
            }
        }
        return new TibcoModel.Scope.Flow.Activity.Invoke(inputVariable, outputVariable, operation, partnerLink,
                inputBindings, targets, sources);
    }

    private static List<TibcoModel.Scope.Flow.Activity.InputBinding> parseInputBindings(Element element) {
        String tag = getTagNameWithoutNameSpace(element);
        return switch (tag) {
            case "inputBinding" -> List.of(
                    new TibcoModel.Scope.Flow.Activity.InputBinding(parseExpressionNode(element)));
            case "inputBindings" ->
                    ElementIterable.of(element).stream().map(each -> new TibcoModel.Scope.Flow.Activity.InputBinding(
                            parseExpressionNode(each))).toList();
            default -> throw new ParserException("Unsupported input binding tag" + tag, element);
        };
    }

    private static TibcoModel.Scope.Flow.Activity.Expression parseExpressionNode(Element node) {
        String language = node.getAttribute("expressionLanguage");
        if (language.contains("xslt")) {
            return parseXSLTExpression(node);
        } else {
            throw new ParserException("Unsupported expression language: " + language, node);
        }
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
        // Process in specific order to avoid double processing issues
        // First handle &amp; to avoid affecting other replacements
        String result = escapedXml.replaceAll("&amp;", "&");

        // Handle nested quotes: &quot; inside XML should become single quotes to avoid
        // double-escaping
        result = result.replaceAll("&quot;", "'");
        result = result.replaceAll("&apos;", "'");

        // Handle other XML entities
        result = result.replaceAll("&lt;", "<");
        result = result.replaceAll("&gt;", ">");

        result = result.replaceAll("&#xa;", "\n");

        return result;
    }

    private static TibcoModel.Scope.Flow.Activity parseExtensionActivity(Element element) {
        Element activity = expectNChildren(element, 1).iterator().next();
        String tag = getTagNameWithoutNameSpace(activity);
        switch (tag) {
            case "receiveEvent" -> {
                return parseReceiveEvent(activity);
            }
            case "activityExtension" -> {
                return parseActivityExtension(activity);
            }
            case "extActivity" -> {
                return parseExtActivity(activity);
            }
            default -> throw new ParserException(
                    "Unsupported extension activity tag: " + tag, element);
        }
    }

    private static TibcoModel.Scope.Flow.Activity.ExtActivity parseExtActivity(Element element) {
        TibcoModel.Scope.Flow.Activity.Expression expression = parseExpressionNode(element);
        String inputVariable = element.getAttribute("inputVariable");
        String outputVariable = element.getAttribute("outputVariable");
        Collection<TibcoModel.Scope.Flow.Activity.Source> sources = null;
        List<TibcoModel.Scope.Flow.Activity.InputBinding> inputBindings = new ArrayList<>();
        TibcoModel.Scope.Flow.Activity.ExtActivity.CallProcess callProcess = null;
        for (Element each : ElementIterable.of(element)) {
            String tag = getTagNameWithoutNameSpace(each);
            switch (tag) {
                case "sources" -> {
                    if (sources != null) {
                        throw new ParserException("Multiple sources elements found in the XML", element);
                    }
                    sources = ElementIterable.of(getFirstChildWithTag(element, "sources")).stream()
                            .map(XmlToTibcoModelConverter::parseSource).toList();
                }
                case "inputBindings", "inputBinding" -> inputBindings.addAll(parseInputBindings(each));
                case "CallProcess" -> {
                    if (callProcess != null) {
                        throw new ParserException("Multiple CallProcess elements found in the XML", element);
                    }
                    callProcess = parseCallProcesses(each);
                }
                default -> throw new ParserException("Unsupported extActivity element tag: " + tag, element);
            }
        }
        return new TibcoModel.Scope.Flow.Activity.ExtActivity(expression, inputVariable, outputVariable, sources,
                inputBindings, callProcess);
    }

    private static TibcoModel.Scope.Flow.Activity.ExtActivity.CallProcess parseCallProcesses(Element element) {
        String subProcessName = element.getAttribute("subProcessName");
        return new TibcoModel.Scope.Flow.Activity.ExtActivity.CallProcess(subProcessName);
    }

    private static TibcoModel.Scope.Flow.Activity.ActivityExtension parseActivityExtension(Element activity) {
        String inputVariable = activity.getAttribute("inputVariable");
        var outputVariable = tryGetAttributeIgnoringNamespace(activity, "outputVariable");
        Optional<Element> targetsElement = tryGetFirstChildWithTag(activity, "targets");
        Collection<TibcoModel.Scope.Flow.Activity.Target> targets =
                targetsElement.map(ElementIterable::of).map(ElementIterable::stream)
                        .map(s -> s.map(XmlToTibcoModelConverter::parseTarget).toList())
                        .orElse(List.of());

        Optional<Element> sourcesElement = tryGetFirstChildWithTag(activity, "sources");
        Collection<TibcoModel.Scope.Flow.Activity.Source> sources =
                sourcesElement.map(ElementIterable::of).map(ElementIterable::stream)
                        .map(s -> s.map(XmlToTibcoModelConverter::parseSource).toList())
                        .orElse(List.of());
        List<TibcoModel.Scope.Flow.Activity.InputBinding> inputBindings =
                ElementIterable.of(getFirstChildWithTag(activity, "inputBindings")).stream()
                        .map(each -> new TibcoModel.Scope.Flow.Activity.InputBinding(parseExpressionNode(each)))
                        .toList();
        TibcoModel.Scope.Flow.Activity.ActivityExtension.Config config =
                parseActivityExtensionConfig(getFirstChildWithTag(activity, "config"));
        return new TibcoModel.Scope.Flow.Activity.ActivityExtension(inputVariable, outputVariable, targets, sources,
                inputBindings, config);
    }

    private static Collection<TibcoModel.Scope.Flow.Activity.Target> parseTargets(Element each) {
        return ElementIterable.of(each).stream().map(XmlToTibcoModelConverter::parseTarget).toList();
    }

    private static TibcoModel.Scope.Flow.Activity.Target parseTarget(Element each) {
        return new TibcoModel.Scope.Flow.Activity.Target(each.getAttribute("linkName"));
    }

    private static TibcoModel.Scope.Flow.Activity.ActivityExtension.Config parseActivityExtensionConfig(
            Element config) {
        Element activity = getFirstChildWithTag(config, "BWActivity");
        String activityTypeID = activity.getAttribute("activityTypeID");
        TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.ExtensionKind kind =
                TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.ExtensionKind.fromTypeId(activityTypeID);
        return switch (kind) {
            case END -> new TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.End();
            case FILE_WRITE -> new TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.FileWrite();
            case HTTP_SEND -> new TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.HTTPSend();
            case JSON_RENDER -> new TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.JsonOperation(
                    TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.ExtensionKind.JSON_RENDER,
                    TibcoModel.Type.Schema.TibcoType.of("nil"));
            case JSON_PARSER -> new TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.JsonOperation(
                    TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.ExtensionKind.JSON_PARSER,
                    TibcoModel.Type.Schema.TibcoType.of("nil"));
            case SEND_HTTP_RESPONSE -> new TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.SendHTTPResponse();
            case SQL -> parasSqlActivityExtension(config);
        };
    }

    private static TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.SQL parasSqlActivityExtension(
            Element config) {
        Element properties = getFirstChildWithTag(
                getFirstChildWithTag(getFirstChildWithTag(config, "BWActivity"),
                        "activityConfig"), "properties");
        Element value = getFirstChildWithTag(properties, "value");
        String sharedResourceProperty = value.getAttribute("sharedResourceProperty");
        assert !sharedResourceProperty.isEmpty();
        String query = unEscapeXml(value.getAttribute("sqlStatement"));
        List<TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.SQL.SQLParameter> parameters =
                getChildrenWithTag(value, "PreparedParameters")
                        .map(XmlToTibcoModelConverter::parseSqlParameter).toList();
        return new TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.SQL(sharedResourceProperty, query,
                parameters);
    }

    private static TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.SQL.SQLParameter parseSqlParameter(
            Element preparedParameters
    ) {
        return new TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.SQL.SQLParameter(
                preparedParameters.getAttribute("ParameterName"),
                TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.SQL.SQLParameter.SQLType.fromString(
                        preparedParameters.getAttribute("DataTypeDisplayValue")));
    }

    private static TibcoModel.Scope.Flow.Activity.ReceiveEvent parseReceiveEvent(Element activity) {
        boolean createInstance = activity.getAttribute("createInstance").equals("yes");
        float eventTimeout = expectFloatAttribute(activity, "eventTimeout");
        String variable = activity.getAttribute("variable");
        Collection<TibcoModel.Scope.Flow.Activity.Source> sources =
                ElementIterable.of(getFirstChildWithTag(activity, "sources")).stream()
                        .map(XmlToTibcoModelConverter::parseSource).toList();

        return new TibcoModel.Scope.Flow.Activity.ReceiveEvent(createInstance, eventTimeout, variable, sources);
    }

    private static TibcoModel.Scope.Flow.Activity.Source parseSource(Element each) {
        return new TibcoModel.Scope.Flow.Activity.ReceiveEvent.Source(
                each.getAttribute("linkName"));
    }

    private static Collection<TibcoModel.Scope.Flow.Link> parseLinks(Element element) {
        return ElementIterable.of(element).stream()
                .map(link -> new TibcoModel.Scope.Flow.Link(link.getAttribute("name"))).toList();
    }

    private static Collection<TibcoModel.Variable> parseVariables(Element element) {
        return ElementIterable.of(element).stream().map(XmlToTibcoModelConverter::parseVariable).toList();
    }

    private static TibcoModel.Variable parseVariable(Element element) {
        String name = element.getAttribute("name");
        Optional<String> internal = tryGetAttributeIgnoringNamespace(element, "internal");
        boolean isInternal = internal.map(val -> val.equals("true")).orElse(false);
        return new TibcoModel.Variable(name, isInternal);
    }

    private static Optional<String> tryGetAttributeIgnoringNamespace(Element element, String attributeName) {
        var attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            var attribute = attributes.item(i);
            String nameWithoutNamespace = getTagNameWithoutNameSpace(attribute.getNodeName());
            if (nameWithoutNamespace.equals(attributeName)) {
                return Optional.of(attribute.getNodeValue());
            }
        }
        return Optional.empty();
    }

    private static String getAttributeIgnoringNamespace(Element element, String attributeName) {
        return tryGetAttributeIgnoringNamespace(element, attributeName).orElseThrow(
                () -> new ParserException("Attribute not found: " + attributeName, element));
    }

    private static Collection<TibcoModel.PartnerLink> parsePartnerLinks(Element element) {
        return ElementIterable.of(element).stream().map(XmlToTibcoModelConverter::parsePartnerLink).toList();
    }

    private static TibcoModel.PartnerLink parsePartnerLink(Element element) {
        String name = element.getAttribute("name");
        Optional<Element> referenceBinding = tryGetFirstChildWithTag(element, "ReferenceBinding");
        return referenceBinding.map(value -> finishParsingTibexReferenceBinding(value, name))
                .orElseGet(() -> new TibcoModel.PartnerLink(name, Optional.empty()));
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
        return new TibcoModel.PartnerLink(name, Optional.of(
                new TibcoModel.PartnerLink.Binding(new TibcoModel.PartnerLink.Binding.Path(basePath, path), connector,
                        operation)));
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
                tryGetFirstChildWithTag(operation, "parameters").map(XmlToTibcoModelConverter::parseParameters)
                        .orElseGet(Collections::emptyList);
        return new TibcoModel.PartnerLink.Binding.Operation(method, requestEntityProcessing, requestStyle,
                responseStyle, clientFormat, clientRequestFormat, parameters);
    }

    private static List<TibcoModel.PartnerLink.Binding.Operation.Parameter> parseParameters(Element parameters) {
        return ElementIterable.of(parameters).stream().map(XmlToTibcoModelConverter::parseParameter).toList();
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

    private static Stream<Element> getChildrenWithTag(Element element, String tag) {
        return ElementIterable.of(element).stream().filter(child -> getTagNameWithoutNameSpace(child).equals(tag));
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
                case "definitions" -> tryParseWSDLDefinition(child).ifPresent(members::add);
                default -> throw new ParserException("Unsupported types member tag: " + tag, element);
            }
        }
        return members;
    }

    static TibcoModel.Type.Schema parseSchema(Element element) {
        List<TibcoModel.Type.Schema.ComplexType> types = new ArrayList<>();
        List<TibcoModel.Type.Schema.Element> elements = new ArrayList<>();
        List<TibcoModel.NameSpace> imports = new ArrayList<>();
        List<TibcoModel.Type.Schema.UnhandledType> unhandledTypes = new ArrayList<>();
        for (Element each : ElementIterable.of(element)) {
            tryParseSchemaElement(each, elements, types, imports, unhandledTypes);
        }
        return new TibcoModel.Type.Schema(getTargetNamespace(element), types, elements, imports, unhandledTypes);
    }

    static TibcoModel.NameSpace getTargetNamespace(Element element) {
        String targetNamespace = element.getAttribute("targetNamespace");
        // Look for xmlns:* attributes to find matching prefix
        for (int i = 0; i < element.getAttributes().getLength(); i++) {
            String attrName = element.getAttributes().item(i).getNodeName();
            if (attrName.startsWith("xmlns:")) {
                String prefix = attrName.substring(6); // Remove "xmlns:" prefix
                String namespace = element.getAttribute(attrName);
                if (namespace.equals(targetNamespace)) {
                    return new TibcoModel.NameSpace(prefix, targetNamespace);
                }
            }
        }
        // If no matching prefix found, return without prefix
        return new TibcoModel.NameSpace(targetNamespace);
    }

    private static void tryParseSchemaElement(Element each, List<TibcoModel.Type.Schema.Element> elements,
                                              List<TibcoModel.Type.Schema.ComplexType> types,
                                              List<TibcoModel.NameSpace> imports,
                                              List<TibcoModel.Type.Schema.UnhandledType> unhandledTypes) {
        try {
            parseSchemaElementInner(each, elements, types, imports);
        } catch (Exception e) {
            unhandledTypes.add(parseUnhandledType(each, e.getMessage()));
        }
    }

    private static void parseSchemaElementInner(Element each, List<TibcoModel.Type.Schema.Element> elements,
                                                List<TibcoModel.Type.Schema.ComplexType> types,
                                                List<TibcoModel.NameSpace> imports) {

        String tag = getTagNameWithoutNameSpace(each);
        switch (tag) {
            case "complexType" -> types.add(parseType(each));
            case "import" -> imports.add(parseImport(each));
            case "simpleType" -> elements.add(parseSimpleType(each));
            case "element" -> {
                if (complexElement(each)) {
                    String name = each.getAttribute("name");
                    Element body = getFirstChildWithTag(each, "complexType");
                    types.add(parseComplexTypeInner(body, name));
                } else {
                    elements.add(parseSchemaElement(each));
                }
            }
        }
    }

    private static boolean complexElement(Element element) {
        return tryGetFirstChildWithTag(element, "complexType").isPresent();
    }

    private static TibcoModel.Type.Schema.UnhandledType parseUnhandledType(Element element, String reason) {
        String name = element.getAttribute("name");
        return new TibcoModel.Type.Schema.UnhandledType(name, reason, elementToString(element));
    }

    private static TibcoModel.Type.Schema.Element parseSimpleType(Element element) {
        String name = element.getAttribute("name");
        Element restriction = getFirstChildWithTag(element, "restriction");
        String baseType = restriction.getAttribute("base");
        return new TibcoModel.Type.Schema.Element(name, TibcoModel.Type.Schema.TibcoType.of(baseType));
    }

    private static TibcoModel.NameSpace parseImport(Element each) {
        return new TibcoModel.NameSpace(each.getAttribute("namespace"));
    }

    private static TibcoModel.Type.Schema.Element parseSchemaElement(Element each) {
        String name = each.getAttribute("name");
        String typeName = each.getAttribute("type");
        if (typeName.isEmpty()) {
            typeName = "null";
        }
        return new TibcoModel.Type.Schema.Element(name, TibcoModel.Type.Schema.TibcoType.of(typeName));
    }

    private static TibcoModel.Type.Schema.ComplexType parseType(Element element) {
        Element complexType = expectTag(element, "complexType");
        return parseComplexType(complexType);
    }

    private static TibcoModel.Type.Schema.ComplexType parseComplexType(Element element) {
        String name = element.getAttribute("name");
        return parseComplexTypeInner(element, name);
    }

    private static TibcoModel.Type.Schema.@NotNull ComplexType parseComplexTypeInner(Element element, String name) {
        Element child = expectNChildren(element, 1).iterator().next();
        TibcoModel.Type.Schema.ComplexType.Body body = switch (getTagNameWithoutNameSpace(child)) {
            case "sequence" -> parseComplexTypeSequence(child);
            case "complexContent" -> parseComplexContent(child);
            case "choice" -> parseComplexTypeChoice(child);
            case "all" -> parseComplexTypeAll(child);
            case "simpleContent" -> parseSimpleContent(child);
            default ->
                    throw new ParserException("Unsupported complex type body tag: " + getTagNameWithoutNameSpace(child),
                            element);
        };
        return new TibcoModel.Type.Schema.ComplexType(name, body);
    }

    private static TibcoModel.Type.Schema.ComplexType.SimpleContent parseSimpleContent(Element child) {
        Element extension = getFirstChildWithTag(child, "extension");
        String baseTypeName = extension.getAttribute("base");
        return new TibcoModel.Type.Schema.ComplexType.SimpleContent(TibcoModel.Type.Schema.TibcoType.of(baseTypeName));
    }

    private static TibcoModel.Type.Schema.ComplexType.Body parseComplexTypeAll(Element sequence) {
        // Records are anyway unordered
        return parseComplexTypeSequence(sequence);
    }

    private static TibcoModel.Type.Schema.ComplexType.Body parseComplexTypeSequence(Element sequence) {
        Collection<TibcoModel.Type.Schema.ComplexType.SequenceBody.Member> elements =
                parseSequence(sequence, XmlToTibcoModelConverter::parseComplexTypeSequenceElement);
        return new TibcoModel.Type.Schema.ComplexType.SequenceBody(elements);
    }

    private static TibcoModel.Type.Schema.ComplexType.SequenceBody.Member parseComplexTypeSequenceElement(
            Element element) {
        String tag = getTagNameWithoutNameSpace(element);
        return switch (tag) {
            case "element" -> parseComplexTypeElement(element);
            case "any" -> {
                boolean isLax = Boolean.parseBoolean(element.getAttribute("processContents"));
                yield new TibcoModel.Type.Schema.ComplexType.SequenceBody.Member.Rest();
            }
            case "choice" -> parseComplexTypeChoice(element);
            default -> throw new ParserException("Unsupported complex type element tag: " + tag, element);
        };
    }

    static TibcoModel.Type.Schema.ComplexType.SequenceBody.Member parseComplexTypeElement(
            Element element) {
        String elementName = element.getAttribute("name");
        var actualType = parseActualType(element);
        if (elementName.isEmpty()) {
            elementName = anonFieldName();
        }
        int minOccurs = nOccurs("minOccurs", element);
        int maxOccurs = nOccurs("maxOccurs", element);
        boolean isOptional = minOccurs == 0;
        if (maxOccurs > 1) {
            return new TibcoModel.Type.Schema.ComplexType.SequenceBody.Member.ElementArray(elementName,
                    actualType, minOccurs, maxOccurs);
        }
        return new TibcoModel.Type.Schema.ComplexType.SequenceBody.Member.Element(elementName,
                actualType, isOptional);
    }

    // TODO: need to think of a better representation for these.
    private static TibcoModel.Type.Schema.TibcoType parseActualType(Element element) {
        String typeName = element.hasAttribute("type") ? element.getAttribute("type") : element.getAttribute("ref");
        if (!typeName.isEmpty()) {
            return TibcoModel.Type.Schema.TibcoType.of(typeName);
        }
        // TODO: we are ignoring things like attributes here
        TibcoModel.Type.Schema.ComplexType wrapper = parseComplexType(getFirstChildWithTag(element, "complexType"));
        if (!(wrapper.body() instanceof TibcoModel.Type.Schema.ComplexType.SimpleContent(
                TibcoModel.Type.Schema.TibcoType base
        ))) {
            throw new ParserException("Only simple content is supported for anonymous types", element);
        }
        return base;
    }

    private static int nOccurs(String fieldName, Element element) {
        if (!element.hasAttribute(fieldName)) {
            return 1;
        }
        String value = element.getAttribute(fieldName);
        if (value.equals("unbounded")) {
            return Integer.MAX_VALUE;
        }
        return Integer.parseInt(value);
    }

    private static TibcoModel.Type.Schema.ComplexType.ComplexContent parseComplexContent(Element element) {
        return new TibcoModel.Type.Schema.ComplexType.ComplexContent(
                parseComplexContentExtension(expectNChildren(element, 1).iterator().next()));
    }

    private static TibcoModel.Type.Schema.ComplexType.Choice parseComplexTypeChoice(Element element) {
        Collection<TibcoModel.Type.Schema.ComplexType.Choice.Element> elements =
                parseSequence(element, XmlToTibcoModelConverter::parseComplexTypeChoiceElement);
        return new TibcoModel.Type.Schema.ComplexType.Choice(elements);
    }

    private static TibcoModel.Type.Schema.ComplexType.Choice.Element parseComplexTypeChoiceElement(Element element) {
        String typeName = element.hasAttribute("ref") ? element.getAttribute("ref") : element.getAttribute("type");
        int minOccurs = element.hasAttribute("minOccurs") ? expectIntAttribute(element, "minOccurs") : 1;
        int maxOccurs = element.hasAttribute("maxOccurs") ? expectIntAttribute(element, "maxOccurs") : 1;
        return new TibcoModel.Type.Schema.ComplexType.Choice.Element(maxOccurs, minOccurs,
                TibcoModel.Type.Schema.TibcoType.of(typeName));
    }

    private static Optional<TibcoModel.Type.WSDLDefinition> tryParseWSDLDefinition(Element element) {
        try {
            return Optional.of(parseWSDLDefinition(element));
        } catch (Exception e) {
            logger.warning(String.format(
                    "Failed to parse WSDL definition due to %s, resulting process may be missing services",
                    e.getMessage()));
            return Optional.empty();
        }
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
        if (partnerLinkType == null) {
            throw new ParserException("PartnerLinkType not found in WSDL definition", element);
        }
        return new TibcoModel.Type.WSDLDefinition(namespaces, partnerLinkType, imports, messages, portTypes);
    }

    private static TibcoModel.Type.WSDLDefinition.PortType parsePortType(Element element) {
        String name = element.getAttribute("name");
        String apiPath = getAttributeIgnoringNamespace(element, "bw.rest.apipath");
        String basePath = getAttributeIgnoringNamespace(element, "bw.rest.basepath");
        Element operation = getFistMatchingChild(element,
                (child) -> getTagNameWithoutNameSpace(child).equals("operation")).orElseThrow(
                () -> new ParserException("Operation not found in portType", element));
        TibcoModel.Type.WSDLDefinition.PortType.Operation portOperation = parsePortOperation(operation);
        return new TibcoModel.Type.WSDLDefinition.PortType(name, apiPath, basePath, portOperation);
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
                case "input" ->
                        input = new TibcoModel.Type.WSDLDefinition.PortType.Operation.Input(messageNamePair.message,
                                messageNamePair.name);
                case "output" ->
                        output = new TibcoModel.Type.WSDLDefinition.PortType.Operation.Output(messageNamePair.message,
                                messageNamePair.name);
                case "fault" ->
                        faults.add(new TibcoModel.Type.WSDLDefinition.PortType.Operation.Fault(messageNamePair.message,
                                messageNamePair.name));
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
                parseSequence(element, XmlToTibcoModelConverter::parseMessagePart);
        return new TibcoModel.Type.WSDLDefinition.Message(name, parts);
    }

    private static TibcoModel.Type.WSDLDefinition.Message.Part parseMessagePart(Element node) {
        if (node.hasAttribute("element")) {
            return parseReferencePart(node);
        } else {
            return parseInlinePart(node);
        }
    }

    private static TibcoModel.Type.WSDLDefinition.Message.Part.InlineError parseInlinePart(Element node) {
        String name = node.getAttribute("name");
        String typeName = node.getAttribute("type");
        String value = getAttributeIgnoringNamespace(node, "reasonPhrase");
        return new TibcoModel.Type.WSDLDefinition.Message.Part.InlineError(name, value, typeName);
    }

    private static TibcoModel.Type.WSDLDefinition.Message.Part.Reference parseReferencePart(Element node) {
        String name = node.getAttribute("name");
        TibcoModel.NameSpaceValue element = TibcoModel.NameSpaceValue.from(node.getAttribute("element"));
        // TODO: is this correct?
        boolean multipleNamespaces = node.hasAttribute("multipleNamespaces");
        return new TibcoModel.Type.WSDLDefinition.Message.Part.Reference(name, element, multipleNamespaces);
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

    private static String getTagNameWithoutNameSpace(Element element) {
        String tagName = element.getTagName();
        return getTagNameWithoutNameSpace(tagName);
    }

    public static String getTagNameWithoutNameSpace(String tagName) {
        String[] parts = tagName.split(":");
        if (parts.length == 1) {
            return parts[0];
        }
        assert parts.length == 2 && !parts[1].isEmpty();
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
        Collection<TibcoModel.Type.Schema.ComplexType.SequenceBody.Member> elements = new ArrayList<>();
        for (Element child : ElementIterable.of(element)) {
            String tag = getTagNameWithoutNameSpace(child);
            if (tag.equals("sequence")) {
                elements = parseSequence(child, XmlToTibcoModelConverter::parseComplexTypeElement);
                break;
            } else {
                throw new ParserException("Unsupported complex content extension tag: " + tag, element);
            }
        }
        return new TibcoModel.Type.Schema.ComplexType.ComplexContent.Extension(base,
                elements.stream().map(each -> (TibcoModel.Type.Schema.ComplexType.SequenceBody.Member.Element) each)
                        .toList());
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
            case "true", "yes" -> true;
            case "false", "no" -> false;
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
            throw new RuntimeException("Failed to convertTypes element to string", e);
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

    private static String anonFieldName() {
        return "anon" + anonFieldCount++;
    }
}
