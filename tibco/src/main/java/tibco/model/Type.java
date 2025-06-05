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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public sealed interface Type {

    record WSDLDefinition(Map<String, String> namespaces, Collection<WSDLDefinition.PartnerLinkType> partnerLinkTypes,
                          Collection<NameSpace> imports, Collection<WSDLDefinition.Message> messages,
                          Collection<WSDLDefinition.PortType> portType) implements Type {

        public record PartnerLinkType(String name, WSDLDefinition.PartnerLinkType.Role role) {

            public record Role(String name, NameSpaceValue portType) {

            }
        }

        public record Message(String name, List<WSDLDefinition.Message.Part> parts) {

            public sealed interface Part {

                String name();

                record Reference(String name, NameSpaceValue element, boolean hasMultipleNamespaces)
                        implements WSDLDefinition.Message.Part {

                }

                record InlineError(String name, String value, String type) implements WSDLDefinition.Message.Part {

                }
            }
        }

        public record PortType(String name, String apiPath, String basePath,
                               WSDLDefinition.PortType.Operation operation) {

            public record Operation(String name, WSDLDefinition.PortType.Operation.Input input,
                                    WSDLDefinition.PortType.Operation.Output output,
                                    Collection<WSDLDefinition.PortType.Operation.Fault> faults) {

                public record Input(NameSpaceValue message, String name) {

                }

                public record Output(NameSpaceValue message, String name) {

                }

                public record Fault(NameSpaceValue message, String name) {

                }
            }
        }

    }

    record Schema(Element element) implements Type {

    }

    record TibcoType(String name) {

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
}
