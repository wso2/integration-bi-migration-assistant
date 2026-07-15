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

import common.BallerinaModel.Function;
import common.BallerinaModel.Parameter;
import common.BallerinaModel.Statement;
import common.BallerinaModel.TypeDesc;
import common.BallerinaModel.TypeDesc.BuiltinType;
import synapse.converter.ConversionContext;
import synapse.converter.SequenceContext;
import synapse.model.Synapse.Sequence;
import synapse.model.Synapse.SynapseNode;

import java.util.ArrayList;
import java.util.List;

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
public class SequenceConverter implements BIRConverter<ConversionContext> {

    @Override
    public void convert(SynapseNode node, ConversionContext context) {
        Sequence sequence = (Sequence) node;
        SequenceContext sequenceContext = new SequenceContext(context);
        MediatorConverters.convertMediators(sequence.mediators(), sequenceContext);
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
