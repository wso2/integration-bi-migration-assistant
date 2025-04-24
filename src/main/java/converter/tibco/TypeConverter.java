package converter.tibco;

import ballerina.BallerinaModel;
import ballerina.BallerinaModel.Statement.Return;
import ballerina.BallerinaModel.Statement.VarDeclStatment;
import ballerina.CodeGenerator;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.tools.text.TextDocuments;
import io.ballerina.xsd.core.response.NodeResponse;
import org.jetbrains.annotations.NotNull;
import tibco.TibcoModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Stream;

import static ballerina.BallerinaModel.TypeDesc.BuiltinType.XML;
import static io.ballerina.xsd.core.XSDToRecord.generateNodes;

class TypeConverter {

    private TypeConverter() {
    }

    static SyntaxTree convertSchemas(ContextWithFile cx, Collection<TibcoModel.Type.Schema> schemas) {
        String[] content = schemas.stream().map(TibcoModel.Type.Schema::element).map(ConversionUtils::elementToString)
                .toArray(String[]::new);

        for (int i = 0; i < content.length; i++) {
            cx.getProjectContext().incrementTypeCount();
        }
        try {
            NodeResponse response = generateNodes(content);
            assert response.diagnostics().isEmpty();
            SyntaxTree syntaxTree = SyntaxTree.from(TextDocuments.from(""));
            syntaxTree = syntaxTree.modifyWith(response.types());
            return CodeGenerator.formatSyntaxTree(syntaxTree);
        } catch (Exception e) {
            // TODO: deal with errors
            throw new RuntimeException("Type conversion failed due to: " + e.getMessage(), e);
        }
    }

    static Collection<BallerinaModel.Service> convertWsdlDefinition(ProcessContext cx,
            TibcoModel.Type.WSDLDefinition wsdlDefinition) {
        Map<String, String> messageTypes = getMessageTypeDefinitions(cx, wsdlDefinition);
        return wsdlDefinition.portType().stream()
                .map(portType -> convertPortType(cx, messageTypes, portType, wsdlDefinition.namespaces(),
                        wsdlDefinition.messages()))
                .toList();
    }

    private static Map<String, String> getMessageTypeDefinitions(ProcessContext cx,
            TibcoModel.Type.WSDLDefinition wsdlDefinition) {
        Map<String, String> result = new HashMap<>();
        for (TibcoModel.Type.WSDLDefinition.Message message : wsdlDefinition.messages()) {
            Optional<String> referredTypeName = getMessageTypeName(cx, message);
            if (referredTypeName.isEmpty()) {
                continue;
            }
            result.put(message.name(), referredTypeName.get());
        }
        return result;
    }

    private static Optional<String> getMessageTypeName(ProcessContext cx,
            TibcoModel.Type.WSDLDefinition.Message message) {
        Optional<TibcoModel.Type.WSDLDefinition.Message.Part> part;
        if (message.parts().size() == 1) {
            part = Optional.ofNullable(message.parts().getFirst());
        } else {
            part = message.parts().stream().filter(each -> !each.name().contains("httpHeaders")).findFirst();
        }
        if (part.isEmpty()) {
            return Optional.empty();
        }
        String typeName = switch (part.get()) {
            case TibcoModel.Type.WSDLDefinition.Message.Part.InlineError inlineError -> {
                String constantName = inlineError.name();
                yield cx.declareConstant(constantName, inlineError.value(), inlineError.type());
            }
            case TibcoModel.Type.WSDLDefinition.Message.Part.Reference ref -> ref.element().value();
        };
        return Optional.of(typeName);
    }

    private static BallerinaModel.Service convertPortType(ProcessContext cx,
            Map<String, String> messageTypes,
            TibcoModel.Type.WSDLDefinition.PortType portType,
            Map<String, String> wsdlNamespaces,
            Collection<TibcoModel.Type.WSDLDefinition.Message> messages) {
        String basePath = portType.basePath();
        if (!basePath.startsWith("/")) {
            basePath = "/" + basePath;
        }
        String apiPath = portType.apiPath();
        List<BallerinaModel.Resource> resources = List
                .of(convertOperation(cx, apiPath, messageTypes, portType.operation(), wsdlNamespaces, messages));
        List<String> listenerRefs = List.of(cx.getDefaultHttpListenerRef());
        return new BallerinaModel.Service(basePath, listenerRefs, Optional.empty(), resources, List.of(), List.of(),
                List.of(), List.of());
    }

    private static BallerinaModel.Resource convertOperation(
            ProcessContext cx,
            String apiPath, Map<String, String> messageTypes,
            TibcoModel.Type.WSDLDefinition.PortType.Operation operation,
            Map<String, String> wsdlNameSpaces,
            Collection<TibcoModel.Type.WSDLDefinition.Message> messages) {
        String resourceMethodName = operation.name();
        var resourcePath = toResourcePath(apiPath);
        String path = resourcePath.path;
        var paramsXML = initParams(cx, resourceMethodName, resourcePath, operation.input(), wsdlNameSpaces, messages);

        List<BallerinaModel.Statement> body = new ArrayList<>(paramsXML.initStatements);

        Optional<BallerinaModel.Parameter> resourceMethodParameter =
                resourceMethodName.equals("get") ? Optional.empty() :
                        Optional.of(cx.getTypeByName(messageTypes.get(operation.input().message().value())))
                                .map(ty -> new BallerinaModel.Parameter("input", ty));

        BallerinaModel.TypeDesc returnType = getOperationReturnType(cx, messageTypes, operation);

        String[] startFunctionArgs = Stream.concat(
                resourceMethodParameter.map(BallerinaModel.Parameter::name).or("()"::describeConstable).stream(),
                Stream.of(paramsXML.paramName)
        ).toArray(String[]::new);

        body.add(new Return<>(Optional.of(
                new BallerinaModel.Expression.FunctionCall(cx.getProcessStartFunction().name(), startFunctionArgs))));

        return new BallerinaModel.Resource(resourceMethodName, path, resourceMethodParameter.stream().toList(),
                Optional.of(returnType.toString()), body);
    }

