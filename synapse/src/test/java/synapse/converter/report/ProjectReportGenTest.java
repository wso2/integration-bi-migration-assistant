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

import static org.testng.Assert.assertEquals;

public class ProjectReportGenTest {

    @Test
    public void testReportGeneration() throws IOException {
        Mediator[] mediators = {
            new Mediator("unit-successful", 4, 0.9, 0.2),
            new Mediator("byte-array-to-string-transformer", 2, 0.8, 0.3),
            new Mediator("smtp-outbound-endpoint", 2, 0.7, 0.4),
            new Mediator("string-to-byte-array-transformer", 2, 0.6, 0.5),
            new Mediator("scripting-component", 2, 0.5, 0.6),
            new Mediator("spring-beans", 1, 0.4, 0.7),
            new Mediator("secure-property-placeholder-config", 1, 0.3, 0.8),
            new Mediator("db-generic-config", 1, 0.2, 0.9),
            new Mediator("sap-platform-gw-api", 1, 0.1, 1.0),
            new Mediator("file-outbound-endpoint", 2, 0.05, 0.95)
        };

        Project project = new Project(0.82, mediators);
        Estimation mediatorEstimate = new Estimation(11.0, 22.3, 34.6);
        Estimation lineEstimate = new Estimation(2 / 60.0, 5 / 60.0, 10 / 60.0);
        int totalCodeLines = 173;

        String actual = ProjectReportGen.genReport("test-project", project, totalCodeLines,
                mediatorEstimate, lineEstimate);
        String expected = Files.readString(Paths.get("src/test/resources/report/test-project-report.html"));

        // Replace the <footer>...</footer> in expected with the one from actual
        String footerRegex = "<footer><p>.*?</p></footer>";
        String actualFooter = actual.replaceAll("(?s).*?(<footer><p>.*?</p></footer>).*", "$1");
        expected = expected.replaceAll(footerRegex, actualFooter);

        assertEquals(actual, expected);
    }
}
