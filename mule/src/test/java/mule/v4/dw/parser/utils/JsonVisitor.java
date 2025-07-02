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
package mule.v4.dw.parser.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import mule.v4.dataweave.parser.DataWeaveBaseVisitor;
import mule.v4.dataweave.parser.DataWeaveParser;
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
        bodyNode.set("expression", visit(ctx.expression()));
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
    public JsonNode visitValueIdentifierExpression(DataWeaveParser.ValueIdentifierExpressionContext ctx) {
        ObjectNode identifierNode = objectMapper.createObjectNode();
        identifierNode.put("type", "Identifier");
        identifierNode.put("name", ctx.getText());
        return identifierNode;
    }

    @Override
    public JsonNode visitIndexIdentifierExpression(DataWeaveParser.IndexIdentifierExpressionContext ctx) {
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
    public JsonNode visitMultiKeyValueObject(DataWeaveParser.MultiKeyValueObjectContext ctx) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type", "Object");
        for (DataWeaveParser.KeyValueContext kv : ctx.keyValue()) {
            objectNode.set(kv.IDENTIFIER().getText(), visit(kv.expression()));
        }
        return objectNode;
    }

    @Override
    public JsonNode visitSingleKeyValueObject(DataWeaveParser.SingleKeyValueObjectContext ctx) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type", "Object");
        DataWeaveParser.KeyValueContext kv = ctx.keyValue();
        objectNode.set(kv.IDENTIFIER().getText(), visit(kv.expression()));
        return objectNode;
    }

    @Override
    public JsonNode visitSelectorExpressionWrapper(DataWeaveParser.SelectorExpressionWrapperContext ctx) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type", "SelectorExpression");
        objectNode.set("primary", visit(ctx.primaryExpression()));
        objectNode.set("selector", visit(ctx.selectorExpression()));
        return objectNode;
    }

    @Override
    public JsonNode visitSingleValueSelector(DataWeaveParser.SingleValueSelectorContext ctx) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type", "SingleValueSelector");
        objectNode.put("identifier", ctx.IDENTIFIER().getText());
        return objectNode;
    }

    @Override
    public JsonNode visitImplicitLambdaExpression(DataWeaveParser.ImplicitLambdaExpressionContext ctx) {
        if (ctx.inlineLambda() != null) {
            return visit(ctx.inlineLambda());
        }
        if (ctx.expression() != null) {
            return visit(ctx.expression());
        }
        return visit(ctx.implicitLambdaExpression());
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
    public JsonNode visitDefaultExpressionWrapper(DataWeaveParser.DefaultExpressionWrapperContext ctx) {
        if (ctx.defaultExpressionRest() == null || ctx.defaultExpressionRest() instanceof
                DataWeaveParser.DefaultExpressionEndContext) {
            return visit(ctx.logicalOrExpression());
        }
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type", "DefaultExpression");
        objectNode.set("expression", visit(ctx.logicalOrExpression()));
        objectNode.set("default", visit(ctx.defaultExpressionRest()));
        return objectNode;
    }


    @Override
    public JsonNode visitMapExpression(DataWeaveParser.MapExpressionContext ctx) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type", "Map");
        objectNode.set("lambda", visit(ctx.implicitLambdaExpression()));
        if (!(ctx.defaultExpressionRest() instanceof DataWeaveParser.DefaultExpressionEndContext)) {
            objectNode.set("default", visit(ctx.defaultExpressionRest()));
        }
        return objectNode;
    }

    @Override
    public JsonNode visitFilterExpression(DataWeaveParser.FilterExpressionContext ctx) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type", "Filter");
        objectNode.set("lambda", visit(ctx.implicitLambdaExpression()));
        if (!(ctx.defaultExpressionRest() instanceof DataWeaveParser.DefaultExpressionEndContext)) {
            objectNode.set("default", visit(ctx.defaultExpressionRest()));
        }
        return objectNode;
    }

    @Override
    public JsonNode visitReplaceExpression(DataWeaveParser.ReplaceExpressionContext ctx) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type", "Replace");
        objectNode.put("regex", ctx.REGEX().getText());
        objectNode.set("replacement", visit(ctx.expression()));
        return objectNode;
    }

    @Override
    public JsonNode visitConcatExpression(DataWeaveParser.ConcatExpressionContext ctx) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type", "Concat");
        objectNode.set("expression", visit(ctx.expression()));
        return objectNode;
    }

    @Override
    public JsonNode visitGrouped(DataWeaveParser.GroupedContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public JsonNode visitLogicalOrExpression(DataWeaveParser.LogicalOrExpressionContext ctx) {
        if (ctx.logicalAndExpression().size() == 1) {
            return visit(ctx.logicalAndExpression(0));
        }
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type", "LogicalOrExpression");
        objectNode.set("left", visit(ctx.logicalAndExpression(0)));
        for (int i = 1; i < ctx.logicalAndExpression().size(); i++) {
            objectNode.put("operator", "or");
            objectNode.set("right" + i, visit(ctx.logicalAndExpression(i)));
        }
        return objectNode;
    }

    @Override
    public JsonNode visitLogicalAndExpression(DataWeaveParser.LogicalAndExpressionContext ctx) {
        if (ctx.equalityExpression().size() == 1) {
            return visit(ctx.equalityExpression(0));
        }
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type", "LogicalAndExpression");
        objectNode.set("left", visit(ctx.equalityExpression(0)));
        for (int i = 1; i < ctx.equalityExpression().size(); i++) {
            objectNode.put("operator", "and");
            objectNode.set("right" + i, visit(ctx.equalityExpression(i)));
        }
        return objectNode;
    }

    @Override
    public JsonNode visitEqualityExpression(DataWeaveParser.EqualityExpressionContext ctx) {
        if (ctx.relationalExpression().size() == 1) {
            return visit(ctx.relationalExpression(0));
        }
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type", "EqualityExpression");
        objectNode.set("left", visit(ctx.relationalExpression(0)));
        for (int i = 1; i < ctx.relationalExpression().size(); i++) {
            objectNode.put("operator", ctx.OPERATOR_EQUALITY((i - 1)).getText());
            objectNode.set("right" + i, visit(ctx.relationalExpression(i)));
        }
        return objectNode;
    }

    @Override
    public JsonNode visitRelationalExpression(DataWeaveParser.RelationalExpressionContext ctx) {
        if (ctx.additiveExpression().size() == 1) {
            return visit(ctx.additiveExpression(0));
        }
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type", "RelationalExpression");
        objectNode.set("left", visit(ctx.additiveExpression(0)));
        for (int i = 1; i < ctx.additiveExpression().size(); i++) {
            objectNode.put("operator", ctx.OPERATOR_RELATIONAL(i - 1).getText());
            objectNode.set("right" + i, visit(ctx.additiveExpression(i)));
        }
        return objectNode;
    }

    @Override
    public JsonNode visitAdditiveExpression(DataWeaveParser.AdditiveExpressionContext ctx) {
        if (ctx.multiplicativeExpression().size() == 1) {
            return visit(ctx.multiplicativeExpression(0));
        }
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type", "AdditiveExpression");
        objectNode.set("left", visit(ctx.multiplicativeExpression(0)));
        for (int i = 1; i < ctx.multiplicativeExpression().size(); i++) {
            objectNode.put("operator", ctx.OPERATOR_ADDITIVE(i - 1).getText());
            objectNode.set("right" + i, visit(ctx.multiplicativeExpression(i)));
        }
        return objectNode;
    }

    @Override
    public JsonNode visitMultiplicativeExpression(DataWeaveParser.MultiplicativeExpressionContext ctx) {
        if (ctx.typeCoercionExpression().size() == 1) {
            return visit(ctx.typeCoercionExpression(0));
        }
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type", "MultiplicativeExpression");
        objectNode.set("left", visit(ctx.typeCoercionExpression(0)));
        for (int i = 1; i < ctx.typeCoercionExpression().size(); i++) {
            objectNode.put("operator", ctx.OPERATOR_MULTIPLICATIVE(i - 1).getText());
            objectNode.set("right" + i, visit(ctx.typeCoercionExpression(i)));
        }
        return objectNode;
    }

    @Override
    public JsonNode visitTypeCoercionExpression(DataWeaveParser.TypeCoercionExpressionContext ctx) {
        if (ctx.typeExpression() == null) {
            return visit(ctx.unaryExpression());
        }
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type", "TypeCoercionExpression");
        objectNode.set("expression", visit(ctx.unaryExpression()));
        objectNode.put("operator", ctx.OPERATOR_TYPE_COERCION().getText());
        objectNode.set("type", visit(ctx.typeExpression()));
        if (ctx.formatOption() != null) {
            objectNode.set("format", visit(ctx.formatOption()));
        }
        return objectNode;
    }

    @Override
    public JsonNode visitUpperExpression(DataWeaveParser.UpperExpressionContext ctx) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type", "UpperExpression");
        objectNode.set("expression", visit(ctx.expression()));
        return objectNode;
    }

    @Override
    public JsonNode visitLowerExpression(DataWeaveParser.LowerExpressionContext ctx) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type", "LowerExpression");
        objectNode.set("expression", visit(ctx.expression()));
        return objectNode;
    }

    @Override
    public JsonNode visitWhenCondition(DataWeaveParser.WhenConditionContext ctx) {
        List<DataWeaveParser.DefaultExpressionContext> contexts = ctx.defaultExpression();
        return buildWhenCondition(contexts, 0);
    }

    private JsonNode buildWhenCondition(List<DataWeaveParser.DefaultExpressionContext> contexts, int index) {
        if (index >= contexts.size()) {
            return null;
        }
        ObjectNode node = objectMapper.createObjectNode();
        node.put("type", "WhenCondition");
        if (index < contexts.size() - 1) {
            node.set("condition", visit(contexts.get(index + 1)));
        }
        node.set("when-body", visit(contexts.get(index)));
        if (index + 2 < contexts.size()) {
            JsonNode otherwiseNode = buildWhenCondition(contexts, index + 2);
            if (otherwiseNode != null) {
                node.set("otherwise-body", otherwiseNode);
            }
        } else if (index + 1 == contexts.size() - 1) {
            node.set("otherwise-body", visit(contexts.get(index + 1)));
        }
        return node;
    }

    @Override
    public JsonNode visitTypeExpression(DataWeaveParser.TypeExpressionContext ctx) {
        return new TextNode(ctx.IDENTIFIER().getText());
    }

    @Override
    public JsonNode visitFormatOption(DataWeaveParser.FormatOptionContext ctx) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type", "FormatOption");
        objectNode.put("key", ctx.IDENTIFIER().getText());
        objectNode.put("value", ctx.STRING().getText());
        return objectNode;
    }

}
