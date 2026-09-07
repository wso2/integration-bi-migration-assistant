// Generated from src/main/java/mule/v4/dataweave/parser/DataWeave.g4 by ANTLR 4.13.2
package mule.v4.dataweave.parser;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class DataWeaveParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, VAR=14, FUNCTION=15, IMPORT=16, 
		NAMESPACE=17, OUTPUT=18, INPUT=19, DW=20, TYPE=21, ASSIGN=22, ARROW=23, 
		BOOLEAN=24, AND=25, OR=26, NOT=27, IF=28, ELSE=29, UNLESS=30, USING=31, 
		AS=32, IS=33, NULL=34, DEFAULT=35, CASE=36, THROW=37, DO=38, FOR=39, YIELD=40, 
		ENUM=41, PRIVATE=42, ASYNC=43, MAP=44, FILTER=45, GROUP_BY=46, SIZE_OF=47, 
		UPPER=48, LOWER=49, REPLACE=50, WITH=51, FROM=52, NOW=53, OPERATOR_EQUALITY=54, 
		OPERATOR_RELATIONAL=55, OPERATOR_MULTIPLICATIVE=56, OPERATOR_ADDITIVE=57, 
		MINUS=58, OPERATOR_RANGE=59, CONCAT=60, IDENTIFIER=61, INDEX_IDENTIFIER=62, 
		VALUE_IDENTIFIER=63, URL=64, MEDIA_TYPE=65, NUMBER=66, BLOCK_COMMENT=67, 
		STRING=68, DATE=69, REGEX=70, DOT=71, DOUBLE_COLON=72, COLON=73, COMMA=74, 
		LCURLY=75, RCURLY=76, LSQUARE=77, RSQUARE=78, LPAREN=79, RPAREN=80, SEPARATOR=81, 
		WS=82, NEWLINE=83, COMMENT=84, STAR=85, AT=86, QUESTION=87;
	public static final int
		RULE_script = 0, RULE_header = 1, RULE_directive = 2, RULE_dwVersion = 3, 
		RULE_outputDirective = 4, RULE_outputOption = 5, RULE_outputOptionValue = 6, 
		RULE_inputDirective = 7, RULE_importDirective = 8, RULE_importSpec = 9, 
		RULE_namespaceDirective = 10, RULE_variableDeclaration = 11, RULE_functionDeclaration = 12, 
		RULE_typeDeclaration = 13, RULE_body = 14, RULE_expression = 15, RULE_operationExpression = 16, 
		RULE_defaultExpression = 17, RULE_implicitLambdaExpression = 18, RULE_inlineLambda = 19, 
		RULE_functionParameters = 20, RULE_functionParameter = 21, RULE_logicalOrExpression = 22, 
		RULE_logicalAndExpression = 23, RULE_equalityExpression = 24, RULE_relationalExpression = 25, 
		RULE_additiveExpression = 26, RULE_additiveOperator = 27, RULE_multiplicativeExpression = 28, 
		RULE_typeCoercionExpression = 29, RULE_formatOption = 30, RULE_unaryExpression = 31, 
		RULE_primaryExpression = 32, RULE_builtInFunction = 33, RULE_grouped = 34, 
		RULE_doBlock = 35, RULE_selectorExpression = 36, RULE_literal = 37, RULE_array = 38, 
		RULE_object = 39, RULE_objectField = 40, RULE_qualifiedIdentifier = 41, 
		RULE_functionCall = 42, RULE_typeExpression = 43;
	private static String[] makeRuleNames() {
		return new String[] {
			"script", "header", "directive", "dwVersion", "outputDirective", "outputOption", 
			"outputOptionValue", "inputDirective", "importDirective", "importSpec", 
			"namespaceDirective", "variableDeclaration", "functionDeclaration", "typeDeclaration", 
			"body", "expression", "operationExpression", "defaultExpression", "implicitLambdaExpression", 
			"inlineLambda", "functionParameters", "functionParameter", "logicalOrExpression", 
			"logicalAndExpression", "equalityExpression", "relationalExpression", 
			"additiveExpression", "additiveOperator", "multiplicativeExpression", 
			"typeCoercionExpression", "formatOption", "unaryExpression", "primaryExpression", 
			"builtInFunction", "grouped", "doBlock", "selectorExpression", "literal", 
			"array", "object", "objectField", "qualifiedIdentifier", "functionCall", 
			"typeExpression"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'String'", "'Boolean'", "'Number'", "'Regex'", "'Null'", "'Date'", 
			"'DateTime'", "'LocalDateTime'", "'LocalTime'", "'Time'", "'Period'", 
			"'Object'", "'Any'", "'var'", "'fun'", "'import'", "'ns'", "'output'", 
			"'input'", "'%dw'", "'type'", "'='", "'->'", null, "'and'", "'or'", null, 
			"'if'", "'else'", "'unless'", "'using'", "'as'", "'is'", "'null'", "'default'", 
			"'case'", "'throw'", "'do'", "'for'", "'yield'", "'enum'", "'private'", 
			"'async'", "'map'", "'filter'", "'groupBy'", "'sizeOf'", "'upper'", "'lower'", 
			"'replace'", "'with'", "'from'", "'now'", null, null, null, null, "'-'", 
			"'..'", "'++'", null, "'$$'", "'$'", null, null, null, null, null, null, 
			null, "'.'", "'::'", "':'", "','", "'{'", "'}'", "'['", "']'", "'('", 
			"')'", "'---'", null, null, null, "'*'", "'@'", "'?'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, "VAR", "FUNCTION", "IMPORT", "NAMESPACE", "OUTPUT", "INPUT", 
			"DW", "TYPE", "ASSIGN", "ARROW", "BOOLEAN", "AND", "OR", "NOT", "IF", 
			"ELSE", "UNLESS", "USING", "AS", "IS", "NULL", "DEFAULT", "CASE", "THROW", 
			"DO", "FOR", "YIELD", "ENUM", "PRIVATE", "ASYNC", "MAP", "FILTER", "GROUP_BY", 
			"SIZE_OF", "UPPER", "LOWER", "REPLACE", "WITH", "FROM", "NOW", "OPERATOR_EQUALITY", 
			"OPERATOR_RELATIONAL", "OPERATOR_MULTIPLICATIVE", "OPERATOR_ADDITIVE", 
			"MINUS", "OPERATOR_RANGE", "CONCAT", "IDENTIFIER", "INDEX_IDENTIFIER", 
			"VALUE_IDENTIFIER", "URL", "MEDIA_TYPE", "NUMBER", "BLOCK_COMMENT", "STRING", 
			"DATE", "REGEX", "DOT", "DOUBLE_COLON", "COLON", "COMMA", "LCURLY", "RCURLY", 
			"LSQUARE", "RSQUARE", "LPAREN", "RPAREN", "SEPARATOR", "WS", "NEWLINE", 
			"COMMENT", "STAR", "AT", "QUESTION"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "DataWeave.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public DataWeaveParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ScriptContext extends ParserRuleContext {
		public HeaderContext header() {
			return getRuleContext(HeaderContext.class,0);
		}
		public TerminalNode EOF() { return getToken(DataWeaveParser.EOF, 0); }
		public TerminalNode SEPARATOR() { return getToken(DataWeaveParser.SEPARATOR, 0); }
		public List<TerminalNode> NEWLINE() { return getTokens(DataWeaveParser.NEWLINE); }
		public TerminalNode NEWLINE(int i) {
			return getToken(DataWeaveParser.NEWLINE, i);
		}
		public BodyContext body() {
			return getRuleContext(BodyContext.class,0);
		}
		public ScriptContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_script; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterScript(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitScript(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitScript(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ScriptContext script() throws RecognitionException {
		ScriptContext _localctx = new ScriptContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_script);
		int _la;
		try {
			setState(115);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case VAR:
			case FUNCTION:
			case IMPORT:
			case NAMESPACE:
			case OUTPUT:
			case INPUT:
			case DW:
			case TYPE:
				enterOuterAlt(_localctx, 1);
				{
				setState(88);
				header();
				setState(93);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==SEPARATOR) {
					{
					setState(89);
					match(SEPARATOR);
					setState(91);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (((((_la - 24)) & ~0x3f) == 0 && ((1L << (_la - 24)) & 47416319284364313L) != 0)) {
						{
						setState(90);
						body();
						}
					}

					}
				}

				setState(98);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==NEWLINE) {
					{
					{
					setState(95);
					match(NEWLINE);
					}
					}
					setState(100);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(101);
				match(EOF);
				}
				break;
			case BOOLEAN:
			case NOT:
			case IF:
			case NULL:
			case DO:
			case SIZE_OF:
			case UPPER:
			case LOWER:
			case NOW:
			case MINUS:
			case IDENTIFIER:
			case INDEX_IDENTIFIER:
			case VALUE_IDENTIFIER:
			case NUMBER:
			case STRING:
			case DATE:
			case REGEX:
			case LCURLY:
			case LSQUARE:
			case LPAREN:
			case SEPARATOR:
				enterOuterAlt(_localctx, 2);
				{
				setState(104);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==SEPARATOR) {
					{
					setState(103);
					match(SEPARATOR);
					}
				}

				setState(106);
				body();
				setState(110);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==NEWLINE) {
					{
					{
					setState(107);
					match(NEWLINE);
					}
					}
					setState(112);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(113);
				match(EOF);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class HeaderContext extends ParserRuleContext {
		public List<DirectiveContext> directive() {
			return getRuleContexts(DirectiveContext.class);
		}
		public DirectiveContext directive(int i) {
			return getRuleContext(DirectiveContext.class,i);
		}
		public List<TerminalNode> NEWLINE() { return getTokens(DataWeaveParser.NEWLINE); }
		public TerminalNode NEWLINE(int i) {
			return getToken(DataWeaveParser.NEWLINE, i);
		}
		public List<TerminalNode> WS() { return getTokens(DataWeaveParser.WS); }
		public TerminalNode WS(int i) {
			return getToken(DataWeaveParser.WS, i);
		}
		public HeaderContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_header; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterHeader(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitHeader(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitHeader(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HeaderContext header() throws RecognitionException {
		HeaderContext _localctx = new HeaderContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_header);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(124); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(117);
				directive();
				setState(121);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(118);
						_la = _input.LA(1);
						if ( !(_la==WS || _la==NEWLINE) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						}
						} 
					}
					setState(123);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
				}
				}
				}
				setState(126); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & 4177920L) != 0) );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DirectiveContext extends ParserRuleContext {
		public DwVersionContext dwVersion() {
			return getRuleContext(DwVersionContext.class,0);
		}
		public OutputDirectiveContext outputDirective() {
			return getRuleContext(OutputDirectiveContext.class,0);
		}
		public InputDirectiveContext inputDirective() {
			return getRuleContext(InputDirectiveContext.class,0);
		}
		public ImportDirectiveContext importDirective() {
			return getRuleContext(ImportDirectiveContext.class,0);
		}
		public NamespaceDirectiveContext namespaceDirective() {
			return getRuleContext(NamespaceDirectiveContext.class,0);
		}
		public VariableDeclarationContext variableDeclaration() {
			return getRuleContext(VariableDeclarationContext.class,0);
		}
		public FunctionDeclarationContext functionDeclaration() {
			return getRuleContext(FunctionDeclarationContext.class,0);
		}
		public TypeDeclarationContext typeDeclaration() {
			return getRuleContext(TypeDeclarationContext.class,0);
		}
		public DirectiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_directive; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterDirective(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitDirective(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitDirective(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DirectiveContext directive() throws RecognitionException {
		DirectiveContext _localctx = new DirectiveContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_directive);
		try {
			setState(136);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case DW:
				enterOuterAlt(_localctx, 1);
				{
				setState(128);
				dwVersion();
				}
				break;
			case OUTPUT:
				enterOuterAlt(_localctx, 2);
				{
				setState(129);
				outputDirective();
				}
				break;
			case INPUT:
				enterOuterAlt(_localctx, 3);
				{
				setState(130);
				inputDirective();
				}
				break;
			case IMPORT:
				enterOuterAlt(_localctx, 4);
				{
				setState(131);
				importDirective();
				}
				break;
			case NAMESPACE:
				enterOuterAlt(_localctx, 5);
				{
				setState(132);
				namespaceDirective();
				}
				break;
			case VAR:
				enterOuterAlt(_localctx, 6);
				{
				setState(133);
				variableDeclaration();
				}
				break;
			case FUNCTION:
				enterOuterAlt(_localctx, 7);
				{
				setState(134);
				functionDeclaration();
				}
				break;
			case TYPE:
				enterOuterAlt(_localctx, 8);
				{
				setState(135);
				typeDeclaration();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DwVersionContext extends ParserRuleContext {
		public TerminalNode DW() { return getToken(DataWeaveParser.DW, 0); }
		public TerminalNode NUMBER() { return getToken(DataWeaveParser.NUMBER, 0); }
		public DwVersionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dwVersion; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterDwVersion(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitDwVersion(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitDwVersion(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DwVersionContext dwVersion() throws RecognitionException {
		DwVersionContext _localctx = new DwVersionContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_dwVersion);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(138);
			match(DW);
			setState(139);
			match(NUMBER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class OutputDirectiveContext extends ParserRuleContext {
		public TerminalNode OUTPUT() { return getToken(DataWeaveParser.OUTPUT, 0); }
		public TerminalNode MEDIA_TYPE() { return getToken(DataWeaveParser.MEDIA_TYPE, 0); }
		public List<OutputOptionContext> outputOption() {
			return getRuleContexts(OutputOptionContext.class);
		}
		public OutputOptionContext outputOption(int i) {
			return getRuleContext(OutputOptionContext.class,i);
		}
		public OutputDirectiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_outputDirective; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterOutputDirective(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitOutputDirective(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitOutputDirective(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OutputDirectiveContext outputDirective() throws RecognitionException {
		OutputDirectiveContext _localctx = new OutputDirectiveContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_outputDirective);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(141);
			match(OUTPUT);
			setState(142);
			match(MEDIA_TYPE);
			setState(146);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==IDENTIFIER) {
				{
				{
				setState(143);
				outputOption();
				}
				}
				setState(148);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class OutputOptionContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(DataWeaveParser.IDENTIFIER, 0); }
		public TerminalNode ASSIGN() { return getToken(DataWeaveParser.ASSIGN, 0); }
		public OutputOptionValueContext outputOptionValue() {
			return getRuleContext(OutputOptionValueContext.class,0);
		}
		public OutputOptionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_outputOption; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterOutputOption(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitOutputOption(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitOutputOption(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OutputOptionContext outputOption() throws RecognitionException {
		OutputOptionContext _localctx = new OutputOptionContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_outputOption);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(149);
			match(IDENTIFIER);
			setState(150);
			match(ASSIGN);
			setState(151);
			outputOptionValue();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class OutputOptionValueContext extends ParserRuleContext {
		public TerminalNode STRING() { return getToken(DataWeaveParser.STRING, 0); }
		public TerminalNode BOOLEAN() { return getToken(DataWeaveParser.BOOLEAN, 0); }
		public TerminalNode NUMBER() { return getToken(DataWeaveParser.NUMBER, 0); }
		public TerminalNode IDENTIFIER() { return getToken(DataWeaveParser.IDENTIFIER, 0); }
		public OutputOptionValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_outputOptionValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterOutputOptionValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitOutputOptionValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitOutputOptionValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OutputOptionValueContext outputOptionValue() throws RecognitionException {
		OutputOptionValueContext _localctx = new OutputOptionValueContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_outputOptionValue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(153);
			_la = _input.LA(1);
			if ( !(((((_la - 24)) & ~0x3f) == 0 && ((1L << (_la - 24)) & 22127671508993L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class InputDirectiveContext extends ParserRuleContext {
		public TerminalNode INPUT() { return getToken(DataWeaveParser.INPUT, 0); }
		public TerminalNode IDENTIFIER() { return getToken(DataWeaveParser.IDENTIFIER, 0); }
		public TerminalNode MEDIA_TYPE() { return getToken(DataWeaveParser.MEDIA_TYPE, 0); }
		public InputDirectiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_inputDirective; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterInputDirective(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitInputDirective(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitInputDirective(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InputDirectiveContext inputDirective() throws RecognitionException {
		InputDirectiveContext _localctx = new InputDirectiveContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_inputDirective);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(155);
			match(INPUT);
			setState(156);
			match(IDENTIFIER);
			setState(157);
			match(MEDIA_TYPE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ImportDirectiveContext extends ParserRuleContext {
		public TerminalNode IMPORT() { return getToken(DataWeaveParser.IMPORT, 0); }
		public List<ImportSpecContext> importSpec() {
			return getRuleContexts(ImportSpecContext.class);
		}
		public ImportSpecContext importSpec(int i) {
			return getRuleContext(ImportSpecContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(DataWeaveParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(DataWeaveParser.COMMA, i);
		}
		public TerminalNode FROM() { return getToken(DataWeaveParser.FROM, 0); }
		public QualifiedIdentifierContext qualifiedIdentifier() {
			return getRuleContext(QualifiedIdentifierContext.class,0);
		}
		public TerminalNode STRING() { return getToken(DataWeaveParser.STRING, 0); }
		public ImportDirectiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_importDirective; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterImportDirective(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitImportDirective(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitImportDirective(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ImportDirectiveContext importDirective() throws RecognitionException {
		ImportDirectiveContext _localctx = new ImportDirectiveContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_importDirective);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(159);
			match(IMPORT);
			setState(160);
			importSpec();
			setState(165);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(161);
				match(COMMA);
				setState(162);
				importSpec();
				}
				}
				setState(167);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(173);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==FROM) {
				{
				setState(168);
				match(FROM);
				setState(171);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case IDENTIFIER:
					{
					setState(169);
					qualifiedIdentifier();
					}
					break;
				case STRING:
					{
					setState(170);
					match(STRING);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ImportSpecContext extends ParserRuleContext {
		public TerminalNode STAR() { return getToken(DataWeaveParser.STAR, 0); }
		public QualifiedIdentifierContext qualifiedIdentifier() {
			return getRuleContext(QualifiedIdentifierContext.class,0);
		}
		public TerminalNode AS() { return getToken(DataWeaveParser.AS, 0); }
		public TerminalNode IDENTIFIER() { return getToken(DataWeaveParser.IDENTIFIER, 0); }
		public ImportSpecContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_importSpec; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterImportSpec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitImportSpec(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitImportSpec(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ImportSpecContext importSpec() throws RecognitionException {
		ImportSpecContext _localctx = new ImportSpecContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_importSpec);
		int _la;
		try {
			setState(181);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STAR:
				enterOuterAlt(_localctx, 1);
				{
				setState(175);
				match(STAR);
				}
				break;
			case IDENTIFIER:
				enterOuterAlt(_localctx, 2);
				{
				setState(176);
				qualifiedIdentifier();
				setState(179);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==AS) {
					{
					setState(177);
					match(AS);
					setState(178);
					match(IDENTIFIER);
					}
				}

				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NamespaceDirectiveContext extends ParserRuleContext {
		public TerminalNode NAMESPACE() { return getToken(DataWeaveParser.NAMESPACE, 0); }
		public TerminalNode IDENTIFIER() { return getToken(DataWeaveParser.IDENTIFIER, 0); }
		public TerminalNode URL() { return getToken(DataWeaveParser.URL, 0); }
		public NamespaceDirectiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_namespaceDirective; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterNamespaceDirective(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitNamespaceDirective(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitNamespaceDirective(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NamespaceDirectiveContext namespaceDirective() throws RecognitionException {
		NamespaceDirectiveContext _localctx = new NamespaceDirectiveContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_namespaceDirective);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(183);
			match(NAMESPACE);
			setState(184);
			match(IDENTIFIER);
			setState(185);
			match(URL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class VariableDeclarationContext extends ParserRuleContext {
		public TerminalNode VAR() { return getToken(DataWeaveParser.VAR, 0); }
		public TerminalNode IDENTIFIER() { return getToken(DataWeaveParser.IDENTIFIER, 0); }
		public TerminalNode ASSIGN() { return getToken(DataWeaveParser.ASSIGN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public VariableDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variableDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterVariableDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitVariableDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitVariableDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariableDeclarationContext variableDeclaration() throws RecognitionException {
		VariableDeclarationContext _localctx = new VariableDeclarationContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_variableDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(187);
			match(VAR);
			setState(188);
			match(IDENTIFIER);
			setState(189);
			match(ASSIGN);
			setState(190);
			expression();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FunctionDeclarationContext extends ParserRuleContext {
		public TerminalNode FUNCTION() { return getToken(DataWeaveParser.FUNCTION, 0); }
		public TerminalNode IDENTIFIER() { return getToken(DataWeaveParser.IDENTIFIER, 0); }
		public TerminalNode LPAREN() { return getToken(DataWeaveParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(DataWeaveParser.RPAREN, 0); }
		public TerminalNode ASSIGN() { return getToken(DataWeaveParser.ASSIGN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public FunctionParametersContext functionParameters() {
			return getRuleContext(FunctionParametersContext.class,0);
		}
		public TerminalNode COLON() { return getToken(DataWeaveParser.COLON, 0); }
		public TypeExpressionContext typeExpression() {
			return getRuleContext(TypeExpressionContext.class,0);
		}
		public FunctionDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterFunctionDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitFunctionDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitFunctionDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionDeclarationContext functionDeclaration() throws RecognitionException {
		FunctionDeclarationContext _localctx = new FunctionDeclarationContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_functionDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(192);
			match(FUNCTION);
			setState(193);
			match(IDENTIFIER);
			setState(194);
			match(LPAREN);
			setState(196);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IDENTIFIER) {
				{
				setState(195);
				functionParameters();
				}
			}

			setState(198);
			match(RPAREN);
			setState(201);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COLON) {
				{
				setState(199);
				match(COLON);
				setState(200);
				typeExpression();
				}
			}

			setState(203);
			match(ASSIGN);
			setState(204);
			expression();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TypeDeclarationContext extends ParserRuleContext {
		public TerminalNode TYPE() { return getToken(DataWeaveParser.TYPE, 0); }
		public TerminalNode IDENTIFIER() { return getToken(DataWeaveParser.IDENTIFIER, 0); }
		public TerminalNode ASSIGN() { return getToken(DataWeaveParser.ASSIGN, 0); }
		public TypeExpressionContext typeExpression() {
			return getRuleContext(TypeExpressionContext.class,0);
		}
		public TypeDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterTypeDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitTypeDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitTypeDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeDeclarationContext typeDeclaration() throws RecognitionException {
		TypeDeclarationContext _localctx = new TypeDeclarationContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_typeDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(206);
			match(TYPE);
			setState(207);
			match(IDENTIFIER);
			setState(208);
			match(ASSIGN);
			setState(209);
			typeExpression();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class BodyContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public List<TerminalNode> NEWLINE() { return getTokens(DataWeaveParser.NEWLINE); }
		public TerminalNode NEWLINE(int i) {
			return getToken(DataWeaveParser.NEWLINE, i);
		}
		public BodyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_body; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterBody(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitBody(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitBody(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BodyContext body() throws RecognitionException {
		BodyContext _localctx = new BodyContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_body);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(211);
			expression();
			setState(215);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,17,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(212);
					match(NEWLINE);
					}
					} 
				}
				setState(217);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,17,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExpressionContext extends ParserRuleContext {
		public OperationExpressionContext operationExpression() {
			return getRuleContext(OperationExpressionContext.class,0);
		}
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		ExpressionContext _localctx = new ExpressionContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(218);
			operationExpression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class OperationExpressionContext extends ParserRuleContext {
		public OperationExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_operationExpression; }
	 
		public OperationExpressionContext() { }
		public void copyFrom(OperationExpressionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class MapExpressionContext extends OperationExpressionContext {
		public OperationExpressionContext operationExpression() {
			return getRuleContext(OperationExpressionContext.class,0);
		}
		public TerminalNode MAP() { return getToken(DataWeaveParser.MAP, 0); }
		public ImplicitLambdaExpressionContext implicitLambdaExpression() {
			return getRuleContext(ImplicitLambdaExpressionContext.class,0);
		}
		public MapExpressionContext(OperationExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterMapExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitMapExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitMapExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class InfixFunctionCallContext extends OperationExpressionContext {
		public OperationExpressionContext operationExpression() {
			return getRuleContext(OperationExpressionContext.class,0);
		}
		public TerminalNode IDENTIFIER() { return getToken(DataWeaveParser.IDENTIFIER, 0); }
		public DefaultExpressionContext defaultExpression() {
			return getRuleContext(DefaultExpressionContext.class,0);
		}
		public InfixFunctionCallContext(OperationExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterInfixFunctionCall(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitInfixFunctionCall(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitInfixFunctionCall(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class OperationExpressionWrapperContext extends OperationExpressionContext {
		public DefaultExpressionContext defaultExpression() {
			return getRuleContext(DefaultExpressionContext.class,0);
		}
		public OperationExpressionWrapperContext(OperationExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterOperationExpressionWrapper(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitOperationExpressionWrapper(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitOperationExpressionWrapper(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class FilterExpressionContext extends OperationExpressionContext {
		public OperationExpressionContext operationExpression() {
			return getRuleContext(OperationExpressionContext.class,0);
		}
		public TerminalNode FILTER() { return getToken(DataWeaveParser.FILTER, 0); }
		public ImplicitLambdaExpressionContext implicitLambdaExpression() {
			return getRuleContext(ImplicitLambdaExpressionContext.class,0);
		}
		public FilterExpressionContext(OperationExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterFilterExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitFilterExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitFilterExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class GroupByExpressionContext extends OperationExpressionContext {
		public OperationExpressionContext operationExpression() {
			return getRuleContext(OperationExpressionContext.class,0);
		}
		public TerminalNode GROUP_BY() { return getToken(DataWeaveParser.GROUP_BY, 0); }
		public ImplicitLambdaExpressionContext implicitLambdaExpression() {
			return getRuleContext(ImplicitLambdaExpressionContext.class,0);
		}
		public GroupByExpressionContext(OperationExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterGroupByExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitGroupByExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitGroupByExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ReplaceExpressionContext extends OperationExpressionContext {
		public OperationExpressionContext operationExpression() {
			return getRuleContext(OperationExpressionContext.class,0);
		}
		public TerminalNode REPLACE() { return getToken(DataWeaveParser.REPLACE, 0); }
		public TerminalNode REGEX() { return getToken(DataWeaveParser.REGEX, 0); }
		public TerminalNode WITH() { return getToken(DataWeaveParser.WITH, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ReplaceExpressionContext(OperationExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterReplaceExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitReplaceExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitReplaceExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ConcatExpressionContext extends OperationExpressionContext {
		public OperationExpressionContext operationExpression() {
			return getRuleContext(OperationExpressionContext.class,0);
		}
		public TerminalNode CONCAT() { return getToken(DataWeaveParser.CONCAT, 0); }
		public DefaultExpressionContext defaultExpression() {
			return getRuleContext(DefaultExpressionContext.class,0);
		}
		public ConcatExpressionContext(OperationExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterConcatExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitConcatExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitConcatExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OperationExpressionContext operationExpression() throws RecognitionException {
		return operationExpression(0);
	}

	private OperationExpressionContext operationExpression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		OperationExpressionContext _localctx = new OperationExpressionContext(_ctx, _parentState);
		OperationExpressionContext _prevctx = _localctx;
		int _startState = 32;
		enterRecursionRule(_localctx, 32, RULE_operationExpression, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			_localctx = new OperationExpressionWrapperContext(_localctx);
			_ctx = _localctx;
			_prevctx = _localctx;

			setState(221);
			defaultExpression();
			}
			_ctx.stop = _input.LT(-1);
			setState(245);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,19,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(243);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
					case 1:
						{
						_localctx = new FilterExpressionContext(new OperationExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_operationExpression);
						setState(223);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(224);
						match(FILTER);
						setState(225);
						implicitLambdaExpression();
						}
						break;
					case 2:
						{
						_localctx = new MapExpressionContext(new OperationExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_operationExpression);
						setState(226);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(227);
						match(MAP);
						setState(228);
						implicitLambdaExpression();
						}
						break;
					case 3:
						{
						_localctx = new GroupByExpressionContext(new OperationExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_operationExpression);
						setState(229);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(230);
						match(GROUP_BY);
						setState(231);
						implicitLambdaExpression();
						}
						break;
					case 4:
						{
						_localctx = new ReplaceExpressionContext(new OperationExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_operationExpression);
						setState(232);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(233);
						match(REPLACE);
						setState(234);
						match(REGEX);
						setState(235);
						match(WITH);
						setState(236);
						expression();
						}
						break;
					case 5:
						{
						_localctx = new ConcatExpressionContext(new OperationExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_operationExpression);
						setState(237);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(238);
						match(CONCAT);
						setState(239);
						defaultExpression();
						}
						break;
					case 6:
						{
						_localctx = new InfixFunctionCallContext(new OperationExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_operationExpression);
						setState(240);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(241);
						match(IDENTIFIER);
						setState(242);
						defaultExpression();
						}
						break;
					}
					} 
				}
				setState(247);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,19,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DefaultExpressionContext extends ParserRuleContext {
		public List<LogicalOrExpressionContext> logicalOrExpression() {
			return getRuleContexts(LogicalOrExpressionContext.class);
		}
		public LogicalOrExpressionContext logicalOrExpression(int i) {
			return getRuleContext(LogicalOrExpressionContext.class,i);
		}
		public TerminalNode DEFAULT() { return getToken(DataWeaveParser.DEFAULT, 0); }
		public DefaultExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_defaultExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterDefaultExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitDefaultExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitDefaultExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DefaultExpressionContext defaultExpression() throws RecognitionException {
		DefaultExpressionContext _localctx = new DefaultExpressionContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_defaultExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(248);
			logicalOrExpression();
			setState(251);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,20,_ctx) ) {
			case 1:
				{
				setState(249);
				match(DEFAULT);
				setState(250);
				logicalOrExpression();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ImplicitLambdaExpressionContext extends ParserRuleContext {
		public InlineLambdaContext inlineLambda() {
			return getRuleContext(InlineLambdaContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode LPAREN() { return getToken(DataWeaveParser.LPAREN, 0); }
		public ImplicitLambdaExpressionContext implicitLambdaExpression() {
			return getRuleContext(ImplicitLambdaExpressionContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(DataWeaveParser.RPAREN, 0); }
		public ImplicitLambdaExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_implicitLambdaExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterImplicitLambdaExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitImplicitLambdaExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitImplicitLambdaExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ImplicitLambdaExpressionContext implicitLambdaExpression() throws RecognitionException {
		ImplicitLambdaExpressionContext _localctx = new ImplicitLambdaExpressionContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_implicitLambdaExpression);
		try {
			setState(259);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(253);
				inlineLambda();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(254);
				expression();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(255);
				match(LPAREN);
				setState(256);
				implicitLambdaExpression();
				setState(257);
				match(RPAREN);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class InlineLambdaContext extends ParserRuleContext {
		public TerminalNode LPAREN() { return getToken(DataWeaveParser.LPAREN, 0); }
		public FunctionParametersContext functionParameters() {
			return getRuleContext(FunctionParametersContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(DataWeaveParser.RPAREN, 0); }
		public TerminalNode ARROW() { return getToken(DataWeaveParser.ARROW, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public InlineLambdaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_inlineLambda; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterInlineLambda(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitInlineLambda(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitInlineLambda(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InlineLambdaContext inlineLambda() throws RecognitionException {
		InlineLambdaContext _localctx = new InlineLambdaContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_inlineLambda);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(261);
			match(LPAREN);
			setState(262);
			functionParameters();
			setState(263);
			match(RPAREN);
			setState(264);
			match(ARROW);
			setState(265);
			expression();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FunctionParametersContext extends ParserRuleContext {
		public List<FunctionParameterContext> functionParameter() {
			return getRuleContexts(FunctionParameterContext.class);
		}
		public FunctionParameterContext functionParameter(int i) {
			return getRuleContext(FunctionParameterContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(DataWeaveParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(DataWeaveParser.COMMA, i);
		}
		public FunctionParametersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionParameters; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterFunctionParameters(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitFunctionParameters(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitFunctionParameters(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionParametersContext functionParameters() throws RecognitionException {
		FunctionParametersContext _localctx = new FunctionParametersContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_functionParameters);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(267);
			functionParameter();
			setState(272);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(268);
				match(COMMA);
				setState(269);
				functionParameter();
				}
				}
				setState(274);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FunctionParameterContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(DataWeaveParser.IDENTIFIER, 0); }
		public TerminalNode COLON() { return getToken(DataWeaveParser.COLON, 0); }
		public TypeExpressionContext typeExpression() {
			return getRuleContext(TypeExpressionContext.class,0);
		}
		public TerminalNode ASSIGN() { return getToken(DataWeaveParser.ASSIGN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public FunctionParameterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionParameter; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterFunctionParameter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitFunctionParameter(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitFunctionParameter(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionParameterContext functionParameter() throws RecognitionException {
		FunctionParameterContext _localctx = new FunctionParameterContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_functionParameter);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(275);
			match(IDENTIFIER);
			setState(278);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COLON) {
				{
				setState(276);
				match(COLON);
				setState(277);
				typeExpression();
				}
			}

			setState(282);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ASSIGN) {
				{
				setState(280);
				match(ASSIGN);
				setState(281);
				expression();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class LogicalOrExpressionContext extends ParserRuleContext {
		public List<LogicalAndExpressionContext> logicalAndExpression() {
			return getRuleContexts(LogicalAndExpressionContext.class);
		}
		public LogicalAndExpressionContext logicalAndExpression(int i) {
			return getRuleContext(LogicalAndExpressionContext.class,i);
		}
		public List<TerminalNode> OR() { return getTokens(DataWeaveParser.OR); }
		public TerminalNode OR(int i) {
			return getToken(DataWeaveParser.OR, i);
		}
		public LogicalOrExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_logicalOrExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterLogicalOrExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitLogicalOrExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitLogicalOrExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LogicalOrExpressionContext logicalOrExpression() throws RecognitionException {
		LogicalOrExpressionContext _localctx = new LogicalOrExpressionContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_logicalOrExpression);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(284);
			logicalAndExpression();
			setState(289);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,25,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(285);
					match(OR);
					setState(286);
					logicalAndExpression();
					}
					} 
				}
				setState(291);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,25,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class LogicalAndExpressionContext extends ParserRuleContext {
		public List<EqualityExpressionContext> equalityExpression() {
			return getRuleContexts(EqualityExpressionContext.class);
		}
		public EqualityExpressionContext equalityExpression(int i) {
			return getRuleContext(EqualityExpressionContext.class,i);
		}
		public List<TerminalNode> AND() { return getTokens(DataWeaveParser.AND); }
		public TerminalNode AND(int i) {
			return getToken(DataWeaveParser.AND, i);
		}
		public LogicalAndExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_logicalAndExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterLogicalAndExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitLogicalAndExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitLogicalAndExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LogicalAndExpressionContext logicalAndExpression() throws RecognitionException {
		LogicalAndExpressionContext _localctx = new LogicalAndExpressionContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_logicalAndExpression);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(292);
			equalityExpression();
			setState(297);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,26,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(293);
					match(AND);
					setState(294);
					equalityExpression();
					}
					} 
				}
				setState(299);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,26,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class EqualityExpressionContext extends ParserRuleContext {
		public List<RelationalExpressionContext> relationalExpression() {
			return getRuleContexts(RelationalExpressionContext.class);
		}
		public RelationalExpressionContext relationalExpression(int i) {
			return getRuleContext(RelationalExpressionContext.class,i);
		}
		public List<TerminalNode> OPERATOR_EQUALITY() { return getTokens(DataWeaveParser.OPERATOR_EQUALITY); }
		public TerminalNode OPERATOR_EQUALITY(int i) {
			return getToken(DataWeaveParser.OPERATOR_EQUALITY, i);
		}
		public EqualityExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_equalityExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterEqualityExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitEqualityExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitEqualityExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EqualityExpressionContext equalityExpression() throws RecognitionException {
		EqualityExpressionContext _localctx = new EqualityExpressionContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_equalityExpression);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(300);
			relationalExpression();
			setState(305);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,27,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(301);
					match(OPERATOR_EQUALITY);
					setState(302);
					relationalExpression();
					}
					} 
				}
				setState(307);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,27,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class RelationalExpressionContext extends ParserRuleContext {
		public RelationalExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relationalExpression; }
	 
		public RelationalExpressionContext() { }
		public void copyFrom(RelationalExpressionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class IsExpressionContext extends RelationalExpressionContext {
		public AdditiveExpressionContext additiveExpression() {
			return getRuleContext(AdditiveExpressionContext.class,0);
		}
		public TerminalNode IS() { return getToken(DataWeaveParser.IS, 0); }
		public TypeExpressionContext typeExpression() {
			return getRuleContext(TypeExpressionContext.class,0);
		}
		public IsExpressionContext(RelationalExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterIsExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitIsExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitIsExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class RelationalComparisonContext extends RelationalExpressionContext {
		public List<AdditiveExpressionContext> additiveExpression() {
			return getRuleContexts(AdditiveExpressionContext.class);
		}
		public AdditiveExpressionContext additiveExpression(int i) {
			return getRuleContext(AdditiveExpressionContext.class,i);
		}
		public List<TerminalNode> OPERATOR_RELATIONAL() { return getTokens(DataWeaveParser.OPERATOR_RELATIONAL); }
		public TerminalNode OPERATOR_RELATIONAL(int i) {
			return getToken(DataWeaveParser.OPERATOR_RELATIONAL, i);
		}
		public RelationalComparisonContext(RelationalExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterRelationalComparison(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitRelationalComparison(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitRelationalComparison(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RelationalExpressionContext relationalExpression() throws RecognitionException {
		RelationalExpressionContext _localctx = new RelationalExpressionContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_relationalExpression);
		try {
			int _alt;
			setState(320);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,29,_ctx) ) {
			case 1:
				_localctx = new RelationalComparisonContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(308);
				additiveExpression();
				setState(313);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,28,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(309);
						match(OPERATOR_RELATIONAL);
						setState(310);
						additiveExpression();
						}
						} 
					}
					setState(315);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,28,_ctx);
				}
				}
				break;
			case 2:
				_localctx = new IsExpressionContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(316);
				additiveExpression();
				setState(317);
				match(IS);
				setState(318);
				typeExpression();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AdditiveExpressionContext extends ParserRuleContext {
		public List<MultiplicativeExpressionContext> multiplicativeExpression() {
			return getRuleContexts(MultiplicativeExpressionContext.class);
		}
		public MultiplicativeExpressionContext multiplicativeExpression(int i) {
			return getRuleContext(MultiplicativeExpressionContext.class,i);
		}
		public List<AdditiveOperatorContext> additiveOperator() {
			return getRuleContexts(AdditiveOperatorContext.class);
		}
		public AdditiveOperatorContext additiveOperator(int i) {
			return getRuleContext(AdditiveOperatorContext.class,i);
		}
		public AdditiveExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_additiveExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterAdditiveExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitAdditiveExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitAdditiveExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AdditiveExpressionContext additiveExpression() throws RecognitionException {
		AdditiveExpressionContext _localctx = new AdditiveExpressionContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_additiveExpression);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(322);
			multiplicativeExpression();
			setState(328);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,30,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(323);
					additiveOperator();
					setState(324);
					multiplicativeExpression();
					}
					} 
				}
				setState(330);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,30,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AdditiveOperatorContext extends ParserRuleContext {
		public TerminalNode OPERATOR_ADDITIVE() { return getToken(DataWeaveParser.OPERATOR_ADDITIVE, 0); }
		public TerminalNode MINUS() { return getToken(DataWeaveParser.MINUS, 0); }
		public AdditiveOperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_additiveOperator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterAdditiveOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitAdditiveOperator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitAdditiveOperator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AdditiveOperatorContext additiveOperator() throws RecognitionException {
		AdditiveOperatorContext _localctx = new AdditiveOperatorContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_additiveOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(331);
			_la = _input.LA(1);
			if ( !(_la==OPERATOR_ADDITIVE || _la==MINUS) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class MultiplicativeExpressionContext extends ParserRuleContext {
		public List<TypeCoercionExpressionContext> typeCoercionExpression() {
			return getRuleContexts(TypeCoercionExpressionContext.class);
		}
		public TypeCoercionExpressionContext typeCoercionExpression(int i) {
			return getRuleContext(TypeCoercionExpressionContext.class,i);
		}
		public List<TerminalNode> OPERATOR_MULTIPLICATIVE() { return getTokens(DataWeaveParser.OPERATOR_MULTIPLICATIVE); }
		public TerminalNode OPERATOR_MULTIPLICATIVE(int i) {
			return getToken(DataWeaveParser.OPERATOR_MULTIPLICATIVE, i);
		}
		public MultiplicativeExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_multiplicativeExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterMultiplicativeExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitMultiplicativeExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitMultiplicativeExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MultiplicativeExpressionContext multiplicativeExpression() throws RecognitionException {
		MultiplicativeExpressionContext _localctx = new MultiplicativeExpressionContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_multiplicativeExpression);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(333);
			typeCoercionExpression(0);
			setState(338);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,31,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(334);
					match(OPERATOR_MULTIPLICATIVE);
					setState(335);
					typeCoercionExpression(0);
					}
					} 
				}
				setState(340);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,31,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TypeCoercionExpressionContext extends ParserRuleContext {
		public UnaryExpressionContext unaryExpression() {
			return getRuleContext(UnaryExpressionContext.class,0);
		}
		public TypeCoercionExpressionContext typeCoercionExpression() {
			return getRuleContext(TypeCoercionExpressionContext.class,0);
		}
		public TerminalNode AS() { return getToken(DataWeaveParser.AS, 0); }
		public TypeExpressionContext typeExpression() {
			return getRuleContext(TypeExpressionContext.class,0);
		}
		public FormatOptionContext formatOption() {
			return getRuleContext(FormatOptionContext.class,0);
		}
		public TypeCoercionExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeCoercionExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterTypeCoercionExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitTypeCoercionExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitTypeCoercionExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeCoercionExpressionContext typeCoercionExpression() throws RecognitionException {
		return typeCoercionExpression(0);
	}

	private TypeCoercionExpressionContext typeCoercionExpression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		TypeCoercionExpressionContext _localctx = new TypeCoercionExpressionContext(_ctx, _parentState);
		TypeCoercionExpressionContext _prevctx = _localctx;
		int _startState = 58;
		enterRecursionRule(_localctx, 58, RULE_typeCoercionExpression, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(342);
			unaryExpression();
			}
			_ctx.stop = _input.LT(-1);
			setState(352);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,33,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new TypeCoercionExpressionContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_typeCoercionExpression);
					setState(344);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(345);
					match(AS);
					setState(346);
					typeExpression();
					setState(348);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,32,_ctx) ) {
					case 1:
						{
						setState(347);
						formatOption();
						}
						break;
					}
					}
					} 
				}
				setState(354);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,33,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FormatOptionContext extends ParserRuleContext {
		public TerminalNode LCURLY() { return getToken(DataWeaveParser.LCURLY, 0); }
		public TerminalNode IDENTIFIER() { return getToken(DataWeaveParser.IDENTIFIER, 0); }
		public TerminalNode COLON() { return getToken(DataWeaveParser.COLON, 0); }
		public TerminalNode STRING() { return getToken(DataWeaveParser.STRING, 0); }
		public TerminalNode RCURLY() { return getToken(DataWeaveParser.RCURLY, 0); }
		public FormatOptionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formatOption; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterFormatOption(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitFormatOption(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitFormatOption(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FormatOptionContext formatOption() throws RecognitionException {
		FormatOptionContext _localctx = new FormatOptionContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_formatOption);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(355);
			match(LCURLY);
			setState(356);
			match(IDENTIFIER);
			setState(357);
			match(COLON);
			setState(358);
			match(STRING);
			setState(359);
			match(RCURLY);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class UnaryExpressionContext extends ParserRuleContext {
		public UnaryExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unaryExpression; }
	 
		public UnaryExpressionContext() { }
		public void copyFrom(UnaryExpressionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PrimaryExpressionWrapperContext extends UnaryExpressionContext {
		public PrimaryExpressionContext primaryExpression() {
			return getRuleContext(PrimaryExpressionContext.class,0);
		}
		public PrimaryExpressionWrapperContext(UnaryExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterPrimaryExpressionWrapper(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitPrimaryExpressionWrapper(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitPrimaryExpressionWrapper(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class NegativeExpressionContext extends UnaryExpressionContext {
		public TerminalNode MINUS() { return getToken(DataWeaveParser.MINUS, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public NegativeExpressionContext(UnaryExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterNegativeExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitNegativeExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitNegativeExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SizeOfExpressionWithParenthesesContext extends UnaryExpressionContext {
		public TerminalNode SIZE_OF() { return getToken(DataWeaveParser.SIZE_OF, 0); }
		public TerminalNode LPAREN() { return getToken(DataWeaveParser.LPAREN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(DataWeaveParser.RPAREN, 0); }
		public SizeOfExpressionWithParenthesesContext(UnaryExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterSizeOfExpressionWithParentheses(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitSizeOfExpressionWithParentheses(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitSizeOfExpressionWithParentheses(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class UpperExpressionWithParenthesesContext extends UnaryExpressionContext {
		public TerminalNode UPPER() { return getToken(DataWeaveParser.UPPER, 0); }
		public TerminalNode LPAREN() { return getToken(DataWeaveParser.LPAREN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(DataWeaveParser.RPAREN, 0); }
		public UpperExpressionWithParenthesesContext(UnaryExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterUpperExpressionWithParentheses(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitUpperExpressionWithParentheses(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitUpperExpressionWithParentheses(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SizeOfExpressionContext extends UnaryExpressionContext {
		public TerminalNode SIZE_OF() { return getToken(DataWeaveParser.SIZE_OF, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public SizeOfExpressionContext(UnaryExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterSizeOfExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitSizeOfExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitSizeOfExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class UpperExpressionContext extends UnaryExpressionContext {
		public TerminalNode UPPER() { return getToken(DataWeaveParser.UPPER, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public UpperExpressionContext(UnaryExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterUpperExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitUpperExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitUpperExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class LowerExpressionContext extends UnaryExpressionContext {
		public TerminalNode LOWER() { return getToken(DataWeaveParser.LOWER, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public LowerExpressionContext(UnaryExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterLowerExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitLowerExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitLowerExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class LowerExpressionWithParenthesesContext extends UnaryExpressionContext {
		public TerminalNode LOWER() { return getToken(DataWeaveParser.LOWER, 0); }
		public TerminalNode LPAREN() { return getToken(DataWeaveParser.LPAREN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(DataWeaveParser.RPAREN, 0); }
		public LowerExpressionWithParenthesesContext(UnaryExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterLowerExpressionWithParentheses(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitLowerExpressionWithParentheses(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitLowerExpressionWithParentheses(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class NotExpressionContext extends UnaryExpressionContext {
		public TerminalNode NOT() { return getToken(DataWeaveParser.NOT, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public NotExpressionContext(UnaryExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterNotExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitNotExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitNotExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UnaryExpressionContext unaryExpression() throws RecognitionException {
		UnaryExpressionContext _localctx = new UnaryExpressionContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_unaryExpression);
		try {
			setState(387);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,34,_ctx) ) {
			case 1:
				_localctx = new SizeOfExpressionWithParenthesesContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(361);
				match(SIZE_OF);
				setState(362);
				match(LPAREN);
				setState(363);
				expression();
				setState(364);
				match(RPAREN);
				}
				break;
			case 2:
				_localctx = new SizeOfExpressionContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(366);
				match(SIZE_OF);
				setState(367);
				expression();
				}
				break;
			case 3:
				_localctx = new UpperExpressionWithParenthesesContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(368);
				match(UPPER);
				setState(369);
				match(LPAREN);
				setState(370);
				expression();
				setState(371);
				match(RPAREN);
				}
				break;
			case 4:
				_localctx = new UpperExpressionContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(373);
				match(UPPER);
				setState(374);
				expression();
				}
				break;
			case 5:
				_localctx = new LowerExpressionWithParenthesesContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(375);
				match(LOWER);
				setState(376);
				match(LPAREN);
				setState(377);
				expression();
				setState(378);
				match(RPAREN);
				}
				break;
			case 6:
				_localctx = new LowerExpressionContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(380);
				match(LOWER);
				setState(381);
				expression();
				}
				break;
			case 7:
				_localctx = new NotExpressionContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(382);
				match(NOT);
				setState(383);
				expression();
				}
				break;
			case 8:
				_localctx = new NegativeExpressionContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(384);
				match(MINUS);
				setState(385);
				expression();
				}
				break;
			case 9:
				_localctx = new PrimaryExpressionWrapperContext(_localctx);
				enterOuterAlt(_localctx, 9);
				{
				setState(386);
				primaryExpression(0);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class PrimaryExpressionContext extends ParserRuleContext {
		public PrimaryExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primaryExpression; }
	 
		public PrimaryExpressionContext() { }
		public void copyFrom(PrimaryExpressionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class LambdaExpressionContext extends PrimaryExpressionContext {
		public InlineLambdaContext inlineLambda() {
			return getRuleContext(InlineLambdaContext.class,0);
		}
		public LambdaExpressionContext(PrimaryExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterLambdaExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitLambdaExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitLambdaExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ArrayExpressionContext extends PrimaryExpressionContext {
		public ArrayContext array() {
			return getRuleContext(ArrayContext.class,0);
		}
		public ArrayExpressionContext(PrimaryExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterArrayExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitArrayExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitArrayExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SelectorExpressionWrapperWithDefaultContext extends PrimaryExpressionContext {
		public PrimaryExpressionContext primaryExpression() {
			return getRuleContext(PrimaryExpressionContext.class,0);
		}
		public SelectorExpressionContext selectorExpression() {
			return getRuleContext(SelectorExpressionContext.class,0);
		}
		public TerminalNode DEFAULT() { return getToken(DataWeaveParser.DEFAULT, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public SelectorExpressionWrapperWithDefaultContext(PrimaryExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterSelectorExpressionWrapperWithDefault(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitSelectorExpressionWrapperWithDefault(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitSelectorExpressionWrapperWithDefault(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class IdentifierExpressionContext extends PrimaryExpressionContext {
		public TerminalNode IDENTIFIER() { return getToken(DataWeaveParser.IDENTIFIER, 0); }
		public IdentifierExpressionContext(PrimaryExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterIdentifierExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitIdentifierExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitIdentifierExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SelectorExpressionWrapperContext extends PrimaryExpressionContext {
		public PrimaryExpressionContext primaryExpression() {
			return getRuleContext(PrimaryExpressionContext.class,0);
		}
		public SelectorExpressionContext selectorExpression() {
			return getRuleContext(SelectorExpressionContext.class,0);
		}
		public SelectorExpressionWrapperContext(PrimaryExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterSelectorExpressionWrapper(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitSelectorExpressionWrapper(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitSelectorExpressionWrapper(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class IndexIdentifierExpressionContext extends PrimaryExpressionContext {
		public TerminalNode INDEX_IDENTIFIER() { return getToken(DataWeaveParser.INDEX_IDENTIFIER, 0); }
		public IndexIdentifierExpressionContext(PrimaryExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterIndexIdentifierExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitIndexIdentifierExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitIndexIdentifierExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class GroupedExpressionContext extends PrimaryExpressionContext {
		public GroupedContext grouped() {
			return getRuleContext(GroupedContext.class,0);
		}
		public GroupedExpressionContext(PrimaryExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterGroupedExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitGroupedExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitGroupedExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ObjectExpressionContext extends PrimaryExpressionContext {
		public ObjectContext object() {
			return getRuleContext(ObjectContext.class,0);
		}
		public ObjectExpressionContext(PrimaryExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterObjectExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitObjectExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitObjectExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class IfElseConditionContext extends PrimaryExpressionContext {
		public List<TerminalNode> IF() { return getTokens(DataWeaveParser.IF); }
		public TerminalNode IF(int i) {
			return getToken(DataWeaveParser.IF, i);
		}
		public List<TerminalNode> LPAREN() { return getTokens(DataWeaveParser.LPAREN); }
		public TerminalNode LPAREN(int i) {
			return getToken(DataWeaveParser.LPAREN, i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<TerminalNode> RPAREN() { return getTokens(DataWeaveParser.RPAREN); }
		public TerminalNode RPAREN(int i) {
			return getToken(DataWeaveParser.RPAREN, i);
		}
		public List<TerminalNode> ELSE() { return getTokens(DataWeaveParser.ELSE); }
		public TerminalNode ELSE(int i) {
			return getToken(DataWeaveParser.ELSE, i);
		}
		public IfElseConditionContext(PrimaryExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterIfElseCondition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitIfElseCondition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitIfElseCondition(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class BuiltInFunctionExpressionContext extends PrimaryExpressionContext {
		public BuiltInFunctionContext builtInFunction() {
			return getRuleContext(BuiltInFunctionContext.class,0);
		}
		public BuiltInFunctionExpressionContext(PrimaryExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterBuiltInFunctionExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitBuiltInFunctionExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitBuiltInFunctionExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class DoBlockExpressionContext extends PrimaryExpressionContext {
		public DoBlockContext doBlock() {
			return getRuleContext(DoBlockContext.class,0);
		}
		public DoBlockExpressionContext(PrimaryExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterDoBlockExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitDoBlockExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitDoBlockExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class FunctionCallExpressionContext extends PrimaryExpressionContext {
		public FunctionCallContext functionCall() {
			return getRuleContext(FunctionCallContext.class,0);
		}
		public FunctionCallExpressionContext(PrimaryExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterFunctionCallExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitFunctionCallExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitFunctionCallExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class LiteralExpressionContext extends PrimaryExpressionContext {
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public LiteralExpressionContext(PrimaryExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterLiteralExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitLiteralExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitLiteralExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ValueIdentifierExpressionContext extends PrimaryExpressionContext {
		public TerminalNode VALUE_IDENTIFIER() { return getToken(DataWeaveParser.VALUE_IDENTIFIER, 0); }
		public ValueIdentifierExpressionContext(PrimaryExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterValueIdentifierExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitValueIdentifierExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitValueIdentifierExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PrimaryExpressionContext primaryExpression() throws RecognitionException {
		return primaryExpression(0);
	}

	private PrimaryExpressionContext primaryExpression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		PrimaryExpressionContext _localctx = new PrimaryExpressionContext(_ctx, _parentState);
		PrimaryExpressionContext _prevctx = _localctx;
		int _startState = 64;
		enterRecursionRule(_localctx, 64, RULE_primaryExpression, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(422);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,37,_ctx) ) {
			case 1:
				{
				_localctx = new IfElseConditionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(390);
				match(IF);
				setState(391);
				match(LPAREN);
				setState(392);
				expression();
				setState(393);
				match(RPAREN);
				setState(394);
				expression();
				setState(404);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,35,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(395);
						match(ELSE);
						setState(396);
						match(IF);
						setState(397);
						match(LPAREN);
						setState(398);
						expression();
						setState(399);
						match(RPAREN);
						setState(400);
						expression();
						}
						} 
					}
					setState(406);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,35,_ctx);
				}
				setState(409);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,36,_ctx) ) {
				case 1:
					{
					setState(407);
					match(ELSE);
					setState(408);
					expression();
					}
					break;
				}
				}
				break;
			case 2:
				{
				_localctx = new DoBlockExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(411);
				doBlock();
				}
				break;
			case 3:
				{
				_localctx = new LambdaExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(412);
				inlineLambda();
				}
				break;
			case 4:
				{
				_localctx = new GroupedExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(413);
				grouped();
				}
				break;
			case 5:
				{
				_localctx = new LiteralExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(414);
				literal();
				}
				break;
			case 6:
				{
				_localctx = new FunctionCallExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(415);
				functionCall();
				}
				break;
			case 7:
				{
				_localctx = new ArrayExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(416);
				array();
				}
				break;
			case 8:
				{
				_localctx = new ObjectExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(417);
				object();
				}
				break;
			case 9:
				{
				_localctx = new BuiltInFunctionExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(418);
				builtInFunction();
				}
				break;
			case 10:
				{
				_localctx = new IdentifierExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(419);
				match(IDENTIFIER);
				}
				break;
			case 11:
				{
				_localctx = new ValueIdentifierExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(420);
				match(VALUE_IDENTIFIER);
				}
				break;
			case 12:
				{
				_localctx = new IndexIdentifierExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(421);
				match(INDEX_IDENTIFIER);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(433);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,39,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(431);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,38,_ctx) ) {
					case 1:
						{
						_localctx = new SelectorExpressionWrapperContext(new PrimaryExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_primaryExpression);
						setState(424);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(425);
						selectorExpression();
						}
						break;
					case 2:
						{
						_localctx = new SelectorExpressionWrapperWithDefaultContext(new PrimaryExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_primaryExpression);
						setState(426);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(427);
						selectorExpression();
						setState(428);
						match(DEFAULT);
						setState(429);
						expression();
						}
						break;
					}
					} 
				}
				setState(435);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,39,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class BuiltInFunctionContext extends ParserRuleContext {
		public BuiltInFunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_builtInFunction; }
	 
		public BuiltInFunctionContext() { }
		public void copyFrom(BuiltInFunctionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class NowFunctionContext extends BuiltInFunctionContext {
		public TerminalNode NOW() { return getToken(DataWeaveParser.NOW, 0); }
		public TerminalNode LPAREN() { return getToken(DataWeaveParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(DataWeaveParser.RPAREN, 0); }
		public NowFunctionContext(BuiltInFunctionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterNowFunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitNowFunction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitNowFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BuiltInFunctionContext builtInFunction() throws RecognitionException {
		BuiltInFunctionContext _localctx = new BuiltInFunctionContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_builtInFunction);
		try {
			_localctx = new NowFunctionContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(436);
			match(NOW);
			setState(437);
			match(LPAREN);
			setState(438);
			match(RPAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class GroupedContext extends ParserRuleContext {
		public TerminalNode LPAREN() { return getToken(DataWeaveParser.LPAREN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(DataWeaveParser.RPAREN, 0); }
		public GroupedContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_grouped; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterGrouped(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitGrouped(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitGrouped(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GroupedContext grouped() throws RecognitionException {
		GroupedContext _localctx = new GroupedContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_grouped);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(440);
			match(LPAREN);
			setState(441);
			expression();
			setState(442);
			match(RPAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DoBlockContext extends ParserRuleContext {
		public TerminalNode DO() { return getToken(DataWeaveParser.DO, 0); }
		public TerminalNode LCURLY() { return getToken(DataWeaveParser.LCURLY, 0); }
		public TerminalNode SEPARATOR() { return getToken(DataWeaveParser.SEPARATOR, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RCURLY() { return getToken(DataWeaveParser.RCURLY, 0); }
		public HeaderContext header() {
			return getRuleContext(HeaderContext.class,0);
		}
		public DoBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_doBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterDoBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitDoBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitDoBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DoBlockContext doBlock() throws RecognitionException {
		DoBlockContext _localctx = new DoBlockContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_doBlock);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(444);
			match(DO);
			setState(445);
			match(LCURLY);
			setState(447);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 4177920L) != 0)) {
				{
				setState(446);
				header();
				}
			}

			setState(449);
			match(SEPARATOR);
			setState(450);
			expression();
			setState(451);
			match(RCURLY);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SelectorExpressionContext extends ParserRuleContext {
		public SelectorExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_selectorExpression; }
	 
		public SelectorExpressionContext() { }
		public void copyFrom(SelectorExpressionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExistenceQuerySelectorContext extends SelectorExpressionContext {
		public TerminalNode QUESTION() { return getToken(DataWeaveParser.QUESTION, 0); }
		public ExistenceQuerySelectorContext(SelectorExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterExistenceQuerySelector(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitExistenceQuerySelector(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitExistenceQuerySelector(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SingleValueSelectorContext extends SelectorExpressionContext {
		public TerminalNode DOT() { return getToken(DataWeaveParser.DOT, 0); }
		public TerminalNode IDENTIFIER() { return getToken(DataWeaveParser.IDENTIFIER, 0); }
		public SingleValueSelectorContext(SelectorExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterSingleValueSelector(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitSingleValueSelector(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitSingleValueSelector(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class KeySelectorContext extends SelectorExpressionContext {
		public TerminalNode DOT() { return getToken(DataWeaveParser.DOT, 0); }
		public TerminalNode STRING() { return getToken(DataWeaveParser.STRING, 0); }
		public KeySelectorContext(SelectorExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterKeySelector(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitKeySelector(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitKeySelector(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class IndexedSelectorContext extends SelectorExpressionContext {
		public TerminalNode LSQUARE() { return getToken(DataWeaveParser.LSQUARE, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RSQUARE() { return getToken(DataWeaveParser.RSQUARE, 0); }
		public IndexedSelectorContext(SelectorExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterIndexedSelector(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitIndexedSelector(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitIndexedSelector(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class MultiValueSelectorContext extends SelectorExpressionContext {
		public TerminalNode DOT() { return getToken(DataWeaveParser.DOT, 0); }
		public TerminalNode STAR() { return getToken(DataWeaveParser.STAR, 0); }
		public TerminalNode IDENTIFIER() { return getToken(DataWeaveParser.IDENTIFIER, 0); }
		public MultiValueSelectorContext(SelectorExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterMultiValueSelector(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitMultiValueSelector(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitMultiValueSelector(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AttributeSelectorContext extends SelectorExpressionContext {
		public TerminalNode DOT() { return getToken(DataWeaveParser.DOT, 0); }
		public TerminalNode AT() { return getToken(DataWeaveParser.AT, 0); }
		public TerminalNode IDENTIFIER() { return getToken(DataWeaveParser.IDENTIFIER, 0); }
		public AttributeSelectorContext(SelectorExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterAttributeSelector(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitAttributeSelector(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitAttributeSelector(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class DescendantsSelectorContext extends SelectorExpressionContext {
		public TerminalNode OPERATOR_RANGE() { return getToken(DataWeaveParser.OPERATOR_RANGE, 0); }
		public TerminalNode IDENTIFIER() { return getToken(DataWeaveParser.IDENTIFIER, 0); }
		public DescendantsSelectorContext(SelectorExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterDescendantsSelector(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitDescendantsSelector(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitDescendantsSelector(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SelectorExpressionContext selectorExpression() throws RecognitionException {
		SelectorExpressionContext _localctx = new SelectorExpressionContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_selectorExpression);
		try {
			setState(470);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,41,_ctx) ) {
			case 1:
				_localctx = new SingleValueSelectorContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(453);
				match(DOT);
				setState(454);
				match(IDENTIFIER);
				}
				break;
			case 2:
				_localctx = new KeySelectorContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(455);
				match(DOT);
				setState(456);
				match(STRING);
				}
				break;
			case 3:
				_localctx = new MultiValueSelectorContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(457);
				match(DOT);
				setState(458);
				match(STAR);
				setState(459);
				match(IDENTIFIER);
				}
				break;
			case 4:
				_localctx = new DescendantsSelectorContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(460);
				match(OPERATOR_RANGE);
				setState(461);
				match(IDENTIFIER);
				}
				break;
			case 5:
				_localctx = new IndexedSelectorContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(462);
				match(LSQUARE);
				setState(463);
				expression();
				setState(464);
				match(RSQUARE);
				}
				break;
			case 6:
				_localctx = new AttributeSelectorContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(466);
				match(DOT);
				setState(467);
				match(AT);
				setState(468);
				match(IDENTIFIER);
				}
				break;
			case 7:
				_localctx = new ExistenceQuerySelectorContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(469);
				match(QUESTION);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class LiteralContext extends ParserRuleContext {
		public TerminalNode STRING() { return getToken(DataWeaveParser.STRING, 0); }
		public TerminalNode NUMBER() { return getToken(DataWeaveParser.NUMBER, 0); }
		public TerminalNode BOOLEAN() { return getToken(DataWeaveParser.BOOLEAN, 0); }
		public TerminalNode DATE() { return getToken(DataWeaveParser.DATE, 0); }
		public TerminalNode REGEX() { return getToken(DataWeaveParser.REGEX, 0); }
		public TerminalNode NULL() { return getToken(DataWeaveParser.NULL, 0); }
		public LiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_literal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LiteralContext literal() throws RecognitionException {
		LiteralContext _localctx = new LiteralContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_literal);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(472);
			_la = _input.LA(1);
			if ( !(((((_la - 24)) & ~0x3f) == 0 && ((1L << (_la - 24)) & 127543348823041L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ArrayContext extends ParserRuleContext {
		public TerminalNode LSQUARE() { return getToken(DataWeaveParser.LSQUARE, 0); }
		public TerminalNode RSQUARE() { return getToken(DataWeaveParser.RSQUARE, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(DataWeaveParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(DataWeaveParser.COMMA, i);
		}
		public ArrayContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_array; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterArray(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitArray(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitArray(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArrayContext array() throws RecognitionException {
		ArrayContext _localctx = new ArrayContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_array);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(474);
			match(LSQUARE);
			setState(483);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (((((_la - 24)) & ~0x3f) == 0 && ((1L << (_la - 24)) & 47416319284364313L) != 0)) {
				{
				setState(475);
				expression();
				setState(480);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(476);
					match(COMMA);
					setState(477);
					expression();
					}
					}
					setState(482);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(485);
			match(RSQUARE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ObjectContext extends ParserRuleContext {
		public ObjectContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_object; }
	 
		public ObjectContext() { }
		public void copyFrom(ObjectContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class MultiFieldObjectContext extends ObjectContext {
		public TerminalNode LCURLY() { return getToken(DataWeaveParser.LCURLY, 0); }
		public List<ObjectFieldContext> objectField() {
			return getRuleContexts(ObjectFieldContext.class);
		}
		public ObjectFieldContext objectField(int i) {
			return getRuleContext(ObjectFieldContext.class,i);
		}
		public TerminalNode RCURLY() { return getToken(DataWeaveParser.RCURLY, 0); }
		public List<TerminalNode> COMMA() { return getTokens(DataWeaveParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(DataWeaveParser.COMMA, i);
		}
		public MultiFieldObjectContext(ObjectContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterMultiFieldObject(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitMultiFieldObject(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitMultiFieldObject(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class EmptyObjectContext extends ObjectContext {
		public TerminalNode LCURLY() { return getToken(DataWeaveParser.LCURLY, 0); }
		public TerminalNode RCURLY() { return getToken(DataWeaveParser.RCURLY, 0); }
		public EmptyObjectContext(ObjectContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterEmptyObject(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitEmptyObject(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitEmptyObject(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SingleFieldObjectContext extends ObjectContext {
		public TerminalNode LCURLY() { return getToken(DataWeaveParser.LCURLY, 0); }
		public ObjectFieldContext objectField() {
			return getRuleContext(ObjectFieldContext.class,0);
		}
		public TerminalNode RCURLY() { return getToken(DataWeaveParser.RCURLY, 0); }
		public SingleFieldObjectContext(ObjectContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterSingleFieldObject(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitSingleFieldObject(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitSingleFieldObject(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ObjectContext object() throws RecognitionException {
		ObjectContext _localctx = new ObjectContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_object);
		int _la;
		try {
			setState(506);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,46,_ctx) ) {
			case 1:
				_localctx = new MultiFieldObjectContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(487);
				match(LCURLY);
				setState(488);
				objectField();
				setState(495);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (((((_la - 61)) & ~0x3f) == 0 && ((1L << (_la - 61)) & 270465L) != 0)) {
					{
					{
					setState(490);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==COMMA) {
						{
						setState(489);
						match(COMMA);
						}
					}

					setState(492);
					objectField();
					}
					}
					setState(497);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(498);
				match(RCURLY);
				}
				break;
			case 2:
				_localctx = new SingleFieldObjectContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(500);
				match(LCURLY);
				setState(501);
				objectField();
				setState(502);
				match(RCURLY);
				}
				break;
			case 3:
				_localctx = new EmptyObjectContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(504);
				match(LCURLY);
				setState(505);
				match(RCURLY);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ObjectFieldContext extends ParserRuleContext {
		public ObjectFieldContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_objectField; }
	 
		public ObjectFieldContext() { }
		public void copyFrom(ObjectFieldContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ConditionalFieldContext extends ObjectFieldContext {
		public TerminalNode LPAREN() { return getToken(DataWeaveParser.LPAREN, 0); }
		public ObjectFieldContext objectField() {
			return getRuleContext(ObjectFieldContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(DataWeaveParser.RPAREN, 0); }
		public TerminalNode IF() { return getToken(DataWeaveParser.IF, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ConditionalFieldContext(ObjectFieldContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterConditionalField(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitConditionalField(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitConditionalField(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class UnquotedKeyFieldContext extends ObjectFieldContext {
		public TerminalNode IDENTIFIER() { return getToken(DataWeaveParser.IDENTIFIER, 0); }
		public TerminalNode COLON() { return getToken(DataWeaveParser.COLON, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public UnquotedKeyFieldContext(ObjectFieldContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterUnquotedKeyField(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitUnquotedKeyField(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitUnquotedKeyField(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class DynamicKeyFieldContext extends ObjectFieldContext {
		public TerminalNode LPAREN() { return getToken(DataWeaveParser.LPAREN, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode RPAREN() { return getToken(DataWeaveParser.RPAREN, 0); }
		public TerminalNode COLON() { return getToken(DataWeaveParser.COLON, 0); }
		public DynamicKeyFieldContext(ObjectFieldContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterDynamicKeyField(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitDynamicKeyField(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitDynamicKeyField(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class QuotedKeyFieldContext extends ObjectFieldContext {
		public TerminalNode STRING() { return getToken(DataWeaveParser.STRING, 0); }
		public TerminalNode COLON() { return getToken(DataWeaveParser.COLON, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public QuotedKeyFieldContext(ObjectFieldContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterQuotedKeyField(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitQuotedKeyField(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitQuotedKeyField(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ObjectFieldContext objectField() throws RecognitionException {
		ObjectFieldContext _localctx = new ObjectFieldContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_objectField);
		try {
			setState(526);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,47,_ctx) ) {
			case 1:
				_localctx = new UnquotedKeyFieldContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(508);
				match(IDENTIFIER);
				setState(509);
				match(COLON);
				setState(510);
				expression();
				}
				break;
			case 2:
				_localctx = new QuotedKeyFieldContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(511);
				match(STRING);
				setState(512);
				match(COLON);
				setState(513);
				expression();
				}
				break;
			case 3:
				_localctx = new DynamicKeyFieldContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(514);
				match(LPAREN);
				setState(515);
				expression();
				setState(516);
				match(RPAREN);
				setState(517);
				match(COLON);
				setState(518);
				expression();
				}
				break;
			case 4:
				_localctx = new ConditionalFieldContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(520);
				match(LPAREN);
				setState(521);
				objectField();
				setState(522);
				match(RPAREN);
				setState(523);
				match(IF);
				setState(524);
				expression();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class QualifiedIdentifierContext extends ParserRuleContext {
		public List<TerminalNode> IDENTIFIER() { return getTokens(DataWeaveParser.IDENTIFIER); }
		public TerminalNode IDENTIFIER(int i) {
			return getToken(DataWeaveParser.IDENTIFIER, i);
		}
		public List<TerminalNode> DOUBLE_COLON() { return getTokens(DataWeaveParser.DOUBLE_COLON); }
		public TerminalNode DOUBLE_COLON(int i) {
			return getToken(DataWeaveParser.DOUBLE_COLON, i);
		}
		public QualifiedIdentifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_qualifiedIdentifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterQualifiedIdentifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitQualifiedIdentifier(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitQualifiedIdentifier(this);
			else return visitor.visitChildren(this);
		}
	}

	public final QualifiedIdentifierContext qualifiedIdentifier() throws RecognitionException {
		QualifiedIdentifierContext _localctx = new QualifiedIdentifierContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_qualifiedIdentifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(528);
			match(IDENTIFIER);
			setState(533);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DOUBLE_COLON) {
				{
				{
				setState(529);
				match(DOUBLE_COLON);
				setState(530);
				match(IDENTIFIER);
				}
				}
				setState(535);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FunctionCallContext extends ParserRuleContext {
		public QualifiedIdentifierContext qualifiedIdentifier() {
			return getRuleContext(QualifiedIdentifierContext.class,0);
		}
		public TerminalNode LPAREN() { return getToken(DataWeaveParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(DataWeaveParser.RPAREN, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(DataWeaveParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(DataWeaveParser.COMMA, i);
		}
		public FunctionCallContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionCall; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterFunctionCall(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitFunctionCall(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitFunctionCall(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionCallContext functionCall() throws RecognitionException {
		FunctionCallContext _localctx = new FunctionCallContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_functionCall);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(536);
			qualifiedIdentifier();
			setState(537);
			match(LPAREN);
			setState(546);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (((((_la - 24)) & ~0x3f) == 0 && ((1L << (_la - 24)) & 47416319284364313L) != 0)) {
				{
				setState(538);
				expression();
				setState(543);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(539);
					match(COMMA);
					setState(540);
					expression();
					}
					}
					setState(545);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(548);
			match(RPAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TypeExpressionContext extends ParserRuleContext {
		public TypeExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeExpression; }
	 
		public TypeExpressionContext() { }
		public void copyFrom(TypeExpressionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class BooleanTypeContext extends TypeExpressionContext {
		public BooleanTypeContext(TypeExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterBooleanType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitBooleanType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitBooleanType(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class NumberTypeContext extends TypeExpressionContext {
		public NumberTypeContext(TypeExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterNumberType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitNumberType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitNumberType(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AnyTypeContext extends TypeExpressionContext {
		public AnyTypeContext(TypeExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterAnyType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitAnyType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitAnyType(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class TimeTypeContext extends TypeExpressionContext {
		public TimeTypeContext(TypeExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterTimeType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitTimeType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitTimeType(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class LocalTimeTypeContext extends TypeExpressionContext {
		public LocalTimeTypeContext(TypeExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterLocalTimeType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitLocalTimeType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitLocalTimeType(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class DateTimeTypeContext extends TypeExpressionContext {
		public DateTimeTypeContext(TypeExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterDateTimeType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitDateTimeType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitDateTimeType(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class RegexTypeContext extends TypeExpressionContext {
		public RegexTypeContext(TypeExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterRegexType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitRegexType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitRegexType(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ObjectTypeContext extends TypeExpressionContext {
		public ObjectTypeContext(TypeExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterObjectType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitObjectType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitObjectType(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class NamedTypeContext extends TypeExpressionContext {
		public TerminalNode IDENTIFIER() { return getToken(DataWeaveParser.IDENTIFIER, 0); }
		public NamedTypeContext(TypeExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterNamedType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitNamedType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitNamedType(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class DateTypeContext extends TypeExpressionContext {
		public DateTypeContext(TypeExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterDateType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitDateType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitDateType(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PeriodTypeContext extends TypeExpressionContext {
		public PeriodTypeContext(TypeExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterPeriodType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitPeriodType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitPeriodType(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class StringTypeContext extends TypeExpressionContext {
		public StringTypeContext(TypeExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterStringType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitStringType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitStringType(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class LocalDateTimeTypeContext extends TypeExpressionContext {
		public LocalDateTimeTypeContext(TypeExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).enterLocalDateTimeType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataWeaveListener ) ((DataWeaveListener)listener).exitLocalDateTimeType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataWeaveVisitor ) return ((DataWeaveVisitor<? extends T>)visitor).visitLocalDateTimeType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeExpressionContext typeExpression() throws RecognitionException {
		TypeExpressionContext _localctx = new TypeExpressionContext(_ctx, getState());
		enterRule(_localctx, 86, RULE_typeExpression);
		try {
			setState(564);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IDENTIFIER:
				_localctx = new NamedTypeContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(550);
				match(IDENTIFIER);
				}
				break;
			case T__0:
				_localctx = new StringTypeContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(551);
				match(T__0);
				}
				break;
			case T__1:
				_localctx = new BooleanTypeContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(552);
				match(T__1);
				}
				break;
			case T__2:
				_localctx = new NumberTypeContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(553);
				match(T__2);
				}
				break;
			case T__3:
				_localctx = new RegexTypeContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(554);
				match(T__3);
				}
				break;
			case T__4:
				_localctx = new RegexTypeContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(555);
				match(T__4);
				}
				break;
			case T__5:
				_localctx = new DateTypeContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(556);
				match(T__5);
				}
				break;
			case T__6:
				_localctx = new DateTimeTypeContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(557);
				match(T__6);
				}
				break;
			case T__7:
				_localctx = new LocalDateTimeTypeContext(_localctx);
				enterOuterAlt(_localctx, 9);
				{
				setState(558);
				match(T__7);
				}
				break;
			case T__8:
				_localctx = new LocalTimeTypeContext(_localctx);
				enterOuterAlt(_localctx, 10);
				{
				setState(559);
				match(T__8);
				}
				break;
			case T__9:
				_localctx = new TimeTypeContext(_localctx);
				enterOuterAlt(_localctx, 11);
				{
				setState(560);
				match(T__9);
				}
				break;
			case T__10:
				_localctx = new PeriodTypeContext(_localctx);
				enterOuterAlt(_localctx, 12);
				{
				setState(561);
				match(T__10);
				}
				break;
			case T__11:
				_localctx = new ObjectTypeContext(_localctx);
				enterOuterAlt(_localctx, 13);
				{
				setState(562);
				match(T__11);
				}
				break;
			case T__12:
				_localctx = new AnyTypeContext(_localctx);
				enterOuterAlt(_localctx, 14);
				{
				setState(563);
				match(T__12);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 16:
			return operationExpression_sempred((OperationExpressionContext)_localctx, predIndex);
		case 29:
			return typeCoercionExpression_sempred((TypeCoercionExpressionContext)_localctx, predIndex);
		case 32:
			return primaryExpression_sempred((PrimaryExpressionContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean operationExpression_sempred(OperationExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 7);
		case 1:
			return precpred(_ctx, 6);
		case 2:
			return precpred(_ctx, 5);
		case 3:
			return precpred(_ctx, 4);
		case 4:
			return precpred(_ctx, 3);
		case 5:
			return precpred(_ctx, 2);
		}
		return true;
	}
	private boolean typeCoercionExpression_sempred(TypeCoercionExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 6:
			return precpred(_ctx, 2);
		}
		return true;
	}
	private boolean primaryExpression_sempred(PrimaryExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 7:
			return precpred(_ctx, 2);
		case 8:
			return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u0001W\u0237\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"+
		"\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b"+
		"\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007\u001e"+
		"\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!\u0007!\u0002\"\u0007\"\u0002"+
		"#\u0007#\u0002$\u0007$\u0002%\u0007%\u0002&\u0007&\u0002\'\u0007\'\u0002"+
		"(\u0007(\u0002)\u0007)\u0002*\u0007*\u0002+\u0007+\u0001\u0000\u0001\u0000"+
		"\u0001\u0000\u0003\u0000\\\b\u0000\u0003\u0000^\b\u0000\u0001\u0000\u0005"+
		"\u0000a\b\u0000\n\u0000\f\u0000d\t\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0003\u0000i\b\u0000\u0001\u0000\u0001\u0000\u0005\u0000m\b\u0000"+
		"\n\u0000\f\u0000p\t\u0000\u0001\u0000\u0001\u0000\u0003\u0000t\b\u0000"+
		"\u0001\u0001\u0001\u0001\u0005\u0001x\b\u0001\n\u0001\f\u0001{\t\u0001"+
		"\u0004\u0001}\b\u0001\u000b\u0001\f\u0001~\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0003"+
		"\u0002\u0089\b\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0005\u0004\u0091\b\u0004\n\u0004\f\u0004\u0094\t\u0004"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001"+
		"\b\u0001\b\u0005\b\u00a4\b\b\n\b\f\b\u00a7\t\b\u0001\b\u0001\b\u0001\b"+
		"\u0003\b\u00ac\b\b\u0003\b\u00ae\b\b\u0001\t\u0001\t\u0001\t\u0001\t\u0003"+
		"\t\u00b4\b\t\u0003\t\u00b6\b\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001\u000b"+
		"\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001"+
		"\f\u0001\f\u0003\f\u00c5\b\f\u0001\f\u0001\f\u0001\f\u0003\f\u00ca\b\f"+
		"\u0001\f\u0001\f\u0001\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001"+
		"\u000e\u0001\u000e\u0005\u000e\u00d6\b\u000e\n\u000e\f\u000e\u00d9\t\u000e"+
		"\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010"+
		"\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010"+
		"\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010"+
		"\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010"+
		"\u0001\u0010\u0005\u0010\u00f4\b\u0010\n\u0010\f\u0010\u00f7\t\u0010\u0001"+
		"\u0011\u0001\u0011\u0001\u0011\u0003\u0011\u00fc\b\u0011\u0001\u0012\u0001"+
		"\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0003\u0012\u0104"+
		"\b\u0012\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001"+
		"\u0013\u0001\u0014\u0001\u0014\u0001\u0014\u0005\u0014\u010f\b\u0014\n"+
		"\u0014\f\u0014\u0112\t\u0014\u0001\u0015\u0001\u0015\u0001\u0015\u0003"+
		"\u0015\u0117\b\u0015\u0001\u0015\u0001\u0015\u0003\u0015\u011b\b\u0015"+
		"\u0001\u0016\u0001\u0016\u0001\u0016\u0005\u0016\u0120\b\u0016\n\u0016"+
		"\f\u0016\u0123\t\u0016\u0001\u0017\u0001\u0017\u0001\u0017\u0005\u0017"+
		"\u0128\b\u0017\n\u0017\f\u0017\u012b\t\u0017\u0001\u0018\u0001\u0018\u0001"+
		"\u0018\u0005\u0018\u0130\b\u0018\n\u0018\f\u0018\u0133\t\u0018\u0001\u0019"+
		"\u0001\u0019\u0001\u0019\u0005\u0019\u0138\b\u0019\n\u0019\f\u0019\u013b"+
		"\t\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0003\u0019\u0141"+
		"\b\u0019\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0005\u001a\u0147"+
		"\b\u001a\n\u001a\f\u001a\u014a\t\u001a\u0001\u001b\u0001\u001b\u0001\u001c"+
		"\u0001\u001c\u0001\u001c\u0005\u001c\u0151\b\u001c\n\u001c\f\u001c\u0154"+
		"\t\u001c\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001"+
		"\u001d\u0001\u001d\u0003\u001d\u015d\b\u001d\u0005\u001d\u015f\b\u001d"+
		"\n\u001d\f\u001d\u0162\t\u001d\u0001\u001e\u0001\u001e\u0001\u001e\u0001"+
		"\u001e\u0001\u001e\u0001\u001e\u0001\u001f\u0001\u001f\u0001\u001f\u0001"+
		"\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001"+
		"\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001"+
		"\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001"+
		"\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0003\u001f\u0184"+
		"\b\u001f\u0001 \u0001 \u0001 \u0001 \u0001 \u0001 \u0001 \u0001 \u0001"+
		" \u0001 \u0001 \u0001 \u0001 \u0005 \u0193\b \n \f \u0196\t \u0001 \u0001"+
		" \u0003 \u019a\b \u0001 \u0001 \u0001 \u0001 \u0001 \u0001 \u0001 \u0001"+
		" \u0001 \u0001 \u0001 \u0003 \u01a7\b \u0001 \u0001 \u0001 \u0001 \u0001"+
		" \u0001 \u0001 \u0005 \u01b0\b \n \f \u01b3\t \u0001!\u0001!\u0001!\u0001"+
		"!\u0001\"\u0001\"\u0001\"\u0001\"\u0001#\u0001#\u0001#\u0003#\u01c0\b"+
		"#\u0001#\u0001#\u0001#\u0001#\u0001$\u0001$\u0001$\u0001$\u0001$\u0001"+
		"$\u0001$\u0001$\u0001$\u0001$\u0001$\u0001$\u0001$\u0001$\u0001$\u0001"+
		"$\u0001$\u0003$\u01d7\b$\u0001%\u0001%\u0001&\u0001&\u0001&\u0001&\u0005"+
		"&\u01df\b&\n&\f&\u01e2\t&\u0003&\u01e4\b&\u0001&\u0001&\u0001\'\u0001"+
		"\'\u0001\'\u0003\'\u01eb\b\'\u0001\'\u0005\'\u01ee\b\'\n\'\f\'\u01f1\t"+
		"\'\u0001\'\u0001\'\u0001\'\u0001\'\u0001\'\u0001\'\u0001\'\u0001\'\u0003"+
		"\'\u01fb\b\'\u0001(\u0001(\u0001(\u0001(\u0001(\u0001(\u0001(\u0001(\u0001"+
		"(\u0001(\u0001(\u0001(\u0001(\u0001(\u0001(\u0001(\u0001(\u0001(\u0003"+
		"(\u020f\b(\u0001)\u0001)\u0001)\u0005)\u0214\b)\n)\f)\u0217\t)\u0001*"+
		"\u0001*\u0001*\u0001*\u0001*\u0005*\u021e\b*\n*\f*\u0221\t*\u0003*\u0223"+
		"\b*\u0001*\u0001*\u0001+\u0001+\u0001+\u0001+\u0001+\u0001+\u0001+\u0001"+
		"+\u0001+\u0001+\u0001+\u0001+\u0001+\u0001+\u0003+\u0235\b+\u0001+\u0000"+
		"\u0003 :@,\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016"+
		"\u0018\u001a\u001c\u001e \"$&(*,.02468:<>@BDFHJLNPRTV\u0000\u0004\u0001"+
		"\u0000RS\u0004\u0000\u0018\u0018==BBDD\u0001\u00009:\u0004\u0000\u0018"+
		"\u0018\"\"BBDF\u026e\u0000s\u0001\u0000\u0000\u0000\u0002|\u0001\u0000"+
		"\u0000\u0000\u0004\u0088\u0001\u0000\u0000\u0000\u0006\u008a\u0001\u0000"+
		"\u0000\u0000\b\u008d\u0001\u0000\u0000\u0000\n\u0095\u0001\u0000\u0000"+
		"\u0000\f\u0099\u0001\u0000\u0000\u0000\u000e\u009b\u0001\u0000\u0000\u0000"+
		"\u0010\u009f\u0001\u0000\u0000\u0000\u0012\u00b5\u0001\u0000\u0000\u0000"+
		"\u0014\u00b7\u0001\u0000\u0000\u0000\u0016\u00bb\u0001\u0000\u0000\u0000"+
		"\u0018\u00c0\u0001\u0000\u0000\u0000\u001a\u00ce\u0001\u0000\u0000\u0000"+
		"\u001c\u00d3\u0001\u0000\u0000\u0000\u001e\u00da\u0001\u0000\u0000\u0000"+
		" \u00dc\u0001\u0000\u0000\u0000\"\u00f8\u0001\u0000\u0000\u0000$\u0103"+
		"\u0001\u0000\u0000\u0000&\u0105\u0001\u0000\u0000\u0000(\u010b\u0001\u0000"+
		"\u0000\u0000*\u0113\u0001\u0000\u0000\u0000,\u011c\u0001\u0000\u0000\u0000"+
		".\u0124\u0001\u0000\u0000\u00000\u012c\u0001\u0000\u0000\u00002\u0140"+
		"\u0001\u0000\u0000\u00004\u0142\u0001\u0000\u0000\u00006\u014b\u0001\u0000"+
		"\u0000\u00008\u014d\u0001\u0000\u0000\u0000:\u0155\u0001\u0000\u0000\u0000"+
		"<\u0163\u0001\u0000\u0000\u0000>\u0183\u0001\u0000\u0000\u0000@\u01a6"+
		"\u0001\u0000\u0000\u0000B\u01b4\u0001\u0000\u0000\u0000D\u01b8\u0001\u0000"+
		"\u0000\u0000F\u01bc\u0001\u0000\u0000\u0000H\u01d6\u0001\u0000\u0000\u0000"+
		"J\u01d8\u0001\u0000\u0000\u0000L\u01da\u0001\u0000\u0000\u0000N\u01fa"+
		"\u0001\u0000\u0000\u0000P\u020e\u0001\u0000\u0000\u0000R\u0210\u0001\u0000"+
		"\u0000\u0000T\u0218\u0001\u0000\u0000\u0000V\u0234\u0001\u0000\u0000\u0000"+
		"X]\u0003\u0002\u0001\u0000Y[\u0005Q\u0000\u0000Z\\\u0003\u001c\u000e\u0000"+
		"[Z\u0001\u0000\u0000\u0000[\\\u0001\u0000\u0000\u0000\\^\u0001\u0000\u0000"+
		"\u0000]Y\u0001\u0000\u0000\u0000]^\u0001\u0000\u0000\u0000^b\u0001\u0000"+
		"\u0000\u0000_a\u0005S\u0000\u0000`_\u0001\u0000\u0000\u0000ad\u0001\u0000"+
		"\u0000\u0000b`\u0001\u0000\u0000\u0000bc\u0001\u0000\u0000\u0000ce\u0001"+
		"\u0000\u0000\u0000db\u0001\u0000\u0000\u0000ef\u0005\u0000\u0000\u0001"+
		"ft\u0001\u0000\u0000\u0000gi\u0005Q\u0000\u0000hg\u0001\u0000\u0000\u0000"+
		"hi\u0001\u0000\u0000\u0000ij\u0001\u0000\u0000\u0000jn\u0003\u001c\u000e"+
		"\u0000km\u0005S\u0000\u0000lk\u0001\u0000\u0000\u0000mp\u0001\u0000\u0000"+
		"\u0000nl\u0001\u0000\u0000\u0000no\u0001\u0000\u0000\u0000oq\u0001\u0000"+
		"\u0000\u0000pn\u0001\u0000\u0000\u0000qr\u0005\u0000\u0000\u0001rt\u0001"+
		"\u0000\u0000\u0000sX\u0001\u0000\u0000\u0000sh\u0001\u0000\u0000\u0000"+
		"t\u0001\u0001\u0000\u0000\u0000uy\u0003\u0004\u0002\u0000vx\u0007\u0000"+
		"\u0000\u0000wv\u0001\u0000\u0000\u0000x{\u0001\u0000\u0000\u0000yw\u0001"+
		"\u0000\u0000\u0000yz\u0001\u0000\u0000\u0000z}\u0001\u0000\u0000\u0000"+
		"{y\u0001\u0000\u0000\u0000|u\u0001\u0000\u0000\u0000}~\u0001\u0000\u0000"+
		"\u0000~|\u0001\u0000\u0000\u0000~\u007f\u0001\u0000\u0000\u0000\u007f"+
		"\u0003\u0001\u0000\u0000\u0000\u0080\u0089\u0003\u0006\u0003\u0000\u0081"+
		"\u0089\u0003\b\u0004\u0000\u0082\u0089\u0003\u000e\u0007\u0000\u0083\u0089"+
		"\u0003\u0010\b\u0000\u0084\u0089\u0003\u0014\n\u0000\u0085\u0089\u0003"+
		"\u0016\u000b\u0000\u0086\u0089\u0003\u0018\f\u0000\u0087\u0089\u0003\u001a"+
		"\r\u0000\u0088\u0080\u0001\u0000\u0000\u0000\u0088\u0081\u0001\u0000\u0000"+
		"\u0000\u0088\u0082\u0001\u0000\u0000\u0000\u0088\u0083\u0001\u0000\u0000"+
		"\u0000\u0088\u0084\u0001\u0000\u0000\u0000\u0088\u0085\u0001\u0000\u0000"+
		"\u0000\u0088\u0086\u0001\u0000\u0000\u0000\u0088\u0087\u0001\u0000\u0000"+
		"\u0000\u0089\u0005\u0001\u0000\u0000\u0000\u008a\u008b\u0005\u0014\u0000"+
		"\u0000\u008b\u008c\u0005B\u0000\u0000\u008c\u0007\u0001\u0000\u0000\u0000"+
		"\u008d\u008e\u0005\u0012\u0000\u0000\u008e\u0092\u0005A\u0000\u0000\u008f"+
		"\u0091\u0003\n\u0005\u0000\u0090\u008f\u0001\u0000\u0000\u0000\u0091\u0094"+
		"\u0001\u0000\u0000\u0000\u0092\u0090\u0001\u0000\u0000\u0000\u0092\u0093"+
		"\u0001\u0000\u0000\u0000\u0093\t\u0001\u0000\u0000\u0000\u0094\u0092\u0001"+
		"\u0000\u0000\u0000\u0095\u0096\u0005=\u0000\u0000\u0096\u0097\u0005\u0016"+
		"\u0000\u0000\u0097\u0098\u0003\f\u0006\u0000\u0098\u000b\u0001\u0000\u0000"+
		"\u0000\u0099\u009a\u0007\u0001\u0000\u0000\u009a\r\u0001\u0000\u0000\u0000"+
		"\u009b\u009c\u0005\u0013\u0000\u0000\u009c\u009d\u0005=\u0000\u0000\u009d"+
		"\u009e\u0005A\u0000\u0000\u009e\u000f\u0001\u0000\u0000\u0000\u009f\u00a0"+
		"\u0005\u0010\u0000\u0000\u00a0\u00a5\u0003\u0012\t\u0000\u00a1\u00a2\u0005"+
		"J\u0000\u0000\u00a2\u00a4\u0003\u0012\t\u0000\u00a3\u00a1\u0001\u0000"+
		"\u0000\u0000\u00a4\u00a7\u0001\u0000\u0000\u0000\u00a5\u00a3\u0001\u0000"+
		"\u0000\u0000\u00a5\u00a6\u0001\u0000\u0000\u0000\u00a6\u00ad\u0001\u0000"+
		"\u0000\u0000\u00a7\u00a5\u0001\u0000\u0000\u0000\u00a8\u00ab\u00054\u0000"+
		"\u0000\u00a9\u00ac\u0003R)\u0000\u00aa\u00ac\u0005D\u0000\u0000\u00ab"+
		"\u00a9\u0001\u0000\u0000\u0000\u00ab\u00aa\u0001\u0000\u0000\u0000\u00ac"+
		"\u00ae\u0001\u0000\u0000\u0000\u00ad\u00a8\u0001\u0000\u0000\u0000\u00ad"+
		"\u00ae\u0001\u0000\u0000\u0000\u00ae\u0011\u0001\u0000\u0000\u0000\u00af"+
		"\u00b6\u0005U\u0000\u0000\u00b0\u00b3\u0003R)\u0000\u00b1\u00b2\u0005"+
		" \u0000\u0000\u00b2\u00b4\u0005=\u0000\u0000\u00b3\u00b1\u0001\u0000\u0000"+
		"\u0000\u00b3\u00b4\u0001\u0000\u0000\u0000\u00b4\u00b6\u0001\u0000\u0000"+
		"\u0000\u00b5\u00af\u0001\u0000\u0000\u0000\u00b5\u00b0\u0001\u0000\u0000"+
		"\u0000\u00b6\u0013\u0001\u0000\u0000\u0000\u00b7\u00b8\u0005\u0011\u0000"+
		"\u0000\u00b8\u00b9\u0005=\u0000\u0000\u00b9\u00ba\u0005@\u0000\u0000\u00ba"+
		"\u0015\u0001\u0000\u0000\u0000\u00bb\u00bc\u0005\u000e\u0000\u0000\u00bc"+
		"\u00bd\u0005=\u0000\u0000\u00bd\u00be\u0005\u0016\u0000\u0000\u00be\u00bf"+
		"\u0003\u001e\u000f\u0000\u00bf\u0017\u0001\u0000\u0000\u0000\u00c0\u00c1"+
		"\u0005\u000f\u0000\u0000\u00c1\u00c2\u0005=\u0000\u0000\u00c2\u00c4\u0005"+
		"O\u0000\u0000\u00c3\u00c5\u0003(\u0014\u0000\u00c4\u00c3\u0001\u0000\u0000"+
		"\u0000\u00c4\u00c5\u0001\u0000\u0000\u0000\u00c5\u00c6\u0001\u0000\u0000"+
		"\u0000\u00c6\u00c9\u0005P\u0000\u0000\u00c7\u00c8\u0005I\u0000\u0000\u00c8"+
		"\u00ca\u0003V+\u0000\u00c9\u00c7\u0001\u0000\u0000\u0000\u00c9\u00ca\u0001"+
		"\u0000\u0000\u0000\u00ca\u00cb\u0001\u0000\u0000\u0000\u00cb\u00cc\u0005"+
		"\u0016\u0000\u0000\u00cc\u00cd\u0003\u001e\u000f\u0000\u00cd\u0019\u0001"+
		"\u0000\u0000\u0000\u00ce\u00cf\u0005\u0015\u0000\u0000\u00cf\u00d0\u0005"+
		"=\u0000\u0000\u00d0\u00d1\u0005\u0016\u0000\u0000\u00d1\u00d2\u0003V+"+
		"\u0000\u00d2\u001b\u0001\u0000\u0000\u0000\u00d3\u00d7\u0003\u001e\u000f"+
		"\u0000\u00d4\u00d6\u0005S\u0000\u0000\u00d5\u00d4\u0001\u0000\u0000\u0000"+
		"\u00d6\u00d9\u0001\u0000\u0000\u0000\u00d7\u00d5\u0001\u0000\u0000\u0000"+
		"\u00d7\u00d8\u0001\u0000\u0000\u0000\u00d8\u001d\u0001\u0000\u0000\u0000"+
		"\u00d9\u00d7\u0001\u0000\u0000\u0000\u00da\u00db\u0003 \u0010\u0000\u00db"+
		"\u001f\u0001\u0000\u0000\u0000\u00dc\u00dd\u0006\u0010\uffff\uffff\u0000"+
		"\u00dd\u00de\u0003\"\u0011\u0000\u00de\u00f5\u0001\u0000\u0000\u0000\u00df"+
		"\u00e0\n\u0007\u0000\u0000\u00e0\u00e1\u0005-\u0000\u0000\u00e1\u00f4"+
		"\u0003$\u0012\u0000\u00e2\u00e3\n\u0006\u0000\u0000\u00e3\u00e4\u0005"+
		",\u0000\u0000\u00e4\u00f4\u0003$\u0012\u0000\u00e5\u00e6\n\u0005\u0000"+
		"\u0000\u00e6\u00e7\u0005.\u0000\u0000\u00e7\u00f4\u0003$\u0012\u0000\u00e8"+
		"\u00e9\n\u0004\u0000\u0000\u00e9\u00ea\u00052\u0000\u0000\u00ea\u00eb"+
		"\u0005F\u0000\u0000\u00eb\u00ec\u00053\u0000\u0000\u00ec\u00f4\u0003\u001e"+
		"\u000f\u0000\u00ed\u00ee\n\u0003\u0000\u0000\u00ee\u00ef\u0005<\u0000"+
		"\u0000\u00ef\u00f4\u0003\"\u0011\u0000\u00f0\u00f1\n\u0002\u0000\u0000"+
		"\u00f1\u00f2\u0005=\u0000\u0000\u00f2\u00f4\u0003\"\u0011\u0000\u00f3"+
		"\u00df\u0001\u0000\u0000\u0000\u00f3\u00e2\u0001\u0000\u0000\u0000\u00f3"+
		"\u00e5\u0001\u0000\u0000\u0000\u00f3\u00e8\u0001\u0000\u0000\u0000\u00f3"+
		"\u00ed\u0001\u0000\u0000\u0000\u00f3\u00f0\u0001\u0000\u0000\u0000\u00f4"+
		"\u00f7\u0001\u0000\u0000\u0000\u00f5\u00f3\u0001\u0000\u0000\u0000\u00f5"+
		"\u00f6\u0001\u0000\u0000\u0000\u00f6!\u0001\u0000\u0000\u0000\u00f7\u00f5"+
		"\u0001\u0000\u0000\u0000\u00f8\u00fb\u0003,\u0016\u0000\u00f9\u00fa\u0005"+
		"#\u0000\u0000\u00fa\u00fc\u0003,\u0016\u0000\u00fb\u00f9\u0001\u0000\u0000"+
		"\u0000\u00fb\u00fc\u0001\u0000\u0000\u0000\u00fc#\u0001\u0000\u0000\u0000"+
		"\u00fd\u0104\u0003&\u0013\u0000\u00fe\u0104\u0003\u001e\u000f\u0000\u00ff"+
		"\u0100\u0005O\u0000\u0000\u0100\u0101\u0003$\u0012\u0000\u0101\u0102\u0005"+
		"P\u0000\u0000\u0102\u0104\u0001\u0000\u0000\u0000\u0103\u00fd\u0001\u0000"+
		"\u0000\u0000\u0103\u00fe\u0001\u0000\u0000\u0000\u0103\u00ff\u0001\u0000"+
		"\u0000\u0000\u0104%\u0001\u0000\u0000\u0000\u0105\u0106\u0005O\u0000\u0000"+
		"\u0106\u0107\u0003(\u0014\u0000\u0107\u0108\u0005P\u0000\u0000\u0108\u0109"+
		"\u0005\u0017\u0000\u0000\u0109\u010a\u0003\u001e\u000f\u0000\u010a\'\u0001"+
		"\u0000\u0000\u0000\u010b\u0110\u0003*\u0015\u0000\u010c\u010d\u0005J\u0000"+
		"\u0000\u010d\u010f\u0003*\u0015\u0000\u010e\u010c\u0001\u0000\u0000\u0000"+
		"\u010f\u0112\u0001\u0000\u0000\u0000\u0110\u010e\u0001\u0000\u0000\u0000"+
		"\u0110\u0111\u0001\u0000\u0000\u0000\u0111)\u0001\u0000\u0000\u0000\u0112"+
		"\u0110\u0001\u0000\u0000\u0000\u0113\u0116\u0005=\u0000\u0000\u0114\u0115"+
		"\u0005I\u0000\u0000\u0115\u0117\u0003V+\u0000\u0116\u0114\u0001\u0000"+
		"\u0000\u0000\u0116\u0117\u0001\u0000\u0000\u0000\u0117\u011a\u0001\u0000"+
		"\u0000\u0000\u0118\u0119\u0005\u0016\u0000\u0000\u0119\u011b\u0003\u001e"+
		"\u000f\u0000\u011a\u0118\u0001\u0000\u0000\u0000\u011a\u011b\u0001\u0000"+
		"\u0000\u0000\u011b+\u0001\u0000\u0000\u0000\u011c\u0121\u0003.\u0017\u0000"+
		"\u011d\u011e\u0005\u001a\u0000\u0000\u011e\u0120\u0003.\u0017\u0000\u011f"+
		"\u011d\u0001\u0000\u0000\u0000\u0120\u0123\u0001\u0000\u0000\u0000\u0121"+
		"\u011f\u0001\u0000\u0000\u0000\u0121\u0122\u0001\u0000\u0000\u0000\u0122"+
		"-\u0001\u0000\u0000\u0000\u0123\u0121\u0001\u0000\u0000\u0000\u0124\u0129"+
		"\u00030\u0018\u0000\u0125\u0126\u0005\u0019\u0000\u0000\u0126\u0128\u0003"+
		"0\u0018\u0000\u0127\u0125\u0001\u0000\u0000\u0000\u0128\u012b\u0001\u0000"+
		"\u0000\u0000\u0129\u0127\u0001\u0000\u0000\u0000\u0129\u012a\u0001\u0000"+
		"\u0000\u0000\u012a/\u0001\u0000\u0000\u0000\u012b\u0129\u0001\u0000\u0000"+
		"\u0000\u012c\u0131\u00032\u0019\u0000\u012d\u012e\u00056\u0000\u0000\u012e"+
		"\u0130\u00032\u0019\u0000\u012f\u012d\u0001\u0000\u0000\u0000\u0130\u0133"+
		"\u0001\u0000\u0000\u0000\u0131\u012f\u0001\u0000\u0000\u0000\u0131\u0132"+
		"\u0001\u0000\u0000\u0000\u01321\u0001\u0000\u0000\u0000\u0133\u0131\u0001"+
		"\u0000\u0000\u0000\u0134\u0139\u00034\u001a\u0000\u0135\u0136\u00057\u0000"+
		"\u0000\u0136\u0138\u00034\u001a\u0000\u0137\u0135\u0001\u0000\u0000\u0000"+
		"\u0138\u013b\u0001\u0000\u0000\u0000\u0139\u0137\u0001\u0000\u0000\u0000"+
		"\u0139\u013a\u0001\u0000\u0000\u0000\u013a\u0141\u0001\u0000\u0000\u0000"+
		"\u013b\u0139\u0001\u0000\u0000\u0000\u013c\u013d\u00034\u001a\u0000\u013d"+
		"\u013e\u0005!\u0000\u0000\u013e\u013f\u0003V+\u0000\u013f\u0141\u0001"+
		"\u0000\u0000\u0000\u0140\u0134\u0001\u0000\u0000\u0000\u0140\u013c\u0001"+
		"\u0000\u0000\u0000\u01413\u0001\u0000\u0000\u0000\u0142\u0148\u00038\u001c"+
		"\u0000\u0143\u0144\u00036\u001b\u0000\u0144\u0145\u00038\u001c\u0000\u0145"+
		"\u0147\u0001\u0000\u0000\u0000\u0146\u0143\u0001\u0000\u0000\u0000\u0147"+
		"\u014a\u0001\u0000\u0000\u0000\u0148\u0146\u0001\u0000\u0000\u0000\u0148"+
		"\u0149\u0001\u0000\u0000\u0000\u01495\u0001\u0000\u0000\u0000\u014a\u0148"+
		"\u0001\u0000\u0000\u0000\u014b\u014c\u0007\u0002\u0000\u0000\u014c7\u0001"+
		"\u0000\u0000\u0000\u014d\u0152\u0003:\u001d\u0000\u014e\u014f\u00058\u0000"+
		"\u0000\u014f\u0151\u0003:\u001d\u0000\u0150\u014e\u0001\u0000\u0000\u0000"+
		"\u0151\u0154\u0001\u0000\u0000\u0000\u0152\u0150\u0001\u0000\u0000\u0000"+
		"\u0152\u0153\u0001\u0000\u0000\u0000\u01539\u0001\u0000\u0000\u0000\u0154"+
		"\u0152\u0001\u0000\u0000\u0000\u0155\u0156\u0006\u001d\uffff\uffff\u0000"+
		"\u0156\u0157\u0003>\u001f\u0000\u0157\u0160\u0001\u0000\u0000\u0000\u0158"+
		"\u0159\n\u0002\u0000\u0000\u0159\u015a\u0005 \u0000\u0000\u015a\u015c"+
		"\u0003V+\u0000\u015b\u015d\u0003<\u001e\u0000\u015c\u015b\u0001\u0000"+
		"\u0000\u0000\u015c\u015d\u0001\u0000\u0000\u0000\u015d\u015f\u0001\u0000"+
		"\u0000\u0000\u015e\u0158\u0001\u0000\u0000\u0000\u015f\u0162\u0001\u0000"+
		"\u0000\u0000\u0160\u015e\u0001\u0000\u0000\u0000\u0160\u0161\u0001\u0000"+
		"\u0000\u0000\u0161;\u0001\u0000\u0000\u0000\u0162\u0160\u0001\u0000\u0000"+
		"\u0000\u0163\u0164\u0005K\u0000\u0000\u0164\u0165\u0005=\u0000\u0000\u0165"+
		"\u0166\u0005I\u0000\u0000\u0166\u0167\u0005D\u0000\u0000\u0167\u0168\u0005"+
		"L\u0000\u0000\u0168=\u0001\u0000\u0000\u0000\u0169\u016a\u0005/\u0000"+
		"\u0000\u016a\u016b\u0005O\u0000\u0000\u016b\u016c\u0003\u001e\u000f\u0000"+
		"\u016c\u016d\u0005P\u0000\u0000\u016d\u0184\u0001\u0000\u0000\u0000\u016e"+
		"\u016f\u0005/\u0000\u0000\u016f\u0184\u0003\u001e\u000f\u0000\u0170\u0171"+
		"\u00050\u0000\u0000\u0171\u0172\u0005O\u0000\u0000\u0172\u0173\u0003\u001e"+
		"\u000f\u0000\u0173\u0174\u0005P\u0000\u0000\u0174\u0184\u0001\u0000\u0000"+
		"\u0000\u0175\u0176\u00050\u0000\u0000\u0176\u0184\u0003\u001e\u000f\u0000"+
		"\u0177\u0178\u00051\u0000\u0000\u0178\u0179\u0005O\u0000\u0000\u0179\u017a"+
		"\u0003\u001e\u000f\u0000\u017a\u017b\u0005P\u0000\u0000\u017b\u0184\u0001"+
		"\u0000\u0000\u0000\u017c\u017d\u00051\u0000\u0000\u017d\u0184\u0003\u001e"+
		"\u000f\u0000\u017e\u017f\u0005\u001b\u0000\u0000\u017f\u0184\u0003\u001e"+
		"\u000f\u0000\u0180\u0181\u0005:\u0000\u0000\u0181\u0184\u0003\u001e\u000f"+
		"\u0000\u0182\u0184\u0003@ \u0000\u0183\u0169\u0001\u0000\u0000\u0000\u0183"+
		"\u016e\u0001\u0000\u0000\u0000\u0183\u0170\u0001\u0000\u0000\u0000\u0183"+
		"\u0175\u0001\u0000\u0000\u0000\u0183\u0177\u0001\u0000\u0000\u0000\u0183"+
		"\u017c\u0001\u0000\u0000\u0000\u0183\u017e\u0001\u0000\u0000\u0000\u0183"+
		"\u0180\u0001\u0000\u0000\u0000\u0183\u0182\u0001\u0000\u0000\u0000\u0184"+
		"?\u0001\u0000\u0000\u0000\u0185\u0186\u0006 \uffff\uffff\u0000\u0186\u0187"+
		"\u0005\u001c\u0000\u0000\u0187\u0188\u0005O\u0000\u0000\u0188\u0189\u0003"+
		"\u001e\u000f\u0000\u0189\u018a\u0005P\u0000\u0000\u018a\u0194\u0003\u001e"+
		"\u000f\u0000\u018b\u018c\u0005\u001d\u0000\u0000\u018c\u018d\u0005\u001c"+
		"\u0000\u0000\u018d\u018e\u0005O\u0000\u0000\u018e\u018f\u0003\u001e\u000f"+
		"\u0000\u018f\u0190\u0005P\u0000\u0000\u0190\u0191\u0003\u001e\u000f\u0000"+
		"\u0191\u0193\u0001\u0000\u0000\u0000\u0192\u018b\u0001\u0000\u0000\u0000"+
		"\u0193\u0196\u0001\u0000\u0000\u0000\u0194\u0192\u0001\u0000\u0000\u0000"+
		"\u0194\u0195\u0001\u0000\u0000\u0000\u0195\u0199\u0001\u0000\u0000\u0000"+
		"\u0196\u0194\u0001\u0000\u0000\u0000\u0197\u0198\u0005\u001d\u0000\u0000"+
		"\u0198\u019a\u0003\u001e\u000f\u0000\u0199\u0197\u0001\u0000\u0000\u0000"+
		"\u0199\u019a\u0001\u0000\u0000\u0000\u019a\u01a7\u0001\u0000\u0000\u0000"+
		"\u019b\u01a7\u0003F#\u0000\u019c\u01a7\u0003&\u0013\u0000\u019d\u01a7"+
		"\u0003D\"\u0000\u019e\u01a7\u0003J%\u0000\u019f\u01a7\u0003T*\u0000\u01a0"+
		"\u01a7\u0003L&\u0000\u01a1\u01a7\u0003N\'\u0000\u01a2\u01a7\u0003B!\u0000"+
		"\u01a3\u01a7\u0005=\u0000\u0000\u01a4\u01a7\u0005?\u0000\u0000\u01a5\u01a7"+
		"\u0005>\u0000\u0000\u01a6\u0185\u0001\u0000\u0000\u0000\u01a6\u019b\u0001"+
		"\u0000\u0000\u0000\u01a6\u019c\u0001\u0000\u0000\u0000\u01a6\u019d\u0001"+
		"\u0000\u0000\u0000\u01a6\u019e\u0001\u0000\u0000\u0000\u01a6\u019f\u0001"+
		"\u0000\u0000\u0000\u01a6\u01a0\u0001\u0000\u0000\u0000\u01a6\u01a1\u0001"+
		"\u0000\u0000\u0000\u01a6\u01a2\u0001\u0000\u0000\u0000\u01a6\u01a3\u0001"+
		"\u0000\u0000\u0000\u01a6\u01a4\u0001\u0000\u0000\u0000\u01a6\u01a5\u0001"+
		"\u0000\u0000\u0000\u01a7\u01b1\u0001\u0000\u0000\u0000\u01a8\u01a9\n\u0002"+
		"\u0000\u0000\u01a9\u01b0\u0003H$\u0000\u01aa\u01ab\n\u0001\u0000\u0000"+
		"\u01ab\u01ac\u0003H$\u0000\u01ac\u01ad\u0005#\u0000\u0000\u01ad\u01ae"+
		"\u0003\u001e\u000f\u0000\u01ae\u01b0\u0001\u0000\u0000\u0000\u01af\u01a8"+
		"\u0001\u0000\u0000\u0000\u01af\u01aa\u0001\u0000\u0000\u0000\u01b0\u01b3"+
		"\u0001\u0000\u0000\u0000\u01b1\u01af\u0001\u0000\u0000\u0000\u01b1\u01b2"+
		"\u0001\u0000\u0000\u0000\u01b2A\u0001\u0000\u0000\u0000\u01b3\u01b1\u0001"+
		"\u0000\u0000\u0000\u01b4\u01b5\u00055\u0000\u0000\u01b5\u01b6\u0005O\u0000"+
		"\u0000\u01b6\u01b7\u0005P\u0000\u0000\u01b7C\u0001\u0000\u0000\u0000\u01b8"+
		"\u01b9\u0005O\u0000\u0000\u01b9\u01ba\u0003\u001e\u000f\u0000\u01ba\u01bb"+
		"\u0005P\u0000\u0000\u01bbE\u0001\u0000\u0000\u0000\u01bc\u01bd\u0005&"+
		"\u0000\u0000\u01bd\u01bf\u0005K\u0000\u0000\u01be\u01c0\u0003\u0002\u0001"+
		"\u0000\u01bf\u01be\u0001\u0000\u0000\u0000\u01bf\u01c0\u0001\u0000\u0000"+
		"\u0000\u01c0\u01c1\u0001\u0000\u0000\u0000\u01c1\u01c2\u0005Q\u0000\u0000"+
		"\u01c2\u01c3\u0003\u001e\u000f\u0000\u01c3\u01c4\u0005L\u0000\u0000\u01c4"+
		"G\u0001\u0000\u0000\u0000\u01c5\u01c6\u0005G\u0000\u0000\u01c6\u01d7\u0005"+
		"=\u0000\u0000\u01c7\u01c8\u0005G\u0000\u0000\u01c8\u01d7\u0005D\u0000"+
		"\u0000\u01c9\u01ca\u0005G\u0000\u0000\u01ca\u01cb\u0005U\u0000\u0000\u01cb"+
		"\u01d7\u0005=\u0000\u0000\u01cc\u01cd\u0005;\u0000\u0000\u01cd\u01d7\u0005"+
		"=\u0000\u0000\u01ce\u01cf\u0005M\u0000\u0000\u01cf\u01d0\u0003\u001e\u000f"+
		"\u0000\u01d0\u01d1\u0005N\u0000\u0000\u01d1\u01d7\u0001\u0000\u0000\u0000"+
		"\u01d2\u01d3\u0005G\u0000\u0000\u01d3\u01d4\u0005V\u0000\u0000\u01d4\u01d7"+
		"\u0005=\u0000\u0000\u01d5\u01d7\u0005W\u0000\u0000\u01d6\u01c5\u0001\u0000"+
		"\u0000\u0000\u01d6\u01c7\u0001\u0000\u0000\u0000\u01d6\u01c9\u0001\u0000"+
		"\u0000\u0000\u01d6\u01cc\u0001\u0000\u0000\u0000\u01d6\u01ce\u0001\u0000"+
		"\u0000\u0000\u01d6\u01d2\u0001\u0000\u0000\u0000\u01d6\u01d5\u0001\u0000"+
		"\u0000\u0000\u01d7I\u0001\u0000\u0000\u0000\u01d8\u01d9\u0007\u0003\u0000"+
		"\u0000\u01d9K\u0001\u0000\u0000\u0000\u01da\u01e3\u0005M\u0000\u0000\u01db"+
		"\u01e0\u0003\u001e\u000f\u0000\u01dc\u01dd\u0005J\u0000\u0000\u01dd\u01df"+
		"\u0003\u001e\u000f\u0000\u01de\u01dc\u0001\u0000\u0000\u0000\u01df\u01e2"+
		"\u0001\u0000\u0000\u0000\u01e0\u01de\u0001\u0000\u0000\u0000\u01e0\u01e1"+
		"\u0001\u0000\u0000\u0000\u01e1\u01e4\u0001\u0000\u0000\u0000\u01e2\u01e0"+
		"\u0001\u0000\u0000\u0000\u01e3\u01db\u0001\u0000\u0000\u0000\u01e3\u01e4"+
		"\u0001\u0000\u0000\u0000\u01e4\u01e5\u0001\u0000\u0000\u0000\u01e5\u01e6"+
		"\u0005N\u0000\u0000\u01e6M\u0001\u0000\u0000\u0000\u01e7\u01e8\u0005K"+
		"\u0000\u0000\u01e8\u01ef\u0003P(\u0000\u01e9\u01eb\u0005J\u0000\u0000"+
		"\u01ea\u01e9\u0001\u0000\u0000\u0000\u01ea\u01eb\u0001\u0000\u0000\u0000"+
		"\u01eb\u01ec\u0001\u0000\u0000\u0000\u01ec\u01ee\u0003P(\u0000\u01ed\u01ea"+
		"\u0001\u0000\u0000\u0000\u01ee\u01f1\u0001\u0000\u0000\u0000\u01ef\u01ed"+
		"\u0001\u0000\u0000\u0000\u01ef\u01f0\u0001\u0000\u0000\u0000\u01f0\u01f2"+
		"\u0001\u0000\u0000\u0000\u01f1\u01ef\u0001\u0000\u0000\u0000\u01f2\u01f3"+
		"\u0005L\u0000\u0000\u01f3\u01fb\u0001\u0000\u0000\u0000\u01f4\u01f5\u0005"+
		"K\u0000\u0000\u01f5\u01f6\u0003P(\u0000\u01f6\u01f7\u0005L\u0000\u0000"+
		"\u01f7\u01fb\u0001\u0000\u0000\u0000\u01f8\u01f9\u0005K\u0000\u0000\u01f9"+
		"\u01fb\u0005L\u0000\u0000\u01fa\u01e7\u0001\u0000\u0000\u0000\u01fa\u01f4"+
		"\u0001\u0000\u0000\u0000\u01fa\u01f8\u0001\u0000\u0000\u0000\u01fbO\u0001"+
		"\u0000\u0000\u0000\u01fc\u01fd\u0005=\u0000\u0000\u01fd\u01fe\u0005I\u0000"+
		"\u0000\u01fe\u020f\u0003\u001e\u000f\u0000\u01ff\u0200\u0005D\u0000\u0000"+
		"\u0200\u0201\u0005I\u0000\u0000\u0201\u020f\u0003\u001e\u000f\u0000\u0202"+
		"\u0203\u0005O\u0000\u0000\u0203\u0204\u0003\u001e\u000f\u0000\u0204\u0205"+
		"\u0005P\u0000\u0000\u0205\u0206\u0005I\u0000\u0000\u0206\u0207\u0003\u001e"+
		"\u000f\u0000\u0207\u020f\u0001\u0000\u0000\u0000\u0208\u0209\u0005O\u0000"+
		"\u0000\u0209\u020a\u0003P(\u0000\u020a\u020b\u0005P\u0000\u0000\u020b"+
		"\u020c\u0005\u001c\u0000\u0000\u020c\u020d\u0003\u001e\u000f\u0000\u020d"+
		"\u020f\u0001\u0000\u0000\u0000\u020e\u01fc\u0001\u0000\u0000\u0000\u020e"+
		"\u01ff\u0001\u0000\u0000\u0000\u020e\u0202\u0001\u0000\u0000\u0000\u020e"+
		"\u0208\u0001\u0000\u0000\u0000\u020fQ\u0001\u0000\u0000\u0000\u0210\u0215"+
		"\u0005=\u0000\u0000\u0211\u0212\u0005H\u0000\u0000\u0212\u0214\u0005="+
		"\u0000\u0000\u0213\u0211\u0001\u0000\u0000\u0000\u0214\u0217\u0001\u0000"+
		"\u0000\u0000\u0215\u0213\u0001\u0000\u0000\u0000\u0215\u0216\u0001\u0000"+
		"\u0000\u0000\u0216S\u0001\u0000\u0000\u0000\u0217\u0215\u0001\u0000\u0000"+
		"\u0000\u0218\u0219\u0003R)\u0000\u0219\u0222\u0005O\u0000\u0000\u021a"+
		"\u021f\u0003\u001e\u000f\u0000\u021b\u021c\u0005J\u0000\u0000\u021c\u021e"+
		"\u0003\u001e\u000f\u0000\u021d\u021b\u0001\u0000\u0000\u0000\u021e\u0221"+
		"\u0001\u0000\u0000\u0000\u021f\u021d\u0001\u0000\u0000\u0000\u021f\u0220"+
		"\u0001\u0000\u0000\u0000\u0220\u0223\u0001\u0000\u0000\u0000\u0221\u021f"+
		"\u0001\u0000\u0000\u0000\u0222\u021a\u0001\u0000\u0000\u0000\u0222\u0223"+
		"\u0001\u0000\u0000\u0000\u0223\u0224\u0001\u0000\u0000\u0000\u0224\u0225"+
		"\u0005P\u0000\u0000\u0225U\u0001\u0000\u0000\u0000\u0226\u0235\u0005="+
		"\u0000\u0000\u0227\u0235\u0005\u0001\u0000\u0000\u0228\u0235\u0005\u0002"+
		"\u0000\u0000\u0229\u0235\u0005\u0003\u0000\u0000\u022a\u0235\u0005\u0004"+
		"\u0000\u0000\u022b\u0235\u0005\u0005\u0000\u0000\u022c\u0235\u0005\u0006"+
		"\u0000\u0000\u022d\u0235\u0005\u0007\u0000\u0000\u022e\u0235\u0005\b\u0000"+
		"\u0000\u022f\u0235\u0005\t\u0000\u0000\u0230\u0235\u0005\n\u0000\u0000"+
		"\u0231\u0235\u0005\u000b\u0000\u0000\u0232\u0235\u0005\f\u0000\u0000\u0233"+
		"\u0235\u0005\r\u0000\u0000\u0234\u0226\u0001\u0000\u0000\u0000\u0234\u0227"+
		"\u0001\u0000\u0000\u0000\u0234\u0228\u0001\u0000\u0000\u0000\u0234\u0229"+
		"\u0001\u0000\u0000\u0000\u0234\u022a\u0001\u0000\u0000\u0000\u0234\u022b"+
		"\u0001\u0000\u0000\u0000\u0234\u022c\u0001\u0000\u0000\u0000\u0234\u022d"+
		"\u0001\u0000\u0000\u0000\u0234\u022e\u0001\u0000\u0000\u0000\u0234\u022f"+
		"\u0001\u0000\u0000\u0000\u0234\u0230\u0001\u0000\u0000\u0000\u0234\u0231"+
		"\u0001\u0000\u0000\u0000\u0234\u0232\u0001\u0000\u0000\u0000\u0234\u0233"+
		"\u0001\u0000\u0000\u0000\u0235W\u0001\u0000\u0000\u00004[]bhnsy~\u0088"+
		"\u0092\u00a5\u00ab\u00ad\u00b3\u00b5\u00c4\u00c9\u00d7\u00f3\u00f5\u00fb"+
		"\u0103\u0110\u0116\u011a\u0121\u0129\u0131\u0139\u0140\u0148\u0152\u015c"+
		"\u0160\u0183\u0194\u0199\u01a6\u01af\u01b1\u01bf\u01d6\u01e0\u01e3\u01ea"+
		"\u01ef\u01fa\u020e\u0215\u021f\u0222\u0234";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}