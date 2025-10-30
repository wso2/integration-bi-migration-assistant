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
package mule.common.report;

import mule.common.MuleLogger;
import mule.common.ProjectMigrationResult;

import java.nio.file.Path;
import java.util.List;

import static mule.common.report.IndividualReportGenerator.AVG_CASE_COMP_TIME_NEW;
import static mule.common.report.IndividualReportGenerator.AVG_CASE_COMP_TIME_REPEATED;
import static mule.common.report.IndividualReportGenerator.AVG_CASE_DW_EXPR_TIME;
import static mule.common.report.IndividualReportGenerator.INDIVIDUAL_REPORT_NAME;

/**
 * Utility class to generate and write an aggregate migration report.
 *
 * @since 1.1.1
 */
public class AggregateReportGenerator {

    public static final String AGGREGATE_MIGRATION_REPORT_NAME = "aggregate_migration_report.html";
    public static final String MIGRATION_SUMMARY_TITLE = "Aggregate Migration Summary";
    public static final String MIGRATION_ASSESSMENT_TITLE = "Aggregate Migration Assessment";

    public static String generateHtmlReport(MuleLogger logger, List<ProjectMigrationResult> projectResults,
                                            Path convertedProjectsDir, boolean dryRun) {
        logger.logState("Generating aggregate migration report...");
        double avgCoverage = projectResults.stream().map(ProjectMigrationResult::getMigrationStats)
                .mapToDouble(ProjectMigrationStats::migrationCoverage)
                .average().orElse(0.0);

        double totalAverageCaseDays = projectResults.stream().map(ProjectMigrationResult::getMigrationStats)
                .mapToDouble(ProjectMigrationStats::averageCaseDays)
                .sum();

        // Calculate total items across all projects
        int totalElements = projectResults.stream().map(ProjectMigrationResult::getMigrationStats)
                .mapToInt(AggregateReportGenerator::calculateTotalXmlElements)
                .sum();

        int totalDWExpressions = projectResults.stream().map(ProjectMigrationResult::getMigrationStats)
                .mapToInt(ps -> ps.dwConversionStats().getTotalEncounteredCount())
                .sum();


        int totalItems = totalElements + totalDWExpressions;


        int migratableElements = projectResults.stream().map(ProjectMigrationResult::getMigrationStats)
                .mapToInt(AggregateReportGenerator::calculateMigratableXmlElements)
                .sum();
        int migratableDWExpressions = projectResults.stream().map(ProjectMigrationResult::getMigrationStats)
                .mapToInt(ps -> ps.dwConversionStats().getConvertedCount())
                .sum();

        int migratableItems = migratableElements + migratableDWExpressions;
        int nonMigratableItems = totalItems - migratableItems;


        // Get appropriate color based on coverage
        String barColor = avgCoverage >= 75 ? "#4CAF50" :  // Green for high
                          avgCoverage >= 50 ? "#FFC107" :  // Amber for medium
                          "#F44336";                       // Red for low

        String reportTitle = dryRun ? MIGRATION_ASSESSMENT_TITLE : MIGRATION_SUMMARY_TITLE;
        logger.logInfo("Formating aggregate migration report...");
        return String.format(
                AggregateReportTemplate.getHtmlTemplate(),
                reportTitle,                                     // %s - title
                reportTitle,                                     // %s - title again
                projectResults.size(),                         // %d - project count
                avgCoverage,                                     // %.0f - avg coverage
                avgCoverage,                                     // %.0f - avg coverage for width
                barColor,                                        // %s - color for coverage bar
                totalItems,                                      // %d - total items
                migratableItems,                                 // %d - migratable items
                        nonMigratableItems, // %d - non-migratable items
                        totalAverageCaseDays, // %.1f - average case days
                projectResults.size(),                         // %d - project count again
                        avgCoverage, // %.0f - avg coverage again
                        AVG_CASE_COMP_TIME_NEW,
                        AVG_CASE_COMP_TIME_REPEATED * 8,
                        AVG_CASE_DW_EXPR_TIME * 8 * 60,
                        generateProjectCards(projectResults, convertedProjectsDir),
                // html
                generateFailedElementsRows(projectResults)      // %s - failed elements rows html
        );
    }

