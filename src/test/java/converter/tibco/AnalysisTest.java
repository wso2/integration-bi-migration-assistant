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

package converter.tibco;

import converter.tibco.analyzer.ModelAnalyser;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;
import tibco.TibcoModel;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

import static org.testng.Assert.*;

public class AnalysisTest {

    @Test
    public void test() throws IOException, ParserConfigurationException, SAXException {
        Path path = Path.of("src/test/resources/tibco.helloworld/MainProcess.bwp");
        var element = TibcoToBalConverter.parseXmlFile(path.toString());
        var process = XmlToTibcoModelConverter.parseProcess(element);
        var analysisData = ModelAnalyser.analyseProcess(process);
        assertFalse(analysisData.endActivities(process).isEmpty());
        TibcoModel.Scope.Flow.Link FICOScoreTopostOut = new TibcoModel.Scope.Flow.Link("FICOScoreTopostOut");

        Collection<TibcoModel.Scope.Flow.Activity> sources = analysisData.sources(FICOScoreTopostOut);
        assertEquals(sources.size(), 1);
        TibcoModel.Scope.Flow.Activity source = sources.iterator().next();
        assertTrue(source instanceof TibcoModel.Scope.Flow.Activity.ExtActivity);

        TibcoModel.Scope.Flow.Link ExperianScoreTopostOut = new TibcoModel.Scope.Flow.Link("ExperianScoreTopostOut");
        sources = analysisData.sources(ExperianScoreTopostOut);
        assertEquals(sources.size(), 1);
        source = sources.iterator().next();
        assertTrue(source instanceof TibcoModel.Scope.Flow.Activity.ExtActivity);

        Collection<TibcoModel.Scope.Flow.Activity> destinations = analysisData.destinations(FICOScoreTopostOut);
        assertEquals(destinations.size(), 1);
        assertTrue(destinations.stream().allMatch(each -> each instanceof TibcoModel.Scope.Flow.Activity.Reply));
        assertTrue(destinations.stream().flatMap(each -> analysisData.sources(each).stream())
                .allMatch(link -> link.equals(FICOScoreTopostOut) || link.equals(ExperianScoreTopostOut)));
    }
}
