package mule;

import java.util.List;
import java.util.Map;

public record MuleModel() {

    public record HttpListener(int tag, String configRef, String resourcePath, String[] allowedMethods) {
    }

    public record Logger(int tag, String message, String level) {
    }

    public record Payload(int tag, String expr) {
    }

    public record HttpRequest(int tag, String method, String url, String path, Map<String, String> queryParams) {
    }

    public record Choice(String kind, List<WhenInChoice> whens, List<Record> otherwiseProcess) {
        public Choice(List<WhenInChoice> whens, List<Record> otherwiseProcess) {
            this("Choice", whens, otherwiseProcess);
        }
    }

    public record WhenInChoice(String condition, List<Record> process) {
    }

    // TODO: rename type to something meaningful like tag
    public record ListenerConfig(String type, String name, String basePath, String port, Map<String, String> config) {
    }

    // Transformers
    public record SetVariable(String variableName, String value) {
    }
}
