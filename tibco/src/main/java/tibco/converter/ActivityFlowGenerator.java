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
import common.BallerinaModel.Expression.Check;
import common.BallerinaModel.Expression.FunctionCall;
import common.BallerinaModel.Expression.Not;
import common.BallerinaModel.Expression.StringConstant;
import common.BallerinaModel.Expression.Trap;
import common.BallerinaModel.Expression.TypeCheckExpression;
import common.BallerinaModel.Expression.VariableReference;
import common.BallerinaModel.Expression.XMLTemplate;
import common.BallerinaModel.Statement;
import common.BallerinaModel.Statement.CallStatement;
import common.BallerinaModel.Statement.Comment;
import common.BallerinaModel.Statement.IfElseStatement;
import common.BallerinaModel.Statement.VarAssignStatement;
import common.BallerinaModel.Statement.VarDeclStatment;
import common.BallerinaModel.TypeDesc.UnionTypeDesc;
import org.jetbrains.annotations.NotNull;
import tibco.analyzer.AnalysisResult;
import tibco.model.Process5.ExplicitTransitionGroup;
import tibco.model.Process5.ExplicitTransitionGroup.InlineActivity;
import tibco.model.Process5.ExplicitTransitionGroup.Transition;
import tibco.model.Scope;
import tibco.model.Scope.Flow.Activity;
import tibco.model.Scope.Flow.Activity.Expression.XPath;
import tibco.model.Scope.Flow.Activity.Source.Predicate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

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
 * plain calls without any guards. Activities that can only be reached through links that never fire are left out.
 * <p>
 * BW6 flows take their links from the analysed scope; BW5 flows take them from the group's transitions.
 */
final class ActivityFlowGenerator {

    // Fields are direct children of the stored root, as mappings read them as `$_error/Msg` (`$_error/root/Msg`
    // after conversion); addToContext drops the outer ActivityErrorData element.
    private static final String ERROR_DATA_TEMPLATE =
            "<ns:ActivityErrorData xmlns:ns=\"http://www.tibco.com/pe/EngineTypes\">"
            + "<ProcessStack></ProcessStack><StackTrace>${%1$s.stackTrace().toString()}</StackTrace>"
            + "<Msg>${%1$s.message()}</Msg><MsgCode></MsgCode></ns:ActivityErrorData>";
    private static final String ERROR_VARIABLE = "_error";
    // Compared by identity, so it is never confused with a condition that merely evaluates to false.
    private static final Expression NEVER = exprFrom("false");

    private final ProcessContext cx;
    private final AnalysisResult analysisResult;
    private final FlowLinks links;
    private final Function<FunctionCall, Expression> callHandler;
    private final Set<Activity> activitiesInFlow;
    private final Set<String> linksIntoFlow = new HashSet<>();
    private final Map<Activity, Map<String, Optional<Expression>>> firedLinks = new HashMap<>();
    private final Map<String, Optional<Expression>> firedEntryLinks = new HashMap<>();
    private final Set<String> usedNames = new HashSet<>();
    private final VariableReference context = new VariableReference("cx");
    private final List<Statement> body = new ArrayList<>();

    private ActivityFlowGenerator(ProcessContext cx, FlowLinks links, List<Activity> activities,
                                  Function<FunctionCall, Expression> callHandler) {
        this.cx = cx;
        this.analysisResult = cx.getAnalysisResult();
        this.links = links;
        this.callHandler = callHandler;
        this.activitiesInFlow = new HashSet<>(activities);
        Set<Activity> deadEnds = deadEndEmptyActivities(activities);
        activities.stream()
                .filter(activity -> !deadEnds.contains(activity))
                .forEach(activity -> linksIntoFlow.addAll(links.incoming(activity)));
        // A local named like an activity function would shadow it for every later call in the runner.
        analysisResult.activities().forEach(activity -> usedNames.add(analysisResult.from(activity).functionName()));
    }

    static @NotNull List<Statement> generate(ProcessContext cx, List<Activity> activities,
                                             Function<FunctionCall, Expression> callHandler) {
        return generate(cx, new ScopeLinks(cx), activities, callHandler);
    }

    static @NotNull List<Statement> generate(ProcessContext cx, ExplicitTransitionGroup group,
                                             List<Activity> activities,
                                             Function<FunctionCall, Expression> callHandler) {
        return generate(cx, new TransitionLinks(cx, group), activities, callHandler);
    }

