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

package mule.dataweave.converter;

import common.BallerinaModel.Statement.BallerinaStatement;
import io.ballerina.compiler.internal.parser.LexerTerminals;
import mule.Constants;
import mule.Context;
import mule.dataweave.converter.builder.IfStatementBuilder;
import mule.dataweave.parser.DataWeaveBaseVisitor;
import mule.dataweave.parser.DataWeaveParser;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static common.BallerinaModel.BlockFunctionBody;
import static common.BallerinaModel.Expression;
import static common.BallerinaModel.ExternFunctionBody;
import static common.BallerinaModel.Function;
import static common.BallerinaModel.Import;
import static common.BallerinaModel.Parameter;
import static common.BallerinaModel.Statement;
import static mule.Constants.BAL_HANDLE_TYPE;
import static mule.Constants.BAL_INT_TYPE;
import static mule.Constants.BAL_STRING_TYPE;
import static common.ConversionUtils.typeFrom;

public class BallerinaVisitor extends DataWeaveBaseVisitor<Void> {

    private final DWContext dwContext;
    private final Context ctx;
    private int varCount = 0;

    private final DWConversionStats stats;

    public BallerinaVisitor(DWContext context, Context ctx, DWConversionStats dwConversionStats) {
        this.dwContext = context;
        this.ctx = ctx;
        this.stats = dwConversionStats;
    }

    @Override
    public Void visitScript(DataWeaveParser.ScriptContext ctx) {
        if (ctx.header() != null) {
            visit(ctx.header());
        }
        visit(ctx.body());
        return null;
    }

    @Override
    public Void visitDwVersion(DataWeaveParser.DwVersionContext ctx) {
        this.dwContext.currentScriptContext.dwVersion = ctx.NUMBER().getText();
        return null;
    }

    @Override
    public Void visitOutputDirective(DataWeaveParser.OutputDirectiveContext ctx) {
        TerminalNode mediaType = ctx.MEDIA_TYPE();
        this.dwContext.currentScriptContext.outputType = DWUtils.findBallerinaType(mediaType.getText());
        stats.record(DWConstruct.OUTPUT_DIRECTIVE, true);
        return null;
    }

    @Override
    public Void visitInputDirective(DataWeaveParser.InputDirectiveContext ctx) {
        this.dwContext.currentScriptContext.inputType = DWUtils.findBallerinaType(ctx.MEDIA_TYPE().getText());
        this.dwContext.currentScriptContext.params.add(new Parameter(ctx.IDENTIFIER().getText(),
                typeFrom(this.dwContext.currentScriptContext.inputType), Optional.empty()));
        stats.record(DWConstruct.INPUT_DIRECTIVE, true);
        return null;
    }

    @Override
    public Void visitNamespaceDirective(DataWeaveParser.NamespaceDirectiveContext ctx) {
        stats.record(DWConstruct.NAMESPACE_DIRECTIVE, false);
        dwContext.addUnsupportedComment(ctx.getText());
        return null;
    }

    @Override
    public Void visitVariableDeclaration(DataWeaveParser.VariableDeclarationContext ctx) {
        String expression = ctx.expression().getText();
        String dwType = DWUtils.getVarTypeFromExpression(expression);
        String ballerinaType = DWUtils.getBallerinaType(dwType, this.ctx);
        visit(ctx.expression());
        String valueExpr = this.dwContext.getExpression();
        ballerinaType = dwType.equals(DWUtils.NUMBER) ? refineNumberType(valueExpr, ballerinaType) : ballerinaType;
        String statement = ballerinaType + " " + ctx.IDENTIFIER().getText() + " " + ctx.ASSIGN().getText() + " "
                + valueExpr + ";";
        this.dwContext.currentScriptContext.statements.add(new BallerinaStatement(statement));
        stats.record(DWConstruct.VARIABLE_DECLARATION, true);
        return null;
    }

    @Override
    public Void visitFunctionDeclaration(DataWeaveParser.FunctionDeclarationContext ctx) {
        stats.record(DWConstruct.FUNCTION_DECLARATION, false);
        dwContext.addUnsupportedComment(ctx.getText());
        return null;
    }

    private String refineNumberType(String valueExpr, String ballerinaType) {
        if (valueExpr.contains(".")) {
            return "float";
        }
        return ballerinaType;
    }

    @Override
    public Void visitBody(DataWeaveParser.BodyContext ctx) {
        String methodName = String.format(DWUtils.DW_FUNCTION_NAME, this.ctx.projectCtx.counters.dwMethodCount++);
        visitChildren(ctx);
        dwContext.finalizeFunction();
        String outputType = dwContext.currentScriptContext.outputType;
        if (dwContext.currentScriptContext.containsCheck) {
            outputType = dwContext.currentScriptContext.outputType + "| error";
        }
        this.dwContext.functionNames.add(methodName);
        this.ctx.currentFileCtx.balConstructs.functions.add(new Function(methodName,
                dwContext.currentScriptContext.params, typeFrom(outputType),
                dwContext.currentScriptContext.statements));
        return null;
    }

    @Override
    public Void visitMultiKeyValueObject(DataWeaveParser.MultiKeyValueObjectContext ctx) {
        List<String> keyValuePairs = new ArrayList<>();
        for (var kv : ctx.keyValue()) {
            String key = "\"" + kv.IDENTIFIER().getText() + "\"";
            visit(kv.expression());
            String value = dwContext.getExpression();
            if (!isBasicType(dwContext.currentScriptContext.currentType)) {
                value += ".ensureType(" + dwContext.currentScriptContext.outputType + ")";
                if (!value.startsWith("check")) {
                    value = "check " + value;
                    this.dwContext.currentScriptContext.containsCheck = true;
                }
            }
            keyValuePairs.add(key + ": " + value);
        }
        dwContext.append("{ ").append(String.join(", ", keyValuePairs)).append(" }");
        dwContext.currentScriptContext.currentType = DWUtils.OBJECT;
        stats.record(DWConstruct.OBJECT, true);
        return null;
    }

