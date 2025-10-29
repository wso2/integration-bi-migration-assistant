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

import mule.MuleMigrator.MuleVersion;
import mule.common.DWConstructBase;
import mule.common.DWConversionStats;
import mule.common.MigrationMetrics;
import mule.common.MuleLogger;
import common.report.ReportComponent;
import common.report.Styles;
import common.TimeEstimation;
import common.ReportUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class IndividualReportGenerator {

    // For a new element
    public static final double BEST_CASE_COMP_TIME_NEW = 1;
    public static final double AVG_CASE_COMP_TIME_NEW = 2;
    public static final double WORST_CASE_COMP_TIME_NEW = 3;

    // For a repeated element
    public static final double BEST_CASE_COMP_TIME_REPEATED = 0.125; // 1 hour
    public static final double AVG_CASE_COMP_TIME_REPEATED = 0.25; // 2 hours
    public static final double WORST_CASE_COMP_TIME_REPEATED = 0.5; // 4 hours

    public static final double BEST_DW_EXPR_TIME = 0.5 / 30; // 8 min
    public static final double AVG_CASE_DW_EXPR_TIME = 1.5 / 60; // 12 min
    public static final double WORST_CASE_DW_EXPR_TIME = 0.125 / 3; // 20 min

    public static final String INDIVIDUAL_REPORT_NAME = "migration_report.html";
    public static final String MIGRATION_SUMMARY_TITLE = "Migration Summary";
    public static final String MIGRATION_ASSESSMENT_TITLE = "Migration Assessment";

    public static String generateJsonReport(ProjectMigrationStats pms) {
        int migrationCoverage = pms.migrationCoverage();
        String coverageStatus = getCoverageStatus(migrationCoverage);

        int totalXmlElements = calculateTotalXmlElements(pms);
        int migratableXmlElements = calculateMigratableXmlElements(pms);
        int nonMigratableXmlElements = totalXmlElements - migratableXmlElements;

        // DataWeave metrics
        DWConversionStats<? extends DWConstructBase> dwStats = pms.dwConversionStats();
        int totalDwConstructs = dwStats.getTotalEncounteredCount();
        int migratableDwConstructs = dwStats.getConvertedCount();
        int nonMigratableDwConstructs = totalDwConstructs - migratableDwConstructs;

        // Calculate total items, migratable items, and non-migratable items
        int totalItems = totalXmlElements + totalDwConstructs;
        int migratableItems = migratableXmlElements + migratableDwConstructs;
        int nonMigratableItems = nonMigratableXmlElements + nonMigratableDwConstructs;

        return """
                {
                    "coverageOverview": {
                        "unitName": "code lines",
                        "coveragePercentage": %d,
                        "coverageLevel": "%s",
                        "totalElements": %d,
                        "migratableElements": %d,
                        "nonMigratableElements": %d
                    }
                }""".formatted(
                migrationCoverage,
                coverageStatus,
                totalItems,
                migratableItems,
                nonMigratableItems
        );
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

        // DataWeave metrics
        DWConversionStats<? extends DWConstructBase> dwStats = pms.dwConversionStats();
        int totalDwConstructs = dwStats.getTotalEncounteredCount();
        int migratableDwConstructs = dwStats.getConvertedCount();
        int nonMigratableDwConstructs = totalDwConstructs - migratableDwConstructs;

        int dataweaveCoverage = calculateDataweaveCoverage(pms);
        String dataweaveCoverageColor = getCoverageColor(dataweaveCoverage);

        // Format DataWeave coverage to display N/A when total is zero
        String dataweaveDisplayValue = totalDwConstructs > 0 ? dataweaveCoverage + "%" : "N/A";
        String dataweaveBarWidth = totalDwConstructs > 0 ? dataweaveCoverage + "%" : "0%";

        // Calculate total items, migratable items, and non-migratable items
        int totalItems = totalXmlElements + totalDwConstructs;
        int migratableItems = migratableXmlElements + migratableDwConstructs;
        int nonMigratableItems = nonMigratableXmlElements + nonMigratableDwConstructs;

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
                        BEST_CASE_COMP_TIME_NEW, BEST_CASE_COMP_TIME_REPEATED, BEST_DW_EXPR_TIME,
                AVG_CASE_COMP_TIME_NEW, AVG_CASE_COMP_TIME_REPEATED, AVG_CASE_DW_EXPR_TIME,
                WORST_CASE_COMP_TIME_NEW, WORST_CASE_COMP_TIME_REPEATED, WORST_CASE_DW_EXPR_TIME);

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
                totalDwConstructs, migratableDwConstructs, nonMigratableDwConstructs,
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
        int failedDWExprCount = countUnsupportedDWExpressions(metrics.dwConversionStats);

        // Calculate implementation times
        double bestCaseDays = calculateBestCaseEstimate(metrics.failedXMLTags, failedDWExprCount);
        double avgCaseDays = calculateAverageCaseEstimate(metrics.failedXMLTags, failedDWExprCount);
        double worstCaseDays = calculateWorstCaseEstimate(metrics.failedXMLTags, failedDWExprCount);

        int migrationCoverage = calculateMigrationCoverage(muleVersion, metrics);
        return new ProjectMigrationStats(metrics.passedXMLTags, metrics.failedXMLTags, metrics.failedBlocks,
                metrics.dwConversionStats, migrationCoverage, bestCaseDays, avgCaseDays, worstCaseDays,
                metrics.failedXMLTags.size(), failedDWExprCount);
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

    private static double calculateBestCaseEstimate(LinkedHashMap<String, Integer> failedXMLTags, int dwExpressions) {
        double repeatedElementTime = failedXMLTags.values().stream().filter(x -> x > 1).mapToInt(x -> x - 1)
                .mapToDouble(integer -> (integer - 1) * BEST_CASE_COMP_TIME_REPEATED).sum();
        return failedXMLTags.size() * BEST_CASE_COMP_TIME_NEW + repeatedElementTime + dwExpressions * BEST_DW_EXPR_TIME;
    }

    private static double calculateAverageCaseEstimate(LinkedHashMap<String, Integer> failedXMLTags,
                                                       int dwExpressions) {
        double repeatedElementTime = failedXMLTags.values().stream().filter(x -> x > 1).mapToInt(x -> x - 1)
                .mapToDouble(integer -> (integer - 1) * AVG_CASE_COMP_TIME_REPEATED).sum();
        return failedXMLTags.size() * AVG_CASE_COMP_TIME_NEW + repeatedElementTime +
                dwExpressions * AVG_CASE_DW_EXPR_TIME;
    }

    private static double calculateWorstCaseEstimate(LinkedHashMap<String, Integer> failedXMLTags, int dwExpressions) {
        double repeatedElementTime = failedXMLTags.values().stream().filter(x -> x > 1).mapToInt(x -> x - 1)
                .mapToDouble(integer -> (integer - 1) * WORST_CASE_COMP_TIME_REPEATED).sum();
        return failedXMLTags.size() * WORST_CASE_COMP_TIME_NEW + repeatedElementTime +
                dwExpressions * WORST_CASE_DW_EXPR_TIME;
    }

    private static int countUnsupportedDWExpressions(DWConversionStats<? extends DWConstructBase> dwStats) {
        return dwStats.getFailedDWExpressions().size();
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
                    double bestCaseCompTimeRepeated, double bestDwExprTime,
            double avgCaseCompTimeNew,
                    double avgCaseCompTimeRepeated, double avgCaseDwExprTime,
            double worstCaseCompTimeNew,
            double worstCaseCompTimeRepeated, double worstCaseDwExprTime) {
        String content = String.format(
                """
                        <div class="estimation-notes">
                            <p><strong>Estimation Scenarios:</strong> Time measurement: 1 day = 8 hours, 5 working days = 1 week</p>
                            <ul>
                              <li>Best case scenario:
                                <ul>
                                  <li>%s day per each new unsupported element code line for analysis, implementation, and testing</li>
                                  <li>%s hour per each repeated unsupported element code line for implementation</li>
                                  <li>%s minutes per each unsupported dataweave code line for translation</li>
                                  <li>Assumes minimal complexity and straightforward implementations</li>
                                </ul>
                              </li>
                              <li>Average case scenario:
                                <ul>
                                  <li>%s days per each new unsupported element code line for analysis, implementation, and testing</li>
                                  <li>%s hour per each repeated unsupported element code line for implementation</li>
                                  <li>%s minutes per each unsupported dataweave code line for translation</li>
                                  <li>Assumes medium complexity with moderate implementation challenges</li>
                                </ul>
                              </li>
                              <li>Worst case scenario:
                                <ul>
                                  <li>%s days per each new unsupported element code line for analysis, implementation, and testing</li>
                                  <li>%s hour per each repeated unsupported element code line for implementation</li>
                                  <li>%s minutes per each unsupported dataweave code line for translation</li>
                                  <li>Assumes high complexity with significant implementation challenges</li>
                                </ul>
                              </li>
                            </ul>
                          </div>""",
                bestCaseCompTimeNew, bestCaseCompTimeRepeated * 8, bestDwExprTime * 8 * 60,
                        avgCaseCompTimeNew, avgCaseCompTimeRepeated * 8, avgCaseDwExprTime * 8 * 60,
                worstCaseCompTimeNew, worstCaseCompTimeRepeated * 8, worstCaseDwExprTime * 8 * 60);

        return new ReportComponent(content, new Styles(Map.of()));
    }

    public static ReportComponent generateManualWorkEstimationComponent(double bestCaseDays, double avgCaseDays,
            double worstCaseDays, double bestCaseCompTimeNew,
            double bestCaseCompTimeRepeated, double bestDwExprTime,
            double avgCaseCompTimeNew,
                    double avgCaseCompTimeRepeated, double avgCaseDwExprTime,
            double worstCaseCompTimeNew,
            double worstCaseCompTimeRepeated, double worstCaseDwExprTime) {
        // Create TimeEstimation object from the day values
        TimeEstimation estimation = new TimeEstimation(bestCaseDays, avgCaseDays, worstCaseDays);

        // Generate horizontal estimation view using ReportUtils
        ReportComponent estimateView = ReportUtils.generateEstimateView("Manual Work Estimation", estimation);

        // Generate Mule-specific detailed notes
        ReportComponent muleNotes = generateMuleEstimationNotes(
                bestCaseCompTimeNew, bestCaseCompTimeRepeated, bestDwExprTime,
                avgCaseCompTimeNew, avgCaseCompTimeRepeated,
                avgCaseDwExprTime, worstCaseCompTimeNew,
                worstCaseCompTimeRepeated, worstCaseDwExprTime);

        // Combine both components
        String combinedContent = estimateView.content() + muleNotes.content();
        Styles combinedStyles = estimateView.styles().merge(muleNotes.styles());

        return new ReportComponent(combinedContent, combinedStyles);
    }
}
