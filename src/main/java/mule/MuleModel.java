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

    public record ListenerConfig(Kind kind, String name, String basePath, String port,
                                 Map<String, String> config) implements MuleRecord {
        public ListenerConfig(String name, String basePath, String port, Map<String,
                String> config) {
            this(Kind.LISTENER_CONFIG, name, basePath, port, config);
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
        LISTENER_CONFIG,
        SET_VARIABLE,
        TRANSFORM_MESSAGE,
        FLOW
    }
}
