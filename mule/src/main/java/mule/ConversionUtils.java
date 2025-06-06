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
package mule;

import io.ballerina.compiler.syntax.tree.SyntaxInfo;
import org.w3c.dom.Element;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import static common.BallerinaModel.ModuleVar;
import static common.ConversionUtils.exprFrom;
import static mule.MELConverter.convertMELToBal;

/**
 * Utility class for converting mule configs.
 */
public class ConversionUtils {

    private static final Pattern UNESCAPED_SPECIAL_CHAR_SET =
            Pattern.compile("([$&+,:;=\\?@#\\\\|/'\\ \\[\\}\\]<\\>.\"^*{}~`()%!-])");

    /**
     * Converts mule path to a Ballerina resource path.
     *
     * @param ctx mule to bal converter data
     * @param path mule path
     * @return ballerina resource path
     */
    static String getBallerinaResourcePath(Context ctx, String path, List<String> pathParams) {
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
                        String balExpr = convertMELToBal(ctx, s, false);
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
     * @param ctx mule to bal converter data
     * @param basePath mule http request path
     * @return ballerina client resource path
     */
    static String getBallerinaClientResourcePath(Context ctx, String basePath) {
        List<String> list = Arrays.stream(basePath.split("/")).filter(s -> !s.isEmpty())
                .map(s -> {
                    if (s.startsWith("#[") && s.endsWith("]")) {
                        // Handle MEL in url. e.g. /users/#[flowVars.userId]
                        String balExpr = convertMELToBal(ctx, s, false);
                        return "[" + balExpr + "]";
                    }
                    return ConversionUtils.convertToBalIdentifier(s);
                }).toList();
        return list.isEmpty() ? "/" : "/" + String.join("/", list);
    }

    static void processExprCompContent(Context ctx, String convertedBalStmts) {
        List<String> list =
                Arrays.stream(convertedBalStmts.split(";")).filter(s -> !s.isEmpty()).map(String::trim).toList();
        for (String stmt : list) {
            processStatement(ctx, stmt);
        }
    }

    private static void processStatement(Context ctx, String statement) {
        String regex = "ctx\\.(sessionVars|flowVars)\\.(\\w+)\\s*=\\s*(.*)";
        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(statement);
        if (matcher.find()) {
            String varCategory = matcher.group(1);
            String varName = matcher.group(2);
            String varValue = matcher.group(3);

            if ("sessionVars".equals(varCategory) && !ctx.projectCtx.sessionVars.containsKey(varName)) {
                String inferredType = inferTypeFromBalExpr(varValue);
                ctx.projectCtx.sessionVars.put(varName, inferredType);
            } else if ("flowVars".equals(varCategory) && !ctx.projectCtx.flowVars.containsKey(varName)) {
                String inferredType = inferTypeFromBalExpr(varValue);
                ctx.projectCtx.flowVars.put(varName, inferredType);
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

    public static String[] getAllowedMethods(String allowedMethods) {
        if (allowedMethods.isEmpty()) {
            // Leaving empty will allow all methods
            return new String[]{"DEFAULT"};
        }
        return allowedMethods.split(",\\s*");
    }

    static String insertLeadingSlash(String basePath) {
        return basePath.startsWith("/") ? basePath : "/" + basePath;
    }

    static String genQueryParam(Context ctx, Map<String, String> queryParams) {
        return queryParams.entrySet().stream().map(e -> {
                    String k = e.getKey();
                    String key = SyntaxInfo.isKeyword(k) ? "'" + k : k;
                    String balExpr = convertMuleExprToBal(ctx, e.getValue());
                    if (balExpr.startsWith("ctx.")) {
                        balExpr = "check " + balExpr + ".ensureType(http:QueryParamType)";
                    }
                    return String.format("%s = %s", key, balExpr);
                })
                .reduce((a, b) -> a + ", " + b).orElse("");
    }

    public static String convertMuleExprToBal(Context ctx, String melExpression) {
        return convertMuleExprToBal(ctx, melExpression, false);
    }

    public static String convertMuleExprToBalStringLiteral(Context ctx, String melExpression) {
        return convertMuleExprToBal(ctx, melExpression, true);
    }

    private static String convertMuleExprToBal(Context ctx, String melExpression,
                                               boolean addToStringCalls) {
        // Direct MEL expression (e.g., "#[payload]")
        if (melExpression.startsWith("#[") && melExpression.endsWith("]")) {
            return convertMELToBal(ctx, melExpression, addToStringCalls);
        }

        // Handle mixed string with embedded MEL or property expressions
        StringBuilder result = new StringBuilder();
        int currentPos = 0;
        boolean hasExpressionParts = false;

        while (currentPos < melExpression.length()) {
            int exprStart = findNextExpressionStart(melExpression, currentPos);

            // No more expressions found, add remaining text
            if (exprStart == -1) {
                result.append(melExpression.substring(currentPos));
                break;
            }

            // Add text before expression
            if (exprStart > currentPos) {
                result.append(melExpression, currentPos, exprStart);
            }

            // Determine expression type and process accordingly
            hasExpressionParts = true;
            if (melExpression.startsWith("#[", exprStart)) {
                currentPos = processMELExpression(ctx, melExpression, exprStart, result, addToStringCalls);
            } else if (melExpression.startsWith("${", exprStart)) {
                currentPos = processPropertyAccessExpr(ctx, melExpression, exprStart, result);
            } else {
                // Should never happen given the findNextExpressionStart logic
                throw new IllegalStateException();
            }
        }

        // Format the final result
        return formatResult(result.toString(), hasExpressionParts, melExpression);
    }
    private static int findNextExpressionStart(String text, int startFrom) {
        int melStart = text.indexOf("#[", startFrom);
        int propStart = text.indexOf("${", startFrom);

        if (melStart == -1 && propStart == -1) {
            return -1;  // No more expressions
        } else if (melStart == -1) {
            return propStart;
        } else if (propStart == -1) {
            return melStart;
        } else {
            return Math.min(melStart, propStart);  // Return the earlier one
        }
    }

    private static int processMELExpression(Context ctx, String text, int startPos,
                                            StringBuilder result, boolean addToStringCalls) {
        int bracketDepth = 1;
        int i = startPos + 2;  // Skip "#["

        while (i < text.length() && bracketDepth > 0) {
            char c = text.charAt(i);
            if (c == '[') {
                bracketDepth++;
            } else if (c == ']') {
                bracketDepth--;
            }
            i++;
        }

        String melExpr = text.substring(startPos, i);
        String convertedExpr = convertMELToBal(ctx, melExpr, addToStringCalls);
        result.append("${").append(convertedExpr).append("}");

        return i;
    }

    private static int processPropertyAccessExpr(Context ctx, String text, int startPos,
                                                 StringBuilder result) {
        int i = startPos + 2;  // Skip "${"

        StringBuilder propertyName = new StringBuilder();
        while (i < text.length() && isTokenChar(text.charAt(i))) {
            propertyName.append(text.charAt(i));
            i++;
        }

        if (i < text.length() && text.charAt(i) == '}') {
            String configVarName = processPropertyName(ctx, propertyName.toString());
            result.append("${").append(configVarName).append("}");
            return i + 1;
        } else {
            // Possibly malformed expression
            result.append("${").append(propertyName);
            return i;
        }
    }

    private static String processPropertyName(Context ctx, String propertyName) {
        String configVarName = propertyName.replace('.', '_');
        if (!ctx.projectCtx.configurableVarExists(configVarName)) {
            var configVarDecl = new ModuleVar(configVarName, "string", Optional.of(exprFrom("?")), false, true);
            ctx.currentFileCtx.configs.configurableVars.put(configVarName, configVarDecl);
        }

        return configVarName;
    }

    public static String getAttrVal(Context ctx, String propValue) {
        return getAttrVal(ctx, propValue, false);
    }

    public static String getAttrValInt(Context ctx, String propValue) {
        return getAttrVal(ctx, propValue, true);
    }

    private static String getAttrVal(Context ctx, String propValue, boolean isInt) {
        if (propValue.startsWith("${") && propValue.endsWith("}")) {
            String configVarRef = processPropertyName(ctx, propValue.substring(2, propValue.length() - 1));
            return isInt ? "check int:fromString(%s)".formatted(configVarRef) : configVarRef;
        } else {
            return isInt ? propValue : "\"" + propValue.replace("\"", "\\\"") + "\"";
        }
    }

    public static boolean isTokenChar(char currentChar) {
        return Character.isLetterOrDigit(currentChar) || currentChar == '.' || currentChar == '_';
    }

    private static String formatResult(String content, boolean hasExpressionParts, String originalExpression) {
        if (hasExpressionParts) {
            return "string `%s`".formatted(content);
        } else {
            return "\"" + originalExpression.replace("\"", "\\\"") + "\"";
        }
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
