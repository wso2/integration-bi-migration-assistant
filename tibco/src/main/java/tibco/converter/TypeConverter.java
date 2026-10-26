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

package tibco.converter;

import common.BallerinaModel;
import common.BallerinaModel.Expression.CheckPanic;
import common.BallerinaModel.Expression.FunctionCall;
import common.BallerinaModel.Expression.TypeCheckExpression;
import common.BallerinaModel.Expression.VariableReference;
import common.BallerinaModel.Expression.XMLTemplate;
import common.BallerinaModel.Statement.IfElseStatement;
import common.BallerinaModel.Statement.Return;
import common.BallerinaModel.Statement.VarDeclStatment;
import common.BallerinaModel.TypeDesc.MapTypeDesc;
import common.CodeGenerator;
import common.LoggingUtils;
import io.ballerina.compiler.syntax.tree.AnnotationNode;
import io.ballerina.compiler.syntax.tree.ConstantDeclarationNode;
import io.ballerina.compiler.syntax.tree.EnumDeclarationNode;
import io.ballerina.compiler.syntax.tree.ImportDeclarationNode;
import io.ballerina.compiler.syntax.tree.MappingConstructorExpressionNode;
import io.ballerina.compiler.syntax.tree.MetadataNode;
import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;
import io.ballerina.compiler.syntax.tree.ModulePartNode;
import io.ballerina.compiler.syntax.tree.NodeFactory;
import io.ballerina.compiler.syntax.tree.NodeList;
import io.ballerina.compiler.syntax.tree.NodeParser;
import io.ballerina.compiler.syntax.tree.SpecificFieldNode;
import io.ballerina.compiler.syntax.tree.SyntaxKind;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.compiler.syntax.tree.TypeDefinitionNode;
import io.ballerina.tools.text.TextDocuments;
import io.ballerina.xsd.core.response.NodeResponse;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Element;
import tibco.LoggingContext;
import tibco.model.Type;
import tibco.model.Type.WSDLDefinition;
import tibco.model.Type.WSDLDefinition.PortType.Operation;
import tibco.parser.XmlToTibcoModelParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static common.BallerinaModel.TypeDesc.BuiltinType.ERROR;
import static common.BallerinaModel.TypeDesc.BuiltinType.XML;
import static common.ConversionUtils.exprFrom;
import static io.ballerina.xsd.core.XSDToRecord.generateNodes;

public final class TypeConverter {

    private static final Set<String> NAMED_SCHEMA_COMPONENTS =
            Set.of("element", "complexType", "simpleType", "group", "attributeGroup");
    private static final Set<String> QNAME_REFERENCE_ATTRIBUTES =
            Set.of("type", "base", "itemType", "substitutionGroup");

    private TypeConverter() {
    }

    // xsd-core uses names verbatim as identifiers, so `E-Mail` would not compile. Names are sanitized like
    // getTypeByName does; colliding ones (`E-Mail` vs `E_Mail`) get a suffix that getTypeByName doesn't know.
    public static @NotNull NodeResponse generateTypes(Collection<Type.Schema> schemas) {
        List<Element> copies = schemas.stream()
                .map(schema -> (Element) schema.element().cloneNode(true))
                .toList();
        Map<String, String> identifiers = componentIdentifiers(copies);
        Map<String, String> renamedRootElements = new HashMap<>();
        NodeResponse response = generateNodes(copies.stream()
                .map(schema -> withValidIdentifierNames(schema, identifiers, renamedRootElements))
                .map(ConversionUtils::elementToString)
                .toArray(String[]::new));
        if (renamedRootElements.isEmpty()) {
            return response;
        }
        return new NodeResponse(withXmlNames(response.types(), renamedRootElements), response.diagnostics(),
                response.nodeContext());
    }

