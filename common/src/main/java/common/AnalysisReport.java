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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A generic analysis report that can be used for different types of integrations.
 */
public class AnalysisReport {

    private final String reportTitle;
    private final int totalElementCount;
    private final int unhandledElementCount;
    private final String elementType;
    private final Map<String, Collection<UnhandledElement>> unhandledElements;
    private final int partiallySupportedElementCount;
    private final Map<String, Collection<UnhandledElement>> partiallySupportedElements;
    private final TimeEstimation estimation;
    private final TimeEstimation manualConversionEstimation;
    private final TimeEstimation validationEstimation;

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
                max-width: 600px;
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
                width: 100%;
                height: 12px;
                background-color: #f0f0f0;
                border-radius: 6px;
                overflow: hidden;
                box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.1);
                margin: 10px 0 20px 0;
            }

            .coverage-bar {
                height: 100%;
                border-radius: 6px;
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

            /* Code blocks styling */
            .unsupported-blocks {
                padding: 10px;
            }

            .block-item {
                background-color: #f8f9fa;
                border: 1px solid #ddd;
                border-radius: 5px;
                margin-bottom: 15px;
                overflow: hidden;
                transition: transform 0.2s, box-shadow 0.2s;
            }

            .block-item:hover {
                transform: translateY(-2px);
                box-shadow: 0 5px 15px rgba(0, 0, 0, 0.12);
            }

            .block-header {
                background-color: #4682B4;
                color: white;
                padding: 10px;
                display: flex;
                justify-content: space-between;
            }

            .block-code {
                margin: 0;
                padding: 15px;
                background-color: #fff;
                overflow-x: auto;
                font-family: monospace;
                white-space: pre-wrap;
            }

            .block-number {
                font-weight: bold;
            }

            .block-type {
                font-family: monospace;
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

            /* Code in tables */
            table code {
                background-color: #f0f0f0;
                padding: 2px 6px;
                border-radius: 4px;
                font-family: monospace;
                font-size: 0.9em;
            }

            /* Metric styling with box shape and hover effects */
            .metric {
                width: 100%;
                box-sizing: border-box;
                padding: 15px 20px;
                display: flex;
                flex-direction: row;
                align-items: flex-start;
                gap: 20px;
                background-color: #f8f9fa;
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
                font-size: 1.8em;
                color: #4682B4;
                margin-bottom: 5px;
            }

            .metric-label {
                font-size: 0.9em;
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

            /* Time estimation table styling */
            .time-best {
                color: #4CAF50;
                font-weight: 600;
            }

            .time-avg {
                color: #4682B4;
                font-weight: 600;
            }

            .time-worst {
                color: #FF5722;
                font-weight: 600;
            }

            .drawer { overflow: hidden; transition: max-height 0.3s ease-out; max-height: 0; }
            .drawer.open { max-height: 500px; }
            .empty-message { text-align: center; padding: 20px; color: #666; }

            /* Coverage bar with data attributes */
            .coverage-bar[data-width] {
                height: 100%;
                border-radius: 6px;
                transition: width 0.5s ease-in-out;
            }

            /* Utility classes for visibility */
            .hidden {
                display: none;
            }

            .visible {
                display: block;
            }
            """;

    /**
     * Create a new generic analysis report with separate manual conversion and validation estimations.
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
     * @param validationEstimation  Validation time estimation
     */
    public AnalysisReport(String reportTitle, int totalElementCount, int unhandledElementCount, String elementType,
                          Map<String, Collection<UnhandledElement>> unhandledElements,
                          int partiallySupportedElementCount,
                          Map<String, Collection<UnhandledElement>> partiallySupportedElements,
                          TimeEstimation manualConversionEstimation,
                          TimeEstimation validationEstimation) {
        assert totalElementCount >= unhandledElementCount;
        this.reportTitle = reportTitle;
        this.totalElementCount = totalElementCount;
        this.unhandledElementCount = unhandledElementCount;
        this.elementType = elementType;
        this.unhandledElements = unhandledElements;
        this.partiallySupportedElementCount = partiallySupportedElementCount;
        this.partiallySupportedElements = partiallySupportedElements;
        this.estimation = TimeEstimation.sum(manualConversionEstimation, validationEstimation);
        this.manualConversionEstimation = manualConversionEstimation;
        this.validationEstimation = validationEstimation;
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

        StringBuilder html = new StringBuilder();

        // Start HTML document
        html.append("""
                <!DOCTYPE html>
                <html>
                <head>
                    <title>%s</title>
                    <style>%s</style>
                </head>
                <body>
                    <div class="container">
                        <h1>%s</h1>
                """.formatted(reportTitle, CSS_STYLES, reportTitle));

        // Calculate automated migration coverage percentage
        double coveragePercentage = 100 - calculatePercentage(unhandledElementCount, totalElementCount);

        // Generate summary container
        html.append(
                generateSummaryContainer(coveragePercentage, totalElementCount, unhandledElementCount, elementType));

        // Generate manual work estimation section
        if (manualConversionEstimation != null && validationEstimation != null) {
            html.append(generateSeparateManualWorkEstimation(manualConversionEstimation, validationEstimation,
                    elementType));
        } else {
            html.append(generateManualWorkEstimation(estimation, elementType));
        }

        // Generate estimation notes
        html.append(generateEstimationNotes(elementType));

        // Generate unsupported elements section
        html.append(generateUnsupportedElements(typeFrequencyMap));

        // Generate unsupported elements blocks
        html.append(generateUnsupportedElementsBlocks(unhandledElements));

        // Generate partially supported elements section
        html.append(generatePartiallySupportedElements(partiallySupportedElements));

        // Generate footer
        html.append(generateFooter());

        return html.toString();
    }

    /**
     * Generates the summary container section HTML.
     *
     * @param coveragePercentage    The percentage of automated migration coverage
     * @param totalElementCount     Total count of elements analyzed
     * @param unhandledElementCount Count of unhandled elements
     * @param elementType           The type of elements being analyzed
     * @return HTML string for the summary container section
     */
    private static String generateSummaryContainer(double coveragePercentage, int totalElementCount,
            int unhandledElementCount, String elementType) {
        // Determine status badge based on coverage
        String statusClass = coveragePercentage >= 90 ? "status-high"
                : coveragePercentage >= 70 ? "status-medium" : "status-low";
        String statusText = coveragePercentage >= 90 ? "High Coverage"
                : coveragePercentage >= 70 ? "Medium Coverage" : "Low Coverage";

        return """
                    <div class="summary-container">
                        <h2>Migration Coverage Overview</h2>
                        <div class="metrics">

                          <!-- Overall Coverage -->
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
                            <div class="metric-right">
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
                        unhandledElementCount);
    }

    /**
     * Generates the manual work estimation section HTML.
     *
     * @param estimation  Time estimation
     * @param elementType The type of elements being analyzed
     * @return HTML string for the manual work estimation section
     */
    private static String generateManualWorkEstimation(TimeEstimation estimation, String elementType) {
        int bestCaseWeeks = estimation.bestCaseWeeks();
        int avgCaseWeeks = estimation.averageCaseWeeks();
        int worstCaseWeeks = estimation.worstCaseWeeks();

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
                                <td class="time-best">%s</td>
                                <td class="time-best">%s</td>
                            </tr>
                            <tr>
                                <td>Average Case</td>
                                <td class="time-avg">%s</td>
                                <td class="time-avg">%s</td>
                            </tr>
                            <tr>
                                <td>Worst Case</td>
                                <td class="time-worst">%s</td>
                                <td class="time-worst">%s</td>
                            </tr>
                        </table>
                """.formatted(
                toDays(estimation.bestCaseDaysAsInt()), toWeeks(bestCaseWeeks),
                toDays(estimation.averageCaseDaysAsInt()), toWeeks(avgCaseWeeks),
                toDays(estimation.worstCaseDaysAsInt()), toWeeks(worstCaseWeeks)
        );
    }

    /**
     * Generates a consolidated manual work estimation section showing breakdown and total.
     *
     * @param conversionEstimation Manual conversion time estimation
     * @param validationEstimation Validation time estimation
     * @param elementType The type of elements being analyzed
     * @return HTML string for the consolidated manual work estimation section
     */
    private static String generateSeparateManualWorkEstimation(TimeEstimation conversionEstimation,
                                                               TimeEstimation validationEstimation,
                                                               String elementType) {
        int conversionBestWeeks = conversionEstimation.bestCaseWeeks();
        int conversionAvgWeeks = conversionEstimation.averageCaseWeeks();
        int conversionWorstWeeks = conversionEstimation.worstCaseWeeks();

        int validationBestWeeks = validationEstimation.bestCaseWeeks();
        int validationAvgWeeks = validationEstimation.averageCaseWeeks();
        int validationWorstWeeks = validationEstimation.worstCaseWeeks();

        TimeEstimation totalEstimation = TimeEstimation.sum(conversionEstimation, validationEstimation);
        int totalBestWeeks = totalEstimation.bestCaseWeeks();
        int totalAvgWeeks = totalEstimation.averageCaseWeeks();
        int totalWorstWeeks = totalEstimation.worstCaseWeeks();

        return """
                    <div class="summary-container">
                        <h2>Manual Work Estimation</h2>
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
                """.formatted(
                toDays(conversionEstimation.bestCaseDaysAsInt()),
                toDays(conversionEstimation.averageCaseDaysAsInt()),
                toDays(conversionEstimation.worstCaseDaysAsInt()),
                toDays(validationEstimation.bestCaseDaysAsInt()),
                toDays(validationEstimation.averageCaseDaysAsInt()),
                toDays(validationEstimation.worstCaseDaysAsInt()),
                toDays(totalEstimation.bestCaseDaysAsInt()),
                toDays(totalEstimation.averageCaseDaysAsInt()),
                toDays(totalEstimation.worstCaseDaysAsInt())
        );
    }

    /**
     * Generates the estimation notes section HTML.
     *
     * @param elementType The type of elements being analyzed
     * @return HTML string for the estimation notes section
     */
    private static String generateEstimationNotes(String elementType) {
        return """
                        <div class="estimation-notes">
                            <p><strong>Estimation Scenarios:</strong> Time measurement: 1 day = 8 hours, 5 working days = 1 week</p>
                            <ul>
                                <li>Best case scenario:
                                  <ul>
                                    <li>1.0 day per each new unsupported %s for analysis, implementation, and testing</li>
                                    <li>1.0 hour per each repeated unsupported %s for implementation</li>
                                    <li>2 minutes per each line of code generated</li>
                                    <li>Assumes minimal complexity and straightforward implementations</li>
                                  </ul>
                                </li>
                                <li>Average case scenario:
                                  <ul>
                                    <li>2.0 days per each new unsupported %s for analysis, implementation, and testing</li>
                                    <li>2.0 hour per each repeated unsupported %s for implementation</li>
                                    <li>5 minutes per each line of code generated</li>
                                    <li>Assumes medium complexity with moderate implementation challenges</li>
                                  </ul>
                                </li>
                                <li>Worst case scenario:
                                  <ul>
                                    <li>3.0 days per each new unsupported %s for analysis, implementation, and testing</li>
                                    <li>4.0 hour per each repeated unsupported %s for implementation</li>
                                    <li>10 minutes per each line of code generated</li>
                                    <li>Assumes high complexity with significant implementation challenges</li>
                                  </ul>
                                </li>
                            </ul>
                        </div>
                    </div>
                """.formatted(
                elementType.toLowerCase(), elementType.toLowerCase(),
                elementType.toLowerCase(), elementType.toLowerCase(),
                elementType.toLowerCase(), elementType.toLowerCase()
        );
    }

    /**
     * Generates the unsupported elements section HTML.
     *
     * @param typeFrequencyMap Map containing element types and their frequencies
     * @return HTML string for the unsupported elements section
     */
    private static String generateUnsupportedElements(Map<String, Integer> typeFrequencyMap) {
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
            return html.toString();
        } else {
            // No unsupported elements
            return """
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
    }

    /**
     * Generates the unsupported elements blocks section HTML.
     *
     * @param unhandledElements Map containing unhandled elements
     * @return HTML string for the unsupported elements blocks section
     */
    private static String generateUnsupportedElementsBlocks(
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

        if (hasUnhandledBlocks) {
            blocksSection.append("</div>");
            return blocksSection.toString();
        }

        return "";
    }

    /**
     * Generates the partially supported elements section HTML.
     *
     * @param partiallySupportedElements Map containing partially supported elements
     * @return HTML string for the partially supported elements section
     */
    private static String generatePartiallySupportedElements(
            Map<String, Collection<UnhandledElement>> partiallySupportedElements) {
        // Calculate frequencies for partially supported elements
        Map<String, Integer> partiallySupportedFrequencyMap = new HashMap<>();
        for (Map.Entry<String, Collection<UnhandledElement>> entry : partiallySupportedElements.entrySet()) {
            partiallySupportedFrequencyMap.put(entry.getKey(), entry.getValue().size());
        }

        if (!partiallySupportedFrequencyMap.isEmpty()) {
            StringBuilder html = new StringBuilder();
            html.append("""
                    <div class="summary-container">
                        <h2>Activities that need manual validation</h2>
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
            return html.toString();
        } else {
            // No partially supported elements
            return """
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
    }

    /**
     * Generates the footer section HTML.
     *
     * @return HTML string for the footer section
     */
    private static String generateFooter() {
        return """
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
                """.formatted(name, fileName, kind, escapeHtml(code)));
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

    /**
     * Format a number as a day string with correct singular/plural form.
     *
     * @param number The number of days
     * @return A string with the number and "day" or "days"
     */
    private static String toDays(int number) {
        return number + " " + (number == 1 ? "day" : "days");
    }

    /**
     * Format a number as a week string with correct singular/plural form.
     *
     * @param number The number of weeks
     * @return A string with the number and "week" or "weeks"
     */
    private static String toWeeks(int number) {
        return number + " " + (number == 1 ? "week" : "weeks");
    }

    /**
     * Escape special HTML characters in a string.
     *
     * @param input The input string to escape
     * @return The escaped string
     */
    private static String escapeHtml(String input) {
        if (input == null) {
            return "";
        }

        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    public record UnhandledElement(String code, Optional<String> name, String fileName) {

    }
}
