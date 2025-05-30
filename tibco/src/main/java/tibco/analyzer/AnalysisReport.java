/*
 *  Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
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

package tibco.analyzer;

import org.w3c.dom.Element;
import tibco.converter.ConversionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public record AnalysisReport(int totalActivityCount, int unhandledActivityCount,
                             Collection<UnhandledActivityElement> unhandledActivityElements) {
    private static final String REPORT_TITLE = "Migration Assessment";
    private static final int BEST_CASE_ACTIVITY_TIME = 1;
    private static final int WORST_CASE_ACTIVITY_TIME = 3;

    public AnalysisReport {
        assert totalActivityCount >= unhandledActivityCount;
        unhandledActivityElements = Collections.unmodifiableCollection(unhandledActivityElements);
    }

    sealed interface UnhandledActivityElement {
        Element element();

        record NamedUnhandledActivityElement(String name, String type,
                                             Element element) implements UnhandledActivityElement {

        }

        record UnNamedUnhandledActivityElement(Element element) implements UnhandledActivityElement {

        }
    }

    /**
     * Counts unique unhandled activities.
     * - For unnamed activities, each one is treated as unique
     * - For named activities, they are considered unique based on their type (not name)
     *
     * @return The count of unique unhandled activities
     */
    private int countUniqueUnhandledActivities() {
        // Create a set to track unique types for named activities
        java.util.Set<String> uniqueNamedTypes = new java.util.HashSet<>();
        int uniqueCount = 0;

        for (UnhandledActivityElement element : unhandledActivityElements) {
            if (element instanceof UnhandledActivityElement.NamedUnhandledActivityElement named) {
                // For named activities, uniqueness is based on type
                uniqueNamedTypes.add(named.type());
            } else {
                // Each unnamed activity is considered unique
                uniqueCount++;
            }
        }

        return uniqueCount + uniqueNamedTypes.size();
    }

    public String toHTML() {
        StringBuilder html = new StringBuilder();
        
        // Start HTML document
        html.append("<!DOCTYPE html>\n")
            .append("<html>\n")
            .append("<head>\n")
            .append("    <title>").append(REPORT_TITLE).append("</title>\n")
            .append("    <style>\n")
            .append("        body { font-family: Arial, sans-serif; background-color: #f4f4f9; color: #333; }\n")
            .append("        table { width: 100%; border-collapse: collapse; margin: 20px 0; }\n")
            .append("        th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }\n")
            .append("        th { background-color: #4682B4; color: white; }\n")
            .append("        tr:nth-child(even) { background-color: #e0f0ff; }\n")
            .append("        tr:hover { background-color: #b0d4f1; }\n")
            .append("        h1 { text-align: center; }\n")
            .append("        footer { text-align: center; margin-top: 20px; font-size: 0.9em; color: #666; }\n")
            .append("        .drawer { overflow: hidden; transition: max-height 0.3s ease-out; max-height: 0; }\n")
            .append("        .drawer.open { max-height: 500px; }\n")
            .append("        .summary-container { background-color: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); margin: 20px 0; }\n")
            .append("        .blue-table th { background-color: #4682B4; color: white; }\n")
            .append("        .blue-table tr:nth-child(even) { background-color: #e0f0ff; }\n")
            .append("        .blue-table tr:hover { background-color: #b0d4f1; }\n")
            .append("        .estimation-notes { margin-top: 20px; padding: 15px; background-color: #f8f9fa; border-radius: 5px; }\n")
            .append("        .estimation-notes ul { margin: 10px 0 0 20px; }\n")
            .append("        .estimation-notes li { margin-bottom: 5px; }\n")
            .append("        .unsupported-blocks { padding: 10px; }\n")
            .append("        .block-item {\n")
            .append("            background-color: #f8f9fa;\n")
            .append("            border: 1px solid #ddd;\n")
            .append("            border-radius: 5px;\n")
            .append("            margin-bottom: 15px;\n")
            .append("            overflow: hidden;\n")
            .append("        }\n")
            .append("        .block-header {\n")
            .append("            background-color: #4682B4;\n")
            .append("            color: white;\n")
            .append("            padding: 10px;\n")
            .append("            display: flex;\n")
            .append("            justify-content: space-between;\n")
            .append("        }\n")
            .append("        .block-code {\n")
            .append("            margin: 0;\n")
            .append("            padding: 15px;\n")
            .append("            background-color: #fff;\n")
            .append("            overflow-x: auto;\n")
            .append("            font-family: monospace;\n")
            .append("            white-space: pre-wrap;\n")
            .append("        }\n")
            .append("        .block-number { font-weight: bold; }\n")
            .append("        .block-type { font-family: monospace; }\n")
            .append("        code {\n")
            .append("            background-color: #f0f0f0;\n")
            .append("            padding: 2px 6px;\n")
            .append("            border-radius: 4px;\n")
            .append("            font-family: monospace;\n")
            .append("            font-size: 0.9em;\n")
            .append("        }\n")
            .append("    </style>\n")
            .append("</head>\n")
            .append("<body>\n")
            .append("    <h1>").append(REPORT_TITLE).append("</h1>\n");
            
        // Calculate automated migration coverage percentage
        double coveragePercentage = 100 - calculatePercentage(unhandledActivityCount, totalActivityCount);
        html.append("    <h3>Automated Migration Coverage: ").append(String.format("%.0f", coveragePercentage)).append("%</h3>\n");
        
        // Implementation time estimation
        html.append("    <div class=\"summary-container\">\n")
            .append("        <h3>Implementation Time Estimation</h3>\n")
            .append("        <table class=\"blue-table\">\n")
            .append("            <tr>\n")
            .append("                <th>Scenario</th>\n")
            .append("                <th>Days</th>\n")
            .append("                <th>Weeks (approx.)</th>\n")
            .append("            </tr>\n");
            
        // Calculate time estimates
        int uniqueUnhandledCount = countUniqueUnhandledActivities();
        int bestCaseEstimate = uniqueUnhandledCount * BEST_CASE_ACTIVITY_TIME;
        int avgCaseEstimate = uniqueUnhandledCount * ((BEST_CASE_ACTIVITY_TIME + WORST_CASE_ACTIVITY_TIME) / 2);
        int worstCaseEstimate = uniqueUnhandledCount * WORST_CASE_ACTIVITY_TIME;
        
        html.append("            <tr>\n")
            .append("                <td>Best Case</td>\n")
            .append("                <td>").append(bestCaseEstimate).append(" days</td>\n")
            .append("                <td>").append(String.format("%.1f", bestCaseEstimate/5.0)).append(" weeks</td>\n")
            .append("            </tr>\n")
            .append("            <tr>\n")
            .append("                <td>Average Case</td>\n")
            .append("                <td>").append(avgCaseEstimate).append(" days</td>\n")
            .append("                <td>").append(String.format("%.1f", avgCaseEstimate/5.0)).append(" weeks</td>\n")
            .append("            </tr>\n")
            .append("            <tr>\n")
            .append("                <td>Worst Case</td>\n")
            .append("                <td>").append(worstCaseEstimate).append(" days</td>\n")
            .append("                <td>").append(String.format("%.1f", worstCaseEstimate/5.0)).append(" weeks</td>\n")
            .append("            </tr>\n")
            .append("        </table>\n");
        
        // Estimation notes
        html.append("        <div class=\"estimation-notes\">\n")
            .append("            <p><strong>Note:</strong></p>\n")
            .append("            <ul>\n")
            .append("                <li>Best Case: ").append(BEST_CASE_ACTIVITY_TIME).append(" day per unsupported activity</li>\n")
            .append("                <li>Average Case: ").append((BEST_CASE_ACTIVITY_TIME + WORST_CASE_ACTIVITY_TIME)/2).append(" days per unsupported activity</li>\n")
            .append("                <li>Worst Case: ").append(WORST_CASE_ACTIVITY_TIME).append(" days per unsupported activity</li>\n")
            .append("                <li>Total unsupported activities: ").append(uniqueUnhandledCount).append("</li>\n")
            .append("            </ul>\n")
            .append("        </div>\n")
            .append("    </div>\n");
        
        // Unsupported activities frequency table
        if (!unhandledActivityElements.isEmpty()) {
            html.append("    <div class=\"summary-container\">\n")
                .append("        <h3>Unsupported Activities Frequency</h3>\n")
                .append("        <table class=\"blue-table\">\n")
                .append("            <tr>\n")
                .append("                <th>Activity Name</th>\n")
                .append("                <th>Frequency</th>\n")
                .append("            </tr>\n");
            
            // Generate tag frequency map
            java.util.Map<String, Integer> typeCountMap = new java.util.HashMap<>();
            int unnamedCount = 0;
            
            for (UnhandledActivityElement element : unhandledActivityElements) {
                if (element instanceof UnhandledActivityElement.NamedUnhandledActivityElement named) {
                    // For named activities, count by type
                    typeCountMap.put(named.type(), typeCountMap.getOrDefault(named.type(), 0) + 1);
                } else {
                    // Count unnamed activities
                    unnamedCount++;
                }
            }
            
            // Add unnamed activities first if any exist
            if (unnamedCount > 0) {
                html.append("            <tr>\n")
                    .append("                <td><code>unnamed-activity</code></td>\n")
                    .append("                <td>").append(unnamedCount).append("</td>\n")
                    .append("            </tr>\n");
            }
            
            // Add named activities by type
            typeCountMap.entrySet().stream()
                    .sorted(java.util.Map.Entry.comparingByKey())  // Sort by type name
                    .forEach(entry -> {
                        html.append("            <tr>\n")
                            .append("                <td><code>").append(entry.getKey()).append("</code></td>\n")
                            .append("                <td>").append(entry.getValue()).append("</td>\n")
                            .append("            </tr>\n");
                    });
            
            html.append("        </table>\n")
                .append("    </div>\n");
            
            // Unsupported activities section
            html.append("    <div class=\"summary-container\">\n")
                .append("        <h3>Unsupported TIBCO Activities</h3>\n")
                .append("        <div class=\"unsupported-blocks\">\n");
            
            int blockCount = 1;
            for (UnhandledActivityElement element : unhandledActivityElements) {
                html.append("            <div class=\"block-item\">\n")
                    .append("                <div class=\"block-header\">\n")
                    .append("                    <span class=\"block-number\">Block #").append(blockCount++).append("</span>\n");
                
                if (element instanceof UnhandledActivityElement.NamedUnhandledActivityElement named) {
                    html.append("                    <span class=\"block-type\">").append(named.type()).append("</span>\n");
                } else {
                    html.append("                    <span class=\"block-type\">unnamed-activity</span>\n");
                }
                
                html.append("                </div>\n")
                    .append("                <pre class=\"block-code\"><code>").append(escapeHtml(ConversionUtils.elementToString(element.element())))
                    .append("</code></pre>\n")
                    .append("            </div>\n");
            }
            
            html.append("        </div>\n")
                .append("    </div>\n");
        }
        
        // Footer with date
        html.append("    <footer>\n")
            .append("        <p>Report generated on: <span id=\"datetime\"></span></p>\n")
            .append("    </footer>\n")
            .append("    <script>\n")
            .append("        document.getElementById(\"datetime\").innerHTML = new Date().toLocaleString();\n")
            .append("    </script>\n")
            .append("</body>\n")
            .append("</html>");

        return html.toString();
    }

    // The appendUnhandledActivitySummary and formatUnhandledActivityElement methods were removed
    // as they are no longer needed with the new HTML template format

    private String escapeHtml(String input) {
        if (input == null) {
            return "";
        }

        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private double calculatePercentage(int part, int total) {
        if (total == 0) {
            return 0;
        }
        return Math.round(((double) part / total) * 1000) / 10.0; // Round to 1 decimal place
    }

    public static AnalysisReport combine(AnalysisReport report1, AnalysisReport report2) {
        Collection<UnhandledActivityElement> unhandledActivities = new HashSet<>(report1.unhandledActivityElements());
        unhandledActivities.addAll(report2.unhandledActivityElements());
        return new AnalysisReport(
                report1.totalActivityCount() + report2.totalActivityCount(),
                unhandledActivities.size(),
                unhandledActivities);
    }

    public static AnalysisReport empty() {
        return new AnalysisReport(0, 0, Collections.emptyList());
    }
    
    // The generateUniqueActivityTypeBreakdown method was removed
    // as it is no longer needed with the new HTML template format
}
