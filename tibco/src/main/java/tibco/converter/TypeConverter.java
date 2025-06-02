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
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.tools.text.TextDocuments;
import io.ballerina.xsd.core.response.NodeResponse;
import org.jetbrains.annotations.NotNull;
import tibco.TibcoModel;
import tibco.TibcoModel.Type.WSDLDefinition;
import tibco.TibcoModel.Type.WSDLDefinition.PortType.Operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static common.BallerinaModel.TypeDesc.BuiltinType.ERROR;
import static common.BallerinaModel.TypeDesc.BuiltinType.XML;
import static common.ConversionUtils.exprFrom;
import static io.ballerina.xsd.core.XSDToRecord.generateNodes;

class TypeConverter {

    private static final Logger logger = ProjectConverter.logger();
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
            logTypeConversionErrors(response);
            SyntaxTree syntaxTree = SyntaxTree.from(TextDocuments.from(""));
            syntaxTree = syntaxTree.modifyWith(response.types());
            return CodeGenerator.formatSyntaxTree(syntaxTree);
        } catch (Exception e) {
            throw new RuntimeException("Type conversion failed due to: " + e.getMessage(), e);
        }
    }

    private static void logTypeConversionErrors(NodeResponse response) {
        if (response.diagnostics().isEmpty()) {
            return;
        }
        logger.log(Level.WARNING, "Errors detected while trying to convert XSD schemas to Ballerina types. " +
                "Falling back to placeholder types");
        for (var each : response.diagnostics()) {
            logger.log(Level.WARNING, each.message());
        }
    }

    static Collection<BallerinaModel.Service> convertWsdlDefinition(ProcessContext cx, WSDLDefinition wsdlDefinition) {
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
        List<String> listenerRefs = List.of(cx.getDefaultHttpListenerRef());
        return new BallerinaModel.Service(basePath, listenerRefs, Optional.empty(), resources, List.of(), List.of(),
                List.of(), List.of());
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

        BallerinaModel.TypeDesc returnType = getOperationReturnType(cx, messageTypes, operation);

        String[] startFunctionArgs = Stream.concat(
                bodyDataBinding.map(ParamInitResult::paramName).or("()"::describeConstable).stream(),
                Stream.of(paramsXML.paramName)
        ).toArray(String[]::new);

        body.add(new Return<>(Optional.of(
                new FunctionCall(cx.getProcessStartFunction().name(), startFunctionArgs))));

        return new BallerinaModel.Resource(resourceMethodName, path, resourceMethodParameter.stream().toList(),
                Optional.of(returnType.toString()), body);
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
                List.of(new Return<>(exprFrom("<http:InternalServerError>{}"))));
        body.add(handleError);
        return new ParamInitResult(inputDecl.varName(), body);
    }

    private static BallerinaModel.TypeDesc getOperationReturnType(ProcessContext cx, Map<String, String> messageTypes,
                                                                  Operation operation) {
        List<BallerinaModel.TypeDesc> returnTypeMembers =
                Stream.concat(
                        Stream.of(operation.output().message()),
                                operation.faults().stream().map(Operation.Fault::message))
                        .map(message -> cx.getTypeByName(messageTypes.get(message.value())))
                        .toList();
        return returnTypeMembers.size() == 1 ? returnTypeMembers.getFirst() :
                new BallerinaModel.TypeDesc.UnionTypeDesc(returnTypeMembers);
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