    @Override
    public Void visitSingleKeyValueObject(DataWeaveParser.SingleKeyValueObjectContext ctx) {
        List<String> keyValuePairs = new ArrayList<>();
        DataWeaveParser.KeyValueContext kv = ctx.keyValue();
        String key = "\"" + kv.IDENTIFIER().getText() + "\"";
        visit(kv.expression());
        String value = dwContext.getExpression();
        if (!isBasicType(dwContext.currentScriptContext.currentType)) {
            value += ".ensureType(" + dwContext.currentScriptContext.outputType + ")";
            if (!value.startsWith("check")) {
                value = "check " + value;
                this.dwContext.currentScriptContext.containsCheck = true;
            }
        }
        keyValuePairs.add(key + ": " + value);
        dwContext.append("{ ").append(String.join(", ", keyValuePairs)).append(" }");
        dwContext.currentScriptContext.currentType = DWUtils.OBJECT;
        stats.record(DWConstruct.OBJECT, true);
        return null;
    }

    private boolean isBasicType(String currentType) {
        return currentType == null || currentType.equals(DWUtils.STRING) || currentType.equals(DWUtils.NUMBER) ||
                currentType.equals(DWUtils.BOOLEAN);
    }

    @Override
    public Void visitArray(DataWeaveParser.ArrayContext ctx) {
        List<String> elements = new ArrayList<>();
        for (var expr : ctx.expression()) {
            visit(expr);
            elements.add(dwContext.getExpression());
        }
        dwContext.append("[").append(String.join(", ", elements)).append("]");
        dwContext.currentScriptContext.currentType = DWUtils.ARRAY;
        stats.record(DWConstruct.ARRAY, true);
        return null;
    }

    @Override
    public Void visitLiteral(DataWeaveParser.LiteralContext ctx) {
        if (ctx.STRING() != null) {
            this.dwContext.currentScriptContext.currentType = DWUtils.STRING;
            String text = ctx.STRING().getText();
            if (text.startsWith("'")) {
                text = "\"" + text.substring(1, text.length() - 1) + "\"";
            }
            this.dwContext.append(text);
        } else if (ctx.NUMBER() != null) {
            this.dwContext.currentScriptContext.currentType = DWUtils.NUMBER;
            this.dwContext.append(ctx.NUMBER().getText());
        } else if (ctx.BOOLEAN() != null) {
            this.dwContext.currentScriptContext.currentType = DWUtils.BOOLEAN;
            this.dwContext.append(ctx.BOOLEAN().getText());
        } else if (ctx.DATE() != null) {
            this.dwContext.currentScriptContext.currentType = DWUtils.DATE;
            this.ctx.addImport(new Import(Constants.ORG_BALLERINA, Constants.MODULE_TIME));
            this.dwContext.currentScriptContext.containsCheck = true;
            this.dwContext.append("check time:civilFromString(");
            String dateString = ctx.DATE().getText();
            this.dwContext.append("\"" + dateString.substring(1, dateString.length() - 1) + "\"");
            this.dwContext.append(")");
        } else {
            this.dwContext.currentScriptContext.currentType = DWUtils.NULL;
            this.dwContext.append("()");
        }
        stats.record(DWConstruct.LITERAL, ctx.REGEX() == null);
        return null;
    }

    @Override
    public Void visitFunctionCall(DataWeaveParser.FunctionCallContext ctx) {
        List<String> arguments = new ArrayList<>();
        for (var expr : ctx.expression()) {
            visit(expr);
            arguments.add(dwContext.getExpression());
        }
        stats.record(DWConstruct.FUNCTION_CALL, true);
        return null;
    }

    @Override
    public Void visitTypeExpression(DataWeaveParser.TypeExpressionContext ctx) {
        stats.record(DWConstruct.TYPE_EXPRESSION, false);
        super.visitTypeExpression(ctx);
        dwContext.addUnsupportedComment(ctx.getText());
        return null;
    }

    @Override
    public Void visitSizeOfExpression(DataWeaveParser.SizeOfExpressionContext ctx) {
        visit(ctx.expression());
        boolean supported = Objects.equals(dwContext.currentScriptContext.inputType, LexerTerminals.JSON);
        if (supported) {
            String varName = DWUtils.VAR_PREFIX + varCount++;
            String castStatement = "var " + varName + " = " + dwContext.getExpression() + ";";
            dwContext.currentScriptContext.statements.add(new BallerinaStatement(castStatement));
            dwContext.append(varName).append(".length()");
            dwContext.currentScriptContext.currentType = DWUtils.NUMBER;
        } else {
            dwContext.addUnsupportedCommentWithType(ctx.getText(), dwContext.currentScriptContext.inputType);
        }
        stats.record(DWConstruct.SIZE_OF, supported);
        return null;
    }

    @Override
    public Void visitDefaultExpressionWrapper(DataWeaveParser.DefaultExpressionWrapperContext ctx) {
        if (ctx.defaultExpressionRest() == null ||
                ctx.defaultExpressionRest() instanceof DataWeaveParser.DefaultExpressionEndContext) {
            return visit(ctx.logicalOrExpression());
        }
        return visit(ctx.defaultExpressionRest());
    }

    @Override
    public Void visitMapExpression(DataWeaveParser.MapExpressionContext ctx) {
        boolean supported = Objects.equals(dwContext.currentScriptContext.inputType, LexerTerminals.JSON);
        if (supported) {
            visit((((DataWeaveParser.DefaultExpressionWrapperContext) ctx.getParent()).logicalOrExpression()));
            String varName = DWUtils.VAR_PREFIX + varCount++;
            String castStatement = "var " + varName + " = " + dwContext.getExpression() + ";";
            dwContext.currentScriptContext.varTypes.put(varName, "var");
            dwContext.currentScriptContext.varTypes.put(DWUtils.ELEMENT_ARG, "var");
            dwContext.currentScriptContext.varNames.put(DWUtils.DW_INDEX_IDENTIFIER, varName);
            dwContext.currentScriptContext.statements.add(
                    new BallerinaStatement(castStatement));
            dwContext.append(varName).append(".'map(");
            visit(ctx.implicitLambdaExpression());
            dwContext.append(")");
            if (ctx.defaultExpressionRest() != null
                    || !(ctx.defaultExpressionRest() instanceof DataWeaveParser.DefaultExpressionEndContext)) {
                visit(ctx.defaultExpressionRest());
            }
        } else {
            stats.record(DWConstruct.MAP, supported);
            dwContext.addUnsupportedCommentWithType(ctx.getText(), dwContext.currentScriptContext.inputType);
        }
        return null;
    }

