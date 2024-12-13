import java.util.List;
import java.util.Map;
import java.util.Optional;


public record BallerinaModel(DefaultPackage defaultPackage, List<Module> modules) {

    public record DefaultPackage(String org, String name, String version) {

    }

    public record Module(String moduleName, List<Import> imports, List<Variable> variables,
                         List<Listener> listeners, List<Service> services) {

    }

    public record Import(String org, String module) {

    }

    public record Variable(String name, String type, Object value) {

    }

    public record Service(String basePath, List<String> listenerRefs, List<Resource> resources, List<String> pathParams,
                          List<String> queryParams) {

    }

    // TODO: type means http:Listener, tcp:Listener, etc.
    // TODO: move port to config map
    public record Listener(String type, String name, String port, Map<String, String> config) {

    }

    public record Resource(String resourceMethodName, String path, List<Parameter> parameters,
                           String returnType, List<Statement> body) {

    }

    public record Parameter(String name, String type, Optional<BallerinaExpression> defaultExpr) {

    }

//    public record ReturnType(String type, List<String> types) {
//
//    }

    public record BallerinaStatement(String stmt) implements Statement {

    }

    public record BallerinaExpression(String expr) {

    }

    public record IfElseStatement(BallerinaExpression condition, List<Statement> ifBody,
                                  List<Statement> elseBody) implements Statement {
    }

    public sealed interface Statement permits BallerinaStatement, IfElseStatement {
    }

    public record Value(String type, Map<String, Object> content) {

    }
}
