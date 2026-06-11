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
import common.BallerinaModel.Service;
import common.BallerinaModel.TypeDesc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Holds the state accumulated while converting Synapse records to Ballerina.
 *
 * <p>Contexts form a tree that mirrors the Synapse scope nesting (api -> resource -> ...). A child
 * context is created for each nested scope via {@link #ConversionContext(ConversionContext)}. State
 * falls into two categories:
 * <ul>
 *   <li><b>Shared / root</b> (e.g. the generated services) is held only at the root and accessed
 *       through the parent chain, so it survives once a child scope ends.</li>
 *   <li><b>Scope-local</b> (e.g. the current {@code payload} and the {@code respondInitialized}
 *       flag) lives only on the context it is set on and dies with that scope.</li>
 * </ul>
 * When adding a new field, decide which category it belongs to.
 */
public class ConversionContext {

    private final ConversionContext parent;

    private final List<Service> services;

    private Payload payload;
    private boolean respondInitialized;

    public ConversionContext() {
        this(null);
    }

    public ConversionContext(ConversionContext parent) {
        this.parent = parent;
        this.services = (parent == null) ? new ArrayList<>() : null;
    }

    private ConversionContext root() {
        return (parent == null) ? this : parent.root();
    }

    public boolean isRespondInitialized() {
        return respondInitialized;
    }

    public void setRespondInitialized(boolean respondInitialized) {
        this.respondInitialized = respondInitialized;
    }

    public void addService(Service service) {
        root().services.add(service);
    }

    public List<Service> services() {
        return root().services;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    public Optional<Payload> payload() {
        return Optional.ofNullable(payload);
    }

    /**
     * A payload extracted from a Synapse mediator (e.g. {@code <payloadFactory>}): the Ballerina
     * type of the payload and the expression producing its value. How it is finally emitted (as a
     * return value, a variable, etc.) depends on the surrounding context (e.g. whether a
     * {@code <respond>} follows), which is resolved later.
     */
    public record Payload(TypeDesc type, Expression value) {
    }
}
