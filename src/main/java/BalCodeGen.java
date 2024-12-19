import io.ballerina.compiler.syntax.tree.FunctionBodyBlockNode;
import io.ballerina.compiler.syntax.tree.FunctionDefinitionNode;
import io.ballerina.compiler.syntax.tree.ImportDeclarationNode;
import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;
import io.ballerina.compiler.syntax.tree.ModulePartNode;
import io.ballerina.compiler.syntax.tree.Node;
import io.ballerina.compiler.syntax.tree.NodeFactory;
import io.ballerina.compiler.syntax.tree.NodeList;
import io.ballerina.compiler.syntax.tree.NodeParser;
import io.ballerina.compiler.syntax.tree.ServiceDeclarationNode;
import io.ballerina.compiler.syntax.tree.SyntaxKind;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.tools.text.TextDocuments;
import org.ballerinalang.formatter.core.Formatter;
import org.ballerinalang.formatter.core.FormatterException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BalCodeGen {
    private final BallerinaModel ballerinaModel;


    public BalCodeGen(BallerinaModel ballerinaModel) {
        this.ballerinaModel = ballerinaModel;
    }

    public void generateBalCode() {
        System.out.println("Generating Ballerina code...");
        List<ImportDeclarationNode> imports = new ArrayList<>();
        List<ModuleMemberDeclarationNode> moduleMembers = new ArrayList<>();
        for (BallerinaModel.Module module : ballerinaModel.modules()) {
            for (BallerinaModel.Import importDeclaration : module.imports()) {
                ImportDeclarationNode importDeclarationNode = NodeParser.parseImportDeclaration(
                        String.format("import %s/%s;", importDeclaration.org(), importDeclaration.module()));
                imports.add(importDeclarationNode);
            }

            for (BallerinaModel.Listener listener : module.listeners()) {
                ModuleMemberDeclarationNode member = NodeParser.parseModuleMemberDeclaration(
                        String.format("listener http:Listener %s = new (%s, {host: \"%s\"});", listener.name(),
                                listener.port(), listener.config().get("host")));
                moduleMembers.add(member);
            }

            for (BallerinaModel.Service service : module.services()) {
                StringBuilder stringBuilder = new StringBuilder();
                Iterator<String> iterator = service.listenerRefs().iterator();
                while (iterator.hasNext()) {
                    String listener = iterator.next();
                    stringBuilder.append(listener);
                    if (iterator.hasNext()) {
                        stringBuilder.append(", ");
                    }
                }

                ServiceDeclarationNode serviceDecl = (ServiceDeclarationNode) NodeParser.parseModuleMemberDeclaration(
                        String.format("service %s on %s { }", service.basePath(), stringBuilder));


                List<Node> members = new ArrayList<>();
                for (BallerinaModel.Resource resource : service.resources()) {
                    List<BallerinaModel.Parameter> parameters = resource.parameters();
                    String queryParamStr = String.join(",",
                            parameters.stream().map(p -> p.defaultExpr().isPresent() ?
                                            String.format("%s %s = %s", p.type(), p.name(), p.defaultExpr().get().expr()) :
                                            String.format("%s %s", p.type(), p.name()))
                                    .toList());

                    FunctionDefinitionNode resourceMethod = (FunctionDefinitionNode) NodeParser.parseObjectMember(
                            String.format("resource function %s %s(%s) returns %s {}",
                            resource.resourceMethodName(), resource.path(), queryParamStr, resource.returnType()));
                    List<String> strList = new ArrayList<>();
                    for (BallerinaModel.Statement statement : resource.body()) {
                        String s = generateStatement(statement);
                        strList.add(s);
                    }

                    String join = String.join("", strList);
                    FunctionBodyBlockNode funcBodyBlock = NodeParser.parseFunctionBodyBlock(String.format("{ %s }", join));
                    resourceMethod = resourceMethod.modify().withFunctionBody(funcBodyBlock).apply();
                    members.add(resourceMethod);
                }
                NodeList<Node> nodeList = NodeFactory.createNodeList(members);
                serviceDecl = serviceDecl.modify().withMembers(nodeList).apply();
                moduleMembers.add(serviceDecl);
            }
        }

        NodeList<ImportDeclarationNode> importDecls = NodeFactory.createNodeList(imports);
        NodeList<ModuleMemberDeclarationNode> moduleMemberDecls = NodeFactory.createNodeList(moduleMembers);
        ModulePartNode modulePartNode = NodeFactory.createModulePartNode(
                importDecls,
                moduleMemberDecls,
                NodeFactory.createToken(SyntaxKind.EOF_TOKEN)
        );

        SyntaxTree syntaxTree = SyntaxTree.from(TextDocuments.from(""));
        syntaxTree = syntaxTree.modifyWith(modulePartNode);
        SyntaxTree formattedSyntaxTree;
        try {
            formattedSyntaxTree = Formatter.format(syntaxTree);
        } catch (FormatterException e) {
            throw new RuntimeException(e);
        }

        System.out.println("============================================");
        System.out.println(formattedSyntaxTree.toSourceCode());
        System.out.println("============================================");
    }

    private static String generateStatement(BallerinaModel.Statement stmt) {
        if (stmt instanceof BallerinaModel.BallerinaStatement ballerinaStatement) {
            return ballerinaStatement.stmt();
        } else if (stmt instanceof BallerinaModel.IfElseStatement ifElseStatement) {
            StringBuilder stringBuilder = new StringBuilder();
            for (BallerinaModel.ElseIfClause elseIfClause : ifElseStatement.elseIfClauses()) {
                stringBuilder.append(
                        String.format("else if(%s) { %s }", elseIfClause.condition().expr(),
                                String.join("",
                                        elseIfClause.elseIfBody().stream().map(BalCodeGen::generateStatement).toList())));
            }

            return String.format("if (%s) { %s } %s else { %s }",
                    ifElseStatement.ifCondition().expr(),
                    String.join("", ifElseStatement.ifBody().stream().map(BalCodeGen::generateStatement).toList()),
                    stringBuilder,
                    String.join("", ifElseStatement.elseBody().stream().map(BalCodeGen::generateStatement).toList()));
        } else {
            throw new IllegalStateException();
        }
    }
}
