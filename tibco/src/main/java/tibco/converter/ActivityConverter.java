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
import common.BallerinaModel.Action.RemoteMethodCallAction;
import common.BallerinaModel.Expression.Check;
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
import common.BallerinaModel.Statement.VarDeclStatment;
import common.BallerinaModel.TypeDesc.StreamTypeDesc;
import common.BallerinaModel.TypeDesc.UnionTypeDesc;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Element;
import tibco.analyzer.AnalysisResult;
import tibco.model.PartnerLink;
import tibco.model.Process5;
import tibco.model.Process5.ExplicitTransitionGroup.InlineActivity;
import tibco.model.Resource;
import tibco.model.Scope.Flow.Activity;
import tibco.model.Scope.Flow.Activity.ActivityExtension;
import tibco.model.Scope.Flow.Activity.ActivityExtension.Config.JsonOperation;
import tibco.model.Scope.Flow.Activity.CatchAll;
import tibco.model.Scope.Flow.Activity.Empty;
import tibco.model.Scope.Flow.Activity.ExtActivity;
import tibco.model.Scope.Flow.Activity.InputBinding;
import tibco.model.Scope.Flow.Activity.Invoke;
import tibco.model.Scope.Flow.Activity.Pick;
import tibco.model.Scope.Flow.Activity.ReceiveEvent;
import tibco.model.Scope.Flow.Activity.Reply;
import tibco.model.Scope.Flow.Activity.Throw;
import tibco.model.Scope.Flow.Activity.UnhandledActivity;
import tibco.model.ValueSource;
import tibco.model.XSD;
import tibco.xslt.AddMissingParameters;
import tibco.xslt.IgnoreRootWrapper;
import tibco.xslt.ReplaceDotAccessWithXPath;
import tibco.xslt.ReplaceVariableReference;
import tibco.xslt.TransformPipeline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;

