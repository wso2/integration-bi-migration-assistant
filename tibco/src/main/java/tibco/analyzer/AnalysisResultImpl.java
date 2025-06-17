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

package tibco.analyzer;

import org.jetbrains.annotations.NotNull;
import tibco.converter.ConversionUtils;
import tibco.model.PartnerLink;
import tibco.model.Process;
import tibco.model.Process5.ExplicitTransitionGroup;
import tibco.model.Scope;
import tibco.model.XSD;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

final class AnalysisResultImpl implements AnalysisResult {

    private final Map<Scope.Flow.Link, Collection<Scope.Flow.Activity>> destinationMap;
    private final Map<Scope.Flow.Link, Collection<Scope.Flow.Activity>> sourceMap;
    private final Map<Scope.Flow.Activity, ActivityData> activityData;
    private final Map<String, PartnerLink.RestPartnerLink.Binding> partnerLinkBindings;
    private final Map<Scope.Flow.Activity.ActivityExtension.Config.SQL, Integer> queryIndex;
    private final Map<Process, Collection<String>> inputTypeNames;
    private final Map<Process, String> outputTypeName;
    private final Map<Process, Map<String, String>> variableTypes;
    private final Map<Scope, Graph<GraphNode>> dependencyGraphs;
    private final Map<Scope, ControlFlowFunctions> controlFlowFunctions;
    private final Map<Process, Collection<Scope>> scopes;
    private final Map<String, Scope.Flow.Activity> activityByName;
    private final Map<ExplicitTransitionGroup, Graph<GraphNode>> explicitTransitionGroupDependencies;
    private final Map<ExplicitTransitionGroup, ControlFlowFunctions> explicitTransitionGroupControlFlowFunctions;
    private final Map<String, XSD.XSDType> xsdTypes;
    TibcoAnalysisReport report;

    AnalysisResultImpl(Map<Scope.Flow.Link, Collection<Scope.Flow.Activity>> destinationMap,
                       Map<Scope.Flow.Link, Collection<Scope.Flow.Activity>> sourceMap,
                       Map<Scope.Flow.Activity, ActivityData> activityData,
                       Map<String, PartnerLink.RestPartnerLink.Binding> partnerLinkBindings,
                       Map<Scope.Flow.Activity.ActivityExtension.Config.SQL, Integer> queryIndex,
                       Map<Process, Collection<String>> inputTypeNames,
                       Map<Process, String> outputTypeName,
                       Map<Process, Map<String, String>> variableTypes,
                       Map<Scope, Graph<GraphNode>> dependencyGraphs,
                       Map<Scope, ControlFlowFunctions> controlFlowFunctions,
                       Map<Process, Collection<Scope>> scopes,
                       Map<String, Scope.Flow.Activity> activityByName,
                       Map<ExplicitTransitionGroup, Graph<GraphNode>> explicitTransitionGroupDependencies,
                       Map<ExplicitTransitionGroup, ControlFlowFunctions> explicitTransitionGroupControlFlowFunctions,
                       Map<String, XSD.XSDType> xsdTypes, TibcoAnalysisReport report) {
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
        this.report = report;
        this.xsdTypes = xsdTypes;
    }

    @Override
    public Collection<String> inputTypeName(Process process) {
        return inputTypeNames.get(process);
    }

    @Override
    public String outputTypeName(Process process) {
        return outputTypeName.get(process);
    }

    @Override
    public Collection<Scope.Flow.Activity> sources(Scope.Flow.Link link) {
        var sources = sourceMap.get(link);
        // this can happen for invalid activities
        if (sources == null) {
            return List.of();
        }
        return sources;
    }

    @Override
    public Optional<Scope.Flow.Activity> findActivity(String name) {
        return Optional.ofNullable(activityByName.get(name));
    }

    @Override
    public String variableType(Process process, String variableName) {
        var variableType = variableTypes.get(process);
        if (variableType == null) {
            throw new IllegalArgumentException("No variable type found for process: " + process);
        }
        return Objects.requireNonNull(variableType.get(variableName),
                () -> "Variable type not found for: " + variableName);
    }

