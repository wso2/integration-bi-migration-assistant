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

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Stream;

/**
 * A class to generate combined summary reports that link to individual project reports.
 */
public class CombinedSummaryReport {

    private final String reportTitle;
    private final List<ProjectSummary> projectSummaries;

    // CSS styles as a constant to avoid format specifier issues
    private static final String CSS_STYLES = """
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

            h1, h2 {
                text-align: center;
                color: #333;
            }

            /* Container styling */
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

            .summary-container h2 {
                margin-top: 0;
                color: #4682B4;
                border-bottom: 2px solid #f0f0f0;
                padding-bottom: 10px;
                margin-bottom: 20px;
            }

            /* Centered title with subtle border */
            .container > h1 {
                color: #4682B4;
                font-size: 2.5em;
                font-weight: 300;
                margin: 15px auto 40px;
                padding: 0 0 15px;
                max-width: 600px;
                position: relative;
                border-bottom: 1px solid rgba(70, 130, 180, 0.2);
            }

            .container > h1::after {
                content: "";
                position: absolute;
                bottom: -1px;
                left: 50%;
                transform: translateX(-50%);
                width: 100px;
                height: 3px;
                background-color: rgba(70, 130, 180, 0.8);
            }

            /* Metrics styling */
            .metrics {
                display: flex;
                flex-wrap: wrap;
                gap: 20px;
                margin: 25px 0;
                justify-content: space-around;
            }

            .metric {
                display: flex;
                flex-direction: column;
                align-items: center;
                background-color: #f8f9fa;
                padding: 15px 25px;
                border-radius: 8px;
                min-width: 150px;
                transition: transform 0.2s, box-shadow 0.2s;
            }

            .metric:hover {
                transform: translateY(-3px);
                box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
            }

            .metric-value {
                font-weight: bold;
                font-size: 1.8em;
                color: #4682B4;
                margin-bottom: 5px;
            }

            .metric-label {
                font-size: 0.9em;
                color: #666;
                text-align: center;
            }

            /* Project card metrics adjustments */
            .project-card .metrics {
                margin-top: 10px;
                justify-content: flex-start;
            }

            .project-card .metric-value {
                font-size: 1.4em;
            }

            .project-card .metric-label {
                font-size: 0.9em;
            }

            .project-card .metric {
                display: flex;
                flex-direction: row;
                align-items: flex-start;
                width: 100%;
                gap: 20px;
            }

            .project-card .metric-left {
                flex: 1;
                display: flex;
                flex-direction: column;
                align-items: center;
            }

            .project-card .metric-right {
                flex: 1;
                padding-top: 10px;
            }

            .project-card .metric .coverage-indicator {
                width: 80%;
                height: 6px;
                background-color: #f0f0f0;
                border-radius: 3px;
                overflow: hidden;
                box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.1);
                margin-top: 8px;
            }

            .project-card .metric .coverage-bar {
                height: 100%;
                border-radius: 3px;
                transition: width 0.5s ease-in-out;
            }

            .project-card .metric .coverage-breakdown {
                font-size: 0.85em;
                color: #666;
            }

            .project-card .metric .coverage-breakdown div {
                display: flex;
                justify-content: space-between;
                margin-bottom: 4px;
            }

            .project-card .metric .coverage-breakdown .breakdown-label {
                margin-right: 8px;
            }

            .project-card .metric .coverage-breakdown .breakdown-value {
                font-weight: 600;
            }

            /* Project cards */
            .project-card {
                background-color: #fff;
                border-radius: 10px;
                box-shadow: 0 3px 12px rgba(0, 0, 0, 0.08);
                margin: 20px 0;
                padding: 20px;
                display: grid;
                grid-template-columns: 3fr 2fr;
                gap: 15px;
                transition: transform 0.2s, box-shadow 0.2s;
                align-items: center;
            }

            .project-card:hover {
                transform: translateY(-2px);
                box-shadow: 0 5px 15px rgba(0, 0, 0, 0.12);
            }

            .project-header {
                display: flex;
                align-items: center;
                margin-bottom: 15px;
                gap: 12px;
            }

            .project-name {
                font-size: 1.2em;
                font-weight: 600;
                margin-right: 10px;
            }

            .project-link {
                color: #4682B4;
                text-decoration: none;
                position: relative;
                padding-bottom: 2px;
            }

            .project-link:after {
                content: '';
                position: absolute;
                width: 0;
                height: 2px;
                bottom: 0;
                left: 0;
                background-color: #4682B4;
                transition: width 0.3s;
            }

            .project-link:hover:after {
                width: 100%;
            }

            .project-details {
                display: flex;
                flex-direction: column;
                gap: 15px;
            }

            .project-metrics {
                display: flex;
                align-items: flex-start;
                gap: 20px;
            }

            /* Project coverage indicators */
            .project-coverage {
                margin-top: 10px;
            }

            .coverage-indicator {
                width: 100%;
                height: 12px;
                background-color: #f0f0f0;
                border-radius: 6px;
                overflow: hidden;
                box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.1);
            }

            .coverage-bar {
                height: 100%;
                border-radius: 6px;
                transition: width 0.5s ease-in-out;
            }

            /* Project left column styling */
            .project-left {
                display: flex;
                flex-direction: column;
                justify-content: center;
            }

            /* Time estimates styling */
            .time-estimates {
                display: grid;
                grid-template-columns: repeat(3, 1fr);
                gap: 10px;
                background-color: #f8f9fa;
                border-radius: 8px;
                padding: 15px;
                padding-top: 30px;
                height: fit-content;
                justify-self: center;
                position: relative;
                transition: transform 0.2s, box-shadow 0.2s;
            }

            .time-estimates:hover {
                transform: translateY(-3px);
                box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
            }

            .time-estimates::before {
                content: "Manual Work Estimation";
                position: absolute;
                top: 8px;
                left: 0;
                width: 100%;
                text-align: center;
                font-weight: normal;
                font-size: 0.9em;
                color: #666;
            }

            .time-estimate {
                display: flex;
                flex-direction: column;
                align-items: center;
            }

            .time-label {
                font-size: 0.8em;
                color: #666;
                margin-bottom: 5px;
            }

            .time-value {
                font-weight: bold;
                color: #4682B4;
                display: flex;
                flex-direction: column;
                align-items: center;
            }

            .time-days {
                font-size: 1.1em;
            }

            .time-weeks {
                font-size: 0.75em;
                color: #777;
                margin-top: 2px;
            }

            .time-best {
                color: #4CAF50;
            }

            .time-worst {
                color: #FF5722;
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

            /* Estimation notes */
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

            /* Footer */
            footer {
                text-align: center;
                margin-top: 20px;
                font-size: 0.9em;
                color: #666;
            }

            /* Overview specific styles */
            .overview-metrics {
                flex-direction: column;
                align-items: center;
                width: 100%;
            }

            .overview-metric {
                width: 100%;
                box-sizing: border-box;
                padding: 15px 20px;
            }

            .overview-metric:nth-child(2) {
                display: flex;
                flex-direction: row;
                align-items: flex-start;
                gap: 20px;
            }

            .overview-indicator {
                width: 80%;
                height: 6px;
                background-color: #f0f0f0;
                border-radius: 3px;
                overflow: hidden;
                box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.1);
                margin-top: 8px;
            }

            .breakdown-row {
                display: flex;
                justify-content: space-between;
                margin-bottom: 4px;
            }

            .time-estimates-container {
                display: flex;
                justify-content: center;
                margin: 20px 0;
            }

            .overview-time-estimates {
                width: 100%;
                box-sizing: border-box;
            }

            /* Responsive design */
            @media (max-width: 768px) {
                .metrics {
                    flex-direction: column;
                    align-items: center;
                    gap: 15px;
                }

                .metric {
                    width: 80%;
                }

                .project-card {
                    grid-template-columns: 1fr;
                }

                .time-estimates {
                    margin-top: 15px;
                }
            }
            """;

