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

import ballerina.BallerinaModel;
import ballerina.BallerinaModel.Statement.Return;
import ballerina.BallerinaModel.Statement.VarAssignStatement;
import ballerina.BallerinaModel.Statement.VarDeclStatment;
import converter.tibco.analyzer.AnalysisResult;
import org.jetbrains.annotations.NotNull;
import tibco.TibcoModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static ballerina.BallerinaModel.TypeDesc.BuiltinType.JSON;
import static ballerina.BallerinaModel.TypeDesc.BuiltinType.STRING;
import static ballerina.BallerinaModel.TypeDesc.BuiltinType.XML;

class ActivityConverter {

        private ActivityConverter() {
        }

        public static BallerinaModel.Function convertActivity(ProcessContext cx,
                        TibcoModel.Scope.Flow.Activity activity) {
                return convertActivity(new ActivityContext(cx, activity), activity);
        }

        private static BallerinaModel.Function convertActivity(ActivityContext cx,
                        TibcoModel.Scope.Flow.Activity activity) {
                List<BallerinaModel.Statement> body = switch (activity) {
                        case TibcoModel.Scope.Flow.Activity.ActivityExtension activityExtension ->
                                convertActivityExtension(cx, activityExtension);
                        case TibcoModel.Scope.Flow.Activity.Empty ignored -> convertEmptyAction(cx);
                        case TibcoModel.Scope.Flow.Activity.ExtActivity extActivity ->
                                convertExtActivity(cx, extActivity);
                        case TibcoModel.Scope.Flow.Activity.Invoke invoke -> convertInvoke(cx, invoke);
                        case TibcoModel.Scope.Flow.Activity.Pick pick -> convertPickAction(cx, pick);
                        case TibcoModel.Scope.Flow.Activity.ReceiveEvent receiveEvent ->
                                convertReceiveEvent(cx, receiveEvent);
                        case TibcoModel.Scope.Flow.Activity.Reply reply -> convertReply(cx, reply);
                };
                return new BallerinaModel.Function(cx.functionName(), cx.parameters(), cx.returnType(), body);
        }

        private static List<BallerinaModel.Statement> convertReply(ActivityContext cx,
                        TibcoModel.Scope.Flow.Activity.Reply reply) {
                List<BallerinaModel.Statement> body = new ArrayList<>();
                BallerinaModel.Expression.VariableReference input = cx.getInputAsXml();
                BallerinaModel.Expression.VariableReference result;
                if (reply.inputBindings().isEmpty()) {
                        result = input;
                } else {
                        List<VarDeclStatment> inputBindings = convertInputBindings(cx, input, reply.inputBindings());
                        body.addAll(inputBindings);
                        result = new BallerinaModel.Expression.VariableReference(inputBindings.getLast().varName());
                }
                body.add(new Return<>(result));
                return body;
        }

        private static List<BallerinaModel.Statement> convertPickAction(ActivityContext cx,
                        TibcoModel.Scope.Flow.Activity.Pick pick) {
                return convertEmptyAction(cx);
        }

        private static List<BallerinaModel.Statement> convertEmptyAction(ActivityContext cx) {
                List<BallerinaModel.Statement> body = new ArrayList<>();
                BallerinaModel.Expression.VariableReference inputXml = cx.getInputAsXml();
                body.add(new Return<>(Optional.of(inputXml)));
                return body;
        }

