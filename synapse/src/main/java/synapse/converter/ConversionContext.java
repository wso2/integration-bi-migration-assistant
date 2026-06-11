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
 * Holds the state accumulated while converting Synapse records to Ballerina,
 * such as the generated services.
 */
public class ConversionContext {

    private final List<Service> services = new ArrayList<>();
    private Payload payload;
    private boolean respondInitialized;

    public boolean isRespondInitialized() {
        return respondInitialized;
    }

    public void setRespondInitialized(boolean respondInitialized) {
        this.respondInitialized = respondInitialized;
    }

    public void addService(Service service) {
        services.add(service);
    }

    public List<Service> services() {
        return services;
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
