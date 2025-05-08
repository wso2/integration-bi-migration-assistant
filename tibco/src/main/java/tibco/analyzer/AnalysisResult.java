/*
 *  Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
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

package tibco.analyzer;

import common.BallerinaModel;
import org.jetbrains.annotations.NotNull;
import tibco.TibcoModel;
import tibco.TibcoModel.Process.ExplicitTransitionGroup;
import tibco.converter.ConversionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public final class AnalysisResult {

    private final Map<TibcoModel.Scope.Flow.Link, Collection<TibcoModel.Scope.Flow.Activity>> destinationMap;
    private final Map<TibcoModel.Scope.Flow.Link, Collection<TibcoModel.Scope.Flow.Activity>> sourceMap;
    private final Map<TibcoModel.Scope.Flow.Activity, ActivityData> activityData;
    private final Map<String, TibcoModel.PartnerLink.RestPartnerLink.Binding> partnerLinkBindings;
    private final Map<TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.SQL, Integer> queryIndex;
    private final Map<TibcoModel.Process, Collection<String>> inputTypeNames;
    private final Map<TibcoModel.Process, String> outputTypeName;
    private final Map<TibcoModel.Process, Map<String, String>> variableTypes;
    private final Map<TibcoModel.Scope, Graph<GraphNode>> dependencyGraphs;
    private final Map<TibcoModel.Scope, ControlFlowFunctions> controlFlowFunctions;
    private final Map<TibcoModel.Process, Collection<TibcoModel.Scope>> scopes;
    private final Map<String, TibcoModel.Scope.Flow.Activity> activityByName;
    private final Map<ExplicitTransitionGroup, Graph<GraphNode>> explicitTransitionGroupDependencies;
    private final Map<ExplicitTransitionGroup, ControlFlowFunctions> explicitTransitionGroupControlFlowFunctions;

    AnalysisResult(Map<TibcoModel.Scope.Flow.Link, Collection<TibcoModel.Scope.Flow.Activity>> destinationMap,
                   Map<TibcoModel.Scope.Flow.Link, Collection<TibcoModel.Scope.Flow.Activity>> sourceMap,
                   Map<TibcoModel.Scope.Flow.Activity, ActivityData> activityData,
                   Map<String, TibcoModel.PartnerLink.RestPartnerLink.Binding> partnerLinkBindings,
                   Map<TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.SQL, Integer> queryIndex,
                   Map<TibcoModel.Process, Collection<String>> inputTypeNames,
                   Map<TibcoModel.Process, String> outputTypeName,
                   Map<TibcoModel.Process, Map<String, String>> variableTypes,
                   Map<TibcoModel.Scope, Graph<GraphNode>> dependencyGraphs,
                   Map<TibcoModel.Scope, ControlFlowFunctions> controlFlowFunctions,
                   Map<TibcoModel.Process, Collection<TibcoModel.Scope>> scopes,
                   Map<String, TibcoModel.Scope.Flow.Activity> activityByName,
                   Map<ExplicitTransitionGroup, Graph<GraphNode>> explicitTransitionGroupDependencies,
                   Map<ExplicitTransitionGroup, ControlFlowFunctions> explicitTransitionGroupControlFlowFunctions) {
        this.destinationMap = destinationMap;
        this.sourceMap = sourceMap;
        this.activityData = activityData;
        this.partnerLinkBindings = partnerLinkBindings;
        this.queryIndex = queryIndex;
        this.inputTypeNames = inputTypeNames;
        this.outputTypeName = outputTypeName;
        this.variableTypes = variableTypes;
        this.dependencyGraphs = dependencyGraphs;
        this.controlFlowFunctions = controlFlowFunctions;
        this.scopes = scopes;
        this.activityByName = activityByName;
        this.explicitTransitionGroupDependencies = explicitTransitionGroupDependencies;
        this.explicitTransitionGroupControlFlowFunctions = explicitTransitionGroupControlFlowFunctions;
    }

    public Collection<String> inputTypeName(TibcoModel.Process process) {
        return inputTypeNames.get(process);
    }

    public String outputTypeName(TibcoModel.Process process) {
        return outputTypeName.get(process);
    }

    public Collection<TibcoModel.Scope.Flow.Activity> sources(TibcoModel.Scope.Flow.Link link) {
        var sources = sourceMap.get(link);
        // this can happen for invalid activities
        if (sources == null) {
            return List.of();
        }
        return sources;
    }

    public Optional<TibcoModel.Scope.Flow.Activity> findActivity(String name) {
        return Optional.ofNullable(activityByName.get(name));
    }

    public String variableType(TibcoModel.Process process, String variableName) {
        var variableType = variableTypes.get(process);
        if (variableType == null) {
            throw new IllegalArgumentException("No variable type found for process: " + process);
        }
        return Objects.requireNonNull(variableType.get(variableName),
                () -> "Variable type not found for: " + variableName);
    }

    public ActivityData from(TibcoModel.Scope.Flow.Activity activity) {
        var data = activityData.get(activity);
        if (data == null) {
            throw new IllegalArgumentException("No data found for activity: " + activity);
        }
        return data;
    }

    public Collection<TibcoModel.Scope.Flow.Activity> activities() {
        return activityData.keySet();
    }

    public Collection<TibcoModel.Scope.Flow.Link> links() {
        return destinationMap.keySet();
    }

    public Collection<TibcoModel.Scope.Flow.Link> sources(TibcoModel.Scope.Flow.Activity activity) {
        if (activity instanceof TibcoModel.Scope.Flow.Activity.ActivityWithTargets targets) {
            return targets.targets().stream().map(TibcoModel.Scope.Flow.Activity.Target::linkName)
                    .map(TibcoModel.Scope.Flow.Link::new)
                    .toList();
        }
        return List.of();
    }

    public int queryIndex(TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.SQL sql) {
        if (!queryIndex.containsKey(sql)) {
            throw new IllegalArgumentException("No query index found for: " + sql);
        }
        return queryIndex.get(sql);
    }

    public Stream<TransitionData> transitionConditions(
            TibcoModel.Scope.Flow.Activity activity) {
        if (!(activity instanceof TibcoModel.Scope.Flow.Activity.ActivityWithTargets activityWithTargets)) {
            return Stream.empty();
        }
        Stream.Builder<Stream<TransitionData>> predicateStreams = Stream.builder();
        for (TibcoModel.Scope.Flow.Activity.Target target : activityWithTargets.targets()) {
            TibcoModel.Scope.Flow.Link link = new TibcoModel.Scope.Flow.Link(target.linkName());
            Collection<TibcoModel.Scope.Flow.Activity> sources = sourceMap.get(link);
            for (TibcoModel.Scope.Flow.Activity source : sources) {
                predicateStreams.add(transitionCondition(source, link).map(prec ->
                        new TransitionData(source, prec)));
            }
        }
        return predicateStreams.build().flatMap(Function.identity());
    }

    public Stream<TibcoModel.Scope.Flow.Activity.Source.Predicate> transitionCondition(
            TibcoModel.Scope.Flow.Activity activity, TibcoModel.Scope.Flow.Link link) {
        if (!(activity instanceof TibcoModel.Scope.Flow.Activity.ActivityWithSources activityWithSources)) {
            return Stream.empty();
        }
        return activityWithSources.sources().stream()
                .filter(source -> source.linkName().equals(link.name()))
                .map(TibcoModel.Scope.Flow.Activity.Source::condition)
                .flatMap(Optional::stream);
    }

    public Stream<TibcoModel.Scope.Flow.Activity> sortedActivities(TibcoModel.Scope scope) {
        Graph<GraphNode> dependencyGraph = dependencyGraphs.get(scope);
        if (dependencyGraph == null) {
            throw new IllegalArgumentException("No dependency graph found for scope: " + scope);
        }
        return sortedActivitiesInner(dependencyGraph);
    }


    public Stream<TibcoModel.Scope.Flow.Activity> sortedActivities(ExplicitTransitionGroup group) {
        Graph<GraphNode> dependencyGraph = explicitTransitionGroupDependencies.get(group);
        if (dependencyGraph == null) {
            throw new IllegalArgumentException("No dependency graph found for group: " + group);
        }
        return sortedActivitiesInner(dependencyGraph);
    }

    public Stream<TibcoModel.Scope.Flow.Activity> sortedErrorHandlerActivities(ExplicitTransitionGroup group) {
        Graph<GraphNode> dependencyGraph = explicitTransitionGroupDependencies.get(group);
        if (dependencyGraph == null) {
            throw new IllegalArgumentException("No dependency graph found for group: " + group);
        }

        List<GraphNode> errorRoots = group.activities().stream()
                .filter(each -> each instanceof ExplicitTransitionGroup.InlineActivity.ErrorHandlerInlineActivity)
                .map(each ->
                        new GraphNode(ConversionUtils.sanitizes(each.name()), GraphNode.Kind.INLINE_ACTIVITY, each))
                .toList();
        if (errorRoots.isEmpty()) {
            return Stream.empty();
        }
        return sortedActivitiesInner(dependencyGraph, errorRoots);
    }

    private static @NotNull Stream<TibcoModel.Scope.Flow.Activity> sortedActivitiesInner(
            Graph<GraphNode> dependencyGraph) {
        return dependencyGraph.topologicalSort().stream()
                .filter(node -> node.kind == GraphNode.Kind.ACTIVITY ||
                        node.kind == GraphNode.Kind.INLINE_ACTIVITY)
                .map(node -> (TibcoModel.Scope.Flow.Activity) node.data);
    }

    private static @NotNull Stream<TibcoModel.Scope.Flow.Activity> sortedActivitiesInner(
            Graph<GraphNode> dependencyGraph, Collection<GraphNode> roots) {
        return dependencyGraph.topologicalSortWithRoots(roots).stream()
                .filter(node -> node.kind == GraphNode.Kind.ACTIVITY ||
                        node.kind == GraphNode.Kind.INLINE_ACTIVITY)
                .map(node -> (TibcoModel.Scope.Flow.Activity) node.data);
    }

    public Collection<TibcoModel.Scope> scopes(TibcoModel.Process process) {
        return Objects.requireNonNull(scopes.get(process));
    }

    public TibcoModel.PartnerLink.RestPartnerLink.Binding getBinding(String partnerLinkName) {
        return Objects.requireNonNull(partnerLinkBindings.get(partnerLinkName));
    }

    public ControlFlowFunctions getControlFlowFunctions(TibcoModel.Scope scope) {
        return Objects.requireNonNull(controlFlowFunctions.get(scope));
    }

    public ControlFlowFunctions getControlFlowFunctions(ExplicitTransitionGroup group) {
        return explicitTransitionGroupControlFlowFunctions.get(group);
    }

    public record LinkData(Collection<TibcoModel.Scope.Flow.Activity> sourceActivities,
                           Collection<TibcoModel.Scope.Flow.Activity> destinationActivities) {

    }

    public record ActivityData(String functionName, BallerinaModel.TypeDesc argumentType,
                               BallerinaModel.TypeDesc returnType) {

    }

    public record TransitionData(TibcoModel.Scope.Flow.Activity activity,
                                 TibcoModel.Scope.Flow.Activity.Source.Predicate predicate) {

    }

    public record GraphNode(String name, Kind kind, Object data) {

        public enum Kind {
            ACTIVITY, LINK, INLINE_ACTIVITY
        }
    }

    public record ControlFlowFunctions(String scopeFn, String activityRunner, String errorHandler) {
    }
}
