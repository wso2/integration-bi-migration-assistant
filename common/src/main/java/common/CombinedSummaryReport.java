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

/**
 * A class to generate combined summary reports that link to individual project reports.
 */
public class CombinedSummaryReport {
    private final String reportTitle;
    private final List<ProjectSummary> projectSummaries;

    /**
     * Create a new combined summary report.
     *
     * @param reportTitle        The title of the combined report
     * @param projectSummaries   List of project summaries to include in the report
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

        // Start HTML document
        html.append("""
                <!DOCTYPE html>
                <html>
                <head>
                    <title>%s</title>
                    <style>
                        body { font-family: Arial, sans-serif; background-color: #f4f4f9; color: #333; }
                        table { width: 100%%; border-collapse: collapse; margin: 20px 0; }
                        th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }
                        th { background-color: #4682B4; color: white; }
                        tr:nth-child(even) { background-color: #e0f0ff; }
                        tr:hover { background-color: #b0d4f1; }
                        h1 { text-align: center; color: #4682B4; }
                        h2 { color: #4682B4; }
                        footer { text-align: center; margin-top: 20px; font-size: 0.9em; color: #666; }
                        .summary-container { background-color: #fff; padding: 20px; border-radius: 8px;
                            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); margin: 20px 0; }
                        .blue-table th { background-color: #4682B4; color: white; }
                        .blue-table tr:nth-child(even) { background-color: #e0f0ff; }
                        .blue-table tr:hover { background-color: #b0d4f1; }
                        .project-link { color: #4682B4; text-decoration: none; font-weight: bold; }
                        .project-link:hover { text-decoration: underline; }
                        .overall-stats { background-color: #f0f8ff; padding: 15px; border-radius: 5px;
                            border-left: 4px solid #4682B4; margin: 20px 0; }
                        .stat-value { font-size: 1.2em; font-weight: bold; color: #4682B4; }
                        .center-text { text-align: center; }
                        .no-projects { text-align: center; color: #666; font-style: italic; }
                    </style>
                </head>
                <body>
                    <h1>%s</h1>
                """.formatted(reportTitle, reportTitle));

        if (projectSummaries.isEmpty()) {
            html.append("""
                    <div class="summary-container">
                        <div class="no-projects">No projects found for analysis.</div>
                    </div>
                    """);
        } else {
            // Overall statistics
            appendOverallStatistics(html);

            // Project summaries table
            appendProjectSummariesTable(html);

            // Overall time estimation
            appendOverallTimeEstimation(html);
        }

        // Footer with date
        html.append("""
                    <footer>
                        <p>Combined Summary Report generated on: <span id="datetime"></span></p>
                    </footer>
                    <script>
                        document.getElementById("datetime").innerHTML = new Date().toLocaleString();
                    </script>
                </body>
                </html>
                """);

        return html.toString();
    }

    private void appendOverallStatistics(StringBuilder html) {
        int totalProjects = projectSummaries.size();
        int totalElements = projectSummaries.stream()
                .mapToInt(p -> p.activityEstimation().totalActivityCount())
                .sum();
        int totalUnhandledElements = projectSummaries.stream()
                .mapToInt(p -> p.activityEstimation().unhandledActivityCount())
                .sum();
        double averageConversionPercentage = projectSummaries.stream()
                .mapToDouble(ProjectSummary::successfulConversionPercentage)
                .average()
                .orElse(0.0);

        html.append("""
                <div class="overall-stats">
                    <h2>Overall Statistics</h2>
                    <div style="display: flex; justify-content: space-around; text-align: center;">
                        <div>
                            <div class="stat-value">%d</div>
                            <div>Projects Analyzed</div>
                        </div>
                        <div>
                            <div class="stat-value">%d</div>
                            <div>Total Elements</div>
                        </div>
                        <div>
                            <div class="stat-value">%d</div>
                            <div>Unhandled Elements</div>
                        </div>
                        <div>
                            <div class="stat-value">%.1f%%</div>
                            <div>Average Conversion</div>
                        </div>
                    </div>
                </div>
                """.formatted(totalProjects, totalElements, totalUnhandledElements, averageConversionPercentage));
    }

    private void appendProjectSummariesTable(StringBuilder html) {
        html.append("""
                <div class="summary-container">
                    <h2>Project Summaries</h2>
                    <table class="blue-table">
                        <tr>
                            <th>Project Name</th>
                            <th>Total Elements</th>
                            <th>Unhandled Elements</th>
                            <th>Conversion Success Rate</th>
                            <th>Time Estimation (Days)</th>
                            <th>Detailed Report</th>
                        </tr>
                """);

        for (ProjectSummary project : projectSummaries) {
            html.append("""
                            <tr>
                                <td><strong>%s</strong></td>
                                <td class="center-text">%d</td>
                                <td class="center-text">%d</td>
                                <td class="center-text">%.1f%%</td>
                                <td class="center-text">%d - %d</td>
                                <td class="center-text">
                                    <a href="%s" class="project-link">View Report</a>
                                </td>
                            </tr>
                    """.formatted(
                    project.projectName(),
                    project.activityEstimation().totalActivityCount(),
                            project.activityEstimation().unhandledActivityCount(),
                    project.successfulConversionPercentage(),
                    project.activityEstimation().timeEstimation().bestCaseDays(),
                            project.activityEstimation().timeEstimation().worstCaseDays(),
                    project.reportPath()
            ));
        }

        html.append("""
                        </table>
                    </div>
                """);
    }

    private void appendOverallTimeEstimation(StringBuilder html) {
        int totalBestCaseDays = projectSummaries.stream()
                .mapToInt(p -> p.activityEstimation().timeEstimation().bestCaseDays())
                .sum();
        int totalAverageCaseDays = projectSummaries.stream()
                .mapToInt(p -> p.activityEstimation().timeEstimation().averageCaseDays())
                .sum();
        int totalWorstCaseDays = projectSummaries.stream()
                .mapToInt(p -> p.activityEstimation().timeEstimation().worstCaseDays())
                .sum();

        int totalBestCaseWeeks = (int) Math.ceil(totalBestCaseDays / 5.0);
        int totalAverageCaseWeeks = (int) Math.ceil(totalAverageCaseDays / 5.0);
        int totalWorstCaseWeeks = (int) Math.ceil(totalWorstCaseDays / 5.0);

        html.append("""
                <div class="summary-container">
                    <h2>Overall Time Estimation</h2>
                    <table class="blue-table">
                        <tr>
                            <th>Scenario</th>
                            <th>Total Days</th>
                            <th>Total Weeks (approx.)</th>
                        </tr>
                        <tr>
                            <td>Best Case</td>
                            <td class="center-text">%s</td>
                            <td class="center-text">%s</td>
                        </tr>
                        <tr>
                            <td>Average Case</td>
                            <td class="center-text">%s</td>
                            <td class="center-text">%s</td>
                        </tr>
                        <tr>
                            <td>Worst Case</td>
                            <td class="center-text">%s</td>
                            <td class="center-text">%s</td>
                        </tr>
                    </table>
                    <div style="margin-top: 15px; padding: 10px; background-color: #f8f9fa; border-radius: 5px;">
                        <p><strong>Note:</strong> Time estimates are cumulative across all projects and assume sequential development.</p>
                    </div>
                </div>
                """.formatted(
                toDays(totalBestCaseDays), toWeeks(totalBestCaseWeeks),
                toDays(totalAverageCaseDays), toWeeks(totalAverageCaseWeeks),
                toDays(totalWorstCaseDays), toWeeks(totalWorstCaseWeeks)
        ));
    }

    /**
     * Format a number as a day string with correct singular/plural form.
     *
     * @param number The number of days
     * @return A string with the number and "day" or "days"
     */
    private String toDays(int number) {
        return number + " " + (number == 1 ? "day" : "days");
    }

    /**
     * Format a number as a week string with correct singular/plural form.
     *
     * @param number The number of weeks
     * @return A string with the number and "week" or "weeks"
     */
    private String toWeeks(int number) {
        return number + " " + (number == 1 ? "week" : "weeks");
    }
}
