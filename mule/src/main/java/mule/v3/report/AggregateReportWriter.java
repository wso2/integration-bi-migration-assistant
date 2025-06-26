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
package mule.v3.report;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static mule.v3.MuleMigrationExecutor.logger;

/**
 * Utility class to generate and write an aggregate migration report.
 *
 * @since 1.1.1
 */
public class AggregateReportWriter {

    public static final String AGGREGATE_MIGRATION_REPORT_NAME = "aggregate_migration_report.html";
    public static final String MIGRATION_SUMMARY_TITLE = "Aggregate Migration Summary";
    public static final String MIGRATION_ASSESSMENT_TITLE = "Aggregate Migration Assessment";

    public static void genAndWriteAggregateReport(List<ProjectMigrationSummary> projectSummaries,
                                                  Path convertedProjectsDir, boolean dryRun) {
        Path reportFilePath = convertedProjectsDir.resolve(AGGREGATE_MIGRATION_REPORT_NAME);
        String reportTitle = dryRun ? MIGRATION_ASSESSMENT_TITLE : MIGRATION_SUMMARY_TITLE;
        try {
            String reportContent = generateReport(projectSummaries, reportTitle, convertedProjectsDir);
            Files.writeString(reportFilePath, reportContent);
            logger().info("'%s' report written to %s".formatted(reportTitle, reportFilePath));
        } catch (IOException e) {
            logger().severe("Error writing aggregate migration report to file: " + e.getMessage());
        }
    }

    private static String generateReport(List<ProjectMigrationSummary> projectSummaries, String reportTitle,
                                         Path convertedProjectsDir) {
        double avgCoverage = projectSummaries.stream()
                .mapToDouble(ProjectMigrationSummary::migrationCoverage)
                .average()
                .orElse(0.0);

        int totalDistinctFailedElements = (int) projectSummaries.stream()
                .flatMap(ps -> ps.failedXMLTags().keySet().stream())
                .distinct()
                .count();

        int totalFailedDWExpressions = projectSummaries.stream()
                .mapToInt(ProjectMigrationSummary::failedDWExprCount)
                .sum();

        double totalBestCaseDays = projectSummaries.stream()
                .mapToDouble(ProjectMigrationSummary::bestCaseDays)
                .sum();

        double totalAverageCaseDays = projectSummaries.stream()
                .mapToDouble(ProjectMigrationSummary::averageCaseDays)
                .sum();

        double totalWorstCaseDays = projectSummaries.stream()
                .mapToDouble(ProjectMigrationSummary::worstCaseDays)
                .sum();

        return String.format(
                AggregateReportTemplate.getHtmlTemplate(),
                reportTitle,
                reportTitle,
                projectSummaries.size(),
                avgCoverage,
                totalDistinctFailedElements,
                totalFailedDWExpressions,
                generateProjectCards(projectSummaries, convertedProjectsDir),
                generateFailedElementsRows(projectSummaries),
                generateProjectRows(projectSummaries),
                totalBestCaseDays,
                totalAverageCaseDays,
                totalWorstCaseDays
        );
    }

