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
import tibco.converter.ConversionUtils;
import tibco.converter.ProjectConverter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static common.BallerinaModel.TypeDesc.BuiltinType.XML;

public class ModelAnalyser {

    private static final Logger logger = ProjectConverter.LOGGER;

    private ModelAnalyser() {

    }

    public static AnalysisResult analyseProcess(TibcoModel.Process process) {
        ProcessAnalysisContext cx = new ProcessAnalysisContext();
        analyseProcess(cx, process);

        Map<TibcoModel.Scope.Flow.Activity, AnalysisResult.ActivityData> activityData = cx.activityData();
        Map<String, TibcoModel.PartnerLink.RestPartnerLink.Binding> partnerLinkBindings = Collections
                .unmodifiableMap(cx.partnerLinkBindings);

        logger.info(String.format("Process Statistics - Name: %s, Total Activities: %d, Unhandled Activities: %d",
                process.name(), cx.totalActivityCount, cx.unhandledActivityCount));
        Map<TibcoModel.Process, Collection<String>> inputTypeNames = Map.of(process, cx.getInputTypeName());
        Map<TibcoModel.Process, String> outputTypeName = Map.of(process, cx.getOutputTypeName());
        Map<TibcoModel.Process, Map<String, String>> variableTypes = Map.of(process, cx.variableTypes);
        Map<TibcoModel.Process, Collection<TibcoModel.Scope>> scopes = Map.of(process, cx.dependencyGraphs.keySet());
        record ActivityNames(String name, TibcoModel.Scope.Flow.Activity activity) {
        }
        Map<String, TibcoModel.Scope.Flow.Activity> activityByName = cx.activities.stream()
                .filter(each -> each instanceof TibcoModel.Scope.Flow.Activity.ActivityWithName)
                .map(each -> (TibcoModel.Scope.Flow.Activity.ActivityWithName) each)
                .filter(each -> each.getName().isPresent())
                .map(each -> new ActivityNames(each.getName().get(), each))
                .collect(Collectors.toMap(ActivityNames::name, ActivityNames::activity));
        return new AnalysisResult(cx.destinationMap, cx.sourceMap,
                activityData, partnerLinkBindings, cx.queryIndex, inputTypeNames,
                outputTypeName, variableTypes, cx.dependencyGraphs, cx.controlFlowFunctions, scopes, activityByName);
    }

    private static void analyseProcess(ProcessAnalysisContext cx, TibcoModel.Process process) {
        analyzeVariables(cx, process.variables());
        analysePartnerLinks(cx, process.partnerLinks());
        analyseTypes(cx, process.types());
        analyseScope(cx, process.scope());
        process.processInterface().ifPresent(i -> analyzeProcessInterface(cx, i));
    }

    private static void analyzeVariables(ProcessAnalysisContext cx, Collection<TibcoModel.Variable> variables) {
        variables.forEach(variable -> {
            String typeName = ConversionUtils.stripNamespace(variable.type());
            cx.setVariableType(variable.name(), typeName);
        });
    }

    private static void analyzeProcessInterface(ProcessAnalysisContext cx,
                                                TibcoModel.ProcessInterface processInterface) {
        String inputType = sanitizeTypeName(processInterface.input());
        if (!inputType.isEmpty()) {
            cx.appendInputTypeName(inputType);
        }
        String outputType = sanitizeTypeName(processInterface.output());
        if (!outputType.isEmpty()) {
            cx.setOutputTypeName(outputType);
        }
    }

    private static String sanitizeTypeName(String typeName) {
        if (typeName == null) {
            return "";
        }
        if (typeName.contains("{") && typeName.contains("}")) {
            typeName = typeName.substring(typeName.indexOf("}") + 1);
        }
        return typeName;
    }

    private static void analyseTypes(ProcessAnalysisContext cx, Collection<TibcoModel.Type> types) {
        types.forEach(type -> {
            if (type instanceof TibcoModel.Type.WSDLDefinition wsdlDefinition) {
                analyseWSDLDefinition(cx, wsdlDefinition);
            }
        });
    }

    private static void analyseWSDLDefinition(ProcessAnalysisContext cx,
                                              TibcoModel.Type.WSDLDefinition wsdlDefinition) {
        var messageTypes = getMessageTypeDefinitions(wsdlDefinition);
        wsdlDefinition.portType().forEach(portType -> {
            var operation = portType.operation();
            String inputType = messageTypes.get(operation.input().message().value());
            if (inputType == null) {
                inputType = "null";
            }
            cx.appendInputTypeName(inputType);
            cx.setOutputTypeName(messageTypes.get(operation.output().message().value()));
        });
    }

