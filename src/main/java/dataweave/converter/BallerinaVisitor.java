package dataweave.converter;

import ballerina.BallerinaModel;
import converter.ConversionUtils;
import converter.MuleToBalConverter;
import dataweave.parser.DataWeaveBaseVisitor;
import dataweave.parser.DataWeaveParser;
import io.ballerina.compiler.internal.parser.LexerTerminals;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static dataweave.converter.DWUtils.DW_VALUE_IDENTIFIER;
import static dataweave.converter.DWUtils.VAR_PREFIX;

public class BallerinaVisitor extends DataWeaveBaseVisitor<Void> {

    private final DWContext dwContext;
    private final MuleToBalConverter.Data data;
    private int varCount = 0;

    public BallerinaVisitor(DWContext context, MuleToBalConverter.Data data) {
        this.dwContext = context;
        this.data = data;
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
        this.dwContext.dwVersion = ctx.NUMBER().getText();
        return null;
    }

    @Override
    public Void visitOutputDirective(DataWeaveParser.OutputDirectiveContext ctx) {
        TerminalNode mediaType = ctx.MEDIA_TYPE();
        this.dwContext.outputType = DWUtils.findBallerinaType(mediaType.getText());
        return null;
    }

    @Override
    public Void visitInputDirective(DataWeaveParser.InputDirectiveContext ctx) {
        this.dwContext.inputType = DWUtils.findBallerinaType(ctx.MEDIA_TYPE().getText());
        this.dwContext.params.add(new BallerinaModel.Parameter(ctx.IDENTIFIER().getText()
                , this.dwContext.inputType, Optional.empty()));
        return null;
    }

    @Override
    public Void visitVariableDeclaration(DataWeaveParser.VariableDeclarationContext ctx) {
        String expression = ctx.expression().getText();
        String dwType = DWUtils.getVarTypeFromExpression(expression);
        String ballerinaType = DWUtils.getBallerinaType(dwType);
        visit(ctx.expression());
        String valueExpr = this.dwContext.getExpression();
        ballerinaType = dwType.equals(DWUtils.NUMBER) ? refineNumberType(valueExpr, ballerinaType) : ballerinaType;
        String statement = ballerinaType + " " + ctx.IDENTIFIER().getText() + " " + ctx.ASSIGN().getText() + " "
                + valueExpr + ";";
        this.dwContext.statements.add(new BallerinaModel.BallerinaStatement(statement));
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
        String methodName = String.format(DWUtils.DW_FUNCTION_NAME,
                data.dwMethodCount++);
        visitChildren(ctx);
        dwContext.finalizeFunction();
        String outputType = dwContext.outputType;
        if (dwContext.containsCheck) {
            outputType = dwContext.outputType + "| error";
        }
        this.dwContext.functionNames.add(methodName);
        this.data.functions.add(new BallerinaModel.Function(Optional.empty(), methodName, dwContext.params,
                Optional.of(outputType), dwContext.statements));
        return null;
    }

    @Override
    public Void visitMultiKeyValueObject(DataWeaveParser.MultiKeyValueObjectContext ctx) {
        List<String> keyValuePairs = new ArrayList<>();
        for (var kv : ctx.keyValue()) {
            String key = "\"" + kv.IDENTIFIER().getText() + "\"";
            visit(kv.expression());
            String value = dwContext.getExpression();
            keyValuePairs.add(key + ": " + value);
        }
        dwContext.exprBuilder.append("{ ").append(String.join(", ", keyValuePairs)).append(" }");
        return null;
    }

    @Override
    public Void visitSingleKeyValueObject(DataWeaveParser.SingleKeyValueObjectContext ctx) {
        List<String> keyValuePairs = new ArrayList<>();
        DataWeaveParser.KeyValueContext kv = ctx.keyValue();
        String key = "\"" + kv.IDENTIFIER().getText() + "\"";
        visit(kv.expression());
        String value = dwContext.getExpression();
        keyValuePairs.add(key + ": " + value);
        dwContext.exprBuilder.append("{ ").append(String.join(", ", keyValuePairs)).append(" }");
        return null;
    }

    @Override
    public Void visitArray(DataWeaveParser.ArrayContext ctx) {
        List<String> elements = new ArrayList<>();
        for (var expr : ctx.expression()) {
            visit(expr);
            elements.add(dwContext.getExpression());
        }
        dwContext.exprBuilder.append("[").append(String.join(", ", elements)).append("]");
        return null;
    }

    @Override
    public Void visitLiteral(DataWeaveParser.LiteralContext ctx) {
        if (ctx.STRING() != null) {
            this.dwContext.exprBuilder.append(ctx.STRING().getText()); // Return string value
        } else if (ctx.NUMBER() != null) {
            this.dwContext.exprBuilder.append(ctx.NUMBER().getText()); // Return number value
        } else if (ctx.BOOLEAN() != null) {
            this.dwContext.exprBuilder.append(ctx.BOOLEAN().getText()); // Return boolean value
        } else {
            this.dwContext.exprBuilder.append("()"); // Default case
        }
        return null;
    }

    @Override
    public Void visitFunctionCall(DataWeaveParser.FunctionCallContext ctx) {
        List<String> arguments = new ArrayList<>();
        for (var expr : ctx.expression()) {
            visit(expr);
            arguments.add(dwContext.getExpression());
        }
        return null;
    }

