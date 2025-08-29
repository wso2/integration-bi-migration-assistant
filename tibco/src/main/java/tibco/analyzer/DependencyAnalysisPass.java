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

import common.LoggingUtils;
import org.jetbrains.annotations.NotNull;
import tibco.model.Process;
import tibco.model.Process5;
import tibco.model.Resource;
import tibco.model.Scope;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class DependencyAnalysisPass extends AnalysisPass {

    private final Set<Process> calledProcesses = new HashSet<>();

    @Override
    protected void analyseActivity(ProcessAnalysisContext cx, Scope.Flow.Activity activity) {
        // Handle resource lookup for activities with resources
        if (activity instanceof Scope.Flow.ActivityWithResources activityWithResources) {
            for (Resource.ResourceIdentifier resource : activityWithResources.resources()) {
                if (cx.lookupResource(resource).isEmpty()) {
                    cx.log(LoggingUtils.Level.SEVERE,
                            String.format("Failed to find resource %s", resource.path()));
                }
            }
        }

        // Handle CallProcess activities for BW6
        if (activity instanceof Scope.Flow.Activity.ExtActivity extActivity &&
            extActivity.callProcess() != null) {
            String subprocessName = extActivity.callProcess().subprocessName();
            Process.ProcessIdentifier processId = new Process.ProcessIdentifier(subprocessName);
            Optional<Process> calledProcess = cx.lookupProcess(processId);
            if (calledProcess.isEmpty()) {
                cx.log(LoggingUtils.Level.WARN,
                        String.format("CallProcess references unknown process: %s", subprocessName));
            } else {
                markAsCalled(calledProcess.get());
            }
        }

        // Handle CallProcess activities for BW5
        if (activity instanceof Process5.ExplicitTransitionGroup.InlineActivity.CallProcess callProcess) {
            String processName = callProcess.processName();
            Process.ProcessIdentifier processId = new Process.ProcessIdentifier(processName);
            Optional<Process> calledProcess = cx.lookupProcess(processId);
            if (calledProcess.isEmpty()) {
                cx.log(LoggingUtils.Level.WARN,
                        String.format("CallProcess references unknown process: %s", processName));
            } else {
                markAsCalled(calledProcess.get());
            }
        }
    }

    private void markAsCalled(Process process) {
        calledProcesses.add(process);
    }

    @Override
    public @NotNull AnalysisResult getResult(ProcessAnalysisContext cx, Process process) {
        return new AnalysisResultImpl(
            Collections.emptyMap(), // destinationMap
            Collections.emptyMap(), // sourceMap  
            Collections.emptyMap(), // activityData
            Collections.emptyMap(), // partnerLinkBindings
            Collections.emptyMap(), // queryIndex
            Collections.emptyMap(), // inputTypeNames
            Collections.emptyMap(), // outputTypeName
            Collections.emptyMap(), // variableTypes
            Collections.emptyMap(), // dependencyGraphs
            Collections.emptyMap(), // controlFlowFunctions
            Collections.emptyMap(), // scopes
            Collections.emptyMap(), // activityByName
            Collections.emptyMap(), // explicitTransitionGroupDependencies
            Collections.emptyMap(), // explicitTransitionGroupControlFlowFunctions
            Collections.emptyMap(), // xsdTypes
            calledProcesses,        // called processes
            TibcoAnalysisReport.empty()
        );
    }

}
