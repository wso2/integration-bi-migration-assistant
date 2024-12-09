import java.util.List;
import java.util.Map;


public record BallerinaModel(DefaultPackage defaultPackage, List<Module> modules) {

    public record DefaultPackage(String org, String name, String version) {

    }

    public record Module(String moduleName, List<Import> imports, List<Variable> variables, List<Service> services) {

    }

    public record Import(String org, String module) {

    }

    public record Variable(String name, String type, Object value) {

    }

    public record Service(String basePath, List<Listener> listeners, List<Resource> resources, List<String> pathParams,
                          List<String> queryParams) {

    }

    public record Listener(String type, String name, String port, Map<String, String> config) {

    }

    public record Resource(String resourceMethodName, String path, List<Parameter> parameters,
                           String returnType, List<BodyStatement> body) {

    }

    public record Parameter(String name, String type) {

    }

//    public record ReturnType(String type, List<String> types) {
//
//    }

    public record BodyStatement(String stmt) {

    }

    public record Value(String type, Map<String, Object> content) {

    }
}
