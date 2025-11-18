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
package mule.v4;

import io.ballerina.compiler.syntax.tree.SyntaxInfo;
import mule.v4.converter.ScriptConversionException;
import org.w3c.dom.Element;

import java.io.StringWriter;
import java.util.ArrayList;
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
import static mule.v4.converter.MELConverter.convertMELToBal;
import static mule.v4.model.MuleModel.UnsupportedBlock;

/**
 * Utility class for converting mule configs.
 */
public class ConversionUtils {

    /**
     * Converts mule path to a Ballerina resource path.
     *
     * @param ctx  mule to bal converter data
     * @param path mule path
     * @return ballerina resource path
     * @throws ScriptConversionException if MEL conversion fails
     */
    public static String getBallerinaResourcePath(Context ctx, String path, List<String> pathParams)
            throws ScriptConversionException {
        List<String> list = new ArrayList<>();
        for (String segment : path.split("/")) {
            if (!segment.isEmpty()) {
                list.add(processPathSegment(ctx, segment, pathParams));
            }
        }

        return list.isEmpty() ? "." : String.join("/", list);
    }

    private static String processPathSegment(Context ctx, String segment, List<String> pathParams)
            throws ScriptConversionException {
        if (segment.startsWith("{") && segment.endsWith("}")) {
            // We come here for mule path params. e.g. foo/{bar}/baz
            String pathParamName = segment.substring(1, segment.length() - 1);
            pathParamName = convertToBalIdentifier(pathParamName);
            pathParams.add(pathParamName);
            return "[string " + pathParamName + "]";
        }
        if (segment.startsWith("#[") && segment.endsWith("]")) {
            // Handle MEL in url. e.g. /users/#[vars.userId]
            String balExpr = convertMELToBal(ctx, segment, false);
            return "[" + balExpr + "]";
        }
        return convertToBalIdentifier(segment);
    }

    /**
     * Converts mule base path to a Ballerina absolute path.
     *
     * @param basePath mule base path
     * @return ballerina absolute path
     */
    public static String getBallerinaAbsolutePath(String basePath) {
        List<String> list = Arrays.stream(basePath.split("/")).filter(s -> !s.isEmpty())
                .map(ConversionUtils::convertToBalIdentifier).toList();

        return list.isEmpty() ? "/" : "/" + String.join("/", list);
    }

    /**
     * Converts mule http request path to a Ballerina client resource path.
     *
     * @param ctx      mule to bal converter data
     * @param basePath mule http request path
     * @return ballerina client resource path
     * @throws ScriptConversionException if MEL conversion fails
     */
    public static String getBallerinaClientResourcePath(Context ctx, String basePath)
            throws ScriptConversionException {
        List<String> list = new ArrayList<>();
        for (String segment : basePath.split("/")) {
            if (!segment.isEmpty()) {
                list.add(processClientPathSegment(ctx, segment));
            }
        }
        return list.isEmpty() ? "/" : "/" + String.join("/", list);
    }

    private static String processClientPathSegment(Context ctx, String segment)
            throws ScriptConversionException {
        if (segment.startsWith("#[") && segment.endsWith("]")) {
            // Handle MEL in url. e.g. /users/#[vars.userId]
            String balExpr = convertMELToBal(ctx, segment, false);
            return "[" + balExpr + "]";
        }
        return ConversionUtils.convertToBalIdentifier(segment);
    }

    public static void processExprCompContent(Context ctx, String convertedBalStmts) {
        List<String> list =
                Arrays.stream(convertedBalStmts.split(";")).filter(s -> !s.isEmpty()).map(String::trim).toList();
        for (String stmt : list) {
            processStatement(ctx, stmt);
        }
    }

    private static void processStatement(Context ctx, String statement) {
        String regex = "ctx\\.(vars)\\.(\\w+)\\s*=\\s*(.*)";
        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(statement);
        if (matcher.find()) {
            String varCategory = matcher.group(1);
            String varName = matcher.group(2);
            String varValue = matcher.group(3);

            if ("vars".equals(varCategory) && !ctx.projectCtx.vars.containsKey(varName)) {
                String inferredType = inferTypeFromBalExpr(varValue);
                ctx.projectCtx.vars.put(varName, inferredType);
            }
        }
    }

