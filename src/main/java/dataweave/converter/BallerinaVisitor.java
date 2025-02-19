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

import static dataweave.converter.DWUtils.DW_INDEX_IDENTIFIER;
import static dataweave.converter.DWUtils.DW_VALUE_IDENTIFIER;

public class BallerinaVisitor extends DataWeaveBaseVisitor<Void> {

    private final DWContext dwContext;
    private final MuleToBalConverter.Data data;

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
    public Void visitPrimaryExpressionWrapper(DataWeaveParser.PrimaryExpressionWrapperContext ctx) {
        visit(ctx.primaryExpression());
        if (ctx.expressionRest() != null) {
            return visit(ctx.expressionRest());
        }
        if (ctx.selectorExpression() != null) {
            return visit(ctx.selectorExpression());
        }
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
            String castStatement = "var jsonArg = " + dwContext.getExpression() + ";";
            dwContext.statements.add(new BallerinaModel.BallerinaStatement(castStatement));
            dwContext.exprBuilder.append("jsonArg").append(".length()");
            return null;
        }
        dwContext.parentStatements.add(new BallerinaModel.BallerinaStatement(
                ConversionUtils.wrapElementInUnsupportedBlockComment(DWUtils.DW_FUNCTION_SIZE_OF)));
        return null;
    }

    @Override
    public Void visitMapExpression(DataWeaveParser.MapExpressionContext ctx) {
        if (Objects.equals(dwContext.inputType, LexerTerminals.JSON)) {
            String castStatement = "var " + DWUtils.ARRAY_ARG + " = " + dwContext.getExpression() + ";";
            dwContext.varTypes.put(DWUtils.ARRAY_ARG, "var");
            dwContext.varTypes.put(DWUtils.ELEMENT_ARG, "var");
            dwContext.statements.add(new BallerinaModel.BallerinaStatement(castStatement));
            dwContext.exprBuilder.append(DWUtils.ARRAY_ARG + ".'map(");
            visit(ctx.implicitLambdaExpression());
            dwContext.exprBuilder.append(")");
            return null;
        }
        return null;
    }

    @Override
    public Void visitFilterExpression(DataWeaveParser.FilterExpressionContext ctx) {
        if (Objects.equals(dwContext.inputType, LexerTerminals.JSON)) {
            String castStatement = "var " + DWUtils.ARRAY_ARG + " = " + dwContext.getExpression() + ";";
            dwContext.varTypes.put(DWUtils.ARRAY_ARG, "var");
            dwContext.varTypes.put(DWUtils.ELEMENT_ARG, "var");
            dwContext.statements.add(new BallerinaModel.BallerinaStatement(castStatement));
            dwContext.exprBuilder.append(DWUtils.ARRAY_ARG + ".filter(");
            visit(ctx.implicitLambdaExpression());
            dwContext.exprBuilder.append(")");
            return null;
        }
        return null;
    }

    @Override
    public Void visitSingleParameterImplicitLambda(DataWeaveParser.SingleParameterImplicitLambdaContext ctx) {
        String expr = ctx.expression().getText();
        dwContext.exprBuilder.append(DWUtils.ELEMENT_ARG).append("=>");
        if (expr.contains(DW_VALUE_IDENTIFIER)) {
            dwContext.commonArgs.put(DW_VALUE_IDENTIFIER, DWUtils.ELEMENT_ARG);
        }
        visit(ctx.expression());
        return null;
    }

    @Override
    public Void visitMathExpression(DataWeaveParser.MathExpressionContext ctx) {
        String expression = dwContext.getExpression();
        expression += ctx.OPERATOR_MATH().getText();
        visit(ctx.expression());
        expression += dwContext.getExpression();
        dwContext.exprBuilder.append(expression);
        return null;
    }

    @Override
    public Void visitComparisonExpression(DataWeaveParser.ComparisonExpressionContext ctx)  {
        String expression = dwContext.getExpression();
        expression += ctx.OPERATOR_COMPARISON().getText();
        visit(ctx.expression());
        expression += dwContext.getExpression();
        dwContext.exprBuilder.append(expression);
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
        String varName = ctx.IDENTIFIER().getText();
        if (varName.equals(DW_VALUE_IDENTIFIER)) {
            varName = this.dwContext.commonArgs.get(DW_VALUE_IDENTIFIER);
        }
        if (varName.equals(DW_INDEX_IDENTIFIER)) {
            varName = DWUtils.ARRAY_ARG + ".indexOf(" + DWUtils.ELEMENT_ARG + ")";
        }
        this.dwContext.exprBuilder.append(varName);
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
