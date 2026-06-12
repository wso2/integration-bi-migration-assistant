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

import common.ReportUtils;
import common.TimeEstimation;
import common.report.ReportComponent;
import common.report.Styles;
import mule.MuleMigrator.MuleVersion;
import mule.common.DWConstructBase;
import mule.common.DWConversionStats;
import mule.common.MigrationMetrics;
import mule.common.MuleLogger;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;


public class IndividualReportGenerator {

    // Baseline estimates in minutes for a new element
    public static final double BEST_CASE_COMP_TIME_NEW_MINUTES = 60;
    public static final double AVG_CASE_COMP_TIME_NEW_MINUTES = 120;
    public static final double WORST_CASE_COMP_TIME_NEW_MINUTES = 180;

    // Baseline estimates in minutes for a repeated element
    public static final double BEST_CASE_COMP_TIME_REPEATED_MINUTES = 7.5;
    public static final double AVG_CASE_COMP_TIME_REPEATED_MINUTES = 15;
    public static final double WORST_CASE_COMP_TIME_REPEATED_MINUTES = 30;

    // Baseline estimates in minutes for DataWeave expressions
    public static final double BEST_DW_LINE_TIME_MINUTES = 1;
    public static final double AVG_CASE_DW_LINE_TIME_MINUTES = 1.5;
    public static final double WORST_CASE_DW_LINE_TIME_MINUTES = 2.5;

    public static final String INDIVIDUAL_REPORT_NAME = "migration_report.html";
    public static final String MIGRATION_SUMMARY_TITLE = "Migration Summary";
    public static final String MIGRATION_ASSESSMENT_TITLE = "Migration Assessment";

    public static Map<String, Object> generateJsonReport(ProjectMigrationStats pms) {
        int migrationCoverage = pms.migrationCoverage();
        String coverageStatus = getCoverageStatus(migrationCoverage);

        int totalXmlElements = calculateTotalXmlElements(pms);
        int migratableXmlElements = calculateMigratableXmlElements(pms);
        int nonMigratableXmlElements = totalXmlElements - migratableXmlElements;

        // DataWeave metrics (line-based: converted lines via construct count, failed lines explicitly tracked)
        DWConversionStats<? extends DWConstructBase> dwStats = pms.dwConversionStats();
        int totalDwLines = dwStats.getTotalDWLineCount();
        int migratableDwLines = dwStats.getConvertedDWLineCount();
        int nonMigratableDwLines = dwStats.getFailedDWLineCount();

        // Calculate total items, migratable items, and non-migratable items
        int totalItems = totalXmlElements + totalDwLines;
        int migratableItems = migratableXmlElements + migratableDwLines;
        int nonMigratableItems = nonMigratableXmlElements + nonMigratableDwLines;

        Map<String, Object> coverageOverview = new LinkedHashMap<>();
        coverageOverview.put("unitName", "code lines");
        coverageOverview.put("coveragePercentage", migrationCoverage);
        coverageOverview.put("coverageLevel", coverageStatus);
        coverageOverview.put("totalElements", totalItems);
        coverageOverview.put("migratableElements", migratableItems);
        coverageOverview.put("nonMigratableElements", nonMigratableItems);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("coverageOverview", coverageOverview);
        return result;
    }

