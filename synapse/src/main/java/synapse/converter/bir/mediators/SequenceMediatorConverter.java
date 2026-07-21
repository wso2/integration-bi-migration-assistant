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
import synapse.converter.ConversionContext;
import synapse.converter.ScopeContext;
import synapse.converter.bir.BIRConverter;
import synapse.model.Synapse.SequenceMediator;
import synapse.model.Synapse.SynapseNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts a Synapse {@code <sequence key="name"/>} mediator into a call to the
 * Ballerina function
 * generated for the referenced sequence. When that sequence takes a {@code Context ctx} parameter, the
 * call passes {@code ctx} (declaring it first if not already in scope). Every generated function returns
 * {@code error?}, so the call is always {@code check}ed; and if the referenced sequence responds
 * (directly or down a call chain), the enclosing scope is marked as responding, since the callee sends
 * the response through {@code ctx.caller}.
 */
public class SequenceMediatorConverter implements BIRConverter<ScopeContext> {

    @Override
    public void convert(SynapseNode node, ScopeContext context) {
        SequenceMediator sequenceMediator = (SequenceMediator) node;
        ConversionContext.SequenceMetadata metadata = context.shared().sequenceMetadata(sequenceMediator.key())
                .orElse(null);
        if (metadata == null) {
            throw new UnsupportedOperationException("No metadata found for referenced sequence '"
                    + sequenceMediator.key() + "'.");
        }
        List<Expression> args = new ArrayList<>();
        if (metadata.usesContext()) {
            context.ensureContextAvailable();
            args.add(new Expression.VariableReference("ctx"));
        }
        Expression call = new Expression.FunctionCall(sequenceMediator.key(), args);
        context.statements().add(new Statement.CallStatement(new Expression.Check(call)));
        if (metadata.containsRespond()) {
            context.markResponded();
        }
    }
}
