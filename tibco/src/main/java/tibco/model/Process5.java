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

package tibco.model;

import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public record Process5(String name, String path, Collection<NameSpace> nameSpaces,
                       ExplicitTransitionGroup transitionGroup) implements Process {

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public record ExplicitTransitionGroup(List<ExplicitTransitionGroup.InlineActivity> activities,
                                          List<ExplicitTransitionGroup.Transition> transitions,
                                          ExplicitTransitionGroup.InlineActivity start,
                                          Optional<Scope.Flow.Activity.Expression.XSLT> returnBindings) {

        public ExplicitTransitionGroup() {
            this(null);
        }

        public Optional<InlineActivity> startActivity() {
            return Optional.ofNullable(start);
        }

        public boolean isEmpty() {
            return activities.isEmpty() && transitions.isEmpty();
        }

        ExplicitTransitionGroup(ExplicitTransitionGroup.InlineActivity startActivity) {
            this(List.of(), List.of(), startActivity, Optional.empty());
        }

        public ExplicitTransitionGroup {
            activities = Collections.unmodifiableList(activities);
            transitions = Collections.unmodifiableList(transitions);
        }

        public ExplicitTransitionGroup append(ExplicitTransitionGroup.InlineActivity activity) {
            List<ExplicitTransitionGroup.InlineActivity> newActivities = new ArrayList<>(activities);
            newActivities.add(activity);
            return new ExplicitTransitionGroup(newActivities, transitions, start, returnBindings);
        }

        public ExplicitTransitionGroup append(ExplicitTransitionGroup.Transition transition) {
            List<ExplicitTransitionGroup.Transition> newTransitions = new ArrayList<>(transitions);
            newTransitions.add(transition);
            return new ExplicitTransitionGroup(activities, newTransitions, start, returnBindings);
        }

        public ExplicitTransitionGroup setStartActivity(ExplicitTransitionGroup.InlineActivity startActivity) {
            List<ExplicitTransitionGroup.InlineActivity> remainingActivities = activities.stream()
                    .filter(each -> !each.equals(startActivity)).toList();
            return new ExplicitTransitionGroup(remainingActivities, transitions, startActivity, returnBindings);
        }

        public ExplicitTransitionGroup setReturnBindings(Scope.Flow.Activity.Expression.XSLT expression) {
            return new ExplicitTransitionGroup(activities, transitions, start, Optional.of(expression));
        }

        public sealed interface InlineActivityWithBody extends ExplicitTransitionGroup.InlineActivity {

            ExplicitTransitionGroup body();
        }

        public sealed interface NestedGroup extends ExplicitTransitionGroup.InlineActivityWithBody {

            record LoopGroup(Element element, String name, InputBinding inputBinding,
                             ExplicitTransitionGroup.NestedGroup.LoopGroup.SourceExpression over,
                             Optional<String> elementSlot, Optional<String> indexSlot,
                             Optional<String> activityOutputName, boolean accumulateOutput,
                             ExplicitTransitionGroup body, String fileName)
                    implements ExplicitTransitionGroup.NestedGroup {

                public LoopGroup {
                    assert !accumulateOutput || activityOutputName.isPresent();
                }

                @Override
                public InlineActivityType type() {
                    return InlineActivityType.LOOP_GROUP;
                }

                @Override
                public boolean hasInputBinding() {
                    return inputBinding != null;
                }

                public record SourceExpression(String variableName, Optional<String> xPath) {

                    public SourceExpression {
                        assert xPath != null;
                        assert variableName != null && !variableName.isEmpty();
                    }
                }

            }
        }

        public record Transition(String from, String to) {

        }

        public sealed interface InlineActivity extends Scope.Flow.Activity {

            String name();

            ExplicitTransitionGroup.InlineActivity.InlineActivityType type();

            boolean hasInputBinding();

            InputBinding inputBinding();

            enum InlineActivityType {
                ASSIGN,
                HTTP_EVENT_SOURCE,
                HTTP_RESPONSE,
                UNHANDLED,
                NULL,
                WRITE_LOG,
                CALL_PROCESS,
                FILE_WRITE,
                FILE_READ,
                XML_RENDER_ACTIVITY,
                XML_PARSE_ACTIVITY,
                XML_TRANSFORM_ACTIVITY,
                SOAP_SEND_RECEIVE,
                SOAP_SEND_REPLY,
                LOOP_GROUP,
                REST,
                CATCH,
                JSON_PARSER_ACTIVITY,
                JSON_RENDER_ACTIVITY,
                JDBC,
                JDBC_QUERY,
                JDBC_UPDATE,
                MAPPER,
                JMS_QUEUE_EVENT_SOURCE,
                JMS_QUEUE_SEND_ACTIVITY,
                JMS_QUEUE_GET_MESSAGE_ACTIVITY,
                JMS_TOPIC_PUBLISH_ACTIVITY,
                SLEEP,
                GENERATE_ERROR,
                GET_SHARED_VARIABLE,
                SET_SHARED_VARIABLE,
                FILE_EVENT_SOURCE,
                ON_STARTUP,
                LIST_FILES;

                public static ExplicitTransitionGroup.InlineActivity.InlineActivityType parse(String type) {
                    record LookUpData(String suffix,
                                      ExplicitTransitionGroup.InlineActivity.InlineActivityType activityType) {

                    }
                    return Stream.of(
                                    new LookUpData("MapperActivity", MAPPER),
                                    new LookUpData("HTTPEventSource", HTTP_EVENT_SOURCE),
                                    new LookUpData("AssignActivity", ASSIGN),
                                    new LookUpData("NullActivity", NULL),
                                    new LookUpData("HTTPResponseActivity", HTTP_RESPONSE),
                                    new LookUpData("XMLRendererActivity", XML_RENDER_ACTIVITY),
                                    new LookUpData("XMLParseActivity", XML_PARSE_ACTIVITY),
                                    new LookUpData("XMLTransformActivity", XML_TRANSFORM_ACTIVITY),
                                    new LookUpData("LoopGroup", LOOP_GROUP),
                                    new LookUpData("WriteToLogActivity", WRITE_LOG),
                                    new LookUpData("CatchActivity", CATCH),
                                    new LookUpData("FileReadActivity", FILE_READ),
                                    new LookUpData("FileWriteActivity", FILE_WRITE),
                                    new LookUpData("JDBCGeneralActivity", JDBC),
                                    new LookUpData("RestActivity", REST),
                                    new LookUpData("CallProcessActivity", CALL_PROCESS),
                                    new LookUpData("SOAPSendReceiveActivity", SOAP_SEND_RECEIVE),
                                    new LookUpData("JSONParserActivity", JSON_PARSER_ACTIVITY),
                                    new LookUpData("JSONRenderActivity", JSON_RENDER_ACTIVITY),
                                    new LookUpData("SOAPSendReplyActivity", SOAP_SEND_REPLY),
                                    new LookUpData("JMSQueueEventSource", JMS_QUEUE_EVENT_SOURCE),
                                    new LookUpData("JMSQueueSendActivity", JMS_QUEUE_SEND_ACTIVITY),
                                    new LookUpData("JMSQueueGetMessageActivity", JMS_QUEUE_GET_MESSAGE_ACTIVITY),
                                    new LookUpData("JMSTopicPublishActivity", JMS_TOPIC_PUBLISH_ACTIVITY),
                                    new LookUpData("GenerateErrorActivity", GENERATE_ERROR),
                                    new LookUpData("SleepActivity", SLEEP),
                                    new LookUpData("GetSharedVariableActivity", GET_SHARED_VARIABLE),
                                    new LookUpData("SetSharedVariableActivity", SET_SHARED_VARIABLE),
                                    new LookUpData("JDBCQueryActivity", JDBC_QUERY),
                                    new LookUpData("JDBCUpdateActivity", JDBC_UPDATE),
                                    new LookUpData("FileEventSource", FILE_EVENT_SOURCE),
                                    new LookUpData("OnStartupEventSource", ON_STARTUP),
                                    new LookUpData("ListFilesActivity", LIST_FILES))
                            .filter(each -> type.endsWith(each.suffix)).findFirst()
                            .map(LookUpData::activityType).orElse(UNHANDLED);
                }

                public String toTibcoType() {
                    return switch (this) {
                        case MAPPER -> "com.tibco.plugin.mapper.MapperActivity";
                        case HTTP_EVENT_SOURCE -> "com.tibco.plugin.http.HTTPEventSource";
                        case ASSIGN -> "com.tibco.pe.core.AssignActivity";
                        case NULL -> "com.tibco.plugin.timer.NullActivity";
                        case HTTP_RESPONSE -> "com.tibco.plugin.http.HTTPResponseActivity";
                        case XML_RENDER_ACTIVITY -> "com.tibco.plugin.xml.XMLRendererActivity";
                        case XML_PARSE_ACTIVITY -> "com.tibco.plugin.xml.XMLParseActivity";
                        case XML_TRANSFORM_ACTIVITY -> "com.tibco.plugin.xml.XMLTransformActivity";
                        case LOOP_GROUP -> "com.tibco.pe.core.LoopGroup";
                        case WRITE_LOG -> "com.tibco.pe.core.WriteToLogActivity";
                        case CATCH -> "com.tibco.pe.core.CatchActivity";
                        case FILE_READ -> "com.tibco.plugin.file.FileReadActivity";
                        case FILE_WRITE -> "com.tibco.plugin.file.FileWriteActivity";
                        case JDBC -> "com.tibco.plugin.jdbc.JDBCGeneralActivity";
                        case REST -> "com.tibco.plugin.json.activities.RestActivity";
                        case CALL_PROCESS -> "com.tibco.pe.core.CallProcessActivity";
                        case SOAP_SEND_RECEIVE -> "com.tibco.plugin.soap.SOAPSendReceiveActivity";
                        case JSON_PARSER_ACTIVITY -> "com.tibco.plugin.json.activities.JSONParserActivity";
                        case JSON_RENDER_ACTIVITY -> "com.tibco.plugin.json.activities.JSONRenderActivity";
                        case SOAP_SEND_REPLY -> "com.tibco.plugin.soap.SOAPSendReplyActivity";
                        case JMS_QUEUE_EVENT_SOURCE -> "com.tibco.plugin.jms.JMSQueueEventSource";
                        case JMS_QUEUE_SEND_ACTIVITY -> "com.tibco.plugin.jms.JMSQueueSendActivity";
                        case JMS_QUEUE_GET_MESSAGE_ACTIVITY -> "com.tibco.plugin.jms.JMSQueueGetMessageActivity";
                        case JMS_TOPIC_PUBLISH_ACTIVITY -> "com.tibco.plugin.jms.JMSTopicPublishActivity";
                        case GENERATE_ERROR -> "com.tibco.pe.core.GenerateErrorActivity";
                        case SLEEP -> "com.tibco.plugin.timer.SleepActivity";
                        case GET_SHARED_VARIABLE -> "com.tibco.pe.core.GetSharedVariableActivity";
                        case SET_SHARED_VARIABLE -> "com.tibco.pe.core.SetSharedVariableActivity";
                        case JDBC_QUERY -> "com.tibco.plugin.jdbc.JDBCQueryActivity";
                        case JDBC_UPDATE -> "com.tibco.plugin.jdbc.JDBCUpdateActivity";
                        case FILE_EVENT_SOURCE -> "com.tibco.plugin.file.FileEventSource";
                        case ON_STARTUP -> "com.tibco.pe.core.OnStartupEventSource";
                        case LIST_FILES -> "com.tibco.plugin.file.ListFilesActivity";
                        case UNHANDLED -> "UNHANDLED";
                    };
                }
            }

            record JDBC(Element element, String name, InputBinding inputBinding,
                        String connection, String fileName) implements ExplicitTransitionGroup.InlineActivity,
                    Scope.Flow.ActivityWithResources {

                @Override
                public Collection<Resource.ResourceIdentifier> resources() {
                    return List.of(new Resource.ResourceIdentifier(Resource.ResourceKind.JDBC_SHARED, connection));
                }

                public JDBC {
                    assert inputBinding != null;
                }

                @Override
                public InlineActivityType type() {
                    return InlineActivityType.JDBC;
                }

                @Override
                public boolean hasInputBinding() {
                    return true;
                }
            }

            record JDBCQuery(Element element, String name, String fileName, InputBinding inputBinding,
                             String connection, Optional<Integer> timeout, Optional<Integer> maxRow,
                             Optional<String> statement)
                    implements ExplicitTransitionGroup.InlineActivity, Scope.Flow.ActivityWithResources {

                @Override
                public InlineActivityType type() {
                    return InlineActivityType.JDBC_QUERY;
                }

                @Override
                public boolean hasInputBinding() {
                    return inputBinding != null;
                }

                @Override
                public Collection<Resource.ResourceIdentifier> resources() {
                    return List.of(new Resource.ResourceIdentifier(Resource.ResourceKind.JDBC_SHARED, connection));
                }
            }

            record JDBCUpdate(Element element, String name, String fileName, InputBinding inputBinding,
                              String connection, Optional<String> statement, boolean hasPreparedData)
                    implements ExplicitTransitionGroup.InlineActivity, Scope.Flow.ActivityWithResources {

                @Override
                public InlineActivityType type() {
                    return InlineActivityType.JDBC_UPDATE;
                }

                @Override
                public boolean hasInputBinding() {
                    return inputBinding != null;
                }

                @Override
                public Collection<Resource.ResourceIdentifier> resources() {
                    return List.of(new Resource.ResourceIdentifier(Resource.ResourceKind.JDBC_SHARED, connection));
                }
            }

            record JSONRender(Element element, String name, InputBinding inputBinding,
                              Optional<XSD> targetType, String fileName)
                    implements ExplicitTransitionGroup.InlineActivity {

                public JSONRender {
                    assert inputBinding != null;
                }

                @Override
                public InlineActivityType type() {
                    return InlineActivityType.JSON_PARSER_ACTIVITY;
                }

                @Override
                public boolean hasInputBinding() {
                    return true;
                }
            }

            record JSONParser(Element element, String name, InputBinding inputBinding,
                              Optional<XSD> targetType, String fileName)
                    implements ExplicitTransitionGroup.InlineActivity {

                public JSONParser {
                    assert inputBinding != null;
                }

                @Override
                public InlineActivityType type() {
                    return InlineActivityType.JSON_PARSER_ACTIVITY;
                }

                @Override
                public boolean hasInputBinding() {
                    return true;
                }
            }

            record REST(Element element, String name, InputBinding inputBinding,
                        ExplicitTransitionGroup.InlineActivity.REST.Method method,
                    ExplicitTransitionGroup.InlineActivity.REST.ResponseType responseType, String url,
                    String fileName) implements ExplicitTransitionGroup.InlineActivity {

                public enum ResponseType {
                    JSON,
                    XML;

                    public static REST.ResponseType from(String string) {
                        return switch (string.toLowerCase()) {
                            case "json" -> JSON;
                            case "xml" -> XML;
                            default -> throw new IllegalArgumentException("Unknown response type: " + string);
                        };
                    }
                }

                public enum Method {
                    PUT,
                    GET,
                    DELETE,
                    POST;

                    public static REST.Method from(String string) {
                        return switch (string.toLowerCase()) {
                            case "put" -> PUT;
                            case "get" -> GET;
                            case "delete" -> DELETE;
                            case "post" -> POST;
                            default -> throw new IllegalArgumentException("Unknown method: " + string);
                        };
                    }
                }

                @Override
                public InlineActivityType type() {
                    return InlineActivityType.REST;
                }

                @Override
                public boolean hasInputBinding() {
                    return inputBinding != null;
                }
            }

            record CallProcess(Element element, String name, InputBinding inputBinding,
                               String processName, String fileName) implements ExplicitTransitionGroup.InlineActivity {

                @Override
                public InlineActivityType type() {
                    return InlineActivityType.CALL_PROCESS;
                }

                @Override
                public boolean hasInputBinding() {
                    return inputBinding != null;
                }
            }

            record FileRead(Element element, String name, InputBinding inputBinding,
                            String encoding, String fileName) implements ExplicitTransitionGroup.InlineActivity {

                public FileRead {
                    assert inputBinding != null;
                }

                @Override
                public InlineActivityType type() {
                    return InlineActivityType.FILE_READ;
                }

                @Override
                public boolean hasInputBinding() {
                    return true;
                }
            }

            record FileWrite(Element element, String name, InputBinding inputBinding, String encoding,
                             boolean append, String fileName) implements ExplicitTransitionGroup.InlineActivity {

                public FileWrite {
                    assert inputBinding != null;
                }

                @Override
                public InlineActivityType type() {
                    return InlineActivityType.FILE_WRITE;
                }

                @Override
                public boolean hasInputBinding() {
                    return true;
                }
            }

            record XMLParseActivity(Element element, String name,
                                    InputBinding inputBinding, InputStyle inputStyle, String fileName)
                    implements ExplicitTransitionGroup.InlineActivity {

                public enum InputStyle {
                    TEXT,
                    BINARY;

                    public static InputStyle from(String s) {
                        return switch (s.toLowerCase()) {
                            case "text" -> TEXT;
                            case "binary" -> BINARY;
                            default -> throw new IllegalArgumentException("Unknown XMLParseActivity input style: " + s);
                        };
                    }
                }

                public XMLParseActivity {
                    assert inputBinding != null;
                }

                @Override
                public InlineActivityType type() {
                    return InlineActivityType.XML_PARSE_ACTIVITY;
                }

                @Override
                public boolean hasInputBinding() {
                    return true;
                }
            }

            record XMLRenderActivity(Element element, String name,
                    InputBinding inputBinding, String fileName)
                    implements ExplicitTransitionGroup.InlineActivity {

                public XMLRenderActivity {
                    assert inputBinding != null;
                }

                @Override
                public InlineActivityType type() {
                    return InlineActivityType.XML_RENDER_ACTIVITY;
                }

                @Override
                public boolean hasInputBinding() {
                    return true;
                }
            }

            record XMLTransformActivity(Element element, String name,
                                        InputBinding inputBinding, String styleSheet, String fileName)
                    implements ExplicitTransitionGroup.InlineActivity {

                public XMLTransformActivity {
                    assert inputBinding != null;
                }

                @Override
                public InlineActivityType type() {
                    return InlineActivityType.XML_TRANSFORM_ACTIVITY;
                }

                @Override
                public boolean hasInputBinding() {
                    return true;
                }
            }

            record SOAPSendReply(Element element, String name,
                    InputBinding inputBinding, String fileName)
                    implements ExplicitTransitionGroup.InlineActivity {

                public SOAPSendReply {
                    assert inputBinding != null;
                }

                @Override
                public InlineActivityType type() {
                    return InlineActivityType.SOAP_SEND_REPLY;
                }

                @Override
                public boolean hasInputBinding() {
                    return true;
                }
            }

            record SOAPSendReceive(Element element, String name, InputBinding inputBinding,
                                   Optional<String> soapAction, String endpointURL, String fileName) implements
                    ExplicitTransitionGroup.InlineActivity {

                @Override
                public InlineActivityType type() {
                    return InlineActivityType.SOAP_SEND_RECEIVE;
                }

                @Override
                public boolean hasInputBinding() {
                    return inputBinding != null;
                }
            }

            sealed interface ErrorHandlerInlineActivity {

            }

            record Catch(Element element, String name,
                         InputBinding inputBinding, String fileName) implements ExplicitTransitionGroup.InlineActivity,
                    ExplicitTransitionGroup.InlineActivity.ErrorHandlerInlineActivity {

                @Override
                public InlineActivityType type() {
                    return InlineActivityType.CATCH;
                }

                @Override
                public boolean hasInputBinding() {
                    return inputBinding != null;
                }
            }

            record WriteLog(Element element, String name, InputBinding inputBinding, String fileName) implements
                    ExplicitTransitionGroup.InlineActivity {

                public WriteLog {
                    assert inputBinding != null;
                }

                @Override
                public InlineActivityType type() {
                    return InlineActivityType.WRITE_LOG;
                }

                @Override
                public boolean hasInputBinding() {
                    return true;
                }
            }

            record HTTPResponse(Element element, String name, InputBinding inputBinding, String fileName) implements
                    ExplicitTransitionGroup.InlineActivity {

                public HTTPResponse {
                    assert inputBinding != null;
                }

                @Override
                public InlineActivityType type() {
                    return InlineActivityType.HTTP_RESPONSE;
                }

                @Override
                public boolean hasInputBinding() {
                    return true;
                }
            }

            record NullActivity(Element element, String name, InputBinding inputBinding, String fileName) implements
                    ExplicitTransitionGroup.InlineActivity {

                @Override
                public InlineActivityType type() {
                    return InlineActivityType.NULL;
                }

                @Override
                public boolean hasInputBinding() {
                    return inputBinding != null;
                }
            }

            record UnhandledInlineActivity(Element element, String name, String activityType,
                                           InputBinding inputBinding, String fileName) implements
                    ExplicitTransitionGroup.InlineActivity {

                @Override
                public InlineActivityType type() {
                    return InlineActivityType.UNHANDLED;
                }

                @Override
                public boolean hasInputBinding() {
                    return inputBinding != null;
                }
            }

            record MapperActivity(Element element, String name,
                    InputBinding inputBinding, String fileName)
                    implements ExplicitTransitionGroup.InlineActivity {

                public MapperActivity {
                    assert inputBinding != null;
                }

                @Override
                public InlineActivityType type() {
                    return InlineActivityType.MAPPER;
                }

                @Override
                public boolean hasInputBinding() {
                    return true;
                }
            }

            record AssignActivity(Element element, String name, String variableName,
                    InputBinding inputBinding, String fileName)
                    implements ExplicitTransitionGroup.InlineActivity {

                @Override
                public InlineActivityType type() {
                    return InlineActivityType.ASSIGN;
                }

                @Override
                public boolean hasInputBinding() {
                    return inputBinding != null;
                }
            }

            record HttpEventSource(Element element, String name, String sharedChannel,
                    InputBinding inputBinding, String fileName)
                    implements ExplicitTransitionGroup.InlineActivity, Scope.Flow.ActivityWithResources {

                @Override
                public InlineActivityType type() {
                    return InlineActivityType.HTTP_EVENT_SOURCE;
                }

                @Override
                public boolean hasInputBinding() {
                    return inputBinding != null;
                }

                @Override
                public Collection<Resource.ResourceIdentifier> resources() {
                    return List.of(new Resource.ResourceIdentifier(Resource.ResourceKind.HTTP_SHARED, sharedChannel));
                }
            }

            record FileEventSource(Element element, String name, InputBinding inputBinding,
                                   boolean createEvent, boolean modifyEvent, boolean deleteEvent,
                                   boolean excludeContent, String fileName)
                    implements ExplicitTransitionGroup.InlineActivity {

                @Override
                public InlineActivityType type() {
                    return InlineActivityType.FILE_EVENT_SOURCE;
                }

                @Override
                public boolean hasInputBinding() {
                    return inputBinding != null;
                }
            }

            record OnStartupEventSource(Element element, String name,
                    InputBinding inputBinding, String fileName) implements ExplicitTransitionGroup.InlineActivity {

                @Override
                public InlineActivityType type() {
                    return InlineActivityType.ON_STARTUP;
                }

                @Override
                public boolean hasInputBinding() {
                    return inputBinding != null;
                }
            }

            record JMSActivityBase(Element element, String name, InputBinding inputBinding,
                                   String permittedMessageType, SessionAttributes sessionAttributes,
                                   ConfigurableHeaders configurableHeaders,
                    String connectionReference, String fileName) {

                public record SessionAttributes(Optional<Boolean> transacted, Optional<Integer> acknowledgeMode,
                        Optional<Integer> maxSessions, Optional<String> destination) {

                }

                public record ConfigurableHeaders(String jmsDeliveryMode, String jmsExpiration,
                                                  String jmsPriority) {

                }

            }

            record JMSQueueEventSource(Element element, String name, InputBinding inputBinding,
                                       String permittedMessageType, JMSActivityBase.SessionAttributes sessionAttributes,
                                       JMSActivityBase.ConfigurableHeaders configurableHeaders,
                                       String connectionReference, String fileName)
                    implements ExplicitTransitionGroup.InlineActivity,
                    Scope.Flow.ActivityWithResources {

                public JMSQueueEventSource(JMSActivityBase base) {
                    this(base.element, base.name, base.inputBinding, base.permittedMessageType,
                            base.sessionAttributes, base.configurableHeaders, base.connectionReference, base.fileName);
                }

                @Override
                public Collection<Resource.ResourceIdentifier> resources() {
                    return List
                            .of(new Resource.ResourceIdentifier(Resource.ResourceKind.JMS_SHARED, connectionReference));
                }

                @Override
                public InlineActivityType type() {
                    return InlineActivityType.JMS_QUEUE_EVENT_SOURCE;
                }

                @Override
                public boolean hasInputBinding() {
                    return inputBinding != null;
                }
            }

            record JMSQueueSendActivity(Element element, String name, InputBinding inputBinding,
                                        String permittedMessageType,
                                        JMSActivityBase.SessionAttributes sessionAttributes,
                                        JMSActivityBase.ConfigurableHeaders configurableHeaders,
                                        String connectionReference, String fileName)
                    implements ExplicitTransitionGroup.InlineActivity,
                    Scope.Flow.ActivityWithResources {

                public JMSQueueSendActivity(JMSActivityBase base) {
                    this(base.element, base.name, base.inputBinding, base.permittedMessageType,
                            base.sessionAttributes, base.configurableHeaders, base.connectionReference, base.fileName);
                }

                @Override
                public Collection<Resource.ResourceIdentifier> resources() {
                    return List
                            .of(new Resource.ResourceIdentifier(Resource.ResourceKind.JMS_SHARED, connectionReference));
                }

                @Override
                public InlineActivityType type() {
                    return InlineActivityType.JMS_QUEUE_SEND_ACTIVITY;
                }

                @Override
                public boolean hasInputBinding() {
                    return inputBinding != null;
                }
            }

            record JMSQueueGetMessageActivity(Element element, String name, InputBinding inputBinding,
                                              String permittedMessageType,
                                              JMSActivityBase.SessionAttributes sessionAttributes,
                                              JMSActivityBase.ConfigurableHeaders configurableHeaders,
                    String connectionReference, String fileName)
                    implements ExplicitTransitionGroup.InlineActivity, Scope.Flow.ActivityWithResources {

                public JMSQueueGetMessageActivity(JMSActivityBase base) {
                    this(base.element, base.name, base.inputBinding, base.permittedMessageType,
                            base.sessionAttributes, base.configurableHeaders, base.connectionReference, base.fileName);
                }

                @Override
                public Collection<Resource.ResourceIdentifier> resources() {
                    return List
                            .of(new Resource.ResourceIdentifier(Resource.ResourceKind.JMS_SHARED, connectionReference));
                }

                @Override
                public InlineActivityType type() {
                    return InlineActivityType.JMS_QUEUE_GET_MESSAGE_ACTIVITY;
                }

                @Override
                public boolean hasInputBinding() {
                    return inputBinding != null;
                }
            }

            record JMSTopicPublishActivity(Element element, String name, InputBinding inputBinding,
                                           String permittedMessageType,
                                           JMSActivityBase.SessionAttributes sessionAttributes,
                                           JMSActivityBase.ConfigurableHeaders configurableHeaders,
                                           String connectionReference,
                                           String fileName)
                    implements ExplicitTransitionGroup.InlineActivity, Scope.Flow.ActivityWithResources {

                public JMSTopicPublishActivity(JMSActivityBase base) {
                    this(base.element, base.name, base.inputBinding, base.permittedMessageType,
                            base.sessionAttributes, base.configurableHeaders, base.connectionReference, base.fileName);
                }

                @Override
                public Collection<Resource.ResourceIdentifier> resources() {
                    return List
                            .of(new Resource.ResourceIdentifier(Resource.ResourceKind.JMS_SHARED, connectionReference));
                }

                @Override
                public InlineActivityType type() {
                    return InlineActivityType.JMS_TOPIC_PUBLISH_ACTIVITY;
                }

                @Override
                public boolean hasInputBinding() {
                    return inputBinding != null;
                }

                @Override
                public String fileName() {
                    return fileName;
                }
            }

            record Sleep(Element element, String name, InputBinding inputBinding, String fileName)
                    implements ExplicitTransitionGroup.InlineActivity {

                public Sleep {
                    assert inputBinding != null;
                }

                @Override
                public InlineActivityType type() {
                    return InlineActivityType.SLEEP;
                }

                @Override
                public boolean hasInputBinding() {
                    return true;
                }

                @Override
                public String fileName() {
                    return fileName;
                }
            }

            record GetSharedVariable(Element element, String name, InputBinding inputBinding,
                                     String variableConfig, String fileName)
                    implements ExplicitTransitionGroup.InlineActivity,
                    Scope.Flow.ActivityWithResources {

                @Override
                public Collection<Resource.ResourceIdentifier> resources() {
                    return List
                            .of(new Resource.ResourceIdentifier(Resource.ResourceKind.SHARED_VARIABLE, variableConfig));
                }

                @Override
                public InlineActivityType type() {
                    return InlineActivityType.GET_SHARED_VARIABLE;
                }

                @Override
                public boolean hasInputBinding() {
                    return inputBinding != null;
                }
            }

            record SetSharedVariable(Element element, String name, InputBinding inputBinding,
                                     String variableConfig, String fileName)
                    implements ExplicitTransitionGroup.InlineActivity,
                    Scope.Flow.ActivityWithResources {

                public SetSharedVariable {
                    assert inputBinding != null;
                }

                @Override
                public Collection<Resource.ResourceIdentifier> resources() {
                    return List
                            .of(new Resource.ResourceIdentifier(Resource.ResourceKind.SHARED_VARIABLE, variableConfig));
                }

                @Override
                public InlineActivityType type() {
                    return InlineActivityType.SET_SHARED_VARIABLE;
                }

                @Override
                public boolean hasInputBinding() {
                    return true;
                }
            }

            record ListFilesActivity(Element element, String name, InputBinding inputBinding, Mode mode,
                    String fileName)
                    implements ExplicitTransitionGroup.InlineActivity {
                public enum Mode {
                    FILES_AND_DIRECTORIES,
                    ONLY_FILES;

                    public static Mode from(String s) {
                        return switch (s) {
                            case "files-and-directories" -> FILES_AND_DIRECTORIES;
                            case "only-files" -> ONLY_FILES;
                            default -> throw new IllegalArgumentException("Unknown ListFilesActivity mode: " + s);
                        };
                    }
                }

                @Override
                public InlineActivityType type() {
                    return InlineActivityType.LIST_FILES;
                }

                @Override
                public boolean hasInputBinding() {
                    return inputBinding != null;
                }
            }

            record GenerateError(Element element, String name, InputBinding inputBinding, String fileName)
                    implements ExplicitTransitionGroup.InlineActivity {

                public GenerateError {
                    assert inputBinding != null;
                }

                @Override
                public InlineActivityType type() {
                    return InlineActivityType.GENERATE_ERROR;
                }

                @Override
                public boolean hasInputBinding() {
                    return true;
                }
            }

        }
    }
}
