package mule;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

public enum MuleXMLTag {

    // Global Elements
    HTTP_LISTENER_CONFIG("http:listener-config"),
    DB_MYSQL_CONFIG("db:mysql-config"),
    DB_TEMPLATE_QUERY("db:template-query"),

    // Flow Control
    CHOICE("choice", 4),

    // Components
    LOGGER("logger", 2),
    TRANSFORM_MESSAGE("dw:transform-message"),

    // Scopes
    FLOW("flow", 3),
    SUB_FLOW("sub-flow", 3),
    FLOW_REFERENCE("flow-ref"),
    ENRICHER("enricher"),

    // Transformers
    SET_VARIABLE("set-variable", 2),
    SET_SESSION_VARIABLE("set-session-variable", 2),
    SET_PAYLOAD("set-payload", 2),
    OBJECT_TO_JSON("json:object-to-json-transformer"),
    OBJECT_TO_STRING("object-to-string-transformer"),

    // Error Handling
    CATCH_EXCEPTION_STRATEGY("catch-exception-strategy"),
    CHOICE_EXCEPTION_STRATEGY("choice-exception-strategy"),
    REFERENCE_EXCEPTION_STRATEGY("exception-strategy"),

    // HTTP Module
    HTTP_LISTENER("http:listener", 4),
    HTTP_REQUEST("http:request", 4),
    HTTP_QUERY_PARAMS("http:query-params"),

    // Database Connector
    DB_INSERT("db:insert"),
    DB_SELECT("db:select"),
    DB_UPDATE("db:update"),
    DB_DELETE("db:delete"),
    DB_PARAMETERIZED_QUERY("db:parameterized-query", 5),
    DB_DYNAMIC_QUERY("db:dynamic-query"),
    DB_TEMPLATE_QUERY_REF("db:template-query-ref"),

    // Dataweave
    DW_SET_VARIABLE("dw:set-variable", 2),
    DW_SET_SESSION_VARIABLE("dw:set-session-variable", 2),
    DW_SET_PAYLOAD("dw:set-payload", 2),
    DW_INPUT_PAYLOAD("dw:input-payload"),


    UNSUPPORTED_TAG("unsupported-tag");

    private final String tag;
    private final int weight;

    MuleXMLTag(String tag) {
        this.tag = tag;
        this.weight = 1; // default weight is considered to be 1
    }

    MuleXMLTag(String tag, int weight) {
        this.tag = tag;
        this.weight = weight;
    }

    public String tag() {
        return tag;
    }

    public int weight() {
        return weight;
    }

    public static MuleXMLTag fromTag(String tag) {
        for (MuleXMLTag muleXMLTag : values()) {
            if (muleXMLTag.tag.equals(tag)) {
                return muleXMLTag;
            }
        }
        return UNSUPPORTED_TAG;
    }

    public static final HashSet<String> COMPATIBLE_XML_TAGS =
            Arrays.stream(MuleXMLTag.values()).map(MuleXMLTag::tag).collect(Collectors.toCollection(HashSet::new));

    public static boolean isCompatible(String tag) {
        return COMPATIBLE_XML_TAGS.contains(tag);
    }

    public static final Map<String, Integer> TAG_WEIGHTS_MAP =
            Arrays.stream(MuleXMLTag.values()).collect(Collectors.toMap(MuleXMLTag::tag, MuleXMLTag::weight));

    public static int getWeightFromTag(String tag) {
        return TAG_WEIGHTS_MAP.getOrDefault(tag, 1);
    }
}
