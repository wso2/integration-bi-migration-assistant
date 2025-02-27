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
                          Collection<PartnerLink> partnerLinks, Collection<Variable> variables, Scope scope) {

        public Process {
            assert name != null;
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

        record WSDLDefinition(Map<String, String> namespaces, PartnerLinkType partnerLinkType,
                              Collection<NameSpace> imports, Collection<Message> messages,
                              Collection<PortType> portTypes) implements Type {

            public record PartnerLinkType(String name, Role role) {

                public record Role(String name, NameSpaceValue portType) {

                }
            }

            public record Message(String name, List<Part> parts) {

                public record Part(String name, Optional<NameSpaceValue> element, boolean hasMultipleNamespaces) {

                }
            }

            public record PortType(String name, String apiPath, Operation operation) {

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

        record Schema(Collection<ComplexType> types, Collection<Element> elements, Collection<NameSpace> imports)
                implements Type {

            public record Element(String name, TibcoType type) {

            }

            public record TibcoType(String name) {

                private static final HashMap<String, TibcoType> TYPES = new HashMap<>();

                public static TibcoType of(String name) {
                    return TYPES.computeIfAbsent(name, TibcoType::new);
                }

            }

            public record ComplexType(String name, Body body) {

                public ComplexType {
                    assert name != null;
                    assert body != null;
                }

                public sealed interface Body {

                }

                public record Choice(Collection<Element> elements) implements Body {

                    public record Element(int maxOccurs, int minOccurs, TibcoType ref) {

                    }
                }

                public record SequenceBody(Collection<Member> elements) implements Body {

                    public sealed interface Member {

                        record Element(String name, TibcoType type) implements Member {

                        }

                        record Rest(boolean isLax) implements Member {

                        }
                    }
                }

                public record ComplexContent(Extension extension) implements Body {

                    public record Extension(TibcoType base, Collection<SequenceBody.Member.Element> elements) {

                        public Extension {
                            assert base != null;
                            elements = Collections.unmodifiableCollection(elements);
                        }
                    }
                }

            }
        }
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

    public record PartnerLink(String name, Binding Binding) {

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
                        if (value.equals("POST")) {
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

            public record Link(String name) {

            }

            public sealed interface Activity {

                sealed interface Expression {

                    record XSLT(String expression) implements Expression {

                    }
                }

                record ReceiveEvent(boolean createInstance, float eventTimeout, String variable,
                                    Collection<Source> sources) implements Activity {

                    public record Source(String linkName) {

                    }
                }

                record ActivityExtension(Expression expression, String inputVariable, Collection<Target> targets,
                                         Collection<InputBinding> inputBindings, Config config) implements Activity {

                    public record Config() {

                    }
                }

                record Invoke(String inputVariable, String outputVariable, Operation operation, String partnerLink,
                              List<InputBinding> inputBindings, Collection<Target> targets, Collection<Source> sources)
                        implements Activity {

                    public enum Operation {
                        POST
                    }

                }

                record Target(String linkName) {

                }

                record Source(String linkName) {

                }

                record InputBinding(Expression expression) {

                }

            }
        }
    }

    public record NameSpace(String nameSpace) {

    }

    public record NameSpaceValue(NameSpace nameSpace, String value) {

        public static NameSpaceValue from(String value) {
            String[] parts = value.split(":");
            return new NameSpaceValue(new NameSpace(parts[0]), parts[1]);
        }
    }

}