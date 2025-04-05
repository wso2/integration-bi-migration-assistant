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

import converter.tibco.ConversionUtils;
import tibco.TibcoModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import static ballerina.BallerinaModel.TypeDesc.BuiltinType.XML;

public class ModelAnalyser {

    private static final Logger logger = Logger.getLogger(ModelAnalyser.class.getName());

    private ModelAnalyser() {

    }

    public static AnalysisResult analyseProcess(TibcoModel.Process process) {
        ProcessAnalysisContext cx = new ProcessAnalysisContext();
        analyseProcess(cx, process);
        Map<TibcoModel.Process, Collection<TibcoModel.Scope.Flow.Activity>> startActivities =
                Map.of(process, cx.startActivities);
        Map<TibcoModel.Process, Collection<TibcoModel.Scope.Flow.Activity>> faultHandlerStartActivities =
                Map.of(process, cx.faultHandlerStartActivities);

        Map<TibcoModel.Process, Collection<TibcoModel.Scope.Flow.Activity>> endActivities =
                Map.of(process, cx.endActivities);
        Map<TibcoModel.Scope.Flow.Activity, AnalysisResult.ActivityData> activityData = cx.activityData();
        Map<TibcoModel.Scope.Flow.Link, String> workerNames = cx.workerNames();
        Map<String, TibcoModel.PartnerLink.Binding> partnerLinkBindings =
                Collections.unmodifiableMap(cx.partnerLinkBindings);

        logger.info(String.format("Process Statistics - Name: %s, Total Activities: %d, Unhandled Activities: %d",
                process.name(), cx.totalActivityCount, cx.unhandledActivityCount));
        Map<TibcoModel.Process, String> inputTypeName = Map.of(process, cx.getInputTypeName());
        Map<TibcoModel.Process, String> outputTypeName = Map.of(process, cx.getOutputTypeName());
        return new AnalysisResult(cx.destinationMap, cx.sourceMap, startActivities, faultHandlerStartActivities,
                endActivities, workerNames, activityData, partnerLinkBindings, cx.queryIndex, inputTypeName,
                outputTypeName, cx.workerDependencyGraph);
    }

    private static void analyseProcess(ProcessAnalysisContext cx, TibcoModel.Process process) {
        process.scope().ifPresent(s -> analyseScope(cx, s));
        analysePartnerLinks(cx, process.partnerLinks());
        analyseTypes(cx, process.types());
        process.processInterface().ifPresent(i -> analyzeProcessInterface(cx, i));
    }

