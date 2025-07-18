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
 * @param activityEstimation             The activity estimation data
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
        ActivityEstimation activityEstimation,
        double successfulConversionPercentage,
        Map<String, Collection<AnalysisReport.UnhandledElement>> unhandledActivities,
        Map<String, Collection<AnalysisReport.UnhandledElement>> partiallySupportedActivities) {
    /**
     * Record to hold activity count and time estimation data.
     *
     * @param totalActivityCount     The total number of activities
     * @param unhandledActivityCount The number of unhandled activities
     * @param timeEstimation         The time estimation data
     */
    public record ActivityEstimation(
            int totalActivityCount,
            int unhandledActivityCount,
            TimeEstimation timeEstimation) {
    }

    /**
     * Record to hold time estimation data.
     *
     * @param bestCaseDays    The best case scenario in days
     * @param averageCaseDays The average case scenario in days
     * @param worstCaseDays   The worst case scenario in days
     */
    public record TimeEstimation(
            int bestCaseDays,
            int averageCaseDays,
            int worstCaseDays
    ) {
        public int bestCaseWeeks() {
            return (int) Math.ceil(bestCaseDays / 5.0);
        }

        public int averageCaseWeeks() {
            return (int) Math.ceil(averageCaseDays / 5.0);
        }

        public int worstCaseWeeks() {
            return (int) Math.ceil(worstCaseDays / 5.0);
        }
    }

    /**
     * Calculate the successful conversion percentage.
     *
     * @return The percentage of successful conversions (0-100)
     */
    public double getSuccessfulConversionPercentage() {
        return successfulConversionPercentage;
    }

    /**
     * Get the number of unique unhandled element types for time estimation.
     *
     * @return The number of unique unhandled element types
     */
    public int getUniqueUnhandledElementCount() {
        return unhandledActivities != null ? unhandledActivities.size() : 0;
    }
}
