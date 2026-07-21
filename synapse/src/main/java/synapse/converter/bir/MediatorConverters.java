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
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package synapse.converter.bir;

import synapse.converter.ScopeContext;
import synapse.converter.bir.mediators.PayloadFactoryConverter;
import synapse.converter.bir.mediators.PropertyConverter;
import synapse.converter.bir.mediators.RespondConverter;
import synapse.converter.bir.mediators.SequenceMediatorConverter;
import synapse.converter.bir.mediators.UnsupportedConverter;
import synapse.model.Synapse.Kind;
import synapse.model.Synapse.SynapseNode;

import java.util.List;
import java.util.Map;

/**
 * The dispatch that drives the {@link BIRConverter} implementations for the mediators that appear
 * inside a resource or sequence body. Each converter (see {@code synapse.converter.bir.mediators})
 * appends to the enclosing {@link ScopeContext}; {@link #convertMediators} walks a mediator list and
 * routes each element to its converter, stopping at the first respond.
 */
public final class MediatorConverters {

    private static final Map<Kind, BIRConverter<ScopeContext>> MEDIATOR_CONVERTERS = Map.of(
            Kind.PAYLOAD_FACTORY, new PayloadFactoryConverter(),
            Kind.PROPERTY, new PropertyConverter(),
            Kind.SEQUENCE_MEDIATOR, new SequenceMediatorConverter(),
            Kind.RESPOND, new RespondConverter());

    private MediatorConverters() {
    }

    /**
     * Converts a list of Synapse mediators into Ballerina, accumulating the result
     * in {@code context}.
     * A {@code <respond>} mediator (or a call to a sequence that responds, directly or down a call chain)
     * is terminal, so conversion stops at the first one. Every generated function and resource returns
     * {@code error?}, and a respond sends its response through {@code ctx.caller} via the generated
     * {@code respond} utility rather than by returning an {@code http:Response}.
     */
    static void convertMediators(List<SynapseNode> mediators, ScopeContext context) {
        for (SynapseNode mediator : mediators) {
            BIRConverter<ScopeContext> converter = MEDIATOR_CONVERTERS.getOrDefault(mediator.kind(),
                    new UnsupportedConverter());
            converter.convert(mediator, context);
            if (context.isResponded()) {
                break;
            }
        }
    }
}
