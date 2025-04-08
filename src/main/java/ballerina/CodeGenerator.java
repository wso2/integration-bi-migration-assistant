package ballerina;

import io.ballerina.compiler.syntax.tree.FunctionBodyBlockNode;
import io.ballerina.compiler.syntax.tree.FunctionDefinitionNode;
import io.ballerina.compiler.syntax.tree.ImportDeclarationNode;
import io.ballerina.compiler.syntax.tree.MinutiaeList;
import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;
import io.ballerina.compiler.syntax.tree.ModulePartNode;
import io.ballerina.compiler.syntax.tree.Node;
import io.ballerina.compiler.syntax.tree.NodeFactory;
import io.ballerina.compiler.syntax.tree.NodeList;
import io.ballerina.compiler.syntax.tree.NodeParser;
import io.ballerina.compiler.syntax.tree.ObjectFieldNode;
import io.ballerina.compiler.syntax.tree.ServiceDeclarationNode;
import io.ballerina.compiler.syntax.tree.SyntaxKind;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.compiler.syntax.tree.TypeDefinitionNode;
import io.ballerina.tools.text.TextDocuments;
import org.ballerinalang.formatter.core.Formatter;
import org.ballerinalang.formatter.core.FormatterException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static ballerina.BallerinaModel.BallerinaStatement;
import static ballerina.BallerinaModel.DoStatement;
import static ballerina.BallerinaModel.ElseIfClause;
import static ballerina.BallerinaModel.Function;
import static ballerina.BallerinaModel.IfElseStatement;
import static ballerina.BallerinaModel.Import;
import static ballerina.BallerinaModel.Listener;
import static ballerina.BallerinaModel.Module;
import static ballerina.BallerinaModel.ModuleTypeDef;
import static ballerina.BallerinaModel.ModuleVar;
import static ballerina.BallerinaModel.ObjectField;
import static ballerina.BallerinaModel.OnFailClause;
import static ballerina.BallerinaModel.Parameter;
import static ballerina.BallerinaModel.Resource;
import static ballerina.BallerinaModel.Service;
import static ballerina.BallerinaModel.Statement;
import static ballerina.BallerinaModel.TextDocument;

public class CodeGenerator {
    private final BallerinaModel ballerinaModel;

    public CodeGenerator(BallerinaModel ballerinaModel) {
        this.ballerinaModel = ballerinaModel;
    }

