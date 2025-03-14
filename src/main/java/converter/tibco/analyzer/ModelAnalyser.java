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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static ballerina.BallerinaModel.TypeDesc.BuiltinType.XML;

import converter.tibco.ConversionUtils;
import tibco.TibcoModel;

public class ModelAnalyser {

    private ModelAnalyser() {

    }

    public static AnalysisResult analyseProcess(TibcoModel.Process process) {
        ProcessAnalysisContext cx = new ProcessAnalysisContext();
        analyseProcess(cx, process);
        Map<TibcoModel.Process, Collection<TibcoModel.Scope.Flow.Activity>> startActivities =
                Map.of(process, cx.startActivities);

        Map<TibcoModel.Process, Collection<TibcoModel.Scope.Flow.Activity>> endActivities =
                Map.of(process, cx.endActivities);
        Map<TibcoModel.Scope.Flow.Activity, AnalysisResult.ActivityData> activityData = cx.activityData();
        Map<TibcoModel.Scope.Flow.Link, String> workerNames = cx.workerNames();
        Map<String, TibcoModel.PartnerLink.Binding> partnerLinkBindings =
                Collections.unmodifiableMap(cx.partnerLinkBindings);
        return new AnalysisResult(cx.destinationMap, cx.sourceMap, startActivities, endActivities, workerNames,
                activityData, partnerLinkBindings);
    }

    private static void analyseProcess(ProcessAnalysisContext cx, TibcoModel.Process process) {
        process.scope().ifPresent(s -> analyseScope(cx, s));
        analysePartnerLinks(cx, process.partnerLinks());
    }

    private static void analysePartnerLinks(ProcessAnalysisContext cx, Collection<TibcoModel.PartnerLink> links) {
        links.stream().filter(link -> link.binding().isPresent())
                .forEach(link -> cx.partnerLinkBindings.put(link.name(), link.binding().get()));
    }

    private static void analyseScope(ProcessAnalysisContext cx, TibcoModel.Scope scope) {
        scope.flows().forEach(flow -> analyseFlow(cx, flow));
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

        if (activity instanceof TibcoModel.Scope.Flow.Activity.Pick pick) {
            analysePick(cx, pick);
            return;
        }
        if (!(activity instanceof TibcoModel.Scope.Flow.Activity.ActivityWithSources)) {
            cx.addEndActivity(activity);
        }

        if (!(activity instanceof TibcoModel.Scope.Flow.Activity.ActivityWithTargets)) {
            cx.addStartActivity(activity);
        }
    }

    private static void analysePick(ProcessAnalysisContext cx, TibcoModel.Scope.Flow.Activity.Pick pick) {
        analyseScope(cx, pick.onMessage().scope());
    }

    private static void analyseLink(ProcessAnalysisContext cx, TibcoModel.Scope.Flow.Link link) {
        cx.allocateWorkerIfNeeded(link);
    }

    private static class ProcessAnalysisContext {

        private final Collection<TibcoModel.Scope.Flow.Activity> startActivities = new HashSet<>();
        private final Collection<TibcoModel.Scope.Flow.Activity> endActivities = new HashSet<>();
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

        public void addEndActivity(TibcoModel.Scope.Flow.Activity activity) {
            endActivities.add(activity);
        }

        public void addStartActivity(TibcoModel.Scope.Flow.Activity activity) {
            startActivities.add(activity);
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
            String prefix = switch (activity) {
                case TibcoModel.Scope.Flow.Activity.ActivityExtension ignored -> "activityExtension";
                case TibcoModel.Scope.Flow.Activity.Empty ignored -> "empty";
                case TibcoModel.Scope.Flow.Activity.ExtActivity ignored -> "extActivity";
                case TibcoModel.Scope.Flow.Activity.Invoke ignored -> "invoke";
                case TibcoModel.Scope.Flow.Activity.Pick ignored -> "pick";
                case TibcoModel.Scope.Flow.Activity.ReceiveEvent ignored -> "receiveEvent";
                case TibcoModel.Scope.Flow.Activity.Reply ignored -> "reply";
            };
            String activityName = ConversionUtils.getSanitizedUniqueName(prefix, activityFunctionNames.values());
            activityFunctionNames.put(activity, activityName);
            activityWorkerNames.put(activity, activityName + "_worker");
        }

        public void addDestination(TibcoModel.Scope.Flow.Link source, TibcoModel.Scope.Flow.Activity activity) {
            destinationMap.computeIfAbsent(source, (ignored) -> new ArrayList<>()).add(activity);
        }

        public void addSource(TibcoModel.Scope.Flow.Activity source, TibcoModel.Scope.Flow.Link destination) {
            sourceMap.computeIfAbsent(destination, (ignored) -> new ArrayList<>()).add(source);
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
    }
}
