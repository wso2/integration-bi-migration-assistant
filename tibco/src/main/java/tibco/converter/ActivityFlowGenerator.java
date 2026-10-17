/*
 *  Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com).
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

package tibco.converter;

import common.BallerinaModel.Expression;
import common.BallerinaModel.Expression.BinaryLogical;
import common.BallerinaModel.Expression.FunctionCall;
import common.BallerinaModel.Expression.Not;
import common.BallerinaModel.Expression.StringConstant;
import common.BallerinaModel.Expression.Trap;
import common.BallerinaModel.Expression.TypeCheckExpression;
import common.BallerinaModel.Expression.VariableReference;
import common.BallerinaModel.Expression.XMLTemplate;
import common.BallerinaModel.Statement;
import common.BallerinaModel.Statement.CallStatement;
import common.BallerinaModel.Statement.IfElseStatement;
import common.BallerinaModel.Statement.VarAssignStatement;
import common.BallerinaModel.Statement.VarDeclStatment;
import common.BallerinaModel.TypeDesc.UnionTypeDesc;
import org.jetbrains.annotations.NotNull;
import tibco.analyzer.AnalysisResult;
import tibco.model.Scope;
import tibco.model.Scope.Flow.Activity;
import tibco.model.Scope.Flow.Activity.Expression.XPath;
import tibco.model.Scope.Flow.Activity.Source.Predicate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static common.BallerinaModel.TypeDesc.BuiltinType.BOOLEAN;
import static common.BallerinaModel.TypeDesc.BuiltinType.ERROR;
import static common.BallerinaModel.TypeDesc.BuiltinType.NIL;
import static common.ConversionUtils.exprFrom;

/**
 * Generates the body of an activity runner from link reachability, following TIBCO's dead-path elimination: an
 * activity runs only if at least one of its incoming links fired, and a link fires only if its source ran and the
 * link's own rule (success, condition, otherwise or error) held when the source completed.
 * <p>
 * A reachability that is always true is represented by an empty {@code Optional}, so straight-line flows produce
 * plain calls without any guards.
 */
final class ActivityFlowGenerator {

    // Fields are direct children of the stored root, as mappings read them as `$_error/Msg` (`$_error/root/Msg`
    // after conversion); addToContext drops the outer ActivityErrorData element.
    private static final String ERROR_DATA_TEMPLATE =
            "<ns:ActivityErrorData xmlns:ns=\"http://www.tibco.com/pe/EngineTypes\">"
            + "<ProcessStack></ProcessStack><StackTrace>${%1$s.stackTrace().toString()}</StackTrace>"
            + "<Msg>${%1$s.message()}</Msg><MsgCode></MsgCode></ns:ActivityErrorData>";
    private static final String ERROR_VARIABLE = "_error";

    private final ProcessContext cx;
    private final AnalysisResult analysisResult;
    private final Function<FunctionCall, Expression> callHandler;
    private final Set<Activity> activitiesInFlow;
    private final Set<String> linksIntoFlow = new HashSet<>();
    private final Map<Activity, Map<String, Optional<Expression>>> firedLinks = new HashMap<>();
    private final Set<String> usedNames = new HashSet<>();
    private final VariableReference context = new VariableReference("cx");
    private final List<Statement> body = new ArrayList<>();

    private ActivityFlowGenerator(ProcessContext cx, List<Activity> activities,
                                  Function<FunctionCall, Expression> callHandler) {
        this.cx = cx;
        this.analysisResult = cx.getAnalysisResult();
        this.callHandler = callHandler;
        this.activitiesInFlow = new HashSet<>(activities);
        activities.forEach(activity -> analysisResult.sources(activity)
                .forEach(link -> linksIntoFlow.add(link.name())));
        // A local named like an activity function would shadow it for every later call in the runner.
        analysisResult.activities().forEach(activity -> usedNames.add(analysisResult.from(activity).functionName()));
    }

    static @NotNull List<Statement> generate(ProcessContext cx, List<Activity> activities,
                                             Function<FunctionCall, Expression> callHandler) {
        ActivityFlowGenerator generator = new ActivityFlowGenerator(cx, activities, callHandler);
        activities.forEach(generator::generateActivity);
        return generator.body;
    }