    private static String generateProjectCards(List<ProjectMigrationResult> projectResults,
                                               Path convertedProjectsDir) {
        StringBuilder html = new StringBuilder();

        for (ProjectMigrationResult result : projectResults) {
            ProjectMigrationStats stats = result.getMigrationStats();
            String statusClass = stats.migrationCoverage() >= 75 ? "status-high" :
                    stats.migrationCoverage() >= 50 ? "status-medium" : "status-low";
            String statusText = stats.migrationCoverage() >= 75 ? "High Coverage" :
                    stats.migrationCoverage() >= 50 ? "Medium Coverage" : "Low Coverage";

            Path reportPath = result.getTargetPath().resolve(result.getProjectName()).resolve(INDIVIDUAL_REPORT_NAME);
            Path relativeReportPath = convertedProjectsDir.relativize(reportPath);

            // Get the appropriate color for the coverage bar
            String barColor = stats.migrationCoverage() >= 75 ? "#4CAF50" : // Green for high
                    stats.migrationCoverage() >= 50 ? "#FFC107" :            // Amber for medium
                   "#F44336";                                                        // Red for low

            int totalItems = calculateTotalXmlElements(stats) +
                    stats.dwConversionStats().getTotalEncounteredCount();
            int migratableItems = calculateMigratableXmlElements(stats) +
                    stats.dwConversionStats().getConvertedCount();
            int nonMigratableItems = totalItems - migratableItems;

            html.append("    <div class=\"project-card\">\n");
            html.append("      <div class=\"project-left\">\n");
            html.append("        <div class=\"project-header\">\n");
            html.append("          <div class=\"project-name\">\n");
            html.append("            <a href=\"").append(relativeReportPath).append("\" class=\"project-link\">")
                    .append(result.getSourceName()).append("</a>\n");
            html.append("          </div>\n");
            html.append("          <span class=\"status-badge ").append(statusClass).append("\">").append(statusText)
                    .append("</span>\n");
            html.append("        </div>\n\n");

            html.append("        <div class=\"project-details\">\n");
            html.append("          <div class=\"project-metrics\">\n");
            html.append("            <div class=\"metric\">\n");
            html.append("              <div class=\"metric-left\">\n");
            html.append("                <span class=\"metric-value\">")
                    .append(String.format("%d%%", stats.migrationCoverage())).append("</span>\n");
            html.append("                <span class=\"metric-label\">Automated Coverage</span>\n");
            html.append("                <div class=\"coverage-indicator\">\n");
            html.append("                  <div class=\"coverage-bar\" style=\"width: ")
                    .append(stats.migrationCoverage()).append("%; background-color: ").append(barColor)
                    .append(";\"></div>\n");
            html.append("                </div>\n");
            html.append("              </div>\n");
            html.append("              <div class=\"metric-right\">\n");
            html.append("                <div class=\"coverage-breakdown\">\n");
            html.append("                  <div>\n");
            html.append("                    <span class=\"breakdown-label\">Total Code Lines:</span>\n");
            html.append("                    <span class=\"breakdown-value\">").append(totalItems).append("</span>\n");
            html.append("                  </div>\n");
            html.append("                  <div>\n");
            html.append("                    <span class=\"breakdown-label\">Migratable Code Lines:</span>\n");
            html.append("                    <span class=\"breakdown-value\">").append(migratableItems)
                    .append("</span>\n");
            html.append("                  </div>\n");
            html.append("                  <div>\n");
            html.append("                    <span class=\"breakdown-label\">Non-migratable Code Lines:</span>\n");
            html.append("                    <span class=\"breakdown-value\">").append(nonMigratableItems)
                    .append("</span>\n");
            html.append("                  </div>\n");
            html.append("                </div>\n");
            html.append("              </div>\n");
            html.append("            </div>\n");
            html.append("          </div>\n");
            html.append("        </div>\n");
            html.append("      </div>\n\n");

            html.append("      <div class=\"time-estimates\">\n");
            html.append("        <div class=\"time-estimate best-case\">\n");
            html.append("          <div class=\"time-label\">Best Case</div>\n");
            html.append("          <div class=\"time-value time-best\">\n");
            html.append("            <span class=\"time-days\">").append(String.format("%.1fd", stats.bestCaseDays()))
                    .append("</span>\n");
            html.append("            <span class=\"time-weeks\">(~")
                    .append(String.format("%.1fw", stats.bestCaseDays() / 5.0)).append(")</span>\n");
            html.append("          </div>\n");
            html.append("        </div>\n");
            html.append("        <div class=\"time-estimate avg-case\">\n");
            html.append("          <div class=\"time-label\">Average Case</div>\n");
            html.append("          <div class=\"time-value time-avg\">\n");
            html.append("            <span class=\"time-days\">")
                    .append(String.format("%.1fd", stats.averageCaseDays())).append("</span>\n");
            html.append("            <span class=\"time-weeks\">(~")
                    .append(String.format("%.1fw", stats.averageCaseDays() / 5.0)).append(")</span>\n");
            html.append("          </div>\n");
            html.append("        </div>\n");
            html.append("        <div class=\"time-estimate worst-case\">\n");
            html.append("          <div class=\"time-label\">Worst Case</div>\n");
            html.append("          <div class=\"time-value time-worst\">\n");
            html.append("            <span class=\"time-days\">").append(String.format("%.1fd", stats.worstCaseDays()))
                    .append("</span>\n");
            html.append("            <span class=\"time-weeks\">(~")
                    .append(String.format("%.1fw", stats.worstCaseDays() / 5.0)).append(")</span>\n");
            html.append("          </div>\n");
            html.append("        </div>\n");
            html.append("      </div>\n");
            html.append("    </div>\n\n");
        }

        return html.toString();
    }

