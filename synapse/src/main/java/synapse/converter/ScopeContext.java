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

import common.BallerinaModel.Import;
import common.BallerinaModel.Statement;

import java.util.ArrayList;
import java.util.List;

/**
 * State local to a single Synapse scope being converted (a resource body within
 * an {@code <api>}, or
 * a {@code <sequence>} body). Each scope gets its own instance, holding the
 * statements emitted for
 * that scope, while sharing the project-wide {@link ConversionContext}
 * referenced by {@link #shared()}.
 *
 * @see ResourceContext
 * @see SequenceContext
 */
public abstract class ScopeContext {

    private final ConversionContext shared;
    private final List<Statement> statements = new ArrayList<>();
    private final List<Import> importStatements = new ArrayList<>();
    private boolean contextParam;
    private boolean contextInitialized;
    private boolean responded;

    protected ScopeContext(ConversionContext shared) {
        assert shared != null : "shared ConversionContext must not be null";
        this.shared = shared;
    }

    public ConversionContext shared() {
        return shared;
    }

    /**
     * Records that this scope responds — from a {@code <respond>} mediator or a call to a responding
     * sequence — a terminal event that stops mediator conversion, and ensures a {@code ctx} (which
     * carries the {@code http:Caller}) is in scope. Every generated function and resource returns
     * {@code error?}, so the respond is {@code check}ed regardless.
     */
    public void markResponded() {
        setResponded(true);
        ensureContextAvailable();
    }

    /**
     * Declares the {@code Context ctx} local at the top of a resource body, seeded with the
     * {@code http:Caller caller} parameter every generated resource takes. Called once at the start of
     * each resource so {@code ctx} is unconditionally in scope; sequences receive {@code ctx} as a
     * parameter instead (see {@link #ensureContextAvailable()}) and never call this.
     */
    public void initContext() {
        statements.add(new Statement.BallerinaStatement("Context ctx = {variables: {}, caller: caller};"));
        setContextInitialized(true);
    }

    /**
     * Ensures a {@code Context ctx} is in scope for a sequence body: the generated function takes a
     * {@code Context ctx} parameter it mutates in place. Resources declare {@code ctx} upfront via
     * {@link #initContext()}, so this is a no-op for them.
     */
    public void ensureContextAvailable() {
        if (contextAvailable()) {
            return;
        }
        setContextParam(true);
    }

    public List<Statement> statements() {
        return statements;
    }

    public List<Import> importStatements() {
        return importStatements;
    }

    /**
     * Whether a {@code ctx} holding the default-scope (Synapse) properties is in scope — either a
     * {@code Context ctx} parameter (a sequence function that turned out to touch default properties,
     * directly or down a call chain, see {@link #hasContextParam()}) or a {@code Context ctx} local
     * declared in this scope. Default properties live for the duration of a request, so within a
     * resource {@code ctx} is a local declared at the top, whereas a sequence receives it as a
     * parameter it mutates in place.
     */
    public boolean contextAvailable() {
        return contextParam || contextInitialized;
    }

    /**
     * Whether the sequence function generated for this scope takes a {@code Context ctx} parameter,
     * which it mutates in place. Set while converting the body — when a {@code <property>} in the
     * default scope, or a call to a sequence that sets one, is reached in a sequence scope — and read
     * back afterwards to shape the function's signature. A resource scope declares a {@code ctx} local
     * instead, so this stays {@code false} there.
     */
    public boolean hasContextParam() {
        return contextParam;
    }

    public void setContextParam(boolean contextParam) {
        this.contextParam = contextParam;
    }

    public void setContextInitialized(boolean contextInitialized) {
        this.contextInitialized = contextInitialized;
    }

    /**
     * Whether a respond has occurred in this scope — a {@code <respond>} mediator
     * or a call to a
     * responding sequence. A respond is terminal, so mediator conversion stops once
     * this is set.
     */
    public boolean isResponded() {
        return responded;
    }

    public void setResponded(boolean responded) {
        this.responded = responded;
    }
}
