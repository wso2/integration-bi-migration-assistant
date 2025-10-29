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

import common.ProjectSummary;
import common.TimeEstimation;
import common.UnhandledElement;
import common.report.ProjectReport;
import org.w3c.dom.Element;
import tibco.analyzer.TibcoAnalysisReport.UnhandledActivityElement.NamedUnhandledActivityElement;
import tibco.converter.ConversionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class TibcoAnalysisReport {

    private static final String REPORT_TITLE = "Migration Assessment";

    private final int totalActivityCount;
    private final int unhandledActivityCount;
    private final Collection<UnhandledActivityElement> unhandledActivityElements;
    private final int partiallySupportedActivityCount;
    private final Collection<PartiallySupportedActivityElement> partiallySupportedActivityElements;
    private long lineCount = 0;

    public TibcoAnalysisReport(int totalActivityCount, int unhandledActivityCount,
                               Collection<UnhandledActivityElement> unhandledActivityElements,
                               int partiallySupportedActivityCount,
                               Collection<PartiallySupportedActivityElement> partiallySupportedActivityElements) {
        assert totalActivityCount >= unhandledActivityCount;
        unhandledActivityElements = Collections.unmodifiableCollection(unhandledActivityElements);
        partiallySupportedActivityElements = Collections.unmodifiableCollection(partiallySupportedActivityElements);
        this.totalActivityCount = totalActivityCount;
        this.unhandledActivityCount = unhandledActivityCount;
        this.unhandledActivityElements = unhandledActivityElements;
        this.partiallySupportedActivityCount = partiallySupportedActivityCount;
        this.partiallySupportedActivityElements = partiallySupportedActivityElements;
    }

    public sealed
    interface UnhandledActivityElement {

        Element element();

        String fileName();

        record NamedUnhandledActivityElement(String name, String type,
                                             Element element, String fileName) implements UnhandledActivityElement {

            @Override
            public boolean equals(Object obj) {
                if (!(obj instanceof UnhandledActivityElement other)) {
                    return false;
                }
                return other.element().equals(this.element);
            }

            @Override
            public int hashCode() {
                return Objects.hash(element);
            }
        }

        record UnNamedUnhandledActivityElement(Element element, String fileName) implements UnhandledActivityElement {

            @Override
            public boolean equals(Object obj) {
                if (!(obj instanceof UnhandledActivityElement other)) {
                    return false;
                }
                return other.element().equals(this.element);
            }

            @Override
            public int hashCode() {
                return Objects.hash(element);
            }
        }
    }

    public sealed
    interface PartiallySupportedActivityElement {

        Element element();

        String fileName();

        record NamedPartiallySupportedActivityElement(String name, String type,
                                                      Element element, String fileName)
                implements PartiallySupportedActivityElement {

            @Override
            public boolean equals(Object obj) {
                if (!(obj instanceof PartiallySupportedActivityElement other)) {
                    return false;
                }
                return other.element().equals(this.element);
            }
        }

        record UnNamedPartiallySupportedActivityElement(Element element, String fileName)
                implements PartiallySupportedActivityElement {

            @Override
            public boolean equals(Object obj) {
                if (!(obj instanceof PartiallySupportedActivityElement other)) {
                    return false;
                }
                return other.element().equals(this.element);
            }
        }
    }

    /**
     * Creates a map of unhandled activity elements using their kind as keys. - For named activities, they are grouped
     * by type - For unnamed activities, each one gets a unique key "unnamed-activity-#" as they are treated as unique
     *
     * @return A map with activity kinds as keys and collections of string representations as values
     */
    private Map<String, Collection<UnhandledElement>> createUnhandledElementsMap() {
        Map<String, Collection<UnhandledElement>> unhandledElementsMap = new HashMap<>();
        for (UnhandledActivityElement unhandledActivityElement : unhandledActivityElements) {
            String code = ConversionUtils.elementToString(unhandledActivityElement.element());
            String fileName = unhandledActivityElement.fileName();
            switch (unhandledActivityElement) {
                case NamedUnhandledActivityElement namedElement -> {
                    String type = namedElement.type();
                    String name = namedElement.name();
                    unhandledElementsMap
                            .computeIfAbsent(type, k -> new HashSet<>())
                            .add(new UnhandledElement(code, Optional.of(name), fileName));
                }
                case UnhandledActivityElement.UnNamedUnhandledActivityElement ignored -> {
                    String uniqueKey = "unnamed-activity-" + unhandledElementsMap.size();
                    unhandledElementsMap.put(uniqueKey, List.of(
                            new UnhandledElement(code, Optional.empty(), fileName)));
                }
            }
        }
        return unhandledElementsMap;
    }

    /**
     * Creates a map of partially supported activity elements using their kind as keys. - For named activities, they are
     * grouped by type - For unnamed activities, each one gets a unique key "unnamed-activity-#" as they are treated as
     * unique
     *
     * @return A map with activity kinds as keys and collections of string representations as values
     */
    private Map<String, Collection<UnhandledElement>> createPartiallySupportedElementsMap() {
        Map<String, Collection<UnhandledElement>> partiallySupportedElementsMap = new HashMap<>();
        for (PartiallySupportedActivityElement partiallySupportedActivityElement : partiallySupportedActivityElements) {
            String code = ConversionUtils.elementToString(partiallySupportedActivityElement.element());
            String fileName = partiallySupportedActivityElement.fileName();
            switch (partiallySupportedActivityElement) {
                case PartiallySupportedActivityElement.NamedPartiallySupportedActivityElement namedElement -> {
                    String type = namedElement.type();
                    String name = namedElement.name();
                    partiallySupportedElementsMap
                            .computeIfAbsent(type, k -> new HashSet<>())
                            .add(new UnhandledElement(code, Optional.of(name), fileName));
                }
                case PartiallySupportedActivityElement.UnNamedPartiallySupportedActivityElement ignored -> {
                    String uniqueKey = "unnamed-activity-" + partiallySupportedElementsMap.size();
                    partiallySupportedElementsMap.put(uniqueKey, List.of(
                            new UnhandledElement(code, Optional.empty(), fileName)));
                }
            }
        }
        return partiallySupportedElementsMap;
    }

    public static TibcoAnalysisReport combine(TibcoAnalysisReport report1, TibcoAnalysisReport report2) {
        Collection<UnhandledActivityElement> unhandledActivities = new HashSet<>(report1.unhandledActivityElements());
        unhandledActivities.addAll(report2.unhandledActivityElements());

        Collection<PartiallySupportedActivityElement> partiallySupportedActivities =
                new HashSet<>(report1.partiallySupportedActivityElements());
        partiallySupportedActivities.addAll(report2.partiallySupportedActivityElements());

        return new TibcoAnalysisReport(
                report1.totalActivityCount() + report2.totalActivityCount(),
                unhandledActivities.size(),
                unhandledActivities,
                partiallySupportedActivities.size(),
                partiallySupportedActivities);
    }

    public static TibcoAnalysisReport empty() {
        return new TibcoAnalysisReport(0, 0, Collections.emptyList(), 0, Collections.emptyList());
    }

    private static TimeEstimation timeEstimationPerElement(UnhandledElement element,
                                                           int count) {
        assert count > 0 : "There should be at least one element to estimate time";
        TimeEstimation estimation = new TimeEstimation(1, 2, 3);
        if (count == 1) {
            return estimation;
        }
        int repeatedCount = count - 1;
        TimeEstimation repeatedEstimation = new TimeEstimation(
                1.0 / 8.0, // 1 hour = 1/8 day
                2.0 / 8.0, // 2 hours = 2/8 day
                4.0 / 8.0 // 4 hours = 4/8 day
        ).prod(repeatedCount);
        return TimeEstimation.sum(estimation, repeatedEstimation);
    }

    private static TimeEstimation getManualConversionTimeEstimation(
            Map<String, Collection<UnhandledElement>> unhandledElementsMap) {
        TimeEstimation estimation = new TimeEstimation(0, 0, 0);

        for (Collection<UnhandledElement> elements : unhandledElementsMap.values()) {
            int count = elements.size();
            if (count > 0) {
                estimation =
                        TimeEstimation.sum(estimation, timeEstimationPerElement(elements.iterator().next(), count));
            }
        }

        return estimation;
    }

    /**
     * Generates a JSON report of the TIBCO analysis.
     *
     * @return A string containing the JSON report
     */
    public String toJSON() {
        double coveragePercentage = 100.0
                - (totalActivityCount > 0 ?
                (double) unhandledActivityCount / totalActivityCount * 100.0 : 0.0);

        String coverageLevel;
        if (coveragePercentage >= 90) {
            coverageLevel = "high";
        } else if (coveragePercentage >= 75) {
            coverageLevel = "medium";
        } else {
            coverageLevel = "low";
        }

        return """
                {
                    "coverageOverview": {
                        "unitName": "activity",
                        "coveragePercentage": %d,
                        "coverageLevel": "%s",
                        "totalElements": %d,
                        "migratableElements": %d,
                        "nonMigratableElements": %d
                    }
                }""".formatted(
                Math.round(coveragePercentage),
                coverageLevel,
                totalActivityCount,
                totalActivityCount - unhandledActivityCount,
                unhandledActivityCount
        );
    }

    /**
     * Generates an HTML report of the TIBCO analysis. Delegates the HTML generation to the generic ProjectReport
     * class.
     *
     * @return A string containing the HTML report
     */
    public String toHTML() {
        // Create a map of unhandled elements grouped by their kind
        Map<String, Collection<UnhandledElement>> unhandledElementsMap = createUnhandledElementsMap();

        TimeEstimation manualConversionEstimation = getManualConversionTimeEstimation(unhandledElementsMap);

        // Create maps for partially supported activities
        Map<String, Collection<UnhandledElement>> partiallySupportedElementsMap =
                createPartiallySupportedElementsMap();

        // Create and use a generic ProjectReport
        ProjectReport report = new ProjectReport(
                REPORT_TITLE,
                totalActivityCount,
                unhandledActivityCount,
                "Activity",
                unhandledElementsMap,
                partiallySupportedActivityCount,
                partiallySupportedElementsMap,
                manualConversionEstimation,
                lineCount
        );

        return report.toHTML();
    }

    /**
     * Creates a ProjectSummary from this TibcoAnalysisReport.
     *
     * @param projectName The name of the project
     * @param projectPath The path to the project
     * @param reportPath  The path to the individual report file
     * @return A ProjectSummary instance
     */
    public ProjectSummary toProjectSummary(String projectName, String projectPath, String reportPath) {
        double conversionPercentage = 100.0
                - (totalActivityCount > 0 ?
                (double) unhandledActivityCount / totalActivityCount * 100.0 : 0.0);

        // Create a map of unhandled elements grouped by their kind
        Map<String, Collection<UnhandledElement>> unhandledElementsMap = createUnhandledElementsMap();

        // Get time estimation using the separate methods
        TimeEstimation manualConversionEstimation = getManualConversionTimeEstimation(unhandledElementsMap);

        Map<String, Collection<UnhandledElement>> partiallySupportedElementsMap =
                createPartiallySupportedElementsMap();

        return new ProjectSummary(
                projectName,
                projectPath,
                reportPath,
                totalActivityCount,
                unhandledActivityCount,
                manualConversionEstimation,
                lineCount,
                conversionPercentage,
                unhandledElementsMap,
                partiallySupportedElementsMap
        );
    }

    public int totalActivityCount() {
        return totalActivityCount;
    }

    public int unhandledActivityCount() {
        return unhandledActivityCount;
    }

    public Collection<UnhandledActivityElement> unhandledActivityElements() {
        return unhandledActivityElements;
    }

    public int partiallySupportedActivityCount() {
        return partiallySupportedActivityCount;
    }

    public Collection<PartiallySupportedActivityElement> partiallySupportedActivityElements() {
        return partiallySupportedActivityElements;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (TibcoAnalysisReport) obj;
        return this.totalActivityCount == that.totalActivityCount &&
                this.unhandledActivityCount == that.unhandledActivityCount &&
                Objects.equals(this.unhandledActivityElements, that.unhandledActivityElements) &&
                this.partiallySupportedActivityCount == that.partiallySupportedActivityCount &&
                Objects.equals(this.partiallySupportedActivityElements, that.partiallySupportedActivityElements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalActivityCount, unhandledActivityCount, unhandledActivityElements,
                partiallySupportedActivityCount, partiallySupportedActivityElements);
    }

    @Override
    public String toString() {
        return "TibcoAnalysisReport[" +
                "totalActivityCount=" + totalActivityCount + ", " +
                "unhandledActivityCount=" + unhandledActivityCount + ", " +
                "unhandledActivityElements=" + unhandledActivityElements + ", " +
                "partiallySupportedActivityCount=" + partiallySupportedActivityCount + ", " +
                "partiallySupportedActivityElements=" + partiallySupportedActivityElements + ']';
    }

    public void lineCount(long count) {
        this.lineCount = count;
    }

    public long lineCount() {
        return this.lineCount;
    }

}
