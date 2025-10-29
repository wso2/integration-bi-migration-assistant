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

package common.report;

import common.ReportUtils;
import common.TimeEstimation;
import common.UnhandledElement;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A generic analysis report that can be used for different types of integrations.
 */
public class ProjectReport {

    private final String reportTitle;
    private final int totalElementCount;
    private final int unhandledElementCount;
    private final String elementType;
    private final Map<String, Collection<UnhandledElement>> unhandledElements;
    private final int partiallySupportedElementCount;
    private final Map<String, Collection<UnhandledElement>> partiallySupportedElements;
    private final TimeEstimation estimation;
    private final TimeEstimation manualConversionEstimation;
    private final long generatedLineCount;

    /**
     * Create a new generic analysis report with manual conversion estimation and generated line count.
     *
     * @param reportTitle           The title of the report
     * @param totalElementCount     Total count of elements analyzed
     * @param unhandledElementCount Count of unhandled elements
     * @param elementType           The type of elements being analyzed (e.g., "Activity", "Component")
     * @param unhandledElements     Map containing unhandled elements with their type as key and collection of string
     *                              representations as value
     * @param partiallySupportedElementCount Count of partially supported elements
     * @param partiallySupportedElements Map containing partially supported elements with their type as key and collection of string
     *                              representations as value
     * @param manualConversionEstimation Manual conversion time estimation
     * @param generatedLineCount    Number of lines of code generated
     */
    public ProjectReport(String reportTitle, int totalElementCount, int unhandledElementCount, String elementType,
                         Map<String, Collection<UnhandledElement>> unhandledElements,
                         int partiallySupportedElementCount,
                         Map<String, Collection<UnhandledElement>> partiallySupportedElements,
                         TimeEstimation manualConversionEstimation,
                         long generatedLineCount) {
        assert totalElementCount >= unhandledElementCount;
        this.reportTitle = reportTitle;
        this.totalElementCount = totalElementCount;
        this.unhandledElementCount = unhandledElementCount;
        this.elementType = elementType;
        this.unhandledElements = unhandledElements;
        this.partiallySupportedElementCount = partiallySupportedElementCount;
        this.partiallySupportedElements = partiallySupportedElements;
        this.estimation = manualConversionEstimation;
        this.manualConversionEstimation = manualConversionEstimation;
        this.generatedLineCount = generatedLineCount;
    }

    /**
     * Calculate frequencies of different element types from the unhandled elements map. Each key represents a unique
     * kind of element, and the size of the collection for each key represents the number of instances of that kind.
     *
     * @return A map with activity types as keys and their frequencies as values
     */
    private Map<String, Integer> calculateTypeFrequencies() {
        Map<String, Integer> typeFrequencyMap = new HashMap<>();

        // Count instances for each element type
        for (Map.Entry<String, Collection<UnhandledElement>> entry : unhandledElements.entrySet()) {
            typeFrequencyMap.put(entry.getKey(), entry.getValue().size());
        }

        return typeFrequencyMap;
    }

    /**
     * Generates an HTML report of the analysis.
     *
     * @return A string containing the HTML report
     */
    public String toHTML() {
        // Calculate frequencies for types from the unhandled elements map
        Map<String, Integer> typeFrequencyMap = calculateTypeFrequencies();

        // Calculate automated migration coverage percentage
        double coveragePercentage = 100 - calculatePercentage(unhandledElementCount, totalElementCount);

        // Generate all report components
        ReportComponent summaryComponent = generateSummaryContainer(coveragePercentage, totalElementCount,
                unhandledElementCount, elementType, generatedLineCount);
        ReportComponent estimateViewComponent = ReportUtils.generateEstimateView("Manual Work Estimation",
                manualConversionEstimation, elementType);
        ReportComponent estimationScenariosComponent = ReportUtils.generateEstimationScenarios(elementType);
        ReportComponent unsupportedElementsComponent = generateUnsupportedElements(typeFrequencyMap);
        ReportComponent unsupportedBlocksComponent = generateUnsupportedElementsBlocks(unhandledElements);
        ReportComponent partiallySupportedComponent = generatePartiallySupportedElements(partiallySupportedElements);
        ReportComponent footerComponent = generateFooter();

        // Merge all styles from components
        Styles mergedStyles = StyleDefinitions.getBaseStyles()
                .merge(summaryComponent.styles())
                .merge(estimateViewComponent.styles())
                .merge(estimationScenariosComponent.styles())
                .merge(unsupportedElementsComponent.styles())
                .merge(unsupportedBlocksComponent.styles())
                .merge(partiallySupportedComponent.styles())
                .merge(footerComponent.styles());

        // Build final HTML document
        StringBuilder html = new StringBuilder();

        // HTML head with merged styles
        html.append("""
                <!DOCTYPE html>
                <html>
                <head>
                    <title>%s</title>
                    <style>
                %s
                    </style>
                </head>
                <body>
                    <div class="container">
                        <h1>%s</h1>
                """.formatted(reportTitle, mergedStyles.toHTML(), reportTitle));

        // Append all component content
        html.append(summaryComponent.content());
        html.append(estimateViewComponent.content());
        html.append(estimationScenariosComponent.content());
        html.append(unsupportedElementsComponent.content());
        html.append(unsupportedBlocksComponent.content());
        html.append(partiallySupportedComponent.content());
        html.append("    </div>\n"); // Close the container div
        html.append(footerComponent.content());

        return html.toString();
    }

