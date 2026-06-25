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

import common.BallerinaModel.Expression;
import common.BallerinaModel.Statement;
import common.BallerinaModel.TypeDesc;
import common.BallerinaModel.TypeDesc.BuiltinType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * State local to a single Synapse scope being converted (a resource body within an {@code <api>}, or
 * a {@code <sequence>} body). Each scope gets its own instance, holding the statements emitted for
 * that scope and the in-flight {@code payload}, while sharing the project-wide {@link ConversionContext}
 * referenced by {@link #shared()}.
 *
 * @see ResourceContext
 * @see SequenceContext
 */
public abstract class ScopeContext {

    private final ConversionContext shared;
    private final List<Statement> statements = new ArrayList<>();
    private Payload payload;
    private boolean respondInitialized;
    private boolean responded;
    private TypeDesc returnType = BuiltinType.NIL;

    protected ScopeContext(ConversionContext shared) {
        assert shared != null : "shared ConversionContext must not be null";
        this.shared = shared;
    }

    public ConversionContext shared() {
        return shared;
    }

    /**
     * Whether this scope is a resource body, where an HTTP {@code response} object is in scope (as
     * opposed to a plain sequence function). Mediators whose Ballerina shape depends on having a
     * response — e.g. a {@code transport}-scope property setting a header — branch on this.
     */
    public boolean isWithinResource() {
        return false;
    }

    public List<Statement> statements() {
        return statements;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    public Optional<Payload> payload() {
        return Optional.ofNullable(payload);
    }

    public boolean isRespondInitialized() {
        return respondInitialized;
    }

    public void setRespondInitialized(boolean respondInitialized) {
        this.respondInitialized = respondInitialized;
    }

    /**
     * Whether a respond has occurred in this scope — a {@code <respond>} mediator or a call to a
     * responding sequence. A respond is terminal, so mediator conversion stops once this is set.
     */
    public boolean isResponded() {
        return responded;
    }

    public void setResponded(boolean responded) {
        this.responded = responded;
    }

    /**
     * The type this scope's enclosing resource or function should return: {@code http:Response} once a
     * respond has been emitted into this (resource) scope, and {@link BuiltinType#NIL} otherwise.
     */
    public TypeDesc returnType() {
        return returnType;
    }

    public void setReturnType(TypeDesc returnType) {
        this.returnType = returnType;
    }

    public record Payload(TypeDesc type, Expression value) {
    }
}
