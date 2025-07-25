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
import java.util.Comparator;

public class CommonComponents {

    private static String estimationNote(String scenario, double hours, double lines, String reason) {
        return """
                <li>%s:
                  <ul>
                    <li>%.1f day per each mediator code line for analysis, implementation, and testing</li>
                    <li>%.1f minutes per each converted code line for inspection and verification</li>
                    <li>%s</li>
                  </ul>
                </li>
                """.formatted(scenario, hours, lines, reason);
    }

    static String estimationNotes(Estimation mediatorEstimate, Estimation lineEstimate) {
        String bestCase = estimationNote("Best case scenario",
                mediatorEstimate.bestCaseHours() / 8.0, lineEstimate.bestCaseHours() * 60.0,
                "Assumes minimal complexity and straightforward implementations");

        String avgCase = estimationNote("Average case scenario",
                mediatorEstimate.avgCaseHours() / 8.0, lineEstimate.avgCaseHours() * 60.0,
                "Assumes medium complexity with moderate implementation challenges");

        String worstCase = estimationNote("Worst case scenario",
                mediatorEstimate.worstCaseHours() / 8.0, lineEstimate.worstCaseHours() * 60.0,
                "Assumes high complexity with significant implementation challenges");
        return """
                <div class="estimation-notes">
                      <p><strong>Estimation Scenarios:</strong> Time measurement: 1 day = 8 hours,
                         5 working days = 1 week</p>
                      <ul>
                        %s
                        %s
                        %s
                      </ul>
                    </div>
                """.formatted(bestCase, avgCase, worstCase);
    }

    static String generateMetric(double overallConfidence, int totalCodeLines, int mediatorCount) {
        String confidencePercentage = String.format("%.0f%%", overallConfidence * 100);
        String confidenceLevel = getConfidenceLevel(overallConfidence);
        String confidenceBadgeClass = getConfidenceBadgeClass(overallConfidence);
        String coverageBarColor = getCoverageBarColor(overallConfidence);
        int coverageWidth = (int) Math.round(overallConfidence * 100);
        return """
                <div class="metric">
                  <div class="metric-left">
                    <span class="metric-value">%s</span>
                    <span class="metric-label">Overall Confidence</span>
                    <div class="coverage-indicator">
                      <div class="coverage-bar" style="width: %d%%; background-color: %s;"></div>
                    </div>
                    <div class="status-badge-container">
                      <span class="status-badge %s">%s</span>
                    </div>
                  </div>
                  <div class="metric-right">
                    <div class="coverage-breakdown">
                      <div style="display: flex; justify-content: space-between; margin-bottom: 4px;">
                        <span class="breakdown-label">Total Code Lines:</span>
                        <span class="breakdown-value">%d</span>
                      </div>
                      <div style="display: flex; justify-content: space-between; margin-bottom: 4px;">
                        <span class="breakdown-label">Mediators:</span>
                        <span class="breakdown-value">%d</span>
                      </div>
                    </div>
                  </div>
                </div>
                """.formatted(confidencePercentage, coverageWidth, coverageBarColor, confidenceBadgeClass,
                confidenceLevel, totalCodeLines, mediatorCount);
    }

    private static String getConfidenceLevel(double confidence) {
        if (confidence >= 0.8) {
            return "HIGH CONFIDENCE";
        }
        if (confidence >= 0.5) {
            return "MEDIUM CONFIDENCE";
        }
        return "LOW CONFIDENCE";
    }

    private static String getConfidenceBadgeClass(double confidence) {
        if (confidence >= 0.8) {
            return "status-high";
        }
        if (confidence >= 0.5) {
            return "status-medium";
        }
        return "status-low";
    }

    private static String getCoverageBarColor(double confidence) {
        if (confidence >= 0.8) {
            return "#4CAF50"; // Green
        }
        if (confidence >= 0.5) {
            return "#FF9800"; // Orange
        }
        return "#F44336"; // Red
    }

    static String generateMediatorBreakdown(Collection<Mediator> mediators) {
        StringBuilder tableRows = new StringBuilder();

        // Sort mediators by confidence (least confident first)
        Mediator[] sortedMediators = mediators.stream()
                .sorted(Comparator.comparingDouble(Mediator::confidenceScore))
                .toArray(Mediator[]::new);

        for (Mediator mediator : sortedMediators) {
            tableRows.append("""
                            <tr>
                              <td>%s</td>
                              <td>%d</td>
                              <td>%.2f</td>
                              <td>%.2f</td>
                            </tr>
                    """.formatted(mediator.name(), mediator.instances(), mediator.confidenceScore(),
                    mediator.complexityScore()));
        }

        return """
                  <div class="summary-container">
                    <h2>Mediator wise breakdown</h2>
                    <table>
                      <tr><th>Tag Name</th><th>Frequency</th><th>Confidence</th><th>Complexity</th></tr>
                       %s
                  </table>
                  </div>
                """.formatted(tableRows.toString());
    }
}
