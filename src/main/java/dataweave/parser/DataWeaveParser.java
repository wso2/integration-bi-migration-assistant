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
            T__0 = 1, T__1 = 2, VAR = 3, FUNCTION = 4, INPUT = 5, NAMESPACE = 6, OUTPUT = 7, DW = 8,
            ASSIGN = 9, ARROW = 10, BOOLEAN = 11, IDENTIFIER = 12, URL = 13, MEDIA_TYPE = 14,
            NUMBER = 15, STRING = 16, DATE = 17, REGEX = 18, DOT = 19, COLON = 20, COMMA = 21, LCURLY = 22,
            RCURLY = 23, LSQUARE = 24, RSQUARE = 25, SEPARATOR = 26, WS = 27, NEWLINE = 28, COMMENT = 29,
            OPERATOR_MATH = 30, OPERATOR_COMPARISON = 31, OPERATOR_LOGICAL = 32, OPERATOR_BITWISE = 33,
            OPERATOR_CONDITIONAL = 34, OPERATOR_RANGE = 35, OPERATOR_CHAIN = 36, BUILTIN_FUNCTION = 37;
    public static final int
            RULE_script = 0, RULE_header = 1, RULE_directive = 2, RULE_dwVersion = 3,
            RULE_outputDirective = 4, RULE_inputDirective = 5, RULE_namespaceDirective = 6,
            RULE_variableDeclaration = 7, RULE_functionDeclaration = 8, RULE_body = 9,
            RULE_expression = 10, RULE_builtInFunctionCall = 11, RULE_inlineLambdaMap = 12,
            RULE_inlineLambda = 13, RULE_functionParameters = 14, RULE_literal = 15,
            RULE_array = 16, RULE_object = 17, RULE_keyValue = 18, RULE_functionCall = 19;

    private static String[] makeRuleNames() {
        return new String[]{
                "script", "header", "directive", "dwVersion", "outputDirective", "inputDirective",
                "namespaceDirective", "variableDeclaration", "functionDeclaration", "body",
                "expression", "builtInFunctionCall", "inlineLambdaMap", "inlineLambda",
                "functionParameters", "literal", "array", "object", "keyValue", "functionCall"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'('", "')'", "'%var'", "'%function'", "'%input'", "'%namespace'",
                "'%output'", "'%dw'", "'='", "'->'", null, null, null, null, null, null,
                null, null, "'.'", "':'", "','", "'{'", "'}'", "'['", "']'", "'---'",
                null, null, null, null, null, null, null, null, "'..'", "'++'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, null, null, "VAR", "FUNCTION", "INPUT", "NAMESPACE", "OUTPUT",
                "DW", "ASSIGN", "ARROW", "BOOLEAN", "IDENTIFIER", "URL", "MEDIA_TYPE",
                "NUMBER", "STRING", "DATE", "REGEX", "DOT", "COLON", "COMMA", "LCURLY",
                "RCURLY", "LSQUARE", "RSQUARE", "SEPARATOR", "WS", "NEWLINE", "COMMENT",
                "OPERATOR_MATH", "OPERATOR_COMPARISON", "OPERATOR_LOGICAL", "OPERATOR_BITWISE",
                "OPERATOR_CONDITIONAL", "OPERATOR_RANGE", "OPERATOR_CHAIN", "BUILTIN_FUNCTION"
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
                setState(41);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 504L) != 0)) {
                    {
                        setState(40);
                        header();
                    }
                }

                setState(43);
                match(SEPARATOR);
                setState(45);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 137460422658L) != 0)) {
                    {
                        setState(44);
                        body();
                    }
                }

                setState(50);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == NEWLINE) {
                    {
                        {
                            setState(47);
                            match(NEWLINE);
                        }
                    }
                    setState(52);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
                setState(53);
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
                setState(62);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(55);
                            directive();
                            setState(59);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                            while (_la == WS || _la == NEWLINE) {
                                {
                                    {
                                        setState(56);
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
                                setState(61);
                                _errHandler.sync(this);
                                _la = _input.LA(1);
                            }
                        }
                    }
                    setState(64);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 504L) != 0));
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
            setState(72);
            _errHandler.sync(this);
            switch (_input.LA(1)) {
                case DW:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(66);
                    dwVersion();
                }
                break;
                case OUTPUT:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(67);
                    outputDirective();
                }
                break;
                case INPUT:
                    enterOuterAlt(_localctx, 3);
                {
                    setState(68);
                    inputDirective();
                }
                break;
                case NAMESPACE:
                    enterOuterAlt(_localctx, 4);
                {
                    setState(69);
                    namespaceDirective();
                }
                break;
                case VAR:
                    enterOuterAlt(_localctx, 5);
                {
                    setState(70);
                    variableDeclaration();
                }
                break;
                case FUNCTION:
                    enterOuterAlt(_localctx, 6);
                {
                    setState(71);
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
                setState(74);
                match(DW);
                setState(75);
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
                setState(77);
                match(OUTPUT);
                setState(78);
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
                setState(80);
                match(INPUT);
                setState(81);
                match(IDENTIFIER);
                setState(82);
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
                setState(84);
                match(NAMESPACE);
                setState(85);
                match(IDENTIFIER);
                setState(86);
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
                setState(88);
                match(VAR);
                setState(89);
                match(IDENTIFIER);
                setState(90);
                match(ASSIGN);
                setState(91);
                expression(0);
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
                setState(93);
                match(FUNCTION);
                setState(94);
                match(IDENTIFIER);
                setState(95);
                match(T__0);
                setState(97);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == IDENTIFIER) {
                    {
                        setState(96);
                        functionParameters();
                    }
                }

                setState(99);
                match(T__1);
                setState(100);
                expression(0);
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
        public List<ExpressionContext> expression() {
            return getRuleContexts(ExpressionContext.class);
        }

        public ExpressionContext expression(int i) {
            return getRuleContext(ExpressionContext.class, i);
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
        int _la;
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                setState(102);
                expression(0);
                setState(111);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 8, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        {
                            {
                                setState(104);
                                _errHandler.sync(this);
                                _la = _input.LA(1);
                                do {
                                    {
                                        {
                                            setState(103);
                                            match(NEWLINE);
                                        }
                                    }
                                    setState(106);
                                    _errHandler.sync(this);
                                    _la = _input.LA(1);
                                } while (_la == NEWLINE);
                                setState(108);
                                expression(0);
                            }
                        }
                    }
                    setState(113);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 8, _ctx);
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
    public static class LambdaExpressionContext extends ExpressionContext {
        public InlineLambdaContext inlineLambda() {
            return getRuleContext(InlineLambdaContext.class, 0);
        }

        public LambdaExpressionContext(ExpressionContext ctx) {
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
    public static class ArrayExpressionContext extends ExpressionContext {
        public ArrayContext array() {
            return getRuleContext(ArrayContext.class, 0);
        }

        public ArrayExpressionContext(ExpressionContext ctx) {
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
    public static class ConditionalExpressionContext extends ExpressionContext {
        public List<ExpressionContext> expression() {
            return getRuleContexts(ExpressionContext.class);
        }

        public ExpressionContext expression(int i) {
            return getRuleContext(ExpressionContext.class, i);
        }

        public TerminalNode OPERATOR_CONDITIONAL() {
            return getToken(DataWeaveParser.OPERATOR_CONDITIONAL, 0);
        }

        public TerminalNode COLON() {
            return getToken(DataWeaveParser.COLON, 0);
        }

        public ConditionalExpressionContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterConditionalExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitConditionalExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitConditionalExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class IdentifierExpressionContext extends ExpressionContext {
        public TerminalNode IDENTIFIER() {
            return getToken(DataWeaveParser.IDENTIFIER, 0);
        }

        public IdentifierExpressionContext(ExpressionContext ctx) {
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
    public static class MathExpressionContext extends ExpressionContext {
        public List<ExpressionContext> expression() {
            return getRuleContexts(ExpressionContext.class);
        }

        public ExpressionContext expression(int i) {
            return getRuleContext(ExpressionContext.class, i);
        }

        public TerminalNode OPERATOR_MATH() {
            return getToken(DataWeaveParser.OPERATOR_MATH, 0);
        }

        public MathExpressionContext(ExpressionContext ctx) {
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
    public static class ComparisonExpressionContext extends ExpressionContext {
        public List<ExpressionContext> expression() {
            return getRuleContexts(ExpressionContext.class);
        }

        public ExpressionContext expression(int i) {
            return getRuleContext(ExpressionContext.class, i);
        }

        public TerminalNode OPERATOR_COMPARISON() {
            return getToken(DataWeaveParser.OPERATOR_COMPARISON, 0);
        }

        public ComparisonExpressionContext(ExpressionContext ctx) {
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
    public static class LogicalExpressionContext extends ExpressionContext {
        public List<ExpressionContext> expression() {
            return getRuleContexts(ExpressionContext.class);
        }

        public ExpressionContext expression(int i) {
            return getRuleContext(ExpressionContext.class, i);
        }

        public TerminalNode OPERATOR_LOGICAL() {
            return getToken(DataWeaveParser.OPERATOR_LOGICAL, 0);
        }

        public LogicalExpressionContext(ExpressionContext ctx) {
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

    @SuppressWarnings("CheckReturnValue")
    public static class GroupedExpressionContext extends ExpressionContext {
        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public GroupedExpressionContext(ExpressionContext ctx) {
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

    @SuppressWarnings("CheckReturnValue")
    public static class ObjectExpressionContext extends ExpressionContext {
        public ObjectContext object() {
            return getRuleContext(ObjectContext.class, 0);
        }

        public ObjectExpressionContext(ExpressionContext ctx) {
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
    public static class ChainExpressionContext extends ExpressionContext {
        public List<ExpressionContext> expression() {
            return getRuleContexts(ExpressionContext.class);
        }

        public ExpressionContext expression(int i) {
            return getRuleContext(ExpressionContext.class, i);
        }

        public TerminalNode OPERATOR_CHAIN() {
            return getToken(DataWeaveParser.OPERATOR_CHAIN, 0);
        }

        public ChainExpressionContext(ExpressionContext ctx) {
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
    public static class BuiltInFunctionExpressionContext extends ExpressionContext {
        public BuiltInFunctionCallContext builtInFunctionCall() {
            return getRuleContext(BuiltInFunctionCallContext.class, 0);
        }

        public BuiltInFunctionExpressionContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener)
                ((DataWeaveListener) listener).enterBuiltInFunctionExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener)
                ((DataWeaveListener) listener).exitBuiltInFunctionExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitBuiltInFunctionExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class FunctionCallExpressionContext extends ExpressionContext {
        public FunctionCallContext functionCall() {
            return getRuleContext(FunctionCallContext.class, 0);
        }

        public FunctionCallExpressionContext(ExpressionContext ctx) {
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
    public static class RangeExpressionContext extends ExpressionContext {
        public List<ExpressionContext> expression() {
            return getRuleContexts(ExpressionContext.class);
        }

        public ExpressionContext expression(int i) {
            return getRuleContext(ExpressionContext.class, i);
        }

        public TerminalNode OPERATOR_RANGE() {
            return getToken(DataWeaveParser.OPERATOR_RANGE, 0);
        }

        public RangeExpressionContext(ExpressionContext ctx) {
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
    public static class BitwiseExpressionContext extends ExpressionContext {
        public List<ExpressionContext> expression() {
            return getRuleContexts(ExpressionContext.class);
        }

        public ExpressionContext expression(int i) {
            return getRuleContext(ExpressionContext.class, i);
        }

        public TerminalNode OPERATOR_BITWISE() {
            return getToken(DataWeaveParser.OPERATOR_BITWISE, 0);
        }

        public BitwiseExpressionContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterBitwiseExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitBitwiseExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitBitwiseExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class LiteralExpressionContext extends ExpressionContext {
        public LiteralContext literal() {
            return getRuleContext(LiteralContext.class, 0);
        }

        public LiteralExpressionContext(ExpressionContext ctx) {
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

    public final ExpressionContext expression() throws RecognitionException {
        return expression(0);
    }

    private ExpressionContext expression(int _p) throws RecognitionException {
        ParserRuleContext _parentctx = _ctx;
        int _parentState = getState();
        ExpressionContext _localctx = new ExpressionContext(_ctx, _parentState);
        ExpressionContext _prevctx = _localctx;
        int _startState = 20;
        enterRecursionRule(_localctx, 20, RULE_expression, _p);
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                setState(126);
                _errHandler.sync(this);
                switch (getInterpreter().adaptivePredict(_input, 9, _ctx)) {
                    case 1: {
                        _localctx = new BuiltInFunctionExpressionContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;

                        setState(115);
                        builtInFunctionCall();
                    }
                    break;
                    case 2: {
                        _localctx = new FunctionCallExpressionContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(116);
                        functionCall();
                    }
                    break;
                    case 3: {
                        _localctx = new LambdaExpressionContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(117);
                        inlineLambda();
                    }
                    break;
                    case 4: {
                        _localctx = new LiteralExpressionContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(118);
                        literal();
                    }
                    break;
                    case 5: {
                        _localctx = new ArrayExpressionContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(119);
                        array();
                    }
                    break;
                    case 6: {
                        _localctx = new ObjectExpressionContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(120);
                        object();
                    }
                    break;
                    case 7: {
                        _localctx = new IdentifierExpressionContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(121);
                        match(IDENTIFIER);
                    }
                    break;
                    case 8: {
                        _localctx = new GroupedExpressionContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(122);
                        match(T__0);
                        setState(123);
                        expression(0);
                        setState(124);
                        match(T__1);
                    }
                    break;
                }
                _ctx.stop = _input.LT(-1);
                setState(154);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 11, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        if (_parseListeners != null) triggerExitRuleEvent();
                        _prevctx = _localctx;
                        {
                            setState(152);
                            _errHandler.sync(this);
                            switch (getInterpreter().adaptivePredict(_input, 10, _ctx)) {
                                case 1: {
                                    _localctx = new ConditionalExpressionContext(new ExpressionContext(_parentctx, _parentState));
                                    pushNewRecursionContext(_localctx, _startState, RULE_expression);
                                    setState(128);
                                    if (!(precpred(_ctx, 14)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 14)");
                                    setState(129);
                                    match(OPERATOR_CONDITIONAL);
                                    setState(130);
                                    expression(0);
                                    setState(131);
                                    match(COLON);
                                    setState(132);
                                    expression(15);
                                }
                                break;
                                case 2: {
                                    _localctx = new LogicalExpressionContext(new ExpressionContext(_parentctx, _parentState));
                                    pushNewRecursionContext(_localctx, _startState, RULE_expression);
                                    setState(134);
                                    if (!(precpred(_ctx, 13)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 13)");
                                    setState(135);
                                    match(OPERATOR_LOGICAL);
                                    setState(136);
                                    expression(14);
                                }
                                break;
                                case 3: {
                                    _localctx = new ComparisonExpressionContext(new ExpressionContext(_parentctx, _parentState));
                                    pushNewRecursionContext(_localctx, _startState, RULE_expression);
                                    setState(137);
                                    if (!(precpred(_ctx, 12)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 12)");
                                    setState(138);
                                    match(OPERATOR_COMPARISON);
                                    setState(139);
                                    expression(13);
                                }
                                break;
                                case 4: {
                                    _localctx = new BitwiseExpressionContext(new ExpressionContext(_parentctx, _parentState));
                                    pushNewRecursionContext(_localctx, _startState, RULE_expression);
                                    setState(140);
                                    if (!(precpred(_ctx, 11)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 11)");
                                    setState(141);
                                    match(OPERATOR_BITWISE);
                                    setState(142);
                                    expression(12);
                                }
                                break;
                                case 5: {
                                    _localctx = new MathExpressionContext(new ExpressionContext(_parentctx, _parentState));
                                    pushNewRecursionContext(_localctx, _startState, RULE_expression);
                                    setState(143);
                                    if (!(precpred(_ctx, 10)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 10)");
                                    setState(144);
                                    match(OPERATOR_MATH);
                                    setState(145);
                                    expression(11);
                                }
                                break;
                                case 6: {
                                    _localctx = new RangeExpressionContext(new ExpressionContext(_parentctx, _parentState));
                                    pushNewRecursionContext(_localctx, _startState, RULE_expression);
                                    setState(146);
                                    if (!(precpred(_ctx, 9)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 9)");
                                    setState(147);
                                    match(OPERATOR_RANGE);
                                    setState(148);
                                    expression(10);
                                }
                                break;
                                case 7: {
                                    _localctx = new ChainExpressionContext(new ExpressionContext(_parentctx, _parentState));
                                    pushNewRecursionContext(_localctx, _startState, RULE_expression);
                                    setState(149);
                                    if (!(precpred(_ctx, 8)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 8)");
                                    setState(150);
                                    match(OPERATOR_CHAIN);
                                    setState(151);
                                    expression(9);
                                }
                                break;
                            }
                        }
                    }
                    setState(156);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 11, _ctx);
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            unrollRecursionContexts(_parentctx);
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class BuiltInFunctionCallContext extends ParserRuleContext {
        public TerminalNode BUILTIN_FUNCTION() {
            return getToken(DataWeaveParser.BUILTIN_FUNCTION, 0);
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

        public BuiltInFunctionCallContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_builtInFunctionCall;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterBuiltInFunctionCall(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitBuiltInFunctionCall(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitBuiltInFunctionCall(this);
            else return visitor.visitChildren(this);
        }
    }

    public final BuiltInFunctionCallContext builtInFunctionCall() throws RecognitionException {
        BuiltInFunctionCallContext _localctx = new BuiltInFunctionCallContext(_ctx, getState());
        enterRule(_localctx, 22, RULE_builtInFunctionCall);
        int _la;
        try {
            setState(174);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 13, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(157);
                    match(BUILTIN_FUNCTION);
                    setState(158);
                    match(T__0);
                    setState(159);
                    expression(0);
                    setState(160);
                    match(T__1);
                }
                break;
                case 2:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(162);
                    match(BUILTIN_FUNCTION);
                    setState(163);
                    match(T__0);
                    setState(164);
                    expression(0);
                    setState(169);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                    while (_la == COMMA) {
                        {
                            {
                                setState(165);
                                match(COMMA);
                                setState(166);
                                expression(0);
                            }
                        }
                        setState(171);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                    }
                    setState(172);
                    match(T__1);
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
    public static class InlineLambdaMapContext extends ParserRuleContext {
        public TerminalNode ARROW() {
            return getToken(DataWeaveParser.ARROW, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public List<TerminalNode> IDENTIFIER() {
            return getTokens(DataWeaveParser.IDENTIFIER);
        }

        public TerminalNode IDENTIFIER(int i) {
            return getToken(DataWeaveParser.IDENTIFIER, i);
        }

        public TerminalNode COMMA() {
            return getToken(DataWeaveParser.COMMA, 0);
        }

        public InlineLambdaMapContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_inlineLambdaMap;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterInlineLambdaMap(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitInlineLambdaMap(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitInlineLambdaMap(this);
            else return visitor.visitChildren(this);
        }
    }

    public final InlineLambdaMapContext inlineLambdaMap() throws RecognitionException {
        InlineLambdaMapContext _localctx = new InlineLambdaMapContext(_ctx, getState());
        enterRule(_localctx, 24, RULE_inlineLambdaMap);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(176);
                match(T__0);
                setState(182);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == IDENTIFIER) {
                    {
                        setState(177);
                        match(IDENTIFIER);
                        setState(180);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        if (_la == COMMA) {
                            {
                                setState(178);
                                match(COMMA);
                                setState(179);
                                match(IDENTIFIER);
                            }
                        }

                    }
                }

                setState(184);
                match(ARROW);
                setState(185);
                expression(0);
                setState(186);
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
        enterRule(_localctx, 26, RULE_inlineLambda);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(188);
                match(T__0);
                setState(189);
                functionParameters();
                setState(190);
                match(T__1);
                setState(191);
                match(ARROW);
                setState(192);
                expression(0);
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
        enterRule(_localctx, 28, RULE_functionParameters);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(194);
                match(IDENTIFIER);
                setState(199);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == COMMA) {
                    {
                        {
                            setState(195);
                            match(COMMA);
                            setState(196);
                            match(IDENTIFIER);
                        }
                    }
                    setState(201);
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
        enterRule(_localctx, 30, RULE_literal);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(202);
                _la = _input.LA(1);
                if (!((((_la) & ~0x3f) == 0 && ((1L << _la) & 493568L) != 0))) {
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
        enterRule(_localctx, 32, RULE_array);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(204);
                match(LSQUARE);
                setState(213);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 137460422658L) != 0)) {
                    {
                        setState(205);
                        expression(0);
                        setState(210);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        while (_la == COMMA) {
                            {
                                {
                                    setState(206);
                                    match(COMMA);
                                    setState(207);
                                    expression(0);
                                }
                            }
                            setState(212);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        }
                    }
                }

                setState(215);
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
        public TerminalNode LCURLY() {
            return getToken(DataWeaveParser.LCURLY, 0);
        }

        public TerminalNode RCURLY() {
            return getToken(DataWeaveParser.RCURLY, 0);
        }

        public List<KeyValueContext> keyValue() {
            return getRuleContexts(KeyValueContext.class);
        }

        public KeyValueContext keyValue(int i) {
            return getRuleContext(KeyValueContext.class, i);
        }

        public List<TerminalNode> COMMA() {
            return getTokens(DataWeaveParser.COMMA);
        }

        public TerminalNode COMMA(int i) {
            return getToken(DataWeaveParser.COMMA, i);
        }

        public ObjectContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_object;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterObject(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitObject(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor) return ((DataWeaveVisitor<? extends T>) visitor).visitObject(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ObjectContext object() throws RecognitionException {
        ObjectContext _localctx = new ObjectContext(_ctx, getState());
        enterRule(_localctx, 34, RULE_object);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(217);
                match(LCURLY);
                setState(226);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == IDENTIFIER) {
                    {
                        setState(218);
                        keyValue();
                        setState(223);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        while (_la == COMMA) {
                            {
                                {
                                    setState(219);
                                    match(COMMA);
                                    setState(220);
                                    keyValue();
                                }
                            }
                            setState(225);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        }
                    }
                }

                setState(228);
                match(RCURLY);
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
        enterRule(_localctx, 36, RULE_keyValue);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(230);
                match(IDENTIFIER);
                setState(231);
                match(COLON);
                setState(232);
                expression(0);
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
        enterRule(_localctx, 38, RULE_functionCall);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(234);
                match(IDENTIFIER);
                setState(235);
                match(T__0);
                setState(244);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 137460422658L) != 0)) {
                    {
                        setState(236);
                        expression(0);
                        setState(241);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        while (_la == COMMA) {
                            {
                                {
                                    setState(237);
                                    match(COMMA);
                                    setState(238);
                                    expression(0);
                                }
                            }
                            setState(243);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        }
                    }
                }

                setState(246);
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

    public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
        switch (ruleIndex) {
            case 10:
                return expression_sempred((ExpressionContext) _localctx, predIndex);
        }
        return true;
    }

    private boolean expression_sempred(ExpressionContext _localctx, int predIndex) {
        switch (predIndex) {
            case 0:
                return precpred(_ctx, 14);
            case 1:
                return precpred(_ctx, 13);
            case 2:
                return precpred(_ctx, 12);
            case 3:
                return precpred(_ctx, 11);
            case 4:
                return precpred(_ctx, 10);
            case 5:
                return precpred(_ctx, 9);
            case 6:
                return precpred(_ctx, 8);
        }
        return true;
    }

    public static final String _serializedATN =
            "\u0004\u0001%\u00f9\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002" +
                    "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002" +
                    "\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002" +
                    "\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002" +
                    "\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f" +
                    "\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012" +
                    "\u0002\u0013\u0007\u0013\u0001\u0000\u0003\u0000*\b\u0000\u0001\u0000" +
                    "\u0001\u0000\u0003\u0000.\b\u0000\u0001\u0000\u0005\u00001\b\u0000\n\u0000" +
                    "\f\u00004\t\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0005" +
                    "\u0001:\b\u0001\n\u0001\f\u0001=\t\u0001\u0004\u0001?\b\u0001\u000b\u0001" +
                    "\f\u0001@\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002" +
                    "\u0001\u0002\u0003\u0002I\b\u0002\u0001\u0003\u0001\u0003\u0001\u0003" +
                    "\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005" +
                    "\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007" +
                    "\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001" +
                    "\b\u0001\b\u0003\bb\b\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0004" +
                    "\ti\b\t\u000b\t\f\tj\u0001\t\u0005\tn\b\t\n\t\f\tq\t\t\u0001\n\u0001\n" +
                    "\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001" +
                    "\n\u0001\n\u0003\n\u007f\b\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001" +
                    "\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001" +
                    "\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001" +
                    "\n\u0005\n\u0099\b\n\n\n\f\n\u009c\t\n\u0001\u000b\u0001\u000b\u0001\u000b" +
                    "\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b" +
                    "\u0001\u000b\u0005\u000b\u00a8\b\u000b\n\u000b\f\u000b\u00ab\t\u000b\u0001" +
                    "\u000b\u0001\u000b\u0003\u000b\u00af\b\u000b\u0001\f\u0001\f\u0001\f\u0001" +
                    "\f\u0003\f\u00b5\b\f\u0003\f\u00b7\b\f\u0001\f\u0001\f\u0001\f\u0001\f" +
                    "\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\u000e\u0001\u000e" +
                    "\u0001\u000e\u0005\u000e\u00c6\b\u000e\n\u000e\f\u000e\u00c9\t\u000e\u0001" +
                    "\u000f\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0005" +
                    "\u0010\u00d1\b\u0010\n\u0010\f\u0010\u00d4\t\u0010\u0003\u0010\u00d6\b" +
                    "\u0010\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011\u0001\u0011\u0001" +
                    "\u0011\u0005\u0011\u00de\b\u0011\n\u0011\f\u0011\u00e1\t\u0011\u0003\u0011" +
                    "\u00e3\b\u0011\u0001\u0011\u0001\u0011\u0001\u0012\u0001\u0012\u0001\u0012" +
                    "\u0001\u0012\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013" +
                    "\u0005\u0013\u00f0\b\u0013\n\u0013\f\u0013\u00f3\t\u0013\u0003\u0013\u00f5" +
                    "\b\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0000\u0001\u0014\u0014\u0000" +
                    "\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c" +
                    "\u001e \"$&\u0000\u0002\u0001\u0000\u001b\u001c\u0002\u0000\u000b\u000b" +
                    "\u000f\u0012\u010a\u0000)\u0001\u0000\u0000\u0000\u0002>\u0001\u0000\u0000" +
                    "\u0000\u0004H\u0001\u0000\u0000\u0000\u0006J\u0001\u0000\u0000\u0000\b" +
                    "M\u0001\u0000\u0000\u0000\nP\u0001\u0000\u0000\u0000\fT\u0001\u0000\u0000" +
                    "\u0000\u000eX\u0001\u0000\u0000\u0000\u0010]\u0001\u0000\u0000\u0000\u0012" +
                    "f\u0001\u0000\u0000\u0000\u0014~\u0001\u0000\u0000\u0000\u0016\u00ae\u0001" +
                    "\u0000\u0000\u0000\u0018\u00b0\u0001\u0000\u0000\u0000\u001a\u00bc\u0001" +
                    "\u0000\u0000\u0000\u001c\u00c2\u0001\u0000\u0000\u0000\u001e\u00ca\u0001" +
                    "\u0000\u0000\u0000 \u00cc\u0001\u0000\u0000\u0000\"\u00d9\u0001\u0000" +
                    "\u0000\u0000$\u00e6\u0001\u0000\u0000\u0000&\u00ea\u0001\u0000\u0000\u0000" +
                    "(*\u0003\u0002\u0001\u0000)(\u0001\u0000\u0000\u0000)*\u0001\u0000\u0000" +
                    "\u0000*+\u0001\u0000\u0000\u0000+-\u0005\u001a\u0000\u0000,.\u0003\u0012" +
                    "\t\u0000-,\u0001\u0000\u0000\u0000-.\u0001\u0000\u0000\u0000.2\u0001\u0000" +
                    "\u0000\u0000/1\u0005\u001c\u0000\u00000/\u0001\u0000\u0000\u000014\u0001" +
                    "\u0000\u0000\u000020\u0001\u0000\u0000\u000023\u0001\u0000\u0000\u0000" +
                    "35\u0001\u0000\u0000\u000042\u0001\u0000\u0000\u000056\u0005\u0000\u0000" +
                    "\u00016\u0001\u0001\u0000\u0000\u00007;\u0003\u0004\u0002\u00008:\u0007" +
                    "\u0000\u0000\u000098\u0001\u0000\u0000\u0000:=\u0001\u0000\u0000\u0000" +
                    ";9\u0001\u0000\u0000\u0000;<\u0001\u0000\u0000\u0000<?\u0001\u0000\u0000" +
                    "\u0000=;\u0001\u0000\u0000\u0000>7\u0001\u0000\u0000\u0000?@\u0001\u0000" +
                    "\u0000\u0000@>\u0001\u0000\u0000\u0000@A\u0001\u0000\u0000\u0000A\u0003" +
                    "\u0001\u0000\u0000\u0000BI\u0003\u0006\u0003\u0000CI\u0003\b\u0004\u0000" +
                    "DI\u0003\n\u0005\u0000EI\u0003\f\u0006\u0000FI\u0003\u000e\u0007\u0000" +
                    "GI\u0003\u0010\b\u0000HB\u0001\u0000\u0000\u0000HC\u0001\u0000\u0000\u0000" +
                    "HD\u0001\u0000\u0000\u0000HE\u0001\u0000\u0000\u0000HF\u0001\u0000\u0000" +
                    "\u0000HG\u0001\u0000\u0000\u0000I\u0005\u0001\u0000\u0000\u0000JK\u0005" +
                    "\b\u0000\u0000KL\u0005\u000f\u0000\u0000L\u0007\u0001\u0000\u0000\u0000" +
                    "MN\u0005\u0007\u0000\u0000NO\u0005\u000e\u0000\u0000O\t\u0001\u0000\u0000" +
                    "\u0000PQ\u0005\u0005\u0000\u0000QR\u0005\f\u0000\u0000RS\u0005\u000e\u0000" +
                    "\u0000S\u000b\u0001\u0000\u0000\u0000TU\u0005\u0006\u0000\u0000UV\u0005" +
                    "\f\u0000\u0000VW\u0005\r\u0000\u0000W\r\u0001\u0000\u0000\u0000XY\u0005" +
                    "\u0003\u0000\u0000YZ\u0005\f\u0000\u0000Z[\u0005\t\u0000\u0000[\\\u0003" +
                    "\u0014\n\u0000\\\u000f\u0001\u0000\u0000\u0000]^\u0005\u0004\u0000\u0000" +
                    "^_\u0005\f\u0000\u0000_a\u0005\u0001\u0000\u0000`b\u0003\u001c\u000e\u0000" +
                    "a`\u0001\u0000\u0000\u0000ab\u0001\u0000\u0000\u0000bc\u0001\u0000\u0000" +
                    "\u0000cd\u0005\u0002\u0000\u0000de\u0003\u0014\n\u0000e\u0011\u0001\u0000" +
                    "\u0000\u0000fo\u0003\u0014\n\u0000gi\u0005\u001c\u0000\u0000hg\u0001\u0000" +
                    "\u0000\u0000ij\u0001\u0000\u0000\u0000jh\u0001\u0000\u0000\u0000jk\u0001" +
                    "\u0000\u0000\u0000kl\u0001\u0000\u0000\u0000ln\u0003\u0014\n\u0000mh\u0001" +
                    "\u0000\u0000\u0000nq\u0001\u0000\u0000\u0000om\u0001\u0000\u0000\u0000" +
                    "op\u0001\u0000\u0000\u0000p\u0013\u0001\u0000\u0000\u0000qo\u0001\u0000" +
                    "\u0000\u0000rs\u0006\n\uffff\uffff\u0000s\u007f\u0003\u0016\u000b\u0000" +
                    "t\u007f\u0003&\u0013\u0000u\u007f\u0003\u001a\r\u0000v\u007f\u0003\u001e" +
                    "\u000f\u0000w\u007f\u0003 \u0010\u0000x\u007f\u0003\"\u0011\u0000y\u007f" +
                    "\u0005\f\u0000\u0000z{\u0005\u0001\u0000\u0000{|\u0003\u0014\n\u0000|" +
                    "}\u0005\u0002\u0000\u0000}\u007f\u0001\u0000\u0000\u0000~r\u0001\u0000" +
                    "\u0000\u0000~t\u0001\u0000\u0000\u0000~u\u0001\u0000\u0000\u0000~v\u0001" +
                    "\u0000\u0000\u0000~w\u0001\u0000\u0000\u0000~x\u0001\u0000\u0000\u0000" +
                    "~y\u0001\u0000\u0000\u0000~z\u0001\u0000\u0000\u0000\u007f\u009a\u0001" +
                    "\u0000\u0000\u0000\u0080\u0081\n\u000e\u0000\u0000\u0081\u0082\u0005\"" +
                    "\u0000\u0000\u0082\u0083\u0003\u0014\n\u0000\u0083\u0084\u0005\u0014\u0000" +
                    "\u0000\u0084\u0085\u0003\u0014\n\u000f\u0085\u0099\u0001\u0000\u0000\u0000" +
                    "\u0086\u0087\n\r\u0000\u0000\u0087\u0088\u0005 \u0000\u0000\u0088\u0099" +
                    "\u0003\u0014\n\u000e\u0089\u008a\n\f\u0000\u0000\u008a\u008b\u0005\u001f" +
                    "\u0000\u0000\u008b\u0099\u0003\u0014\n\r\u008c\u008d\n\u000b\u0000\u0000" +
                    "\u008d\u008e\u0005!\u0000\u0000\u008e\u0099\u0003\u0014\n\f\u008f\u0090" +
                    "\n\n\u0000\u0000\u0090\u0091\u0005\u001e\u0000\u0000\u0091\u0099\u0003" +
                    "\u0014\n\u000b\u0092\u0093\n\t\u0000\u0000\u0093\u0094\u0005#\u0000\u0000" +
                    "\u0094\u0099\u0003\u0014\n\n\u0095\u0096\n\b\u0000\u0000\u0096\u0097\u0005" +
                    "$\u0000\u0000\u0097\u0099\u0003\u0014\n\t\u0098\u0080\u0001\u0000\u0000" +
                    "\u0000\u0098\u0086\u0001\u0000\u0000\u0000\u0098\u0089\u0001\u0000\u0000" +
                    "\u0000\u0098\u008c\u0001\u0000\u0000\u0000\u0098\u008f\u0001\u0000\u0000" +
                    "\u0000\u0098\u0092\u0001\u0000\u0000\u0000\u0098\u0095\u0001\u0000\u0000" +
                    "\u0000\u0099\u009c\u0001\u0000\u0000\u0000\u009a\u0098\u0001\u0000\u0000" +
                    "\u0000\u009a\u009b\u0001\u0000\u0000\u0000\u009b\u0015\u0001\u0000\u0000" +
                    "\u0000\u009c\u009a\u0001\u0000\u0000\u0000\u009d\u009e\u0005%\u0000\u0000" +
                    "\u009e\u009f\u0005\u0001\u0000\u0000\u009f\u00a0\u0003\u0014\n\u0000\u00a0" +
                    "\u00a1\u0005\u0002\u0000\u0000\u00a1\u00af\u0001\u0000\u0000\u0000\u00a2" +
                    "\u00a3\u0005%\u0000\u0000\u00a3\u00a4\u0005\u0001\u0000\u0000\u00a4\u00a9" +
                    "\u0003\u0014\n\u0000\u00a5\u00a6\u0005\u0015\u0000\u0000\u00a6\u00a8\u0003" +
                    "\u0014\n\u0000\u00a7\u00a5\u0001\u0000\u0000\u0000\u00a8\u00ab\u0001\u0000" +
                    "\u0000\u0000\u00a9\u00a7\u0001\u0000\u0000\u0000\u00a9\u00aa\u0001\u0000" +
                    "\u0000\u0000\u00aa\u00ac\u0001\u0000\u0000\u0000\u00ab\u00a9\u0001\u0000" +
                    "\u0000\u0000\u00ac\u00ad\u0005\u0002\u0000\u0000\u00ad\u00af\u0001\u0000" +
                    "\u0000\u0000\u00ae\u009d\u0001\u0000\u0000\u0000\u00ae\u00a2\u0001\u0000" +
                    "\u0000\u0000\u00af\u0017\u0001\u0000\u0000\u0000\u00b0\u00b6\u0005\u0001" +
                    "\u0000\u0000\u00b1\u00b4\u0005\f\u0000\u0000\u00b2\u00b3\u0005\u0015\u0000" +
                    "\u0000\u00b3\u00b5\u0005\f\u0000\u0000\u00b4\u00b2\u0001\u0000\u0000\u0000" +
                    "\u00b4\u00b5\u0001\u0000\u0000\u0000\u00b5\u00b7\u0001\u0000\u0000\u0000" +
                    "\u00b6\u00b1\u0001\u0000\u0000\u0000\u00b6\u00b7\u0001\u0000\u0000\u0000" +
                    "\u00b7\u00b8\u0001\u0000\u0000\u0000\u00b8\u00b9\u0005\n\u0000\u0000\u00b9" +
                    "\u00ba\u0003\u0014\n\u0000\u00ba\u00bb\u0005\u0002\u0000\u0000\u00bb\u0019" +
                    "\u0001\u0000\u0000\u0000\u00bc\u00bd\u0005\u0001\u0000\u0000\u00bd\u00be" +
                    "\u0003\u001c\u000e\u0000\u00be\u00bf\u0005\u0002\u0000\u0000\u00bf\u00c0" +
                    "\u0005\n\u0000\u0000\u00c0\u00c1\u0003\u0014\n\u0000\u00c1\u001b\u0001" +
                    "\u0000\u0000\u0000\u00c2\u00c7\u0005\f\u0000\u0000\u00c3\u00c4\u0005\u0015" +
                    "\u0000\u0000\u00c4\u00c6\u0005\f\u0000\u0000\u00c5\u00c3\u0001\u0000\u0000" +
                    "\u0000\u00c6\u00c9\u0001\u0000\u0000\u0000\u00c7\u00c5\u0001\u0000\u0000" +
                    "\u0000\u00c7\u00c8\u0001\u0000\u0000\u0000\u00c8\u001d\u0001\u0000\u0000" +
                    "\u0000\u00c9\u00c7\u0001\u0000\u0000\u0000\u00ca\u00cb\u0007\u0001\u0000" +
                    "\u0000\u00cb\u001f\u0001\u0000\u0000\u0000\u00cc\u00d5\u0005\u0018\u0000" +
                    "\u0000\u00cd\u00d2\u0003\u0014\n\u0000\u00ce\u00cf\u0005\u0015\u0000\u0000" +
                    "\u00cf\u00d1\u0003\u0014\n\u0000\u00d0\u00ce\u0001\u0000\u0000\u0000\u00d1" +
                    "\u00d4\u0001\u0000\u0000\u0000\u00d2\u00d0\u0001\u0000\u0000\u0000\u00d2" +
                    "\u00d3\u0001\u0000\u0000\u0000\u00d3\u00d6\u0001\u0000\u0000\u0000\u00d4" +
                    "\u00d2\u0001\u0000\u0000\u0000\u00d5\u00cd\u0001\u0000\u0000\u0000\u00d5" +
                    "\u00d6\u0001\u0000\u0000\u0000\u00d6\u00d7\u0001\u0000\u0000\u0000\u00d7" +
                    "\u00d8\u0005\u0019\u0000\u0000\u00d8!\u0001\u0000\u0000\u0000\u00d9\u00e2" +
                    "\u0005\u0016\u0000\u0000\u00da\u00df\u0003$\u0012\u0000\u00db\u00dc\u0005" +
                    "\u0015\u0000\u0000\u00dc\u00de\u0003$\u0012\u0000\u00dd\u00db\u0001\u0000" +
                    "\u0000\u0000\u00de\u00e1\u0001\u0000\u0000\u0000\u00df\u00dd\u0001\u0000" +
                    "\u0000\u0000\u00df\u00e0\u0001\u0000\u0000\u0000\u00e0\u00e3\u0001\u0000" +
                    "\u0000\u0000\u00e1\u00df\u0001\u0000\u0000\u0000\u00e2\u00da\u0001\u0000" +
                    "\u0000\u0000\u00e2\u00e3\u0001\u0000\u0000\u0000\u00e3\u00e4\u0001\u0000" +
                    "\u0000\u0000\u00e4\u00e5\u0005\u0017\u0000\u0000\u00e5#\u0001\u0000\u0000" +
                    "\u0000\u00e6\u00e7\u0005\f\u0000\u0000\u00e7\u00e8\u0005\u0014\u0000\u0000" +
                    "\u00e8\u00e9\u0003\u0014\n\u0000\u00e9%\u0001\u0000\u0000\u0000\u00ea" +
                    "\u00eb\u0005\f\u0000\u0000\u00eb\u00f4\u0005\u0001\u0000\u0000\u00ec\u00f1" +
                    "\u0003\u0014\n\u0000\u00ed\u00ee\u0005\u0015\u0000\u0000\u00ee\u00f0\u0003" +
                    "\u0014\n\u0000\u00ef\u00ed\u0001\u0000\u0000\u0000\u00f0\u00f3\u0001\u0000" +
                    "\u0000\u0000\u00f1\u00ef\u0001\u0000\u0000\u0000\u00f1\u00f2\u0001\u0000" +
                    "\u0000\u0000\u00f2\u00f5\u0001\u0000\u0000\u0000\u00f3\u00f1\u0001\u0000" +
                    "\u0000\u0000\u00f4\u00ec\u0001\u0000\u0000\u0000\u00f4\u00f5\u0001\u0000" +
                    "\u0000\u0000\u00f5\u00f6\u0001\u0000\u0000\u0000\u00f6\u00f7\u0005\u0002" +
                    "\u0000\u0000\u00f7\'\u0001\u0000\u0000\u0000\u0017)-2;@Hajo~\u0098\u009a" +
                    "\u00a9\u00ae\u00b4\u00b6\u00c7\u00d2\u00d5\u00df\u00e2\u00f1\u00f4";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}