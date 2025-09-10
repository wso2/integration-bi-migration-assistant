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
package mule.v3.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public record MuleModel() {

    public record HttpListener(Kind kind, String configRef, String resourcePath, String[] allowedMethods)
            implements MuleRecord {
        public HttpListener(String configRef, String resourcePath, String[] allowedMethods) {
            this(Kind.HTTP_LISTENER, configRef, resourcePath, allowedMethods);
        }
    }

    public record VMInboundEndpoint(Kind kind, String path, String exchangePattern)
            implements MuleRecord {
        public VMInboundEndpoint(String path, String exchangePattern) {
            this(Kind.VM_INBOUND_ENDPOINT, path, exchangePattern);
        }
    }

    public record VMOutboundEndpoint(Kind kind, String path, String exchangePattern)
            implements MuleRecord {
        public VMOutboundEndpoint(String path, String exchangePattern) {
            this(Kind.VM_OUTBOUND_ENDPOINT, path, exchangePattern);
        }
    }

    public record QuartzInboundEndpoint(Kind kind, String jobName, String cronExpression,
                                      String repeatCount, String repeatInterval) implements MuleRecord {
        public QuartzInboundEndpoint(String jobName, String cronExpression, String repeatCount, String repeatInterval) {
            this(Kind.QUARTZ_INBOUND_ENDPOINT, jobName, cronExpression, repeatCount, repeatInterval);
        }
    }

    public record Logger(Kind kind, String message, LogLevel level) implements MuleRecord {
        public Logger(String message, LogLevel level) {
            this(Kind.LOGGER, message, level);
        }
    }

    public record ExpressionComponent(Kind kind, String exprCompContent) implements MuleRecord {
        public ExpressionComponent(String exprCompContent) {
            this(Kind.EXPRESSION_COMPONENT, exprCompContent);
        }
    }

    public record TransformMessage(Kind kind, List<TransformMessageElement> children) implements MuleRecord {
        public TransformMessage(List<TransformMessageElement> children) {
            this(Kind.TRANSFORM_MESSAGE, children);
        }
    }

    public interface TransformMessageElement extends MuleRecord {
    }

    public record SetPayloadElement(Kind kind, String resource, String script) implements TransformMessageElement {
        public SetPayloadElement(String resource, String script) {
            this(Kind.DW_SET_PAYLOAD, resource, script);
        }
    }

    public record SetVariableElement(Kind kind, String resource, String script, String variableName)
            implements TransformMessageElement {
        public SetVariableElement(String resource, String script, String variableName) {
            this(Kind.DW_SET_VARIABLE, resource, script, variableName);
        }
    }

    public record SetSessionVariableElement(Kind kind, String resource, String script, String variableName)
            implements TransformMessageElement {
        public SetSessionVariableElement(String resource, String script, String variableName) {
            this(Kind.DW_SET_SESSION_VARIABLE, resource, script, variableName);
        }
    }

    public record InputPayloadElement(Kind kind, String mimeType, String samplePath)
            implements TransformMessageElement {
        // TODO - add support for reader property
        public InputPayloadElement(String mimeType, String samplePath) {
            this(Kind.DW_INPUT_PAYLOAD, mimeType, samplePath);
        }
    }

    public enum LogLevel {
        DEBUG,
        ERROR,
        INFO,
        TRACE,
        WARN
    }

    public record FlowReference(Kind kind, String flowName) implements MuleRecord {
        public FlowReference(String flowName) {
            this(Kind.FLOW_REFERENCE, flowName);
        }
    }

    public record ReferenceExceptionStrategy(Kind kind, String refName) implements MuleRecord {
        public ReferenceExceptionStrategy(String flowName) {
            this(Kind.REFERENCE_EXCEPTION_STRATEGY, flowName);
        }
    }

    public record HttpRequest(Kind kind, String configRef, String method, String url, String path,
                              Map<String, String> queryParams) implements MuleRecord {
        public HttpRequest(String configRef, String method, String url, String path, Map<String, String> queryParams) {
            this(Kind.HTTP_REQUEST, configRef, method, url, path, queryParams);
        }
    }

    public record Choice(Kind kind, List<WhenInChoice> whens, List<MuleRecord> otherwiseProcess) implements MuleRecord {
        public Choice(List<WhenInChoice> whens, List<MuleRecord> otherwiseProcess) {
            this(Kind.CHOICE, whens, otherwiseProcess);
        }
    }

    public record WhenInChoice(Kind kind, String condition, List<MuleRecord> process) implements MuleRecord {
        public WhenInChoice(String condition, List<MuleRecord> process) {
            this(Kind.WHEN_IN_CHOICE, condition, process);
        }
    }

    // Scopes
    public record Flow(Kind kind, String name, Optional<MuleRecord> source, List<MuleRecord> flowBlocks)
            implements MuleRecord {
        public Flow(String name, Optional<MuleRecord> source, List<MuleRecord> flowBlocks) {
            this(Kind.FLOW, name, source, flowBlocks);
        }
    }

    public record SubFlow(Kind kind, String name, List<MuleRecord> flowBlocks) implements MuleRecord {
        public SubFlow(String name, List<MuleRecord> flowBlocks) {
            this(Kind.SUB_FLOW, name, flowBlocks);
        }
    }

    public record Async(Kind kind, List<MuleRecord> flowBlocks) implements MuleRecord {
        public Async(List<MuleRecord> flowBlocks) {
            this(Kind.ASYNC, flowBlocks);
        }
    }

    public record Foreach(Kind kind, String collection, List<MuleRecord> flowBlocks) implements MuleRecord {
        public Foreach(String collection, List<MuleRecord> flowBlocks) {
            this(Kind.FOREACH, collection, flowBlocks);
        }
    }

    public record ProcessorChain(Kind kind, List<MuleRecord> flowBlocks) implements MuleRecord {
        public ProcessorChain(List<MuleRecord> flowBlocks) {
            this(Kind.PROCESSOR_CHAIN, flowBlocks);
        }
    }

    public record Enricher(Kind kind, String source, String target, Optional<MuleRecord> innerBlock)
            implements MuleRecord {
        public Enricher(String source, String target, Optional<MuleRecord> innerBlock) {
            this(Kind.MESSAGE_ENRICHER, source, target, innerBlock);
        }
    }

    public record Poll(Kind kind, String frequency, String startDelay, String timeUnit) implements MuleRecord {
        public Poll(String frequency, String startDelay, String timeUnit) {
            this(Kind.POLL, frequency, startDelay, timeUnit);
        }
    }

    // Flow Control
    public record ScatterGather(Kind kind, List<ProcessorChain> processorChains) implements MuleRecord {
        public ScatterGather(List<ProcessorChain> processorChains) {
            this(Kind.SCATTER_GATHER, processorChains);
        }
    }

    public record FirstSuccessful(Kind kind, List<ProcessorChain> processorChains) implements MuleRecord {
        public FirstSuccessful(List<ProcessorChain> processorChains) {
            this(Kind.FIRST_SUCCESSFUL, processorChains);
        }
    }

    // Transformers
    public record SetVariable(Kind kind, String variableName, String value) implements MuleRecord {
        public SetVariable(String variableName, String value) {
            this(Kind.SET_VARIABLE, variableName, value);
        }
    }

    public record RemoveVariable(Kind kind, String variableName) implements MuleRecord {
    }

    public record SetSessionVariable(Kind kind, String variableName, String value) implements MuleRecord {
        public SetSessionVariable(String variableName, String value) {
            this(Kind.SET_SESSION_VARIABLE, variableName, value);
        }
    }

    public record Payload(Kind kind, String expr) implements MuleRecord {
        public Payload(String expr) {
            this(Kind.PAYLOAD, expr);
        }
    }

    public record ObjectToJson(Kind kind) implements MuleRecord {
        public ObjectToJson() {
            this(Kind.OBJECT_TO_JSON);
        }
    }

    public record ObjectToString(Kind kind) implements MuleRecord {
        public ObjectToString() {
            this(Kind.OBJECT_TO_STRING);
        }
    }

    // Error handling
    public record CatchExceptionStrategy(Kind kind, List<MuleRecord> catchBlocks, String when, String name)
            implements MuleRecord {
        public CatchExceptionStrategy(List<MuleRecord> catchBlocks, String when, String name) {
            this(Kind.CATCH_EXCEPTION_STRATEGY, catchBlocks, when, name);
        }
    }

    public record ChoiceExceptionStrategy(Kind kind, List<CatchExceptionStrategy> catchExceptionStrategyList,
                                          String name) implements MuleRecord {
        public ChoiceExceptionStrategy(List<CatchExceptionStrategy> catchExceptionStrategyList, String name) {
            this(Kind.CHOICE_EXCEPTION_STRATEGY, catchExceptionStrategyList, name);
        }
    }

    // Global Elements
    public record HTTPListenerConfig(Kind kind, String name, String basePath, String port,
                                     String host) implements MuleRecord {
        public HTTPListenerConfig(String name, String basePath, String port, String host) {
            this(Kind.HTTP_LISTENER_CONFIG, name, basePath, port, host);
        }
    }

    public record HTTPRequestConfig(Kind kind, String name, String host, String port,
                                    String protocol) implements MuleRecord {
        public HTTPRequestConfig(String name, String host, String port, String protocol) {
            this(Kind.HTTP_REQUEST_CONFIG, name, host, port, protocol);
        }
    }

    // TODO: typo - MySQL
    public record DbMSQLConfig(Kind kind, String name, String host, String port, String user, String password,
                               String database) implements MuleRecord {
        public DbMSQLConfig(String name, String host, String port, String user, String password, String database) {
            this(Kind.DB_MYSQL_CONFIG, name, host, port, user, password, database);
        }
    }

    public record DbOracleConfig(Kind kind, String name, String host, String port, String user, String password,
                               String instance) implements MuleRecord {
        public DbOracleConfig(String name, String host, String port, String user, String password, String instance) {
            this(Kind.DB_ORACLE_CONFIG, name, host, port, user, password, instance);
        }
    }

    public record DbGenericConfig(Kind kind, String name, String url, String user, String password)
            implements MuleRecord {
        public DbGenericConfig(String name, String url, String user, String password) {
            this(Kind.DB_GENERIC_CONFIG, name, url, user, password);
        }
    }

    public record DbTemplateQuery(Kind kind, String name, String parameterizedQuery,
                                  List<DbInParam> dbInParams) implements MuleRecord {
        public DbTemplateQuery(String name, String parameterizedQuery, List<DbInParam> dbInParams) {
            this(Kind.DB_TEMPLATE_QUERY, name, parameterizedQuery, dbInParams);
        }
    }

    public record DbInParam(Kind kind, String name, Type type, String defaultValue) implements MuleRecord {
        public DbInParam(String name, Type type, String defaultValue) {
            this(Kind.DB_IN_PARAM, name, type, defaultValue);
        }
    }

    // Database Connector
    public record Database(Kind kind, String configRef, QueryType queryType, String query) implements MuleRecord {
    }

    // Misc
    public record UnsupportedBlock(Kind kind, String xmlBlock) implements MuleRecord {
        public UnsupportedBlock(String xmlBlock) {
            this(Kind.UNSUPPORTED_BLOCK, xmlBlock);
        }
    }

    public interface MuleRecord {
        Kind kind();
    }

    public enum Kind {
        HTTP_LISTENER,
        VM_INBOUND_ENDPOINT,
        VM_OUTBOUND_ENDPOINT,
        QUARTZ_INBOUND_ENDPOINT,
        LOGGER,
        EXPRESSION_COMPONENT,
        PAYLOAD,
        FLOW_REFERENCE,
        HTTP_REQUEST,
        CHOICE,
        WHEN_IN_CHOICE,
        HTTP_LISTENER_CONFIG,
        HTTP_REQUEST_CONFIG,
        DB_MYSQL_CONFIG,
        DB_ORACLE_CONFIG,
        DB_GENERIC_CONFIG,
        DB_TEMPLATE_QUERY,
        DB_INSERT,
        DB_SELECT,
        DB_UPDATE,
        DB_DELETE,
        DB_IN_PARAM,
        SET_VARIABLE,
        SET_SESSION_VARIABLE,
        REMOVE_VARIABLE,
        REMOVE_SESSION_VARIABLE,
        OBJECT_TO_JSON,
        OBJECT_TO_STRING,
        TRANSFORM_MESSAGE,
        DW_SET_PAYLOAD,
        DW_SET_VARIABLE,
        DW_SET_SESSION_VARIABLE,
        DW_INPUT_PAYLOAD,
        FLOW,
        SUB_FLOW,
        ASYNC,
        FOREACH,
        MESSAGE_ENRICHER,
        POLL,
        CATCH_EXCEPTION_STRATEGY,
        CHOICE_EXCEPTION_STRATEGY,
        REFERENCE_EXCEPTION_STRATEGY,
        UNSUPPORTED_BLOCK,
        SCATTER_GATHER,
        FIRST_SUCCESSFUL,
        PROCESSOR_CHAIN
    }

    public enum Type {
        BOOLEAN,
        VARCHAR;

        public static Type from(String type) {
            try {
                return Type.valueOf(type);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Illegal type: " + type, e);
            }
        }
    }

    public enum QueryType {
        PARAMETERIZED_QUERY,
        DYNAMIC_QUERY,
        TEMPLATE_QUERY_REF
    }
}
