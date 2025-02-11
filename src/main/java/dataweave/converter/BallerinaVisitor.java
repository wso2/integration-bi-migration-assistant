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

    public BallerinaVisitor(String mimeType, MuleToBalConverter.Data data,
                            List<BallerinaModel.Statement> statementList) {
        this.dwContext = new DWContext(mimeType, statementList);
        this.data = data;
        if (mimeType != null) {
            this.dwContext.inputType = DWUtils.findBallerinaType(mimeType);
        }
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
        dwContext.parentStatements.add(new BallerinaModel.BallerinaStatement(this.dwContext.outputType + " "
                + DWUtils.DATAWEAVE_OUTPUT_VARIABLE_NAME + " = " + methodName + "(" +
                DWUtils.getParamsString(dwContext.params) + ");"));
        dwContext.finalizeFunction();
        if (dwContext.containsCheck) {
            dwContext.outputType = dwContext.outputType + "| error";
        }
        this.data.functions.add(new BallerinaModel.Function(Optional.empty(), methodName, dwContext.params,
                Optional.of(dwContext.outputType), dwContext.statements));
        return null;
    }

    @Override
    public Void visitObject(DataWeaveParser.ObjectContext ctx) {
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
            String castStatement = "json[] jsonArg = <json[]>" + dwContext.getExpression() + ";";
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
        visit(ctx.expression());
        if (Objects.equals(dwContext.inputType, LexerTerminals.JSON)) {
            String castStatement = "json[] " + DWUtils.MAP_ARG + " = <json[]>" + dwContext.getExpression() + ";";
            dwContext.varTypes.put(DWUtils.MAP_ARG, "json[]");
            dwContext.varTypes.put(DWUtils.ELEMENT_ARG, "json");
            dwContext.statements.add(new BallerinaModel.BallerinaStatement(castStatement));
            dwContext.exprBuilder.append(DWUtils.MAP_ARG + ".'map(");
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
        this.dwContext.statements.add(
                new BallerinaModel.BallerinaStatement(String.format(DWUtils.TYPE_CAST_COMMENT, ctx.getText())));
        this.dwContext.exprBuilder.append("<int>");
        visit(ctx.expression(0));
        String expression = dwContext.getExpression();
        expression += ctx.OPERATOR_MATH().getText();
        visit(ctx.expression(1));
        expression += "<int>" + dwContext.getExpression();
        dwContext.exprBuilder.append(expression);
        return null;
    }

    @Override
    public Void visitSingleValueSelector(DataWeaveParser.SingleValueSelectorContext ctx) {
        visit(ctx.primaryExpression());
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
            varName = DWUtils.MAP_ARG + ".indexOf(" + DWUtils.ELEMENT_ARG + ")";
        }
        this.dwContext.exprBuilder.append(varName);
        return null;
    }


//
//    @Override
//    public Void visitVariableDeclaration(DataWeaveParser.VariableDeclarationContext ctx) {
//        String varName = ctx.IDENTIFIER().getText();
//        BallerinaModel.BallerinaExpression value = (BallerinaModel.BallerinaExpression) visit(ctx.expression());
//        String inferredType = inferBallerinaType(value.expr());
//
//        BallerinaModel.ModuleVar var = new BallerinaModel.ModuleVar(varName, inferredType, value);
//        moduleVars.add(var);
//        return var;
//    }
//
//    @Override
//    public Void visitFunctionDeclaration(DataWeaveParser.FunctionDeclarationContext ctx) {
//        String funcName = ctx.IDENTIFIER().getText();
//        List<BallerinaModel.Parameter> parameters = ctx.functionParameters() != null
//                ? visitFunctionParameters(ctx.functionParameters())
//                : List.of();
//
//        List<BallerinaModel.Statement> body = List.of(
//                new BallerinaModel.BallerinaStatement("return " + visit(ctx.expression()) + ";")
//        );
//
//        BallerinaModel.Function function = new BallerinaModel.Function(
//                Optional.of("public"), funcName, parameters, Optional.of("any"), body
//        );
//        functions.add(function);
//        return function;
//    }
//
//    public List<BallerinaModel.Parameter> visitFunctionParameters(DataWeaveParser.FunctionParametersContext ctx) {
//        List<BallerinaModel.Parameter> parameters = new ArrayList<>();
//        for (var id : ctx.IDENTIFIER()) {
//            parameters.add(new BallerinaModel.Parameter(id.getText(), "any", Optional.empty()));
//        }
//        return parameters;
//    }
//
//    @Override
//    public Void visitLiteral(DataWeaveParser.LiteralContext ctx) {
//        if (ctx.STRING() != null) {
//            return new BallerinaModel.BallerinaExpression(ctx.STRING().getText());
//        } else if (ctx.NUMBER() != null) {
//            return new BallerinaModel.BallerinaExpression(ctx.NUMBER().getText());
//        } else if (ctx.BOOLEAN() != null) {
//            return new BallerinaModel.BallerinaExpression(ctx.BOOLEAN().getText());
//        }
//        return new BallerinaModel.BallerinaExpression("null");
//    }
//
//    @Override
//    public Void visitArray(DataWeaveParser.ArrayContext ctx) {
//        List<String> elements = new ArrayList<>();
//        for (var expr : ctx.expression()) {
//            elements.add(visit(expr).toString());
//        }
//        return new BallerinaModel.BallerinaExpression("[" + String.join(", ", elements) + "]");
//    }
//
//    @Override
//    public Void visitObject(DataWeaveParser.ObjectContext ctx) {
//        List<String> keyValuePairs = new ArrayList<>();
//        for (var kv : ctx.keyValue()) {
//            String key = "\"" + kv.IDENTIFIER().getText() + "\"";
//            String value = visit(kv.expression()).toString();
//            keyValuePairs.add(key + ": " + value);
//        }
//        return new BallerinaModel.BallerinaExpression("{ " + String.join(", ", keyValuePairs) + " }");
//    }
//
//    @Override
//    public Void visitFunctionCall(DataWeaveParser.FunctionCallContext ctx) {
//        String functionName = ctx.IDENTIFIER().getText();
//        List<String> arguments = new ArrayList<>();
//        for (var expr : ctx.expression()) {
//            arguments.add(visit(expr).toString());
//        }
//        return new BallerinaModel.BallerinaExpression(functionName + "(" + String.join(", ", arguments) + ")");
//    }
//
//    private String inferBallerinaType(String expression) {
//        if (expression.matches("\".*\"")) {
//            return "string";
//        } else if (expression.matches("[0-9]+(\\.[0-9]+)?")) {
//            return "int";
//        } else if (expression.matches("true|false")) {
//            return "boolean";
//        } else if (expression.startsWith("[")) {
//            return "any[]";
//        } else if (expression.startsWith("{")) {
//            return "map<any>";
//        }
//        return "any";
//    }
}
