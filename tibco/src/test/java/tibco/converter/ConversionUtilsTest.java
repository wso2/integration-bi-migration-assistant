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

package tibco.converter;

import common.BallerinaModel;
import common.BallerinaModel.TypeDesc.RecordTypeDesc;
import common.LoggingUtils;
import org.jetbrains.annotations.NotNull;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import tibco.ConversionContext;
import tibco.ProjectConversionContext;
import tibco.analyzer.AnalysisResult;
import tibco.converter.ConversionUtils.LineCount;
import tibco.model.NameSpace;
import tibco.model.Process;
import tibco.model.Resource;
import tibco.model.Variable;
import tibco.model.XSD;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import static tibco.converter.TibcoConverter.createVerboseLogger;

public class ConversionUtilsTest {

    @Test
    public void testLineCountPureBallerina() {
        String source = """
                import ballerina/http;

                public listener http:Listener GeneralConnection = new (9090);

                service on GeneralConnection {
                    resource function 'default test() returns string {
                        return "hello";
                    }
                }
                """;

        LineCount result = ConversionUtils.lineCount(source);
        Assert.assertEquals(result.ballerina(), 7);
        Assert.assertEquals(result.xml(), 0);
    }

    @Test
    public void testLineCountSingleLineXml() {
        String source = """
                import ballerina/http;

                function test() {
                    xml data = xml `<root>hello</root>`;
                    return data;
                }
                """;

        LineCount result = ConversionUtils.lineCount(source);
        Assert.assertEquals(result.ballerina(), 4);
        Assert.assertEquals(result.xml(), 1);
    }

    @Test
    public void testLineCountMultiLineXml() {
        String source = """
                import ballerina/http;

                function test() {
                    xml data = xml `<root>
                        <item>hello</item>
                        <item>world</item>
                    </root>`;
                    return data;
                }
                """;

        LineCount result = ConversionUtils.lineCount(source);
        Assert.assertEquals(result.ballerina(), 4);
        Assert.assertEquals(result.xml(), 4);
    }

    @Test
    public void testLineCountMultipleXmlBlocks() {
        String source = """
                import ballerina/http;

                function test() {
                    xml data1 = xml `<first>hello</first>`;
                    xml data2 = xml `<second>
                        <nested>world</nested>
                    </second>`;
                    return data1;
                }
                """;

        LineCount result = ConversionUtils.lineCount(source);
        Assert.assertEquals(result.ballerina(), 4);
        Assert.assertEquals(result.xml(), 4);
    }

    @Test
    public void testLineCountXsltTransform() {
        String source = """
                function Transform() {
                    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
                <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
                    <xsl:template match="/">
                        <result>
                            <xsl:value-of select="//item"/>
                        </result>
                    </xsl:template>
                </xsl:stylesheet>`, cx.variables);
                    return var1;
                }
                """;

        LineCount result = ConversionUtils.lineCount(source);
        Assert.assertEquals(result.ballerina(), 3);
        Assert.assertEquals(result.xml(), 8);
    }

    @Test
    public void testLineCountComplexMixedContent() {
        String source = """
                import ballerina/http;

                service on GeneralConnection {
                    resource function 'default test(xml input) returns xml {
                        xml inputVal = xml `<root>
                    <item>
                        ${input}
                    </item>
                </root>`;
                        Context cx = initContext();
                        xml response = cx.result;
                        return response;
                    }
                }
                """;

        LineCount result = ConversionUtils.lineCount(source);
        Assert.assertEquals(result.ballerina(), 8);
        Assert.assertEquals(result.xml(), 5);
    }

    @Test
    public void testLineCountEmptyLines() {
        String source = """
                import ballerina/http;


                function test() {

                    xml data = xml `<root>

                        <item>hello</item>

                    </root>`;

                    return data;

                }
                """;

        LineCount result = ConversionUtils.lineCount(source);
        Assert.assertEquals(result.ballerina(), 4);
        Assert.assertEquals(result.xml(), 3);
    }

    @Test
    public void testLineCountXmlEndingSameLine() {
        String source = """
                function test() {
                    xml data = xml `<root>
                        <item>hello</item>
                    </root>`;
                    xml another = xml `<single/>`;
                    return data;
                }
                """;

        LineCount result = ConversionUtils.lineCount(source);
        Assert.assertEquals(result.ballerina(), 3);
        Assert.assertEquals(result.xml(), 4);
    }

