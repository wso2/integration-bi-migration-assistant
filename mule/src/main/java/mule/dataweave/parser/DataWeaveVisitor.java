/*
 *  Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
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

// Generated from src/main/java/dataweave/parser/DataWeave.g4 by ANTLR 4.13.2
package mule.dataweave.parser;

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
     * Visit a parse tree produced by the {@code expressionWrapper}
     * labeled alternative in {@link DataWeaveParser#expression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitExpressionWrapper(DataWeaveParser.ExpressionWrapperContext ctx);

    /**
     * Visit a parse tree produced by the {@code conditionalExpressionWrapper}
     * labeled alternative in {@link DataWeaveParser#expression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitConditionalExpressionWrapper(DataWeaveParser.ConditionalExpressionWrapperContext ctx);

    /**
     * Visit a parse tree produced by the {@code whenCondition}
     * labeled alternative in {@link DataWeaveParser#conditionalExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitWhenCondition(DataWeaveParser.WhenConditionContext ctx);

    /**
     * Visit a parse tree produced by the {@code unlessCondition}
     * labeled alternative in {@link DataWeaveParser#conditionalExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitUnlessCondition(DataWeaveParser.UnlessConditionContext ctx);

    /**
     * Visit a parse tree produced by {@link DataWeaveParser#implicitLambdaExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitImplicitLambdaExpression(DataWeaveParser.ImplicitLambdaExpressionContext ctx);

    /**
     * Visit a parse tree produced by {@link DataWeaveParser#inlineLambda}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitInlineLambda(DataWeaveParser.InlineLambdaContext ctx);

    /**
     * Visit a parse tree produced by {@link DataWeaveParser#functionParameters}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFunctionParameters(DataWeaveParser.FunctionParametersContext ctx);

    /**
     * Visit a parse tree produced by the {@code defaultExpressionWrapper}
     * labeled alternative in {@link DataWeaveParser#defaultExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDefaultExpressionWrapper(DataWeaveParser.DefaultExpressionWrapperContext ctx);

    /**
     * Visit a parse tree produced by the {@code filterExpression}
     * labeled alternative in {@link DataWeaveParser#defaultExpressionRest}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFilterExpression(DataWeaveParser.FilterExpressionContext ctx);

    /**
     * Visit a parse tree produced by the {@code mapExpression}
     * labeled alternative in {@link DataWeaveParser#defaultExpressionRest}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMapExpression(DataWeaveParser.MapExpressionContext ctx);

    /**
     * Visit a parse tree produced by the {@code groupByExpression}
     * labeled alternative in {@link DataWeaveParser#defaultExpressionRest}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitGroupByExpression(DataWeaveParser.GroupByExpressionContext ctx);

    /**
     * Visit a parse tree produced by the {@code replaceExpression}
     * labeled alternative in {@link DataWeaveParser#defaultExpressionRest}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitReplaceExpression(DataWeaveParser.ReplaceExpressionContext ctx);

    /**
     * Visit a parse tree produced by the {@code concatExpression}
     * labeled alternative in {@link DataWeaveParser#defaultExpressionRest}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitConcatExpression(DataWeaveParser.ConcatExpressionContext ctx);

    /**
     * Visit a parse tree produced by the {@code defaultExpressionEnd}
     * labeled alternative in {@link DataWeaveParser#defaultExpressionRest}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDefaultExpressionEnd(DataWeaveParser.DefaultExpressionEndContext ctx);

    /**
     * Visit a parse tree produced by {@link DataWeaveParser#logicalOrExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLogicalOrExpression(DataWeaveParser.LogicalOrExpressionContext ctx);

    /**
     * Visit a parse tree produced by {@link DataWeaveParser#logicalAndExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLogicalAndExpression(DataWeaveParser.LogicalAndExpressionContext ctx);

    /**
     * Visit a parse tree produced by {@link DataWeaveParser#equalityExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEqualityExpression(DataWeaveParser.EqualityExpressionContext ctx);

    /**
     * Visit a parse tree produced by {@link DataWeaveParser#relationalExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRelationalExpression(DataWeaveParser.RelationalExpressionContext ctx);

    /**
     * Visit a parse tree produced by {@link DataWeaveParser#additiveExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitAdditiveExpression(DataWeaveParser.AdditiveExpressionContext ctx);

    /**
     * Visit a parse tree produced by {@link DataWeaveParser#multiplicativeExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMultiplicativeExpression(DataWeaveParser.MultiplicativeExpressionContext ctx);

    /**
     * Visit a parse tree produced by {@link DataWeaveParser#typeCoercionExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTypeCoercionExpression(DataWeaveParser.TypeCoercionExpressionContext ctx);

    /**
     * Visit a parse tree produced by {@link DataWeaveParser#formatOption}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFormatOption(DataWeaveParser.FormatOptionContext ctx);

    /**
     * Visit a parse tree produced by the {@code sizeOfExpression}
     * labeled alternative in {@link DataWeaveParser#unaryExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSizeOfExpression(DataWeaveParser.SizeOfExpressionContext ctx);

    /**
     * Visit a parse tree produced by the {@code sizeOfExpressionWithParentheses}
     * labeled alternative in {@link DataWeaveParser#unaryExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSizeOfExpressionWithParentheses(DataWeaveParser.SizeOfExpressionWithParenthesesContext ctx);

    /**
     * Visit a parse tree produced by the {@code upperExpression}
     * labeled alternative in {@link DataWeaveParser#unaryExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitUpperExpression(DataWeaveParser.UpperExpressionContext ctx);

    /**
     * Visit a parse tree produced by the {@code upperExpressionWithParentheses}
     * labeled alternative in {@link DataWeaveParser#unaryExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitUpperExpressionWithParentheses(DataWeaveParser.UpperExpressionWithParenthesesContext ctx);

    /**
     * Visit a parse tree produced by the {@code lowerExpression}
     * labeled alternative in {@link DataWeaveParser#unaryExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLowerExpression(DataWeaveParser.LowerExpressionContext ctx);

    /**
     * Visit a parse tree produced by the {@code lowerExpressionWithParentheses}
     * labeled alternative in {@link DataWeaveParser#unaryExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLowerExpressionWithParentheses(DataWeaveParser.LowerExpressionWithParenthesesContext ctx);

    /**
     * Visit a parse tree produced by the {@code primaryExpressionWrapper}
     * labeled alternative in {@link DataWeaveParser#unaryExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitPrimaryExpressionWrapper(DataWeaveParser.PrimaryExpressionWrapperContext ctx);

    /**
     * Visit a parse tree produced by the {@code objectExpression}
     * labeled alternative in {@link DataWeaveParser#primaryExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitObjectExpression(DataWeaveParser.ObjectExpressionContext ctx);

    /**
     * Visit a parse tree produced by the {@code lambdaExpression}
     * labeled alternative in {@link DataWeaveParser#primaryExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLambdaExpression(DataWeaveParser.LambdaExpressionContext ctx);

    /**
     * Visit a parse tree produced by the {@code arrayExpression}
     * labeled alternative in {@link DataWeaveParser#primaryExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitArrayExpression(DataWeaveParser.ArrayExpressionContext ctx);

    /**
     * Visit a parse tree produced by the {@code identifierExpression}
     * labeled alternative in {@link DataWeaveParser#primaryExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitIdentifierExpression(DataWeaveParser.IdentifierExpressionContext ctx);

    /**
     * Visit a parse tree produced by the {@code functionCallExpression}
     * labeled alternative in {@link DataWeaveParser#primaryExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFunctionCallExpression(DataWeaveParser.FunctionCallExpressionContext ctx);

    /**
     * Visit a parse tree produced by the {@code selectorExpressionWrapper}
     * labeled alternative in {@link DataWeaveParser#primaryExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSelectorExpressionWrapper(DataWeaveParser.SelectorExpressionWrapperContext ctx);

    /**
     * Visit a parse tree produced by the {@code literalExpression}
     * labeled alternative in {@link DataWeaveParser#primaryExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLiteralExpression(DataWeaveParser.LiteralExpressionContext ctx);

    /**
     * Visit a parse tree produced by the {@code indexIdentifierExpression}
     * labeled alternative in {@link DataWeaveParser#primaryExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitIndexIdentifierExpression(DataWeaveParser.IndexIdentifierExpressionContext ctx);

    /**
     * Visit a parse tree produced by the {@code valueIdentifierExpression}
     * labeled alternative in {@link DataWeaveParser#primaryExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitValueIdentifierExpression(DataWeaveParser.ValueIdentifierExpressionContext ctx);

    /**
     * Visit a parse tree produced by the {@code groupedExpression}
     * labeled alternative in {@link DataWeaveParser#primaryExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitGroupedExpression(DataWeaveParser.GroupedExpressionContext ctx);

    /**
     * Visit a parse tree produced by {@link DataWeaveParser#grouped}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitGrouped(DataWeaveParser.GroupedContext ctx);

    /**
     * Visit a parse tree produced by the {@code singleValueSelector}
     * labeled alternative in {@link DataWeaveParser#selectorExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSingleValueSelector(DataWeaveParser.SingleValueSelectorContext ctx);

    /**
     * Visit a parse tree produced by the {@code multiValueSelector}
     * labeled alternative in {@link DataWeaveParser#selectorExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMultiValueSelector(DataWeaveParser.MultiValueSelectorContext ctx);

    /**
     * Visit a parse tree produced by the {@code descendantsSelector}
     * labeled alternative in {@link DataWeaveParser#selectorExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDescendantsSelector(DataWeaveParser.DescendantsSelectorContext ctx);

    /**
     * Visit a parse tree produced by the {@code indexedSelector}
     * labeled alternative in {@link DataWeaveParser#selectorExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitIndexedSelector(DataWeaveParser.IndexedSelectorContext ctx);

    /**
     * Visit a parse tree produced by the {@code attributeSelector}
     * labeled alternative in {@link DataWeaveParser#selectorExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitAttributeSelector(DataWeaveParser.AttributeSelectorContext ctx);

    /**
     * Visit a parse tree produced by the {@code existenceQuerySelector}
     * labeled alternative in {@link DataWeaveParser#selectorExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitExistenceQuerySelector(DataWeaveParser.ExistenceQuerySelectorContext ctx);

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
     * Visit a parse tree produced by the {@code multiKeyValueObject}
     * labeled alternative in {@link DataWeaveParser#object}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMultiKeyValueObject(DataWeaveParser.MultiKeyValueObjectContext ctx);

    /**
     * Visit a parse tree produced by the {@code singleKeyValueObject}
     * labeled alternative in {@link DataWeaveParser#object}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSingleKeyValueObject(DataWeaveParser.SingleKeyValueObjectContext ctx);

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

    /**
     * Visit a parse tree produced by {@link DataWeaveParser#typeExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTypeExpression(DataWeaveParser.TypeExpressionContext ctx);
}
