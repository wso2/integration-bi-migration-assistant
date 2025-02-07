package dw.parser.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dataweave.parser.DataWeaveBaseVisitor;
import dataweave.parser.DataWeaveParser;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.stream.Collectors;

public class JsonVisitor extends DataWeaveBaseVisitor<JsonNode> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public JsonNode visitScript(DataWeaveParser.ScriptContext ctx) {
        ObjectNode scriptNode = objectMapper.createObjectNode();
        scriptNode.put("type", "Script");

        if (ctx.header() != null) {
            scriptNode.set("header", visit(ctx.header()));
        }

        if (ctx.body() != null) {
            scriptNode.set("body", visit(ctx.body()));
        }

        return scriptNode;
    }

    @Override
    public JsonNode visitHeader(DataWeaveParser.HeaderContext ctx) {
        ObjectNode headerNode = objectMapper.createObjectNode();
        headerNode.put("type", "Header");
        ArrayNode directives = objectMapper.createArrayNode();

        for (DataWeaveParser.DirectiveContext directive : ctx.directive()) {
            directives.add(visit(directive));
        }

        headerNode.set("directives", directives);
        return headerNode;
    }

    @Override
    public JsonNode visitDirective(DataWeaveParser.DirectiveContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public JsonNode visitDwVersion(DataWeaveParser.DwVersionContext ctx) {
        ObjectNode directiveNode = objectMapper.createObjectNode();
        directiveNode.put("type", "Version");
        directiveNode.put("version", ctx.NUMBER().getText());
        return directiveNode;
    }

    @Override
    public JsonNode visitOutputDirective(DataWeaveParser.OutputDirectiveContext ctx) {
        ObjectNode directiveNode = objectMapper.createObjectNode();
        directiveNode.put("type", "Output");
        directiveNode.put("output", ctx.MEDIA_TYPE().getText());
        return directiveNode;
    }

    @Override
    public JsonNode visitInputDirective(DataWeaveParser.InputDirectiveContext ctx) {
        ObjectNode directiveNode = objectMapper.createObjectNode();
        directiveNode.put("type", "Input");
        directiveNode.put("identifier", ctx.IDENTIFIER().getText());
        directiveNode.put("input", ctx.MEDIA_TYPE().getText());
        return directiveNode;
    }

    @Override
    public JsonNode visitNamespaceDirective(DataWeaveParser.NamespaceDirectiveContext ctx) {
        ObjectNode directiveNode = objectMapper.createObjectNode();
        directiveNode.put("type", "NameSpace");
        directiveNode.put("identifier", ctx.IDENTIFIER().getText());
        directiveNode.put("value", ctx.URL().getText());
        return directiveNode;
    }

    @Override
    public JsonNode visitVariableDeclaration(DataWeaveParser.VariableDeclarationContext ctx) {
        ObjectNode directiveNode = objectMapper.createObjectNode();
        directiveNode.put("type", "Variable");
        directiveNode.put("identifier", ctx.IDENTIFIER().getText());
        directiveNode.set("expression", visit(ctx.expression()));
        return directiveNode;
    }

    @Override
    public JsonNode visitFunctionDeclaration(DataWeaveParser.FunctionDeclarationContext ctx) {
        ObjectNode directiveNode = objectMapper.createObjectNode();
        directiveNode.put("type", "Function");
        directiveNode.put("identifier", ctx.IDENTIFIER().getText());
        directiveNode.set("args", visit(ctx.functionParameters()));
        directiveNode.set("expression", visit(ctx.expression()));
        return directiveNode;
    }

    @Override
    public JsonNode visitFunctionParameters(DataWeaveParser.FunctionParametersContext ctx) {
        ObjectNode directiveNode = objectMapper.createObjectNode();
        String args = ctx.IDENTIFIER()
                .stream()
                .map(ParseTree::getText)
                .collect(Collectors.joining(","));
        return directiveNode.textNode(args);
    }

    @Override
    public JsonNode visitBody(DataWeaveParser.BodyContext ctx) {
        ObjectNode bodyNode = objectMapper.createObjectNode();
        bodyNode.put("type", "Body");
        DataWeaveParser.ExpressionContext expression = ctx.expression();
        bodyNode.set("expression", visit(expression));
        return bodyNode;
    }

    @Override
    public JsonNode visitLiteralExpression(DataWeaveParser.LiteralExpressionContext ctx) {
        ObjectNode literalNode = objectMapper.createObjectNode();
        literalNode.put("type", "Literal");
        literalNode.put("value", ctx.getText());
        return literalNode;
    }

    @Override
    public JsonNode visitIdentifierExpression(DataWeaveParser.IdentifierExpressionContext ctx) {
        ObjectNode identifierNode = objectMapper.createObjectNode();
        identifierNode.put("type", "Identifier");
        identifierNode.put("name", ctx.getText());
        return identifierNode;
    }

    @Override
    public JsonNode visitArrayExpression(DataWeaveParser.ArrayExpressionContext ctx) {
        return visitArrayJsonNodes(ctx.array());
    }

    private ObjectNode visitArrayJsonNodes(DataWeaveParser.ArrayContext ctx) {
        ObjectNode arrayNode = objectMapper.createObjectNode();
        arrayNode.put("type", "Array");
        ArrayNode elements = objectMapper.createArrayNode();

        for (DataWeaveParser.ExpressionContext expr : ctx.expression()) {
            elements.add(visit(expr));
        }

        arrayNode.set("elements", elements);
        return arrayNode;
    }

    @Override
    public JsonNode visitArray(DataWeaveParser.ArrayContext ctx) {
        return visitArrayJsonNodes(ctx);
    }


    @Override
    public JsonNode visitObjectExpression(DataWeaveParser.ObjectExpressionContext ctx) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type", "Object");
        for (DataWeaveParser.KeyValueContext kv : ctx.object().keyValue()) {
            objectNode.set(kv.IDENTIFIER().getText(), visit(kv.expression()));
        }
        return objectNode;
    }

    @Override
    public JsonNode visitSingleValueSelector(DataWeaveParser.SingleValueSelectorContext ctx) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type", "SingleValueSelector");
        objectNode.set("primary", visit(ctx.primaryExpression()));
        objectNode.put("identifier", ctx.IDENTIFIER().getText());
        return objectNode;
    }

    @Override
    public JsonNode visitInlineLambda(DataWeaveParser.InlineLambdaContext ctx) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type", "InlineLambda");
        objectNode.set("args", visit(ctx.functionParameters()));
        objectNode.set("expression", visit(ctx.expression()));
        return objectNode;
    }

    public JsonNode visitFunctionCall(DataWeaveParser.FunctionCallContext ctx) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type", "FunctionCall");
        objectNode.put("name", ctx.IDENTIFIER().getText());
        ArrayNode args = objectMapper.createArrayNode();
        for (DataWeaveParser.ExpressionContext expr : ctx.expression()) {
            args.add(visit(expr));
        }
        objectNode.set("args", args);
        return objectNode;
    }

    @Override
    public JsonNode visitSizeOfExpression(DataWeaveParser.SizeOfExpressionContext ctx) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type", "SizeOf");
        objectNode.set("expression", visit(ctx.expression()));
        return objectNode;
    }

    @Override
    public JsonNode visitMapExpression(DataWeaveParser.MapExpressionContext ctx) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type", "Map");
        objectNode.set("element", visit(ctx.expression()));
        objectNode.set("lambda", visit(ctx.implicitLambdaExpression()));
        return objectNode;
    }

    @Override
    public JsonNode visitMathExpression(DataWeaveParser.MathExpressionContext ctx) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type", "MathBinaryExpression");
        objectNode.put("operator", ctx.OPERATOR_MATH().getText());
        objectNode.set("left", visit(ctx.expression(0)));
        objectNode.set("right", visit(ctx.expression(1)));
        return objectNode;
    }


}
