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

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public record Process5(String name, Collection<NameSpace> nameSpaces,
                       ExplicitTransitionGroup transitionGroup) implements Process {

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public record ExplicitTransitionGroup(List<ExplicitTransitionGroup.InlineActivity> activities,
                                          List<ExplicitTransitionGroup.Transition> transitions,
                                          ExplicitTransitionGroup.InlineActivity startActivity,
                                          Optional<Scope.Flow.Activity.Expression.XSLT> returnBindings) {

        public ExplicitTransitionGroup() {
            this(null);
        }

        public boolean isEmpty() {
            return activities.isEmpty() && transitions.isEmpty() && startActivity == null;
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
            return new ExplicitTransitionGroup(newActivities, transitions, startActivity, returnBindings);
        }

        public ExplicitTransitionGroup append(ExplicitTransitionGroup.Transition transition) {
            List<ExplicitTransitionGroup.Transition> newTransitions = new ArrayList<>(transitions);
            newTransitions.add(transition);
            return new ExplicitTransitionGroup(activities, newTransitions, startActivity, returnBindings);
        }

        public ExplicitTransitionGroup setStartActivity(ExplicitTransitionGroup.InlineActivity startActivity) {
            List<ExplicitTransitionGroup.InlineActivity> remainingActivities = activities.stream()
                    .filter(each -> !each.equals(startActivity)).toList();
            return new ExplicitTransitionGroup(remainingActivities, transitions, startActivity, returnBindings);
        }

        public ExplicitTransitionGroup setReturnBindings(Scope.Flow.Activity.Expression.XSLT expression) {
            return new ExplicitTransitionGroup(activities, transitions, startActivity, Optional.of(expression));
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

        public @NotNull ExplicitTransitionGroup resolve() {
            if (startActivity != null) {
                return this;
            }
            if (activities.isEmpty()) {
                return this;
            }
            String startActivityName = transitions.stream()
                    .filter(transition -> transition.from().equalsIgnoreCase("start"))
                    .findFirst().map(ExplicitTransitionGroup.Transition::to)
                    .orElseThrow(() -> new IllegalStateException("failed to find start activity"));
            ExplicitTransitionGroup.InlineActivity startActivity = activities.stream()
                    .filter(each -> each.name().equals(startActivityName))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("no such activity" + startActivityName));
            return this.setStartActivity(startActivity);
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
                SOAP_SEND_RECEIVE,
                SOAP_SEND_REPLY,
                LOOP_GROUP,
                REST,
                CATCH,
                JSON_PARSER_ACTIVITY,
                JSON_RENDER_ACTIVITY,
                JDBC,
                MAPPER;

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
                                    new LookUpData("WriteToLogActivity", WRITE_LOG))
                            .filter(each -> type.endsWith(each.suffix)).findFirst()
                            .map(LookUpData::activityType).orElse(UNHANDLED);
                }
            }

            record JDBC(Element element, String name, InputBinding inputBinding,
                        String connection, String fileName) implements ExplicitTransitionGroup.InlineActivity {

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

            record JSONRender(Element element, String name, InputBinding inputBinding,
                              XSD targetType, String fileName) implements ExplicitTransitionGroup.InlineActivity {

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
                              XSD targetType, String fileName) implements ExplicitTransitionGroup.InlineActivity {

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

                public CallProcess {
                    assert inputBinding != null;
                }

                @Override
                public InlineActivityType type() {
                    return InlineActivityType.CALL_PROCESS;
                }

                @Override
                public boolean hasInputBinding() {
                    return true;
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
                                    InputBinding inputBinding, String fileName) 
                    implements ExplicitTransitionGroup.InlineActivity {

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
                    implements ExplicitTransitionGroup.InlineActivity {

                @Override
                public InlineActivityType type() {
                    return InlineActivityType.HTTP_EVENT_SOURCE;
                }

                @Override
                public boolean hasInputBinding() {
                    return inputBinding != null;
                }
            }

        }
    }
}
