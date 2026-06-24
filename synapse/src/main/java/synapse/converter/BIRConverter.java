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
import common.BallerinaModel.Function;
import common.BallerinaModel.Parameter;
import common.BallerinaModel.Resource;
import common.BallerinaModel.Service;
import common.BallerinaModel.Statement;
import common.BallerinaModel.TypeDesc;
import common.BallerinaModel.TypeDesc.BuiltinType;
import synapse.model.Synapse.Api;
import synapse.model.Synapse.Kind;
import synapse.model.Synapse.PayloadFactory;
import synapse.model.Synapse.Property;
import synapse.model.Synapse.Sequence;
import synapse.model.Synapse.SequenceMediator;
import synapse.model.Synapse.SynapseNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Converts a single {@link SynapseNode} into its Ballerina BIR representation, accumulating the result
 * in a context.
 *
 * <p>There are two families, distinguished by the context they operate on:
 * <ul>
 *   <li><b>Root converters</b> ({@code BIRConverter<ConversionContext>}) handle artifact-level elements
 *       ({@code <api>}, {@code <sequence>}). They read the shared {@link ConversionContext}, create a
 *       {@link ScopeContext} for the body they generate, and write the result (a service or function)
 *       back to the shared context.</li>
 *   <li><b>Mediator converters</b> ({@code BIRConverter<ScopeContext>}) handle the mediators inside a
 *       resource or sequence body, appending to the enclosing {@link ScopeContext}.</li>
 * </ul>
 *
 * @param <C> the context this converter operates on
 */
public interface BIRConverter<C> {

    Map<Kind, BIRConverter<ScopeContext>> MEDIATOR_CONVERTERS = Map.of(
            Kind.PAYLOAD_FACTORY, new PayloadFactoryConverter(),
            Kind.PROPERTY, new PropertyConverter(),
            Kind.SEQUENCE_MEDIATOR, new SequenceMediatorConverter());

    void convert(SynapseNode node, C context);

    /**
     * Converts a list of Synapse mediators into Ballerina, accumulating the result in {@code context}.
     * Returns the type the enclosing scope (resource or function) should return: a {@code respond}
     * mediator yields {@code http:Response}, otherwise {@link BuiltinType#NIL}.
     */
    static TypeDesc convertMediators(List<SynapseNode> mediators, ScopeContext context) {
        TypeDesc returnType = BuiltinType.NIL;
        for (SynapseNode node : mediators) {
            if (node.kind() == Kind.RESPOND) {
                returnType = respond(context);
            } else {
                BIRConverter<ScopeContext> converter =
                        MEDIATOR_CONVERTERS.getOrDefault(node.kind(), new UnsupportedConverter());
                converter.convert(node, context);
            }
        }
        return returnType;
    }

    private static TypeDesc respond(ScopeContext context) {
        Optional<ScopeContext.Payload> payload = context.payload();
        if (payload.isEmpty()) {
            return BuiltinType.NIL;
        }
        List<Statement> statements = context.statements();
        if (!context.isRespondInitialized()) {
            statements.add(0, new Statement.BallerinaStatement("http:Response response = new;"));
            context.setRespondInitialized(true);
        }
        ScopeContext.Payload p = payload.get();
        statements.add(new Statement.CallStatement(new Expression.MethodCall(
                new Expression.VariableReference("response"), "setPayload", List.of(p.value()))));
        statements.add(new Statement.Return<>(Optional.of(new Expression.VariableReference("response"))));
        context.setPayload(null);
        return new TypeDesc.BallerinaType("http:Response");
    }

    /**
     * Converts a Synapse {@code <api>} element into a Ballerina HTTP service.
     */
    class APIConverter implements BIRConverter<ConversionContext> {

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
            String method = resource.methods().toLowerCase();
            String path = buildResourcePath(resource.path());

