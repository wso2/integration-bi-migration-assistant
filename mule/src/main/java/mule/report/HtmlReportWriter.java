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

package mule.report;

import mule.Context;
import mule.dataweave.converter.DWConversionStats;
import mule.model.MuleXMLTag;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Logger;

public class HtmlReportWriter {

    public static int writeHtmlReport(Logger logger, Path reportFilePath, Context.MigrationMetrics migrationMetrics) {
        try {
            String reportContent = generateReport(migrationMetrics);
            Files.writeString(reportFilePath, reportContent);
            logger.info("Migration assessment report written to " + reportFilePath);
            return calculateMigrationCoverage(migrationMetrics);
        } catch (IOException e) {
            logger.severe("Error writing report to file: " + e.getMessage());
            return 0;
        }
    }

    private static String generateReport(Context.MigrationMetrics metrics) {
        int totalElements = countDistinctUnsupportedElements(metrics.failedXMLTags);
        int totalDWExpressions = countUnsupportedDWExpressions(metrics.dwConversionStats);

        // Calculate implementation times
        double bestCaseDays = calculateBestCaseEstimate(totalElements, totalDWExpressions);
        double avgCaseDays = calculateAverageCaseEstimate(totalElements, totalDWExpressions);
        double worstCaseDays = calculateWorstCaseEstimate(totalElements, totalDWExpressions);

        String unsupportedElementsTable = generateUnsupportedElementsTable(metrics.failedXMLTags);
        String unsupportedBlocksHtml = generateUnsupportedBlocksHtml(metrics.failedBlocks);
        String dataweaveExpressionsHtml = generateDataweaveExpressionsHtml(metrics.dwConversionStats);

        return String.format(
                MigrationReportTemplate.getHtmlTemplate(),
                calculateMigrationCoverage(metrics),
                bestCaseDays, (int) Math.ceil(bestCaseDays / 5.0),
                avgCaseDays, (int) Math.ceil(avgCaseDays / 5.0),
                worstCaseDays, (int) Math.ceil(worstCaseDays / 5.0),
                totalElements,
                totalDWExpressions,
                unsupportedElementsTable,
                unsupportedBlocksHtml,
                dataweaveExpressionsHtml
        );
    }

    private static int calculateMigrationCoverage(Context.MigrationMetrics metrics) {
        int totalPassedWeight = calculateTotalWeight(metrics.passedXMLTags);
        int totalFailedWeight = calculateTotalWeight(metrics.failedXMLTags);
        int dwTotalWeight = metrics.dwConversionStats.getTotalWeight();
        int dwConvertedWeight = metrics.dwConversionStats.getConvertedWeight();

        int combinedTotalWeight = totalPassedWeight + totalFailedWeight + dwTotalWeight;
        int combinedConvertedWeight = totalPassedWeight + dwConvertedWeight;

        return combinedTotalWeight == 0 ? 0 : (combinedConvertedWeight * 100) / combinedTotalWeight;
    }

    private static int calculateTotalWeight(LinkedHashMap<String, Integer> tagMap) {
        return tagMap.entrySet().stream()
                .mapToInt(entry -> MuleXMLTag.getWeightFromTag(entry.getKey()) * entry.getValue())
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
                i + 1,
                getBlockType(failedBlocks.get(i)),
                failedBlocks.get(i)));
        }
        return sb.toString();
    }

    private static String generateDataweaveExpressionsHtml(DWConversionStats dwStats) {
        StringBuilder sb = new StringBuilder();
        int expressionCount = 1;
        for (String dwExpr : dwStats.getFailedDWExpressions()) {
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

    private static double calculateBestCaseEstimate(int elements, int dwExpressions) {
        return elements * 1.0 + dwExpressions * 0.25;
    }

    private static double calculateAverageCaseEstimate(int elements, int dwExpressions) {
        return elements * 3.0 + dwExpressions * 0.5;
    }

    private static double calculateWorstCaseEstimate(int elements, int dwExpressions) {
        return elements * 6.0 + dwExpressions * 1.5;
    }

    private static int countDistinctUnsupportedElements(LinkedHashMap<String, Integer> failedTags) {
        return failedTags.size();
    }

    private static int countUnsupportedDWExpressions(DWConversionStats dwStats) {
        return dwStats.getFailedDWExpressions().size();
    }
}
