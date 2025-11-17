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
import io.ballerina.compiler.internal.parser.LexerTerminals;
import mule.common.DWConversionStats;
import mule.v4.Constants;
import mule.v4.Context;
import mule.v4.dataweave.converter.builder.IfStatementBuilder;
import mule.v4.dataweave.parser.DataWeaveBaseVisitor;
import mule.v4.dataweave.parser.DataWeaveParser;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static common.BallerinaModel.BlockFunctionBody;
import static common.BallerinaModel.Expression;
import static common.BallerinaModel.ExternFunctionBody;
import static common.BallerinaModel.Function;
import static common.BallerinaModel.Import;
import static common.BallerinaModel.Parameter;
import static common.BallerinaModel.Statement;
import static common.ConversionUtils.typeFrom;
import static mule.v4.Constants.ATTRIBUTES_FIELD_ACCESS;
import static mule.v4.Constants.BAL_HANDLE_TYPE;
import static mule.v4.Constants.BAL_INT_TYPE;
import static mule.v4.Constants.BAL_STRING_TYPE;
import static mule.v4.Constants.HTTP_REQUEST_REF;
import static mule.v4.Constants.HTTP_REQUEST_TYPE;
import static mule.v4.Constants.URI_PARAMS_REF;

public class BallerinaVisitor extends DataWeaveBaseVisitor<Void> {

    private final DWContext dwContext;
    private final Context ctx;
    private int varCount = 0;
    private final String namePrefix;
    private final Map<String, Integer> prefixCounters;

    private final DWConversionStats stats;