    private static @NotNull Map<String, String> componentIdentifiers(List<Element> schemas) {
        assert schemas != null : "schemas must not be null";
        List<String> names = schemas.stream()
                .flatMap(schema -> childElements(schema).stream())
                .filter(component -> NAMED_SCHEMA_COMPONENTS.contains(componentKind(component)))
                .filter(component -> component.hasAttribute("name"))
                .map(component -> component.getAttribute("name"))
                .distinct()
                .toList();
        Map<String, String> identifiers = new HashMap<>();
        Set<String> allocated = new HashSet<>();
        names.stream().filter(name -> toValidIdentifier(name).equals(name)).forEach(name -> {
            identifiers.put(name, name);
            allocated.add(name);
        });
        names.stream().filter(name -> !identifiers.containsKey(name)).forEach(name -> {
            String identifier = ConversionUtils.getSanitizedUniqueName(name, allocated);
            identifiers.put(name, identifier);
            allocated.add(identifier);
        });
        return identifiers;
    }

    private static @NotNull Element withValidIdentifierNames(Element schema, Map<String, String> identifiers,
                                                             Map<String, String> renamedRootElements) {
        assert schema != null : "schema must not be null";
        assert identifiers != null : "identifiers must not be null";
        assert renamedRootElements != null : "renamedRootElements must not be null";
        for (Element child : childElements(schema)) {
            if (componentKind(child).equals("element") && child.hasAttribute("name")) {
                String name = child.getAttribute("name");
                String identifier = identifiers.get(name);
                if (!identifier.equals(name)) {
                    renamedRootElements.put(identifier, name);
                }
            }
        }
        renameComponents(schema, identifiers, true);
        return schema;
    }

    private static void renameComponents(Element parent, Map<String, String> identifiers, boolean topLevel) {
        for (Element component : childElements(parent)) {
            String kind = componentKind(component);
            // Annotation attributes are metadata, not schema names.
            if (kind.equals("annotation")) {
                continue;
            }
            // Local declarations become fields, not types, so they can't collide.
            if (NAMED_SCHEMA_COMPONENTS.contains(kind) && component.hasAttribute("name")) {
                String name = component.getAttribute("name");
                component.setAttribute("name", topLevel ? identifiers.get(name) : toValidIdentifier(name));
            }
            QNAME_REFERENCE_ATTRIBUTES.forEach(attribute -> renameQName(component, attribute, identifiers));
            // Attributes keep their names, so refs to them must too.
            if (!kind.equals("attribute")) {
                renameQName(component, "ref", identifiers);
            }
            if (component.hasAttribute("memberTypes")) {
                component.setAttribute("memberTypes", Arrays.stream(component.getAttribute("memberTypes").split("\\s+"))
                        .filter(memberType -> !memberType.isEmpty())
                        .map(memberType -> toValidQName(memberType, identifiers))
                        .collect(Collectors.joining(" ")));
            }
            renameComponents(component, identifiers, false);
        }
    }

