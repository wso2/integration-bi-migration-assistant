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

import common.AnalysisReport;
import org.w3c.dom.Element;
import tibco.analyzer.TibcoAnalysisReport.UnhandledActivityElement.NamedUnhandledActivityElement;
import tibco.converter.ConversionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public record TibcoAnalysisReport(int totalActivityCount, int unhandledActivityCount,
                                  Collection<UnhandledActivityElement> unhandledActivityElements) {
    private static final String REPORT_TITLE = "Migration Assessment";

    public TibcoAnalysisReport {
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
     * Creates a map of unhandled activity elements using their unique identifiers as keys.
     * - For named activities, type is used as the key
     * - For unnamed activities, each one gets a unique key "unnamed-activity-#"
     *
     * @return A map with unique activity identifiers as keys and string representations of their elements
     */
    private Map<String, String> createUnhandledElementsMap() {
        Map<String, String> elementsMap = new HashMap<>();

        // Map to store representative elements for each named type
        Map<String, NamedUnhandledActivityElement> namedTypeRepresentatives = new HashMap<>();
        int unnamedCounter = 0;

        // First pass: collect representatives for named types
        for (UnhandledActivityElement element : unhandledActivityElements) {
            if (element instanceof NamedUnhandledActivityElement named) {
                namedTypeRepresentatives.putIfAbsent(named.type(), named);
            }
        }

        // Add the representative elements for named types as strings
        for (Map.Entry<String, NamedUnhandledActivityElement> entry : namedTypeRepresentatives.entrySet()) {
            elementsMap.put(entry.getKey(), ConversionUtils.elementToString(entry.getValue().element()));
        }

        // Add unnamed elements with unique keys as strings
        for (UnhandledActivityElement element : unhandledActivityElements) {
            if (element instanceof UnhandledActivityElement.UnNamedUnhandledActivityElement) {
                elementsMap.put("unnamed-activity-" + (++unnamedCounter),
                        ConversionUtils.elementToString(element.element()));
            }
        }

        return elementsMap;
    }

    public static TibcoAnalysisReport combine(TibcoAnalysisReport report1, TibcoAnalysisReport report2) {
        Collection<UnhandledActivityElement> unhandledActivities = new HashSet<>(report1.unhandledActivityElements());
        unhandledActivities.addAll(report2.unhandledActivityElements());
        return new TibcoAnalysisReport(
                report1.totalActivityCount() + report2.totalActivityCount(),
                unhandledActivities.size(),
                unhandledActivities);
    }

    public static TibcoAnalysisReport empty() {
        return new TibcoAnalysisReport(0, 0, Collections.emptyList());
    }

    /**
     * Generates an HTML report of the TIBCO analysis.
     * Delegates the HTML generation to the generic AnalysisReport class.
     *
     * @return A string containing the HTML report
     */
    public String toHTML() {
        // Create a map of unhandled elements as strings
        Map<String, String> unhandledElementsMap = createUnhandledElementsMap();

        // Create and use a generic AnalysisReport
        AnalysisReport report = new AnalysisReport(
                REPORT_TITLE,
                totalActivityCount,
                unhandledActivityCount,
                "Activity",
                unhandledElementsMap
        );

        return report.toHTML();
    }
}
