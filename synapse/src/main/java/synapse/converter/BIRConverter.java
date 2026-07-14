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
import common.BallerinaModel.Import;
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
 * Converts a single {@link SynapseNode} into its Ballerina BIR representation,
 * accumulating the result
 * in a context.
 *
 * <p>
 * There are two families, distinguished by the context they operate on:
 * <ul>
 * <li><b>Root converters</b> ({@code BIRConverter<ConversionContext>}) handle
 * artifact-level elements
 * ({@code <api>}, {@code <sequence>}). They read the shared
 * {@link ConversionContext}, create a
 * {@link ScopeContext} for the body they generate, and write the result (a
 * service or function)
 * back to the shared context.</li>
 * <li><b>Mediator converters</b> ({@code BIRConverter<ScopeContext>}) handle
 * the mediators inside a
 * resource or sequence body, appending to the enclosing
 * {@link ScopeContext}.</li>
 * </ul>
 *
 * @param <C> the context this converter operates on
 */
public interface BIRConverter<C> {

    Map<Kind, BIRConverter<ScopeContext>> MEDIATOR_CONVERTERS = Map.of(
            Kind.PAYLOAD_FACTORY, new PayloadFactoryConverter(),
            Kind.PROPERTY, new PropertyConverter(),
            Kind.SEQUENCE_MEDIATOR, new SequenceMediatorConverter());

    Import HTTP_IMPORT = new Import("ballerina", "http");

    void convert(SynapseNode node, C context);

    /**
     * Converts a list of Synapse mediators into Ballerina, accumulating the result
     * in {@code context}.
     * The type the enclosing scope (resource or function) should return is recorded
     * on the context: it
     * becomes {@code http:Response} when the scope responds — a {@code <respond>}
     * mediator, or a call to
     * a sequence that responds (directly or down a call chain) — and stays
     * {@link BuiltinType#NIL}
     * otherwise. Callers read it back via {@link ScopeContext#returnType()}.
     *
     * <p>
     * Only a resource body actually emits the {@code return response;}; a
     * {@code <sequence>} body is
     * generated as a plain {@code nil}-returning function and its respond bubbles
     * up, via the sequence
     * metadata, to the resource call site that returns to the client.
     */
    static void convertMediators(List<SynapseNode> mediators, ScopeContext context) {
        for (SynapseNode mediator : mediators) {
            if (mediator.kind() == Kind.RESPOND) {
                emitRespond(context);
            } else {
                BIRConverter<ScopeContext> converter = MEDIATOR_CONVERTERS.getOrDefault(mediator.kind(),
                        new UnsupportedConverter());
                converter.convert(mediator, context);
            }
            if (context.isResponded()) {
                break;
            }
        }
    }

    private static void emitRespond(ScopeContext context) {
        context.setResponded(true);
        if (!context.isWithinResource()) {
            return;
        }
        ensureResponseAvailable(context);
        returnResponse(context);
    }

    private static void ensureResponseAvailable(ScopeContext context) {
        context.importStatements().add(HTTP_IMPORT);
        if (context.responseAvailable()) {
            return;
        }
        if (context.isWithinResource()) {
            context.statements().add(0, new Statement.BallerinaStatement("http:Response response = new;"));
            context.setRespondInitialized(true);
        } else {
            context.setResponseParam(true);
        }
    }

    private static void ensureContextAvailable(ScopeContext context) {
        if (context.contextAvailable()) {
            return;
        }
        if (context.isWithinResource()) {
            context.statements().add(0, new Statement.BallerinaStatement("Context ctx = {};"));
            context.setContextInitialized(true);
        } else {
            context.setContextParam(true);
        }
    }

