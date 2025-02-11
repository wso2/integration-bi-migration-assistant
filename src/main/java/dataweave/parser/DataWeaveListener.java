// Generated from src/main/java/dataweave/parser/DataWeave.g4 by ANTLR 4.13.2
package dataweave.parser;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link DataWeaveParser}.
 */
public interface DataWeaveListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link DataWeaveParser#script}.
     *
     * @param ctx the parse tree
     */
    void enterScript(DataWeaveParser.ScriptContext ctx);

    /**
     * Exit a parse tree produced by {@link DataWeaveParser#script}.
     *
     * @param ctx the parse tree
     */
    void exitScript(DataWeaveParser.ScriptContext ctx);

    /**
     * Enter a parse tree produced by {@link DataWeaveParser#header}.
     *
     * @param ctx the parse tree
     */
    void enterHeader(DataWeaveParser.HeaderContext ctx);

    /**
     * Exit a parse tree produced by {@link DataWeaveParser#header}.
     *
     * @param ctx the parse tree
     */
    void exitHeader(DataWeaveParser.HeaderContext ctx);

    /**
     * Enter a parse tree produced by {@link DataWeaveParser#directive}.
     *
     * @param ctx the parse tree
     */
    void enterDirective(DataWeaveParser.DirectiveContext ctx);

    /**
     * Exit a parse tree produced by {@link DataWeaveParser#directive}.
     *
     * @param ctx the parse tree
     */
    void exitDirective(DataWeaveParser.DirectiveContext ctx);

    /**
     * Enter a parse tree produced by {@link DataWeaveParser#dwVersion}.
     *
     * @param ctx the parse tree
     */
    void enterDwVersion(DataWeaveParser.DwVersionContext ctx);

    /**
     * Exit a parse tree produced by {@link DataWeaveParser#dwVersion}.
     *
     * @param ctx the parse tree
     */
    void exitDwVersion(DataWeaveParser.DwVersionContext ctx);

    /**
     * Enter a parse tree produced by {@link DataWeaveParser#outputDirective}.
     *
     * @param ctx the parse tree
     */
    void enterOutputDirective(DataWeaveParser.OutputDirectiveContext ctx);

    /**
     * Exit a parse tree produced by {@link DataWeaveParser#outputDirective}.
     *
     * @param ctx the parse tree
     */
    void exitOutputDirective(DataWeaveParser.OutputDirectiveContext ctx);

    /**
     * Enter a parse tree produced by {@link DataWeaveParser#inputDirective}.
     *
     * @param ctx the parse tree
     */
    void enterInputDirective(DataWeaveParser.InputDirectiveContext ctx);

    /**
     * Exit a parse tree produced by {@link DataWeaveParser#inputDirective}.
     *
     * @param ctx the parse tree
     */
    void exitInputDirective(DataWeaveParser.InputDirectiveContext ctx);

    /**
     * Enter a parse tree produced by {@link DataWeaveParser#namespaceDirective}.
     *
     * @param ctx the parse tree
     */
    void enterNamespaceDirective(DataWeaveParser.NamespaceDirectiveContext ctx);

    /**
     * Exit a parse tree produced by {@link DataWeaveParser#namespaceDirective}.
     *
     * @param ctx the parse tree
     */
    void exitNamespaceDirective(DataWeaveParser.NamespaceDirectiveContext ctx);

    /**
     * Enter a parse tree produced by {@link DataWeaveParser#variableDeclaration}.
     *
     * @param ctx the parse tree
     */
    void enterVariableDeclaration(DataWeaveParser.VariableDeclarationContext ctx);

    /**
     * Exit a parse tree produced by {@link DataWeaveParser#variableDeclaration}.
     *
     * @param ctx the parse tree
     */
    void exitVariableDeclaration(DataWeaveParser.VariableDeclarationContext ctx);

    /**
     * Enter a parse tree produced by {@link DataWeaveParser#functionDeclaration}.
     *
     * @param ctx the parse tree
     */
    void enterFunctionDeclaration(DataWeaveParser.FunctionDeclarationContext ctx);

    /**
     * Exit a parse tree produced by {@link DataWeaveParser#functionDeclaration}.
     *
     * @param ctx the parse tree
     */
    void exitFunctionDeclaration(DataWeaveParser.FunctionDeclarationContext ctx);

    /**
     * Enter a parse tree produced by {@link DataWeaveParser#body}.
     *
     * @param ctx the parse tree
     */
    void enterBody(DataWeaveParser.BodyContext ctx);

    /**
     * Exit a parse tree produced by {@link DataWeaveParser#body}.
     *
     * @param ctx the parse tree
     */
    void exitBody(DataWeaveParser.BodyContext ctx);

    /**
     * Enter a parse tree produced by the {@code primaryExpressionWrapper}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void enterPrimaryExpressionWrapper(DataWeaveParser.PrimaryExpressionWrapperContext ctx);

    /**
     * Exit a parse tree produced by the {@code primaryExpressionWrapper}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void exitPrimaryExpressionWrapper(DataWeaveParser.PrimaryExpressionWrapperContext ctx);

    /**
     * Enter a parse tree produced by the {@code chainExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void enterChainExpression(DataWeaveParser.ChainExpressionContext ctx);

    /**
     * Exit a parse tree produced by the {@code chainExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void exitChainExpression(DataWeaveParser.ChainExpressionContext ctx);

    /**
     * Enter a parse tree produced by the {@code mapExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void enterMapExpression(DataWeaveParser.MapExpressionContext ctx);

    /**
     * Exit a parse tree produced by the {@code mapExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void exitMapExpression(DataWeaveParser.MapExpressionContext ctx);

    /**
     * Enter a parse tree produced by the {@code conditionalExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void enterConditionalExpression(DataWeaveParser.ConditionalExpressionContext ctx);

    /**
     * Exit a parse tree produced by the {@code conditionalExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void exitConditionalExpression(DataWeaveParser.ConditionalExpressionContext ctx);

    /**
     * Enter a parse tree produced by the {@code filterExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void enterFilterExpression(DataWeaveParser.FilterExpressionContext ctx);

    /**
     * Exit a parse tree produced by the {@code filterExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void exitFilterExpression(DataWeaveParser.FilterExpressionContext ctx);

    /**
     * Enter a parse tree produced by the {@code mathExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void enterMathExpression(DataWeaveParser.MathExpressionContext ctx);

    /**
     * Exit a parse tree produced by the {@code mathExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void exitMathExpression(DataWeaveParser.MathExpressionContext ctx);

    /**
     * Enter a parse tree produced by the {@code rangeExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void enterRangeExpression(DataWeaveParser.RangeExpressionContext ctx);

    /**
     * Exit a parse tree produced by the {@code rangeExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void exitRangeExpression(DataWeaveParser.RangeExpressionContext ctx);

    /**
     * Enter a parse tree produced by the {@code comparisonExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void enterComparisonExpression(DataWeaveParser.ComparisonExpressionContext ctx);

    /**
     * Exit a parse tree produced by the {@code comparisonExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void exitComparisonExpression(DataWeaveParser.ComparisonExpressionContext ctx);

    /**
     * Enter a parse tree produced by the {@code bitwiseExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void enterBitwiseExpression(DataWeaveParser.BitwiseExpressionContext ctx);

    /**
     * Exit a parse tree produced by the {@code bitwiseExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void exitBitwiseExpression(DataWeaveParser.BitwiseExpressionContext ctx);

    /**
     * Enter a parse tree produced by the {@code logicalExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void enterLogicalExpression(DataWeaveParser.LogicalExpressionContext ctx);

    /**
     * Exit a parse tree produced by the {@code logicalExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void exitLogicalExpression(DataWeaveParser.LogicalExpressionContext ctx);

    /**
     * Enter a parse tree produced by the {@code lambdaExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void enterLambdaExpression(DataWeaveParser.LambdaExpressionContext ctx);

    /**
     * Exit a parse tree produced by the {@code lambdaExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void exitLambdaExpression(DataWeaveParser.LambdaExpressionContext ctx);

    /**
     * Enter a parse tree produced by the {@code arrayExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void enterArrayExpression(DataWeaveParser.ArrayExpressionContext ctx);

    /**
     * Exit a parse tree produced by the {@code arrayExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void exitArrayExpression(DataWeaveParser.ArrayExpressionContext ctx);

    /**
     * Enter a parse tree produced by the {@code upperExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void enterUpperExpression(DataWeaveParser.UpperExpressionContext ctx);

    /**
     * Exit a parse tree produced by the {@code upperExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void exitUpperExpression(DataWeaveParser.UpperExpressionContext ctx);

    /**
     * Enter a parse tree produced by the {@code lowerExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void enterLowerExpression(DataWeaveParser.LowerExpressionContext ctx);

    /**
     * Exit a parse tree produced by the {@code lowerExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void exitLowerExpression(DataWeaveParser.LowerExpressionContext ctx);

    /**
     * Enter a parse tree produced by the {@code identifierExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void enterIdentifierExpression(DataWeaveParser.IdentifierExpressionContext ctx);

    /**
     * Exit a parse tree produced by the {@code identifierExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void exitIdentifierExpression(DataWeaveParser.IdentifierExpressionContext ctx);

    /**
     * Enter a parse tree produced by the {@code indexedSelector}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void enterIndexedSelector(DataWeaveParser.IndexedSelectorContext ctx);

    /**
     * Exit a parse tree produced by the {@code indexedSelector}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void exitIndexedSelector(DataWeaveParser.IndexedSelectorContext ctx);

    /**
     * Enter a parse tree produced by the {@code attributeSelector}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void enterAttributeSelector(DataWeaveParser.AttributeSelectorContext ctx);

    /**
     * Exit a parse tree produced by the {@code attributeSelector}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void exitAttributeSelector(DataWeaveParser.AttributeSelectorContext ctx);

    /**
     * Enter a parse tree produced by the {@code descendantsSelector}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void enterDescendantsSelector(DataWeaveParser.DescendantsSelectorContext ctx);

    /**
     * Exit a parse tree produced by the {@code descendantsSelector}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void exitDescendantsSelector(DataWeaveParser.DescendantsSelectorContext ctx);

    /**
     * Enter a parse tree produced by the {@code groupedExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void enterGroupedExpression(DataWeaveParser.GroupedExpressionContext ctx);

    /**
     * Exit a parse tree produced by the {@code groupedExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void exitGroupedExpression(DataWeaveParser.GroupedExpressionContext ctx);

    /**
     * Enter a parse tree produced by the {@code objectExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void enterObjectExpression(DataWeaveParser.ObjectExpressionContext ctx);

    /**
     * Exit a parse tree produced by the {@code objectExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void exitObjectExpression(DataWeaveParser.ObjectExpressionContext ctx);

    /**
     * Enter a parse tree produced by the {@code existenceQuerySelector}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void enterExistenceQuerySelector(DataWeaveParser.ExistenceQuerySelectorContext ctx);

    /**
     * Exit a parse tree produced by the {@code existenceQuerySelector}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void exitExistenceQuerySelector(DataWeaveParser.ExistenceQuerySelectorContext ctx);

    /**
     * Enter a parse tree produced by the {@code singleValueSelector}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void enterSingleValueSelector(DataWeaveParser.SingleValueSelectorContext ctx);

    /**
     * Exit a parse tree produced by the {@code singleValueSelector}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void exitSingleValueSelector(DataWeaveParser.SingleValueSelectorContext ctx);

    /**
     * Enter a parse tree produced by the {@code sizeOfExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void enterSizeOfExpression(DataWeaveParser.SizeOfExpressionContext ctx);

    /**
     * Exit a parse tree produced by the {@code sizeOfExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void exitSizeOfExpression(DataWeaveParser.SizeOfExpressionContext ctx);

    /**
     * Enter a parse tree produced by the {@code functionCallExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void enterFunctionCallExpression(DataWeaveParser.FunctionCallExpressionContext ctx);

    /**
     * Exit a parse tree produced by the {@code functionCallExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void exitFunctionCallExpression(DataWeaveParser.FunctionCallExpressionContext ctx);

    /**
     * Enter a parse tree produced by the {@code multiValueSelector}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void enterMultiValueSelector(DataWeaveParser.MultiValueSelectorContext ctx);

    /**
     * Exit a parse tree produced by the {@code multiValueSelector}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void exitMultiValueSelector(DataWeaveParser.MultiValueSelectorContext ctx);

    /**
     * Enter a parse tree produced by the {@code literalExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void enterLiteralExpression(DataWeaveParser.LiteralExpressionContext ctx);

    /**
     * Exit a parse tree produced by the {@code literalExpression}
     * labeled alternative in {@link DataWeaveParser}.
     *
     * @param ctx the parse tree
     */
    void exitLiteralExpression(DataWeaveParser.LiteralExpressionContext ctx);

    /**
     * Enter a parse tree produced by the {@code singleParameterImplicitLambda}
     * labeled alternative in {@link DataWeaveParser#implicitLambdaExpression}.
     *
     * @param ctx the parse tree
     */
    void enterSingleParameterImplicitLambda(DataWeaveParser.SingleParameterImplicitLambdaContext ctx);

    /**
     * Exit a parse tree produced by the {@code singleParameterImplicitLambda}
     * labeled alternative in {@link DataWeaveParser#implicitLambdaExpression}.
     *
     * @param ctx the parse tree
     */
    void exitSingleParameterImplicitLambda(DataWeaveParser.SingleParameterImplicitLambdaContext ctx);

    /**
     * Enter a parse tree produced by the {@code multiParameterImplicitLambda}
     * labeled alternative in {@link DataWeaveParser#implicitLambdaExpression}.
     *
     * @param ctx the parse tree
     */
    void enterMultiParameterImplicitLambda(DataWeaveParser.MultiParameterImplicitLambdaContext ctx);

    /**
     * Exit a parse tree produced by the {@code multiParameterImplicitLambda}
     * labeled alternative in {@link DataWeaveParser#implicitLambdaExpression}.
     *
     * @param ctx the parse tree
     */
    void exitMultiParameterImplicitLambda(DataWeaveParser.MultiParameterImplicitLambdaContext ctx);

    /**
     * Enter a parse tree produced by {@link DataWeaveParser#builtInFunctionCall}.
     *
     * @param ctx the parse tree
     */
    void enterBuiltInFunctionCall(DataWeaveParser.BuiltInFunctionCallContext ctx);

    /**
     * Exit a parse tree produced by {@link DataWeaveParser#builtInFunctionCall}.
     *
     * @param ctx the parse tree
     */
    void exitBuiltInFunctionCall(DataWeaveParser.BuiltInFunctionCallContext ctx);

    /**
     * Enter a parse tree produced by {@link DataWeaveParser#inlineLambda}.
     *
     * @param ctx the parse tree
     */
    void enterInlineLambda(DataWeaveParser.InlineLambdaContext ctx);

    /**
     * Exit a parse tree produced by {@link DataWeaveParser#inlineLambda}.
     *
     * @param ctx the parse tree
     */
    void exitInlineLambda(DataWeaveParser.InlineLambdaContext ctx);

    /**
     * Enter a parse tree produced by {@link DataWeaveParser#functionParameters}.
     *
     * @param ctx the parse tree
     */
    void enterFunctionParameters(DataWeaveParser.FunctionParametersContext ctx);

    /**
     * Exit a parse tree produced by {@link DataWeaveParser#functionParameters}.
     *
     * @param ctx the parse tree
     */
    void exitFunctionParameters(DataWeaveParser.FunctionParametersContext ctx);

    /**
     * Enter a parse tree produced by {@link DataWeaveParser#literal}.
     *
     * @param ctx the parse tree
     */
    void enterLiteral(DataWeaveParser.LiteralContext ctx);

    /**
     * Exit a parse tree produced by {@link DataWeaveParser#literal}.
     *
     * @param ctx the parse tree
     */
    void exitLiteral(DataWeaveParser.LiteralContext ctx);

    /**
     * Enter a parse tree produced by {@link DataWeaveParser#array}.
     *
     * @param ctx the parse tree
     */
    void enterArray(DataWeaveParser.ArrayContext ctx);

    /**
     * Exit a parse tree produced by {@link DataWeaveParser#array}.
     *
     * @param ctx the parse tree
     */
    void exitArray(DataWeaveParser.ArrayContext ctx);

    /**
     * Enter a parse tree produced by the {@code multiKeyValueObject}
     * labeled alternative in {@link DataWeaveParser#object}.
     *
     * @param ctx the parse tree
     */
    void enterMultiKeyValueObject(DataWeaveParser.MultiKeyValueObjectContext ctx);

    /**
     * Exit a parse tree produced by the {@code multiKeyValueObject}
     * labeled alternative in {@link DataWeaveParser#object}.
     *
     * @param ctx the parse tree
     */
    void exitMultiKeyValueObject(DataWeaveParser.MultiKeyValueObjectContext ctx);

    /**
     * Enter a parse tree produced by the {@code singleKeyValueObject}
     * labeled alternative in {@link DataWeaveParser#object}.
     *
     * @param ctx the parse tree
     */
    void enterSingleKeyValueObject(DataWeaveParser.SingleKeyValueObjectContext ctx);

    /**
     * Exit a parse tree produced by the {@code singleKeyValueObject}
     * labeled alternative in {@link DataWeaveParser#object}.
     *
     * @param ctx the parse tree
     */
    void exitSingleKeyValueObject(DataWeaveParser.SingleKeyValueObjectContext ctx);

    /**
     * Enter a parse tree produced by {@link DataWeaveParser#keyValue}.
     *
     * @param ctx the parse tree
     */
    void enterKeyValue(DataWeaveParser.KeyValueContext ctx);

    /**
     * Exit a parse tree produced by {@link DataWeaveParser#keyValue}.
     *
     * @param ctx the parse tree
     */
    void exitKeyValue(DataWeaveParser.KeyValueContext ctx);

    /**
     * Enter a parse tree produced by {@link DataWeaveParser#functionCall}.
     *
     * @param ctx the parse tree
     */
    void enterFunctionCall(DataWeaveParser.FunctionCallContext ctx);

    /**
     * Exit a parse tree produced by {@link DataWeaveParser#functionCall}.
     *
     * @param ctx the parse tree
     */
    void exitFunctionCall(DataWeaveParser.FunctionCallContext ctx);

    /**
     * Enter a parse tree produced by {@link DataWeaveParser#grouped}.
     *
     * @param ctx the parse tree
     */
    void enterGrouped(DataWeaveParser.GroupedContext ctx);

    /**
     * Exit a parse tree produced by {@link DataWeaveParser#grouped}.
     *
     * @param ctx the parse tree
     */
    void exitGrouped(DataWeaveParser.GroupedContext ctx);
}