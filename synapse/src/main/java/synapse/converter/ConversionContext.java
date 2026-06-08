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

import common.BallerinaModel.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds the state accumulated while converting Synapse records to Ballerina,
 * such as the generated services.
 */
public class ConversionContext {

    private final List<Service> services = new ArrayList<>();

    public void addService(Service service) {
        services.add(service);
    }

    public List<Service> services() {
        return services;
    }
}