    public SyntaxTree generateBalCode() {
        List<SyntaxTree> syntaxTrees = new ArrayList<>();
        List<Module> modules = ballerinaModel.modules();
        // TODO: assumed single module for now
        Module module = modules.getFirst();

        for (TextDocument textDocument : module.textDocuments()) {
            List<ImportDeclarationNode> imports = new ArrayList<>();
            for (Import importDeclaration : textDocument.imports()) {
                ImportDeclarationNode importDeclarationNode = NodeParser.parseImportDeclaration(
                        constructImportDeclaration(importDeclaration));
                imports.add(importDeclarationNode);
            }

            List<ModuleMemberDeclarationNode> moduleMembers = new ArrayList<>();

            for (ModuleTypeDef moduleTypeDef : textDocument.moduleTypeDefs()) {
                // TODO: handle visibility qualifier properly
                TypeDefinitionNode typeDefinitionNode = (TypeDefinitionNode) NodeParser.parseModuleMemberDeclaration(
                        String.format("public type %s %s;", moduleTypeDef.name(), moduleTypeDef.type()));
                moduleMembers.add(typeDefinitionNode);
            }

            for (ModuleVar moduleVar : textDocument.moduleVars()) {
                ModuleMemberDeclarationNode member = NodeParser.parseModuleMemberDeclaration(
                        String.format("%s %s = %s;", moduleVar.type(), moduleVar.name(), moduleVar.expr().expr()));
                moduleMembers.add(member);
            }

            for (Listener listener : textDocument.listeners()) {
                // TODO: handle visibility qualifier properly
                ModuleMemberDeclarationNode member = NodeParser.parseModuleMemberDeclaration(
                        String.format("public listener http:Listener %s = new (%s, {host: \"%s\"});", listener.name(),
                                listener.port(), listener.config().get("host")));
                moduleMembers.add(member);
            }

            for (Service service : textDocument.services()) {
                String listenerRefs = constructCommaSeparatedString(service.listenerRefs());
                ServiceDeclarationNode serviceDecl = (ServiceDeclarationNode) NodeParser.parseModuleMemberDeclaration(
                        String.format("service %s on %s { }", service.basePath(), listenerRefs));

                List<Node> members = new ArrayList<>();
                for (ObjectField field : service.fields()) {
                    ObjectFieldNode objectFieldNode = (ObjectFieldNode) NodeParser.parseObjectMember(
                            String.format("%s %s;", field.type(), field.name()));
                    members.add(objectFieldNode);
                }

                if (service.initFunc().isPresent()) {
                    members.add(genFunctionDefinitionNode(service.initFunc().get()));
                }

                for (Resource resource : service.resources()) {
                    String funcParamStr = constructFunctionParameterString(resource.parameters(), false);
                    FunctionDefinitionNode resourceMethod = (FunctionDefinitionNode) NodeParser.parseObjectMember(
                            String.format("resource function %s %s(%s) %s {}",
                                    resource.resourceMethodName(), resource.path(), funcParamStr,
                                    getReturnTypeDescriptor(resource.returnType())));

                    FunctionBodyBlockNode funcBodyBlock = constructFunctionBodyBlock(resource.body());
                    resourceMethod = resourceMethod.modify().withFunctionBody(funcBodyBlock).apply();
                    members.add(resourceMethod);
                }

                for (Function function : service.functions()) {
                    FunctionDefinitionNode funcDefn = genFunctionDefinitionNode(function);
                    members.add(funcDefn);
                }

                NodeList<Node> nodeList = NodeFactory.createNodeList(members);
                serviceDecl = serviceDecl.modify().withMembers(nodeList).apply();
                moduleMembers.add(serviceDecl);
            }

            for (Function f : textDocument.functions()) {
                String funcParamString = constructFunctionParameterString(f.parameters(), false);
                String methodName = f.funcName();
                FunctionDefinitionNode functionDefinitionNode;
                if (f.body() instanceof BallerinaModel.BlockFunctionBody) {
                    FunctionDefinitionNode fd = (FunctionDefinitionNode) NodeParser.parseModuleMemberDeclaration(
                            String.format("%sfunction %s(%s) %s {}", getVisibilityQualifier(f.visibilityQualifier()),
                                    methodName, funcParamString, getReturnTypeDescriptor(f.returnType())));
                    functionDefinitionNode = generateBallerinaFunction(fd, f.body());
                } else {
                    functionDefinitionNode = generateBallerinaExternalFunction(f, funcParamString, methodName);
                }
                moduleMembers.add(functionDefinitionNode);
            }

            NodeList<ImportDeclarationNode> importDecls = NodeFactory.createNodeList(imports);
            NodeList<ModuleMemberDeclarationNode> moduleMemberDecls = NodeFactory.createNodeList(moduleMembers);

            MinutiaeList eofLeadingMinutiae;
            if (textDocument.Comments().isEmpty()) {
                eofLeadingMinutiae = NodeFactory.createEmptyMinutiaeList();
            } else {
                String comments = String.join("", textDocument.Comments());
                eofLeadingMinutiae = parseLeadingMinutiae(comments);
            }

            SyntaxTree syntaxTree = createSyntaxTree(importDecls, moduleMemberDecls, eofLeadingMinutiae);
            syntaxTree = formatSyntaxTree(syntaxTree);
            syntaxTrees.add(syntaxTree);
        }

        // only a single bal file is considered for now
        return syntaxTrees.getFirst();
    }