    public static String generateHtmlReport(MuleLogger logger, ProjectMigrationStats pms, MuleVersion muleVersion,
                                            boolean dryRun, String sourceProjectName) {
        logger.logState("Generating individual migration report...");
        String unsupportedElementsTable = generateUnsupportedElementsTable(pms.failedXMLTags());
        String unsupportedBlocksHtml = generateUnsupportedBlocksHtml(pms.failedBlocks());
        String dataweaveExpressionsHtml = generateDataweaveExpressionsHtml(pms.dwConversionStats());

        // XML Elements metrics
        int totalXmlElements = calculateTotalXmlElements(pms);
        int migratableXmlElements = calculateMigratableXmlElements(pms);
        int nonMigratableXmlElements = totalXmlElements - migratableXmlElements;

        // Calculate element coverage and DataWeave coverage
        int elementCoverage = calculateElementsCoverage(pms, muleVersion);
        String elementCoverageColor = getCoverageColor(elementCoverage);

        // DataWeave metrics (line-based: converted lines via construct count, failed lines explicitly tracked)
        DWConversionStats<? extends DWConstructBase> dwStats = pms.dwConversionStats();
        int totalDwLines = dwStats.getTotalDWLineCount();
        int migratableDwLines = dwStats.getConvertedDWLineCount();
        int nonMigratableDwLines = dwStats.getFailedDWLineCount();
        int dataweaveCoverage = calculateDataweaveCoverage(pms);
        String dataweaveCoverageColor = getCoverageColor(dataweaveCoverage);

        // Format DataWeave coverage to display N/A when total is zero
        String dataweaveDisplayValue = totalDwLines > 0 ? dataweaveCoverage + "%" : "N/A";
        String dataweaveBarWidth = totalDwLines > 0 ? dataweaveCoverage + "%" : "0%";
        // Calculate total items, migratable items, and non-migratable items
        int totalItems = totalXmlElements + totalDwLines;
        int migratableItems = migratableXmlElements + migratableDwLines;
        int nonMigratableItems = nonMigratableXmlElements + nonMigratableDwLines;

        // Calculate overall coverage metrics
        int migrationCoverage = pms.migrationCoverage();
        String coverageColor = getCoverageColor(migrationCoverage);
        String coverageStatus = getCoverageStatus(migrationCoverage);
        String badgeClass = getCoverageBadgeClass(migrationCoverage);

        String reportTitle = dryRun ? MIGRATION_ASSESSMENT_TITLE : MIGRATION_SUMMARY_TITLE;
        logger.logInfo("Formating individual migration report...");

        // Generate Manual Work Estimation component
        ReportComponent estimationComponent = generateManualWorkEstimationComponent(
                pms.bestCaseDays(), pms.averageCaseDays(), pms.worstCaseDays(),
                BEST_CASE_COMP_TIME_NEW_MINUTES, BEST_CASE_COMP_TIME_REPEATED_MINUTES,
                BEST_DW_LINE_TIME_MINUTES,
                AVG_CASE_COMP_TIME_NEW_MINUTES, AVG_CASE_COMP_TIME_REPEATED_MINUTES,
                AVG_CASE_DW_LINE_TIME_MINUTES,
                WORST_CASE_COMP_TIME_NEW_MINUTES, WORST_CASE_COMP_TIME_REPEATED_MINUTES,
                WORST_CASE_DW_LINE_TIME_MINUTES);

        return String.format(
                IndividualReportTemplate.getHtmlTemplate(),
                reportTitle,
                estimationComponent.styles().toHTML(),
                sourceProjectName,
                reportTitle,
                // Overall coverage section parameters
                migrationCoverage,
                migrationCoverage, coverageColor,
                badgeClass, coverageStatus,
                totalItems, migratableItems, nonMigratableItems,
                // Elements coverage section parameters
                elementCoverage,
                elementCoverage, elementCoverageColor,
                totalXmlElements, migratableXmlElements, nonMigratableXmlElements,
                // DataWeave coverage section parameters
                dataweaveDisplayValue,
                dataweaveBarWidth, dataweaveCoverageColor,
                totalDwLines, migratableDwLines, nonMigratableDwLines,
                // Manual Work Estimation component
                estimationComponent.content(),
                // Content sections
                unsupportedElementsTable,
                unsupportedBlocksHtml,
                dataweaveExpressionsHtml
        );
    }

    private static String getCoverageColor(int coverage) {
        if (coverage >= 75) {
            return "#4CAF50"; // Green for high coverage
        } else if (coverage >= 50) {
            return "#FFC107"; // Yellow/amber for medium coverage
        } else {
            return "#F44336"; // Red for low coverage
        }
    }

