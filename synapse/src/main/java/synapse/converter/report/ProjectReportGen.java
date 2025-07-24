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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;

public class ProjectReportGen {

    public static String genReport(String projectName, Project project, int totalCodeLines, Estimation mediatorEstimate,
                                   Estimation lineEstimate) {
        StringBuilder html = new StringBuilder();

        html.append(generateHtmlHeader());
        html.append(generatePageTitle(projectName));
        html.append(generateCoverageOverview(project, totalCodeLines));
        html.append(generateManualWorkEstimation(project, totalCodeLines, mediatorEstimate, lineEstimate));
        html.append(generateMediatorBreakdown(project));
        html.append(generateFooter());
        html.append(generateHtmlFooter());

        return html.toString();
    }

    private static String generateHtmlHeader() {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                  <title>Migration Assessment</title>
                  <style>
                    /* Base styles */
                    body {
                      font-family: Arial, sans-serif;
                      background-color: #f4f4f9;
                      color: #333;
                      margin: 0;
                      padding: 20px;
                    }
                
                    .container {
                      max-width: 1200px;
                      margin: 0 auto;
                    }
                
                    h1, h2, h3 {
                      color: #333;
                    }
                
                    h1 {
                      text-align: center;
                      color: #4682B4;
                      font-size: 2.5em;
                      font-weight: 300;
                      margin: 15px auto 40px;
                      padding: 0 0 15px;
                      max-width: fit-content;
                      position: relative;
                      border-bottom: 1px solid rgba(70, 130, 180, 0.2);
                    }
                
                    h1::after {
                      content: "";
                      position: absolute;
                      bottom: -1px;
                      left: 50%;
                      transform: translateX(-50%);
                      width: 100px;
                      height: 3px;
                      background-color: rgba(70, 130, 180, 0.8);
                    }
                
                    .summary-container h2 {
                      margin-top: 0;
                      color: #4682B4;
                      border-bottom: 2px solid #f0f0f0;
                      padding-bottom: 10px;
                      margin-bottom: 20px;
                      text-align: center;
                      font-size: 1.5em;
                    }
                
                    h3 {
                      color: #4682B4;
                      border-bottom: 2px solid #f0f0f0;
                      padding-bottom: 10px;
                      margin-bottom: 20px;
                    }
                
                    /* Summary container styling */
                    .summary-container {
                      background-color: #fff;
                      padding: 25px;
                      border-radius: 10px;
                      box-shadow: 0 3px 10px rgba(0, 0, 0, 0.1);
                      margin: 25px 0;
                      transition: box-shadow 0.3s;
                    }
                
                    .summary-container:hover {
                      box-shadow: 0 5px 15px rgba(0, 0, 0, 0.15);
                    }
                
                    /* Coverage indicator */
                    .coverage-indicator {
                      width: 80%;
                      height: 6px;
                      background-color: #f0f0f0;
                      border-radius: 3px;
                      overflow: hidden;
                      box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.1);
                      margin-top: 8px;
                    }
                
                    .coverage-bar {
                      height: 100%;
                      border-radius: 3px;
                      transition: width 0.5s ease-in-out;
                    }
                
                    /* Table styling */
                    table {
                      width: 100%;
                      border-collapse: collapse;
                      margin: 20px 0;
                    }
                
                    th, td {
                      border: 1px solid #ddd;
                      padding: 12px;
                      text-align: left;
                    }
                
                    th {
                      background-color: #4682B4;
                      color: white;
                    }
                
                    tr:nth-child(even) {
                      background-color: #f2f2f2;
                    }
                
                    tr:hover {
                      background-color: #ddd;
                    }
                
                    /* Status badges */
                    .status-badge {
                      padding: 6px 12px;
                      border-radius: 20px;
                      font-size: 0.75em;
                      font-weight: 600;
                      letter-spacing: 0.3px;
                      text-transform: uppercase;
                      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
                      display: inline-block;
                      margin-left: 15px;
                    }
                
                    .status-high {
                      background-color: #e8f5e9;
                      color: #2e7d32;
                      border: 1px solid rgba(46, 125, 50, 0.2);
                    }
                
                    .status-medium {
                      background-color: #fff8e1;
                      color: #f57c00;
                      border: 1px solid rgba(245, 124, 0, 0.2);
                    }
                
                    .status-low {
                      background-color: #ffebee;
                      color: #c62828;
                      border: 1px solid rgba(198, 40, 40, 0.2);
                    }
                
                    /* Footer */
                    footer {
                      text-align: center;
                      margin-top: 20px;
                      font-size: 0.9em;
                      color: #666;
                    }
                
                    /* Metric styling with box shape and hover effects */
                    .metric {
                      width: 100%;
                      box-sizing: border-box;
                      padding: 15px 20px;
                      display: flex;
                      flex-direction: row;
                      align-items: center;
                      gap: 20px;
                      background-color: #f0f8ff;
                      border-radius: 8px;
                      transition: transform 0.2s, box-shadow 0.2s;
                      margin-bottom: 15px;
                      border: 1px solid #eaeaea;
                      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
                    }
                
                    .metric:hover {
                      transform: translateY(-3px);
                      box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
                    }
                
                    .metric-value {
                      font-weight: bold;
                      font-size: 2em;
                      color: #4682B4;
                      margin-bottom: 5px;
                    }
                
                    .metric-label {
                      font-size: 1em;
                      margin-top: 5px;
                      color: #666;
                      text-align: center;
                    }
                
                    .metric-left {
                      flex: 1;
                      display: flex;
                      flex-direction: column;
                      align-items: center;
                    }
                
                    .metric-right {
                      flex: 1;
                      padding-top: 10px;
                    }
                
