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

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import tibco.converter.ConversionUtils.LineCount;

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
        };
    }

    @Test(dataProvider = "sanitizesProvider")
    public void testSanitizes(String name, String expected) {
        Assert.assertEquals(ConversionUtils.sanitizes(name), expected);
    }
}