        private static List<BallerinaModel.Statement> convertActivityExtension(
                        ActivityContext cx,
                        TibcoModel.Scope.Flow.Activity.ActivityExtension activityExtension) {
                var inputBindings = convertInputBindings(cx, cx.getInputAsXml(), activityExtension.inputBindings());
                List<BallerinaModel.Statement> body = new ArrayList<>(inputBindings);
                BallerinaModel.Expression.VariableReference result = new BallerinaModel.Expression.VariableReference(
                                inputBindings.getLast().varName());

                TibcoModel.Scope.Flow.Activity.ActivityExtension.Config config = activityExtension.config();
                List<BallerinaModel.Statement> rest = switch (config) {
                        case TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.End ignored ->
                                List.of(new Return<>(result));
                        case TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.HTTPSend httpSend ->
                                createHttpSend(cx, result, httpSend, activityExtension.outputVariable());
                        case TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.JsonOperation jsonOperation ->
                                createJsonOperation(cx, result, jsonOperation, activityExtension.outputVariable());
                };
                body.addAll(rest);
                return body;
        }

        private static List<BallerinaModel.Statement> createJsonOperation(
                        ActivityContext cx,
                        BallerinaModel.Expression.VariableReference inputVar,
                        TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.JsonOperation jsonOperation,
                        Optional<String> outputVarName) {
                // TODO: how to implement this
                return outputVarName.<List<BallerinaModel.Statement>>map(
                                s -> List.of(addToContext(cx, inputVar, s), new Return<>(inputVar)))
                                .orElseGet(() -> List.of(new Return<>(inputVar)));
        }

        private static List<BallerinaModel.Statement> createHttpSend(
                        ActivityContext cx,
                        BallerinaModel.Expression.VariableReference configVar,
                        TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.HTTPSend httpSend,
                        Optional<String> outputVarName) {
                String parseFn = cx.getParseHttpConfigFunction();
                List<BallerinaModel.Statement> body = new ArrayList<>();
                BallerinaModel.TypeDesc.TypeReference httpConfigType = cx.getHttpConfigType();
                BallerinaModel.Expression.FunctionCall parseCall = new BallerinaModel.Expression.FunctionCall(parseFn,
                                new String[] { configVar.varName() });
                VarDeclStatment configVarDecl = new VarDeclStatment(httpConfigType, cx.getAnnonVarName(), parseCall);
                body.add(configVarDecl);
                BallerinaModel.Expression.VariableReference configVarRef = new BallerinaModel.Expression.VariableReference(
                                configVarDecl.varName());

                BallerinaModel.Expression.VariableReference configurableHost = cx.addConfigurableVariable(STRING,
                                "host");
                VarDeclStatment client = createHTTPClientWithBasePath(cx, configurableHost);
                body.add(client);

                BallerinaModel.Expression.FunctionCall pathGetFunctionCall = new BallerinaModel.Expression.FunctionCall(
                                Intrinsics.CREATE_HTTP_REQUEST_PATH_FROM_CONFIG.name,
                                List.of(configVarRef));
                VarDeclStatment requestURI = new VarDeclStatment(STRING, cx.getAnnonVarName(), pathGetFunctionCall);
                body.add(requestURI);

                // TODO: handle non-post
                BallerinaModel.Action.RemoteMethodCallAction call = new BallerinaModel.Action.RemoteMethodCallAction(
                                new BallerinaModel.Expression.VariableReference(
                                                client.varName()),
                                "/" + requestURI.varName() + ".post",
                                List.of(new BallerinaModel.Expression.FieldAccess(configVarRef, "PostData"),
                                                new BallerinaModel.Expression.FieldAccess(configVarRef, "Headers")));
                BallerinaModel.Expression.CheckPanic checkPanic = new BallerinaModel.Expression.CheckPanic(call);
                VarDeclStatment responseDecl = new VarDeclStatment(JSON, cx.getAnnonVarName(), checkPanic);
                body.add(responseDecl);

                String jsonToXmlFunction = cx.processContext.getJsonToXMLFunction();
                BallerinaModel.Expression.FunctionCall jsonToXmlFunctionCall = new BallerinaModel.Expression.FunctionCall(
                                jsonToXmlFunction, new String[] { responseDecl.varName() });
                VarDeclStatment resultDecl = new VarDeclStatment(XML, cx.getAnnonVarName(), jsonToXmlFunctionCall);
                body.add(resultDecl);
                BallerinaModel.Expression.VariableReference result = new BallerinaModel.Expression.VariableReference(
                                resultDecl.varName());
                outputVarName.ifPresent(s -> body.add(addToContext(cx, result, s)));
                body.add(new Return<>(result));

                return body;
        }

