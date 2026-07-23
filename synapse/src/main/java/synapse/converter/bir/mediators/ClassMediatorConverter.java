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
import common.BallerinaModel.Expression.StringConstant;
import common.BallerinaModel.Function;
import common.BallerinaModel.Parameter;
import common.BallerinaModel.Statement;
import common.BallerinaModel.TypeDesc;
import common.BallerinaModel.TypeDesc.BuiltinType;
import synapse.converter.ScopeContext;
import synapse.converter.bir.BIRConverter;
import synapse.model.Synapse.ClassMediator;
import synapse.model.Synapse.Property;
import synapse.model.Synapse.SynapseNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts a Synapse {@code <class>} mediator into a call to a generated Ballerina stub function.
 *
 * <p>Because the mediator's Java logic cannot be automatically translated, a stub function is
 * emitted into {@code functions.bal} with an empty body. The stub takes the {@code Context ctx}
 * followed by each static {@code <property>} value as a {@code string} argument; dynamic
 * {@code expression} properties are omitted, since they cannot be evaluated statically.
 * The developer replaces the stub body with the equivalent Ballerina.
 */
public class ClassMediatorConverter implements BIRConverter<ScopeContext> {

    @Override
    public void convert(SynapseNode node, ScopeContext context) {
        ClassMediator classMediator = (ClassMediator) node;
        String functionName = stubFunctionName(classMediator.className());

        // Register the stub only once: the same mediator (or two classes sharing a simple name)
        // can appear at several sites, but functions.bal must declare each function name once.
        if (!isStubRegistered(context, functionName)) {
            context.shared().addFunction(
                    new Function(functionName, buildStubParams(classMediator), buildStubBody()));
        }

        context.statements().add(new Statement.CallStatement(
                new Expression.FunctionCall(functionName, buildCallArgs(classMediator, context))));
    }

    /** Converts {@code org.example.MyMediator} to {@code myMediator}. */
    private static String stubFunctionName(String className) {
        String simpleName = className.contains(".")
                ? className.substring(className.lastIndexOf('.') + 1)
                : className;
        return Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
    }

    private static boolean isStubRegistered(ScopeContext context, String functionName) {
        return context.shared().functions().stream()
                .anyMatch(f -> f.functionName().equals(functionName));
    }

    private static List<Parameter> buildStubParams(ClassMediator mediator) {
        List<Parameter> params = new ArrayList<>();
        params.add(new Parameter("ctx", new TypeDesc.BallerinaType("Context")));
        for (Property property : mediator.properties()) {
            if (property.expression().isPresent()) {
                // Dynamic (XPath/JSONPath) values are resolved at runtime, so they cannot become
                // a static string parameter.
                continue;
            }
            params.add(new Parameter(property.name(), BuiltinType.STRING));
        }
        return params;
    }

    private static List<Expression> buildCallArgs(ClassMediator mediator, ScopeContext context) {
        context.ensureContextAvailable();
        List<Expression> args = new ArrayList<>();
        args.add(new Expression.VariableReference("ctx"));
        for (Property property : mediator.properties()) {
            if (property.expression().isPresent()) {
                continue;
            }
            args.add(new StringConstant(property.value()));
        }
        return args;
    }

    private static List<Statement> buildStubBody() {
        return List.of();
    }
}
