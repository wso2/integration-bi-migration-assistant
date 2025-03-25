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

package tibco;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class TibcoModel {

    public record Process(String name, Collection<Type> types, ProcessInfo processInfo,
                          Optional<ProcessInterface> processInterface,
                          Optional<ProcessTemplateConfigurations> processTemplateConfigurations,
                          Collection<PartnerLink> partnerLinks, Collection<Variable> variables, Optional<Scope> scope) {

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Process other)) {
                return false;
            }
            return other.name().equals(name);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        public Process {
            if (name == null) {
                throw new IllegalArgumentException("Name cannot be null");
            }
            if (types == null) {
                types = List.of();
            } else {
                types = Collections.unmodifiableCollection(types);
            }
            if (partnerLinks == null) {
                partnerLinks = List.of();
            } else {
                partnerLinks = Collections.unmodifiableCollection(partnerLinks);
            }
            if (variables == null) {
                variables = List.of();
            } else {
                variables = Collections.unmodifiableCollection(variables);
            }
        }

    }

    public sealed interface Type {

        record WSDLDefinition(Map<String, String> namespaces, Collection<PartnerLinkType> partnerLinkTypes,
                              Collection<NameSpace> imports, Collection<Message> messages,
                              Collection<PortType> portType) implements Type {

            public record PartnerLinkType(String name, Role role) {

                public record Role(String name, NameSpaceValue portType) {

                }
            }

            public record Message(String name, List<Part> parts) {

                public sealed interface Part {

                    String name();

                    record Reference(String name, NameSpaceValue element, boolean hasMultipleNamespaces)
                            implements Part {

                    }

                    record InlineError(String name, String value, String type) implements Part {

                    }
                }
            }

            public record PortType(String name, String apiPath, String basePath, Operation operation) {

                public record Operation(String name, Input input, Output output, Collection<Fault> faults) {

                    public record Input(NameSpaceValue message, String name) {

                    }

                    public record Output(NameSpaceValue message, String name) {

                    }

                    public record Fault(NameSpaceValue message, String name) {

                    }
                }
            }

        }

        record Schema(NameSpace targetNamespace, Collection<ComplexType> types, Collection<Element> elements,
                      Collection<NameSpace> imports,
                      Collection<UnhandledType> unhandledTypes)
                implements Type {

            public record Element(String name, TibcoType type) {

            }

            public record TibcoType(String name) {

                public TibcoType {
                    if (name == null || name.isEmpty()) {
                        throw new IllegalArgumentException("Name cannot be null or empty");
                    }
                }

                private static final HashMap<String, TibcoType> TYPES = new HashMap<>();

                public static TibcoType of(String name) {
                    return TYPES.computeIfAbsent(name, TibcoType::new);
                }

            }

            public record UnhandledType(String name, String reason, String elementAsString) {

                public UnhandledType {
                    if (name == null || name.isEmpty()) {
                        throw new IllegalArgumentException("Name cannot be null or empty");
                    }
                }
            }

            public record ComplexType(String name, Body body) {

                public ComplexType {
                    if (name == null) {
                        throw new IllegalArgumentException("Name cannot be null");
                    }
                    if (body == null) {
                        throw new IllegalArgumentException("Body cannot be null");
                    }
                }

                public sealed interface Body {

                }

                public record Choice(Collection<Element> elements) implements Body, SequenceBody.Member {

                    public record Element(int maxOccurs, int minOccurs, TibcoType ref) {

                    }
                }

                public record SequenceBody(Collection<Member> elements) implements Body {

                    public sealed interface Member {

                        record ElementArray(String name, TibcoType elementType, int min, int max) implements Member {

                        }

                        record Element(String name, TibcoType type, boolean optional) implements Member {

                            public Element {
                                if (name == null || name.isEmpty()) {
                                    throw new IllegalArgumentException("Name cannot be null or empty");
                                }
                            }
                        }

                        record Rest(Optional<TibcoType> type) implements Member {

                            public Rest(TibcoType type) {
                                this(Optional.of(type));
                            }

                            public Rest() {
                                this(Optional.empty());
                            }
                        }
                    }
                }

                public record ComplexContent(Extension extension) implements Body {

                    public record Extension(TibcoType base, Collection<SequenceBody.Member.Element> elements) {

                        public Extension {
                            if (base == null) {
                                throw new IllegalArgumentException("Base cannot be null");
                            }
                            elements = Collections.unmodifiableCollection(elements);
                        }
                    }
                }

                public record SimpleContent(TibcoType base) implements Body {

                }
            }
        }
    }

    public record ProcessInterface(String context, String input, String output) {

    }

    // TODO: fill this
    public record ProcessTemplateConfigurations() {

    }

    public record ProcessInfo(boolean callable, boolean extraErrorVars, Set<Modifier> modifiers, boolean scalable,
                              boolean singleton, boolean stateless, Type type) {

        public enum Modifier {
            PUBLIC
        }

        public enum Type {
            IT
        }
    }

    public record Variable(String name, boolean isInternal) {

    }

    public record PartnerLink(String name, Optional<Binding> binding) {

        public record Binding(Path path, Connector connector, Operation operation) {

            public record Path(String basePath, String path) {

            }

            public enum Connector {
                HTTP_CLIENT_RESOURCE_2;

                public static Connector from(String value) {
                    if (value.contains("HttpClientResource2")) {
                        return HTTP_CLIENT_RESOURCE_2;
                    }
                    throw new IllegalArgumentException("Unknown connector: " + value);
                }
            }

            public record Operation(Method method, RequestEntityProcessing requestEntityProcessing,
                                    MessageStyle requestStyle, MessageStyle responseStyle, Format clientFormat,
                                    Format clientRequestFormat, List<Parameter> parameters) {

                public enum Method {
                    POST;

                    public static Method from(String value) {
                        if (value.equalsIgnoreCase("post")) {
                            return POST;
                        }
                        throw new IllegalArgumentException("Unknown method: " + value);
                    }
                }

                public enum RequestEntityProcessing {
                    CHUNKED;

                    public static RequestEntityProcessing from(String value) {
                        if (value.equals("chunked")) {
                            return CHUNKED;
                        }
                        throw new IllegalArgumentException("Unknown request entity processing: " + value);
                    }
                }

                public enum MessageStyle {
                    ELEMENT;

                    public static MessageStyle from(String value) {
                        if (value.equals("element")) {
                            return ELEMENT;
                        }
                        throw new IllegalArgumentException("Unknown message style: " + value);
                    }
                }

                public enum Format {
                    JSON
                }

                public record Parameter() {

                }
            }

        }

    }

    public record Scope(String name, Collection<Flow> flows) {

        public record Flow(String name, Collection<Link> links, List<Activity> activities) {

            public Flow {
                assert name != null;
                assert links != null;
                assert activities != null;
            }

            public record Link(String name) {

            }

            public sealed interface Activity {

                sealed interface ActivityWithSources extends Activity {

                    Collection<Source> sources();
                }

                sealed interface ActivityWithTargets extends Activity {

                    Collection<Target> targets();

                }

                sealed interface Expression {

                    record XSLT(String expression) implements Expression {

                    }
                }

                record UnhandledActivity(String reason, String elementAsString, Collection<Source> sources,
                        Collection<Target> targets) implements Activity, ActivityWithSources,
                        ActivityWithTargets {

                }

                record Reply(String name, PartnerLink.Binding.Operation.Method operation, String partnerLink,
                             String portType, List<InputBinding> inputBindings, Collection<Target> targets)
                        implements Activity, ActivityWithTargets {

                }

                record Empty(String name) implements Activity {

                }

                record Pick(boolean createInstance, OnMessage onMessage) implements Activity {

                    public record OnMessage(PartnerLink.Binding.Operation.Method operation, String partnerLink,
                                            String portType, String variable, Scope scope) {

                    }
                }

                record ReceiveEvent(boolean createInstance, float eventTimeout, String variable,
                                    Collection<Source> sources) implements Activity, ActivityWithSources {

                }

                record ExtActivity(Expression expression, String inputVariable, String outputVariable,
                                   Collection<Source> sources, List<InputBinding> inputBindings,
                                   CallProcess callProcess) implements Activity, ActivityWithSources {

                    public record CallProcess(String subprocessName) {

                    }
                }

                record ActivityExtension(String inputVariable,
                                         Optional<String> outputVariable,
                                         Collection<Target> targets, Collection<Source> sources,
                                         List<InputBinding> inputBindings, Config config)
                        implements Activity, ActivityWithTargets, ActivityWithSources {

                    public sealed interface Config {

                        ExtensionKind kind();

                        record End() implements Config {

                            @Override
                            public ExtensionKind kind() {
                                return ExtensionKind.END;
                            }
                        }

                        record HTTPSend() implements Config {

                            @Override
                            public ExtensionKind kind() {
                                return ExtensionKind.HTTP_SEND;
                            }
                        }

                        record JsonOperation(ExtensionKind kind, Type.Schema.TibcoType type) implements Config {

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

                        record SendHTTPResponse() implements Config {

                            @Override
                            public ExtensionKind kind() {
                                return ExtensionKind.SEND_HTTP_RESPONSE;
                            }
                        }

                        record FileWrite() implements Config {

                            @Override
                            public ExtensionKind kind() {
                                return ExtensionKind.FILE_WRITE;
                            }
                        }

                        record Log() implements Config {

                            @Override
                            public ExtensionKind kind() {
                                return ExtensionKind.LOG;
                            }
                        }

                        record RenderXML() implements Config {

                            @Override
                            public ExtensionKind kind() {
                                return ExtensionKind.RENDER_XML;
                            }
                        }

                        record SQL(String sharedResourcePropertyName, String query,
                                   List<SQLParameter> parameters) implements Config {

                            @Override
                            public ExtensionKind kind() {
                                return ExtensionKind.SQL;
                            }

                            public record SQLParameter(String name, SQLType type) {

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

                                    public static SQLType fromString(String type) {
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
                            }
                        }

                        enum ExtensionKind {
                            END,
                            FILE_WRITE,
                            HTTP_SEND,
                            JSON_PARSER,
                            JSON_RENDER,
                            LOG,
                            RENDER_XML,
                            SEND_HTTP_RESPONSE,
                            SQL;

                            public static ExtensionKind fromTypeId(String typeId) {
                                return switch (typeId) {
                                    case "bw.internal.end" -> END;
                                    case "bw.http.sendHTTPRequest" -> HTTP_SEND;
                                    case "bw.restjson.JsonRender" -> JSON_RENDER;
                                    case "bw.restjson.JsonParser" -> JSON_PARSER;
                                    case "bw.http.sendHTTPResponse" -> SEND_HTTP_RESPONSE;
                                    case "bw.file.write" -> FILE_WRITE;
                                    case "bw.generalactivities.log" -> LOG;
                                    case "bw.xml.renderxml" -> RENDER_XML;
                                    default -> patternMatch(typeId);
                                };
                            }

                            private static ExtensionKind patternMatch(String typeId) {
                                if (typeId.contains("jdbc")) {
                                    return SQL;
                                }
                                throw new IllegalArgumentException("Unknown extension kind: " + typeId);
                            }

                        }
                    }

                }

                record Invoke(String inputVariable, String outputVariable, Operation operation, String partnerLink,
                              List<InputBinding> inputBindings, Collection<Target> targets, Collection<Source> sources)
                        implements Activity, ActivityWithSources, ActivityWithTargets {

                    public enum Operation {
                        POST
                    }

                }

                record Target(String linkName) {

                }

                record Source(String linkName) {

                }

                record InputBinding(Expression expression) {

                    public Expression.XSLT xslt() {
                        if (expression instanceof Expression.XSLT xslt) {
                            return xslt;
                        }
                        throw new IllegalStateException("Not an XSLT expression: " + expression);
                    }
                }

            }
        }
    }

    public record NameSpace(Optional<String> prefix, String uri) {

        public NameSpace(String nameSpace) {
            this(Optional.empty(), nameSpace);
        }

        public NameSpace(String prefix, String nameSpace) {
            this(Optional.of(prefix), nameSpace);
        }
    }

    public record NameSpaceValue(NameSpace nameSpace, String value) {

        public static NameSpaceValue from(String value) {
            String[] parts = value.split(":");
            return new NameSpaceValue(new NameSpace(parts[0]), parts[1]);
        }
    }

}
