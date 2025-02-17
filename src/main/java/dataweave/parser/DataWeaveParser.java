// Generated from src/main/java/dataweave/parser/DataWeave.g4 by ANTLR 4.13.2
package dataweave.parser;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class DataWeaveParser extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            T__0 = 1, T__1 = 2, T__2 = 3, T__3 = 4, T__4 = 5, T__5 = 6, T__6 = 7, VAR = 8, FUNCTION = 9,
            INPUT = 10, NAMESPACE = 11, OUTPUT = 12, DW = 13, ASSIGN = 14, ARROW = 15, BOOLEAN = 16,
            OPERATOR_LOGICAL = 17, OPERATOR_COMPARISON = 18, OPERATOR_MATH = 19, OPERATOR_RANGE = 20,
            OPERATOR_CHAIN = 21, IDENTIFIER = 22, URL = 23, MEDIA_TYPE = 24, NUMBER = 25, STRING = 26,
            DATE = 27, REGEX = 28, DOT = 29, COLON = 30, COMMA = 31, LCURLY = 32, RCURLY = 33, LSQUARE = 34,
            RSQUARE = 35, SEPARATOR = 36, WS = 37, NEWLINE = 38, COMMENT = 39, STAR = 40, AT = 41,
            QUESTION = 42, BUILTIN_FUNCTION = 43, INDEX_IDENTIFIER = 44, VALUE_IDENTIFIER = 45;
    public static final int
            RULE_script = 0, RULE_header = 1, RULE_directive = 2, RULE_dwVersion = 3,
            RULE_outputDirective = 4, RULE_inputDirective = 5, RULE_namespaceDirective = 6,
            RULE_variableDeclaration = 7, RULE_functionDeclaration = 8, RULE_body = 9,
            RULE_expression = 10, RULE_expressionRest = 11, RULE_primaryExpression = 12,
            RULE_primitive = 13, RULE_grouped = 14, RULE_selectorExpression = 15,
            RULE_implicitLambdaExpression = 16, RULE_inlineLambda = 17, RULE_functionParameters = 18,
            RULE_literal = 19, RULE_array = 20, RULE_object = 21, RULE_keyValue = 22,
            RULE_functionCall = 23;

    private static String[] makeRuleNames() {
        return new String[]{
                "script", "header", "directive", "dwVersion", "outputDirective", "inputDirective",
                "namespaceDirective", "variableDeclaration", "functionDeclaration", "body",
                "expression", "expressionRest", "primaryExpression", "primitive", "grouped",
                "selectorExpression", "implicitLambdaExpression", "inlineLambda", "functionParameters",
                "literal", "array", "object", "keyValue", "functionCall"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'('", "')'", "'filter'", "'map'", "'sizeOf'", "'upper'", "'lower'",
                "'%var'", "'%function'", "'%input'", "'%namespace'", "'%output'", "'%dw'",
                "'='", "'->'", null, null, null, null, "'..'", "'++'", null, null, null,
                null, null, null, null, "'.'", "':'", "','", "'{'", "'}'", "'['", "']'",
                "'---'", null, null, null, "'*'", "'@'", "'?'", null, "'$$'", "'$'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, null, null, null, null, null, null, null, "VAR", "FUNCTION", "INPUT",
                "NAMESPACE", "OUTPUT", "DW", "ASSIGN", "ARROW", "BOOLEAN", "OPERATOR_LOGICAL",
                "OPERATOR_COMPARISON", "OPERATOR_MATH", "OPERATOR_RANGE", "OPERATOR_CHAIN",
                "IDENTIFIER", "URL", "MEDIA_TYPE", "NUMBER", "STRING", "DATE", "REGEX",
                "DOT", "COLON", "COMMA", "LCURLY", "RCURLY", "LSQUARE", "RSQUARE", "SEPARATOR",
                "WS", "NEWLINE", "COMMENT", "STAR", "AT", "QUESTION", "BUILTIN_FUNCTION",
                "INDEX_IDENTIFIER", "VALUE_IDENTIFIER"
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
    public String getGrammarFileName() {
        return "DataWeave.g4";
    }

    @Override
    public String[] getRuleNames() {
        return ruleNames;
    }

    @Override
    public String getSerializedATN() {
        return _serializedATN;
    }

    @Override
    public ATN getATN() {
        return _ATN;
    }

    public DataWeaveParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ScriptContext extends ParserRuleContext {
        public TerminalNode SEPARATOR() {
            return getToken(DataWeaveParser.SEPARATOR, 0);
        }

        public TerminalNode EOF() {
            return getToken(DataWeaveParser.EOF, 0);
        }

        public HeaderContext header() {
            return getRuleContext(HeaderContext.class, 0);
        }

        public BodyContext body() {
            return getRuleContext(BodyContext.class, 0);
        }

        public List<TerminalNode> NEWLINE() {
            return getTokens(DataWeaveParser.NEWLINE);
        }

        public TerminalNode NEWLINE(int i) {
            return getToken(DataWeaveParser.NEWLINE, i);
        }

        public ScriptContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_script;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterScript(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitScript(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor) return ((DataWeaveVisitor<? extends T>) visitor).visitScript(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ScriptContext script() throws RecognitionException {
        ScriptContext _localctx = new ScriptContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_script);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(49);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 16128L) != 0)) {
                    {
                        setState(48);
                        header();
                    }
                }

                setState(51);
                match(SEPARATOR);
                setState(53);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 21982413026L) != 0)) {
                    {
                        setState(52);
                        body();
                    }
                }

                setState(58);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == NEWLINE) {
                    {
                        {
                            setState(55);
                            match(NEWLINE);
                        }
                    }
                    setState(60);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
                setState(61);
                match(EOF);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
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
            return getRuleContext(DirectiveContext.class, i);
        }

        public List<TerminalNode> NEWLINE() {
            return getTokens(DataWeaveParser.NEWLINE);
        }

        public TerminalNode NEWLINE(int i) {
            return getToken(DataWeaveParser.NEWLINE, i);
        }

        public List<TerminalNode> WS() {
            return getTokens(DataWeaveParser.WS);
        }

        public TerminalNode WS(int i) {
            return getToken(DataWeaveParser.WS, i);
        }

        public HeaderContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_header;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterHeader(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitHeader(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor) return ((DataWeaveVisitor<? extends T>) visitor).visitHeader(this);
            else return visitor.visitChildren(this);
        }
    }

    public final HeaderContext header() throws RecognitionException {
        HeaderContext _localctx = new HeaderContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_header);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(70);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(63);
                            directive();
                            setState(67);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                            while (_la == WS || _la == NEWLINE) {
                                {
                                    {
                                        setState(64);
                                        _la = _input.LA(1);
                                        if (!(_la == WS || _la == NEWLINE)) {
                                            _errHandler.recoverInline(this);
                                        } else {
                                            if (_input.LA(1) == Token.EOF) matchedEOF = true;
                                            _errHandler.reportMatch(this);
                                            consume();
                                        }
                                    }
                                }
                                setState(69);
                                _errHandler.sync(this);
                                _la = _input.LA(1);
                            }
                        }
                    }
                    setState(72);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 16128L) != 0));
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class DirectiveContext extends ParserRuleContext {
        public DwVersionContext dwVersion() {
            return getRuleContext(DwVersionContext.class, 0);
        }

        public OutputDirectiveContext outputDirective() {
            return getRuleContext(OutputDirectiveContext.class, 0);
        }

        public InputDirectiveContext inputDirective() {
            return getRuleContext(InputDirectiveContext.class, 0);
        }

        public NamespaceDirectiveContext namespaceDirective() {
            return getRuleContext(NamespaceDirectiveContext.class, 0);
        }

        public VariableDeclarationContext variableDeclaration() {
            return getRuleContext(VariableDeclarationContext.class, 0);
        }

        public FunctionDeclarationContext functionDeclaration() {
            return getRuleContext(FunctionDeclarationContext.class, 0);
        }

        public DirectiveContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_directive;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterDirective(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitDirective(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitDirective(this);
            else return visitor.visitChildren(this);
        }
    }

    public final DirectiveContext directive() throws RecognitionException {
        DirectiveContext _localctx = new DirectiveContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_directive);
        try {
            setState(80);
            _errHandler.sync(this);
            switch (_input.LA(1)) {
                case DW:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(74);
                    dwVersion();
                }
                break;
                case OUTPUT:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(75);
                    outputDirective();
                }
                break;
                case INPUT:
                    enterOuterAlt(_localctx, 3);
                {
                    setState(76);
                    inputDirective();
                }
                break;
                case NAMESPACE:
                    enterOuterAlt(_localctx, 4);
                {
                    setState(77);
                    namespaceDirective();
                }
                break;
                case VAR:
                    enterOuterAlt(_localctx, 5);
                {
                    setState(78);
                    variableDeclaration();
                }
                break;
                case FUNCTION:
                    enterOuterAlt(_localctx, 6);
                {
                    setState(79);
                    functionDeclaration();
                }
                break;
                default:
                    throw new NoViableAltException(this);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class DwVersionContext extends ParserRuleContext {
        public TerminalNode DW() {
            return getToken(DataWeaveParser.DW, 0);
        }

        public TerminalNode NUMBER() {
            return getToken(DataWeaveParser.NUMBER, 0);
        }

        public DwVersionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_dwVersion;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterDwVersion(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitDwVersion(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitDwVersion(this);
            else return visitor.visitChildren(this);
        }
    }

    public final DwVersionContext dwVersion() throws RecognitionException {
        DwVersionContext _localctx = new DwVersionContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_dwVersion);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(82);
                match(DW);
                setState(83);
                match(NUMBER);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class OutputDirectiveContext extends ParserRuleContext {
        public TerminalNode OUTPUT() {
            return getToken(DataWeaveParser.OUTPUT, 0);
        }

        public TerminalNode MEDIA_TYPE() {
            return getToken(DataWeaveParser.MEDIA_TYPE, 0);
        }

        public OutputDirectiveContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_outputDirective;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterOutputDirective(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitOutputDirective(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitOutputDirective(this);
            else return visitor.visitChildren(this);
        }
    }

    public final OutputDirectiveContext outputDirective() throws RecognitionException {
        OutputDirectiveContext _localctx = new OutputDirectiveContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_outputDirective);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(85);
                match(OUTPUT);
                setState(86);
                match(MEDIA_TYPE);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class InputDirectiveContext extends ParserRuleContext {
        public TerminalNode INPUT() {
            return getToken(DataWeaveParser.INPUT, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(DataWeaveParser.IDENTIFIER, 0);
        }

        public TerminalNode MEDIA_TYPE() {
            return getToken(DataWeaveParser.MEDIA_TYPE, 0);
        }

        public InputDirectiveContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_inputDirective;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterInputDirective(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitInputDirective(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitInputDirective(this);
            else return visitor.visitChildren(this);
        }
    }

    public final InputDirectiveContext inputDirective() throws RecognitionException {
        InputDirectiveContext _localctx = new InputDirectiveContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_inputDirective);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(88);
                match(INPUT);
                setState(89);
                match(IDENTIFIER);
                setState(90);
                match(MEDIA_TYPE);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class NamespaceDirectiveContext extends ParserRuleContext {
        public TerminalNode NAMESPACE() {
            return getToken(DataWeaveParser.NAMESPACE, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(DataWeaveParser.IDENTIFIER, 0);
        }

        public TerminalNode URL() {
            return getToken(DataWeaveParser.URL, 0);
        }

        public NamespaceDirectiveContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_namespaceDirective;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterNamespaceDirective(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitNamespaceDirective(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitNamespaceDirective(this);
            else return visitor.visitChildren(this);
        }
    }

    public final NamespaceDirectiveContext namespaceDirective() throws RecognitionException {
        NamespaceDirectiveContext _localctx = new NamespaceDirectiveContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_namespaceDirective);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(92);
                match(NAMESPACE);
                setState(93);
                match(IDENTIFIER);
                setState(94);
                match(URL);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class VariableDeclarationContext extends ParserRuleContext {
        public TerminalNode VAR() {
            return getToken(DataWeaveParser.VAR, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(DataWeaveParser.IDENTIFIER, 0);
        }

        public TerminalNode ASSIGN() {
            return getToken(DataWeaveParser.ASSIGN, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public VariableDeclarationContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_variableDeclaration;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterVariableDeclaration(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitVariableDeclaration(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitVariableDeclaration(this);
            else return visitor.visitChildren(this);
        }
    }

    public final VariableDeclarationContext variableDeclaration() throws RecognitionException {
        VariableDeclarationContext _localctx = new VariableDeclarationContext(_ctx, getState());
        enterRule(_localctx, 14, RULE_variableDeclaration);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(96);
                match(VAR);
                setState(97);
                match(IDENTIFIER);
                setState(98);
                match(ASSIGN);
                setState(99);
                expression();
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class FunctionDeclarationContext extends ParserRuleContext {
        public TerminalNode FUNCTION() {
            return getToken(DataWeaveParser.FUNCTION, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(DataWeaveParser.IDENTIFIER, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public FunctionParametersContext functionParameters() {
            return getRuleContext(FunctionParametersContext.class, 0);
        }

        public FunctionDeclarationContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_functionDeclaration;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterFunctionDeclaration(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitFunctionDeclaration(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitFunctionDeclaration(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FunctionDeclarationContext functionDeclaration() throws RecognitionException {
        FunctionDeclarationContext _localctx = new FunctionDeclarationContext(_ctx, getState());
        enterRule(_localctx, 16, RULE_functionDeclaration);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(101);
                match(FUNCTION);
                setState(102);
                match(IDENTIFIER);
                setState(103);
                match(T__0);
                setState(105);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == IDENTIFIER) {
                    {
                        setState(104);
                        functionParameters();
                    }
                }

                setState(107);
                match(T__1);
                setState(108);
                expression();
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class BodyContext extends ParserRuleContext {
        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public List<TerminalNode> NEWLINE() {
            return getTokens(DataWeaveParser.NEWLINE);
        }

        public TerminalNode NEWLINE(int i) {
            return getToken(DataWeaveParser.NEWLINE, i);
        }

        public BodyContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_body;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterBody(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitBody(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor) return ((DataWeaveVisitor<? extends T>) visitor).visitBody(this);
            else return visitor.visitChildren(this);
        }
    }

    public final BodyContext body() throws RecognitionException {
        BodyContext _localctx = new BodyContext(_ctx, getState());
        enterRule(_localctx, 18, RULE_body);
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                setState(110);
                expression();
                setState(114);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 7, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        {
                            {
                                setState(111);
                                match(NEWLINE);
                            }
                        }
                    }
                    setState(116);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 7, _ctx);
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ExpressionContext extends ParserRuleContext {
        public ExpressionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_expression;
        }

        public ExpressionContext() {
        }

        public void copyFrom(ExpressionContext ctx) {
            super.copyFrom(ctx);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class PrimaryExpressionWrapperContext extends ExpressionContext {
        public PrimaryExpressionContext primaryExpression() {
            return getRuleContext(PrimaryExpressionContext.class, 0);
        }

        public SelectorExpressionContext selectorExpression() {
            return getRuleContext(SelectorExpressionContext.class, 0);
        }

        public ExpressionRestContext expressionRest() {
            return getRuleContext(ExpressionRestContext.class, 0);
        }

        public PrimaryExpressionWrapperContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener)
                ((DataWeaveListener) listener).enterPrimaryExpressionWrapper(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener)
                ((DataWeaveListener) listener).exitPrimaryExpressionWrapper(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitPrimaryExpressionWrapper(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ExpressionContext expression() throws RecognitionException {
        ExpressionContext _localctx = new ExpressionContext(_ctx, getState());
        enterRule(_localctx, 20, RULE_expression);
        try {
            _localctx = new PrimaryExpressionWrapperContext(_localctx);
            enterOuterAlt(_localctx, 1);
            {
                setState(117);
                primaryExpression();
                setState(119);
                _errHandler.sync(this);
                switch (getInterpreter().adaptivePredict(_input, 8, _ctx)) {
                    case 1: {
                        setState(118);
                        selectorExpression();
                    }
                    break;
                }
                setState(122);
                _errHandler.sync(this);
                switch (getInterpreter().adaptivePredict(_input, 9, _ctx)) {
                    case 1: {
                        setState(121);
                        expressionRest();
                    }
                    break;
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ExpressionRestContext extends ParserRuleContext {
        public ExpressionRestContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_expressionRest;
        }

        public ExpressionRestContext() {
        }

        public void copyFrom(ExpressionRestContext ctx) {
            super.copyFrom(ctx);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ChainExpressionContext extends ExpressionRestContext {
        public TerminalNode OPERATOR_CHAIN() {
            return getToken(DataWeaveParser.OPERATOR_CHAIN, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public ChainExpressionContext(ExpressionRestContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterChainExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitChainExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitChainExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class MapExpressionContext extends ExpressionRestContext {
        public ImplicitLambdaExpressionContext implicitLambdaExpression() {
            return getRuleContext(ImplicitLambdaExpressionContext.class, 0);
        }

        public MapExpressionContext(ExpressionRestContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterMapExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitMapExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitMapExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class FilterExpressionContext extends ExpressionRestContext {
        public ImplicitLambdaExpressionContext implicitLambdaExpression() {
            return getRuleContext(ImplicitLambdaExpressionContext.class, 0);
        }

        public FilterExpressionContext(ExpressionRestContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterFilterExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitFilterExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitFilterExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class RangeExpressionContext extends ExpressionRestContext {
        public TerminalNode OPERATOR_RANGE() {
            return getToken(DataWeaveParser.OPERATOR_RANGE, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public RangeExpressionContext(ExpressionRestContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterRangeExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitRangeExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitRangeExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class MathExpressionContext extends ExpressionRestContext {
        public TerminalNode OPERATOR_MATH() {
            return getToken(DataWeaveParser.OPERATOR_MATH, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public MathExpressionContext(ExpressionRestContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterMathExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitMathExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitMathExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ComparisonExpressionContext extends ExpressionRestContext {
        public TerminalNode OPERATOR_COMPARISON() {
            return getToken(DataWeaveParser.OPERATOR_COMPARISON, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public ComparisonExpressionContext(ExpressionRestContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterComparisonExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitComparisonExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitComparisonExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class LogicalExpressionContext extends ExpressionRestContext {
        public TerminalNode OPERATOR_LOGICAL() {
            return getToken(DataWeaveParser.OPERATOR_LOGICAL, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public LogicalExpressionContext(ExpressionRestContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterLogicalExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitLogicalExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitLogicalExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ExpressionRestContext expressionRest() throws RecognitionException {
        ExpressionRestContext _localctx = new ExpressionRestContext(_ctx, getState());
        enterRule(_localctx, 22, RULE_expressionRest);
        try {
            setState(138);
            _errHandler.sync(this);
            switch (_input.LA(1)) {
                case OPERATOR_CHAIN:
                    _localctx = new ChainExpressionContext(_localctx);
                    enterOuterAlt(_localctx, 1);
                {
                    setState(124);
                    match(OPERATOR_CHAIN);
                    setState(125);
                    expression();
                }
                break;
                case OPERATOR_RANGE:
                    _localctx = new RangeExpressionContext(_localctx);
                    enterOuterAlt(_localctx, 2);
                {
                    setState(126);
                    match(OPERATOR_RANGE);
                    setState(127);
                    expression();
                }
                break;
                case OPERATOR_MATH:
                    _localctx = new MathExpressionContext(_localctx);
                    enterOuterAlt(_localctx, 3);
                {
                    setState(128);
                    match(OPERATOR_MATH);
                    setState(129);
                    expression();
                }
                break;
                case OPERATOR_COMPARISON:
                    _localctx = new ComparisonExpressionContext(_localctx);
                    enterOuterAlt(_localctx, 4);
                {
                    setState(130);
                    match(OPERATOR_COMPARISON);
                    setState(131);
                    expression();
                }
                break;
                case OPERATOR_LOGICAL:
                    _localctx = new LogicalExpressionContext(_localctx);
                    enterOuterAlt(_localctx, 5);
                {
                    setState(132);
                    match(OPERATOR_LOGICAL);
                    setState(133);
                    expression();
                }
                break;
                case T__2:
                    _localctx = new FilterExpressionContext(_localctx);
                    enterOuterAlt(_localctx, 6);
                {
                    setState(134);
                    match(T__2);
                    setState(135);
                    implicitLambdaExpression();
                }
                break;
                case T__3:
                    _localctx = new MapExpressionContext(_localctx);
                    enterOuterAlt(_localctx, 7);
                {
                    setState(136);
                    match(T__3);
                    setState(137);
                    implicitLambdaExpression();
                }
                break;
                default:
                    throw new NoViableAltException(this);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class PrimaryExpressionContext extends ParserRuleContext {
        public PrimaryExpressionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_primaryExpression;
        }

        public PrimaryExpressionContext() {
        }

        public void copyFrom(PrimaryExpressionContext ctx) {
            super.copyFrom(ctx);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class PrimitiveExpressionContext extends PrimaryExpressionContext {
        public PrimitiveContext primitive() {
            return getRuleContext(PrimitiveContext.class, 0);
        }

        public PrimitiveExpressionContext(PrimaryExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterPrimitiveExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitPrimitiveExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitPrimitiveExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class LambdaExpressionContext extends PrimaryExpressionContext {
        public InlineLambdaContext inlineLambda() {
            return getRuleContext(InlineLambdaContext.class, 0);
        }

        public LambdaExpressionContext(PrimaryExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterLambdaExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitLambdaExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitLambdaExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class SizeOfExpressionContext extends PrimaryExpressionContext {
        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public SizeOfExpressionContext(PrimaryExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterSizeOfExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitSizeOfExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitSizeOfExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class UpperExpressionContext extends PrimaryExpressionContext {
        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public UpperExpressionContext(PrimaryExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterUpperExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitUpperExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitUpperExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class LowerExpressionContext extends PrimaryExpressionContext {
        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public LowerExpressionContext(PrimaryExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterLowerExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitLowerExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitLowerExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class FunctionCallExpressionContext extends PrimaryExpressionContext {
        public FunctionCallContext functionCall() {
            return getRuleContext(FunctionCallContext.class, 0);
        }

        public FunctionCallExpressionContext(PrimaryExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterFunctionCallExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitFunctionCallExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitFunctionCallExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class GroupedExpressionContext extends PrimaryExpressionContext {
        public GroupedContext grouped() {
            return getRuleContext(GroupedContext.class, 0);
        }

        public GroupedExpressionContext(PrimaryExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterGroupedExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitGroupedExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitGroupedExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    public final PrimaryExpressionContext primaryExpression() throws RecognitionException {
        PrimaryExpressionContext _localctx = new PrimaryExpressionContext(_ctx, getState());
        enterRule(_localctx, 24, RULE_primaryExpression);
        try {
            setState(168);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 14, _ctx)) {
                case 1:
                    _localctx = new GroupedExpressionContext(_localctx);
                    enterOuterAlt(_localctx, 1);
                {
                    setState(140);
                    grouped();
                }
                break;
                case 2:
                    _localctx = new PrimitiveExpressionContext(_localctx);
                    enterOuterAlt(_localctx, 2);
                {
                    setState(141);
                    primitive();
                }
                break;
                case 3:
                    _localctx = new FunctionCallExpressionContext(_localctx);
                    enterOuterAlt(_localctx, 3);
                {
                    setState(142);
                    functionCall();
                }
                break;
                case 4:
                    _localctx = new SizeOfExpressionContext(_localctx);
                    enterOuterAlt(_localctx, 4);
                {
                    setState(143);
                    match(T__4);
                    setState(149);
                    _errHandler.sync(this);
                    switch (getInterpreter().adaptivePredict(_input, 11, _ctx)) {
                        case 1: {
                            setState(144);
                            match(T__0);
                            setState(145);
                            expression();
                            setState(146);
                            match(T__1);
                        }
                        break;
                        case 2: {
                            setState(148);
                            expression();
                        }
                        break;
                    }
                }
                break;
                case 5:
                    _localctx = new UpperExpressionContext(_localctx);
                    enterOuterAlt(_localctx, 5);
                {
                    setState(151);
                    match(T__5);
                    setState(157);
                    _errHandler.sync(this);
                    switch (getInterpreter().adaptivePredict(_input, 12, _ctx)) {
                        case 1: {
                            setState(152);
                            match(T__0);
                            setState(153);
                            expression();
                            setState(154);
                            match(T__1);
                        }
                        break;
                        case 2: {
                            setState(156);
                            expression();
                        }
                        break;
                    }
                }
                break;
                case 6:
                    _localctx = new LowerExpressionContext(_localctx);
                    enterOuterAlt(_localctx, 6);
                {
                    setState(159);
                    match(T__6);
                    setState(165);
                    _errHandler.sync(this);
                    switch (getInterpreter().adaptivePredict(_input, 13, _ctx)) {
                        case 1: {
                            setState(160);
                            match(T__0);
                            setState(161);
                            expression();
                            setState(162);
                            match(T__1);
                        }
                        break;
                        case 2: {
                            setState(164);
                            expression();
                        }
                        break;
                    }
                }
                break;
                case 7:
                    _localctx = new LambdaExpressionContext(_localctx);
                    enterOuterAlt(_localctx, 7);
                {
                    setState(167);
                    inlineLambda();
                }
                break;
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class PrimitiveContext extends ParserRuleContext {
        public PrimitiveContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_primitive;
        }

        public PrimitiveContext() {
        }

        public void copyFrom(PrimitiveContext ctx) {
            super.copyFrom(ctx);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ObjectExpressionContext extends PrimitiveContext {
        public ObjectContext object() {
            return getRuleContext(ObjectContext.class, 0);
        }

        public ObjectExpressionContext(PrimitiveContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterObjectExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitObjectExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitObjectExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ArrayExpressionContext extends PrimitiveContext {
        public ArrayContext array() {
            return getRuleContext(ArrayContext.class, 0);
        }

        public ArrayExpressionContext(PrimitiveContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterArrayExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitArrayExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitArrayExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class IdentifierExpressionContext extends PrimitiveContext {
        public TerminalNode IDENTIFIER() {
            return getToken(DataWeaveParser.IDENTIFIER, 0);
        }

        public IdentifierExpressionContext(PrimitiveContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterIdentifierExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitIdentifierExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitIdentifierExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class LiteralExpressionContext extends PrimitiveContext {
        public LiteralContext literal() {
            return getRuleContext(LiteralContext.class, 0);
        }

        public LiteralExpressionContext(PrimitiveContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterLiteralExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitLiteralExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitLiteralExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    public final PrimitiveContext primitive() throws RecognitionException {
        PrimitiveContext _localctx = new PrimitiveContext(_ctx, getState());
        enterRule(_localctx, 26, RULE_primitive);
        try {
            setState(174);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 15, _ctx)) {
                case 1:
                    _localctx = new LiteralExpressionContext(_localctx);
                    enterOuterAlt(_localctx, 1);
                {
                    setState(170);
                    literal();
                }
                break;
                case 2:
                    _localctx = new ArrayExpressionContext(_localctx);
                    enterOuterAlt(_localctx, 2);
                {
                    setState(171);
                    array();
                }
                break;
                case 3:
                    _localctx = new ObjectExpressionContext(_localctx);
                    enterOuterAlt(_localctx, 3);
                {
                    setState(172);
                    object();
                }
                break;
                case 4:
                    _localctx = new IdentifierExpressionContext(_localctx);
                    enterOuterAlt(_localctx, 4);
                {
                    setState(173);
                    match(IDENTIFIER);
                }
                break;
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class GroupedContext extends ParserRuleContext {
        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public GroupedContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_grouped;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterGrouped(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitGrouped(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitGrouped(this);
            else return visitor.visitChildren(this);
        }
    }

    public final GroupedContext grouped() throws RecognitionException {
        GroupedContext _localctx = new GroupedContext(_ctx, getState());
        enterRule(_localctx, 28, RULE_grouped);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(176);
                match(T__0);
                setState(177);
                expression();
                setState(178);
                match(T__1);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class SelectorExpressionContext extends ParserRuleContext {
        public SelectorExpressionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_selectorExpression;
        }

        public SelectorExpressionContext() {
        }

        public void copyFrom(SelectorExpressionContext ctx) {
            super.copyFrom(ctx);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ExistenceQuerySelectorContext extends SelectorExpressionContext {
        public TerminalNode QUESTION() {
            return getToken(DataWeaveParser.QUESTION, 0);
        }

        public ExistenceQuerySelectorContext(SelectorExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterExistenceQuerySelector(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitExistenceQuerySelector(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitExistenceQuerySelector(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class SingleValueSelectorContext extends SelectorExpressionContext {
        public TerminalNode DOT() {
            return getToken(DataWeaveParser.DOT, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(DataWeaveParser.IDENTIFIER, 0);
        }

        public SingleValueSelectorContext(SelectorExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterSingleValueSelector(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitSingleValueSelector(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitSingleValueSelector(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class IndexedSelectorContext extends SelectorExpressionContext {
        public TerminalNode LSQUARE() {
            return getToken(DataWeaveParser.LSQUARE, 0);
        }

        public TerminalNode NUMBER() {
            return getToken(DataWeaveParser.NUMBER, 0);
        }

        public TerminalNode RSQUARE() {
            return getToken(DataWeaveParser.RSQUARE, 0);
        }

        public IndexedSelectorContext(SelectorExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterIndexedSelector(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitIndexedSelector(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitIndexedSelector(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class MultiValueSelectorContext extends SelectorExpressionContext {
        public TerminalNode DOT() {
            return getToken(DataWeaveParser.DOT, 0);
        }

        public TerminalNode STAR() {
            return getToken(DataWeaveParser.STAR, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(DataWeaveParser.IDENTIFIER, 0);
        }

        public MultiValueSelectorContext(SelectorExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterMultiValueSelector(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitMultiValueSelector(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitMultiValueSelector(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class AttributeSelectorContext extends SelectorExpressionContext {
        public TerminalNode DOT() {
            return getToken(DataWeaveParser.DOT, 0);
        }

        public TerminalNode AT() {
            return getToken(DataWeaveParser.AT, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(DataWeaveParser.IDENTIFIER, 0);
        }

        public AttributeSelectorContext(SelectorExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterAttributeSelector(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitAttributeSelector(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitAttributeSelector(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class DescendantsSelectorContext extends SelectorExpressionContext {
        public TerminalNode OPERATOR_RANGE() {
            return getToken(DataWeaveParser.OPERATOR_RANGE, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(DataWeaveParser.IDENTIFIER, 0);
        }

        public DescendantsSelectorContext(SelectorExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterDescendantsSelector(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitDescendantsSelector(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitDescendantsSelector(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SelectorExpressionContext selectorExpression() throws RecognitionException {
        SelectorExpressionContext _localctx = new SelectorExpressionContext(_ctx, getState());
        enterRule(_localctx, 30, RULE_selectorExpression);
        try {
            setState(194);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 16, _ctx)) {
                case 1:
                    _localctx = new SingleValueSelectorContext(_localctx);
                    enterOuterAlt(_localctx, 1);
                {
                    {
                        setState(180);
                        match(DOT);
                        setState(181);
                        match(IDENTIFIER);
                    }
                }
                break;
                case 2:
                    _localctx = new MultiValueSelectorContext(_localctx);
                    enterOuterAlt(_localctx, 2);
                {
                    {
                        setState(182);
                        match(DOT);
                        setState(183);
                        match(STAR);
                        setState(184);
                        match(IDENTIFIER);
                    }
                }
                break;
                case 3:
                    _localctx = new DescendantsSelectorContext(_localctx);
                    enterOuterAlt(_localctx, 3);
                {
                    {
                        setState(185);
                        match(OPERATOR_RANGE);
                        setState(186);
                        match(IDENTIFIER);
                    }
                }
                break;
                case 4:
                    _localctx = new IndexedSelectorContext(_localctx);
                    enterOuterAlt(_localctx, 4);
                {
                    {
                        setState(187);
                        match(LSQUARE);
                        setState(188);
                        match(NUMBER);
                        setState(189);
                        match(RSQUARE);
                    }
                }
                break;
                case 5:
                    _localctx = new AttributeSelectorContext(_localctx);
                    enterOuterAlt(_localctx, 5);
                {
                    {
                        setState(190);
                        match(DOT);
                        setState(191);
                        match(AT);
                        setState(192);
                        match(IDENTIFIER);
                    }
                }
                break;
                case 6:
                    _localctx = new ExistenceQuerySelectorContext(_localctx);
                    enterOuterAlt(_localctx, 6);
                {
                    {
                        setState(193);
                        match(QUESTION);
                    }
                }
                break;
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ImplicitLambdaExpressionContext extends ParserRuleContext {
        public ImplicitLambdaExpressionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_implicitLambdaExpression;
        }

        public ImplicitLambdaExpressionContext() {
        }

        public void copyFrom(ImplicitLambdaExpressionContext ctx) {
            super.copyFrom(ctx);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class SingleParameterImplicitLambdaContext extends ImplicitLambdaExpressionContext {
        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public SingleParameterImplicitLambdaContext(ImplicitLambdaExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener)
                ((DataWeaveListener) listener).enterSingleParameterImplicitLambda(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener)
                ((DataWeaveListener) listener).exitSingleParameterImplicitLambda(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitSingleParameterImplicitLambda(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class MultiParameterImplicitLambdaContext extends ImplicitLambdaExpressionContext {
        public List<TerminalNode> IDENTIFIER() {
            return getTokens(DataWeaveParser.IDENTIFIER);
        }

        public TerminalNode IDENTIFIER(int i) {
            return getToken(DataWeaveParser.IDENTIFIER, i);
        }

        public TerminalNode ARROW() {
            return getToken(DataWeaveParser.ARROW, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public List<TerminalNode> COMMA() {
            return getTokens(DataWeaveParser.COMMA);
        }

        public TerminalNode COMMA(int i) {
            return getToken(DataWeaveParser.COMMA, i);
        }

        public MultiParameterImplicitLambdaContext(ImplicitLambdaExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener)
                ((DataWeaveListener) listener).enterMultiParameterImplicitLambda(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener)
                ((DataWeaveListener) listener).exitMultiParameterImplicitLambda(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitMultiParameterImplicitLambda(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ImplicitLambdaExpressionContext implicitLambdaExpression() throws RecognitionException {
        ImplicitLambdaExpressionContext _localctx = new ImplicitLambdaExpressionContext(_ctx, getState());
        enterRule(_localctx, 32, RULE_implicitLambdaExpression);
        int _la;
        try {
            setState(209);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 18, _ctx)) {
                case 1:
                    _localctx = new SingleParameterImplicitLambdaContext(_localctx);
                    enterOuterAlt(_localctx, 1);
                {
                    setState(196);
                    expression();
                }
                break;
                case 2:
                    _localctx = new MultiParameterImplicitLambdaContext(_localctx);
                    enterOuterAlt(_localctx, 2);
                {
                    setState(197);
                    match(T__0);
                    setState(198);
                    match(IDENTIFIER);
                    setState(203);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                    while (_la == COMMA) {
                        {
                            {
                                setState(199);
                                match(COMMA);
                                setState(200);
                                match(IDENTIFIER);
                            }
                        }
                        setState(205);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                    }
                    setState(206);
                    match(T__1);
                    setState(207);
                    match(ARROW);
                    setState(208);
                    expression();
                }
                break;
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class InlineLambdaContext extends ParserRuleContext {
        public FunctionParametersContext functionParameters() {
            return getRuleContext(FunctionParametersContext.class, 0);
        }

        public TerminalNode ARROW() {
            return getToken(DataWeaveParser.ARROW, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public InlineLambdaContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_inlineLambda;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterInlineLambda(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitInlineLambda(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitInlineLambda(this);
            else return visitor.visitChildren(this);
        }
    }

    public final InlineLambdaContext inlineLambda() throws RecognitionException {
        InlineLambdaContext _localctx = new InlineLambdaContext(_ctx, getState());
        enterRule(_localctx, 34, RULE_inlineLambda);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(211);
                match(T__0);
                setState(212);
                functionParameters();
                setState(213);
                match(T__1);
                setState(214);
                match(ARROW);
                setState(215);
                expression();
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class FunctionParametersContext extends ParserRuleContext {
        public List<TerminalNode> IDENTIFIER() {
            return getTokens(DataWeaveParser.IDENTIFIER);
        }

        public TerminalNode IDENTIFIER(int i) {
            return getToken(DataWeaveParser.IDENTIFIER, i);
        }

        public List<TerminalNode> COMMA() {
            return getTokens(DataWeaveParser.COMMA);
        }

        public TerminalNode COMMA(int i) {
            return getToken(DataWeaveParser.COMMA, i);
        }

        public FunctionParametersContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_functionParameters;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterFunctionParameters(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitFunctionParameters(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitFunctionParameters(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FunctionParametersContext functionParameters() throws RecognitionException {
        FunctionParametersContext _localctx = new FunctionParametersContext(_ctx, getState());
        enterRule(_localctx, 36, RULE_functionParameters);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(217);
                match(IDENTIFIER);
                setState(222);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == COMMA) {
                    {
                        {
                            setState(218);
                            match(COMMA);
                            setState(219);
                            match(IDENTIFIER);
                        }
                    }
                    setState(224);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class LiteralContext extends ParserRuleContext {
        public TerminalNode STRING() {
            return getToken(DataWeaveParser.STRING, 0);
        }

        public TerminalNode NUMBER() {
            return getToken(DataWeaveParser.NUMBER, 0);
        }

        public TerminalNode BOOLEAN() {
            return getToken(DataWeaveParser.BOOLEAN, 0);
        }

        public TerminalNode DATE() {
            return getToken(DataWeaveParser.DATE, 0);
        }

        public TerminalNode REGEX() {
            return getToken(DataWeaveParser.REGEX, 0);
        }

        public LiteralContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_literal;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterLiteral(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitLiteral(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitLiteral(this);
            else return visitor.visitChildren(this);
        }
    }

    public final LiteralContext literal() throws RecognitionException {
        LiteralContext _localctx = new LiteralContext(_ctx, getState());
        enterRule(_localctx, 38, RULE_literal);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(225);
                _la = _input.LA(1);
                if (!((((_la) & ~0x3f) == 0 && ((1L << _la) & 503382016L) != 0))) {
                    _errHandler.recoverInline(this);
                } else {
                    if (_input.LA(1) == Token.EOF) matchedEOF = true;
                    _errHandler.reportMatch(this);
                    consume();
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ArrayContext extends ParserRuleContext {
        public TerminalNode LSQUARE() {
            return getToken(DataWeaveParser.LSQUARE, 0);
        }

        public TerminalNode RSQUARE() {
            return getToken(DataWeaveParser.RSQUARE, 0);
        }

        public List<ExpressionContext> expression() {
            return getRuleContexts(ExpressionContext.class);
        }

        public ExpressionContext expression(int i) {
            return getRuleContext(ExpressionContext.class, i);
        }

        public List<TerminalNode> COMMA() {
            return getTokens(DataWeaveParser.COMMA);
        }

        public TerminalNode COMMA(int i) {
            return getToken(DataWeaveParser.COMMA, i);
        }

        public ArrayContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_array;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterArray(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitArray(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor) return ((DataWeaveVisitor<? extends T>) visitor).visitArray(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ArrayContext array() throws RecognitionException {
        ArrayContext _localctx = new ArrayContext(_ctx, getState());
        enterRule(_localctx, 40, RULE_array);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(227);
                match(LSQUARE);
                setState(236);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 21982413026L) != 0)) {
                    {
                        setState(228);
                        expression();
                        setState(233);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        while (_la == COMMA) {
                            {
                                {
                                    setState(229);
                                    match(COMMA);
                                    setState(230);
                                    expression();
                                }
                            }
                            setState(235);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        }
                    }
                }

                setState(238);
                match(RSQUARE);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ObjectContext extends ParserRuleContext {
        public ObjectContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_object;
        }

        public ObjectContext() {
        }

        public void copyFrom(ObjectContext ctx) {
            super.copyFrom(ctx);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class MultiKeyValueObjectContext extends ObjectContext {
        public TerminalNode LCURLY() {
            return getToken(DataWeaveParser.LCURLY, 0);
        }

        public List<KeyValueContext> keyValue() {
            return getRuleContexts(KeyValueContext.class);
        }

        public KeyValueContext keyValue(int i) {
            return getRuleContext(KeyValueContext.class, i);
        }

        public TerminalNode RCURLY() {
            return getToken(DataWeaveParser.RCURLY, 0);
        }

        public List<TerminalNode> COMMA() {
            return getTokens(DataWeaveParser.COMMA);
        }

        public TerminalNode COMMA(int i) {
            return getToken(DataWeaveParser.COMMA, i);
        }

        public MultiKeyValueObjectContext(ObjectContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterMultiKeyValueObject(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitMultiKeyValueObject(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitMultiKeyValueObject(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class SingleKeyValueObjectContext extends ObjectContext {
        public KeyValueContext keyValue() {
            return getRuleContext(KeyValueContext.class, 0);
        }

        public SingleKeyValueObjectContext(ObjectContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterSingleKeyValueObject(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitSingleKeyValueObject(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitSingleKeyValueObject(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ObjectContext object() throws RecognitionException {
        ObjectContext _localctx = new ObjectContext(_ctx, getState());
        enterRule(_localctx, 42, RULE_object);
        int _la;
        try {
            setState(252);
            _errHandler.sync(this);
            switch (_input.LA(1)) {
                case LCURLY:
                    _localctx = new MultiKeyValueObjectContext(_localctx);
                    enterOuterAlt(_localctx, 1);
                {
                    setState(240);
                    match(LCURLY);
                    setState(241);
                    keyValue();
                    setState(246);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                    while (_la == COMMA) {
                        {
                            {
                                setState(242);
                                match(COMMA);
                                setState(243);
                                keyValue();
                            }
                        }
                        setState(248);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                    }
                    setState(249);
                    match(RCURLY);
                }
                break;
                case IDENTIFIER:
                    _localctx = new SingleKeyValueObjectContext(_localctx);
                    enterOuterAlt(_localctx, 2);
                {
                    setState(251);
                    keyValue();
                }
                break;
                default:
                    throw new NoViableAltException(this);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class KeyValueContext extends ParserRuleContext {
        public TerminalNode IDENTIFIER() {
            return getToken(DataWeaveParser.IDENTIFIER, 0);
        }

        public TerminalNode COLON() {
            return getToken(DataWeaveParser.COLON, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public KeyValueContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_keyValue;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterKeyValue(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitKeyValue(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitKeyValue(this);
            else return visitor.visitChildren(this);
        }
    }

    public final KeyValueContext keyValue() throws RecognitionException {
        KeyValueContext _localctx = new KeyValueContext(_ctx, getState());
        enterRule(_localctx, 44, RULE_keyValue);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(254);
                match(IDENTIFIER);
                setState(255);
                match(COLON);
                setState(256);
                expression();
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class FunctionCallContext extends ParserRuleContext {
        public TerminalNode IDENTIFIER() {
            return getToken(DataWeaveParser.IDENTIFIER, 0);
        }

        public List<ExpressionContext> expression() {
            return getRuleContexts(ExpressionContext.class);
        }

        public ExpressionContext expression(int i) {
            return getRuleContext(ExpressionContext.class, i);
        }

        public List<TerminalNode> COMMA() {
            return getTokens(DataWeaveParser.COMMA);
        }

        public TerminalNode COMMA(int i) {
            return getToken(DataWeaveParser.COMMA, i);
        }

        public FunctionCallContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_functionCall;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterFunctionCall(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitFunctionCall(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitFunctionCall(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FunctionCallContext functionCall() throws RecognitionException {
        FunctionCallContext _localctx = new FunctionCallContext(_ctx, getState());
        enterRule(_localctx, 46, RULE_functionCall);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(258);
                match(IDENTIFIER);
                setState(259);
                match(T__0);
                setState(268);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 21982413026L) != 0)) {
                    {
                        setState(260);
                        expression();
                        setState(265);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        while (_la == COMMA) {
                            {
                                {
                                    setState(261);
                                    match(COMMA);
                                    setState(262);
                                    expression();
                                }
                            }
                            setState(267);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        }
                    }
                }

                setState(270);
                match(T__1);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static final String _serializedATN =
            "\u0004\u0001-\u0111\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002" +
                    "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002" +
                    "\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002" +
                    "\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002" +
                    "\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f" +
                    "\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012" +
                    "\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015" +
                    "\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0001\u0000\u0003\u0000" +
                    "2\b\u0000\u0001\u0000\u0001\u0000\u0003\u00006\b\u0000\u0001\u0000\u0005" +
                    "\u00009\b\u0000\n\u0000\f\u0000<\t\u0000\u0001\u0000\u0001\u0000\u0001" +
                    "\u0001\u0001\u0001\u0005\u0001B\b\u0001\n\u0001\f\u0001E\t\u0001\u0004" +
                    "\u0001G\b\u0001\u000b\u0001\f\u0001H\u0001\u0002\u0001\u0002\u0001\u0002" +
                    "\u0001\u0002\u0001\u0002\u0001\u0002\u0003\u0002Q\b\u0002\u0001\u0003" +
                    "\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005" +
                    "\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006" +
                    "\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007" +
                    "\u0001\b\u0001\b\u0001\b\u0001\b\u0003\bj\b\b\u0001\b\u0001\b\u0001\b" +
                    "\u0001\t\u0001\t\u0005\tq\b\t\n\t\f\tt\t\t\u0001\n\u0001\n\u0003\nx\b" +
                    "\n\u0001\n\u0003\n{\b\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b" +
                    "\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b" +
                    "\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0003\u000b\u008b\b\u000b" +
                    "\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001" +
                    "\f\u0003\f\u0096\b\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0003" +
                    "\f\u009e\b\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0003\f\u00a6" +
                    "\b\f\u0001\f\u0003\f\u00a9\b\f\u0001\r\u0001\r\u0001\r\u0001\r\u0003\r" +
                    "\u00af\b\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000f" +
                    "\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f" +
                    "\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f" +
                    "\u0001\u000f\u0003\u000f\u00c3\b\u000f\u0001\u0010\u0001\u0010\u0001\u0010" +
                    "\u0001\u0010\u0001\u0010\u0005\u0010\u00ca\b\u0010\n\u0010\f\u0010\u00cd" +
                    "\t\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0003\u0010\u00d2\b\u0010" +
                    "\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011" +
                    "\u0001\u0012\u0001\u0012\u0001\u0012\u0005\u0012\u00dd\b\u0012\n\u0012" +
                    "\f\u0012\u00e0\t\u0012\u0001\u0013\u0001\u0013\u0001\u0014\u0001\u0014" +
                    "\u0001\u0014\u0001\u0014\u0005\u0014\u00e8\b\u0014\n\u0014\f\u0014\u00eb" +
                    "\t\u0014\u0003\u0014\u00ed\b\u0014\u0001\u0014\u0001\u0014\u0001\u0015" +
                    "\u0001\u0015\u0001\u0015\u0001\u0015\u0005\u0015\u00f5\b\u0015\n\u0015" +
                    "\f\u0015\u00f8\t\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0003\u0015" +
                    "\u00fd\b\u0015\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0017" +
                    "\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0005\u0017\u0108\b\u0017" +
                    "\n\u0017\f\u0017\u010b\t\u0017\u0003\u0017\u010d\b\u0017\u0001\u0017\u0001" +
                    "\u0017\u0001\u0017\u0000\u0000\u0018\u0000\u0002\u0004\u0006\b\n\f\u000e" +
                    "\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,.\u0000\u0002" +
                    "\u0001\u0000%&\u0002\u0000\u0010\u0010\u0019\u001c\u0126\u00001\u0001" +
                    "\u0000\u0000\u0000\u0002F\u0001\u0000\u0000\u0000\u0004P\u0001\u0000\u0000" +
                    "\u0000\u0006R\u0001\u0000\u0000\u0000\bU\u0001\u0000\u0000\u0000\nX\u0001" +
                    "\u0000\u0000\u0000\f\\\u0001\u0000\u0000\u0000\u000e`\u0001\u0000\u0000" +
                    "\u0000\u0010e\u0001\u0000\u0000\u0000\u0012n\u0001\u0000\u0000\u0000\u0014" +
                    "u\u0001\u0000\u0000\u0000\u0016\u008a\u0001\u0000\u0000\u0000\u0018\u00a8" +
                    "\u0001\u0000\u0000\u0000\u001a\u00ae\u0001\u0000\u0000\u0000\u001c\u00b0" +
                    "\u0001\u0000\u0000\u0000\u001e\u00c2\u0001\u0000\u0000\u0000 \u00d1\u0001" +
                    "\u0000\u0000\u0000\"\u00d3\u0001\u0000\u0000\u0000$\u00d9\u0001\u0000" +
                    "\u0000\u0000&\u00e1\u0001\u0000\u0000\u0000(\u00e3\u0001\u0000\u0000\u0000" +
                    "*\u00fc\u0001\u0000\u0000\u0000,\u00fe\u0001\u0000\u0000\u0000.\u0102" +
                    "\u0001\u0000\u0000\u000002\u0003\u0002\u0001\u000010\u0001\u0000\u0000" +
                    "\u000012\u0001\u0000\u0000\u000023\u0001\u0000\u0000\u000035\u0005$\u0000" +
                    "\u000046\u0003\u0012\t\u000054\u0001\u0000\u0000\u000056\u0001\u0000\u0000" +
                    "\u00006:\u0001\u0000\u0000\u000079\u0005&\u0000\u000087\u0001\u0000\u0000" +
                    "\u00009<\u0001\u0000\u0000\u0000:8\u0001\u0000\u0000\u0000:;\u0001\u0000" +
                    "\u0000\u0000;=\u0001\u0000\u0000\u0000<:\u0001\u0000\u0000\u0000=>\u0005" +
                    "\u0000\u0000\u0001>\u0001\u0001\u0000\u0000\u0000?C\u0003\u0004\u0002" +
                    "\u0000@B\u0007\u0000\u0000\u0000A@\u0001\u0000\u0000\u0000BE\u0001\u0000" +
                    "\u0000\u0000CA\u0001\u0000\u0000\u0000CD\u0001\u0000\u0000\u0000DG\u0001" +
                    "\u0000\u0000\u0000EC\u0001\u0000\u0000\u0000F?\u0001\u0000\u0000\u0000" +
                    "GH\u0001\u0000\u0000\u0000HF\u0001\u0000\u0000\u0000HI\u0001\u0000\u0000" +
                    "\u0000I\u0003\u0001\u0000\u0000\u0000JQ\u0003\u0006\u0003\u0000KQ\u0003" +
                    "\b\u0004\u0000LQ\u0003\n\u0005\u0000MQ\u0003\f\u0006\u0000NQ\u0003\u000e" +
                    "\u0007\u0000OQ\u0003\u0010\b\u0000PJ\u0001\u0000\u0000\u0000PK\u0001\u0000" +
                    "\u0000\u0000PL\u0001\u0000\u0000\u0000PM\u0001\u0000\u0000\u0000PN\u0001" +
                    "\u0000\u0000\u0000PO\u0001\u0000\u0000\u0000Q\u0005\u0001\u0000\u0000" +
                    "\u0000RS\u0005\r\u0000\u0000ST\u0005\u0019\u0000\u0000T\u0007\u0001\u0000" +
                    "\u0000\u0000UV\u0005\f\u0000\u0000VW\u0005\u0018\u0000\u0000W\t\u0001" +
                    "\u0000\u0000\u0000XY\u0005\n\u0000\u0000YZ\u0005\u0016\u0000\u0000Z[\u0005" +
                    "\u0018\u0000\u0000[\u000b\u0001\u0000\u0000\u0000\\]\u0005\u000b\u0000" +
                    "\u0000]^\u0005\u0016\u0000\u0000^_\u0005\u0017\u0000\u0000_\r\u0001\u0000" +
                    "\u0000\u0000`a\u0005\b\u0000\u0000ab\u0005\u0016\u0000\u0000bc\u0005\u000e" +
                    "\u0000\u0000cd\u0003\u0014\n\u0000d\u000f\u0001\u0000\u0000\u0000ef\u0005" +
                    "\t\u0000\u0000fg\u0005\u0016\u0000\u0000gi\u0005\u0001\u0000\u0000hj\u0003" +
                    "$\u0012\u0000ih\u0001\u0000\u0000\u0000ij\u0001\u0000\u0000\u0000jk\u0001" +
                    "\u0000\u0000\u0000kl\u0005\u0002\u0000\u0000lm\u0003\u0014\n\u0000m\u0011" +
                    "\u0001\u0000\u0000\u0000nr\u0003\u0014\n\u0000oq\u0005&\u0000\u0000po" +
                    "\u0001\u0000\u0000\u0000qt\u0001\u0000\u0000\u0000rp\u0001\u0000\u0000" +
                    "\u0000rs\u0001\u0000\u0000\u0000s\u0013\u0001\u0000\u0000\u0000tr\u0001" +
                    "\u0000\u0000\u0000uw\u0003\u0018\f\u0000vx\u0003\u001e\u000f\u0000wv\u0001" +
                    "\u0000\u0000\u0000wx\u0001\u0000\u0000\u0000xz\u0001\u0000\u0000\u0000" +
                    "y{\u0003\u0016\u000b\u0000zy\u0001\u0000\u0000\u0000z{\u0001\u0000\u0000" +
                    "\u0000{\u0015\u0001\u0000\u0000\u0000|}\u0005\u0015\u0000\u0000}\u008b" +
                    "\u0003\u0014\n\u0000~\u007f\u0005\u0014\u0000\u0000\u007f\u008b\u0003" +
                    "\u0014\n\u0000\u0080\u0081\u0005\u0013\u0000\u0000\u0081\u008b\u0003\u0014" +
                    "\n\u0000\u0082\u0083\u0005\u0012\u0000\u0000\u0083\u008b\u0003\u0014\n" +
                    "\u0000\u0084\u0085\u0005\u0011\u0000\u0000\u0085\u008b\u0003\u0014\n\u0000" +
                    "\u0086\u0087\u0005\u0003\u0000\u0000\u0087\u008b\u0003 \u0010\u0000\u0088" +
                    "\u0089\u0005\u0004\u0000\u0000\u0089\u008b\u0003 \u0010\u0000\u008a|\u0001" +
                    "\u0000\u0000\u0000\u008a~\u0001\u0000\u0000\u0000\u008a\u0080\u0001\u0000" +
                    "\u0000\u0000\u008a\u0082\u0001\u0000\u0000\u0000\u008a\u0084\u0001\u0000" +
                    "\u0000\u0000\u008a\u0086\u0001\u0000\u0000\u0000\u008a\u0088\u0001\u0000" +
                    "\u0000\u0000\u008b\u0017\u0001\u0000\u0000\u0000\u008c\u00a9\u0003\u001c" +
                    "\u000e\u0000\u008d\u00a9\u0003\u001a\r\u0000\u008e\u00a9\u0003.\u0017" +
                    "\u0000\u008f\u0095\u0005\u0005\u0000\u0000\u0090\u0091\u0005\u0001\u0000" +
                    "\u0000\u0091\u0092\u0003\u0014\n\u0000\u0092\u0093\u0005\u0002\u0000\u0000" +
                    "\u0093\u0096\u0001\u0000\u0000\u0000\u0094\u0096\u0003\u0014\n\u0000\u0095" +
                    "\u0090\u0001\u0000\u0000\u0000\u0095\u0094\u0001\u0000\u0000\u0000\u0096" +
                    "\u00a9\u0001\u0000\u0000\u0000\u0097\u009d\u0005\u0006\u0000\u0000\u0098" +
                    "\u0099\u0005\u0001\u0000\u0000\u0099\u009a\u0003\u0014\n\u0000\u009a\u009b" +
                    "\u0005\u0002\u0000\u0000\u009b\u009e\u0001\u0000\u0000\u0000\u009c\u009e" +
                    "\u0003\u0014\n\u0000\u009d\u0098\u0001\u0000\u0000\u0000\u009d\u009c\u0001" +
                    "\u0000\u0000\u0000\u009e\u00a9\u0001\u0000\u0000\u0000\u009f\u00a5\u0005" +
                    "\u0007\u0000\u0000\u00a0\u00a1\u0005\u0001\u0000\u0000\u00a1\u00a2\u0003" +
                    "\u0014\n\u0000\u00a2\u00a3\u0005\u0002\u0000\u0000\u00a3\u00a6\u0001\u0000" +
                    "\u0000\u0000\u00a4\u00a6\u0003\u0014\n\u0000\u00a5\u00a0\u0001\u0000\u0000" +
                    "\u0000\u00a5\u00a4\u0001\u0000\u0000\u0000\u00a6\u00a9\u0001\u0000\u0000" +
                    "\u0000\u00a7\u00a9\u0003\"\u0011\u0000\u00a8\u008c\u0001\u0000\u0000\u0000" +
                    "\u00a8\u008d\u0001\u0000\u0000\u0000\u00a8\u008e\u0001\u0000\u0000\u0000" +
                    "\u00a8\u008f\u0001\u0000\u0000\u0000\u00a8\u0097\u0001\u0000\u0000\u0000" +
                    "\u00a8\u009f\u0001\u0000\u0000\u0000\u00a8\u00a7\u0001\u0000\u0000\u0000" +
                    "\u00a9\u0019\u0001\u0000\u0000\u0000\u00aa\u00af\u0003&\u0013\u0000\u00ab" +
                    "\u00af\u0003(\u0014\u0000\u00ac\u00af\u0003*\u0015\u0000\u00ad\u00af\u0005" +
                    "\u0016\u0000\u0000\u00ae\u00aa\u0001\u0000\u0000\u0000\u00ae\u00ab\u0001" +
                    "\u0000\u0000\u0000\u00ae\u00ac\u0001\u0000\u0000\u0000\u00ae\u00ad\u0001" +
                    "\u0000\u0000\u0000\u00af\u001b\u0001\u0000\u0000\u0000\u00b0\u00b1\u0005" +
                    "\u0001\u0000\u0000\u00b1\u00b2\u0003\u0014\n\u0000\u00b2\u00b3\u0005\u0002" +
                    "\u0000\u0000\u00b3\u001d\u0001\u0000\u0000\u0000\u00b4\u00b5\u0005\u001d" +
                    "\u0000\u0000\u00b5\u00c3\u0005\u0016\u0000\u0000\u00b6\u00b7\u0005\u001d" +
                    "\u0000\u0000\u00b7\u00b8\u0005(\u0000\u0000\u00b8\u00c3\u0005\u0016\u0000" +
                    "\u0000\u00b9\u00ba\u0005\u0014\u0000\u0000\u00ba\u00c3\u0005\u0016\u0000" +
                    "\u0000\u00bb\u00bc\u0005\"\u0000\u0000\u00bc\u00bd\u0005\u0019\u0000\u0000" +
                    "\u00bd\u00c3\u0005#\u0000\u0000\u00be\u00bf\u0005\u001d\u0000\u0000\u00bf" +
                    "\u00c0\u0005)\u0000\u0000\u00c0\u00c3\u0005\u0016\u0000\u0000\u00c1\u00c3" +
                    "\u0005*\u0000\u0000\u00c2\u00b4\u0001\u0000\u0000\u0000\u00c2\u00b6\u0001" +
                    "\u0000\u0000\u0000\u00c2\u00b9\u0001\u0000\u0000\u0000\u00c2\u00bb\u0001" +
                    "\u0000\u0000\u0000\u00c2\u00be\u0001\u0000\u0000\u0000\u00c2\u00c1\u0001" +
                    "\u0000\u0000\u0000\u00c3\u001f\u0001\u0000\u0000\u0000\u00c4\u00d2\u0003" +
                    "\u0014\n\u0000\u00c5\u00c6\u0005\u0001\u0000\u0000\u00c6\u00cb\u0005\u0016" +
                    "\u0000\u0000\u00c7\u00c8\u0005\u001f\u0000\u0000\u00c8\u00ca\u0005\u0016" +
                    "\u0000\u0000\u00c9\u00c7\u0001\u0000\u0000\u0000\u00ca\u00cd\u0001\u0000" +
                    "\u0000\u0000\u00cb\u00c9\u0001\u0000\u0000\u0000\u00cb\u00cc\u0001\u0000" +
                    "\u0000\u0000\u00cc\u00ce\u0001\u0000\u0000\u0000\u00cd\u00cb\u0001\u0000" +
                    "\u0000\u0000\u00ce\u00cf\u0005\u0002\u0000\u0000\u00cf\u00d0\u0005\u000f" +
                    "\u0000\u0000\u00d0\u00d2\u0003\u0014\n\u0000\u00d1\u00c4\u0001\u0000\u0000" +
                    "\u0000\u00d1\u00c5\u0001\u0000\u0000\u0000\u00d2!\u0001\u0000\u0000\u0000" +
                    "\u00d3\u00d4\u0005\u0001\u0000\u0000\u00d4\u00d5\u0003$\u0012\u0000\u00d5" +
                    "\u00d6\u0005\u0002\u0000\u0000\u00d6\u00d7\u0005\u000f\u0000\u0000\u00d7" +
                    "\u00d8\u0003\u0014\n\u0000\u00d8#\u0001\u0000\u0000\u0000\u00d9\u00de" +
                    "\u0005\u0016\u0000\u0000\u00da\u00db\u0005\u001f\u0000\u0000\u00db\u00dd" +
                    "\u0005\u0016\u0000\u0000\u00dc\u00da\u0001\u0000\u0000\u0000\u00dd\u00e0" +
                    "\u0001\u0000\u0000\u0000\u00de\u00dc\u0001\u0000\u0000\u0000\u00de\u00df" +
                    "\u0001\u0000\u0000\u0000\u00df%\u0001\u0000\u0000\u0000\u00e0\u00de\u0001" +
                    "\u0000\u0000\u0000\u00e1\u00e2\u0007\u0001\u0000\u0000\u00e2\'\u0001\u0000" +
                    "\u0000\u0000\u00e3\u00ec\u0005\"\u0000\u0000\u00e4\u00e9\u0003\u0014\n" +
                    "\u0000\u00e5\u00e6\u0005\u001f\u0000\u0000\u00e6\u00e8\u0003\u0014\n\u0000" +
                    "\u00e7\u00e5\u0001\u0000\u0000\u0000\u00e8\u00eb\u0001\u0000\u0000\u0000" +
                    "\u00e9\u00e7\u0001\u0000\u0000\u0000\u00e9\u00ea\u0001\u0000\u0000\u0000" +
                    "\u00ea\u00ed\u0001\u0000\u0000\u0000\u00eb\u00e9\u0001\u0000\u0000\u0000" +
                    "\u00ec\u00e4\u0001\u0000\u0000\u0000\u00ec\u00ed\u0001\u0000\u0000\u0000" +
                    "\u00ed\u00ee\u0001\u0000\u0000\u0000\u00ee\u00ef\u0005#\u0000\u0000\u00ef" +
                    ")\u0001\u0000\u0000\u0000\u00f0\u00f1\u0005 \u0000\u0000\u00f1\u00f6\u0003" +
                    ",\u0016\u0000\u00f2\u00f3\u0005\u001f\u0000\u0000\u00f3\u00f5\u0003,\u0016" +
                    "\u0000\u00f4\u00f2\u0001\u0000\u0000\u0000\u00f5\u00f8\u0001\u0000\u0000" +
                    "\u0000\u00f6\u00f4\u0001\u0000\u0000\u0000\u00f6\u00f7\u0001\u0000\u0000" +
                    "\u0000\u00f7\u00f9\u0001\u0000\u0000\u0000\u00f8\u00f6\u0001\u0000\u0000" +
                    "\u0000\u00f9\u00fa\u0005!\u0000\u0000\u00fa\u00fd\u0001\u0000\u0000\u0000" +
                    "\u00fb\u00fd\u0003,\u0016\u0000\u00fc\u00f0\u0001\u0000\u0000\u0000\u00fc" +
                    "\u00fb\u0001\u0000\u0000\u0000\u00fd+\u0001\u0000\u0000\u0000\u00fe\u00ff" +
                    "\u0005\u0016\u0000\u0000\u00ff\u0100\u0005\u001e\u0000\u0000\u0100\u0101" +
                    "\u0003\u0014\n\u0000\u0101-\u0001\u0000\u0000\u0000\u0102\u0103\u0005" +
                    "\u0016\u0000\u0000\u0103\u010c\u0005\u0001\u0000\u0000\u0104\u0109\u0003" +
                    "\u0014\n\u0000\u0105\u0106\u0005\u001f\u0000\u0000\u0106\u0108\u0003\u0014" +
                    "\n\u0000\u0107\u0105\u0001\u0000\u0000\u0000\u0108\u010b\u0001\u0000\u0000" +
                    "\u0000\u0109\u0107\u0001\u0000\u0000\u0000\u0109\u010a\u0001\u0000\u0000" +
                    "\u0000\u010a\u010d\u0001\u0000\u0000\u0000\u010b\u0109\u0001\u0000\u0000" +
                    "\u0000\u010c\u0104\u0001\u0000\u0000\u0000\u010c\u010d\u0001\u0000\u0000" +
                    "\u0000\u010d\u010e\u0001\u0000\u0000\u0000\u010e\u010f\u0005\u0002\u0000" +
                    "\u0000\u010f/\u0001\u0000\u0000\u0000\u001a15:CHPirwz\u008a\u0095\u009d" +
                    "\u00a5\u00a8\u00ae\u00c2\u00cb\u00d1\u00de\u00e9\u00ec\u00f6\u00fc\u0109" +
                    "\u010c";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}