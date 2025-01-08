package mule;

import java.util.List;
import java.util.Map;

public record MuleModel() {

    public record HttpListener(Kind kind, String configRef, String resourcePath, String[] allowedMethods) implements MuleRecord {
        public HttpListener(String configRef, String resourcePath, String[] allowedMethods) {
            this(Kind.HTTP_LISTENER, configRef, resourcePath, allowedMethods);
        }
    }

    public record Logger(Kind kind, String message, String level) implements MuleRecord {
        public Logger(String message, String level) {
            this(Kind.LOGGER, message, level);
        }
    }

    public record Payload(Kind kind, String expr) implements MuleRecord {
        public Payload(String expr) {
            this(Kind.PAYLOAD, expr);
        }
    }

    public record HttpRequest(Kind kind, String method, String url, String path, Map<String, String> queryParams) implements MuleRecord {
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

    public record Flow(Kind kind, String name, List<MuleRecord> flowElements) implements MuleRecord {
        public Flow(String name, List<MuleRecord> flowElements) {
            this(Kind.FLOW, name, flowElements);
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
        HTTP_REQUEST,
        CHOICE,
        WHEN_IN_CHOICE,
        LISTENER_CONFIG,
        SET_VARIABLE,
        FLOW
    }
}