    @Override
    public Void visitGroupByExpression(DataWeaveParser.GroupByExpressionContext ctx) {
        stats.record(DWConstruct.GROUP_BY, false);
        dwContext.addUnsupportedComment(ctx.getText());
        return null;
    }

    @Override
    public Void visitFilterExpression(DataWeaveParser.FilterExpressionContext ctx) {
        boolean supported = Objects.equals(dwContext.currentScriptContext.inputType, LexerTerminals.JSON);
        if (supported) {
            visit((((DataWeaveParser.DefaultExpressionWrapperContext) ctx.getParent()).logicalOrExpression()));
            String varName = DWUtils.VAR_PREFIX + varCount++;
            String expression = dwContext.getExpression();
            String castStatement;
            if (!this.dwContext.currentScriptContext.currentType.equals(DWUtils.ARRAY)) {
                castStatement = "json[] " + varName + " = check (" + expression + ").ensureType();";
                dwContext.currentScriptContext.containsCheck = true;
                dwContext.currentScriptContext.varTypes.put(varName, "json[]");
                dwContext.currentScriptContext.varTypes.put(DWUtils.ELEMENT_ARG, "json");
            } else {
                castStatement = "var " + varName + " = " + expression + ";";
                dwContext.currentScriptContext.varTypes.put(varName, "var");
                dwContext.currentScriptContext.varTypes.put(DWUtils.ELEMENT_ARG, "var");
            }
            dwContext.currentScriptContext.varNames.put(DWUtils.DW_INDEX_IDENTIFIER, varName);
            dwContext.currentScriptContext.statements.add(new BallerinaStatement(castStatement));
            dwContext.append(varName).append(".filter(");
            visit(ctx.implicitLambdaExpression());
            dwContext.append(")");
            if (ctx.defaultExpressionRest() != null
                    || !(ctx.defaultExpressionRest() instanceof DataWeaveParser.DefaultExpressionEndContext)) {
                visit(ctx.defaultExpressionRest());
            }
        } else {
            dwContext.addUnsupportedCommentWithType(ctx.getText(), dwContext.currentScriptContext.inputType);
            stats.record(DWConstruct.FILTER, supported);
        }
        return null;
    }

    @Override
    public Void visitReplaceExpression(DataWeaveParser.ReplaceExpressionContext ctx) {
        String regexVar = "_pattern_";
        String statement = "string:RegExp " + regexVar + " = re `" + ctx.REGEX().getText() + "`;";
        dwContext.currentScriptContext.statements.add(new BallerinaStatement(statement));
        visit((((DataWeaveParser.DefaultExpressionWrapperContext) ctx.getParent()).logicalOrExpression()));
        String varName = DWUtils.VAR_PREFIX + varCount++;
        String parameterStatement = "var " + varName + " = " + dwContext.getExpression() + ";";
        dwContext.currentScriptContext.statements.add(
                new BallerinaStatement(parameterStatement));
        dwContext.append(regexVar + ".replace(");
        dwContext.append(varName);
        dwContext.append(", ");
        visit(ctx.expression());
        dwContext.append(")");
        stats.record(DWConstruct.REPLACE, true);
        return null;
    }

    @Override
    public Void visitConcatExpression(DataWeaveParser.ConcatExpressionContext ctx) {
        visit((((DataWeaveParser.DefaultExpressionWrapperContext) ctx.getParent()).logicalOrExpression()));
        String leftExpr = dwContext.getExpression();
        String leftType = dwContext.currentScriptContext.currentType;
        visit(ctx.expression());
        String rightExpr = dwContext.getExpression();
        switch (dwContext.currentScriptContext.currentType) {
            case DWUtils.STRING:
                if (!leftType.equals(DWUtils.STRING)) {
                    leftExpr += ".toString()";
                }
                dwContext.append(leftExpr).append(" + ").append(rightExpr);
                break;
            case DWUtils.ARRAY:
                String leftArr = DWUtils.VAR_PREFIX + varCount++;
                String leftArrayStatement = "any[] " + leftArr + " = " + leftExpr + ";";
                dwContext.currentScriptContext.statements.add(
                        new BallerinaStatement(leftArrayStatement));
                String rightArr = DWUtils.VAR_PREFIX + varCount++;
                String rightArrayStatement = "var " + rightArr + " = " + rightExpr + ";";
                dwContext.currentScriptContext.statements.add(
                        new BallerinaStatement(rightArrayStatement));
                dwContext.append(leftArr).append(".push(...").append(rightArr).append(")");
                break;
            default:
                String leftMap = DWUtils.VAR_PREFIX + varCount++;
                String leftMapStatement = "var " + leftMap + " = " + leftExpr + ";";
                dwContext.currentScriptContext.statements.add(
                        new BallerinaStatement(leftMapStatement));
                dwContext.append(rightExpr.replaceFirst("\\{", "{ " + leftMap + ", "));
        }
        stats.record(DWConstruct.CONCAT, true);
        return null;
    }

    @Override
    public Void visitGrouped(DataWeaveParser.GroupedContext ctx) {
        stats.record(DWConstruct.GROUPED, true);
        return visit(ctx.expression());
    }