    private static String generateProjectCards(List<ProjectMigrationSummary> projectSummaries,
                                               Path convertedProjectsDir) {
        StringBuilder html = new StringBuilder();

        for (ProjectMigrationSummary projectSummary : projectSummaries) {
            String statusClass = projectSummary.migrationCoverage() >= 75 ? "status-high" :
                    projectSummary.migrationCoverage() >= 50 ? "status-medium" : "status-low";
            String statusText = projectSummary.migrationCoverage() >= 75 ? "High Coverage" :
                    projectSummary.migrationCoverage() >= 50 ? "Medium Coverage" : "Low Coverage";

            Path relativePath = convertedProjectsDir.relativize(projectSummary.reportFilePath());
//            String relativePath = projectSummary.reportFilePath().toString();

            html.append("      <div class=\"project-card\">\n");
            html.append("        <div>\n");
            html.append("          <div class=\"project-name\">\n");
            html.append("            <a href=\"").append(relativePath).append("\" class=\"project-link\">")
                    .append(projectSummary.sourceProjectName()).append("</a>\n");
            html.append("          </div>\n");
            html.append("          <div class=\"metrics\">\n");
            html.append("            <div class=\"metric\">\n");
            html.append("              <span class=\"metric-value\">").append(String.format("%d%%", projectSummary.migrationCoverage())).append("</span>\n");
            html.append("              <span class=\"metric-label\">Coverage</span>\n");
            html.append("            </div>\n");
            html.append("            <div class=\"metric\">\n");
            html.append("              <span class=\"metric-value\">").append(String.format("%.1fd", projectSummary.bestCaseDays())).append("</span>\n");
            html.append("              <span class=\"metric-label\">Best Case</span>\n");
            html.append("            </div>\n");
            html.append("            <div class=\"metric\">\n");
            html.append("              <span class=\"metric-value\">").append(projectSummary.failedDWExprCount()).append("</span>\n");
            html.append("              <span class=\"metric-label\">Failed DataWeave</span>\n");
            html.append("            </div>\n");
            html.append("            <div class=\"metric\">\n");
            html.append("              <span class=\"metric-value\">").append(projectSummary.failedDistinctXMLTagCount()).append("</span>\n");
            html.append("              <span class=\"metric-label\">Failed XML Elements</span>\n");
            html.append("            </div>\n");
            html.append("          </div>\n");
            html.append("        </div>\n");
            html.append("        <div style=\"display: flex; flex-direction: column; align-items: center;\">\n");
            html.append("          <div class=\"coverage-indicator\">\n");
            html.append("            <div class=\"coverage-bar\" style=\"width: ").append(projectSummary.migrationCoverage()).append("%\"></div>\n");
            html.append("          </div>\n");
            html.append("          <span class=\"status-badge ").append(statusClass).append("\">").append(statusText).append("</span>\n");
            html.append("        </div>\n");
            html.append("      </div>\n\n");
        }

        return html.toString();
    }

    private static String generateProjectRows(List<ProjectMigrationSummary> projectSummaries) {
        StringBuilder html = new StringBuilder();

        for (ProjectMigrationSummary project : projectSummaries) {
            html.append("        <tr>\n");
            html.append("          <td>").append(project.sourceProjectName()).append("</td>\n");
            html.append("          <td>").append(String.format("%.1f days", project.bestCaseDays())).append("</td>\n");
            html.append("          <td>").append(String.format("%.1f days", project.averageCaseDays())).append("</td>\n");
            html.append("          <td>").append(String.format("%.1f days", project.worstCaseDays())).append("</td>\n");
            html.append("        </tr>\n");
        }

        return html.toString();
    }

    private static String generateFailedElementsRows(List<ProjectMigrationSummary> projectSummaries) {
        StringBuilder html = new StringBuilder();

        // Create a map to collect all failed XML tags across all projects
        // Key: XML tag name, Value: Map<Project name, Count>
        var elementFrequencyMap = new java.util.LinkedHashMap<String, java.util.Map<String, Integer>>();

        // Populate the map with data from all projects
        for (ProjectMigrationSummary project : projectSummaries) {
            for (var entry : project.failedXMLTags().entrySet()) {
                String elementType = entry.getKey();
                Integer count = entry.getValue();

                elementFrequencyMap.computeIfAbsent(elementType, k -> new java.util.LinkedHashMap<>())
                        .put(project.sourceProjectName(), count);
            }
        }

        // Sort elements by total frequency (descending)
        var sortedElements = elementFrequencyMap.entrySet().stream()
                .sorted((e1, e2) -> {
                    int total1 = e1.getValue().values().stream().mapToInt(Integer::intValue).sum();
                    int total2 = e2.getValue().values().stream().mapToInt(Integer::intValue).sum();
                    return Integer.compare(total2, total1); // Descending order
                })
                .toList();

        // Generate HTML rows
        for (var entry : sortedElements) {
            String elementType = entry.getKey();
            var projectCounts = entry.getValue();
            int totalFrequency = projectCounts.values().stream().mapToInt(Integer::intValue).sum();
            String affectedProjects = String.join(", ", projectCounts.keySet());

            html.append("        <tr>\n");
            html.append("          <td>").append(elementType).append("</td>\n");
            html.append("          <td>").append(totalFrequency).append("</td>\n");
            html.append("          <td>").append(affectedProjects).append("</td>\n");
            html.append("        </tr>\n");
        }

        return html.toString();
    }
}
