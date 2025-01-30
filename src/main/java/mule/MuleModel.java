package mule;

import java.util.List;
import java.util.Map;

public record MuleModel() {

    public record HttpListener(Kind kind, String configRef, String resourcePath, String[] allowedMethods)
            implements MuleRecord {
        public HttpListener(String configRef, String resourcePath, String[] allowedMethods) {
            this(Kind.HTTP_LISTENER, configRef, resourcePath, allowedMethods);
        }
    }

    public record Logger(Kind kind, String message, LogLevel level) implements MuleRecord {
        public Logger(String message, LogLevel level) {
            this(Kind.LOGGER, message, level);
        }
    }

    public record TransformMessage(Kind kind, String mimeType, String script) implements MuleRecord {
        public TransformMessage(String mimeType, String script) {
            this(Kind.TRANSFORM_MESSAGE, mimeType, script);
        }
    }

    public enum LogLevel {
        DEBUG,
        ERROR,
        INFO,
        TRACE,
        WARN
    }

    public record Payload(Kind kind, String expr) implements MuleRecord {
        public Payload(String expr) {
            this(Kind.PAYLOAD, expr);
        }
    }

    public record FlowReference(Kind kind, String flowName) implements MuleRecord {
        public FlowReference(String flowName) {
            this(Kind.FLOW_REFERENCE, flowName);
        }
    }

    public record HttpRequest(Kind kind, String method, String url, String path, Map<String, String> queryParams)
            implements MuleRecord {
        public HttpRequest(String method, String url, String path, Map<String, String> queryParams) {
            this(Kind.HTTP_REQUEST, method, url, path, queryParams);
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

    public record Flow(Kind kind, String name, MuleRecord source, List<MuleRecord> flowBlocks) implements MuleRecord {
        public Flow(String name, MuleRecord source, List<MuleRecord> flowBlocks) {
            this(Kind.FLOW, name, source, flowBlocks);
        }
    }

    public record SubFlow(Kind kind, String name, List<MuleRecord> flowBlocks) implements MuleRecord {
        public SubFlow(String name, List<MuleRecord> flowBlocks) {
            this(Kind.SUB_FLOW, name, flowBlocks);
        }
    }

    // Transformers
    public record SetVariable(Kind kind, String variableName, String value) implements MuleRecord {
        public SetVariable(String variableName, String value) {
            this(Kind.SET_VARIABLE, variableName, value);
        }
    }

    // Global Elements
    public record HTTPListenerConfig(Kind kind, String name, String basePath, String port,
                                     Map<String, String> config) implements MuleRecord {
        public HTTPListenerConfig(String name, String basePath, String port, Map<String,
                String> config) {
            this(Kind.HTTP_LISTENER_CONFIG, name, basePath, port, config);
        }
    }

    public record DbMSQLConfig(Kind kind, String name, String host, String port, String user, String password,
                               String database) implements MuleRecord {
        public DbMSQLConfig(String name, String host, String port, String user, String password, String database) {
            this(Kind.DB_MYSQL_CONFIG, name, host, port, user, password, database);
        }
    }

    // Database Connector
    public record DbSelect(Kind kind, String configRef, String query) implements MuleRecord {
        public DbSelect(String configRef, String query) {
            this(Kind.DB_SELECT, configRef, query);
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
        LOGGER,
        PAYLOAD,
        FLOW_REFERENCE,
        HTTP_REQUEST,
        CHOICE,
        WHEN_IN_CHOICE,
        HTTP_LISTENER_CONFIG,
        DB_MYSQL_CONFIG,
        DB_SELECT,
        SET_VARIABLE,
        TRANSFORM_MESSAGE,
        FLOW,
        SUB_FLOW,
        UNSUPPORTED_BLOCK
    }
}
