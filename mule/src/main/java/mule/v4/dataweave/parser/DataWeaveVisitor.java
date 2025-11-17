// Generated from src/main/java/mule/v4/dataweave/parser/DataWeave.g4 by ANTLR 4.13.2
package mule.v4.dataweave.parser;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link DataWeaveParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface DataWeaveVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link DataWeaveParser#script}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScript(DataWeaveParser.ScriptContext ctx);
	/**
	 * Visit a parse tree produced by {@link DataWeaveParser#header}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHeader(DataWeaveParser.HeaderContext ctx);
	/**
	 * Visit a parse tree produced by {@link DataWeaveParser#directive}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDirective(DataWeaveParser.DirectiveContext ctx);
	/**
	 * Visit a parse tree produced by {@link DataWeaveParser#dwVersion}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDwVersion(DataWeaveParser.DwVersionContext ctx);
	/**
	 * Visit a parse tree produced by {@link DataWeaveParser#outputDirective}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOutputDirective(DataWeaveParser.OutputDirectiveContext ctx);
	/**
	 * Visit a parse tree produced by {@link DataWeaveParser#inputDirective}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInputDirective(DataWeaveParser.InputDirectiveContext ctx);
	/**
	 * Visit a parse tree produced by {@link DataWeaveParser#importDirective}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImportDirective(DataWeaveParser.ImportDirectiveContext ctx);
	/**
	 * Visit a parse tree produced by {@link DataWeaveParser#namespaceDirective}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNamespaceDirective(DataWeaveParser.NamespaceDirectiveContext ctx);
	/**
	 * Visit a parse tree produced by {@link DataWeaveParser#variableDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableDeclaration(DataWeaveParser.VariableDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link DataWeaveParser#functionDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionDeclaration(DataWeaveParser.FunctionDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link DataWeaveParser#typeDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeDeclaration(DataWeaveParser.TypeDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link DataWeaveParser#body}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBody(DataWeaveParser.BodyContext ctx);
	/**
	 * Visit a parse tree produced by {@link DataWeaveParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(DataWeaveParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code mapExpression}
	 * labeled alternative in {@link DataWeaveParser#operationExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMapExpression(DataWeaveParser.MapExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code operationExpressionWrapper}
	 * labeled alternative in {@link DataWeaveParser#operationExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperationExpressionWrapper(DataWeaveParser.OperationExpressionWrapperContext ctx);
	/**
	 * Visit a parse tree produced by the {@code filterExpression}
	 * labeled alternative in {@link DataWeaveParser#operationExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFilterExpression(DataWeaveParser.FilterExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code groupByExpression}
	 * labeled alternative in {@link DataWeaveParser#operationExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGroupByExpression(DataWeaveParser.GroupByExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code replaceExpression}
	 * labeled alternative in {@link DataWeaveParser#operationExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReplaceExpression(DataWeaveParser.ReplaceExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code concatExpression}
	 * labeled alternative in {@link DataWeaveParser#operationExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConcatExpression(DataWeaveParser.ConcatExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link DataWeaveParser#implicitLambdaExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImplicitLambdaExpression(DataWeaveParser.ImplicitLambdaExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link DataWeaveParser#inlineLambda}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInlineLambda(DataWeaveParser.InlineLambdaContext ctx);
	/**
	 * Visit a parse tree produced by {@link DataWeaveParser#functionParameters}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionParameters(DataWeaveParser.FunctionParametersContext ctx);
	/**
	 * Visit a parse tree produced by {@link DataWeaveParser#logicalOrExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalOrExpression(DataWeaveParser.LogicalOrExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link DataWeaveParser#logicalAndExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalAndExpression(DataWeaveParser.LogicalAndExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link DataWeaveParser#equalityExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEqualityExpression(DataWeaveParser.EqualityExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code relationalComparison}
	 * labeled alternative in {@link DataWeaveParser#relationalExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelationalComparison(DataWeaveParser.RelationalComparisonContext ctx);
	/**
	 * Visit a parse tree produced by the {@code isExpression}
	 * labeled alternative in {@link DataWeaveParser#relationalExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIsExpression(DataWeaveParser.IsExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link DataWeaveParser#additiveExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAdditiveExpression(DataWeaveParser.AdditiveExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link DataWeaveParser#multiplicativeExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiplicativeExpression(DataWeaveParser.MultiplicativeExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link DataWeaveParser#typeCoercionExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeCoercionExpression(DataWeaveParser.TypeCoercionExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link DataWeaveParser#formatOption}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatOption(DataWeaveParser.FormatOptionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code sizeOfExpression}
	 * labeled alternative in {@link DataWeaveParser#unaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSizeOfExpression(DataWeaveParser.SizeOfExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code sizeOfExpressionWithParentheses}
	 * labeled alternative in {@link DataWeaveParser#unaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSizeOfExpressionWithParentheses(DataWeaveParser.SizeOfExpressionWithParenthesesContext ctx);
	/**
	 * Visit a parse tree produced by the {@code upperExpression}
	 * labeled alternative in {@link DataWeaveParser#unaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUpperExpression(DataWeaveParser.UpperExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code upperExpressionWithParentheses}
	 * labeled alternative in {@link DataWeaveParser#unaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUpperExpressionWithParentheses(DataWeaveParser.UpperExpressionWithParenthesesContext ctx);
	/**
	 * Visit a parse tree produced by the {@code lowerExpression}
	 * labeled alternative in {@link DataWeaveParser#unaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLowerExpression(DataWeaveParser.LowerExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code lowerExpressionWithParentheses}
	 * labeled alternative in {@link DataWeaveParser#unaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLowerExpressionWithParentheses(DataWeaveParser.LowerExpressionWithParenthesesContext ctx);
	/**
	 * Visit a parse tree produced by the {@code notExpression}
	 * labeled alternative in {@link DataWeaveParser#unaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNotExpression(DataWeaveParser.NotExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code negativeExpression}
	 * labeled alternative in {@link DataWeaveParser#unaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNegativeExpression(DataWeaveParser.NegativeExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code primaryExpressionWrapper}
	 * labeled alternative in {@link DataWeaveParser#unaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimaryExpressionWrapper(DataWeaveParser.PrimaryExpressionWrapperContext ctx);
	/**
	 * Visit a parse tree produced by the {@code lambdaExpression}
	 * labeled alternative in {@link DataWeaveParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLambdaExpression(DataWeaveParser.LambdaExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code arrayExpression}
	 * labeled alternative in {@link DataWeaveParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayExpression(DataWeaveParser.ArrayExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code selectorExpressionWrapperWithDefault}
	 * labeled alternative in {@link DataWeaveParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectorExpressionWrapperWithDefault(DataWeaveParser.SelectorExpressionWrapperWithDefaultContext ctx);
	/**
	 * Visit a parse tree produced by the {@code identifierExpression}
	 * labeled alternative in {@link DataWeaveParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdentifierExpression(DataWeaveParser.IdentifierExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code selectorExpressionWrapper}
	 * labeled alternative in {@link DataWeaveParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectorExpressionWrapper(DataWeaveParser.SelectorExpressionWrapperContext ctx);
	/**
	 * Visit a parse tree produced by the {@code indexIdentifierExpression}
	 * labeled alternative in {@link DataWeaveParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIndexIdentifierExpression(DataWeaveParser.IndexIdentifierExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code groupedExpression}
	 * labeled alternative in {@link DataWeaveParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGroupedExpression(DataWeaveParser.GroupedExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code objectExpression}
	 * labeled alternative in {@link DataWeaveParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObjectExpression(DataWeaveParser.ObjectExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ifElseCondition}
	 * labeled alternative in {@link DataWeaveParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfElseCondition(DataWeaveParser.IfElseConditionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code builtInFunctionExpression}
	 * labeled alternative in {@link DataWeaveParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBuiltInFunctionExpression(DataWeaveParser.BuiltInFunctionExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code functionCallExpression}
	 * labeled alternative in {@link DataWeaveParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionCallExpression(DataWeaveParser.FunctionCallExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code literalExpression}
	 * labeled alternative in {@link DataWeaveParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteralExpression(DataWeaveParser.LiteralExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code valueIdentifierExpression}
	 * labeled alternative in {@link DataWeaveParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValueIdentifierExpression(DataWeaveParser.ValueIdentifierExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code nowFunction}
	 * labeled alternative in {@link DataWeaveParser#builtInFunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNowFunction(DataWeaveParser.NowFunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link DataWeaveParser#grouped}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGrouped(DataWeaveParser.GroupedContext ctx);
	/**
	 * Visit a parse tree produced by the {@code singleValueSelector}
	 * labeled alternative in {@link DataWeaveParser#selectorExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSingleValueSelector(DataWeaveParser.SingleValueSelectorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code keySelector}
	 * labeled alternative in {@link DataWeaveParser#selectorExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitKeySelector(DataWeaveParser.KeySelectorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code multiValueSelector}
	 * labeled alternative in {@link DataWeaveParser#selectorExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiValueSelector(DataWeaveParser.MultiValueSelectorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code descendantsSelector}
	 * labeled alternative in {@link DataWeaveParser#selectorExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDescendantsSelector(DataWeaveParser.DescendantsSelectorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code indexedSelector}
	 * labeled alternative in {@link DataWeaveParser#selectorExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIndexedSelector(DataWeaveParser.IndexedSelectorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code attributeSelector}
	 * labeled alternative in {@link DataWeaveParser#selectorExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttributeSelector(DataWeaveParser.AttributeSelectorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code existenceQuerySelector}
	 * labeled alternative in {@link DataWeaveParser#selectorExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExistenceQuerySelector(DataWeaveParser.ExistenceQuerySelectorContext ctx);
	/**
	 * Visit a parse tree produced by {@link DataWeaveParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteral(DataWeaveParser.LiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link DataWeaveParser#array}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArray(DataWeaveParser.ArrayContext ctx);
	/**
	 * Visit a parse tree produced by the {@code multiFieldObject}
	 * labeled alternative in {@link DataWeaveParser#object}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiFieldObject(DataWeaveParser.MultiFieldObjectContext ctx);
	/**
	 * Visit a parse tree produced by the {@code singleFieldObject}
	 * labeled alternative in {@link DataWeaveParser#object}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSingleFieldObject(DataWeaveParser.SingleFieldObjectContext ctx);
	/**
	 * Visit a parse tree produced by the {@code unquotedKeyField}
	 * labeled alternative in {@link DataWeaveParser#objectField}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnquotedKeyField(DataWeaveParser.UnquotedKeyFieldContext ctx);
	/**
	 * Visit a parse tree produced by the {@code quotedKeyField}
	 * labeled alternative in {@link DataWeaveParser#objectField}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQuotedKeyField(DataWeaveParser.QuotedKeyFieldContext ctx);
	/**
	 * Visit a parse tree produced by the {@code dynamicKeyField}
	 * labeled alternative in {@link DataWeaveParser#objectField}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDynamicKeyField(DataWeaveParser.DynamicKeyFieldContext ctx);
	/**
	 * Visit a parse tree produced by {@link DataWeaveParser#functionCall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionCall(DataWeaveParser.FunctionCallContext ctx);
	/**
	 * Visit a parse tree produced by the {@code namedType}
	 * labeled alternative in {@link DataWeaveParser#typeExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNamedType(DataWeaveParser.NamedTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code stringType}
	 * labeled alternative in {@link DataWeaveParser#typeExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringType(DataWeaveParser.StringTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code booleanType}
	 * labeled alternative in {@link DataWeaveParser#typeExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleanType(DataWeaveParser.BooleanTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code numberType}
	 * labeled alternative in {@link DataWeaveParser#typeExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumberType(DataWeaveParser.NumberTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code regexType}
	 * labeled alternative in {@link DataWeaveParser#typeExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRegexType(DataWeaveParser.RegexTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code dateType}
	 * labeled alternative in {@link DataWeaveParser#typeExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDateType(DataWeaveParser.DateTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code dateTimeType}
	 * labeled alternative in {@link DataWeaveParser#typeExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDateTimeType(DataWeaveParser.DateTimeTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code localDateTimeType}
	 * labeled alternative in {@link DataWeaveParser#typeExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLocalDateTimeType(DataWeaveParser.LocalDateTimeTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code localTimeType}
	 * labeled alternative in {@link DataWeaveParser#typeExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLocalTimeType(DataWeaveParser.LocalTimeTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code timeType}
	 * labeled alternative in {@link DataWeaveParser#typeExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTimeType(DataWeaveParser.TimeTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code periodType}
	 * labeled alternative in {@link DataWeaveParser#typeExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPeriodType(DataWeaveParser.PeriodTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code objectType}
	 * labeled alternative in {@link DataWeaveParser#typeExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObjectType(DataWeaveParser.ObjectTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code anyType}
	 * labeled alternative in {@link DataWeaveParser#typeExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnyType(DataWeaveParser.AnyTypeContext ctx);
}