    @DataProvider
    public Object[][] sanitizesProvider() {
        return new Object[][]{
                // A name with no alphabetic character at all used to be stripped down to "" and then crash.
                {"404", "'404"},
                {"500", "'500"},
                {"123", "'123"},
                {"1.0", "'1_0"},
                {"123abc", "abc"},
                {"_Out--Throttling-1", "Out__Throttling_1"},
                {"[Esc] Throttling", "Esc__Throttling"},
                {"Start", "Start"},
                {"Get Sales Order", "Get_Sales_Order"},
                {"type", "'type"},
                {"Resources/AWS/SendMail/applicationId", "Resources_AWS_SendMail_applicationId"},
                {"anagrafica_clienti_isu_das/LocalFilePathArchive", "anagrafica_clienti_isu_das_LocalFilePathArchive"},
                {"", "unnamed"},
        };
    }

    @Test(dataProvider = "sanitizesProvider")
    public void testSanitizes(String name, String expected) {
        Assert.assertEquals(ConversionUtils.sanitizes(name), expected);
    }

    @Test
    public void testEscapeString() {
        Assert.assertEquals(ConversionUtils.escapeString("pass\"word"), "pass\\\"word");
        // The backslash has to be escaped before the quote, otherwise the backslash
        // added for the quote is
        // escaped again and the literal no longer round trips.
        Assert.assertEquals(ConversionUtils.escapeString("DOMAIN\\admin"), "DOMAIN\\\\admin");
        Assert.assertEquals(ConversionUtils.escapeString("a\\\"b"), "a\\\\\\\"b");
        Assert.assertEquals(ConversionUtils.escapeString(null), "");
    }

    @Test
    public void testNameSpacePrefixCollision() {
        ProjectContext projectContext = newProjectContext("NamespaceCollision");

        NameSpace resolvedFirst = projectContext.registerNameSpace(new NameSpace("ns1", "http://example.com/uriA"));
        NameSpace resolvedSecond = projectContext.registerNameSpace(new NameSpace("ns1", "http://example.com/uriB"));

        Assert.assertEquals(resolvedFirst.prefix().orElseThrow(), "ns1");
        Assert.assertEquals(resolvedFirst.uri(), "http://example.com/uriA");
        Assert.assertNotEquals(resolvedSecond.prefix(), resolvedFirst.prefix(),
                "a colliding prefix bound to a different uri must be renamed rather than reused");
        Assert.assertEquals(resolvedSecond.uri(), "http://example.com/uriB");
    }

    @Test
    public void testNameSpaceSameUriDedup() {
        ProjectContext projectContext = newProjectContext("NamespaceSameUri");

        NameSpace resolvedFirst = projectContext.registerNameSpace(new NameSpace("ns1", "http://example.com/uriA"));
        NameSpace resolvedSecond = projectContext.registerNameSpace(new NameSpace("ns1", "http://example.com/uriA"));

        Assert.assertEquals(resolvedFirst, resolvedSecond);
    }

    @Test
    public void testNameSpaceChainedCollisions() {
        ProjectContext projectContext = newProjectContext("NamespaceChainedCollision");

        NameSpace a = projectContext.registerNameSpace(new NameSpace("ns1", "http://example.com/a"));
        NameSpace b = projectContext.registerNameSpace(new NameSpace("ns1", "http://example.com/b"));
        NameSpace c = projectContext.registerNameSpace(new NameSpace("ns1", "http://example.com/c"));

        Assert.assertEquals(Set.of(a.prefix().orElseThrow(), b.prefix().orElseThrow(), c.prefix().orElseThrow()).size(),
                3, "three different uris colliding on the same raw prefix must each get a distinct prefix");
    }

    private static ProjectContext newProjectContext(String projectName) {
        ProjectConversionContext conversionContext = TestUtils.createTestProjectConversionContext("test", projectName);
        return new ProjectContext(conversionContext, Map.<Process, AnalysisResult>of());
    }

    @Test(groups = { "tibco", "converter" })
    public void testSlashSeparatedResourcePathIsSanitizedAsConfigurableVariable() {
        ProjectContext projectContext = newProjectContext();
        projectContext.addConfigurableVariable("myProp", "Resources/AWS/SendMail/applicationId");
        String configVarName = projectContext.getConfigVarName("myProp");
        Assert.assertEquals(configVarName, "Resources_AWS_SendMail_applicationId");
        Assert.assertFalse(configVarName.contains("/"), "configurable variable name must not contain '/'");
    }

    @Test(groups = { "tibco", "converter" })
    public void testResourceVariableRoundTripThroughProcessContext() {
        ProjectContext projectContext = newProjectContext();
        ProcessContext processContext = new ProcessContext(projectContext, null);
        processContext.addResourceVariable(
                new Variable.PropertyVariable.SimpleProperty(
                        "myProp", "anagrafica_clienti_isu_das/LocalFilePathArchive", "string"));
        Assert.assertEquals(processContext.getConfigVarName("myProp"),
                "anagrafica_clienti_isu_das_LocalFilePathArchive");
    }