        private static List<BallerinaModel.Statement> convertReceiveEvent(
                        ActivityContext cx,
                        TibcoModel.Scope.Flow.Activity.ReceiveEvent receiveEvent) {
                // This is just a no-op since, we have created the service already and connected
                // it to the process function
                // when handling the WSDL type definition.
                return createNoOp(cx);
        }

        private static @NotNull List<BallerinaModel.Statement> createNoOp(ActivityContext cx) {
                return List.of(new Return<>(cx.getInputAsXml()));
        }

        private static List<BallerinaModel.Statement> convertInvoke(ActivityContext cx,
                        TibcoModel.Scope.Flow.Activity.Invoke invoke) {
                List<BallerinaModel.Statement> body = new ArrayList<>();
                AnalysisResult ar = cx.processContext.analysisResult;
                TibcoModel.PartnerLink.Binding binding = ar.getBinding(invoke.partnerLink());
                VarDeclStatment clientDecl = createClientForBinding(cx, binding);
                body.add(clientDecl);
                // TODO: may be this needs to be json
                VarDeclStatment bindingCallResult = createBindingCall(cx, binding,
                                new BallerinaModel.Expression.VariableReference(clientDecl.varName()),
                                cx.getInputAsXml());
                body.add(bindingCallResult);
                String jsonToXMLFunction = cx.processContext.getJsonToXMLFunction();
                BallerinaModel.Expression.FunctionCall jsonToXMLFunctionCall = new BallerinaModel.Expression.FunctionCall(
                                jsonToXMLFunction,
                                new String[] { bindingCallResult.varName() });
                VarDeclStatment resultDecl = new VarDeclStatment(XML, cx.getAnnonVarName(), jsonToXMLFunctionCall);
                body.add(resultDecl);
                BallerinaModel.Expression.VariableReference result = new BallerinaModel.Expression.VariableReference(
                                resultDecl.varName());

                body.add(addToContext(cx, result, invoke.outputVariable()));
                body.add(new Return<>(result));
                return body;
        }

        private static VarDeclStatment createBindingCall(ActivityContext cx,
                        TibcoModel.PartnerLink.Binding binding,
                        BallerinaModel.Expression.VariableReference client,
                        BallerinaModel.Expression.VariableReference value) {
                String path = binding.path().path();
                assert binding.operation().method() == TibcoModel.PartnerLink.Binding.Operation.Method.POST;
                BallerinaModel.Action.RemoteMethodCallAction call = new BallerinaModel.Action.RemoteMethodCallAction(
                                client, "post",
                                List.of(new BallerinaModel.Expression.StringConstant(path), value));
                BallerinaModel.Expression.CheckPanic checkPanic = new BallerinaModel.Expression.CheckPanic(call);
                return new VarDeclStatment(JSON, cx.getAnnonVarName(), checkPanic);
        }

        private static VarDeclStatment createClientForBinding(ActivityContext cx,
                        TibcoModel.PartnerLink.Binding binding) {
                String basePath = binding.path().basePath();
                return createHTTPClientWithBasePath(cx, new BallerinaModel.Expression.StringConstant(basePath));
        }

        private static VarDeclStatment createHTTPClientWithBasePath(ActivityContext cx,
                        BallerinaModel.Expression basePath) {
                BallerinaModel.Expression.NewExpression newExpression = new BallerinaModel.Expression.NewExpression(
                                List.of(basePath));
                BallerinaModel.Expression.CheckPanic checkPanic = new BallerinaModel.Expression.CheckPanic(
                                newExpression);
                return new VarDeclStatment(cx.processContext.getTypeByName("http:Client"), cx.getAnnonVarName(),
                                checkPanic);
        }

