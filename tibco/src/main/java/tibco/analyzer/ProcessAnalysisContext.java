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

import common.LoggingContext;
import common.LoggingUtils;
import tibco.converter.ConversionUtils;
import tibco.model.PartnerLink;
import tibco.model.Process5.ExplicitTransitionGroup;
import tibco.model.Scope;
import tibco.model.XSD;

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

public class ProcessAnalysisContext implements LoggingContext {

    private final ProjectAnalysisContext projectAnalysisContext;

    private int unhandledActivityCount = 0;
    private int totalActivityCount = 0;
    private Scope currentScope = null;
    private Map<String, AnalysisResult.GraphNode> activityNodes;

    public ProcessAnalysisContext(ProjectAnalysisContext projectAnalysisContext) {
        this.projectAnalysisContext = projectAnalysisContext;
    }

    public int getUnhandledActivityCount() {
        return unhandledActivityCount;
    }

    public int getTotalActivityCount() {
        return totalActivityCount;
    }

    public Scope getCurrentScope() {
        return currentScope;
    }

    // We are using order preserving sets purely for tests
    private final Collection<Scope.Flow.Activity> endActivities = new LinkedHashSet<>();
    // places where data added to the link ends up
    private final Map<Scope.Flow.Link, Collection<Scope.Flow.Activity>> destinationMap =
            new HashMap<>();

    // activities that add data to the link
    private final Map<Scope.Flow.Link, Collection<Scope.Flow.Activity>> sourceMap =
            new HashMap<>();

    private final Set<Scope.Flow.Activity> activities = new HashSet<>();
    private final Set<Scope.Flow.Link> links = new HashSet<>();

    public Map<Scope.Flow.Link, Collection<Scope.Flow.Activity>> getDestinationMap() {
        return Collections.unmodifiableMap(destinationMap);
    }

    public Map<Scope.Flow.Link, Collection<Scope.Flow.Activity>> getSourceMap() {
        return Collections.unmodifiableMap(sourceMap);
    }

    public Set<Scope.Flow.Activity> getActivities() {
        return Collections.unmodifiableSet(activities);
    }

    private final Map<String, PartnerLink.RestPartnerLink.Binding> partnerLinkBindings =
            new HashMap<>();

    private final Map<Scope.Flow.Activity.ActivityExtension.Config.SQL, Integer> queryIndex =
            new IdentityHashMap<>();
    private final Set<String> inputTypeNames = new HashSet<>();
    private String outputTypeName;
    private final Map<String, String> variableTypes = new HashMap<>();

    public Map<Scope.Flow.Activity.ActivityExtension.Config.SQL, Integer> getQueryIndex() {
        return Collections.unmodifiableMap(queryIndex);
    }

    public Map<String, String> getVariableTypes() {
        return Collections.unmodifiableMap(variableTypes);
    }

    private final Map<Scope, AnalysisResult.ControlFlowFunctions> controlFlowFunctions = new HashMap<>();
    private final Map<Scope, Graph<AnalysisResult.GraphNode>> dependencyGraphs = new HashMap<>();
    private final Stack<Scope> scopeStack = new Stack<>();
    private final Stack<Boolean> inSequence = new Stack<>();

    public Map<Scope, AnalysisResult.ControlFlowFunctions> getControlFlowFunctions() {
        return Collections.unmodifiableMap(controlFlowFunctions);
    }

