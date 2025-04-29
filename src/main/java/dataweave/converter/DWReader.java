package dataweave.converter;

import common.BallerinaModel;
import common.BallerinaModel.Statement.BallerinaStatement;
import converter.Constants;
import converter.ConversionUtils;
import converter.MuleToBalConverter;
import dataweave.parser.DataWeaveLexer;
import dataweave.parser.DataWeaveParser;
import mule.MuleModel;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class DWReader {

    public static ParseTree readDWScriptFromFile(String filePath, DWContext context) {
        Path path = Paths.get(filePath);
        if (Files.exists(path) && Files.isRegularFile(path)) {
            return parseFromFile(path, context);
        }
        InputStream inputStream = DWReader.class.getClassLoader().getResourceAsStream(filePath);
        if (inputStream != null) {
            return parseFromStream(inputStream, filePath, context);
        }
        throw new RuntimeException("File not found: " + filePath);
    }

    private static ParseTree parseFromFile(Path path, DWContext context) {
        if (!path.toString().toLowerCase().endsWith(".dwl")) {
            throw new RuntimeException("Invalid file type. Expected a .dwl file: " + path);
        }
        try {
            String script = Files.readString(path, StandardCharsets.UTF_8);
            return parseScript(script, context);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file - " + path, e);
        }
    }

    private static ParseTree parseFromStream(InputStream inputStream, String filePath, DWContext context) {
        try {
            String script = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return parseScript(script, context);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file from resources: " + filePath, e);
        }
    }

    private static ParseTree parseScript(String script, DWContext context) {
        DataWeaveLexer lexer = new DataWeaveLexer(CharStreams.fromString(script));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        DataWeaveParser parser = new DataWeaveParser(tokens);

        parser.removeErrorListeners();
        lexer.removeErrorListeners();

        DWParserErrorListener errorListener = new DWParserErrorListener();
        parser.addErrorListener(errorListener);
        lexer.addErrorListener(errorListener);

        ParseTree tree = parser.script();

        if (errorListener.hasErrors()) {
            context.currentScriptContext.errors.add(errorListener.getErrors());
        }
        return tree;
    }

    public static void processDWElements(List<MuleModel.TransformMessageElement> children, MuleToBalConverter.Data data,
                                         List<BallerinaModel.Statement> statementList) {
        DWContext context = new DWContext(statementList);
        for (MuleModel.TransformMessageElement child : children) {
            switch (child.kind()) {
                case DW_SET_PAYLOAD:
                    MuleModel.SetPayloadElement setPayloadElement = (MuleModel.SetPayloadElement) child;
                    addStatementToList(setPayloadElement.script(), setPayloadElement.resource(),
                            context, data, DWUtils.DATAWEAVE_OUTPUT_VARIABLE_NAME, statementList);
                    break;
                case DW_SET_VARIABLE:
                    MuleModel.SetVariableElement setVariableElement = (MuleModel.SetVariableElement) child;
                    addStatementToList(setVariableElement.script(), setVariableElement.resource(),
                            context, data, setVariableElement.variableName(), statementList);
                    break;
                case DW_SET_SESSION_VARIABLE:
                    MuleModel.SetSessionVariableElement sessionVariableElement =
                            (MuleModel.SetSessionVariableElement) child;
                    addStatementToList(sessionVariableElement.script(), sessionVariableElement.resource(),
                            context, data, sessionVariableElement.variableName(), statementList);
                    break;
                case DW_INPUT_PAYLOAD:
                    context.setMimeType(((MuleModel.InputPayloadElement) child).mimeType());
                    break;
                default:
                    statementList.add(new BallerinaStatement(
                            ConversionUtils.wrapElementInUnsupportedBlockComment(child.toString())));
                    break;
            }
        }
    }

    private static void addStatementToList(String script, String resourcePath,
                                           DWContext context,
                                           MuleToBalConverter.Data data,
                                           String varName,
                                           List<BallerinaModel.Statement> statementList) {
        String funcStatement = getFunctionStatement(script, resourcePath, context, data, varName);
        statementList.add(new BallerinaStatement(funcStatement));
        context.clearScript();
    }

    private static String getFunctionStatement(String script, String resourcePath, DWContext context,
                                               MuleToBalConverter.Data data, String varName) {
        if (script != null) {
            ParseTree tree = parseScript(script, context);
            BallerinaVisitor visitor = new BallerinaVisitor(context, data,
                    data.sharedProjectData.getDwConversionStats());
            visitor.visit(tree);
            context.currentScriptContext.funcName = context.functionNames.getLast();
            return buildStatement(context, varName);
        }
        if (context.scriptCache.containsKey(resourcePath)) {
            context.currentScriptContext = context.scriptCache.get(resourcePath);
            return buildStatement(context, varName);
        }
        ParseTree tree = readDWScriptFromFile(resourcePath.replace(Constants.CLASSPATH, Constants.CLASSPATH_DIR),
                context);
        BallerinaVisitor visitor = new BallerinaVisitor(context, data, data.sharedProjectData.getDwConversionStats());
        visitor.visit(tree);
        context.currentScriptContext.funcName = context.functionNames.getLast();
        context.scriptCache.put(resourcePath, context.currentScriptContext);
        return buildStatement(context, varName);

    }

    private static String buildStatement(DWContext context, String varName) {
        boolean isOutputVar = varName.equals(DWUtils.DATAWEAVE_OUTPUT_VARIABLE_NAME);
        StringBuilder statement = new StringBuilder();
        if (isOutputVar && !context.isOutputVarSet) {
            context.isOutputVarSet = true;
            statement.append(context.currentScriptContext.outputType).append(" ");
        } else if (!isOutputVar) {
            statement.append(context.currentScriptContext.outputType).append(" ");
        }
        statement.append(varName).append(" = ");
        if (context.currentScriptContext.containsCheck) {
            statement.append("check ");
        }
        String paramsString = DWUtils.getParamsString(context.currentScriptContext.params);
        statement.append(context.functionNames.getLast())
                .append("(")
                .append(!paramsString.isEmpty() ? String.format("ctx.%s.toJson()", paramsString) : "")
                .append(");");
        return statement.toString();
    }
}
