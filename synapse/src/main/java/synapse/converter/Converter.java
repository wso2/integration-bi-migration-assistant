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
import synapse.model.Synapse.Api;
import synapse.model.Synapse.SynapseNode;

import java.util.ArrayList;

/**
 * Converts a single {@link SynapseNode} into its Ballerina representation,
 * accumulating the result in the supplied {@link ConversionContext}.
 */
public interface Converter {

    void convert(SynapseNode node, ConversionContext context);

    /**
     * Converts a Synapse {@code <api>} element into a Ballerina HTTP service.
     */
    class APIConverter implements Converter {

        private static final String DEFAULT_LISTENER_REF = "httpListener";

        @Override
        public void convert(SynapseNode node, ConversionContext context) {
            Api api = (Api) node;
            // <api context="/api"> -> Ballerina HTTP service with basePath = context.
            // Resources are empty for now since the <api> body is not modeled yet.
            Service service = new Service(api.context(), DEFAULT_LISTENER_REF, new ArrayList<>());
            context.addService(service);
        }
    }
}
