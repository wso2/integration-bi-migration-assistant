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
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package mule.v4.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

public enum MuleXMLTag {

    // Global Elements
    HTTP_LISTENER_CONFIG("http:listener-config", 3),
    HTTP_LISTENER_CONNECTION("http:listener-connection", 2),
    HTTP_REQUEST_CONFIG("http:request-config", 3),
    HTTP_REQUEST_CONNECTION("http:request-connection", 2),
    DB_CONFIG("db:config", 1),
    DB_MY_SQL_CONNECTION("db:my-sql-connection", 3),
    DB_ORACLE_CONNECTION("db:oracle-connection", 3),

    // Flow Control
    CHOICE("choice", 3),
    WHEN("when", 2),
    OTHERWISE("otherwise", 2),

    // Components
    LOGGER("logger", 1),
    TRANSFORM_MESSAGE("dw:transform-message", 3),
    EXPRESSION_COMPONENT("expression-component", 3),

    // Scopes
    FLOW("flow", 3),
    SUB_FLOW("sub-flow", 3),
    FLOW_REFERENCE("flow-ref", 3),
    ENRICHER("enricher", 5),
    ASYNC("async", 4),

    // Transformers
    SET_VARIABLE("set-variable", 1),
    SET_SESSION_VARIABLE("set-session-variable", 1),
    REMOVE_VARIABLE("remove-variable", 1),
    REMOVE_SESSION_VARIABLE("remove-session-variable", 1),
    SET_PAYLOAD("set-payload", 3),
    OBJECT_TO_JSON("json:object-to-json-transformer", 2),
    OBJECT_TO_STRING("object-to-string-transformer", 2),

    // Error Handling
    ERROR_HANDLER("error-handler", 2),
    ON_ERROR_CONTINUE("on-error-continue", 2),
    ON_ERROR_PROPAGATE("on-error-propagate", 2),

    // HTTP Module
    HTTP_LISTENER("http:listener", 5),
    HTTP_REQUEST("http:request", 4),
    HTTP_REQEUST_BUILDER("http:request-builder", 3),
    HTTP_QUERY_PARAM("http:query-param", 2),

    // VM connector
    VM_CONFIG("vm:config", 1),
    VM_LISTENER("vm:listener", 5),
    VM_PUBLISH("vm:publish", 5),
    VM_CONSUME("vm:consume", 5),
    VM_QUEUES("vm:queues", 1),
    VM_QUEUE("vm:queue", 1),

    // Database Connector
    DB_INSERT("db:insert", 2),
    DB_SELECT("db:select", 2),
    DB_UPDATE("db:update", 2),
    DB_DELETE("db:delete", 2),
    DB_SQL("db:sql", 3),
    DB_INPUT_PARAMETERS("db:input-parameters", 2),

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
    SET_PROPERTY("set-property", 1),
    SMTP_OUTBOUND_ENDPOINT("smtp:outbound-endpoint", 4),

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