    @Override
    public Void visitLogicalOrExpression(DataWeaveParser.LogicalOrExpressionContext ctx) {
        if (ctx.logicalAndExpression().size() == 1) {
            return visit(ctx.logicalAndExpression(0));
        }
        visit(ctx.logicalAndExpression(0));
        StringBuilder statement = new StringBuilder(dwContext.getExpression());
        for (int i = 1; i < ctx.logicalAndExpression().size(); i++) {
            statement.append(" || ");
            visit(ctx.logicalAndExpression(i));
            statement.append(dwContext.getExpression());
        }
        this.dwContext.append(statement.toString());
        stats.record(DWConstruct.OR_OPERATOR, true);
        return null;
    }

    @Override
    public Void visitLogicalAndExpression(DataWeaveParser.LogicalAndExpressionContext ctx) {
        if (ctx.equalityExpression().size() == 1) {
            return visit(ctx.equalityExpression(0));
        }
        visit(ctx.equalityExpression(0));
        StringBuilder statement = new StringBuilder(dwContext.getExpression());
        for (int i = 1; i < ctx.equalityExpression().size(); i++) {
            statement.append(" && ");
            visit(ctx.equalityExpression(i));
            statement.append(dwContext.getExpression());
        }
        this.dwContext.append(statement.toString());
        stats.record(DWConstruct.AND_OPERATOR, true);
        return null;
    }

    @Override
    public Void visitImplicitLambdaExpression(DataWeaveParser.ImplicitLambdaExpressionContext ctx) {
        dwContext.append(DWUtils.ELEMENT_ARG).append("=>");
        String expr;
        if (ctx.inlineLambda() != null) {
            expr = ctx.inlineLambda().getText();
            if (expr.contains(DWUtils.DW_VALUE_IDENTIFIER)) {
                dwContext.commonArgs.put(DWUtils.DW_VALUE_IDENTIFIER, DWUtils.ELEMENT_ARG);
            }
            visit(ctx.inlineLambda());
        } else if (ctx.expression() != null) {
            expr = ctx.expression().getText();
            if (expr.contains(DWUtils.DW_VALUE_IDENTIFIER)) {
                dwContext.commonArgs.put(DWUtils.DW_VALUE_IDENTIFIER, DWUtils.ELEMENT_ARG);
            }
            visit(ctx.expression());
        }
        stats.record(DWConstruct.LAMBDA_EXPRESSION, true);
        return null;
    }

    @Override
    public Void visitInlineLambda(DataWeaveParser.InlineLambdaContext ctx) {
        List<TerminalNode> identifiers = ctx.functionParameters().IDENTIFIER();
        if (identifiers.size() == 1) {
            this.dwContext.currentScriptContext.varNames.put(identifiers.getFirst().getText(), DWUtils.ELEMENT_ARG);
        } else {
            this.dwContext.currentScriptContext.varNames.put(identifiers.getFirst().getText(), DWUtils.ELEMENT_ARG);
            this.dwContext.currentScriptContext.varNames.put(identifiers.get(1).getText(),
                    this.dwContext.currentScriptContext.varNames.get(DWUtils.DW_INDEX_IDENTIFIER) + ".indexOf(" +
                            DWUtils.ELEMENT_ARG + ")");
        }
        return visit(ctx.expression());
    }

    @Override
    public Void visitEqualityExpression(DataWeaveParser.EqualityExpressionContext ctx) {
        if (ctx.relationalExpression().size() == 1) {
            return visit(ctx.relationalExpression(0));
        }
        visit(ctx.relationalExpression(0));
        StringBuilder statement = new StringBuilder(dwContext.getExpression());
        for (int i = 1; i < ctx.relationalExpression().size(); i++) {
            statement.append(ctx.OPERATOR_EQUALITY(i - 1).getText());
            visit(ctx.relationalExpression(i));
            statement.append(dwContext.getExpression());
        }
        this.dwContext.append(statement.toString());
        stats.record(DWConstruct.EQUALITY_OPERATOR, true);
        return null;
    }

    @Override
    public Void visitRelationalExpression(DataWeaveParser.RelationalExpressionContext ctx) {
        if (ctx.additiveExpression().size() == 1) {
            return visit(ctx.additiveExpression(0));
        }
        visit(ctx.additiveExpression(0));
        StringBuilder statement = new StringBuilder(dwContext.getExpression());
        for (int i = 1; i < ctx.additiveExpression().size(); i++) {
            statement.append(ctx.OPERATOR_RELATIONAL(i - 1).getText());
            visit(ctx.additiveExpression(i));
            statement.append(dwContext.getExpression());
        }
        this.dwContext.append(statement.toString());
        stats.record(DWConstruct.RELATIONAL_OPERATOR, true);
        return null;
    }

    @Override
    public Void visitAdditiveExpression(DataWeaveParser.AdditiveExpressionContext ctx) {
        if (ctx.multiplicativeExpression().size() == 1) {
            return visit(ctx.multiplicativeExpression(0));
        }
        visit(ctx.multiplicativeExpression(0));
        StringBuilder expression = new StringBuilder(dwContext.getExpression());
        for (int i = 1; i < ctx.multiplicativeExpression().size(); i++) {
            String operator = ctx.OPERATOR_ADDITIVE(i - 1).getText();
            visit(ctx.multiplicativeExpression(i));
            String rightExpression = dwContext.getExpression();
            expression.append(" ").append(operator).append(" ").append(rightExpression);
        }
        dwContext.append(expression.toString());
        stats.record(DWConstruct.ADDITIVE_OPERATOR, true);
        return null;
    }

    @Override
    public Void visitMultiplicativeExpression(DataWeaveParser.MultiplicativeExpressionContext ctx) {
        if (ctx.typeCoercionExpression().size() == 1) {
            return visit(ctx.typeCoercionExpression(0));
        }
        visit(ctx.typeCoercionExpression(0));
        StringBuilder expression = new StringBuilder(dwContext.getExpression());
        for (int i = 1; i < ctx.typeCoercionExpression().size(); i++) {
            String operator = ctx.OPERATOR_MULTIPLICATIVE(i - 1).getText();
            visit(ctx.typeCoercionExpression(i));
            String rightExpression = dwContext.getExpression();
            expression.append(" ").append(operator).append(" ").append(rightExpression);
        }
        dwContext.append(expression.toString());
        stats.record(DWConstruct.MULTIPLICATIVE_OPERATOR, true);
        return null;
    }

