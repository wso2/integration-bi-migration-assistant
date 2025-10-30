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
import common.UnhandledElement;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class TibcoProjectReportTest {

    @Test
    public void testToProjectSummaryWithUnhandledActivities() throws Exception {
        // Create a test document
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();

        // Create test elements
        Element element1 = document.createElement("test-activity");
        element1.setAttribute("name", "TestActivity1");
        element1.setAttribute("type", "CustomActivity");

        Element element2 = document.createElement("test-activity");
        element2.setAttribute("name", "TestActivity2");
        element2.setAttribute("type", "CustomActivity");

        Element element3 = document.createElement("test-activity");
        element3.setAttribute("name", "TestActivity3");
        element3.setAttribute("type", "AnotherActivity");

        // Create unhandled activity elements
        List<TibcoAnalysisReport.UnhandledActivityElement> unhandledElements = new ArrayList<>();
        unhandledElements.add(new TibcoAnalysisReport.UnhandledActivityElement.NamedUnhandledActivityElement(
                "TestActivity1", "CustomActivity", element1, "test1.xml"));
        unhandledElements.add(new TibcoAnalysisReport.UnhandledActivityElement.NamedUnhandledActivityElement(
                "TestActivity2", "CustomActivity", element2, "test2.xml"));
        unhandledElements.add(new TibcoAnalysisReport.UnhandledActivityElement.NamedUnhandledActivityElement(
                "TestActivity3", "AnotherActivity", element3, "test3.xml"));

        // Create analysis report
        TibcoAnalysisReport report = new TibcoAnalysisReport(10, 3, unhandledElements, 0, new ArrayList<>());

        // Create project summary
        ProjectSummary summary = report.toProjectSummary("TestProject", "/path/to/project", "report.html");

        // Verify the summary contains unhandled activities
        Assert.assertNotNull(summary.unhandledActivities());
        Assert.assertEquals(summary.unhandledActivities().size(), 2); // 2 unique types

        // Verify CustomActivity type
        Collection<UnhandledElement> customActivities =
                summary.unhandledActivities().get("CustomActivity");
        Assert.assertNotNull(customActivities);
        Assert.assertEquals(customActivities.size(), 2);

        // Verify AnotherActivity type
        Collection<UnhandledElement> anotherActivities =
                summary.unhandledActivities().get("AnotherActivity");
        Assert.assertNotNull(anotherActivities);
        Assert.assertEquals(anotherActivities.size(), 1);

        // Verify other fields
        Assert.assertEquals(summary.projectName(), "TestProject");
        Assert.assertEquals(summary.projectPath(), "/path/to/project");
        Assert.assertEquals(summary.reportPath(), "report.html");
        Assert.assertEquals(summary.totalActivityCount(), 10);
        Assert.assertEquals(summary.unhandledActivityCount(), 3);
        Assert.assertEquals(summary.successfulConversionPercentage(), 70.0); // 70% success rate
    }

    @Test
    public void testToProjectSummaryWithNoUnhandledActivities() {
        // Create analysis report with no unhandled activities
        TibcoAnalysisReport report = new TibcoAnalysisReport(5, 0, new ArrayList<>(), 0, new ArrayList<>());

        // Create project summary
        ProjectSummary summary = report.toProjectSummary("TestProject", "/path/to/project", "report.html");

        // Verify the summary contains empty unhandled activities
        Assert.assertNotNull(summary.unhandledActivities());
        Assert.assertEquals(summary.unhandledActivities().size(), 0);

        // Verify other fields
        Assert.assertEquals(summary.totalActivityCount(), 5);
        Assert.assertEquals(summary.unhandledActivityCount(), 0);
        Assert.assertEquals(summary.successfulConversionPercentage(), 100.0); // 100% success rate
    }
}
