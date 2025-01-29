package dw.parser.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dataweave.parser.DataWeaveBaseVisitor;
import dataweave.parser.DataWeaveParser;

import java.util.List;

public class JsonVisitor extends DataWeaveBaseVisitor<ObjectNode> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ObjectNode visitScript(DataWeaveParser.ScriptContext ctx) {
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
    public ObjectNode visitHeader(DataWeaveParser.HeaderContext ctx) {
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
    public ObjectNode visitDirective(DataWeaveParser.DirectiveContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public ObjectNode visitDwVersion(DataWeaveParser.DwVersionContext ctx) {
        ObjectNode directiveNode = objectMapper.createObjectNode();
        directiveNode.put("type", "Version");
        directiveNode.put("version", ctx.NUMBER().getText());
        return directiveNode;
    }

    @Override
    public ObjectNode visitOutputDirective(DataWeaveParser.OutputDirectiveContext ctx) {
        ObjectNode directiveNode = objectMapper.createObjectNode();
        directiveNode.put("type", "Output");
        for (int i = 0; i < ctx.IDENTIFIER().size(); i++) {
            directiveNode.put("output" + i, ctx.IDENTIFIER(i).getText());
        }
        return directiveNode;
    }

    @Override
    public ObjectNode visitInputDirective(DataWeaveParser.InputDirectiveContext ctx) {
        ObjectNode directiveNode = objectMapper.createObjectNode();
        directiveNode.put("type", "Input");
        directiveNode.put("input", ctx.INPUT().getText());
        return directiveNode;
    }

    @Override
    public ObjectNode visitNamespaceDirective(DataWeaveParser.NamespaceDirectiveContext ctx) {
        ObjectNode directiveNode = objectMapper.createObjectNode();
        directiveNode.put("type", "NameSpace");
        directiveNode.put("namespace", ctx.NAMESPACE().getText());
        return directiveNode;
    }

    @Override
    public ObjectNode visitVariableDeclaration(DataWeaveParser.VariableDeclarationContext ctx) {
        ObjectNode directiveNode = objectMapper.createObjectNode();
        directiveNode.put("type", "Variable");
        directiveNode.put("identifier", ctx.IDENTIFIER().getText());
        directiveNode.set("expression", visit(ctx.expression()));
        return directiveNode;
    }

    @Override
    public ObjectNode visitFunctionDeclaration(DataWeaveParser.FunctionDeclarationContext ctx) {
        ObjectNode directiveNode = objectMapper.createObjectNode();
        directiveNode.put("type", "Function");
        directiveNode.put("identifier", ctx.IDENTIFIER().getText());
        directiveNode.set("expression", visit(ctx.expression()));
        return directiveNode;
    }


    @Override
    public ObjectNode visitBody(DataWeaveParser.BodyContext ctx) {
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
    public ObjectNode visitLiteralExpression(DataWeaveParser.LiteralExpressionContext ctx) {
        ObjectNode literalNode = objectMapper.createObjectNode();
        literalNode.put("type", "Literal");
        literalNode.put("value", ctx.getText());
        return literalNode;
    }

    @Override
    public ObjectNode visitIdentifierExpression(DataWeaveParser.IdentifierExpressionContext ctx) {
        ObjectNode identifierNode = objectMapper.createObjectNode();
        identifierNode.put("type", "Identifier");
        identifierNode.put("name", ctx.getText());
        return identifierNode;
    }

    @Override
    public ObjectNode visitArrayExpression(DataWeaveParser.ArrayExpressionContext ctx) {
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
    public ObjectNode visitObjectExpression(DataWeaveParser.ObjectExpressionContext ctx) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type", "Object");

        for (DataWeaveParser.KeyValueContext kv : ctx.object().keyValue()) {
            objectNode.set(kv.IDENTIFIER().getText(), visit(kv.expression()));
        }

        return objectNode;
    }
}
