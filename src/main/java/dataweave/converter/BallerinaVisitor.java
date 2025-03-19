package dataweave.converter;

import ballerina.BallerinaModel;
import converter.ConversionUtils;
import converter.MuleToBalConverter;
import dataweave.converter.builder.IfStatementBuilder;
import dataweave.parser.DataWeaveBaseVisitor;
import dataweave.parser.DataWeaveParser;
import io.ballerina.compiler.internal.parser.LexerTerminals;
import mule.Constants;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
        this.dwContext.currentScriptContext.dwVersion = ctx.NUMBER().getText();
        return null;
    }

    @Override
    public Void visitOutputDirective(DataWeaveParser.OutputDirectiveContext ctx) {
        TerminalNode mediaType = ctx.MEDIA_TYPE();
        this.dwContext.currentScriptContext.outputType = DWUtils.findBallerinaType(mediaType.getText());
        return null;
    }

    @Override
    public Void visitInputDirective(DataWeaveParser.InputDirectiveContext ctx) {
        this.dwContext.currentScriptContext.inputType = DWUtils.findBallerinaType(ctx.MEDIA_TYPE().getText());
        this.dwContext.currentScriptContext.params.add(new BallerinaModel.Parameter(ctx.IDENTIFIER().getText()
                , this.dwContext.currentScriptContext.inputType, Optional.empty()));
        return null;
    }

    @Override
    public Void visitVariableDeclaration(DataWeaveParser.VariableDeclarationContext ctx) {
        String expression = ctx.expression().getText();
        String dwType = DWUtils.getVarTypeFromExpression(expression);
        String ballerinaType = DWUtils.getBallerinaType(dwType, data);
        visit(ctx.expression());
        String valueExpr = this.dwContext.getExpression();
        ballerinaType = dwType.equals(DWUtils.NUMBER) ? refineNumberType(valueExpr, ballerinaType) : ballerinaType;
        String statement = ballerinaType + " " + ctx.IDENTIFIER().getText() + " " + ctx.ASSIGN().getText() + " "
                + valueExpr + ";";
        this.dwContext.currentScriptContext.statements.add(new BallerinaModel.BallerinaStatement(statement));
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
        String outputType = dwContext.currentScriptContext.outputType;
        if (dwContext.currentScriptContext.containsCheck) {
            outputType = dwContext.currentScriptContext.outputType + "| error";
        }
        this.dwContext.functionNames.add(methodName);
        this.data.functions.add(new BallerinaModel.Function(Optional.empty(), methodName,
                dwContext.currentScriptContext.params, Optional.of(outputType),
                new BallerinaModel.BlockFunctionBody(dwContext.currentScriptContext.statements)));
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
            this.data.imports.add(new BallerinaModel.Import(Constants.ORG_BALLERINA, Constants.MODULE_TIME,
                    Optional.empty()));
            this.dwContext.currentScriptContext.containsCheck = true;
            this.dwContext.append("check time:civilFromString(");
            String dateString = ctx.DATE().getText();
            this.dwContext.append("\"" + dateString.substring(1, dateString.length() - 1) + "\"");
            this.dwContext.append(")");
        } else {
            this.dwContext.currentScriptContext.currentType = DWUtils.NULL;
            this.dwContext.append("()");
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
        if (Objects.equals(dwContext.currentScriptContext.inputType, LexerTerminals.JSON)) {
            String varName = DWUtils.VAR_PREFIX + varCount++;
            String castStatement = "var " + varName + " = " + dwContext.getExpression() + ";";
            dwContext.currentScriptContext.statements.add(new BallerinaModel.BallerinaStatement(castStatement));
            dwContext.append(varName).append(".length()");
            dwContext.currentScriptContext.currentType = DWUtils.NUMBER;
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
        if (Objects.equals(dwContext.currentScriptContext.inputType, LexerTerminals.JSON)) {
            visit((((DataWeaveParser.DefaultExpressionWrapperContext) ctx.getParent()).logicalOrExpression()));
            String varName = DWUtils.VAR_PREFIX + varCount++;
            String castStatement = "var " + varName + " = " + dwContext.getExpression() + ";";
            dwContext.currentScriptContext.varTypes.put(varName, "var");
            dwContext.currentScriptContext.varTypes.put(DWUtils.ELEMENT_ARG, "var");
            dwContext.currentScriptContext.varNames.put(DWUtils.DW_INDEX_IDENTIFIER, varName);
            dwContext.currentScriptContext.statements.add(new BallerinaModel.BallerinaStatement(castStatement));
            dwContext.append(varName).append(".'map(");
            visit(ctx.implicitLambdaExpression());
            dwContext.append(")");
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
        if (Objects.equals(dwContext.currentScriptContext.inputType, LexerTerminals.JSON)) {
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
            dwContext.currentScriptContext.statements.add(new BallerinaModel.BallerinaStatement(castStatement));
            dwContext.append(varName).append(".filter(");
            visit(ctx.implicitLambdaExpression());
            dwContext.append(")");
            if (ctx.defaultExpressionRest() != null || !(ctx.defaultExpressionRest() instanceof
                    DataWeaveParser.DefaultExpressionEndContext)) {
                visit(ctx.defaultExpressionRest());
            }
            return null;
        }
        return null;
    }

    @Override
    public Void visitReplaceExpression(DataWeaveParser.ReplaceExpressionContext ctx) {
        String regexVar = "_pattern_";
        String statement = "string:RegExp " + regexVar + " = re `" + ctx.REGEX().getText() + "`;";
        dwContext.currentScriptContext.statements.add(new BallerinaModel.BallerinaStatement(statement));
        visit((((DataWeaveParser.DefaultExpressionWrapperContext) ctx.getParent()).logicalOrExpression()));
        String varName = DWUtils.VAR_PREFIX + varCount++;
        String parameterStatement = "var " + varName + " = " + dwContext.getExpression() + ";";
        dwContext.currentScriptContext.statements.add(new BallerinaModel.BallerinaStatement(parameterStatement));
        dwContext.append(regexVar + ".replace(");
        dwContext.append(varName);
        dwContext.append(", ");
        visit(ctx.expression());
        dwContext.append(")");
        return null;
    }

    @Override
    public Void visitConcatExpression(DataWeaveParser.ConcatExpressionContext ctx) {
        visit((((DataWeaveParser.DefaultExpressionWrapperContext) ctx.getParent()).logicalOrExpression()));
        String leftExpr = dwContext.getExpression();
        visit(ctx.expression());
        String rightExpr = dwContext.getExpression();
        switch (dwContext.currentScriptContext.currentType) {
            case DWUtils.STRING:
                dwContext.append(leftExpr).append(" + ").append(rightExpr);
                break;
            case DWUtils.ARRAY:
                String leftArr = DWUtils.VAR_PREFIX + varCount++;
                String leftArrayStatement = "any[] " + leftArr + " = " + leftExpr + ";";
                dwContext.currentScriptContext.statements.add(
                        new BallerinaModel.BallerinaStatement(leftArrayStatement));
                String rightArr = DWUtils.VAR_PREFIX + varCount++;
                String rightArrayStatement = "var " + rightArr + " = " + rightExpr + ";";
                dwContext.currentScriptContext.statements.add(
                        new BallerinaModel.BallerinaStatement(rightArrayStatement));
                dwContext.append(leftArr).append(".push(...").append(rightArr).append(")");
                break;
            default:
                String leftMap = DWUtils.VAR_PREFIX + varCount++;
                String leftMapStatement = "var " + leftMap + " = " + leftExpr + ";";
                dwContext.currentScriptContext.statements.add(
                        new BallerinaModel.BallerinaStatement(leftMapStatement));
                dwContext.append(rightExpr.replaceFirst("\\{", "{ " + leftMap + ", "));
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
        this.dwContext.append(statement.toString());
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
        return null;
    }

    @Override
    public Void visitImplicitLambdaExpression(DataWeaveParser.ImplicitLambdaExpressionContext ctx) {
        String expr = ctx.expression().getText();
        dwContext.append(DWUtils.ELEMENT_ARG).append("=>");
        if (expr.contains(DWUtils.DW_VALUE_IDENTIFIER)) {
            dwContext.commonArgs.put(DWUtils.DW_VALUE_IDENTIFIER, DWUtils.ELEMENT_ARG);
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
        this.dwContext.append(statement.toString());
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
        return null;
    }

    @Override
    public Void visitTypeCoercionExpression(DataWeaveParser.TypeCoercionExpressionContext ctx) {
        if (ctx.typeExpression() == null) {
            return visit(ctx.unaryExpression());
        }
        visit(ctx.unaryExpression());
        String type = ctx.typeExpression().IDENTIFIER().getText();
        String balType = DWUtils.getBallerinaType(type, data);
        String expression = this.dwContext.getExpression();
        switch (balType) {
            case "string":
                if (this.dwContext.currentScriptContext.currentType.equals(DWUtils.NUMBER)) {
                    if (ctx.formatOption() != null) {
                        if (!data.utilFunctions.contains(DWUtils.INT_TO_STRING)) {
                            this.data.imports.add(new BallerinaModel.Import(Constants.ORG_BALLERINA,
                                    Constants.MODULE_JAVA, Optional.empty()));
                            this.data.utilFunctions.add(DWUtils.INT_TO_STRING);
                            this.data.functions.add(getIntToStringFunction());
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
                            if (!data.utilFunctions.contains(DWUtils.FORMAT_DATE_TIME_STRING)) {
                                this.data.functions.add(generateFormatDateTimeFunctions());
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
                }
                break;
            default:
                this.dwContext.currentScriptContext.exprBuilder.append(".ensureType(").append(balType).append(")");
        }
        return null;
    }

    private BallerinaModel.Function generateFormatDateTimeFunctions() {
        // create formatDateTime() function
        data.utilFunctions.add(DWUtils.FORMAT_DATE_TIME);
        BallerinaModel.ExternFunctionBody body = new BallerinaModel.ExternFunctionBody("java.time.LocalDateTime",
                Optional.empty(), "@java:Method", Optional.empty());
        data.functions.add(new BallerinaModel.Function(Optional.of("public"), DWUtils.FORMAT_DATE_TIME,
                List.of(new BallerinaModel.Parameter("dateTime", LexerTerminals.HANDLE,
                                Optional.empty())
                        , new BallerinaModel.Parameter("formatter", LexerTerminals.HANDLE,
                                Optional.empty()))
                , Optional.of(LexerTerminals.HANDLE), body));

        // create formatDateTimeString() function
        data.utilFunctions.add(DWUtils.GET_DATE_TIME_FORMATTER);
        body = new BallerinaModel.ExternFunctionBody("java.time.format.DateTimeFormatter",
                Optional.of("ofPattern"), "@java:Method", Optional.of(List.of("java.lang.String")));
        data.functions.add(new BallerinaModel.Function(Optional.of("public"), DWUtils.GET_DATE_TIME_FORMATTER,
                List.of(new BallerinaModel.Parameter("format", LexerTerminals.HANDLE,
                        Optional.empty())), Optional.of(LexerTerminals.HANDLE), body));

        // create getZoneId() function
        data.utilFunctions.add(DWUtils.GET_ZONE_ID);
        body = new BallerinaModel.ExternFunctionBody("java.time.ZoneId",
                Optional.of("of"), "@java:Method", Optional.of(List.of("java.lang.String")));
        data.functions.add(new BallerinaModel.Function(Optional.of("public"), DWUtils.GET_ZONE_ID,
                List.of(new BallerinaModel.Parameter("zoneId", LexerTerminals.HANDLE,
                        Optional.empty())), Optional.of(LexerTerminals.HANDLE), body));

        // create getDateTime() function
        data.utilFunctions.add(DWUtils.GET_DATE_TIME);
        body = new BallerinaModel.ExternFunctionBody("java.time.LocalDateTime",
                Optional.of("ofInstant"), "@java:Method", Optional.of(List.of("java.time.Instant",
                "java.time.ZoneId")));
        data.functions.add(new BallerinaModel.Function(Optional.of("public"), DWUtils.GET_DATE_TIME,
                List.of(new BallerinaModel.Parameter("instant", LexerTerminals.HANDLE,
                        Optional.empty()), new BallerinaModel.Parameter("zoneId",
                        LexerTerminals.HANDLE, Optional.empty()))
                , Optional.of(LexerTerminals.HANDLE), body));

        // create parseInstant() function
        data.utilFunctions.add(DWUtils.PARSE_INSTANT);
        body = new BallerinaModel.ExternFunctionBody("java.time.Instant",
                Optional.of("parse"), "@java:Method", Optional.empty());
        data.functions.add(new BallerinaModel.Function(Optional.of("public"), DWUtils.PARSE_INSTANT,
                List.of(new BallerinaModel.Parameter("instant", LexerTerminals.HANDLE,
                        Optional.empty()))
                , Optional.of(LexerTerminals.HANDLE), body));

        // create getFormattedStringFromDate() function
        data.utilFunctions.add(DWUtils.GET_FORMATTED_STRING_FROM_DATE);
        List<BallerinaModel.Parameter> params = new ArrayList<>();
        params.add(new BallerinaModel.Parameter("dateString", LexerTerminals.STRING, Optional.empty()));
        params.add(new BallerinaModel.Parameter("format", LexerTerminals.STRING, Optional.empty()));
        List<BallerinaModel.Statement> statements = new ArrayList<>();
        statements.add(new BallerinaModel.BallerinaStatement("handle localDateTime = " +
                "getDateTime(parseInstant(java:fromString(dateString)), \n" +
                "    getZoneId(java:fromString(\"UTC\")));"));
        statements.add(new BallerinaModel.BallerinaStatement("return formatDateTime(localDateTime, " +
                "getDateTimeFormatter(java:fromString(format))).toString();"));
        return new BallerinaModel.Function(Optional.of("public"), DWUtils.GET_FORMATTED_STRING_FROM_DATE,
                params, Optional.of(LexerTerminals.STRING), new BallerinaModel.BlockFunctionBody(statements));
    }

    private BallerinaModel.Function getIntToStringFunction() {
        if (!data.utilFunctions.contains(DWUtils.NEW_DECIMAL_FORMAT)) {
            data.utilFunctions.add(DWUtils.NEW_DECIMAL_FORMAT);
            BallerinaModel.ExternFunctionBody body = new BallerinaModel.ExternFunctionBody("java.text.DecimalFormat",
                    Optional.empty(), "@java:Constructor", Optional.empty());
            data.functions.add(new BallerinaModel.Function(Optional.of("public"), DWUtils.NEW_DECIMAL_FORMAT,
                    List.of(new BallerinaModel.Parameter("format", LexerTerminals.HANDLE,
                            Optional.empty())), Optional.of(LexerTerminals.HANDLE), body));
        }
        if (!data.utilFunctions.contains(DWUtils.GET_FORMATTED_STRING_FROM_NUMBER)) {
            data.utilFunctions.add(DWUtils.GET_FORMATTED_STRING_FROM_NUMBER);
            BallerinaModel.ExternFunctionBody body = new BallerinaModel.ExternFunctionBody("java.text.NumberFormat",
                    Optional.of("format"),  "@java:Method", Optional.of(List.of("long")));
            data.functions.add(new BallerinaModel.Function(Optional.of("public"),
                    DWUtils.GET_FORMATTED_STRING_FROM_NUMBER,
                    List.of(new BallerinaModel.Parameter("formatObject", LexerTerminals.HANDLE,
                            Optional.empty()), new BallerinaModel.Parameter("value", LexerTerminals.INT,
                            Optional.empty())), Optional.of(LexerTerminals.HANDLE), body));
        }
        List<BallerinaModel.Parameter> params = new ArrayList<>();
        params.add(new BallerinaModel.Parameter("intValue", "int", Optional.empty()));
        params.add(new BallerinaModel.Parameter("format", "string", Optional.empty()));
        List<BallerinaModel.Statement> statements = new ArrayList<>();
        statements.add(new BallerinaModel.BallerinaStatement("handle formatObj = " + DWUtils.NEW_DECIMAL_FORMAT +
                "(java:fromString(format));"));
        statements.add(new BallerinaModel.BallerinaStatement("handle stringResult = " +
                DWUtils.GET_FORMATTED_STRING_FROM_NUMBER + "(formatObj, intValue);"));
        statements.add(new BallerinaModel.BallerinaStatement("return stringResult.toString();"));
        return new BallerinaModel.Function(Optional.of("public"), DWUtils.INT_TO_STRING, params,
                Optional.of(LexerTerminals.STRING), new BallerinaModel.BlockFunctionBody(statements));
    }

    @Override
    public Void visitSingleValueSelector(DataWeaveParser.SingleValueSelectorContext ctx) {
        if (!Objects.equals(dwContext.currentScriptContext.inputType, LexerTerminals.JSON)) {
            dwContext.append("[").append(ctx.IDENTIFIER().getText()).append("]");
        } else {
            dwContext.append(".").append(ctx.IDENTIFIER().getText());
        }
        dwContext.addCheckExpr();
        return null;
    }

    @Override
    public Void visitIdentifierExpression(DataWeaveParser.IdentifierExpressionContext ctx) {
        this.dwContext.currentScriptContext.currentType = DWUtils.IDENTIFIER;
        if (ctx.IDENTIFIER().getText().equals(DWUtils.DW_NOW_IDENTIFIER)) {
            if (!this.data.utilFunctions.contains(DWUtils.GET_CURRENT_TIME_STRING)) {
                this.data.imports.add(new BallerinaModel.Import(Constants.ORG_BALLERINA,
                        Constants.MODULE_TIME, Optional.empty()));
                this.data.utilFunctions.add(DWUtils.GET_CURRENT_TIME_STRING);
                data.functions.add(getCurrentTimeStringFunction());
            }
            this.dwContext.append(DWUtils.GET_CURRENT_TIME_STRING).append("()");
            return null;
        }
        this.dwContext.append(ctx.IDENTIFIER().getText());
        return null;
    }

    private BallerinaModel.Function getCurrentTimeStringFunction() {
        return new BallerinaModel.Function(Optional.of("public"), DWUtils.GET_CURRENT_TIME_STRING, new ArrayList<>(),
                Optional.of(LexerTerminals.STRING), new BallerinaModel.BlockFunctionBody(List.of(
                new BallerinaModel.BallerinaStatement("return time:utcToString(time:utcNow());")
        )));
    }

    @Override
    public Void visitValueIdentifierExpression(DataWeaveParser.ValueIdentifierExpressionContext ctx) {
        this.dwContext.append(this.dwContext.commonArgs.get(DWUtils.DW_VALUE_IDENTIFIER));
        return null;
    }

    @Override
    public Void visitIndexIdentifierExpression(DataWeaveParser.IndexIdentifierExpressionContext ctx) {
        this.dwContext.append(this.dwContext.currentScriptContext.varNames.get(
                        DWUtils.DW_INDEX_IDENTIFIER))
                .append(".indexOf(").append(DWUtils.ELEMENT_ARG).append(")");
        return null;
    }

    @Override
    public Void visitUpperExpression(DataWeaveParser.UpperExpressionContext ctx) {
        visit(ctx.expression());
        this.dwContext.append(".toUpperAscii()");
        this.dwContext.currentScriptContext.currentType = DWUtils.STRING;
        return null;
    }

    @Override
    public Void visitLowerExpression(DataWeaveParser.LowerExpressionContext ctx) {
        visit(ctx.expression());
        this.dwContext.append(".toLowerAscii()");
        this.dwContext.currentScriptContext.currentType = DWUtils.STRING;
        return null;
    }

    @Override
    public Void visitWhenCondition(DataWeaveParser.WhenConditionContext ctx) {
        List<DataWeaveParser.DefaultExpressionContext> contexts = ctx.defaultExpression();
        dwContext.currentScriptContext.statements.add(buildWhenCondition(contexts));
        return null;
    }

    private BallerinaModel.IfElseStatement buildWhenCondition(List<DataWeaveParser.DefaultExpressionContext> contexts) {
        int size = contexts.size();
        if (size < 3) {
            throw new RuntimeException("Invalid when condition");
        }
        IfStatementBuilder builder = new IfStatementBuilder();
        visit(contexts.get(1));
        builder.setIfCondition(new BallerinaModel.BallerinaExpression(dwContext.getExpression()));
        visit(contexts.get(0));
        String varName = DWUtils.VAR_PREFIX + varCount++;
        builder.resultVar = varName;
        dwContext.currentScriptContext.statements.add(new BallerinaModel.BallerinaStatement(
                dwContext.currentScriptContext.outputType + " " + varName + ";"));
        builder.addIfBody(new BallerinaModel.BallerinaStatement(varName + " = " + dwContext.getExpression() + ";"));
        if (size == 3) {
            visit(contexts.get(2));
            builder.addElseBody(new BallerinaModel.BallerinaStatement(varName + " = " +
                    dwContext.getExpression() + ";"));
            dwContext.append(" " + varName);
            return builder.getStatement();
        }
        for (int i = 2; i < size - 1; i += 2) {
            visit(contexts.get(i + 1));
            String condition = dwContext.getExpression();
            List<BallerinaModel.Statement> statements = new ArrayList<>();
            visit(contexts.get(i));
            statements.add(new BallerinaModel.BallerinaStatement(varName + " = " + dwContext.getExpression() + ";"));
            builder.addElseIfClause(new BallerinaModel.BallerinaExpression(condition), statements);
        }
        visit(contexts.get(size - 1));
        builder.addElseBody(new BallerinaModel.BallerinaStatement(varName + " = " + dwContext.getExpression() + ";"));
        dwContext.append(" " + varName);
        return builder.getStatement();
    }

}
