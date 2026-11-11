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

package tibco.xslt;

import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class AnalysisResultTest {

    private static List<String> parameterUsages(String xPath) {
        return AnalysisResult.analyse("<xsl:value-of select=\"%s\"/>".formatted(xPath)).parameterUsage().stream()
                .map(AnalysisResult.Chunk::value)
                .toList();
    }

    @Test
    public void testVariableAfterLiteral() {
        assertEquals(parameterUsages("concat('Request: ', $HTTP-Receiver/ProcessStarterOutput/RequestURI)"),
                List.of("$HTTP-Receiver"));
    }

    @Test
    public void testVariableAfterLiteralInArithmetic() {
        assertEquals(parameterUsages("concat('EC-', $_globalVariables/ns1:GlobalVariables/X + 10)"),
                List.of("$_globalVariables"));
    }

    @Test
    public void testVariableBetweenLiterals() {
        assertEquals(parameterUsages("eic:addMapping('LOGCONFIG', $processAndActivityName, 'x')"),
                List.of("$processAndActivityName"));
    }

    @Test
    public void testMultipleLiteralsAndVariables() {
        assertEquals(parameterUsages("concat('a', $x, 'b', $y)"), List.of("$x", "$y"));
    }

    @Test
    public void testVariableInsideLiteralIgnored() {
        assertEquals(parameterUsages("concat('$notAVar', $real)"), List.of("$real"));
    }

    @Test
    public void testVariableAfterQuotEntityLiteral() {
        assertEquals(parameterUsages("concat(&quot;a&quot;, $v)"), List.of("$v"));
    }

    @Test
    public void testUnterminatedLiteral() {
        assertEquals(parameterUsages("concat($v, 'abc"), List.of("$v"));
    }

    @Test
    public void testMissingParameterDeclaredForVariableAfterLiteral() {
        String xslt = """
                <?xml version="1.0" encoding="UTF-8"?>
                <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
                <xsl:template match="/"><message>\
                <xsl:value-of select="concat('Request: ', $HTTP-Receiver/ProcessStarterOutput/RequestURI)"/>\
                </message></xsl:template>
                </xsl:stylesheet>""";
        String result = new AddMissingParameters().transform(new TransformContext(null, xslt), xslt);
        assertTrue(result.contains("<xsl:param name=\"HTTP-Receiver\"/>"), result);
    }

    @Test
    public void testMissingParameterInSingleLineStylesheet() {
        String xslt = "<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" version=\"2.0\">"
                + "<xsl:template match=\"/\"><xsl:value-of select=\"concat('a', $Undeclared)\"/></xsl:template>"
                + "</xsl:stylesheet>";
        String result = new AddMissingParameters().transform(new TransformContext(null, xslt), xslt);
        assertTrue(result.contains("version=\"2.0\"><xsl:param name=\"Undeclared\"/><xsl:template"), result);
    }

    @Test
    public void testRootWrapperSkipsLocalVariablePaths() {
        String xslt = "<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" version=\"2.0\">"
                + "<xsl:param name=\"Start\"/><xsl:template match=\"/\">"
                + "<xsl:variable name=\"v\" select=\"$Start/item\"/>"
                + "<xsl:value-of select=\"concat($v/child, $Start/other)\"/>"
                + "<xsl:value-of select=\"$v/child\"/>"
                + "</xsl:template></xsl:stylesheet>";
        TransformPipeline pipeline = new TransformPipeline();
        pipeline.append(new IgnoreRootWrapper());
        String result = pipeline.apply(null, xslt);
        assertTrue(result.contains("<xsl:variable name=\"v\" select=\"$Start/root/item\"/>"), result);
        assertTrue(result.contains("select=\"concat($v/child, $Start/root/other)\""), result);
        assertTrue(result.contains("<xsl:value-of select=\"$v/child\"/>"), result);
    }

    @Test
    public void testMissingParameterSkipsStylesheetProcessingInstruction() {
        String xslt = "<?xml-stylesheet type=\"text/xsl\" href=\"a.xsl\"?>"
                + "<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" version=\"2.0\">"
                + "<xsl:template match=\"/\"><xsl:value-of select=\"$Undeclared\"/></xsl:template>"
                + "</xsl:stylesheet>";
        String result = new AddMissingParameters().transform(new TransformContext(null, xslt), xslt);
        assertTrue(result.startsWith("<?xml-stylesheet type=\"text/xsl\" href=\"a.xsl\"?><xsl:stylesheet"), result);
        assertTrue(result.contains("version=\"2.0\"><xsl:param name=\"Undeclared\"/><xsl:template"), result);
    }
}
