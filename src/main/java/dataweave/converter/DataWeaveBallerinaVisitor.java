package dataweave.converter;

import ballerina.BallerinaModel;
import converter.Mule2BalConverter;
import dataweave.parser.DataWeaveBaseVisitor;
import dataweave.parser.DataWeaveParser;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;
import java.util.regex.Matcher;

public class DataWeaveBallerinaVisitor extends DataWeaveBaseVisitor<Object> {

    private final DataWeaveContext dwContext;

    public DataWeaveBallerinaVisitor(String mimeType, Mule2BalConverter.Data data,
                                     List<BallerinaModel.Statement> statementList) {
        this.dwContext = new DataWeaveContext(mimeType, data, statementList);
    }

    @Override
    public Object visitHeader(DataWeaveParser.HeaderContext ctx) {
        return super.visitHeader(ctx);
    }

    @Override
    public Object visitDwVersion(DataWeaveParser.DwVersionContext ctx) {
        this.dwContext.setDwVersion(ctx.NUMBER().getText());
        return visitChildren(ctx);
    }

    @Override
    public Object visitOutputDirective(DataWeaveParser.OutputDirectiveContext ctx) {
        List<TerminalNode> identifiers = ctx.IDENTIFIER();
        if (identifiers.isEmpty() || identifiers.size() > 2) {
            throw new BallerinaDWException("Invalid output directive");
        }

        String primaryDirective = identifiers.get(0).getText();
        String secondaryDirective = identifiers.size() > 1 ? identifiers.get(1).getText() : "";

        // Find and set the directive using the enum
        this.dwContext.setOutputDirective(DWConstants.OutputDirective.findDirective(primaryDirective, secondaryDirective));
        return visitChildren(ctx);
    }

    @Override
    public Object visitVariableDeclaration(DataWeaveParser.VariableDeclarationContext ctx) {
        String expression = ctx.expression().getText();
        String dwType = getVarTypeFromExpression(expression);
        String ballerinaType = this.dwContext.getBallerinaType(dwType);
        String statement = ballerinaType + " " + ctx.IDENTIFIER().getText() + " " + ctx.ASSIGN().getText() + " " + expression + ";";
        this.dwContext.addStatement(statement);

        return visitChildren(ctx);
    }

    private String getVarTypeFromExpression(String expression) {
        if (expression.startsWith(DWConstants.SINGLE_QUOTE) || expression.startsWith(DWConstants.DOUBLE_QUOTE)) {
            return DWConstants.STRING;
        }
        if (expression.equals("true") || expression.equals("false")) {
            return DWConstants.BOOLEAN;
        }
        if (expression.contains(DWConstants.VERTICAL_LINE)) {
            return DWConstants.DATE; // TODO : Need to handle all date types
        }
        Matcher matcher = DWConstants.NUMBER_PATTERN.matcher(expression);
        if (matcher.matches()) {
            return DWConstants.NUMBER;
        }
        if (expression.startsWith(DWConstants.SQUARE_START_BRACKET) &&
                expression.endsWith(DWConstants.SQUARE_END_BRACKET)) {
            return DWConstants.ARRAY;
        }
        if (expression.startsWith(DWConstants.CURLY_START_BRACKET) &&
                expression.endsWith(DWConstants.CURLY_END_BRACKET)) {
            return DWConstants.OBJECT;
        }
        throw new BallerinaDWException("Unsupported type: " + expression);
    }

    @Override
    public Object visitFunctionCall(DataWeaveParser.FunctionCallContext ctx) {
        return visitFunctionCall(ctx);
    }

    @Override
    public Object visitBody(DataWeaveParser.BodyContext ctx) {
        DWConstants.OutputDirective outputDirective = dwContext.getOutputDirective();
        String balType = getOutputBallerinaType(outputDirective);
        visitChildren(ctx);
        dwContext.addStatement(balType + " " + DWConstants.DATAWEAVE_OUTPUT_VARIABLE_NAME + " = " +
                dwContext.getExpression() + ";");
        return null;
    }

    private String getOutputBallerinaType(DWConstants.OutputDirective outputDirective) {
        switch (outputDirective) {
            case APPLICATION_JAVA, APPLICATION_CSV, APPLICATION_DW:
                return "any";
            case APPLICATION_JSON, TEXT_JSON:
                return "json";
            case APPLICATION_XML, TEXT_XML:
                return "xml";
            case TEXT_CSV, TEXT_PLAIN:
                return "string";
        }
        return null;
    }

    @Override
    public Object visitLiteralExpression(DataWeaveParser.LiteralExpressionContext ctx) {
        String literal = this.visitLiteral(ctx.literal());
        this.dwContext.appendExpr(literal);
        return null;
    }

    @Override
    public String visitLiteral(DataWeaveParser.LiteralContext ctx) {
        if (ctx.STRING() != null) {
            return ctx.STRING().getText(); // Return string value
        } else if (ctx.NUMBER() != null) {
            return ctx.NUMBER().getText(); // Return number value
        } else if (ctx.BOOLEAN() != null) {
            return ctx.BOOLEAN().getText(); // Return boolean value
        }
        return "()"; // Default case
    }


}
