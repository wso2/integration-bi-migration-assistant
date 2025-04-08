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
        public Import(String orgName, String moduleName) {
            this(orgName, moduleName, Optional.empty());
        }
    }

    public record ModuleTypeDef(Type type, String name) {
    }

    public interface Type {
    }

    public record BallerinaType(String type) implements Type {
        @Override
        public String toString() {
            return type;
        }
    }

    public interface RecordType extends Type {
        List<RecordField> recordFields();
    }

    public record ClosedRecordType(List<RecordField> recordFields, Optional<Type> restType) implements RecordType {
        public ClosedRecordType(List<RecordField> recordFields) {
            this(recordFields, Optional.empty());
        }

        @Override
        public String toString() {
            StringBuilder fieldsSb = new StringBuilder();
            for (RecordField recordField : recordFields()) {
                fieldsSb.append(String.format(recordField.isOptional() ? "%s %s?;" : "%s %s;", recordField.type(),
                        recordField.name()));
            }

            return String.format("record {| %s %s |}", fieldsSb, restType().map(Object::toString)
                    .orElse(""));
        }
    }

    public record OpenRecordType(List<RecordField> recordFields) implements RecordType {
        @Override
        public String toString() {
            StringBuilder fieldsSb = new StringBuilder();
            for (RecordField recordField : recordFields()) {
                fieldsSb.append(String.format(recordField.isOptional() ? "%s %s?;" : "%s %s;",
                        recordField.type(), recordField.name()));
            }

            return String.format("record { %s }", fieldsSb);
        }
    }

    public record RecordField(Type type, String name, boolean isOptional) {

    }

    public record ModuleVar(String name, Type type, BallerinaExpression expr) {
    }

    public record Service(String basePath, List<String> listenerRefs, Optional<Function> initFunc,
                          List<Resource> resources, List<Function> functions, List<String> pathParams,
                          List<String> queryParams, List<ObjectField> fields) {
    }

    public record ObjectField(Type type, String name) {

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

    public record Function(Optional<String> visibilityQualifier, String funcName, List<Parameter> parameters,
                           Optional<String> returnType, FunctionBody body) {
        public Function(String funcName, List<Parameter> parameters, List<Statement> body) {
            this(Optional.empty(), funcName, parameters, Optional.empty(),
                    new BlockFunctionBody(body));
        }

        public static Function publicFunction(String funcName, List<Parameter> parameters, List<Statement> body) {
            return new Function(Optional.of("public"), funcName, parameters, Optional.empty(),
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

    public record Parameter(String name, Type type, Optional<BallerinaExpression> defaultExpr) {
        public Parameter(String name, Type type) {
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

    public record TypeBindingPattern(Type type, String variableName) {
    }

    public sealed interface Statement permits BallerinaStatement, IfElseStatement, DoStatement {
    }
}
