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
            T__0 = 1, T__1 = 2, T__2 = 3, VAR = 4, FUNCTION = 5, INPUT = 6, NAMESPACE = 7, OUTPUT = 8,
            DW = 9, ASSIGN = 10, ARROW = 11, BOOLEAN = 12, IDENTIFIER = 13, NUMBER = 14, STRING = 15,
            DATE = 16, REGEX = 17, DOT = 18, COLON = 19, COMMA = 20, LCURLY = 21, RCURLY = 22, LSQUARE = 23,
            RSQUARE = 24, SEPARATOR = 25, WS = 26, NEWLINE = 27, COMMENT = 28, OPERATOR_MATH = 29,
            OPERATOR_COMPARISON = 30, OPERATOR_LOGICAL = 31, OPERATOR_BITWISE = 32, OPERATOR_CONDITIONAL = 33,
            OPERATOR_RANGE = 34, OPERATOR_CHAIN = 35, BUILTIN_FUNCTION = 36;
    public static final int
            RULE_script = 0, RULE_header = 1, RULE_directive = 2, RULE_dwVersion = 3,
            RULE_outputDirective = 4, RULE_inputDirective = 5, RULE_namespaceDirective = 6,
            RULE_variableDeclaration = 7, RULE_functionDeclaration = 8, RULE_body = 9,
            RULE_expression = 10, RULE_builtInFunctionCall = 11, RULE_inlineLambda = 12,
            RULE_literal = 13, RULE_array = 14, RULE_object = 15, RULE_keyValue = 16,
            RULE_functionCall = 17;

    private static String[] makeRuleNames() {
        return new String[]{
                "script", "header", "directive", "dwVersion", "outputDirective", "inputDirective",
                "namespaceDirective", "variableDeclaration", "functionDeclaration", "body",
                "expression", "builtInFunctionCall", "inlineLambda", "literal", "array",
                "object", "keyValue", "functionCall"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'/'", "'('", "')'", "'%var'", "'%function'", "'%input'", "'%namespace'",
                "'%output'", "'%dw'", "'='", "'->'", null, null, null, null, null, null,
                "'.'", "':'", "','", "'{'", "'}'", "'['", "']'", "'---'", null, null,
                null, null, null, null, null, null, "'..'", "'++'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, null, null, null, "VAR", "FUNCTION", "INPUT", "NAMESPACE", "OUTPUT",
                "DW", "ASSIGN", "ARROW", "BOOLEAN", "IDENTIFIER", "NUMBER", "STRING",
                "DATE", "REGEX", "DOT", "COLON", "COMMA", "LCURLY", "RCURLY", "LSQUARE",
                "RSQUARE", "SEPARATOR", "WS", "NEWLINE", "COMMENT", "OPERATOR_MATH",
                "OPERATOR_COMPARISON", "OPERATOR_LOGICAL", "OPERATOR_BITWISE", "OPERATOR_CONDITIONAL",
                "OPERATOR_RANGE", "OPERATOR_CHAIN", "BUILTIN_FUNCTION"
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
                setState(37);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1008L) != 0)) {
                    {
                        setState(36);
                        header();
                    }
                }

                setState(39);
                match(SEPARATOR);
                setState(41);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 68730220548L) != 0)) {
                    {
                        setState(40);
                        body();
                    }
                }

                setState(46);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == NEWLINE) {
                    {
                        {
                            setState(43);
                            match(NEWLINE);
                        }
                    }
                    setState(48);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
                setState(49);
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
                setState(58);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(51);
                            directive();
                            setState(55);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                            while (_la == WS || _la == NEWLINE) {
                                {
                                    {
                                        setState(52);
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
                                setState(57);
                                _errHandler.sync(this);
                                _la = _input.LA(1);
                            }
                        }
                    }
                    setState(60);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1008L) != 0));
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
            setState(68);
            _errHandler.sync(this);
            switch (_input.LA(1)) {
                case DW:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(62);
                    dwVersion();
                }
                break;
                case OUTPUT:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(63);
                    outputDirective();
                }
                break;
                case INPUT:
                    enterOuterAlt(_localctx, 3);
                {
                    setState(64);
                    inputDirective();
                }
                break;
                case NAMESPACE:
                    enterOuterAlt(_localctx, 4);
                {
                    setState(65);
                    namespaceDirective();
                }
                break;
                case VAR:
                    enterOuterAlt(_localctx, 5);
                {
                    setState(66);
                    variableDeclaration();
                }
                break;
                case FUNCTION:
                    enterOuterAlt(_localctx, 6);
                {
                    setState(67);
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
                setState(70);
                match(DW);
                setState(71);
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

        public List<TerminalNode> IDENTIFIER() {
            return getTokens(DataWeaveParser.IDENTIFIER);
        }

        public TerminalNode IDENTIFIER(int i) {
            return getToken(DataWeaveParser.IDENTIFIER, i);
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
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(73);
                match(OUTPUT);
                setState(74);
                match(IDENTIFIER);
                setState(77);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == T__0) {
                    {
                        setState(75);
                        match(T__0);
                        setState(76);
                        match(IDENTIFIER);
                    }
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
    public static class InputDirectiveContext extends ParserRuleContext {
        public TerminalNode INPUT() {
            return getToken(DataWeaveParser.INPUT, 0);
        }

        public List<TerminalNode> IDENTIFIER() {
            return getTokens(DataWeaveParser.IDENTIFIER);
        }

        public TerminalNode IDENTIFIER(int i) {
            return getToken(DataWeaveParser.IDENTIFIER, i);
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
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(79);
                match(INPUT);
                setState(80);
                match(IDENTIFIER);
                setState(81);
                match(IDENTIFIER);
                setState(84);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == T__0) {
                    {
                        setState(82);
                        match(T__0);
                        setState(83);
                        match(IDENTIFIER);
                    }
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
    public static class NamespaceDirectiveContext extends ParserRuleContext {
        public TerminalNode NAMESPACE() {
            return getToken(DataWeaveParser.NAMESPACE, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(DataWeaveParser.IDENTIFIER, 0);
        }

        public TerminalNode STRING() {
            return getToken(DataWeaveParser.STRING, 0);
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
                setState(86);
                match(NAMESPACE);
                setState(87);
                match(IDENTIFIER);
                setState(88);
                match(STRING);
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
                setState(90);
                match(VAR);
                setState(91);
                match(IDENTIFIER);
                setState(92);
                match(ASSIGN);
                setState(93);
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

        public TerminalNode ARROW() {
            return getToken(DataWeaveParser.ARROW, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
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
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(95);
                match(FUNCTION);
                setState(96);
                match(IDENTIFIER);
                setState(97);
                match(ARROW);
                setState(98);
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
                setState(100);
                expression(0);
                setState(109);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 9, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        {
                            {
                                setState(102);
                                _errHandler.sync(this);
                                _la = _input.LA(1);
                                do {
                                    {
                                        {
                                            setState(101);
                                            match(NEWLINE);
                                        }
                                    }
                                    setState(104);
                                    _errHandler.sync(this);
                                    _la = _input.LA(1);
                                } while (_la == NEWLINE);
                                setState(106);
                                expression(0);
                            }
                        }
                    }
                    setState(111);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 9, _ctx);
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
                setState(123);
                _errHandler.sync(this);
                switch (getInterpreter().adaptivePredict(_input, 10, _ctx)) {
                    case 1: {
                        _localctx = new BuiltInFunctionExpressionContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;

                        setState(113);
                        builtInFunctionCall();
                    }
                    break;
                    case 2: {
                        _localctx = new FunctionCallExpressionContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(114);
                        functionCall();
                    }
                    break;
                    case 3: {
                        _localctx = new LiteralExpressionContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(115);
                        literal();
                    }
                    break;
                    case 4: {
                        _localctx = new ArrayExpressionContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(116);
                        array();
                    }
                    break;
                    case 5: {
                        _localctx = new ObjectExpressionContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(117);
                        object();
                    }
                    break;
                    case 6: {
                        _localctx = new IdentifierExpressionContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(118);
                        match(IDENTIFIER);
                    }
                    break;
                    case 7: {
                        _localctx = new GroupedExpressionContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(119);
                        match(T__1);
                        setState(120);
                        expression(0);
                        setState(121);
                        match(T__2);
                    }
                    break;
                }
                _ctx.stop = _input.LT(-1);
                setState(151);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 12, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        if (_parseListeners != null) triggerExitRuleEvent();
                        _prevctx = _localctx;
                        {
                            setState(149);
                            _errHandler.sync(this);
                            switch (getInterpreter().adaptivePredict(_input, 11, _ctx)) {
                                case 1: {
                                    _localctx = new ConditionalExpressionContext(new ExpressionContext(_parentctx, _parentState));
                                    pushNewRecursionContext(_localctx, _startState, RULE_expression);
                                    setState(125);
                                    if (!(precpred(_ctx, 13)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 13)");
                                    setState(126);
                                    match(OPERATOR_CONDITIONAL);
                                    setState(127);
                                    expression(0);
                                    setState(128);
                                    match(COLON);
                                    setState(129);
                                    expression(14);
                                }
                                break;
                                case 2: {
                                    _localctx = new LogicalExpressionContext(new ExpressionContext(_parentctx, _parentState));
                                    pushNewRecursionContext(_localctx, _startState, RULE_expression);
                                    setState(131);
                                    if (!(precpred(_ctx, 12)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 12)");
                                    setState(132);
                                    match(OPERATOR_LOGICAL);
                                    setState(133);
                                    expression(13);
                                }
                                break;
                                case 3: {
                                    _localctx = new ComparisonExpressionContext(new ExpressionContext(_parentctx, _parentState));
                                    pushNewRecursionContext(_localctx, _startState, RULE_expression);
                                    setState(134);
                                    if (!(precpred(_ctx, 11)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 11)");
                                    setState(135);
                                    match(OPERATOR_COMPARISON);
                                    setState(136);
                                    expression(12);
                                }
                                break;
                                case 4: {
                                    _localctx = new BitwiseExpressionContext(new ExpressionContext(_parentctx, _parentState));
                                    pushNewRecursionContext(_localctx, _startState, RULE_expression);
                                    setState(137);
                                    if (!(precpred(_ctx, 10)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 10)");
                                    setState(138);
                                    match(OPERATOR_BITWISE);
                                    setState(139);
                                    expression(11);
                                }
                                break;
                                case 5: {
                                    _localctx = new MathExpressionContext(new ExpressionContext(_parentctx, _parentState));
                                    pushNewRecursionContext(_localctx, _startState, RULE_expression);
                                    setState(140);
                                    if (!(precpred(_ctx, 9)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 9)");
                                    setState(141);
                                    match(OPERATOR_MATH);
                                    setState(142);
                                    expression(10);
                                }
                                break;
                                case 6: {
                                    _localctx = new RangeExpressionContext(new ExpressionContext(_parentctx, _parentState));
                                    pushNewRecursionContext(_localctx, _startState, RULE_expression);
                                    setState(143);
                                    if (!(precpred(_ctx, 8)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 8)");
                                    setState(144);
                                    match(OPERATOR_RANGE);
                                    setState(145);
                                    expression(9);
                                }
                                break;
                                case 7: {
                                    _localctx = new ChainExpressionContext(new ExpressionContext(_parentctx, _parentState));
                                    pushNewRecursionContext(_localctx, _startState, RULE_expression);
                                    setState(146);
                                    if (!(precpred(_ctx, 7)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 7)");
                                    setState(147);
                                    match(OPERATOR_CHAIN);
                                    setState(148);
                                    expression(8);
                                }
                                break;
                            }
                        }
                    }
                    setState(153);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 12, _ctx);
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
            setState(171);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 14, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(154);
                    match(BUILTIN_FUNCTION);
                    setState(155);
                    match(T__1);
                    setState(156);
                    expression(0);
                    setState(157);
                    match(T__2);
                }
                break;
                case 2:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(159);
                    match(BUILTIN_FUNCTION);
                    setState(160);
                    match(T__1);
                    setState(161);
                    expression(0);
                    setState(166);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                    while (_la == COMMA) {
                        {
                            {
                                setState(162);
                                match(COMMA);
                                setState(163);
                                expression(0);
                            }
                        }
                        setState(168);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                    }
                    setState(169);
                    match(T__2);
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
        enterRule(_localctx, 24, RULE_inlineLambda);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(173);
                match(T__1);
                setState(179);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == IDENTIFIER) {
                    {
                        setState(174);
                        match(IDENTIFIER);
                        setState(177);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        if (_la == COMMA) {
                            {
                                setState(175);
                                match(COMMA);
                                setState(176);
                                match(IDENTIFIER);
                            }
                        }

                    }
                }

                setState(181);
                match(ARROW);
                setState(182);
                expression(0);
                setState(183);
                match(T__2);
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
        enterRule(_localctx, 26, RULE_literal);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(185);
                _la = _input.LA(1);
                if (!((((_la) & ~0x3f) == 0 && ((1L << _la) & 249856L) != 0))) {
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
        enterRule(_localctx, 28, RULE_array);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(187);
                match(LSQUARE);
                setState(196);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 68730220548L) != 0)) {
                    {
                        setState(188);
                        expression(0);
                        setState(193);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        while (_la == COMMA) {
                            {
                                {
                                    setState(189);
                                    match(COMMA);
                                    setState(190);
                                    expression(0);
                                }
                            }
                            setState(195);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        }
                    }
                }

                setState(198);
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
        enterRule(_localctx, 30, RULE_object);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(200);
                match(LCURLY);
                setState(209);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == IDENTIFIER) {
                    {
                        setState(201);
                        keyValue();
                        setState(206);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        while (_la == COMMA) {
                            {
                                {
                                    setState(202);
                                    match(COMMA);
                                    setState(203);
                                    keyValue();
                                }
                            }
                            setState(208);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        }
                    }
                }

                setState(211);
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
        enterRule(_localctx, 32, RULE_keyValue);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(213);
                match(IDENTIFIER);
                setState(214);
                match(COLON);
                setState(215);
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
        enterRule(_localctx, 34, RULE_functionCall);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(217);
                match(IDENTIFIER);
                setState(218);
                match(T__1);
                setState(227);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 68730220548L) != 0)) {
                    {
                        setState(219);
                        expression(0);
                        setState(224);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        while (_la == COMMA) {
                            {
                                {
                                    setState(220);
                                    match(COMMA);
                                    setState(221);
                                    expression(0);
                                }
                            }
                            setState(226);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        }
                    }
                }

                setState(229);
                match(T__2);
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
                return precpred(_ctx, 13);
            case 1:
                return precpred(_ctx, 12);
            case 2:
                return precpred(_ctx, 11);
            case 3:
                return precpred(_ctx, 10);
            case 4:
                return precpred(_ctx, 9);
            case 5:
                return precpred(_ctx, 8);
            case 6:
                return precpred(_ctx, 7);
        }
        return true;
    }

    public static final String _serializedATN =
            "\u0004\u0001$\u00e8\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002" +
                    "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002" +
                    "\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002" +
                    "\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002" +
                    "\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f" +
                    "\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0001\u0000\u0003\u0000" +
                    "&\b\u0000\u0001\u0000\u0001\u0000\u0003\u0000*\b\u0000\u0001\u0000\u0005" +
                    "\u0000-\b\u0000\n\u0000\f\u00000\t\u0000\u0001\u0000\u0001\u0000\u0001" +
                    "\u0001\u0001\u0001\u0005\u00016\b\u0001\n\u0001\f\u00019\t\u0001\u0004" +
                    "\u0001;\b\u0001\u000b\u0001\f\u0001<\u0001\u0002\u0001\u0002\u0001\u0002" +
                    "\u0001\u0002\u0001\u0002\u0001\u0002\u0003\u0002E\b\u0002\u0001\u0003" +
                    "\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004" +
                    "\u0003\u0004N\b\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005" +
                    "\u0001\u0005\u0003\u0005U\b\u0005\u0001\u0006\u0001\u0006\u0001\u0006" +
                    "\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007" +
                    "\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0004\tg\b\t" +
                    "\u000b\t\f\th\u0001\t\u0005\tl\b\t\n\t\f\to\t\t\u0001\n\u0001\n\u0001" +
                    "\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0003" +
                    "\n|\b\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001" +
                    "\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001" +
                    "\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0005\n\u0096" +
                    "\b\n\n\n\f\n\u0099\t\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b" +
                    "\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b" +
                    "\u0005\u000b\u00a5\b\u000b\n\u000b\f\u000b\u00a8\t\u000b\u0001\u000b\u0001" +
                    "\u000b\u0003\u000b\u00ac\b\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0003" +
                    "\f\u00b2\b\f\u0003\f\u00b4\b\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\r" +
                    "\u0001\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0005\u000e\u00c0" +
                    "\b\u000e\n\u000e\f\u000e\u00c3\t\u000e\u0003\u000e\u00c5\b\u000e\u0001" +
                    "\u000e\u0001\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0005" +
                    "\u000f\u00cd\b\u000f\n\u000f\f\u000f\u00d0\t\u000f\u0003\u000f\u00d2\b" +
                    "\u000f\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0010\u0001" +
                    "\u0010\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0005" +
                    "\u0011\u00df\b\u0011\n\u0011\f\u0011\u00e2\t\u0011\u0003\u0011\u00e4\b" +
                    "\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0000\u0001\u0014\u0012\u0000" +
                    "\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c" +
                    "\u001e \"\u0000\u0002\u0001\u0000\u001a\u001b\u0002\u0000\f\f\u000e\u0011" +
                    "\u00fa\u0000%\u0001\u0000\u0000\u0000\u0002:\u0001\u0000\u0000\u0000\u0004" +
                    "D\u0001\u0000\u0000\u0000\u0006F\u0001\u0000\u0000\u0000\bI\u0001\u0000" +
                    "\u0000\u0000\nO\u0001\u0000\u0000\u0000\fV\u0001\u0000\u0000\u0000\u000e" +
                    "Z\u0001\u0000\u0000\u0000\u0010_\u0001\u0000\u0000\u0000\u0012d\u0001" +
                    "\u0000\u0000\u0000\u0014{\u0001\u0000\u0000\u0000\u0016\u00ab\u0001\u0000" +
                    "\u0000\u0000\u0018\u00ad\u0001\u0000\u0000\u0000\u001a\u00b9\u0001\u0000" +
                    "\u0000\u0000\u001c\u00bb\u0001\u0000\u0000\u0000\u001e\u00c8\u0001\u0000" +
                    "\u0000\u0000 \u00d5\u0001\u0000\u0000\u0000\"\u00d9\u0001\u0000\u0000" +
                    "\u0000$&\u0003\u0002\u0001\u0000%$\u0001\u0000\u0000\u0000%&\u0001\u0000" +
                    "\u0000\u0000&\'\u0001\u0000\u0000\u0000\')\u0005\u0019\u0000\u0000(*\u0003" +
                    "\u0012\t\u0000)(\u0001\u0000\u0000\u0000)*\u0001\u0000\u0000\u0000*.\u0001" +
                    "\u0000\u0000\u0000+-\u0005\u001b\u0000\u0000,+\u0001\u0000\u0000\u0000" +
                    "-0\u0001\u0000\u0000\u0000.,\u0001\u0000\u0000\u0000./\u0001\u0000\u0000" +
                    "\u0000/1\u0001\u0000\u0000\u00000.\u0001\u0000\u0000\u000012\u0005\u0000" +
                    "\u0000\u00012\u0001\u0001\u0000\u0000\u000037\u0003\u0004\u0002\u0000" +
                    "46\u0007\u0000\u0000\u000054\u0001\u0000\u0000\u000069\u0001\u0000\u0000" +
                    "\u000075\u0001\u0000\u0000\u000078\u0001\u0000\u0000\u00008;\u0001\u0000" +
                    "\u0000\u000097\u0001\u0000\u0000\u0000:3\u0001\u0000\u0000\u0000;<\u0001" +
                    "\u0000\u0000\u0000<:\u0001\u0000\u0000\u0000<=\u0001\u0000\u0000\u0000" +
                    "=\u0003\u0001\u0000\u0000\u0000>E\u0003\u0006\u0003\u0000?E\u0003\b\u0004" +
                    "\u0000@E\u0003\n\u0005\u0000AE\u0003\f\u0006\u0000BE\u0003\u000e\u0007" +
                    "\u0000CE\u0003\u0010\b\u0000D>\u0001\u0000\u0000\u0000D?\u0001\u0000\u0000" +
                    "\u0000D@\u0001\u0000\u0000\u0000DA\u0001\u0000\u0000\u0000DB\u0001\u0000" +
                    "\u0000\u0000DC\u0001\u0000\u0000\u0000E\u0005\u0001\u0000\u0000\u0000" +
                    "FG\u0005\t\u0000\u0000GH\u0005\u000e\u0000\u0000H\u0007\u0001\u0000\u0000" +
                    "\u0000IJ\u0005\b\u0000\u0000JM\u0005\r\u0000\u0000KL\u0005\u0001\u0000" +
                    "\u0000LN\u0005\r\u0000\u0000MK\u0001\u0000\u0000\u0000MN\u0001\u0000\u0000" +
                    "\u0000N\t\u0001\u0000\u0000\u0000OP\u0005\u0006\u0000\u0000PQ\u0005\r" +
                    "\u0000\u0000QT\u0005\r\u0000\u0000RS\u0005\u0001\u0000\u0000SU\u0005\r" +
                    "\u0000\u0000TR\u0001\u0000\u0000\u0000TU\u0001\u0000\u0000\u0000U\u000b" +
                    "\u0001\u0000\u0000\u0000VW\u0005\u0007\u0000\u0000WX\u0005\r\u0000\u0000" +
                    "XY\u0005\u000f\u0000\u0000Y\r\u0001\u0000\u0000\u0000Z[\u0005\u0004\u0000" +
                    "\u0000[\\\u0005\r\u0000\u0000\\]\u0005\n\u0000\u0000]^\u0003\u0014\n\u0000" +
                    "^\u000f\u0001\u0000\u0000\u0000_`\u0005\u0005\u0000\u0000`a\u0005\r\u0000" +
                    "\u0000ab\u0005\u000b\u0000\u0000bc\u0003\u0014\n\u0000c\u0011\u0001\u0000" +
                    "\u0000\u0000dm\u0003\u0014\n\u0000eg\u0005\u001b\u0000\u0000fe\u0001\u0000" +
                    "\u0000\u0000gh\u0001\u0000\u0000\u0000hf\u0001\u0000\u0000\u0000hi\u0001" +
                    "\u0000\u0000\u0000ij\u0001\u0000\u0000\u0000jl\u0003\u0014\n\u0000kf\u0001" +
                    "\u0000\u0000\u0000lo\u0001\u0000\u0000\u0000mk\u0001\u0000\u0000\u0000" +
                    "mn\u0001\u0000\u0000\u0000n\u0013\u0001\u0000\u0000\u0000om\u0001\u0000" +
                    "\u0000\u0000pq\u0006\n\uffff\uffff\u0000q|\u0003\u0016\u000b\u0000r|\u0003" +
                    "\"\u0011\u0000s|\u0003\u001a\r\u0000t|\u0003\u001c\u000e\u0000u|\u0003" +
                    "\u001e\u000f\u0000v|\u0005\r\u0000\u0000wx\u0005\u0002\u0000\u0000xy\u0003" +
                    "\u0014\n\u0000yz\u0005\u0003\u0000\u0000z|\u0001\u0000\u0000\u0000{p\u0001" +
                    "\u0000\u0000\u0000{r\u0001\u0000\u0000\u0000{s\u0001\u0000\u0000\u0000" +
                    "{t\u0001\u0000\u0000\u0000{u\u0001\u0000\u0000\u0000{v\u0001\u0000\u0000" +
                    "\u0000{w\u0001\u0000\u0000\u0000|\u0097\u0001\u0000\u0000\u0000}~\n\r" +
                    "\u0000\u0000~\u007f\u0005!\u0000\u0000\u007f\u0080\u0003\u0014\n\u0000" +
                    "\u0080\u0081\u0005\u0013\u0000\u0000\u0081\u0082\u0003\u0014\n\u000e\u0082" +
                    "\u0096\u0001\u0000\u0000\u0000\u0083\u0084\n\f\u0000\u0000\u0084\u0085" +
                    "\u0005\u001f\u0000\u0000\u0085\u0096\u0003\u0014\n\r\u0086\u0087\n\u000b" +
                    "\u0000\u0000\u0087\u0088\u0005\u001e\u0000\u0000\u0088\u0096\u0003\u0014" +
                    "\n\f\u0089\u008a\n\n\u0000\u0000\u008a\u008b\u0005 \u0000\u0000\u008b" +
                    "\u0096\u0003\u0014\n\u000b\u008c\u008d\n\t\u0000\u0000\u008d\u008e\u0005" +
                    "\u001d\u0000\u0000\u008e\u0096\u0003\u0014\n\n\u008f\u0090\n\b\u0000\u0000" +
                    "\u0090\u0091\u0005\"\u0000\u0000\u0091\u0096\u0003\u0014\n\t\u0092\u0093" +
                    "\n\u0007\u0000\u0000\u0093\u0094\u0005#\u0000\u0000\u0094\u0096\u0003" +
                    "\u0014\n\b\u0095}\u0001\u0000\u0000\u0000\u0095\u0083\u0001\u0000\u0000" +
                    "\u0000\u0095\u0086\u0001\u0000\u0000\u0000\u0095\u0089\u0001\u0000\u0000" +
                    "\u0000\u0095\u008c\u0001\u0000\u0000\u0000\u0095\u008f\u0001\u0000\u0000" +
                    "\u0000\u0095\u0092\u0001\u0000\u0000\u0000\u0096\u0099\u0001\u0000\u0000" +
                    "\u0000\u0097\u0095\u0001\u0000\u0000\u0000\u0097\u0098\u0001\u0000\u0000" +
                    "\u0000\u0098\u0015\u0001\u0000\u0000\u0000\u0099\u0097\u0001\u0000\u0000" +
                    "\u0000\u009a\u009b\u0005$\u0000\u0000\u009b\u009c\u0005\u0002\u0000\u0000" +
                    "\u009c\u009d\u0003\u0014\n\u0000\u009d\u009e\u0005\u0003\u0000\u0000\u009e" +
                    "\u00ac\u0001\u0000\u0000\u0000\u009f\u00a0\u0005$\u0000\u0000\u00a0\u00a1" +
                    "\u0005\u0002\u0000\u0000\u00a1\u00a6\u0003\u0014\n\u0000\u00a2\u00a3\u0005" +
                    "\u0014\u0000\u0000\u00a3\u00a5\u0003\u0014\n\u0000\u00a4\u00a2\u0001\u0000" +
                    "\u0000\u0000\u00a5\u00a8\u0001\u0000\u0000\u0000\u00a6\u00a4\u0001\u0000" +
                    "\u0000\u0000\u00a6\u00a7\u0001\u0000\u0000\u0000\u00a7\u00a9\u0001\u0000" +
                    "\u0000\u0000\u00a8\u00a6\u0001\u0000\u0000\u0000\u00a9\u00aa\u0005\u0003" +
                    "\u0000\u0000\u00aa\u00ac\u0001\u0000\u0000\u0000\u00ab\u009a\u0001\u0000" +
                    "\u0000\u0000\u00ab\u009f\u0001\u0000\u0000\u0000\u00ac\u0017\u0001\u0000" +
                    "\u0000\u0000\u00ad\u00b3\u0005\u0002\u0000\u0000\u00ae\u00b1\u0005\r\u0000" +
                    "\u0000\u00af\u00b0\u0005\u0014\u0000\u0000\u00b0\u00b2\u0005\r\u0000\u0000" +
                    "\u00b1\u00af\u0001\u0000\u0000\u0000\u00b1\u00b2\u0001\u0000\u0000\u0000" +
                    "\u00b2\u00b4\u0001\u0000\u0000\u0000\u00b3\u00ae\u0001\u0000\u0000\u0000" +
                    "\u00b3\u00b4\u0001\u0000\u0000\u0000\u00b4\u00b5\u0001\u0000\u0000\u0000" +
                    "\u00b5\u00b6\u0005\u000b\u0000\u0000\u00b6\u00b7\u0003\u0014\n\u0000\u00b7" +
                    "\u00b8\u0005\u0003\u0000\u0000\u00b8\u0019\u0001\u0000\u0000\u0000\u00b9" +
                    "\u00ba\u0007\u0001\u0000\u0000\u00ba\u001b\u0001\u0000\u0000\u0000\u00bb" +
                    "\u00c4\u0005\u0017\u0000\u0000\u00bc\u00c1\u0003\u0014\n\u0000\u00bd\u00be" +
                    "\u0005\u0014\u0000\u0000\u00be\u00c0\u0003\u0014\n\u0000\u00bf\u00bd\u0001" +
                    "\u0000\u0000\u0000\u00c0\u00c3\u0001\u0000\u0000\u0000\u00c1\u00bf\u0001" +
                    "\u0000\u0000\u0000\u00c1\u00c2\u0001\u0000\u0000\u0000\u00c2\u00c5\u0001" +
                    "\u0000\u0000\u0000\u00c3\u00c1\u0001\u0000\u0000\u0000\u00c4\u00bc\u0001" +
                    "\u0000\u0000\u0000\u00c4\u00c5\u0001\u0000\u0000\u0000\u00c5\u00c6\u0001" +
                    "\u0000\u0000\u0000\u00c6\u00c7\u0005\u0018\u0000\u0000\u00c7\u001d\u0001" +
                    "\u0000\u0000\u0000\u00c8\u00d1\u0005\u0015\u0000\u0000\u00c9\u00ce\u0003" +
                    " \u0010\u0000\u00ca\u00cb\u0005\u0014\u0000\u0000\u00cb\u00cd\u0003 \u0010" +
                    "\u0000\u00cc\u00ca\u0001\u0000\u0000\u0000\u00cd\u00d0\u0001\u0000\u0000" +
                    "\u0000\u00ce\u00cc\u0001\u0000\u0000\u0000\u00ce\u00cf\u0001\u0000\u0000" +
                    "\u0000\u00cf\u00d2\u0001\u0000\u0000\u0000\u00d0\u00ce\u0001\u0000\u0000" +
                    "\u0000\u00d1\u00c9\u0001\u0000\u0000\u0000\u00d1\u00d2\u0001\u0000\u0000" +
                    "\u0000\u00d2\u00d3\u0001\u0000\u0000\u0000\u00d3\u00d4\u0005\u0016\u0000" +
                    "\u0000\u00d4\u001f\u0001\u0000\u0000\u0000\u00d5\u00d6\u0005\r\u0000\u0000" +
                    "\u00d6\u00d7\u0005\u0013\u0000\u0000\u00d7\u00d8\u0003\u0014\n\u0000\u00d8" +
                    "!\u0001\u0000\u0000\u0000\u00d9\u00da\u0005\r\u0000\u0000\u00da\u00e3" +
                    "\u0005\u0002\u0000\u0000\u00db\u00e0\u0003\u0014\n\u0000\u00dc\u00dd\u0005" +
                    "\u0014\u0000\u0000\u00dd\u00df\u0003\u0014\n\u0000\u00de\u00dc\u0001\u0000" +
                    "\u0000\u0000\u00df\u00e2\u0001\u0000\u0000\u0000\u00e0\u00de\u0001\u0000" +
                    "\u0000\u0000\u00e0\u00e1\u0001\u0000\u0000\u0000\u00e1\u00e4\u0001\u0000" +
                    "\u0000\u0000\u00e2\u00e0\u0001\u0000\u0000\u0000\u00e3\u00db\u0001\u0000" +
                    "\u0000\u0000\u00e3\u00e4\u0001\u0000\u0000\u0000\u00e4\u00e5\u0001\u0000" +
                    "\u0000\u0000\u00e5\u00e6\u0005\u0003\u0000\u0000\u00e6#\u0001\u0000\u0000" +
                    "\u0000\u0017%).7<DMThm{\u0095\u0097\u00a6\u00ab\u00b1\u00b3\u00c1\u00c4" +
                    "\u00ce\u00d1\u00e0\u00e3";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}