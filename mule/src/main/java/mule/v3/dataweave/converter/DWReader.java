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

package mule.v3.dataweave.converter;

import common.BallerinaModel.Statement.BallerinaStatement;
import mule.v3.Constants;
import mule.v3.Context;
import mule.v3.ConversionUtils;
import mule.v3.dataweave.parser.DataWeaveLexer;
import mule.v3.dataweave.parser.DataWeaveParser;
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

import static common.BallerinaModel.Statement;
import static mule.v3.model.MuleModel.InputPayloadElement;
import static mule.v3.model.MuleModel.SetPayloadElement;
import static mule.v3.model.MuleModel.SetSessionVariableElement;
import static mule.v3.model.MuleModel.SetVariableElement;
import static mule.v3.model.MuleModel.TransformMessageElement;

public class DWReader {

    private static String readDWScriptFromFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (Files.exists(path) && Files.isRegularFile(path)) {
            if (!filePath.toLowerCase().endsWith(".dwl")) {
                throw new IOException("Invalid file type. Expected a .dwl file: " + filePath);
            }
            return Files.readString(path, StandardCharsets.UTF_8);
        }
        try (InputStream inputStream = DWReader.class.getClassLoader().getResourceAsStream(filePath)) {
            if (inputStream != null) {
                return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }
        }
        throw new IOException("File not found: " + filePath);
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
            String errors = errorListener.getErrors();
            context.currentScriptContext.errors.add(errors);
            throw new BallerinaDWException(errors);
        }
        return tree;
    }

    public static void processDWElements(List<TransformMessageElement> children, Context ctx,
                                         List<Statement> statementList) {
        DWContext context = new DWContext(ctx, statementList);
        for (TransformMessageElement child : children) {
            switch (child.kind()) {
                case DW_SET_PAYLOAD:
                    SetPayloadElement setPayloadElement = (SetPayloadElement) child;
                    addStatementToList(setPayloadElement.script(), setPayloadElement.resource(),
                            context, ctx, DWUtils.DATAWEAVE_OUTPUT_VARIABLE_NAME, statementList);
                    break;
                case DW_SET_VARIABLE:
                    SetVariableElement setVariableElement = (SetVariableElement) child;
                    addStatementToList(setVariableElement.script(), setVariableElement.resource(),
                            context, ctx, setVariableElement.variableName(), statementList);
                    break;
                case DW_SET_SESSION_VARIABLE:
                    SetSessionVariableElement sessionVariableElement =
                            (SetSessionVariableElement) child;
                    addStatementToList(sessionVariableElement.script(), sessionVariableElement.resource(),
                            context, ctx, sessionVariableElement.variableName(), statementList);
                    break;
                case DW_INPUT_PAYLOAD:
                    context.setMimeType(((InputPayloadElement) child).mimeType());
                    break;
                default:
                    ctx.migrationMetrics.failedBlocks.add(child.toString());
                    statementList.add(new BallerinaStatement(
                            ConversionUtils.wrapElementInUnsupportedBlockComment(child.toString())));
                    break;
            }
        }
    }

    private static void addStatementToList(String script, String resourcePath,
                                           DWContext context,
                                           Context ctx,
                                           String varName,
                                           List<Statement> statementList) {
        String funcStatement = getFunctionStatement(script, resourcePath, context, ctx, varName);
        statementList.add(new BallerinaStatement(funcStatement));
        context.clearScript();
    }

    private static String getFunctionStatement(String script, String resourcePath, DWContext context,
                                               Context ctx, String varName) {
        if (script != null) {
            ParseTree tree;
            try {
                tree = parseScript(script, context);
            } catch (BallerinaDWException e) {
                ctx.migrationMetrics.dwConversionStats.recordParseFailure(countDWBodyLines(script), script);
                return ConversionUtils.wrapElementInTodoComment(script, "DATAWEAVE PARSING FAILED.",
                        "Error details: " + e.getScriptIdentifier());
            }
            BallerinaVisitor visitor = new BallerinaVisitor(context, ctx, ctx.migrationMetrics.dwConversionStats);
            visitor.visit(tree);
            context.currentScriptContext.funcName = context.functionNames.getLast();
            return buildStatement(context, varName);
        }
        if (context.scriptCache.containsKey(resourcePath)) {
            context.currentScriptContext = context.scriptCache.get(resourcePath);
            return buildStatement(context, varName);
        }
        String filePath = resourcePath.replace(Constants.CLASSPATH, Constants.CLASSPATH_DIR);
        String fileScript;
        try {
            fileScript = readDWScriptFromFile(filePath);
        } catch (IOException e) {
            ctx.migrationMetrics.dwConversionStats.record(DWConstruct.MISSING_SCRIPT, false);
            ctx.migrationMetrics.dwConversionStats.addMissingScriptLineEstimate();
            ctx.migrationMetrics.dwConversionStats.failedDWExpressions
                    .add("// DataWeave script not found: " + filePath);
            return ConversionUtils.wrapElementInTodoComment(filePath, "DATAWEAVE FILE NOT FOUND.");
        }
        ParseTree tree;
        try {
            tree = parseScript(fileScript, context);
        } catch (BallerinaDWException e) {
            ctx.migrationMetrics.dwConversionStats.recordParseFailure(countDWBodyLines(fileScript), fileScript);
            return ConversionUtils.wrapElementInTodoComment(fileScript, "DATAWEAVE PARSING FAILED.",
                    "Error details: " + e.getScriptIdentifier());
        }
        BallerinaVisitor visitor = new BallerinaVisitor(context, ctx, ctx.migrationMetrics.dwConversionStats);
        visitor.visit(tree);
        context.currentScriptContext.funcName = context.functionNames.getLast();
        context.scriptCache.put(resourcePath, context.currentScriptContext);
        return buildStatement(context, varName);
    }

    private static int countDWBodyLines(String script) {
        return (int) script.lines()
                .filter(l -> !l.trim().startsWith("%dw") && !l.trim().equals("---"))
                .count();
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
