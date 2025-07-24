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

package synapse.converter.report;

import java.util.Collection;
import java.util.Map;

public class AggregateReportGen {

    public record ProjectData(Project project, int totalCodeLines) {

    }

    public String generateReport(Map<String, ProjectData> data, Estimation mediatorEstimate,
                                 Estimation lineEstimation) {
        return "";
    }

    private record WorkEstimation(double bestCaseDays, double avgCaseDays, double worstCaseDays) {

        public int bestCaseWeeks() {
            return Math.max(1, (int) Math.ceil(bestCaseDays / 5));
        }

        public int avgCaseWeeks() {
            return Math.max(1, (int) Math.ceil(avgCaseDays / 5));
        }

        public int worstCaseWeeks() {
            return Math.max(1, (int) Math.ceil(worstCaseDays / 5));
        }

        static WorkEstimation combine(WorkEstimation we1, WorkEstimation we2) {
            return new WorkEstimation(
                    we1.bestCaseDays + we2.bestCaseDays,
                    we1.avgCaseDays + we2.avgCaseDays,
                    we1.worstCaseDays + we2.worstCaseDays
            );
        }
    }

    private static double totalConfidence(Collection<ProjectData> projects) {
        int totalLines = projects.stream().map(ProjectData::totalCodeLines).reduce(0, Integer::sum);
        double accumConfidence = projects.stream().map(each -> each.project.overallConfidence() * each.totalCodeLines)
                .reduce(0.0, Double::sum);
        return accumConfidence / totalLines;
    }
}
