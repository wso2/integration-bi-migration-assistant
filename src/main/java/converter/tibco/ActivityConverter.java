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
import ballerina.BallerinaModel.Expression.Check;
import ballerina.BallerinaModel.Expression.FunctionCall;
import ballerina.BallerinaModel.Expression.StringConstant;
import ballerina.BallerinaModel.Expression.Trap;
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

import static ballerina.BallerinaModel.TypeDesc.BuiltinType.ERROR;
import static ballerina.BallerinaModel.TypeDesc.BuiltinType.JSON;
import static ballerina.BallerinaModel.TypeDesc.BuiltinType.NIL;
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
                        case TibcoModel.Scope.Flow.Activity.CatchAll catchAll -> convertCatchAll(cx, catchAll);
                        case TibcoModel.Scope.Flow.Activity.ReceiveEvent receiveEvent ->
                                convertReceiveEvent(cx, receiveEvent);
                        case TibcoModel.Scope.Flow.Activity.Reply reply -> convertReply(cx, reply);
                        case TibcoModel.Scope.Flow.Activity.UnhandledActivity unhandledActivity ->
                                convertUnhandledActivity(cx, unhandledActivity);
                        case TibcoModel.Scope.Flow.Activity.Throw throwActivity ->
                                convertThrowActivity(cx, throwActivity);
                };
                return new BallerinaModel.Function(cx.functionName(), cx.parameters(),
                        Optional.of(ActivityContext.returnType().toString()), body);
        }

        private static List<BallerinaModel.Statement> convertThrowActivity(
                ActivityContext cx,
                TibcoModel.Scope.Flow.Activity.Throw throwActivity) {
                List<BallerinaModel.Statement> body = new ArrayList<>();
                BallerinaModel.Expression.VariableReference input = cx.getInputAsXml();
                BallerinaModel.Expression.VariableReference result;
                if (throwActivity.inputBindings().isEmpty()) {
                        result = input;
                } else {
                        List<VarDeclStatment> inputBindings = convertInputBindings(cx, input,
                                throwActivity.inputBindings());
                        body.addAll(inputBindings);
                        result = inputBindings.getLast().ref();
                }
                // TODO: set the body correctly
                VarDeclStatment errorValue = new VarDeclStatment(ERROR,
                        cx.getAnnonVarName(),
                        new BallerinaModel.BallerinaExpression("error(\"TODO: create error value\")"));
                body.add(errorValue);
                body.add(new BallerinaModel.BallerinaStatement("panic " + errorValue.varName() + ";"));

                return body;
        }

        private static List<BallerinaModel.Statement> convertCatchAll(ActivityContext cx,
                                                                      TibcoModel.Scope.Flow.Activity.CatchAll catchAll) {
                return convertEmptyAction(cx);
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
                ActivityExtensionConfigConversion conversion = switch (config) {
                        case TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.End ignored ->
                                new ActivityExtensionConfigConversion(List.of(), result);
                        case TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.HTTPSend httpSend ->
                                createHttpSend(cx, result, httpSend);
                        case TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.JsonOperation ignored ->
                                new ActivityExtensionConfigConversion(List.of(), result);
                        case TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.SQL sql ->
                                createSQLOperation(cx, result, sql);
                        case TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.SendHTTPResponse ignored ->
                                new ActivityExtensionConfigConversion(List.of(), result);
                        case TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.FileWrite fileWrite ->
                                createFileWriteOperation(cx, result, fileWrite);
                        case TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.Log log ->
                                createLogOperation(cx, result, log);
                        case TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.RenderXML ignored ->
                                new ActivityExtensionConfigConversion(List.of(), result);
                };
                body.addAll(conversion.body());
                activityExtension.outputVariable()
                        .ifPresent(outputVar -> body.add(addToContext(cx, conversion.result(), outputVar)));
                body.add(new Return<>(conversion.result()));
                return body;
        }

        private static ActivityExtensionConfigConversion createLogOperation(
                        ActivityContext cx,
                        BallerinaModel.Expression.VariableReference result,
                        TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.Log log) {
                List<BallerinaModel.Statement> body = new ArrayList<>();

                BallerinaModel.TypeDesc dataType = cx.getLogInputType();
                VarDeclStatment dataDecl = new VarDeclStatment(dataType,
                                cx.getAnnonVarName(),
                        new FunctionCall(cx.getConvertToTypeFunction(dataType),
                                                List.of(result)));
                body.add(dataDecl);

                CallStatement callStatement = new CallStatement(
                        new FunctionCall(cx.getLogFunction(), List.of(
                                                new BallerinaModel.Expression.VariableReference(dataDecl.varName()))));
                body.add(callStatement);

                return new ActivityExtensionConfigConversion(body, result);
        }

        private static ActivityExtensionConfigConversion createFileWriteOperation(
                        ActivityContext cx,
                        BallerinaModel.Expression.VariableReference result,
                        TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.FileWrite fileWrite) {
                List<BallerinaModel.Statement> body = new ArrayList<>();
                BallerinaModel.TypeDesc dataType = cx.getFileWriteConfigType();
                VarDeclStatment dataDecl = new VarDeclStatment(dataType,
                        cx.getAnnonVarName(),
                        new BallerinaModel.Expression.FunctionCall(cx.getConvertToTypeFunction(dataType),
                                List.of(result)));
                body.add(dataDecl);

                VarDeclStatment fileNameDecl = new VarDeclStatment(STRING, cx.getAnnonVarName(),
                                new BallerinaModel.Expression.TypeCast(STRING,
                                                new BallerinaModel.Expression.FieldAccess(
                                                                new BallerinaModel.Expression.VariableReference(
                                                                                dataDecl.varName()),
                                                                "fileName")));
                body.add(fileNameDecl);

                VarDeclStatment textContentDecl = new VarDeclStatment(STRING,
                                cx.getAnnonVarName(),
                                new BallerinaModel.Expression.FieldAccess(
                                                new BallerinaModel.Expression.VariableReference(dataDecl.varName()),
                                                "textContent"));
                body.add(textContentDecl);

                CallStatement callStatement = new CallStatement(
                        new BallerinaModel.Expression.CheckPanic(new BallerinaModel.Expression.FunctionCall(
                                                cx.getFileWriteFunction(),
                                                List.of(new BallerinaModel.Expression.VariableReference(
                                                                fileNameDecl.varName()),
                                                                new BallerinaModel.Expression.VariableReference(
                                                                                textContentDecl.varName())))));
                body.add(callStatement);
                return new ActivityExtensionConfigConversion(body, result);
        }

        private static ActivityExtensionConfigConversion createSQLOperation(
                        ActivityContext cx,
                        BallerinaModel.Expression.VariableReference inputVar,
                        TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.SQL sql) {
                List<BallerinaModel.Statement> body = new ArrayList<>();
                BallerinaModel.TypeDesc dataType = ConversionUtils.createQueryInputType(cx, sql);
                VarDeclStatment dataDecl = new VarDeclStatment(dataType, "data",
                        new BallerinaModel.Expression.FunctionCall(cx.getConvertToTypeFunction(dataType),
                                                List.of(inputVar)));
                body.add(dataDecl);
                BallerinaModel.Expression.VariableReference paramData = new BallerinaModel.Expression.VariableReference(
                        dataDecl.varName());

                VarDeclStatment queryDecl = ConversionUtils.createQueryDecl(cx, paramData, sql);
                body.add(queryDecl);
                VariableReference query = new VariableReference(queryDecl.varName());

                BallerinaModel.Expression.VariableReference dbClient = cx.client(sql.sharedResourcePropertyName());
                if (sql.query().toUpperCase().startsWith("SELECT")) {
                        return finishSelectQuery(cx, sql, dbClient, query, body);
                }
                BallerinaModel.TypeDesc executionResultType = cx.processContext.getTypeByName("sql:ExecutionResult");
                VarDeclStatment result = new VarDeclStatment(executionResultType,
                                cx.getAnnonVarName(),
                        new BallerinaModel.Expression.Check(
                                                new BallerinaModel.Action.RemoteMethodCallAction(
                                                                dbClient, "execute", List.of(query))));
                body.add(result);
                return new ActivityExtensionConfigConversion(body, result.ref());
        }

        private static @NotNull ActivityExtensionConfigConversion finishSelectQuery(
                ActivityContext cx,
                TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.SQL sql,
                BallerinaModel.Expression.VariableReference dbClient,
                BallerinaModel.Expression.VariableReference query,
                List<BallerinaModel.Statement> body) {
                BallerinaModel.TypeDesc columnTy = ConversionUtils.createQueryResultType(cx, sql);
                BallerinaModel.TypeDesc.StreamTypeDesc streamTypeDesc = new BallerinaModel.TypeDesc.StreamTypeDesc(
                        columnTy,
                        BallerinaModel.TypeDesc.UnionTypeDesc.of(
                                cx.processContext.getTypeByName("sql:ExecutionResult"),
                                NIL));
                VarDeclStatment stream = new VarDeclStatment(streamTypeDesc,
                        cx.getAnnonVarName(),
                        new BallerinaModel.Action.RemoteMethodCallAction(dbClient, "query", List.of(query)));
                body.add(stream);

                VarDeclStatment accum = new VarDeclStatment(XML, cx.getAnnonVarName(),
                        new BallerinaModel.Expression.XMLTemplate(""));
                body.add(accum);

                BallerinaModel.BallerinaStatement foreachStatement = new BallerinaModel.BallerinaStatement("""
                        check from var each in %s do {
                            %s = %s + each;
                        };
                        """.formatted(stream.ref(), accum.ref(), accum.ref()));
                body.add(foreachStatement);

                VarDeclStatment result = new VarDeclStatment(XML, cx.getAnnonVarName(),
                        new BallerinaModel.Expression.XMLTemplate("<root>${%s}</root>".formatted(accum.ref())));
                body.add(result);
                return new ActivityExtensionConfigConversion(body, result.ref());
        }

        private static ActivityExtensionConfigConversion createHttpSend(
                        ActivityContext cx,
                        BallerinaModel.Expression.VariableReference configVar,
                        TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.HTTPSend httpSend) {
                String parseFn = cx.getParseHttpConfigFunction();
                List<BallerinaModel.Statement> body = new ArrayList<>();
                BallerinaModel.TypeDesc.TypeReference httpConfigType = cx.getHttpConfigType();
                BallerinaModel.Expression.FunctionCall parseCall = new BallerinaModel.Expression.FunctionCall(parseFn,
                                new String[] { configVar.varName() });
                VarDeclStatment configVarDecl = new VarDeclStatment(httpConfigType,
                        cx.getAnnonVarName(), parseCall);
                body.add(configVarDecl);
                BallerinaModel.Expression.VariableReference configVarRef =
                        new BallerinaModel.Expression.VariableReference(
                                configVarDecl.varName());
                BallerinaModel.Expression.VariableReference client = cx.client(httpSend.httpClientResource());

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
                VarDeclStatment responseDecl = new VarDeclStatment(JSON, cx.getAnnonVarName(),
                        new Check(call));
                body.add(responseDecl);

                String jsonToXmlFunction = cx.processContext.getJsonToXMLFunction();
                BallerinaModel.Expression.FunctionCall jsonToXmlFunctionCall =
                        new BallerinaModel.Expression.FunctionCall(
                                jsonToXmlFunction, new String[] { responseDecl.varName() });
                VarDeclStatment resultDecl = new VarDeclStatment(XML,
                        cx.getAnnonVarName(),
                        new BallerinaModel.Expression.Check(jsonToXmlFunctionCall));
                body.add(resultDecl);
                BallerinaModel.Expression.VariableReference result = new BallerinaModel.Expression.VariableReference(
                                resultDecl.varName());

                return new ActivityExtensionConfigConversion(body, result);
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
                VarDeclStatment resultDecl = new VarDeclStatment(XML, cx.getAnnonVarName(),
                        new Check(jsonToXMLFunctionCall));
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
                return new VarDeclStatment(JSON, cx.getAnnonVarName(),
                        new Check(call));
        }

        private static VarDeclStatment createClientForBinding(ActivityContext cx,
                        TibcoModel.PartnerLink.Binding binding) {
                String basePath = binding.path().basePath();
                return createHTTPClientWithBasePath(cx, new StringConstant(basePath));
        }

        private static VarDeclStatment createHTTPClientWithBasePath(
                ActivityContext cx,
                        BallerinaModel.Expression basePath) {
                BallerinaModel.Expression.NewExpression newExpression = new BallerinaModel.Expression.NewExpression(
                                List.of(basePath));
                return new VarDeclStatment(cx.processContext.getTypeByName("http:Client"),
                        cx.getAnnonVarName(),
                        new Check(newExpression));
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

                VarDeclStatment resultDecl = new VarDeclStatment(XML, cx.getAnnonVarName(), new Check(
                        new FunctionCall(cx.processContext.getToXmlFunction(), List.of(new Check(
                                new Trap(new FunctionCall(startFunction.name(),
                                        List.of(convertToTypeFunctionCall))))))));
                body.add(resultDecl);
                BallerinaModel.Expression.VariableReference resultRef = resultDecl.ref();
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

        private static VarDeclStatment xsltTransform(
                ActivityContext cx,
                BallerinaModel.Expression.VariableReference inputVariable,
                        TibcoModel.Scope.Flow.Activity.Expression.XSLT xslt) {
                cx.addLibraryImport(Library.XSLT);
                String stylesheetTransformer = cx.getTransformXSLTFn();
                BallerinaModel.Expression.FunctionCall transformerCall = new BallerinaModel.Expression.FunctionCall(
                                stylesheetTransformer,
                        List.of(new BallerinaModel.Expression.XMLTemplate(
                                replaceVariableReferences(cx, xslt.expression()))));
                BallerinaModel.Expression.FunctionCall callExpr = new BallerinaModel.Expression.FunctionCall(
                                "xslt:transform",
                                new BallerinaModel.Expression[] { inputVariable,
                                        transformerCall, cx.contextVarRef()});
                return new VarDeclStatment(XML, cx.getAnnonVarName(),
                        new BallerinaModel.Expression.Check(callExpr));
        }

        private static String replaceVariableReferences(ActivityContext cx, String expression) {
                java.util.regex.Pattern pattern = java.util.regex.Pattern
                        .compile("bw:getModuleProperty\\('([^']+)'\\)");
                java.util.regex.Matcher matcher = pattern.matcher(expression);
                StringBuilder result = new StringBuilder();

                while (matcher.find()) {
                        String propertyName = matcher.group(1);
                        String configVarName = cx.getConfigVarName(propertyName);
                        matcher.appendReplacement(result, "\\$\\{" + configVarName + "\\}");
                }
                matcher.appendTail(result);
                return result.toString();
        }

        private static BallerinaModel.Statement addToContext(ActivityContext cx,
                        BallerinaModel.Expression.VariableReference value,
                        String key) {
                assert !key.isEmpty();
                String addToContextFn = cx.getAddToContextFn();
                return new CallStatement(
                        new BallerinaModel.Expression.FunctionCall(addToContextFn, List.of(cx.contextVarRef(),
                                new BallerinaModel.Expression.StringConstant(key), value)));
        }

        private record ActivityExtensionConfigConversion(List<BallerinaModel.Statement> body,
                                                         BallerinaModel.Expression.VariableReference result) {

        }
}
