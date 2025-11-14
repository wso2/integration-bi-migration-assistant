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
package mule.v4.dataweave.converter;

import common.BallerinaModel.Statement.BallerinaStatement;
import mule.v4.Constants;
import mule.v4.Context;
import mule.v4.ConversionUtils;
import mule.v4.dataweave.parser.DataWeaveLexer;
import mule.v4.dataweave.parser.DataWeaveParser;
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
import static common.ConversionUtils.stmtFrom;
import static mule.v4.model.MuleModel.SetPayloadElement;
import static mule.v4.model.MuleModel.SetVariableElement;
import static mule.v4.model.MuleModel.TransformMessageElement;

public class DWReader {

    public static ParseTree readDWScriptFromFile(String filePath, DWContext context) throws DWCodeGenException {
        try {
            return readDWScriptFromFileInner(filePath, context);
        } catch (Exception e) {
            throw new DWCodeGenException(filePath, e);
        }
    }

    private static ParseTree readDWScriptFromFileInner(String filePath, DWContext context) throws IOException {
        Path path = Paths.get(filePath);
        if (Files.exists(path) && Files.isRegularFile(path)) {
            return parseFromFile(path, context);
        }
        InputStream inputStream = DWReader.class.getClassLoader().getResourceAsStream(filePath);
        if (inputStream != null) {
            return parseFromStream(inputStream, filePath, context);
        }
        throw new IOException("File not found: " + filePath);
    }

    private static ParseTree parseFromFile(Path path, DWContext context) throws IOException {
        if (!path.toString().toLowerCase().endsWith(".dwl")) {
            throw new IOException("Invalid file type. Expected a .dwl file: " + path);
        }
        String script = Files.readString(path, StandardCharsets.UTF_8);
        return parseScript(script, context);
    }

    private static ParseTree parseFromStream(InputStream inputStream, String filePath, DWContext context)
            throws IOException {
        String script = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        return parseScript(script, context);
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

    public static void processDWElements(List<TransformMessageElement> children, Context ctx,
                                         List<Statement> statementList) throws DWCodeGenException {
        try {
            processDWElementsInner(children, ctx, statementList);
        } catch (DWCodeGenException e) {
            throw e;
        } catch (Exception e) {
            throw new DWCodeGenException(resolveScriptIdentifier(ctx), e);
        }
    }

    private static void processDWElementsInner(List<TransformMessageElement> children, Context ctx,
                                               List<Statement> statementList) throws DWCodeGenException {
        DWContext context = new DWContext(ctx, statementList);
        for (TransformMessageElement child : children) {
            switch (child.kind()) {
                case DW_SET_PAYLOAD:
                    SetPayloadElement setPayloadElement = (SetPayloadElement) child;
                    addStatementToList(setPayloadElement.script(), setPayloadElement.resource(),
                            context, ctx, DWUtils.DATAWEAVE_OUTPUT_VARIABLE_NAME, statementList);
                    statementList.add(stmtFrom("%s.payload = %s;".formatted(Constants.CONTEXT_REFERENCE,
                            DWUtils.DATAWEAVE_OUTPUT_VARIABLE_NAME)));
                    break;
                case DW_SET_VARIABLE:
                    SetVariableElement setVariableElement = (SetVariableElement) child;
                    addStatementToList(setVariableElement.script(), setVariableElement.resource(),
                            context, ctx, setVariableElement.variableName(), statementList);
                    break;
                default:
                    // TODO: add this to unsupported blocks in report?
                    statementList.add(new BallerinaStatement(
                            ConversionUtils.wrapElementInUnsupportedBlockComment(child.toString())));
                    break;
            }
        }
    }

    private static String resolveScriptIdentifier(Context ctx) {
        if (ctx != null && ctx.currentFileCtx != null && ctx.currentFileCtx.filePath != null) {
            return ctx.currentFileCtx.filePath;
        }
        return "<unknown-dataweave-script>";
    }

    private static void addStatementToList(String script, String resourcePath,
                                           DWContext context,
                                           Context ctx,
                                           String varName,
                                           List<Statement> statementList) throws DWCodeGenException {
        String funcStatement = getFunctionStatement(script, resourcePath, context, ctx, varName);
        statementList.add(new BallerinaStatement(funcStatement));
        ctx.projectCtx.vars.put(varName, context.currentScriptContext.outputType);
        statementList.add(stmtFrom(
                "%s.%s = %s;".formatted(Constants.VARS_FIELD_ACCESS, varName, varName)));
        context.clearScript();
    }

    private static String getFunctionStatement(String script, String resourcePath, DWContext context,
                                               Context ctx, String varName) throws DWCodeGenException {
        if (script != null) {
            ParseTree tree = parseScript(script, context);
            BallerinaVisitor visitor = new BallerinaVisitor(context, ctx, ctx.migrationMetrics.dwConversionStats);
            visitor.visit(tree);
            context.currentScriptContext.funcName = context.functionNames.getLast();
            return buildStatement(context, varName);
        }
        if (context.scriptCache.containsKey(resourcePath)) {
            context.currentScriptContext = context.scriptCache.get(resourcePath);
            return buildStatement(context, varName);
        }
        String resolvedPath = resourcePath.replace(Constants.CLASSPATH, Constants.CLASSPATH_DIR);
        ParseTree tree = readDWScriptFromFile(resolvedPath, context);
        BallerinaVisitor visitor = new BallerinaVisitor(context, ctx, ctx.migrationMetrics.dwConversionStats);
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
        String paramsString = DWUtils.getParamsString(Constants.FUNC_PARAMS_WITH_CONTEXT);
        statement.append(context.functionNames.getLast())
                .append("(")
                .append(paramsString)
                .append(");");
        return statement.toString();
    }
}