    @Override
    public ActivityData from(Scope.Flow.Activity activity) {
        var data = activityData.get(activity);
        if (data == null) {
            throw new IllegalArgumentException("No data found for activity: " + activity);
        }
        return data;
    }

    @Override
    public Collection<Scope.Flow.Activity> activities() {
        return activityData.keySet();
    }

    @Override
    public Collection<Scope.Flow.Link> links() {
        return destinationMap.keySet();
    }

    @Override
    public Collection<Scope.Flow.Link> sources(Scope.Flow.Activity activity) {
        if (activity instanceof Scope.Flow.Activity.ActivityWithTargets targets) {
            return targets.targets().stream().map(Scope.Flow.Activity.Target::linkName)
                    .map(Scope.Flow.Link::new)
                    .toList();
        }
        return List.of();
    }

    @Override
    public Stream<TransitionData> transitionConditions(
            Scope.Flow.Activity activity) {
        if (!(activity instanceof Scope.Flow.Activity.ActivityWithTargets activityWithTargets)) {
            return Stream.empty();
        }
        Stream.Builder<Stream<TransitionData>> predicateStreams = Stream.builder();
        for (Scope.Flow.Activity.Target target : activityWithTargets.targets()) {
            Scope.Flow.Link link = new Scope.Flow.Link(target.linkName());
            Collection<Scope.Flow.Activity> sources = sourceMap.get(link);
            for (Scope.Flow.Activity source : sources) {
                predicateStreams.add(transitionCondition(source, link).map(prec -> new TransitionData(source, prec)));
            }
        }
        return predicateStreams.build().flatMap(Function.identity());
    }

    @Override
    public Stream<Scope.Flow.Activity.Source.Predicate> transitionCondition(
            Scope.Flow.Activity activity, Scope.Flow.Link link) {
        if (!(activity instanceof Scope.Flow.Activity.ActivityWithSources activityWithSources)) {
            return Stream.empty();
        }
        return activityWithSources.sources().stream()
                .filter(source -> source.linkName().equals(link.name()))
                .map(Scope.Flow.Activity.Source::condition)
                .flatMap(Optional::stream);
    }

    @Override
    public Stream<Scope.Flow.Activity> sortedActivities(Scope scope) {
        Graph<GraphNode> dependencyGraph = dependencyGraphs.get(scope);
        if (dependencyGraph == null) {
            throw new IllegalArgumentException("No dependency graph found for scope: " + scope);
        }
        return sortedActivitiesInner(dependencyGraph);
    }

    @Override
    public Stream<Scope.Flow.Activity> sortedActivities(ExplicitTransitionGroup group) {
        Graph<GraphNode> dependencyGraph = explicitTransitionGroupDependencies.get(group);
        if (dependencyGraph == null) {
            throw new IllegalArgumentException("No dependency graph found for group: " + group);
        }
        return sortedActivitiesInner(dependencyGraph);
    }

    @Override
    public Stream<Scope.Flow.Activity> sortedErrorHandlerActivities(ExplicitTransitionGroup group) {
        Graph<GraphNode> dependencyGraph = explicitTransitionGroupDependencies.get(group);
        if (dependencyGraph == null) {
            throw new IllegalArgumentException("No dependency graph found for group: " + group);
        }

        List<GraphNode> errorRoots = group.activities().stream()
                .filter(each -> each instanceof ExplicitTransitionGroup.InlineActivity.ErrorHandlerInlineActivity)
                .map(each -> new GraphNode(ConversionUtils.sanitizes(each.name()), GraphNode.Kind.INLINE_ACTIVITY,
                        each))
                .toList();
        if (errorRoots.isEmpty()) {
            return Stream.empty();
        }
        return sortedActivitiesInner(dependencyGraph, errorRoots);
    }