        private static List<BallerinaModel.Statement> convertExtActivity(
                        ActivityContext cx,
                        TibcoModel.Scope.Flow.Activity.ExtActivity extActivity) {
                List<BallerinaModel.Statement> body = new ArrayList<>();
                BallerinaModel.Expression.VariableReference result = cx.getInputAsXml();
                if (!extActivity.inputBindings().isEmpty()) {
                        List<VarDeclStatment> inputBindings = convertInputBindings(cx, result,
                                        extActivity.inputBindings());
                        body.addAll(inputBindings);
                        result = new BallerinaModel.Expression.VariableReference(inputBindings.getLast().varName());
                }
                var startFunction = cx.getProcessStartFunctionName(extActivity.callProcess().subprocessName());

                String convertToTypeFunction = cx.processContext.getConvertToTypeFunction(startFunction.inputType());
                BallerinaModel.Expression.FunctionCall convertToTypeFunctionCall = new BallerinaModel.Expression.FunctionCall(
                                convertToTypeFunction, new String[] { result.varName() });

                BallerinaModel.Expression.FunctionCall startFunctionCall = new BallerinaModel.Expression.FunctionCall(
                                startFunction.name(), List.of(convertToTypeFunctionCall));
                BallerinaModel.Expression.FunctionCall convertToXmlCall = new BallerinaModel.Expression.FunctionCall(
                                cx.processContext.getToXmlFunction(),
                                List.of(startFunctionCall));
                VarDeclStatment resultDecl = new VarDeclStatment(XML, cx.getAnnonVarName(), convertToXmlCall);
                body.add(resultDecl);
                BallerinaModel.Expression.VariableReference resultRef = new BallerinaModel.Expression.VariableReference(
                                resultDecl.varName());
                body.add(addToContext(cx, resultRef, extActivity.outputVariable()));
                body.add(new Return<>(resultRef));
                return body;
        }

        private static List<VarDeclStatment> convertInputBindings(ActivityContext cx,
                        BallerinaModel.Expression.VariableReference input,
                        Collection<TibcoModel.Scope.Flow.Activity.InputBinding> inputBindings) {
                List<VarDeclStatment> varDelStatements = new ArrayList<>();
                BallerinaModel.Expression.VariableReference last = input;
                for (TibcoModel.Scope.Flow.Activity.InputBinding transform : inputBindings) {
                        TibcoModel.Scope.Flow.Activity.Expression.XSLT xslt = transform.xslt();

                        VarDeclStatment varDecl = xsltTransform(cx, last, xslt);
                        varDelStatements.add(varDecl);
                        last = new BallerinaModel.Expression.VariableReference(varDecl.varName());
                }
                return varDelStatements;
        }

        private static VarDeclStatment xsltTransform(ActivityContext cx,
                        BallerinaModel.Expression.VariableReference inputVariable,
                        TibcoModel.Scope.Flow.Activity.Expression.XSLT xslt) {
                cx.processContext.addLibraryImport(Library.XSLT);
                BallerinaModel.Expression.FunctionCall callExpr = new BallerinaModel.Expression.FunctionCall(
                                "xslt:transform",
                                new BallerinaModel.Expression[] { inputVariable,
                                                new BallerinaModel.Expression.XMLTemplate(xslt.expression()),
                                                cx.contextVarRef() });
                BallerinaModel.Expression.CheckPanic checkPanic = new BallerinaModel.Expression.CheckPanic(callExpr);
                return new VarDeclStatment(XML, cx.getAnnonVarName(), checkPanic);
        }

        private static VarAssignStatement addToContext(ActivityContext cx,
                        BallerinaModel.Expression.VariableReference value,
                        String key) {
                assert !key.isEmpty();
                return new VarAssignStatement(new BallerinaModel.Expression.MemberAccess(cx.contextVarRef(), key),
                                value);
        }
}
