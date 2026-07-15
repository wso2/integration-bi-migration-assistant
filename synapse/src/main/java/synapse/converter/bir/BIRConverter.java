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

import synapse.converter.ConversionContext;
import synapse.converter.ScopeContext;
import synapse.model.Synapse.SynapseNode;

/**
 * Converts a single {@link SynapseNode} into its Ballerina BIR representation,
 * accumulating the result
 * in a context.
 *
 * <p>
 * There are two families, distinguished by the context they operate on:
 * <ul>
 * <li><b>Root converters</b> ({@code BIRConverter<ConversionContext>}) handle
 * artifact-level elements
 * ({@code <api>}, {@code <sequence>}). They read the shared
 * {@link ConversionContext}, create a
 * {@link ScopeContext} for the body they generate, and write the result (a
 * service or function)
 * back to the shared context. See {@link APIConverter} and
 * {@link SequenceConverter}.</li>
 * <li><b>Mediator converters</b> ({@code BIRConverter<ScopeContext>}) handle
 * the mediators inside a
 * resource or sequence body, appending to the enclosing
 * {@link ScopeContext}. See {@link MediatorConverters}.</li>
 * </ul>
 *
 * @param <C> the context this converter operates on
 */
public interface BIRConverter<C> {

    void convert(SynapseNode node, C context);
}