    @Test(groups = { "tibco", "converter" })
    public void testCollidingSanitizedResourcePathsGetUniqueSuffix() {
        ProjectContext projectContext = newProjectContext();
        projectContext.addConfigurableVariable("propOne", "Resources/AWS/SendMail");
        projectContext.addConfigurableVariable("propTwo", "Resources.AWS.SendMail");

        String nameOne = projectContext.getConfigVarName("propOne");
        String nameTwo = projectContext.getConfigVarName("propTwo");

        Assert.assertEquals(nameOne, "Resources_AWS_SendMail");
        Assert.assertNotEquals(nameTwo, nameOne);
        Assert.assertFalse(nameTwo.contains("/") || nameTwo.contains("."));
    }

    @Test(groups = { "tibco", "converter" })
    public void testDuplicateLogicalNameConfigurableVariablesAreBothEmitted() {
        ProjectContext projectContext = newProjectContext();
        String nameOne = projectContext.addConfigurableVariable("prop", "Resources/AWS/SendMail");
        String nameTwo = projectContext.addConfigurableVariable("prop", "Resources/AWS/ReceiveMail");

        Assert.assertNotEquals(nameTwo, nameOne,
                "configurable variables sharing a logical name but different sources must get distinct emitted names");

        BallerinaModel.Module module = projectContext.serialize(List.of());
        BallerinaModel.TextDocument utilsFile = module.textDocuments().stream()
                .filter(doc -> doc.documentName().equals("utils.bal"))
                .findFirst().orElseThrow();
        Set<String> emittedNames = utilsFile.moduleVars().stream()
                .map(BallerinaModel.ModuleVar::name)
                .collect(java.util.stream.Collectors.toSet());

        Assert.assertTrue(emittedNames.contains(nameOne),
                "declaration from the first addConfigurableVariable call must not be dropped");
        Assert.assertTrue(emittedNames.contains(nameTwo),
                "declaration from the second addConfigurableVariable call must be emitted");
    }

    @Test(groups = { "tibco", "converter" })
    public void testComplexTypeFieldNameCollidingWithKeywordIsQuoted() {
        XSD.XSDType.ComplexType complexType = new XSD.XSDType.ComplexType(
                new XSD.XSDType.ComplexType.ComplexTypeBody.Sequence(List.of(
                        new XSD.Element("applicationId", XSD.XSDType.BasicXSDType.STRING,
                                Optional.empty(), Optional.empty()),
                        new XSD.Element("function", XSD.XSDType.BasicXSDType.STRING,
                                Optional.empty(), Optional.empty()))));

        BallerinaModel.TypeDesc typeDesc = ConversionUtils.toTypeDesc(complexType);
        Assert.assertTrue(typeDesc instanceof RecordTypeDesc);
        List<String> fieldNames = ((RecordTypeDesc) typeDesc).fields().stream()
                .map(RecordTypeDesc.RecordField::name)
                .toList();
        Assert.assertEquals(fieldNames, List.of("applicationId", "'function"));
    }

    @Test(groups = { "tibco", "converter" })
    public void testInitContextIsGeneratedOnceForEveryEntryPoint() {
        ProjectContext projectContext = newProjectContext("InitContextSingleDefinition");
        projectContext.addSharedVariable(new Resource.SharedVariable("sharedVar",
                "/Resources/sharedVar.sharedvariable", false, "<root/>", true));

        projectContext.getInitContextFn();
        projectContext.getInitContextFn();

        BallerinaModel.TextDocument utils = utilsFile(projectContext);
        Assert.assertEquals(utils.intrinsics().stream()
                        .filter(each -> each.stripLeading().startsWith("function initContext(")).count(), 1,
                "initContext must be emitted once however many entry points request it");
        Assert.assertEquals(utils.moduleVars().stream()
                        .filter(moduleVar -> moduleVar.name().startsWith("sharedVar")).count(), 1,
                "a project shared variable must be declared once, not re-registered per entry point");
    }

    @NotNull
    private static BallerinaModel.TextDocument utilsFile(ProjectContext projectContext) {
        return projectContext.serialize(List.of()).textDocuments().stream()
                .filter(each -> each.documentName().equals("utils.bal"))
                .findFirst().orElseThrow();
    }

    @NotNull
    private static ProjectContext newProjectContext() {
        Logger logger = createVerboseLogger("test");
        return new ProjectContext(
                new ProjectConversionContext(
                        new ConversionContext("testOrg", false, true,
                                LoggingUtils.wrapLoggerForStateCallback(logger),
                                LoggingUtils.wrapLoggerForStateCallback(logger)),
                        "test"),
                Map.of());
    }
}
