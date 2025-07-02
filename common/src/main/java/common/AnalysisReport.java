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

    private static final int BEST_CASE_ACTIVITY_TIME = 1;
    private static final int WORST_CASE_ACTIVITY_TIME = 3;
    private static final double N_WORKING_DAYS = 5.0;

    /**
     * Create a new generic analysis report.
     *
     * @param reportTitle           The title of the report
     * @param totalElementCount     Total count of elements analyzed
     * @param unhandledElementCount Count of unhandled elements
     * @param elementType           The type of elements being analyzed (e.g., "Activity", "Component")
     * @param unhandledElements     Map containing unhandled elements with their type as key and collection of string
     *                              representations as value
     */
    public AnalysisReport(String reportTitle, int totalElementCount, int unhandledElementCount, String elementType,
                          Map<String, Collection<UnhandledElement>> unhandledElements) {
        assert totalElementCount >= unhandledElementCount;
        this.reportTitle = reportTitle;
        this.totalElementCount = totalElementCount;
        this.unhandledElementCount = unhandledElementCount;
        this.elementType = elementType;
        this.unhandledElements = unhandledElements;
    }

    /**
     * Calculate frequencies of different element types from the unhandled elements map.
     * Each key represents a unique kind of element, and the size of the collection for each key
     * represents the number of instances of that kind.
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
                    <style>
                        body { font-family: Arial, sans-serif; background-color: #f4f4f9; color: #333; }
                        table { width: 100%%; border-collapse: collapse; margin: 20px 0; }
                        th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }
                        th { background-color: #4682B4; color: white; }
                        tr:nth-child(even) { background-color: #e0f0ff; }
                        tr:hover { background-color: #b0d4f1; }
                        h1 { text-align: center; }
                        footer { text-align: center; margin-top: 20px; font-size: 0.9em; color: #666; }
                        .drawer { overflow: hidden; transition: max-height 0.3s ease-out; max-height: 0; }
                        .drawer.open { max-height: 500px; }
                        .summary-container { background-color: #fff; padding: 20px; border-radius: 8px;
                            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); margin: 20px 0; }
                        .blue-table th { background-color: #4682B4; color: white; }
                        .blue-table tr:nth-child(even) { background-color: #e0f0ff; }
                        .blue-table tr:hover { background-color: #b0d4f1; }
                        .estimation-notes { margin-top: 20px; padding: 15px; background-color: #f8f9fa;
                            border-radius: 5px; }
                        .estimation-notes ul { margin: 10px 0 0 20px; }
                        .estimation-notes li { margin-bottom: 5px; }
                        .unsupported-blocks { padding: 10px; }
                        .block-item {
                            background-color: #f8f9fa;
                            border: 1px solid #ddd;
                            border-radius: 5px;
                            margin-bottom: 15px;
                            overflow: hidden;
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
                        .block-number { font-weight: bold; }
                        .block-type { font-family: monospace; }
                        code {
                            background-color: #f0f0f0;
                            padding: 2px 6px;
                            border-radius: 4px;
                            font-family: monospace;
                            font-size: 0.9em;
                        }
                    </style>
                </head>
                <body>
                    <h1>%s</h1>
                """.formatted(reportTitle, reportTitle));

        // Calculate automated migration coverage percentage
        double coveragePercentage = 100 - calculatePercentage(unhandledElementCount, totalElementCount);
        html.append("""
                    <h3>Automated Migration Coverage: %.0f%%</h3>
                
                    <div class="summary-container">
                        <h3>Implementation Time Estimation</h3>
                        <table class="blue-table">
                            <tr>
                                <th>Scenario</th>
                                <th>Days</th>
                                <th>Weeks (approx.)</th>
                            </tr>
                """.formatted(coveragePercentage));

        // Calculate time estimates based on unique type counts
        int uniqueTypeCount = typeFrequencyMap.size();
        int bestCaseEstimate = uniqueTypeCount * BEST_CASE_ACTIVITY_TIME;
        int avgCaseEstimate = uniqueTypeCount * ((BEST_CASE_ACTIVITY_TIME + WORST_CASE_ACTIVITY_TIME) / 2);
        int worstCaseEstimate = uniqueTypeCount * WORST_CASE_ACTIVITY_TIME;

        // Extract week estimates to separate variables and round up to nearest whole number
        int bestCaseWeeks = (int) Math.ceil(bestCaseEstimate / N_WORKING_DAYS);
        int avgCaseWeeks = (int) Math.ceil(avgCaseEstimate / N_WORKING_DAYS);
        int worstCaseWeeks = (int) Math.ceil(worstCaseEstimate / N_WORKING_DAYS);

        html.append("""
                            <tr>
                                <td>Best Case</td>
                                <td>%s</td>
                                <td>%s</td>
                            </tr>
                            <tr>
                                <td>Average Case</td>
                                <td>%s</td>
                                <td>%s</td>
                            </tr>
                            <tr>
                                <td>Worst Case</td>
                                <td>%s</td>
                                <td>%s</td>
                            </tr>
                        </table>
                """.formatted(
                toDays(bestCaseEstimate), toWeeks(bestCaseWeeks),
                toDays(avgCaseEstimate), toWeeks(avgCaseWeeks),
                toDays(worstCaseEstimate), toWeeks(worstCaseWeeks)
        ));

        // Estimation notes
        html.append("""
                        <div class="estimation-notes">
                            <p><strong>Note:</strong></p>
                            <ul>
                                <li>Best Case: %s per unsupported %s for analysis, implementation and testing</li>
                                <li>Average Case: %s per unsupported %s for analysis, implementation and testing</li>
                                <li>Worst Case: %s per unsupported %s for analysis, implementation and testing</li>
                                <li>Total distinct unsupported %s(s): %d</li>
                            </ul>
                        </div>
                    </div>
                """.formatted(
                toDays(BEST_CASE_ACTIVITY_TIME), elementType.toLowerCase(),
                toDays((BEST_CASE_ACTIVITY_TIME + WORST_CASE_ACTIVITY_TIME) / 2), elementType.toLowerCase(),
                toDays(WORST_CASE_ACTIVITY_TIME), elementType.toLowerCase(),
                elementType.toLowerCase(), uniqueTypeCount
        ));

        // Unsupported elements frequency table
        if (!typeFrequencyMap.isEmpty()) {
            html.append("""
                    <div class="summary-container">
                        <h3>%s(s) Awaiting tool support</h3>
                        <table class="blue-table">
                            <tr>
                                <th>%s Type</th>
                                <th>Frequency</th>
                            </tr>
                    """.formatted(elementType, elementType));

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
                            <div class="estimation-notes">
                                <p><strong>Note:</strong> These %s(s) will be supported in future versions of the
                                migration tool.</p>
                            </div>
                        </div>
                    """.formatted(elementType.toLowerCase()));

            // Unsupported elements section
            html.append("""
                    <div class="summary-container">
                        <h3>Unsupported %s(s)</h3>
                        <div class="unsupported-blocks">
                    """.formatted(elementType));

            for (Map.Entry<String, Collection<UnhandledElement>> entry : unhandledElements.entrySet()) {
                for (var each : entry.getValue()) {
                    appendElement(html, entry.getKey(), each.name().orElse("UnhandledElement"), each.code());
                }
            }

            html.append("""
                            </div>
                        </div>
                    """);
        }

        // Footer with date
        html.append("""
                    <footer>
                        <p>Report generated on: <span id="datetime"></span></p>
                    </footer>
                    <script>
                        document.getElementById("datetime").innerHTML = new Date().toLocaleString();
                    </script>
                </body>
                </html>
                """);

        return html.toString();
    }

    private void appendElement(StringBuilder sb, String kind, String name, String code) {
        sb.append("""
                        <div class="block-item">
                            <div class="block-header">
                                <span class="block-number">%s</span>
                                <span class="block-type">%s</span>
                            </div>
                            <pre class="block-code"><code>%s</code></pre>
                        </div>
                """.formatted(name, kind, escapeHtml(code)));
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

    /**
     * Escape special HTML characters in a string.
     *
     * @param input The input string to escape
     * @return The escaped string
     */
    private String escapeHtml(String input) {
        if (input == null) {
            return "";
        }

        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    public record UnhandledElement(String code, Optional<String> name) {

    }
}