import static common.BallerinaModel.TypeDesc.BuiltinType.DECIMAL;
import static common.BallerinaModel.TypeDesc.BuiltinType.ERROR;
import static common.BallerinaModel.TypeDesc.BuiltinType.INT;
import static common.BallerinaModel.TypeDesc.BuiltinType.JSON;
import static common.BallerinaModel.TypeDesc.BuiltinType.NIL;
import static common.BallerinaModel.TypeDesc.BuiltinType.STRING;
import static common.BallerinaModel.TypeDesc.BuiltinType.XML;
import static common.ConversionUtils.exprFrom;
import static common.ConversionUtils.stmtFrom;
import static common.ConversionUtils.typeFrom;
import static common.LoggingUtils.Level.SEVERE;
import static common.LoggingUtils.Level.WARN;
import static tibco.model.Process5.ExplicitTransitionGroup.InlineActivity.ListFilesActivity.Mode.FILES_AND_DIRECTORIES;
import static tibco.converter.BallerinaSQLConstants.PARAMETERIZED_QUERY_TYPE;

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

    public static Optional<BallerinaModel.Function> convertActivity(ProcessContext cx, Activity activity) {
        try {
            return Optional.of(convertActivity(new ActivityContext(cx, activity), activity));
        } catch (Exception e) {
            cx.log(SEVERE, "Cascading activity conversion failure for activity: " + activity.toString());
            return Optional.empty();
        }
    }

    private static BallerinaModel.Function convertActivity(ActivityContext cx, Activity activity) {
        List<Statement> body;
        try {
            body = tryConvertActivityBody(cx, activity);
            boolean isUnhandled = activity instanceof UnhandledActivity ||
                    activity instanceof InlineActivity.UnhandledInlineActivity;
            boolean hasComment = body.stream().anyMatch(stmt -> stmt instanceof Comment);
            if (!isUnhandled && hasComment) {
                    cx.registerPartiallySupportedActivity(activity);
            }
        } catch (Exception e) {
            cx.registerUnhandledActivity(activity, e);
            List<Activity.Source> sources = activity instanceof Activity.ActivityWithSources activityWithSources
                    ? activityWithSources.sources()
                    : List.of();
            Collection<Activity.Target> targets =
                    activity instanceof Activity.ActivityWithTargets activityWithTargets
                            ? activityWithTargets.targets()
                            : List.of();
            UnhandledActivity unhandledActivity = new UnhandledActivity(
                    "Failed to codegen activity due to %s".formatted(e.getMessage()),
                    sources, targets, activity.element(), activity.fileName());
            body = convertUnhandledActivity(cx, unhandledActivity);
        }
        BallerinaModel.TypeDesc.FunctionTypeDesc activityFnType = ConversionUtils.activityFnType(cx.processContext);
        return new BallerinaModel.Function(cx.functionName(), activityFnType.parameters(), activityFnType.returnType(),
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
            case InlineActivity inlineActivity -> convertInlineActivity(cx, inlineActivity);
        };
    }

    private static @NotNull List<Statement> convertInlineActivity(ActivityContext cx, InlineActivity inlineActivity) {
        List<Statement> body = new ArrayList<>();
        BallerinaModel.Expression startingValue = getStartingValue(cx, inlineActivity);
        VarDeclStatment inputDecl = new VarDeclStatment(XML, cx.getAnnonVarName(), startingValue);
        body.add(inputDecl);
        VariableReference result;
        if (inlineActivity.hasInputBinding()) {
            InputBindingResult inputBindings = convertInputBindings(cx, inputDecl.ref(),
                    List.of(inlineActivity.inputBinding()));
            body.addAll(inputBindings.statements());
            result = inputBindings.resultRef();
        } else {
            result = inputDecl.ref();
        }
        ActivityConversionResult conversion = switch (inlineActivity) {
            case InlineActivity.HttpEventSource ignored -> emptyExtensionConversion(cx, result);
            case InlineActivity.FileEventSource fileEventSource -> convertFileEventSource(cx, result, fileEventSource);
            case InlineActivity.MapperActivity ignored -> emptyExtensionConversion(cx, result);
            case InlineActivity.UnhandledInlineActivity unhandledInlineActivity ->
                    convertUnhandledActivity(cx, result, unhandledInlineActivity);
            case InlineActivity.AssignActivity assignActivity -> convertAssignActivity(cx, result, assignActivity);
            case InlineActivity.NullActivity ignored -> emptyExtensionConversion(cx, result);
            case InlineActivity.HTTPResponse httpResponse -> convertHttpResponse(cx, result, httpResponse);
            case InlineActivity.WriteLog writeLog -> convertWriteLogActivity(cx, result, writeLog);
            case InlineActivity.CallProcess callProcess -> convertCallProcess(cx, result, callProcess);
            case InlineActivity.FileWrite fileWrite -> convertFileWrite(cx, result, fileWrite);
            case InlineActivity.FileRead fileRead -> convertFileRead(cx, result, fileRead);
            case InlineActivity.XMLRenderActivity xmlRenderActivity ->
                    convertXmlRenderActivity(cx, result, xmlRenderActivity);
            case InlineActivity.XMLParseActivity xmlParseActivity ->
                    convertXmlParseActivity(cx, result, xmlParseActivity);
            case InlineActivity.XMLTransformActivity xmlTransformActivity ->
                    convertXmlTransformActivity(cx, result, xmlTransformActivity);
            case InlineActivity.SOAPSendReceive soapSendReceive -> convertSoapSendReceive(cx, result, soapSendReceive);
            case InlineActivity.SOAPSendReply soapSendReply -> convertSoapSendReply(cx, result, soapSendReply);
            case Process5.ExplicitTransitionGroup.NestedGroup.LoopGroup loopGroup ->
                    convertLoopGroup(cx, result, loopGroup);
            case InlineActivity.REST rest -> convertREST(cx, result, rest);
            case InlineActivity.Catch ignored -> emptyExtensionConversion(cx, result);
            case InlineActivity.JSONParser jsonParser -> convertJsonParser(cx, result, jsonParser);
            case InlineActivity.JSONRender jsonRender -> convertJsonRender(cx, result, jsonRender);
            case InlineActivity.JDBC jdbc -> convertJDBC(cx, result, jdbc);
            case InlineActivity.JMSQueueEventSource jmsEventSource -> convertJMSEventSource(cx, result, jmsEventSource);
            case InlineActivity.JMSQueueSendActivity jmsQueueSendActivity ->
                    convertJMSQueueSendActivity(cx, result, jmsQueueSendActivity);
            case InlineActivity.JMSQueueGetMessageActivity jmsQueueGetMessageActivity ->
                    convertJMSQueueGetActivity(cx, result, jmsQueueGetMessageActivity);
            case InlineActivity.JMSTopicPublishActivity jmsTopicPublishActivity ->
                    convertJMSTopicPublishActivity(cx, result, jmsTopicPublishActivity);
            case InlineActivity.Sleep sleep -> convertSleep(cx, result, sleep);
            case InlineActivity.GetSharedVariable getSharedVariable ->
                    convertGetSharedVariable(cx, result, getSharedVariable);
            case InlineActivity.SetSharedVariable setSharedVariable ->
                    convertSetSharedVariable(cx, result, setSharedVariable);
            case InlineActivity.OnStartupEventSource ignored -> emptyExtensionConversion(cx, result);
            case InlineActivity.ListFilesActivity listFilesActivity ->
                    convertListFilesActivity(cx, result, listFilesActivity);
            case InlineActivity.GenerateError generateError -> convertGenerateError(cx, result, generateError);
        };
        body.addAll(conversion.body());
        body.add(addToContext(cx, conversion.result(), inlineActivity.name()));
        if (inlineActivity instanceof InlineActivity.GenerateError) {
            body.add(new Statement.Return<>(new FunctionCall("error",
                    List.of(new StringConstant("Error generated by %s".formatted(inlineActivity.name()))))));
        }
        return body;
    }

    private static ActivityConversionResult convertListFilesActivity(
            ActivityContext cx, VariableReference input, InlineActivity.ListFilesActivity listFilesActivity) {
        List<Statement> body = new ArrayList<>();
        cx.log(WARN, "ListFilesActivity: only fileName and fullName are supported in output.");
        body.add(new Comment("WARNING: Only fileName and fullName are supported in ListFilesActivity output."));
        VarDeclStatment fileName = new VarDeclStatment(STRING, cx.getAnnonVarName(),
                exprFrom("(%s/**/<fileName>/*).toString().trim()".formatted(input.varName())));
        body.add(fileName);
        String filesInPath = cx.getFilesInPathFunction();
        BallerinaModel.TypeDesc.TypeReference fileDataTy = ConversionUtils.Constants.FILE_DATA;
        VarDeclStatment files = new VarDeclStatment(new BallerinaModel.TypeDesc.ArrayTypeDesc(fileDataTy),
                cx.getAnnonVarName(),
                new Check(new FunctionCall(filesInPath, List.of(fileName.ref(),
                        new BallerinaModel.Expression.BooleanConstant(
                                listFilesActivity.mode() == FILES_AND_DIRECTORIES)))));
        body.add(files);
        VarDeclStatment resultBody = new VarDeclStatment(XML, cx.getAnnonVarName(), new XMLTemplate(""));
        body.add(resultBody);
        body.add(common.ConversionUtils.stmtFrom("""
                foreach %s file in %s {
                    %s += xml `<fileInfo>
                                    <fileName>${file.fileName}</fileName>
                                    <fullName>${file.fullName}</fullName>
                               </fileInfo>`;
                }
                """.formatted(fileDataTy, files.ref(), resultBody.ref())));
        VarDeclStatment result = new VarDeclStatment(XML, cx.getAnnonVarName(),
                new XMLTemplate("""
                        <root>
                            <ListFilesActivityOutput xmlns="http://www.tibco.com/namespaces/tnt/plugins/file">
                                <files>${%s}</files>
                            </ListFilesActivityOutput>
                        </root>""".formatted(resultBody.ref())));
        body.add(result);
        return new ActivityConversionResult(result.ref(), body);
    }

    private static BallerinaModel.@NotNull Expression getStartingValue(ActivityContext cx,
                                                                       InlineActivity inlineActivity) {
        return switch (inlineActivity) {
            case InlineActivity.JMSQueueEventSource ignored -> getFromContext(cx, "jms");
            case InlineActivity.FileEventSource ignored -> getFromContext(cx, "file");
            default -> defaultEmptyXml();
        };
    }

    private static ActivityConversionResult convertFileEventSource(ActivityContext cx, VariableReference input,
                                                                   InlineActivity.FileEventSource fileEventSource) {
        XMLTemplate fileTemplate = new XMLTemplate(
                """
                        <root>
                            <EventSourceOutputNoContentClass xmlns="http://www.tibco.com/namespaces/tnt/plugins/file">
                                 ${%s}
                            </EventSourceOutputNoContentClass>
                        </root>
                        """.formatted(input));
        VarDeclStatment output = new VarDeclStatment(XML, cx.getAnnonVarName(), fileTemplate);
        return new ActivityConversionResult(output.ref(), List.of(output));
    }

    private static ActivityConversionResult convertSetSharedVariable(
            ActivityContext cx, VariableReference input, InlineActivity.SetSharedVariable setSharedVariable) {
        String setSharedVariableFn = cx.processContext.getSetSharedVariableFn();
        Optional<Resource.SharedVariable> sharedVariable =
                cx.getSharedVariableByRelativePath(setSharedVariable.variableConfig());
        String sharedVariableName;
        List<Statement> body = new ArrayList<>();
        if (sharedVariable.isPresent()) {
            sharedVariableName = sharedVariable.get().name();
        } else {
            String message =
                    "Failed to find shared variable for: %s using a placeholder".formatted(setSharedVariable.name());
            body.add(new Comment(message));
            cx.log(SEVERE, message);
            sharedVariableName = "sharedVariable_" + ConversionUtils.sanitizes(setSharedVariable.name());
        }
        body.add(new CallStatement(
                new FunctionCall(setSharedVariableFn,
                        List.of(cx.contextVarRef(), new StringConstant(sharedVariableName), input))));
        return new ActivityConversionResult(input, body);
    }

    private static ActivityConversionResult convertGetSharedVariable(
            ActivityContext cx, VariableReference input, InlineActivity.GetSharedVariable getSharedVariable) {
        String getSharedVariableFn = cx.processContext.getGetSharedVariableFn();
        Optional<Resource.SharedVariable> sharedVariable =
                cx.getSharedVariableByRelativePath(getSharedVariable.variableConfig());
        String sharedVariableName;
        List<Statement> body = new ArrayList<>();
        if (sharedVariable.isPresent()) {
            sharedVariableName = sharedVariable.get().name();
        } else {
            String message =
                    "Failed to find shared variable for: %s using a placeholder".formatted(getSharedVariable.name());

            body.add(new Comment(message));
            cx.log(SEVERE, message);
            sharedVariableName = "sharedVariable_" + ConversionUtils.sanitizes(getSharedVariable.name());
        }
        VarDeclStatment value = new VarDeclStatment(XML, cx.getAnnonVarName(),
                new FunctionCall(getSharedVariableFn,
                        List.of(cx.contextVarRef(), new StringConstant(sharedVariableName))));
        body.add(value);
        return new ActivityConversionResult(value.ref(), body);
    }

    private static void checkAndLogSessionAttributesWarnings(ActivityContext cx,
                    InlineActivity.JMSActivityBase.SessionAttributes sessionAttributes, InlineActivity activity) {
            List<String> noneValues = new ArrayList<>();

            if (sessionAttributes.transacted().isEmpty()) {
                    noneValues.add("transacted");
            }
            if (sessionAttributes.acknowledgeMode().isEmpty()) {
                    noneValues.add("acknowledgeMode");
            }
            if (sessionAttributes.maxSessions().isEmpty()) {
                    noneValues.add("maxSessions");
            }
            if (sessionAttributes.destination().isEmpty()) {
                    noneValues.add("destination");
            }

            if (!noneValues.isEmpty()) {
                    String warningMessage = String.format(
                                    "WARNING: JMS Activity '%s' has unsupported values for SessionAttributes: %s. Most likely they were TIBCO configuration values",
                                    activity.name(), String.join(", ", noneValues));
                    cx.log(WARN, warningMessage);
                    cx.registerPartiallySupportedActivity(activity);
            }
    }

    private static ActivityConversionResult convertJMSQueueGetActivity(
            ActivityContext cx, VariableReference input,
            InlineActivity.JMSQueueGetMessageActivity jmsQueueGetMessageActivity) {
        cx.addLibraryImport(Library.JMS);
        List<Statement> body = new ArrayList<>();
        checkAndLogSessionAttributesWarnings(cx, jmsQueueGetMessageActivity.sessionAttributes(),
                        jmsQueueGetMessageActivity);

        JMSConnectionData jmsConnectionData =
                JMSConnectionData.from(cx, cx.getJmsResource(jmsQueueGetMessageActivity.connectionReference()));
        body.add(jmsConnectionData.connection);
        body.add(jmsConnectionData.session);
        body.add(new Comment("WARNING: using default destination configuration"));
        VarDeclStatment consumer = new VarDeclStatment(ConversionUtils.Constants.JMS_MESSAGE_CONSUMER,
                cx.getAnnonVarName(),
                new Check(new MethodCall(jmsConnectionData.session().ref(), "createConsumer",
                        List.of(common.ConversionUtils.exprFrom("""
                                destination = {
                                    'type: jms:QUEUE,
                                    name: "Default queue"
                                }
                                """)))));
        body.add(consumer);
        VarDeclStatment msg =
                new VarDeclStatment(UnionTypeDesc.of(NIL, ConversionUtils.Constants.JMS_MESSAGE), cx.getAnnonVarName(),
                        new Check(new RemoteMethodCallAction(consumer.ref(), "receive", List.of())));
        body.add(msg);
        body.add(Statement.IfElseStatement.ifStatement(
                exprFrom("%s !is %s".formatted(msg.ref(), ConversionUtils.Constants.JMS_TEXT_MESSAGE)),
                List.of(new Statement.Return<>(
                        new FunctionCall("error", List.of(new StringConstant("Unexpected msg type")))))));
        VarDeclStatment contentString = new VarDeclStatment(STRING, cx.getAnnonVarName(),
                new BallerinaModel.Expression.FieldAccess(msg.ref(), "content"));
        body.add(contentString);
        VarDeclStatment result = new VarDeclStatment(XML, cx.getAnnonVarName(), new XMLTemplate(
                """
                           <root>
                               <ActivityOutput xmlns="http://www.tibco.com/namespaces/tnt/plugins/jms">
                                    <Body>
                                        ${%s}
                                    </Body>
                               </ActivityOutput>
                           </root>
                        """.formatted(contentString.ref())
        ));
        body.add(result);
        return new ActivityConversionResult(result.ref(), body);
    }

    private static ActivityConversionResult convertJMSQueueSendActivity(
            ActivityContext cx, VariableReference input, InlineActivity.JMSQueueSendActivity jmsQueueSendActivity) {
        cx.addLibraryImport(Library.JMS);
        List<Statement> body = new ArrayList<>();

        checkAndLogSessionAttributesWarnings(cx, jmsQueueSendActivity.sessionAttributes(), jmsQueueSendActivity);

        JMSConnectionData jmsConnectionData =
                JMSConnectionData.from(cx, cx.getJmsResource(jmsQueueSendActivity.connectionReference()));
        body.add(jmsConnectionData.connection);
        body.add(jmsConnectionData.session);
        if (!jmsQueueSendActivity.permittedMessageType().equalsIgnoreCase("Text")) {
            body.add(new Comment(
                    "WARNING: Unexpected message type: %s Only Text messages are supported in JMSQueueSendActivity"
                            .formatted(jmsQueueSendActivity.permittedMessageType())));
        }
        body.add(new Comment("WARNING: using default destination configuration"));
        VarDeclStatment producer = new VarDeclStatment(ConversionUtils.Constants.JMS_MESSAGE_PRODUCER,
                cx.getAnnonVarName(),
                new Check(new MethodCall(jmsConnectionData.session().ref(), "createProducer", List.of())));
        body.add(producer);

        VarDeclStatment contentString = new VarDeclStatment(STRING, cx.getAnnonVarName(),
                exprFrom("(%s/**/<Body>/*).toString().trim()".formatted(input.varName())));
        body.add(contentString);

        VarDeclStatment msg = new VarDeclStatment(ConversionUtils.Constants.JMS_TEXT_MESSAGE, cx.getAnnonVarName(),
                exprFrom("{ content: %s }".formatted(contentString.ref())));
        body.add(msg);

        body.add(new CallStatement(new Check(new RemoteMethodCallAction(producer.ref(), "send", List.of(msg.ref())))));
        VarDeclStatment emptyResult = new VarDeclStatment(XML, cx.getAnnonVarName(), defaultEmptyXml());
        body.add(emptyResult);
        return new ActivityConversionResult(emptyResult.ref(), body);
}

