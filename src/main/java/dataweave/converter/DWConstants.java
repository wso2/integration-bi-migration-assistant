package dataweave.converter;

import java.util.regex.Pattern;

public class DWConstants {
    public static final String SEPARATOR = "---";
    public static final String DW_FUNCTION_PREFIX = "dw(";
    public static final String SINGLE_QUOTE = "'";
    public static final String DOUBLE_QUOTE = "\"";
    public static final String VERTICAL_LINE = "|";
    public static final String SQUARE_START_BRACKET = "[";
    public static final String SQUARE_END_BRACKET = "]";
    public static final String NUMBER_REGEX = "^-?(\\d+\\.\\d+|\\d+|\\.\\d+)$";
    public static final Pattern NUMBER_PATTERN = Pattern.compile(NUMBER_REGEX);
    public static final String CURLY_START_BRACKET = "{";
    public static final String CURLY_END_BRACKET = "}";
    public static final String DATAWEAVE_OUTPUT_VARIABLE_NAME = "_dwOutput";

    public enum OutputDirective {
        APPLICATION_JAVA("application", "java"),
        APPLICATION_CSV("application", "csv"),
        APPLICATION_JSON("application", "json"),
        APPLICATION_XML("application", "xml"),
        APPLICATION_DW("application", "dw"),
        TEXT_CSV("text", "csv"),
        TEXT_JSON("text", "json"),
        TEXT_XML("text", "xml"),
        TEXT_PLAIN("text", "plain");

        private final String primary;
        private final String secondary;

        OutputDirective(String primary, String secondary) {
            this.primary = primary;
            this.secondary = secondary;
        }

        public static OutputDirective findDirective(String primary, String secondary) {
            for (OutputDirective outputDirective : values()) {
                if (outputDirective.primary.equals(primary) && outputDirective.secondary.equals(secondary)) {
                    return outputDirective;
                }
            }
            throw new BallerinaDWException("Invalid output directive: " + primary + " " + secondary);
        }
    }

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
}
