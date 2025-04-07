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

package converter.tibco.analyzer;

import ballerina.BallerinaModel;
import tibco.TibcoModel;

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
    private final Map<TibcoModel.Process, Collection<TibcoModel.Scope.Flow.Activity>> startActivities;
    private final Map<TibcoModel.Process, Collection<TibcoModel.Scope.Flow.Activity>> faultHandlerStartActivities;
    private final Map<TibcoModel.Scope.Flow.Activity, ActivityData> activityData;
    private final Map<String, TibcoModel.PartnerLink.Binding> partnerLinkBindings;
    private final Map<TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.SQL, Integer> queryIndex;
    private final Map<TibcoModel.Process, String> inputTypeName;
    private final Map<TibcoModel.Process, String> outputTypeName;
    private final Graph<GraphNode> dependencyGraph;

    AnalysisResult(Map<TibcoModel.Scope.Flow.Link, Collection<TibcoModel.Scope.Flow.Activity>> destinationMap,
                   Map<TibcoModel.Scope.Flow.Link, Collection<TibcoModel.Scope.Flow.Activity>> sourceMap,
                   Map<TibcoModel.Process, Collection<TibcoModel.Scope.Flow.Activity>> startActivities,
                   Map<TibcoModel.Process, Collection<TibcoModel.Scope.Flow.Activity>> faultHandlerStartActivities,
                   Map<TibcoModel.Scope.Flow.Activity, ActivityData> activityData,
                   Map<String, TibcoModel.PartnerLink.Binding> partnerLinkBindings,
                   Map<TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.SQL, Integer> queryIndex,
                   Map<TibcoModel.Process, String> inputTypeName, Map<TibcoModel.Process, String> outputTypeName,
                   Graph<GraphNode> dependencyGraph) {
        this.destinationMap = destinationMap;
        this.sourceMap = sourceMap;
        this.startActivities = startActivities;
        this.faultHandlerStartActivities = faultHandlerStartActivities;
        this.activityData = activityData;
        this.partnerLinkBindings = partnerLinkBindings;
        this.queryIndex = queryIndex;
        this.inputTypeName = inputTypeName;
        this.outputTypeName = outputTypeName;
        this.dependencyGraph = dependencyGraph;
    }

    public String inputTypeName(TibcoModel.Process process) {
        return inputTypeName.get(process);
    }

    public String outputTypeName(TibcoModel.Process process) {
        return outputTypeName.get(process);
    }

    public Collection<TibcoModel.Scope.Flow.Activity> startActivities(TibcoModel.Process process) {
        return startActivities.get(process);
    }

    public Collection<TibcoModel.Scope.Flow.Activity> faultHandlerStartActivities(TibcoModel.Process process) {
        return faultHandlerStartActivities.get(process);
    }

    public Collection<TibcoModel.Scope.Flow.Activity> destinations(TibcoModel.Scope.Flow.Link link) {
        var destinations = destinationMap.get(link);
        if (destinations == null) {
            return List.of();
        }
        return destinations;
    }

    public Collection<TibcoModel.Scope.Flow.Activity> sources(TibcoModel.Scope.Flow.Link link) {
        var sources = sourceMap.get(link);
        // this can happen for invalid activities
        if (sources == null) {
            return List.of();
        }
        return sources;
    }

    public LinkData from(TibcoModel.Scope.Flow.Link link) {
        return new LinkData(sources(link), destinations(link));
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

    public Stream<TibcoModel.Scope.Flow.Activity> sortedActivities(TibcoModel.Process process) {
        return sortedActivitiesFromRoots(startActivityNodes(process));
    }

    public Stream<TibcoModel.Scope.Flow.Activity> sortedFaultHandlerActivities(TibcoModel.Process process) {
        return sortedActivitiesFromRoots(faultHandlerStartActivityNodes(process));
    }

    public Stream<TibcoModel.Scope.Flow.Activity> sortedActivitiesFromRoots(Collection<GraphNode> roots) {
        return dependencyGraph.topologicalSortWithRoots(roots).stream()
                .filter(node -> node.kid == GraphNode.Kind.ACTIVITY)
                .map(node -> (TibcoModel.Scope.Flow.Activity) node.data);
    }

    private Collection<GraphNode> faultHandlerStartActivityNodes(TibcoModel.Process process) {
        return graphNodes(faultHandlerStartActivities(process).stream());
    }

    private Collection<GraphNode> startActivityNodes(TibcoModel.Process process) {
        return graphNodes(startActivities(process).stream());
    }

    private Collection<GraphNode> graphNodes(Stream<TibcoModel.Scope.Flow.Activity> activities) {
        record Data(TibcoModel.Scope.Flow.Activity activity, ActivityData activityData) {

        }
        return activities
                .map(activity -> new Data(activity, from(activity)))
                .map(data -> new GraphNode(data.activityData.functionName, GraphNode.Kind.ACTIVITY, data.activity))
                .toList();
    }

    public TibcoModel.PartnerLink.Binding getBinding(String partnerLinkName) {
        return Objects.requireNonNull(partnerLinkBindings.get(partnerLinkName));
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

    public record GraphNode(String name, Kind kid, Object data) {

        public enum Kind {
            ACTIVITY, LINK
        }
    }
}
