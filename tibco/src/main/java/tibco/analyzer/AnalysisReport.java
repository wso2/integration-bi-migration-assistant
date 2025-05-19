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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public record AnalysisReport(int totalActivityCount, int unhandledActivityCount,
                             Collection<UnhandledActivityElement> unhandledActivityElements) {
    private static final String HEADING = "TIBCO migration analysis report";

    public AnalysisReport {
        assert totalActivityCount >= unhandledActivityCount;
        unhandledActivityElements = Collections.unmodifiableCollection(unhandledActivityElements);
    }

    sealed interface UnhandledActivityElement {
        Element element();

        record NamedUnhandledActivityElement(String name, Element element) implements UnhandledActivityElement {

        }

        record UnNamedUnhandledActivityElement(Element element) implements UnhandledActivityElement {

        }
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
        html.append("        <p>Unhandled Activities: ").append(unhandledActivityCount)
                .append(" (").append(calculatePercentage(unhandledActivityCount, totalActivityCount))
                .append("%)</p>\n");
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

    /**
     * Formats an unhandled activity element as HTML.
     *
     * @param element The unhandled activity element to format
     * @return The HTML representation as a string
     */
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
        Collection<UnhandledActivityElement> unhandledActivities = new ArrayList<>(report1.unhandledActivityElements());
        unhandledActivities.addAll(report2.unhandledActivityElements());
        return new AnalysisReport(
                report1.totalActivityCount() + report2.totalActivityCount(),
                report1.unhandledActivityCount() + report2.unhandledActivityCount(),
                unhandledActivities);
    }

    public static AnalysisReport empty() {
        return new AnalysisReport(0, 0, Collections.emptyList());
    }
}