    private static String getCoverageStatus(int coverage) {
        if (coverage >= 75) {
            return "High";
        } else if (coverage >= 50) {
            return "Medium";
        } else {
            return "Low";
        }
    }

    private static String getCoverageBadgeClass(int coverage) {
        if (coverage >= 75) {
            return "status-high";
        } else if (coverage >= 50) {
            return "status-medium";
        } else {
            return "status-low";
        }
    }

    public static ProjectMigrationStats getProjectMigrationStats(MuleVersion muleVersion,
                                                                 MigrationMetrics<? extends DWConstructBase> metrics) {
        int failedDWLineCount = metrics.dwConversionStats.getFailedDWLineCount();

        // Calculate implementation times
        double bestCaseDays = calculateBestCaseEstimate(metrics.failedXMLTags, failedDWLineCount);
        double avgCaseDays = calculateAverageCaseEstimate(metrics.failedXMLTags, failedDWLineCount);
        double worstCaseDays = calculateWorstCaseEstimate(metrics.failedXMLTags, failedDWLineCount);

        int migrationCoverage = calculateMigrationCoverage(muleVersion, metrics);
        return new ProjectMigrationStats(metrics.passedXMLTags, metrics.failedXMLTags, metrics.failedBlocks,
                metrics.dwConversionStats, migrationCoverage, bestCaseDays, avgCaseDays, worstCaseDays,
                metrics.failedXMLTags.size(), failedDWLineCount);
    }

    private static int calculateMigrationCoverage(MuleVersion muleVersion,
                                                  MigrationMetrics<? extends DWConstructBase> metrics) {
        int totalPassedWeight = calculateTotalWeight(muleVersion, metrics.passedXMLTags);
        int totalFailedWeight = calculateTotalWeight(muleVersion, metrics.failedXMLTags);
        int dwTotalWeight = metrics.dwConversionStats.getTotalWeight();
        int dwConvertedWeight = metrics.dwConversionStats.getConvertedWeight();

        int combinedTotalWeight = totalPassedWeight + totalFailedWeight + dwTotalWeight;
        int combinedConvertedWeight = totalPassedWeight + dwConvertedWeight;

        return combinedTotalWeight == 0 ? 0 : (combinedConvertedWeight * 100) / combinedTotalWeight;
    }

    private static int calculateTotalWeight(MuleVersion muleVersion, LinkedHashMap<String, Integer> tagMap) {
        // TODO: refactor this
        Function<String, Integer> getWeightFromTag = muleVersion.equals(MuleVersion.MULE_V3) ?
                mule.v3.model.MuleXMLTag::getWeightFromTag : mule.v4.model.MuleXMLTag::getWeightFromTag;
        return tagMap.entrySet().stream()
                .mapToInt(entry -> getWeightFromTag.apply(entry.getKey()) * entry.getValue())
                .sum();
    }

    private static String generateUnsupportedElementsTable(LinkedHashMap<String, Integer> failedTags) {
        StringBuilder sb = new StringBuilder();
        failedTags.forEach((tag, frequency) ->
            sb.append(String.format("<tr><td><code>%s</code></td><td>%d</td></tr>\n", tag, frequency))
        );
        return sb.toString();
    }