    private void generateActivity(Activity activity) {
        String functionName = analysisResult.from(activity).functionName();
        Optional<Expression> reached = reachedCondition(activity)
                .map(condition -> materialize(functionName + "Reached", condition));
        FunctionCall call = new FunctionCall(functionName, List.of(context));
        List<Activity.Source> errorLinks = errorLinksIntoFlow(activity);
        if (errorLinks.isEmpty()) {
            guarded(reached, new CallStatement(callHandler.apply(call)));
            addFiredLinks(activity, reached, Optional.empty());
            return;
        }
        // An error link takes over any fault of its source, including panics such as those raised by Throw.
        VariableReference result = new VariableReference(freshName(functionName + "Result"));
        if (reached.isEmpty()) {
            body.add(new VarDeclStatment(UnionTypeDesc.of(NIL, ERROR), result.varName(), new Trap(call)));
        } else {
            body.add(new VarDeclStatment(UnionTypeDesc.of(NIL, ERROR), result.varName(), exprFrom("()")));
            guarded(reached, new VarAssignStatement(result, new Trap(call)));
        }
        body.add(IfElseStatement.ifStatement(new TypeCheckExpression(result, ERROR),
                storeFaultData(errorLinks, result)));
        addFiredLinks(activity, reached, Optional.of(result));
    }

    private Optional<Expression> reachedCondition(Activity activity) {
        List<Expression> incoming = new ArrayList<>();
        for (Scope.Flow.Link link : analysisResult.sources(activity)) {
            for (Activity source : analysisResult.sources(link)) {
                if (!activitiesInFlow.contains(source)) {
                    continue;
                }
                assert firedLinks.containsKey(source) : "activities must be generated after their link sources";
                Optional<Expression> fired = firedLinks.get(source).getOrDefault(link.name(), Optional.empty());
                if (fired.isEmpty()) {
                    return Optional.empty();
                }
                incoming.add(fired.get());
            }
        }
        return incoming.isEmpty() ? Optional.empty() : Optional.of(anyOf(incoming));
    }

    // Consecutive activities on the same path share one if-block; this is safe because reachability flags are
    // never reassigned.
    private void guarded(Optional<Expression> reached, Statement statement) {
        if (reached.isEmpty()) {
            body.add(statement);
            return;
        }
        Expression condition = reached.get();
        if (!body.isEmpty() && body.getLast() instanceof IfElseStatement previous
                && previous.ifCondition().equals(condition) && previous.elseIfClauses().isEmpty()
                && previous.elseBody().isEmpty()) {
            List<Statement> merged = new ArrayList<>(previous.ifBody());
            merged.add(statement);
            body.set(body.size() - 1, IfElseStatement.ifStatement(condition, merged));
            return;
        }
        body.add(IfElseStatement.ifStatement(condition, List.of(statement)));
    }

    // An error is only handled here when its error path continues inside this flow; otherwise it must still
    // propagate to the enclosing scope's fault handler.
    private List<Activity.Source> errorLinksIntoFlow(Activity activity) {
        if (!(activity instanceof Activity.ActivityWithSources withSources)) {
            return List.of();
        }
        return withSources.sources().stream()
                .filter(source -> source.condition().orElse(null) instanceof Predicate.Error
                        && linksIntoFlow.contains(source.linkName()))
                .toList();
    }

    private List<Statement> storeFaultData(List<Activity.Source> errorLinks, VariableReference result) {
        Expression faultDetails = new XMLTemplate(ERROR_DATA_TEMPLATE.formatted(result.varName()));
        Set<String> detailVariables = new LinkedHashSet<>(List.of(ERROR_VARIABLE));
        Set<String> nameVariables = new LinkedHashSet<>();
        errorLinks.forEach(link -> {
            link.faultDetailsVar().ifPresent(detailVariables::add);
            link.faultNameVar().ifPresent(nameVariables::add);
        });
        List<Statement> statements = new ArrayList<>();
        detailVariables.forEach(variable -> statements.add(addToContext(variable, faultDetails)));
        nameVariables.forEach(variable -> statements.add(addToContext(variable,
                new XMLTemplate("<root>${%s.message()}</root>".formatted(result.varName())))));
        return statements;
    }

