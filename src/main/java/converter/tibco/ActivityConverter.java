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
import ballerina.BallerinaModel.Expression.BallerinaExpression;
import ballerina.BallerinaModel.Expression.Check;
import ballerina.BallerinaModel.Expression.FunctionCall;
import ballerina.BallerinaModel.Expression.Trap;
import ballerina.BallerinaModel.Expression.VariableReference;
import ballerina.BallerinaModel.Statement.CallStatement;
import ballerina.BallerinaModel.Statement.Return;
import ballerina.BallerinaModel.Statement.VarDeclStatment;
import converter.tibco.analyzer.AnalysisResult;
import converter.tibco.xslt.IgnoreRootWrapper;
import converter.tibco.xslt.ReplaceDotAccessWithXPath;
import converter.tibco.xslt.ReplaceVariableReference;
import converter.tibco.xslt.TransformPipeline;
import org.jetbrains.annotations.NotNull;
import tibco.TibcoModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ballerina.BallerinaModel.TypeDesc.BuiltinType.ERROR;
import static ballerina.BallerinaModel.TypeDesc.BuiltinType.JSON;
import static ballerina.BallerinaModel.TypeDesc.BuiltinType.NIL;
import static ballerina.BallerinaModel.TypeDesc.BuiltinType.STRING;
import static ballerina.BallerinaModel.TypeDesc.BuiltinType.XML;

class ActivityConverter {

        private static final TransformPipeline xsltTransformer = createXsltTransformer();

        private ActivityConverter() {
        }

        private static TransformPipeline createXsltTransformer() {
                TransformPipeline xsltTransformer = new TransformPipeline();
                xsltTransformer.append(new ReplaceVariableReference());
                xsltTransformer.append(new ReplaceDotAccessWithXPath());
                xsltTransformer.append(new IgnoreRootWrapper());
                return xsltTransformer;
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
                return new BallerinaModel.Function(cx.functionName(), cx.parameters(), ActivityContext.returnType(),
                        body);
        }

        private static List<BallerinaModel.Statement> convertThrowActivity(
                        ActivityContext cx,
                        TibcoModel.Scope.Flow.Activity.Throw throwActivity) {
                List<BallerinaModel.Statement> body = new ArrayList<>();
                BallerinaModel.Expression result;
                VarDeclStatment inputDecl = new VarDeclStatment(XML, cx.getAnnonVarName(),
                                defaultEmptyXml());
                body.add(inputDecl);
                BallerinaModel.Expression.VariableReference input = inputDecl.ref();
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
                        new BallerinaExpression("error(\"TODO: create error value\")"));
                body.add(errorValue);
                body.add(new BallerinaModel.Statement.BallerinaStatement("panic " + errorValue.varName() + ";"));

