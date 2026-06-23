/*
 *  Copyright (c) 2026, WSO2 LLC. (http://www.wso2.com).
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
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package synapse.model;

import java.util.List;

public record Synapse() {

    public record Api(Kind kind, String name, String context, List<SynapseNode> resources)
            implements SynapseNode {
        public Api(String name, String context, List<SynapseNode> resources) {
            this(Kind.API, name, context, resources);
        }
    }

    public record Resource(Kind kind, String methods, String path,
                           List<String> queryParams, InSequence inSequence) implements SynapseNode {
        public Resource(String methods, String path, List<String> queryParams, InSequence inSequence) {
            this(Kind.RESOURCE, methods, path, queryParams, inSequence);
        }
    }

    // <inSequence> ... </inSequence> -> the request-processing mediator sequence of a resource.
    public record InSequence(Kind kind, List<SynapseNode> mediators) implements SynapseNode {
        public InSequence(List<SynapseNode> mediators) {
            this(Kind.IN_SEQUENCE, mediators);
        }
    }

    // <sequence name="..." onError="..." description="..."> ... </sequence>
    // -> a named, reusable mediator sequence declared at the top level.
    public record Sequence(Kind kind, String name, String onError, String description,
                           List<SynapseNode> mediators) implements SynapseNode {
        public Sequence(String name, String onError, String description, List<SynapseNode> mediators) {
            this(Kind.SEQUENCE, name, onError, description, mediators);
        }
    }

    // <payloadFactory media-type="json"><format>{"Hello":"World"}</format></payloadFactory>
    // -> sets the response payload to the given format (of the given media type).
    public record PayloadFactory(Kind kind, String mediaType, String format) implements SynapseNode {
        public PayloadFactory(String mediaType, String format) {
            this(Kind.PAYLOAD_FACTORY, mediaType, format);
        }
    }

    // <respond/> -> sends the current message back as the response.
    public record Respond(Kind kind) implements SynapseNode {
        public Respond() {
            this(Kind.RESPOND);
        }
    }

    // <sequence key="name"/> -> invokes the named sequence referenced by 'key'.
    public record SequenceMediator(Kind kind, String key) implements SynapseNode {
        public SequenceMediator(String key) {
            this(Kind.SEQUENCE_MEDIATOR, key);
        }
    }

    // <property name="..." scope="default|transport|axis2|axis2-client" type="string" value="..."/>
    // -> sets a named property (of the given type and scope) to the given value.
    public record Property(Kind kind, String name, String type, String scope, String value)
            implements SynapseNode {
        public Property(String name, String type, String scope, String value) {
            this(Kind.PROPERTY, name, type, scope, value);
        }
    }

    public interface SynapseNode {
        Kind kind();
    }

    public enum Kind {
        API,
        RESOURCE,
        IN_SEQUENCE,
        SEQUENCE,
        PAYLOAD_FACTORY,
        RESPOND,
        PROPERTY,
        SEQUENCE_MEDIATOR
    }
}
