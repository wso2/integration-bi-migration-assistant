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

import java.util.List;
import java.util.Optional;

public sealed interface PartnerLink {

    record UnhandledPartnerLink(Optional<String> name, String reason) implements PartnerLink {

        public UnhandledPartnerLink(String name, String reason) {
            this(name == null || name.isEmpty() ? Optional.empty() : Optional.of(name), reason);
        }
    }

    record EmptyPartnerLink(String name) implements PartnerLink {

    }

    sealed interface NonEmptyPartnerLink extends PartnerLink {

        String name();

        Binding binding();

    }

    record SoapPartnerLink(String name, String path) implements NonEmptyPartnerLink {

        private static final Binding.Operation OPERATION = new Binding.Operation(Method.POST,
                Binding.Operation.RequestEntityProcessing.CHUNKED, Binding.Operation.MessageStyle.ELEMENT,
                Binding.Operation.MessageStyle.ELEMENT, Binding.Operation.Format.XML, Binding.Operation.Format.XML,
                List.of());

        @Override
        public Binding binding() {
            return new Binding(new Binding.Path(path, ""), Binding.Connector.HTTP_CLIENT_RESOURCE_2, OPERATION);
        }
    }

    record RestPartnerLink(String name, Binding binding) implements NonEmptyPartnerLink {

    }

    record Binding(Binding.Path path, Binding.Connector connector, Binding.Operation operation) {

        public record Path(String basePath, String path) {

        }

        public enum Connector {
            HTTP_CLIENT_RESOURCE,
            HTTP_CLIENT_RESOURCE_2;

            public static Binding.Connector from(String value) {
                if (value.contains("HttpClientResource")) {
                    return HTTP_CLIENT_RESOURCE;
                }
                if (value.contains("HttpClientResource2")) {
                    return HTTP_CLIENT_RESOURCE_2;
                }
                throw new IllegalArgumentException("Unknown connector: " + value);
            }
        }

        public record Operation(Method method,
                                Optional<Binding.Operation.RequestEntityProcessing> requestEntityProcessing,
                                Optional<Binding.Operation.MessageStyle> requestStyle,
                                Optional<Binding.Operation.MessageStyle> responseStyle,
                                Optional<Binding.Operation.Format> clientFormat,
                                Optional<Binding.Operation.Format> clientRequestFormat,
                                List<Binding.Operation.Parameter> parameters) {

            public Operation(Method method, Binding.Operation.RequestEntityProcessing requestEntityProcessing,
                             Binding.Operation.MessageStyle requestStyle, Binding.Operation.MessageStyle responseStyle,
                             Binding.Operation.Format clientFormat,
                             Binding.Operation.Format clientRequestFormat,
                             List<Binding.Operation.Parameter> parameters) {
                this(method, Optional.of(requestEntityProcessing), Optional.of(requestStyle),
                        Optional.of(responseStyle), Optional.of(clientFormat), Optional.of(clientRequestFormat),
                        parameters);
            }

            public enum RequestEntityProcessing {
                CHUNKED;

                public static Binding.Operation.RequestEntityProcessing from(String value) {
                    if (value.equals("chunked")) {
                        return CHUNKED;
                    }
                    throw new IllegalArgumentException("Unknown request entity processing: " + value);
                }
            }

            public enum MessageStyle {
                ELEMENT;

                public static Binding.Operation.MessageStyle from(String value) {
                    if (value.equals("element")) {
                        return ELEMENT;
                    }
                    throw new IllegalArgumentException("Unknown message style: " + value);
                }
            }

            public enum Format {
                JSON,
                XML;
            }

            public record Parameter() {

            }
        }

    }
}
