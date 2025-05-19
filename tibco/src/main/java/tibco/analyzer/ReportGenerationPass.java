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

import org.jetbrains.annotations.NotNull;
import tibco.TibcoModel;
import tibco.TibcoModel.Process.ExplicitTransitionGroup.InlineActivity.UnhandledInlineActivity;
import tibco.TibcoModel.Scope.Flow.Activity.UnhandledActivity;

import java.util.ArrayList;
import java.util.Collection;


public class ReportGenerationPass extends AnalysisPass {
    Collection<AnalysisReport.UnhandledActivityElement> reportElements = new ArrayList<>();
    int totalActivities = 0;

    @Override
    protected void analyseActivity(ProcessAnalysisContext cx, TibcoModel.Scope.Flow.Activity activity) {
        totalActivities++;
        AnalysisReport.UnhandledActivityElement unhandledActivityElement = switch (activity) {
            case UnhandledActivity unhandledActivity -> generateUnhandledActivityReport(unhandledActivity);
            case UnhandledInlineActivity unhandledInlineActivity ->
                    generateUnhandledActivityReport(unhandledInlineActivity);
            default -> null;
        };
        if (unhandledActivityElement != null) {
            reportElements.add(unhandledActivityElement);
        }
    }

    private AnalysisReport.UnhandledActivityElement generateUnhandledActivityReport(UnhandledActivity unhandledActivity) {
        return new AnalysisReport.UnhandledActivityElement.UnNamedUnhandledActivityElement(unhandledActivity.element());
    }

    private AnalysisReport.UnhandledActivityElement generateUnhandledActivityReport(UnhandledInlineActivity unhandledActivity) {
        return new AnalysisReport.UnhandledActivityElement.NamedUnhandledActivityElement(unhandledActivity.name(), unhandledActivity.element());
    }

    @Override
    public @NotNull AnalysisResult getResult(ProcessAnalysisContext cx, TibcoModel.Process process) {
        AnalysisReport report = new AnalysisReport(totalActivities, reportElements.size(), this.reportElements);
        AnalysisResult result = AnalysisResult.empty();
        result.report = report;
        return result;
    }
}