    private FunctionDefinitionNode genFunctionDefinitionNode(Function function) {
        String funcParamString = constructFunctionParameterString(function.parameters(), false);
        FunctionDefinitionNode functionDefinitionNode;
        if (function.body() instanceof BallerinaModel.BlockFunctionBody) {
            functionDefinitionNode = (FunctionDefinitionNode) NodeParser.parseObjectMember(
                    String.format("%sfunction %s(%s) %s {}", getVisibilityQualifier(
                                    function.visibilityQualifier()), function.funcName(), funcParamString,
                            getReturnTypeDescriptor(function.returnType())));
            functionDefinitionNode = generateBallerinaFunction(functionDefinitionNode, function.body());
        } else {
            functionDefinitionNode = generateBallerinaExternalFunction(function, funcParamString,
                    function.funcName());
        }
        return functionDefinitionNode;
    }

    private FunctionDefinitionNode generateBallerinaExternalFunction(Function f, String funcParamString,
                                                                     String methodName) {
        BallerinaModel.ExternFunctionBody body = (BallerinaModel.ExternFunctionBody) f.body();
        return (FunctionDefinitionNode) NodeParser.parseModuleMemberDeclaration(
                String.format("%sfunction %s(%s) %s  = %s { %s } external;", getVisibilityQualifier(
                        f.visibilityQualifier()),
                        methodName, funcParamString, getReturnTypeDescriptor(f.returnType()), body.annotation(),
                        getExternBody((BallerinaModel.ExternFunctionBody) f.body())));
    }

    private String getExternBody(BallerinaModel.ExternFunctionBody body) {
        StringBuilder s = new StringBuilder();
        s.append("\n'class: \"").append(body.className()).append("\"");
        if (body.javaMethodName().isPresent()) {
            s.append(",\n name: \"").append(body.javaMethodName().get()).append("\"");
        }
        if (body.paramTypes().isPresent()) {
            s.append(",\n paramTypes: [");
            String paramTypeString = String.join(", ",
                    body.paramTypes().get().stream()
                            .map(param -> "\"" + param + "\"")
                            .toList()
            );

            s.append(paramTypeString).append("]");
        }
        return s.append("\n").toString();
    }

    private FunctionDefinitionNode generateBallerinaFunction(FunctionDefinitionNode fd,
                                                             BallerinaModel.FunctionBody body) {
        FunctionBodyBlockNode funcBodyBlock = constructFunctionBodyBlock(((BallerinaModel.BlockFunctionBody)
                body).statements());
        return fd.modify().withFunctionBody(funcBodyBlock).apply();
    }

    private static MinutiaeList parseLeadingMinutiae(String leadingMinutiae) {
        return NodeParser.parseImportDeclaration(leadingMinutiae + "\nimport x/y;").leadingMinutiae();
    }

    private static SyntaxTree createSyntaxTree(NodeList<ImportDeclarationNode> importDecls,
                                               NodeList<ModuleMemberDeclarationNode> moduleMemberDecls,
                                               MinutiaeList eofLeadingMinutiae) {
        ModulePartNode modulePartNode = NodeFactory.createModulePartNode(
                importDecls,
                moduleMemberDecls,
                NodeFactory.createToken(SyntaxKind.EOF_TOKEN, eofLeadingMinutiae, NodeFactory.createEmptyMinutiaeList())
        );

        SyntaxTree syntaxTree = SyntaxTree.from(TextDocuments.from(""));
        syntaxTree = syntaxTree.modifyWith(modulePartNode);
        return syntaxTree;
    }

    private static SyntaxTree formatSyntaxTree(SyntaxTree syntaxTree) {
        try {
            syntaxTree = Formatter.format(syntaxTree);
        } catch (FormatterException e) {
            throw new RuntimeException("Error formatting the syntax tree");
        }
        return syntaxTree;
    }