    private static String generateFailedElementsRows(List<ProjectMigrationResult> projectResults) {
        StringBuilder html = new StringBuilder();

        // Create a map to collect all failed XML tags across all projects
        // Key: XML tag name, Value: Map<Project name, Count>
        var elementFrequencyMap = new java.util.LinkedHashMap<String, java.util.Map<String, Integer>>();

        // Populate the map with data from all projects
        for (ProjectMigrationResult result : projectResults) {
            for (var entry : result.getMigrationStats().failedXMLTags().entrySet()) {
                String elementType = entry.getKey();
                Integer count = entry.getValue();

                elementFrequencyMap.computeIfAbsent(elementType, k -> new java.util.LinkedHashMap<>())
                        .put(result.getSourceName(), count);
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

            // Generate HTML list for affected projects
            StringBuilder projectsList = new StringBuilder("<ul style=\"margin: 0; padding-left: 20px;\">");
            for (String projectName : projectCounts.keySet()) {
                projectsList.append("<li>").append(projectName).append("</li>");
            }
            projectsList.append("</ul>");

            html.append("        <tr>\n");
            html.append("          <td>").append(elementType).append("</td>\n");
            html.append("          <td>").append(totalFrequency).append("</td>\n");
            html.append("          <td>").append(projectsList).append("</td>\n");
            html.append("        </tr>\n");
        }

        return html.toString();
    }

    private static int calculateTotalXmlElements(ProjectMigrationStats pms) {
        int passedCount = pms.passedXMLTags().values().stream().mapToInt(i -> i).sum();
        int failedCount = pms.failedXMLTags().values().stream().mapToInt(i -> i).sum();
        return passedCount + failedCount;
    }

    private static int calculateMigratableXmlElements(ProjectMigrationStats pms) {
        return pms.passedXMLTags().values().stream().mapToInt(i -> i).sum();
    }
}
