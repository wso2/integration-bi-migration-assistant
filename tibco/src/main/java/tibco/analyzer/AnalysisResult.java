package tibco.analyzer;

import common.BallerinaModel;
import tibco.TibcoModel;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

public interface AnalysisResult {
    /**
     * Creates and returns an immutable, empty instance of {@code AnalysisResult}.
     * <p>
     * This factory method is intended for cases where an empty result is required.
     * All internal maps in the returned instance are empty and immutable.
     *
     * @return an immutable, empty {@code AnalysisResult} instance
     */
    static AnalysisResult empty() {
        return new AnalysisResultImpl(Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(),
                Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(),
                Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(),
                Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap());
    }

    Collection<String> inputTypeName(TibcoModel.Process process);

    String outputTypeName(TibcoModel.Process process);

    Collection<TibcoModel.Scope.Flow.Activity> sources(TibcoModel.Scope.Flow.Link link);

    Optional<TibcoModel.Scope.Flow.Activity> findActivity(String name);

    String variableType(TibcoModel.Process process, String variableName);

    ActivityData from(TibcoModel.Scope.Flow.Activity activity);

    Collection<TibcoModel.Scope.Flow.Activity> activities();

    Collection<TibcoModel.Scope.Flow.Link> links();

    Collection<TibcoModel.Scope.Flow.Link> sources(TibcoModel.Scope.Flow.Activity activity);

    Stream<TransitionData> transitionConditions(
            TibcoModel.Scope.Flow.Activity activity);

    Stream<TibcoModel.Scope.Flow.Activity.Source.Predicate> transitionCondition(
            TibcoModel.Scope.Flow.Activity activity, TibcoModel.Scope.Flow.Link link);

    Stream<TibcoModel.Scope.Flow.Activity> sortedActivities(TibcoModel.Scope scope);

    Stream<TibcoModel.Scope.Flow.Activity> sortedActivities(TibcoModel.Process.ExplicitTransitionGroup group);

    Stream<TibcoModel.Scope.Flow.Activity> sortedErrorHandlerActivities(
            TibcoModel.Process.ExplicitTransitionGroup group);

    Collection<TibcoModel.Scope> scopes(TibcoModel.Process process);

    TibcoModel.PartnerLink.Binding getBinding(String partnerLinkName);

    ControlFlowFunctions getControlFlowFunctions(TibcoModel.Scope scope);

    ControlFlowFunctions getControlFlowFunctions(TibcoModel.Process.ExplicitTransitionGroup group);

    AnalysisResult combine(AnalysisResult other);

    record ActivityData(String functionName, BallerinaModel.TypeDesc argumentType,
                        BallerinaModel.TypeDesc returnType) {

    }

    record TransitionData(TibcoModel.Scope.Flow.Activity activity,
                          TibcoModel.Scope.Flow.Activity.Source.Predicate predicate) {

    }

    record GraphNode(String name, Kind kind, Object data) {

        public enum Kind {
            ACTIVITY, LINK, INLINE_ACTIVITY
        }
    }

    record ControlFlowFunctions(String scopeFn, String activityRunner, String errorHandler) {
    }
}