    private Statement addToContext(String key, Expression value) {
        return new CallStatement(new FunctionCall(cx.getAddToContextFn(),
                List.of(context, new StringConstant(key), value)));
    }

    // Flags are computed right after the source ran, as TIBCO evaluates a link's condition when its source
    // completes, before any later activity can change the variables the condition reads.
    private void addFiredLinks(Activity activity, Optional<Expression> reached, Optional<VariableReference> result) {
        Map<String, Optional<Expression>> fired = new HashMap<>();
        firedLinks.put(activity, fired);
        if (!(activity instanceof Activity.ActivityWithSources withSources)) {
            return;
        }
        // Conditional links come first, since an "otherwise" link is taken exactly when none of them is.
        List<Expression> conditionalTaken = new ArrayList<>();
        for (Activity.Source source : withSources.sources()) {
            if (!(source.condition().orElse(null) instanceof XPath xPath)) {
                continue;
            }
            // Transition predicates resolve their `$var` references from the context, so the input document is
            // only the XPath evaluation root and carries no data.
            Expression condition = new FunctionCall(cx.predicateFunction(xPath),
                    List.of(new XMLTemplate("<root></root>"), context));
            if (!linksIntoFlow.contains(source.linkName())) {
                conditionalTaken.add(condition);
                continue;
            }
            Optional<Expression> taken = firedCondition(source.linkName(),
                    successParts(reached, result, Optional.of(condition)));
            fired.put(source.linkName(), taken);
            conditionalTaken.add(taken.orElseThrow());
        }
        for (Activity.Source source : withSources.sources()) {
            Predicate predicate = source.condition().orElse(null);
            if (predicate instanceof XPath || !linksIntoFlow.contains(source.linkName())) {
                continue;
            }
            // The result stays () when the source is skipped, so the type test alone already implies reached.
            List<Expression> parts = predicate instanceof Predicate.Error
                    ? List.of(new TypeCheckExpression(result.orElseThrow(), ERROR))
                    : successParts(reached, result, predicate instanceof Predicate.Else
                            && !conditionalTaken.isEmpty()
                            ? Optional.of(noneOf(conditionalTaken))
                            : Optional.empty());
            fired.put(source.linkName(), firedCondition(source.linkName(), parts));
        }
    }

    private static List<Expression> successParts(Optional<Expression> reached, Optional<VariableReference> result,
                                                 Optional<Expression> condition) {
        List<Expression> parts = new ArrayList<>();
        reached.ifPresent(parts::add);
        result.ifPresent(ref -> parts.add(exprFrom(ref.varName() + " !is error")));
        condition.ifPresent(parts::add);
        return parts;
    }

    private Optional<Expression> firedCondition(String linkName, List<Expression> parts) {
        if (parts.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(materialize(ConversionUtils.sanitizes(linkName) + "Taken", parts.size() == 1
                ? parts.getFirst()
                : parts.stream()
                        .map(part -> part instanceof VariableReference ? part : braced(part))
                        .reduce((left, right) -> new BinaryLogical(left, right, BinaryLogical.Operator.AND))
                        .orElseThrow()));
    }

    private Expression materialize(String baseName, Expression condition) {
        if (condition instanceof VariableReference) {
            return condition;
        }
        VarDeclStatment declaration = new VarDeclStatment(BOOLEAN, freshName(baseName), condition);
        body.add(declaration);
        return declaration.ref();
    }

    private String freshName(String baseName) {
        String name = baseName;
        for (int i = 1; !usedNames.add(name); i++) {
            name = baseName + i;
        }
        return name;
    }

    private static Expression anyOf(List<Expression> expressions) {
        assert !expressions.isEmpty();
        return expressions.stream()
                .reduce((left, right) -> new BinaryLogical(left, right, BinaryLogical.Operator.OR))
                .orElseThrow();
    }

    private static Expression noneOf(List<Expression> expressions) {
        return new Not(expressions.size() == 1 && expressions.getFirst() instanceof VariableReference reference
                ? reference : braced(anyOf(expressions)));
    }

    private static Expression braced(Expression expression) {
        return exprFrom("(" + expression + ")");
    }
}
