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

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

// Saxon can't call TIBCO's XPath extension functions, so the ones in use are defined as stylesheet functions in
// the TIBCO namespace, which leaves their call sites unchanged. tib:uuid() needs a fresh value per run, which XSLT
// can't produce, so each call site reads a parameter that the generated Ballerina fills with a new UUID; repeated
// calls at one call site inside a for-each therefore share a value.
public class TibcoFunctions implements Transform {

    private static final String NAMESPACE = "http://www.tibco.com/bw/xslt/custom-functions";
    private static final String UUID_PARAMETER_PREFIX = "tibcoUuid";
    private static final Pattern PREFIX_DECLARATION =
            Pattern.compile("xmlns:([\\w.-]+)\\s*=\\s*([\"'])" + Pattern.quote(NAMESPACE) + "\\2");
    private static final Pattern STYLESHEET_START = Pattern.compile("<xsl:(?:stylesheet|transform)\\b[^>]*>");
    // XSLT requires every xsl:import to precede the other top-level declarations.
    private static final Pattern IMPORT =
            Pattern.compile("\\s*<xsl:import\\b[^>]*?(?:/>|>.*?</xsl:import>)", Pattern.DOTALL);
    private static final Pattern STYLESHEET_END = Pattern.compile("</xsl:(?:stylesheet|transform)>\\s*$");
    private static final Pattern UUID_PARAMETER =
            Pattern.compile("<xsl:param name=\"(" + UUID_PARAMETER_PREFIX + "\\d+)\"/>");
    private static final String ROOT_UNWRAPPED_NODE =
            "if ($node instance of document-node() and $node/root) then $node/root/node() else $node";
    private static final Map<String, String> DEFINITIONS = Map.of(
            "trim", """
                    <xsl:function name="tibfn:trim" xmlns:tibfn="%1$s">\
                    <xsl:param name="value"/>\
                    <xsl:sequence select="replace(string($value), '^\\s+|\\s+$', '')"/>\
                    </xsl:function>""".formatted(NAMESPACE),
            // A converter variable is a document wrapping its value in <root>, which TIBCO never renders.
            "render-xml", """
                    <xsl:function name="tibfn:render-xml" xmlns:tibfn="%1$s">\
                    <xsl:param name="node"/>\
                    <xsl:sequence select="serialize(%2$s)"/>\
                    </xsl:function>\
                    <xsl:function name="tibfn:render-xml" xmlns:tibfn="%1$s">\
                    <xsl:param name="node"/><xsl:param name="includeDeclaration"/>\
                    <xsl:sequence select="tibfn:render-xml($node, $includeDeclaration, false())"/>\
                    </xsl:function>\
                    <xsl:function name="tibfn:render-xml" xmlns:tibfn="%1$s">\
                    <xsl:param name="node"/><xsl:param name="includeDeclaration"/><xsl:param name="pretty"/>\
                    <xsl:sequence select="serialize(%2$s, map{'omit-xml-declaration': \
                    not(boolean($includeDeclaration)), 'indent': boolean($pretty)})"/>\
                    </xsl:function>""".formatted(NAMESPACE, ROOT_UNWRAPPED_NODE),
            // An empty delimiter matches at the end of the value, as with Java's lastIndexOf; tokenize rejects it.
            "substring-before-last", """
                    <xsl:function name="tibfn:substring-before-last" xmlns:tibfn="%1$s">\
                    <xsl:param name="value"/><xsl:param name="delimiter"/>\
                    <xsl:choose><xsl:when test="string($delimiter) = ''">\
                    <xsl:sequence select="string($value)"/></xsl:when><xsl:otherwise>\
                    <xsl:variable name="parts" select="tokenize(string($value), string($delimiter), 'q')"/>\
                    <xsl:sequence select="string-join(subsequence($parts, 1, count($parts) - 1), \
                    string($delimiter))"/>\
                    </xsl:otherwise></xsl:choose>\
                    </xsl:function>""".formatted(NAMESPACE));

    @Override
    public String transform(TransformContext cx, String content) {
        return rewrite(content);
    }

    public static @NotNull String rewrite(String stylesheet) {
        List<String> prefixes = PREFIX_DECLARATION.matcher(stylesheet).results()
                .map(match -> match.group(1)).distinct().toList();
        Matcher stylesheetEnd = STYLESHEET_END.matcher(stylesheet);
        int parameterPosition = topLevelDeclarationStart(stylesheet);
        if (prefixes.isEmpty() || !stylesheetEnd.find() || parameterPosition == -1) {
            return stylesheet;
        }
        String prefixAlternatives = prefixes.stream().map(Pattern::quote).collect(Collectors.joining("|"));
        String withUuidParameters = replaceUuidCalls(stylesheet, prefixAlternatives, parameterPosition);
        String definitions = Pattern.compile("(?<![\\w.-])(?:" + prefixAlternatives + "):([a-zA-Z-]+)\\s*\\(")
                .matcher(withUuidParameters).results()
                .map(match -> match.group(1))
                .filter(DEFINITIONS::containsKey)
                .collect(Collectors.toCollection(TreeSet::new)).stream()
                .map(DEFINITIONS::get)
                .collect(Collectors.joining());
        int definitionPosition = withUuidParameters.length() - (stylesheet.length() - stylesheetEnd.start());
        return withUuidParameters.substring(0, definitionPosition) + definitions
                + withUuidParameters.substring(definitionPosition);
    }

    private static int topLevelDeclarationStart(String stylesheet) {
        Matcher start = STYLESHEET_START.matcher(stylesheet);
        if (!start.find()) {
            return -1;
        }
        Matcher imports = IMPORT.matcher(stylesheet);
        int position = start.end();
        while (imports.region(position, stylesheet.length()).lookingAt()) {
            position = imports.end();
        }
        return position;
    }

    private static String replaceUuidCalls(String stylesheet, String prefixAlternatives, int parameterPosition) {
        Matcher uuidCall = Pattern.compile("(?<![\\w.-])(?:" + prefixAlternatives + "):uuid\\s*\\(\\s*\\)")
                .matcher(stylesheet);
        StringBuilder replaced = new StringBuilder();
        StringBuilder parameters = new StringBuilder();
        int count = 0;
        uuidCall.region(parameterPosition, stylesheet.length());
        while (uuidCall.find()) {
            count++;
            uuidCall.appendReplacement(replaced, Matcher.quoteReplacement("$" + UUID_PARAMETER_PREFIX + count));
            parameters.append("<xsl:param name=\"%s%d\"/>".formatted(UUID_PARAMETER_PREFIX, count));
        }
        uuidCall.appendTail(replaced);
        return replaced.insert(parameterPosition, parameters).toString();
    }

    public static @NotNull List<String> uuidParameters(String stylesheet) {
        return UUID_PARAMETER.matcher(stylesheet).results().map(match -> match.group(1)).toList();
    }

    public static boolean isDefined(String stylesheet, String functionName) {
        return stylesheet.contains("<xsl:function name=\"tibfn:%s\"".formatted(functionName));
    }
}
