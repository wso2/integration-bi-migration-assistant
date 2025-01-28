// Generated from src/main/java/dataweave/parser/DataWeave.g4 by ANTLR 4.13.2
package dataweave.parser;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link DataWeaveParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface DataWeaveVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link DataWeaveParser#script}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitScript(DataWeaveParser.ScriptContext ctx);

    /**
     * Visit a parse tree produced by {@link DataWeaveParser#header}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitHeader(DataWeaveParser.HeaderContext ctx);

    /**
     * Visit a parse tree produced by {@link DataWeaveParser#directive}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDirective(DataWeaveParser.DirectiveContext ctx);

    /**
     * Visit a parse tree produced by {@link DataWeaveParser#dwVersion}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDwVersion(DataWeaveParser.DwVersionContext ctx);

    /**
     * Visit a parse tree produced by {@link DataWeaveParser#outputDirective}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOutputDirective(DataWeaveParser.OutputDirectiveContext ctx);

    /**
     * Visit a parse tree produced by {@link DataWeaveParser#inputDirective}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitInputDirective(DataWeaveParser.InputDirectiveContext ctx);

    /**
     * Visit a parse tree produced by {@link DataWeaveParser#namespaceDirective}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitNamespaceDirective(DataWeaveParser.NamespaceDirectiveContext ctx);

    /**
     * Visit a parse tree produced by {@link DataWeaveParser#variableDeclaration}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitVariableDeclaration(DataWeaveParser.VariableDeclarationContext ctx);

    /**
     * Visit a parse tree produced by {@link DataWeaveParser#functionDeclaration}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFunctionDeclaration(DataWeaveParser.FunctionDeclarationContext ctx);

    /**
     * Visit a parse tree produced by {@link DataWeaveParser#body}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBody(DataWeaveParser.BodyContext ctx);

    /**
     * Visit a parse tree produced by the {@code arrayExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitArrayExpression(DataWeaveParser.ArrayExpressionContext ctx);

    /**
     * Visit a parse tree produced by the {@code conditionalExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitConditionalExpression(DataWeaveParser.ConditionalExpressionContext ctx);

    /**
     * Visit a parse tree produced by the {@code identifierExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitIdentifierExpression(DataWeaveParser.IdentifierExpressionContext ctx);

    /**
     * Visit a parse tree produced by the {@code mathExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMathExpression(DataWeaveParser.MathExpressionContext ctx);

    /**
     * Visit a parse tree produced by the {@code comparisonExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitComparisonExpression(DataWeaveParser.ComparisonExpressionContext ctx);

    /**
     * Visit a parse tree produced by the {@code logicalExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLogicalExpression(DataWeaveParser.LogicalExpressionContext ctx);

    /**
     * Visit a parse tree produced by the {@code groupedExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitGroupedExpression(DataWeaveParser.GroupedExpressionContext ctx);

    /**
     * Visit a parse tree produced by the {@code objectExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitObjectExpression(DataWeaveParser.ObjectExpressionContext ctx);

    /**
     * Visit a parse tree produced by the {@code chainExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitChainExpression(DataWeaveParser.ChainExpressionContext ctx);

    /**
     * Visit a parse tree produced by the {@code builtInFunctionExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBuiltInFunctionExpression(DataWeaveParser.BuiltInFunctionExpressionContext ctx);

    /**
     * Visit a parse tree produced by the {@code functionCallExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFunctionCallExpression(DataWeaveParser.FunctionCallExpressionContext ctx);

    /**
     * Visit a parse tree produced by the {@code rangeExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRangeExpression(DataWeaveParser.RangeExpressionContext ctx);

    /**
     * Visit a parse tree produced by the {@code bitwiseExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBitwiseExpression(DataWeaveParser.BitwiseExpressionContext ctx);

    /**
     * Visit a parse tree produced by the {@code literalExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLiteralExpression(DataWeaveParser.LiteralExpressionContext ctx);

    /**
     * Visit a parse tree produced by {@link DataWeaveParser#builtInFunctionCall}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBuiltInFunctionCall(DataWeaveParser.BuiltInFunctionCallContext ctx);

    /**
     * Visit a parse tree produced by {@link DataWeaveParser#inlineLambda}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitInlineLambda(DataWeaveParser.InlineLambdaContext ctx);

    /**
     * Visit a parse tree produced by {@link DataWeaveParser#literal}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLiteral(DataWeaveParser.LiteralContext ctx);

    /**
     * Visit a parse tree produced by {@link DataWeaveParser#array}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitArray(DataWeaveParser.ArrayContext ctx);

    /**
     * Visit a parse tree produced by {@link DataWeaveParser#object}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitObject(DataWeaveParser.ObjectContext ctx);

    /**
     * Visit a parse tree produced by {@link DataWeaveParser#keyValue}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitKeyValue(DataWeaveParser.KeyValueContext ctx);

    /**
     * Visit a parse tree produced by {@link DataWeaveParser#functionCall}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFunctionCall(DataWeaveParser.FunctionCallContext ctx);
}