    @Override
    public Void visitTypeCoercionExpression(DataWeaveParser.TypeCoercionExpressionContext ctx) {
        if (ctx.typeExpression() == null) {
            return visit(ctx.unaryExpression());
        }
        visit(ctx.unaryExpression());
        String type = ctx.typeExpression().IDENTIFIER().getText();
        String balType = DWUtils.getBallerinaType(type, this.ctx);
        boolean supported = balType.equals("map<anydata>") || type.equals("any");
        String expression = this.dwContext.getExpression();
        stats.record(DWConstruct.TYPE_COERCION, supported);
        switch (balType) {
            case "string":
                if (this.dwContext.currentScriptContext.currentType.equals(DWUtils.NUMBER)) {
                    if (ctx.formatOption() != null) {
                        if (!this.ctx.currentFileCtx.balConstructs.utilFunctions.contains(DWUtils.INT_TO_STRING)) {
                            this.ctx.addImport(new Import(Constants.ORG_BALLERINA, Constants.MODULE_JAVA));
                            this.ctx.currentFileCtx.balConstructs.utilFunctions.add(DWUtils.INT_TO_STRING);
                            this.ctx.currentFileCtx.balConstructs.functions.add(getIntToStringFunction());
                        }
                        this.dwContext.currentScriptContext.exprBuilder.append(DWUtils.INT_TO_STRING)
                                .append("(").append(expression).append(", ")
                                .append(ctx.formatOption().STRING().getText()).append(")");
                    } else {
                        this.dwContext.currentScriptContext.exprBuilder.append(expression).append(".toString()");
                    }
                } else if (this.dwContext.currentScriptContext.currentType.equals(DWUtils.IDENTIFIER)) {
                    if (ctx.formatOption() != null) {
                        if (expression.startsWith(DWUtils.GET_CURRENT_TIME_STRING)) {
                            if (!this.ctx.currentFileCtx.balConstructs.utilFunctions
                                    .contains(DWUtils.FORMAT_DATE_TIME_STRING)) {
                                this.ctx.currentFileCtx.balConstructs.functions
                                        .add(generateFormatDateTimeToStringFunctions());
                            }
                            this.dwContext.append(DWUtils.GET_FORMATTED_STRING_FROM_DATE).append("(")
                                    .append(expression).append(", ").append(ctx.formatOption().STRING().getText())
                                    .append(")");
                            return null;
                        }
                        this.dwContext.currentScriptContext.exprBuilder.append("string:join(").append(expression)
                                .append(", ").append(ctx.formatOption().STRING().getText()).append(")");
                    } else {
                        this.dwContext.currentScriptContext.exprBuilder.append(expression).append(".toString()");
                    }
                } else {
                    this.dwContext.currentScriptContext.exprBuilder.append(expression).append(".toString()");
                }
                break;
            case "int":
                if (this.dwContext.currentScriptContext.currentType.equals(DWUtils.STRING)) {
                    this.dwContext.currentScriptContext.exprBuilder.append("check int:fromString(")
                            .append(expression).append(")");
                    this.dwContext.currentScriptContext.containsCheck = true;
                } else if (this.dwContext.currentScriptContext.currentType.equals(DWUtils.DATE)) {
                    DataWeaveParser.FormatOptionContext formatOption = ctx.formatOption();
                    if (formatOption != null && formatOption.STRING().getText().equals(DWUtils.MILLISECONDS)) {
                        String utcStmt = "time:Utc " + DWUtils.UTC_VAR + " = check time:utcFromCivil(" +
                                expression + ");";
                        this.dwContext.currentScriptContext.containsCheck = true;
                        this.dwContext.currentScriptContext.statements.add(
                                new BallerinaStatement(utcStmt));
                        this.dwContext.append("(" + DWUtils.UTC_VAR + "[0] * 1000 + <int>(" + DWUtils.UTC_VAR +
                                "[1] * 1000))");
                    } else {
                        this.dwContext.append("(check time:utcFromCivil(").append(expression).append("))[0]");
                        this.dwContext.currentScriptContext.containsCheck = true;
                    }
                } else {
                    this.dwContext.currentScriptContext.exprBuilder.append(expression).append(".intValue()");
                }
                break;
            case "time:Civil":
                if (this.dwContext.currentScriptContext.currentType.equals(DWUtils.NUMBER)) {
                    this.dwContext.append("[").append(expression).append(", 0]");
                } else {
                    DataWeaveParser.FormatOptionContext formatOption = ctx.formatOption();
                    if (this.dwContext.currentScriptContext.currentType.equals(DWUtils.STRING) &&
                            formatOption != null) {
                        if (!this.ctx.currentFileCtx.balConstructs.utilFunctions
                                .contains(DWUtils.GET_DATE_FROM_FORMATTED_STRING)) {
                            generateFormatDateTimeToDateFunctions();
                        }
                        String dateFormat = formatOption.STRING().getText();
                        this.dwContext.append("check ").append(DWUtils.GET_DATE_FROM_FORMATTED_STRING).append("(")
                                .append(expression).append(", ").append(dateFormat).append(")");
                        this.dwContext.currentScriptContext.containsCheck = true;
                    }
                }
                break;
            default:
                this.dwContext.currentScriptContext.exprBuilder.append(".ensureType(").append(balType).append(")");
        }
        return null;
    }

