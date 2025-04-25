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

import static ballerina.BallerinaModel.Function;
import static ballerina.BallerinaModel.Import;
import static ballerina.BallerinaModel.Listener;
import static ballerina.BallerinaModel.Module;
import static ballerina.BallerinaModel.ModuleTypeDef;
import static ballerina.BallerinaModel.ModuleVar;
import static ballerina.BallerinaModel.ObjectField;
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
                ImportDeclarationNode importDeclarationNode =
                        NodeParser.parseImportDeclaration(importDeclaration.toString());
                imports.add(importDeclarationNode);
            }

            List<ModuleMemberDeclarationNode> moduleMembers = new ArrayList<>(textDocument.astNodes());

            for (ModuleTypeDef moduleTypeDef : textDocument.moduleTypeDefs()) {
                // TODO: handle visibility qualifier properly
                TypeDefinitionNode typeDefinitionNode = (TypeDefinitionNode) NodeParser.parseModuleMemberDeclaration(
                        moduleTypeDef.toString());
                moduleMembers.add(typeDefinitionNode);
            }

            for (ModuleVar moduleVar : textDocument.moduleVars()) {
                ModuleMemberDeclarationNode member = NodeParser.parseModuleMemberDeclaration(moduleVar.toString());
                moduleMembers.add(member);
            }

            for (Listener listener : textDocument.listeners()) {
                // TODO: handle visibility qualifier properly
                ModuleMemberDeclarationNode member = NodeParser.parseModuleMemberDeclaration(listener.toString());
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
                String methodName = f.functionName();
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

            for (String f : textDocument.intrinsics()) {
                moduleMembers.add(NodeParser.parseModuleMemberDeclaration(f));
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
                                    function.visibilityQualifier()), function.functionName(), funcParamString,
                            getReturnTypeDescriptor(function.returnType())));
            functionDefinitionNode = generateBallerinaFunction(functionDefinitionNode, function.body());
        } else {
            functionDefinitionNode = generateBallerinaExternalFunction(function, funcParamString,
                    function.functionName());
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

    public static SyntaxTree formatSyntaxTree(SyntaxTree syntaxTree) {
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
            stmtList.add(statement.toString());
        }

        String joinedStatements = String.join("", stmtList);
        return NodeParser.parseFunctionBodyBlock(String.format("{ %s }", joinedStatements));
    }
}
