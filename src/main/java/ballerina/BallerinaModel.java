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

    public record ModuleTypeDef(Type type, String name) {
    }

    public interface Type {
    }

    public record BallerinaType(String type) implements Type {
    }

    public interface RecordType extends Type {
        List<RecordField> recordFields();
    }

    public record ClosedRecordType(List<RecordField> recordFields, Optional<String> restType) implements RecordType {
        public ClosedRecordType(List<RecordField> recordFields) {
            this(recordFields, Optional.empty());
        }
    }

    public record OpenRecordType(List<RecordField> recordFields) implements RecordType {
    }

    public record RecordField(String type, String name, boolean isOptional) {

    }

    public record ModuleVar(String name, String type, BallerinaExpression expr) {
    }

    public record Service(String basePath, List<String> listenerRefs, Optional<Function> initFunc,
                          List<Resource> resources, List<Function> functions, List<String> pathParams,
                          List<String> queryParams, List<ObjectField> fields) {
    }

    public record ObjectField(String type, String name) {

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
                           Optional<String> returnType, FunctionBody body) {
        public Function(String methodName, List<Parameter> parameters, List<Statement> body) {
            this(Optional.empty(), methodName, parameters, Optional.empty(),
                    new BlockFunctionBody(body));
        }
    }

    public interface FunctionBody {
    }

    public record BlockFunctionBody(List<Statement> statements) implements FunctionBody {
    }

    public record ExternFunctionBody(String className, Optional<String> javaMethodName, String annotation,
                                     Optional<List<String>> paramTypes) implements FunctionBody {
    }

    public record Parameter(String name, String type, Optional<BallerinaExpression> defaultExpr) {
        public Parameter(String name, String type) {
            this(name, type, Optional.empty());
        }
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
        public DoStatement(List<Statement> doBody) {
            this(doBody, Optional.empty());
        }

        public DoStatement(List<Statement> doBody, OnFailClause onFailClause) {
            this(doBody, Optional.of(onFailClause));
        }
    }

    public record OnFailClause(List<Statement> onFailBody, Optional<TypeBindingPattern> typeBindingPattern) {
        public OnFailClause(List<Statement> onFailBody) {
            this(onFailBody, Optional.empty());
        }

        public OnFailClause(List<Statement> onFailBody, TypeBindingPattern typeBindingPattern) {
            this(onFailBody, Optional.of(typeBindingPattern));
        }
    }

    public record TypeBindingPattern(String type, String variableName) { // TODO: change all string type to Type type
    }

    public sealed interface Statement permits BallerinaStatement, IfElseStatement, DoStatement {
    }
}