    private static @NotNull List<Statement> generate(ProcessContext cx, FlowLinks links, List<Activity> activities,
                                                     Function<FunctionCall, Expression> callHandler) {
        ActivityFlowGenerator generator = new ActivityFlowGenerator(cx, links, activities, callHandler);
        generator.firedEntryLinks.putAll(generator.firedLinks(links.linksFromFlowStart(), Optional.empty(),
                Optional.empty()));
        activities.forEach(generator::generateActivity);
        return generator.body;
    }

    // Empty activities have no function, so one that no live activity in the flow depends on needs neither its
    // reachability nor the links into it. Activities are ordered after their link sources, so a reverse walk sees
    // every target before its source.
    private Set<Activity> deadEndEmptyActivities(List<Activity> activities) {
        Map<String, List<Activity>> targetsByLink = new HashMap<>();
        activities.forEach(activity -> links.incoming(activity).forEach(link ->
                targetsByLink.computeIfAbsent(link, ignored -> new ArrayList<>()).add(activity)));
        Set<Activity> deadEnds = new HashSet<>();
        for (Activity activity : activities.reversed()) {
            if (activity instanceof Activity.Empty && links.outgoing(activity).stream()
                    .flatMap(link -> targetsByLink.getOrDefault(link.name(), List.of()).stream())
                    .allMatch(deadEnds::contains)) {
                deadEnds.add(activity);
            }
        }
        return deadEnds;
    }