    private String getReturnTypeDescriptor(Optional<String> returnType) {
        return returnType.map(r ->  String.format("returns %s", r)).orElse("");
    }

    private String getVisibilityQualifier(Optional<String> visibilityQualifier) {
        return visibilityQualifier.map(s -> s + " ").orElse("");
    }

    private String constructCommaSeparatedString(List<String> strings) {
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<String> iterator = strings.iterator();
        while (iterator.hasNext()) {
            String listener = iterator.next();
            stringBuilder.append(listener);
            if (iterator.hasNext()) {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder.toString();
    }

    private String constructFunctionParameterString(List<Parameter> parameters, boolean skipDefaultExpr) {
        if (skipDefaultExpr) {
            return String.join(",", parameters.stream().map(p -> String.format("%s %s", p.type(), p.name()))
                    .toList());
        }

        return String.join(",", parameters.stream().map(p -> p.defaultExpr().isPresent() ?
                String.format("%s %s = %s", p.type(), p.name(), p.defaultExpr().get().expr()) :
                String.format("%s %s", p.type(), p.name())).toList());
    }

    private FunctionBodyBlockNode constructFunctionBodyBlock(List<Statement> body) {
        List<String> stmtList = new ArrayList<>();
        for (Statement statement : body) {
            String s = constructBallerinaStatements(statement);
            stmtList.add(s);
        }

        String joinedStatements = String.join("", stmtList);
        return NodeParser.parseFunctionBodyBlock(String.format("{ %s }", joinedStatements));
    }

    private static String constructBallerinaStatements(Statement stmt) {
        if (stmt instanceof BallerinaStatement balStmt) {
            return balStmt.stmt();
        } else if (stmt instanceof DoStatement doStatement) {
            return constructDoStatement(doStatement);
        } else if (stmt instanceof IfElseStatement ifElseStmt) {
            StringBuilder elseIfStringBuilder = new StringBuilder();
            for (ElseIfClause elseIfClause : ifElseStmt.elseIfClauses()) {
                elseIfStringBuilder.append(
                        String.format("else if %s { %s }",
                                elseIfClause.condition().expr(),
                                String.join("", elseIfClause.elseIfBody().stream()
                                        .map(CodeGenerator::constructBallerinaStatements).toList())
                        ));
            }

            List<Statement> elseBody = ifElseStmt.elseBody();
            String elseString = elseBody.isEmpty() ? "" :
                    String.format("else { %s }", String.join("", elseBody.stream()
                            .map(CodeGenerator::constructBallerinaStatements).toList()));

            return String.format("if %s { %s } %s %s",
                    ifElseStmt.ifCondition().expr(),
                    String.join("", ifElseStmt.ifBody().stream()
                            .map(CodeGenerator::constructBallerinaStatements).toList()), elseIfStringBuilder,
                    elseString);
        } else {
            throw new IllegalStateException();
        }
    }

    private static String constructDoStatement(DoStatement doStatement) {
        String doBlock = String.format("do { %s }", String.join("", doStatement.doBody().stream()
                .map(CodeGenerator::constructBallerinaStatements).toList()));
        if (doStatement.onFailClause().isEmpty()) {
            return doBlock;
        }

        OnFailClause onFailClause = doStatement.onFailClause().get();
        String typeBindingPattern = onFailClause.typeBindingPattern().map(tbp -> String.format("%s %s", tbp.type(),
                tbp.variableName())).orElse("");

        String onFailBody = String.join("", onFailClause.onFailBody().stream()
                .map(CodeGenerator::constructBallerinaStatements).toList());
        return String.format("%s on fail %s { %s }", doBlock, typeBindingPattern, onFailBody);
    }

    private static String constructImportDeclaration(Import importDeclaration) {
        String importPrefix = importDeclaration.importPrefix().map(ip -> String.format(" as %s", ip)).orElse("");
        return String.format("import %s/%s%s;", importDeclaration.orgName(), importDeclaration.moduleName(),
                importPrefix);
    }
}