    private void generateFormatDateTimeToDateFunctions() {
        this.ctx.addImport(new Import(Constants.ORG_BALLERINA, Constants.MODULE_JAVA));

        if (!ctx.currentFileCtx.balConstructs.utilFunctions.contains(DWUtils.GET_DATE_TIME_FORMATTER)) {
            generateGetDateTimeFormatter();
        }

        // create parseDateTime() function
        ctx.currentFileCtx.balConstructs.utilFunctions.add(DWUtils.PARSE_DATE_TIME);
        ExternFunctionBody body = new ExternFunctionBody("java.time.LocalDateTime",
                Optional.of("parse"), "@java:Method", Optional.of(List.of("java.lang.CharSequence",
                        "java.time.format.DateTimeFormatter")));
        ctx.currentFileCtx.balConstructs.functions.add(new Function(Optional.of("public"), DWUtils.PARSE_DATE_TIME,
                List.of(new Parameter("date", BAL_HANDLE_TYPE),
                        new Parameter("formatter", BAL_HANDLE_TYPE)),
                Optional.of(typeFrom(LexerTerminals.HANDLE)), body));

        // create toInstant() function
        ctx.currentFileCtx.balConstructs.utilFunctions.add(DWUtils.TO_INSTANT);
        body = new ExternFunctionBody("java.time.LocalDateTime",
                Optional.empty(), "@java:Method",
                Optional.of(List.of("java.time.ZoneOffset")));
        ctx.currentFileCtx.balConstructs.functions.add(new Function(Optional.of("public"), DWUtils.TO_INSTANT,
                List.of(new Parameter("localDateTime", BAL_HANDLE_TYPE),
                        new Parameter("zoneOffset", BAL_HANDLE_TYPE)),
                Optional.of(typeFrom(LexerTerminals.HANDLE)), body));

        // create utcZoneOffset() function
        ctx.currentFileCtx.balConstructs.utilFunctions.add(DWUtils.UTC_ZONE_OFFSET);
        body = new ExternFunctionBody("java.time.ZoneOffset",
                Optional.of("UTC"), "@java:FieldGet", Optional.empty());
        ctx.currentFileCtx.balConstructs.functions.add(new Function(Optional.of("public"), DWUtils.UTC_ZONE_OFFSET,
                List.of(), Optional.of(typeFrom(LexerTerminals.HANDLE)), body));

        // create dateFromFormattedString() function
        ctx.currentFileCtx.balConstructs.utilFunctions.add(DWUtils.GET_DATE_FROM_FORMATTED_STRING);
        Function dateFromFormattedString = getGetDateFromFormattedString();
        ctx.currentFileCtx.balConstructs.functions.add(dateFromFormattedString);
    }

    private static Function getGetDateFromFormattedString() {
        List<Parameter> params = new ArrayList<>();
        params.add(new Parameter("dateString", BAL_STRING_TYPE, Optional.empty()));
        params.add(new Parameter("format", BAL_STRING_TYPE, Optional.empty()));
        List<Statement> statements = new ArrayList<>();
        statements.add(new BallerinaStatement("handle localDateTime = " +
                "parseDateTime(java:fromString(dateString), getDateTimeFormatter(java:fromString(format)));"));
        statements.add(new BallerinaStatement("return check time:utcFromString(" +
                "toInstant(localDateTime, UTC()).toString());"));
        return new Function(Optional.of("public"),
                DWUtils.GET_DATE_FROM_FORMATTED_STRING, params, Optional.of(typeFrom("time:Utc|error")),
                new BlockFunctionBody(statements));
    }

    private Function generateFormatDateTimeToStringFunctions() {
        this.ctx.addImport(new Import(Constants.ORG_BALLERINA, Constants.MODULE_JAVA, Optional.empty()));

        // create formatDateTime() function
        ctx.currentFileCtx.balConstructs.utilFunctions.add(DWUtils.FORMAT_DATE_TIME);
        ExternFunctionBody body = new ExternFunctionBody("java.time.LocalDateTime",
                Optional.empty(), "@java:Method", Optional.empty());
        ctx.currentFileCtx.balConstructs.functions.add(new Function(Optional.of("public"), DWUtils.FORMAT_DATE_TIME,
                List.of(new Parameter("dateTime", BAL_HANDLE_TYPE),
                        new Parameter("formatter", BAL_HANDLE_TYPE)),
                Optional.of(typeFrom(LexerTerminals.HANDLE)), body));

        // create formatDateTimeString() function
        if (!ctx.currentFileCtx.balConstructs.utilFunctions.contains(DWUtils.GET_DATE_TIME_FORMATTER)) {
            generateGetDateTimeFormatter();
        }

        // create getZoneId() function
        ctx.currentFileCtx.balConstructs.utilFunctions.add(DWUtils.GET_ZONE_ID);
        body = new ExternFunctionBody("java.time.ZoneId",
                Optional.of("of"), "@java:Method", Optional.of(List.of("java.lang.String")));
        ctx.currentFileCtx.balConstructs.functions.add(new Function(Optional.of("public"), DWUtils.GET_ZONE_ID,
                List.of(new Parameter("zoneId", BAL_HANDLE_TYPE)),
                Optional.of(typeFrom(LexerTerminals.HANDLE)),
                body));

        // create getDateTime() function
        ctx.currentFileCtx.balConstructs.utilFunctions.add(DWUtils.GET_DATE_TIME);
        body = new ExternFunctionBody("java.time.LocalDateTime",
                Optional.of("ofInstant"), "@java:Method", Optional.of(List.of("java.time.Instant",
                        "java.time.ZoneId")));
        ctx.currentFileCtx.balConstructs.functions.add(new Function(Optional.of("public"), DWUtils.GET_DATE_TIME,
                List.of(new Parameter("instant", BAL_HANDLE_TYPE),
                        new Parameter("zoneId", BAL_HANDLE_TYPE)),
                Optional.of(typeFrom(LexerTerminals.HANDLE)), body));

        // create parseInstant() function
        ctx.currentFileCtx.balConstructs.utilFunctions.add(DWUtils.PARSE_INSTANT);
        body = new ExternFunctionBody("java.time.Instant",
                Optional.of("parse"), "@java:Method", Optional.empty());
        ctx.currentFileCtx.balConstructs.functions.add(new Function(Optional.of("public"), DWUtils.PARSE_INSTANT,
                List.of(new Parameter("instant", BAL_HANDLE_TYPE)),
                Optional.of(typeFrom(LexerTerminals.HANDLE)), body));

        // create getFormattedStringFromDate() function
        ctx.currentFileCtx.balConstructs.utilFunctions.add(DWUtils.GET_FORMATTED_STRING_FROM_DATE);
        List<Parameter> params = new ArrayList<>();
        params.add(new Parameter("dateString", BAL_STRING_TYPE));
        params.add(new Parameter("format", BAL_STRING_TYPE));
        List<Statement> statements = new ArrayList<>();
        statements.add(new BallerinaStatement("handle localDateTime = " +
                "getDateTime(parseInstant(java:fromString(dateString)), \n" +
                "    getZoneId(java:fromString(\"UTC\")));"));
        statements.add(new BallerinaStatement("return formatDateTime(localDateTime, " +
                "getDateTimeFormatter(java:fromString(format))).toString();"));
        return new Function(Optional.of("public"), DWUtils.GET_FORMATTED_STRING_FROM_DATE,
                params, Optional.of(typeFrom(LexerTerminals.STRING)), new BlockFunctionBody(statements));
    }

