package dataweave.converter;

import ballerina.BallerinaModel;
import converter.ConversionUtils;
import converter.MuleToBalConverter;
import dataweave.parser.DataWeaveLexer;
import dataweave.parser.DataWeaveParser;
import mule.Constants;
import mule.MuleModel;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class DWReader {

    public static ParseTree readDWScript(String script) {
        DataWeaveLexer lexer = new DataWeaveLexer(CharStreams.fromString(script));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        DataWeaveParser parser = new DataWeaveParser(tokens);
        return parser.script();
    }

    public static ParseTree readDWScriptFromFile(String filePath) {
        Path path = Paths.get(filePath).toAbsolutePath();
        if (!Files.exists(path)) {
            throw new RuntimeException("File does not exist: " + filePath);
        }
        if (!Files.isRegularFile(path)) {
            throw new RuntimeException("Provided path is not a file: " + filePath);
        }
        if (!filePath.toLowerCase().endsWith(".dwl")) {
            throw new RuntimeException("Invalid file type. Expected a .dwl file: " + filePath);
        }
        String script;
        try {
            script = Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file - " + filePath, e);
        }
        DataWeaveLexer lexer = new DataWeaveLexer(CharStreams.fromString(script));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        DataWeaveParser parser = new DataWeaveParser(tokens);
        return parser.script();
    }

    public static void processDWElements(List<MuleModel.TransformMessageElement> children, MuleToBalConverter.Data data,
                                         List<BallerinaModel.Statement> statementList) {
        DWContext context = new DWContext(statementList);
        ParseTree tree;
        BallerinaVisitor visitor;
        for (MuleModel.TransformMessageElement child : children) {
            switch (child.kind()) {
                case DW_SET_PAYLOAD:
                    MuleModel.SetPayloadElement setPayloadElement = (MuleModel.SetPayloadElement) child;

                    if (setPayloadElement.script() != null) {
                        tree = readDWScript(setPayloadElement.script());
                    } else {
                        tree = readDWScriptFromFile(setPayloadElement.resource().replace(Constants.CLASSPATH
                                , Constants.CLASSPATH_DIR));
                    }
                    visitor = new BallerinaVisitor(context, data);
                    visitor.visit(tree);
                    StringBuilder statement = new StringBuilder(context.outputType + " "
                            + DWUtils.DATAWEAVE_OUTPUT_VARIABLE_NAME + " = ");
                    if (context.containsCheck) {
                        statement.append("check ");
                    }
                    statement.append(context.functionNames.getLast()).append("(")
                            .append(DWUtils.getParamsString(context.params)).append(");");
                    statementList.add(new BallerinaModel.BallerinaStatement(statement.toString()));
                    context.clearScript();
                    break;
                case DW_SET_VARIABLE:
                    MuleModel.SetVariableElement setVariableElement = (MuleModel.SetVariableElement) child;
                    if (setVariableElement.script() != null) {
                        tree = readDWScript(setVariableElement.script());
                    } else {
                        tree = readDWScriptFromFile(setVariableElement.resource().replace(Constants.CLASSPATH
                                , Constants.CLASSPATH_DIR));
                    }
                    visitor = new BallerinaVisitor(context, data);
                    visitor.visit(tree);
                    statementList.add(new BallerinaModel.BallerinaStatement(context.outputType + " "
                            + setVariableElement.variableName() + " = " + context.functionNames.getLast() + "(" +
                            DWUtils.getParamsString(context.params) + ");"));
                    context.clearScript();
                    break;
                case DW_SET_SESSION_VARIABLE:
                    MuleModel.SetSessionVariableElement sessionVariableElement =
                            (MuleModel.SetSessionVariableElement) child;
                    if (sessionVariableElement.script() != null) {
                        tree = readDWScript(sessionVariableElement.script());
                    } else {
                        tree = readDWScriptFromFile(sessionVariableElement.resource().replace(Constants.CLASSPATH
                                , Constants.CLASSPATH_DIR));
                    }
                    visitor = new BallerinaVisitor(context, data);
                    visitor.visit(tree);
                    statementList.add(new BallerinaModel.BallerinaStatement(context.outputType + " "
                            + sessionVariableElement.variableName() + " = " + context.functionNames.getLast() + "(" +
                            DWUtils.getParamsString(context.params) + ");"));
                    context.clearScript();
                    break;
                case DW_INPUT_PAYLOAD:
                    String mimeType = ((MuleModel.InputPayloadElement) child).mimeType();
                    context.setMimeType(mimeType);
                    break;
                default:
                    statementList.add(new BallerinaModel.BallerinaStatement(
                            ConversionUtils.wrapElementInUnsupportedBlockComment(child.toString())));
                    break;
            }
        }


    }
}
