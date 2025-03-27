package converter.tibco;

import ballerina.BallerinaModel;
import ballerina.BallerinaModel.Comment;
import tibco.TibcoModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static ballerina.BallerinaModel.TypeDesc.BuiltinType.ANYDATA;
import static ballerina.BallerinaModel.TypeDesc.BuiltinType.NEVER;

import ballerina.BallerinaModel;
import ballerina.BallerinaModel.Statement.Return;
import tibco.TibcoModel;

class TypeConverter {

    private TypeConverter() {
    }

    private record TypeConversionContext(ContextWithFile fileCx,
            BallerinaModel.TypeDesc.RecordTypeDesc.Namespace namespace) {

    }

    static void convertSchema(ContextWithFile cx,
            TibcoModel.Type.Schema schema) {
        // FIXME:
        var tcx = new TypeConversionContext(cx, ConversionUtils.createNamespace(schema.targetNamespace()));
        Stream<BallerinaModel.ModuleTypeDef> newTypeDefinitions = schema.types().stream()
                .filter(type -> !type.name().equals("anydata"))
                .map(type -> convertComplexType(tcx, type));
        Stream<BallerinaModel.ModuleTypeDef> typeAliases = schema.elements().stream()
                .map(element -> convertTypeAlias(cx, element))
                .filter(Optional::isPresent).map(Optional::get);
        Stream<BallerinaModel.ModuleTypeDef> unhandledTypes = schema.unhandledTypes().stream()
                .map(ty -> convertUnhandledTypes(cx, ty));
        var list = Stream.of(newTypeDefinitions, typeAliases, unhandledTypes)
                .flatMap(s -> s).toList();
    }

    private static BallerinaModel.ModuleTypeDef convertUnhandledTypes(
            ContextWithFile cx, TibcoModel.Type.Schema.UnhandledType unhandledType) {
        cx.getProjectContext().incrementUnhandledTypeCount();
        List<Comment> comments = List.of(
                new Comment("FIXME: Failed to convert type due to " + unhandledType.reason()),
                new Comment(unhandledType.elementAsString()));
        var defn = new BallerinaModel.ModuleTypeDef(unhandledType.name(), ANYDATA, comments);
        cx.addModuleTypeDef(unhandledType.name(), defn);
        return defn;
    }

    private static Optional<BallerinaModel.ModuleTypeDef> convertTypeAlias(ContextWithFile cx,
            TibcoModel.Type.Schema.Element element) {
        String name = XmlToTibcoModelConverter.getTagNameWithoutNameSpace(element.name());
        BallerinaModel.TypeDesc ref = cx.getTypeByName(element.type().name());
        BallerinaModel.ModuleTypeDef defn = new BallerinaModel.ModuleTypeDef(name, ref);
        cx.getProjectContext().incrementTypeAliasCount();
        return cx.addModuleTypeDef(name, defn) ? Optional.of(defn) : Optional.empty();
    }

    private static BallerinaModel.ModuleTypeDef convertComplexType(TypeConversionContext cx,
            TibcoModel.Type.Schema.ComplexType complexType) {
        BallerinaModel.TypeDesc typeDesc = switch (complexType.body()) {
            case TibcoModel.Type.Schema.ComplexType.Choice choice -> convertTypeChoice(cx, choice);
            case TibcoModel.Type.Schema.ComplexType.SequenceBody sequenceBody -> convertSequenceBody(cx, sequenceBody);
            case TibcoModel.Type.Schema.ComplexType.ComplexContent complexContent ->
                convertTypeInclusion(cx, complexContent);

            case TibcoModel.Type.Schema.ComplexType.SimpleContent simpleContent ->
                cx.fileCx.getTypeByName(simpleContent.base().name());
        };
        String name = complexType.name();
        BallerinaModel.ModuleTypeDef typeDef = new BallerinaModel.ModuleTypeDef(name, typeDesc);
        cx.fileCx.addModuleTypeDef(name, typeDef);
        cx.fileCx.getProjectContext().incrementTypeCount();
        return typeDef;
    }