    /**
     * Generates the summary container section HTML.
     *
     * @param coveragePercentage    The percentage of automated migration coverage
     * @param totalElementCount     Total count of elements analyzed
     * @param unhandledElementCount Count of unhandled elements
     * @param elementType           The type of elements being analyzed
     * @param generatedLineCount    Number of lines of code generated
     * @return ReportComponent containing HTML and styles for the summary container section
     */
    private static ReportComponent generateSummaryContainer(double coveragePercentage, int totalElementCount,
            int unhandledElementCount, String elementType, long generatedLineCount) {
        // Determine status badge based on coverage
        String statusClass = coveragePercentage >= 90 ? "status-high"
                : coveragePercentage >= 70 ? "status-medium" : "status-low";
        String statusText = coveragePercentage >= 90 ? "High Coverage"
                : coveragePercentage >= 70 ? "Medium Coverage" : "Low Coverage";

        String htmlContent = """
                    <div class="summary-container">
                        <h2>Migration Coverage Overview</h2>
                        <div class="metrics">

                          <div class="metric">
                            <div class="metric-left">
                              <span class="metric-value">%.0f%%</span>
                              <span class="metric-label">Overall Coverage</span>
                              <div class="coverage-indicator">
                                <div class="coverage-bar" data-width="%.0f" data-color="%s"></div>
                              </div>
                              <div>
                                <span class="status-badge %s">%s</span>
                              </div>
                            </div>
                            <div class="metric-right" style="display: flex; justify-content: center; align-items: center;">
                              <div>
                                <div class="coverage-breakdown">
                                  <div>
                                    <span class="breakdown-label">Total %s(s):</span>
                                    <span class="breakdown-value">%d</span>
                                  </div>
                                  <div>
                                    <span class="breakdown-label">Migratable %s(s):</span>
                                    <span class="breakdown-value">%d</span>
                                  </div>
                                  <div>
                                    <span class="breakdown-label">Non-migratable %s(s):</span>
                                    <span class="breakdown-value">%d</span>
                                  </div>
                                  <div>
                                    <span class="breakdown-label">Lines Generated:</span>
                                    <span class="breakdown-value">%,d</span>
                                  </div>
                                </div>
                              </div>
                            </div>
                          </div>
                        </div>
                    </div>
                """
                .formatted(
                        coveragePercentage,
                        coveragePercentage,
                        coveragePercentage >= 90 ? "#4CAF50" : coveragePercentage >= 70 ? "#FF9800" : "#F44336",
                        statusClass,
                        statusText,
                        elementType.toLowerCase(),
                        totalElementCount,
                        elementType.toLowerCase(),
                        totalElementCount - unhandledElementCount,
                        elementType.toLowerCase(),
                        unhandledElementCount,
                        generatedLineCount);

        Styles styles = StyleDefinitions.getSharedContainerStyles()
                .merge(StyleDefinitions.getCoverageIndicatorStyles())
                .merge(StyleDefinitions.getMetricStyles())
                .merge(StyleDefinitions.getStatusBadgeStyles());

        return new ReportComponent(htmlContent, styles);
    }


