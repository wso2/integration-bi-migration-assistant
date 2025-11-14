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

import mule.common.MuleXMLTagBase;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

public enum MuleXMLTag implements MuleXMLTagBase {

    // Global Elements
    IMPORT("import", 1),
    HTTP_LISTENER_CONFIG("http:listener-config", 3),
    HTTP_LISTENER_CONNECTION("http:listener-connection", 2),
    HTTP_REQUEST_CONFIG("http:request-config", 3),
    HTTP_REQUEST_CONNECTION("http:request-connection", 2),
    DB_CONFIG("db:config", 1),
    DB_MY_SQL_CONNECTION("db:my-sql-connection", 3),
    DB_ORACLE_CONNECTION("db:oracle-connection", 3),
    DB_GENERIC_CONNECTION("db:generic-connection", 3),
    VM_CONFIG("vm:config", 1),
    CONFIGURATION_PROPERTIES("configuration-properties", 1),
    GLOBAL_PROPERTY("global-property", 1),
    APIKIT_CONFIG("apikit:config", 1),
    APIKIT_ROUTER("apikit:router", 1),

    // Flow Control
    CHOICE("choice", 3),
    WHEN("when", 2),
    OTHERWISE("otherwise", 2),
    SCATTER_GATHER("scatter-gather", 4),
    FIRST_SUCCESSFUL("first-successful", 4),
    ROUTE("route", 2),

    // Components
    LOGGER("logger", 1),
    TRANSFORM_MESSAGE("ee:transform", 3),
    EXPRESSION_COMPONENT("expression-component", 3),

    // Endpoints
    SCHEDULER("scheduler", 3),
    SCHEDULING_STRATEGY("scheduling-strategy", 1),
    FIXED_FREQUENCY("fixed-frequency", 1),

    // Scopes
    FLOW("flow", 3),
    SUB_FLOW("sub-flow", 3),
    FLOW_REFERENCE("flow-ref", 3),
    ENRICHER("enricher", 5),
    ASYNC("async", 4),
    TRY("try", 2),
    FOREACH("foreach", 3),

    // Transformers
    SET_VARIABLE("set-variable", 1),
    REMOVE_VARIABLE("remove-variable", 1),
    REMOVE_SESSION_VARIABLE("remove-session-variable", 1),
    SET_PAYLOAD("set-payload", 3),
    OBJECT_TO_JSON("json:object-to-json-transformer", 2),
    OBJECT_TO_STRING("object-to-string-transformer", 2),

    // Error Handling
    ERROR_HANDLER("error-handler", 2),
    ON_ERROR_CONTINUE("on-error-continue", 2),
    ON_ERROR_PROPAGATE("on-error-propagate", 2),
    RAISE_ERROR("raise-error", 2),

    // HTTP Module
    HTTP_LISTENER("http:listener", 5),
    HTTP_REQUEST("http:request", 4),
    HTTP_REQEUST_BUILDER("http:request-builder", 3),
    HTTP_QUERY_PARAM("http:query-param", 2),
    HTTP_HEADERS("http:headers", 2),
    HTTP_URI_PARAMS("http:uri-params", 2),
    HTTP_QUERY_PARAMS("http:query-params", 2),

    // VM connector
    VM_LISTENER("vm:listener", 5),
    VM_PUBLISH("vm:publish", 5),
    VM_CONSUME("vm:consume", 5),
    VM_QUEUES("vm:queues", 1),
    VM_QUEUE("vm:queue", 1),

    // Anypoint MQ connector
    ANYPOINT_MQ_CONFIG("anypoint-mq:config", 3),
    ANYPOINT_MQ_SUBSCRIBER("anypoint-mq:subscriber", 5),
    ANYPOINT_MQ_CONNECTION("anypoint-mq:connection", 2),

    // Database Connector
    DB_INSERT("db:insert", 2),
    DB_SELECT("db:select", 2),
    DB_UPDATE("db:update", 2),
    DB_DELETE("db:delete", 2),
    DB_SQL("db:sql", 3),
    DB_INPUT_PARAMETERS("db:input-parameters", 2),

    // Dataweave
    EE_MESSAGE("ee:message", 2),
    EE_SET_VARIABLE("ee:set-variable", 2),
    EE_SET_PAYLOAD("ee:set-payload", 2),
    EE_VARIABLES("ee:variables", 2),

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
