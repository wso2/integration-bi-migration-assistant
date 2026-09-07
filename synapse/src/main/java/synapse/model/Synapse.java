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
import java.util.Optional;
import java.util.function.Predicate;

public record Synapse() {

    // The conventional project-level sequence name a resource with no faultSequence of its own falls
    // back to implicitly, if one exists.
    public static final String DEFAULT_FAULT_SEQUENCE_KEY = "fault";

    // An http/https inbound endpoint parameter: a regex selecting which deployed <api> artifacts are
    // additionally exposed on the endpoint's own listener.
    public static final String DISPATCH_FILTER_PATTERN_PARAM = "dispatch.filter.pattern";

    // The default-scope property Synapse itself populates with the caught error's message on entry to
    // fault handling. Unlike a property set by a <property> mediator, it is always treated as an
    // available default-scope property (see ConversionContext#availableDefaultScopeProperties) rather
    // than only once discovered while converting a fault handler.
    public static final String ERROR_MESSAGE_PROPERTY = "ERROR_MESSAGE";

    public record Api(Kind kind, String name, String context, List<SynapseNode> resources)
            implements SynapseNode {
        public Api(String name, String context, List<SynapseNode> resources) {
            this(Kind.API, name, context, resources);
        }
    }

    // matchAnyPath is set when the Synapse resource declares neither 'uri-template' nor 'url-mapping';
    // such a resource matches any path, converted to a Ballerina rest path parameter ([string... path]).
    public record Resource(Kind kind, String methods, String path, boolean matchAnyPath,
                           List<String> queryParams, InSequence inSequence,
                           FaultSequenceRef faultSequenceRef) implements SynapseNode {
        public Resource(String methods, String path, boolean matchAnyPath, List<String> queryParams,
                        InSequence inSequence, FaultSequenceRef faultSequenceRef) {
            this(Kind.RESOURCE, methods, path, matchAnyPath, queryParams, inSequence, faultSequenceRef);
        }

        // A KeyRef naming no known sequence, or no faultSequence at all (None), means this resource
        // falls back to the project's default "fault" sequence. sequenceExists tells whether a given
        // key names a known sequence, in whichever form the caller has that knowledge available.
        public boolean fallsBackToDefaultFaultSequence(Predicate<String> sequenceExists) {
            assert sequenceExists != null : "sequenceExists must not be null";
            return switch (faultSequenceRef) {
                case FaultSequenceRef.KeyRef(String key) -> !sequenceExists.test(key);
                case FaultSequenceRef.Inline ignored -> false;
                case FaultSequenceRef.None ignored -> true;
            };
        }
    }

    // A resource's faultSequence attribute/inline element, resolved to a single reference at parse
    // time: at most one of the two ever applies for a given resource, mirroring Synapse's own
    // ResourceFactory#configureSequences.
    public sealed interface FaultSequenceRef {
        // <resource faultSequence="key"> -> a named reference to a sequence declared elsewhere.
        record KeyRef(String key) implements FaultSequenceRef {
        }

        // <resource><faultSequence>...</faultSequence></resource> -> the inline sequence itself.
        record Inline(FaultSequence sequence) implements FaultSequenceRef {
        }

        // Neither a faultSequence attribute nor an inline element was present.
        record None() implements FaultSequenceRef {
        }
    }

    // <inSequence> ... </inSequence> -> the request-processing mediator sequence of a resource.
    public record InSequence(Kind kind, List<SynapseNode> mediators) implements SynapseNode {
        public InSequence(List<SynapseNode> mediators) {
            this(Kind.IN_SEQUENCE, mediators);
        }
    }