    Map<Scope, Graph<AnalysisResult.GraphNode>> getDependencyGraphs() {
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

    public void addEndActivity(Scope.Flow.Activity activity) {
        endActivities.add(activity);
    }

    public void addStartActivity(Scope.Flow.Activity activity) {
        dependencyGraphs.get(currentScope).addRoot(activityNode(activity));
    }

    public void setVariableType(String name, String type) {
        variableTypes.put(name, type);
    }

    public void allocateLinkIfNeeded(Scope.Flow.Link link) {
        links.add(link);
    }

    public void allocateActivityNameIfNeeded(Scope.Flow.Activity activity) {
        Map<Scope.Flow.Activity, String> activityFunctionNames =
                projectAnalysisContext.activityFunctionNames();
        if (activityFunctionNames.containsKey(activity)) {
            return;
        }
        totalActivityCount++;
        String prefix = switch (activity) {
            case Scope.Flow.Activity.ActivityExtension ignored -> "activityExtension";
            case Scope.Flow.Activity.Empty ignored -> "empty";
            case Scope.Flow.Activity.ExtActivity ignored -> "extActivity";
            case Scope.Flow.Activity.Invoke ignored -> "invoke";
            case Scope.Flow.Activity.Pick ignored -> "pick";
            case Scope.Flow.Activity.ReceiveEvent ignored -> "receiveEvent";
            case Scope.Flow.Activity.Reply ignored -> "reply";
            case Scope.Flow.Activity.CatchAll ignored -> "catchAll";
            case Scope.Flow.Activity.Throw ignored -> "throw";
            case Scope.Flow.Activity.NestedScope ignored -> "nestedScope";
            case Scope.Flow.Activity.Assign ignored -> "assign";
            case Scope.Flow.Activity.Foreach ignored -> "forEach";
            case Scope.Flow.Activity.UnhandledActivity ignored -> {
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

    public void addDestination(Scope.Flow.Activity source, Scope.Flow.Activity destination) {
        dependencyGraphs.get(currentScope).addEdge(activityNode(source), activityNode(destination));
    }

    public void addDestination(Scope.Flow.Link source, Scope.Flow.Activity destination) {
        dependencyGraphs.get(currentScope).addEdge(linkNode(source), activityNode(destination));
        destinationMap.computeIfAbsent(source, (ignored) -> new ArrayList<>()).add(destination);
    }

    public void addSource(Scope.Flow.Activity source, Scope.Flow.Link destination) {
        dependencyGraphs.get(currentScope).addEdge(activityNode(source), linkNode(destination));
        sourceMap.computeIfAbsent(destination, (ignored) -> new ArrayList<>()).add(source);
    }

    private String activityNodeName(Scope.Flow.Activity activity) {
        allocateActivityNameIfNeeded(activity);
        return projectAnalysisContext.activityFunctionNames().get(activity);
    }

    public Map<Scope.Flow.Activity, AnalysisResult.ActivityData> activityData() {
        Map<Scope.Flow.Activity, AnalysisResult.ActivityData> data = new HashMap<>();
        for (var activity : activities) {
            String functionName = projectAnalysisContext.activityFunctionNames().get(activity);
            data.put(activity, new AnalysisResult.ActivityData(functionName, XML, XML));
        }
        return Collections.unmodifiableMap(data);
    }

    public void allocateIndexForQuery(Scope.Flow.Activity.ActivityExtension.Config.SQL sql) {
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

    public void pushScope(Scope scope) {
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

    public void allocateControlFlowFunctionsIfNeeded(Scope scope) {
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

    public void setPartnerLinkBinding(PartnerLink.NonEmptyPartnerLink link,
                                      PartnerLink.RestPartnerLink.Binding binding) {
        partnerLinkBindings.put(link.name(), binding);
    }

    public Map<String, PartnerLink.RestPartnerLink.Binding> getPartnerLinkBindings() {
        return Collections.unmodifiableMap(partnerLinkBindings);
    }

    public AnalysisResult.GraphNode activityNode(ExplicitTransitionGroup.InlineActivity inlineActivity) {
        String name = ConversionUtils.sanitizes(inlineActivity.name());
        return new AnalysisResult.GraphNode(name, AnalysisResult.GraphNode.Kind.INLINE_ACTIVITY, inlineActivity);
    }

    public AnalysisResult.GraphNode activityNode(Scope.Flow.Activity activity) {
        String name = activityNodeName(activity);
        return new AnalysisResult.GraphNode(name, AnalysisResult.GraphNode.Kind.ACTIVITY, activity);
    }

    public AnalysisResult.GraphNode linkNode(Scope.Flow.Link link) {
        return new AnalysisResult.GraphNode(link.name(), AnalysisResult.GraphNode.Kind.LINK, link);
    }

    Graph<AnalysisResult.GraphNode> getExplicitTransitionGroupGraph(ExplicitTransitionGroup group) {
        return explicitTransitionGroupDependencyGraph.computeIfAbsent(group, (ignored) -> new Graph<>());
    }

    public void setActivityNodes(Map<String, AnalysisResult.GraphNode> activityNodes) {
        this.activityNodes = activityNodes;
    }

    public Map<String, AnalysisResult.GraphNode> getActivityNodes() {
        return Collections.unmodifiableMap(activityNodes);
    }

    void addXsdType(String name, XSD.XSDType type) {
        projectAnalysisContext.addXsdType(name, type);
    }

    public Map<String, XSD.XSDType> xsdTypes() {
        return projectAnalysisContext.xsdTypes();
    }

    @Override
    public void log(LoggingUtils.Level level, String message) {
        projectAnalysisContext.log(level, message);
    }

    @Override
    public void logState(String message) {
        projectAnalysisContext.logState(message);
    }
}
