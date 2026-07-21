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

import common.BallerinaModel;
import common.BallerinaModel.Expression;
import common.BallerinaModel.Expression.BallerinaExpression;
import common.BallerinaModel.Expression.StringConstant;
import common.BallerinaModel.Expression.XMLTemplate;
import common.BallerinaModel.Function;
import common.BallerinaModel.Import;
import common.BallerinaModel.Parameter;
import common.BallerinaModel.Statement;
import common.BallerinaModel.TypeDesc;
import common.BallerinaModel.TypeDesc.BuiltinType;
import synapse.converter.ConversionContext;
import synapse.converter.ScopeContext;
import synapse.model.Synapse.ClassMediator;
import synapse.model.Synapse.Kind;
import synapse.model.Synapse.PayloadFactory;
import synapse.model.Synapse.Property;
import synapse.model.Synapse.SequenceMediator;
import synapse.model.Synapse.SynapseNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The {@link BIRConverter} implementations for the mediators that appear inside a resource or sequence
 * body, together with the dispatch that drives them. Each converter appends to the enclosing
 * {@link ScopeContext}; {@link #convertMediators} walks a mediator list and routes each element to its
 * converter, stopping at the first respond.
 */
public final class MediatorConverters {

    private static final Map<Kind, BIRConverter<ScopeContext>> MEDIATOR_CONVERTERS = Map.of(
            Kind.PAYLOAD_FACTORY, new PayloadFactoryConverter(),
            Kind.PROPERTY, new PropertyConverter(),
            Kind.SEQUENCE_MEDIATOR, new SequenceMediatorConverter(),
            Kind.CLASS_MEDIATOR, new ClassMediatorConverter());

    private MediatorConverters() {
    }

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
                context.emitRespond();
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
    static class SequenceMediatorConverter implements BIRConverter<ScopeContext> {

        @Override
        public void convert(SynapseNode node, ScopeContext context) {
            SequenceMediator sequenceMediator = (SequenceMediator) node;
            ConversionContext.SequenceMetadata metadata = context.shared().sequenceMetadata(sequenceMediator.key())
                    .orElse(null);
            List<Expression> args = new ArrayList<>();
            if (metadata != null && metadata.usesContext()) {
                context.ensureContextAvailable();
                args.add(new Expression.VariableReference("ctx"));
            }
            if (metadata != null && metadata.containsPayloadFactory()) {
                context.ensureResponseAvailable();
                args.add(new Expression.VariableReference("response"));
            }
            context.statements().add(new Statement.CallStatement(
                    new Expression.FunctionCall(sequenceMediator.key(), args)));
            if (metadata != null && metadata.containsRespond()) {
                context.emitRespond();
            }
        }
    }

    static class PayloadFactoryConverter implements BIRConverter<ScopeContext> {

        @Override
        public void convert(SynapseNode node, ScopeContext context) {
            PayloadFactory payloadFactory = (PayloadFactory) node;
            Expression value = extractValue(payloadFactory.mediaType(), payloadFactory.format());
            context.ensureResponseAvailable();
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
    static class PropertyConverter implements BIRConverter<ScopeContext> {

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
            context.ensureContextAvailable();
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
            return switch (synapseType.toUpperCase(Locale.ROOT)) {
                case "INTEGER", "INT", "LONG", "SHORT" -> "int";
                case "BOOLEAN" -> "boolean";
                case "DOUBLE", "FLOAT" -> "float";
                default -> "string";
            };
        }
    }

    /**
     * Converts a Synapse {@code <class>} mediator into a call to a generated Ballerina stub function.
     *
     * <p>Because the mediator's Java logic cannot be automatically translated, a stub function is
     * emitted into {@code functions.bal}. The call site passes any {@code <property>} values as
     * {@code string} arguments, and {@code http:Response response} when one is in scope.
     * The developer is expected to replace the stub body with equivalent Ballerina.
     */
    static class ClassMediatorConverter implements BIRConverter<ScopeContext> {

        private static final Import HTTP_IMPORT = new Import("ballerina", "http");

        @Override
        public void convert(SynapseNode node, ScopeContext context) {
            ClassMediator classMediator = (ClassMediator) node;
            String functionName = stubFunctionName(classMediator.className());

            // Build the stub function once and register it for functions.bal.
            // The stub takes http:Response first (when a response is in scope), then
            // each <property> value as a string parameter.
            List<Parameter> params = buildStubParams(classMediator, context);
            List<Statement> stubBody = buildStubBody(classMediator);
            Function stub = new Function(functionName, params, stubBody);
            context.shared().addFunction(stub);
            context.shared().addImports(ConversionContext.FUNCTIONS_BAL_FILE,
                    params.stream().anyMatch(p -> p.name().equals("response"))
                            ? List.of(HTTP_IMPORT)
                            : List.of());

            // Emit the call at the mediator site.
            List<Expression> callArgs = buildCallArgs(classMediator, context, functionName);
            context.statements().add(new Statement.CallStatement(
                    new Expression.FunctionCall(functionName, callArgs)));
        }

        /** Converts {@code org.example.MyMediator} → {@code myMediator}. */
        private static String stubFunctionName(String className) {
            String simpleName = className.contains(".")
                    ? className.substring(className.lastIndexOf('.') + 1)
                    : className;
            return Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
        }

        private static List<Parameter> buildStubParams(ClassMediator mediator, ScopeContext context) {
            List<Parameter> params = new ArrayList<>();
            if (context.responseAvailable() || context.isWithinResource()) {
                params.add(new Parameter("response", new TypeDesc.BallerinaType("http:Response")));
            }
            for (Property property : mediator.properties()) {
                if (property.expression().isPresent()) {
                    // Expressions are dynamic (XPath/JSONPath evaluated at runtime) — skip as parameter.
                    continue;
                }
                params.add(new Parameter(property.name(), BuiltinType.STRING));
            }
            return params;
        }

        private static List<Expression> buildCallArgs(ClassMediator mediator, ScopeContext context, String functionName) {
            List<Expression> args = new ArrayList<>();
            if (context.responseAvailable() || context.isWithinResource()) {
                if (!context.responseAvailable()) {
                    context.statements().add(0, new Statement.BallerinaStatement("http:Response response = new;"));
                    context.setRespondInitialized(true);
                }
                context.importStatements().add(new Import("ballerina", "http"));
                args.add(new Expression.VariableReference("response"));
            }
            for (Property property : mediator.properties()) {
                if (property.expression().isPresent()) {
                    continue;
                }
                args.add(new StringConstant(property.value()));
            }
            return args;
        }

        private static List<Statement> buildStubBody(ClassMediator mediator) {
            return List.of();
        }
    }

    static class UnsupportedConverter implements BIRConverter<ScopeContext> {

        @Override
        public void convert(SynapseNode node, ScopeContext context) {
            throw new UnsupportedOperationException("No converter implemented for Synapse node kind: " + node.kind());
        }
    }
}