                    .coverage-breakdown {
                      font-size: 0.85em;
                      color: #666;
                    }
                
                    .breakdown-label {}
                    .breakdown-value {
                      font-weight: 600;
                    }
                
                    .status-badge-container {
                      margin-top: 5px;
                    }
                
                    /* Time estimation table styling */
                    .time-best {
                      color: #4CAF50; /* Green for Best Case */
                      font-weight: 600;
                    }
                
                    .time-avg {
                      color: #4682B4; /* Blue for Average Case */
                      font-weight: 600;
                    }
                
                    .time-worst {
                      color: #FF5722; /* Orange/Red for Worst Case */
                      font-weight: 600;
                    }
                
                    .estimation-notes {
                      margin-top: 25px;
                      padding: 20px;
                      background-color: #f8f9fa;
                      border-radius: 8px;
                      border-left: 4px solid #4682B4;
                      box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
                    }
                
                    .estimation-notes p {
                      margin-top: 0;
                    }
                
                    .estimation-notes ul {
                      margin: 15px 0 5px 25px;
                      padding-left: 0;
                    }
                
                    .estimation-notes li {
                      margin-bottom: 8px;
                      line-height: 1.4;
                    }
                  </style>
                </head>
                <body>
                <div class="container">
                """;
    }

    private static String generatePageTitle(String projectName) {
        return """
                  <h1>%s - Migration Assessment</h1>
                """.formatted(projectName);
    }

    private static String generateCoverageOverview(Project project, int totalCodeLines) {
        double overallConfidence = project.overallConfidence();
        String confidencePercentage = String.format("%.0f%%", overallConfidence * 100);
        String confidenceLevel = getConfidenceLevel(overallConfidence);
        String confidenceBadgeClass = getConfidenceBadgeClass(overallConfidence);
        String coverageBarColor = getCoverageBarColor(overallConfidence);
        int coverageWidth = (int) Math.round(overallConfidence * 100);

        return """
                  <div class="summary-container">
                    <h2>Migration Coverage Overview</h2>
                    <div class="metrics" style="flex-direction: column; align-items: center; width: 100%%;">
                
                      <!-- Overall Confidence -->
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
                    </div>
                  </div>
                """.formatted(confidencePercentage, coverageWidth, coverageBarColor, confidenceBadgeClass,
                confidenceLevel,
                totalCodeLines, project.mediators().length);
    }

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

    private static String generateManualWorkEstimation(Project project, double lines, Estimation mediatorEstimate,
                                                       Estimation lineEstimate) {
        double bestEstimate =
                project.timeEstimate(mediatorEstimate.bestCaseHours()) + lineEstimate.bestCaseHours() * lines;
        double avgEstimate =
                project.timeEstimate(mediatorEstimate.avgCaseHours()) + lineEstimate.avgCaseHours() * lines;
        double worstEstimate =
                project.timeEstimate(mediatorEstimate.worstCaseHours()) + lineEstimate.worstCaseHours() * lines;

        double bestDays = bestEstimate / 8.0;
        double avgDays = avgEstimate / 8.0;
        double worstDays = worstEstimate / 8.0;

        int bestWeeks = Math.max(1, (int) Math.ceil(bestDays / 5.0));
        int avgWeeks = Math.max(1, (int) Math.ceil(avgDays / 5.0));
        int worstWeeks = Math.max(1, (int) Math.ceil(worstDays / 5.0));
        String bestCase = estimationNote("Best case scenario",
                mediatorEstimate.bestCaseHours() / 8.0, lineEstimate.bestCaseHours() / 60.0,
                "Assumes minimal complexity and straightforward implementations");

        String avgCase = estimationNote("Average case scenario",
                mediatorEstimate.avgCaseHours() / 8.0, lineEstimate.avgCaseHours() / 60.0,
                "Assumes medium complexity with moderate implementation challenges");

        String worstCase = estimationNote("Worst case scenario",
                mediatorEstimate.worstCaseHours() / 8.0, lineEstimate.worstCaseHours() / 60.0,
                "Assumes high complexity with significant implementation challenges");
        return """
                  <div class="summary-container">
                    <h2>Manual Work Estimation</h2>
                    <table>
                      <tr>
                        <th>Scenario</th>
                        <th>Working Days</th>
                        <th>Weeks (approx.)</th>
                      </tr>
                      <tr>
                        <td>Best Case</td>
                        <td class="time-best">%.1f days</td>
                        <td class="time-best">%d weeks</td>
                      </tr>
                      <tr>
                        <td>Average Case</td>
                        <td class="time-avg">%.1f days</td>
                        <td class="time-avg">%d weeks</td>
                      </tr>
                      <tr>
                        <td>Worst Case</td>
                        <td class="time-worst">%.1f days</td>
                        <td class="time-worst">%d weeks</td>
                      </tr>
                    </table>
                    <div class="estimation-notes">
                      <p><strong>Estimation Scenarios:</strong> Time measurement: 1 day = 8 hours,
                         5 working days = 1 week</p>
                      <ul>
                        %s
                        %s
                        %s
                      </ul>
                    </div>
                  </div>
                """.formatted(bestDays, bestWeeks, avgDays, avgWeeks, worstDays, worstWeeks,
                bestCase, avgCase, worstCase);
    }

    private static String generateMediatorBreakdown(Project project) {
        StringBuilder tableRows = new StringBuilder();

        // Sort mediators by confidence (least confident first)
        Mediator[] sortedMediators = Arrays.stream(project.mediators())
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

    private static String generateFooter() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy, h:mm:ss a");
        return """
                </div>
                <footer><p>Report generated on: %s</p></footer>
                """.formatted(now.format(formatter));
    }

    private static String generateHtmlFooter() {
        return """
                </body></html>
                """;
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
}
