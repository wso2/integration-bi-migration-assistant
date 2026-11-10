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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.MatchResult;
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
    // The marker tells generated parameters apart from ones the stylesheet already declares under the same name.
    private static final String UUID_PARAMETER_DECLARATION =
            "<xsl:param name=\"%s\" xmlns:tibfn=\"" + NAMESPACE + "\" tibfn:generated=\"uuid\"/>";
    private static final Pattern PREFIX_DECLARATION =
            Pattern.compile("xmlns:([\\w.-]+)\\s*=\\s*([\"'])" + Pattern.quote(NAMESPACE) + "\\2");
    private static final Pattern STYLESHEET_START = Pattern.compile("<xsl:(?:stylesheet|transform)\\b[^>]*>");
    // XSLT requires every xsl:import to precede the other top-level declarations.
    private static final Pattern IMPORT = Pattern.compile(
            "(?:\\s|<!--.*?-->|<\\?.*?\\?>)*<xsl:import\\b[^>]*?(?:/>|>.*?</xsl:import>)", Pattern.DOTALL);
    private static final Pattern STYLESHEET_END = Pattern.compile("</xsl:(?:stylesheet|transform)>\\s*$");
    private static final Pattern UUID_PARAMETER = Pattern.compile("<xsl:param name=\"(" + UUID_PARAMETER_PREFIX
            + "\\d+)\" xmlns:tibfn=\"" + Pattern.quote(NAMESPACE) + "\" tibfn:generated=\"uuid\"/>");
    private static final Pattern DECLARED_NAME =
            Pattern.compile("<xsl:(?:param|variable)\\b[^>]*?\\sname\\s*=\\s*[\"']([^\"']*)[\"']");
    private static final Pattern FUNCTION_DECLARATION = Pattern.compile(
            "<xsl:function\\b[^>]*?\\sname\\s*=\\s*[\"']([\\w.-]+):([\\w.-]+)[\"'][^>]*?(?:/>|>(.*?)</xsl:function>)",
            Pattern.DOTALL);
    private static final Pattern FUNCTION_PARAMETER = Pattern.compile("<xsl:param\\b");
    // Only attribute values of start tags hold XPath; comments, CDATA and text are left alone.
    private static final Pattern MARKUP = Pattern.compile(
            "<!--.*?-->|<!\\[CDATA\\[.*?]]>|(<[\\w.:-]+(?:\\s+[\\w.:-]+\\s*=\\s*(?:\"[^\"]*\"|'[^']*'))*\\s*/?>)",
            Pattern.DOTALL);
    private static final Pattern ATTRIBUTE_VALUE = Pattern.compile("(\")([^\"]*)\"|(')([^']*)'");
    private static final String XPATH_STRING_LITERAL = "'[^']*'|\"[^\"]*\"|&quot;.*?&quot;|&apos;.*?&apos;";
    private static final String ROOT_UNWRAPPED_NODE =
            "if ($node instance of document-node() and $node/root) then $node/root/node() else $node";
    private static final Map<String, List<String>> DEFINITIONS = Map.of(
            "trim", List.of("""
                    <xsl:function name="tibfn:trim" xmlns:tibfn="%1$s">\
                    <xsl:param name="value"/>\
                    <xsl:sequence select="replace(string($value), '^\\s+|\\s+$', '')"/>\
                    </xsl:function>""".formatted(NAMESPACE)),
            // A converter variable is a document wrapping its value in <root>, which TIBCO never renders.
            "render-xml", List.of("""
                    <xsl:function name="tibfn:render-xml" xmlns:tibfn="%1$s">\
                    <xsl:param name="node"/>\
                    <xsl:sequence select="serialize(%2$s)"/>\
                    </xsl:function>""".formatted(NAMESPACE, ROOT_UNWRAPPED_NODE), """
                    <xsl:function name="tibfn:render-xml" xmlns:tibfn="%1$s">\
                    <xsl:param name="node"/><xsl:param name="includeDeclaration"/>\
                    <xsl:sequence select="tibfn:render-xml($node, $includeDeclaration, false())"/>\
                    </xsl:function>""".formatted(NAMESPACE), """
                    <xsl:function name="tibfn:render-xml" xmlns:tibfn="%1$s">\
                    <xsl:param name="node"/><xsl:param name="includeDeclaration"/><xsl:param name="pretty"/>\
                    <xsl:sequence select="serialize(%2$s, map{'omit-xml-declaration': \
                    not(boolean($includeDeclaration)), 'indent': boolean($pretty)})"/>\
                    </xsl:function>""".formatted(NAMESPACE, ROOT_UNWRAPPED_NODE)),
            // An empty delimiter matches at the end of the value, as with Java's lastIndexOf; tokenize rejects it.
            "substring-before-last", List.of("""
                    <xsl:function name="tibfn:substring-before-last" xmlns:tibfn="%1$s">\
                    <xsl:param name="value"/><xsl:param name="delimiter"/>\
                    <xsl:choose><xsl:when test="string($delimiter) = ''">\
                    <xsl:sequence select="string($value)"/></xsl:when><xsl:otherwise>\
                    <xsl:variable name="parts" select="tokenize(string($value), string($delimiter), 'q')"/>\
                    <xsl:sequence select="string-join(subsequence($parts, 1, count($parts) - 1), \
                    string($delimiter))"/>\
                    </xsl:otherwise></xsl:choose>\
                    </xsl:function>""".formatted(NAMESPACE)));

    @Override
    public @NotNull String transform(TransformContext cx, String content) {
        return rewrite(content);
    }

    public static @NotNull String rewrite(String stylesheet) {
        List<String> prefixes = tibcoPrefixes(stylesheet);
        Matcher stylesheetEnd = STYLESHEET_END.matcher(stylesheet);
        int parameterPosition = topLevelDeclarationStart(stylesheet);
        if (prefixes.isEmpty() || !stylesheetEnd.find() || parameterPosition == -1) {
            return stylesheet;
        }
        String prefixAlternatives = prefixes.stream().map(Pattern::quote).collect(Collectors.joining("|"));
        String withUuidParameters = replaceUuidCalls(stylesheet, prefixAlternatives, parameterPosition);
        Set<String> declared = declaredFunctions(stylesheet, prefixes);
        String definitions = Pattern.compile("(?<![\\w.-])(?:" + prefixAlternatives + "):([a-zA-Z-]+)\\s*\\(")
                .matcher(withUuidParameters).results()
                .map(match -> match.group(1))
                .filter(DEFINITIONS::containsKey)
                .collect(Collectors.toCollection(TreeSet::new)).stream()
                .flatMap(name -> DEFINITIONS.get(name).stream()
                        .filter(definition -> !declared.contains(functionKey(name, arity(definition)))))
                .collect(Collectors.joining());
        int definitionPosition = withUuidParameters.length() - (stylesheet.length() - stylesheetEnd.start());
        return withUuidParameters.substring(0, definitionPosition) + definitions
                + withUuidParameters.substring(definitionPosition);
    }

    private static @NotNull List<String> tibcoPrefixes(String stylesheet) {
        return PREFIX_DECLARATION.matcher(stylesheet).results().map(match -> match.group(1)).distinct().toList();
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

    private static @NotNull String replaceUuidCalls(String stylesheet, String prefixAlternatives,
                                                    int parameterPosition) {
        Pattern literalOrUuidCall = Pattern.compile(
                XPATH_STRING_LITERAL + "|(?<![\\w.-])(?:" + prefixAlternatives + "):uuid\\s*\\(\\s*\\)");
        Set<String> takenNames = DECLARED_NAME.matcher(stylesheet).results().map(match -> match.group(1))
                .collect(Collectors.toCollection(HashSet::new));
        List<String> parameters = new ArrayList<>();
        String declarations = MARKUP.matcher(stylesheet.substring(parameterPosition)).replaceAll(markup ->
                Matcher.quoteReplacement(markup.group(1) == null ? markup.group()
                        : replaceInAttributes(markup.group(1), literalOrUuidCall, takenNames, parameters)));
        return stylesheet.substring(0, parameterPosition)
                + parameters.stream().map(UUID_PARAMETER_DECLARATION::formatted).collect(Collectors.joining())
                + declarations;
    }

    private static @NotNull String replaceInAttributes(String startTag, Pattern literalOrUuidCall,
                                                       Set<String> takenNames, List<String> parameters) {
        return ATTRIBUTE_VALUE.matcher(startTag).replaceAll(attribute -> {
            int group = attribute.group(1) != null ? 1 : 3;
            String quote = attribute.group(group);
            String value = literalOrUuidCall.matcher(attribute.group(group + 1)).replaceAll(token ->
                    Matcher.quoteReplacement(isUuidCall(token) ? "$" + newUuidParameter(takenNames, parameters)
                            : token.group()));
            return Matcher.quoteReplacement(quote + value + quote);
        });
    }

    private static boolean isUuidCall(MatchResult token) {
        char first = token.group().charAt(0);
        return first != '\'' && first != '"' && first != '&';
    }

    private static @NotNull String newUuidParameter(Set<String> takenNames, List<String> parameters) {
        int index = 1;
        while (takenNames.contains(UUID_PARAMETER_PREFIX + index)) {
            index++;
        }
        String name = UUID_PARAMETER_PREFIX + index;
        takenNames.add(name);
        parameters.add(name);
        return name;
    }

    private static @NotNull Set<String> declaredFunctions(String stylesheet, List<String> prefixes) {
        return FUNCTION_DECLARATION.matcher(stylesheet).results()
                .filter(declaration -> prefixes.contains(declaration.group(1)))
                .map(declaration -> functionKey(declaration.group(2),
                        declaration.group(3) == null ? 0 : arity(declaration.group(3))))
                .collect(Collectors.toSet());
    }

    private static long arity(String functionContent) {
        return FUNCTION_PARAMETER.matcher(functionContent).results().count();
    }

    private static @NotNull String functionKey(String name, long arity) {
        return name + "#" + arity;
    }

    public static @NotNull List<String> uuidParameters(String stylesheet) {
        return UUID_PARAMETER.matcher(stylesheet).results().map(match -> match.group(1)).toList();
    }

    public static boolean isDefined(String stylesheet, String functionName) {
        return declaredFunctions(stylesheet, tibcoPrefixes(stylesheet)).stream()
                .anyMatch(key -> key.startsWith(functionName + "#"));
    }
}
