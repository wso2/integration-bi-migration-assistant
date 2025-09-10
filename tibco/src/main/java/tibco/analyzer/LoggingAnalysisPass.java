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
import tibco.model.Process5.ExplicitTransitionGroup.InlineActivity.UnhandledInlineActivity;
import tibco.model.Scope;


public class LoggingAnalysisPass extends AnalysisPass {
    private int totalActivityCount = 0;
    private int unhandledActivityCount = 0;

    @Override
    protected void analyseActivity(ProcessAnalysisContext cx, Scope.Flow.Activity activity) {
        totalActivityCount++;
        if (activity instanceof Scope.Flow.Activity.UnhandledActivity ||
                activity instanceof UnhandledInlineActivity) {
            unhandledActivityCount++;
        }
    }

    @Override
    public @NotNull AnalysisResult getResult(ProcessAnalysisContext cx, Process process) {
        cx.log(LoggingUtils.Level.INFO,
                String.format("Process Statistics - Name: %s, Total Activities: %d, Unhandled Activities: %d",
                process.name(), totalActivityCount, unhandledActivityCount));
        return AnalysisResult.empty();
    }

}