    /**
     * Generates the unsupported elements section HTML.
     *
     * @param typeFrequencyMap Map containing element types and their frequencies
     * @return ReportComponent containing HTML and styles for the unsupported elements section
     */
    private static ReportComponent generateUnsupportedElements(Map<String, Integer> typeFrequencyMap) {
        String htmlContent;
        if (!typeFrequencyMap.isEmpty()) {
            StringBuilder html = new StringBuilder();
            html.append("""
                    <div class="summary-container">
                        <h2>Currently Unsupported Activities</h2>
                        <div id="toolSupportSection">
                            <table>
                                <tr><th>Activity name</th><th>Frequency</th></tr>
                    """);

            // Add all elements by type
            typeFrequencyMap.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())  // Sort by type name
                    .forEach(entry -> html.append("""
                                    <tr>
                                        <td><code>%s</code></td>
                                        <td>%d</td>
                                    </tr>
                            """.formatted(entry.getKey(), entry.getValue())));

            html.append("""
                            </table>
                            <p class="empty-message hidden" id="toolSupportEmpty">
                                No unsupported activities found
                            </p>
                            <div class="estimation-notes">
                                <p><strong>Note:</strong> These activities are expected to be supported in future versions of the migration tool.</p>
                            </div>
                        </div>
                    </div>
                    """);
            htmlContent = html.toString();
        } else {
            // No unsupported elements
            htmlContent = """
                    <div class="summary-container">
                        <h2>Currently Unsupported Activities</h2>
                        <div id="toolSupportSection">
                            <table class="hidden">
                                <tr><th>Activity name</th><th>Frequency</th></tr>
                            </table>
                            <p class="empty-message visible" id="toolSupportEmpty">
                                No unsupported activities found
                            </p>
                            <div class="estimation-notes hidden">
                                <p><strong>Note:</strong> These activities are expected to be supported in future versions of the migration tool.</p>
                            </div>
                        </div>
                    </div>
                    """;
        }

        Styles styles = StyleDefinitions.getSharedContainerStyles()
                .merge(StyleDefinitions.getTableStyles())
                .merge(StyleDefinitions.getUtilityStyles());