    private void generateGetDateTimeFormatter() {
        ctx.currentFileCtx.balConstructs.utilFunctions.add(DWUtils.GET_DATE_TIME_FORMATTER);
        ExternFunctionBody body = new ExternFunctionBody(
                "java.time.format.DateTimeFormatter",
                Optional.of("ofPattern"), "@java:Method", Optional.of(List.of("java.lang.String")));
        ctx.currentFileCtx.balConstructs.functions.add(new Function(Optional.of("public"),
                DWUtils.GET_DATE_TIME_FORMATTER,
                List.of(new Parameter("format", BAL_HANDLE_TYPE)),
                Optional.of(typeFrom(LexerTerminals.HANDLE)), body));
    }

    private Function getIntToStringFunction() {
        if (!ctx.currentFileCtx.balConstructs.utilFunctions.contains(DWUtils.NEW_DECIMAL_FORMAT)) {
            ctx.currentFileCtx.balConstructs.utilFunctions.add(DWUtils.NEW_DECIMAL_FORMAT);
            ExternFunctionBody body = new ExternFunctionBody("java.text.DecimalFormat",
                    Optional.empty(), "@java:Constructor", Optional.empty());
            ctx.currentFileCtx.balConstructs.functions.add(new Function(Optional.of("public"),
                    DWUtils.NEW_DECIMAL_FORMAT,
                    List.of(new Parameter("format", BAL_HANDLE_TYPE)),
                    Optional.of(typeFrom(LexerTerminals.HANDLE)), body));
        }
        if (!ctx.currentFileCtx.balConstructs.utilFunctions.contains(DWUtils.GET_FORMATTED_STRING_FROM_NUMBER)) {
            ctx.currentFileCtx.balConstructs.utilFunctions.add(DWUtils.GET_FORMATTED_STRING_FROM_NUMBER);
            ExternFunctionBody body = new ExternFunctionBody("java.text.NumberFormat",
                    Optional.of("format"), "@java:Method", Optional.of(List.of("long")));
            ctx.currentFileCtx.balConstructs.functions.add(new Function(Optional.of("public"),
                    DWUtils.GET_FORMATTED_STRING_FROM_NUMBER,
                    List.of(new Parameter("formatObject", BAL_HANDLE_TYPE),
                            new Parameter("value", BAL_INT_TYPE,
                                    Optional.empty())),
                    Optional.of(typeFrom(LexerTerminals.HANDLE)), body));
        }
        List<Parameter> params = new ArrayList<>();
        params.add(new Parameter("intValue", BAL_INT_TYPE));
        params.add(new Parameter("format", BAL_STRING_TYPE));
        List<Statement> statements = new ArrayList<>();
        statements.add(
                new BallerinaStatement("handle formatObj = " + DWUtils.NEW_DECIMAL_FORMAT +
                        "(java:fromString(format));"));
        statements.add(new BallerinaStatement("handle stringResult = " +
                DWUtils.GET_FORMATTED_STRING_FROM_NUMBER + "(formatObj, intValue);"));
        statements.add(new BallerinaStatement("return stringResult.toString();"));
        return new Function(Optional.of("public"), DWUtils.INT_TO_STRING, params,
                Optional.of(typeFrom(LexerTerminals.STRING)), new BlockFunctionBody(statements));
    }

    @Override
    public Void visitSingleValueSelector(DataWeaveParser.SingleValueSelectorContext ctx) {
        if (!Objects.equals(dwContext.currentScriptContext.inputType, LexerTerminals.JSON)) {
            dwContext.append("[").append(ctx.IDENTIFIER().getText()).append("]");
        } else {
            dwContext.append(".").append(ctx.IDENTIFIER().getText());
        }
        dwContext.addCheckExpr();
        stats.record(DWConstruct.SINGLE_VALUE_SELECTOR, true);
        return null;
    }

    @Override
    public Void visitMultiValueSelector(DataWeaveParser.MultiValueSelectorContext ctx) {
        stats.record(DWConstruct.MULTI_VALUE_SELECTOR, false);
        dwContext.addUnsupportedComment(ctx.getText());
        return null;
    }

    @Override
    public Void visitDescendantsSelector(DataWeaveParser.DescendantsSelectorContext ctx) {
        stats.record(DWConstruct.DESCENDANT_SELECTOR, false);
        dwContext.addUnsupportedComment(ctx.getText());
        return null;
    }

    @Override
    public Void visitIndexedSelector(DataWeaveParser.IndexedSelectorContext ctx) {
        stats.record(DWConstruct.INDEXED_SELECTOR, false);
        dwContext.addUnsupportedComment(ctx.getText());
        return null;
    }

    @Override
    public Void visitAttributeSelector(DataWeaveParser.AttributeSelectorContext ctx) {
        stats.record(DWConstruct.ATTRIBUTE_SELECTOR, false);
        dwContext.addUnsupportedComment(ctx.getText());
        return null;
    }

