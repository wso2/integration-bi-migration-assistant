package ballerina;

import java.util.List;
import java.util.Map;
import java.util.Optional;


public record BallerinaModel(DefaultPackage defaultPackage, List<Module> modules) {

    public record DefaultPackage(String org, String name, String version) {
    }

    public record Module(String name, List<TextDocument> textDocuments) {
    }

    public record TextDocument(String documentName, List<Import> imports, List<ModuleTypeDef> moduleTypeDefs,
                               List<ModuleVar> moduleVars, List<Listener> listeners, List<Service> services,
                               List<Function> functions, List<String> Comments) {
    }

    public record Import(String orgName, String moduleName, Optional<String> importPrefix) {
    }

    public record ModuleTypeDef(String name, String type) {
    }

    public record ModuleVar(String name, String type, BallerinaExpression expr) {
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

    public record DoStatement(List<Statement> doBody, Optional<OnFailClause> onFailClause) implements Statement {
    }

    public record OnFailClause(List<Statement> onFailBody) {
    }

    public sealed interface Statement permits BallerinaStatement, IfElseStatement, DoStatement {
    }
}
