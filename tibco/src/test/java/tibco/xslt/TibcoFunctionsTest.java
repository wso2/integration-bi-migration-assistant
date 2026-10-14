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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class TibcoFunctionsTest {

    private static String stylesheet(String prefix, String select) {
        return """
                <?xml version="1.0" encoding="UTF-8"?>
                <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" \
                xmlns:%s="http://www.tibco.com/bw/xslt/custom-functions" version="2.0">\
                <xsl:template match="/"><out><xsl:value-of select="%s"/></out></xsl:template>\
                </xsl:stylesheet>""".formatted(prefix, select);
    }

    @Test
    public void testUuidParameters() {
        String rewritten = TibcoFunctions.rewrite(stylesheet("tib", "concat(tib:uuid(), '-', tib:uuid ( ))"));
        assertTrue(rewritten.contains("concat($tibcoUuid1, '-', $tibcoUuid2)"), rewritten);
        assertTrue(rewritten.contains("version=\"2.0\"><xsl:param name=\"tibcoUuid1\"/>"
                + "<xsl:param name=\"tibcoUuid2\"/>"), rewritten);
        assertEquals(TibcoFunctions.uuidParameters(rewritten), List.of("tibcoUuid1", "tibcoUuid2"));
        assertFalse(rewritten.contains("<xsl:function"), rewritten);
    }

    @Test
    public void testUsedDefinitions() {
        String rewritten = TibcoFunctions.rewrite(stylesheet("tib", "tib:trim(tib:render-xml($Start))"));
        assertTrue(rewritten.contains("name=\"tibfn:trim\""), rewritten);
        assertTrue(rewritten.contains("name=\"tibfn:render-xml\""), rewritten);
        assertFalse(rewritten.contains("name=\"tibfn:substring-before-last\""), rewritten);
        assertTrue(rewritten.endsWith("</xsl:function></xsl:stylesheet>"), rewritten);
        assertTrue(rewritten.contains("tib:trim(tib:render-xml($Start))"), rewritten);
    }

    @Test
    public void testOtherPrefix() {
        String rewritten = TibcoFunctions.rewrite(
                stylesheet("tib2", "concat(tib2:uuid(), tib2:substring-before-last('a/b', '/'))"));
        assertTrue(rewritten.contains("concat($tibcoUuid1, tib2:substring-before-last('a/b', '/'))"), rewritten);
        assertTrue(rewritten.contains("name=\"tibfn:substring-before-last\""), rewritten);
    }

    @Test
    public void testNoTibcoFunctions() {
        String plain = """
                <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">\
                <xsl:template match="/"><out><xsl:value-of select="concat('a', 'b')"/></out></xsl:template>\
                </xsl:stylesheet>""";
        assertEquals(TibcoFunctions.rewrite(plain), plain);
        String unusedNamespace = stylesheet("tib", "concat('a', 'b')");
        assertEquals(TibcoFunctions.rewrite(unusedNamespace), unusedNamespace);
    }

    @Test
    public void testIsDefined() {
        String rewritten = TibcoFunctions.rewrite(stylesheet("tib", "tib:trim(tib:parse-dateTime('a', 'b'))"));
        assertTrue(TibcoFunctions.isDefined(rewritten, "trim"));
        assertFalse(TibcoFunctions.isDefined(rewritten, "parse-dateTime"));
        assertFalse(TibcoFunctions.isDefined(rewritten, "render-xml"));
    }

    @Test
    public void testTransformRoot() {
        String transform = """
                <xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform" \
                xmlns:tib='http://www.tibco.com/bw/xslt/custom-functions' version="2.0">\
                <xsl:template match="/"><out><xsl:value-of select="tib:trim(tib:uuid())"/></out></xsl:template>\
                </xsl:transform>""";
        String rewritten = TibcoFunctions.rewrite(transform);
        assertTrue(rewritten.contains("version=\"2.0\"><xsl:param name=\"tibcoUuid1\"/><xsl:template"), rewritten);
        assertTrue(rewritten.contains("tib:trim($tibcoUuid1)"), rewritten);
        assertTrue(rewritten.endsWith("</xsl:function></xsl:transform>"), rewritten);
    }

    @Test
    public void testAfterImports() {
        String withImports = """
                <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" \
                xmlns:tib="http://www.tibco.com/bw/xslt/custom-functions" version="2.0">
                    <xsl:import href="a.xsl"/>
                    <xsl:import href="b.xsl"></xsl:import>
                    <xsl:template match="/"><out><xsl:value-of select="tib:uuid()"/></out></xsl:template>
                </xsl:stylesheet>""";
        String rewritten = TibcoFunctions.rewrite(withImports);
        assertTrue(rewritten.contains("</xsl:import><xsl:param name=\"tibcoUuid1\"/>"), rewritten);
    }

    @Test
    public void testUnknownRoot() {
        String unrecognised = stylesheet("tib", "tib:uuid()").replace("xsl:stylesheet", "x:stylesheet");
        assertEquals(TibcoFunctions.rewrite(unrecognised), unrecognised);
    }
}