    public BallerinaVisitor(DWContext context, Context ctx, DWConversionStats dwConversionStats, String namePrefix,
                            Map<String, Integer> prefixCounters) {
        Objects.requireNonNull(namePrefix);
        assert !namePrefix.isEmpty();
        this.dwContext = context;
        this.ctx = ctx;
        this.stats = dwConversionStats;
        this.namePrefix = namePrefix;
        this.prefixCounters = prefixCounters;
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
    public Void visitImportDirective(DataWeaveParser.ImportDirectiveContext ctx) {
        stats.record(DWConstruct.NAMESPACE_DIRECTIVE, false);
        dwContext.addUnsupportedComment(ctx.getText());
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

    @Override
    public Void visitTypeDeclaration(DataWeaveParser.TypeDeclarationContext ctx) {
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
        int count = prefixCounters.getOrDefault(namePrefix, 0);
        String methodName = count == 0 ? namePrefix : namePrefix + count;
        prefixCounters.put(namePrefix, count + 1);
        visitChildren(ctx);
        dwContext.finalizeFunction();
        String outputType = dwContext.currentScriptContext.outputType;
        if (dwContext.currentScriptContext.containsCheck) {
            outputType = dwContext.currentScriptContext.outputType + "| error";
        }
        this.dwContext.functionNames.add(methodName);
        this.ctx.currentFileCtx.balConstructs.functions.add(new Function(methodName,
                Constants.FUNC_PARAMS_WITH_CONTEXT, typeFrom(outputType), dwContext.currentScriptContext.statements));
        return null;
    }

    @Override
    public Void visitMultiFieldObject(DataWeaveParser.MultiFieldObjectContext ctx) {
        List<String> keyValuePairs = new ArrayList<>();
        int checkCount = 0;
        for (var field : ctx.objectField()) {
            visit(field);
            String expr = dwContext.getExpression();
            if (expr.contains("check")) {
                checkCount++;
            }
            keyValuePairs.add(expr);
        }
        String delimiter = ",";
        if (checkCount > 1) {
            delimiter = delimiter + System.lineSeparator();
        }
        dwContext.append("{ ").append(String.join(delimiter, keyValuePairs)).append(" }");
        dwContext.currentScriptContext.currentType = DWUtils.OBJECT;
        stats.record(DWConstruct.OBJECT, true);
        return null;
    }

    @Override
    public Void visitSingleFieldObject(DataWeaveParser.SingleFieldObjectContext ctx) {
        visit(ctx.objectField());
        String keyValue = dwContext.getExpression();
        dwContext.append("{ ").append(keyValue).append(" }");
        dwContext.currentScriptContext.currentType = DWUtils.OBJECT;
        stats.record(DWConstruct.OBJECT, true);
        return null;
    }

    @Override
    public Void visitUnquotedKeyField(DataWeaveParser.UnquotedKeyFieldContext ctx) {
        String key = "\"" + ctx.IDENTIFIER().getText() + "\"";
        visit(ctx.expression());
        String value = dwContext.getExpression();
        if (!isBasicType(dwContext.currentScriptContext.currentType)) {
            value += ".ensureType(" + dwContext.currentScriptContext.outputType + ")";
            if (!value.startsWith("check")) {
                value = "check " + value;
                this.dwContext.currentScriptContext.containsCheck = true;
            }
        }
        dwContext.append(key + ": " + value);
        return null;
    }

    @Override
    public Void visitQuotedKeyField(DataWeaveParser.QuotedKeyFieldContext ctx) {
        String key = ctx.STRING().getText();
        visit(ctx.expression());
        String value = dwContext.getExpression();
        if (!isBasicType(dwContext.currentScriptContext.currentType)) {
            value += ".ensureType(" + dwContext.currentScriptContext.outputType + ")";
            if (!value.startsWith("check")) {
                value = "check " + value;
                this.dwContext.currentScriptContext.containsCheck = true;
            }
        }
        dwContext.append(key + ": " + value);
        return null;
    }

    @Override
    public Void visitDynamicKeyField(DataWeaveParser.DynamicKeyFieldContext ctx) {
        List<DataWeaveParser.ExpressionContext> expressions = ctx.expression();
        visit(expressions.get(0));
        String key = dwContext.getExpression();
        visit(expressions.get(1));
        String value = dwContext.getExpression();
        if (!isBasicType(dwContext.currentScriptContext.currentType)) {
            value += ".ensureType(" + dwContext.currentScriptContext.outputType + ")";
            if (!value.startsWith("check")) {
                value = "check " + value;
                this.dwContext.currentScriptContext.containsCheck = true;
            }
        }
        dwContext.append("[" + key + "]: " + value);
        return null;
    }

    private boolean isBasicType(String currentType) {
        return currentType == null || currentType.equals(DWUtils.STRING) || currentType.equals(DWUtils.NUMBER) ||
                currentType.equals(DWUtils.BOOLEAN) || currentType.equals(DWUtils.PAYLOAD);
    }

    @Override
    public Void visitArrayExpression(DataWeaveParser.ArrayExpressionContext ctx) {
        return visit(ctx.array());
    }

    @Override
    public Void visitSelectorExpressionWrapperWithDefault(
            DataWeaveParser.SelectorExpressionWrapperWithDefaultContext ctx) {
        dwContext.inDefaultAccess = true;
        boolean headerAttributeAccess = isHeaderAttributeAccess(ctx.primaryExpression());
        boolean uriParamAttributeAccess = isUriParamAttributeAccess(ctx.primaryExpression());
        boolean queryParamAttributeAccess = isQueryParamAttributeAccess(ctx.primaryExpression());
        StringBuilder exprBuilder = this.dwContext.currentScriptContext.exprBuilder;
        if (headerAttributeAccess) {
            handleAttributeHeaderAccess(ctx.primaryExpression(), ctx.selectorExpression());
        } else if (uriParamAttributeAccess) {
            handleAttributeUriParamAccess(ctx.primaryExpression(), ctx.selectorExpression());
        } else if (queryParamAttributeAccess) {
            handleAttributeQueryParamAccess(ctx.primaryExpression(), ctx.selectorExpression());
        } else {
            visit(ctx.primaryExpression());
            visit(ctx.selectorExpression());
        }
        if (headerAttributeAccess | uriParamAttributeAccess) {
            dwContext.append(" : ");
        } else {
            dwContext.append(" ?: ");
        }
        visit(ctx.expression());
        if (headerAttributeAccess | uriParamAttributeAccess) {
            exprBuilder.append(")");
        }
        dwContext.inDefaultAccess = false;
        return null;
    }

    private static boolean isHeaderAttributeAccess(DataWeaveParser.PrimaryExpressionContext primaryExpression) {
        return primaryExpression.getText().equals("attributes.headers");
    }

    private static boolean isUriParamAttributeAccess(DataWeaveParser.PrimaryExpressionContext primaryExpression) {
        return primaryExpression.getText().equals("attributes.uriParams");
    }

    private static boolean isQueryParamAttributeAccess(DataWeaveParser.PrimaryExpressionContext primaryExpression) {
        return primaryExpression.getText().equals("attributes.queryParams");
    }

    private void handleAttributeHeaderAccess(DataWeaveParser.PrimaryExpressionContext primaryExpression,
                                             DataWeaveParser.SelectorExpressionContext selectorExpression) {
        var exprBuilder = this.dwContext.currentScriptContext.exprBuilder;
        exprBuilder.append("(");
        contextRequestMethodCall(selectorExpression, "hasHeader");
        exprBuilder.append(" ? ");
        contextRequestMethodCall(selectorExpression, "getHeader");
    }

    private void contextRequestMethodCall(DataWeaveParser.SelectorExpressionContext selectorExpression,
                                          String methodName) {
        var exprBuilder = this.dwContext.currentScriptContext.exprBuilder;
        exprBuilder.append("(<").append(HTTP_REQUEST_TYPE).append(">").append(ATTRIBUTES_FIELD_ACCESS).append(".")
                .append(HTTP_REQUEST_REF).append(").").append(methodName).append("(\"");
        this.dwContext.inKeyAccess = true;
        visit(selectorExpression);
        this.dwContext.inKeyAccess = false;
        exprBuilder.append("\")");
    }

    private void handleAttributeQueryParamAccess(DataWeaveParser.PrimaryExpressionContext primaryExpression,
                                                 DataWeaveParser.SelectorExpressionContext selectorExpression) {
        var exprBuilder = this.dwContext.currentScriptContext.exprBuilder;
        contextRequestMethodCall(selectorExpression, "getQueryParamValue");
    }

    private void handleAttributeUriParamAccess(DataWeaveParser.PrimaryExpressionContext primaryExpression,
                                               DataWeaveParser.SelectorExpressionContext selectorExpression) {
        var exprBuilder = this.dwContext.currentScriptContext.exprBuilder;
        exprBuilder.append("(");
        attributeAttributeUriParamAccess(selectorExpression, "hasKey");
        exprBuilder.append(" ? ");
        attributeAttributeUriParamAccess(selectorExpression, "get");
    }

    private void attributeAttributeUriParamAccess(DataWeaveParser.SelectorExpressionContext selectorExpression,
                                                  String methodName) {
        var exprBuilder = this.dwContext.currentScriptContext.exprBuilder;
        exprBuilder.append(ATTRIBUTES_FIELD_ACCESS).append(".").append(URI_PARAMS_REF).append(".").append(methodName)
                .append("(\"");
        this.dwContext.inKeyAccess = true;
        visit(selectorExpression);
        this.dwContext.inKeyAccess = false;
        exprBuilder.append("\")");
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
    public Void visitLiteralExpression(DataWeaveParser.LiteralExpressionContext ctx) {
        return visit(ctx.literal());
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
        } else if (ctx.REGEX() != null) {
            this.dwContext.currentScriptContext.currentType = DWUtils.STRING;
            stats.record(DWConstruct.LITERAL, false);
            dwContext.addUnsupportedComment(ctx.getText());
            return null;
        } else {
            this.dwContext.currentScriptContext.currentType = DWUtils.NULL;
            this.dwContext.append("()");
        }
        stats.record(DWConstruct.LITERAL, true);
        return null;
    }

    @Override
    public Void visitFunctionCall(DataWeaveParser.FunctionCallContext ctx) {
        String functionName = ctx.IDENTIFIER().getText();
        List<String> arguments = new ArrayList<>();
        for (var expr : ctx.expression()) {
            visit(expr);
            arguments.add(dwContext.getExpression());
        }
        dwContext.append(functionName).append("(").append(String.join(", ", arguments)).append(")");
        stats.record(DWConstruct.FUNCTION_CALL, true);
        return null;
    }

    @Override
    public Void visitNamedType(DataWeaveParser.NamedTypeContext ctx) {
        this.dwContext.append(ctx.IDENTIFIER().getText());
        return null;
    }

    @Override
    public Void visitStringType(DataWeaveParser.StringTypeContext ctx) {
        this.dwContext.append("String");
        return null;
    }

    @Override
    public Void visitNumberType(DataWeaveParser.NumberTypeContext ctx) {
        this.dwContext.append("Number");
        return null;
    }

    @Override
    public Void visitBooleanType(DataWeaveParser.BooleanTypeContext ctx) {
        this.dwContext.append("Boolean");
        return null;
    }

    @Override
    public Void visitDateTimeType(DataWeaveParser.DateTimeTypeContext ctx) {
        this.dwContext.append("DateTime");
        return null;
    }

    @Override
    public Void visitLocalDateTimeType(DataWeaveParser.LocalDateTimeTypeContext ctx) {
        this.dwContext.append("LocalDateTime");
        return null;
    }

    @Override
    public Void visitDateType(DataWeaveParser.DateTypeContext ctx) {
        this.dwContext.append("Date");
        return null;
    }

    @Override
    public Void visitTimeType(DataWeaveParser.TimeTypeContext ctx) {
        this.dwContext.append("Time");
        return null;
    }

    @Override
    public Void visitObjectType(DataWeaveParser.ObjectTypeContext ctx) {
        this.dwContext.append("Object");
        return null;
    }

    @Override
    public Void visitAnyType(DataWeaveParser.AnyTypeContext ctx) {
        this.dwContext.append("Any");
        return null;
    }

    @Override
    public Void visitSizeOfExpression(DataWeaveParser.SizeOfExpressionContext ctx) {
        visit(ctx.expression());
        String varName = DWUtils.VAR_PREFIX + varCount++;
        String castStatement = "var " + varName + " = " + dwContext.getExpression() + ";";
        dwContext.currentScriptContext.statements.add(new BallerinaStatement(castStatement));
        dwContext.append(varName).append(".length()");
        dwContext.currentScriptContext.currentType = DWUtils.NUMBER;
        stats.record(DWConstruct.SIZE_OF, true);
        return null;
    }

    @Override
    public Void visitSizeOfExpressionWithParentheses(DataWeaveParser.SizeOfExpressionWithParenthesesContext ctx) {
        visit(ctx.expression());
        String varName = DWUtils.VAR_PREFIX + varCount++;
        String castStatement = "var " + varName + " = " + dwContext.getExpression() + ";";
        dwContext.currentScriptContext.statements.add(new BallerinaStatement(castStatement));
        dwContext.append(varName).append(".length()");
        dwContext.currentScriptContext.currentType = DWUtils.NUMBER;
        stats.record(DWConstruct.SIZE_OF, true);
        return null;
    }

    @Override
    public Void visitMapExpression(DataWeaveParser.MapExpressionContext ctx) {
        visit(ctx.operationExpression());
        String varName = DWUtils.VAR_PREFIX + varCount++;
        String castStatement = "var " + varName + " = " + dwContext.getExpression() + ";";
        dwContext.currentScriptContext.varTypes.put(varName, "var");
        dwContext.currentScriptContext.varTypes.put(DWUtils.ELEMENT_ARG, "var");
        dwContext.currentScriptContext.varNames.put(DWUtils.DW_INDEX_IDENTIFIER, varName);
        dwContext.currentScriptContext.statements.add(new BallerinaStatement(castStatement));
        dwContext.append(varName).append(".'map(");
        visit(ctx.implicitLambdaExpression());
        dwContext.append(")");
        stats.record(DWConstruct.MAP, true);
        return null;
    }

    @Override
    public Void visitOperationExpressionWrapper(DataWeaveParser.OperationExpressionWrapperContext ctx) {
        return visit(ctx.logicalOrExpression());
    }

    @Override
    public Void visitFilterExpression(DataWeaveParser.FilterExpressionContext ctx) {
        visit(ctx.operationExpression());
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
        stats.record(DWConstruct.FILTER, true);
        return null;
    }

    @Override
    public Void visitReplaceExpression(DataWeaveParser.ReplaceExpressionContext ctx) {
        String regexVar = "_pattern_";
        String statement = "string:RegExp " + regexVar + " = re `" + ctx.REGEX().getText() + "`;";
        dwContext.currentScriptContext.statements.add(new BallerinaStatement(statement));
        visit(ctx.operationExpression());
        String varName = DWUtils.VAR_PREFIX + varCount++;
        String parameterStatement = "var " + varName + " = " + dwContext.getExpression() + ";";
        dwContext.currentScriptContext.statements.add(new BallerinaStatement(parameterStatement));
        dwContext.append(regexVar + ".replace(");
        dwContext.append(varName);
        dwContext.append(", ");
        visit(ctx.expression());
        dwContext.append(")");
        stats.record(DWConstruct.REPLACE, true);
        return null;
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
    public Void visitConcatExpression(DataWeaveParser.ConcatExpressionContext ctx) {
        visit((ctx.operationExpression()));
        String leftExpr = dwContext.getExpression();
        String leftType = dwContext.currentScriptContext.currentType;
        visit(ctx.logicalOrExpression());
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
                dwContext.currentScriptContext.statements.add(new BallerinaStatement(leftArrayStatement));
                String rightArr = DWUtils.VAR_PREFIX + varCount++;
                String rightArrayStatement = "var " + rightArr + " = " + rightExpr + ";";
                dwContext.currentScriptContext.statements.add(new BallerinaStatement(rightArrayStatement));
                dwContext.append(leftArr).append(".push(...").append(rightArr).append(")");
                break;
            default:
                String leftMap = DWUtils.VAR_PREFIX + varCount++;
                String leftMapStatement = "var " + leftMap + " = " + leftExpr + ";";
                dwContext.currentScriptContext.statements.add(new BallerinaStatement(leftMapStatement));
                dwContext.append(rightExpr.replaceFirst("\\{", "{ " + leftMap + ", "));
        }
        stats.record(DWConstruct.CONCAT, true);
        return null;
    }

    @Override
    public Void visitLambdaExpression(DataWeaveParser.LambdaExpressionContext ctx) {
        return visit(ctx.inlineLambda());
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
            statement.append(" ").append(ctx.OPERATOR_EQUALITY(i - 1).getText()).append(" ");
            visit(ctx.relationalExpression(i));
            statement.append(dwContext.getExpression());
        }
        this.dwContext.append(statement.toString());
        stats.record(DWConstruct.EQUALITY_OPERATOR, true);
        return null;
    }

    @Override
    public Void visitRelationalComparison(DataWeaveParser.RelationalComparisonContext ctx) {
        if (ctx.additiveExpression().size() == 1) {
            return visit(ctx.additiveExpression(0));
        }
        visit(ctx.additiveExpression(0));
        StringBuilder statement = new StringBuilder(dwContext.getExpression());
        for (int i = 1; i < ctx.additiveExpression().size(); i++) {
            statement.append(" ").append(ctx.OPERATOR_RELATIONAL(i - 1).getText()).append(" ");
            visit(ctx.additiveExpression(i));
            statement.append(dwContext.getExpression());
        }
        this.dwContext.append(statement.toString());
        stats.record(DWConstruct.RELATIONAL_OPERATOR, true);
        return null;
    }

    @Override
    public Void visitIsExpression(DataWeaveParser.IsExpressionContext ctx) {
        visit(ctx.additiveExpression());
        String expression = dwContext.getExpression();
        visit(ctx.typeExpression());
        dwContext.append(expression).append(" is ").append(dwContext.getExpression());
        stats.record(DWConstruct.TYPE_EXPRESSION, false);
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
        String type = ctx.typeExpression().getText();
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
                        this.dwContext.currentScriptContext.statements.add(new BallerinaStatement(utcStmt));
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
                    this.dwContext.append("time:utcToString([%s,0])".formatted(expression));
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
                                .append(expression).append(", " + dateFormat + ")");
                        this.dwContext.currentScriptContext.containsCheck = true;
                    }
                }
                break;
            default:
                this.dwContext.currentScriptContext.exprBuilder.append(".ensureType(").append(balType).append(")");
        }
        return null;
    }

    @Override
    public Void visitUpperExpression(DataWeaveParser.UpperExpressionContext ctx) {
        visit(ctx.expression());
        if (this.dwContext.currentScriptContext.exprBuilder.toString().startsWith("check payload")) {
            this.dwContext.currentScriptContext.exprBuilder.insert(0, "(");
            this.dwContext.append(").toString()");
        }
        this.dwContext.append(".toUpperAscii()");
        this.dwContext.currentScriptContext.currentType = DWUtils.STRING;
        stats.record(DWConstruct.UPPER, true);
        return null;
    }

    @Override
    public Void visitUpperExpressionWithParentheses(DataWeaveParser.UpperExpressionWithParenthesesContext ctx) {
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
    public Void visitLowerExpressionWithParentheses(DataWeaveParser.LowerExpressionWithParenthesesContext ctx) {
        visit(ctx.expression());
        this.dwContext.append(".toLowerAscii()");
        this.dwContext.currentScriptContext.currentType = DWUtils.STRING;
        stats.record(DWConstruct.LOWER, true);
        return null;
    }

    @Override
    public Void visitNotExpression(DataWeaveParser.NotExpressionContext ctx) {
        visit(ctx.expression());
        this.dwContext.append("!(" + dwContext.getExpression() + ")");
        stats.record(DWConstruct.LITERAL, true);
        return null;
    }

    @Override
    public Void visitNegativeExpression(DataWeaveParser.NegativeExpressionContext ctx) {
        visit(ctx.expression());
        this.dwContext.append("-" + dwContext.getExpression());
        stats.record(DWConstruct.LITERAL, true);
        return null;
    }

    @Override
    public Void visitPrimaryExpressionWrapper(DataWeaveParser.PrimaryExpressionWrapperContext ctx) {
        return visit(ctx.primaryExpression());
    }

    @Override
    public Void visitBuiltInFunctionExpression(DataWeaveParser.BuiltInFunctionExpressionContext ctx) {
        return visit(ctx.builtInFunction());
    }

    @Override
    public Void visitNowFunction(DataWeaveParser.NowFunctionContext ctx) {
        this.dwContext.currentScriptContext.currentType = DWUtils.IDENTIFIER;
        if (!this.ctx.currentFileCtx.balConstructs.utilFunctions.contains(DWUtils.GET_CURRENT_TIME_STRING)) {
            this.ctx.currentFileCtx.balConstructs.imports.add(new Import(Constants.ORG_BALLERINA,
                    Constants.MODULE_TIME, Optional.empty()));
            this.ctx.currentFileCtx.balConstructs.utilFunctions.add(DWUtils.GET_CURRENT_TIME_STRING);
            this.ctx.currentFileCtx.balConstructs.functions.add(getCurrentTimeStringFunction());
        }
        this.dwContext.append(DWUtils.GET_CURRENT_TIME_STRING).append("()");
        stats.record(DWConstruct.FUNCTION_CALL, true);
        return null;
    }

    @Override
    public Void visitSelectorExpressionWrapper(DataWeaveParser.SelectorExpressionWrapperContext ctx) {
        boolean headerAttributeAccess = isHeaderAttributeAccess(ctx.primaryExpression());
        boolean uriParamAttributeAccess = isUriParamAttributeAccess(ctx.primaryExpression());
        boolean queryParamAttributeAccess = isQueryParamAttributeAccess(ctx.primaryExpression());
        if (headerAttributeAccess) {
            StringBuilder exprBuilder = this.dwContext.currentScriptContext.exprBuilder;
            handleAttributeHeaderAccess(ctx.primaryExpression(), ctx.selectorExpression());
            exprBuilder.append(": error(\"no such header\"))");
        } else if (uriParamAttributeAccess) {
            StringBuilder exprBuilder = this.dwContext.currentScriptContext.exprBuilder;
            handleAttributeUriParamAccess(ctx.primaryExpression(), ctx.selectorExpression());
            exprBuilder.append(": error(\"no such uri param\"))");
        } else if (queryParamAttributeAccess) {
            handleAttributeQueryParamAccess(ctx.primaryExpression(), ctx.selectorExpression());
        } else {
            visit(ctx.primaryExpression());
            visit(ctx.selectorExpression());
        }
        return null;
    }

    @Override
    public Void visitSingleValueSelector(DataWeaveParser.SingleValueSelectorContext ctx) {
        String accessOp;
        if (dwContext.inKeyAccess) {
            accessOp = "";
        } else if (dwContext.inDefaultAccess) {
            accessOp = "?.";
        } else {
            accessOp = ".";
        }
        dwContext.append(accessOp).append(ctx.IDENTIFIER().getText());
        dwContext.addCheckExpr();
        stats.record(DWConstruct.SINGLE_VALUE_SELECTOR, true);
        return null;
    }

    @Override
    public Void visitKeySelector(DataWeaveParser.KeySelectorContext ctx) {
        assert dwContext.inKeyAccess;
        dwContext.append(ctx.STRING().getText());
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
        dwContext.append("[");
        visit(ctx.expression());
        dwContext.append("]");
        stats.record(DWConstruct.INDEXED_SELECTOR, true);
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
        if (identifier.equals(DWUtils.DW_PAYLOAD_IDENTIFIER)) {
            this.dwContext.currentScriptContext.currentType = DWUtils.PAYLOAD;
            this.dwContext.referringToPayload = true;
        }
        this.dwContext.append(identifier);
        return null;
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
    public Void visitIfElseCondition(DataWeaveParser.IfElseConditionContext ctx) {
        List<DataWeaveParser.LogicalOrExpressionContext> expressions = ctx.logicalOrExpression();

        if (expressions.size() < 2) {
            throw new RuntimeException("Invalid if condition structure - need at least condition and result");
        }

        IfStatementBuilder builder = new IfStatementBuilder();
        String varName = DWUtils.VAR_PREFIX + varCount++;
        builder.resultVar = varName;

        dwContext.currentScriptContext.statements.add(new BallerinaStatement(
                dwContext.currentScriptContext.outputType + " " + varName + ";"));

        // First IF: condition and result
        visit(expressions.get(0)); // IF condition
        String ifCondition = dwContext.getExpression();
        visit(expressions.get(1)); // IF result
        String ifResult = dwContext.getExpression();

        builder.setIfCondition(new Expression.BallerinaExpression(ifCondition));
        builder.addIfBody(new BallerinaStatement(varName + " = " + ifResult + ";"));

        // Process ELSE IF clauses (pairs of condition and result)
        int i = 2;
        while (i < expressions.size() - 1) {
            visit(expressions.get(i)); // ELSE IF condition
            String elseIfCondition = dwContext.getExpression();
            visit(expressions.get(i + 1)); // ELSE IF result
            String elseIfResult = dwContext.getExpression();

            List<Statement> statements = new ArrayList<>();
            statements.add(new BallerinaStatement(varName + " = " + elseIfResult + ";"));
            builder.addElseIfClause(new Expression.BallerinaExpression(elseIfCondition), statements);

            i += 2;
        }

        // Final ELSE clause (if present)
        if (i == expressions.size() - 1) {
            visit(expressions.get(i)); // ELSE result
            builder.addElseBody(new BallerinaStatement(varName + " = " + dwContext.getExpression() + ";"));
        }

        dwContext.currentScriptContext.statements.add(builder.getStatement());
        dwContext.append(" " + varName);
        stats.record(DWConstruct.CONDITIONAL, true);
        return null;
    }

    // Helper methods
    private void generateFormatDateTimeToDateFunctions() {
        this.ctx.addImport(new Import(Constants.ORG_BALLERINA, Constants.MODULE_JAVA));

        if (!ctx.currentFileCtx.balConstructs.utilFunctions.contains(DWUtils.GET_DATE_TIME_FORMATTER)) {
            generateGetDateTimeFormatter();
        }

        ctx.currentFileCtx.balConstructs.utilFunctions.add(DWUtils.PARSE_DATE_TIME);
        ExternFunctionBody body = new ExternFunctionBody("java.time.LocalDateTime",
                Optional.of("parse"), "@java:Method", Optional.of(List.of("java.lang.CharSequence",
                        "java.time.format.DateTimeFormatter")));
        ctx.currentFileCtx.balConstructs.functions.add(new Function(Optional.of("public"), DWUtils.PARSE_DATE_TIME,
                List.of(new Parameter("date", BAL_HANDLE_TYPE),
                        new Parameter("formatter", BAL_HANDLE_TYPE)),
                Optional.of(typeFrom(LexerTerminals.HANDLE)), body));

        ctx.currentFileCtx.balConstructs.utilFunctions.add(DWUtils.TO_INSTANT);
        body = new ExternFunctionBody("java.time.LocalDateTime",
                Optional.empty(), "@java:Method",
                Optional.of(List.of("java.time.ZoneOffset")));
        ctx.currentFileCtx.balConstructs.functions.add(new Function(Optional.of("public"), DWUtils.TO_INSTANT,
                List.of(new Parameter("localDateTime", BAL_HANDLE_TYPE),
                        new Parameter("zoneOffset", BAL_HANDLE_TYPE)),
                Optional.of(typeFrom(LexerTerminals.HANDLE)), body));

        ctx.currentFileCtx.balConstructs.utilFunctions.add(DWUtils.UTC_ZONE_OFFSET);
        body = new ExternFunctionBody("java.time.ZoneOffset",
                Optional.of("UTC"), "@java:FieldGet", Optional.empty());
        ctx.currentFileCtx.balConstructs.functions.add(new Function(Optional.of("public"), DWUtils.UTC_ZONE_OFFSET,
                List.of(), Optional.of(typeFrom(LexerTerminals.HANDLE)), body));

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

        ctx.currentFileCtx.balConstructs.utilFunctions.add(DWUtils.FORMAT_DATE_TIME);
        ExternFunctionBody body = new ExternFunctionBody("java.time.LocalDateTime",
                Optional.empty(), "@java:Method", Optional.empty());
        ctx.currentFileCtx.balConstructs.functions.add(new Function(Optional.of("public"), DWUtils.FORMAT_DATE_TIME,
                List.of(new Parameter("dateTime", BAL_HANDLE_TYPE),
                        new Parameter("formatter", BAL_HANDLE_TYPE)),
                Optional.of(typeFrom(LexerTerminals.HANDLE)), body));

        if (!ctx.currentFileCtx.balConstructs.utilFunctions.contains(DWUtils.GET_DATE_TIME_FORMATTER)) {
            generateGetDateTimeFormatter();
        }

        ctx.currentFileCtx.balConstructs.utilFunctions.add(DWUtils.GET_ZONE_ID);
        body = new ExternFunctionBody("java.time.ZoneId",
                Optional.of("of"), "@java:Method", Optional.of(List.of("java.lang.String")));
        ctx.currentFileCtx.balConstructs.functions.add(new Function(Optional.of("public"), DWUtils.GET_ZONE_ID,
                List.of(new Parameter("zoneId", BAL_HANDLE_TYPE)),
                Optional.of(typeFrom(LexerTerminals.HANDLE)),
                body));

        ctx.currentFileCtx.balConstructs.utilFunctions.add(DWUtils.GET_DATE_TIME);
        body = new ExternFunctionBody("java.time.LocalDateTime",
                Optional.of("ofInstant"), "@java:Method", Optional.of(List.of("java.time.Instant",
                        "java.time.ZoneId")));
        ctx.currentFileCtx.balConstructs.functions.add(new Function(Optional.of("public"), DWUtils.GET_DATE_TIME,
                List.of(new Parameter("instant", BAL_HANDLE_TYPE),
                        new Parameter("zoneId", BAL_HANDLE_TYPE)),
                Optional.of(typeFrom(LexerTerminals.HANDLE)), body));

        ctx.currentFileCtx.balConstructs.utilFunctions.add(DWUtils.PARSE_INSTANT);
        body = new ExternFunctionBody("java.time.Instant",
                Optional.of("parse"), "@java:Method", Optional.empty());
        ctx.currentFileCtx.balConstructs.functions.add(new Function(Optional.of("public"), DWUtils.PARSE_INSTANT,
                List.of(new Parameter("instant", BAL_HANDLE_TYPE)),
                Optional.of(typeFrom(LexerTerminals.HANDLE)), body));

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

    private Function getCurrentTimeStringFunction() {
        return new Function(Optional.of("public"), DWUtils.GET_CURRENT_TIME_STRING, new ArrayList<>(),
                Optional.of(typeFrom(LexerTerminals.STRING)), new BlockFunctionBody(List.of(
                        new BallerinaStatement("return time:utcToString(time:utcNow());"))));
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