    private static @NotNull List<Element> childElements(Element parent) {
        org.w3c.dom.NodeList children = parent.getChildNodes();
        List<Element> elements = new ArrayList<>();
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i) instanceof Element child) {
                elements.add(child);
            }
        }
        return elements;
    }

    private static @NotNull String componentKind(Element component) {
        return XmlToTibcoModelParser.getTagNameWithoutNameSpace(component.getTagName());
    }

    private static void renameQName(Element component, String attribute, Map<String, String> identifiers) {
        if (component.hasAttribute(attribute)) {
            component.setAttribute(attribute, toValidQName(component.getAttribute(attribute), identifiers));
        }
    }

    private static @NotNull String toValidQName(String qName, Map<String, String> identifiers) {
        int localNameStart = qName.lastIndexOf(':') + 1;
        String localName = qName.substring(localNameStart);
        return qName.substring(0, localNameStart) +
                identifiers.getOrDefault(localName, toValidIdentifier(localName));
    }

    // Quoted identifiers aren't valid XSD names; xsd-core quotes keywords itself.
    private static @NotNull String toValidIdentifier(String name) {
        String sanitized = ConversionUtils.sanitizes(name);
        return sanitized.startsWith("'") ? name : sanitized;
    }

    // Keeps renamed root elements serialized under their original name. If xsd-core already added @xmldata:Name
    // (element sharing a type's name), that annotation is replaced instead.
    private static @NotNull ModulePartNode withXmlNames(ModulePartNode types,
                                                        Map<String, String> renamedRootElements) {
        assert types != null : "types must not be null";
        assert renamedRootElements != null : "renamedRootElements must not be null";
        Set<String> namedByAnnotation = types.members().stream()
                .flatMap(member -> member instanceof TypeDefinitionNode typeDefinition ?
                        xmlName(typeDefinition).stream() : Stream.empty())
                .collect(Collectors.toSet());
        List<ModuleMemberDeclarationNode> members = types.members().stream()
                .map(member -> member instanceof TypeDefinitionNode typeDefinition ?
                        withXmlName(typeDefinition, renamedRootElements, namedByAnnotation) : member)
                .toList();
        return types.modify()
                .withImports(withXmldataImport(types.imports()))
                .withMembers(NodeFactory.createNodeList(members))
                .apply();
    }

    private static @NotNull ModuleMemberDeclarationNode withXmlName(TypeDefinitionNode typeDefinition,
                                                                    Map<String, String> renamedRootElements,
                                                                    Set<String> namedByAnnotation) {
        Optional<String> elementName = xmlName(typeDefinition).or(() ->
                Optional.of(typeDefinition.typeName().text()).filter(name -> !namedByAnnotation.contains(name)));
        Optional<String> originalName = elementName.map(renamedRootElements::get);
        if (originalName.isEmpty()) {
            return typeDefinition;
        }
        MetadataNode metadata = typeDefinition.metadata()
                .orElseGet(() -> NodeFactory.createMetadataNode(null, NodeFactory.createEmptyNodeList()));
        List<AnnotationNode> annotations = Stream.concat(Stream.of(xmlNameAnnotation(originalName.get())),
                metadata.annotations().stream().filter(annotation -> !isXmlNameAnnotation(annotation))).toList();
        return typeDefinition.modify()
                .withMetadata(metadata.modify().withAnnotations(NodeFactory.createNodeList(annotations)).apply())
                .apply();
    }

    private static Optional<String> xmlName(TypeDefinitionNode typeDefinition) {
        return typeDefinition.metadata().stream()
                .flatMap(metadata -> metadata.annotations().stream())
                .filter(TypeConverter::isXmlNameAnnotation)
                .flatMap(annotation -> annotation.annotValue().stream())
                .flatMap(mapping -> mapping.fields().stream())
                .flatMap(field -> field instanceof SpecificFieldNode specificField ? Stream.of(specificField) :
                        Stream.empty())
                .filter(field -> field.fieldName().toSourceCode().trim().equals("value"))
                .flatMap(field -> field.valueExpr().stream())
                .map(value -> value.toSourceCode().trim())
                .map(literal -> literal.substring(1, literal.length() - 1))
                .findFirst();
    }

    private static boolean isXmlNameAnnotation(AnnotationNode annotation) {
        return annotation.annotReference().toSourceCode().trim().equals("xmldata:Name");
    }

    private static @NotNull AnnotationNode xmlNameAnnotation(String name) {
        return NodeFactory.createAnnotationNode(NodeFactory.createToken(SyntaxKind.AT_TOKEN),
                NodeParser.parseTypeDescriptor("xmldata:Name"),
                (MappingConstructorExpressionNode) NodeParser.parseExpression(
                        "{value: \"%s\"}".formatted(ConversionUtils.escapeString(name))));
    }

    private static @NotNull NodeList<ImportDeclarationNode> withXmldataImport(
            NodeList<ImportDeclarationNode> imports) {
        boolean hasXmldataImport = imports.stream()
                .anyMatch(importNode -> importNode.toSourceCode().contains("ballerina/data.xmldata"));
        return hasXmldataImport ? imports :
                imports.add(NodeParser.parseImportDeclaration("import ballerina/data.xmldata;"));
    }

    public static @NotNull Set<String> declaredNames(NodeResponse response) {
        return response.types().members().stream()
                .map(TypeConverter::declaredName)
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());
    }

    private static Optional<String> declaredName(ModuleMemberDeclarationNode member) {
        return switch (member) {
            case TypeDefinitionNode typeDefinition -> Optional.of(typeDefinition.typeName().text());
            case EnumDeclarationNode enumDeclaration -> Optional.of(enumDeclaration.identifier().text());
            case ConstantDeclarationNode constant -> Optional.of(constant.variableName().text());
            default -> Optional.empty();
        };
    }

    static @NotNull SyntaxTree convertSchemas(ContextWithFile cx, Collection<Type.Schema> schemas,
                                              Optional<NodeResponse> pregenerated) {
        cx.logState("Converting XSD schemas to Ballerina types");
        for (int i = 0; i < schemas.size(); i++) {
            cx.getProjectContext().incrementTypeCount();
        }
        try {
            NodeResponse response = pregenerated.orElseGet(() -> generateTypes(schemas));
            logTypeConversionErrors(cx, response);
            SyntaxTree syntaxTree = SyntaxTree.from(TextDocuments.from(""));
            syntaxTree = syntaxTree.modifyWith(response.types());
            cx.logState("Done converting XSD schemas to Ballerina types");
            return CodeGenerator.formatSyntaxTree(syntaxTree);
        } catch (Exception e) {
            throw new RuntimeException("Type conversion failed due to: " + e.getMessage(), e);
        }
    }

    private static void logTypeConversionErrors(LoggingContext cx, NodeResponse response) {
        if (response.diagnostics().isEmpty()) {
            return;
        }
        cx.log(LoggingUtils.Level.WARN, "Errors detected while trying to convert XSD schemas to Ballerina types. " +
                "Falling back to placeholder types");
        for (var each : response.diagnostics()) {
            cx.log(LoggingUtils.Level.WARN, each.message());
        }
    }

    static @NotNull Collection<BallerinaModel.Service> convertWsdlDefinition(ProcessContext cx,
            WSDLDefinition wsdlDefinition) {
        Map<String, String> messageTypes = getMessageTypeDefinitions(cx, wsdlDefinition);
        return wsdlDefinition.portType().stream()
                .map(portType -> convertPortType(cx, messageTypes, portType, wsdlDefinition.namespaces(),
                        wsdlDefinition.messages()))
                .toList();
    }

    private static Map<String, String> getMessageTypeDefinitions(ProcessContext cx, WSDLDefinition wsdlDefinition) {
        Map<String, String> result = new HashMap<>();
        for (WSDLDefinition.Message message : wsdlDefinition.messages()) {
            Optional<String> referredTypeName = getMessageTypeName(cx, message);
            if (referredTypeName.isEmpty()) {
                continue;
            }
            result.put(message.name(), referredTypeName.get());
        }
        return result;
    }

    private static Optional<String> getMessageTypeName(ProcessContext cx, WSDLDefinition.Message message) {
        Optional<WSDLDefinition.Message.Part> part;
        if (message.parts().size() == 1) {
            part = Optional.ofNullable(message.parts().getFirst());
        } else {
            part = message.parts().stream().filter(each -> !each.name().contains("httpHeaders")).findFirst();
        }
        if (part.isEmpty()) {
            return Optional.empty();
        }
        String typeName = switch (part.get()) {
            case WSDLDefinition.Message.Part.InlineError inlineError -> {
                String constantName = inlineError.name();
                yield cx.declareConstant(constantName, inlineError.value(), inlineError.type());
            }
            case WSDLDefinition.Message.Part.Reference ref -> ref.element().value();
        };
        return Optional.of(typeName);
    }

    private static BallerinaModel.Service convertPortType(ProcessContext cx, Map<String, String> messageTypes,
                                                          WSDLDefinition.PortType portType,
                                                          Map<String, String> wsdlNamespaces,
                                                          Collection<WSDLDefinition.Message> messages) {
        String basePath = portType.basePath();
        if (!basePath.startsWith("/")) {
            basePath = "/" + basePath;
        }
        String apiPath = portType.apiPath();
        List<BallerinaModel.Resource> resources = List
                .of(convertOperation(cx, apiPath, messageTypes, portType.operation(), wsdlNamespaces, messages));
        return new BallerinaModel.Service(basePath, cx.getDefaultHttpListenerRef(), resources);
    }

    private static BallerinaModel.Resource convertOperation(ProcessContext cx, String apiPath,
                                                            Map<String, String> messageTypes,
                                                            Operation operation,
                                                            Map<String, String> wsdlNamespaces,
                                                            Collection<WSDLDefinition.Message> messages) {

        VariableReference bodyRef = new VariableReference("req");
        String resourceMethodName = operation.name();
        var resourcePath = toResourcePath(apiPath);
        String path = resourcePath.path;
        Optional<BallerinaModel.TypeDesc> expectedBodyType = resourceMethodName.equals("get") ? Optional.empty() :
                Optional.of(cx.getTypeByName(messageTypes.get(operation.input().message().value())));
        Optional<ParamInitResult> bodyDataBinding =
                expectedBodyType.map(inTy -> dataBindingForBody(cx, inTy, bodyRef));
        List<BallerinaModel.Statement> body = new ArrayList<>();
        bodyDataBinding.map(ParamInitResult::initStatements).ifPresent(body::addAll);
        ParamInitResult paramsXML =
                initParams(cx, resourceMethodName, resourcePath, operation.input(), wsdlNamespaces, messages);
        body.addAll(paramsXML.initStatements);

        Optional<BallerinaModel.Parameter> resourceMethodParameter =
                expectedBodyType.map(expectedTy ->
                        new BallerinaModel.Parameter(bodyRef.varName(), cx.serviceInputType(expectedTy)));

        VarDeclStatment contextDecl = new VarDeclStatment(cx.contextType(), ConversionUtils.Constants.CONTEXT_VAR_NAME,
                new FunctionCall(cx.getInitContextFn(),
                        List.of(new VariableReference(paramsXML.paramName))));
        body.add(contextDecl);
        ProjectContext.FunctionData startFuncData = cx.getProcessStartFunction();
        body.add(new BallerinaModel.Statement.CallStatement(
                new FunctionCall(startFuncData.name(), List.of(contextDecl.ref()))));

        body.add(new Return<>(new FunctionCall(cx.getResponseFromContextFn(), List.of(contextDecl.ref()))));

        cx.addLibraryImport(Library.HTTP);
        return new BallerinaModel.Resource(resourceMethodName, path, resourceMethodParameter.stream().toList(),
                Optional.of(BallerinaModel.TypeDesc.UnionTypeDesc.of(ConversionUtils.Constants.HTTP_RESPONSE, ERROR)),
                body);
    }

    private static ParamInitResult dataBindingForBody(ProcessContext cx,
                                                      BallerinaModel.TypeDesc targetType,
                                                      VariableReference inputValRef) {
        List<BallerinaModel.Statement> body = new ArrayList<>();
        VarDeclStatment inputDecl = new VarDeclStatment(
                BallerinaModel.TypeDesc.UnionTypeDesc.of(targetType, ERROR),
                "input",
                new FunctionCall(cx.getTryDataBindToTypeFunction(targetType), List.of(inputValRef)));
        body.add(inputDecl);
        IfElseStatement handleError = IfElseStatement.ifStatement(new TypeCheckExpression(inputDecl.ref(), ERROR),
                List.of(new Return<>(inputDecl.ref())));
        body.add(handleError);
        return new ParamInitResult(inputDecl.varName(), body);
    }

    private record ParamInitResult(String paramName, List<BallerinaModel.Statement> initStatements) {

        ParamInitResult {
            assert paramName != null && !paramName.isEmpty();
            initStatements = Collections.unmodifiableList(initStatements);
        }

    }

    private static ParamInitResult initParams(ProcessContext cx, String resourceMethod, ResourcePath resourcePath,
                                              Operation.Input input, Map<String, String> wsdlNameSpaces,
                                              Collection<WSDLDefinition.Message> messages) {
        Optional<ParamInitResult> pathParams = initPathParameters(resourcePath, input, wsdlNameSpaces, messages);
        Optional<ParamInitResult> bodyParams = resourceMethod.equals("get") ? Optional.empty()
                : Optional.of(initRequestBodyParams(cx));
        List<BallerinaModel.Statement> body = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        sb.append("<root>");
        pathParams.ifPresent(p -> {
            sb.append("${").append(p.paramName).append("}");
            body.addAll(p.initStatements);
        });
        bodyParams.ifPresent(p -> {
            sb.append("${").append(p.paramName).append("}");
            body.addAll(p.initStatements);
        });
        sb.append("</root>");

        VarDeclStatment inputXml = new VarDeclStatment(XML, "inputXmlMap", new XMLTemplate(sb.toString()));
        body.add(inputXml);

        VarDeclStatment paramXmlDecl = new VarDeclStatment(new MapTypeDesc(XML), "paramXML",
                exprFrom("{%s: %s}".formatted(resourceMethod, inputXml.varName())));
        body.add(paramXmlDecl);
        return new ParamInitResult(paramXmlDecl.varName(), body);
    }

    private static ParamInitResult initRequestBodyParams(ProcessContext cx) {
        String inputParamName = "input";
        String toXmlFunction = cx.getToXmlFunction();
        List<BallerinaModel.Statement> body = new ArrayList<>();
        VarDeclStatment inputValXml = new VarDeclStatment(XML, "inputValXml",
                new CheckPanic(new FunctionCall(toXmlFunction, new String[]{inputParamName})));
        body.add(inputValXml);
        VarDeclStatment extractedBody = new VarDeclStatment(XML, "extractedBody",
                exprFrom("%s/*".formatted(inputValXml.varName())));
        body.add(extractedBody);

        // TODO: how to handle arrays
        String xmlTemplate = """
                <item>
                    ${%s}
                </item>
                """.formatted(extractedBody.varName());
        VarDeclStatment inputXml = new VarDeclStatment(XML, "inputXml", new XMLTemplate(xmlTemplate));
        body.add(inputXml);
        return new ParamInitResult(inputXml.varName(), body);
    }

    private static Optional<ParamInitResult> initPathParameters(ResourcePath resourcePath, Operation.Input input,
                                                                Map<String, String> wsdlNameSpaces,
                                                                Collection<WSDLDefinition.Message> messages) {
        if (resourcePath.pathParams.isEmpty()) {
            return Optional.empty();
        }
        List<BallerinaModel.Statement> body = new ArrayList<>();
        StringBuilder xmlBody = new StringBuilder();
        for (String each : resourcePath.pathParams()) {
            xmlBody.append("""
                    <%s>
                        ${%s}
                    </%s>
                    """.formatted(each, each, each));
        }
        WSDLDefinition.Message inputMessage = messages.stream()
                .filter(message -> message.name().equals(input.message().value())).findFirst()
                .orElseThrow(() -> new IllegalStateException("Failed to find input message"));
        WSDLDefinition.Message.Part parameterPart = inputMessage.parts().stream()
                .filter(each -> each.name().equals("parameters")).findFirst()
                .orElseThrow(() -> new IllegalStateException("Failed to find parameter part"));
        assert parameterPart instanceof WSDLDefinition.Message.Part.Reference;
        String namespace = ((WSDLDefinition.Message.Part.Reference) parameterPart).element().nameSpace()
                .uri();
        String xml = """
                <parameters>
                    <ns:%1$s xmlns:ns="%2$s">
                        %3$s
                    </ns:%1$s>
                </parameters>""".formatted(parameterPart.name(), wsdlNameSpaces.get(namespace), xmlBody);
        VarDeclStatment pathParamDecl =
                new VarDeclStatment(XML, "pathParams", new XMLTemplate(xml));
        body.add(pathParamDecl);
        return Optional.of(new ParamInitResult(pathParamDecl.varName(), body));
    }

    private static @NotNull ResourcePath toResourcePath(String apiPath) {
        if (apiPath == null || apiPath.isEmpty()) {
            return new ResourcePath(".", List.of());
        }

        if (apiPath.startsWith("/")) {
            apiPath = apiPath.substring(1);
        }

        String[] segments = apiPath.split("/");
        StringJoiner joiner = new StringJoiner("/");
        List<String> params = new ArrayList<>();

        for (String segment : segments) {
            if (segment.startsWith("{") && segment.endsWith("}")) {
                String paramName = segment.substring(1, segment.length() - 1);
                params.add(paramName);
                joiner.add("[string " + paramName + "]");
            } else {
                joiner.add(segment);
            }
        }

        return new ResourcePath(joiner.toString(), params);
    }

    record ResourcePath(String path, List<String> pathParams) {

    }
}
