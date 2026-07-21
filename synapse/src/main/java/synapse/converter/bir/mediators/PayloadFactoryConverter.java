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

import common.BallerinaModel.Expression;
import common.BallerinaModel.Expression.BallerinaExpression;
import common.BallerinaModel.Expression.StringConstant;
import common.BallerinaModel.Expression.XMLTemplate;
import common.BallerinaModel.Statement;
import synapse.converter.ScopeContext;
import synapse.converter.bir.BIRConverter;
import synapse.model.Synapse.PayloadFactory;
import synapse.model.Synapse.SynapseNode;

/**
 * Converts a Synapse {@code <payloadFactory>} mediator into an assignment of the built payload onto
 * {@code ctx.payload}. The payload is not written to an {@code http:Response} here; a later
 * {@code <respond>} reads it back off {@code ctx} through the generated {@code respond} utility.
 */
public class PayloadFactoryConverter implements BIRConverter<ScopeContext> {

    @Override
    public void convert(SynapseNode node, ScopeContext context) {
        PayloadFactory payloadFactory = (PayloadFactory) node;
        Expression value = extractValue(payloadFactory.mediaType(), payloadFactory.format());
        context.ensureContextAvailable();
        context.statements().add(new Statement.VarAssignStatement(
                new Expression.FieldAccess(new Expression.VariableReference("ctx"), "payload"), value));
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