    private void generateActivity(Activity activity) {
        Optional<Expression> reachedCondition = reachedCondition(activity);
        if (reachedCondition.isPresent() && reachedCondition.get() == NEVER) {
            Map<String, Optional<Expression>> fired = new HashMap<>();
            links.outgoing(activity).forEach(link -> fired.put(link.name(), Optional.of(NEVER)));
            firedLinks.put(activity, fired);
            return;
        }
        String functionName = analysisResult.from(activity).functionName();
        if (activity instanceof Activity.Empty) {
            boolean anchorsLinks = links.outgoing(activity).stream()
                    .anyMatch(link -> linksIntoFlow.contains(link.name()));
            firedLinks.put(activity, firedLinks(links.outgoing(activity), anchorsLinks
                    ? reachedCondition.map(condition -> materialize(functionName + "Reached", condition))
                    : Optional.empty(), Optional.empty()));
            return;
        }
        Optional<Expression> reached = reachedCondition
                .map(condition -> materialize(functionName + "Reached", condition));
        FunctionCall call = new FunctionCall(functionName, List.of(context));
        List<OutLink> errorLinks = errorLinksIntoFlow(activity);
        if (errorLinks.isEmpty()) {
            guarded(reached, new CallStatement(callHandler.apply(call)));
            firedLinks.put(activity, firedLinks(links.outgoing(activity), reached, Optional.empty()));
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
        firedLinks.put(activity, firedLinks(links.outgoing(activity), reached, Optional.of(result)));
    }

    // NEVER is kept apart from a condition that is merely false so that unreachable activities can be left out.
    private Optional<Expression> reachedCondition(Activity activity) {
        List<Expression> incoming = new ArrayList<>();
        boolean hasIncoming = false;
        for (String link : links.incoming(activity)) {
            List<Optional<Expression>> fired = new ArrayList<>();
            if (firedEntryLinks.containsKey(link)) {
                fired.add(firedEntryLinks.get(link));
            }
            for (Activity source : links.sources(link)) {
                if (!activitiesInFlow.contains(source)) {
                    continue;
                }
                assert firedLinks.containsKey(source) : "activities must be generated after their link sources";
                fired.add(firedLinks.get(source).getOrDefault(link, Optional.empty()));
            }
            for (Optional<Expression> each : fired) {
                if (each.isEmpty()) {
                    return Optional.empty();
                }
                hasIncoming = true;
                if (each.get() != NEVER) {
                    incoming.add(each.get());
                }
            }
        }
        if (!hasIncoming) {
            return Optional.empty();
        }
        return Optional.of(incoming.isEmpty() ? NEVER : anyOf(incoming));
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
    private List<OutLink> errorLinksIntoFlow(Activity activity) {
        return links.outgoing(activity).stream()
                .filter(link -> link.rule() instanceof Rule.Error && linksIntoFlow.contains(link.name()))
                .toList();
    }

    private List<Statement> storeFaultData(List<OutLink> errorLinks, VariableReference result) {
        Expression faultDetails = new XMLTemplate(ERROR_DATA_TEMPLATE.formatted(result.varName()));
        Set<String> detailVariables = new LinkedHashSet<>(List.of(ERROR_VARIABLE));
        Set<String> nameVariables = new LinkedHashSet<>();
        errorLinks.forEach(link -> {
            detailVariables.addAll(link.faultDetailsVariables());
            nameVariables.addAll(link.faultNameVariables());
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
    private Map<String, Optional<Expression>> firedLinks(List<OutLink> outgoing, Optional<Expression> reached,
                                                         Optional<VariableReference> result) {
        Map<String, Optional<Expression>> fired = new HashMap<>();
        // Conditional links come first, since an "otherwise" link is taken exactly when none of them is.
        List<Expression> conditionalTaken = new ArrayList<>();
        for (OutLink link : outgoing) {
            if (!(link.rule() instanceof Rule.Condition(Supplier<String> predicateFunction))) {
                continue;
            }
            // Transition predicates resolve their `$var` references from the context, so the input document is
            // only the XPath evaluation root and carries no data.
            Expression condition = new Check(new FunctionCall(predicateFunction.get(),
                    List.of(new XMLTemplate("<root></root>"), context)));
            if (!linksIntoFlow.contains(link.name())) {
                conditionalTaken.add(condition);
                continue;
            }
            Optional<Expression> taken = firedCondition(link.name(),
                    successParts(reached, result, Optional.of(condition)));
            fired.put(link.name(), taken);
            conditionalTaken.add(taken.orElseThrow());
        }
        for (OutLink link : outgoing) {
            if (link.rule() instanceof Rule.Condition || !linksIntoFlow.contains(link.name())) {
                continue;
            }
            // Neither an Empty activity nor the flow's entry runs anything, so their error links never fire.
            if (link.rule() instanceof Rule.Error && result.isEmpty()) {
                fired.put(link.name(), Optional.of(NEVER));
                continue;
            }
            if (link.rule() instanceof Rule.Unsupported) {
                body.add(new Comment(("FIXME: the condition of transition '%s' could not be converted; "
                        + "it is always taken").formatted(link.name())));
            }
            // The result stays () when the source is skipped, so the type test alone already implies reached.
            List<Expression> parts = link.rule() instanceof Rule.Error
                    ? List.of(new TypeCheckExpression(result.orElseThrow(), ERROR))
                    : successParts(reached, result, link.rule() instanceof Rule.Otherwise
                            && !conditionalTaken.isEmpty()
                            ? Optional.of(noneOf(conditionalTaken))
                            : Optional.empty());
            fired.put(link.name(), firedCondition(link.name(), parts));
        }
        return fired;
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

    private sealed interface Rule {

        record Success() implements Rule {

        }

        // Naming is deferred until the condition is generated so that BW6 predicate functions keep the numbering
        // they had before BW5 transitions shared this generator.
        record Condition(Supplier<String> predicateFunction) implements Rule {

        }

        record Otherwise() implements Rule {

        }

        record Error() implements Rule {

        }

        record Unsupported() implements Rule {

        }
    }

    private record OutLink(String name, Rule rule, List<String> faultDetailsVariables,
                           List<String> faultNameVariables) {

    }

    private interface FlowLinks {

        @NotNull List<String> incoming(Activity target);

        @NotNull List<Activity> sources(String link);

        @NotNull List<OutLink> outgoing(Activity source);

        @NotNull List<OutLink> linksFromFlowStart();
    }

    private record ScopeLinks(ProcessContext cx) implements FlowLinks {

        @Override
        public @NotNull List<String> incoming(Activity target) {
            return cx.getAnalysisResult().sources(target).stream().map(Scope.Flow.Link::name).toList();
        }

        @Override
        public @NotNull List<Activity> sources(String link) {
            return List.copyOf(cx.getAnalysisResult().sources(new Scope.Flow.Link(link)));
        }

        @Override
        public @NotNull List<OutLink> outgoing(Activity source) {
            if (!(source instanceof Activity.ActivityWithSources withSources)) {
                return List.of();
            }
            return withSources.sources().stream()
                    .map(each -> new OutLink(each.linkName(), rule(each.condition()),
                            each.faultDetailsVar().stream().toList(), each.faultNameVar().stream().toList()))
                    .toList();
        }

        @Override
        public @NotNull List<OutLink> linksFromFlowStart() {
            return List.of();
        }

        private Rule rule(Optional<Predicate> condition) {
            return switch (condition.orElse(null)) {
                case XPath xPath -> new Rule.Condition(() -> cx.predicateFunction(xPath));
                case Predicate.Else ignored -> new Rule.Otherwise();
                case Predicate.Error ignored -> new Rule.Error();
                case null -> new Rule.Success();
            };
        }
    }

    // BW5 transitions are unnamed, so each gets a readable name that is made unique, as names built from activity
    // names can collide (`X_to` -> `Y` and `X` -> `to_Y`).
    private static final class TransitionLinks implements FlowLinks {

        private static final String ERROR_VARIABLE_PREFIX = "_error_";

        private final ProcessContext cx;
        private final Map<String, List<String>> incomingByTarget = new HashMap<>();
        private final Map<String, Activity> sourceByLink = new HashMap<>();
        private final Map<String, List<OutLink>> outgoingBySource = new LinkedHashMap<>();
        private final Set<String> activityNames = new HashSet<>();

        private TransitionLinks(ProcessContext cx, ExplicitTransitionGroup group) {
            this.cx = cx;
            Map<String, Activity> activitiesByName = new HashMap<>();
            group.activities().forEach(activity -> activitiesByName.put(activity.name(), activity));
            group.startActivity().ifPresent(start -> activitiesByName.put(start.name(), start));
            activityNames.addAll(activitiesByName.keySet());
            Set<String> linkNames = new HashSet<>();
            for (Transition transition : group.transitions()) {
                String link = uniqueName(transition.from() + "_to_" + transition.to(), linkNames);
                incomingByTarget.computeIfAbsent(transition.to(), ignored -> new ArrayList<>()).add(link);
                Optional.ofNullable(activitiesByName.get(transition.from()))
                        .ifPresent(source -> sourceByLink.put(link, source));
                outgoingBySource.computeIfAbsent(transition.from(), ignored -> new ArrayList<>())
                        .add(outLink(link, transition));
            }
        }

        private static String uniqueName(String baseName, Set<String> usedNames) {
            String name = baseName;
            for (int i = 1; !usedNames.add(name); i++) {
                name = baseName + i;
            }
            return name;
        }

        // Mappings read the fault of a specific activity as `$_error_<activity>`, with spaces written as '-'.
        private OutLink outLink(String name, Transition transition) {
            return switch (transition.condition()) {
                case Transition.Condition.Always ignored ->
                        new OutLink(name, new Rule.Success(), List.of(), List.of());
                case Transition.Condition.XPath xPath -> new OutLink(name,
                        new Rule.Condition(() -> cx.transitionPredicateFunction(xPath)), List.of(), List.of());
                case Transition.Condition.Otherwise ignored ->
                        new OutLink(name, new Rule.Otherwise(), List.of(), List.of());
                case Transition.Condition.Error ignored -> new OutLink(name, new Rule.Error(),
                        List.of(ERROR_VARIABLE_PREFIX + transition.from().replace(' ', '-')), List.of());
                case Transition.Condition.Unsupported ignored ->
                        new OutLink(name, new Rule.Unsupported(), List.of(), List.of());
            };
        }

        @Override
        public @NotNull List<String> incoming(Activity target) {
            if (!(target instanceof InlineActivity activity)) {
                return List.of();
            }
            return incomingByTarget.getOrDefault(activity.name(), List.of());
        }

        @Override
        public @NotNull List<Activity> sources(String link) {
            return Optional.ofNullable(sourceByLink.get(link)).stream().toList();
        }

        @Override
        public @NotNull List<OutLink> outgoing(Activity source) {
            if (!(source instanceof InlineActivity activity)) {
                return List.of();
            }
            return outgoingBySource.getOrDefault(activity.name(), List.of());
        }

        @Override
        public @NotNull List<OutLink> linksFromFlowStart() {
            return outgoingBySource.entrySet().stream()
                    .filter(entry -> !activityNames.contains(entry.getKey()))
                    .flatMap(entry -> entry.getValue().stream())
                    .toList();
        }
    }
}
