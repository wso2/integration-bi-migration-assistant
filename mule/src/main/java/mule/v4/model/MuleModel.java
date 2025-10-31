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

import java.util.Collections;
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

    public record VMQueue(String queueName, String queueType) {
    }

    public record VMListener(Kind kind, String configRef, String queueName)
            implements MuleRecord {
        public VMListener(String configRef, String queueName) {
            this(Kind.VM_LISTENER, configRef, queueName);
        }
    }

    public record VMPublish(Kind kind, String configRef, String queueName)
            implements MuleRecord {
        public VMPublish(String configRef, String queueName) {
            this(Kind.VM_PUBLISH, configRef, queueName);
        }
    }

    public record VMConsume(Kind kind, String configRef, String queueName)
            implements MuleRecord {
        public VMConsume(String configRef, String queueName) {
            this(Kind.VM_CONSUME, configRef, queueName);
        }
    }

    public record Logger(Kind kind, String message, LogLevel level) implements MuleRecord {
        public Logger(String message, LogLevel level) {
            this(Kind.LOGGER, message, level);
        }
    }

    public record Scheduler(Kind kind, String frequency, String timeUnit, String startDelay) implements MuleRecord {
        public Scheduler(String frequency, String timeUnit, String startDelay) {
            this(Kind.SCHEDULER, frequency, timeUnit, startDelay);
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

    public record HttpRequest(Kind kind, String configRef, String method, String url, String path,
                              Map<String, String> queryParams) implements MuleRecord {
        public HttpRequest(String configRef, String method, String url, String path, Map<String, String> queryParams) {
            this(Kind.HTTP_REQUEST, configRef, method, url, path, queryParams);
        }
    }

    // Flow Control
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

    public record ScatterGather(Kind kind, List<Route> routes) implements MuleRecord {
        public ScatterGather(List<Route> routes) {
            this(Kind.SCATTER_GATHER, routes);
        }
    }

    public record FirstSuccessful(Kind kind, List<Route> routes) implements MuleRecord {
        public FirstSuccessful(List<Route> routes) {
            this(Kind.FIRST_SUCCESSFUL, routes);
        }
    }

    public record Route(Kind kind, List<MuleRecord> flowBlocks) implements MuleRecord {
        public Route(List<MuleRecord> flowBlocks) {
            this(Kind.ROUTE, flowBlocks);
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

    public record Try(Kind kind, List<MuleRecord> flowBlocks) implements MuleRecord {
        public Try(List<MuleRecord> flowBlocks) {
            this(Kind.TRY, flowBlocks);
        }
    }

    public record Foreach(Kind kind, String collection, List<MuleRecord> flowBlocks) implements MuleRecord {
        public Foreach(String collection, List<MuleRecord> flowBlocks) {
            this(Kind.FOREACH, collection, flowBlocks);
        }
    }

    public record Enricher(Kind kind, String source, String target, Optional<MuleRecord> innerBlock)
            implements MuleRecord {
        public Enricher(String source, String target, Optional<MuleRecord> innerBlock) {
            this(Kind.MESSAGE_ENRICHER, source, target, innerBlock);
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

    public record Payload(Kind kind, String expr, String mimeType) implements MuleRecord {
        public Payload(String expr, String mimeType) {
            this(Kind.PAYLOAD, expr, mimeType);
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
    public record ErrorHandler(Kind kind, String name, String ref, List<ErrorHandlerRecord> errorHandlers)
            implements MuleRecord {
        public ErrorHandler(String name, String ref, List<ErrorHandlerRecord> errorHandlers) {
            this(Kind.ERROR_HANDLER, name, ref, errorHandlers);
        }
    }

    public interface ErrorHandlerRecord extends MuleRecord {
        String type();

        String when();

        List<MuleRecord> errorBlocks();
    }

    public record OnErrorContinue(Kind kind, List<MuleRecord> errorBlocks, String type, String when,
                                  String enableNotifications, String logException) implements ErrorHandlerRecord {
        public OnErrorContinue(List<MuleRecord> errorBlocks, String type, String when,
                               String enableNotifications, String logException) {
            this(Kind.ON_ERROR_CONTINUE, errorBlocks, type, when, enableNotifications, logException);
        }
    }

    public record OnErrorPropagate(Kind kind, List<MuleRecord> errorBlocks, String type, String when,
                                   String enableNotifications, String logException) implements ErrorHandlerRecord {
        public OnErrorPropagate(List<MuleRecord> errorBlocks, String type, String when,
                                String enableNotifications, String logException) {
            this(Kind.ON_ERROR_PROPAGATE, errorBlocks, type, when, enableNotifications, logException);
        }
    }

    public record RaiseError(Kind kind, String type, String description) implements MuleRecord {
        public RaiseError(String type, String description) {
            this(Kind.RAISE_ERROR, type, description);
        }
    }

    // Global Elements
    public record MuleImport(String file) {
    }

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

    public record GlobalProperty(Kind kind, String name, String value) implements MuleRecord {
        public GlobalProperty(String name, String value) {
            this(Kind.GLOBAL_PROPERTY, name, value);
        }
    }

    public record VMConfig(Kind kind, String name, List<VMQueue> queues) implements MuleRecord {
        public VMConfig(String name, List<VMQueue> queues) {
            this(Kind.VM_CONFIG, name, queues);
        }
    }

    public record DbConfig(Kind kind, String name, DbConnection dbConnection) implements MuleRecord {
        public DbConfig(String name, DbConnection dbConnection) {
            this(Kind.DB_CONFIG, name, dbConnection);
        }
    }

    public interface DbConnection extends MuleRecord {
    }

    public record DbMySqlConnection(Kind kind, String name, String host, String port, String user, String password,
                                    String database) implements DbConnection {
        public DbMySqlConnection(String name, String host, String port, String user, String password, String database) {
            this(Kind.DB_MYSQL_CONNECTION, name, host, port, user, password, database);
        }
    }

    public record DbOracleConnection(Kind kind, String name, String host, String port, String user, String password,
                                     String instance, String serviceName) implements DbConnection {
        public DbOracleConnection(String name, String host, String port, String user, String password, String instance,
                                  String serviceName) {
            this(Kind.DB_ORACLE_CONNECTION, name, host, port, user, password, instance, serviceName);
        }
    }

    public record DbGenericConnection(Kind kind, String name, String url, String user, String password)
            implements DbConnection {
        public DbGenericConnection(String name, String url, String user, String password) {
            this(Kind.DB_GENERIC_CONNECTION, name, url, user, password);
        }
    }

    // Database Connector
    public record Database(Kind kind, String configRef, String query, List<UnsupportedBlock> unsupportedBlocks)
            implements MuleRecord {
        public Database(Kind kind, String configRef, String query) {
            this(kind, configRef, query, Collections.emptyList());
        }
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
        VM_CONFIG,
        GLOBAL_PROPERTY,
        VM_LISTENER,
        VM_PUBLISH,
        VM_CONSUME,
        LOGGER,
        SCHEDULER,
        EXPRESSION_COMPONENT,
        PAYLOAD,
        FLOW_REFERENCE,
        HTTP_REQUEST,
        CHOICE,
        WHEN_IN_CHOICE,
        SCATTER_GATHER,
        FIRST_SUCCESSFUL,
        ROUTE,
        HTTP_LISTENER_CONFIG,
        HTTP_REQUEST_CONFIG,
        DB_CONFIG,
        DB_MYSQL_CONNECTION,
        DB_ORACLE_CONNECTION,
        DB_GENERIC_CONNECTION,
        DB_INSERT,
        DB_SELECT,
        DB_UPDATE,
        DB_DELETE,
        SET_VARIABLE,
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
        TRY,
        FOREACH,
        MESSAGE_ENRICHER,
        ERROR_HANDLER,
        ON_ERROR_CONTINUE,
        ON_ERROR_PROPAGATE,
        RAISE_ERROR,
        UNSUPPORTED_BLOCK
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
}