    private static Map<String, String> getMessageTypeDefinitions(TibcoModel.Type.WSDLDefinition wsdlDefinition) {
        Map<String, String> result = new HashMap<>();
        for (TibcoModel.Type.WSDLDefinition.Message message : wsdlDefinition.messages()) {
            Optional<String> referredTypeName = getMessageTypeName(message);
            if (referredTypeName.isEmpty()) {
                continue;
            }
            result.put(message.name(), referredTypeName.get());
        }
        return result;
    }

    private static Optional<String> getMessageTypeName(TibcoModel.Type.WSDLDefinition.Message message) {
        Optional<TibcoModel.Type.WSDLDefinition.Message.Part> part;
        // if (message.parts().size() == 1) {
        // part = Optional.ofNullable(message.parts().getFirst());
        // } else {
        part = message.parts().stream().filter(each -> each.name().equals("item")).findFirst();
        // }
        if (part.isEmpty()) {
            return Optional.empty();
        }
        String typeName = switch (part.get()) {
            case TibcoModel.Type.WSDLDefinition.Message.Part.InlineError inlineError -> inlineError.name();
            case TibcoModel.Type.WSDLDefinition.Message.Part.Reference ref -> ref.element().value();
        };
        return Optional.of(typeName);
    }

    private static void analysePartnerLinks(ProcessAnalysisContext cx, Collection<TibcoModel.PartnerLink> links) {
        links.stream()
                .flatMap(link -> link instanceof TibcoModel.PartnerLink.NonEmptyPartnerLink nonEmptyPartnerLink
                        ? Stream.of(nonEmptyPartnerLink)
                        : Stream.empty())
                .forEach(link -> cx.partnerLinkBindings.put(link.name(), link.binding()));
    }

    private static void analyseScope(ProcessAnalysisContext cx, TibcoModel.Scope scope) {
        cx.allocateControlFlowFunctionsIfNeeded(scope);
        cx.pushScope(scope);
        scope.flows().forEach(flow -> analyseFlow(cx, flow));
        scope.faultHandlers().forEach(faultHandler -> analyseActivity(cx, faultHandler));
        scope.sequence().forEach(sequence -> analyseSequence(cx, sequence));
        cx.popScope();
    }

    private static void analyseSequence(ProcessAnalysisContext cx, TibcoModel.Scope.Sequence sequence) {
        cx.inSequence.push(true);
        List<TibcoModel.Scope.Flow.Activity> activities = sequence.activities();
        for (int i = 0; i < activities.size(); i++) {
            TibcoModel.Scope.Flow.Activity activity = activities.get(i);
            if (i == 0) {
                cx.addStartActivity(activity);
            } else {
                cx.addDestination(activities.get(i - 1), activity);
            }
            if (i == activities.size() - 1) {
                cx.addEndActivity(activity);
            }
            analyseActivity(cx, activity);
        }
        cx.inSequence.pop();
    }

    private static void analyseFlow(ProcessAnalysisContext cx, TibcoModel.Scope.Flow flow) {
        cx.inSequence.push(false);
        flow.links().forEach(link -> analyseLink(cx, link));
        flow.activities().forEach(activity -> analyseActivity(cx, activity));
        cx.inSequence.pop();
    }

    private static void analyseActivity(ProcessAnalysisContext cx, TibcoModel.Scope.Flow.Activity activity) {
        if (activity instanceof TibcoModel.Scope.Flow.Activity.Empty) {
            return;
        }
        boolean isInSequence = cx.inSequence.peek();
        cx.allocateActivityNameIfNeeded(activity);
        if (activity instanceof TibcoModel.Scope.Flow.Activity.ActivityWithSources activityWithSources) {
            Collection<TibcoModel.Scope.Flow.Activity.Source> sources = activityWithSources.sources();
            if (!isInSequence && sources.isEmpty()) {
                cx.addEndActivity(activity);
            }
            sources.stream().map(TibcoModel.Scope.Flow.Activity.Source::linkName).map(
                    TibcoModel.Scope.Flow.Link::new).forEach(link -> cx.addSource(activity, link));
        }
        if (activity instanceof TibcoModel.Scope.Flow.Activity.ActivityWithTargets activityWithTargets) {
            Collection<TibcoModel.Scope.Flow.Activity.Target> targets = activityWithTargets.targets();
            if (!isInSequence && targets.isEmpty()) {
                cx.addStartActivity(activity);
            }
            targets.stream().map(TibcoModel.Scope.Flow.Activity.Target::linkName).map(
                    TibcoModel.Scope.Flow.Link::new).forEach(link -> cx.addDestination(link, activity));
        }
        if (activity instanceof TibcoModel.Scope.Flow.Activity.StartActivity) {
            cx.addStartActivity(activity);
        }
        analyseActivityInner(cx, activity);
    }