private static ActivityConversionResult convertJMSTopicPublishActivity(
                ActivityContext cx, VariableReference input,
                InlineActivity.JMSTopicPublishActivity jmsTopicPublishActivity) {
        cx.addLibraryImport(Library.JMS);
        List<Statement> body = new ArrayList<>();

        checkAndLogSessionAttributesWarnings(cx, jmsTopicPublishActivity.sessionAttributes(), jmsTopicPublishActivity);

        JMSConnectionData jmsConnectionData = JMSConnectionData.from(cx,
                        cx.getJmsResource(jmsTopicPublishActivity.connectionReference()));
        body.add(jmsConnectionData.connection);
        body.add(jmsConnectionData.session);
        body.add(new Comment("WARNING: using default destination configuration"));

        // Use destination from SessionAttributes if available, otherwise use default
        String destinationName = jmsTopicPublishActivity.sessionAttributes().destination().orElse("Default topic");
        VarDeclStatment producer = new VarDeclStatment(ConversionUtils.Constants.JMS_MESSAGE_PRODUCER,
                        cx.getAnnonVarName(),
                        new Check(new MethodCall(jmsConnectionData.session().ref(), "createProducer", List.of(
                                        exprFrom("""
                                                        destination = {
                                                            'type: jms:TOPIC,
                                                            name: "%s"
                                                        }
                                                        """.formatted(destinationName))))));
        body.add(producer);

        VarDeclStatment contentString = new VarDeclStatment(STRING, cx.getAnnonVarName(),
                        exprFrom("(%s/**/<Body>/*).toString().trim()".formatted(input.varName())));
        body.add(contentString);

        VarDeclStatment msg = new VarDeclStatment(ConversionUtils.Constants.JMS_TEXT_MESSAGE, cx.getAnnonVarName(),
                        exprFrom("{ content: %s }".formatted(contentString.ref())));
        body.add(msg);

        body.add(new CallStatement(new Check(new RemoteMethodCallAction(producer.ref(), "send", List.of(msg.ref())))));
        VarDeclStatment emptyResult = new VarDeclStatment(XML, cx.getAnnonVarName(), defaultEmptyXml());
        body.add(emptyResult);
        return new ActivityConversionResult(emptyResult.ref(), body);
    }

    record JMSConnectionData(VarDeclStatment connection, VarDeclStatment session) {

        static JMSConnectionData from(ActivityContext cx, Resource.JMSSharedResource jmsSharedResource) {
            StringBuilder sb = new StringBuilder();
            String initialContextFactory = jmsSharedResource.namingEnvironment()
                    .namingInitialContextFactory();
            sb.append("initialContextFactory = \"").append(initialContextFactory).append("\",");
            String providerUrl = jmsSharedResource.namingEnvironment().providerURL();
            sb.append("providerUrl = \"").append(providerUrl).append("\"");
            jmsSharedResource.connectionAttributes().username().ifPresent(userName -> {
                if (sb.charAt(sb.length() - 1) != ',') {
                    sb.append(",");
                }
                sb.append("username = \"").append(userName).append("\"");
            });
            jmsSharedResource.connectionAttributes().password().ifPresent(password -> {
                if (sb.charAt(sb.length() - 1) != ',') {
                    sb.append(",");
                }
                sb.append("password = \"").append(password).append("\"");
            });
            VarDeclStatment connection =
                    new VarDeclStatment(ConversionUtils.Constants.JMS_CONNECTION, cx.getAnnonVarName(),
                            new Check(exprFrom("new (" + sb + ")")));

            VarDeclStatment session = new VarDeclStatment(ConversionUtils.Constants.JMS_SESSION,
                    cx.getAnnonVarName(),
                    new Check(new RemoteMethodCallAction(connection.ref(), "createSession", List.of())));

            return new JMSConnectionData(connection, session);
        }
    }

    private static ActivityConversionResult convertJMSEventSource(
            ActivityContext cx, VariableReference input, InlineActivity.JMSQueueEventSource jmsEventSource) {
        XMLTemplate jmsTemplate = new XMLTemplate(
                """
                           <root>
                               <ActivityOutput xmlns="http://www.tibco.com/namespaces/tnt/plugins/jms">
                                    <Body>
                                        ${%s}
                                    </Body>
                               </ActivityOutput>
                           </root>
                        """.formatted(input)
        );
        VarDeclStatment jmsOutput = new VarDeclStatment(XML, cx.getAnnonVarName(), jmsTemplate);
        return new ActivityConversionResult(jmsOutput.ref(), List.of(jmsOutput));
    }

    private static ActivityConversionResult convertSleep(
            ActivityContext cx, VariableReference input, InlineActivity.Sleep sleep) {
        List<Statement> body = new ArrayList<>();

        // Extract the interval value from the input XML
        VarDeclStatment intervalInMillisec = new VarDeclStatment(DECIMAL, cx.getAnnonVarName(),
                new Check(new FunctionCall("decimal:fromString", List.of(
                        exprFrom("(%s/**/<IntervalInMillisec>/*).toString().trim()"
                                .formatted(input.varName()))))));
        body.add(intervalInMillisec);

        // Call runtime:sleep with the interval converted to seconds
        cx.addLibraryImport(Library.RUNTIME);
        body.add(new CallStatement(new FunctionCall("runtime:sleep", List.of(
                exprFrom("%s / 1000".formatted(intervalInMillisec.ref()))))));
        VarDeclStatment empty = new VarDeclStatment(XML, cx.getAnnonVarName(), defaultEmptyXml());
        body.add(empty);
        return new ActivityConversionResult(empty.ref(), body);
    }

    private static ActivityConversionResult convertGenerateError(
            ActivityContext cx, VariableReference input, InlineActivity.GenerateError generateError) {
        List<Statement> body = new ArrayList<>();
        cx.addLibraryImport(Library.RUNTIME);
        VarDeclStatment stackTrace = new VarDeclStatment(STRING, cx.getAnnonVarName(),
                new MethodCall(new FunctionCall("runtime:getStackTrace", List.of()), "toString", List.of()));
        body.add(stackTrace);

        VarDeclStatment result = new VarDeclStatment(XML, cx.getAnnonVarName(),
                new XMLTemplate(
                        """
                                <ns:ErrorReport xmlns:ns="http://www.tibco.com/pe/EngineTypes">
                                    <StackTrace>${stackTrace}</StackTrace>
                                    ${%s/*}
                                </ns:ErrorReport>""".formatted(
                                input)));
        body.add(result);

        // Add the result to context with $_error key only
        body.add(addToContext(cx, result.ref(), "$_error"));

        return new ActivityConversionResult(result.ref(), body);
    }

    private static ActivityConversionResult convertJDBC(
            ActivityContext cx, VariableReference input, InlineActivity.JDBC jdbc) {
        List<Statement> body = new ArrayList<>();
        VarDeclStatment statement = new VarDeclStatment(STRING, cx.getAnnonVarName(),
                exprFrom("(%s/**/<statement>/*).toString().trim()".formatted(input.varName())));
        body.add(statement);
        VarDeclStatment query = new VarDeclStatment(cx.processContext.getTypeByName(PARAMETERIZED_QUERY_TYPE),
                cx.getAnnonVarName(), exprFrom("``"));
        body.add(query);
        body.add(common.ConversionUtils.stmtFrom("%s.strings = [%s];".formatted(query.ref(), statement.ref())));
        VariableReference client = cx.dbClient(jdbc.connection());
        VarDeclStatment result = new VarDeclStatment(XML, cx.getAnnonVarName());
        body.add(result);

        List<Statement> ifSelectBody = new ArrayList<>();
        var ifSelectResult = finishSelectQuery(cx, client, query.ref(), ifSelectBody);
        ifSelectBody.add(new Statement.VarAssignStatement(result.ref(), ifSelectResult.result()));

        List<Statement> queryBody = new ArrayList<>();
        var queryResult = finishSQLQuery(cx, client, query.ref(), queryBody);
        queryBody.add(new Statement.VarAssignStatement(result.ref(), queryResult.result()));

        body.add(new Statement.IfElseStatement(
                exprFrom("%s.startsWith(\"SELECT\")".formatted(statement.ref())), ifSelectBody, List.of(), queryBody));
        body.add(new Comment("WARNING: validate jdbc query result mapping"));
        return new ActivityConversionResult(result.ref(), body);
    }

    private static ActivityConversionResult convertJsonRender(
            ActivityContext cx, VariableReference input, InlineActivity.JSONRender jsonRender) {
        List<Statement> body = new ArrayList<>();
        VarDeclStatment xmlInput = new VarDeclStatment(XML, cx.getAnnonVarName(),
                exprFrom("(%s/*)".formatted(input.varName())));
        body.add(xmlInput);
        body.add(stmtFrom("xmlns \"http://www.tibco.com/namespaces/tnt/plugins/json\" as ns;"));
        cx.log(WARN, "JSONRender: assuming single element");
        BallerinaModel.TypeDesc targetType = jsonRender.targetType().map(ConversionUtils::toTypeDesc).orElseGet(
                () -> new BallerinaModel.TypeDesc.MapTypeDesc(JSON));
        return finishConvertJsonRender(cx, body, targetType, "ns:ActivityOutputClass", xmlInput.ref());
    }

    private static @NotNull ActivityConversionResult finishConvertJsonRender(ActivityContext cx,
                                                                             List<Statement> body,
                                                                             BallerinaModel.TypeDesc targetType,
                                                                             String outerTag,
                                                                             VariableReference input) {
        body.add(new Comment("WARNING: assuming single element"));
        cx.addLibraryImport(Library.XML_DATA);
        String parseAsTypeFn = XMLDataConstants.PARSE_AS_TYPE;
        VarDeclStatment value = new VarDeclStatment(targetType, cx.getAnnonVarName(),
                new Check(new FunctionCall(parseAsTypeFn, List.of(input))));
        body.add(value);

        VarDeclStatment jsonStringContent = new VarDeclStatment(STRING, cx.getAnnonVarName(),
                new MethodCall(value.ref(), "toJsonString", List.of()));
        body.add(jsonStringContent);

        VarDeclStatment jsonString = new VarDeclStatment(XML, cx.getAnnonVarName(),
                new XMLTemplate("<jsonString>${%s}</jsonString>".formatted(jsonStringContent.ref())));
        body.add(jsonString);

        var intermediateResult = new ActivityConversionResult(jsonString.ref(), body);

        VarDeclStatment result = new VarDeclStatment(XML, cx.getAnnonVarName(), new XMLTemplate(
                "<%s>${%s}</%s>"
                        .formatted(outerTag, intermediateResult.result, outerTag)));
        body.add(result);
        return new ActivityConversionResult(result.ref(), body);
    }

    private static ActivityConversionResult convertJsonParser(
            ActivityContext cx, VariableReference input, InlineActivity.JSONParser jsonParser) {
        List<Statement> body = new ArrayList<>();

        String targetTypeName = jsonParser.targetType()
                .map(xsd -> xsd.type().name())
                .orElseGet(() -> new BallerinaModel.TypeDesc.MapTypeDesc(JSON).toString());
        try {
            if (jsonParser.targetType().isPresent()) {
                cx.addXSDSchemaToConversion(jsonParser.targetType().get().toSchema());
            }
        } catch (ParserConfigurationException e) {
            cx.log(SEVERE, "Error converting Element to String: " + e.getMessage()
                        + ". Continuing with conversion.");
        }
        body.add(stmtFrom("xmlns \"http://www.tibco.com/namespaces/tnt/plugins/json\" as ns;"));
        return finishConvertJsonParser(cx, input, targetTypeName, "ActivityOutputClass", body);
    }

    private static @NotNull ActivityConversionResult finishConvertJsonParser(
            ActivityContext cx, VariableReference input, String targetTypeName, String outerTag, List<Statement> body) {
        var intermediateResult = finishConvertJsonParser(cx, input, targetTypeName, body, true);
        VarDeclStatment result = new VarDeclStatment(XML, cx.getAnnonVarName(),
                new XMLTemplate("<%s>%s</%s>".formatted(outerTag, intermediateResult.result, outerTag)));
        body.add(result);
        VarDeclStatment wrappedResult = wrapWithRoot(cx, result.ref());
        body.add(wrappedResult);
        return new ActivityConversionResult(wrappedResult.ref(), body);
    }

    private static @NotNull ActivityConversionResult finishConvertJsonParser(
            ActivityContext cx, VariableReference input, String targetTypeName, List<Statement> body,
            boolean noWrapper) {
        String renderFn = cx.getRenderJsonAsXMLFunction(targetTypeName);
        VarDeclStatment result = new VarDeclStatment(XML, cx.getAnnonVarName(),
                new Check(new FunctionCall(renderFn, List.of(input))));
        body.add(result);
        if (noWrapper) {
            return new ActivityConversionResult(result.ref(), body);
        }
        VarDeclStatment wrappedResult = wrapWithRoot(cx, result.ref());
        body.add(wrappedResult);
        return new ActivityConversionResult(wrappedResult.ref(), body);
    }

    private static ActivityConversionResult convertSoapSendReply(
            ActivityContext cx, VariableReference result, InlineActivity.SOAPSendReply soapSendReply) {
        VarDeclStatment envelop = new VarDeclStatment(XML, cx.getAnnonVarName(),
                new XMLTemplate(ConversionUtils.createSoapEnvelope(result)));
        VarDeclStatment wrappedEnvelop = wrapWithRoot(cx, envelop.ref());
        return new ActivityConversionResult(wrappedEnvelop.ref(), List.of(envelop, wrappedEnvelop));
    }

    private static ActivityConversionResult convertSoapSendReceive(
            ActivityContext cx, VariableReference result, InlineActivity.SOAPSendReceive soapSendReceive) {
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
        VarDeclStatment wrappedResponse = wrapWithRoot(cx, res.ref());
        body.add(wrappedResponse);

        return new ActivityConversionResult(wrappedResponse.ref(), body);
    }

    private static Collection<Statement> initSoapClient(ActivityContext cx,
                                                        InlineActivity.SOAPSendReceive soapSendReceive,
                                                        String clientName) {
        cx.addLibraryImport(Library.SOAP);
        return List.of(stmtFrom("soap11:Client %s = check new (\"%s\");"
                .formatted(clientName, soapSendReceive.endpointURL())));
    }

    private static ActivityConversionResult convertLoopGroup(
            ActivityContext cx, VariableReference result,
            Process5.ExplicitTransitionGroup.NestedGroup.LoopGroup loopGroup) {
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
        return new ActivityConversionResult(resultValue.ref(), body);
    }

    private static Statement loopBody(ActivityContext cx,
                                      Process5.ExplicitTransitionGroup.NestedGroup.LoopGroup loop,
                                      VariableReference loopSequence,
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
        AnalysisResult analysisResult = cx.processContext.getAnalysisResult();
        String scopeFn = analysisResult.getControlFlowFunctions(loop.body()).scopeFn();
        sb.append(new CallStatement(new FunctionCall(scopeFn, List.of(cx.contextVarRef()))));
        // TODO: need a cleaner way to do this
        VarDeclStatment resultDecl = new VarDeclStatment(XML, "result",
                        ConversionUtils.getXMLResultFromContext(cx.contextVarRef()));
        sb.append(resultDecl);
        VariableReference res = resultDecl.ref();
        BallerinaModel.Expression resultUpdate;
        if (loop.accumulateOutput()) {
            resultUpdate = exprFrom("%s + %s".formatted(result, res));
        } else {
            resultUpdate = res;
        }
        sb.append(new Statement.VarAssignStatement(result, resultUpdate));
        sb.append("}");
        return stmtFrom(sb.toString());
    }

    private static ActivityConversionResult convertREST(
            ActivityContext cx, VariableReference input, InlineActivity.REST rest) {
        if (rest.method() == InlineActivity.REST.Method.GET) {
            return convertGet(cx, rest);
        }
        List<Statement> body = new ArrayList<>();
        VarDeclStatment requestBody = new VarDeclStatment(XML, cx.getAnnonVarName(),
                exprFrom("%s/**/<Body>".formatted(input.varName())));
        body.add(requestBody);
        String toJsonFn = cx.getToJsonFunction();
        BallerinaModel.TypeDesc.MapTypeDesc jsonMap = new BallerinaModel.TypeDesc.MapTypeDesc(JSON);
        VarDeclStatment json = new VarDeclStatment(jsonMap, cx.getAnnonVarName(),
                new TypeCast(jsonMap, new FunctionCall(toJsonFn, List.of(requestBody.ref()))));
        body.add(json);

        String remoteMethod = switch (rest.method()) {
            case PUT -> "put";
            case GET -> throw new IllegalStateException("Unexpected");
            case DELETE -> "delete";
            case POST -> "post";
        };
        return finishRESTConversion(cx, rest, remoteMethod, body, List.of(
                exprFrom("%s[\"Body\"]".formatted(json.ref()))));
    }

    private static ActivityConversionResult convertGet(ActivityContext cx, InlineActivity.REST rest) {
        List<Statement> body = new ArrayList<>();
        body.add(stmtFrom("xmlns \"http://www.tibco.com/namespaces/tnt/plugins/json\" as ns;"));
        return finishRESTConversion(cx, rest, "get", body, List.of());
    }

    private static @NotNull ActivityConverter.ActivityConversionResult finishRESTConversion(
            ActivityContext cx, InlineActivity.REST rest, String remoteMethod,
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
        VarDeclStatment wrappedResult = wrapWithRoot(cx, result.ref());
        body.add(wrappedResult);
        return new ActivityConversionResult(wrappedResult.ref(), body);
    }

    private static ActivityConversionResult convertCallProcess(
            ActivityContext cx, VariableReference input, InlineActivity.CallProcess callProcess) {
        Optional<VariableReference> processClient = cx.getProcessClient(callProcess.processName());
        VariableReference client;
        List<Statement> body = new ArrayList<>();
        if (processClient.isPresent()) {
            client = processClient.get();
        } else {
            // TODO: properly handle this using package approach
            String message =
                    "Failed to find process client for: %s using a placeholder".formatted(callProcess.processName());
            cx.log(SEVERE, message);
            body.add(new Comment(message));
            client = new VariableReference("processClient_" + ConversionUtils.sanitizes(callProcess.processName()));
        }
        VarDeclStatment request = new VarDeclStatment(XML, cx.getAnnonVarName(),
                exprFrom("%s/*".formatted(input.varName())));
        body.add(request);

        VarDeclStatment returnedValue = new VarDeclStatment(XML, cx.getAnnonVarName(),
                new Check(new RemoteMethodCallAction(client, "post",
                        List.of(new StringConstant(""), request.ref()))));
        body.add(returnedValue);
        VarDeclStatment result = wrapWithRoot(cx, returnedValue.ref());
        body.add(result);
        return new ActivityConversionResult(result.ref(), body);
    }

    private static VarDeclStatment wrapWithRoot(ActivityContext cx, VariableReference input) {
        return new VarDeclStatment(XML, cx.getAnnonVarName(),
                new XMLTemplate("<root>${%s}</root>".formatted(input.varName())));
    }


    private static ActivityConversionResult convertFileRead(
            ActivityContext cx, VariableReference result, InlineActivity.FileRead fileRead) {
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
        return new ActivityConversionResult(wrapped.ref(), body);
    }

    private static ActivityConversionResult convertFileWrite(
            ActivityContext cx, VariableReference input, InlineActivity.FileWrite fileWrite) {
        assert fileWrite.encoding().equals("text");
        boolean append = fileWrite.append();
        return finishFileWrite(cx, input, append);
    }

    private static @NotNull ActivityConversionResult finishFileWrite(ActivityContext cx,
                                                                     VariableReference input,
                                                                     boolean append) {
        List<Statement> body = new ArrayList<>();
        VarDeclStatment fileName = new VarDeclStatment(STRING, "fileName",
                exprFrom("(%s/**/<fileName>/*).toString()".formatted(input.varName())));
        body.add(fileName);
        VarDeclStatment textContent = new VarDeclStatment(STRING, "content",
                exprFrom("(%s/**/<textContent>/*).toString()".formatted(input.varName())));
        body.add(textContent);
        StringConstant mode = append ? new StringConstant("APPEND") : new StringConstant("OVERWRITE");
        cx.addLibraryImport(Library.IO);
        body.add(new CallStatement(new Check(new FunctionCall(IOConstants.FILE_WRITE_FUNCTION,
                List.of(fileName.ref(), textContent.ref(), mode)))));
        return new ActivityConversionResult(input, body);
    }

    private static ActivityConversionResult convertXmlParseActivity(
            ActivityContext cx, VariableReference result, InlineActivity.XMLParseActivity xmlParseActivity) {
        List<Statement> body = new ArrayList<>();

        VariableReference stringRepr;
        if (xmlParseActivity.inputStyle() == InlineActivity.XMLParseActivity.InputStyle.TEXT) {
            VarDeclStatment xmlString = new VarDeclStatment(XML, cx.getAnnonVarName(),
                    exprFrom("%s/<xmlString>/*".formatted(result.varName())));
            body.add(xmlString);
            VarDeclStatment asString = new VarDeclStatment(STRING, cx.getAnnonVarName(),
                    new MethodCall(xmlString.ref(), "toString", List.of()));
            body.add(asString);
            stringRepr = asString.ref();
        } else {
            VarDeclStatment bytes = new VarDeclStatment(XML, cx.getAnnonVarName(),
                    exprFrom("%s/<bytes>/*".formatted(result.varName())));
            body.add(bytes);
            VarDeclStatment byteArr = new VarDeclStatment(new BallerinaModel.TypeDesc.ArrayTypeDesc(
                    BallerinaModel.TypeDesc.BuiltinType.BYTE), cx.getAnnonVarName());
            body.add(byteArr);
            body.add(new Comment(
                    "WARNING: xml parse from bytes detected properly initialize %s using %s".formatted(byteArr.ref(),
                            bytes.ref())));
            VarDeclStatment stringValue = new VarDeclStatment(STRING, cx.getAnnonVarName(),
                    new Check(new FunctionCall("string:fromBytes", List.of(byteArr.ref()))));
            body.add(stringValue);
            stringRepr = stringValue.ref();
        }
        VarDeclStatment xmlValue = new VarDeclStatment(XML, cx.getAnnonVarName(),
                new Check(new FunctionCall("xml:fromString", List.of(stringRepr))));
        body.add(xmlValue);
        VarDeclStatment wrappedValue = new VarDeclStatment(XML, cx.getAnnonVarName(),
                new XMLTemplate("<root>${%s}</root>".formatted(xmlValue.ref())));
        body.add(wrappedValue);
        return new ActivityConversionResult(wrappedValue.ref(), body);
    }

    private static ActivityConversionResult convertXmlRenderActivity(
            ActivityContext cx, VariableReference input, InlineActivity.XMLRenderActivity xmlRenderActivity) {
        return finishXmlRenderActivity(cx, input, "xmlString");
    }

    @NotNull
    private static ActivityConversionResult convertXmlTransformActivity(
            ActivityContext cx, VariableReference input, InlineActivity.XMLTransformActivity xmlTransformActivity) {
        List<Statement> body = new ArrayList<>();
        String xsltContent = xsltTransformer.apply(cx, xmlTransformActivity.styleSheet());
        if (xsltContent.startsWith("FIXME: failed to find xslt file at ")) {
            body.add(new Comment(xsltContent));
            return new ActivityConversionResult(input, body);
        } else {
            cx.addLibraryImport(Library.XSLT);
            VarDeclStatment transformResult = new VarDeclStatment(XML, cx.getAnnonVarName(), new Check(
                    new FunctionCall(XSLTConstants.XSLT_TRANSFORM_FUNCTION,
                            List.of(input, new XMLTemplate(xsltContent),
                                    cx.contextVarRef()))));
            body.add(transformResult);
            return new ActivityConversionResult(transformResult.ref(), body);
        }
    }

    private static @NotNull ActivityConversionResult finishXmlRenderActivity(ActivityContext cx,
                                                                             VariableReference input, String outerTag) {
        List<Statement> body = new ArrayList<>();
        VarDeclStatment stringValue = new VarDeclStatment(STRING, cx.getAnnonVarName(),
                new MethodCall(input, "toBalString", List.of()));
        body.add(stringValue);
        VarDeclStatment res = new VarDeclStatment(XML, cx.getAnnonVarName(),
                new XMLTemplate("<root><%s>${%s}</%s></root>".formatted(outerTag, stringValue.ref(), outerTag)));
        body.add(res);
        return new ActivityConversionResult(res.ref(), body);
    }

    private static @NotNull ActivityConversionResult finishXmlRenderActivity(ActivityContext cx,
                                                                             VariableReference input) {
        List<Statement> body = new ArrayList<>();
        VarDeclStatment stringValue = new VarDeclStatment(STRING, cx.getAnnonVarName(),
                new MethodCall(input, "toBalString", List.of()));
        body.add(stringValue);
        VarDeclStatment res = new VarDeclStatment(XML, cx.getAnnonVarName(),
                new XMLTemplate("<root>${%s}</root>".formatted(stringValue.ref())));
        body.add(res);
        return new ActivityConversionResult(res.ref(), body);
    }

    private static ActivityConversionResult convertWriteLogActivity(
            ActivityContext cx, VariableReference input, InlineActivity.WriteLog writeLog) {
        return finishWriteLogActivity(cx, input);
    }

    private static @NotNull ActivityConversionResult finishWriteLogActivity(ActivityContext cx,
                                                                            VariableReference input) {
        List<Statement> body = new ArrayList<>();
        VarDeclStatment message = new VarDeclStatment(XML, cx.getAnnonVarName(),
                exprFrom("%s/**/<message>/*".formatted(input.varName())));
        body.add(message);
        cx.addLibraryImport(Library.LOG);
        body.add(new CallStatement(new FunctionCall(LogConstants.LOG_INFO_FUNCTION,
                List.of(new MethodCall(message.ref(), "toString", List.of())))));
        return new ActivityConversionResult(message.ref(), body);
    }

    private static ActivityConversionResult convertHttpResponse(
            ActivityContext cx, VariableReference input, InlineActivity.HTTPResponse httpResponse) {
        List<Statement> body = new ArrayList<>();
        VarDeclStatment responseValue = new VarDeclStatment(XML, cx.getAnnonVarName(),
                exprFrom("%s/**/<asciiContent>/*".formatted(input.varName())));
        body.add(responseValue);
        VarDeclStatment result = wrapWithRoot(cx, responseValue.ref());
        body.add(result);
        return new ActivityConversionResult(result.ref(), body);
    }

    private static ActivityConversionResult convertAssignActivity(
            ActivityContext cx, VariableReference result, InlineActivity.AssignActivity assignActivity) {
        List<Statement> body = new ArrayList<>();
        body.add(addToContext(cx, result, assignActivity.variableName()));
        VarDeclStatment assignedValue = new VarDeclStatment(XML, cx.getAnnonVarName(),
                getFromContext(cx, assignActivity.variableName()));
        body.add(assignedValue);
        return new ActivityConversionResult(assignedValue.ref(), body);
    }

    private static ActivityConversionResult convertUnhandledActivity(
            ActivityContext cx, VariableReference result,
            InlineActivity.UnhandledInlineActivity unhandledInlineActivity) {
        List<Statement> body = List.of(
                new Comment("FIXME: Failed to convert rest of activity"),
                elementAsComment(unhandledInlineActivity.element()));
        return new ActivityConversionResult(result, body);
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
        String scopeFn = cx.processContext.getAnalysisResult().getControlFlowFunctions(foreach.scope()).scopeFn();
        body.add(stmtFrom("""
                    foreach int %1$s in %2$s ..< %3$s {
                        %4$s
                        %5$s = %6$s(%7$s);
                    }
                    """.formatted(foreach.counterName(), init, end, contextUpdate, result.ref(), scopeFn,
                cx.contextVarRef())));
        return body;
    }

    private static @NotNull List<Statement> convertAssign(ActivityContext cx, Activity.Assign assign) {
        List<Statement> body = new ArrayList<>();
        ValueSource from = assign.operation().from();
        BallerinaModel.Expression sourceExp = convertValueSource(cx, from, body, XML);
        VarDeclStatment source = new VarDeclStatment(XML, cx.getAnnonVarName(), sourceExp);
        body.add(source);
        body.add(addToContext(cx, source.ref(), assign.operation().to().name()));
        return body;
    }

    private static BallerinaModel.@NotNull Expression convertValueSource(
            ActivityContext cx, ValueSource from, List<Statement> body,
            BallerinaModel.TypeDesc expectedType) {
        return switch (from) {
            case Activity.Expression.XSLT xslt -> {
                VarDeclStatment init = new VarDeclStatment(expectedType, cx.getAnnonVarName(),
                        defaultEmptyXml());
                body.add(init);
                XsltTransformResult transformResult = xsltTransform(cx, init.ref(), xslt);
                addNonStandardXsltWarning(transformResult.nonStandardFunctions(), body);
                yield transformResult.expression();
            }
            case ValueSource.VarRef varRef -> getFromContext(cx, varRef.name());
            case Activity.Expression.XPath xPath -> {
                VarDeclStatment result = new VarDeclStatment(expectedType, cx.getAnnonVarName(),
                        ConversionUtils.xPath(cx.processContext, defaultEmptyXml(),
                                cx.contextVarRef(), xPath));
                body.add(result);
                yield result.ref();
            }
            case ValueSource.Constant constant -> {
                VarDeclStatment result = new VarDeclStatment(expectedType, cx.getAnnonVarName(),
                        exprFrom(constant.value()));
                body.add(result);
                yield result.ref();
            }
        };
    }

    private static List<Statement> convertThrowActivity(ActivityContext cx, Throw throwActivity) {
        List<Statement> body = new ArrayList<>();
        VarDeclStatment inputDecl = new VarDeclStatment(XML, cx.getAnnonVarName(), defaultEmptyXml());
        body.add(inputDecl);
        VariableReference input = inputDecl.ref();
        InputBindingResult inputBindings = convertInputBindings(cx, input, throwActivity.inputBindings());
        body.addAll(inputBindings.statements());
        // TODO: set the body correctly using inputBindings.resultRef() if needed
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
        String scopeFn =
                cx.processContext.getAnalysisResult().getControlFlowFunctions(activityWithScope.scope())
                        .scopeFn();
        return List.of(new CallStatement(new FunctionCall(scopeFn, List.of(cx.contextVarRef()))));
    }

    private static List<Statement> convertUnhandledActivity(ActivityContext cx,
                                                            UnhandledActivity unhandledActivity) {
        return List.of(new Comment(unhandledActivity.reason()), elementAsComment(unhandledActivity.element()));
    }

    private static Comment elementAsComment(Element element) {
        return new Comment(ConversionUtils.elementToString(element));
    }

    private static List<Statement> convertReply(ActivityContext cx, Reply reply) {
        List<Statement> body = new ArrayList<>();
        VarDeclStatment inputDecl = new VarDeclStatment(XML, cx.getAnnonVarName(), defaultEmptyXml());
        body.add(inputDecl);
        VariableReference result = inputDecl.ref();
        if (!reply.inputBindings().isEmpty()) {
            InputBindingResult inputBindings = convertInputBindings(cx, result, reply.inputBindings());
            body.addAll(inputBindings.statements());
            result = inputBindings.resultRef();
        }
        body.add(new CallStatement(new FunctionCall(cx.getSetXMLResponseFn(), List.of(cx.contextVarRef(), result,
                exprFrom("{}")))));
        return body;
    }

    private static List<Statement> convertPickAction(ActivityContext cx, Pick pick) {
        return convertActivityWithScope(cx, pick);
    }

    private static List<Statement> convertEmptyAction(ActivityContext cx) {
        return List.of();
    }

    private static List<Statement> convertActivityExtension(ActivityContext cx,
                                                            ActivityExtension activityExtension) {
        List<Statement> body = new ArrayList<>();
        VarDeclStatment inputDecl = new VarDeclStatment(XML, cx.getAnnonVarName(),
                activityExtension.inputVariable()
                        .map(name -> (BallerinaModel.Expression) getFromContext(cx, name))
                        .orElseGet(ActivityConverter::defaultEmptyXml));
        body.add(inputDecl);
        InputBindingResult inputBindings = convertInputBindings(cx, inputDecl.ref(), activityExtension.inputBindings());
        body.addAll(inputBindings.statements());

        VariableReference result = activityExtension.inputBindings().isEmpty() ? inputDecl.ref()
                : inputBindings.resultRef();
        assert result != null;

        ActivityExtension.Config config = activityExtension.config();
        ActivityConversionResult conversion = switch (config) {
            case ActivityExtension.Config.End ignored -> emptyExtensionConversion(cx, result);
            case ActivityExtension.Config.HTTPSend httpSend -> createHttpSend(cx, result, httpSend);
            case JsonOperation jsonOperation -> createJsonOperation(cx, result, jsonOperation);
            case ActivityExtension.Config.SQL sql -> createSQLOperation(cx, result, sql);
            case ActivityExtension.Config.SendHTTPResponse sendHTTPResponse ->
                    createSendHttpResponse(cx, result, sendHTTPResponse);
            case ActivityExtension.Config.FileWrite fileWrite -> createFileWriteOperation(cx, result, fileWrite);
            case ActivityExtension.Config.Log log -> createLogOperation(cx, result, log);
            case ActivityExtension.Config.RenderXML ignored -> finishXmlRenderActivity(cx, result);
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
        return body;
    }

    private static ActivityConversionResult createSendHttpResponse(
            ActivityContext cx, VariableReference input, ActivityExtension.Config.SendHTTPResponse sendHTTPResponse) {
        List<Statement> body = new ArrayList<>();
        body.add(new Comment("FIXME ignoring headers others than content type"));
        VarDeclStatment contentType = new VarDeclStatment(STRING, cx.getAnnonVarName(),
                        exprFrom("(%s/**/<Content\\-Type>/*).toString()".formatted(input.varName())));
        VarDeclStatment asciiContent = new VarDeclStatment(STRING, cx.getAnnonVarName(),
                        exprFrom("(%s/**/<asciiContent>/*).toString()".formatted(input.varName())));
        VarDeclStatment headers = new VarDeclStatment(XML, cx.getAnnonVarName(),
                        exprFrom("(%s/**/<Headers>/*)".formatted(input.varName())));
        body.add(contentType);
        body.add(asciiContent);
        body.add(headers);
        VarDeclStatment headerMap =
                new VarDeclStatment(new BallerinaModel.TypeDesc.MapTypeDesc(STRING), cx.getAnnonVarName(),
                        new FunctionCall(cx.getParseHeadersFn(), List.of(
                                headers.ref())));
        body.add(headerMap);
        cx.addLibraryImport(Library.JSON_DATA);
        String setJSONResponseFn = cx.getSetJSONResponseFn();
        String setXMLResponseFn = cx.getSetXMLResponseFn();
        String setTextResponseFn = cx.getSetTextResponseFn();
        body.add(stmtFrom(
                """
                match %5$s {
                    "application/json" => {
                        map<json> jsonRepr = check jsondata:parseString(%6$s);
                        %2$s(%1$s, jsonRepr, %7$s);
                    }
                    "application/xml" => {
                        xml xmlRepr = xml `${%6$s}`;
                        %3$s(%1$s, xmlRepr, %7$s);
                    }
                    _ => {
                        %4$s(%1$s, %6$s, %7$s);
                    }
                }
                """.formatted(cx.contextVarRef(), setJSONResponseFn, setXMLResponseFn, setTextResponseFn,
                contentType.ref(), asciiContent.ref(), headerMap.ref())));
        return new ActivityConversionResult(asciiContent.ref(), body);
    }

    private static ActivityConversionResult createAccumulateEnd(
            ActivityContext cx, ActivityExtension.Config.AccumulateEnd accumulateEnd,
            String resultName) {
        AnalysisResult ar = cx.processContext.getAnalysisResult();
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
        return new ActivityConversionResult(accumResult.ref(), body);
    }

    private static ActivityConversionResult createJsonOperation(
            ActivityContext cx, VariableReference result,
            JsonOperation jsonOperation) {
        return switch (jsonOperation.kind()) {
            case JSON_RENDER -> createJsonRenderOperation(cx, result, jsonOperation);
            case JSON_PARSER -> createJsonParserOperation(cx, result, jsonOperation);
            default -> throw new IllegalStateException(
                    "Unexpected json operation kind: " + jsonOperation.kind());
        };
    }

    private static ActivityConversionResult createJsonParserOperation(
            ActivityContext cx, VariableReference input,
            JsonOperation jsonOperation) {
        ArrayList<Statement> body = new ArrayList<>();
        return finishConvertJsonParser(cx, input, jsonOperation.type().name(), body, false);
    }

    private static ActivityConversionResult createJsonRenderOperation(ActivityContext cx,
                                                                      VariableReference input,
                                                                      JsonOperation jsonOperation) {
        AnalysisResult ar = cx.processContext.getAnalysisResult();
        BallerinaModel.TypeDesc targetType = ConversionUtils.toTypeDesc(ar.getType(jsonOperation.type().name()));
        return finishConvertJsonRender(cx, new ArrayList<>(), targetType, "root", input);
    }

    private static @NotNull ActivityConversionResult emptyExtensionConversion(ActivityContext cx,
                                                                              VariableReference result) {
        VarDeclStatment wrapped = new VarDeclStatment(XML, cx.getAnnonVarName(),
                new XMLTemplate("<root>${%s}</root>".formatted(result.varName())));
        return new ActivityConversionResult(wrapped.ref(), List.of(wrapped));
    }

    private static ActivityConversionResult createLogOperation(ActivityContext cx,
                                                               VariableReference input,
                                                               ActivityExtension.Config.Log log) {
        return finishWriteLogActivity(cx, input);
    }

    private static ActivityConversionResult createFileWriteOperation(
            ActivityContext cx, VariableReference result, ActivityExtension.Config.FileWrite fileWrite) {
        return finishFileWrite(cx, result, false);
    }

    private static ActivityConversionResult createSQLOperation(
            ActivityContext cx, VariableReference inputVar, ActivityExtension.Config.SQL sql) {
        List<Statement> body = new ArrayList<>();
        Map<String, VariableReference> vars = addParamDecl(body, inputVar, sql);

        VarDeclStatment queryDecl = ConversionUtils.createQueryDecl(cx, vars, sql);
        body.add(queryDecl);

        VariableReference dbClient = cx.client(sql.sharedResourcePropertyName());
        if (sql.query().toUpperCase().startsWith("SELECT")) {
            return finishSelectQuery(cx, dbClient, queryDecl.ref(), body);
        }
        return finishSQLQuery(cx, dbClient, queryDecl.ref(), body);
    }

    private static @NotNull ActivityConversionResult finishSQLQuery(
            ActivityContext cx, VariableReference dbClient, VariableReference query, List<Statement> body) {
        body.add(new VarDeclStatment(
                cx.processContext.getTypeByName(BallerinaSQLConstants.EXECUTION_RESULT_TYPE),
                cx.getAnnonVarName(),
                new Check(new RemoteMethodCallAction(dbClient, BallerinaSQLConstants.EXECUTE_METHOD, List.of(query)))));
        VarDeclStatment dummyXmlResult = new VarDeclStatment(XML, cx.getAnnonVarName(), defaultEmptyXml());
        body.add(dummyXmlResult);
        return new ActivityConversionResult(dummyXmlResult.ref(), body);
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

    private static @NotNull ActivityConverter.ActivityConversionResult finishSelectQuery(
                ActivityContext cx, VariableReference dbClient, VariableReference query, List<Statement> body) {
            StreamTypeDesc streamTypeDesc = new StreamTypeDesc(
                    // map<anydata> don't work due to some reason
                    common.ConversionUtils.typeFrom("record{|anydata...;|}"),
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
                        %4$s = %5$s + xml `<Record>${%2$s}</Record>`;
                    };
                    """.formatted(stream.ref(), xmlVar, toXmlFn, accum.ref(), accum.ref())));

        VarDeclStatment result = new VarDeclStatment(XML, cx.getAnnonVarName(),
                new XMLTemplate("<root>${%s}</root>".formatted(accum.ref())));
        body.add(result);
        return new ActivityConversionResult(result.ref(), body);
    }

    private static ActivityConversionResult createHttpSend(
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

        return new ActivityConversionResult(new VariableReference(resultDecl.varName()), body);
    }

    private static List<Statement> convertReceiveEvent(ActivityContext cx, ReceiveEvent receiveEvent) {
        if (receiveEvent.variable().isPresent()) {
            var variable = receiveEvent.variable().get();
            return List.of(
                    addToContext(cx, getFromContext(cx,
                            ConversionUtils.Constants.CONTEXT_INPUT_NAME), variable));
        } else {
            return List.of();
        }
    }

    private static List<Statement> convertInvoke(ActivityContext cx, Invoke invoke) {
        List<Statement> body = new ArrayList<>();
        VarDeclStatment inputDecl = new VarDeclStatment(XML, cx.getAnnonVarName(), defaultEmptyXml());
        body.add(inputDecl);
        InputBindingResult inputBindings = convertInputBindings(cx, inputDecl.ref(), invoke.inputBindings());
        body.addAll(inputBindings.statements());
        VariableReference input = inputBindings.resultRef();

        AnalysisResult ar = cx.processContext.getAnalysisResult();
        PartnerLink.Binding binding = ar.getBinding(invoke.partnerLink());
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
        VarDeclStatment returnVale = wrapWithRoot(cx, resultDecl.ref());
        body.add(returnVale);

        body.add(addToContext(cx, returnVale.ref(), invoke.outputVariable()));
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
            XsltTransformResult transformResult = xsltTransform(cx, result, xslt);
            addNonStandardXsltWarning(transformResult.nonStandardFunctions(), body);
            VarDeclStatment transformDecl =
                    new VarDeclStatment(XML, cx.getAnnonVarName(), transformResult.expression());
            body.add(transformDecl);
            result = transformDecl.ref();
        }
        if (!extActivity.inputBindings().isEmpty()) {
            InputBindingResult inputBindings = convertInputBindings(cx, result,
                    extActivity.inputBindings());
            body.addAll(inputBindings.statements());
            result = inputBindings.resultRef();
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
        VarDeclStatment wrappedResult = wrapWithRoot(cx, resultDecl.ref());
        body.add(wrappedResult);
        body.add(addToContext(cx, wrappedResult.ref(), extActivity.outputVariable()));
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

    private static InputBindingResult convertInputBindings(ActivityContext cx, VariableReference input,
                                                           Collection<InputBinding> inputBindings) {
        List<Statement> statements = new ArrayList<>();
        VariableReference last = input;
        for (InputBinding transform : inputBindings) {
            switch (transform) {
                case InputBinding.CompleteBinding completeBinding -> {
                    XsltTransformResult transformResult = xsltTransform(cx, last, completeBinding.xslt());
                    addNonStandardXsltWarning(transformResult.nonStandardFunctions(), statements);
                    VarDeclStatment varDecl = new VarDeclStatment(XML, cx.getAnnonVarName(),
                            transformResult.expression());
                    statements.add(varDecl);
                    last = varDecl.ref();
                }
                case InputBinding.PartialBindings partialBindings -> {
                    InputBindingResult partialResult = convertPartialInputBinding(cx, partialBindings, last);
                    statements.addAll(partialResult.statements());
                    last = partialResult.resultRef();
                }
            }
        }
        return new InputBindingResult(statements, last);
    }

    private static InputBindingResult convertPartialInputBinding(
            ActivityContext cx, InputBinding.PartialBindings partialBindings, VariableReference last) {
        List<Statement> statements = new ArrayList<>();
        List<VarDeclStatment> xsltStatements = new ArrayList<>();
        Set<String> allNonStandardFunctions = new HashSet<>();
        for (Activity.Expression.XSLT each : partialBindings.xslt()) {
            XsltTransformResult transformResult = xsltTransform(cx, last, each);
            allNonStandardFunctions.addAll(transformResult.nonStandardFunctions());
            VarDeclStatment vds = new VarDeclStatment(XML, cx.getAnnonVarName(), transformResult.expression());
            xsltStatements.add(vds);
        }
        addNonStandardXsltWarning(new ArrayList<>(allNonStandardFunctions), statements);
        statements.addAll(xsltStatements);
        String concat = xsltStatements.stream().map(VarDeclStatment::varName).collect(Collectors.joining(" + "));
        VarDeclStatment concatResult = new VarDeclStatment(XML, cx.getAnnonVarName(),
                new XMLTemplate("<root>${%s}</root>".formatted(concat)));
        statements.add(concatResult);
        VariableReference resultRef = xsltStatements.isEmpty() ? last : new VariableReference(concatResult.varName());
        return new InputBindingResult(statements, resultRef);
    }

    @NotNull
    private static XsltTransformResult xsltTransform(ActivityContext cx, VariableReference inputVariable,
                                                    Activity.Expression.XSLT xslt) {
        cx.addLibraryImport(Library.XSLT);
        String styleSheet = xsltTransformer.apply(cx, xslt.expression());

        // Detect non-standard functions (Tibco-specific functions)
        List<String> nonStandardFunctions = detectNonStandardFunctions(styleSheet);

        BallerinaModel.Expression expression = new Check(new FunctionCall(XSLTConstants.XSLT_TRANSFORM_FUNCTION,
                List.of(inputVariable, new XMLTemplate(styleSheet),
                        new BallerinaModel.Expression.FieldAccess(cx.contextVarRef(), "variables"))));

        return new XsltTransformResult(expression, nonStandardFunctions);
    }

    @NotNull
    static List<String> detectNonStandardFunctions(String xsltContent) {
        List<String> nonStandardFunctions = new ArrayList<>();

        // Pattern to match Tibco-specific functions like tib:trim, tib:parse-dateTime, etc.
        Pattern tibcoFunctionPattern = Pattern.compile("tib:[a-zA-Z-]+\\s*\\(");
        Matcher matcher = tibcoFunctionPattern.matcher(xsltContent);

        while (matcher.find()) {
            String function = matcher.group().replaceAll("\\s*\\($", "");
            if (!nonStandardFunctions.contains(function)) {
                nonStandardFunctions.add(function);
            }
        }

        return nonStandardFunctions;
    }

    private static void addNonStandardXsltWarning(List<String> nonStandardFunctions, List<Statement> body) {
        if (!nonStandardFunctions.isEmpty()) {
            body.add(new Comment("WARNING: Non-standard XSLT functions detected: " +
                    String.join(", ", nonStandardFunctions.stream().distinct().toList())));
        }
    }

    private static XMLTemplate defaultEmptyXml() {
        return new XMLTemplate("<root></root>");
    }

    private static FunctionCall getFromContext(ActivityContext cx, String key) {
        assert !key.isEmpty();
        String getFromContextFn = cx.getFromContextFn();
        return new FunctionCall(getFromContextFn, List.of(cx.contextVarRef(), new StringConstant(key)));
    }

    private static Statement addToContext(ActivityContext cx, BallerinaModel.Expression value, String key) {
        assert !key.isEmpty();
        String addToContextFn = cx.getAddToContextFn();
        return new CallStatement(new FunctionCall(addToContextFn,
                List.of(cx.contextVarRef(), new StringConstant(key.replace(' ', '-')), value)));
    }

    private record ActivityConversionResult(VariableReference result, List<Statement> body) {

    }

    record XsltTransformResult(BallerinaModel.Expression expression, List<String> nonStandardFunctions) {

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
        static final String PARSE_AS_TYPE = "xmldata:parseAsType";

        private XMLDataConstants() {

        }
    }

    private record InputBindingResult(List<Statement> statements, VariableReference resultRef) {
    }
}
