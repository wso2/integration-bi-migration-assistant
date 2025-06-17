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

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public record Scope(String name, Collection<Flow> flows, Collection<Sequence> sequence,
                    Collection<FaultHandler> faultHandlers) {

    public sealed interface FaultHandler extends Flow.Activity {

        Scope scope();
    }

    public record Sequence(String name, List<Flow.Activity> activities) {

    }

    public record Flow(String name, Collection<Flow.Link> links, List<Flow.Activity> activities) {

        public Flow {
            assert name != null;
            assert links != null;
            assert activities != null;
        }

        public record Link(String name) {

        }

        public sealed interface Activity
                permits Process5.ExplicitTransitionGroup.InlineActivity, FaultHandler, Activity.ActivityExtension,
                Activity.ActivityWithName, Activity.ActivityWithOutput, Activity.ActivityWithScope,
                Activity.ActivityWithSources, Activity.ActivityWithTargets, Activity.Assign, Activity.Empty,
                Activity.ExtActivity, Activity.Foreach, Activity.Invoke, Activity.NestedScope, Activity.Pick,
                Activity.ReceiveEvent, Activity.Reply, Activity.StartActivity, Activity.Throw,
                Activity.UnhandledActivity {

            Element element();

            String fileName();

            sealed interface ActivityWithSources extends Flow.Activity {

                List<Source> sources();
            }

            sealed interface ActivityWithName extends Flow.Activity {

                Optional<String> getName();
            }

            sealed interface ActivityWithOutput extends Flow.Activity {

                Optional<String> outVariableName();
            }

            sealed interface ActivityWithScope extends Flow.Activity {

                Scope scope();
            }

            sealed interface StartActivity extends Flow.Activity {

            }

            sealed interface ActivityWithTargets extends Flow.Activity {

                Collection<Target> targets();

            }

            sealed interface Expression {

                record XSLT(String expression) implements Flow.Activity.Expression, ValueSource {

                    public XSLT {
                        assert expression != null;
                    }

                    @Override
                    public boolean equals(Object o) {
                        if (this == o) {
                            return true;
                        }
                        if (!(o instanceof XSLT(String expression1))) {
                            return false;
                        }
                        String expr1 = expression.replaceAll("\\s+", "");
                        String expr2 = expression1.replaceAll("\\s+", "");
                        return expr1.equals(expr2);
                    }

                    @Override
                    public int hashCode() {
                        return expression.replaceAll("\\s+", "").hashCode();
                    }
                }

                record XPath(String expression)
                        implements Flow.Activity.Expression, Flow.Activity.Source.Predicate, ValueSource {

                }
            }

            record NestedScope(String name, List<Flow.Activity.Source> sources,
                               Collection<Flow.Activity.Target> targets,
                               Collection<Sequence> sequences, Collection<Flow> flows,
                               Collection<FaultHandler> faultHandlers, Element element, String fileName)
                    implements Flow.Activity,
                    Flow.Activity.ActivityWithSources, Flow.Activity.ActivityWithTargets,
                    Flow.Activity.ActivityWithScope, Flow.Activity.ActivityWithName {

                public Scope scope() {
                    return new Scope(name, flows, sequences, faultHandlers);
                }

                @Override
                public Optional<String> getName() {
                    return Optional.of(name);
                }
            }

            record CatchAll(Scope scope, Element element, String fileName)
                    implements FaultHandler, Flow.Activity.ActivityWithScope,
                    Flow.Activity.StartActivity {

            }

            record UnhandledActivity(String reason, List<Flow.Activity.Source> sources,
                                     Collection<Flow.Activity.Target> targets,
                                     Element element, String fileName)
                    implements Flow.Activity, Flow.Activity.ActivityWithSources,
                    Flow.Activity.ActivityWithTargets {

            }

            record Assign(List<Flow.Activity.Source> sources, Collection<Flow.Activity.Target> targets,
                          Flow.Activity.Assign.Copy operation,
                          Element element, String fileName) implements Flow.Activity, Flow.Activity.ActivityWithSources,
                    Flow.Activity.ActivityWithTargets {

                public record Copy(ValueSource from, ValueSource.VarRef to) {

                }
            }

            record Foreach(String counterName, Scope scope, ValueSource startCounterValue,
                           ValueSource finalCounterValue, Element element, String fileName) implements Flow.Activity,
                    Flow.Activity.ActivityWithScope {

            }

            record Reply(String name, Method operation, String partnerLink, String portType,
                         List<Flow.Activity.InputBinding> inputBindings, Collection<Flow.Activity.Target> targets,
                         Element element, String fileName)
                    implements Flow.Activity, Flow.Activity.ActivityWithTargets, Flow.Activity.ActivityWithName {

                @Override
                public Optional<String> getName() {
                    return Optional.of(name);
                }
            }

            record Throw(List<Flow.Activity.InputBinding> inputBindings, 
                         Collection<Flow.Activity.Target> targets, Element element, String fileName)
                    implements Flow.Activity, Flow.Activity.ActivityWithTargets {

            }

            record Empty(String name, Element element, String fileName) 
                    implements Flow.Activity, Flow.Activity.ActivityWithName {

                @Override
                public Optional<String> getName() {
                    return Optional.of(name);
                }
            }

            record Pick(boolean createInstance, Flow.Activity.Pick.OnMessage onMessage,
                        Element element, String fileName) implements Flow.Activity, Flow.Activity.ActivityWithScope,
                    Flow.Activity.StartActivity {

                @Override
                public Scope scope() {
                    return onMessage().scope();
                }

                public record OnMessage(Method operation, String partnerLink, String portType, String variable,
                                        Scope scope) {

                }
            }

            record ReceiveEvent(boolean createInstance, float eventTimeout, Optional<String> variable,
                                List<Flow.Activity.Source> sources, Element element, String fileName) 
                    implements Flow.Activity, Flow.Activity.ActivityWithSources {

                public ReceiveEvent(boolean createInstance, float eventTimeout, String variable,
                                    List<Source> sources, Element element, String fileName) {
                    this(createInstance, eventTimeout,
                            variable.isEmpty() ? Optional.empty() : Optional.of(variable), sources, element, fileName);
                }

            }

            record ExtActivity(Optional<Flow.Activity.Expression> expression, String inputVariable,
                               String outputVariable,
                               List<Flow.Activity.Source> sources, List<Flow.Activity.Target> targets,
                               List<Flow.Activity.InputBinding> inputBindings,
                               Flow.Activity.ExtActivity.CallProcess callProcess, Element element, String fileName) 
                    implements Flow.Activity, Flow.Activity.ActivityWithSources, Flow.Activity.ActivityWithTargets,
                    Flow.Activity.ActivityWithOutput {

                public ExtActivity {
                    assert expression != null;
                    assert sources != null;
                    assert targets != null;
                    assert inputBindings != null;
                    assert callProcess != null;
                    assert element != null;
                }

                @Override
                public Optional<String> outVariableName() {
                    return Optional.of(outputVariable);
                }

                public record CallProcess(String subprocessName) {

                }
            }

            record ActivityExtension(Optional<String> name, Optional<String> inputVariable,
                                     Optional<String> outputVariable, Collection<Flow.Activity.Target> targets,
                                     List<Flow.Activity.Source> sources, List<Flow.Activity.InputBinding> inputBindings,
                                     Flow.Activity.ActivityExtension.Config config,
                                     Element element, String fileName) 
                    implements Flow.Activity, Flow.Activity.ActivityWithTargets,
                    Flow.Activity.ActivityWithSources, Flow.Activity.ActivityWithName, 
                    Flow.Activity.ActivityWithOutput {

                @Override
                public Optional<String> getName() {
                    return name;
                }

                @Override
                public Optional<String> outVariableName() {
                    return outputVariable;
                }

                public sealed interface Config {

                    ActivityExtension.Config.ExtensionKind kind();

                    record End() implements ActivityExtension.Config {

                        @Override
                        public ExtensionKind kind() {
                            return ExtensionKind.END;
                        }
                    }

                    record HTTPSend(String httpClientResource) implements ActivityExtension.Config {

                        public HTTPSend {
                            assert !httpClientResource.isEmpty();
                        }

                        @Override
                        public ExtensionKind kind() {
                            return ExtensionKind.HTTP_SEND;
                        }
                    }

                    record JsonOperation(ActivityExtension.Config.ExtensionKind kind, Type.TibcoType type) implements
                            ActivityExtension.Config {

                        public JsonOperation {
                            if (!(kind == ExtensionKind.JSON_PARSER || kind == ExtensionKind.JSON_RENDER)) {
                                throw new IllegalArgumentException(
                                        "Kind must be either JSON_PARSER or JSON_RENDER");
                            }
                            if (type == null) {
                                throw new IllegalArgumentException("Type cannot be null");
                            }
                        }
                    }

                    record SendHTTPResponse(Optional<String> ResponseActivityInputNamespace)
                            implements ActivityExtension.Config {

                        @Override
                        public ExtensionKind kind() {
                            return ExtensionKind.SEND_HTTP_RESPONSE;
                        }
                    }

                    record FileWrite() implements ActivityExtension.Config {

                        @Override
                        public ExtensionKind kind() {
                            return ExtensionKind.FILE_WRITE;
                        }
                    }

                    record Log() implements ActivityExtension.Config {

                        @Override
                        public ExtensionKind kind() {
                            return ExtensionKind.LOG;
                        }
                    }

                    record RenderXML() implements ActivityExtension.Config {

                        @Override
                        public ExtensionKind kind() {
                            return ExtensionKind.RENDER_XML;
                        }
                    }

                    record Mapper() implements ActivityExtension.Config {

                        @Override
                        public ExtensionKind kind() {
                            return ExtensionKind.MAPPER;
                        }
                    }

                    record AccumulateEnd(String activityName) implements ActivityExtension.Config {

                        @Override
                        public ExtensionKind kind() {
                            return ExtensionKind.ACCUMULATE_END;
                        }
                    }

                    record SQL(String sharedResourcePropertyName, String query,
                               List<ActivityExtension.Config.SQL.Column> resultColumns,
                               List<ActivityExtension.Config.SQL.SQLParameter> parameters) implements
                            ActivityExtension.Config {

                        @Override
                        public ExtensionKind kind() {
                            return ExtensionKind.SQL;
                        }

                        public enum SQLType {
                            INTEGER,
                            BIGINT,
                            SMALLINT,
                            DECIMAL,
                            NUMERIC,
                            REAL,
                            DOUBLE,
                            VARCHAR,
                            CHAR,
                            TEXT,
                            DATE,
                            TIME,
                            TIMESTAMP,
                            BOOLEAN,
                            BLOB,
                            CLOB;

                            public static SQL.SQLType fromString(String type) {
                                return switch (type.toUpperCase()) {
                                    case "INTEGER", "INT", "INT4" -> INTEGER;
                                    case "BIGINT", "INT8" -> BIGINT;
                                    case "SMALLINT", "INT2" -> SMALLINT;
                                    case "DECIMAL", "DEC" -> DECIMAL;
                                    case "NUMERIC", "NUMBER" -> NUMERIC;
                                    case "REAL", "FLOAT4" -> REAL;
                                    case "DOUBLE", "FLOAT8" -> DOUBLE;
                                    case "VARCHAR", "VARCHAR2", "NVARCHAR" -> VARCHAR;
                                    case "CHAR", "CHARACTER" -> CHAR;
                                    case "TEXT" -> TEXT;
                                    case "DATE" -> DATE;
                                    case "TIME" -> TIME;
                                    case "TIMESTAMP", "DATETIME" -> TIMESTAMP;
                                    case "BOOLEAN", "BOOL" -> BOOLEAN;
                                    case "BLOB", "BINARY LARGE OBJECT" -> BLOB;
                                    case "CLOB", "CHARACTER LARGE OBJECT" -> CLOB;
                                    default -> throw new IllegalArgumentException("Unknown SQL type: " + type);
                                };
                            }
                        }

                        public record Column(String name, SQL.SQLType type, boolean isOptional) {

                        }

                        public record SQLParameter(String name, SQL.SQLType type) {

                        }
                    }

                    enum ExtensionKind {
                        ACCUMULATE_END,
                        END,
                        FILE_WRITE,
                        HTTP_SEND,
                        JSON_PARSER,
                        JSON_RENDER,
                        LOG,
                        RENDER_XML,
                        SEND_HTTP_RESPONSE,
                        MAPPER,
                        SQL;

                        public static ActivityExtension.Config.ExtensionKind fromTypeId(String typeId) {
                            return switch (typeId) {
                                case "bw.internal.end" -> END;
                                case "bw.http.sendHTTPRequest" -> HTTP_SEND;
                                case "bw.restjson.JsonRender" -> JSON_RENDER;
                                case "bw.restjson.JsonParser" -> JSON_PARSER;
                                case "bw.http.sendHTTPResponse" -> SEND_HTTP_RESPONSE;
                                case "bw.file.write" -> FILE_WRITE;
                                case "bw.generalactivities.log" -> LOG;
                                case "bw.xml.renderxml" -> RENDER_XML;
                                case "bw.generalactivities.mapper" -> MAPPER;
                                case "bw.internal.accumulateend" -> ACCUMULATE_END;
                                default -> patternMatch(typeId);
                            };
                        }

                        private static ActivityExtension.Config.ExtensionKind patternMatch(String typeId) {
                            if (typeId.contains("jdbc")) {
                                return SQL;
                            }
                            throw new IllegalArgumentException("Unknown extension kind: " + typeId);
                        }

                    }
                }

            }

            record Invoke(String inputVariable, String outputVariable, Method operation, String partnerLink,
                          List<Flow.Activity.InputBinding> inputBindings, Collection<Flow.Activity.Target> targets,
                          List<Flow.Activity.Source> sources,
                          Element element, String fileName)
                    implements Flow.Activity, Flow.Activity.ActivityWithSources, 
                    Flow.Activity.ActivityWithTargets, Flow.Activity.ActivityWithOutput {

                @Override
                public Optional<String> outVariableName() {
                    return Optional.of(outputVariable);
                }
            }

            record Target(String linkName) {

            }

            record Source(String linkName, Optional<Flow.Activity.Source.Predicate> condition) {

                public Source(String linkName) {
                    this(linkName, Optional.empty());
                }

                public sealed interface Predicate {

                    record Else() implements Flow.Activity.Source.Predicate {

                    }
                }
            }

            sealed interface InputBinding {

                record CompleteBinding(Flow.Activity.Expression expression) implements Flow.Activity.InputBinding {

                    public Flow.Activity.Expression.XSLT xslt() {
                        if (expression instanceof Flow.Activity.Expression.XSLT xslt) {
                            return xslt;
                        }
                        throw new IllegalStateException("Not an XSLT expression: " + expression);
                    }
                }

                record PartialBindings(List<Flow.Activity.Expression> expressions) implements
                        Flow.Activity.InputBinding {

                    public List<Flow.Activity.Expression.XSLT> xslt() {
                        return expressions.stream()
                                .map(expr -> {
                                    if (expr instanceof Flow.Activity.Expression.XSLT xslt) {
                                        return xslt;
                                    }
                                    throw new IllegalStateException("Not an XSLT expression: " + expr);
                                })
                                .toList();
                    }
                }
            }

        }
    }
}
