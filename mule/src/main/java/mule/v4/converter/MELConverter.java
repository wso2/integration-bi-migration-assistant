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
package mule.v4.converter;

import mule.v4.Constants;
import mule.v4.Context;
import mule.v4.ConversionUtils;

import static mule.v4.ConversionUtils.getAttrVal;
import static mule.v4.ConversionUtils.isTokenChar;

public class MELConverter {

    private static String normalize(String melExpr) {
        String normalizedExpr = melExpr.trim();
        if (normalizedExpr.startsWith("output")) {
            int dashIndex = normalizedExpr.indexOf("---");
            if (dashIndex != -1) {
                normalizedExpr = normalizedExpr.substring(dashIndex + 3).trim();
            }
        }
        normalizedExpr = normalizedExpr.replace("Mule::p(", "p(");
        return normalizedExpr;
    }

    /**
     * Converts a MEL expression to a Ballerina expression.
     *
     * @param ctx              Mule to bal converter data
     * @param mel              MEL expression to convert (in form #[...])
     * @param addToStringCalls flag to add toString() calls to converted tokens
     * @return equivalent Ballerina expression
     * @throws ScriptConversionException if conversion fails
     */
    public static String convertMELToBal(Context ctx, String mel, boolean addToStringCalls)
            throws ScriptConversionException {
        try {
            return convertMELToBalInner(ctx, mel, addToStringCalls);
        } catch (Exception e) {
            throw new ScriptConversionException(mel, e);
        }
    }

    private static String convertMELToBalInner(Context ctx, String mel, boolean addToStringCalls)
            throws ScriptConversionException {
        if (!mel.startsWith("#[") || !mel.endsWith("]")) {
            throw new IllegalArgumentException("Invalid MEL expression format: " + mel);
        }

        String melExpr = mel.substring(2, mel.length() - 1).trim();

        melExpr = normalize(melExpr);
        // Handle empty expression
        if (melExpr.isEmpty()) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        StringBuilder token = new StringBuilder();

        for (int i = 0; i < melExpr.length(); i++) {
            char currentChar = melExpr.charAt(i);

            String tokenStr = token.toString();
            if (currentChar == '\'' || currentChar == '\"') {
                // Handle string literals
                processToken(token, result, addToStringCalls);
                i = processStringLiteral(ctx, melExpr, i, result);
                continue;
            }

            if (currentChar == '.' || currentChar == '[') {
                if (isAttributesToken(tokenStr)) {
                    // We reach here for two kinds of syntax.
                    // 1. attributes.queryParams.foo
                    // 2. attributes['queryParams'].foo
                    i = processAttributes(melExpr, i, tokenStr, result, addToStringCalls);
                    token.setLength(0);
                    continue;
                }
                if (isVarsToken(tokenStr)) {
                    // We reach here for two kinds of syntax.
                    // 1. vars.foo
                    // 2. vars['foo']
                    i = processVars(melExpr, i, tokenStr, result, addToStringCalls);
                    token.setLength(0);
                    continue;
                }
            }

            if (currentChar == '(') {
                if (i + 1 < melExpr.length() && melExpr.charAt(i + 1) == '\'' && tokenStr.equals("p")) {
                    i = processPPropAccess(ctx, melExpr, i, tokenStr, result);
                    token.setLength(0);
                    continue;
                }
                if (tokenStr.equals("exception.causedBy")) {
                    // Handle choice-exception conditions like exception.causedBy(java.lang.NullPointerException)
                    i = processExceptionCausedBy(melExpr, i, tokenStr, result);
                    token.setLength(0);
                    continue;
                }
            }

            if (currentChar == '[' && !token.isEmpty()) {
                // Handle general array access like payload[0].lat
                processToken(token, result, false); // Don't add toString here
                i = processArrayAccess(melExpr, i, result);
                continue;
            }

            if (currentChar == '+' && i + 1 < melExpr.length() && melExpr.charAt(i + 1) == '+') {
                // e.g. melExpr = "payload ++ 'foo' ++ 'bar'"
                i++; // Skip the next '+'
            }

            // Build tokens for identifiers and keywords
            if (isTokenChar(currentChar)) {
                token.append(currentChar);
            } else {
                switch (token.toString()) {
                    case "and" -> result.append("&&");
                    case "or" -> result.append("||");
                    case "map" -> result.append(".map(");
                    case "contains" -> {
                        throw new UnsupportedOperationException("query operations not supported");
                    }
                    default -> {
                        processToken(token, result, addToStringCalls);
                        result.append(currentChar);
                    }
                }
                token.setLength(0);
            }
        }

        // Process any remaining token
        processToken(token, result, addToStringCalls);

        return result.toString();
    }

