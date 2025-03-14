package mule;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

public enum MuleXMLTag {

    // Global Elements
    HTTP_LISTENER_CONFIG("http:listener-config", 4),
    DB_MYSQL_CONFIG("db:mysql-config", 4),
    DB_TEMPLATE_QUERY("db:template-query", 3),

    // Flow Control
    CHOICE("choice", 3),
    WHEN("when", 2),
    OTHERWISE("otherwise", 2),

    // Components
    LOGGER("logger", 1),
    TRANSFORM_MESSAGE("dw:transform-message", 3),

    // Scopes
    FLOW("flow", 3),
    SUB_FLOW("sub-flow", 3),
    FLOW_REFERENCE("flow-ref", 3),
    ENRICHER("enricher", 4),

    // Transformers
    SET_VARIABLE("set-variable", 1),
    SET_SESSION_VARIABLE("set-session-variable", 2),
    SET_PAYLOAD("set-payload", 2),
    OBJECT_TO_JSON("json:object-to-json-transformer", 2),
    OBJECT_TO_STRING("object-to-string-transformer", 2),

    // Error Handling
    CATCH_EXCEPTION_STRATEGY("catch-exception-strategy", 2),
    CHOICE_EXCEPTION_STRATEGY("choice-exception-strategy", 4),
    REFERENCE_EXCEPTION_STRATEGY("exception-strategy", 3),

    // HTTP Module
    HTTP_LISTENER("http:listener", 4),
    HTTP_REQUEST("http:request", 4),
    HTTP_QUERY_PARAMS("http:query-params", 2),

    // Database Connector
    DB_INSERT("db:insert", 2),
    DB_SELECT("db:select", 2),
    DB_UPDATE("db:update", 2),
    DB_DELETE("db:delete", 2),
    DB_PARAMETERIZED_QUERY("db:parameterized-query", 3),
    DB_DYNAMIC_QUERY("db:dynamic-query", 3),
    DB_TEMPLATE_QUERY_REF("db:template-query-ref", 3),

    // Dataweave
    DW_SET_VARIABLE("dw:set-variable", 2),
    DW_SET_SESSION_VARIABLE("dw:set-session-variable", 2),
    DW_SET_PAYLOAD("dw:set-payload", 2),
    DW_INPUT_PAYLOAD("dw:input-payload", 2),


    // Yet to support
    SPRING_BEANS("spring:beans", 7),
    API_PLATFORM_GW_API("api-platform-gw:api", 8),
    CONFIGURATION("configuration", 1),
    SECURE_PROPERTY_PLACEHOLDER_CONFIG("secure-property-placeholder:config", 3),
    FILE_CONNECTOR("file:connector", 5),
    WS_CONSUMER_CONFIG("ws:consumer-config", 4),
    SMTP_CONNECTOR("smtp:connector", 4),
    ASYNC("async", 3),
    SET_PROPERTY("set-property", 1),
    SMTP_OUTBOUND_ENDPOINT("smtp:outbound-endpoint", 4),
    EXPRESSION_COMPONENT("expression-component", 3),

    UNSUPPORTED_TAG("unsupported-tag", 1000);

    private final String tag;
    private final int weight;

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
            Arrays.stream(MuleXMLTag.values()).filter(muleXMLTag -> SPRING_BEANS.compareTo(muleXMLTag) > 0)
                    .map(MuleXMLTag::tag).collect(Collectors.toCollection(HashSet::new));

    public static boolean isCompatible(String tag) {
        return COMPATIBLE_XML_TAGS.contains(tag);
    }

    public static final Map<String, Integer> TAG_WEIGHTS_MAP =
            Arrays.stream(MuleXMLTag.values()).collect(Collectors.toMap(MuleXMLTag::tag, MuleXMLTag::weight));

    private static final int DEFAULT_WEIGHT = 5;

    public static int getWeightFromTag(String tag) {
        return TAG_WEIGHTS_MAP.getOrDefault(tag, DEFAULT_WEIGHT);
    }
}
