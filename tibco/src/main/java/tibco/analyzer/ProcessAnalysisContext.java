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

import tibco.TibcoModel;
import tibco.TibcoModel.Process.ExplicitTransitionGroup;
import tibco.converter.ConversionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import static common.BallerinaModel.TypeDesc.BuiltinType.XML;

public class ProcessAnalysisContext {

    private final ProjectAnalysisContext projectAnalysisContext;

    private int unhandledActivityCount = 0;
    private int totalActivityCount = 0;
    private TibcoModel.Scope currentScope = null;

    public ProcessAnalysisContext(ProjectAnalysisContext projectAnalysisContext) {
        this.projectAnalysisContext = projectAnalysisContext;
    }

    public int getUnhandledActivityCount() {
        return unhandledActivityCount;
    }

    public int getTotalActivityCount() {
        return totalActivityCount;
    }

    public TibcoModel.Scope getCurrentScope() {
        return currentScope;
    }

    // We are using order preserving sets purely for tests
    private final Collection<TibcoModel.Scope.Flow.Activity> endActivities = new LinkedHashSet<>();
    // places where data added to the link ends up
    private final Map<TibcoModel.Scope.Flow.Link, Collection<TibcoModel.Scope.Flow.Activity>> destinationMap =
            new HashMap<>();

    // activities that add data to the link
    private final Map<TibcoModel.Scope.Flow.Link, Collection<TibcoModel.Scope.Flow.Activity>> sourceMap =
            new HashMap<>();

    private final Set<TibcoModel.Scope.Flow.Activity> activities = new HashSet<>();
    private final Set<TibcoModel.Scope.Flow.Link> links = new HashSet<>();

    public Map<TibcoModel.Scope.Flow.Link, Collection<TibcoModel.Scope.Flow.Activity>> getDestinationMap() {
        return Collections.unmodifiableMap(destinationMap);
    }

    public Map<TibcoModel.Scope.Flow.Link, Collection<TibcoModel.Scope.Flow.Activity>> getSourceMap() {
        return Collections.unmodifiableMap(sourceMap);
    }

    public Set<TibcoModel.Scope.Flow.Activity> getActivities() {
        return Collections.unmodifiableSet(activities);
    }

    private final Map<String, TibcoModel.PartnerLink.RestPartnerLink.Binding> partnerLinkBindings =
            new HashMap<>();

    private final Map<TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.SQL, Integer> queryIndex =
            new IdentityHashMap<>();
    private final Set<String> inputTypeNames = new HashSet<>();
    private String outputTypeName;
    private final Map<String, String> variableTypes = new HashMap<>();

    public Map<TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.SQL, Integer> getQueryIndex() {
        return Collections.unmodifiableMap(queryIndex);
    }

    public Map<String, String> getVariableTypes() {
        return Collections.unmodifiableMap(variableTypes);
    }

    private final Map<TibcoModel.Scope, AnalysisResult.ControlFlowFunctions> controlFlowFunctions = new HashMap<>();
    private final Map<TibcoModel.Scope, Graph<AnalysisResult.GraphNode>> dependencyGraphs = new HashMap<>();
    private final Stack<TibcoModel.Scope> scopeStack = new Stack<>();
    private final Stack<Boolean> inSequence = new Stack<>();

    public Map<TibcoModel.Scope, AnalysisResult.ControlFlowFunctions> getControlFlowFunctions() {
        return Collections.unmodifiableMap(controlFlowFunctions);
    }

    Map<TibcoModel.Scope, Graph<AnalysisResult.GraphNode>> getDependencyGraphs() {
        return Collections.unmodifiableMap(dependencyGraphs);
    }

    public Stack<Boolean> getInSequence() {
        return inSequence;
    }

    private final Map<ExplicitTransitionGroup, Graph<AnalysisResult.GraphNode>> explicitTransitionGroupDependencyGraph
            = new HashMap<>();

    private final Map<ExplicitTransitionGroup, AnalysisResult.ControlFlowFunctions> transitionGroupControlFlowFunctions
            = new HashMap<>();

    Map<ExplicitTransitionGroup, Graph<AnalysisResult.GraphNode>> getExplicitTransitionGroupDependencyGraph() {
        return Collections.unmodifiableMap(explicitTransitionGroupDependencyGraph);
    }

