/*
 *  Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
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
import common.BallerinaModel.Action.RemoteMethodCallAction;
import common.BallerinaModel.Expression.Check;
import common.BallerinaModel.Expression.CheckPanic;
import common.BallerinaModel.Expression.FieldAccess;
import common.BallerinaModel.Expression.FunctionCall;
import common.BallerinaModel.Expression.MethodCall;
import common.BallerinaModel.Expression.StringConstant;
import common.BallerinaModel.Expression.Trap;
import common.BallerinaModel.Expression.TypeCast;
import common.BallerinaModel.Expression.TypeCheckExpression;
import common.BallerinaModel.Expression.VariableReference;
import common.BallerinaModel.Expression.XMLTemplate;
import common.BallerinaModel.Statement;
import common.BallerinaModel.Statement.CallStatement;
import common.BallerinaModel.Statement.Comment;
import common.BallerinaModel.Statement.Return;
import common.BallerinaModel.Statement.VarDeclStatment;
import common.BallerinaModel.TypeDesc.StreamTypeDesc;
import common.BallerinaModel.TypeDesc.UnionTypeDesc;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Element;
import tibco.TibcoModel;
import tibco.TibcoModel.Process.ExplicitTransitionGroup.InlineActivity.AssignActivity;
import tibco.TibcoModel.Process.ExplicitTransitionGroup.InlineActivity.CallProcess;
import tibco.TibcoModel.Process.ExplicitTransitionGroup.InlineActivity.FileRead;
import tibco.TibcoModel.Process.ExplicitTransitionGroup.InlineActivity.FileWrite;
import tibco.TibcoModel.Process.ExplicitTransitionGroup.InlineActivity.HTTPResponse;
import tibco.TibcoModel.Process.ExplicitTransitionGroup.InlineActivity.REST;
import tibco.TibcoModel.Process.ExplicitTransitionGroup.InlineActivity.SOAPSendReceive;
import tibco.TibcoModel.Process.ExplicitTransitionGroup.InlineActivity.SOAPSendReply;
import tibco.TibcoModel.Process.ExplicitTransitionGroup.InlineActivity.UnhandledInlineActivity;
import tibco.TibcoModel.Process.ExplicitTransitionGroup.InlineActivity.WriteLog;
import tibco.TibcoModel.Process.ExplicitTransitionGroup.InlineActivity.XMLParseActivity;
import tibco.TibcoModel.Process.ExplicitTransitionGroup.InlineActivity.XMLRenderActivity;
import tibco.TibcoModel.Process.ExplicitTransitionGroup.NestedGroup.LoopGroup;
import tibco.TibcoModel.Scope.Flow.Activity;
import tibco.TibcoModel.Scope.Flow.Activity.ActivityExtension;
import tibco.TibcoModel.Scope.Flow.Activity.CatchAll;
import tibco.TibcoModel.Scope.Flow.Activity.Empty;
import tibco.TibcoModel.Scope.Flow.Activity.ExtActivity;
import tibco.TibcoModel.Scope.Flow.Activity.InputBinding;
import tibco.TibcoModel.Scope.Flow.Activity.Invoke;
import tibco.TibcoModel.Scope.Flow.Activity.Pick;
import tibco.TibcoModel.Scope.Flow.Activity.ReceiveEvent;
import tibco.TibcoModel.Scope.Flow.Activity.Reply;
import tibco.TibcoModel.Scope.Flow.Activity.Throw;
import tibco.TibcoModel.Scope.Flow.Activity.UnhandledActivity;
import tibco.analyzer.AnalysisResult;
import tibco.xslt.AddMissingParameters;
import tibco.xslt.IgnoreRootWrapper;
import tibco.xslt.ReplaceDotAccessWithXPath;
import tibco.xslt.ReplaceVariableReference;
import tibco.xslt.TransformPipeline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static common.BallerinaModel.TypeDesc.BuiltinType.ANYDATA;
import static common.BallerinaModel.TypeDesc.BuiltinType.ERROR;
import static common.BallerinaModel.TypeDesc.BuiltinType.INT;
import static common.BallerinaModel.TypeDesc.BuiltinType.JSON;
import static common.BallerinaModel.TypeDesc.BuiltinType.NIL;
import static common.BallerinaModel.TypeDesc.BuiltinType.STRING;
import static common.BallerinaModel.TypeDesc.BuiltinType.XML;
import static common.ConversionUtils.exprFrom;
import static common.ConversionUtils.stmtFrom;
import static common.ConversionUtils.typeFrom;

final class ActivityConverter {

        private static final TransformPipeline xsltTransformer = createXsltTransformer();

        private ActivityConverter() {
        }

        private static TransformPipeline createXsltTransformer() {
            TransformPipeline xsltTransformer = new TransformPipeline();
            xsltTransformer.append(new AddMissingParameters());
            xsltTransformer.append(new ReplaceVariableReference());
            xsltTransformer.append(new ReplaceDotAccessWithXPath());
            xsltTransformer.append(new IgnoreRootWrapper());
            return xsltTransformer;
        }

        public static BallerinaModel.Function convertActivity(ProcessContext cx, Activity activity) {
            return convertActivity(new ActivityContext(cx, activity), activity);
        }

        private static BallerinaModel.Function convertActivity(ActivityContext cx, Activity activity) {
            List<Statement> body;
            try {
                body = tryConvertActivityBody(cx, activity);
            } catch (Exception e) {
                List<Activity.Source> sources = activity instanceof Activity.ActivityWithSources activityWithSources
                        ? activityWithSources.sources()
                        : List.of();
                Collection<Activity.Target> targets =
                        activity instanceof Activity.ActivityWithTargets activityWithTargets
                                ? activityWithTargets.targets()
                                : List.of();
                UnhandledActivity unhandledActivity = new UnhandledActivity(
                        "Failed to codegen activity due to %s".formatted(e.getMessage()),
                        sources, targets, activity.element());
                body = tryConvertActivityBody(cx, unhandledActivity);
            }
            return new BallerinaModel.Function(cx.functionName(), cx.parameters(), ActivityContext.returnType(),
                    body);
        }

        private static @NotNull List<Statement> tryConvertActivityBody(ActivityContext cx, Activity activity) {
                return switch (activity) {
                        case ActivityExtension activityExtension -> convertActivityExtension(cx, activityExtension);
                        case Empty ignored -> convertEmptyAction(cx);
                        case ExtActivity extActivity -> convertExtActivity(cx, extActivity);
                        case Invoke invoke -> convertInvoke(cx, invoke);
                        case Pick pick -> convertPickAction(cx, pick);
                        case CatchAll catchAll -> convertCatchAll(cx, catchAll);
                        case ReceiveEvent receiveEvent -> convertReceiveEvent(cx, receiveEvent);
                        case Reply reply -> convertReply(cx, reply);
                        case UnhandledActivity unhandledActivity -> convertUnhandledActivity(cx, unhandledActivity);
                        case Throw throwActivity -> convertThrowActivity(cx, throwActivity);
                        case Activity.Assign assign -> convertAssign(cx, assign);
                        case Activity.Foreach foreach -> convertForeach(cx, foreach);
                        case Activity.NestedScope nestedScope -> convertNestedScope(cx, nestedScope);
                        case TibcoModel.Process.ExplicitTransitionGroup.InlineActivity inlineActivity ->
                                convertInlineActivity(cx, inlineActivity);
                };
        }

        private static @NotNull List<Statement> convertInlineActivity(
                ActivityContext cx, TibcoModel.Process.ExplicitTransitionGroup.InlineActivity inlineActivity) {
            List<Statement> body = new ArrayList<>();
            VarDeclStatment inputDecl = new VarDeclStatment(XML, cx.getAnnonVarName(), defaultEmptyXml());
            body.add(inputDecl);
            VariableReference result;
            if (inlineActivity.hasInputBinding()) {
                List<VarDeclStatment> inputBindings = convertInputBindings(cx, inputDecl.ref(),
                        List.of(inlineActivity.inputBinding()));
                body.addAll(inputBindings);
                result = new VariableReference(inputBindings.getLast().varName());
            } else {
                result = inputDecl.ref();
            }
            ActivityExtensionConfigConversion conversion = switch (inlineActivity) {
                case TibcoModel.Process.ExplicitTransitionGroup.InlineActivity.HttpEventSource ignored ->
                        emptyExtensionConversion(cx, result);
                case TibcoModel.Process.ExplicitTransitionGroup.InlineActivity.MapperActivity ignored ->
                        emptyExtensionConversion(cx, result);
                case UnhandledInlineActivity unhandledInlineActivity ->
                        convertUnhandledActivity(cx, result, unhandledInlineActivity);
                case AssignActivity assignActivity -> convertAssignActivity(cx, result, assignActivity);
                case TibcoModel.Process.ExplicitTransitionGroup.InlineActivity.NullActivity ignored ->
                        emptyExtensionConversion(cx, result);
                case HTTPResponse httpResponse -> convertHttpResponse(cx, result, httpResponse);
                case WriteLog writeLog -> convertWriteLogActivity(cx, result, writeLog);
                case CallProcess callProcess -> convertCallProcess(cx, result, callProcess);
                case FileWrite fileWrite -> convertFileWrite(cx, result, fileWrite);
                case FileRead fileRead -> convertFileRead(cx, result, fileRead);
                case XMLRenderActivity xmlRenderActivity -> convertXmlRenderActivity(cx, result, xmlRenderActivity);
                case XMLParseActivity xmlParseActivity -> convertXmlParseActivity(cx, result, xmlParseActivity);
                case SOAPSendReceive soapSendReceive -> convertSoapSendReceive(cx, result, soapSendReceive);
                case SOAPSendReply soapSendReply -> convertSoapSendReply(cx, result, soapSendReply);
                case LoopGroup loopGroup -> convertLoopGroup(cx, result, loopGroup);
                case REST rest -> convertREST(cx, result, rest);
            };
            body.addAll(conversion.body());
            body.add(addToContext(cx, conversion.result(), inlineActivity.name()));
            body.add(new Return<>(conversion.result()));
            return body;
        }

        private static ActivityExtensionConfigConversion convertSoapSendReply(
                ActivityContext cx, VariableReference result, SOAPSendReply soapSendReply) {
            VarDeclStatment envelop = new VarDeclStatment(XML, cx.getAnnonVarName(),
                    new XMLTemplate(ConversionUtils.createSoapEnvelope(result)));
            return new ActivityExtensionConfigConversion(envelop.ref(), List.of(envelop));
        }

        private static ActivityExtensionConfigConversion convertSoapSendReceive(
                ActivityContext cx, VariableReference result, SOAPSendReceive soapSendReceive) {
            String clientName = cx.getAnnonVarName();
            List<Statement> body = new ArrayList<>(initSoapClient(cx, soapSendReceive, clientName));

            VarDeclStatment envelope = new VarDeclStatment(XML, cx.getAnnonVarName(),
                    new XMLTemplate(ConversionUtils.createSoapEnvelope(result)));
            body.add(envelope);
            // We only support SOAP 1.1
            BallerinaModel.Expression soapAction = soapSendReceive.soapAction().map(StringConstant::new)
                    .orElseThrow(() -> new IllegalArgumentException("SOAP action is required"));
            VarDeclStatment res = new VarDeclStatment(XML, cx.getAnnonVarName(), new Check(
                    new RemoteMethodCallAction(new VariableReference(clientName), "sendReceive",
                            List.of(envelope.ref(), soapAction))));
            body.add(res);

            return new ActivityExtensionConfigConversion(res.ref(), body);
        }

        private static Collection<Statement> initSoapClient(ActivityContext cx, SOAPSendReceive soapSendReceive,
                                                            String clientName) {
            cx.addLibraryImport(Library.SOAP);
            return List.of(stmtFrom("soap11:Client %s = check new (\"%s\");"
                    .formatted(clientName, soapSendReceive.endpointURL())));
        }

        private static ActivityExtensionConfigConversion convertLoopGroup(
                ActivityContext cx, VariableReference result, LoopGroup loopGroup) {
            // TODO: deal with result once we have examples for loops with input bindings
            List<Statement> body = new ArrayList<>();
            VarDeclStatment resultValue = new VarDeclStatment(XML, cx.getAnnonVarName(), defaultEmptyXml());
            body.add(resultValue);
            VarDeclStatment loopSequence = new VarDeclStatment(XML, cx.getAnnonVarName(),
                    getFromContext(cx, loopGroup.over().variableName()));
            body.add(loopSequence);
            if (loopGroup.over().xPath().isPresent()) {
                String xPath = loopGroup.over().xPath().get();
                cx.addLibraryImport(Library.XML_DATA);
                body.add(new Statement.VarAssignStatement(loopSequence.ref(),
                        new Check(
                                new FunctionCall(XMLDataConstants.X_PATH_FUNCTION,
                                        List.of(loopSequence.ref(),
                                                exprFrom("`%s`".formatted(xPath)))))));
            }
            VarDeclStatment indexValue = new VarDeclStatment(INT, cx.getAnnonVarName(), exprFrom("-1"));
            body.add(indexValue);
            loopGroup.elementSlot().map(elementSlot -> addToContext(cx, defaultEmptyXml(), elementSlot))
                    .ifPresent(body::add);
            body.add(loopBody(cx, loopGroup, loopSequence.ref(), resultValue.ref(), indexValue.varName()));
            loopGroup.activityOutputName()
                    .map(name -> addToContext(cx, resultValue.ref(), name)).ifPresent(body::add);
            return new ActivityExtensionConfigConversion(resultValue.ref(), body);
        }

        private static Statement loopBody(ActivityContext cx, LoopGroup loop, VariableReference loopSequence,
                                          VariableReference result, String indexVar) {
            StringBuilder sb = new StringBuilder();
            String element = "each";
            sb.append("foreach xml %s in %s {".formatted(element, loopSequence));
            sb.append("%1$s = %1$s + 1;".formatted(indexVar));
            loop.indexSlot().map(
                            indexSlot -> addToContext(cx, new XMLTemplate("<root>${%s}</root>".formatted(indexVar)),
                                    indexSlot))
                    .ifPresent(sb::append);
            loop.elementSlot().ifPresent(s -> sb.append(addToContext(cx, new VariableReference(element), s)));
            AnalysisResult analysisResult = cx.processContext.analysisResult;
            String scopeFn = analysisResult.getControlFlowFunctions(loop.body()).scopeFn();
            VarDeclStatment res = new VarDeclStatment(XML, cx.getAnnonVarName(),
                    new FunctionCall(scopeFn, List.of(cx.contextVarRef())));
            sb.append(res);
            BallerinaModel.Expression resultUpdate;
            if (loop.accumulateOutput()) {
                resultUpdate = exprFrom("%s + %s".formatted(result, res));
            } else {
                resultUpdate = res.ref();
            }
            sb.append(new Statement.VarAssignStatement(result, resultUpdate));
            sb.append("}");
            return stmtFrom(sb.toString());
        }

        private static ActivityExtensionConfigConversion convertREST(
                ActivityContext cx, VariableReference input, REST rest) {
            if (rest.method() == REST.Method.GET) {
                return convertGet(cx, rest);
            }
            List<Statement> body = new ArrayList<>();
            body.add(stmtFrom("xmlns \"http://www.tibco.com/namespaces/tnt/plugins/json\" as ns;"));
            VarDeclStatment requestBody = new VarDeclStatment(XML, cx.getAnnonVarName(),
                    exprFrom("%s/**/<ns:Body>".formatted(input.varName())));
            body.add(requestBody);
            String toJsonFn = cx.getToJsonFunction();
            VarDeclStatment json = new VarDeclStatment(JSON, cx.getAnnonVarName(),
                    new FunctionCall(toJsonFn, List.of(requestBody.ref())));
            body.add(json);

            String remoteMethod = switch (rest.method()) {
                case PUT -> "put";
                case GET -> throw new IllegalStateException("Unexpected");
                case DELETE -> "delete";
                case POST -> "post";
            };
            return finishRESTConversion(cx, rest, remoteMethod, body, List.of(json.ref()));
        }

        private static ActivityExtensionConfigConversion convertGet(
                ActivityContext cx, REST rest) {
            List<Statement> body = new ArrayList<>();
            body.add(stmtFrom("xmlns \"http://www.tibco.com/namespaces/tnt/plugins/json\" as ns;"));
            return finishRESTConversion(cx, rest, "get", body, List.of());
        }

        private static @NotNull ActivityExtensionConfigConversion finishRESTConversion(
                ActivityContext cx, REST rest, String remoteMethod,
                List<Statement> body, List<BallerinaModel.Expression> payload) {
            cx.addLibraryImport(Library.HTTP);
            VarDeclStatment client = new VarDeclStatment(typeFrom("http:Client"), cx.getAnnonVarName(),
                    new Check(exprFrom("new (\"%s\")".formatted(rest.url()))));
            body.add(client);
            BallerinaModel.TypeDesc responseType = switch (rest.responseType()) {
                case JSON -> JSON;
                case XML -> XML;
            };
            List<BallerinaModel.Expression> args = new ArrayList<>();
            args.add(new StringConstant("/"));
            args.addAll(payload);
            VarDeclStatment response = new VarDeclStatment(responseType, cx.getAnnonVarName(),
                    new Check(new RemoteMethodCallAction(client.ref(), remoteMethod, args)));
            body.add(response);
            if (responseType == JSON) {
                String toXmlFn = cx.getToXmlFunction();
                VarDeclStatment xmlResponse = new VarDeclStatment(XML, cx.getAnnonVarName(),
                        new Check(new FunctionCall(toXmlFn, List.of(
                                new TypeCast(new BallerinaModel.TypeDesc.MapTypeDesc(JSON),
                                        response.ref())))));
                body.add(xmlResponse);
                response = xmlResponse;
            }
            VarDeclStatment result = new VarDeclStatment(XML, cx.getAnnonVarName(),
                    new XMLTemplate("<ns:RESTOutput><msg>${%s}</msg></ns:RESTOutput>"
                            .formatted(response.ref())));
            body.add(result);
            return new ActivityExtensionConfigConversion(result.ref(), body);
        }

        private static ActivityExtensionConfigConversion convertCallProcess(
                ActivityContext cx, VariableReference result, CallProcess callProcess) {
            VariableReference client = cx.getProcessClient(callProcess.processName());
            VarDeclStatment returnVal = new VarDeclStatment(XML, cx.getAnnonVarName(),
                    new Check(new RemoteMethodCallAction(client, "post",
                            List.of(new StringConstant(""), result))));
            return new ActivityExtensionConfigConversion(returnVal.ref(), List.of(returnVal));
        }

        private static ActivityExtensionConfigConversion convertFileRead(
                ActivityContext cx, VariableReference result, FileRead fileRead) {
            List<Statement> body = new ArrayList<>();
            VarDeclStatment fileName = new VarDeclStatment(STRING, "fileName",
                    exprFrom("(%s/**/<fileName>/*).toString()".formatted(result.varName())));
            body.add(fileName);

            cx.addLibraryImport(Library.IO);
            VarDeclStatment content = new VarDeclStatment(STRING, "content",
                    new Check(new FunctionCall(IOConstants.FILE_READ_FUNCTION, List.of(fileName.ref()))));
            body.add(content);
            VarDeclStatment wrapped = new VarDeclStatment(XML, cx.getAnnonVarName(),
                    new XMLTemplate("""
                            <ns:ReadActivityOutputTextClass xmlns:ns="http://www.tibco.com/namespaces/tnt/plugins/file">
                                <fileContent>
                                    <textContent>${%s}</textContent>
                                </fileContent>
                            </ns:ReadActivityOutputTextClass>
                            """
                            .formatted(content.varName())));
            body.add(wrapped);
            return new ActivityExtensionConfigConversion(wrapped.ref(), body);
        }

        private static ActivityExtensionConfigConversion convertFileWrite(
                ActivityContext cx, VariableReference result, FileWrite fileWrite) {
            assert fileWrite.encoding().equals("text");
            List<Statement> body = new ArrayList<>();
            VarDeclStatment fileName = new VarDeclStatment(STRING, "fileName",
                    exprFrom("(%s/**/<fileName>/*).toString()".formatted(result.varName())));
            body.add(fileName);
            VarDeclStatment textContent = new VarDeclStatment(STRING, "content",
                    exprFrom("(%s/**/<textContent>/*).toString()".formatted(result.varName())));
            body.add(textContent);
            StringConstant mode = fileWrite.append() ? new StringConstant("APPEND")
                    : new StringConstant("OVERWRITE");
            cx.addLibraryImport(Library.IO);
            body.add(new CallStatement(new Check(new FunctionCall(IOConstants.FILE_WRITE_FUNCTION,
                    List.of(fileName.ref(), textContent.ref(), mode)))));
            return new ActivityExtensionConfigConversion(result, body);
        }

        private static ActivityExtensionConfigConversion convertXmlParseActivity(
                ActivityContext cx, VariableReference result, XMLParseActivity xmlParseActivity) {
            List<Statement> body = new ArrayList<>();
            VarDeclStatment xmlString = new VarDeclStatment(XML, cx.getAnnonVarName(),
                    exprFrom("%s/<xmlString>/*".formatted(result.varName())));
            body.add(xmlString);
            VarDeclStatment asString = new VarDeclStatment(STRING, cx.getAnnonVarName(),
                    new MethodCall(xmlString.ref(), "toString", List.of()));
            body.add(asString);
            VarDeclStatment xmlValue = new VarDeclStatment(XML, cx.getAnnonVarName(),
                    new Check(new FunctionCall("xml:fromString", List.of(asString.ref()))));
            body.add(xmlValue);
            VarDeclStatment wrappedValue = new VarDeclStatment(XML, cx.getAnnonVarName(),
                    new XMLTemplate("<root>${%s}</root>".formatted(xmlValue.ref())));
            body.add(wrappedValue);
            return new ActivityExtensionConfigConversion(wrappedValue.ref(), body);
        }

        private static ActivityExtensionConfigConversion convertXmlRenderActivity(
                ActivityContext cx, VariableReference result, XMLRenderActivity xmlRenderActivity) {
            List<Statement> body = new ArrayList<>();
            VarDeclStatment stringValue = new VarDeclStatment(STRING, cx.getAnnonVarName(),
                    new MethodCall(result, "toBalString", List.of()));
            body.add(stringValue);
            VarDeclStatment res = new VarDeclStatment(XML, cx.getAnnonVarName(),
                    new XMLTemplate("<root>/<xmlString>${%s}</xmlString></root>"
                            .formatted(stringValue.ref())));
            body.add(res);
            return new ActivityExtensionConfigConversion(res.ref(), body);
        }

        private static ActivityExtensionConfigConversion convertWriteLogActivity(
                ActivityContext cx, VariableReference result, WriteLog writeLog) {
            List<Statement> body = new ArrayList<>();
            VarDeclStatment message = new VarDeclStatment(XML, cx.getAnnonVarName(),
                    exprFrom("%s/**/<message>/*".formatted(result.varName())));
            body.add(message);
            cx.addLibraryImport(Library.LOG);
            body.add(new CallStatement(new FunctionCall(LogConstants.LOG_INFO_FUNCTION,
                    List.of(new MethodCall(message.ref(), "toString", List.of())))));
            return new ActivityExtensionConfigConversion(message.ref(), body);
        }

        private static ActivityExtensionConfigConversion convertHttpResponse(
                ActivityContext cx, VariableReference result, HTTPResponse httpResponse) {
            List<Statement> body = new ArrayList<>();
            VarDeclStatment responseValue = new VarDeclStatment(XML, cx.getAnnonVarName(),
                    exprFrom("%s/**/<asciiContent>/*".formatted(result.varName())));
            body.add(responseValue);
            return new ActivityExtensionConfigConversion(responseValue.ref(), body);
        }

        private static ActivityExtensionConfigConversion convertAssignActivity(
                ActivityContext cx, VariableReference result, AssignActivity assignActivity) {
            List<Statement> body = new ArrayList<>();
            body.add(addToContext(cx, result, assignActivity.variableName()));
            VarDeclStatment assignedValue = new VarDeclStatment(XML, cx.getAnnonVarName(),
                    getFromContext(cx, assignActivity.variableName()));
            body.add(assignedValue);
            return new ActivityExtensionConfigConversion(assignedValue.ref(), body);
        }

        private static ActivityExtensionConfigConversion convertUnhandledActivity(
                ActivityContext cx, VariableReference result, UnhandledInlineActivity unhandledInlineActivity) {
            List<Statement> body = List.of(
                    new Comment("FIXME: Failed to convert rest of activity"),
                    elementAsComment(unhandledInlineActivity.element()));
            return new ActivityExtensionConfigConversion(result, body);
        }

        private static @NotNull List<Statement> convertNestedScope(ActivityContext cx,
                                                                   Activity.NestedScope nestedScope) {
            return convertActivityWithScope(cx, nestedScope);
        }

        private static @NotNull List<Statement> convertForeach(ActivityContext cx, Activity.Foreach foreach) {
            List<Statement> body = new ArrayList<>();
            BallerinaModel.Expression init = convertValueSource(cx, foreach.startCounterValue(), body, INT);
            BallerinaModel.Expression end = convertValueSource(cx, foreach.finalCounterValue(), body, INT);
            VarDeclStatment result = new VarDeclStatment(XML, cx.getAnnonVarName(), defaultEmptyXml());
            body.add(result);
            Statement contextUpdate = addToContext(cx,
                    new XMLTemplate("<root>${%s}</root>".formatted(foreach.counterName())),
                    foreach.counterName());
            String scopeFn = cx.processContext.analysisResult.getControlFlowFunctions(foreach.scope()).scopeFn();
            body.add(stmtFrom("""
                    foreach int %1$s in %2$s ..< %3$s {
                        %4$s
                        %5$s = %6$s(%7$s);
                    }
                    """.formatted(foreach.counterName(), init, end, contextUpdate, result.ref(), scopeFn,
                    cx.contextVarRef())));
            body.add(new Return<>(result.ref()));
            return body;
        }

        private static @NotNull List<Statement> convertAssign(ActivityContext cx, Activity.Assign assign) {
            List<Statement> body = new ArrayList<>();
            TibcoModel.ValueSource from = assign.operation().from();
            BallerinaModel.Expression sourceExp = convertValueSource(cx, from, body, XML);
            VarDeclStatment source = new VarDeclStatment(XML, cx.getAnnonVarName(), sourceExp);
            body.add(source);
            body.add(addToContext(cx, source.ref(), assign.operation().to().name()));
            return body;
        }

        private static BallerinaModel.@NotNull Expression convertValueSource(
                ActivityContext cx, TibcoModel.ValueSource from, List<Statement> body,
                BallerinaModel.TypeDesc expectedType) {
            return switch (from) {
                case Activity.Expression.XSLT xslt -> {
                    VarDeclStatment init = new VarDeclStatment(expectedType, cx.getAnnonVarName(),
                            defaultEmptyXml());
                    body.add(init);
                    yield xsltTransform(cx, init.ref(), xslt);
                }
                case TibcoModel.ValueSource.VarRef varRef -> getFromContext(cx, varRef.name());
                case Activity.Expression.XPath xPath -> {
                    VarDeclStatment result = new VarDeclStatment(expectedType, cx.getAnnonVarName(),
                            ConversionUtils.xPath(cx.processContext, defaultEmptyXml(),
                                    cx.contextVarRef(), xPath));
                    body.add(result);
                    yield result.ref();
                }
                case TibcoModel.ValueSource.Constant constant -> {
                    VarDeclStatment result = new VarDeclStatment(expectedType, cx.getAnnonVarName(),
                            exprFrom(constant.value()));
                    body.add(result);
                    yield result.ref();
                }
            };
        }

        private static List<Statement> convertThrowActivity(ActivityContext cx, Throw throwActivity) {
            List<Statement> body = new ArrayList<>();
            BallerinaModel.Expression result;
            VarDeclStatment inputDecl = new VarDeclStatment(XML, cx.getAnnonVarName(), defaultEmptyXml());
            body.add(inputDecl);
            VariableReference input = inputDecl.ref();
            if (throwActivity.inputBindings().isEmpty()) {
                result = input;
            } else {
                List<VarDeclStatment> inputBindings = convertInputBindings(cx, input,
                        throwActivity.inputBindings());
                body.addAll(inputBindings);
                result = inputBindings.getLast().ref();
            }
            // TODO: set the body correctly using result
            VarDeclStatment errorValue = new VarDeclStatment(ERROR, cx.getAnnonVarName(),
                    exprFrom("error(\"TODO: create error value\")"));
            body.add(errorValue);
            body.add(stmtFrom(String.format("panic %s;", errorValue.varName())));

            return body;
        }

        private static List<Statement> convertCatchAll(ActivityContext cx, CatchAll catchAll) {
            return convertActivityWithScope(cx, catchAll);
        }

        private static @NotNull List<Statement> convertActivityWithScope(
                ActivityContext cx, Activity.ActivityWithScope activityWithScope) {
            String scopeFn = cx.processContext.analysisResult.getControlFlowFunctions(activityWithScope.scope())
                    .scopeFn();
            return List.of(new Return<>(new FunctionCall(scopeFn, List.of(cx.contextVarRef()))));
        }

        private static List<Statement> convertUnhandledActivity(ActivityContext cx,
                                                                UnhandledActivity unhandledActivity) {
            BallerinaModel.Expression inputXml = defaultEmptyXml();
            return List.of(new Comment(unhandledActivity.reason()), elementAsComment(unhandledActivity.element()),
                    new Return<>(inputXml));
        }

        private static Comment elementAsComment(Element element) {
            return new Comment(ConversionUtils.elementToString(element));
        }

        private static List<Statement> convertReply(ActivityContext cx, Reply reply) {
            List<Statement> body = new ArrayList<>();
            VarDeclStatment inputDecl = new VarDeclStatment(XML, cx.getAnnonVarName(), defaultEmptyXml());
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

        private static List<Statement> convertPickAction(ActivityContext cx, Pick pick) {
            return convertActivityWithScope(cx, pick);
        }

        private static List<Statement> convertEmptyAction(ActivityContext cx) {
            List<Statement> body = new ArrayList<>();
            BallerinaModel.Expression inputXml = defaultEmptyXml();
            body.add(new Return<>(Optional.of(inputXml)));
            return body;
        }

        private static List<Statement> convertActivityExtension(ActivityContext cx,
                                                                ActivityExtension activityExtension) {
            List<Statement> body = new ArrayList<>();
            VarDeclStatment inputDecl = new VarDeclStatment(XML, cx.getAnnonVarName(),
                    activityExtension.inputVariable()
                            .map(name -> (BallerinaModel.Expression) getFromContext(cx, name))
                            .orElseGet(ActivityConverter::defaultEmptyXml));
            body.add(inputDecl);
            List<VarDeclStatment> inputBindings = convertInputBindings(cx, inputDecl.ref(),
                    activityExtension.inputBindings());
            body.addAll(inputBindings);

            VariableReference result = inputBindings.isEmpty() ? inputDecl.ref()
                    : new VariableReference(inputBindings.getLast().varName());

            ActivityExtension.Config config = activityExtension.config();
            ActivityExtensionConfigConversion conversion = switch (config) {
                case ActivityExtension.Config.End ignored -> emptyExtensionConversion(cx, result);
                case ActivityExtension.Config.HTTPSend httpSend -> createHttpSend(cx, result, httpSend);
                case ActivityExtension.Config.JsonOperation jsonOperation -> createJsonOperation(cx, result,
                        jsonOperation,
                        activityExtension.outputVariable().orElseThrow(
                                () -> new IllegalStateException(
                                        "json operation should have output variable")));
                case ActivityExtension.Config.SQL sql -> createSQLOperation(cx, result, sql);
                case ActivityExtension.Config.SendHTTPResponse ignored -> emptyExtensionConversion(cx, result);
                case ActivityExtension.Config.FileWrite fileWrite -> createFileWriteOperation(cx, result, fileWrite);
                case ActivityExtension.Config.Log log -> createLogOperation(cx, result, log);
                case ActivityExtension.Config.RenderXML ignored -> emptyExtensionConversion(cx, result);
                case ActivityExtension.Config.Mapper ignored -> emptyExtensionConversion(cx, result);
                case ActivityExtension.Config.AccumulateEnd accumulateEnd -> createAccumulateEnd(cx,
                        accumulateEnd,
                        activityExtension.outVariableName().orElseThrow(
                                () -> new IllegalStateException(
                                        "accumulate end should have output variable")));
            };
            body.addAll(conversion.body());
            activityExtension.outputVariable()
                    .ifPresent(outputVar -> body.add(addToContext(cx, conversion.result(), outputVar)));
            body.add(new Return<>(conversion.result()));
            return body;
        }

        private static ActivityExtensionConfigConversion createAccumulateEnd(
                ActivityContext cx, ActivityExtension.Config.AccumulateEnd accumulateEnd,
                String resultName) {
            AnalysisResult ar = cx.processContext.analysisResult;
            Activity source = ar.findActivity(accumulateEnd.activityName())
                    .orElseThrow(() -> new IllegalStateException(
                            "Cannot find activity: " + accumulateEnd.activityName()));
            if (!(source instanceof Activity.ActivityWithOutput activityWithOutput &&
                    activityWithOutput.outVariableName().isPresent())) {
                throw new IllegalStateException(
                        "Cannot find output variable for activity: " + accumulateEnd.activityName());
            }
            List<Statement> body = new ArrayList<>();
            String outputVariable = activityWithOutput.outVariableName().get();
            VarDeclStatment currentValue = new VarDeclStatment(UnionTypeDesc.of(XML, NIL), cx.getAnnonVarName(),
                    exprFrom("%s[\"%s\"]".formatted(cx.contextVarRef(), resultName)));
            body.add(currentValue);
            VarDeclStatment addition = new VarDeclStatment(XML, cx.getAnnonVarName(),
                    getFromContext(cx, outputVariable));
            body.add(addition);
            VarDeclStatment accumResult = new VarDeclStatment(XML, cx.getAnnonVarName());
            body.add(accumResult);
            Statement.IfElseStatement accum = new Statement.IfElseStatement(
                    new TypeCheckExpression(currentValue.ref(), XML),
                    List.of(new Statement.VarAssignStatement(accumResult.ref(),
                            exprFrom("%s + %s".formatted(currentValue.ref(), addition.ref())))),
                    List.of(),
                    List.of(new Statement.VarAssignStatement(accumResult.ref(), addition.ref())));
            body.add(accum);
            return new ActivityExtensionConfigConversion(accumResult.ref(), body);
        }

        private static ActivityExtensionConfigConversion createJsonOperation(
                ActivityContext cx, VariableReference result,
                ActivityExtension.Config.JsonOperation jsonOperation, String outputVariable) {
            return switch (jsonOperation.kind()) {
                case JSON_RENDER -> createJsonRenderOperation(cx, result);
                case JSON_PARSER -> createJsonParserOperation(cx, result, jsonOperation, outputVariable);
                default -> throw new IllegalStateException(
                        "Unexpected json operation kind: " + jsonOperation.kind());
            };
        }

        private static ActivityExtensionConfigConversion createJsonParserOperation(
                ActivityContext cx, VariableReference result,
                ActivityExtension.Config.JsonOperation jsonOperation,
                String outputVariable) {
            VarDeclStatment rendered = new VarDeclStatment(XML, cx.getAnnonVarName(),
                    new Check(
                            new FunctionCall(
                                    cx.getRenderJsonAsXMLFunction(
                                            cx.variableType(outputVariable)),
                                    List.of(result))));
            return new ActivityExtensionConfigConversion(rendered.ref(), List.of(rendered));
        }

        private static ActivityExtensionConfigConversion createJsonRenderOperation(ActivityContext cx,
                                                                                   VariableReference result) {
            String jsonRenderFn = cx.getRenderJsonFn();
            VarDeclStatment jsonResult = new VarDeclStatment(XML, cx.getAnnonVarName(),
                    new FunctionCall(jsonRenderFn, List.of(result)));
            return new ActivityExtensionConfigConversion(jsonResult.ref(), List.of(jsonResult));
        }

        private static @NotNull ActivityExtensionConfigConversion emptyExtensionConversion(ActivityContext cx,
                                                                                           VariableReference result) {
            VarDeclStatment wrapped = new VarDeclStatment(XML, cx.getAnnonVarName(),
                    new XMLTemplate("<root>${%s}</root>".formatted(result.varName())));
            return new ActivityExtensionConfigConversion(wrapped.ref(), List.of(wrapped));
        }

        private static ActivityExtensionConfigConversion createLogOperation(ActivityContext cx,
                                                                            VariableReference result,
                                                                            ActivityExtension.Config.Log log) {
            List<Statement> body = new ArrayList<>();

            BallerinaModel.TypeDesc dataType = cx.getLogInputType();
            VarDeclStatment dataDecl = new VarDeclStatment(dataType, cx.getAnnonVarName(),
                    new FunctionCall(cx.getConvertToTypeFunction(dataType), List.of(result)));
            body.add(dataDecl);

            CallStatement callStatement = new CallStatement(
                    new FunctionCall(
                            cx.getLogFunction(),
                            List.of(new VariableReference(dataDecl.varName()))));
            body.add(callStatement);

            return new ActivityExtensionConfigConversion(result, body);
        }

        private static ActivityExtensionConfigConversion createFileWriteOperation(
                ActivityContext cx, VariableReference result, ActivityExtension.Config.FileWrite fileWrite) {
            List<Statement> body = new ArrayList<>();
            BallerinaModel.TypeDesc dataType = cx.getFileWriteConfigType();
            VarDeclStatment dataDecl = new VarDeclStatment(dataType, cx.getAnnonVarName(),
                    new FunctionCall(cx.getConvertToTypeFunction(dataType), List.of(result)));
            body.add(dataDecl);

            VarDeclStatment fileNameDecl = new VarDeclStatment(STRING, cx.getAnnonVarName(),
                    new TypeCast(STRING, new FieldAccess(
                            new VariableReference(dataDecl.varName()),
                            TibcoFileConfigConstants.FILE_NAME_FIELD_NAME)));
            body.add(fileNameDecl);

            VarDeclStatment textContentDecl = new VarDeclStatment(STRING, cx.getAnnonVarName(), new FieldAccess(
                    new VariableReference(dataDecl.varName()),
                    TibcoFileConfigConstants.TEXT_CONTENT_FIELD_NAME));
            body.add(textContentDecl);

            CallStatement callStatement = new CallStatement(
                    new CheckPanic(
                            new FunctionCall(
                                    cx.getFileWriteFunction(),
                                    List.of(new VariableReference(fileNameDecl.varName()),
                                            new VariableReference(textContentDecl
                                                    .varName())))));
            body.add(callStatement);
            return new ActivityExtensionConfigConversion(result, body);
        }

        private static ActivityExtensionConfigConversion createSQLOperation(
                ActivityContext cx, VariableReference inputVar, ActivityExtension.Config.SQL sql) {
            List<Statement> body = new ArrayList<>();
            Map<String, VariableReference> vars = addParamDecl(body, inputVar, sql);

            VarDeclStatment queryDecl = ConversionUtils.createQueryDecl(cx, vars, sql);
            body.add(queryDecl);

            VariableReference dbClient = cx.client(sql.sharedResourcePropertyName());
            if (sql.query().toUpperCase().startsWith("SELECT")) {
                return finishSelectQuery(cx, sql, dbClient, queryDecl.ref(), body);
            }
            body.add(new VarDeclStatment(
                    cx.processContext.getTypeByName(BallerinaSQLConstants.EXECUTION_RESULT_TYPE),
                    cx.getAnnonVarName(),
                    new Check(
                            new RemoteMethodCallAction(dbClient,
                                    BallerinaSQLConstants.EXECUTE_METHOD,
                                    List.of(queryDecl.ref())))));
            VarDeclStatment dummyXmlResult = new VarDeclStatment(XML, cx.getAnnonVarName(), defaultEmptyXml());
            body.add(dummyXmlResult);
            return new ActivityExtensionConfigConversion(dummyXmlResult.ref(), body);
        }

        private static Map<String, VariableReference> addParamDecl(List<Statement> body, VariableReference dataValue,
                                                                   ActivityExtension.Config.SQL sql) {
            Map<String, VariableReference> vars = new HashMap<>();
            for (ActivityExtension.Config.SQL.SQLParameter each : sql.parameters()) {
                String name = each.name();
                VarDeclStatment varDecl = new VarDeclStatment(STRING, name,
                        exprFrom("(%s/<%s>/*).toString().trim()".formatted(dataValue.varName(), name)));
                body.add(varDecl);
                vars.put(name, varDecl.ref());
            }
            return vars;
        }

        private static @NotNull ActivityExtensionConfigConversion finishSelectQuery(
                ActivityContext cx, ActivityExtension.Config.SQL sql, VariableReference dbClient,
                VariableReference query, List<Statement> body) {
            StreamTypeDesc streamTypeDesc = new StreamTypeDesc(
                    new BallerinaModel.TypeDesc.MapTypeDesc(ANYDATA),
                    UnionTypeDesc.of(ERROR, NIL));
            VarDeclStatment stream = new VarDeclStatment(
                    streamTypeDesc, cx.getAnnonVarName(),
                    new RemoteMethodCallAction(dbClient, BallerinaSQLConstants.QUERY_METHOD,
                            List.of(query)));
            body.add(stream);

            VarDeclStatment accum = new VarDeclStatment(XML, cx.getAnnonVarName(), new XMLTemplate(""));
            body.add(accum);
            String toXmlFn = cx.getToXmlFunction();
            String xmlVar = cx.getAnnonVarName();

            body.add(stmtFrom("""
                    check from var each in %1$s do {
                        xml %2$s = check %3$s(each);
                        %4$s = %5$s + %2$s;
                    };
                    """.formatted(stream.ref(), xmlVar, toXmlFn, accum.ref(), accum.ref())));

            VarDeclStatment result = new VarDeclStatment(XML, cx.getAnnonVarName(),
                    new XMLTemplate("<root>${%s}</root>".formatted(accum.ref())));
            body.add(result);
            return new ActivityExtensionConfigConversion(result.ref(), body);
        }

        private static ActivityExtensionConfigConversion createHttpSend(
                ActivityContext cx, VariableReference configVar, ActivityExtension.Config.HTTPSend httpSend) {
            List<Statement> body = new ArrayList<>();
            VariableReference client = cx.client(httpSend.httpClientResource());
            VarDeclStatment method = new VarDeclStatment(STRING, cx.getAnnonVarName(),
                    exprFrom("(%s/**/<Method>[0]).data()".formatted(configVar.varName())));
            body.add(method);

            VarDeclStatment requestURI = new VarDeclStatment(STRING, cx.getAnnonVarName(),
                    exprFrom("(%s/**/<RequestURI>[0]).data()".formatted(configVar.varName())));
            body.add(requestURI);
            // TODO: how to get map<json> from xml?
            // VarDeclStatment headers = new VarDeclStatment(JSON, cx.getAnnonVarName(),
            // new
            // BallerinaExpression("%s/**/<Headers>[0]".formatted(configVar.varName())));
            // body.add(headers);

            VarDeclStatment result = new VarDeclStatment(JSON, cx.getAnnonVarName(), exprFrom("()"));
            body.add(result);

            body.add(stmtFrom("""
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
                    """.formatted(method.varName(), result.varName(), client.varName(),
                    requestURI.varName())));
            VarDeclStatment resultDecl = new VarDeclStatment(XML, cx.getAnnonVarName(),
                    new XMLTemplate("<root><asciiContent>${%s.toJsonString()}</asciiContent></root>"
                            .formatted(
                                    result.varName())));
            body.add(resultDecl);

            return new ActivityExtensionConfigConversion(new VariableReference(resultDecl.varName()), body);
        }

        private static List<Statement> convertReceiveEvent(ActivityContext cx, ReceiveEvent receiveEvent) {
            if (receiveEvent.variable().isPresent()) {
                var variable = receiveEvent.variable().get();
                return List.of(
                        addToContext(cx, getFromContext(cx,
                                ConversionUtils.Constants.CONTEXT_INPUT_NAME), variable),
                        new Return<>(getFromContext(cx, ConversionUtils.Constants.CONTEXT_INPUT_NAME)));
            } else {
                return List.of(new Return<>(defaultEmptyXml()));
            }
        }

        private static List<Statement> convertInvoke(ActivityContext cx, Invoke invoke) {
            List<Statement> body = new ArrayList<>();
            VarDeclStatment inputDecl = new VarDeclStatment(XML, cx.getAnnonVarName(), defaultEmptyXml());
            body.add(inputDecl);
            List<VarDeclStatment> inputBindings = convertInputBindings(cx, inputDecl.ref(), invoke.inputBindings());
            body.addAll(inputBindings);
            VariableReference input = inputBindings.isEmpty() ? inputDecl.ref() : inputBindings.getLast().ref();

            AnalysisResult ar = cx.processContext.analysisResult;
            TibcoModel.PartnerLink.Binding binding = ar.getBinding(invoke.partnerLink());
            String path = binding.path().basePath();
            VariableReference client = cx.getHttpClient(path);

            VarDeclStatment callResult;
            StringConstant bindingPath = new StringConstant(binding.path().path());
            if (binding.operation().method().method.equalsIgnoreCase("get")) {
                // TODO: properly set query path
                callResult = new VarDeclStatment(JSON,
                        cx.getAnnonVarName(),
                        new Check(
                                new RemoteMethodCallAction(client,
                                        binding.operation().method().method,
                                        List.of(bindingPath))));
            } else {
                callResult = new VarDeclStatment(JSON,
                        cx.getAnnonVarName(),
                        new Check(
                                new RemoteMethodCallAction(client,
                                        binding.operation().method().method,
                                        List.of(bindingPath, input))));
            }
            body.add(callResult);

            FunctionCall jsonToXMLFunctionCall = new FunctionCall(cx.getJsonToXMLFunction(),
                    List.of(callResult.ref()));
            VarDeclStatment resultDecl = new VarDeclStatment(XML, cx.getAnnonVarName(),
                    new Check(jsonToXMLFunctionCall));
            body.add(resultDecl);
            VariableReference result = new VariableReference(resultDecl.varName());

            body.add(addToContext(cx, result, invoke.outputVariable()));
            body.add(new Return<>(result));
            return body;
        }

        private static List<Statement> convertExtActivity(ActivityContext cx, ExtActivity extActivity) {
            List<Statement> body = new ArrayList<>();
            VarDeclStatment inputDecl = new VarDeclStatment(XML, cx.getAnnonVarName(), defaultEmptyXml());
            body.add(inputDecl);
            VariableReference result = inputDecl.ref();
            if (!extActivity.expression().isEmpty()) {
                Activity.Expression expression = extActivity.expression().get();
                if (!(expression instanceof Activity.Expression.XSLT xslt)) {
                    throw new IllegalArgumentException("Only XSLT supported as Ext activity expression");
                }
                VarDeclStatment transformDecl = new VarDeclStatment(XML, cx.getAnnonVarName(),
                        xsltTransform(cx, result, xslt));
                body.add(transformDecl);
                result = transformDecl.ref();
            }
            if (!extActivity.inputBindings().isEmpty()) {
                List<VarDeclStatment> inputBindings = convertInputBindings(cx, result,
                        extActivity.inputBindings());
                body.addAll(inputBindings);
                result = new VariableReference(inputBindings.getLast().varName());
            }
            String namespaceFixFn = cx.getNamespaceFixFn();
            VarDeclStatment resultWithoutNamespaces = new VarDeclStatment(XML, cx.getAnnonVarName(),
                    new FunctionCall(namespaceFixFn, List.of(result)));
            body.add(resultWithoutNamespaces);
            VariableReference finalResult = resultWithoutNamespaces.ref();
            String targetProcess = extActivity.callProcess().subprocessName();
            Optional<ProcessContext.DefaultClientDetails> client = cx.getDefaultClientDetails(targetProcess);

            VarDeclStatment resultDecl = client.map(cl -> callProcessUsingClient(cx, cl, finalResult))
                    .orElseGet(() -> callProcessDirectlyUsingStartFunction(
                            cx, cx.getProcessStartFunctionName(targetProcess), finalResult));
            body.add(resultDecl);
            VariableReference resultRef = resultDecl.ref();
            body.add(addToContext(cx, resultRef, extActivity.outputVariable()));
            body.add(new Return<>(resultRef));
            return body;
        }

        private static @NotNull VarDeclStatment callProcessUsingClient(ActivityContext cx,
                                                                       ProcessContext.DefaultClientDetails client,
                                                                       VariableReference result) {
            if (client.method.equalsIgnoreCase("get")) {
                // TODO: properly set path parameters
                return new VarDeclStatment(XML, cx.getAnnonVarName(),
                        new Check(
                                new RemoteMethodCallAction(client.ref(),
                                        client.method,
                                        List.of(new StringConstant("")))));
            } else {
                return new VarDeclStatment(XML, cx.getAnnonVarName(),
                        new Check(
                                new RemoteMethodCallAction(client.ref(),
                                        client.method,
                                        List.of(new StringConstant(""), result))));
            }
        }

        private static @NotNull VarDeclStatment callProcessDirectlyUsingStartFunction(
                ActivityContext cx, ProjectContext.FunctionData startFunction, VariableReference result) {
            String convertToTypeFunction = cx.processContext.getConvertToTypeFunction(startFunction.inputType());
            FunctionCall convertToTypeFunctionCall = new FunctionCall(convertToTypeFunction, List.of(result));

            return new VarDeclStatment(XML, cx.getAnnonVarName(), new Check(
                    new FunctionCall(cx.processContext.getToXmlFunction(), List.of(new Check(
                            new Trap(new FunctionCall(startFunction.name(),
                                    List.of(convertToTypeFunctionCall))))))));
        }

        private static List<VarDeclStatment> convertInputBindings(ActivityContext cx, VariableReference input,
                                                                  Collection<InputBinding> inputBindings) {
            List<VarDeclStatment> varDelStatements = new ArrayList<>();
            VariableReference last = input;
            for (InputBinding transform : inputBindings) {
                VarDeclStatment varDecl = switch (transform) {
                    case InputBinding.CompleteBinding completeBinding -> new VarDeclStatment(XML, cx.getAnnonVarName(),
                            xsltTransform(cx, last, completeBinding.xslt()));
                    case InputBinding.PartialBindings partialBindings ->
                            convertPartialInputBinding(cx, partialBindings, last, varDelStatements);
                };
                varDelStatements.add(varDecl);
                last = varDecl.ref();
            }
            return varDelStatements;
        }

        private static @NotNull VarDeclStatment convertPartialInputBinding(ActivityContext cx,
                                                                           InputBinding.PartialBindings partialBindings,
                                                                           VariableReference last,
                                                                           List<VarDeclStatment> varDelStatements) {
            List<VarDeclStatment> statements = partialBindings.xslt().stream()
                    .map(each -> xsltTransform(cx, last, each))
                    .map(each -> new VarDeclStatment(XML, cx.getAnnonVarName(), each)).toList();
            varDelStatements.addAll(statements);
            String concat = statements.stream().map(VarDeclStatment::varName).collect(Collectors.joining(" + "));
            return new VarDeclStatment(XML, cx.getAnnonVarName(),
                    new XMLTemplate("<root>${%s}</root>".formatted(concat)));
        }

        private static BallerinaModel.Expression xsltTransform(ActivityContext cx, VariableReference inputVariable,
                                                               Activity.Expression.XSLT xslt) {
            cx.addLibraryImport(Library.XSLT);
            String styleSheet = xsltTransformer.apply(cx, xslt.expression());
            return new Check(new FunctionCall(XSLTConstants.XSLT_TRANSFORM_FUNCTION,
                    List.of(inputVariable, new XMLTemplate(styleSheet), cx.contextVarRef())));
        }

        private static XMLTemplate defaultEmptyXml() {
            return new XMLTemplate("<root></root>");
        }

        private static MethodCall getFromContext(ActivityContext cx, String key) {
            assert !key.isEmpty();
            return new MethodCall(cx.contextVarRef(), "get", List.of(new StringConstant(key)));
        }

        private static Statement addToContext(ActivityContext cx, BallerinaModel.Expression value, String key) {
            assert !key.isEmpty();
            String addToContextFn = cx.getAddToContextFn();
            return new CallStatement(new FunctionCall(addToContextFn,
                    List.of(cx.contextVarRef(), new StringConstant(key), value)));
        }

        private record ActivityExtensionConfigConversion(VariableReference result, List<Statement> body) {

        }

    static final class TibcoFileConfigConstants {
        private TibcoFileConfigConstants() {

        }

        public static final String TEXT_CONTENT_FIELD_NAME = "textContent";
        public static final String FILE_NAME_FIELD_NAME = "fileName";
    }

    static final class XSLTConstants {
        static final String XSLT_TRANSFORM_FUNCTION = "xslt:transform";

        private XSLTConstants() {

        }
    }

    static final class LogConstants {
        static final String LOG_INFO_FUNCTION = "log:printInfo";

        private LogConstants() {

        }
    }

    static final class IOConstants {
        static final String FILE_WRITE_FUNCTION = "io:fileWriteString";
        static final String FILE_READ_FUNCTION = "io:fileReadString";

        private IOConstants() {

        }
    }

    static final class XMLDataConstants {
        static final String X_PATH_FUNCTION = "xmldata:transform";

        private XMLDataConstants() {

        }
    }
}
