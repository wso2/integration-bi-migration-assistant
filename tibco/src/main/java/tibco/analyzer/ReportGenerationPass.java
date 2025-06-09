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
import tibco.analyzer.TibcoAnalysisReport.UnhandledActivityElement.NamedUnhandledActivityElement;
import tibco.analyzer.TibcoAnalysisReport.UnhandledActivityElement.UnNamedUnhandledActivityElement;
import tibco.model.Process;
import tibco.model.Process5.ExplicitTransitionGroup.InlineActivity.UnhandledInlineActivity;
import tibco.model.Scope;
import tibco.model.Scope.Flow.Activity.UnhandledActivity;

import java.util.ArrayList;
import java.util.Collection;


public class ReportGenerationPass extends AnalysisPass {
    Collection<TibcoAnalysisReport.UnhandledActivityElement> reportElements = new ArrayList<>();
    int totalActivities = 0;

    @Override
    protected void analyseActivity(ProcessAnalysisContext cx, Scope.Flow.Activity activity) {
        totalActivities++;
        TibcoAnalysisReport.UnhandledActivityElement unhandledActivityElement = switch (activity) {
            case UnhandledActivity unhandledActivity -> generateUnhandledActivityReport(unhandledActivity);
            case UnhandledInlineActivity unhandledInlineActivity ->
                    generateUnhandledActivityReport(unhandledInlineActivity);
            default -> null;
        };
        if (unhandledActivityElement != null) {
            reportElements.add(unhandledActivityElement);
        }
    }

    private TibcoAnalysisReport.UnhandledActivityElement generateUnhandledActivityReport(
            UnhandledActivity unhandledActivity) {
        return new UnNamedUnhandledActivityElement(unhandledActivity.element(), unhandledActivity.fileName());
    }

    private TibcoAnalysisReport.UnhandledActivityElement generateUnhandledActivityReport(
            UnhandledInlineActivity unhandledActivity) {
        return new NamedUnhandledActivityElement(unhandledActivity.name(), unhandledActivity.activityType(),
                unhandledActivity.element(), unhandledActivity.fileName());
    }

    @Override
    public @NotNull AnalysisResult getResult(ProcessAnalysisContext cx, Process process) {
        TibcoAnalysisReport report = new TibcoAnalysisReport(totalActivities, reportElements.size(),
                this.reportElements);
        AnalysisResult result = AnalysisResult.empty();
        result.setReport(report);
        return result;
    }
}