    private static String generateUnsupportedBlocksHtml(List<String> failedBlocks) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < failedBlocks.size(); i++) {
            sb.append(String.format("""
                <div class="block-item">
                    <div class="block-header">
                        <span class="block-number">Block #%d</span>
                        <span class="block-type">%s</span>
                    </div>
                    <pre class="block-code"><code>%s</code></pre>
                </div>
                """,
                i + 1, getBlockType(failedBlocks.get(i)), escapeHtml(failedBlocks.get(i))));
        }
        return sb.toString();
    }

    private static String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private static String generateDataweaveExpressionsHtml(DWConversionStats<? extends DWConstructBase> dwStats) {
        StringBuilder sb = new StringBuilder();
        int expressionCount = 1;
        for (Object dwExpr : dwStats.getFailedDWExpressions()) {
            sb.append("<div class=\"block-item\"><div class=\"block-header\">");
            sb.append("<span class=\"block-number\">Expression #").append(expressionCount++).append("</span>");
            sb.append("<span class=\"block-type\">").append("Dataweave Expression").append("</span>");
            sb.append("</div><pre class=\"block-code\">").append(dwExpr).append("</pre></div>");
        }
        return sb.toString();
    }

    private static String getBlockType(String block) {
        if (block.contains("<")) {
            int start = block.indexOf("<") + 1;
            int end = block.indexOf(" ", start);
            if (end == -1) {
                end = block.indexOf(">", start);
            }
            return end != -1 ? block.substring(start, end) : "Unknown";
        }
        return "Unknown";
    }

    private static double calculateBestCaseEstimate(LinkedHashMap<String, Integer> failedXMLTags, int dwLines) {
        double repeatedElementTime = failedXMLTags.values().stream().filter(x -> x > 1).mapToInt(x -> x - 1)
                .mapToDouble(repeatCount -> repeatCount * BEST_CASE_COMP_TIME_REPEATED_MINUTES).sum();
        double totalMinutes = failedXMLTags.size() * BEST_CASE_COMP_TIME_NEW_MINUTES + repeatedElementTime +
                dwLines * BEST_DW_LINE_TIME_MINUTES;
        return totalMinutes / (8 * 60); // Convert minutes to days (8 hours per day, 60 minutes per hour)
    }

    private static double calculateAverageCaseEstimate(LinkedHashMap<String, Integer> failedXMLTags,
                                                       int dwLines) {
        double repeatedElementTime = failedXMLTags.values().stream().filter(x -> x > 1).mapToInt(x -> x - 1)
                .mapToDouble(repeatCount -> repeatCount * AVG_CASE_COMP_TIME_REPEATED_MINUTES).sum();
        double totalMinutes = failedXMLTags.size() * AVG_CASE_COMP_TIME_NEW_MINUTES + repeatedElementTime +
                dwLines * AVG_CASE_DW_LINE_TIME_MINUTES;
        return totalMinutes / (8 * 60); // Convert minutes to days (8 hours per day, 60 minutes per hour)
    }

    private static double calculateWorstCaseEstimate(LinkedHashMap<String, Integer> failedXMLTags, int dwLines) {
        double repeatedElementTime = failedXMLTags.values().stream().filter(x -> x > 1).mapToInt(x -> x - 1)
                .mapToDouble(repeatCount -> repeatCount * WORST_CASE_COMP_TIME_REPEATED_MINUTES).sum();
        double totalMinutes = failedXMLTags.size() * WORST_CASE_COMP_TIME_NEW_MINUTES + repeatedElementTime +
                dwLines * WORST_CASE_DW_LINE_TIME_MINUTES;
        return totalMinutes / (8 * 60); // Convert minutes to days (8 hours per day, 60 minutes per hour)
    }


    private static int calculateElementsCoverage(ProjectMigrationStats pms, MuleVersion muleVersion) {
        int xmlPassedWeight = calculateTotalWeight(muleVersion, pms.passedXMLTags());
        int xmlFailedWeight = calculateTotalWeight(muleVersion, pms.failedXMLTags());
        int totalXmlWeight = xmlPassedWeight + xmlFailedWeight;
        return totalXmlWeight > 0 ? (xmlPassedWeight * 100) / totalXmlWeight : 0;
    }

    private static int calculateDataweaveCoverage(ProjectMigrationStats pms) {
        DWConversionStats<? extends DWConstructBase> stats = pms.dwConversionStats();
        int convertedWeight = stats.getConvertedWeight();
        int totalWeight = stats.getTotalWeight();
        return totalWeight > 0 ? (convertedWeight * 100) / totalWeight : 0;
    }

    private static int calculateTotalXmlElements(ProjectMigrationStats pms) {
        int passedCount = pms.passedXMLTags().values().stream().mapToInt(i -> i).sum();
        int failedCount = pms.failedXMLTags().values().stream().mapToInt(i -> i).sum();
        return passedCount + failedCount;
    }

    private static int calculateMigratableXmlElements(ProjectMigrationStats pms) {
        return pms.passedXMLTags().values().stream().mapToInt(i -> i).sum();
    }

    private static ReportComponent generateMuleEstimationNotes(double bestCaseCompTimeNew,
                    double bestCaseCompTimeRepeated, double bestDwLineTime,
            double avgCaseCompTimeNew,
                    double avgCaseCompTimeRepeated, double avgCaseDwLineTime,
            double worstCaseCompTimeNew,
            double worstCaseCompTimeRepeated, double worstCaseDwLineTime) {
        String content = String.format(
                """
                        <div class="estimation-notes">
                            <p><strong>Estimation Scenarios:</strong> Time measurement: 1 day = 8 hours,
                                5 working days = 1 week</p>
                            <ul>
                              <li>Best case scenario:
                                <ul>
                        <li>%s hour per each new unsupported element code line for analysis,
                        implementation, and testing</li>
                                      <li>%s minutes per each repeated unsupported element code line for implementation</li>
                                  <li>%s minutes per each unsupported dataweave code line for translation</li>
                                  <li>Assumes minimal complexity and straightforward implementations</li>
                                </ul>
                              </li>
                              <li>Average case scenario:
                                <ul>
                                      <li>%s hours per each new unsupported element code line for analysis,
                                      implementation, and testing</li>
                                      <li>%s minutes per each repeated unsupported element code line for implementation</li>
                                  <li>%s minutes per each unsupported dataweave code line for translation</li>
                                  <li>Assumes medium complexity with moderate implementation challenges</li>
                                </ul>
                              </li>
                              <li>Worst case scenario:
                                <ul>
                                      <li>%s hours per each new unsupported element code line for analysis,
                                      implementation, and testing</li>
                                      <li>%s minutes per each repeated unsupported element code line for implementation</li>
                                  <li>%s minutes per each unsupported dataweave code line for translation</li>
                                  <li>Assumes high complexity with significant implementation challenges</li>
                                </ul>
                              </li>
                            </ul>
                          </div>""",
                bestCaseCompTimeNew / 60, bestCaseCompTimeRepeated, bestDwLineTime,
                avgCaseCompTimeNew / 60, avgCaseCompTimeRepeated, avgCaseDwLineTime,
                worstCaseCompTimeNew / 60, worstCaseCompTimeRepeated, worstCaseDwLineTime);

        return new ReportComponent(content, new Styles(Map.of()));
    }

    public static ReportComponent generateManualWorkEstimationComponent(double bestCaseDays, double avgCaseDays,
            double worstCaseDays, double bestCaseCompTimeNew,
            double bestCaseCompTimeRepeated, double bestDwLineTime,
            double avgCaseCompTimeNew,
                    double avgCaseCompTimeRepeated, double avgCaseDwLineTime,
            double worstCaseCompTimeNew,
            double worstCaseCompTimeRepeated, double worstCaseDwLineTime) {
        // Create TimeEstimation object from the day values
        TimeEstimation estimation = new TimeEstimation(bestCaseDays, avgCaseDays, worstCaseDays);

        // Generate horizontal estimation view using ReportUtils
        ReportComponent estimateView = ReportUtils.generateEstimateView("Manual Work Estimation", estimation);

        // Generate Mule-specific detailed notes
        ReportComponent muleNotes = generateMuleEstimationNotes(
                bestCaseCompTimeNew, bestCaseCompTimeRepeated, bestDwLineTime,
                avgCaseCompTimeNew, avgCaseCompTimeRepeated,
                avgCaseDwLineTime, worstCaseCompTimeNew,
                worstCaseCompTimeRepeated, worstCaseDwLineTime);

        // Combine both components
        String combinedContent = estimateView.content() + muleNotes.content();
        Styles combinedStyles = estimateView.styles().merge(muleNotes.styles());

        return new ReportComponent(combinedContent, combinedStyles);
    }
}
