package converter.tibco;

import ballerina.BallerinaModel;

import ballerina.BallerinaModel.Statement.Return;
import ballerina.BallerinaModel.Statement.VarDeclStatment;
import org.jetbrains.annotations.NotNull;

import ballerina.CodeGenerator;
import tibco.TibcoModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Stream;

import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.tools.text.TextDocuments;
import io.ballerina.xsd.core.response.NodeResponse;
import io.ballerina.xsd.core.response.Response;

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
        return wsdlDefinition.portType().stream().map(portType -> convertPortType(cx, messageTypes, portType)).toList();
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
            TibcoModel.Type.WSDLDefinition.PortType portType) {
        String basePath = portType.basePath();
        if (!basePath.startsWith("/")) {
            basePath = "/" + basePath;
        }
        String apiPath = portType.apiPath();
        List<BallerinaModel.Resource> resources = List
                .of(convertOperation(cx, apiPath, messageTypes, portType.operation()));
        List<String> listenerRefs = List.of(cx.getDefaultHttpListenerRef());
        return new BallerinaModel.Service(basePath, listenerRefs, Optional.empty(), resources, List.of(), List.of(),
                List.of(), List.of());
    }

    private static BallerinaModel.Resource convertOperation(
            ProcessContext cx,
            String apiPath, Map<String, String> messageTypes,
            TibcoModel.Type.WSDLDefinition.PortType.Operation operation) {
        String resourceMethodName = operation.name();
        var resourcePath = toResourcePath(apiPath);
        String path = resourcePath.path;
        ParamInitResult params = initParams(resourceMethodName, resourcePath);
        List<BallerinaModel.Statement> body = new ArrayList<>(params.initStatements());
        Optional<BallerinaModel.TypeDesc> inputType = resourceMethodName.equals("get") ? Optional.empty()
                : Optional.of(cx.getTypeByName(messageTypes.get(operation.input().message().value())));
        Optional<BallerinaModel.Parameter> parameter = inputType.map(ty -> new BallerinaModel.Parameter("input", ty));
        List<BallerinaModel.TypeDesc> returnTypeMembers = Stream.concat(
                Stream.of(operation.output().message()),
                operation.faults().stream().map(
                        TibcoModel.Type.WSDLDefinition.PortType.Operation.Fault::message))
                .map(message -> cx.getTypeByName(messageTypes.get(message.value()))).toList();
        BallerinaModel.TypeDesc returnType = returnTypeMembers.size() == 1
                ? returnTypeMembers.getFirst()
                : new BallerinaModel.TypeDesc.UnionTypeDesc(returnTypeMembers);
        var startFunction = cx.getProcessStartFunction();
        List<String> args = Stream
                .concat(parameter.map(BallerinaModel.Parameter::name).or("()"::describeConstable).stream(),
                        params.paramName().stream())
                .toList();
        body.add(new Return<>(Optional.of(
                new BallerinaModel.Expression.FunctionCall(startFunction.name(), args.toArray(String[]::new)))));
        return new BallerinaModel.Resource(resourceMethodName, path, parameter.stream().toList(),
                Optional.of(returnType.toString()), body);
    }

    private record ParamInitResult(Optional<String> paramName, List<BallerinaModel.Statement> initStatements) {

        private ParamInitResult {
            assert paramName.isEmpty() || !initStatements.isEmpty();
        }
    }

    private static ParamInitResult initParams(String resourceMethod, ResourcePath resourcePath) {
        if (resourcePath.pathParams.isEmpty()) {
            return new ParamInitResult(Optional.empty(), List.of());
        }
        List<BallerinaModel.Statement> body = new ArrayList<>();
        StringBuilder xmlBody = new StringBuilder();
        for (String each : resourcePath.pathParams()) {
            xmlBody.append("""
                    <%s>
                        {$%s}
                    </%s>
                    """.formatted(each, each, each));
        }
        String xml = "<parameters>\n" + xmlBody + "\n</parameters>";
        String paramsXML = "paramsXML";
        String params = "params";
        VarDeclStatment xmlDecl = new VarDeclStatment(XML, paramsXML,
                new BallerinaModel.Expression.XMLTemplate(xml));
        body.add(xmlDecl);

        VarDeclStatment paramsDecl = new VarDeclStatment(
                new BallerinaModel.TypeDesc.MapTypeDesc(XML), params,
                new BallerinaModel.BallerinaExpression("{%s: %s}".formatted(resourceMethod, paramsXML)));
        body.add(paramsDecl);

        return new ParamInitResult(Optional.of(params), body);
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
