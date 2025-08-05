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
import tibco.model.Process;
import tibco.model.Process5;
import tibco.model.Resource;
import tibco.model.Scope;

public class ResourceAnalysisPass extends AnalysisPass {

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

        if (activity instanceof Process5.ExplicitTransitionGroup.InlineActivity.CallProcess callProcess) {
            String processName = callProcess.processName();
            Process.ProcessIdentifier processId = new Process.ProcessIdentifier(processName);
            if (cx.lookupProcess(processId).isEmpty()) {
                cx.log(LoggingUtils.Level.WARN,
                        String.format("CallProcess references unknown process: %s", processName));
            }
        }
    }

}