    @Override
    public Void visitExistenceQuerySelector(DataWeaveParser.ExistenceQuerySelectorContext ctx) {
        stats.record(DWConstruct.EXISTENCE_QUERY_SELECTOR, false);
        dwContext.addUnsupportedComment(ctx.getText());
        return null;
    }

    @Override
    public Void visitIdentifierExpression(DataWeaveParser.IdentifierExpressionContext ctx) {
        this.dwContext.currentScriptContext.currentType = DWUtils.IDENTIFIER;
        stats.record(DWConstruct.IDENTIFIER, true);
        String identifier = ctx.IDENTIFIER().getText();
        if (identifier.equals(DWUtils.DW_NOW_IDENTIFIER)) {
            if (!this.ctx.currentFileCtx.balConstructs.utilFunctions.contains(DWUtils.GET_CURRENT_TIME_STRING)) {
                this.ctx.currentFileCtx.balConstructs.imports.add(new Import(Constants.ORG_BALLERINA,
                        Constants.MODULE_TIME, Optional.empty()));
                this.ctx.currentFileCtx.balConstructs.utilFunctions.add(DWUtils.GET_CURRENT_TIME_STRING);
                this.ctx.currentFileCtx.balConstructs.functions.add(getCurrentTimeStringFunction());
            }
            this.dwContext.append(DWUtils.GET_CURRENT_TIME_STRING).append("()");
            return null;
        }
        if (this.dwContext.currentScriptContext.varNames.containsKey(identifier)) {
            this.dwContext.append(this.dwContext.currentScriptContext.varNames.get(identifier));
            return null;
        }
        this.dwContext.append(identifier);
        return null;
    }

    private Function getCurrentTimeStringFunction() {
        return new Function(Optional.of("public"), DWUtils.GET_CURRENT_TIME_STRING, new ArrayList<>(),
                Optional.of(typeFrom(LexerTerminals.STRING)), new BlockFunctionBody(List.of(
                        new BallerinaStatement("return time:utcToString(time:utcNow());"))));
    }

    @Override
    public Void visitValueIdentifierExpression(DataWeaveParser.ValueIdentifierExpressionContext ctx) {
        this.dwContext.append(this.dwContext.commonArgs.get(DWUtils.DW_VALUE_IDENTIFIER));
        stats.record(DWConstruct.IDENTIFIER, true);
        return null;
    }

    @Override
    public Void visitIndexIdentifierExpression(DataWeaveParser.IndexIdentifierExpressionContext ctx) {
        this.dwContext.append(this.dwContext.currentScriptContext.varNames.get(
                DWUtils.DW_INDEX_IDENTIFIER))
                .append(".indexOf(").append(DWUtils.ELEMENT_ARG).append(")");
        stats.record(DWConstruct.IDENTIFIER, true);
        return null;
    }

    @Override
    public Void visitUpperExpression(DataWeaveParser.UpperExpressionContext ctx) {
        visit(ctx.expression());
        this.dwContext.append(".toUpperAscii()");
        this.dwContext.currentScriptContext.currentType = DWUtils.STRING;
        stats.record(DWConstruct.UPPER, true);
        return null;
    }

    @Override
    public Void visitLowerExpression(DataWeaveParser.LowerExpressionContext ctx) {
        visit(ctx.expression());
        this.dwContext.append(".toLowerAscii()");
        this.dwContext.currentScriptContext.currentType = DWUtils.STRING;
        stats.record(DWConstruct.LOWER, true);
        return null;
    }

    @Override
    public Void visitWhenCondition(DataWeaveParser.WhenConditionContext ctx) {
        List<DataWeaveParser.DefaultExpressionContext> contexts = ctx.defaultExpression();
        dwContext.currentScriptContext.statements.add(buildWhenCondition(contexts));
        stats.record(DWConstruct.CONDITIONAL, true);
        return null;
    }

    private Statement.IfElseStatement buildWhenCondition(
            List<DataWeaveParser.DefaultExpressionContext> contexts) {
        int size = contexts.size();
        if (size < 3) {
            throw new RuntimeException("Invalid when condition");
        }
        IfStatementBuilder builder = new IfStatementBuilder();
        visit(contexts.get(1));
        builder.setIfCondition(new Expression.BallerinaExpression(dwContext.getExpression()));
        visit(contexts.get(0));
        String varName = DWUtils.VAR_PREFIX + varCount++;
        builder.resultVar = varName;
        dwContext.currentScriptContext.statements.add(new BallerinaStatement(
                dwContext.currentScriptContext.outputType + " " + varName + ";"));
        builder.addIfBody(
                new BallerinaStatement(varName + " = " + dwContext.getExpression() + ";"));
        if (size == 3) {
            visit(contexts.get(2));
            builder.addElseBody(new BallerinaStatement(varName + " = " +
                    dwContext.getExpression() + ";"));
            dwContext.append(" " + varName);
            return builder.getStatement();
        }
        for (int i = 2; i < size - 1; i += 2) {
            visit(contexts.get(i + 1));
            String condition = dwContext.getExpression();
            List<Statement> statements = new ArrayList<>();
            visit(contexts.get(i));
            statements.add(
                    new BallerinaStatement(varName + " = " + dwContext.getExpression() + ";"));
            builder.addElseIfClause(new Expression.BallerinaExpression(condition), statements);
        }
        visit(contexts.get(size - 1));
        builder.addElseBody(
                new BallerinaStatement(varName + " = " + dwContext.getExpression() + ";"));
        dwContext.append(" " + varName);
        return builder.getStatement();
    }

    @Override
    public Void visitUnlessCondition(DataWeaveParser.UnlessConditionContext ctx) {
        stats.record(DWConstruct.UNLESS, false);
        dwContext.addUnsupportedComment(ctx.getText());
        return null;
    }

    @Override
    public Void visitTerminal(TerminalNode terminalNode) {
        return null;
    }

    @Override
    public Void visitErrorNode(ErrorNode errorNode) {
        return null;
    }

}
