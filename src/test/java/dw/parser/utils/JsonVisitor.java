package dw.parser.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dataweave.parser.DataWeaveBaseVisitor;
import dataweave.parser.DataWeaveParser;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.List;
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
        List<DataWeaveParser.ExpressionContext> expression = ctx.expression();
        if (expression.size() == 1) {
            bodyNode.set("expression", visit(expression.getFirst()));
        } else {
            ArrayNode expressions = objectMapper.createArrayNode();
            for (DataWeaveParser.ExpressionContext expr : expression) {
                expressions.add(visit(expr));
            }
            bodyNode.set("expression", expressions);
        }
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
        ObjectNode arrayNode = objectMapper.createObjectNode();
        arrayNode.put("type", "Array");
        ArrayNode elements = objectMapper.createArrayNode();

        for (DataWeaveParser.ExpressionContext expr : ctx.array().expression()) {
            elements.add(visit(expr));
        }

        arrayNode.set("elements", elements);
        return arrayNode;
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
    public JsonNode visitInlineLambda(DataWeaveParser.InlineLambdaContext ctx) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type", "InlineLambda");
        objectNode.set("args", visit(ctx.functionParameters()));
        objectNode.set("expression", visit(ctx.expression()));
        return objectNode;
    }

}
