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

    public record Process(String name, Types types, ProcessInfo processInfo) {

    }

    // TODO: may be line this record (just a holder of collection)
    public record Types(Collection<Members> types) {

        public Types {
            types = Collections.unmodifiableCollection(types);
        }

        public interface Members {

        }

        public record WSDLDefinition(Map<String, String> namespaces, PartnerLinkType partnerLinkType,
                                     Collection<NameSpace> imports, Collection<Message> messages,
                                     Collection<PortType> portTypes) implements Members {

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

        public record Schema(Collection<Type> types, Collection<Element> elements, Collection<NameSpace> imports)
                implements
                Members {

            public interface Type {

                String name();
            }

            public record Element(String name, TibcoType type) {

            }

            public record TibcoType(String name) {

                private static final HashMap<String, TibcoType> TYPES = new HashMap<>();

                public static TibcoType of(String name) {
                    return TYPES.computeIfAbsent(name, TibcoType::new);
                }

            }

            public record ComplexType(String name, Body body) implements Type {

                public ComplexType {
                    assert name != null;
                    assert body != null;
                }

                public interface Body {

                }

                public record Choice(Collection<Element> elements) implements Body {

                    public record Element(int maxOccurs, int minOccurs, TibcoType ref) {

                    }
                }

                public record SequenceBody(Collection<Element> elements) implements Body {

                    public interface Element {

                    }
                }

                public record Element(String name, TibcoType type) implements SequenceBody.Element {

                }

                public record ComplexContent(Extension extension) implements Body {

                    public record Extension(TibcoType base, Collection<Element> elements) implements Body {

                        public Extension {
                            assert base != null;
                            elements = Collections.unmodifiableCollection(elements);
                        }
                    }
                }

                public record Rest(boolean isLax) implements SequenceBody.Element {

                }

                public record Elements(Collection<Element> elements) implements Body {

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
    public record NameSpace(String nameSpace) {

    }

    public record NameSpaceValue(NameSpace nameSpace, String value) {

        public static NameSpaceValue from(String value) {
            String[] parts = value.split(":");
            return new NameSpaceValue(new NameSpace(parts[0]), parts[1]);
        }
    }

}