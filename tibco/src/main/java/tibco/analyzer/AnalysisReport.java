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
    private static final String HEADING = "TIBCO migration analysis report";
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

        // HTML header and style
        html.append("""
                <!DOCTYPE html>
                <html>
                <head>
                    <title>%s</title>
                    <style>
                        body { font-family: Arial, sans-serif; margin: 20px; }
                        h1 { color: #333366; }
                        .summary { background-color: #f5f5f5; padding: 10px; border-radius: 5px; margin-bottom: 20px; }
                        .activity { margin-bottom: 15px; padding: 10px; border: 1px solid #ddd; border-radius: 5px; }
                        .activity-name { font-weight: bold; color: #333366; }
                        .activity-type { color: #666699; margin-top: 5px; }
                        pre { background-color: #f9f9f9; padding: 10px; border-radius: 3px; overflow: auto; }
                    </style>
                </head>
                <body>
                    <h1>%s</h1>
                """.formatted(HEADING, HEADING));

        // Summary section
        html.append("""
                    <div class="summary">
                        <h2>Summary</h2>
                """);
        html.append("        <p>Total Activities: ").append(totalActivityCount).append("</p>\n");
        appendUnhandledActivitySummary(html);
        html.append("    </div>\n");

        // Unhandled activities list
        if (!unhandledActivityElements.isEmpty()) {
            html.append("    <h2>Unhandled Activities</h2>\n");

            for (UnhandledActivityElement element : unhandledActivityElements) {
                html.append(formatUnhandledActivityElement(element));
            }
        }

        html.append("""
                </body>
                </html>
                """);

        return html.toString();
    }

    private void appendUnhandledActivitySummary(StringBuilder html) {
        html.append("""
                <p>Unhandled Activities: %d (%.1f%%)</p>
                """.formatted(unhandledActivityCount, calculatePercentage(unhandledActivityCount, totalActivityCount)));

        int uniqueUnhandledCount = countUniqueUnhandledActivities();
        html.append("""
                <p>Unique Unhandled Activities: %d (%.1f%%)</p>
                """.formatted(uniqueUnhandledCount, calculatePercentage(uniqueUnhandledCount, totalActivityCount)));

        if (!unhandledActivityElements.isEmpty()) {
            int bestCaseEstimate = uniqueUnhandledCount * BEST_CASE_ACTIVITY_TIME;
            int worstCaseEstimate = uniqueUnhandledCount * WORST_CASE_ACTIVITY_TIME;
            html.append("""
                    <p>Time Estimate: %d-%d days (based on assumption of %d-%d days per unique activity)</p>
                    """.formatted(bestCaseEstimate, worstCaseEstimate, BEST_CASE_ACTIVITY_TIME,
                    WORST_CASE_ACTIVITY_TIME));
            html.append(generateUniqueActivityTypeBreakdown());
        }
    }

    private String formatUnhandledActivityElement(UnhandledActivityElement element) {
        StringBuilder html = new StringBuilder();
        html.append("""
                    <div class="activity">
                """);

        // Display name if available
        if (element instanceof UnhandledActivityElement.NamedUnhandledActivityElement named) {
            html.append("        <div class=\"activity-name\">Name: ").append(named.name()).append("</div>\n");
        } else {
            html.append("        <div class=\"activity-name\">Unnamed Activity</div>\n");
        }

        // Display XML
        html.append("""
                        <p>XML Element:</p>
                """);
        html.append("        <pre>").append(escapeHtml(ConversionUtils.elementToString(element.element())))
                .append("</pre>\n");
        html.append("    </div>\n");

        return html.toString();
    }

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

    private String generateUniqueActivityTypeBreakdown() {
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

        StringBuilder html = new StringBuilder();
        html.append("<ul>\n");

        // Add unnamed activities first if any exist
        if (unnamedCount > 0) {
            html.append("    <li>Unnamed Activities: ").append(unnamedCount).append("</li>\n");
        }

        // Add named activities by type
        typeCountMap.entrySet().stream()
                .sorted(java.util.Map.Entry.comparingByKey())  // Sort by type name
                .forEach(entry ->
                        html.append("    <li>")
                                .append(entry.getKey())
                                .append(": ")
                                .append(entry.getValue())
                                .append("</li>\n")
                );

        html.append("</ul>");

        return html.toString();
    }
}