    private static void analyseActivityInner(ProcessAnalysisContext cx, TibcoModel.Scope.Flow.Activity activity) {
        if (activity instanceof TibcoModel.Scope.Flow.Activity.ActivityWithScope activityWithScope) {
            analyseScope(cx, activityWithScope.scope());
            return;
        }

        boolean isInSequence = cx.inSequence.peek();
        if (!isInSequence && !(activity instanceof TibcoModel.Scope.Flow.Activity.ActivityWithSources)) {
            cx.addEndActivity(activity);
        }

        if (!isInSequence && !(activity instanceof TibcoModel.Scope.Flow.Activity.ActivityWithTargets)) {
            cx.addStartActivity(activity);
        }
        if (activity instanceof TibcoModel.Scope.Flow.Activity.ActivityExtension activityExtension) {
            analyseActivityExtensionConfig(cx, activityExtension.config());
        }
    }

    private static void analyseActivityExtensionConfig(ProcessAnalysisContext cx,
                                                       TibcoModel.Scope.Flow.Activity.ActivityExtension.Config config) {
        if (config instanceof TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.SQL sql) {
            cx.allocateIndexForQuery(sql);
        }
    }

    private static void analyseLink(ProcessAnalysisContext cx, TibcoModel.Scope.Flow.Link link) {
        cx.allocateLinkIfNeeded(link);
    }

    // NOTE: any static field here may need to be explicitly cleared between tests
    private static class ProcessAnalysisContext {

        public int unhandledActivityCount = 0;
        public int totalActivityCount = 0;
        public TibcoModel.Scope currentScope = null;
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
        private final Map<String, TibcoModel.PartnerLink.RestPartnerLink.Binding> partnerLinkBindings =
                new HashMap<>();

        private static final Map<TibcoModel.Scope.Flow.Activity, String> activityFunctionNames =
                new ConcurrentHashMap<>();
        public Map<TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.SQL, Integer> queryIndex =
                new IdentityHashMap<>();
        private final Set<String> inputTypeNames = new HashSet<>();
        private String outputTypeName;
        private final Map<String, String> variableTypes = new HashMap<>();

        private final Map<TibcoModel.Scope, AnalysisResult.ControlFlowFunctions> controlFlowFunctions = new HashMap<>();
        private static final Set<String> controlFlowFunctionNames = new LinkedHashSet<>();
        private final Map<TibcoModel.Scope, Graph<AnalysisResult.GraphNode>> dependencyGraphs = new HashMap<>();
        private final Stack<TibcoModel.Scope> scopeStack = new Stack<>();
        final Stack<Boolean> inSequence = new Stack<>();

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
            };
            String activityName = ConversionUtils.getSanitizedUniqueName(prefix, activityFunctionNames.values());
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
            return activityFunctionNames.get(activity);
        }

        public Map<TibcoModel.Scope.Flow.Activity, AnalysisResult.ActivityData> activityData() {
            Map<TibcoModel.Scope.Flow.Activity, AnalysisResult.ActivityData> data = new HashMap<>();
            for (var activity : activities) {
                String functionName = activityFunctionNames.get(activity);
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
            name = ConversionUtils.getSanitizedUniqueName(name, controlFlowFunctionNames);
            controlFlowFunctionNames.add(name);
            controlFlowFunctions.put(scope, new AnalysisResult.ControlFlowFunctions(name + "ScopeFn",
                    name + "ActivityRunner", name + "FaultHandler"));
        }

        public String getOutputTypeName() {
            if (outputTypeName == null) {
                return "UNKNOWN";
            }
            return outputTypeName;
        }

        private AnalysisResult.GraphNode activityNode(TibcoModel.Scope.Flow.Activity activity) {
            String name = activityNodeName(activity);
            return new AnalysisResult.GraphNode(name, AnalysisResult.GraphNode.Kind.ACTIVITY, activity);
        }

        private AnalysisResult.GraphNode linkNode(TibcoModel.Scope.Flow.Link link) {
            return new AnalysisResult.GraphNode(link.name(), AnalysisResult.GraphNode.Kind.LINK, link);
        }
    }
}