        return new ReportComponent(htmlContent, styles);
    }

    /**
     * Generates the unsupported elements blocks section HTML.
     *
     * @param unhandledElements Map containing unhandled elements
     * @return ReportComponent containing HTML and styles for the unsupported elements blocks section
     */
    private static ReportComponent generateUnsupportedElementsBlocks(
            Map<String, Collection<UnhandledElement>> unhandledElements) {
        int blockCounter = 1;
        boolean hasUnhandledBlocks = false;
        StringBuilder blocksSection = new StringBuilder();

        for (Map.Entry<String, Collection<UnhandledElement>> entry : unhandledElements.entrySet()) {
            String kind = entry.getKey();
            for (UnhandledElement elem : entry.getValue()) {
                if (!hasUnhandledBlocks) {
                    blocksSection.append("""
                                <div class="summary-container unsupported-blocks">
                                    <h2>Activities that required manual Conversion</h2>
                            """);
                    hasUnhandledBlocks = true;
                }
                String blockName = "Block #" + blockCounter++;
                appendElement(blocksSection, kind, blockName, elem.code(), elem.fileName());
            }
        }

        String htmlContent;
        if (hasUnhandledBlocks) {
            blocksSection.append("</div>");
            htmlContent = blocksSection.toString();
        } else {
            htmlContent = "";
        }

        Styles styles = StyleDefinitions.getSharedContainerStyles()
                .merge(StyleDefinitions.getCodeBlockStyles());

        return new ReportComponent(htmlContent, styles);
    }

    /**
     * Generates the partially supported elements section HTML.
     *
     * @param partiallySupportedElements Map containing partially supported elements
     * @return ReportComponent containing HTML and styles for the partially supported elements section
     */
    private static ReportComponent generatePartiallySupportedElements(
            Map<String, Collection<UnhandledElement>> partiallySupportedElements) {
        // Calculate frequencies for partially supported elements
        Map<String, Integer> partiallySupportedFrequencyMap = new HashMap<>();
        for (Map.Entry<String, Collection<UnhandledElement>> entry : partiallySupportedElements.entrySet()) {
            partiallySupportedFrequencyMap.put(entry.getKey(), entry.getValue().size());
        }

        String htmlContent;
        if (!partiallySupportedFrequencyMap.isEmpty()) {
            StringBuilder html = new StringBuilder();
            html.append("""
                    <div class="summary-container">
                        <h2>Activities that may require manual adjustments</h2>
                        <div id="partiallySupportedSection">
                            <table>
                                <tr><th>Activity name</th><th>Frequency</th></tr>
                    """);

            // Add all elements by type
            partiallySupportedFrequencyMap.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())  // Sort by type name
                    .forEach(entry -> html.append("""
                                    <tr>
                                        <td><code>%s</code></td>
                                        <td>%d</td>
                                    </tr>
                            """.formatted(entry.getKey(), entry.getValue())));

            html.append("""
                            </table>
                            <p class="empty-message hidden" id="partiallySupportedEmpty">
                                No partially supported activities found
                            </p>
                            <div class="estimation-notes">
                                <p><strong>Note:</strong> These activities are converted but may require manual review or adjustments.</p>
                            </div>
                        </div>
                    </div>
                    """);
            htmlContent = html.toString();
        } else {
            // No partially supported elements
            htmlContent = """
                    <div class="summary-container">
                        <h2>Activities that may require manual changes</h2>
                        <div id="partiallySupportedSection">
                            <table class="hidden">
                                <tr><th>Activity name</th><th>Frequency</th></tr>
                            </table>
                            <p class="empty-message visible" id="partiallySupportedEmpty">
                                No partially supported activities found
                            </p>
                            <div class="estimation-notes hidden">
                                <p><strong>Note:</strong> These activities are converted but may require manual review or adjustments.</p>
                            </div>
                        </div>
                    </div>
                    """;
        }

        Styles styles = StyleDefinitions.getSharedContainerStyles()
                .merge(StyleDefinitions.getTableStyles())
                .merge(StyleDefinitions.getUtilityStyles());

        return new ReportComponent(htmlContent, styles);
    }

    /**
     * Generates the footer section HTML.
     *
     * @return ReportComponent containing HTML and styles for the footer section
     */
    private static ReportComponent generateFooter() {
        String htmlContent = """
                    <footer><p>Report generated on: <span id="datetime"></span></p></footer>
                    <script>
                      document.addEventListener('DOMContentLoaded', function() {
                          // Set coverage bar styles from data attributes
                          const coverageBars = document.querySelectorAll('.coverage-bar[data-width]');
                          coverageBars.forEach(function(bar) {
                              const width = bar.getAttribute('data-width');
                              const color = bar.getAttribute('data-color');
                              bar.style.width = width + '%%';
                              bar.style.backgroundColor = color;
                          });

                          // Check Currently Unsupported Elements
                          const toolSupportTable = document.querySelector('#toolSupportSection table');
                          if (toolSupportTable.rows.length <= 1) {
                              toolSupportTable.classList.add('hidden');
                              document.getElementById('toolSupportEmpty').classList.remove('hidden');
                              document.getElementById('toolSupportEmpty').classList.add('visible');
                              document.querySelector('#toolSupportSection .estimation-notes').classList.add('hidden');
                          }

                          // Check Partially Supported Elements
                          const partiallySupportedTable = document.querySelector('#partiallySupportedSection table');
                          if (partiallySupportedTable.rows.length <= 1) {
                              partiallySupportedTable.classList.add('hidden');
                              document.getElementById('partiallySupportedEmpty').classList.remove('hidden');
                              document.getElementById('partiallySupportedEmpty').classList.add('visible');
                              document.querySelector('#partiallySupportedSection .estimation-notes').classList.add('hidden');
                          }
                      });
                    </script>
                    <script>document.getElementById("datetime").innerHTML = new Date().toLocaleString();</script>
                </body>
                </html>
                """;

        // Footer has minimal styles - just need empty styles or utility styles for JavaScript
        Styles styles = StyleDefinitions.getUtilityStyles();

        return new ReportComponent(htmlContent, styles);
    }

    private static void appendElement(StringBuilder sb, String kind, String name, String code, String fileName) {
        sb.append("""
                        <div class="block-item">
                            <div class="block-header">
                                <span class="block-number">%s</span>
                                <span class="file-name">%s</span>
                                <span class="block-type">%s</span>
                            </div>
                            <pre class="block-code"><code>%s</code></pre>
                        </div>
                """.formatted(name, fileName, kind, ReportUtils.escapeHtml(code)));
    }

    /**
     * Calculate percentage of a part relative to a total.
     *
     * @param part  The part value
     * @param total The total value
     * @return The percentage rounded to 1 decimal place
     */
    private double calculatePercentage(int part, int total) {
        if (total == 0) {
            return 0;
        }
        return Math.round(((double) part / total) * 1000) / 10.0; // Round to 1 decimal place
    }

}
