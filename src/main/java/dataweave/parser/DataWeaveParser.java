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
            T__0 = 1, T__1 = 2, T__2 = 3, T__3 = 4, T__4 = 5, T__5 = 6, VAR = 7, FUNCTION = 8, INPUT = 9,
            NAMESPACE = 10, OUTPUT = 11, DW = 12, ASSIGN = 13, ARROW = 14, BOOLEAN = 15, IDENTIFIER = 16,
            URL = 17, MEDIA_TYPE = 18, NUMBER = 19, STRING = 20, DATE = 21, REGEX = 22, DOT = 23,
            COLON = 24, COMMA = 25, LCURLY = 26, RCURLY = 27, LSQUARE = 28, RSQUARE = 29, SEPARATOR = 30,
            WS = 31, NEWLINE = 32, COMMENT = 33, STAR = 34, DOUBLE_DOT = 35, AT = 36, QUESTION = 37,
            OPERATOR_MATH = 38, OPERATOR_COMPARISON = 39, OPERATOR_LOGICAL = 40, OPERATOR_BITWISE = 41,
            OPERATOR_CONDITIONAL = 42, OPERATOR_RANGE = 43, OPERATOR_CHAIN = 44, BUILTIN_FUNCTION = 45,
            INDEX_IDENTIFIER = 46, VALUE_IDENTIFIER = 47;
    public static final int
            RULE_script = 0, RULE_header = 1, RULE_directive = 2, RULE_dwVersion = 3,
            RULE_outputDirective = 4, RULE_inputDirective = 5, RULE_namespaceDirective = 6,
            RULE_variableDeclaration = 7, RULE_functionDeclaration = 8, RULE_body = 9,
            RULE_expression = 10, RULE_primaryExpression = 11, RULE_implicitLambdaExpression = 12,
            RULE_builtInFunctionCall = 13, RULE_inlineLambda = 14, RULE_functionParameters = 15,
            RULE_literal = 16, RULE_array = 17, RULE_object = 18, RULE_keyValue = 19,
            RULE_functionCall = 20, RULE_grouped = 21;

    private static String[] makeRuleNames() {
        return new String[]{
                "script", "header", "directive", "dwVersion", "outputDirective", "inputDirective",
                "namespaceDirective", "variableDeclaration", "functionDeclaration", "body",
                "expression", "primaryExpression", "implicitLambdaExpression", "builtInFunctionCall",
                "inlineLambda", "functionParameters", "literal", "array", "object", "keyValue",
                "functionCall", "grouped"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'('", "')'", "'map'", "'sizeOf'", "'upper'", "'lower'", "'%var'",
                "'%function'", "'%input'", "'%namespace'", "'%output'", "'%dw'", "'='",
                "'->'", null, null, null, null, null, null, null, null, "'.'", "':'",
                "','", "'{'", "'}'", "'['", "']'", "'---'", null, null, null, "'*'",
                "'..'", "'@'", "'?'", null, null, null, null, null, null, "'++'", null,
                "'$$'", "'$'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, null, null, null, null, null, null, "VAR", "FUNCTION", "INPUT",
                "NAMESPACE", "OUTPUT", "DW", "ASSIGN", "ARROW", "BOOLEAN", "IDENTIFIER",
                "URL", "MEDIA_TYPE", "NUMBER", "STRING", "DATE", "REGEX", "DOT", "COLON",
                "COMMA", "LCURLY", "RCURLY", "LSQUARE", "RSQUARE", "SEPARATOR", "WS",
                "NEWLINE", "COMMENT", "STAR", "DOUBLE_DOT", "AT", "QUESTION", "OPERATOR_MATH",
                "OPERATOR_COMPARISON", "OPERATOR_LOGICAL", "OPERATOR_BITWISE", "OPERATOR_CONDITIONAL",
                "OPERATOR_RANGE", "OPERATOR_CHAIN", "BUILTIN_FUNCTION", "INDEX_IDENTIFIER",
                "VALUE_IDENTIFIER"
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
                setState(45);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 8064L) != 0)) {
                    {
                        setState(44);
                        header();
                    }
                }

                setState(47);
                match(SEPARATOR);
                setState(49);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 343507058L) != 0)) {
                    {
                        setState(48);
                        body();
                    }
                }

                setState(54);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == NEWLINE) {
                    {
                        {
                            setState(51);
                            match(NEWLINE);
                        }
                    }
                    setState(56);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
                setState(57);
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
                setState(66);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(59);
                            directive();
                            setState(63);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                            while (_la == WS || _la == NEWLINE) {
                                {
                                    {
                                        setState(60);
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
                                setState(65);
                                _errHandler.sync(this);
                                _la = _input.LA(1);
                            }
                        }
                    }
                    setState(68);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 8064L) != 0));
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
            setState(76);
            _errHandler.sync(this);
            switch (_input.LA(1)) {
                case DW:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(70);
                    dwVersion();
                }
                break;
                case OUTPUT:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(71);
                    outputDirective();
                }
                break;
                case INPUT:
                    enterOuterAlt(_localctx, 3);
                {
                    setState(72);
                    inputDirective();
                }
                break;
                case NAMESPACE:
                    enterOuterAlt(_localctx, 4);
                {
                    setState(73);
                    namespaceDirective();
                }
                break;
                case VAR:
                    enterOuterAlt(_localctx, 5);
                {
                    setState(74);
                    variableDeclaration();
                }
                break;
                case FUNCTION:
                    enterOuterAlt(_localctx, 6);
                {
                    setState(75);
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
                setState(78);
                match(DW);
                setState(79);
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
                setState(81);
                match(OUTPUT);
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
                setState(84);
                match(INPUT);
                setState(85);
                match(IDENTIFIER);
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
                setState(88);
                match(NAMESPACE);
                setState(89);
                match(IDENTIFIER);
                setState(90);
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
                setState(92);
                match(VAR);
                setState(93);
                match(IDENTIFIER);
                setState(94);
                match(ASSIGN);
                setState(95);
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
                setState(97);
                match(FUNCTION);
                setState(98);
                match(IDENTIFIER);
                setState(99);
                match(T__0);
                setState(101);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == IDENTIFIER) {
                    {
                        setState(100);
                        functionParameters();
                    }
                }

                setState(103);
                match(T__1);
                setState(104);
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
                setState(106);
                expression(0);
                setState(110);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 7, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        {
                            {
                                setState(107);
                                match(NEWLINE);
                            }
                        }
                    }
                    setState(112);
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
    public static class MapExpressionContext extends ExpressionContext {
        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public ImplicitLambdaExpressionContext implicitLambdaExpression() {
            return getRuleContext(ImplicitLambdaExpressionContext.class, 0);
        }

        public MapExpressionContext(ExpressionContext ctx) {
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
                {
                    _localctx = new PrimaryExpressionWrapperContext(_localctx);
                    _ctx = _localctx;
                    _prevctx = _localctx;

                    setState(114);
                    primaryExpression(0);
                }
                _ctx.stop = _input.LT(-1);
                setState(145);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 9, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        if (_parseListeners != null) triggerExitRuleEvent();
                        _prevctx = _localctx;
                        {
                            setState(143);
                            _errHandler.sync(this);
                            switch (getInterpreter().adaptivePredict(_input, 8, _ctx)) {
                                case 1: {
                                    _localctx = new ConditionalExpressionContext(new ExpressionContext(_parentctx, _parentState));
                                    pushNewRecursionContext(_localctx, _startState, RULE_expression);
                                    setState(116);
                                    if (!(precpred(_ctx, 8)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 8)");
                                    setState(117);
                                    match(OPERATOR_CONDITIONAL);
                                    setState(118);
                                    expression(0);
                                    setState(119);
                                    match(COLON);
                                    setState(120);
                                    expression(9);
                                }
                                break;
                                case 2: {
                                    _localctx = new LogicalExpressionContext(new ExpressionContext(_parentctx, _parentState));
                                    pushNewRecursionContext(_localctx, _startState, RULE_expression);
                                    setState(122);
                                    if (!(precpred(_ctx, 7)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 7)");
                                    setState(123);
                                    match(OPERATOR_LOGICAL);
                                    setState(124);
                                    expression(8);
                                }
                                break;
                                case 3: {
                                    _localctx = new ComparisonExpressionContext(new ExpressionContext(_parentctx, _parentState));
                                    pushNewRecursionContext(_localctx, _startState, RULE_expression);
                                    setState(125);
                                    if (!(precpred(_ctx, 6)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 6)");
                                    setState(126);
                                    match(OPERATOR_COMPARISON);
                                    setState(127);
                                    expression(7);
                                }
                                break;
                                case 4: {
                                    _localctx = new BitwiseExpressionContext(new ExpressionContext(_parentctx, _parentState));
                                    pushNewRecursionContext(_localctx, _startState, RULE_expression);
                                    setState(128);
                                    if (!(precpred(_ctx, 5)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 5)");
                                    setState(129);
                                    match(OPERATOR_BITWISE);
                                    setState(130);
                                    expression(6);
                                }
                                break;
                                case 5: {
                                    _localctx = new MathExpressionContext(new ExpressionContext(_parentctx, _parentState));
                                    pushNewRecursionContext(_localctx, _startState, RULE_expression);
                                    setState(131);
                                    if (!(precpred(_ctx, 4)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 4)");
                                    setState(132);
                                    match(OPERATOR_MATH);
                                    setState(133);
                                    expression(5);
                                }
                                break;
                                case 6: {
                                    _localctx = new RangeExpressionContext(new ExpressionContext(_parentctx, _parentState));
                                    pushNewRecursionContext(_localctx, _startState, RULE_expression);
                                    setState(134);
                                    if (!(precpred(_ctx, 3)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 3)");
                                    setState(135);
                                    match(OPERATOR_RANGE);
                                    setState(136);
                                    expression(4);
                                }
                                break;
                                case 7: {
                                    _localctx = new ChainExpressionContext(new ExpressionContext(_parentctx, _parentState));
                                    pushNewRecursionContext(_localctx, _startState, RULE_expression);
                                    setState(137);
                                    if (!(precpred(_ctx, 2)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 2)");
                                    setState(138);
                                    match(OPERATOR_CHAIN);
                                    setState(139);
                                    expression(3);
                                }
                                break;
                                case 8: {
                                    _localctx = new MapExpressionContext(new ExpressionContext(_parentctx, _parentState));
                                    pushNewRecursionContext(_localctx, _startState, RULE_expression);
                                    setState(140);
                                    if (!(precpred(_ctx, 1)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 1)");
                                    setState(141);
                                    match(T__2);
                                    setState(142);
                                    implicitLambdaExpression();
                                }
                                break;
                            }
                        }
                    }
                    setState(147);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 9, _ctx);
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
    public static class ArrayExpressionContext extends PrimaryExpressionContext {
        public ArrayContext array() {
            return getRuleContext(ArrayContext.class, 0);
        }

        public ArrayExpressionContext(PrimaryExpressionContext ctx) {
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
    public static class IdentifierExpressionContext extends PrimaryExpressionContext {
        public TerminalNode IDENTIFIER() {
            return getToken(DataWeaveParser.IDENTIFIER, 0);
        }

        public IdentifierExpressionContext(PrimaryExpressionContext ctx) {
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
    public static class IndexedSelectorContext extends PrimaryExpressionContext {
        public PrimaryExpressionContext primaryExpression() {
            return getRuleContext(PrimaryExpressionContext.class, 0);
        }

        public TerminalNode LSQUARE() {
            return getToken(DataWeaveParser.LSQUARE, 0);
        }

        public TerminalNode NUMBER() {
            return getToken(DataWeaveParser.NUMBER, 0);
        }

        public TerminalNode RSQUARE() {
            return getToken(DataWeaveParser.RSQUARE, 0);
        }

        public IndexedSelectorContext(PrimaryExpressionContext ctx) {
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
    public static class AttributeSelectorContext extends PrimaryExpressionContext {
        public PrimaryExpressionContext primaryExpression() {
            return getRuleContext(PrimaryExpressionContext.class, 0);
        }

        public TerminalNode DOT() {
            return getToken(DataWeaveParser.DOT, 0);
        }

        public TerminalNode AT() {
            return getToken(DataWeaveParser.AT, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(DataWeaveParser.IDENTIFIER, 0);
        }

        public AttributeSelectorContext(PrimaryExpressionContext ctx) {
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
    public static class DescendantsSelectorContext extends PrimaryExpressionContext {
        public PrimaryExpressionContext primaryExpression() {
            return getRuleContext(PrimaryExpressionContext.class, 0);
        }

        public TerminalNode OPERATOR_RANGE() {
            return getToken(DataWeaveParser.OPERATOR_RANGE, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(DataWeaveParser.IDENTIFIER, 0);
        }

        public DescendantsSelectorContext(PrimaryExpressionContext ctx) {
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

    @SuppressWarnings("CheckReturnValue")
    public static class ObjectExpressionContext extends PrimaryExpressionContext {
        public ObjectContext object() {
            return getRuleContext(ObjectContext.class, 0);
        }

        public ObjectExpressionContext(PrimaryExpressionContext ctx) {
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
    public static class ExistenceQuerySelectorContext extends PrimaryExpressionContext {
        public PrimaryExpressionContext primaryExpression() {
            return getRuleContext(PrimaryExpressionContext.class, 0);
        }

        public TerminalNode QUESTION() {
            return getToken(DataWeaveParser.QUESTION, 0);
        }

        public ExistenceQuerySelectorContext(PrimaryExpressionContext ctx) {
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
    public static class SingleValueSelectorContext extends PrimaryExpressionContext {
        public PrimaryExpressionContext primaryExpression() {
            return getRuleContext(PrimaryExpressionContext.class, 0);
        }

        public TerminalNode DOT() {
            return getToken(DataWeaveParser.DOT, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(DataWeaveParser.IDENTIFIER, 0);
        }

        public SingleValueSelectorContext(PrimaryExpressionContext ctx) {
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
    public static class MultiValueSelectorContext extends PrimaryExpressionContext {
        public PrimaryExpressionContext primaryExpression() {
            return getRuleContext(PrimaryExpressionContext.class, 0);
        }

        public TerminalNode DOT() {
            return getToken(DataWeaveParser.DOT, 0);
        }

        public TerminalNode STAR() {
            return getToken(DataWeaveParser.STAR, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(DataWeaveParser.IDENTIFIER, 0);
        }

        public MultiValueSelectorContext(PrimaryExpressionContext ctx) {
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
    public static class LiteralExpressionContext extends PrimaryExpressionContext {
        public LiteralContext literal() {
            return getRuleContext(LiteralContext.class, 0);
        }

        public LiteralExpressionContext(PrimaryExpressionContext ctx) {
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

    public final PrimaryExpressionContext primaryExpression() throws RecognitionException {
        return primaryExpression(0);
    }

    private PrimaryExpressionContext primaryExpression(int _p) throws RecognitionException {
        ParserRuleContext _parentctx = _ctx;
        int _parentState = getState();
        PrimaryExpressionContext _localctx = new PrimaryExpressionContext(_ctx, _parentState);
        PrimaryExpressionContext _prevctx = _localctx;
        int _startState = 22;
        enterRecursionRule(_localctx, 22, RULE_primaryExpression, _p);
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                setState(180);
                _errHandler.sync(this);
                switch (getInterpreter().adaptivePredict(_input, 13, _ctx)) {
                    case 1: {
                        _localctx = new FunctionCallExpressionContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;

                        setState(149);
                        functionCall();
                    }
                    break;
                    case 2: {
                        _localctx = new SizeOfExpressionContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(150);
                        match(T__3);
                        setState(156);
                        _errHandler.sync(this);
                        switch (getInterpreter().adaptivePredict(_input, 10, _ctx)) {
                            case 1: {
                                setState(151);
                                match(T__0);
                                {
                                    setState(152);
                                    expression(0);
                                }
                                setState(153);
                                match(T__1);
                            }
                            break;
                            case 2: {
                                setState(155);
                                expression(0);
                            }
                            break;
                        }
                    }
                    break;
                    case 3: {
                        _localctx = new UpperExpressionContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(158);
                        match(T__4);
                        setState(164);
                        _errHandler.sync(this);
                        switch (getInterpreter().adaptivePredict(_input, 11, _ctx)) {
                            case 1: {
                                setState(159);
                                match(T__0);
                                {
                                    setState(160);
                                    expression(0);
                                }
                                setState(161);
                                match(T__1);
                            }
                            break;
                            case 2: {
                                setState(163);
                                expression(0);
                            }
                            break;
                        }
                    }
                    break;
                    case 4: {
                        _localctx = new LowerExpressionContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(166);
                        match(T__5);
                        setState(172);
                        _errHandler.sync(this);
                        switch (getInterpreter().adaptivePredict(_input, 12, _ctx)) {
                            case 1: {
                                setState(167);
                                match(T__0);
                                {
                                    setState(168);
                                    expression(0);
                                }
                                setState(169);
                                match(T__1);
                            }
                            break;
                            case 2: {
                                setState(171);
                                expression(0);
                            }
                            break;
                        }
                    }
                    break;
                    case 5: {
                        _localctx = new LambdaExpressionContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(174);
                        inlineLambda();
                    }
                    break;
                    case 6: {
                        _localctx = new LiteralExpressionContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(175);
                        literal();
                    }
                    break;
                    case 7: {
                        _localctx = new ArrayExpressionContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(176);
                        array();
                    }
                    break;
                    case 8: {
                        _localctx = new ObjectExpressionContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(177);
                        object();
                    }
                    break;
                    case 9: {
                        _localctx = new IdentifierExpressionContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(178);
                        match(IDENTIFIER);
                    }
                    break;
                    case 10: {
                        _localctx = new GroupedExpressionContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(179);
                        grouped();
                    }
                    break;
                }
                _ctx.stop = _input.LT(-1);
                setState(204);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 15, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        if (_parseListeners != null) triggerExitRuleEvent();
                        _prevctx = _localctx;
                        {
                            setState(202);
                            _errHandler.sync(this);
                            switch (getInterpreter().adaptivePredict(_input, 14, _ctx)) {
                                case 1: {
                                    _localctx = new SingleValueSelectorContext(new PrimaryExpressionContext(_parentctx, _parentState));
                                    pushNewRecursionContext(_localctx, _startState, RULE_primaryExpression);
                                    setState(182);
                                    if (!(precpred(_ctx, 6)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 6)");
                                    setState(183);
                                    match(DOT);
                                    setState(184);
                                    match(IDENTIFIER);
                                }
                                break;
                                case 2: {
                                    _localctx = new MultiValueSelectorContext(new PrimaryExpressionContext(_parentctx, _parentState));
                                    pushNewRecursionContext(_localctx, _startState, RULE_primaryExpression);
                                    setState(185);
                                    if (!(precpred(_ctx, 5)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 5)");
                                    setState(186);
                                    match(DOT);
                                    setState(187);
                                    match(STAR);
                                    setState(188);
                                    match(IDENTIFIER);
                                }
                                break;
                                case 3: {
                                    _localctx = new DescendantsSelectorContext(new PrimaryExpressionContext(_parentctx, _parentState));
                                    pushNewRecursionContext(_localctx, _startState, RULE_primaryExpression);
                                    setState(189);
                                    if (!(precpred(_ctx, 4)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 4)");
                                    setState(190);
                                    match(OPERATOR_RANGE);
                                    setState(191);
                                    match(IDENTIFIER);
                                }
                                break;
                                case 4: {
                                    _localctx = new IndexedSelectorContext(new PrimaryExpressionContext(_parentctx, _parentState));
                                    pushNewRecursionContext(_localctx, _startState, RULE_primaryExpression);
                                    setState(192);
                                    if (!(precpred(_ctx, 3)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 3)");
                                    setState(193);
                                    match(LSQUARE);
                                    setState(194);
                                    match(NUMBER);
                                    setState(195);
                                    match(RSQUARE);
                                }
                                break;
                                case 5: {
                                    _localctx = new AttributeSelectorContext(new PrimaryExpressionContext(_parentctx, _parentState));
                                    pushNewRecursionContext(_localctx, _startState, RULE_primaryExpression);
                                    setState(196);
                                    if (!(precpred(_ctx, 2)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 2)");
                                    setState(197);
                                    match(DOT);
                                    setState(198);
                                    match(AT);
                                    setState(199);
                                    match(IDENTIFIER);
                                }
                                break;
                                case 6: {
                                    _localctx = new ExistenceQuerySelectorContext(new PrimaryExpressionContext(_parentctx, _parentState));
                                    pushNewRecursionContext(_localctx, _startState, RULE_primaryExpression);
                                    setState(200);
                                    if (!(precpred(_ctx, 1)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 1)");
                                    setState(201);
                                    match(QUESTION);
                                }
                                break;
                            }
                        }
                    }
                    setState(206);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 15, _ctx);
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
        enterRule(_localctx, 24, RULE_implicitLambdaExpression);
        int _la;
        try {
            setState(220);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 17, _ctx)) {
                case 1:
                    _localctx = new SingleParameterImplicitLambdaContext(_localctx);
                    enterOuterAlt(_localctx, 1);
                {
                    setState(207);
                    expression(0);
                }
                break;
                case 2:
                    _localctx = new MultiParameterImplicitLambdaContext(_localctx);
                    enterOuterAlt(_localctx, 2);
                {
                    setState(208);
                    match(T__0);
                    setState(209);
                    match(IDENTIFIER);
                    setState(214);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                    while (_la == COMMA) {
                        {
                            {
                                setState(210);
                                match(COMMA);
                                setState(211);
                                match(IDENTIFIER);
                            }
                        }
                        setState(216);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                    }
                    setState(217);
                    match(T__1);
                    setState(218);
                    match(ARROW);
                    setState(219);
                    expression(0);
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
        enterRule(_localctx, 26, RULE_builtInFunctionCall);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(222);
                match(BUILTIN_FUNCTION);
                setState(223);
                match(T__0);
                setState(224);
                expression(0);
                setState(229);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == COMMA) {
                    {
                        {
                            setState(225);
                            match(COMMA);
                            setState(226);
                            expression(0);
                        }
                    }
                    setState(231);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
                setState(232);
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
        enterRule(_localctx, 28, RULE_inlineLambda);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(234);
                match(T__0);
                setState(235);
                functionParameters();
                setState(236);
                match(T__1);
                setState(237);
                match(ARROW);
                setState(238);
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
        enterRule(_localctx, 30, RULE_functionParameters);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(240);
                match(IDENTIFIER);
                setState(245);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == COMMA) {
                    {
                        {
                            setState(241);
                            match(COMMA);
                            setState(242);
                            match(IDENTIFIER);
                        }
                    }
                    setState(247);
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
        enterRule(_localctx, 32, RULE_literal);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(248);
                _la = _input.LA(1);
                if (!((((_la) & ~0x3f) == 0 && ((1L << _la) & 7897088L) != 0))) {
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
        enterRule(_localctx, 34, RULE_array);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(250);
                match(LSQUARE);
                setState(259);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 343507058L) != 0)) {
                    {
                        setState(251);
                        expression(0);
                        setState(256);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        while (_la == COMMA) {
                            {
                                {
                                    setState(252);
                                    match(COMMA);
                                    setState(253);
                                    expression(0);
                                }
                            }
                            setState(258);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        }
                    }
                }

                setState(261);
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
        enterRule(_localctx, 36, RULE_object);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(263);
                match(LCURLY);
                setState(272);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == IDENTIFIER) {
                    {
                        setState(264);
                        keyValue();
                        setState(269);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        while (_la == COMMA) {
                            {
                                {
                                    setState(265);
                                    match(COMMA);
                                    setState(266);
                                    keyValue();
                                }
                            }
                            setState(271);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        }
                    }
                }

                setState(274);
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
        enterRule(_localctx, 38, RULE_keyValue);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(276);
                match(IDENTIFIER);
                setState(277);
                match(COLON);
                setState(278);
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
        enterRule(_localctx, 40, RULE_functionCall);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(280);
                match(IDENTIFIER);
                setState(281);
                match(T__0);
                setState(290);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 343507058L) != 0)) {
                    {
                        setState(282);
                        expression(0);
                        setState(287);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        while (_la == COMMA) {
                            {
                                {
                                    setState(283);
                                    match(COMMA);
                                    setState(284);
                                    expression(0);
                                }
                            }
                            setState(289);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        }
                    }
                }

                setState(292);
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
        enterRule(_localctx, 42, RULE_grouped);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(294);
                match(T__0);
                setState(295);
                expression(0);
                setState(296);
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
            case 11:
                return primaryExpression_sempred((PrimaryExpressionContext) _localctx, predIndex);
        }
        return true;
    }

    private boolean expression_sempred(ExpressionContext _localctx, int predIndex) {
        switch (predIndex) {
            case 0:
                return precpred(_ctx, 8);
            case 1:
                return precpred(_ctx, 7);
            case 2:
                return precpred(_ctx, 6);
            case 3:
                return precpred(_ctx, 5);
            case 4:
                return precpred(_ctx, 4);
            case 5:
                return precpred(_ctx, 3);
            case 6:
                return precpred(_ctx, 2);
            case 7:
                return precpred(_ctx, 1);
        }
        return true;
    }

    private boolean primaryExpression_sempred(PrimaryExpressionContext _localctx, int predIndex) {
        switch (predIndex) {
            case 8:
                return precpred(_ctx, 6);
            case 9:
                return precpred(_ctx, 5);
            case 10:
                return precpred(_ctx, 4);
            case 11:
                return precpred(_ctx, 3);
            case 12:
                return precpred(_ctx, 2);
            case 13:
                return precpred(_ctx, 1);
        }
        return true;
    }

    public static final String _serializedATN =
            "\u0004\u0001/\u012b\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002" +
                    "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002" +
                    "\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002" +
                    "\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002" +
                    "\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f" +
                    "\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012" +
                    "\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015" +
                    "\u0001\u0000\u0003\u0000.\b\u0000\u0001\u0000\u0001\u0000\u0003\u0000" +
                    "2\b\u0000\u0001\u0000\u0005\u00005\b\u0000\n\u0000\f\u00008\t\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0005\u0001>\b\u0001\n\u0001" +
                    "\f\u0001A\t\u0001\u0004\u0001C\b\u0001\u000b\u0001\f\u0001D\u0001\u0002" +
                    "\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0003\u0002" +
                    "M\b\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004" +
                    "\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006" +
                    "\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007" +
                    "\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0003\bf\b\b" +
                    "\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0005\tm\b\t\n\t\f\tp\t\t\u0001" +
                    "\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001" +
                    "\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001" +
                    "\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001" +
                    "\n\u0001\n\u0001\n\u0005\n\u0090\b\n\n\n\f\n\u0093\t\n\u0001\u000b\u0001" +
                    "\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001" +
                    "\u000b\u0003\u000b\u009d\b\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001" +
                    "\u000b\u0001\u000b\u0001\u000b\u0003\u000b\u00a5\b\u000b\u0001\u000b\u0001" +
                    "\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0003\u000b\u00ad" +
                    "\b\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001" +
                    "\u000b\u0003\u000b\u00b5\b\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001" +
                    "\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001" +
                    "\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001" +
                    "\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0005\u000b\u00cb" +
                    "\b\u000b\n\u000b\f\u000b\u00ce\t\u000b\u0001\f\u0001\f\u0001\f\u0001\f" +
                    "\u0001\f\u0005\f\u00d5\b\f\n\f\f\f\u00d8\t\f\u0001\f\u0001\f\u0001\f\u0003" +
                    "\f\u00dd\b\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0005\r\u00e4\b\r" +
                    "\n\r\f\r\u00e7\t\r\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001\u000e" +
                    "\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000f\u0001\u000f\u0001\u000f" +
                    "\u0005\u000f\u00f4\b\u000f\n\u000f\f\u000f\u00f7\t\u000f\u0001\u0010\u0001" +
                    "\u0010\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0005\u0011\u00ff" +
                    "\b\u0011\n\u0011\f\u0011\u0102\t\u0011\u0003\u0011\u0104\b\u0011\u0001" +
                    "\u0011\u0001\u0011\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0005" +
                    "\u0012\u010c\b\u0012\n\u0012\f\u0012\u010f\t\u0012\u0003\u0012\u0111\b" +
                    "\u0012\u0001\u0012\u0001\u0012\u0001\u0013\u0001\u0013\u0001\u0013\u0001" +
                    "\u0013\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0005" +
                    "\u0014\u011e\b\u0014\n\u0014\f\u0014\u0121\t\u0014\u0003\u0014\u0123\b" +
                    "\u0014\u0001\u0014\u0001\u0014\u0001\u0015\u0001\u0015\u0001\u0015\u0001" +
                    "\u0015\u0001\u0015\u0000\u0002\u0014\u0016\u0016\u0000\u0002\u0004\u0006" +
                    "\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*\u0000" +
                    "\u0002\u0001\u0000\u001f \u0002\u0000\u000f\u000f\u0013\u0016\u0144\u0000" +
                    "-\u0001\u0000\u0000\u0000\u0002B\u0001\u0000\u0000\u0000\u0004L\u0001" +
                    "\u0000\u0000\u0000\u0006N\u0001\u0000\u0000\u0000\bQ\u0001\u0000\u0000" +
                    "\u0000\nT\u0001\u0000\u0000\u0000\fX\u0001\u0000\u0000\u0000\u000e\\\u0001" +
                    "\u0000\u0000\u0000\u0010a\u0001\u0000\u0000\u0000\u0012j\u0001\u0000\u0000" +
                    "\u0000\u0014q\u0001\u0000\u0000\u0000\u0016\u00b4\u0001\u0000\u0000\u0000" +
                    "\u0018\u00dc\u0001\u0000\u0000\u0000\u001a\u00de\u0001\u0000\u0000\u0000" +
                    "\u001c\u00ea\u0001\u0000\u0000\u0000\u001e\u00f0\u0001\u0000\u0000\u0000" +
                    " \u00f8\u0001\u0000\u0000\u0000\"\u00fa\u0001\u0000\u0000\u0000$\u0107" +
                    "\u0001\u0000\u0000\u0000&\u0114\u0001\u0000\u0000\u0000(\u0118\u0001\u0000" +
                    "\u0000\u0000*\u0126\u0001\u0000\u0000\u0000,.\u0003\u0002\u0001\u0000" +
                    "-,\u0001\u0000\u0000\u0000-.\u0001\u0000\u0000\u0000./\u0001\u0000\u0000" +
                    "\u0000/1\u0005\u001e\u0000\u000002\u0003\u0012\t\u000010\u0001\u0000\u0000" +
                    "\u000012\u0001\u0000\u0000\u000026\u0001\u0000\u0000\u000035\u0005 \u0000" +
                    "\u000043\u0001\u0000\u0000\u000058\u0001\u0000\u0000\u000064\u0001\u0000" +
                    "\u0000\u000067\u0001\u0000\u0000\u000079\u0001\u0000\u0000\u000086\u0001" +
                    "\u0000\u0000\u00009:\u0005\u0000\u0000\u0001:\u0001\u0001\u0000\u0000" +
                    "\u0000;?\u0003\u0004\u0002\u0000<>\u0007\u0000\u0000\u0000=<\u0001\u0000" +
                    "\u0000\u0000>A\u0001\u0000\u0000\u0000?=\u0001\u0000\u0000\u0000?@\u0001" +
                    "\u0000\u0000\u0000@C\u0001\u0000\u0000\u0000A?\u0001\u0000\u0000\u0000" +
                    "B;\u0001\u0000\u0000\u0000CD\u0001\u0000\u0000\u0000DB\u0001\u0000\u0000" +
                    "\u0000DE\u0001\u0000\u0000\u0000E\u0003\u0001\u0000\u0000\u0000FM\u0003" +
                    "\u0006\u0003\u0000GM\u0003\b\u0004\u0000HM\u0003\n\u0005\u0000IM\u0003" +
                    "\f\u0006\u0000JM\u0003\u000e\u0007\u0000KM\u0003\u0010\b\u0000LF\u0001" +
                    "\u0000\u0000\u0000LG\u0001\u0000\u0000\u0000LH\u0001\u0000\u0000\u0000" +
                    "LI\u0001\u0000\u0000\u0000LJ\u0001\u0000\u0000\u0000LK\u0001\u0000\u0000" +
                    "\u0000M\u0005\u0001\u0000\u0000\u0000NO\u0005\f\u0000\u0000OP\u0005\u0013" +
                    "\u0000\u0000P\u0007\u0001\u0000\u0000\u0000QR\u0005\u000b\u0000\u0000" +
                    "RS\u0005\u0012\u0000\u0000S\t\u0001\u0000\u0000\u0000TU\u0005\t\u0000" +
                    "\u0000UV\u0005\u0010\u0000\u0000VW\u0005\u0012\u0000\u0000W\u000b\u0001" +
                    "\u0000\u0000\u0000XY\u0005\n\u0000\u0000YZ\u0005\u0010\u0000\u0000Z[\u0005" +
                    "\u0011\u0000\u0000[\r\u0001\u0000\u0000\u0000\\]\u0005\u0007\u0000\u0000" +
                    "]^\u0005\u0010\u0000\u0000^_\u0005\r\u0000\u0000_`\u0003\u0014\n\u0000" +
                    "`\u000f\u0001\u0000\u0000\u0000ab\u0005\b\u0000\u0000bc\u0005\u0010\u0000" +
                    "\u0000ce\u0005\u0001\u0000\u0000df\u0003\u001e\u000f\u0000ed\u0001\u0000" +
                    "\u0000\u0000ef\u0001\u0000\u0000\u0000fg\u0001\u0000\u0000\u0000gh\u0005" +
                    "\u0002\u0000\u0000hi\u0003\u0014\n\u0000i\u0011\u0001\u0000\u0000\u0000" +
                    "jn\u0003\u0014\n\u0000km\u0005 \u0000\u0000lk\u0001\u0000\u0000\u0000" +
                    "mp\u0001\u0000\u0000\u0000nl\u0001\u0000\u0000\u0000no\u0001\u0000\u0000" +
                    "\u0000o\u0013\u0001\u0000\u0000\u0000pn\u0001\u0000\u0000\u0000qr\u0006" +
                    "\n\uffff\uffff\u0000rs\u0003\u0016\u000b\u0000s\u0091\u0001\u0000\u0000" +
                    "\u0000tu\n\b\u0000\u0000uv\u0005*\u0000\u0000vw\u0003\u0014\n\u0000wx" +
                    "\u0005\u0018\u0000\u0000xy\u0003\u0014\n\ty\u0090\u0001\u0000\u0000\u0000" +
                    "z{\n\u0007\u0000\u0000{|\u0005(\u0000\u0000|\u0090\u0003\u0014\n\b}~\n" +
                    "\u0006\u0000\u0000~\u007f\u0005\'\u0000\u0000\u007f\u0090\u0003\u0014" +
                    "\n\u0007\u0080\u0081\n\u0005\u0000\u0000\u0081\u0082\u0005)\u0000\u0000" +
                    "\u0082\u0090\u0003\u0014\n\u0006\u0083\u0084\n\u0004\u0000\u0000\u0084" +
                    "\u0085\u0005&\u0000\u0000\u0085\u0090\u0003\u0014\n\u0005\u0086\u0087" +
                    "\n\u0003\u0000\u0000\u0087\u0088\u0005+\u0000\u0000\u0088\u0090\u0003" +
                    "\u0014\n\u0004\u0089\u008a\n\u0002\u0000\u0000\u008a\u008b\u0005,\u0000" +
                    "\u0000\u008b\u0090\u0003\u0014\n\u0003\u008c\u008d\n\u0001\u0000\u0000" +
                    "\u008d\u008e\u0005\u0003\u0000\u0000\u008e\u0090\u0003\u0018\f\u0000\u008f" +
                    "t\u0001\u0000\u0000\u0000\u008fz\u0001\u0000\u0000\u0000\u008f}\u0001" +
                    "\u0000\u0000\u0000\u008f\u0080\u0001\u0000\u0000\u0000\u008f\u0083\u0001" +
                    "\u0000\u0000\u0000\u008f\u0086\u0001\u0000\u0000\u0000\u008f\u0089\u0001" +
                    "\u0000\u0000\u0000\u008f\u008c\u0001\u0000\u0000\u0000\u0090\u0093\u0001" +
                    "\u0000\u0000\u0000\u0091\u008f\u0001\u0000\u0000\u0000\u0091\u0092\u0001" +
                    "\u0000\u0000\u0000\u0092\u0015\u0001\u0000\u0000\u0000\u0093\u0091\u0001" +
                    "\u0000\u0000\u0000\u0094\u0095\u0006\u000b\uffff\uffff\u0000\u0095\u00b5" +
                    "\u0003(\u0014\u0000\u0096\u009c\u0005\u0004\u0000\u0000\u0097\u0098\u0005" +
                    "\u0001\u0000\u0000\u0098\u0099\u0003\u0014\n\u0000\u0099\u009a\u0005\u0002" +
                    "\u0000\u0000\u009a\u009d\u0001\u0000\u0000\u0000\u009b\u009d\u0003\u0014" +
                    "\n\u0000\u009c\u0097\u0001\u0000\u0000\u0000\u009c\u009b\u0001\u0000\u0000" +
                    "\u0000\u009d\u00b5\u0001\u0000\u0000\u0000\u009e\u00a4\u0005\u0005\u0000" +
                    "\u0000\u009f\u00a0\u0005\u0001\u0000\u0000\u00a0\u00a1\u0003\u0014\n\u0000" +
                    "\u00a1\u00a2\u0005\u0002\u0000\u0000\u00a2\u00a5\u0001\u0000\u0000\u0000" +
                    "\u00a3\u00a5\u0003\u0014\n\u0000\u00a4\u009f\u0001\u0000\u0000\u0000\u00a4" +
                    "\u00a3\u0001\u0000\u0000\u0000\u00a5\u00b5\u0001\u0000\u0000\u0000\u00a6" +
                    "\u00ac\u0005\u0006\u0000\u0000\u00a7\u00a8\u0005\u0001\u0000\u0000\u00a8" +
                    "\u00a9\u0003\u0014\n\u0000\u00a9\u00aa\u0005\u0002\u0000\u0000\u00aa\u00ad" +
                    "\u0001\u0000\u0000\u0000\u00ab\u00ad\u0003\u0014\n\u0000\u00ac\u00a7\u0001" +
                    "\u0000\u0000\u0000\u00ac\u00ab\u0001\u0000\u0000\u0000\u00ad\u00b5\u0001" +
                    "\u0000\u0000\u0000\u00ae\u00b5\u0003\u001c\u000e\u0000\u00af\u00b5\u0003" +
                    " \u0010\u0000\u00b0\u00b5\u0003\"\u0011\u0000\u00b1\u00b5\u0003$\u0012" +
                    "\u0000\u00b2\u00b5\u0005\u0010\u0000\u0000\u00b3\u00b5\u0003*\u0015\u0000" +
                    "\u00b4\u0094\u0001\u0000\u0000\u0000\u00b4\u0096\u0001\u0000\u0000\u0000" +
                    "\u00b4\u009e\u0001\u0000\u0000\u0000\u00b4\u00a6\u0001\u0000\u0000\u0000" +
                    "\u00b4\u00ae\u0001\u0000\u0000\u0000\u00b4\u00af\u0001\u0000\u0000\u0000" +
                    "\u00b4\u00b0\u0001\u0000\u0000\u0000\u00b4\u00b1\u0001\u0000\u0000\u0000" +
                    "\u00b4\u00b2\u0001\u0000\u0000\u0000\u00b4\u00b3\u0001\u0000\u0000\u0000" +
                    "\u00b5\u00cc\u0001\u0000\u0000\u0000\u00b6\u00b7\n\u0006\u0000\u0000\u00b7" +
                    "\u00b8\u0005\u0017\u0000\u0000\u00b8\u00cb\u0005\u0010\u0000\u0000\u00b9" +
                    "\u00ba\n\u0005\u0000\u0000\u00ba\u00bb\u0005\u0017\u0000\u0000\u00bb\u00bc" +
                    "\u0005\"\u0000\u0000\u00bc\u00cb\u0005\u0010\u0000\u0000\u00bd\u00be\n" +
                    "\u0004\u0000\u0000\u00be\u00bf\u0005+\u0000\u0000\u00bf\u00cb\u0005\u0010" +
                    "\u0000\u0000\u00c0\u00c1\n\u0003\u0000\u0000\u00c1\u00c2\u0005\u001c\u0000" +
                    "\u0000\u00c2\u00c3\u0005\u0013\u0000\u0000\u00c3\u00cb\u0005\u001d\u0000" +
                    "\u0000\u00c4\u00c5\n\u0002\u0000\u0000\u00c5\u00c6\u0005\u0017\u0000\u0000" +
                    "\u00c6\u00c7\u0005$\u0000\u0000\u00c7\u00cb\u0005\u0010\u0000\u0000\u00c8" +
                    "\u00c9\n\u0001\u0000\u0000\u00c9\u00cb\u0005%\u0000\u0000\u00ca\u00b6" +
                    "\u0001\u0000\u0000\u0000\u00ca\u00b9\u0001\u0000\u0000\u0000\u00ca\u00bd" +
                    "\u0001\u0000\u0000\u0000\u00ca\u00c0\u0001\u0000\u0000\u0000\u00ca\u00c4" +
                    "\u0001\u0000\u0000\u0000\u00ca\u00c8\u0001\u0000\u0000\u0000\u00cb\u00ce" +
                    "\u0001\u0000\u0000\u0000\u00cc\u00ca\u0001\u0000\u0000\u0000\u00cc\u00cd" +
                    "\u0001\u0000\u0000\u0000\u00cd\u0017\u0001\u0000\u0000\u0000\u00ce\u00cc" +
                    "\u0001\u0000\u0000\u0000\u00cf\u00dd\u0003\u0014\n\u0000\u00d0\u00d1\u0005" +
                    "\u0001\u0000\u0000\u00d1\u00d6\u0005\u0010\u0000\u0000\u00d2\u00d3\u0005" +
                    "\u0019\u0000\u0000\u00d3\u00d5\u0005\u0010\u0000\u0000\u00d4\u00d2\u0001" +
                    "\u0000\u0000\u0000\u00d5\u00d8\u0001\u0000\u0000\u0000\u00d6\u00d4\u0001" +
                    "\u0000\u0000\u0000\u00d6\u00d7\u0001\u0000\u0000\u0000\u00d7\u00d9\u0001" +
                    "\u0000\u0000\u0000\u00d8\u00d6\u0001\u0000\u0000\u0000\u00d9\u00da\u0005" +
                    "\u0002\u0000\u0000\u00da\u00db\u0005\u000e\u0000\u0000\u00db\u00dd\u0003" +
                    "\u0014\n\u0000\u00dc\u00cf\u0001\u0000\u0000\u0000\u00dc\u00d0\u0001\u0000" +
                    "\u0000\u0000\u00dd\u0019\u0001\u0000\u0000\u0000\u00de\u00df\u0005-\u0000" +
                    "\u0000\u00df\u00e0\u0005\u0001\u0000\u0000\u00e0\u00e5\u0003\u0014\n\u0000" +
                    "\u00e1\u00e2\u0005\u0019\u0000\u0000\u00e2\u00e4\u0003\u0014\n\u0000\u00e3" +
                    "\u00e1\u0001\u0000\u0000\u0000\u00e4\u00e7\u0001\u0000\u0000\u0000\u00e5" +
                    "\u00e3\u0001\u0000\u0000\u0000\u00e5\u00e6\u0001\u0000\u0000\u0000\u00e6" +
                    "\u00e8\u0001\u0000\u0000\u0000\u00e7\u00e5\u0001\u0000\u0000\u0000\u00e8" +
                    "\u00e9\u0005\u0002\u0000\u0000\u00e9\u001b\u0001\u0000\u0000\u0000\u00ea" +
                    "\u00eb\u0005\u0001\u0000\u0000\u00eb\u00ec\u0003\u001e\u000f\u0000\u00ec" +
                    "\u00ed\u0005\u0002\u0000\u0000\u00ed\u00ee\u0005\u000e\u0000\u0000\u00ee" +
                    "\u00ef\u0003\u0014\n\u0000\u00ef\u001d\u0001\u0000\u0000\u0000\u00f0\u00f5" +
                    "\u0005\u0010\u0000\u0000\u00f1\u00f2\u0005\u0019\u0000\u0000\u00f2\u00f4" +
                    "\u0005\u0010\u0000\u0000\u00f3\u00f1\u0001\u0000\u0000\u0000\u00f4\u00f7" +
                    "\u0001\u0000\u0000\u0000\u00f5\u00f3\u0001\u0000\u0000\u0000\u00f5\u00f6" +
                    "\u0001\u0000\u0000\u0000\u00f6\u001f\u0001\u0000\u0000\u0000\u00f7\u00f5" +
                    "\u0001\u0000\u0000\u0000\u00f8\u00f9\u0007\u0001\u0000\u0000\u00f9!\u0001" +
                    "\u0000\u0000\u0000\u00fa\u0103\u0005\u001c\u0000\u0000\u00fb\u0100\u0003" +
                    "\u0014\n\u0000\u00fc\u00fd\u0005\u0019\u0000\u0000\u00fd\u00ff\u0003\u0014" +
                    "\n\u0000\u00fe\u00fc\u0001\u0000\u0000\u0000\u00ff\u0102\u0001\u0000\u0000" +
                    "\u0000\u0100\u00fe\u0001\u0000\u0000\u0000\u0100\u0101\u0001\u0000\u0000" +
                    "\u0000\u0101\u0104\u0001\u0000\u0000\u0000\u0102\u0100\u0001\u0000\u0000" +
                    "\u0000\u0103\u00fb\u0001\u0000\u0000\u0000\u0103\u0104\u0001\u0000\u0000" +
                    "\u0000\u0104\u0105\u0001\u0000\u0000\u0000\u0105\u0106\u0005\u001d\u0000" +
                    "\u0000\u0106#\u0001\u0000\u0000\u0000\u0107\u0110\u0005\u001a\u0000\u0000" +
                    "\u0108\u010d\u0003&\u0013\u0000\u0109\u010a\u0005\u0019\u0000\u0000\u010a" +
                    "\u010c\u0003&\u0013\u0000\u010b\u0109\u0001\u0000\u0000\u0000\u010c\u010f" +
                    "\u0001\u0000\u0000\u0000\u010d\u010b\u0001\u0000\u0000\u0000\u010d\u010e" +
                    "\u0001\u0000\u0000\u0000\u010e\u0111\u0001\u0000\u0000\u0000\u010f\u010d" +
                    "\u0001\u0000\u0000\u0000\u0110\u0108\u0001\u0000\u0000\u0000\u0110\u0111" +
                    "\u0001\u0000\u0000\u0000\u0111\u0112\u0001\u0000\u0000\u0000\u0112\u0113" +
                    "\u0005\u001b\u0000\u0000\u0113%\u0001\u0000\u0000\u0000\u0114\u0115\u0005" +
                    "\u0010\u0000\u0000\u0115\u0116\u0005\u0018\u0000\u0000\u0116\u0117\u0003" +
                    "\u0014\n\u0000\u0117\'\u0001\u0000\u0000\u0000\u0118\u0119\u0005\u0010" +
                    "\u0000\u0000\u0119\u0122\u0005\u0001\u0000\u0000\u011a\u011f\u0003\u0014" +
                    "\n\u0000\u011b\u011c\u0005\u0019\u0000\u0000\u011c\u011e\u0003\u0014\n" +
                    "\u0000\u011d\u011b\u0001\u0000\u0000\u0000\u011e\u0121\u0001\u0000\u0000" +
                    "\u0000\u011f\u011d\u0001\u0000\u0000\u0000\u011f\u0120\u0001\u0000\u0000" +
                    "\u0000\u0120\u0123\u0001\u0000\u0000\u0000\u0121\u011f\u0001\u0000\u0000" +
                    "\u0000\u0122\u011a\u0001\u0000\u0000\u0000\u0122\u0123\u0001\u0000\u0000" +
                    "\u0000\u0123\u0124\u0001\u0000\u0000\u0000\u0124\u0125\u0005\u0002\u0000" +
                    "\u0000\u0125)\u0001\u0000\u0000\u0000\u0126\u0127\u0005\u0001\u0000\u0000" +
                    "\u0127\u0128\u0003\u0014\n\u0000\u0128\u0129\u0005\u0002\u0000\u0000\u0129" +
                    "+\u0001\u0000\u0000\u0000\u001a-16?DLen\u008f\u0091\u009c\u00a4\u00ac" +
                    "\u00b4\u00ca\u00cc\u00d6\u00dc\u00e5\u00f5\u0100\u0103\u010d\u0110\u011f" +
                    "\u0122";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}