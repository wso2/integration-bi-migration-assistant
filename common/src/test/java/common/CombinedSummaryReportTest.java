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

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CombinedSummaryReportTest {

    @Test
    public void testCombinedSummaryReportWithUnhandledActivities() {
        // Create test project summaries with unhandled activities
        Map<String, Collection<AnalysisReport.UnhandledElement>> unhandledActivities1 = new HashMap<>();
        unhandledActivities1.put("CustomActivity", List.of(
                new AnalysisReport.UnhandledElement("code1", java.util.Optional.of("Activity1"), "file1.xml"),
                new AnalysisReport.UnhandledElement("code2", java.util.Optional.of("Activity2"), "file2.xml")
        ));
        unhandledActivities1.put("AnotherActivity", List.of(
                new AnalysisReport.UnhandledElement("code3", java.util.Optional.of("Activity3"), "file3.xml")
        ));

        Map<String, Collection<AnalysisReport.UnhandledElement>> unhandledActivities2 = new HashMap<>();
        unhandledActivities2.put("CustomActivity", List.of(
                new AnalysisReport.UnhandledElement("code4", java.util.Optional.of("Activity4"), "file4.xml")
        ));

        ProjectSummary.ActivityEstimation activityEstimation1 = new ProjectSummary.ActivityEstimation(
                10, 3, new TimeEstimation(2, 4, 6));
        ProjectSummary.ActivityEstimation activityEstimation2 = new ProjectSummary.ActivityEstimation(
                5, 1, new TimeEstimation(1, 2, 3));

        ProjectSummary summary1 = new ProjectSummary(
                "Project1", "/path/to/project1", "report1.html",
                        activityEstimation1, 70.0, unhandledActivities1, new HashMap<>());
        ProjectSummary summary2 = new ProjectSummary(
                "Project2", "/path/to/project2", "report2.html",
                        activityEstimation2, 80.0, unhandledActivities2, new HashMap<>());

        // Create combined summary report
        CombinedSummaryReport combinedReport = new CombinedSummaryReport(
                "Test Combined Report", List.of(summary1, summary2));

        // Generate HTML
        String html = combinedReport.toHTML();

        // Verify the HTML contains information about unhandled activities
        Assert.assertTrue(html.contains("Currently Unsupported Elements"));
        Assert.assertTrue(html.contains("CustomActivity"));
        Assert.assertTrue(html.contains("AnotherActivity"));
        Assert.assertTrue(html.contains("Project1"));
        Assert.assertTrue(html.contains("Project2"));
        Assert.assertTrue(html.contains("Frequency"));
        Assert.assertTrue(html.contains("Projects Affected"));
    }

    @Test
    public void testCombinedSummaryReportWithNoUnhandledActivities() {
        // Create test project summaries with no unhandled activities
        ProjectSummary.ActivityEstimation activityEstimation1 = new ProjectSummary.ActivityEstimation(
                10, 0, new TimeEstimation(0, 0, 0));
        ProjectSummary.ActivityEstimation activityEstimation2 = new ProjectSummary.ActivityEstimation(
                5, 0, new TimeEstimation(0, 0, 0));

        ProjectSummary summary1 = new ProjectSummary(
                "Project1", "/path/to/project1", "report1.html",
                        activityEstimation1, 100.0, new HashMap<>(), new HashMap<>());
        ProjectSummary summary2 = new ProjectSummary(
                "Project2", "/path/to/project2", "report2.html",
                        activityEstimation2, 100.0, new HashMap<>(), new HashMap<>());

        // Create combined summary report
        CombinedSummaryReport combinedReport = new CombinedSummaryReport(
                "Test Combined Report", List.of(summary1, summary2));

        // Generate HTML
        String html = combinedReport.toHTML();

        // Verify the HTML contains the empty message
        Assert.assertTrue(html.contains("Currently Unsupported Elements"));
        Assert.assertTrue(html.contains("No unsupported elements found"));
        Assert.assertTrue(html.contains("All elements in the analyzed projects are currently supported"));
    }
}