    private static BallerinaModel.TypeDesc getOperationReturnType(
            ProcessContext cx, Map<String, String> messageTypes,
            TibcoModel.Type.WSDLDefinition.PortType.Operation operation
    ) {
        List<BallerinaModel.TypeDesc> returnTypeMembers = Stream.concat(
                        Stream.of(operation.output().message()),
                        operation.faults().stream().map(
                                TibcoModel.Type.WSDLDefinition.PortType.Operation.Fault::message))
                .map(message -> cx.getTypeByName(messageTypes.get(message.value()))).toList();
        return returnTypeMembers.size() == 1 ? returnTypeMembers.getFirst() :
                new BallerinaModel.TypeDesc.UnionTypeDesc(returnTypeMembers);
    }

    private record ParamInitResult(String paramName, List<BallerinaModel.Statement> initStatements) {

        ParamInitResult {
            assert paramName != null && !paramName.isEmpty();
            initStatements = Collections.unmodifiableList(initStatements);
        }

    }

    private static ParamInitResult initParams(
            ProcessContext cx,
            String resourceMethod, ResourcePath resourcePath,
            TibcoModel.Type.WSDLDefinition.PortType.Operation.Input input, Map<String, String> wsdlNameSpaces,
            Collection<TibcoModel.Type.WSDLDefinition.Message> messages
    ) {
        Optional<ParamInitResult> pathParams = initPathParameters(resourcePath, input, wsdlNameSpaces, messages);
        Optional<ParamInitResult> bodyParams = resourceMethod.equals("get") ? Optional.empty()
                : Optional.of(initRequestBodyParamsNew(cx));
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

        VarDeclStatment inputXml = new VarDeclStatment(XML, "inputXmlMap",
                new BallerinaModel.Expression.XMLTemplate(sb.toString()));
        body.add(inputXml);

        VarDeclStatment paramXmlDecl = new VarDeclStatment(new BallerinaModel.TypeDesc.MapTypeDesc(XML), "paramXML",
                new BallerinaModel.Expression.BallerinaExpression(
                        "{%s: %s}".formatted(resourceMethod, inputXml.varName())));
        body.add(paramXmlDecl);
        return new ParamInitResult(paramXmlDecl.varName(), body);
    }

    private static ParamInitResult initRequestBodyParamsNew(ProcessContext cx) {
        String inputParamName = "input";
        String toXmlFunction = cx.getToXmlFunction();
        List<BallerinaModel.Statement> body = new ArrayList<>();
        VarDeclStatment inputValXml = new VarDeclStatment(XML, "inputValXml",
                new BallerinaModel.Expression.CheckPanic(new BallerinaModel.Expression.FunctionCall(toXmlFunction,
                        new String[]{inputParamName})));
        body.add(inputValXml);
        VarDeclStatment extractedBody = new VarDeclStatment(XML, "extractedBody",
                new BallerinaModel.Expression.BallerinaExpression("%s/*".formatted(inputValXml.varName())));
        body.add(extractedBody);

        // TODO: how to handle arrays
        String xmlTemplate = """
                <item>
                    ${%s}
                </item>
                """.formatted(extractedBody.varName());
        VarDeclStatment inputXml = new VarDeclStatment(XML, "inputXml",
                new BallerinaModel.Expression.XMLTemplate(xmlTemplate));
        body.add(inputXml);
        return new ParamInitResult(inputXml.varName(), body);
    }

    private static Optional<ParamInitResult> initPathParameters(
            ResourcePath resourcePath,
            TibcoModel.Type.WSDLDefinition.PortType.Operation.Input input,
            Map<String, String> wsdlNameSpaces,
            Collection<TibcoModel.Type.WSDLDefinition.Message> messages
    ) {

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
        TibcoModel.Type.WSDLDefinition.Message inputMessage = messages.stream()
                .filter(message -> message.name().equals(input.message().value())).findFirst()
                .orElseThrow(() -> new IllegalStateException("Failed to find input message"));
        TibcoModel.Type.WSDLDefinition.Message.Part parameterPart = inputMessage.parts().stream()
                .filter(each -> each.name().equals("parameters")).findFirst()
                .orElseThrow(() -> new IllegalStateException("Failed to find parameter part"));
        assert parameterPart instanceof TibcoModel.Type.WSDLDefinition.Message.Part.Reference;
        String namespace = ((TibcoModel.Type.WSDLDefinition.Message.Part.Reference) parameterPart).element().nameSpace()
                .uri();
        String xml = """
                <parameters>
                    <ns:%1$s xmlns:ns="%2$s">
                        %3$s
                    </ns:%1$s>
                </parameters>""".formatted(parameterPart.name(), wsdlNameSpaces.get(namespace), xmlBody);
        VarDeclStatment pathParamDecl =
                new VarDeclStatment(XML, "pathParams", new BallerinaModel.Expression.XMLTemplate(xml));
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
