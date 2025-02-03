package dataweave.converter;

import ballerina.BallerinaModel;

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
    public static final String ARRAY = "Array";
    public static final String BOOLEAN = "Boolean";
    public static final String DATE = "Date";
    public static final String DATETIME = "DateTime";
    public static final String FUNCTION = "Function";
    public static final String NULL = "Null";
    public static final String NUMBER = "Number";
    public static final String OBJECT = "Object";
    public static final String STRING = "String";
    public static final String TIME = "Time";

    // Ballerina Identifiers
    public static final String DW_FUNCTION_NAME = "_dwMethod%s_";

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
        throw new BallerinaDWException("Unsupported type: " + expression);
    }

    public static String getBallerinaType(String dwType) {
        return switch (dwType) {
            case DWUtils.ARRAY -> "anydata[]";
            case DWUtils.BOOLEAN -> "boolean";
            case DWUtils.FUNCTION -> "function";
            case DWUtils.NULL -> "()";
            case DWUtils.NUMBER -> "int";
            case DWUtils.OBJECT -> "map<anydata>";
            case DWUtils.STRING -> "string";
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
