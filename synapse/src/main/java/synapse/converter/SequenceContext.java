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
package synapse.converter;

/**
 * Scope context for a top-level Synapse {@code <sequence>} body, converted into a Ballerina function.
 * Unlike a resource body, no HTTP {@code response} object is in scope, so {@link #isWithinResource()}
 * remains {@code false}.
 */
public final class SequenceContext extends ScopeContext {

    public SequenceContext(ConversionContext shared) {
        super(shared);
    }
}
