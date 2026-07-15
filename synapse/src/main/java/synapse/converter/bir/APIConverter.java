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

import common.BallerinaModel.Parameter;
import common.BallerinaModel.Resource;
import common.BallerinaModel.Service;
import common.BallerinaModel.TypeDesc;
import common.BallerinaModel.TypeDesc.BuiltinType;
import synapse.converter.ConversionContext;
import synapse.converter.ResourceContext;
import synapse.model.Synapse.Api;
import synapse.model.Synapse.SynapseNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Converts a Synapse {@code <api>} element into a Ballerina HTTP service.
 */
public class APIConverter implements BIRConverter<ConversionContext> {

    private static final String DEFAULT_LISTENER_REF = "httpListener";
    private static final String ROOT_RESOURCE_PATH = ".";

    @Override
    public void convert(SynapseNode node, ConversionContext context) {
        Api api = (Api) node;
        List<Resource> resources = new ArrayList<>();
        for (SynapseNode child : api.resources()) {
            if (child instanceof synapse.model.Synapse.Resource resource) {
                resources.add(convertResource(resource, context));
            }
        }
        Service service = new Service(api.context(), DEFAULT_LISTENER_REF, resources);
        context.addService(service);
    }

    private static Resource convertResource(synapse.model.Synapse.Resource resource, ConversionContext context) {
        String method = resource.methods().toLowerCase(Locale.ROOT);
        String path = buildResourcePath(resource.path());

        List<Parameter> parameters = new ArrayList<>();
        for (String queryParam : resource.queryParams()) {
            parameters.add(new Parameter(queryParam, BuiltinType.STRING));
        }

        ResourceContext resourceContext = new ResourceContext(context);
        if (resource.inSequence() != null) {
            MediatorConverters.convertMediators(resource.inSequence().mediators(), resourceContext);
        }
        context.addImports(ConversionContext.MAIN_BAL_FILE, resourceContext.importStatements());
        TypeDesc returnType = resourceContext.returnType();
        Optional<TypeDesc> returnTypeDesc = returnType == BuiltinType.NIL
                ? Optional.empty()
                : Optional.of(returnType);
        return new Resource(method, path, parameters, returnTypeDesc, resourceContext.statements());
    }

    private static String buildResourcePath(String synapsePath) {
        List<String> segments = new ArrayList<>();
        for (String segment : synapsePath.split("/")) {
            if (segment.isEmpty()) {
                continue;
            }
            if (segment.startsWith("{") && segment.endsWith("}")) {
                String paramName = segment.substring(1, segment.length() - 1);
                segments.add("[string " + paramName + "]");
            } else {
                segments.add(segment);
            }
        }
        return segments.isEmpty() ? ROOT_RESOURCE_PATH : String.join("/", segments);
    }
}
