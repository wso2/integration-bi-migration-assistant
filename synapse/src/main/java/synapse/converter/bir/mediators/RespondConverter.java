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
import common.BallerinaModel.Statement;
import synapse.converter.ScopeContext;
import synapse.converter.bir.BIRConverter;
import synapse.model.Synapse.SynapseNode;

import java.util.List;

/**
 * Converts a Synapse {@code <respond>} mediator into {@code check respond(ctx);}, sending the response
 * built from {@code ctx.payload} to {@code ctx.caller} via the generated {@code respond} utility. A
 * respond is terminal, so mediator conversion stops after it.
 */
public class RespondConverter implements BIRConverter<ScopeContext> {

    @Override
    public void convert(SynapseNode node, ScopeContext context) {
        context.markResponded();
        context.statements().add(new Statement.CallStatement(new Expression.Check(
                new Expression.FunctionCall("respond", List.of(new Expression.VariableReference("ctx"))))));
    }
}