    @Override
    public Void visitSizeOfExpression(DataWeaveParser.SizeOfExpressionContext ctx) {
        visit(ctx.expression());
        if (Objects.equals(dwContext.inputType, LexerTerminals.JSON)) {
            String varName = VAR_PREFIX + varCount++;
            String castStatement = "var " + varName + " = " + dwContext.getExpression() + ";";
            dwContext.statements.add(new BallerinaModel.BallerinaStatement(castStatement));
            dwContext.exprBuilder.append(varName).append(".length()");
            return null;
        }
        dwContext.parentStatements.add(new BallerinaModel.BallerinaStatement(
                ConversionUtils.wrapElementInUnsupportedBlockComment(DWUtils.DW_FUNCTION_SIZE_OF)));
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
        if (Objects.equals(dwContext.inputType, LexerTerminals.JSON)) {
            visit((((DataWeaveParser.DefaultExpressionWrapperContext) ctx.getParent()).logicalOrExpression()));
            String varName = VAR_PREFIX + varCount++;
            String castStatement = "var " + varName + " = " + dwContext.getExpression() + ";";
            dwContext.varTypes.put(varName, "var");
            dwContext.varTypes.put(DWUtils.ELEMENT_ARG, "var");
            dwContext.varNames.put(DWUtils.DW_INDEX_IDENTIFIER, varName);
            dwContext.statements.add(new BallerinaModel.BallerinaStatement(castStatement));
            dwContext.exprBuilder.append(varName).append(".'map(");
            visit(ctx.implicitLambdaExpression());
            dwContext.exprBuilder.append(")");
            if (ctx.defaultExpressionRest() != null || !(ctx.defaultExpressionRest() instanceof
                    DataWeaveParser.DefaultExpressionEndContext)) {
                visit(ctx.defaultExpressionRest());
            }
            return null;
        }
        return null;
    }

    @Override
    public Void visitFilterExpression(DataWeaveParser.FilterExpressionContext ctx) {
        if (Objects.equals(dwContext.inputType, LexerTerminals.JSON)) {
            visit((((DataWeaveParser.DefaultExpressionWrapperContext) ctx.getParent()).logicalOrExpression()));
            String varName = VAR_PREFIX + varCount++;
            String castStatement = "var " + varName + " = " + dwContext.getExpression() + ";";
            dwContext.varTypes.put(varName, "var");
            dwContext.varTypes.put(DWUtils.ELEMENT_ARG, "var");
            dwContext.varNames.put(DWUtils.DW_INDEX_IDENTIFIER, varName);
            dwContext.statements.add(new BallerinaModel.BallerinaStatement(castStatement));
            dwContext.exprBuilder.append(varName).append(".filter(");
            visit(ctx.implicitLambdaExpression());
            dwContext.exprBuilder.append(")");
            if (ctx.defaultExpressionRest() != null || !(ctx.defaultExpressionRest() instanceof
                    DataWeaveParser.DefaultExpressionEndContext)) {
                visit(ctx.defaultExpressionRest());
            }
            return null;
        }
        return null;
    }

    @Override
    public Void visitGrouped(DataWeaveParser.GroupedContext ctx) {
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
        this.dwContext.exprBuilder.append(statement);
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
        this.dwContext.exprBuilder.append(statement);
        return null;
    }

    @Override
    public Void visitImplicitLambdaExpression(DataWeaveParser.ImplicitLambdaExpressionContext ctx) {
        String expr = ctx.expression().getText();
        dwContext.exprBuilder.append(DWUtils.ELEMENT_ARG).append("=>");
        if (expr.contains(DW_VALUE_IDENTIFIER)) {
            dwContext.commonArgs.put(DW_VALUE_IDENTIFIER, DWUtils.ELEMENT_ARG);
        }
        visit(ctx.expression());
        return null;
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
        this.dwContext.exprBuilder.append(statement);
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
        this.dwContext.exprBuilder.append(statement);
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
        dwContext.exprBuilder.append(expression);
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
        dwContext.exprBuilder.append(expression);
        return null;
    }

    @Override
    public Void visitTypeCoercionExpression(DataWeaveParser.TypeCoercionExpressionContext ctx) {
        if (ctx.typeExpression() == null) {
            return visit(ctx.unaryExpression());
        }
        visit(ctx.unaryExpression());
        if (ctx.typeExpression() != null) {
            visit(ctx.typeExpression());
        }
        return null;
    }

    @Override
    public Void visitSingleValueSelector(DataWeaveParser.SingleValueSelectorContext ctx) {
        if (!Objects.equals(dwContext.inputType, LexerTerminals.JSON)) {
            dwContext.exprBuilder.append("[").append(ctx.IDENTIFIER().getText()).append("]");
        } else {
            dwContext.exprBuilder.append(".").append(ctx.IDENTIFIER().getText());
        }
        dwContext.addCheckExpr();
        return null;
    }

    @Override
    public Void visitIdentifierExpression(DataWeaveParser.IdentifierExpressionContext ctx) {
        this.dwContext.exprBuilder.append(ctx.IDENTIFIER().getText());
        return null;
    }

    @Override
    public Void visitValueIdentifierExpression(DataWeaveParser.ValueIdentifierExpressionContext ctx) {
        this.dwContext.exprBuilder.append(this.dwContext.commonArgs.get(DW_VALUE_IDENTIFIER));
        return null;
    }

    @Override
    public Void visitIndexIdentifierExpression(DataWeaveParser.IndexIdentifierExpressionContext ctx) {
        this.dwContext.exprBuilder.append(this.dwContext.varNames.get(DWUtils.DW_INDEX_IDENTIFIER))
                .append(".indexOf(").append(DWUtils.ELEMENT_ARG).append(")");
        return null;
    }

    @Override
    public Void visitUpperExpression(DataWeaveParser.UpperExpressionContext ctx) {
        visit(ctx.expression());
        this.dwContext.exprBuilder.append(".toUpperAscii()");
        return null;
    }

    @Override
    public Void visitLowerExpression(DataWeaveParser.LowerExpressionContext ctx) {
        visit(ctx.expression());
        this.dwContext.exprBuilder.append(".toLowerAscii()");
        return null;
    }

}
