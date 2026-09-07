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

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Logger;

import common.LoggingUtils;
import tibco.ConversionContext;
import tibco.ProjectConversionContext;
import tibco.model.NameSpace;
import tibco.model.Process5;
import tibco.model.Process5.ExplicitTransitionGroup.InlineActivity;
import tibco.model.Resource;
import tibco.model.Scope;
import tibco.model.Type;
import tibco.model.XSD;
import tibco.util.TestUtils;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static tibco.converter.TibcoConverter.createVerboseLogger;

public class XmlToModelTests {

    private static final ProjectConversionContext cx;
    static {
        Logger logger = createVerboseLogger("test");
        Consumer<String> stateCallback = LoggingUtils.wrapLoggerForStateCallback(logger);
        Consumer<String> logCallback = LoggingUtils.wrapLoggerForStateCallback(logger);
        ConversionContext conversionContext = new ConversionContext(
                "testOrg", false, true, stateCallback, logCallback);
        cx = new ProjectConversionContext(conversionContext, "test");
    }
    private static final ProjectContext projectContext = new ProjectContext(cx, "test-project");
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
        Resource.HTTPSharedResource resource = XmlToTibcoModelParser
                .parseHTTPSharedResource(new ResourceContext(projectContext, "test"), "test",
                        TestUtils.stringToElement(xmlText))
                .orElseThrow();
        assertEquals(resource.name(), "test");
        assertEquals(resource.host().get(), "localhost");
        assertEquals(resource.port().get(), 9090);
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
        Optional<tibco.model.Process> processOpt = XmlToTibcoModelParser.parseProcess(getProcessContext(),
                TestUtils.stringToElement(processXml));
        assertTrue(processOpt.isPresent());
        Process5 process = (Process5) processOpt.get();
        assertEquals(process.name(), "Processes/MainProcessStarter.process");
        Process5.ExplicitTransitionGroup transitionGroup = process.transitionGroup();
        assertEquals(transitionGroup.startActivity().get().name(), "HTTP Receiver");
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
        Scope.Flow.Activity actual = XmlToTibcoModelParser.parseActivity(getProcessContext(), element).get();
        InlineActivity.MapperActivity expected = new InlineActivity.MapperActivity(element,
                "Failed tests count",
                new Scope.Flow.Activity.InputBinding.CompleteBinding(
                        new Scope.Flow.Activity.Expression.XSLT(
                                """
                                        <?xml version="1.0" encoding="UTF-8"?>
                                        																						<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
                                        	 <xsl:template name="Transform0" match="/">
                                        		<failedTestsCount>
                                        		<xsl:value-of select="count($runAllTests/ns:test-suites-results-msg/test-suites-results/ns3:test-suites-results//ns3:test-failure)"/>
                                        	</failedTestsCount>
                                        	</xsl:template>
                                        </xsl:stylesheet>
                                        		""")),
                ANON_PROCESS);
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
        Scope.Flow.Activity actual = XmlToTibcoModelParser.parseActivity(getProcessContext(), element).get();
        InlineActivity.WriteLog expected = new InlineActivity.WriteLog(element, "Log",
                new Scope.Flow.Activity.InputBinding.CompleteBinding(
                        new Scope.Flow.Activity.Expression.XSLT("""
                                <?xml version="1.0" encoding="UTF-8"?>
                                <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
                                	 <xsl:template name="Transform0" match="/">
                                		<ns:ActivityInput>
                                			<message>
                                				<xsl:value-of select="$JMS-Queue-Receiver/ns1:ActivityOutput/Body"/>
                                			</message>
                                		</ns:ActivityInput>
                                	</xsl:template>
                                </xsl:stylesheet>
                                """)),
                ANON_PROCESS);
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
        Scope.Flow.Activity actual = XmlToTibcoModelParser.parseActivity(getProcessContext(), element).get();
        InlineActivity.AssignActivity expected = new InlineActivity.AssignActivity(element, "Assign", "Error",
                new Scope.Flow.Activity.InputBinding.CompleteBinding(
                        new Scope.Flow.Activity.Expression.XSLT("""
                                <?xml version="1.0" encoding="UTF-8"?>
                                <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
                                	 <xsl:template name="Transform0" match="/">
                                		<Error>
                                			<msg>
                                				<xsl:value-of select="$_error/ns:ErrorReport/Msg"/>
                                			</msg>
                                		</Error>
                                	</xsl:template>
                                </xsl:stylesheet>
                                """)),
                ANON_PROCESS);
        assertEquals(actual, expected);
    }

    @Test
    public void testXSLTWithNamespaces() throws Exception {
        String activityXml = """
                <pd:activity name="TestActivity">
                	<pd:type>com.tibco.pe.core.AssignActivity</pd:type>
                	<pd:resourceType>ae.activities.assignActivity</pd:resourceType>
                	<pd:x>204</pd:x>
                	<pd:y>224</pd:y>
                	<config>
                		<variableName>TestVar</variableName>
                	</config>
                	<pd:inputBindings>
                		<TestElement>
                			<xsl:value-of select="$input/test:data"/>
                		</TestElement>
                	</pd:inputBindings>
                </pd:activity>
                """;

        Element element = TestUtils.stringToElement(activityXml);
        ProcessContext processContext = getProcessContext();
        // Add namespaces to the context
        processContext.nameSpaces = List.of(
                new NameSpace("test", "http://test.example.com"),
                new NameSpace("ns1", "http://ns1.example.com"));

        InlineActivity actual = (InlineActivity) XmlToTibcoModelParser.parseActivity(processContext, element).get();

        // Verify that the XSLT contains the namespaces
        String xsltContent = ((Scope.Flow.Activity.InputBinding.CompleteBinding) actual.inputBinding()).expression()
                .toString();
        assert xsltContent.contains("xmlns:test=\"http://test.example.com\"");
        assert xsltContent.contains("xmlns:ns1=\"http://ns1.example.com\"");
        assert xsltContent.contains("xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"");
        assert !xsltContent.contains("xmlns:tns=\"http://xmlns.example.com\"");
    }

    @Test
    public void testParseSchemaIsolatesUnsupportedField() throws Exception {
        String schemaXml = """
                <xs:schema attributeFormDefault="unqualified"
                            elementFormDefault="qualified"
                            targetNamespace="http://www.tibco.com/pe/EngineTypes"
                            xmlns:tns="http://www.tibco.com/pe/EngineTypes" xmlns:xs="http://www.w3.org/2001/XMLSchema">
                            <xs:complexType name="ErrorReport">
                                <xs:sequence>
                                    <xs:element name="StackTrace" type="xs:string"/>
                                    <xs:element name="Msg" type="xs:string"/>
                                    <xs:element minOccurs="0" name="Data" type="tns:anydata"/>
                                </xs:sequence>
                            </xs:complexType>
                            <xs:complexType name="anydata">
                                <xs:sequence>
                                    <xs:any namespace="##any" processContents="lax"/>
                                </xs:sequence>
                            </xs:complexType>
                        </xs:schema>
                """;
        Optional<Type.Schema> schemaOpt = XmlToTibcoModelParser.parseSchema(projectContext,
                TestUtils.stringToElement(schemaXml));
        assertTrue(schemaOpt.isPresent());
        Type.Schema schema = schemaOpt.get();
        Type.Schema.SchemaXsdType errorReport = schema.xsdTypes().stream()
                .filter(each -> each.name().equals("ErrorReport")).findFirst().orElseThrow();
        XSD.XSDType.ComplexType complexType = (XSD.XSDType.ComplexType) errorReport.type();
        List<XSD.Element> fields = complexType.body().elements();
        assertEquals(fields.size(), 3);
        XSD.Element dataField = fields.stream().filter(each -> each.name().equals("Data")).findFirst().orElseThrow();
        assertEquals(dataField.type(), XSD.XSDType.BasicXSDType.ANY);

        common.BallerinaModel.TypeDesc.RecordTypeDesc recordTypeDesc =
                (common.BallerinaModel.TypeDesc.RecordTypeDesc) tibco.converter.ConversionUtils.toTypeDesc(complexType);
        common.BallerinaModel.TypeDesc.RecordTypeDesc.RecordField dataRecordField = recordTypeDesc.fields().stream()
                .filter(each -> each.name().equals("Data")).findFirst().orElseThrow();
        assertTrue(dataRecordField.comment().isPresent());
        assertTrue(dataRecordField.toString().endsWith("// FIXME: unsupported XSD type, defaulted to anydata"));
    }

    @Test
    public void testParseSchemaPreservesMinOccurs() throws Exception {
        String schemaXml = """
                <xs:schema attributeFormDefault="unqualified"
                            elementFormDefault="qualified"
                            targetNamespace="http://www.tibco.com/pe/EngineTypes"
                            xmlns:tns="http://www.tibco.com/pe/EngineTypes" xmlns:xs="http://www.w3.org/2001/XMLSchema">
                            <xs:complexType name="ErrorReport">
                                <xs:sequence>
                                    <xs:element name="StackTrace" type="xs:string"/>
                                    <xs:element name="Msg" type="xs:string"/>
                                    <xs:element name="Data" type="tns:anydata"/>
                                </xs:sequence>
                            </xs:complexType>
                            <xs:complexType name="anydata">
                                <xs:sequence>
                                    <xs:any namespace="##any" processContents="lax"/>
                                </xs:sequence>
                            </xs:complexType>
                        </xs:schema>
                """;
        Optional<Type.Schema> schemaOpt = XmlToTibcoModelParser.parseSchema(projectContext,
                TestUtils.stringToElement(schemaXml));
        assertTrue(schemaOpt.isPresent());
        Type.Schema schema = schemaOpt.get();
        Type.Schema.SchemaXsdType errorReport = schema.xsdTypes().stream()
                .filter(each -> each.name().equals("ErrorReport")).findFirst().orElseThrow();
        XSD.XSDType.ComplexType complexType = (XSD.XSDType.ComplexType) errorReport.type();
        XSD.Element dataField = complexType.body().elements().stream()
                .filter(each -> each.name().equals("Data")).findFirst().orElseThrow();
        assertEquals(dataField.type(), XSD.XSDType.BasicXSDType.ANY);
        assertTrue(dataField.minOccur().isEmpty());

        common.BallerinaModel.TypeDesc.RecordTypeDesc recordTypeDesc =
                (common.BallerinaModel.TypeDesc.RecordTypeDesc) tibco.converter.ConversionUtils.toTypeDesc(complexType);
        common.BallerinaModel.TypeDesc.RecordTypeDesc.RecordField dataRecordField = recordTypeDesc.fields().stream()
                .filter(each -> each.name().equals("Data")).findFirst().orElseThrow();
        assertTrue(!dataRecordField.isOptional());
    }

    @Test
    public void testParseSchemaSupportsDateType() throws Exception {
        String schemaXml = """
                <xs:schema attributeFormDefault="unqualified"
                            elementFormDefault="qualified"
                            targetNamespace="http://tns.tibco.com/bw/activity/timer/xsd/output"
                            xmlns:tns="http://tns.tibco.com/bw/activity/timer/xsd/output" xmlns:xs="http://www.w3.org/2001/XMLSchema">
                            <xs:complexType name="TimerOutputSchemaType">
                                <xs:sequence>
                                    <xs:element form="unqualified" name="Now" type="xs:long"/>
                                    <xs:element form="unqualified" name="Date" type="xs:date"/>
                                </xs:sequence>
                            </xs:complexType>
                            <xs:element name="TimerOutputSchema" type="tns:TimerOutputSchemaType"/>
                        </xs:schema>
                """;
        Optional<Type.Schema> schemaOpt = XmlToTibcoModelParser.parseSchema(projectContext,
                TestUtils.stringToElement(schemaXml));
        assertTrue(schemaOpt.isPresent());
        Type.Schema.SchemaXsdType timerOutput = schemaOpt.get().xsdTypes().stream()
                .filter(each -> each.name().equals("TimerOutputSchemaType")).findFirst().orElseThrow();
        XSD.XSDType.ComplexType complexType = (XSD.XSDType.ComplexType) timerOutput.type();
        XSD.Element dateField = complexType.body().elements().stream()
                .filter(each -> each.name().equals("Date")).findFirst().orElseThrow();
        assertEquals(dateField.type(), XSD.XSDType.BasicXSDType.DATE);

        assertTrue(tibco.converter.ConversionUtils.usesTimeType(complexType));
        assertEquals(tibco.converter.ConversionUtils.toTypeDesc(dateField.type()),
                new common.BallerinaModel.TypeDesc.TypeReference("time:Date"));
    }

    @Test
    public void testParseSchemaSupportsDateTimeType() throws Exception {
        String schemaXml = """
                <xs:schema attributeFormDefault="unqualified"
                            elementFormDefault="qualified"
                            targetNamespace="http://example.com/effective"
                            xmlns:tns="http://example.com/effective" xmlns:xs="http://www.w3.org/2001/XMLSchema">
                            <xs:complexType name="EffectiveType">
                                <xs:sequence>
                                    <xs:element minOccurs="0" name="EffectiveEndDate" type="dateTime"/>
                                </xs:sequence>
                            </xs:complexType>
                        </xs:schema>
                """;
        Optional<Type.Schema> schemaOpt = XmlToTibcoModelParser.parseSchema(projectContext,
                TestUtils.stringToElement(schemaXml));
        assertTrue(schemaOpt.isPresent());
        Type.Schema.SchemaXsdType effectiveType = schemaOpt.get().xsdTypes().stream()
                .filter(each -> each.name().equals("EffectiveType")).findFirst().orElseThrow();
        XSD.XSDType.ComplexType complexType = (XSD.XSDType.ComplexType) effectiveType.type();
        XSD.Element dateTimeField = complexType.body().elements().stream()
                .filter(each -> each.name().equals("EffectiveEndDate")).findFirst().orElseThrow();
        assertEquals(dateTimeField.type(), XSD.XSDType.BasicXSDType.DATETIME);

        assertTrue(tibco.converter.ConversionUtils.usesTimeType(complexType));
        assertEquals(tibco.converter.ConversionUtils.toTypeDesc(dateTimeField.type()),
                new common.BallerinaModel.TypeDesc.TypeReference("time:Civil"));
    }

    @Test
    public void testJsonParserIsolatesUnsupportedSchema() throws Exception {
        String activityXml = """
                <pd:activity name="Parse JSON">
                	<pd:type>com.tibco.plugin.json.activities.JSONParserActivity</pd:type>
                	<pd:resourceType>ae.activities.JSONParserActivity</pd:resourceType>
                	<config>
                		<SchemaType>xsdType</SchemaType>
                		<ActivityOutputEditor>
                			<xsd:element name="Foo" type="tns:UnknownType"/>
                		</ActivityOutputEditor>
                	</config>
                	<pd:inputBindings>
                		<ns1:ActivityInputClass>
                			<jsonString>
                				<xsl:value-of select="$Start/foo"/>
                			</jsonString>
                		</ns1:ActivityInputClass>
                	</pd:inputBindings>
                </pd:activity>
                """;

        Element element = TestUtils.stringToElement(activityXml);
        Scope.Flow.Activity actual = XmlToTibcoModelParser.parseActivity(getProcessContext(), element).get();
        InlineActivity.JSONParser jsonParser = (InlineActivity.JSONParser) actual;
        assertTrue(jsonParser.targetType().isEmpty());
        assertTrue(jsonParser.inputBinding() != null);
    }
}