    public Map<ExplicitTransitionGroup, AnalysisResult.ControlFlowFunctions>
    getTransitionGroupControlFlowFunctions() {
        return Collections.unmodifiableMap(transitionGroupControlFlowFunctions);
    }

    public void addEndActivity(TibcoModel.Scope.Flow.Activity activity) {
        endActivities.add(activity);
    }

    public void addStartActivity(TibcoModel.Scope.Flow.Activity activity) {
        dependencyGraphs.get(currentScope).addRoot(activityNode(activity));
    }

    public void setVariableType(String name, String type) {
        variableTypes.put(name, type);
    }

    public void allocateLinkIfNeeded(TibcoModel.Scope.Flow.Link link) {
        links.add(link);
    }

    public void allocateActivityNameIfNeeded(TibcoModel.Scope.Flow.Activity activity) {
        Map<TibcoModel.Scope.Flow.Activity, String> activityFunctionNames =
                projectAnalysisContext.activityFunctionNames();
        if (activityFunctionNames.containsKey(activity)) {
            return;
        }
        totalActivityCount++;
        String prefix = switch (activity) {
            case TibcoModel.Scope.Flow.Activity.ActivityExtension ignored -> "activityExtension";
            case TibcoModel.Scope.Flow.Activity.Empty ignored -> "empty";
            case TibcoModel.Scope.Flow.Activity.ExtActivity ignored -> "extActivity";
            case TibcoModel.Scope.Flow.Activity.Invoke ignored -> "invoke";
            case TibcoModel.Scope.Flow.Activity.Pick ignored -> "pick";
            case TibcoModel.Scope.Flow.Activity.ReceiveEvent ignored -> "receiveEvent";
            case TibcoModel.Scope.Flow.Activity.Reply ignored -> "reply";
            case TibcoModel.Scope.Flow.Activity.CatchAll ignored -> "catchAll";
            case TibcoModel.Scope.Flow.Activity.Throw ignored -> "throw";
            case TibcoModel.Scope.Flow.Activity.NestedScope ignored -> "nestedScope";
            case TibcoModel.Scope.Flow.Activity.Assign ignored -> "assign";
            case TibcoModel.Scope.Flow.Activity.Foreach ignored -> "forEach";
            case TibcoModel.Scope.Flow.Activity.UnhandledActivity ignored -> {
                unhandledActivityCount++;
                yield "unhandled";
            }
            case ExplicitTransitionGroup.InlineActivity.UnhandledInlineActivity unhandledInlineActivity -> {
                unhandledActivityCount++;
                yield unhandledInlineActivity.name();
            }
            case ExplicitTransitionGroup.InlineActivity inlineActivity -> inlineActivity.name();
        };
        String activityName = ConversionUtils.getSanitizedUniqueName(prefix,
                activityFunctionNames.values());
        activityFunctionNames.put(activity, activityName);
        activities.add(activity);
    }


    public void addDestination(TibcoModel.Scope.Flow.Activity source, TibcoModel.Scope.Flow.Activity destination) {
        dependencyGraphs.get(currentScope).addEdge(activityNode(source), activityNode(destination));
    }

    public void addDestination(TibcoModel.Scope.Flow.Link source, TibcoModel.Scope.Flow.Activity destination) {
        dependencyGraphs.get(currentScope).addEdge(linkNode(source), activityNode(destination));
        destinationMap.computeIfAbsent(source, (ignored) -> new ArrayList<>()).add(destination);
    }

    public void addSource(TibcoModel.Scope.Flow.Activity source, TibcoModel.Scope.Flow.Link destination) {
        dependencyGraphs.get(currentScope).addEdge(activityNode(source), linkNode(destination));
        sourceMap.computeIfAbsent(destination, (ignored) -> new ArrayList<>()).add(source);
    }

    private String activityNodeName(TibcoModel.Scope.Flow.Activity activity) {
        allocateActivityNameIfNeeded(activity);
        return projectAnalysisContext.activityFunctionNames().get(activity);
    }

    public Map<TibcoModel.Scope.Flow.Activity, AnalysisResult.ActivityData> activityData() {
        Map<TibcoModel.Scope.Flow.Activity, AnalysisResult.ActivityData> data = new HashMap<>();
        for (var activity : activities) {
            String functionName = projectAnalysisContext.activityFunctionNames().get(activity);
            data.put(activity, new AnalysisResult.ActivityData(functionName, XML, XML));
        }
        return Collections.unmodifiableMap(data);
    }

