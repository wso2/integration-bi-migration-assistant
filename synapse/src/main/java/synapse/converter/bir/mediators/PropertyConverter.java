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
package synapse.converter.bir.mediators;

import common.BallerinaModel.Statement;
import common.BallerinaModel.TypeDesc.BuiltinType;
import synapse.converter.ScopeContext;
import synapse.converter.bir.BIRConverter;
import synapse.model.Synapse.Property;
import synapse.model.Synapse.SynapseNode;

import java.util.Locale;

/**
 * Converts a Synapse {@code <property>} mediator. How a property is converted
 * depends on where it
 * lives: a property within a resource contributes to that resource's body,
 * whereas a property
 * outside a resource (e.g. an api-level property) is handled differently. This
 * converter therefore
 * first identifies its scope.
 */
public class PropertyConverter implements BIRConverter<ScopeContext> {

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
                    "ctx.variables." + property.name() + " = " + BuiltinType.NIL + ";"));
            return;
        }
        context.shared().addProperty(property.name(), toBallerinaType(property.type()), property.scope());
        context.statements().add(new Statement.BallerinaStatement(
                "ctx.variables." + property.name() + " = " + property.value() + ";"));
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