    /**
     * Create a new combined summary report.
     *
     * @param reportTitle      The title of the combined report
     * @param projectSummaries List of project summaries to include in the report
     */
    public CombinedSummaryReport(String reportTitle, List<ProjectSummary> projectSummaries) {
        this.reportTitle = reportTitle;
        this.projectSummaries = projectSummaries;
    }

    /**
     * Generates an HTML combined summary report.
     *
     * @return A string containing the HTML report
     */
    public String toHTML() {
        StringBuilder html = new StringBuilder();

        html.append(generateHtmlHeader());

        if (projectSummaries.isEmpty()) {
            html.append(generateEmptyProjectsMessage());
        } else {
            html.append(generateOverallStatistics());
            html.append(generateProjectSummaries());
            html.append(generateOverallTimeEstimation());
        }

        html.append(generateHtmlFooter());

        return html.toString();
    }

    private String generateHtmlHeader() {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>%s</title>
                    <style>%s</style>
                </head>
                <body>
                    <div class="container">
                        <h1>%s</h1>
                """.formatted(reportTitle, CSS_STYLES, reportTitle);
    }

    private String generateHtmlFooter() {
        return """
                    </div>
                    <footer>
                        <p>Report generated on: <span id="datetime"></span></p>
                    </footer>
                    <script>
                        document.addEventListener('DOMContentLoaded', function() {
                            // Set coverage bar styles from data attributes
                            const coverageBars = document.querySelectorAll('.coverage-bar[data-width]');
                            coverageBars.forEach(function(bar) {
                                const width = bar.getAttribute('data-width');
                                const color = bar.getAttribute('data-color');
                                bar.style.width = width + '%';
                                bar.style.backgroundColor = color;
                            });

                            const toolSupportTable = document.querySelector('#toolSupportSection table');
                            if (toolSupportTable.rows.length <= 1) {
                                toolSupportTable.style.display = 'none';
                                document.getElementById('toolSupportEmpty').style.display = 'block';
                                document.querySelector('#toolSupportSection .estimation-notes').style.display = 'none';
                            }
                        });
                    </script>
                    <script>document.getElementById("datetime").innerHTML = new Date().toLocaleString();</script>
                </body>
                </html>
                """;
    }

    private String generateEmptyProjectsMessage() {
        return """
                <div class="summary-container">
                    <div class="no-projects" style="text-align: center; color: #666; font-style: italic;">No projects found for analysis.</div>
                </div>
                """;
    }

    private String generateOverallStatistics() {
        int totalProjects = projectSummaries.size();
        int totalActivities = projectSummaries.stream()
                .mapToInt(ProjectSummary::totalActivityCount)
                .sum();
        int totalUnhandledActivities = projectSummaries.stream()
                .mapToInt(ProjectSummary::unhandledActivityCount)
                .sum();
        double averageConversionPercentage = projectSummaries.stream()
                .mapToDouble(ProjectSummary::successfulConversionPercentage)
                .average()
                .orElse(0.0);

        TimeEstimation totalManualConversionEstimation = projectSummaries.stream()
                .map(ProjectSummary::manualConversionEstimation)
                .reduce(new TimeEstimation(0, 0, 0), TimeEstimation::sum);
        
        TimeEstimation totalValidationEstimation = projectSummaries.stream()
                .map(ProjectSummary::validationEstimation)
                .reduce(new TimeEstimation(0, 0, 0), TimeEstimation::sum);
        
        TimeEstimation totalEstimation = TimeEstimation.sum(totalManualConversionEstimation, totalValidationEstimation);

        return """
                <div class="summary-container">
                    <h2>Overview</h2>
                    <div class="metrics overview-metrics">
                        <div class="metric overview-metric">
                            <span class="metric-value">%d</span>
                            <span class="metric-label">Projects Analyzed</span>
                        </div>
                        <div class="metric overview-metric" style="justify-content: space-around">
                            <div class="metric-left" style="display: flex; flex-direction: column;">
                                <div>
                                    <span class="metric-value">%.0f%%</span>
                                    <span class="metric-label">Average Automated Migration Coverage</span>
                                </div>
                                <div class="coverage-indicator overview-indicator" style="width: 100%%">
                                    <div class="coverage-bar" data-width="%.0f" data-color="%s"></div>
                                </div>
                            </div>
                            <div class="metric-right">
                                <div class="coverage-breakdown">
                                    <div class="breakdown-row">
                                        <span class="breakdown-label">Total Activities:</span>
                                        <span class="breakdown-value">%d</span>
                                    </div>
                                    <div class="breakdown-row">
                                        <span class="breakdown-label">Migratable Activities:</span>
                                        <span class="breakdown-value">%d</span>
                                    </div>
                                    <div class="breakdown-row">
                                        <span class="breakdown-label">Non-migratable Activities:</span>
                                        <span class="breakdown-value">%d</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="time-estimates-container">
                        <div class="time-estimates overview-time-estimates" style="display: flex">
                            <table>
                                <tr>
                                    <th>Work Type</th>
                                    <th>Best Case</th>
                                    <th>Average Case</th>
                                    <th>Worst Case</th>
                                </tr>
                                <tr>
                                    <td><strong>Manual Conversion</strong></td>
                                    <td class="time-best">%s</td>
                                    <td class="time-avg">%s</td>
                                    <td class="time-worst">%s</td>
                                </tr>
                                <tr>
                                    <td><strong>Code Validation</strong></td>
                                    <td class="time-best">%s</td>
                                    <td class="time-avg">%s</td>
                                    <td class="time-worst">%s</td>
                                </tr>
                                <tr style="border-top: 2px solid #4682B4;">
                                    <td><strong>Total</strong></td>
                                    <td class="time-best"><strong>%s</strong></td>
                                    <td class="time-avg"><strong>%s</strong></td>
                                    <td class="time-worst"><strong>%s</strong></td>
                                </tr>
                            </table>
                        </div>
                    </div>

                    <div class="estimation-notes">
                        <p><strong>Note:</strong></p>
                        <ul>
                            <li>%d TIBCO BW projects analyzed for migration to Ballerina</li>
                            <li>%.0f%% average automated conversion rate across all projects</li>
                            <li>Time estimates shown above represents manual work required to complete migration for all projects combined</li>
                        </ul>
                    </div>

                    <div class="estimation-notes">
                        <p><strong>Estimation Scenarios:</strong> Time measurement: 1 day = 8 hours, 5 working days = 1 week</p>
                        <ul>
                            <li>Best case scenario:
                              <ul>
                                <li>1.0 day per each new unsupported activity for analysis, implementation, and testing</li>
                                <li>1.0 hour per each repeated unsupported activity for implementation</li>
                                <li>2 minutes per each line of code generated</li>
                                <li>Assumes minimal complexity and straightforward implementations</li>
                              </ul>
                            </li>
                            <li>Average case scenario:
                              <ul>
                                <li>2.0 days per each new unsupported activity for analysis, implementation, and testing</li>
                                <li>2.0 hour per each repeated unsupported activity for implementation</li>
                                <li>5 minutes per each line of code generated</li>
                                <li>Assumes medium complexity with moderate implementation challenges</li>
                              </ul>
                            </li>
                            <li>Worst case scenario:
                              <ul>
                                <li>3.0 days per each new unsupported activity for analysis, implementation, and testing</li>
                                <li>4.0 hour per each repeated unsupported activity for implementation</li>
                                <li>10 minutes per each line of code generated</li>
                                <li>Assumes high complexity with significant implementation challenges</li>
                              </ul>
                            </li>
                        </ul>
                    </div>
                </div>
                """
                .formatted(
                        totalProjects,
                        averageConversionPercentage,
                        averageConversionPercentage,
                        averageConversionPercentage >= 90 ? "#4CAF50"
                                : averageConversionPercentage >= 70 ? "#FF9800" : "#F44336",
                        totalActivities,
                        totalActivities - totalUnhandledActivities,
                        totalUnhandledActivities,
                        ReportUtils.toDays(totalManualConversionEstimation.bestCaseDaysAsInt()),
                        ReportUtils.toDays(totalManualConversionEstimation.averageCaseDaysAsInt()),
                        ReportUtils.toDays(totalManualConversionEstimation.worstCaseDaysAsInt()),
                        ReportUtils.toDays(totalValidationEstimation.bestCaseDaysAsInt()),
                        ReportUtils.toDays(totalValidationEstimation.averageCaseDaysAsInt()),
                        ReportUtils.toDays(totalValidationEstimation.worstCaseDaysAsInt()),
                        ReportUtils.toDays(totalEstimation.bestCaseDaysAsInt()),
                        ReportUtils.toDays(totalEstimation.averageCaseDaysAsInt()),
                        ReportUtils.toDays(totalEstimation.worstCaseDaysAsInt()),
                        totalProjects, averageConversionPercentage);
    }

    private String generateProjectSummaries() {
        StringBuilder projectCards = new StringBuilder();
        
        for (ProjectSummary project : projectSummaries) {
            projectCards.append(generateProjectCard(project));
        }

        return """
                <div class="projects-container">
                %s
                </div>
                """.formatted(projectCards.toString());
    }
    
    private String generateProjectCard(ProjectSummary project) {
        double coveragePercentage = project.successfulConversionPercentage();
        String statusClass = coveragePercentage >= 90 ? "status-high" :
                coveragePercentage >= 70 ? "status-medium" : "status-low";
        String statusText = coveragePercentage >= 90 ? "High Coverage" :
                coveragePercentage >= 70 ? "Medium Coverage" : "Low Coverage";
        int totalActivityCount = project.totalActivityCount();
        int unhandledActivityCount = project.unhandledActivityCount();
        int convertedActivityCount = totalActivityCount - unhandledActivityCount;
        TimeEstimation timeEstimation =
                TimeEstimation.sum(project.manualConversionEstimation(), project.validationEstimation());

        return """
                <div class="project-card" style="display: flex; flex-direction: column; align-items: stretch;">
                    <div class="project-header">
                        <div class="project-name">
                            <a href="%s" class="project-link">%s</a>
                        </div>
                        <span class="status-badge %s">%s</span>
                    </div>
                    <div style="display: flex; flex-direction: row; justify-content: space-between; align-items: center; margin-right: 2%%;">
                        <div class="project-left" style="flex: 2;">
                            <div class="project-details">
                                <div class="project-metrics">
                                    <div class="metric">
                                        <div class="metric-left">
                                            <span class="metric-value">%.0f%%</span>
                                            <span class="metric-label">Automated Coverage</span>
                                            <div class="coverage-indicator">
                                                <div class="coverage-bar" style="width: %.0f%%; background-color: %s;"></div>
                                            </div>
                                        </div>
                                        <div class="metric-right">
                                            <div class="coverage-breakdown">
                                                <div>
                                                    <span class="breakdown-label">Total Activities:</span>
                                                    <span class="breakdown-value">%d</span>
                                                </div>
                                                <div>
                                                    <span class="breakdown-label">Migratable Activities:</span>
                                                    <span class="breakdown-value">%d</span>
                                                </div>
                                                <div>
                                                    <span class="breakdown-label">Non-migratable Activities:</span>
                                                    <span class="breakdown-value">%d</span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="time-estimates" style="flex: 1; margin-left: 2%%;">
                            <div class="time-estimate best-case">
                                <div class="time-label">Best Case</div>
                                <div class="time-value time-best">
                                    <span class="time-days">%dd</span>
                                    <span class="time-weeks">(~%dw)</span>
                                </div>
                            </div>
                            <div class="time-estimate avg-case">
                                <div class="time-label">Average Case</div>
                                <div class="time-value time-avg">
                                    <span class="time-days">%dd</span>
                                    <span class="time-weeks">(~%dw)</span>
                                </div>
                            </div>
                            <div class="time-estimate worst-case">
                                <div class="time-label">Worst Case</div>
                                <div class="time-value time-worst">
                                    <span class="time-days">%dd</span>
                                    <span class="time-weeks">(~%dw)</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                """.formatted(
                project.reportPath(),
                project.projectName(),
                statusClass,
                statusText,
                coveragePercentage,
                coveragePercentage,
                coveragePercentage >= 90 ? "#4CAF50" : coveragePercentage >= 70 ? "#FF9800" : "#F44336",
                totalActivityCount,
                convertedActivityCount,
                unhandledActivityCount,
                timeEstimation.bestCaseDaysAsInt(),
                timeEstimation.bestCaseWeeks(),
                timeEstimation.averageCaseDaysAsInt(),
                timeEstimation.averageCaseWeeks(),
                timeEstimation.worstCaseDaysAsInt(),
                timeEstimation.worstCaseWeeks()
        );
    }

    private String generateOverallTimeEstimation() {
        Map<String, Integer> allUnsupportedTypes = new HashMap<>();
        Map<String, List<String>> projectsAffected = new HashMap<>();

        for (ProjectSummary project : projectSummaries) {
            for (Map.Entry<String, Collection<AnalysisReport.UnhandledElement>> entry : project
                    .unhandledActivities() == null
                            ? Collections.<String, Collection<AnalysisReport.UnhandledElement>>emptyMap().entrySet()
                            : project.unhandledActivities().entrySet()) {
                String elementType = entry.getKey();
                int frequency = entry.getValue().size();
                allUnsupportedTypes.merge(elementType, frequency, Integer::sum);
                projectsAffected.computeIfAbsent(elementType, k -> new ArrayList<>())
                        .add(project.projectName());
            }
        }

        if (allUnsupportedTypes.isEmpty()) {
            return generateEmptyUnsupportedElementsSection();
        } else {
            return generateUnsupportedElementsTable(allUnsupportedTypes, projectsAffected);
        }
    }
    
    private String generateEmptyUnsupportedElementsSection() {
        return """
                <div class="summary-container">
                    <h2>Currently Unsupported Elements</h2>
                    <div id="toolSupportSection">
                        <p class="empty-message" style="text-align: center; padding: 20px; color: #666;">
                            No unsupported elements found
                        </p>
                        <div class="estimation-notes">
                            <p><strong>Note:</strong> All elements in the analyzed projects are currently supported by the migration tool.</p>
                        </div>
                    </div>
                </div>
                """;
    }
    
    private String generateUnsupportedElementsTable(Map<String, Integer> allUnsupportedTypes, 
                                                   Map<String, List<String>> projectsAffected) {
        StringBuilder tableRows = new StringBuilder();
        
        allUnsupportedTypes.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey()))
                .forEach(entry -> {
                    String elementType = entry.getKey();
                    int frequency = entry.getValue();
                    List<String> affectedProjects = projectsAffected.get(elementType);
                    String projectsList = String.join(", ", affectedProjects);

                    tableRows.append("""
                            <tr>
                                <td>%s</td>
                                <td>%d</td>
                                <td>%s</td>
                            </tr>
                            """.formatted(ReportUtils.escapeHtml(elementType), frequency, ReportUtils.escapeHtml(projectsList)));
                });

        return """
                <div class="summary-container">
                    <h2>Currently Unsupported Elements</h2>
                    <div id="toolSupportSection">
                        <table>
                            <tr>
                                <th>Element Type</th>
                                <th>Frequency</th>
                                <th>Projects Affected</th>
                            </tr>
                            %s
                        </table>
                        <div class="estimation-notes">
                            <p><strong>Note:</strong> These elements are expected to be supported in future versions of the migration tool.</p>
                        </div>
                    </div>
                </div>
                """.formatted(tableRows.toString());
    }

}
