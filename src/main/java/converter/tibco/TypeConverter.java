package converter.tibco;

import ballerina.BallerinaModel;
import ballerina.BallerinaModel.Statement.Return;
import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;
import tibco.TibcoModel;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static ballerina.BallerinaModel.TypeDesc.BuiltinType.ANYDATA;
import static io.ballerina.xsd.core.XSDToRecord.generateNodes;

class TypeConverter {

    private TypeConverter() {
    }

    static void convertSchema(ContextWithFile cx, TibcoModel.Type.Schema schema) {
        String body = ConversionUtils.elementToString(schema.element());
        try {
            cx.getProjectContext().incrementTypeCount();
            Map<String, ModuleMemberDeclarationNode> result = generateNodes(body);
            result.forEach(cx::addTypeAstNode);
        } catch (Exception e) {
            String name = schema.element().getAttribute("name");
            String reason = e.getMessage();
            convertUnhandledTypes(cx, name, body, reason);
        }
    }

    private static void convertUnhandledTypes(ContextWithFile cx, String name, String element, String reason) {
        cx.getProjectContext().incrementUnhandledTypeCount();
        List<BallerinaModel.Comment> comments = List.of(
                new BallerinaModel.Comment("FIXME: Failed to convert type due to " + reason),
                new BallerinaModel.Comment(element));
        cx.addModuleTypeDef(name, new BallerinaModel.ModuleTypeDef(name, ANYDATA, comments));
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
            part = message.parts().stream().filter(each -> each.name().equals("item")).findFirst();
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
        String path = apiPath.startsWith("/") ? apiPath.substring(1) : apiPath;
        BallerinaModel.TypeDesc inputType = cx.getTypeByName(messageTypes.get(operation.input().message().value()));
        List<BallerinaModel.Parameter> parameters = List.of(new BallerinaModel.Parameter("input", inputType));
        List<BallerinaModel.TypeDesc> returnTypeMembers = Stream.concat(
                Stream.of(operation.output().message()),
                operation.faults().stream().map(
                        TibcoModel.Type.WSDLDefinition.PortType.Operation.Fault::message))
                .map(message -> cx.getTypeByName(messageTypes.get(message.value()))).toList();
        BallerinaModel.TypeDesc returnType = returnTypeMembers.size() == 1
                ? returnTypeMembers.getFirst()
                : new BallerinaModel.TypeDesc.UnionTypeDesc(returnTypeMembers);
        var startFunction = cx.getProcessStartFunction();
        List<BallerinaModel.Statement> body = List.of(new Return<>(Optional.of(
                new BallerinaModel.Expression.FunctionCall(startFunction.name(), new String[] { "input" }))));
        return new BallerinaModel.Resource(resourceMethodName, path, parameters, Optional.of(returnType.toString()),
                body);
    }

    private record RecordBody(List<BallerinaModel.TypeDesc.RecordTypeDesc.RecordField> fields,
            Optional<BallerinaModel.TypeDesc> rest) {

    }
}