    public void allocateIndexForQuery(TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.SQL sql) {
        queryIndex.put(sql, queryIndex.size());
    }

    public void appendInputTypeName(String inputTypeName) {
        this.inputTypeNames.add(inputTypeName);
    }

    public Collection<String> getInputTypeName() {
        return inputTypeNames;
    }

    public void setOutputTypeName(String outputTypeName) {
        this.outputTypeName = outputTypeName;
    }

    public void pushScope(TibcoModel.Scope scope) {
        if (!dependencyGraphs.containsKey(scope)) {
            dependencyGraphs.put(scope, new Graph<>());
        }
        scopeStack.push(scope);
        this.currentScope = scope;
    }

    public void popScope() {
        scopeStack.pop();
        if (scopeStack.isEmpty()) {
            this.currentScope = null;
        } else {
            this.currentScope = scopeStack.peek();
        }
    }

    public void allocateControlFlowFunctionsIfNeeded(TibcoModel.Scope scope) {
        if (controlFlowFunctions.containsKey(scope)) {
            return;
        }
        String name = scope.name();
        if (name.isEmpty()) {
            name = "anonScope";
        }
        Set<String> controlFlowFunctionNames = projectAnalysisContext.controlFlowFunctionNames();
        name = ConversionUtils.getSanitizedUniqueName(name, controlFlowFunctionNames);
        controlFlowFunctionNames.add(name);
        controlFlowFunctions.put(scope, new AnalysisResult.ControlFlowFunctions(name + "ScopeFn",
                name + "ActivityRunner", name + "FaultHandler"));
    }


    public void allocateControlFlowFunctionsIfNeeded(ExplicitTransitionGroup transitionGroup) {
        if (transitionGroupControlFlowFunctions.containsKey(transitionGroup)) {
            return;
        }
        String name = "scope" + transitionGroupControlFlowFunctions.size();
        Set<String> controlFlowFunctionNames = projectAnalysisContext.controlFlowFunctionNames();
        name = ConversionUtils.getSanitizedUniqueName(name, controlFlowFunctionNames);
        controlFlowFunctionNames.add(name);
        transitionGroupControlFlowFunctions.put(transitionGroup,
                new AnalysisResult.ControlFlowFunctions(
                        name + "ScopeFn",
                        name + "ActivityRunner",
                        name + "FaultHandler"));
    }

    public String getOutputTypeName() {
        if (outputTypeName == null) {
            return "UNKNOWN";
        }
        return outputTypeName;
    }

    public void setPartnerLinkBinding(TibcoModel.PartnerLink.NonEmptyPartnerLink link,
                                      TibcoModel.PartnerLink.RestPartnerLink.Binding binding) {
        partnerLinkBindings.put(link.name(), binding);
    }

    public Map<String, TibcoModel.PartnerLink.RestPartnerLink.Binding> getPartnerLinkBindings() {
        return Collections.unmodifiableMap(partnerLinkBindings);
    }

    public AnalysisResult.GraphNode activityNode(ExplicitTransitionGroup.InlineActivity inlineActivity) {
        String name = ConversionUtils.sanitizes(inlineActivity.name());
        return new AnalysisResult.GraphNode(name, AnalysisResult.GraphNode.Kind.INLINE_ACTIVITY, inlineActivity);
    }

    public AnalysisResult.GraphNode activityNode(TibcoModel.Scope.Flow.Activity activity) {
        String name = activityNodeName(activity);
        return new AnalysisResult.GraphNode(name, AnalysisResult.GraphNode.Kind.ACTIVITY, activity);
    }

    public AnalysisResult.GraphNode linkNode(TibcoModel.Scope.Flow.Link link) {
        return new AnalysisResult.GraphNode(link.name(), AnalysisResult.GraphNode.Kind.LINK, link);
    }

    Graph<AnalysisResult.GraphNode> getExplicitTransitionGroupGraph(ExplicitTransitionGroup group) {
        return explicitTransitionGroupDependencyGraph.computeIfAbsent(group, (ignored) -> new Graph<>());
    }
}
