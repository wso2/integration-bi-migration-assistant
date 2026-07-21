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
import common.BallerinaModel.Statement;
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
import java.util.Set;

/**
 * Converts a Synapse {@code <api>} element into a Ballerina HTTP service.
 */
public class APIConverter implements BIRConverter<ConversionContext> {

    private static final String DEFAULT_LISTENER_REF = "httpListener";
    private static final String ROOT_RESOURCE_PATH = ".";
    private static final String CALLER_PARAM = "caller";
    private static final String REQUEST_PARAM = "request";

    // Accessors whose HTTP method may carry a request body, so the generated resource takes an
    // http:Request parameter. GET, HEAD and OPTIONS are excluded.
    private static final Set<String> REQUEST_BODY_METHODS = Set.of("post", "put", "patch", "delete", "default");

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
        parameters.add(new Parameter(CALLER_PARAM, new TypeDesc.BallerinaType("http:Caller")));

        ResourceContext resourceContext = new ResourceContext(context);
        resourceContext.initContext();
        if (REQUEST_BODY_METHODS.contains(method)) {
            parameters.add(new Parameter(REQUEST_PARAM, new TypeDesc.BallerinaType("http:Request")));
            resourceContext.statements().add(new Statement.BallerinaStatement("check emitPayload(ctx, request);"));
        }

        if (resource.inSequence() != null) {
            MediatorConverters.convertMediators(resource.inSequence().mediators(), resourceContext);
        }
        context.addImports(ConversionContext.MAIN_BAL_FILE, resourceContext.importStatements());
        return new Resource(method, path, parameters,
                Optional.of(new TypeDesc.BallerinaType("error?")), resourceContext.statements());
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