    private static BallerinaModel.TypeDesc.RecordTypeDesc convertTypeInclusion(
            TypeConversionContext cx,
            TibcoModel.Type.Schema.ComplexType.ComplexContent complexContent) {
        List<BallerinaModel.TypeDesc> inclusions = List
                .of(cx.fileCx.getTypeByName(complexContent.extension().base().name()));
        RecordBody body = getRecordBody(cx, complexContent.extension().elements());
        return new BallerinaModel.TypeDesc.RecordTypeDesc(inclusions, body.fields(), body.rest().orElse(NEVER),
                Optional.of(cx.namespace));
    }

    private static BallerinaModel.TypeDesc.RecordTypeDesc convertSequenceBody(
            TypeConversionContext cx,
            TibcoModel.Type.Schema.ComplexType.SequenceBody sequenceBody) {
        Collection<TibcoModel.Type.Schema.ComplexType.SequenceBody.Member> members = sequenceBody.elements();
        RecordBody body = getRecordBody(cx, members);
        return new BallerinaModel.TypeDesc.RecordTypeDesc(List.of(), body.fields(), body.rest().orElse(NEVER),
                Optional.of(cx.namespace));
    }

    private static RecordBody getRecordBody(
            TypeConversionContext cx,
            Collection<? extends TibcoModel.Type.Schema.ComplexType.SequenceBody.Member> members) {
        List<BallerinaModel.TypeDesc.RecordTypeDesc.RecordField> fields = new ArrayList<>();
        Optional<BallerinaModel.TypeDesc> rest = Optional.empty();
        for (TibcoModel.Type.Schema.ComplexType.SequenceBody.Member member : members) {
            switch (member) {
                case TibcoModel.Type.Schema.ComplexType.SequenceBody.Member.Element element -> {
                    BallerinaModel.TypeDesc typeDesc = cx.fileCx.getTypeByName(element.type().name());
                    String name = ConversionUtils.sanitizes(element.name());
                    fields.add(new BallerinaModel.TypeDesc.RecordTypeDesc.RecordField(name, typeDesc, cx.namespace,
                            element.optional()));
                }
                case TibcoModel.Type.Schema.ComplexType.SequenceBody.Member.Rest ignored -> rest = Optional.of(ANYDATA);
                case TibcoModel.Type.Schema.ComplexType.Choice choice ->
                    rest = Optional.of(convertTypeChoice(cx, choice));
                case TibcoModel.Type.Schema.ComplexType.SequenceBody.Member.ElementArray elementArray -> {
                    BallerinaModel.TypeDesc typeDesc = new BallerinaModel.TypeDesc.ListType(
                            cx.fileCx.getTypeByName(elementArray.elementType().name()));
                    String name = ConversionUtils.sanitizes(elementArray.name());
                    fields.add(new BallerinaModel.TypeDesc.RecordTypeDesc.RecordField(name, typeDesc, cx.namespace));
                }
            }
        }
        return new RecordBody(fields, rest);
    }

    private static BallerinaModel.TypeDesc.UnionTypeDesc convertTypeChoice(
            TypeConversionContext cx,
            TibcoModel.Type.Schema.ComplexType.Choice choice) {
        List<? extends BallerinaModel.TypeDesc> types = choice.elements().stream().map(element -> {
            BallerinaModel.TypeDesc typeDesc = cx.fileCx.getTypeByName(element.ref().name());
            assert element.maxOccurs() == 1;
            if (element.minOccurs() == 0) {
                return BallerinaModel.TypeDesc.UnionTypeDesc.of(typeDesc, BallerinaModel.TypeDesc.BuiltinType.NIL);
            } else {
                return typeDesc;
            }
        }).flatMap(type -> {
            if (type instanceof BallerinaModel.TypeDesc.UnionTypeDesc(Collection<? extends BallerinaModel.TypeDesc> members)) {
                return members.stream();
            } else {
                return Stream.of(type);
            }
        }).distinct().toList();
        return new BallerinaModel.TypeDesc.UnionTypeDesc(types);
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
