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
import common.BallerinaModel.Expression.BallerinaExpression;
import common.BallerinaModel.Expression.StringConstant;
import common.BallerinaModel.Expression.XMLTemplate;
import common.BallerinaModel.Parameter;
import common.BallerinaModel.Resource;
import common.BallerinaModel.Service;
import common.BallerinaModel.Statement;
import common.BallerinaModel.TypeDesc;
import common.BallerinaModel.TypeDesc.BuiltinType;
import synapse.model.Synapse.Api;
import synapse.model.Synapse.Kind;
import synapse.model.Synapse.PayloadFactory;
import synapse.model.Synapse.SynapseNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Converts a single {@link SynapseNode} into its Ballerina BIR representation,
 * accumulating the result in the supplied {@link ConversionContext}.
 */
public interface BIRConverter {

    void convert(SynapseNode node, ConversionContext context);

    /**
     * Converts a Synapse {@code <api>} element into a Ballerina HTTP service.
     */
    class APIConverter implements BIRConverter {

        private static final String DEFAULT_LISTENER_REF = "httpListener";
        private static final String ROOT_RESOURCE_PATH = ".";

        private static final Map<Kind, BIRConverter> CONVERTERS = Map.of(
                Kind.PAYLOAD_FACTORY, new PayloadFactoryConverter(),
                Kind.PROPERTY, new SynapseConverter.PropertyConverter());

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
            String method = resource.methods().toLowerCase();
            String path = buildResourcePath(resource.path());

            List<Parameter> parameters = new ArrayList<>();
            for (String queryParam : resource.queryParams()) {
                parameters.add(new Parameter(queryParam, BuiltinType.STRING));
            }

            ConversionContext resourceContext = new ConversionContext(context);
            TypeDesc returnType = genResourceBody(resource.inSequence(), resourceContext);
            Optional<TypeDesc> returnTypeDesc = returnType == BuiltinType.NIL
                    ? Optional.empty() : Optional.of(returnType);
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

        private static TypeDesc genResourceBody(synapse.model.Synapse.InSequence inSequence, 
                                                ConversionContext context) {
            if (inSequence == null) {
                return BuiltinType.NIL;
            }

            TypeDesc returnType = BuiltinType.NIL;
            for (SynapseNode node : inSequence.mediators()) {
                if (node.kind() == Kind.RESPOND) {
                    returnType = respond(context);
                } else {
                    BIRConverter converter = CONVERTERS.getOrDefault(node.kind(), new UnsupportedConverter());
                    converter.convert(node, context);
                }
            }
            return returnType;
        }

        private static TypeDesc respond(ConversionContext context) {
            Optional<ConversionContext.Payload> payload = context.payload();
            if (payload.isEmpty()) {
                return BuiltinType.NIL;
            }
            List<Statement> statements = context.statements();
            if (!context.isRespondInitialized()) {
                statements.add(0, new Statement.BallerinaStatement("http:Response response = new;"));
                context.setRespondInitialized(true);
            }
            ConversionContext.Payload p = payload.get();
            statements.add(new Statement.CallStatement(new Expression.MethodCall(
                    new Expression.VariableReference("response"), "setPayload", List.of(p.value()))));
            statements.add(new Statement.Return<>(Optional.of(new Expression.VariableReference("response"))));
            context.setPayload(null);
            return new TypeDesc.BallerinaType("http:Response");
        }
    }

    class PayloadFactoryConverter implements BIRConverter {

        @Override
        public void convert(SynapseNode node, ConversionContext context) {
            PayloadFactory payloadFactory = (PayloadFactory) node;
            TypeDesc type = mediaTypeToType(payloadFactory.mediaType());
            Expression value = extractValue(payloadFactory.mediaType(), payloadFactory.format());
            context.setPayload(new ConversionContext.Payload(type, value));
        }

        private static TypeDesc mediaTypeToType(String mediaType) {
            return switch (mediaType) {
                case "json" -> BuiltinType.JSON;
                case "xml" -> BuiltinType.XML;
                case "text" -> BuiltinType.STRING;
                default -> BuiltinType.ANYDATA;
            };
        }

        private static Expression extractValue(String mediaType, String format) {
            return switch (mediaType) {
                case "text" -> new StringConstant(format);
                case "xml" -> new XMLTemplate(format);
                // json (and others): the <format> is already a valid Ballerina literal expression.
                default -> new BallerinaExpression(format);
            };
        }
    }

    class UnsupportedConverter implements BIRConverter {

        @Override
        public void convert(SynapseNode node, ConversionContext context) {
            throw new UnsupportedOperationException("No converter implemented for Synapse node kind: " + node.kind());
        }
    }
}