            List<Parameter> parameters = new ArrayList<>();
            for (String queryParam : resource.queryParams()) {
                parameters.add(new Parameter(queryParam, BuiltinType.STRING));
            }

            ResourceContext resourceContext = new ResourceContext(context);
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
                                                ScopeContext context) {
            if (inSequence == null) {
                return BuiltinType.NIL;
            }
            return convertMediators(inSequence.mediators(), context);
        }
    }

    /**
     * Converts a top-level Synapse {@code <sequence>} into a Ballerina function whose body is the
     * converted mediator flow. The function returns {@code http:Response} when the sequence ends in a
     * {@code <respond>} with a payload, and is otherwise {@code nil}-returning.
     */
    class SequenceConverter implements BIRConverter<ConversionContext> {

        @Override
        public void convert(SynapseNode node, ConversionContext context) {
            Sequence sequence = (Sequence) node;
            SequenceContext sequenceContext = new SequenceContext(context);
            TypeDesc returnType = convertMediators(sequence.mediators(), sequenceContext);
            List<Statement> body = sequenceContext.statements();
            Function function = returnType == BuiltinType.NIL
                    ? new Function(sequence.name(), List.of(), body)
                    : new Function(sequence.name(), List.of(), returnType, body);
            context.addFunction(function);
        }
    }

    /**
     * Converts a Synapse {@code <sequence key="name"/>} mediator into a call to the Ballerina function
     * generated for the referenced sequence. The call passes no arguments and discards any return value.
     */
    class SequenceMediatorConverter implements BIRConverter<ScopeContext> {

        @Override
        public void convert(SynapseNode node, ScopeContext context) {
            SequenceMediator sequenceMediator = (SequenceMediator) node;
            context.statements().add(new Statement.CallStatement(
                    new Expression.FunctionCall(sequenceMediator.key(), List.<Expression>of())));
        }
    }

    class PayloadFactoryConverter implements BIRConverter<ScopeContext> {

        @Override
        public void convert(SynapseNode node, ScopeContext context) {
            PayloadFactory payloadFactory = (PayloadFactory) node;
            TypeDesc type = mediaTypeToType(payloadFactory.mediaType());
            Expression value = extractValue(payloadFactory.mediaType(), payloadFactory.format());
            context.setPayload(new ScopeContext.Payload(type, value));
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

    /**
     * Converts a Synapse {@code <property>} mediator. How a property is converted depends on where it
     * lives: a property within a resource contributes to that resource's body, whereas a property
     * outside a resource (e.g. an api-level property) is handled differently. This converter therefore
     * first identifies its scope.
     */
    class PropertyConverter implements BIRConverter<ScopeContext> {

        private static final String TRANSPORT_SCOPE = "transport";
        private static final String AXIS2_SCOPE = "axis2";

        @Override
        public void convert(SynapseNode node, ScopeContext context) {
            convertProperty((Property) node, context);
        }

        private static void convertProperty(Property property, ScopeContext context) {
            String statement = switch (property.scope()) {
                case TRANSPORT_SCOPE -> "response.setHeader(\"" + property.name() + "\", \""
                        + property.value() + "\");";
                case AXIS2_SCOPE -> "response.statusCode = " + property.value() + ";";
                default -> toBallerinaType(property.type()) + " " + property.name() + " = "
                        + property.value() + ";";
            };
            context.statements().add(new Statement.BallerinaStatement(statement));
        }

        private static String toBallerinaType(String synapseType) {
            return switch (synapseType.toUpperCase()) {
                case "INTEGER", "INT", "LONG", "SHORT" -> "int";
                case "BOOLEAN" -> "boolean";
                case "DOUBLE", "FLOAT" -> "float";
                default -> "string";
            };
        }
    }

    class UnsupportedConverter implements BIRConverter<ScopeContext> {

        @Override
        public void convert(SynapseNode node, ScopeContext context) {
            throw new UnsupportedOperationException("No converter implemented for Synapse node kind: " + node.kind());
        }
    }
}
