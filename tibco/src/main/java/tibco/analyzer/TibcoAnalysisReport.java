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

package tibco.analyzer;

import common.AnalysisReport;
import org.w3c.dom.Element;
import tibco.analyzer.TibcoAnalysisReport.UnhandledActivityElement.NamedUnhandledActivityElement;
import tibco.converter.ConversionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record TibcoAnalysisReport(int totalActivityCount, int unhandledActivityCount,
                                  Collection<UnhandledActivityElement> unhandledActivityElements) {
    private static final String REPORT_TITLE = "Migration Assessment";

    public TibcoAnalysisReport {
        assert totalActivityCount >= unhandledActivityCount;
        unhandledActivityElements = Collections.unmodifiableCollection(unhandledActivityElements);
    }

    sealed interface UnhandledActivityElement {
        Element element();
        
        String fileName();

        record NamedUnhandledActivityElement(String name, String type,
                                             Element element, String fileName) implements UnhandledActivityElement {

        }

        record UnNamedUnhandledActivityElement(Element element, String fileName) implements UnhandledActivityElement {

        }
    }

    /**
     * Creates a map of unhandled activity elements using their kind as keys.
     * - For named activities, they are grouped by type
     * - For unnamed activities, each one gets a unique key "unnamed-activity-#" as they are treated as unique
     *
     * @return A map with activity kinds as keys and collections of string representations as values
     */
    private Map<String, Collection<AnalysisReport.UnhandledElement>> createUnhandledElementsMap() {
        Map<String, Collection<AnalysisReport.UnhandledElement>> unhandledElementsMap = new HashMap<>();
        for (UnhandledActivityElement unhandledActivityElement : unhandledActivityElements) {
            String code = ConversionUtils.elementToString(unhandledActivityElement.element());
            String fileName = unhandledActivityElement.fileName();
            switch (unhandledActivityElement) {
                case NamedUnhandledActivityElement namedElement -> {
                    String type = namedElement.type();
                    String name = namedElement.name();
                    unhandledElementsMap
                            .computeIfAbsent(type, k -> new HashSet<>())
                            .add(new AnalysisReport.UnhandledElement(code, Optional.of(name), fileName));
                }
                case UnhandledActivityElement.UnNamedUnhandledActivityElement ignored -> {
                    String uniqueKey = "unnamed-activity-" + unhandledElementsMap.size();
                    unhandledElementsMap.put(uniqueKey, List.of(
                            new AnalysisReport.UnhandledElement(code, Optional.empty(), fileName)));
                }
            }
        }
        return unhandledElementsMap;
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
        // Create a map of unhandled elements grouped by their kind
        Map<String, Collection<AnalysisReport.UnhandledElement>> unhandledElementsMap = createUnhandledElementsMap();

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
