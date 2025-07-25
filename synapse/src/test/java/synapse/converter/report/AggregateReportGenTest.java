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

package synapse.converter.report;

import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class AggregateReportGenTest {

    @Test
    public void testAggregateReportGeneration() throws IOException {
        Mediator[] project1Mediators = {
            new Mediator("unit-successful", 2, 0.9, 0.2),
            new Mediator("smtp-outbound-endpoint", 1, 0.7, 0.4),
            new Mediator("string-to-byte-array-transformer", 1, 0.6, 0.5)
        };

        Mediator[] project2Mediators = {
            new Mediator("unit-successful", 2, 0.9, 0.2),
            new Mediator("byte-array-to-string-transformer", 1, 0.8, 0.3),
            new Mediator("scripting-component", 1, 0.5, 0.6)
        };

        Project project1 = new Project(0.75, project1Mediators);
        Project project2 = new Project(0.85, project2Mediators);

        Map<String, AggregateReportGen.ProjectData> data = Map.of(
            "project1", new AggregateReportGen.ProjectData(project1, "project1/report.html", 80),
            "project2", new AggregateReportGen.ProjectData(project2, "project2/report.html", 90)
        );

        Estimation mediatorEstimate = new Estimation(11.0, 22.3, 34.6);
        Estimation lineEstimate = new Estimation(2 / 60.0, 5 / 60.0, 10 / 60.0);

        AggregateReportGen generator = new AggregateReportGen();
        String actual = generator.generateReport(data, mediatorEstimate, lineEstimate);

        String expected = Files.readString(Paths.get("src/test/resources/report/test-aggregate-report.html"));

        // Replace the <footer>...</footer> in expected with the one from actual
        String footerRegex = "<footer><p>.*?</p></footer>";
        String actualFooter = actual.replaceAll("(?s).*?(<footer><p>.*?</p></footer>).*", "$1");
        expected = expected.replaceAll(footerRegex, actualFooter);

        assertEquals(actual, expected);
    }
}