    private static void returnResponse(ScopeContext context) {
        context.statements().add(new Statement.Return<>(Optional.of(new Expression.VariableReference("response"))));
        context.setReturnType(new TypeDesc.BallerinaType("http:Response"));
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
            if (resource.inSequence() != null) {
                convertMediators(resource.inSequence().mediators(), resourceContext);
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

    /**
     * Converts a top-level Synapse {@code <sequence>} into a Ballerina function
     * whose body is the
     * converted mediator flow. Whether the function takes an
     * {@code http:Response response} parameter and
     * whether it responds both fall out of converting the body rather than a
     * separate pre-scan: when
     * conversion reaches a {@code <payloadFactory>} (directly or down a call chain)
     * the sequence takes a
     * {@code response} parameter it mutates in place instead of returning, and an
     * unreached payloadFactory
     * (e.g. after a {@code <respond>}) leaves the function parameterless. The
     * function's return type is
     * likewise driven by the converted mediators: unless they yield a
     * non-{@code nil} {@code returnType},
     * the function is {@code nil}-returning.
     */
    class SequenceConverter implements BIRConverter<ConversionContext> {

        @Override
        public void convert(SynapseNode node, ConversionContext context) {
            Sequence sequence = (Sequence) node;
            SequenceContext sequenceContext = new SequenceContext(context);
            convertMediators(sequence.mediators(), sequenceContext);
            boolean containsPayloadFactory = sequenceContext.hasResponseParam();
            boolean usesContext = sequenceContext.hasContextParam();
            context.addSequenceMetadata(new ConversionContext.SequenceMetadata(
                    sequence.name(), sequenceContext.isResponded(), containsPayloadFactory, usesContext));
            context.addImports(ConversionContext.FUNCTIONS_BAL_FILE, sequenceContext.importStatements());
            List<Parameter> params = new ArrayList<>();
            if (usesContext) {
                params.add(new Parameter("ctx", new TypeDesc.BallerinaType("Context")));
            }
            if (containsPayloadFactory) {
                params.add(new Parameter("response", new TypeDesc.BallerinaType("http:Response")));
            }
            TypeDesc returnType = sequenceContext.returnType();
            List<Statement> body = sequenceContext.statements();
            Function function = returnType == BuiltinType.NIL
                    ? new Function(sequence.name(), params, body)
                    : new Function(sequence.name(), params, returnType, body);
            context.addFunction(function);
        }
    }

    /**
     * Converts a Synapse {@code <sequence key="name"/>} mediator into a call to the
     * Ballerina function
     * generated for the referenced sequence. When that function takes an
     * {@code http:Response response}
     * parameter, the call passes {@code response} (declaring it first if not
     * already in scope). If the
     * referenced sequence responds (directly or down a call chain), the call also
     * drives a respond in
     * the enclosing scope.
     */
    class SequenceMediatorConverter implements BIRConverter<ScopeContext> {

        @Override
        public void convert(SynapseNode node, ScopeContext context) {
            SequenceMediator sequenceMediator = (SequenceMediator) node;
            ConversionContext.SequenceMetadata metadata = context.shared().sequenceMetadata(sequenceMediator.key())
                    .orElse(null);
            List<Expression> args = new ArrayList<>();
            if (metadata != null && metadata.usesContext()) {
                ensureContextAvailable(context);
                args.add(new Expression.VariableReference("ctx"));
            }
            if (metadata != null && metadata.containsPayloadFactory()) {
                ensureResponseAvailable(context);
                args.add(new Expression.VariableReference("response"));
            }
            context.statements().add(new Statement.CallStatement(
                    new Expression.FunctionCall(sequenceMediator.key(), args)));
            if (metadata != null && metadata.containsRespond()) {
                emitRespond(context);
            }
        }
    }

    class PayloadFactoryConverter implements BIRConverter<ScopeContext> {

        @Override
        public void convert(SynapseNode node, ScopeContext context) {
            PayloadFactory payloadFactory = (PayloadFactory) node;
            Expression value = extractValue(payloadFactory.mediaType(), payloadFactory.format());
            ensureResponseAvailable(context);
            context.statements().add(new Statement.CallStatement(new Expression.MethodCall(
                    new Expression.VariableReference("response"), "setPayload", List.of(value))));
        }

        private static Expression extractValue(String mediaType, String format) {
            return switch (mediaType) {
                case "text" -> new StringConstant(format);
                case "xml" -> new XMLTemplate(format);
                // json (and others): the <format> is already a valid Ballerina literal
                // expression.
                default -> new BallerinaExpression(format);
            };
        }
    }

    /**
     * Converts a Synapse {@code <property>} mediator. How a property is converted
     * depends on where it
     * lives: a property within a resource contributes to that resource's body,
     * whereas a property
     * outside a resource (e.g. an api-level property) is handled differently. This
     * converter therefore
     * first identifies its scope.
     */
    class PropertyConverter implements BIRConverter<ScopeContext> {

        private static final String TRANSPORT_SCOPE = "transport";
        private static final String AXIS2_SCOPE = "axis2";
        private static final String DEFAULT_SCOPE = "default";
        private static final String SYNAPSE_SCOPE = "synapse";
        private static final String REMOVE_ACTION = "remove";

        @Override
        public void convert(SynapseNode node, ScopeContext context) {
            convertProperty((Property) node, context);
        }

        private static void convertProperty(Property property, ScopeContext context) {
            switch (property.scope()) {
                case TRANSPORT_SCOPE -> {
                    rejectRemoveAction(property);
                    context.statements().add(new Statement.BallerinaStatement(
                            "response.setHeader(\"" + property.name() + "\", \"" + property.value() + "\");"));
                }
                case AXIS2_SCOPE -> {
                    rejectRemoveAction(property);
                    context.statements().add(new Statement.BallerinaStatement(
                            "response.statusCode = " + property.value() + ";"));
                }
                case DEFAULT_SCOPE, SYNAPSE_SCOPE -> convertDefaultProperty(property, context);
                default -> throw new UnsupportedOperationException("The '" + property.scope()
                        + "' scope is not supported for property '" + property.name() + "'.");
            }
        }

        /**
         * The {@code remove} action is only supported in the default scope, where it clears a
         * {@code Context} field. Removing a transport header or an axis2 property has no equivalent in
         * the generated code yet, so reject it as unsupported rather than emit a misleading assignment.
         */
        private static void rejectRemoveAction(Property property) {
            if (REMOVE_ACTION.equals(property.action())) {
                throw new UnsupportedOperationException("The 'remove' action is not supported for property '"
                        + property.name() + "' in the '" + property.scope() + "' scope.");
            }
        }

        private static void convertDefaultProperty(Property property, ScopeContext context) {
            ensureContextAvailable(context);
            if (REMOVE_ACTION.equals(property.action())) {
                context.statements().add(new Statement.BallerinaStatement(
                        "ctx." + property.name() + " = " + BuiltinType.NIL + ";"));
                return;
            }
            context.shared().addProperty(property.name(), toBallerinaType(property.type()), property.scope());
            context.statements().add(new Statement.BallerinaStatement(
                    "ctx." + property.name() + " = " + property.value() + ";"));
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
