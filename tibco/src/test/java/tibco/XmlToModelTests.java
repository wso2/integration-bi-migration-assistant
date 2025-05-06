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

package tibco;

import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import tibco.TibcoModel.Process.ExplicitTransitionGroup.InlineActivity;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;


public class XmlToModelTests {

    /**
     * Converts a XML string to an Element object
     *
     * @param xmlString the XML string to convert
     * @return the Element object
     * @throws ParserConfigurationException if a DocumentBuilder cannot be created
     * @throws IOException                  if there is an error reading the string
     * @throws SAXException                 if there is an error parsing the XML
     */
    private Element stringToElement(String xmlString) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xmlString))).getDocumentElement();
    }

    @Test
    public void testParseInlineActivity() throws Exception {
        // Setup
        String activityXml = """
                 <pd:activity name="Failed tests count">
                        <pd:type>com.tibco.plugin.mapper.MapperActivity</pd:type>
                        <pd:resourceType>ae.activities.MapperActivity</pd:resourceType>
                        <pd:x>343</pd:x>
                        <pd:y>290</pd:y>
                        <config>
                            <element>
                                <xsd:element name="failedTestsCount" type="xsd:int"/>
                            </element>
                        </config>
                        <pd:inputBindings>
                            <failedTestsCount>
                                <xsl:value-of select="count($runAllTests/ns:test-suites-results-msg/test-suites-results/ns3:test-suites-results//ns3:test-failure)"/>
                            </failedTestsCount>
                        </pd:inputBindings>
                    </pd:activity>\
                """;

        Element activityElement = stringToElement(activityXml);

        XmlToTibcoModelConverter.ParseContext parseContext = new XmlToTibcoModelConverter.ParseContext();
        InlineActivity actual = XmlToTibcoModelConverter.parseInlineActivity(parseContext, activityElement);
        InlineActivity.MapperActivity expected = new InlineActivity.MapperActivity("Failed tests count",
                new TibcoModel.Scope.Flow.Activity.InputBinding.CompleteBinding(
                        new TibcoModel.Scope.Flow.Activity.Expression.XSLT("""
                                <?xml version="1.0" encoding="UTF-8"?>
                                <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0">
                                     <xsl:template name="Transform0" match="/">
                                        <failedTestsCount>
                                        <xsl:value-of select="count($runAllTests/ns:test-suites-results-msg/test-suites-results/ns3:test-suites-results//ns3:test-failure)"/>
                                    </failedTestsCount>
                                    </xsl:template>
                                </xsl:stylesheet>
                                """)));
        assertEquals(actual, expected);
    }
}