    private static void analyzeProcessInterface(ProcessAnalysisContext cx,
                                                TibcoModel.ProcessInterface processInterface) {
        String inputType = sanitizeTypeName(processInterface.input());
        if (!inputType.isEmpty()) {
            cx.setInputTypeName(inputType);
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
            cx.setInputTypeName(messageTypes.get(operation.input().message().value()));
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
        if (message.parts().size() == 1) {
            part = Optional.ofNullable(message.parts().getFirst());
        } else {
            part = message.parts().stream().filter(each -> each.name().equals("item")).findFirst();
        }
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
        links.stream().filter(link -> link.binding().isPresent())
                .forEach(link -> cx.partnerLinkBindings.put(link.name(), link.binding().get()));
    }

    private static void analyseScope(ProcessAnalysisContext cx, TibcoModel.Scope scope) {
        scope.flows().forEach(flow -> analyseFlow(cx, flow));
        scope.faultHandlers().forEach(faultHandler -> analyseActivity(cx, faultHandler));
    }

    private static void analyseFlow(ProcessAnalysisContext cx, TibcoModel.Scope.Flow flow) {
        flow.links().forEach(link -> analyseLink(cx, link));
        flow.activities().forEach(activity -> analyseActivity(cx, activity));
    }

    private static void analyseActivity(ProcessAnalysisContext cx, TibcoModel.Scope.Flow.Activity activity) {
        if (activity instanceof TibcoModel.Scope.Flow.Activity.Empty) {
            return;
        }
        cx.allocateActivityNameIfNeeded(activity);
        if (activity instanceof TibcoModel.Scope.Flow.Activity.ActivityWithSources activityWithSources) {
            Collection<TibcoModel.Scope.Flow.Activity.Source> sources = activityWithSources.sources();
            if (sources.isEmpty()) {
                cx.addEndActivity(activity);
            }
            sources.stream().map(TibcoModel.Scope.Flow.Activity.Source::linkName).map(
                    TibcoModel.Scope.Flow.Link::new).forEach(link -> cx.addSource(activity, link));
        }
        if (activity instanceof TibcoModel.Scope.Flow.Activity.ActivityWithTargets activityWithTargets) {
            Collection<TibcoModel.Scope.Flow.Activity.Target> targets = activityWithTargets.targets();
            if (targets.isEmpty()) {
                cx.addStartActivity(activity);
            }
            targets.stream().map(TibcoModel.Scope.Flow.Activity.Target::linkName).map(
                    TibcoModel.Scope.Flow.Link::new).forEach(link -> cx.addDestination(link, activity));
        }
        boolean isFaultHandler = activity instanceof TibcoModel.Scope.FaultHandler;
        if (isFaultHandler) {
            cx.inFaultHandler = true;
        }
        analyseActivityInner(cx, activity);
        if (isFaultHandler) {
            cx.inFaultHandler = false;
        }
    }

    private static void analyseActivityInner(ProcessAnalysisContext cx, TibcoModel.Scope.Flow.Activity activity) {
        if (activity instanceof TibcoModel.Scope.Flow.Activity.ActivityWithScope activityWithScope) {
            analyseScope(cx, activityWithScope.scope());
            return;
        }
        if (!(activity instanceof TibcoModel.Scope.Flow.Activity.ActivityWithSources)) {
            cx.addEndActivity(activity);
        }

        if (!(activity instanceof TibcoModel.Scope.Flow.Activity.ActivityWithTargets)) {
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
        cx.allocateWorkerIfNeeded(link);
    }

    private static class ProcessAnalysisContext {

        public final Graph<AnalysisResult.GraphNode> workerDependencyGraph = new Graph<>();
        public int unhandledActivityCount = 0;
        public int totalActivityCount = 0;
        public boolean inFaultHandler = false;
        // We are using order preserving sets purely for tests
        private final Collection<TibcoModel.Scope.Flow.Activity> startActivities = new LinkedHashSet<>();
        private final Collection<TibcoModel.Scope.Flow.Activity> faultHandlerStartActivities = new LinkedHashSet<>();
        private final Collection<TibcoModel.Scope.Flow.Activity> endActivities = new LinkedHashSet<>();
        // places where data added to the link ends up
        private final Map<TibcoModel.Scope.Flow.Link, Collection<TibcoModel.Scope.Flow.Activity>> destinationMap =
                new HashMap<>();

        // activities that add data to the link
        private final Map<TibcoModel.Scope.Flow.Link, Collection<TibcoModel.Scope.Flow.Activity>> sourceMap =
                new HashMap<>();

        private final Map<TibcoModel.Scope.Flow.Link, String> linkWorkerNames = new HashMap<>();
        private final Map<TibcoModel.Scope.Flow.Activity, String> activityWorkerNames = new HashMap<>();
        private final Map<String, TibcoModel.PartnerLink.Binding> partnerLinkBindings = new HashMap<>();

        private static final Map<TibcoModel.Scope.Flow.Activity, String> activityFunctionNames =
                new ConcurrentHashMap<>();
        public Map<TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.SQL, Integer> queryIndex =
                new IdentityHashMap<>();
        private String inputTypeName;
        private String outputTypeName;

        public void addEndActivity(TibcoModel.Scope.Flow.Activity activity) {
            endActivities.add(activity);
        }

        public void addStartActivity(TibcoModel.Scope.Flow.Activity activity) {
            if (inFaultHandler) {
                faultHandlerStartActivities.add(activity);
            } else {
                startActivities.add(activity);
            }
            workerDependencyGraph.addRoot(activityNode(activity));
        }

        public void allocateWorkerIfNeeded(TibcoModel.Scope.Flow.Link link) {
            if (linkWorkerNames.containsKey(link)) {
                return;
            }

            String workerName = ConversionUtils.getSanitizedUniqueName(link.name(), linkWorkerNames.values());
            linkWorkerNames.put(link, workerName);
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
                case TibcoModel.Scope.Flow.Activity.CatchAll ignored -> "faultHandler";
                case TibcoModel.Scope.Flow.Activity.Throw ignored -> "throw";
                case TibcoModel.Scope.Flow.Activity.UnhandledActivity ignored -> {
                    unhandledActivityCount++;
                    yield "unhandled";
                }
            };
            String activityName = ConversionUtils.getSanitizedUniqueName(prefix, activityFunctionNames.values());
            activityFunctionNames.put(activity, activityName);
            activityWorkerNames.put(activity, activityName + "_worker");
        }

        public void addDestination(TibcoModel.Scope.Flow.Link source, TibcoModel.Scope.Flow.Activity destination) {
            workerDependencyGraph.addEdge(linkNode(source), activityNode(destination));
            destinationMap.computeIfAbsent(source, (ignored) -> new ArrayList<>()).add(destination);
        }

        public void addSource(TibcoModel.Scope.Flow.Activity source, TibcoModel.Scope.Flow.Link destination) {
            workerDependencyGraph.addEdge(activityNode(source), linkNode(destination));
            sourceMap.computeIfAbsent(destination, (ignored) -> new ArrayList<>()).add(source);
        }

        private String workerName(TibcoModel.Scope.Flow.Activity activity) {
            return activityWorkerNames.get(activity);
        }

        private String workerName(TibcoModel.Scope.Flow.Link link) {
            return linkWorkerNames.get(link);
        }

        public Map<TibcoModel.Scope.Flow.Activity, AnalysisResult.ActivityData> activityData() {
            Map<TibcoModel.Scope.Flow.Activity, AnalysisResult.ActivityData> data = new HashMap<>();
            for (var activity : activityWorkerNames.keySet()) {
                String functionName = activityFunctionNames.get(activity);
                String workerName = activityWorkerNames.get(activity);
                data.put(activity, new AnalysisResult.ActivityData(functionName, workerName, XML, XML));
            }
            return Collections.unmodifiableMap(data);
        }

        public Map<TibcoModel.Scope.Flow.Link, String> workerNames() {
            return Collections.unmodifiableMap(linkWorkerNames);
        }

        public void allocateIndexForQuery(TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.SQL sql) {
            queryIndex.put(sql, queryIndex.size());
        }

        public void setInputTypeName(String inputTypeName) {
            this.inputTypeName = inputTypeName;
        }

        public String getInputTypeName() {
            if (inputTypeName == null) {
                return "UNKNOWN";
            }
            return inputTypeName;
        }

        public void setOutputTypeName(String outputTypeName) {
            this.outputTypeName = outputTypeName;
        }

        public String getOutputTypeName() {
            if (outputTypeName == null) {
                return "UNKNOWN";
            }
            return outputTypeName;
        }

        private AnalysisResult.GraphNode activityNode(TibcoModel.Scope.Flow.Activity activity) {
            String name = workerName(activity);
            return new AnalysisResult.GraphNode(name, AnalysisResult.GraphNode.Kind.ACTIVITY, activity);
        }

        private AnalysisResult.GraphNode linkNode(TibcoModel.Scope.Flow.Link link) {
            String name = workerName(link);
            return new AnalysisResult.GraphNode(name, AnalysisResult.GraphNode.Kind.LINK, link);
        }
    }
}
