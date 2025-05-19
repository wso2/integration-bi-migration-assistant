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
import tibco.converter.ConversionUtils;


public class ReportGenerationPass extends AnalysisPass {
    private final StringBuilder stringBuilder = new StringBuilder();

    @Override
    protected void analyseActivity(ProcessAnalysisContext cx, TibcoModel.Scope.Flow.Activity activity) {
        String unhandledActivityData = switch (activity) {
            case UnhandledActivity unhandledActivity -> generateUnhandledActivityReport(unhandledActivity);
            case UnhandledInlineActivity unhandledInlineActivity ->
                    generateUnhandledActivityReport(unhandledInlineActivity);
            default -> defaultReport(activity);
        };
        stringBuilder.append(unhandledActivityData);
    }

    private String defaultReport(TibcoModel.Scope.Flow.Activity activity) {
        String reference =
                activity instanceof TibcoModel.Scope.Flow.Activity.ActivityWithName activityWithName ?
                        activityWithName.getName().orElse(ConversionUtils.elementToString(activity.element())) :
                        ConversionUtils.elementToString(activity.element());
        return """
                Successfully converted activity:
                    %s
                """.formatted(reference);
    }

    private String generateUnhandledActivityReport(UnhandledActivity unhandledActivity) {
        // Implement the logic to generate a report for unhandled activities
        return """
                Failed to convert activity:
                    %s
                due to:
                    %s
                """.formatted(ConversionUtils.elementToString(unhandledActivity.element()), unhandledActivity.reason());
    }

    private String generateUnhandledActivityReport(UnhandledInlineActivity unhandledActivity) {
        return """
                Failed to convert %s
                    %s
                """.formatted(unhandledActivity.name(), unhandledActivity.element());

    }

    @Override
    public @NotNull AnalysisResult getResult(ProcessAnalysisContext cx, TibcoModel.Process process) {
        String result = stringBuilder.toString();
        if (!result.isEmpty()) {
            System.out.println("--------");
            System.out.println(result);
            System.out.println("--------");
        }
        return AnalysisResult.empty();
    }
}
