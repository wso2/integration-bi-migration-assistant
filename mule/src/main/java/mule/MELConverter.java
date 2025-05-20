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

public class MELConverter {

    /**
     * Converts a MEL expression to a Ballerina expression.
     *
     * @param mel MEL expression to convert (in form #[...])
     * @param addToStringCalls flag to add toString() calls to converted tokens
     * @return equivalent Ballerina expression
     */
    public static String convertMELToBal(String mel, boolean addToStringCalls) {
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
                i = processStringLiteral(melExpr, i, result);
                continue;
            }

            if ((currentChar == '[' || currentChar == '.') && isInboundPropertyToken(tokenStr)) {
                // We reach here for two kinds of syntax.
                // 1. message.inboundProperties.'http.query.params'.foo
                // 2. message.inboundProperties['http.query.params'].foo
                i = processInboundProperty(melExpr, i, tokenStr, result, addToStringCalls);
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

    private static int processStringLiteral(String melExpr, int startPos, StringBuilder result) {
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

        result.append("\"").append(stringLiteral).append("\"");
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
        i--; // Adjust for loop increment

        // Generate Ballerina code for array access
        if (hasPropertyAccess) {
            // For patterns like payload[0].lat -> (check ctx.payload.ensureType(jsonMap))[0].get("lat")
            result.append(arrayAccessStr).append(".get(\"").append(propertyName).append("\")");
        } else {
            // For simple array access like payload[0]
            result.append(arrayAccessStr);
        }

        return i;
    }

    private static int processInboundProperty(String melExpr, int startPos, String tokenStr,
                                              StringBuilder result, boolean addToStringCalls) {
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

        String tokenResult = convertInboundPropertyAccess(propertyKeyToken.toString(), paramAccessToken.toString());
        result.append(tokenResult);

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

    private static boolean isTokenChar(char currentChar) {
        return Character.isLetterOrDigit(currentChar) || currentChar == '.' || currentChar == '_';
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
        // Check for compound expressions first (e.g., flowVars.foo)
        if (str.startsWith("flowVars.")) { // TODO: make optional field access
            String[] split = str.split("\\.");
            balStr = "ctx." + String.join(".", split);
        } else if (str.startsWith("sessionVars.")) {
            String[] split = str.split("\\.");
            balStr = "ctx." + String.join(".", split);
        } else if (str.startsWith("payload.")) {
            balStr = "ctx." + str;
        } else {
            // Then handle simple token replacements
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