    /**
     * Converts space-separated text to camelCase. Example: "Transform to Mule Domain" becomes "transformToMuleDomain"
     *
     * @param spaceSeparatedText space-separated text
     * @return camelCase string
     */
    public static String convertToCamelCase(String spaceSeparatedText) {
        if (spaceSeparatedText == null || spaceSeparatedText.isEmpty()) {
            return spaceSeparatedText;
        }
        String[] words = spaceSeparatedText.trim().split("\\s+");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (word.isEmpty()) {
                continue;
            }
            if (i == 0) {
                result.append(word.toLowerCase());
            } else {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase());
            }
        }
        return convertToBalIdentifier(result.toString());
    }

    /**
     * Converts a glob pattern to a regex pattern.
     * Handles common glob wildcards:
     * - `*` → `.*` (zero or more characters)
     * - `?` → `.` (single character)
     * - `**` → `.*` (recursive wildcard)
     * - `.` → `\.` (escape literal dots)
     * - Other special regex characters are escaped
     *
     * @param globPattern the glob pattern (e.g., "*.csv", "file?.txt")
     * @return regex pattern (e.g., ".*\\.csv", "file.\\.txt")
     */
    public static String convertGlobToRegex(String globPattern) {
        if (globPattern == null || globPattern.isEmpty()) {
            return globPattern;
        }

        // Step 1: Replace ** with a placeholder to handle it separately
        String placeholder = "<<<DOUBLE_STAR>>>";
        String result = globPattern.replace("**", placeholder);

        // Step 2: Escape special regex characters except * and ?
        // Characters that need escaping in regex: . + ( ) | ^ $ @ % [ ] { } \
        result = result.replace("\\", "\\\\");  // Escape backslash first
        result = result.replace(".", "\\.");
        result = result.replace("+", "\\+");
        result = result.replace("(", "\\(");
        result = result.replace(")", "\\)");
        result = result.replace("|", "\\|");
        result = result.replace("^", "\\^");
        result = result.replace("$", "\\$");
        result = result.replace("@", "\\@");
        result = result.replace("%", "\\%");
        result = result.replace("[", "\\[");
        result = result.replace("]", "\\]");
        result = result.replace("{", "\\{");
        result = result.replace("}", "\\}");

        // Step 3: Convert glob wildcards to regex
        result = result.replace("*", ".*");  // * → .*
        result = result.replace("?", ".");   // ? → .

        // Step 4: Replace placeholder with .*
        result = result.replace(placeholder, ".*");

        return result;
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
        return common.ConversionUtils.convertToBalIdentifier(varName);
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

    public static String genQueryParam(Context ctx, Map<String, String> queryParams)
            throws ScriptConversionException {
        List<String> paramStrings = new ArrayList<>();
        for (Map.Entry<String, String> e : queryParams.entrySet()) {
            String k = e.getKey();
            String key = SyntaxInfo.isKeyword(k) ? "'" + k : k;
            String balExpr = convertMuleExprToBal(ctx, e.getValue());
            if (balExpr.startsWith("ctx.")) {
                balExpr = "check " + balExpr + ".ensureType(http:QueryParamType)";
            }
            paramStrings.add(String.format("%s = %s", key, balExpr));
        }
        return String.join(", ", paramStrings);
    }

    public static String convertMuleExprToBal(Context ctx, String melExpression)
            throws ScriptConversionException {
        return convertMuleExprToBal(ctx, melExpression, false);
    }

    public static String convertMuleExprToBalStringLiteral(Context ctx, String melExpression)
            throws ScriptConversionException {
        return convertMuleExprToBal(ctx, melExpression, true);
    }

    private static String convertMuleExprToBal(Context ctx, String melExpression,
            boolean addToStringCalls)
            throws ScriptConversionException {
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
            StringBuilder result, boolean addToStringCalls)
            throws ScriptConversionException {
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

    public static String processPropertyName(Context ctx, String propertyName) {
        String configVarName = propertyName.replace('.', '_').replaceAll("::", "_");
        String escapedConfigVarName = convertToBalIdentifier(configVarName);
        if (!ctx.projectCtx.configurableVarExists(escapedConfigVarName)) {
            addConfigVarEntry(ctx, configVarName, null);
        }

        return escapedConfigVarName;
    }

    public static void addConfigVarEntry(Context ctx, String varName, String varValue) {
        String escapedVarName = convertToBalIdentifier(varName);
        String valueExpr = requiredConfigValue(varValue) ? "?" : "\"%s\"".formatted(varValue);
        var configVarDecl = new ModuleVar(escapedVarName, "string", Optional.of(exprFrom(valueExpr)), false, true);
        ctx.addConfigurableVar(escapedVarName, configVarDecl);
    }

    private static boolean requiredConfigValue(String varValue) {
        return varValue == null || varValue.startsWith("${");
    }

    public static String getAttrVal(Context ctx, String propValue)
            throws ScriptConversionException {
        return getAttrVal(ctx, propValue, false);
    }

    public static String getAttrValInt(Context ctx, String propValue)
            throws ScriptConversionException {
        return getAttrVal(ctx, propValue, true);
    }

    private static String getAttrVal(Context ctx, String propValue, boolean isInt)
            throws ScriptConversionException {
        if (propValue.startsWith("${") && propValue.endsWith("}")) {
            String configVarRef = processPropertyName(ctx, propValue.substring(2, propValue.length() - 1));
            return isInt ? "check int:fromString(%s)".formatted(configVarRef) : configVarRef;
        } else if (propValue.startsWith("#[") && propValue.endsWith("]")) {
            return convertMELToBal(ctx, propValue, false);
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
            case Constants.ATTRIBUTES_FIELD_ACCESS + ".uriParams" -> {
                return "map<string>";
            }
            case Constants.ATTRIBUTES_FIELD_ACCESS + ".request.getQueryParams()" -> {
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
        } else if (balExpr.startsWith(Constants.ATTRIBUTES_FIELD_ACCESS + ".request.getQueryParamValue(")) {
            return "string";
        } else if (balExpr.startsWith(Constants.ATTRIBUTES_FIELD_ACCESS + ".uriParams.get(")) {
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

    public static String convertToUnsupportedTODO(Context ctx, List<UnsupportedBlock> unsupportedBlocks) {
        assert !unsupportedBlocks.isEmpty();
        StringBuilder sb = new StringBuilder();
        for (UnsupportedBlock unsupportedBlock : unsupportedBlocks) {
            sb.append(unsupportedBlock.xmlBlock()).append("/n");
            ctx.migrationMetrics.failedBlocks.add(unsupportedBlock.xmlBlock());
        }

        return wrapElementInUnsupportedBlockComment(sb.toString());
    }

    public static String convertToUnsupportedTODO(Context ctx, UnsupportedBlock unsupportedBlock) {
        ctx.migrationMetrics.failedBlocks.add(unsupportedBlock.xmlBlock());
        return wrapElementInUnsupportedBlockComment(unsupportedBlock.xmlBlock());
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
