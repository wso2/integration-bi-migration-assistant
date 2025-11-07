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

package common;

import java.util.regex.Pattern;

public class ConversionUtils {
    private static final Pattern UNESCAPED_SPECIAL_CHAR_SET = Pattern
            .compile("([$&+,:;=\\?@#\\\\|/'\\ \\[\\}\\]<\\>.\"^*{}~`()%!-])");

    public static BallerinaModel.Expression.BallerinaExpression exprFrom(String expr) {
        return new BallerinaModel.Expression.BallerinaExpression(expr);
    }

    public static BallerinaModel.Statement.BallerinaStatement stmtFrom(String stmt) {
        return new BallerinaModel.Statement.BallerinaStatement(stmt);
    }

    public static BallerinaModel.TypeDesc.BallerinaType typeFrom(String type) {
        return new BallerinaModel.TypeDesc.BallerinaType(type);
    }

    /**
     * Escapes special characters in an identifier with a preceding backslash (\).
     * This is part of making an identifier valid in Ballerina syntax.
     *
     * @param identifier the original identifier string
     * @return identifier with special characters escaped
     */
    public static String escapeSpecialCharacters(String identifier) {
        return UNESCAPED_SPECIAL_CHAR_SET.matcher(identifier).replaceAll("\\\\$1");
    }

    /**
     * Strips all special characters from an identifier.
     * This removes all characters matching UNESCAPED_SPECIAL_CHAR_SET to create a
     * valid identifier.
     *
     * @param identifier the original identifier string
     * @return identifier with special characters removed
     */
    public static String escapeIdentifier(String identifier) {
        return UNESCAPED_SPECIAL_CHAR_SET.matcher(identifier).replaceAll("");
    }
}
