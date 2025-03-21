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
import ballerina.BallerinaModel.Comment;
import ballerina.BallerinaModel.Expression.FunctionCall;
import ballerina.BallerinaModel.Expression.StringConstant;
import ballerina.BallerinaModel.Expression.VariableReference;
import ballerina.BallerinaModel.Statement.CallStatement;
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
                        case TibcoModel.Scope.Flow.Activity.UnhandledActivity unhandledActivity ->
                                convertUnhandledActivity(cx, unhandledActivity);
                };
                return new BallerinaModel.Function(cx.functionName(), cx.parameters(), cx.returnType(), body);
        }

        private static List<BallerinaModel.Statement> convertUnhandledActivity(
                ActivityContext cx,
                TibcoModel.Scope.Flow.Activity.UnhandledActivity unhandledActivity) {
            VariableReference inputXml = cx.getInputAsXml();
            return List.of(new Comment(unhandledActivity.reason()), new Return<>(inputXml));
        }

        private static List<BallerinaModel.Statement> convertReply(ActivityContext cx,
                        TibcoModel.Scope.Flow.Activity.Reply reply) {
                List<BallerinaModel.Statement> body = new ArrayList<>();
            VariableReference input = cx.getInputAsXml();
            VariableReference result;
                if (reply.inputBindings().isEmpty()) {
                        result = input;
                } else {
                        List<VarDeclStatment> inputBindings = convertInputBindings(cx, input, reply.inputBindings());
                        body.addAll(inputBindings);
                    result = new VariableReference(inputBindings.getLast().varName());
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
            VariableReference inputXml = cx.getInputAsXml();
                body.add(new Return<>(Optional.of(inputXml)));
                return body;
        }

        private static List<BallerinaModel.Statement> convertActivityExtension(
                        ActivityContext cx,
                        TibcoModel.Scope.Flow.Activity.ActivityExtension activityExtension) {
                var inputBindings = convertInputBindings(cx, cx.getInputAsXml(), activityExtension.inputBindings());
                List<BallerinaModel.Statement> body = new ArrayList<>(inputBindings);
            VariableReference result = new VariableReference(
                                inputBindings.getLast().varName());

                TibcoModel.Scope.Flow.Activity.ActivityExtension.Config config = activityExtension.config();
                List<BallerinaModel.Statement> rest = switch (config) {
                        case TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.End ignored ->
                                List.of(new Return<>(result));
                        case TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.HTTPSend httpSend ->
                                createHttpSend(cx, result, httpSend, activityExtension.outputVariable());
                        case TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.JsonOperation jsonOperation ->
                                createJsonOperation(cx, result, jsonOperation, activityExtension.outputVariable());
                    case TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.SQL sql ->
                            createSQLOperation(cx, result, sql);
                    case TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.SendHTTPResponse ignored ->
                            List.of(new Return<>(result));
                    case TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.FileWrite fileWrite ->
                            createFileWriteOperation(cx, result, fileWrite);
                };
                body.addAll(rest);
                return body;
        }

    private static List<BallerinaModel.Statement> createFileWriteOperation(
            ActivityContext cx,
            BallerinaModel.Expression.VariableReference result,
            TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.FileWrite fileWrite) {
        List<BallerinaModel.Statement> body = new ArrayList<>();
        BallerinaModel.TypeDesc dataType = cx.getFileWriteConfigType();
        VarDeclStatment dataDecl = new VarDeclStatment(dataType, cx.getAnnonVarName(),
                new FunctionCall(cx.getConvertToTypeFunction(dataType), List.of(result)));
        body.add(dataDecl);

        VarDeclStatment fileNameDecl = new VarDeclStatment(STRING, cx.getAnnonVarName(),
                new BallerinaModel.Expression.FieldAccess(
                        new BallerinaModel.Expression.VariableReference(dataDecl.varName()), "fileName"));
        body.add(fileNameDecl);

        VarDeclStatment textContentDecl = new VarDeclStatment(STRING,
                cx.getAnnonVarName(),
                new BallerinaModel.Expression.FieldAccess(
                        new BallerinaModel.Expression.VariableReference(dataDecl.varName()),
                        "textContent"));
        body.add(textContentDecl);

        CallStatement callStatement = new CallStatement(
                new BallerinaModel.Expression.CheckPanic(new FunctionCall(
                        cx.getFileWriteFunction(),
                        List.of(new BallerinaModel.Expression.VariableReference(
                                        fileNameDecl.varName()),
                                new BallerinaModel.Expression.VariableReference(
                                        textContentDecl.varName())))));
        body.add(callStatement);

        body.add(new Return<>(result));
        return body;
    }

    private static List<BallerinaModel.Statement> createSQLOperation(
            ActivityContext cx,
            VariableReference inputVar,
            TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.SQL sql) {
        List<BallerinaModel.Statement> body = new ArrayList<>();
        BallerinaModel.TypeDesc dataType = ConversionUtils.createQueryInputType(cx, sql);
        VarDeclStatment dataDecl = new VarDeclStatment(dataType, "data",
                new FunctionCall(cx.getConvertToTypeFunction(dataType),
                        List.of(inputVar)));
        body.add(dataDecl);
        VariableReference paramData = new VariableReference(dataDecl.varName());

        VarDeclStatment queryDecl = ConversionUtils.createQueryDecl(cx, paramData, sql);
        body.add(queryDecl);
        VariableReference query = new VariableReference(queryDecl.varName());

        VariableReference dbClient = cx.dbClient(sql.sharedResourcePropertyName());
        BallerinaModel.TypeDesc executionResultType = cx.processContext.getTypeByName("sql:ExecutionResult");
        VarDeclStatment result = new VarDeclStatment(executionResultType,
                cx.getAnnonVarName(),
                new BallerinaModel.Expression.CheckPanic(
                        new BallerinaModel.Action.RemoteMethodCallAction(
                                dbClient, "execute", List.of(query))));
        body.add(result);

        // TODO: handle things like select properly
        body.add(new Return<>(inputVar));
        return body;
    }

        private static List<BallerinaModel.Statement> createJsonOperation(
                        ActivityContext cx,
                        VariableReference inputVar,
                        TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.JsonOperation jsonOperation,
                        Optional<String> outputVarName) {
                // TODO: how to implement this
            return outputVarName.map(
                                s -> List.of(addToContext(cx, inputVar, s), new Return<>(inputVar)))
                                .orElseGet(() -> List.of(new Return<>(inputVar)));
        }

        private static List<BallerinaModel.Statement> createHttpSend(
                        ActivityContext cx,
                        VariableReference configVar,
                        TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.HTTPSend httpSend,
                        Optional<String> outputVarName) {
                String parseFn = cx.getParseHttpConfigFunction();
                List<BallerinaModel.Statement> body = new ArrayList<>();
                BallerinaModel.TypeDesc.TypeReference httpConfigType = cx.getHttpConfigType();
            FunctionCall parseCall = new FunctionCall(parseFn,
                                new String[] { configVar.varName() });
                VarDeclStatment configVarDecl = new VarDeclStatment(httpConfigType, cx.getAnnonVarName(), parseCall);
                body.add(configVarDecl);
            VariableReference configVarRef = new VariableReference(
                                configVarDecl.varName());

            VariableReference configurableHost = cx.addConfigurableVariable(STRING,
                                "host");
                VarDeclStatment client = createHTTPClientWithBasePath(cx, configurableHost);
                body.add(client);

            FunctionCall pathGetFunctionCall = new FunctionCall(
                                Intrinsics.CREATE_HTTP_REQUEST_PATH_FROM_CONFIG.name,
                                List.of(configVarRef));
                VarDeclStatment requestURI = new VarDeclStatment(STRING, cx.getAnnonVarName(), pathGetFunctionCall);
                body.add(requestURI);

                // TODO: handle non-post
                BallerinaModel.Action.RemoteMethodCallAction call = new BallerinaModel.Action.RemoteMethodCallAction(
                        new VariableReference(
                                                client.varName()),
                                "/" + requestURI.varName() + ".post",
                                List.of(new BallerinaModel.Expression.FieldAccess(configVarRef, "PostData"),
                                                new BallerinaModel.Expression.FieldAccess(configVarRef, "Headers")));
                BallerinaModel.Expression.CheckPanic checkPanic = new BallerinaModel.Expression.CheckPanic(call);
                VarDeclStatment responseDecl = new VarDeclStatment(JSON, cx.getAnnonVarName(), checkPanic);
                body.add(responseDecl);

                String jsonToXmlFunction = cx.processContext.getJsonToXMLFunction();
            FunctionCall jsonToXmlFunctionCall = new FunctionCall(
                                jsonToXmlFunction, new String[] { responseDecl.varName() });
                VarDeclStatment resultDecl = new VarDeclStatment(XML, cx.getAnnonVarName(), jsonToXmlFunctionCall);
                body.add(resultDecl);
            VariableReference result = new VariableReference(
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
                        new VariableReference(clientDecl.varName()),
                                cx.getInputAsXml());
                body.add(bindingCallResult);
                String jsonToXMLFunction = cx.processContext.getJsonToXMLFunction();
            FunctionCall jsonToXMLFunctionCall = new FunctionCall(
                                jsonToXMLFunction,
                                new String[] { bindingCallResult.varName() });
                VarDeclStatment resultDecl = new VarDeclStatment(XML, cx.getAnnonVarName(), jsonToXMLFunctionCall);
                body.add(resultDecl);
            VariableReference result = new VariableReference(
                                resultDecl.varName());

                body.add(addToContext(cx, result, invoke.outputVariable()));
                body.add(new Return<>(result));
                return body;
        }

        private static VarDeclStatment createBindingCall(ActivityContext cx,
                        TibcoModel.PartnerLink.Binding binding,
                                                         VariableReference client,
                                                         VariableReference value) {
                String path = binding.path().path();
                assert binding.operation().method() == TibcoModel.PartnerLink.Binding.Operation.Method.POST;
                BallerinaModel.Action.RemoteMethodCallAction call = new BallerinaModel.Action.RemoteMethodCallAction(
                                client, "post",
                        List.of(new StringConstant(path), value));
                BallerinaModel.Expression.CheckPanic checkPanic = new BallerinaModel.Expression.CheckPanic(call);
                return new VarDeclStatment(JSON, cx.getAnnonVarName(), checkPanic);
        }

        private static VarDeclStatment createClientForBinding(ActivityContext cx,
                        TibcoModel.PartnerLink.Binding binding) {
                String basePath = binding.path().basePath();
            return createHTTPClientWithBasePath(cx, new StringConstant(basePath));
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
            VariableReference result = cx.getInputAsXml();
                if (!extActivity.inputBindings().isEmpty()) {
                        List<VarDeclStatment> inputBindings = convertInputBindings(cx, result,
                                        extActivity.inputBindings());
                        body.addAll(inputBindings);
                    result = new VariableReference(inputBindings.getLast().varName());
                }
                var startFunction = cx.getProcessStartFunctionName(extActivity.callProcess().subprocessName());

                String convertToTypeFunction = cx.processContext.getConvertToTypeFunction(startFunction.inputType());
            FunctionCall convertToTypeFunctionCall = new FunctionCall(
                                convertToTypeFunction, new String[] { result.varName() });

            FunctionCall startFunctionCall = new FunctionCall(
                                startFunction.name(), List.of(convertToTypeFunctionCall));
            FunctionCall convertToXmlCall = new FunctionCall(
                                cx.processContext.getToXmlFunction(),
                                List.of(startFunctionCall));
                VarDeclStatment resultDecl = new VarDeclStatment(XML, cx.getAnnonVarName(), convertToXmlCall);
                body.add(resultDecl);
            VariableReference resultRef = new VariableReference(
                                resultDecl.varName());
                body.add(addToContext(cx, resultRef, extActivity.outputVariable()));
                body.add(new Return<>(resultRef));
                return body;
        }

        private static List<VarDeclStatment> convertInputBindings(ActivityContext cx,
                                                                  VariableReference input,
                        Collection<TibcoModel.Scope.Flow.Activity.InputBinding> inputBindings) {
                List<VarDeclStatment> varDelStatements = new ArrayList<>();
            VariableReference last = input;
                for (TibcoModel.Scope.Flow.Activity.InputBinding transform : inputBindings) {
                        TibcoModel.Scope.Flow.Activity.Expression.XSLT xslt = transform.xslt();

                        VarDeclStatment varDecl = xsltTransform(cx, last, xslt);
                        varDelStatements.add(varDecl);
                    last = new VariableReference(varDecl.varName());
                }
                return varDelStatements;
        }

        private static VarDeclStatment xsltTransform(ActivityContext cx,
                                                     VariableReference inputVariable,
                        TibcoModel.Scope.Flow.Activity.Expression.XSLT xslt) {
            cx.addLibraryImport(Library.XSLT);
            String stylesheetTransformer = cx.getTransformXSLTFn();
            FunctionCall transformerCall = new FunctionCall(
                    stylesheetTransformer,
                    List.of(new BallerinaModel.Expression.XMLTemplate(xslt.expression())));
            FunctionCall callExpr = new FunctionCall(
                                "xslt:transform",
                                new BallerinaModel.Expression[] { inputVariable,
                                        transformerCall,
                                                cx.contextVarRef() });
                BallerinaModel.Expression.CheckPanic checkPanic = new BallerinaModel.Expression.CheckPanic(callExpr);
                return new VarDeclStatment(XML, cx.getAnnonVarName(), checkPanic);
        }

    private static BallerinaModel.Statement addToContext(ActivityContext cx,
                                                         BallerinaModel.Expression.VariableReference value,
                        String key) {
                assert !key.isEmpty();
        String addToContextFn = cx.getAddToContextFn();
        return new CallStatement(
                new FunctionCall(addToContextFn,
                        List.of(cx.contextVarRef(), new StringConstant(key), value)));
    }
}
