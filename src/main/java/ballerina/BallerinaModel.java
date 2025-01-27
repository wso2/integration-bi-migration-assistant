package ballerina;

import java.util.List;
import java.util.Map;
import java.util.Optional;


public record BallerinaModel(DefaultPackage defaultPackage, List<Module> modules) {

    public record DefaultPackage(String org, String name, String version) {
    }

    public record Module(String moduleName, List<Import> imports, List<Variable> variables, List<Listener> listeners,
                         List<Service> services, List<Function> functions) {
    }

    public record Import(String org, String module) {
    }

    public record Variable(String name, String type, Object value) {
    }

    public record Service(String basePath, List<String> listenerRefs, List<Resource> resources,
                          List<Function> functions, List<String> pathParams, List<String> queryParams) {
    }

    // TODO: move port to config map
    public record Listener(ListenerType type, String name, String port, Map<String, String> config) {
    }

    public enum ListenerType {
        HTTP
    }

    public record Resource(String resourceMethodName, String path, List<Parameter> parameters,
                           Optional<String> returnType, List<Statement> body) {
    }

    public record Function(Optional<String> visibilityQualifier, String methodName, List<Parameter> parameters,
                           Optional<String> returnType,
                           List<Statement> body) {
    }

    public record Parameter(String name, String type, Optional<BallerinaExpression> defaultExpr) {
    }

    public record BallerinaStatement(String stmt) implements Statement {
    }

    public record BallerinaExpression(String expr) {
    }

    public record IfElseStatement(BallerinaExpression ifCondition, List<Statement> ifBody,
                                  List<ElseIfClause> elseIfClauses, List<Statement> elseBody) implements Statement {
    }

    public record ElseIfClause(BallerinaExpression condition, List<Statement> elseIfBody) {
    }

    public sealed interface Statement permits BallerinaStatement, IfElseStatement {
    }
}
