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

package common;

import java.util.Collection;
import java.util.Map;

/**
 * Record to hold project summary data for combined reporting.
 *
 * @param projectName                    The name of the project
 * @param projectPath                    The path to the project
 * @param reportPath                     The path to the report
 * @param totalActivityCount             Total number of activities in the project
 * @param unhandledActivityCount         Number of unhandled activities
 * @param manualConversionEstimation     Time estimation for manual conversion work
 * @param generatedLineCount             Number of lines of code generated
 * @param successfulConversionPercentage The percentage of successful
 *                                       conversions
 * @param unhandledActivities            Map of unhandled activity types to
 *                                       their elements
 * @param partiallySupportedActivities   Map of partially supported activity types to
 *                                       their elements
 */
public record ProjectSummary(
        String projectName,
        String projectPath,
        String reportPath,
        int totalActivityCount,
        int unhandledActivityCount,
        TimeEstimation manualConversionEstimation,
        long generatedLineCount,
        double successfulConversionPercentage,
        Map<String, Collection<AnalysisReport.UnhandledElement>> unhandledActivities,
        Map<String, Collection<AnalysisReport.UnhandledElement>> partiallySupportedActivities) {

}
