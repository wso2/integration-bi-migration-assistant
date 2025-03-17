package ballerina;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public record BallerinaModel(DefaultPackage defaultPackage, List<Module> modules) {

    public record DefaultPackage(String org, String name, String version) {

    }

    public record Module(String name, List<TextDocument> textDocuments) {

    }

    public record TextDocument(String documentName, List<Import> imports, List<ModuleTypeDef> moduleTypeDefs,
                               List<ModuleVar> moduleVars, List<Listener> listeners, List<Service> services,
                               List<Function> functions, List<String> Comments, List<String> intrinsics) {

        public TextDocument(String documentName, List<Import> imports, List<ModuleTypeDef> moduleTypeDefs,
                            List<ModuleVar> moduleVars, List<Listener> listeners, List<Service> services,
                            List<Function> functions, List<String> comments) {
            this(documentName, imports, moduleTypeDefs, moduleVars, listeners, services, functions, comments,
                    List.of());
        }
    }

    public record Import(String orgName, String moduleName, Optional<String> importPrefix) {

        public Import(String orgName, String moduleName) {
            this(orgName, moduleName, Optional.empty());
        }

        @Override
        public String toString() {
            String importPrefix = importPrefix().map(ip -> String.format("as %s", ip)).orElse("");
            return String.format("import %s/%s %s;", orgName(), moduleName(), importPrefix);
        }
    }

    public record ModuleTypeDef(String name, TypeDesc typeDesc) {

    }

    public sealed interface TypeDesc {

        record MapTypeDesc(TypeDesc typeDesc) implements TypeDesc {

            @Override
            public String toString() {
                return "map<" + typeDesc + ">";
            }
        }

        record BallerinaType(String value) implements TypeDesc {

            @Override
            public String toString() {
                return value;
            }
        }

        record RecordTypeDesc(List<TypeDesc> inclusions, List<RecordField> fields, TypeDesc rest)
                implements TypeDesc {

            public RecordTypeDesc(List<RecordField> fields) {
                this(List.of(), fields, BuiltinType.NEVER);
            }

            private static final String INDENT = "  ";

            public static RecordTypeDesc closedRecord(Collection<RecordField> fields) {
                return new RecordTypeDesc(List.of(), List.copyOf(fields), BuiltinType.NEVER);
            }

            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder();
                boolean isExclusive = rest != BuiltinType.ANYDATA;
                sb.append("record ").append(isExclusive ? "{|" : "{").append("\n");
                for (TypeDesc inclusion : inclusions) {
                    sb.append(INDENT).append("*").append(inclusion).append(";").append("\n");
                }
                for (RecordField field : fields) {
                    sb.append(INDENT).append(field).append("\n");
                }
                if (isExclusive && rest != BuiltinType.NEVER) {
                    sb.append(INDENT).append("...").append(rest).append("\n");
                }
                sb.append(isExclusive ? "|}" : "}");
                return sb.toString();
            }

            public record RecordField(String name, TypeDesc typeDesc, boolean isOptional,
                                      Optional<Expression> defaultValue) {

                public RecordField(String name, TypeDesc typeDesc, Optional<Expression> defaultValue) {
                    this(name, typeDesc, false, defaultValue);
                }

                public RecordField(String name, TypeDesc typeDesc) {
                    this(name, typeDesc, false, Optional.empty());
                }

                public RecordField(String name, TypeDesc typeDesc, boolean isOptional) {
                    this(name, typeDesc, isOptional, Optional.empty());
                }

                @Override
                public String toString() {
                    StringBuilder sb = new StringBuilder();
                    sb.append(typeDesc).append(" ").append(name);
                    defaultValue.ifPresent(expression -> sb.append(" = ").append(expression));
                    if (isOptional) {
                        sb.append("?");
                    }
                    sb.append(";");
                    return sb.toString();
                }
            }
        }

        record TypeReference(String name) implements TypeDesc {

            @Override
            public String toString() {
                return name;
            }
        }

        record UnionTypeDesc(Collection<? extends TypeDesc> members) implements TypeDesc {

            public static UnionTypeDesc of(TypeDesc... members) {
                return new UnionTypeDesc(List.of(members));
            }

            @Override
            public String toString() {
                return String.join(" | ", members.stream().map(Object::toString).toList());
            }
        }

        enum BuiltinType implements TypeDesc {
            ANYDATA("anydata"), NEVER("never"), JSON("json"), NIL("()"), STRING("string"), INT("int"), XML("xml"),
            ;

            private final String name;

            BuiltinType(String name) {
                this.name = name;
            }

            @Override
            public String toString() {
                return name;
            }
        }

    }

    // TODO: expr must be optional
    public record ModuleVar(String name, String type, BallerinaExpression expr, boolean isConstant,
                            boolean isConfigurable) {

        public ModuleVar {
            assert !isConfigurable || isConstant;
        }

        public ModuleVar(String name, String type, BallerinaExpression expr) {
            this(name, type, expr, false, false);
        }

        public static ModuleVar constant(String name, TypeDesc typeDesc, BallerinaExpression expr) {
            return new ModuleVar(name, typeDesc.toString(), expr, true, false);
        }

        public static ModuleVar configurable(String name, TypeDesc typeDesc, BallerinaExpression expr) {
            return new ModuleVar(name, typeDesc.toString(), expr, true, true);
        }

        public ModuleVar(String name, TypeDesc type, BallerinaExpression expr) {
            this(name, type.toString(), expr);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (isConfigurable) {
                sb.append("configurable ");
            } else if (isConstant) {
                sb.append("const ");
            }
            sb.append(type).append(" ").append(name);
            if (!expr.expr().isEmpty()) {
                sb.append(" ").append("=").append(" ").append(expr.expr);
            }
            sb.append(";\n");
            return sb.toString();
        }
    }

    public record Service(String basePath, List<String> listenerRefs, Optional<Function> initFunc,
                          List<Resource> resources, List<Function> functions, List<String> pathParams,
                          List<String> queryParams, List<ObjectField> fields) {

    }

    public record ObjectField(TypeDesc type, String name) {

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

    public record Function(Optional<String> visibilityQualifier, String functionName, List<Parameter> parameters,
                           Optional<String> returnType, FunctionBody body) {

        public Function(String funcName, List<Parameter> parameters, Optional<String> returnType,
                        List<Statement> body) {
            this(Optional.empty(), funcName, parameters, returnType, new BlockFunctionBody(body));
        }

        public Function(String funcName, List<Parameter> parameters, String returnType, List<Statement> body) {
            this(Optional.empty(), funcName, parameters, Optional.of(returnType), new BlockFunctionBody(body));
        }

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

    public record Parameter(String name, TypeDesc type, Optional<BallerinaExpression> defaultExpr) {

        public Parameter(String name, TypeDesc type) {
            this(name, type, Optional.empty());
        }
    }

    public record BallerinaStatement(String stmt) implements Statement {

        @Override
        public String toString() {
            return stmt;
        }
    }

    public record BallerinaExpression(String expr) implements Expression {

        @Override
        public String toString() {
            return expr;
        }
    }

    public record IfElseStatement(BallerinaExpression ifCondition, List<Statement> ifBody,
                                  List<ElseIfClause> elseIfClauses, List<Statement> elseBody) implements Statement {

        @Override
        public String toString() {
            String ifBlock = String.format("if %s { %s }", ifCondition, String.join("",
                    ifBody.stream().map(Object::toString).toList()));
            String elseIfs = String.join("", elseIfClauses.stream().map(Object::toString).toList());
            String elseBlock = elseBody.isEmpty() ? ""
                    : String.format("else { %s }",
                    String.join("", elseBody.stream().map(Object::toString).toList()));
            return ifBlock + elseIfs + elseBlock;
        }
    }

    public sealed interface Expression {

        record FieldAccess(Expression expression, String fieldName) implements Expression {

            @Override
            public String toString() {
                return expression + "." + fieldName;
            }
        }

        record MemberAccess(Expression container, String field) implements Expression {

            @Override
            public String toString() {
                return container + "[\"" + field + "\"]";
            }
        }

        record XMLTemplate(String body) implements Expression {

            @Override
            public String toString() {
                return "xml`" + body() + "`";
            }
        }

        record MappingConstructor(List<MappingField> fields) implements Expression {

            @Override
            public String toString() {
                return "{" + String.join(", ", fields.stream().map(Objects::toString).toList()) + "}";
            }

            public record MappingField(String key, Expression value) {

                @Override
                public String toString() {
                    return key + ": " + value;
                }
            }
        }

        record StringConstant(String value) implements Expression {

            @Override
            public String toString() {
                return "\"" + value + "\"";
            }
        }

        record NewExpression(Optional<String> classDescriptor, List<Expression> args) implements Expression {

            public NewExpression(List<Expression> args) {
                this(Optional.empty(), args);
            }

            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder();
                sb.append("new ");
                classDescriptor.ifPresent(s -> sb.append(s).append(" "));
                sb.append("(");
                sb.append(String.join(", ", args.stream().map(Objects::toString).toList()));
                sb.append(")");
                return sb.toString();
            }
        }

        record FunctionCall(String functionName, String[] args) implements Expression {

            public FunctionCall(String functionName, List<Expression> args) {
                this(functionName, args.stream().map(Objects::toString).toArray(String[]::new));
            }

            // TODO: replace this with the list
            public FunctionCall(String functionName, Expression[] args) {
                this(functionName, Arrays.stream(args).map(Objects::toString).toArray(String[]::new));
            }

            @Override
            public String toString() {
                return functionName + "(" + String.join(", ", args) + ")";
            }

        }

        record VariableReference(String varName) implements Expression {

            @Override
            public String toString() {
                return varName;
            }
        }

        record CheckPanic(Expression callExpr) implements Expression {

            @Override
            public String toString() {
                return "checkpanic " + callExpr;
            }
        }

        record TypeCheckExpression(VariableReference variableReference, TypeDesc td) implements Expression {

            @Override
            public String toString() {
                return variableReference.varName + " is " + td;
            }
        }

        record TernaryExpression(Expression condition, Expression ifTrue, Expression ifFalse) implements Expression {

            @Override
            public String toString() {
                return condition + " ? " + ifTrue + " : " + ifFalse;
            }
        }
    }

    public sealed interface Statement {

        record CallStatement(Expression.FunctionCall callExpr) implements Statement {

            @Override
            public String toString() {
                return callExpr + ";";
            }
        }

        record Return<E extends Expression>(Optional<E> value) implements Statement {

            public Return(E value) {
                this(Optional.of(value));
            }

            @Override
            public String toString() {
                return value.map(expression -> "return " + expression + ";").orElse("return;");
            }
        }

        public record Comment(String comment) implements Statement {

            @Override
            public String toString() {
                return "//" + comment + "\n";
            }
        }

        public record ElseIfClause(BallerinaExpression condition, List<Statement> elseIfBody) {

            @Override
            public String toString() {
                return String.format("else if %s { %s }", condition, String.join("", elseIfBody.stream()
                        .map(Object::toString).toList()));
            }
        }

        public record DoStatement(List<Statement> doBody, Optional<OnFailClause> onFailClause) implements Statement {

            public DoStatement(List<Statement> doBody) {
                this(doBody, Optional.empty());
            }

            public DoStatement(List<Statement> doBody, OnFailClause onFailClause) {
                this(doBody, Optional.of(onFailClause));
            }

            @Override
            public String toString() {
                String doBlock = String.format("do { %s }", String.join("", doBody.stream()
                        .map(Object::toString).toList()));
                String onFail = onFailClause.map(ofc -> String.format("on fail %s { %s }",
                        String.join("", ofc.typeBindingPattern().map(Object::toString).orElse("")),
                        String.join("", ofc.onFailBody.stream().map(Object::toString).toList()))).orElse("");
                return doBlock + onFail;
            }
        }

        // Note: should be placed at the beginning of a function-body-block
        record NamedWorkerDecl(String name, Optional<TypeDesc> returnType,
                               List<Statement> statements) implements Statement {

            public NamedWorkerDecl(String name, List<Statement> statements) {
                this(name, Optional.empty(), statements);
            }

            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder();
                sb.append("worker ").append(name);
                returnType.ifPresent(type -> sb.append(" returns ").append(type));
                sb.append(" { ");
                sb.append(String.join("", statements.stream().map(Object::toString).toList()));
                sb.append(" }");
                return sb.toString();
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

        public record TypeBindingPattern(TypeDesc type, String variableName) {

            @Override
            public String toString() {
                return type + " " + variableName;
            }
        }

        record VarDeclStatment(TypeDesc type, String varName, Optional<Expression> expr) implements Statement {

            public VarDeclStatment(TypeDesc type, String varName) {
                this(type, varName, Optional.empty());
            }

            public VarDeclStatment(TypeDesc type, String varName, Expression expr) {
                this(type, varName, Optional.of(expr));
            }

            @Override
            public String toString() {
                return expr.map(expression -> type + " " + varName + " = " + expression + ";")
                        .orElseGet(() -> type + " " + varName + ";");
            }

            public Expression.VariableReference ref() {
                return new Expression.VariableReference(varName());
            }
        }

        record VarAssignStatement(Expression ref, Expression value) implements Statement {

            @Override
            public String toString() {
                return ref + " = " + value + ";";
            }
        }
    }

    // This is wrong but should be good enough for our uses
    public sealed interface Action extends Expression {

        record WorkerSendAction(Expression expression, String peer) implements Action {

            @Override
            public String toString() {
                return expression.toString() + " -> " + peer;
            }
        }

        record WorkerReceiveAction(String peer) implements Action {

            @Override
            public String toString() {
                return "<- " + peer;
            }
        }

        record RemoteMethodCallAction(Expression expression, String methodName, List<Expression> args)
                implements Action {

            @Override
            public String toString() {
                return expression + "->" + methodName + "(" +
                        String.join(", ", args.stream().map(Objects::toString).toList()) + ")";
            }
        }
    }
}