                return body;
        }

        private static List<BallerinaModel.Statement> convertCatchAll(ActivityContext cx,
                        TibcoModel.Scope.Flow.Activity.CatchAll catchAll) {
                return convertEmptyAction(cx);
        }

        private static List<BallerinaModel.Statement> convertUnhandledActivity(
                        ActivityContext cx,
                        TibcoModel.Scope.Flow.Activity.UnhandledActivity unhandledActivity) {
                BallerinaModel.Expression inputXml = defaultEmptyXml();
                return List.of(new BallerinaModel.Statement.Comment(unhandledActivity.reason()),
                                new Return<>(inputXml));
        }

        private static List<BallerinaModel.Statement> convertReply(ActivityContext cx,
                        TibcoModel.Scope.Flow.Activity.Reply reply) {
                List<BallerinaModel.Statement> body = new ArrayList<>();
                VarDeclStatment inputDecl = new VarDeclStatment(
                                XML, cx.getAnnonVarName(), defaultEmptyXml());
                body.add(inputDecl);
                VariableReference input = inputDecl.ref();
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
                BallerinaModel.Expression inputXml = defaultEmptyXml();
                body.add(new Return<>(Optional.of(inputXml)));
                return body;
        }

        private static List<BallerinaModel.Statement> convertActivityExtension(
                        ActivityContext cx,
                        TibcoModel.Scope.Flow.Activity.ActivityExtension activityExtension) {
                List<BallerinaModel.Statement> body = new ArrayList<>();
                VarDeclStatment inputDecl = new VarDeclStatment(XML, cx.getAnnonVarName(),
                                defaultEmptyXml());
                body.add(inputDecl);
                List<VarDeclStatment> inputBindings = convertInputBindings(cx, inputDecl.ref(),
                                activityExtension.inputBindings());
                body.addAll(inputBindings);
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
                                defaultEmptyXml());
                body.add(accum);

                BallerinaModel.Statement.BallerinaStatement
                        foreachStatement = new BallerinaModel.Statement.BallerinaStatement("""
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
                List<BallerinaModel.Statement> body = new ArrayList<>();
                BallerinaModel.Expression.VariableReference client = cx.client(httpSend.httpClientResource());
                VarDeclStatment method = new VarDeclStatment(STRING, cx.getAnnonVarName(),
                        new BallerinaExpression("(%s/**/<Method>[0]).data()".formatted(configVar.varName())));
                body.add(method);

                VarDeclStatment requestURI = new VarDeclStatment(STRING, cx.getAnnonVarName(),
                        new BallerinaExpression("(%s/**/<RequestURI>[0]).data()".formatted(configVar.varName())));
                body.add(requestURI);
                // TODO: how to get map<json> from xml?
//                VarDeclStatment headers = new VarDeclStatment(JSON, cx.getAnnonVarName(),
//                        new BallerinaExpression("%s/**/<Headers>[0]".formatted(configVar.varName())));
//                body.add(headers);

                VarDeclStatment result = new VarDeclStatment(JSON, cx.getAnnonVarName(), new BallerinaExpression("()"));
                body.add(result);

                body.add(new BallerinaModel.Statement.BallerinaStatement("""
                        match %1$s {
                            "GET" => {
                                %2$s = check %3$s->get(%4$s);
                            }
                            "POST" => {
                                json postData = (var1/**/<PostData>[0]).data();
                                %2$s = check %3$s->post(%4$s, postData);
                            }
                            _ => {
                                panic error("Unsupported method: " + %1$s);
                            }
                        }
                        """.formatted(method.varName(), result.varName(), client.varName(), requestURI.varName())));

                VarDeclStatment resultDecl = convertJSONResponseToXML(cx, result);
                body.add(resultDecl);

                return new ActivityExtensionConfigConversion(body, new BallerinaModel.Expression.VariableReference(
                        resultDecl.varName()));
        }

        private static @NotNull VarDeclStatment convertJSONResponseToXML(ActivityContext cx,
                        VarDeclStatment responseDecl) {
                String jsonToXmlFunction = cx.processContext.getJsonToXMLFunction();
                FunctionCall jsonToXmlFunctionCall = new FunctionCall(jsonToXmlFunction,
                                new String[] { responseDecl.varName() });
                return new VarDeclStatment(XML, cx.getAnnonVarName(), new Check(jsonToXmlFunctionCall));
        }

        private static List<BallerinaModel.Statement> convertReceiveEvent(
                        ActivityContext cx,
                        TibcoModel.Scope.Flow.Activity.ReceiveEvent receiveEvent) {
                return List.of(addToContext(cx, getFromContext(cx, ConversionUtils.Constants.CONTEXT_INPUT_NAME),
                                receiveEvent.variable()),
                                new Return<>(getFromContext(cx, ConversionUtils.Constants.CONTEXT_INPUT_NAME)));
        }

        private static List<BallerinaModel.Statement> convertInvoke(ActivityContext cx,
                        TibcoModel.Scope.Flow.Activity.Invoke invoke) {

                List<BallerinaModel.Statement> body = new ArrayList<>();
                VarDeclStatment inputDecl = new VarDeclStatment(XML, cx.getAnnonVarName(),
                                defaultEmptyXml());
                body.add(inputDecl);
                List<VarDeclStatment> inputBindings =

                                convertInputBindings(cx, inputDecl.ref(), invoke.inputBindings());
                body.addAll(inputBindings);
                BallerinaModel.Expression.VariableReference input = inputBindings.isEmpty() ? inputDecl.ref()
                                : inputBindings.getLast().ref();

                AnalysisResult ar = cx.processContext.analysisResult;
                TibcoModel.PartnerLink.Binding binding = ar.getBinding(invoke.partnerLink());
                String path = binding.path().basePath();
                BallerinaModel.Expression.VariableReference client = cx.getHttpClient(path);

                VarDeclStatment callResult = new VarDeclStatment(JSON,
                                cx.getAnnonVarName(),
                                new BallerinaModel.Expression.Check(
                                                new BallerinaModel.Action.RemoteMethodCallAction(client,
                                                                binding.operation().method().method,
                                                                List.of(new BallerinaModel.Expression.StringConstant(
                                                                                binding.path().path()), input))));
                body.add(callResult);

                BallerinaModel.Expression.FunctionCall jsonToXMLFunctionCall =
                        new BallerinaModel.Expression.FunctionCall(
                                cx.processContext.getJsonToXMLFunction(), List.of(callResult.ref()));
                VarDeclStatment resultDecl = new VarDeclStatment(XML, cx.getAnnonVarName(),
                        new BallerinaModel.Expression.Check(jsonToXMLFunctionCall));
                body.add(resultDecl);
                BallerinaModel.Expression.VariableReference result = new BallerinaModel.Expression.VariableReference(
                                resultDecl.varName());

                body.add(addToContext(cx, result, invoke.outputVariable()));
                body.add(new Return<>(result));
                return body;
        }

        private static List<BallerinaModel.Statement> convertExtActivity(
                        ActivityContext cx,
                        TibcoModel.Scope.Flow.Activity.ExtActivity extActivity) {
                List<BallerinaModel.Statement> body = new ArrayList<>();
                VarDeclStatment inputDecl = new VarDeclStatment(XML, cx.getAnnonVarName(),
                                defaultEmptyXml());
                body.add(inputDecl);
                VariableReference result = inputDecl.ref();
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

        private static List<VarDeclStatment> convertInputBindings(
                        ActivityContext cx,
                        BallerinaModel.Expression.VariableReference input,
                        Collection<TibcoModel.Scope.Flow.Activity.InputBinding> inputBindings) {
                List<VarDeclStatment> varDelStatements = new ArrayList<>();
                BallerinaModel.Expression.VariableReference last = input;
                for (TibcoModel.Scope.Flow.Activity.InputBinding transform : inputBindings) {
                        VarDeclStatment varDecl = switch (transform) {
                                case TibcoModel.Scope.Flow.Activity.InputBinding.CompleteBinding completeBinding ->
                                        new VarDeclStatment(XML, cx.getAnnonVarName(),
                                                        xsltTransform(cx, last, completeBinding.xslt()));
                                case TibcoModel.Scope.Flow.Activity.InputBinding.PartialBindings partialBindings ->
                                        convertPartialInputBinding(cx, partialBindings, last, varDelStatements);
                        };
                        varDelStatements.add(varDecl);
                        last = varDecl.ref();
                }
                return varDelStatements;
        }

        private static @NotNull VarDeclStatment convertPartialInputBinding(
                        ActivityContext cx,
                        TibcoModel.Scope.Flow.Activity.InputBinding.PartialBindings partialBindings,
                        VariableReference last,
                        List<VarDeclStatment> varDelStatements) {
                List<VarDeclStatment> statements = partialBindings.xslt().stream()
                                .map(each -> xsltTransform(cx, last, each))
                                .map(each -> new VarDeclStatment(XML, cx.getAnnonVarName(), each)).toList();
                varDelStatements.addAll(statements);
                String concat = statements.stream().map(VarDeclStatment::varName).collect(Collectors.joining(" + "));
                return new VarDeclStatment(XML, cx.getAnnonVarName(),
                                new BallerinaModel.Expression.XMLTemplate("<root>${" + concat + "}</root>"));
        }

        private static BallerinaModel.Expression xsltTransform(
                        ActivityContext cx,
                        BallerinaModel.Expression.VariableReference inputVariable,
                        TibcoModel.Scope.Flow.Activity.Expression.XSLT xslt) {
                cx.addLibraryImport(Library.XSLT);
                String styleSheet = xsltTransformer.apply(cx, xslt.expression());
                return new BallerinaModel.Expression.Check(new BallerinaModel.Expression.FunctionCall("xslt:transform",
                                new BallerinaModel.Expression[] { inputVariable,
                                                new BallerinaModel.Expression.XMLTemplate(styleSheet),
                                                cx.contextVarRef() }));
        }

        private static BallerinaModel.Expression.XMLTemplate defaultEmptyXml() {
                return new BallerinaModel.Expression.XMLTemplate("<root></root>");
        }

        private static BallerinaModel.Expression.MethodCall getFromContext(ActivityContext cx, String key) {
                assert !key.isEmpty();
                return new BallerinaModel.Expression.MethodCall(cx.contextVarRef(), "get", List.of(
                                new BallerinaModel.Expression.StringConstant(key)));
        }

        private static BallerinaModel.Statement addToContext(ActivityContext cx,
                        BallerinaModel.Expression value,
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