    private static boolean isAttributesToken(String token) {
        return token.equals("message.attributes") || token.equals("attributes");
    }

    private static boolean isVarsToken(String token) {
        return token.equals("vars");
    }

    private static int processStringLiteral(Context ctx, String melExpr, int startPos,
            StringBuilder result)
            throws ScriptConversionException {
        char startingChar = melExpr.charAt(startPos);
        int i = startPos + 1;

        // Handle empty string
        if (i < melExpr.length() && melExpr.charAt(i) == startingChar) {
            result.append("\"\"");
            return i;
        }

        StringBuilder stringLiteral = new StringBuilder();
        while (i < melExpr.length()) {
            char currentChar = melExpr.charAt(i);
            if (currentChar == startingChar) {
                break;
            }

            // Escape quotes in string literals
            if (currentChar == '\'' || currentChar == '\"') {
                stringLiteral.append("\\");
            }
            stringLiteral.append(currentChar);
            i++;
        }

        result.append(getAttrVal(ctx, stringLiteral.toString()));
        return i;
    }

    private static int processPPropAccess(Context ctx, String melExpr, int startPos, String baseToken,
                                          StringBuilder result) {
        StringBuilder externalPropName = new StringBuilder();
        int i = startPos;

        assert melExpr.charAt(i) == '(';
        assert melExpr.charAt(i + 1) == '\'';
        i = i + 2; // Skip open paren and starting quote

        while (i < melExpr.length() && melExpr.charAt(i) != '\'') {
            externalPropName.append(melExpr.charAt(i));
            i++;
        }

        if (i < melExpr.length() && melExpr.charAt(i) == '\'') {
            i++; // Skip the ending quote
        }

        if (i < melExpr.length() && melExpr.charAt(i) == ')') {
            i++; // Skip the closing parenthesis
        }

        String configVarName = ConversionUtils.processPropertyName(ctx, externalPropName.toString());
        result.append(configVarName);

        i--; // Adjust for the next iteration
        return i;
    }

    private static int processExceptionCausedBy(String melExpr, int startPos, String baseToken, StringBuilder result) {
        StringBuilder javaExceptionRef = new StringBuilder();
        int i = startPos;

        assert melExpr.charAt(i) == '(';
        i++; // Skip the opening parenthesis

        while (i < melExpr.length() && isTokenChar(melExpr.charAt(i))) {
            javaExceptionRef.append(melExpr.charAt(i));
            i++;
        }

        if (i < melExpr.length() && melExpr.charAt(i) == ')') {
            i++; // Skip the closing parenthesis
        }

        result.append(Constants.ON_FAIL_ERROR_VAR_REF).append(".message() == ")
                .append("\"").append(javaExceptionRef).append("\"");

        i--; // Adjust for the next iteration
        return i;
    }

    private static int processVars(String melExpr, int startPos, String baseToken, StringBuilder result,
                                   boolean addToStringCalls) {
        StringBuilder varNameToken = new StringBuilder();
        int i = startPos;

        char c = melExpr.charAt(i);
        if (c == '[') {
            i++; // Skip the opening bracket
            i++; // Skip the opening quote
        } else if (c == '.') {
            i++; // Skip the dot
        } else {
            throw new IllegalStateException("Unexpected character: " + c);
        }

        while (i < melExpr.length() && isTokenChar(melExpr.charAt(i))) {
            varNameToken.append(melExpr.charAt(i));
            i++;
        }

        if (i < melExpr.length() && melExpr.charAt(i) == '\'') {
            i++; // Skip the closing quote
            i++; // Skip the closing bracket
        }

        String varName = ConversionUtils.convertToBalIdentifier(varNameToken.toString());
        result.append(Constants.VARS_FIELD_ACCESS).append("?.").append(varName);
        if (addToStringCalls) {
            result.append(".toString()");
        }

        i--; // Adjust for the next iteration
        return i;
    }

