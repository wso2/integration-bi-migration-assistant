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

    /**
     * Converts a MEL expression to a Ballerina expression.
     *
     * @param ctx             Mule to bal converter data
     * @param mel              MEL expression to convert (in form #[...])
     * @param addToStringCalls flag to add toString() calls to converted tokens
     * @return equivalent Ballerina expression
     */
    public static String convertMELToBal(Context ctx, String mel, boolean addToStringCalls) {
        if (!mel.startsWith("#[") || !mel.endsWith("]")) {
            throw new IllegalArgumentException("Invalid MEL expression format: " + mel);
        }

        String melExpr = mel.substring(2, mel.length() - 1).trim();

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

            if ((currentChar == '[' || currentChar == '.')) {
                if (isFlowOrSessionVarToken(tokenStr)) {
                    // We reach here for two kinds of syntax.
                    // 1. flowVars.foo
                    // 2. flowVars['foo']
                    i = processFlowOrSessionVars(melExpr, i, tokenStr, result, addToStringCalls);
                    token.setLength(0);
                    continue;
                }
                if (isInboundPropertyToken(tokenStr)) {
                    // We reach here for two kinds of syntax.
                    // 1. message.inboundProperties.'http.query.params'.foo
                    // 2. message.inboundProperties['http.query.params'].foo
                    i = processInboundProperty(melExpr, i, tokenStr, result, addToStringCalls);
                    token.setLength(0);
                    continue;
                }
            }

            if (currentChar == '(' && tokenStr.equals("exception.causedBy")) {
                // Handle choice-exception conditions like exception.causedBy(java.lang.NullPointerException)
                i = processExceptionCausedBy(melExpr, i, tokenStr, result);
                token.setLength(0);
                continue;
            }

            if (currentChar == '[' && !token.isEmpty()) {
                // Handle general array access like payload[0].lat
                processToken(token, result, false); // Don't add toString here
                i = processArrayAccess(melExpr, i, result);
                continue;
            }

            // Build tokens for identifiers and keywords
            if (isTokenChar(currentChar)) {
                token.append(currentChar);
            } else {
                processToken(token, result, addToStringCalls);
                result.append(currentChar);
            }
        }

        // Process any remaining token
        processToken(token, result, addToStringCalls);

        return result.toString();
    }

    private static boolean isInboundPropertyToken(String token) {
        return token.equals("message.inboundProperties") || token.equals("inboundProperties");
    }

    private static boolean isFlowOrSessionVarToken(String token) {
        return token.equals("flowVars") || token.equals("sessionVars");
    }

    private static int processStringLiteral(Context ctx, String melExpr, int startPos,
                                            StringBuilder result) {
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

    private static int processFlowOrSessionVars(String melExpr, int startPos, String baseToken, StringBuilder result,
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
        result.append(Constants.CONTEXT_REFERENCE).append(".").append(baseToken).append(".").append(varName);
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

    private static int processInboundProperty(String melExpr, int startPos, String baseToken, StringBuilder result,
                                              boolean addToStringCalls) {
        StringBuilder propertyKeyToken = new StringBuilder();
        int i = startPos;
        i++; // skip the opening bracket or dot

        assert melExpr.charAt(i) == '\'';
        propertyKeyToken.append('\'');
        i++;

        while (i < melExpr.length() && melExpr.charAt(i) != '\'') {
            propertyKeyToken.append(melExpr.charAt(i));
            i++;
        }

        propertyKeyToken.append('\''); // append the closing quote
        i++;

        if (i < melExpr.length() && melExpr.charAt(i) == ']') {
            i++; // Skip the closing bracket
        }

        // Capture property access if present (e.g., http.query.params.paramName)
        StringBuilder paramAccessToken = new StringBuilder();
        if (i < melExpr.length() && melExpr.charAt(i) == '.') {
            i++; // Skip the dot

            while (i < melExpr.length() && isTokenChar(melExpr.charAt(i))) {
                paramAccessToken.append(melExpr.charAt(i));
                i++;
            }
        }

        String paramName = ConversionUtils.convertToBalIdentifier(paramAccessToken.toString());
        String tokenResult = convertInboundPropertyAccess(propertyKeyToken.toString(), paramName);
        result.append(tokenResult);
        if (addToStringCalls) {
            result.append(".toString()");
        }

        i--; // Adjust for the next iteration
        return i;
    }

    private static String convertInboundPropertyAccess(String propertyKey, String paramName) {
        StringBuilder resultantStr = new StringBuilder();
        resultantStr.append("ctx.inboundProperties");
        switch (propertyKey) {
            case "'http.uri.params'" -> {
                resultantStr.append(".uriParams");
                if (!paramName.isEmpty()) {
                    resultantStr.append(".get(\"%s\")".formatted(paramName));
                }
            }
            case "'http.query.params'" -> {
                resultantStr.append(".request");
                if (paramName.isEmpty()) {
                    resultantStr.append(".getQueryParams()");
                } else {
                    resultantStr.append(".getQueryParamValue(\"%s\")".formatted(paramName));
                }
            }

            case "'http.method'" -> resultantStr.append(".request.method");
            default -> {
                resultantStr.append("[\"%s\"]".formatted(propertyKey.substring(1, propertyKey.length() - 1)));
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
                case "flowVars" -> "ctx.flowVars";
                case "sessionVars" -> "ctx.sessionVars";
                case "message.inboundProperties", "inboundProperties" -> "ctx.inboundProperties";
                default -> str;
            };
        }

        return addToStringCalls ? balStr + ".toString()" : balStr;
    }
}
