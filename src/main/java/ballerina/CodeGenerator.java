package ballerina;

import converter.ConversionUtils;
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
                TypeDefinitionNode typeDefinitionNode = (TypeDefinitionNode) NodeParser.parseModuleMemberDeclaration(
                        String.format("type %s %s;", moduleTypeDef.name(), moduleTypeDef.type()));
                moduleMembers.add(typeDefinitionNode);
            }

            for (ModuleVar moduleVar : textDocument.moduleVars()) {
                ModuleMemberDeclarationNode member = NodeParser.parseModuleMemberDeclaration(
                        String.format("%s %s = %s;", moduleVar.type(), moduleVar.name(), moduleVar.expr().expr()));
                moduleMembers.add(member);
            }

            for (Listener listener : textDocument.listeners()) {
                ModuleMemberDeclarationNode member = NodeParser.parseModuleMemberDeclaration(
                        String.format("listener http:Listener %s = new (%s, {host: \"%s\"});", listener.name(),
                                listener.port(), listener.config().get("host")));
                moduleMembers.add(member);
            }

            for (Service service : textDocument.services()) {
                String listenerRefs = constructCommaSeparatedString(service.listenerRefs());
                ServiceDeclarationNode serviceDecl = (ServiceDeclarationNode) NodeParser.parseModuleMemberDeclaration(
                        String.format("service %s on %s { }", service.basePath(), listenerRefs));

                List<Node> members = new ArrayList<>();
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
                    String funcParamString = constructFunctionParameterString(function.parameters(), false);
                    FunctionDefinitionNode funcDefn = (FunctionDefinitionNode) NodeParser.parseObjectMember(
                            String.format("%sfunction %s(%s) %s {}",
                                    getVisibilityQualifier(function.visibilityQualifier()), function.methodName(),
                                    funcParamString, getReturnTypeDescriptor(function.returnType())));

                    FunctionBodyBlockNode funcBodyBlock = constructFunctionBodyBlock(function.body());
                    funcDefn = funcDefn.modify().withFunctionBody(funcBodyBlock).apply();
                    members.add(funcDefn);
                }

                NodeList<Node> nodeList = NodeFactory.createNodeList(members);
                serviceDecl = serviceDecl.modify().withMembers(nodeList).apply();
                moduleMembers.add(serviceDecl);
            }

            for (Function f : textDocument.functions()) {
                String funcParamString = constructFunctionParameterString(f.parameters(), false);
                String methodName = ConversionUtils.escapeSpecialCharacters(f.methodName());
                FunctionDefinitionNode fd = (FunctionDefinitionNode) NodeParser.parseModuleMemberDeclaration(
                        String.format("%sfunction %s(%s) %s {}", getVisibilityQualifier(f.visibilityQualifier()),
                                methodName, funcParamString, getReturnTypeDescriptor(f.returnType())));
                FunctionBodyBlockNode funcBodyBlock = constructFunctionBodyBlock(f.body());
                fd = fd.modify().withFunctionBody(funcBodyBlock).apply();
                moduleMembers.add(fd);
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
