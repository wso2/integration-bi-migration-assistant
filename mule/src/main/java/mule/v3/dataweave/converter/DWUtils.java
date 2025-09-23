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

package mule.v3.dataweave.converter;

import common.BallerinaModel;
import mule.v3.Constants;
import mule.v3.Context;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DWUtils {
    public static final String SINGLE_QUOTE = "'";
    public static final String DOUBLE_QUOTE = "\"";
    public static final String VERTICAL_LINE = "|";
    public static final String SQUARE_START_BRACKET = "[";
    public static final String SQUARE_END_BRACKET = "]";
    public static final String NUMBER_REGEX = "^-?(\\d+\\.\\d+|\\d+|\\.\\d+)$";
    public static final Pattern NUMBER_PATTERN = Pattern.compile(NUMBER_REGEX);
    public static final String CURLY_START_BRACKET = "{";
    public static final String CURLY_END_BRACKET = "}";
    public static final String DATAWEAVE_OUTPUT_VARIABLE_NAME = "_dwOutput_";
    public static final String DW_INDEX_IDENTIFIER = "$$";
    public static final String DW_VALUE_IDENTIFIER = "$";
    public static final String DW_NOW_IDENTIFIER = "now";

    // IO Types
    public static final String APPLICATION_JAVA = "application/java";
    public static final String APPLICATION_CSV = "application/csv";
    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_XML = "application/xml";
    public static final String APPLICATION_DW = "application/dw";
    public static final String TEXT_CSV = "text/csv";
    public static final String TEXT_JSON = "text/json";
    public static final String TEXT_XML = "text/xml";
    public static final String TEXT_PLAIN = "text/plain";

    // DataWeave Types
    public static final String ARRAY = "array";
    public static final String BOOLEAN = "boolean";
    public static final String DATE = "date";
    public static final String DATETIME = "datetime";
    public static final String FUNCTION = "function";
    public static final String NULL = "null";
    public static final String NUMBER = "number";
    public static final String OBJECT = "object";
    public static final String STRING = "string";
    public static final String TIME = "time";
    public static final String LOCAL_DATE_TIME = "localdatetime";
    public static final String TIME_ZONE = "timeZone";
    public static final String PERIOD = "period";
    public static final String IDENTIFIER = "identifier";
    public static final String UNKNOWN = "unknown";

    // DataWeave Built-in functions
    public static final String DW_FUNCTION_MAP = "map";
    public static final String DW_FUNCTION_FILTER = "filter";
    public static final String DW_FUNCTION_FLATTEN = "flatten";
    public static final String DW_FUNCTION_SIZE_OF = "sizeOf";

    // Ballerina util functions
    public static final String INT_TO_STRING = "intToString";
    public static final String GET_FORMATTED_STRING_FROM_NUMBER = "getFormattedStringFromNumber";
    public static final String NEW_DECIMAL_FORMAT = "newDecimalFormat";
    public static final String GET_CURRENT_TIME_STRING = "getCurrentTimeString";
    public static final String FORMAT_DATE_TIME_STRING = "formatDateTimeString";
    public static final String FORMAT_DATE_TIME = "formatDateTime";
    public static final String GET_DATE_TIME_FORMATTER = "getDateTimeFormatter";
    public static final String GET_ZONE_ID = "getZoneId";
    public static final String GET_DATE_TIME = "getDateTime";
    public static final String PARSE_INSTANT = "parseInstant";
    public static final String GET_FORMATTED_STRING_FROM_DATE = "getFormattedStringFromDate";

    // Ballerina Identifiers
    public static final String DW_FUNCTION_NAME = "_dwMethod%s_";
    public static final String VAR_PREFIX = "_var_";
    public static final String ELEMENT_ARG = "element";

    public static final String TYPE_CAST_COMMENT_MATH = "\n// TODO: AMBIGUOUS TYPE FOUND FOR MATH OPERATOR" +
            " '%s'. MANUAL CASTING REQUIRED.\n";
    public static final String TYPE_CAST_COMMENT_COMPARISON = "\n// TODO: AMBIGUOUS TYPE FOUND FOR COMPARISON " +
            "OPERATOR '%s'. MANUAL CASTING REQUIRED.\n";
    public static final String MILLISECONDS = "\"milliseconds\"";
    public static final String UTC_VAR = "_utcValue_";
    public static final String PARSE_DATE_TIME = "parseDateTime";
    public static final String TO_INSTANT = "toInstant";
    public static final String UTC_ZONE_OFFSET = "UTC";
    public static final String GET_DATE_FROM_FORMATTED_STRING = "getDateFromFormattedString";
    public static final String UNSUPPORTED_DW_NODE = "\n//TODO: UNSUPPORTED DATAWEAVE EXPRESSION '%s' FOUND. " +
            "MANUAL CONVERSION REQUIRED.\n";
    public static final String UNSUPPORTED_DW_NODE_WITH_TYPE = "\n//TODO: UNSUPPORTED DATAWEAVE EXPRESSION " +
            "'%s' OF TYPE '%s' FOUND. MANUAL CONVERSION REQUIRED.\n";

    public static String findBallerinaType(String mediaType) {
        return switch (mediaType) {
            case APPLICATION_JAVA, APPLICATION_CSV, APPLICATION_DW -> "any";
            case APPLICATION_JSON, TEXT_JSON -> "json";
            case APPLICATION_XML, TEXT_XML -> "xml";
            case TEXT_CSV, TEXT_PLAIN -> "string";
            default -> throw new BallerinaDWException("Invalid output directive: " + mediaType);
        };
    }

    public static String getVarTypeFromExpression(String expression) {
        if (expression.startsWith(DWUtils.SINGLE_QUOTE) || expression.startsWith(DWUtils.DOUBLE_QUOTE)) {
            return DWUtils.STRING;
        }
        if (expression.equals("true") || expression.equals("false")) {
            return DWUtils.BOOLEAN;
        }
        if (expression.contains(DWUtils.VERTICAL_LINE)) {
            return DWUtils.DATE; // TODO : Need to handle all date types
        }
        Matcher matcher = DWUtils.NUMBER_PATTERN.matcher(expression);
        if (matcher.matches()) {
            return DWUtils.NUMBER;
        }
        if (expression.startsWith(DWUtils.SQUARE_START_BRACKET) &&
                expression.endsWith(DWUtils.SQUARE_END_BRACKET)) {
            return DWUtils.ARRAY;
        }
        if (expression.startsWith(DWUtils.CURLY_START_BRACKET) &&
                expression.endsWith(DWUtils.CURLY_END_BRACKET)) {
            return DWUtils.OBJECT;
        }
        return DWUtils.UNKNOWN;
    }

    public static String getBallerinaType(String dwType, Context ctx) {
        return switch (dwType) {
            case DWUtils.ARRAY -> "anydata[]";
            case DWUtils.BOOLEAN -> "boolean";
            case DWUtils.FUNCTION -> "function";
            case DWUtils.NULL -> "()";
            case DWUtils.NUMBER -> "int";
            case DWUtils.OBJECT -> "map<anydata>";
            case DWUtils.STRING -> "string";
            case DWUtils.DATE -> {
                ctx.addImport(new BallerinaModel.Import(Constants.ORG_BALLERINA, Constants.MODULE_TIME));
                yield "time:Date";
            }
            case DWUtils.DATETIME, DWUtils.LOCAL_DATE_TIME, DWUtils.PERIOD -> {
                ctx.addImport(new BallerinaModel.Import(Constants.ORG_BALLERINA, Constants.MODULE_TIME));
                yield "time:Civil";
            }
            case DWUtils.TIME -> {
                ctx.addImport(new BallerinaModel.Import(Constants.ORG_BALLERINA, Constants.MODULE_TIME));
                yield "time:TimeOfDayFields";
            }
            case DWUtils.TIME_ZONE -> {
                ctx.addImport(new BallerinaModel.Import(Constants.ORG_BALLERINA, Constants.MODULE_TIME));
                yield "time:ZoneOffset";
            }
            default -> "any";
        };
    }

    public static String getParamsString(List<BallerinaModel.Parameter> params) {
        return params
                .stream()
                .map(BallerinaModel.Parameter::name)
                .collect(Collectors.joining(","));
    }

}
