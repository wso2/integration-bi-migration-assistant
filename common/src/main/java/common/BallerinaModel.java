/*
 *  Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com).
 *
 *  WSO2 LLC. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package common;

import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static common.BallerinaModel.Expression.BallerinaExpression;
import static common.BallerinaModel.Expression.StringConstant;
import static common.BallerinaModel.Expression.VariableReference;
import static common.BallerinaModel.Statement.Comment;

public record BallerinaModel(DefaultPackage defaultPackage, List<Module> modules) {

    // Note: not using this at the moment, but keeping it for future use
    public record DefaultPackage(String org, String name, String version) {

    }

    // Note: not using this at the moment, but keeping it for future use
    public record Module(String name, List<TextDocument> textDocuments) {

    }

    public record TextDocument(String documentName, List<Import> imports, List<ModuleTypeDef> moduleTypeDefs,
                               List<ModuleVar> moduleVars, List<Listener> listeners, List<Service> services,
                               List<ClassDef> classDefs, List<Function> functions, List<String> Comments,
                               List<String> intrinsics, List<ModuleMemberDeclarationNode> astNodes) {

        public TextDocument(String documentName, List<Import> imports, List<ModuleTypeDef> moduleTypeDefs,
                            List<ModuleVar> moduleVars, List<Listener> listeners, List<Service> services,
                            List<Function> functions, List<String> comments, List<String> intrinsics,
                            List<ModuleMemberDeclarationNode> astNodes) {
            this(documentName, imports, moduleTypeDefs, moduleVars, listeners, services, Collections.emptyList(),
                    functions, comments, intrinsics, astNodes);
        }

        public TextDocument(String documentName, List<Import> imports, List<ModuleTypeDef> moduleTypeDefs,
                            List<ModuleVar> moduleVars, List<Listener> listeners, List<Service> services,
                            List<ClassDef> classDefs,
                            List<Function> functions, List<String> comments) {
            this(documentName, imports, moduleTypeDefs, moduleVars, listeners, services, classDefs, functions, comments,
                    List.of(), List.of());
        }

        public String toSource() {
            SyntaxTree st = new CodeGenerator(this).generateSyntaxTree();
            return st.toSourceCode();
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

    public record ModuleTypeDef(String name, TypeDesc typeDesc, List<Comment> comments) {

        public ModuleTypeDef(String name, TypeDesc typeDesc) {
            this(name, typeDesc, List.of());
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (Comment comment : comments) {
                sb.append(comment);
            }
            if (typeDesc instanceof TypeDesc.RecordTypeDesc recordTypeDesc) {
                recordTypeDesc.namespace().ifPresent(ns -> sb.append(ns.annotation()));
                recordTypeDesc.xmlName().ifPresent(name -> sb.append("""
                        @xmldata:Name {
                            value: "%s"
                        }
                        """.formatted(name)));
            }
            sb.append("public type %s %s;".formatted(name, typeDesc));
            return sb.toString();
        }
    }

    public sealed interface TypeDesc {

        record ArrayTypeDesc(TypeDesc elementType, Optional<Long> size) implements TypeDesc {

            public ArrayTypeDesc(TypeDesc elementType) {
                this(elementType, Optional.empty());
            }

            @Override
            @NotNull
            public String toString() {
                return elementType + size.map(s -> "[" + s + "]").orElse("[]");
            }
        }

        record IntersectionTypeDesc(Collection<? extends TypeDesc> members) implements TypeDesc {

            public static IntersectionTypeDesc of(TypeDesc... members) {
                return new IntersectionTypeDesc(List.of(members));
            }

            @Override
            public @NotNull String toString() {
                return String.join(" & ", members.stream().map(Object::toString).toList());
            }
        }

        record FunctionTypeDesc(List<Parameter> parameters, TypeDesc returnType) implements TypeDesc {

            public FunctionTypeDesc(List<Parameter> parameters) {
                this(parameters, BuiltinType.NIL);
            }

            @Override
            public @NotNull String toString() {
                String params = String.join(", ", parameters.stream().map(Object::toString).toList());
                if (returnType == BuiltinType.NIL) {
                    return "function(" + params + ")";
                } else {
                    return "function(" + params + ") returns " + returnType;
                }
            }
        }

        record BallerinaType(String value) implements TypeDesc {

            @Override
            public String toString() {
                return value;
            }
        }

        record MapTypeDesc(TypeDesc typeDesc) implements TypeDesc {

            @Override
            public String toString() {
                return "map<" + typeDesc + ">";
            }
        }

        record StreamTypeDesc(TypeDesc valueTy, TypeDesc completionType) implements TypeDesc {

            @Override
            public String toString() {
                return "stream<" + valueTy + ", " + completionType + ">";
            }
        }

        record RecordTypeDesc(List<TypeDesc> inclusions, List<RecordField> fields, TypeDesc rest,
                              Optional<Namespace> namespace, Optional<String> xmlName)
                implements TypeDesc {

            public RecordTypeDesc(List<RecordField> fields) {
                this(List.of(), fields, BuiltinType.NEVER);
            }

            public RecordTypeDesc(List<TypeDesc> inclusions, List<RecordField> fields, TypeDesc rest) {
                this(inclusions, fields, rest, Optional.empty(), Optional.empty());
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
                                      Optional<Expression> defaultValue, Optional<Namespace> namespace) {

                public RecordField(String name, TypeDesc typeDesc, Expression defaultValue) {
                    this(name, typeDesc, false, Optional.of(defaultValue), Optional.empty());
                }

                public RecordField(String name, TypeDesc typeDesc) {
                    this(name, typeDesc, false, Optional.empty(), Optional.empty());
                }

                public RecordField(String name, TypeDesc typeDesc, boolean isOptional) {
                    this(name, typeDesc, isOptional, Optional.empty(), Optional.empty());
                }

                @Override
                public String toString() {
                    StringBuilder sb = new StringBuilder();
                    namespace.ifPresent(ns -> sb.append(ns.annotation()));
                    sb.append(typeDesc).append(" ").append(name);
                    defaultValue.ifPresent(expression -> sb.append(" = ").append(expression));
                    if (isOptional) {
                        sb.append("?");
                    }
                    sb.append(";");
                    return sb.toString();
                }
            }

            public record Namespace(Optional<String> prefix, String uri) {

                String annotation() {
                    StringBuilder sb = new StringBuilder();
                    sb.append("@xmldata:Namespace {");
                    prefix.ifPresent(p -> sb.append(" prefix: \"").append(p).append("\","));
                    sb.append(" uri: \"").append(uri).append("\" }");
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
            public @NotNull String toString() {
                if (members.size() == 2) {
                    List<TypeDesc> memberList = List.copyOf(members);
                    if (memberList.contains(BuiltinType.NIL)) {
                        return memberList.stream()
                                .filter(type -> type != BuiltinType.NIL)
                                .findFirst()
                                .map(type -> type + "?")
                                .orElse("()");
                    }
                }
                return String.join(" | ", members.stream().map(Object::toString).toList());
            }
        }

        enum BuiltinType implements TypeDesc {
            ANYDATA("anydata"),
            BOOLEAN("boolean"),
            DECIMAL("decimal"),
            FLOAT("float"),
            ERROR("error"),
            INT("int"),
            JSON("json"),
            NEVER("never"),
            NIL("()"),
            STRING("string"),
            HANDLE("handle"),
            READONLY("readonly"),
            XML("xml"),
            BYTE("byte");

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

    public record ModuleVar(String name, String type, Optional<Expression> expr, boolean isConstant,
                            boolean isConfigurable) {

        public ModuleVar {
        }

        public ModuleVar(String name, String type, Expression expr) {
            this(name, type, Optional.of(expr), false, false);
        }

        public ModuleVar(String name, TypeDesc type, Expression expr) {
            this(name, type.toString(), expr);
        }

        public static ModuleVar constant(String name, TypeDesc typeDesc, Expression expr) {
            return new ModuleVar(name, typeDesc.toString(), Optional.of(expr), true, false);
        }

        public static ModuleVar configurable(String name, TypeDesc typeDesc, Expression expr) {
            return new ModuleVar(name, typeDesc.toString(), Optional.of(expr), true, true);
        }

        public static ModuleVar configurable(String name, TypeDesc typeDesc) {
            return new ModuleVar(name, typeDesc.toString(), Optional.of(new BallerinaExpression("?")), true, true);
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
            expr.map(initExpr -> sb.append(" = ").append(initExpr));
            sb.append(";\n");
            return sb.toString();
        }
    }

    public record Remote(Function function) {

    }

    public record Service(String basePath, List<String> listenerRefs, Optional<Function> initFunc,
                          List<Resource> resources, List<Function> functions,
                          List<ObjectField> fields, List<Remote> remoteFunctions, Optional<Comment> comment) {
        public Service(String basePath, String listenerRef, List<Resource> resources) {
            this(basePath, Collections.singletonList(listenerRef), Optional.empty(), resources,
                    Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Optional.empty());
        }

        public Service(String basePath, String listenerRef, List<Resource> resources, Comment comment) {
            this(basePath, Collections.singletonList(listenerRef), Optional.empty(), resources,
                    Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
                    Optional.ofNullable(comment));
        }

        public Service(String basePath, List<String> listenerRefs, Optional<Function> initFunc,
                       List<Resource> resources, List<Function> functions, List<ObjectField> fields) {
            this(basePath, listenerRefs, initFunc, resources, functions, fields, Collections.emptyList(),
                    Optional.empty());
        }
    }

    public record ObjectField(TypeDesc type, String name) {
        @Override
        @NotNull
        public String toString() {
            return "public %s %s;".formatted(type, name);
        }
    }

    public sealed interface Listener {

        ListenerType type();

        String name();

        enum ListenerType {
            HTTP,
            JMS,
            FILE
        }

        record FileListener(String name, String path, boolean recursive) implements Listener {

            @Override
            @NotNull
            public String toString() {
                return """
                        public listener file:Listener %s = new (
                            path = "%s",
                            recursive = %s
                        );""".formatted(name, path, recursive);
            }

            @Override
            public ListenerType type() {
                return ListenerType.FILE;
            }
        }

        record HTTPListener(String name, Expression port, Optional<Expression> host) implements Listener {

            public HTTPListener(String name, String port, String host) {
                this(name, ConversionUtils.exprFrom(port),
                        host.equals("0.0.0.0") ? Optional.empty() : Optional.of(new StringConstant(host)));
            }

            @Override
            @NotNull
            public String toString() {
                String argList;
                argList = host.map(expression -> "(%s, {host: %s})".formatted(port, expression))
                        .orElseGet(() -> "(%s)".formatted(port));
                return "public listener http:Listener %s = new %s;".formatted(name, argList);
            }

            @Override
            public ListenerType type() {
                return ListenerType.HTTP;
            }
        }

        record JMSListener(String name, Expression initialContextFactory, Expression providerUrl,
                           String destinationName, Optional<String> username, Optional<String> password)
                implements Listener {

            @Override
            public ListenerType type() {
                return ListenerType.JMS;
            }

            @Override
            @NotNull
            public String toString() {
                StringBuilder connectionConfig = new StringBuilder();
                connectionConfig.append("initialContextFactory: ").append(initialContextFactory).append(",\n");
                connectionConfig.append("providerUrl: ").append(providerUrl);

                if (username.isPresent() && password.isPresent()) {
                    connectionConfig.append(",\nusername: \"").append(username.get()).append("\",\n");
                    connectionConfig.append("password: \"").append(password.get()).append("\"");
                }

                return String.format("""
                        public listener jms:Listener %s = new jms:Listener(
                            connectionConfig = {
                                %s
                            },
                            consumerOptions = {
                                destination: {
                                    'type: jms:QUEUE,
                                    name: "%s"
                                }
                            }
                        );""", name, connectionConfig, destinationName);
            }
        }
    }

    public record Resource(String resourceMethodName, String path, List<Parameter> parameters,
                           Optional<TypeDesc> returnType, List<Statement> body) {
    }

    public record Function(Optional<String> visibilityQualifier, String functionName, List<Parameter> parameters,
                           Optional<TypeDesc> returnType, FunctionBody body) {

        public Function(String funcName, List<Parameter> parameters, TypeDesc returnType,
                        List<Statement> body) {
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

        public static Function publicFunction(String funcName, List<Parameter> parameters, TypeDesc returnType,
                                              List<Statement> body) {
            return new Function(Optional.of("public"), funcName, parameters, Optional.of(returnType),
                    new BlockFunctionBody(body));
        }
    }

    public record ClassDef(String className, List<TypeDesc> typeInclusions, List<ObjectField> fields,
                        List<Function> methods) {
    }

    public interface FunctionBody {
    }

    public record BlockFunctionBody(List<Statement> statements) implements FunctionBody {
    }

    public record ExternFunctionBody(String className, Optional<String> javaMethodName, String annotation,
                                     Optional<List<String>> paramTypes) implements FunctionBody {
    }

    public record Parameter(String name, TypeDesc type, Optional<BallerinaExpression> defaultExpr) {

        public Parameter(TypeDesc typeDesc, String name, BallerinaExpression defaultExpr) {
            this(name, typeDesc, Optional.of(defaultExpr));
        }

        public Parameter(String name, TypeDesc type) {
            this(name, type, Optional.empty());
        }

        public VariableReference ref() {
            return new VariableReference(name);
        }

        @Override
        public @NotNull String toString() {
            return defaultExpr
                    .map(expr -> String.format("%s %s = %s", type, name, expr))
                    .orElseGet(() -> String.format("%s %s", type, name));
        }
    }

    public sealed interface Expression {

        record BallerinaExpression(String expr) implements Expression {

            @Override
            public String toString() {
                return expr;
            }
        }

        record Not(Expression expression) implements Expression {

            @Override
            public String toString() {
                return "!" + expression;
            }
        }

        record BinaryLogical(Expression left, Expression right, Operator operator) implements Expression {

            @Override
            public String toString() {
                return left + " " + operator + " " + right;
            }

            public enum Operator {
                AND("&&"), OR("||");

                private final String symbol;

                Operator(String symbol) {
                    this.symbol = symbol;
                }

                @Override
                public String toString() {
                    return symbol;
                }
            }
        }

        record TypeCast(TypeDesc typeDesc, Expression expression) implements Expression {

            @Override
            public String toString() {
                return "<" + typeDesc + ">" + expression;
            }
        }

        record FieldAccess(Expression expression, String fieldName) implements Expression {

            @Override
            public String toString() {
                return expression + "." + fieldName;
            }
        }

        record XMLTemplate(String body) implements Expression {

            @Override
            public String toString() {
                return "xml`" + body().trim() + "`";
            }
        }

        record StringTemplate(String body) implements Expression {

            @Override
            public String toString() {
                return "string`" + body() + "`";
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


        record BooleanConstant(boolean value) implements Expression, TypeDesc {

            @Override
            @NotNull
            public String toString() {
                return value ? "true" : "false";
            }
        }

        record StringConstant(String value) implements Expression, TypeDesc {

            @Override
            public String toString() {
                return "\"" + value + "\"";
            }
        }

        record NilConstant() implements Expression, TypeDesc {

            @Override
            public String toString() {
                return "()";
            }
        }

        record IntConstant(int value) implements Expression, TypeDesc {

            @Override
            @NotNull
            public String toString() {
                return Integer.toString(value);
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

        record MethodCall(Expression object, String methodName, List<Expression> args) implements Expression {

            @Override
            public String toString() {
                return object + "." + methodName + "(" +
                        String.join(", ", args.stream().map(Objects::toString).toList()) + ")";
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

        record Panic(Expression callExpr) implements Expression {

            @Override
            public String toString() {
                return "panic " + callExpr;
            }
        }

        record CheckPanic(Expression callExpr) implements Expression {

            @Override
            public String toString() {
                return "checkpanic " + callExpr;
            }
        }

        record Trap(Expression expr) implements Expression {

            @Override
            public String toString() {
                return "trap " + expr;
            }
        }

        record Check(Expression callExpr) implements Expression {
            public Check {
                assert !(callExpr instanceof Check) : "Redundant check";
            }

            @Override
            public String toString() {
                return "check " + callExpr;
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

        record BallerinaStatement(String stmt) implements Statement {

            @Override
            public String toString() {
                return stmt;
            }
        }

        record CallStatement(Expression callExpr) implements Statement {

            @Override
            public String toString() {
                return callExpr + ";";
            }
        }

        record Return<E extends Expression>(Optional<E> value) implements Statement {

            public Return() {
                this(Optional.empty());
            }

            public Return(E value) {
                this(Optional.of(value));
            }

            @Override
            public String toString() {
                return value.map(expression -> "return " + expression + ";").orElse("return;");
            }
        }

        record ElseIfClause(BallerinaExpression condition, List<Statement> elseIfBody) {

            @Override
            public String toString() {
                return String.format("else if %s { %s }", condition, String.join("", elseIfBody.stream()
                        .map(Object::toString).toList()));
            }
        }

        record DoStatement(List<Statement> doBody, Optional<OnFailClause> onFailClause) implements Statement {

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

        record NamedWorkerDecl(String name, Optional<TypeDesc> returnType,
                               List<Statement> statements) implements Statement {
            @Override
            public String toString() {
                return String.format("worker %s returns %s { %s }", name, returnType.map(Object::toString).orElse(""),
                        String.join("", statements.stream().map(Object::toString).toList()));
            }
        }

        record ForkStatement(List<NamedWorkerDecl> workers) implements Statement {
            @Override
            public String toString() {
                return String.format("fork { %s }",
                        String.join("", workers.stream().map(Object::toString).toList()));
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

            public VariableReference ref() {
                return new VariableReference(varName());
            }
        }

        record VarAssignStatement(Expression ref, Expression value) implements Statement {

            @Override
            public String toString() {
                return ref + " = " + value + ";";
            }
        }

        record IfElseStatement(Expression ifCondition, List<Statement> ifBody,
                               List<ElseIfClause> elseIfClauses, List<Statement> elseBody) implements Statement {

            public static IfElseStatement ifStatement(Expression condition, List<Statement> body) {
                return new IfElseStatement(condition, body, List.of(), List.of());
            }

            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder();
                sb.append("if ").append(ifCondition).append(" {\n");
                for (Statement statement : ifBody) {
                    sb.append(statement).append("\n");
                }
                sb.append("}");
                for (ElseIfClause elseIfClause : elseIfClauses) {
                    sb.append(elseIfClause);
                }
                if (!elseBody.isEmpty()) {
                    sb.append("else {\n");
                    for (Statement statement : elseBody) {
                        sb.append(statement).append("\n");
                    }
                    sb.append("}");
                }
                return sb.toString();
            }
        }


        record ForeachStatement(TypeBindingPattern typeBindingPattern, Expression expression,
                                List<Statement> body) implements Statement {

            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder();
                sb.append("foreach ").append(typeBindingPattern).append(" in ").append(expression).append(" {\n");
                for (Statement statement : body) {
                    sb.append(statement).append("\n");
                }
                sb.append("}");
                return sb.toString();
            }
        }

        record Comment(String comment) implements Statement {

            public Comment {
                if (comment == null) {
                    comment = "";
                }
                comment = comment.trim();
            }

            @Override
            public String toString() {
                return "\n// " +
                        comment.lines().filter(Predicate.not(String::isBlank)).collect(Collectors.joining("\n// ")) +
                        "\n";
            }
        }
    }

    // This is wrong but should be good enough for our uses
    public sealed interface Action extends Expression {

        record RemoteMethodCallAction(Expression expression, String methodName, List<Expression> args)
                implements Action {

            @Override
            public String toString() {
                return expression + "->" + methodName + "(" +
                        String.join(", ", args.stream().map(Objects::toString).toList()) + ")";
            }
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
}