    private static int processArrayAccess(String melExpr, int startBracketPos, StringBuilder result) {
        int i = startBracketPos;
        int bracketDepth = 0;
        StringBuilder arrayAccessStr = new StringBuilder();

        // Capture the entire array access expression, could be nested
        do {
            char current = melExpr.charAt(i);
            arrayAccessStr.append(current);

            if (current == '[') {
                bracketDepth++;
            } else if (current == ']') {
                bracketDepth--;
            }

            i++;
        } while (i < melExpr.length() && bracketDepth > 0);

        // Check if this is followed by a property access
        boolean hasPropertyAccess = false;
        String propertyName = "";
        if (i < melExpr.length() && melExpr.charAt(i) == '.') {
            hasPropertyAccess = true;
            i++; // Skip the dot

            // Capture the property name
            StringBuilder propName = new StringBuilder();
            while (i < melExpr.length() && isTokenChar(melExpr.charAt(i))) {
                propName.append(melExpr.charAt(i));
                i++;
            }
            propertyName = propName.toString();
        }

        // Generate Ballerina code for array access
        if (hasPropertyAccess) {
            // For patterns like payload[0].lat -> (check ctx.payload.ensureType(jsonMap))[0].get("lat")
            result.append(arrayAccessStr).append(".get(\"").append(propertyName).append("\")");
        } else {
            // For simple array access like payload[0]
            result.append(arrayAccessStr);
        }

        i--; // Adjust for loop increment
        return i;
    }

    private static int processAttributes(String melExpr, int startPos, String baseToken, StringBuilder result,
                                         boolean addToStringCalls) {
        StringBuilder attributeKeyToken = new StringBuilder();
        int i = startPos;

        char c = melExpr.charAt(i);
        if (c == '[') {
            i++; // Skip the opening bracket
            i++; // Skip the opening quote
        } else if (c == '.') {
            i++; // Skip the dot
        } else {
            throw new IllegalStateException("Unexpected character: " + c);
        }

        while (i < melExpr.length() && isTokenChar(melExpr.charAt(i)) && melExpr.charAt(i) != '.') {
            attributeKeyToken.append(melExpr.charAt(i));
            i++;
        }

        if (i < melExpr.length() && melExpr.charAt(i) == '\'') {
            i++; // Skip the closing quote
            i++; // Skip the closing bracket
        }

        // Capture param access if present (e.g., attributes.uriParams.paramName)
        StringBuilder paramAccessToken = new StringBuilder();
        if (i < melExpr.length() && melExpr.charAt(i) == '.') {
            i++; // Skip the dot

            while (i < melExpr.length() && isTokenChar(melExpr.charAt(i))) {
                paramAccessToken.append(melExpr.charAt(i));
                i++;
            }
        }

        String paramName = ConversionUtils.convertToBalIdentifier(paramAccessToken.toString());
        String tokenResult = convertAttributeAccess(attributeKeyToken.toString(), paramName);
        result.append(tokenResult);
        if (addToStringCalls) {
            result.append(".toString()");
        }

        i--; // Adjust for the next iteration
        return i;
    }

    private static String convertAttributeAccess(String attributeKey, String paramName) {
        StringBuilder resultantStr = new StringBuilder();
        resultantStr.append(Constants.ATTRIBUTES_FIELD_ACCESS);
        switch (attributeKey) {
            case "uriParams" -> {
                resultantStr.append(".uriParams");
                if (!paramName.isEmpty()) {
                    resultantStr.append(".get(\"%s\")".formatted(paramName));
                }
            }
            case "queryParams" -> {
                resultantStr.append(".request");
                if (paramName.isEmpty()) {
                    resultantStr.append(".getQueryParams()");
                } else {
                    resultantStr.append(".getQueryParamValue(\"%s\")".formatted(paramName));
                }
            }

            case "method" -> resultantStr.append(".request.method");
            default -> {
                resultantStr.append("[\"%s\"]".formatted(attributeKey));
                if (!paramName.isEmpty()) {
                    resultantStr.append(".").append(paramName);
                }
            }
        }
        return resultantStr.toString();
    }

    private static void processToken(StringBuilder token, StringBuilder result, boolean addToStringCalls) {
        if (!token.isEmpty()) {
            String tokenStr = token.toString();
            String balStr = convertMuleTokenToBallerina(tokenStr, addToStringCalls);
            result.append(balStr);
            token.setLength(0);
        }
    }

    private static String convertMuleTokenToBallerina(String str, boolean addToStringCalls) {
        String balStr;
         if (str.startsWith("payload.")) {
            balStr = "ctx." + str;
        } else {
            balStr = switch (str) {
                case "payload", "message.payload" -> "ctx.payload";
                case "message" -> "ctx";
                case "null" -> "()";
                case "error.description" -> "%s.message()".formatted(Constants.ON_FAIL_ERROR_VAR_REF);
                case "as" -> "is"; // TODO: Not supported. This is to format it in a pretty way.
                default -> str;
            };
        }

        return addToStringCalls ? balStr + ".toString()" : balStr;
    }
}