    private static @NotNull Stream<Scope.Flow.Activity> sortedActivitiesInner(
            Graph<GraphNode> dependencyGraph) {
        return dependencyGraph.topologicalSort().stream()
                .filter(node -> node.kind() == GraphNode.Kind.ACTIVITY ||
                        node.kind() == GraphNode.Kind.INLINE_ACTIVITY)
                .map(node -> (Scope.Flow.Activity) node.data());
    }

    private static @NotNull Stream<Scope.Flow.Activity> sortedActivitiesInner(
            Graph<GraphNode> dependencyGraph, Collection<GraphNode> roots) {
        return dependencyGraph.topologicalSortWithRoots(roots).stream()
                .filter(node -> node.kind() == GraphNode.Kind.ACTIVITY ||
                        node.kind() == GraphNode.Kind.INLINE_ACTIVITY)
                .map(node -> (Scope.Flow.Activity) node.data());
    }

    @Override
    public Collection<Scope> scopes(Process process) {
        return Objects.requireNonNull(scopes.get(process));
    }

    @Override
    public PartnerLink.RestPartnerLink.Binding getBinding(String partnerLinkName) {
        return Objects.requireNonNull(partnerLinkBindings.get(partnerLinkName));
    }

    @Override
    public ControlFlowFunctions getControlFlowFunctions(Scope scope) {
        return Objects.requireNonNull(controlFlowFunctions.get(scope));
    }

    @Override
    public ControlFlowFunctions getControlFlowFunctions(ExplicitTransitionGroup group) {
        return explicitTransitionGroupControlFlowFunctions.get(group);
    }

    @Override
    public AnalysisResult combine(AnalysisResult otherResult) {
        if (!(otherResult instanceof AnalysisResultImpl other)) {
            throw new IllegalArgumentException("Cannot combine with non-AnalysisResultImpl: " + otherResult);
        }
        return new AnalysisResultImpl(
                combineMap(this.destinationMap, other.destinationMap),
                combineMap(this.sourceMap, other.sourceMap),
                combineMap(this.activityData, other.activityData),
                combineMap(this.partnerLinkBindings, other.partnerLinkBindings),
                combineMap(this.queryIndex, other.queryIndex),
                combineMap(this.inputTypeNames, other.inputTypeNames),
                combineMap(this.outputTypeName, other.outputTypeName),
                combineMap(this.variableTypes, other.variableTypes),
                combineMap(this.dependencyGraphs, other.dependencyGraphs),
                combineMap(this.controlFlowFunctions, other.controlFlowFunctions),
                combineMap(this.scopes, other.scopes),
                combineMap(this.activityByName, other.activityByName),
                combineMap(this.explicitTransitionGroupDependencies, other.explicitTransitionGroupDependencies),
                combineMap(this.explicitTransitionGroupControlFlowFunctions,
                        other.explicitTransitionGroupControlFlowFunctions),
                combineMap(this.xsdTypes, other.xsdTypes), combineReports(other)
        );
    }

    private TibcoAnalysisReport combineReports(AnalysisResultImpl other) {
        return Stream.of(getReport(), other.getReport())
                .flatMap(Optional::stream)
                .reduce(TibcoAnalysisReport::combine)
                .orElseGet(TibcoAnalysisReport::empty);
    }

    @Override
    public Optional<TibcoAnalysisReport> getReport() {
        return Optional.ofNullable(report);
    }

    @Override
    public void setReport(TibcoAnalysisReport report) {
        this.report = report;
    }

    @Override
    public XSD.@NotNull XSDType getType(String name) {
        XSD.XSDType type = xsdTypes.get(name);
        if (type == null) {
            throw new IllegalArgumentException("No XSD type found for name: " + name);
        }
        return type;
    }

    static <K, V> Map<K, V> combineMap(Map<K, V> map1, Map<K, V> map2) {
        Map<K, V> map = new HashMap<>(map1.size() + map2.size());
        map.putAll(map1);
        map.putAll(map2);
        return Collections.unmodifiableMap(map);
    }

}