    // <faultSequence> ... </faultSequence> -> the error-handling mediator sequence of a resource, run
    // when mediating its inSequence fails. Converted to a Ballerina 'on fail' clause.
    public record FaultSequence(Kind kind, List<SynapseNode> mediators) implements SynapseNode {
        public FaultSequence(List<SynapseNode> mediators) {
            this(Kind.FAULT_SEQUENCE, mediators);
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
  
    // <class name="org.example.MyMediator">
    //   <property name="key" value="val"/>
    // </class>
    public record ClassMediator(Kind kind, String className, List<Property> properties) implements SynapseNode {
        public ClassMediator(String className, List<Property> properties) {
            this(Kind.CLASS_MEDIATOR, className, properties);
        }
    }

    // <property name="..." scope="default|transport|axis2|axis2-client" type="string" value="..."
    //           expression="..." action="set|remove"> <om-element/>? </property>
    // -> action "set" (the default) sets a named property (of the given type and scope) to the given
    //    value or expression (mutually exclusive; expression holds a Synapse XPath), or to the inline XML
    //    child element carried in omElement; action "remove" clears it. A present omElement makes the
    //    property an XML (OM) value regardless of the declared type.
    public record Property(Kind kind, String name, SynapseType type, String scope, String value,
                           String expression, String omElement, String action) implements SynapseNode {
        public Property(String name, SynapseType type, String scope, String value, String expression,
                        String omElement, String action) {
            this(Kind.PROPERTY, name, type, scope, value, expression, omElement, action);
        }

        public boolean hasExpression() {
            return expression != null && !expression.isEmpty();
        }

        public boolean hasOmElement() {
            return omElement != null && !omElement.isEmpty();
        }
    }

    // An unsupported mediator captured verbatim so it can be surfaced as a to-do rather than silently
    // dropped. rawXml is the serialized Synapse element; children holds any nested mediators recognised
    // inside a control-flow wrapper (e.g. a <filter>'s <then>/<else>) so they can still be converted.
    public record Unsupported(Kind kind, String tag, String rawXml, List<SynapseNode> children)
            implements SynapseNode {
        public Unsupported(String tag, String rawXml, List<SynapseNode> children) {
            this(Kind.UNSUPPORTED_MEDIATOR, tag, rawXml, children);
        }
    }

    // An unsupported top-level artifact (e.g. <proxy>, <endpoint>) captured verbatim so it can be
    // surfaced in the migration report rather than silently dropped.
    public record UnsupportedArtifact(Kind kind, String tag, String name, String rawXml)
            implements SynapseNode {
        public UnsupportedArtifact(String tag, String name, String rawXml) {
            this(Kind.UNSUPPORTED_ARTIFACT, tag, name, rawXml);
        }
    }

    // <inboundEndpoint name=".." sequence=".." onError=".." protocol=".." class=".." suspend="..">
    //   <parameters><parameter name="inbound.http.port">8085</parameter>...</parameters>
    // </inboundEndpoint>
    // -> a transport-level entry point that forwards every message it receives into 'sequence',
    // routing failures to 'onError'. protocol and className are mutually exclusive per the Synapse
    // schema; className is empty when a built-in protocol is used.
    public record InboundEndpoint(Kind kind, String name, String protocol, String className, String sequenceKey,
                                  FaultSequenceRef onErrorRef, List<Param> parameters,
                                  Optional<KeyStoreConfig> keyStore, boolean suspend, String rawXml)
            implements SynapseNode {
        public InboundEndpoint(String name, String protocol, String className, String sequenceKey,
                               FaultSequenceRef onErrorRef, List<Param> parameters,
                               Optional<KeyStoreConfig> keyStore, boolean suspend, String rawXml) {
            this(Kind.INBOUND_ENDPOINT, name, protocol, className, sequenceKey, onErrorRef, parameters, keyStore,
                    suspend, rawXml);
        }
    }

    // A single <parameter name="key">value</parameter> under <inboundEndpoint>/<parameters>.
    public record Param(String name, String value) {
    }

    // An inbound endpoint's server identity keystore. keyPassword is only present when it differs from
    // the keystore's own password.
    public record KeyStoreConfig(String location, String type, String password, Optional<String> keyPassword) {
    }

    public interface SynapseNode {
        Kind kind();
    }

    public enum Kind {
        API,
        RESOURCE,
        IN_SEQUENCE,
        FAULT_SEQUENCE,
        SEQUENCE,
        PAYLOAD_FACTORY,
        RESPOND,
        PROPERTY,
        SEQUENCE_MEDIATOR,
        CLASS_MEDIATOR,
        UNSUPPORTED_MEDIATOR,
        UNSUPPORTED_ARTIFACT,
        INBOUND_ENDPOINT
    }
}
