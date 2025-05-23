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

package mule;

import io.ballerina.compiler.syntax.tree.SyntaxInfo;
import org.w3c.dom.Element;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import static mule.MELConverter.convertMELToBal;

public class ConversionUtils {

    private static final Pattern UNESCAPED_SPECIAL_CHAR_SET =
            Pattern.compile("([$&+,:;=\\?@#\\\\|/'\\ \\[\\}\\]<\\>.\"^*{}~`()%!-])");

    /**
     * Converts mule path to a Ballerina resource path.
     *
     * @param path mule path
     * @return ballerina resource path
     */
    static String getBallerinaResourcePath(String path, List<String> pathParams) {
        List<String> list = Arrays.stream(path.split("/")).filter(s -> !s.isEmpty())
                .map(s -> {
                    if (s.startsWith("{") && s.endsWith("}")) {
                        // We come here for mule path params. e.g. foo/{bar}/baz
                        String pathParamName = s.substring(1, s.length() - 1);
                        pathParamName = convertToBalIdentifier(pathParamName);
                        pathParams.add(pathParamName);
                        return "[string " + pathParamName + "]";
                    }
                    if (s.startsWith("#[") && s.endsWith("]")) {
                        // Handle MEL in url. e.g. /users/#[flowVars.userId]
                        String balExpr = convertMELToBal(s, false);
                        return "[" + balExpr + "]";
                    }
                    return convertToBalIdentifier(s);
                }).toList();

        return list.isEmpty() ? "." : String.join("/", list);
    }

    /**
     * Converts mule base path to a Ballerina absolute path.
     *
     * @param basePath mule base path
     * @return ballerina absolute path
     */
    static String getBallerinaAbsolutePath(String basePath) {
        List<String> list = Arrays.stream(basePath.split("/")).filter(s -> !s.isEmpty())
                .map(ConversionUtils::convertToBalIdentifier).toList();

        return list.isEmpty() ? "/" : "/" + String.join("/", list);
    }

    /**
     * Converts mule http request path to a Ballerina client resource path.
     *
     * @param basePath mule http request path
     * @return ballerina client resource path
     */
    static String getBallerinaClientResourcePath(String basePath) {
        List<String> list = Arrays.stream(basePath.split("/")).filter(s -> !s.isEmpty())
                .map(s -> {
                    if (s.startsWith("#[") && s.endsWith("]")) {
                        // Handle MEL in url. e.g. /users/#[flowVars.userId]
                        String balExpr = convertMELToBal(s, false);
                        return "[" + balExpr + "]";
                    }
                    return ConversionUtils.convertToBalIdentifier(s);
                }).toList();
        return list.isEmpty() ? "/" : "/" + String.join("/", list);
    }

    static void processExprCompContent(MuleToBalConverter.SharedProjectData sharedProjectData,
                                       String convertedBalStmts) {
        List<String> list =
                Arrays.stream(convertedBalStmts.split(";")).filter(s -> !s.isEmpty()).map(String::trim).toList();
        for (String stmt : list) {
            processStatement(sharedProjectData, stmt);
        }
    }

    private static void processStatement(MuleToBalConverter.SharedProjectData sharedProjectData, String statement) {
        String regex = "ctx\\.(sessionVars|flowVars)\\.(\\w+)\\s*=\\s*(.*)";
        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(statement);
        if (matcher.find()) {
            String varCategory = matcher.group(1);
            String varName = matcher.group(2);
            String varValue = matcher.group(3);

            if ("sessionVars".equals(varCategory) && !sharedProjectData.existingSessionVar(varName)) {
                String inferredType = inferTypeFromBalExpr(varValue);
                sharedProjectData.sessionVars.add(
                        new MuleToBalConverter.SharedProjectData.TypeAndNamePair(inferredType, varName));
            } else if ("flowVars".equals(varCategory) && !sharedProjectData.existingFlowVar(varName)) {
                String inferredType = inferTypeFromBalExpr(varValue);
                sharedProjectData.flowVars.add(
                        new MuleToBalConverter.SharedProjectData.TypeAndNamePair(inferredType, varName));
            }
        }
    }

    /**
     * Converts a mule variable name to a Ballerina identifier syntax by:
     * 1. Escaping special characters with a preceding `\`
     * 2. Adding a single quote prefix if the identifier is a Ballerina keyword
     * 3. Adding a single quote prefix if the identifier starts with a digit (0-9)
     *
     * @param varName mule variable name
     * @return Ballerina identifier
     */
    public static String convertToBalIdentifier(String varName) {
        String var = escapeSpecialCharacters(varName);
        return insertQuoteIfApplicable(var);
    }

    /**
     * Inserts a single quote prefix to the variable name if it is a Ballerina keyword or starts with a digit.
     *
     * @param varName the variable name
     * @return quoted variable name if applicable, otherwise the original variable name
     */
    private static String insertQuoteIfApplicable(String varName) {
        if (varName.isEmpty()) {
            return varName;
        }

        return Character.isDigit(varName.charAt(0)) || SyntaxInfo.isKeyword(varName) ? "'" + varName : varName;
    }

