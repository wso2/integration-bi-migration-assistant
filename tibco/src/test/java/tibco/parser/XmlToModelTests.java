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

package tibco.parser;

import org.testng.annotations.Test;
import org.w3c.dom.Element;

import tibco.model.Process5;
import tibco.model.Process5.ExplicitTransitionGroup.InlineActivity;
import tibco.model.Resource;
import tibco.model.Scope;
import tibco.util.TestUtils;

import static org.testng.Assert.assertEquals;


public class XmlToModelTests {

    private static final ProjectContext projectContext = new ProjectContext();
    private static final String ANON_PROCESS = "ANON.proc";

    private static ProcessContext getProcessContext() {
        return new ProcessContext(projectContext, ANON_PROCESS);
    }

    @Test
    public void testParseHttpSharedResource() throws Exception {
        String xmlText = """
                <ns0:httpSharedResource xmlns:ns0="www.tibco.com/shared/HTTPConnection">
                    <config>
                        <Host>localhost</Host>
                        <serverType>Tomcat</serverType>
                        <Port>9090</Port>
                    </config>
                </ns0:httpSharedResource>
                """;
        Resource.HTTPSharedResource resource =
                XmlToTibcoModelParser.parseHTTPSharedResource(new ResourceContext(projectContext, "test"),
                        TestUtils.stringToElement(xmlText));
        assertEquals(resource.name(), "test");
        assertEquals(resource.host(), "localhost");
        assertEquals(resource.port(), 9090);
    }

    @Test
    public void testParseInlineActivityProcess() throws Exception {
        String processXml = """
                <pd:ProcessDefinition xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
                    <pd:name>Processes/MainProcessStarter.process</pd:name>
                    <pd:startName>HTTP Receiver</pd:startName>
                    <pd:returnBindings/>
                    <pd:starter name="HTTP Receiver">
                        <pd:type>com.tibco.plugin.http.HTTPEventSource</pd:type>
                        <config>
                            <sharedChannel>/SharedResources/GeneralConnection.sharedhttp</sharedChannel>
                        </config>
                        <pd:inputBindings/>
                    </pd:starter>
                    <pd:endName>End</pd:endName>
                    <pd:transition>
                        <pd:from>HTTP Receiver</pd:from>
                        <pd:to>GetProcesName</pd:to>
                        <pd:lineType>Default</pd:lineType>
                        <pd:lineColor>-16777216</pd:lineColor>
                        <pd:conditionType>always</pd:conditionType>
                    </pd:transition>
                    <pd:transition>
                        <pd:from>Start</pd:from>
                        <pd:to>HTTP Receiver</pd:to>
                    </pd:transition>
                </pd:ProcessDefinition>
                """;
        Process5 process =
                (Process5) XmlToTibcoModelParser.parseProcess(getProcessContext(),
                        TestUtils.stringToElement(processXml));
        assertEquals(process.name(), "Processes/MainProcessStarter.process");
        Process5.ExplicitTransitionGroup transitionGroup = process.transitionGroup();
        assertEquals(transitionGroup.startActivity().name(), "HTTP Receiver");
    }

    @Test
    public void testParseMapperActivity() throws Exception {
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

        Element element = TestUtils.stringToElement(activityXml);
        InlineActivity actual =
                XmlToTibcoModelParser.parseInlineActivity(getProcessContext(), element);
        InlineActivity.MapperActivity expected =
                new InlineActivity.MapperActivity(element,
                        "Failed tests count",
                        new Scope.Flow.Activity.InputBinding.CompleteBinding(
                                new Scope.Flow.Activity.Expression.XSLT("""
                                <?xml version="1.0" encoding="UTF-8"?>
                                <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0">
                                     <xsl:template name="Transform0" match="/">
                                        <failedTestsCount>
                                        <xsl:value-of select="count($runAllTests/ns:test-suites-results-msg/test-suites-results/ns3:test-suites-results//ns3:test-failure)"/>
                                    </failedTestsCount>
                                    </xsl:template>
                                </xsl:stylesheet>
                                        """)), ANON_PROCESS);
        assertEquals(actual, expected);
    }


    @Test
    public void testParseWriteToLogActivity() throws Exception {
        String activityXml = """
                <pd:activity name="Log">
                    <pd:type>com.tibco.pe.core.WriteToLogActivity</pd:type>
                    <pd:resourceType>ae.activities.log</pd:resourceType>
                    <pd:x>255</pd:x>
                    <pd:y>56</pd:y>
                    <config>
                        <role>User</role>
                    </config>
                    <pd:inputBindings>
                        <ns:ActivityInput>
                            <message>
                                <xsl:value-of select="$JMS-Queue-Receiver/ns1:ActivityOutput/Body"/>
                            </message>
                        </ns:ActivityInput>
                    </pd:inputBindings>
                </pd:activity>
                """;

        Element element = TestUtils.stringToElement(activityXml);
        InlineActivity actual = XmlToTibcoModelParser.parseInlineActivity(getProcessContext(), element);
        InlineActivity.WriteLog expected = new InlineActivity.WriteLog(element, "Log",
                new Scope.Flow.Activity.InputBinding.CompleteBinding(
                        new Scope.Flow.Activity.Expression.XSLT("""
                                <?xml version="1.0" encoding="UTF-8"?>
                                <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0">
                                     <xsl:template name="Transform0" match="/">
                                        <ns:ActivityInput>
                                            <message>
                                                <xsl:value-of select="$JMS-Queue-Receiver/ns1:ActivityOutput/Body"/>
                                            </message>
                                        </ns:ActivityInput>
                                    </xsl:template>
                                </xsl:stylesheet>
                                """)), ANON_PROCESS);
        assertEquals(actual, expected);
    }

    @Test
    public void testParseAssignActivity() throws Exception {
        String activityXml = """
                <pd:activity name="Assign">
                    <pd:type>com.tibco.pe.core.AssignActivity</pd:type>
                    <pd:resourceType>ae.activities.assignActivity</pd:resourceType>
                    <pd:x>204</pd:x>
                    <pd:y>224</pd:y>
                    <config>
                        <variableName>Error</variableName>
                    </config>
                    <pd:inputBindings>
                        <Error>
                            <msg>
                                <xsl:value-of select="$_error/ns:ErrorReport/Msg"/>
                            </msg>
                        </Error>
                    </pd:inputBindings>
                </pd:activity>
                """;

        Element element = TestUtils.stringToElement(activityXml);
        InlineActivity actual = XmlToTibcoModelParser.parseInlineActivity(getProcessContext(), element);
        InlineActivity.AssignActivity expected = new InlineActivity.AssignActivity(element, "Assign", "Error",
                new Scope.Flow.Activity.InputBinding.CompleteBinding(
                        new Scope.Flow.Activity.Expression.XSLT("""
                                <?xml version="1.0" encoding="UTF-8"?>
                                <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0">
                                     <xsl:template name="Transform0" match="/">
                                        <Error>
                                            <msg>
                                                <xsl:value-of select="$_error/ns:ErrorReport/Msg"/>
                                            </msg>
                                        </Error>
                                    </xsl:template>
                                </xsl:stylesheet>
                                """)), ANON_PROCESS);
        assertEquals(actual, expected);
    }
}
