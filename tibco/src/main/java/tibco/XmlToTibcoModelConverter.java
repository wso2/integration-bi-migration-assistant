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

package tibco;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import tibco.model.NameSpace;
import tibco.model.NameSpaceValue;
import tibco.model.PartnerLink;
import tibco.model.Method;
import tibco.model.Process5;
import tibco.model.Process5.ExplicitTransitionGroup.InlineActivity;
import tibco.model.Process5.ExplicitTransitionGroup.InlineActivity.XMLParseActivity;
import tibco.model.Process5.ExplicitTransitionGroup.InlineActivity.XMLRenderActivity;
import tibco.model.Process5.ExplicitTransitionGroup.NestedGroup.LoopGroup;
import tibco.model.Process5.ExplicitTransitionGroup.NestedGroup.LoopGroup.SourceExpression;
import tibco.model.Process5.ExplicitTransitionGroup.Transition;
import tibco.model.Scope;
import tibco.model.Scope.Flow;
import tibco.model.Scope.Flow.Activity.ActivityExtension.Config;
import tibco.model.Scope.Flow.Activity.ActivityExtension.Config.AccumulateEnd;
import tibco.model.ProcessInfo;
import tibco.model.ProcessInterface;
import tibco.model.ProcessTemplateConfigurations;
import tibco.model.Resource;
import tibco.model.Type;
import tibco.model.XSD;
import tibco.converter.ConversionUtils;
import tibco.converter.ProjectConverter;
import tibco.model.Process;
import tibco.model.Process6;
import tibco.model.ValueSource;
import tibco.model.Variable;

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
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class XmlToTibcoModelConverter {

    private static final Logger logger = ProjectConverter.logger();

    private XmlToTibcoModelConverter() {
    }

    public static Optional<Resource.JDBCSharedResource> parseSharedJDBCResource(Element root) {
        logger.fine("Start parsing JDBCSharedResource");
        try {
            String name = getFirstChildWithTag(root, "name").getTextContent();
            Element configuration = getFirstChildWithTag(root, "config");
            String location = getFirstChildWithTag(configuration, "location").getTextContent();
            logger.fine("Done parsing JDBCSharedResource: " + name);
            return Optional.of(new Resource.JDBCSharedResource(name, location));
        } catch (Exception ex) {
            logger.warning("Failed to parse JDBCSharedResource: " + ex.getMessage());
            return Optional.empty();
        }
    }

    public static Optional<Resource.JMSSharedResource> parseJMSSharedResource(String fileName, Element root) {
        logger.fine("Start parsing JMSSharedResource");
        try {
            String name = getFirstChildWithTag(root, "name").getTextContent();
            Element config = getFirstChildWithTag(root, "config");
            var namingEnvironment = parseJMSNamingEnvironment(config);
            var connectionAttributes = parseJMSConnectionAttributes(config);
            java.util.Map<String, String> jndiProperties = parseJMSJNDIProperties(config);
            logger.fine("Done parsing JMSSharedResource: " + name);
            return Optional.of(new Resource.JMSSharedResource(name, fileName, namingEnvironment, connectionAttributes,
                    jndiProperties));
        } catch (Exception ex) {
            logger.warning("Failed to parse JMSSharedResource: " + ex.getMessage());
            return Optional.empty();
        }
    }

    private static Resource.JMSSharedResource.NamingEnvironment parseJMSNamingEnvironment(Element config) {
        Element namingEnvElement = getFirstChildWithTag(config, "NamingEnvironment");
        boolean useJNDI = Boolean.parseBoolean(getFirstChildWithTag(namingEnvElement, "UseJNDI").getTextContent());
        String providerURL = getFirstChildWithTag(namingEnvElement, "ProviderURL").getTextContent();
        String namingURL = getFirstChildWithTag(namingEnvElement, "NamingURL").getTextContent();
        String namingInitialContextFactory = getFirstChildWithTag(namingEnvElement, "NamingInitialContextFactory").getTextContent();
        String topicFactoryName = getFirstChildWithTag(namingEnvElement, "TopicFactoryName").getTextContent();
        String queueFactoryName = getFirstChildWithTag(namingEnvElement, "QueueFactoryName").getTextContent();
        String namingPrincipal = getFirstChildWithTag(namingEnvElement, "NamingPrincipal").getTextContent();
        String namingCredential = getFirstChildWithTag(namingEnvElement, "NamingCredential").getTextContent();

        return new Resource.JMSSharedResource.NamingEnvironment(useJNDI, providerURL, namingURL,
                namingInitialContextFactory, topicFactoryName, queueFactoryName, namingPrincipal, namingCredential);
    }

    private static Resource.JMSSharedResource.ConnectionAttributes parseJMSConnectionAttributes(Element config) {
        Element connAttrsElement = getFirstChildWithTag(config, "ConnectionAttributes");
        Optional<String> username = tryGetFirstChildWithTag(connAttrsElement, "username")
                .map(Element::getTextContent)
                .filter(s -> !s.isBlank());
        Optional<String> password = tryGetFirstChildWithTag(connAttrsElement, "password")
                .map(Element::getTextContent)
                .filter(s -> !s.isBlank());
        Optional<String> clientID = tryGetFirstChildWithTag(connAttrsElement, "clientID")
                .map(Element::getTextContent)
                .filter(s -> !s.isBlank());
        boolean autoGenClientID = Boolean.parseBoolean(getFirstChildWithTag(connAttrsElement, "autoGenClientID").getTextContent());

        return new Resource.JMSSharedResource.ConnectionAttributes(username, password, clientID, autoGenClientID);
    }

    private static Map<String, String> parseJMSJNDIProperties(Element config) {
        Map<String, String> jndiProperties = new HashMap<>();
        tryGetFirstChildWithTag(config, "JNDIProperties").ifPresent(jndiPropsElement -> {
            for (Element row : new ElementIterable(jndiPropsElement)) {
                if (getTagNameWithoutNameSpace(row).equals("row")) {
                    String propName = getFirstChildWithTag(row, "Name").getTextContent();
                    String propValue = getFirstChildWithTag(row, "Value").getTextContent();
                    jndiProperties.put(propName, propValue);
                }
            }
        });
        return jndiProperties;
    }

    public static Optional<Resource.JDBCResource> parseJDBCResource(Element root) {
        logger.fine("Start parsing JDBCResource");
        try {
            String name = root.getAttribute("name");
            Element configuration = getFirstChildWithTag(root, "configuration");
            String username = configuration.getAttribute("username");
            String password = configuration.getAttribute("password");
            Element connectionConfig = getFirstChildWithTag(configuration, "connectionConfig");
            String jdbcDriver = connectionConfig.getAttribute("jdbcDriver");
            String dbUrl = connectionConfig.getAttribute("dbURL");
            Collection<Resource.SubstitutionBinding> substitutionBindings = getChildrenWithTag(connectionConfig,
                    "substitutionBindings")
                    .map(XmlToTibcoModelConverter::parseSubstitutionBinding).toList();
            logger.fine("Done parsing JDBCResource: " + name);
            return Optional.of(new Resource.JDBCResource(name, username, password, jdbcDriver, dbUrl,
                    substitutionBindings));
        } catch (Exception ex) {
            logger.warning("Failed to parse JDBCResource: " + ex.getMessage());
            return Optional.empty();
        }
    }

    public static Optional<Resource.HTTPConnectionResource> parseHTTPConnectionResource(Element root) {
        logger.fine("Start parsing HTTPConnectionResource");
        try {
            String name = root.getAttribute("name");
            Element configuration = getFirstChildWithTag(root, "configuration");
            String svcRegServiceName = configuration.getAttribute("svcRegServiceName");
            Collection<Resource.SubstitutionBinding> substitutionBindings = getChildrenWithTag(configuration,
                    "substitutionBindings")
                    .map(XmlToTibcoModelConverter::parseSubstitutionBinding).toList();
            logger.fine("Done parsing HTTPConnectionResource: " + name);
            return Optional.of(new Resource.HTTPConnectionResource(name, svcRegServiceName, substitutionBindings));
        } catch (Exception ex) {
            logger.warning("Failed to parse HTTPConnectionResource: " + ex.getMessage());
            return Optional.empty();
        }
    }

    public static Optional<Resource.HTTPSharedResource> parseHTTPSharedResource(String name, Element root) {
        logger.fine("Start parsing HTTPSharedResource");
        try {
            Element config = getFirstChildWithTag(root, "config");
            String host = getFirstChildWithTag(config, "Host").getTextContent();
            int port = Integer.parseInt(getFirstChildWithTag(config, "Port").getTextContent());
            logger.fine("Done parsing HTTPSharedResource: " + name);
            return Optional.of(new Resource.HTTPSharedResource(name, host, port));
        } catch (Exception ex) {
            logger.warning("Failed to parse HTTPSharedResource: " + ex.getMessage());
            return Optional.empty();
        }
    }

    public static Optional<Resource.HTTPClientResource> parseHTTPClientResource(Element root) {
        logger.fine("Start parsing HTTPClientResource");
        try {
            String name = root.getAttribute("name");
            Element configuration = getFirstChildWithTag(root, "configuration");
            Element tcpDetails = getFirstChildWithTag(configuration, "tcpDetails");
            Optional<Integer> port = tcpDetails.hasAttribute("port")
                    ? Optional.of(expectIntAttribute(tcpDetails, "port"))
                    : Optional.empty();
            Collection<Resource.SubstitutionBinding> substitutionBindings = getChildrenWithTag(tcpDetails,
                    "substitutionBindings")
                    .map(XmlToTibcoModelConverter::parseSubstitutionBinding).toList();
            logger.fine("Done parsing HTTPClientResource: " + name);
            return Optional.of(new Resource.HTTPClientResource(name, port, substitutionBindings));
        } catch (Exception ex) {
            logger.warning("Failed to parse HTTPClientResource: " + ex.getMessage());
            return Optional.empty();
        }
    }

    private static Resource.SubstitutionBinding parseSubstitutionBinding(Element element) {
        String template = element.getAttribute("template");
        String propName = element.getAttribute("propName");
        return new Resource.SubstitutionBinding(template, propName);
    }

    public static tibco.model.Process parseProcess(ParseContext cx, Element root) {
        String processName = root.hasAttribute("name") ? root.getAttribute("name") : "<unknown>";
        logger.fine("Start parsing process: " + processName);
        try {
            tibco.model.Process result = parseProcessInner(cx, root);
            logger.fine("Done parsing process: " + processName);
            return result;
        } catch (Exception ex) {
            logger.severe("Exception while parsing process: " + processName + ". " + ex.getMessage());
            throw ex;
        }
    }

    private static @NotNull Process parseProcessInner(ParseContext cx, Element root) {
        String name = root.getAttribute("name");
        if (name.isBlank()) {
            name = tryGetFirstChildWithTag(root, "name").map(Node::getTextContent)
                    .orElseGet(cx::getNextAnonymousProcessName);
        }
        Collection<NameSpace> nameSpaces = getNamespaces(root).entrySet().stream()
                .map(each -> new NameSpace(each.getKey(), each.getValue()))
                .toList();
        cx.nameSpaces = nameSpaces;
        Collection<Type> types = null;
        ProcessInfo processInfo = null;
        Collection<PartnerLink> partnerLinks = null;
        Collection<Variable> variables = null;
        Scope scope = null;
        Optional<ProcessInterface> processInterface = Optional.empty();
        Optional<ProcessTemplateConfigurations> processTemplateConfigurations = Optional.empty();
        Process5.ExplicitTransitionGroup transitionGroup = new Process5.ExplicitTransitionGroup();
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
                    scope = parseScope(cx, element);
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
                case "activity", "group" -> {
                    transitionGroup = transitionGroup.append(parseInlineActivity(cx, element));
                }
                case "transition" -> {
                    transitionGroup = transitionGroup.append(parseTransition(cx, element));
                }
                case "starter" -> transitionGroup = transitionGroup.setStartActivity(parseInlineActivity(cx, element));
                case "returnBindings" -> {
                    if (!isEmpty(element)) {
                        transitionGroup = transitionGroup.setReturnBindings(parseReturnBindings(cx, element));
                    }
                }
                default -> {
                    // ignore
                }
            }
        }
        transitionGroup = transitionGroup.resolve();
        if (transitionGroup.isEmpty()) {
            return new Process6(name, nameSpaces, types, processInfo, processInterface,
                    processTemplateConfigurations, partnerLinks, variables, scope);
        }
        return new Process5(name, nameSpaces, transitionGroup);
    }

    private static Flow.Activity.Expression.XSLT parseReturnBindings(ParseContext cx, Element element) {
        return parseXSLTTag(cx, element);
    }

    static InlineActivity parseInlineActivity(ParseContext cx, Element element) {
        String name = element.getAttribute("name");
        logger.fine("Start parsing inline activity: " + name);
        try {
            InlineActivity result = parseInlineActivityInner(cx, element);
            logger.fine("Done parsing inline activity: " + name);
            return result;
        } catch (Exception ex) {
            logger.severe("Exception while parsing inline activity: " + name + ". " + ex.getMessage());
            return new InlineActivity.UnhandledInlineActivity(element, name,
                    "Unhandled activity type", null);
        }
    }

    private static @NotNull InlineActivity parseInlineActivityInner(ParseContext cx, Element element) {
        Flow.Activity.InputBinding inputBinding = parseInlineActivityInputBinding(cx,
                getFirstChildWithTag(element, "inputBindings"));
        String name = element.getAttribute("name");
        String typeString = getFirstChildWithTag(element, "type").getTextContent();
        InlineActivity.InlineActivityType type = InlineActivity.InlineActivityType.parse(
                typeString);
        return switch (type) {
            case ASSIGN -> parseAssignActivity(name, inputBinding, element);
            case HTTP_EVENT_SOURCE -> parseHttpEventSource(name, inputBinding, element);
            case HTTP_RESPONSE -> new InlineActivity.HTTPResponse(element, name, inputBinding);
            case UNHANDLED -> new InlineActivity.UnhandledInlineActivity(element, name, typeString, inputBinding);
            case NULL -> new InlineActivity.NullActivity(element, name, inputBinding);
            case WRITE_LOG -> new InlineActivity.WriteLog(element, name, inputBinding);
            case CALL_PROCESS -> parseCallProcess(element, name, inputBinding);
            case FILE_WRITE -> parseFileWrite(element, name, inputBinding);
            case FILE_READ -> parseFileRead(element, name, inputBinding);
            case XML_RENDER_ACTIVITY -> parseXmlRenderActivity(element, name, inputBinding);
            case XML_PARSE_ACTIVITY -> parseXmlParseActivity(element, name, inputBinding);
            case SOAP_SEND_RECEIVE -> parseSoapSendReceive(element, name, inputBinding);
            case SOAP_SEND_REPLY -> new InlineActivity.SOAPSendReply(element, name, inputBinding);
            case LOOP_GROUP -> parseLoopGroup(cx, element, name, inputBinding);
            case REST -> parseREST(element, name, inputBinding);
            case CATCH -> new InlineActivity.Catch(element, name, inputBinding);
            case JSON_PARSER_ACTIVITY -> parseJSONParserActivity(element, name, inputBinding);
            case JSON_RENDER_ACTIVITY -> parseJSONRenderActivity(element, name, inputBinding);
            case JDBC -> parseJDBCActivity(element, name, inputBinding);
            case MAPPER -> parseMapperActivity(name, inputBinding, element);
            case JMS_QUEUE_EVENT_SOURCE -> parseJMSQueueEventSource(element, name, inputBinding);
            case JMS_QUEUE_SEND_ACTIVITY -> parseJMSQueueSendActivity(element, name, inputBinding);
            case JMS_QUEUE_GET_MESSAGE_ACTIVITY -> parseJMSQueueGetMessageActivity(element, name, inputBinding);
            case SLEEP -> new InlineActivity.Sleep(element, name, inputBinding);
            case GET_SHARED_VARIABLE -> parseGetSharedVariable(name, inputBinding, element);
            case SET_SHARED_VARIABLE -> parseSetSharedVariable(name, inputBinding, element);
            case FILE_EVENT_SOURCE -> parseFileEventSource(name, inputBinding, element);
            case ON_STARTUP -> new InlineActivity.OnStartupEventSource(element, name, inputBinding);
        };
    }

    private static InlineActivity.JDBC parseJDBCActivity(Element element, String name, Flow.Activity.InputBinding inputBinding) {
        String connection = tryGetInlineActivityConfigValue(element, "jdbcSharedConfig")
                .orElseThrow(() -> new ParserException("Failed to find jdbcSharedConfig", element));
        String[] parts = connection.split("/");
        connection = parts[parts.length - 1];
        String suffix = ".sharedjdbc";
        connection = connection.endsWith(suffix) ? connection.substring(0, connection.length() - suffix.length())
                : connection;
        return new InlineActivity.JDBC(element, name, inputBinding, connection);
    }

    private static InlineActivity.JSONRender parseJSONRenderActivity(Element element, String name, Flow.Activity.InputBinding inputBinding) {
        return new InlineActivity.JSONRender(element, name, inputBinding, getJSONActivityTarget(element));
    }


    private static InlineActivity.JSONParser parseJSONParserActivity(Element element, String name, Flow.Activity.InputBinding inputBinding) {
        return new InlineActivity.JSONParser(element, name, inputBinding, getJSONActivityTarget(element));
    }

    private static @NotNull XSD getJSONActivityTarget(Element element) {
        Element config = getFirstChildWithTag(element, "config");
        Element activityOutputEditor = getFirstChildWithTag(config, "ActivityOutputEditor");
        Element xsdElement = getFirstChildWithTag(activityOutputEditor, "element");
        return parseXSD(xsdElement);
    }

    private static XSD parseXSD(Element element) {
        return new XSD(parseXSDElement(element), element);
    }

    private static XSD.Element parseXSDElement(Element element) {
        String tagName = getTagNameWithoutNameSpace(element);
        if (tagName.equals("any")) {
            return new XSD.Element(XSD.XSDType.BasicXSDType.ANYDATA, Optional.empty(), Optional.empty());
        }

        String name = element.getAttribute("name");
        String refAttr = element.getAttribute("ref");
        String typeAttr = element.getAttribute("type");

        // Handle elements with ref attributes (references to other elements)
        if (!refAttr.isBlank()) {
            String targetTypeName = ConversionUtils.stripNamespace(refAttr);
            logger.info("XSD element reference found: " + refAttr + " -> " + targetTypeName);
            return new XSD.Element(name, new XSD.XSDType.ReferenceType(targetTypeName), Optional.empty(),
                    Optional.empty());
        }

        // Handle element tags with type attributes (alias elements)
        if (tagName.equals("element") && !typeAttr.isBlank() && !XSD.XSDType.BasicXSDType.couldBeBasicType(typeAttr)) {
            String targetTypeName = ConversionUtils.stripNamespace(typeAttr);
            assert !targetTypeName.isBlank();
            logger.info("XSD element type alias found: " + typeAttr + " -> " + targetTypeName);
            return new XSD.Element(name, new XSD.XSDType.ReferenceType(targetTypeName), Optional.empty(),
                    Optional.empty());
        }

        XSD.XSDType type;
        if (typeAttr.isBlank()) {
            // Try to find a complexType child
            Optional<Element> complexTypeChild = tryGetFirstChildWithTag(element, "complexType");
            if (complexTypeChild.isPresent()) {
                type = parseComplexType(complexTypeChild.get());
            } else {
                logger.warning("No type attribute or complexType child found. Using anydata as placeholder.");
                type = XSD.XSDType.BasicXSDType.ANYDATA;
            }
        } else {
            try {
                type = XSD.XSDType.BasicXSDType.parse(typeAttr);
            } catch (IllegalArgumentException e) {
                logger.warning("Unknown or unsupported XSD type: " + typeAttr + ". Using anydata as placeholder.");
                type = XSD.XSDType.BasicXSDType.ANYDATA;
            }
        }

        String minOccursAttrib = element.getAttribute("minOccurs");
        Optional<Integer> minOccurs = minOccursAttrib.isBlank() ? Optional.empty()
                : Optional.of(Integer.parseInt(minOccursAttrib));

        String maxOccursAttrib = element.getAttribute("maxOccurs");
        Optional<Integer> maxOccurs =
                maxOccursAttrib.isBlank() || maxOccursAttrib.equals("unbounded") ? Optional.empty()
                : Optional.of(Integer.parseInt(maxOccursAttrib));

        return new XSD.Element(name, type, minOccurs, maxOccurs);
    }

    private static XSD.XSDType.ComplexType parseComplexType(Element complexType) {
        // Check for complexContent with extension (inheritance)
        Optional<Element> complexContent = tryGetFirstChildWithTag(complexType, "complexContent");
        if (complexContent.isPresent()) {
            Element content = complexContent.get();
            Optional<Element> extension = tryGetFirstChildWithTag(content, "extension");
            if (extension.isPresent()) {
                String baseType = extension.get().getAttribute("base");
                logger.warning("XSD inheritance not supported: " + baseType + ". Using anydata as placeholder.");
                // Return a placeholder complex type with anydata
                return new XSD.XSDType.ComplexType(
                        new XSD.XSDType.ComplexType.ComplexTypeBody.Sequence(
                                List.of(new XSD.Element("placeholder", XSD.XSDType.BasicXSDType.ANYDATA,
                                        Optional.empty(), Optional.empty()))));
            }
        }

        // Check for choice
        Optional<Element> choice = tryGetFirstChildWithTag(complexType, "choice");
        if (choice.isPresent()) {
            return new XSD.XSDType.ComplexType(parseXSDChoice(choice.get()));
        }

        // Check for sequence
        Optional<Element> sequence = tryGetFirstChildWithTag(complexType, "sequence");
        if (sequence.isPresent()) {
            return new XSD.XSDType.ComplexType(parseXSDSequence(sequence.get()));
        }

        // If neither choice nor sequence is found, create a placeholder
        logger.warning("No sequence or choice found in complexType. Using anydata as placeholder.");
        return new XSD.XSDType.ComplexType(
                new XSD.XSDType.ComplexType.ComplexTypeBody.Sequence(
                        List.of(new XSD.Element("placeholder", XSD.XSDType.BasicXSDType.ANYDATA,
                                Optional.empty(), Optional.empty()))));
    }

    private static XSD.XSDType.ComplexType.ComplexTypeBody.Sequence parseXSDSequence(Element sequence) {
        return new XSD.XSDType.ComplexType.ComplexTypeBody.Sequence(
                ElementIterable.of(sequence).stream()
                        .map(XmlToTibcoModelConverter::parseXSDElement)
                        .toList());
    }

    private static XSD.XSDType.ComplexType.ComplexTypeBody.Choice parseXSDChoice(Element choice) {
        return new XSD.XSDType.ComplexType.ComplexTypeBody.Choice(
                ElementIterable.of(choice).stream()
                        .map(XmlToTibcoModelConverter::parseXSDElement)
                        .toList());
    }

    private static InlineActivity.SOAPSendReceive parseSoapSendReceive(Element element, String name,
            Flow.Activity.InputBinding inputBinding) {
        String endpointURL = getInlineActivityConfigValue(element, "endpointURL");
        Optional<String> soapAction = tryGetInlineActivityConfigValue(element, "soapAction");
        return new InlineActivity.SOAPSendReceive(element, name, inputBinding, soapAction, endpointURL);
    }

    private static @NotNull LoopGroup parseLoopGroup(ParseContext cx, Element element, String name,
            Flow.Activity.InputBinding inputBinding) {
        SourceExpression overExpr = parseSourceExpression(getInlineActivityConfigValue(element, "over"));
        Optional<String> iterationElementSlot = tryGetInlineActivityConfigValue(element, "iterationElementSlot");
        Optional<String> indexSlot = tryGetInlineActivityConfigValue(element, "indexSlot");
        Optional<String> activityOutputName = tryGetInlineActivityConfigValue(element, "activityOutputName");
        boolean accumulateOutput = tryGetInlineActivityConfigValue(element, "accumulateOutput")
                .map(val -> val.equals("true")).orElse(false);
        Process5.ExplicitTransitionGroup transitionGroup = new Process5.ExplicitTransitionGroup();
        for (Element child : new ElementIterable(element)) {
            String tag = getTagNameWithoutNameSpace(child);
            switch (tag) {
                case "activity" -> {
                    transitionGroup = transitionGroup.append(parseInlineActivity(cx, child));
                }
                case "transition" -> {
                    transitionGroup = transitionGroup.append(parseTransition(cx, child));
                }
                case "starter" -> transitionGroup = transitionGroup.setStartActivity(parseInlineActivity(cx, child));
                case "returnBindings" -> {
                    if (!isEmpty(child)) {
                        transitionGroup = transitionGroup.setReturnBindings(parseReturnBindings(cx, child));
                    }
                }
                default -> {
                    // ignore
                }
            }
        }
        transitionGroup = transitionGroup.resolve();
        return new LoopGroup(element, name, inputBinding, overExpr, iterationElementSlot, indexSlot,
                activityOutputName, accumulateOutput, transitionGroup);
    }

    private static SourceExpression parseSourceExpression(String expression) {
        StringBuilder varSB = new StringBuilder();
        assert expression.charAt(0) == '$';
        int index = 1;
        while (index < expression.length() && expression.charAt(index) != '/') {
            varSB.append(expression.charAt(index));
            index++;
        }
        String variable = varSB.toString();
        if (index == expression.length()) {
            return new SourceExpression(variable, Optional.empty());
        }
        assert expression.charAt(index) == '/';
        index++;
        String path = expression.substring(index);
        return new SourceExpression(variable, Optional.of(path));
    }

    private static InlineActivity.REST parseREST(Element element, String name,
            Flow.Activity.InputBinding inputBinding) {
        String authChoice = getInlineActivityConfigValue(element, "authChoiceUI");
        if (!authChoice.equals("No Authentication")) {
            throw new ParserException("Authentication in REST activities not supported", element);
        }
        InlineActivity.REST.Method method = InlineActivity.REST.Method.from(
                getInlineActivityConfigValue(element, "restMethodUI"));
        InlineActivity.REST.ResponseType responseType = InlineActivity.REST.ResponseType.from(
                getInlineActivityConfigValue(element, "restResponseType"));
        String url = getInlineActivityConfigValue(element, "restURI");
        return new InlineActivity.REST(element, name, inputBinding, method, responseType, url);
    }

    private static InlineActivity.CallProcess parseCallProcess(Element element, String name,
            Flow.Activity.InputBinding inputBinding) {
        String processName = getInlineActivityConfigValue(element, "processName");
        return new InlineActivity.CallProcess(element, name, inputBinding, processName);
    }

    private static InlineActivity.FileRead parseFileRead(
            Element element, String name, Flow.Activity.InputBinding inputBinding) {
        Element config = getFirstChildWithTag(element, "config");
        String encoding = getFirstChildWithTag(config, "encoding").getTextContent();
        if (!encoding.equals("text")) {
            throw new ParserException("Unsupported encoding" + encoding, element);
        }
        return new InlineActivity.FileRead(element, name, inputBinding, encoding);
    }

    private static XMLParseActivity parseXmlParseActivity(Element element, String name,
            Flow.Activity.InputBinding inputBinding) {
        Element config = getFirstChildWithTag(element, "config");
        String inputStyle = getFirstChildWithTag(config, "inputStyle").getTextContent();
        if (!inputStyle.equalsIgnoreCase("text")) {
            throw new ParserException("Unsupported inputStyle value: " + inputStyle, element);
        }
        return new XMLParseActivity(element, name, inputBinding);
    }

    private static XMLRenderActivity parseXmlRenderActivity(Element element, String name,
            Flow.Activity.InputBinding inputBinding) {
        // TODO: extract this to a method
        Element config = getFirstChildWithTag(element, "config");
        String renderAsText = getFirstChildWithTag(config, "renderAsText").getTextContent();
        if (!renderAsText.equalsIgnoreCase("text")) {
            throw new ParserException("Unsupported renderAsText value: " + renderAsText, element);
        }
        return new XMLRenderActivity(element, name, inputBinding);
    }

    private static InlineActivity.FileWrite parseFileWrite(
            Element element, String name, Flow.Activity.InputBinding inputBinding) {
        Element config = getFirstChildWithTag(element, "config");
        String encoding = getFirstChildWithTag(config, "encoding").getTextContent();
        if (!encoding.equals("text")) {
            throw new ParserException("Unsupported encoding" + encoding, element);
        }
        boolean append = getFirstChildWithTag(config, "append").getTextContent()
                .equalsIgnoreCase("true");
        return new InlineActivity.FileWrite(element, name, inputBinding, encoding, append);
    }

    private static InlineActivity.AssignActivity parseAssignActivity(
            String name, Flow.Activity.InputBinding inputBinding, Element element) {
        String variableName = getInlineActivityConfigValue(element, "variableName");
        return new InlineActivity.AssignActivity(element, name, variableName, inputBinding);
    }

    private static Optional<String> tryGetInlineActivityConfigValue(Element element, String name) {
        return tryGetFirstChildWithTag(element, "config")
                .flatMap(config -> tryGetFirstChildWithTag(config, name))
                .map(Node::getTextContent)
                .filter(Predicate.not(String::isBlank));
    }

    private static String getInlineActivityConfigValue(Element element, String name) {
        Element config = getFirstChildWithTag(element, "config");
        return getFirstChildWithTag(config, name).getTextContent();
    }

    private static InlineActivity.HttpEventSource parseHttpEventSource(String name,
            Flow.Activity.InputBinding inputBinding,
            Element element) {
        String sharedChannel = getInlineActivityConfigValue(element, "sharedChannel");
        return new InlineActivity.HttpEventSource(element, name, sharedChannel, inputBinding);
    }

    private static InlineActivity.MapperActivity parseMapperActivity(String name,
            Flow.Activity.InputBinding inputBinding,
            Element element) {
        return new InlineActivity.MapperActivity(element, name, inputBinding);
    }

    private static InlineActivity.GetSharedVariable parseGetSharedVariable(String name,
            Flow.Activity.InputBinding inputBinding,
            Element element) {
        String variableConfig = getInlineActivityConfigValue(element, "variableConfig");
        return new InlineActivity.GetSharedVariable(element, name, inputBinding, variableConfig);
    }

    private static InlineActivity.SetSharedVariable parseSetSharedVariable(String name,
            Flow.Activity.InputBinding inputBinding,
            Element element) {
        String variableConfig = getInlineActivityConfigValue(element, "variableConfig");
        return new InlineActivity.SetSharedVariable(element, name, inputBinding, variableConfig);
    }

    private static Flow.Activity.InputBinding.CompleteBinding parseInlineActivityInputBinding(
            ParseContext cx, Element element) {
        if (isEmpty(element)) {
            return null;
        }
        Flow.Activity.Expression.XSLT expression = parseXSLTTag(cx, element);
        return new Flow.Activity.InputBinding.CompleteBinding(expression);
    }

    private static boolean isEmpty(Element element) {
        String content = ElementIterable.of(element).stream().map(ConversionUtils::elementToString)
                .collect(Collectors.joining());
        return content.isBlank();
    }

    private static Flow.Activity.Expression.@NotNull XSLT parseXSLTTag(ParseContext cx, Element element) {
        String content = ElementIterable.of(element).stream().map(ConversionUtils::elementToString)
                .collect(Collectors.joining());

        StringBuilder namespaceDeclarations = new StringBuilder();
        // Track prefixes to avoid redeclaration
        Set<String> declaredPrefixes = new java.util.HashSet<>();
        // Always add xsl first
        namespaceDeclarations.append("xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"");
        declaredPrefixes.add("xsl");
        record NamespaceData(String prefix, String uri) {
        }
        cx.nameSpaces.stream()
                .filter(each -> each.prefix().isPresent())
                .map(each -> new NamespaceData(each.prefix().get(), each.uri()))
                .filter(each -> !declaredPrefixes.contains(each.prefix()))
                .forEach(each -> {
                    namespaceDeclarations.append(" xmlns:").append(each.prefix()).append("=\"")
                            .append(each.uri())
                            .append("\"");
                    declaredPrefixes.add(each.prefix());
                });

        String xslt = """
                <?xml version="1.0" encoding="UTF-8"?>
                <xsl:stylesheet %s version="2.0">
                     <xsl:template name="%s" match="/">
                        %s
                    </xsl:template>
                </xsl:stylesheet>
                """
                .formatted(namespaceDeclarations.toString(), cx.getAnonymousXSLTName(), content);
        return new Flow.Activity.Expression.XSLT(xslt);
    }

    private static Transition parseTransition(ParseContext cx, Element element) {
        String from = getFirstChildWithTag(element, "from").getTextContent();
        String to = getFirstChildWithTag(element, "to").getTextContent();
        return new Transition(from, to);
    }

    // TODO: fill this
    private static ProcessTemplateConfigurations parseProcessTemplateConfigurations(Element element) {
        return new ProcessTemplateConfigurations();
    }

    private static ProcessInterface parseProcessInterface(Element element) {
        String context = element.getAttribute("context");
        String input = element.getAttribute("input");
        String output = element.getAttribute("output");
        return new ProcessInterface(context, input, output);
    }

    private static Scope parseScope(ParseContext cx, Element element) {
        String name = element.getAttribute("name");
        Collection<Flow> flows = ElementIterable.of(element).stream()
                .filter(each -> getTagNameWithoutNameSpace(each).equals("flow"))
                .map(e -> parseFlow(cx, e)).toList();

        Collection<Scope.Sequence> sequences = ElementIterable.of(element).stream()
                .filter(each -> getTagNameWithoutNameSpace(each).equals("sequence"))
                .map(e -> parseSequence(cx, e)).toList();
        Collection<Scope.FaultHandler> faultHandlers = ElementIterable.of(element).stream()
                .filter(each -> getTagNameWithoutNameSpace(each).equals("faultHandlers"))
                .flatMap(e -> parseFaultHandlers(cx, e)).toList();
        return new Scope(name, flows, sequences, faultHandlers);
    }

    private static Stream<Scope.FaultHandler> parseFaultHandlers(ParseContext cx, Element element) {
        return ElementIterable.of(element).stream().map(e -> parseFaultHandler(cx, e));
    }

    private static Scope.FaultHandler parseFaultHandler(ParseContext cx, Element element) {
        String tag = getTagNameWithoutNameSpace(element);
        return switch (tag) {
            case "catchAll" -> parseCatchAll(cx, element);
            default -> throw new ParserException("Unsupported fault handler " + tag, element);
        };
    }

    private static Flow.Activity.CatchAll parseCatchAll(ParseContext cx, Element element) {
        Element scope = expectNChildren(element, 1).iterator().next();
        if (!getTagNameWithoutNameSpace(scope).equals("scope")) {
            throw new ParserException("Expected a scope", element);
        }
        return new Flow.Activity.CatchAll(parseScope(cx, scope), element);
    }

    private static Flow parseFlow(ParseContext cx, Element flow) {
        String name = flow.getAttribute("name");
        Collection<Flow.Link> links = null;
        List<Flow.Activity> activities = new ArrayList<>();
        for (Element element : ElementIterable.of(flow)) {
            String tag = getTagNameWithoutNameSpace(element);
            if (tag.equals("links")) {
                if (links != null) {
                    throw new ParserException("Multiple links elements found in the XML", flow);
                }
                links = parseLinks(element);
            } else {
                activities.add(parseActivity(cx, element));
            }
        }
        if (links == null) {
            logger.warning(
                    String.format("Failed to find any links in the flow: %s, maybe failed to parse all activities",
                            name));
            links = List.of();
        }
        return new Flow(name, links, activities);
    }

    public static Flow.Activity parseActivity(ParseContext cx, Element element) {
        String tag = getTagNameWithoutNameSpace(element);
        String name = element.hasAttribute("name") ? element.getAttribute("name") : tag;
        logger.fine("Start parsing activity: " + name);
        try {
            Flow.Activity result = parseActivityInner(cx, element);
            logger.fine("Done parsing activity: " + name);
            return result;
        } catch (Exception ex) {
            logger.severe("Exception while parsing activity: " + name + ". " + ex.getMessage());
            return parseUnhandledActivity(element, ex.getMessage());
        }
    }

    private static Flow.Activity parseActivityInner(ParseContext cx, Element element) {
        String tag = getTagNameWithoutNameSpace(element);
        return switch (tag) {
            case "extensionActivity" -> parseExtensionActivity(element);
            case "invoke" -> parseInvoke(element);
            case "pick" -> parsePick(cx, element);
            case "empty" -> parseEmpty(element);
            case "reply" -> parseReply(element);
            case "throw" -> parseThrow(element);
            case "scope" -> parseNestedScope(cx, element);
            case "assign" -> parseAssign(element);
            case "forEach" -> parseForeach(cx, element);
            case "catchAll" -> parseCatchAll(cx, element);
            case "activity" -> parseInlineActivity(cx, element);
            default -> throw new ParserException("Unsupported activity tag: " + tag, element);
        };
    }

    private static Flow.Activity.Foreach parseForeach(ParseContext cx, Element element) {
        String counterName = element.getAttribute("counterName");
        ValueSource startCounterValue = parseValueSource(
                getFirstChildWithTag(element, "startCounterValue"));
        ValueSource finalCounterValue = parseValueSource(
                getFirstChildWithTag(element, "finalCounterValue"));
        Scope scope = parseScope(cx, getFirstChildWithTag(element, "scope"));
        return new Flow.Activity.Foreach(counterName, scope, startCounterValue, finalCounterValue,
                element);
    }

    private static Flow.Activity.Assign parseAssign(Element element) {
        Collection<Flow.Activity.Target> targets = ElementIterable.of(element).stream()
                .filter(each -> getTagNameWithoutNameSpace(each).equals("targets"))
                .map(XmlToTibcoModelConverter::parseTargets).flatMap(Collection::stream).toList();
        List<Flow.Activity.Source> sources = ElementIterable.of(element).stream()
                .filter(each -> getTagNameWithoutNameSpace(each).equals("sources"))
                .map(XmlToTibcoModelConverter::parseSources).flatMap(Collection::stream).toList();
        Flow.Activity.Assign.Copy copy = parseCopy(getFirstChildWithTag(element, "copy"));
        return new Flow.Activity.Assign(sources, targets, copy, element);
    }

    private static Flow.Activity.Assign.Copy parseCopy(Element element) {
        ValueSource from = parseValueSource(getFirstChildWithTag(element, "from"));
        ValueSource.VarRef to = parseVarRef(getFirstChildWithTag(element, "to"));
        return new Flow.Activity.Assign.Copy(from, to);
    }

    private static ValueSource parseValueSource(Element element) {
        String variable = element.getAttribute("variable");
        if (variable.isEmpty()) {
            String expressionLanguage = element.getAttribute("expressionLanguage");
            String expression = element.getTextContent();
            if (expressionLanguage.contains("xslt")) {
                return new Flow.Activity.Expression.XSLT(expression);
            } else if (expressionLanguage.contains("xpath")) {
                return new Flow.Activity.Expression.XPath(expression);
            } else {
                return new ValueSource.Constant(expression);
            }
        }
        return parseVarRef(element);
    }

    private static ValueSource.VarRef parseVarRef(Element element) {
        String variable = element.getAttribute("variable");
        return new ValueSource.VarRef(variable);
    }

    private static Flow.Activity.NestedScope parseNestedScope(ParseContext cx, Element element) {
        String name = element.getAttribute("name");
        Collection<Flow> flows = ElementIterable.of(element).stream()
                .filter(each -> getTagNameWithoutNameSpace(each).equals("flow"))
                .map(e -> parseFlow(cx, e)).toList();

        Collection<Scope.Sequence> sequences = ElementIterable.of(element).stream()
                .filter(each -> getTagNameWithoutNameSpace(each).equals("sequence"))
                .map(e -> parseSequence(cx, e)).toList();
        Collection<Scope.FaultHandler> faultHandlers = ElementIterable.of(element).stream()
                .filter(each -> getTagNameWithoutNameSpace(each).equals("faultHandlers"))
                .flatMap(e -> parseFaultHandlers(cx, e)).toList();
        Collection<Flow.Activity.Target> targets = ElementIterable.of(element).stream()
                .filter(each -> getTagNameWithoutNameSpace(each).equals("targets"))
                .map(XmlToTibcoModelConverter::parseTargets).flatMap(Collection::stream).toList();
        List<Flow.Activity.Source> sources = ElementIterable.of(element).stream()
                .filter(each -> getTagNameWithoutNameSpace(each).equals("sources"))
                .map(XmlToTibcoModelConverter::parseSources).flatMap(Collection::stream).toList();
        return new Flow.Activity.NestedScope(name, sources, targets, sequences, flows,
                faultHandlers, element);
    }

    private static Scope.Sequence parseSequence(ParseContext cx, Element element) {
        String name = element.getAttribute("name");
        List<Flow.Activity> activities = ElementIterable.of(element).stream()
                .map(e -> parseActivity(cx, e)).toList();
        return new Scope.Sequence(name, activities);
    }

    private static Flow.Activity.Throw parseThrow(Element element) {
        ActivityInputOutput result = getActivityInputOutput(element);
        return new Flow.Activity.Throw(result.inputBindings(), result.targets(), element);
    }

    private static @NotNull ActivityInputOutput getActivityInputOutput(Element element) {
        List<Flow.Activity.InputBinding> inputBindings = new ArrayList<>();
        List<Flow.Activity.Target> targets = new ArrayList<>();
        for (Element each : ElementIterable.of(element)) {
            String tag = getTagNameWithoutNameSpace(each);
            switch (tag) {
                case "inputBinding" -> inputBindings.add(
                        new Flow.Activity.InputBinding.CompleteBinding(parseExpressionNode(each)));
                case "inputBindings" -> inputBindings.addAll(parseInputBindings(each));
                case "targets" -> targets.addAll(parseTargets(each));
                default -> throw new ParserException("Unsupported reply element tag: " + tag, element);
            }
        }
        return new ActivityInputOutput(inputBindings, targets);
    }

    private record ActivityInputOutput(List<Flow.Activity.InputBinding> inputBindings,
            List<Flow.Activity.Target> targets) {

    }

    private static Flow.Activity parseUnhandledActivity(Element element, String reason) {
        Collection<Flow.Activity.Target> targets = new ArrayList<>();
        List<Flow.Activity.Source> sources = new ArrayList<>();

        for (Element child : ElementIterable.of(element)) {
            String tag = getTagNameWithoutNameSpace(child);
            switch (tag) {
                case "targets" -> targets.addAll(parseTargets(child));
                case "sources" -> sources.addAll(parseSources(child));
                default -> {
                }
            }
        }

        return new Flow.Activity.UnhandledActivity(" FIXME: " + reason, sources, targets, element);
    }

    private static Flow.Activity.Reply parseReply(Element element) {
        String name = element.getAttribute("name");
        var operation = Method.from(
                element.getAttribute("operation"));
        String partnerLink = element.getAttribute("partnerLink");
        String portType = element.getAttribute("portType");
        ActivityInputOutput result = getActivityInputOutput(element);
        return new Flow.Activity.Reply(name, operation, partnerLink, portType, result.inputBindings,
                result.targets, element);
    }

    private static Flow.Activity.Empty parseEmpty(Element element) {
        String name = element.getAttribute("name");
        return new Flow.Activity.Empty(name, element);
    }

    private static Flow.Activity.Pick parsePick(ParseContext cx, Element element) {
        boolean createInstance = expectBooleanAttribute(element, "createInstance");
        Flow.Activity.Pick.OnMessage onMessage = parseOnMessage(cx, getFirstChildWithTag(element, "onMessage"));
        return new Flow.Activity.Pick(createInstance, onMessage, element);
    }

    private static Flow.Activity.Pick.OnMessage parseOnMessage(ParseContext cx, Element element) {
        var operation = Method.from(
                element.getAttribute("operation"));
        String partnerLink = element.getAttribute("partnerLink");
        String portType = element.getAttribute("portType");
        String variable = element.getAttribute("variable");
        Scope scope = parseScope(cx, getFirstChildWithTag(element, "scope"));
        return new Flow.Activity.Pick.OnMessage(operation, partnerLink, portType, variable, scope);
    }

    private static Flow.Activity.Invoke parseInvoke(Element element) {
        String inputVariable = element.getAttribute("inputVariable");
        String outputVariable = element.getAttribute("outputVariable");
        Method operation;
        try {
            operation = Method.from(element.getAttribute("operation"));
        } catch (IllegalArgumentException ignored) {
            // TODO: need to handle this properly, this happens for SOAP operations
            operation = Method.POST;
        }
        String partnerLink = element.getAttribute("partnerLink");
        List<Flow.Activity.InputBinding> inputBindings = new ArrayList<>();
        Collection<Flow.Activity.Target> targets = new ArrayList<>();
        List<Flow.Activity.Source> sources = new ArrayList<>();
        for (Element each : ElementIterable.of(element)) {
            String tag = getTagNameWithoutNameSpace(each);
            switch (tag) {
                case "inputBinding" -> inputBindings.add(
                        new Flow.Activity.InputBinding.CompleteBinding(parseExpressionNode(each)));
                case "inputBindings" -> inputBindings.addAll(parseInputBindings(each));
                case "targets" -> {
                    for (Element target : ElementIterable.of(each)) {
                        targets.add(parseTarget(target));
                    }
                }
                case "sources" -> {
                    for (Element source : ElementIterable.of(each)) {
                        sources.add(new Flow.Activity.Source(source.getAttribute("linkName")));
                    }
                }
                default -> throw new ParserException("Unsupported invoke element tag: " + tag, element);
            }
        }
        return new Flow.Activity.Invoke(inputVariable, outputVariable, operation, partnerLink,
                inputBindings, targets, sources, element);
    }

    private static List<? extends Flow.Activity.InputBinding> parseInputBindings(Element element) {
        List<Flow.Activity.Expression> partialBindings = new ArrayList<>();
        List<Flow.Activity.InputBinding.CompleteBinding> completeBindings = new ArrayList<>();
        for (Element each : ElementIterable.of(element)) {
            String tag = getTagNameWithoutNameSpace(each);
            switch (tag) {
                case "inputBinding" -> completeBindings.add(
                        new Flow.Activity.InputBinding.CompleteBinding(parseExpressionNode(each)));
                case "partBinding" -> partialBindings.add(parseExpressionNode(each));
                default -> throw new ParserException("Unsupported input binding element tag: " + tag, element);
            }
        }
        if (!partialBindings.isEmpty() && !completeBindings.isEmpty()) {
            throw new ParserException("Mix of partial bindings and complete bindings detected", element);
        }
        if (!partialBindings.isEmpty()) {
            return List.of(new Flow.Activity.InputBinding.PartialBindings(partialBindings));
        }
        return completeBindings;
    }

    private static Flow.Activity.Expression parseExpressionNode(Element node) {
        String language = node.getAttribute("expressionLanguage");
        if (language.contains("xslt")) {
            return parseXSLTExpression(node);
        } else {
            throw new ParserException("Unsupported expression language: " + language, node);
        }
    }

    private static Flow.Activity.Expression.XSLT parseXSLTExpression(Element node) {
        String expression;
        if (node.hasAttribute("expression")) {
            expression = node.getAttribute("expression");
        } else {
            expression = node.getTextContent();
        }
        expression = unEscapeXml(expression);
        return new Flow.Activity.Expression.XSLT(expression);
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

    private static Flow.Activity parseExtensionActivity(Element element) {
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

    private static Flow.Activity.ExtActivity parseExtActivity(Element element) {
        Optional<Flow.Activity.Expression> expression = element.getAttribute("expression").isBlank()
                ? Optional.empty()
                : Optional.of(parseExpressionNode(element));
        String inputVariable = element.getAttribute("inputVariable");
        String outputVariable = element.getAttribute("outputVariable");
        List<Flow.Activity.Source> sources = null;
        List<Flow.Activity.Target> targets = null;
        List<Flow.Activity.InputBinding> inputBindings = new ArrayList<>();
        Flow.Activity.ExtActivity.CallProcess callProcess = null;
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
                case "targets" -> {
                    if (targets != null) {
                        throw new ParserException("Multiple targets elements found in the XML", element);
                    }
                    targets = ElementIterable.of(getFirstChildWithTag(element, "targets")).stream()
                            .map(XmlToTibcoModelConverter::parseTarget).toList();
                }
                case "inputBinding" -> inputBindings.add(new Flow.Activity.InputBinding.CompleteBinding(
                        parseExpressionNode(each)));
                case "inputBindings" -> inputBindings.addAll(parseInputBindings(each));
                case "CallProcess" -> {
                    if (callProcess != null) {
                        throw new ParserException("Multiple CallProcess elements found in the XML", element);
                    }
                    callProcess = parseCallProcesses(each);
                }
                default -> throw new ParserException("Unsupported extActivity element tag: " + tag, element);
            }
        }
        if (sources == null) {
            sources = List.of();
        }
        if (targets == null) {
            targets = List.of();
        }
        return new Flow.Activity.ExtActivity(expression, inputVariable, outputVariable, sources,
                targets, inputBindings, callProcess, element);
    }

    private static Flow.Activity.ExtActivity.CallProcess parseCallProcesses(Element element) {
        String subProcessName = element.getAttribute("subProcessName");
        return new Flow.Activity.ExtActivity.CallProcess(subProcessName);
    }

    private static Flow.Activity.ActivityExtension parseActivityExtension(Element activity) {
        Optional<String> inputVariable = tryGetAttributeIgnoringNamespace(activity, "inputVariable");
        Optional<String> name = tryGetAttributeIgnoringNamespace(activity, "name");
        Optional<String> outputVariable = tryGetAttributeIgnoringNamespace(activity, "outputVariable");
        Optional<Element> targetsElement = tryGetFirstChildWithTag(activity, "targets");
        Collection<Flow.Activity.Target> targets = targetsElement.map(ElementIterable::of)
                .map(ElementIterable::stream)
                .map(s -> s.map(XmlToTibcoModelConverter::parseTarget).toList())
                .orElse(List.of());

        Optional<Element> sourcesElement = tryGetFirstChildWithTag(activity, "sources");
        List<Flow.Activity.Source> sources = sourcesElement.map(ElementIterable::of)
                .map(ElementIterable::stream)
                .map(s -> s.map(XmlToTibcoModelConverter::parseSource).toList())
                .orElse(List.of());
        List<Flow.Activity.InputBinding> inputBindings = new ArrayList<>();
        Optional<Element> inputBindingElement = tryGetFirstChildWithTag(activity, "inputBindings");
        inputBindingElement.ifPresent(elem -> inputBindings.addAll(parseInputBindings(elem)));
        Config config = parseActivityExtensionConfig(getFirstChildWithTag(activity, "config"));
        return new Flow.Activity.ActivityExtension(name, inputVariable, outputVariable, targets,
                sources, inputBindings, config, activity);
    }

    private static Collection<Flow.Activity.Target> parseTargets(Element each) {
        return ElementIterable.of(each).stream().map(XmlToTibcoModelConverter::parseTarget).toList();
    }

    private static Collection<Flow.Activity.Source> parseSources(Element each) {
        return ElementIterable.of(each).stream().map(XmlToTibcoModelConverter::parseSource).toList();
    }

    private static Flow.Activity.Target parseTarget(Element each) {
        return new Flow.Activity.Target(each.getAttribute("linkName"));
    }

    private static Config parseActivityExtensionConfig(
            Element config) {
        Element activity = getFirstChildWithTag(config, "BWActivity");
        String activityTypeID = activity.getAttribute("activityTypeID");
        Config.ExtensionKind kind = Config.ExtensionKind.fromTypeId(activityTypeID);
        return switch (kind) {
            case END -> new Config.End();
            case FILE_WRITE -> new Config.FileWrite();
            case HTTP_SEND -> parseHTTPSend(activity);
            case JSON_RENDER -> parseJSONOperation(config, Config.ExtensionKind.JSON_RENDER);
            case JSON_PARSER -> parseJSONOperation(config, Config.ExtensionKind.JSON_PARSER);
            case LOG -> new Config.Log();
            case RENDER_XML -> new Config.RenderXML();
            case SEND_HTTP_RESPONSE -> parseSendHTTPResponse(config);
            case MAPPER -> new Config.Mapper();
            case SQL -> parasSqlActivityExtension(config);
            case ACCUMULATE_END -> parseAccumulateEnd(activity);
        };
    }

    private static Config.@NotNull SendHTTPResponse parseSendHTTPResponse(Element config) {
        Element activity = getFirstChildWithTag(config, "BWActivity");
        String responseActivityInputNs = activity.getAttribute("xmlns:ResponseActivityInput").trim();
        if (responseActivityInputNs.isBlank()) {
            return new Config.SendHTTPResponse(Optional.empty());
        }
        return new Config.SendHTTPResponse(Optional.of(responseActivityInputNs));
    }

    private static Config parseJSONOperation(Element config, Config.ExtensionKind kind) {
        Element activity = getFirstChildWithTag(config, "BWActivity");
        Element activityConfig = getFirstChildWithTag(activity, "activityConfig");
        Element properties = getFirstChildWithTag(activityConfig, "properties");
        Element value = getFirstChildWithTag(properties, "value");
        String editorElementTag = switch (kind) {
            case JSON_PARSER -> "outputEditorElement";
            case JSON_RENDER -> "inputEditorElement";
            default -> throw new IllegalStateException("Unexpected value: " + kind);
        };

        Element editorElement = getFirstChildWithTag(value, editorElementTag);
        String href = editorElement.getAttribute("href");
        if (href.isBlank()) {
            throw new ParserException("Missing href attribute in outputEditorElement", editorElement);
        }

        String typeName = extractTypeNameFromHref(href);
        return new Config.JsonOperation(kind, Type.Schema.TibcoType.of(typeName));
    }

    private static String extractTypeNameFromHref(String href) {
        if (href == null || href.isBlank()) {
            throw new ParserException("Href attribute is null or blank", null);
        }

        // Find the "#//" part
        int hashIndex = href.indexOf("#//");
        if (hashIndex == -1) {
            throw new ParserException("Invalid href format: missing '#//' delimiter in href: " + href, null);
        }

        // Extract everything after "#//"
        String afterHash = href.substring(hashIndex + 3);

        // Remove anything after ";" if present
        int semicolonIndex = afterHash.indexOf(";");
        if (semicolonIndex != -1) {
            afterHash = afterHash.substring(0, semicolonIndex);
        }

        String typeName = afterHash.trim();
        if (typeName.isEmpty()) {
            throw new ParserException("Empty type name extracted from href: " + href, null);
        }

        return typeName;
    }

    private static AccumulateEnd parseAccumulateEnd(Element activity) {
        Element activityConfig = getFirstChildWithTag(activity, "activityConfig");
        Element properties = getFirstChildWithTag(activityConfig, "properties");
        Element value = getFirstChildWithTag(properties, "value");
        Element activityNames = getFirstChildWithTag(value, "activityNames");
        return new AccumulateEnd(activityNames.getTextContent());
    }

    private static Config.HTTPSend parseHTTPSend(Element activity) {
        Element activityConfig = getFirstChildWithTag(activity, "activityConfig");
        Element properties = getFirstChildWithTag(activityConfig, "properties");
        Element values = getFirstChildWithTag(properties, "value");
        String httpClientResource = values.getAttribute("httpClientResource");
        return new Config.HTTPSend(httpClientResource);
    }

    private static Config.SQL parasSqlActivityExtension(
            Element config) {
        Element properties = getFirstChildWithTag(
                getFirstChildWithTag(getFirstChildWithTag(config, "BWActivity"),
                        "activityConfig"),
                "properties");
        Element value = getFirstChildWithTag(properties, "value");
        String sharedResourceProperty = value.getAttribute("sharedResourceProperty");
        assert !sharedResourceProperty.isEmpty();
        String query = unEscapeXml(value.getAttribute("sqlStatement"));
        List<Config.SQL.SQLParameter> parameters = getChildrenWithTag(value, "PreparedParameters")
                .map(XmlToTibcoModelConverter::parseSqlParameter).toList();

        List<Config.SQL.Column> columns = getChildrenWithTag(value, "columnMetadata")
                .map(XmlToTibcoModelConverter::parseColumn).toList();
        return new Config.SQL(sharedResourceProperty, query, columns,
                parameters);
    }

    private static Config.SQL.Column parseColumn(
            Element columnMetadata) {
        boolean isOptional = columnMetadata.getAttribute("status").contains("OptionalElement");
        return new Config.SQL.Column(
                columnMetadata.getAttribute("columnName"),
                Config.SQL.SQLType.fromString(
                        columnMetadata.getAttribute("typeName")),
                isOptional);
    }

    private static Config.SQL.SQLParameter parseSqlParameter(
            Element preparedParameters) {
        return new Config.SQL.SQLParameter(
                preparedParameters.getAttribute("ParameterName"),
                Config.SQL.SQLType.fromString(
                        preparedParameters.getAttribute("DataTypeDisplayValue")));
    }

    private static Flow.Activity.ReceiveEvent parseReceiveEvent(Element activity) {
        boolean createInstance = activity.getAttribute("createInstance").equals("yes");
        float eventTimeout = expectFloatAttribute(activity, "eventTimeout");
        String variable = activity.getAttribute("variable");
        List<Flow.Activity.Source> sources = ElementIterable
                .of(getFirstChildWithTag(activity, "sources")).stream()
                .map(XmlToTibcoModelConverter::parseSource).toList();

        return new Flow.Activity.ReceiveEvent(createInstance, eventTimeout, variable, sources,
                activity);
    }

    private static Flow.Activity.Source parseSource(Element element) {
        String linkName = element.getAttribute("linkName");
        Optional<Flow.Activity.Source.Predicate> predicate = tryGetFirstChildWithTag(element,
                "transitionCondition").map(XmlToTibcoModelConverter::parseCondition);
        return new Flow.Activity.ReceiveEvent.Source(linkName, predicate);
    }

    private static Flow.Activity.Source.Predicate parseCondition(Element cond) {
        String body = cond.getTextContent();
        if (body.contains("##otherwise##")) {
            return new Flow.Activity.Source.Predicate.Else();
        } else {
            return parseXPath(cond);
        }
    }

    private static Flow.Activity.Expression.XPath parseXPath(Element element) {
        String expressionLanguage = element.getAttribute("expressionLanguage");
        if (!expressionLanguage.contains("xpath")) {
            throw new ParserException("Unsupported expression language: " + expressionLanguage, element);
        }
        return new Flow.Activity.Expression.XPath(element.getTextContent());
    }

    private static Collection<Flow.Link> parseLinks(Element element) {
        return ElementIterable.of(element).stream()
                .map(link -> new Flow.Link(link.getAttribute("name"))).toList();
    }

    private static Collection<Variable> parseVariables(Element element) {
        return ElementIterable.of(element).stream().map(XmlToTibcoModelConverter::parseVariable).toList();
    }

    private static Variable parseVariable(Element element) {
        String name = element.getAttribute("name");
        String type = element.getAttribute("element");
        boolean isProperty = tryGetAttributeIgnoringNamespace(element, "property").map(val -> val.equals("yes"))
                .orElse(false);
        if (isProperty) {
            return parsePropertyVariable(element, name, type);
        }
        return new Variable.DefaultVariable(name, type);
    }

    private static Variable.@NotNull PropertyVariable parsePropertyVariable(Element element, String name,
                                                                            String type) {
        Optional<Element> from = tryGetFirstChildWithTag(element, "from");
        if (from.isPresent()) {
            Optional<Element> literal = tryGetFirstChildWithTag(from.get(), "literal");
            if (literal.isPresent()) {
                return new Variable.PropertyVariable.PropertyReference(name, literal.get().getTextContent(),
                        type);
            }
        } else {
            String propertySource = getAttributeIgnoringNamespace(element, "propertySource");
            return new Variable.PropertyVariable.SimpleProperty(name, propertySource, type);
        }
        throw new ParserException("Failed to parse property variable", element);
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

    private static Collection<PartnerLink> parsePartnerLinks(Element element) {
        return ElementIterable.of(element).stream().map(XmlToTibcoModelConverter::parsePartnerLink).toList();
    }

    private static PartnerLink parsePartnerLink(Element element) {
        try {
            return tryParsePartnerLink(element);
        } catch (Exception ex) {
            return new PartnerLink.UnhandledPartnerLink(element.getAttribute("name"), ex.getMessage());
        }
    }

    private static PartnerLink tryParsePartnerLink(Element element) {
        String name = element.getAttribute("name");
        Optional<Element> referenceBinding = tryGetFirstChildWithTag(element, "ReferenceBinding");
        return referenceBinding.map(value -> finishParsingTibexReferenceBinding(value, name))
                .orElseGet(() -> new PartnerLink.EmptyPartnerLink(name));
    }

    private static PartnerLink finishParsingTibexReferenceBinding(Element referenceBinding, String name) {
        return finishParsingTibexBinding(getFirstChildWithTag(referenceBinding, "binding"), name);
    }

    private static PartnerLink finishParsingTibexBinding(Element binding, String name) {
        return finishParsingBWBaseBinding(getFirstChildWithTag(binding, "BWBaseBinding"), name);
    }

    private static PartnerLink finishParsingBWBaseBinding(Element bwBaseBinding, String name) {
        return finishParsingReferenceBinding(getFirstChildWithTag(bwBaseBinding, "referenceBinding"), name);
    }

    private static PartnerLink finishParsingReferenceBinding(Element referenceBinding, String name) {
        return finishParsingBinding(getFirstChildWithTag(referenceBinding, "binding"), name);
    }

    private static PartnerLink finishParsingBinding(Element binding, String name) {
        String bindingName = binding.getAttribute("name");
        return switch (bindingName) {
            case "RestReference" -> finishParsingRestBinding(binding, name);
            case "SOAPReferenceBinding" -> finishParsingSoapBinding(binding, name);
            default -> throw new ParserException(
                    "Unsupported binding name: " + bindingName, binding);
        };
    }

    private static PartnerLink.SoapPartnerLink finishParsingSoapBinding(Element binding, String name) {
        String path = binding.getAttribute("locationURI");
        return new PartnerLink.SoapPartnerLink(name, path);
    }

    private static PartnerLink.@NotNull RestPartnerLink finishParsingRestBinding(Element binding,
            String name) {
        String basePath = binding.getAttribute("basePath");
        String path = binding.getAttribute("path");
        var connector = PartnerLink.Binding.Connector.from(binding.getAttribute("connector"));
        var operation = parseBindingOperation(getFirstChildWithTag(binding, "operation"));
        return new PartnerLink.RestPartnerLink(name, new PartnerLink.RestPartnerLink.Binding(
                new PartnerLink.RestPartnerLink.Binding.Path(basePath, path),
                connector, operation));
    }

    private static PartnerLink.RestPartnerLink.Binding.Operation parseBindingOperation(Element operation) {
        Method method = Method.from(
                operation.getAttribute("httpMethod"));

        Optional<PartnerLink.Binding.Operation.RequestEntityProcessing> requestEntityProcessing = parseIfPresent(
                PartnerLink.Binding.Operation.RequestEntityProcessing::from,
                operation.getAttribute("requestEntityProcessing"));
        Optional<PartnerLink.Binding.Operation.MessageStyle> requestStyle = parseIfPresent(
                PartnerLink.Binding.Operation.MessageStyle::from,
                operation.getAttribute("requestStyle"));
        Optional<PartnerLink.Binding.Operation.MessageStyle> responseStyle = parseIfPresent(
                PartnerLink.Binding.Operation.MessageStyle::from,
                operation.getAttribute("responseStyle"));
        Optional<PartnerLink.Binding.Operation.Format> clientFormat = parseIfPresent(
                XmlToTibcoModelConverter::parseFormat,
                tryGetFirstChildWithTag(operation, "clientFormat"));

        Optional<PartnerLink.Binding.Operation.Format> clientRequestFormat = parseIfPresent(
                XmlToTibcoModelConverter::parseFormat,
                tryGetFirstChildWithTag(operation, "clientRequestFormat"));
        List<PartnerLink.RestPartnerLink.Binding.Operation.Parameter> parameters = tryGetFirstChildWithTag(
                operation, "parameters").map(XmlToTibcoModelConverter::parseParameters)
                .orElseGet(Collections::emptyList);
        return new PartnerLink.RestPartnerLink.Binding.Operation(method, requestEntityProcessing,
                requestStyle, responseStyle, clientFormat, clientRequestFormat, parameters);
    }

    private static <E> Optional<E> parseIfPresent(Function<String, E> parseFunction, String attribute) {
        if (attribute.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(parseFunction.apply(attribute));
    }

    private static <E> Optional<E> parseIfPresent(Function<Element, E> parseFunction, Optional<Element> element) {
        return element.map(parseFunction);
    }

    private static List<PartnerLink.RestPartnerLink.Binding.Operation.Parameter> parseParameters(
            Element parameters) {
        return List.of();
        // return
        // ElementIterable.of(parameters).stream().map(XmlToTibcoModelConverter::parseParameter).toList();
    }

    private static PartnerLink.RestPartnerLink.Binding.Operation.Parameter parseParameter(Element element) {
        // TODO: support this when we properly support path parameters
        throw new UnsupportedOperationException("unimplemented");
    }

    private static PartnerLink.RestPartnerLink.Binding.Operation.Format parseFormat(Element clientFormat) {
        String body = clientFormat.getTextContent();
        if (body.equals("json")) {
            return PartnerLink.Binding.Operation.Format.JSON;
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

    private static ProcessInfo parseProcessInfo(Element element) {
        boolean callable = expectBooleanAttribute(element, "callable");
        // TODO: how multiple modifiers represented?
        Set<ProcessInfo.Modifier> modifiers = EnumSet.noneOf(ProcessInfo.Modifier.class);
        String modifiersStr = element.getAttribute("modifiers");
        if (modifiersStr.contains("public")) {
            modifiers.add(ProcessInfo.Modifier.PUBLIC);
        }
        boolean scalable = expectBooleanAttribute(element, "scalable");
        boolean singleton = expectBooleanAttribute(element, "singleton");
        boolean stateless = expectBooleanAttribute(element, "stateless");
        ProcessInfo.Type type = switch (element.getAttribute("type")) {
            case "IT" -> ProcessInfo.Type.IT;
            default -> throw new ParserException("Unsupported process type: " + element.getAttribute("type"), element);
        };
        return new ProcessInfo(callable, modifiers, scalable, singleton, stateless, type);
    }

    private static Collection<Type> parseTypes(Element element) {
        List<Type> members = new ArrayList<>();
        for (Element child : ElementIterable.of(element)) {
            String tag = getTagNameWithoutNameSpace(child);
            switch (tag) {
                case "schema" -> XmlToTibcoModelConverter.parseSchema(child).ifPresent(members::add);
                case "definitions" -> tryParseWSDLDefinition(child).ifPresent(members::add);
                default -> throw new ParserException("Unsupported types member tag: " + tag, element);
            }
        }
        return members;
    }

    static Optional<Type.Schema> parseSchema(Element element) {
        try {
            Map<String, Type.Schema.SchemaXsdType> complexTypes = new HashMap<>();
            Map<String, String> aliases = new HashMap<>();
            for (Element child : ElementIterable.of(element)) {
                String tag = getTagNameWithoutNameSpace(child);
                String name = child.getAttribute("name");
                if (tag.equals("complexType")) {
                    complexTypes.put(name, new Type.Schema.SchemaXsdType(name, parseComplexType(child)));
                } else if (tag.equals("element")) {
                    // Check if this is a simple alias (has type attribute) or complex definition
                    // (has complexType child)
                    String type = child.getAttribute("type");
                    if (!type.isBlank()) {
                        // This is a simple alias - element references another type
                        String strippedType = ConversionUtils.stripNamespace(type);
                        aliases.put(name, strippedType);
                    } else {
                        // This is a complex element definition - check for complexType child
                        Optional<Element> complexTypeChild = tryGetFirstChildWithTag(child, "complexType");
                        if (complexTypeChild.isPresent()) {
                            // Create a complex type for this element
                            complexTypes.put(name,
                                    new Type.Schema.SchemaXsdType(name, parseComplexType(complexTypeChild.get())));
                        } else {
                            // No type attribute and no complexType child - this might be an error or
                            // unsupported case
                            logger.warning("Element '" + name
                                    + "' has no type attribute and no complexType child. Using anydata as placeholder.");
                            complexTypes.put(name, new Type.Schema.SchemaXsdType(name,
                                    new XSD.XSDType.ComplexType(
                                            new XSD.XSDType.ComplexType.ComplexTypeBody.Sequence(
                                                    List.of(new XSD.Element("placeholder", XSD.XSDType.BasicXSDType.ANYDATA,
                                                            Optional.empty(), Optional.empty()))))));
                        }
                    }
                }
            }
            for (var each : aliases.entrySet()) {
                String type = each.getValue();
                String name = each.getKey();
                if (complexTypes.containsKey(type)) {
                    complexTypes.put(name, new Type.Schema.SchemaXsdType(name, complexTypes.get(type).type()));
                }
            }
            return Optional.of(new Type.Schema(element, complexTypes.values()));
        } catch (Exception ex) {
            logger.warning("Failed to parse schema: " + ex.getMessage());
            return Optional.empty();
        }
    }

    private static NameSpace parseImport(Element each) {
        return new NameSpace(each.getAttribute("namespace"));
    }

    private static Optional<Type.WSDLDefinition> tryParseWSDLDefinition(Element element) {
        try {
            return parseWSDLDefinition(element);
        } catch (Exception e) {
            logger.warning(String.format(
                    "Failed to parse WSDL definition due to %s, resulting process may be missing services",
                    e.getMessage()));
            return Optional.empty();
        }
    }

    private static Optional<Type.WSDLDefinition> parseWSDLDefinition(Element element) {
        if (element.getChildNodes().getLength() == 0) {
            return Optional.empty();
        }
        Map<String, String> namespaces = getNamespaces(element);
        Collection<Type.WSDLDefinition.PartnerLinkType> partnerLinkTypes = new ArrayList<>();
        List<NameSpace> imports = new ArrayList<>();
        List<Type.WSDLDefinition.Message> messages = new ArrayList<>();
        List<Type.WSDLDefinition.PortType> portTypes = new ArrayList<>();
        for (Element child : ElementIterable.of(element)) {
            String tag = getTagNameWithoutNameSpace(child);
            switch (tag) {
                case "partnerLinkType" -> partnerLinkTypes.add(parsePartnerLinkType(child));
                case "import" -> imports.add(parseImport(child));
                case "message" -> messages.add(parseMessage(child));
                case "portType" -> portTypes.add(parsePortType(child));
                default -> throw new ParserException("Unsupported WSDL definition tag: " + tag, element);
            }
        }
        if (partnerLinkTypes.isEmpty()) {
            throw new ParserException("PartnerLinkType not found in WSDL definition", element);
        }
        return Optional.of(
                new Type.WSDLDefinition(namespaces, partnerLinkTypes, imports, messages, portTypes));
    }

    private static Map<String, String> getNamespaces(Element element) {
        Map<String, String> namespaces = new HashMap<>();
        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attribute = attributes.item(i);
            String name = attribute.getNodeName();
            if (!name.startsWith("xmlns")) {
                continue;
            }
            String nameSpace = name.split(":")[1];
            String value = attribute.getNodeValue();
            namespaces.put(nameSpace, value);
        }

        return Collections.unmodifiableMap(namespaces);
    }

    private static Type.WSDLDefinition.PortType parsePortType(Element element) {
        String name = element.getAttribute("name");
        String apiPath = getAttributeIgnoringNamespace(element, "bw.rest.apipath");
        String basePath = tryGetAttributeIgnoringNamespace(element, "bw.rest.basepath").orElse("/");
        Element operation = getFistMatchingChild(element,
                (child) -> getTagNameWithoutNameSpace(child).equals("operation")).orElseThrow(
                        () -> new ParserException("Operation not found in portType", element));
        Type.WSDLDefinition.PortType.Operation portOperation = parsePortOperation(operation);
        return new Type.WSDLDefinition.PortType(name, apiPath, basePath, portOperation);
    }

    private static Type.WSDLDefinition.PortType.Operation parsePortOperation(Element operation) {

        record MessageNamePair(NameSpaceValue message, String name) {

            static MessageNamePair from(Element element) {
                return new MessageNamePair(NameSpaceValue.from(element.getAttribute("message")),
                        element.getAttribute("name"));
            }
        }
        String name = operation.getAttribute("name");
        Type.WSDLDefinition.PortType.Operation.Input input = null;
        Type.WSDLDefinition.PortType.Operation.Output output = null;
        List<Type.WSDLDefinition.PortType.Operation.Fault> faults = new ArrayList<>();
        for (Element child : ElementIterable.of(operation)) {
            String tag = getTagNameWithoutNameSpace(child);
            if (!(tag.equals("input") || tag.equals("output") || tag.equals("fault"))) {
                continue;
            }
            MessageNamePair messageNamePair = MessageNamePair.from(child);
            switch (tag) {
                case "input" -> input = new Type.WSDLDefinition.PortType.Operation.Input(messageNamePair.message,
                        messageNamePair.name);
                case "output" -> output = new Type.WSDLDefinition.PortType.Operation.Output(messageNamePair.message,
                        messageNamePair.name);
                case "fault" -> faults.add(new Type.WSDLDefinition.PortType.Operation.Fault(messageNamePair.message,
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
        return new Type.WSDLDefinition.PortType.Operation(name, input, output, faults);
    }

    private static Type.WSDLDefinition.Message parseMessage(Element element) {
        String name = element.getAttribute("name");
        List<Type.WSDLDefinition.Message.Part> parts = parseSequence(element,
                XmlToTibcoModelConverter::parseMessagePart);
        return new Type.WSDLDefinition.Message(name, parts);
    }

    private static Type.WSDLDefinition.Message.Part parseMessagePart(Element node) {
        if (node.hasAttribute("element")) {
            return parseReferencePart(node);
        } else {
            return parseInlinePart(node);
        }
    }

    private static Type.WSDLDefinition.Message.Part.InlineError parseInlinePart(Element node) {
        String name = node.getAttribute("name");
        String typeName = node.getAttribute("type");
        String value = getAttributeIgnoringNamespace(node, "reasonPhrase");
        return new Type.WSDLDefinition.Message.Part.InlineError(name, value, typeName);
    }

    private static Type.WSDLDefinition.Message.Part.Reference parseReferencePart(Element node) {
        String name = node.getAttribute("name");
        NameSpaceValue element = NameSpaceValue.from(node.getAttribute("element"));
        // TODO: is this correct?
        boolean multipleNamespaces = node.hasAttribute("multipleNamespaces");
        return new Type.WSDLDefinition.Message.Part.Reference(name, element, multipleNamespaces);
    }

    private static Type.WSDLDefinition.PartnerLinkType parsePartnerLinkType(Element element) {
        String name = element.getAttribute("name");
        Element role = expectNChildren(element, 1).iterator().next();
        String roleName = role.getAttribute("name");
        String portTypeName = role.getAttribute("portType");
        return new Type.WSDLDefinition.PartnerLinkType(name,
                new Type.WSDLDefinition.PartnerLinkType.Role(roleName,
                        NameSpaceValue.from(portTypeName)));
    }

    @NotNull
    public static String getTagNameWithoutNameSpace(Element element) {
        String tagName = element.getTagName();
        return getTagNameWithoutNameSpace(tagName);
    }

    public static String getTagNameWithoutNameSpace(String tagName) {
        return ConversionUtils.stripNamespace(tagName);
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

    private static float expectFloatAttribute(Element element, String attributeName) {
        try {
            return Float.parseFloat(element.getAttribute(attributeName));
        } catch (NumberFormatException e) {
            throw new ParserException("Invalid float value for attribute: " + attributeName, element);
        }
    }

    private static int expectIntAttribute(Element element, String attributeName) {
        try {
            return Integer.parseInt(element.getAttribute(attributeName));
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

    private static Optional<Element> getFistMatchingChild(Element element, Predicate<Element> predicate) {
        return ElementIterable.of(element).stream().filter(predicate).findFirst();
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
            super("[ParseError] : " + message + "\n" + ConversionUtils.elementToString(element));
        }

    }

    private static InlineActivity.JMSQueueEventSource parseJMSQueueEventSource(Element element, String name,
                                                                               Flow.Activity.InputBinding inputBinding) {
        return new InlineActivity.JMSQueueEventSource(parseJMSActivityInner(element, name, inputBinding));
    }

    private static InlineActivity.JMSQueueSendActivity parseJMSQueueSendActivity(Element element, String name,
                                                                                 Flow.Activity.InputBinding inputBinding) {
        return new InlineActivity.JMSQueueSendActivity(parseJMSActivityInner(element, name, inputBinding));
    }

    private static InlineActivity.JMSQueueGetMessageActivity parseJMSQueueGetMessageActivity(Element element, String name,
                                                                                              Flow.Activity.InputBinding inputBinding) {
        return new InlineActivity.JMSQueueGetMessageActivity(parseJMSActivityInner(element, name, inputBinding));
    }

    private static InlineActivity.JMSActivityBase parseJMSActivityInner(Element element, String name,
                                                                        Flow.Activity.InputBinding inputBinding) {
        Element config = getFirstChildWithTag(element, "config");

        String permittedMessageType = getFirstChildWithTag(config, "PermittedMessageType").getTextContent();
        if (!permittedMessageType.equals("Text")) {
            throw new UnsupportedOperationException("Unsupported permitted message type: " + permittedMessageType);
        }
        InlineActivity.JMSActivityBase.SessionAttributes sessionAttributes = parseJMSSessionAttributes(config);
        InlineActivity.JMSActivityBase.ConfigurableHeaders configurableHeaders = parseJMSConfigurableHeaders(
                config);
        String connectionReference = getFirstChildWithTag(config, "ConnectionReference").getTextContent();

        return new InlineActivity.JMSActivityBase(element, name, inputBinding, permittedMessageType,
                sessionAttributes, configurableHeaders, connectionReference);
    }

    private static InlineActivity.JMSActivityBase.SessionAttributes parseJMSSessionAttributes(Element config) {
        Element sessionAttrs = getFirstChildWithTag(config, "SessionAttributes");
        boolean transacted = Boolean.parseBoolean(getFirstChildWithTag(sessionAttrs, "transacted").getTextContent());
        int acknowledgeMode = Integer.parseInt(getFirstChildWithTag(sessionAttrs, "acknowledgeMode").getTextContent());
        int maxSessions = Integer.parseInt(getFirstChildWithTag(sessionAttrs, "maxSessions").getTextContent());
        String destination = getFirstChildWithTag(sessionAttrs, "destination").getTextContent();

        return new InlineActivity.JMSActivityBase.SessionAttributes(transacted, acknowledgeMode, maxSessions,
                destination);
    }

    private static InlineActivity.JMSActivityBase.ConfigurableHeaders parseJMSConfigurableHeaders(Element config) {
        Element configurableHeaders = getFirstChildWithTag(config, "ConfigurableHeaders");
        String jmsDeliveryMode = getFirstChildWithTag(configurableHeaders, "JMSDeliveryMode").getTextContent();
        String jmsExpiration = getFirstChildWithTag(configurableHeaders, "JMSExpiration").getTextContent();
        String jmsPriority = getFirstChildWithTag(configurableHeaders, "JMSPriority").getTextContent();

        return new InlineActivity.JMSActivityBase.ConfigurableHeaders(jmsDeliveryMode, jmsExpiration, jmsPriority);
    }

    public static Optional<Resource.SharedVariable> parseSharedVariable(ParseContext cx, Element root,
            String relativePath) {
        return parseSharedVariable(cx, root, false, relativePath);
    }

    public static Optional<Resource.SharedVariable> parseJobSharedVariable(ParseContext cx, Element root,
            String relativePath) {
        return parseSharedVariable(cx, root, true, relativePath);
    }

    private static Optional<Resource.SharedVariable> parseSharedVariable(ParseContext cx, Element root,
            boolean isShared, String relativePath) {
        logger.fine("Start parsing SharedVariable");
        try {
            String name = getFirstChildWithTag(root, "name").getTextContent();
            Element config = getFirstChildWithTag(root, "config");
            boolean persistent = tryGetFirstChildWithTag(config, "persistent")
                    .map(Element::getTextContent)
                    .map(Boolean::parseBoolean)
                    .orElse(false);
            String initialValue = tryGetFirstChildWithTag(config, "initialValue")
                    .map(Element::getTextContent).orElse("");
            if (!initialValue.equals("byRef")) {
                logger.severe(
                        "initialValue for sharedVariable '" + name
                                + "' is not byRef. Using empty string as placeholder.");
                initialValue = "<root/>";
                logger.fine("Done parsing SharedVariable: " + name);
                return Optional.of(new Resource.SharedVariable(name, persistent, initialValue, isShared, relativePath));
            }
            String initialValueRef = getFirstChildWithTag(config, "initialValueRef").getTextContent();
            try {
                initialValue = cx.getFileContent(initialValueRef);
            } catch (java.io.IOException e) {
                logger.severe("Failed to read initial value for sharedVariable '" + name + "' from '" + initialValueRef
                        + "': " + e.getMessage());
                initialValue = "<root/>";
            }
            logger.fine("Done parsing SharedVariable: " + name);
            return Optional.of(new Resource.SharedVariable(name, persistent, initialValue, isShared, relativePath));
        } catch (Exception ex) {
            logger.warning("Failed to parse SharedVariable: " + ex.getMessage());
            return Optional.empty();
        }
    }

    private static InlineActivity.FileEventSource parseFileEventSource(String name,
            Flow.Activity.InputBinding inputBinding, Element element) {
        // Parse the required boolean fields with default false if missing
        boolean createEvent = tryGetInlineActivityConfigValue(element, "createEvent")
                .map(Boolean::parseBoolean)
                .orElse(false);
        boolean modifyEvent = tryGetInlineActivityConfigValue(element, "modifyEvent")
                .map(Boolean::parseBoolean)
                .orElse(false);
        boolean deleteEvent = tryGetInlineActivityConfigValue(element, "deleteEvent")
                .map(Boolean::parseBoolean)
                .orElse(false);
        boolean excludeContent = tryGetInlineActivityConfigValue(element, "excludeContent")
                .map(Boolean::parseBoolean)
                .orElse(false);

        // Parse fileName - required field
        String fileName = tryGetInlineActivityConfigValue(element, "fileName")
                .orElseThrow(() -> new ParserException("fileName is required for FileEventSource", element));

        // Log warnings for ignored fields
        if (tryGetInlineActivityConfigValue(element, "mode").isPresent()) {
            logger.warning("Ignoring 'mode' field in FileEventSource configuration");
        }
        if (tryGetInlineActivityConfigValue(element, "encoding").isPresent()) {
            logger.warning("Ignoring 'encoding' field in FileEventSource configuration");
        }
        if (tryGetInlineActivityConfigValue(element, "sortby").isPresent()) {
            logger.warning("Ignoring 'sortby' field in FileEventSource configuration");
        }
        if (tryGetInlineActivityConfigValue(element, "sortorder").isPresent()) {
            logger.warning("Ignoring 'sortorder' field in FileEventSource configuration");
        }
        if (tryGetInlineActivityConfigValue(element, "pollInterval").isPresent()) {
            logger.warning("Ignoring 'pollInterval' field in FileEventSource configuration");
        }

        return new InlineActivity.FileEventSource(element, name, inputBinding, createEvent, modifyEvent, deleteEvent,
                excludeContent, fileName);
    }
}