    /**
     * Escapes special characters in an identifier with a preceding backslash (\).
     * This is part of making an identifier valid in Ballerina syntax.
     *
     * @param identifier the original identifier string
     * @return identifier with special characters escaped
     */
    private static String escapeSpecialCharacters(String identifier) {
        return UNESCAPED_SPECIAL_CHAR_SET.matcher(identifier).replaceAll("\\\\$1");
    }

    static String[] getAllowedMethods(String allowedMethods) {
        if (allowedMethods.isEmpty()) {
            // Leaving empty will allow all methods
            return new String[]{"DEFAULT"};
        }
        return allowedMethods.split(",\\s*");
    }

    static String insertLeadingSlash(String basePath) {
        return basePath.startsWith("/") ? basePath : "/" + basePath;
    }

    static String genQueryParam(Map<String, String> queryParams) {
        return queryParams.entrySet().stream().map(e -> {
                    String k = e.getKey();
                    String key = SyntaxInfo.isKeyword(k) ? "'" + k : k;
                    String balExpr = convertMuleExprToBal(e.getValue());
                    if (balExpr.startsWith("ctx.")) {
                        balExpr = "check " + balExpr + ".ensureType(http:QueryParamType)";
                    }
                    return String.format("%s = %s", key, balExpr);
                })
                .reduce((a, b) -> a + ", " + b).orElse("");
    }

    public static String convertMuleExprToBal(String melExpression) {
        return convertMuleExprToBal(melExpression, false);
    }

    public static String convertMuleExprToBalStringLiteral(String melExpression) {
        return convertMuleExprToBal(melExpression, true);
    }

    private static String convertMuleExprToBal(String melExpression, boolean addToStringCalls) {
        if (melExpression.startsWith("#[") && melExpression.endsWith("]")) {
            return convertMELToBal(melExpression, addToStringCalls);
        }

        StringBuilder result = new StringBuilder();
        StringBuilder currentLiteral = new StringBuilder();
        int i = 0;
        boolean hasMELParts = false;

        while (i < melExpression.length()) {
            // Look for start of MEL expression
            if (i + 1 < melExpression.length() &&
                    melExpression.charAt(i) == '#' && melExpression.charAt(i + 1) == '[') {
                // Add the accumulated literal text
                if (!currentLiteral.isEmpty()) {
                    result.append(currentLiteral);
                    currentLiteral.setLength(0);
                }

                hasMELParts = true;
                int startPos = i;
                i += 2; // Skip '#['
                int bracketDepth = 1;

                // Find matching closing bracket
                while (i < melExpression.length() && bracketDepth > 0) {
                    char c = melExpression.charAt(i);
                    if (c == '[') {
                        bracketDepth++;
                    } else if (c == ']') {
                        bracketDepth--;
                    }
                    i++;
                }

                // Extract the complete MEL expression
                String melExpr = melExpression.substring(startPos, i);
                String convertedExpr = convertMELToBal(melExpr, addToStringCalls);
                result.append("${").append(convertedExpr).append("}");
            } else {
                currentLiteral.append(melExpression.charAt(i));
                i++;
            }
        }

        // Add any remaining literal text
        if (!currentLiteral.isEmpty()) {
            result.append(currentLiteral);
        }

        return hasMELParts ? "string `%s`".formatted(result) : "\"" + melExpression.replace("\"", "\\\"") + "\"";
    }

    public static String inferTypeFromBalExpr(String balExpr) {
        switch (balExpr) {
            case "ctx.inboundProperties.uriParams" -> {
                return "map<string>";
            }
            case "ctx.inboundProperties.request.getQueryParams()" -> {
                return "map<string[]>";
            }
            case "true", "false" -> {
                return "boolean";
            }
        }

        if (balExpr.startsWith("\"") && balExpr.endsWith("\"")) {
            return "string";
        } else if (balExpr.startsWith("[") && balExpr.endsWith("]")) {
            return "anydata[]";
        } else if (balExpr.matches("-?\\d+")) {
            return "int";
        } else if (balExpr.matches("-?\\d+\\.\\d+")) {
            return "decimal";
        } else if (balExpr.startsWith("ctx.inboundProperties.request.getQueryParamValue(")) {
            return "string";
        } else if (balExpr.startsWith("ctx.inboundProperties.uriParams.get(")) {
            return "string";
        } else {
            return "anydata";
        }
    }

    public static String elementToString(Element element) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(element), new StreamResult(writer));
            return writer.getBuffer().toString();
        } catch (Exception e) {
            throw new RuntimeException("Error converting Element to String", e);
        }
    }

    public static String wrapElementInUnsupportedBlockComment(String input) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n\n");
        sb.append("// TODO: UNSUPPORTED MULE BLOCK ENCOUNTERED. MANUAL CONVERSION REQUIRED.\n");
        sb.append("// ------------------------------------------------------------------------\n");
        String[] lines = input.split("\n");
        for (String line : lines) {
            sb.append("// ").append(line).append("\n");
        }
        sb.append("// ------------------------------------------------------------------------\n\n");
        return sb.toString();
    }
}
