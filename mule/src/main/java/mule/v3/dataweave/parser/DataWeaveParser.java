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

// Generated from src/main/java/dataweave/parser/DataWeave.g4 by ANTLR 4.13.2
package mule.v3.dataweave.parser;

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
            T__0 = 1, T__1 = 2, T__2 = 3, T__3 = 4, T__4 = 5, T__5 = 6, T__6 = 7, T__7 = 8, T__8 = 9,
            T__9 = 10, T__10 = 11, T__11 = 12, T__12 = 13, T__13 = 14, T__14 = 15, T__15 = 16, VAR = 17,
            FUNCTION = 18, INPUT = 19, NAMESPACE = 20, OUTPUT = 21, DW = 22, ASSIGN = 23, ARROW = 24,
            BOOLEAN = 25, OPERATOR_EQUALITY = 26, OPERATOR_RELATIONAL = 27, OPERATOR_MULTIPLICATIVE = 28,
            OPERATOR_ADDITIVE = 29, OPERATOR_TYPE_COERCION = 30, OPERATOR_RANGE = 31, IDENTIFIER = 32,
            INDEX_IDENTIFIER = 33, VALUE_IDENTIFIER = 34, URL = 35, MEDIA_TYPE = 36, NUMBER = 37,
            STRING = 38, DATE = 39, REGEX = 40, DOT = 41, COLON = 42, COMMA = 43, LCURLY = 44, RCURLY = 45,
            LSQUARE = 46, RSQUARE = 47, SEPARATOR = 48, WS = 49, NEWLINE = 50, COMMENT = 51, STAR = 52,
            AT = 53, QUESTION = 54;
    public static final int
            RULE_script = 0, RULE_header = 1, RULE_directive = 2, RULE_dwVersion = 3,
            RULE_outputDirective = 4, RULE_inputDirective = 5, RULE_namespaceDirective = 6,
            RULE_variableDeclaration = 7, RULE_functionDeclaration = 8, RULE_body = 9,
            RULE_expression = 10, RULE_conditionalExpression = 11, RULE_implicitLambdaExpression = 12,
            RULE_inlineLambda = 13, RULE_functionParameters = 14, RULE_defaultExpression = 15,
            RULE_defaultExpressionRest = 16, RULE_logicalOrExpression = 17, RULE_logicalAndExpression = 18,
            RULE_equalityExpression = 19, RULE_relationalExpression = 20, RULE_additiveExpression = 21,
            RULE_multiplicativeExpression = 22, RULE_typeCoercionExpression = 23,
            RULE_formatOption = 24, RULE_unaryExpression = 25, RULE_primaryExpression = 26,
            RULE_grouped = 27, RULE_selectorExpression = 28, RULE_literal = 29, RULE_array = 30,
            RULE_object = 31, RULE_keyValue = 32, RULE_functionCall = 33, RULE_typeExpression = 34;

    private static String[] makeRuleNames() {
        return new String[]{
                "script", "header", "directive", "dwVersion", "outputDirective", "inputDirective",
                "namespaceDirective", "variableDeclaration", "functionDeclaration", "body",
                "expression", "conditionalExpression", "implicitLambdaExpression", "inlineLambda",
                "functionParameters", "defaultExpression", "defaultExpressionRest", "logicalOrExpression",
                "logicalAndExpression", "equalityExpression", "relationalExpression",
                "additiveExpression", "multiplicativeExpression", "typeCoercionExpression",
                "formatOption", "unaryExpression", "primaryExpression", "grouped", "selectorExpression",
                "literal", "array", "object", "keyValue", "functionCall", "typeExpression"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'('", "')'", "'when'", "'otherwise'", "'unless'", "'filter'",
                "'map'", "'groupBy'", "'replace'", "'with'", "'++'", "'or'", "'and'",
                "'sizeOf'", "'upper'", "'lower'", "'%var'", "'%function'", "'%input'",
                "'%namespace'", "'%output'", "'%dw'", "'='", "'->'", null, null, null,
                null, null, "'as'", "'..'", null, "'$$'", "'$'", null, null, null, null,
                null, null, "'.'", "':'", "','", "'{'", "'}'", "'['", "']'", "'---'",
                null, null, null, "'*'", "'@'", "'?'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, null, null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, "VAR", "FUNCTION", "INPUT", "NAMESPACE",
                "OUTPUT", "DW", "ASSIGN", "ARROW", "BOOLEAN", "OPERATOR_EQUALITY", "OPERATOR_RELATIONAL",
                "OPERATOR_MULTIPLICATIVE", "OPERATOR_ADDITIVE", "OPERATOR_TYPE_COERCION",
                "OPERATOR_RANGE", "IDENTIFIER", "INDEX_IDENTIFIER", "VALUE_IDENTIFIER",
                "URL", "MEDIA_TYPE", "NUMBER", "STRING", "DATE", "REGEX", "DOT", "COLON",
                "COMMA", "LCURLY", "RCURLY", "LSQUARE", "RSQUARE", "SEPARATOR", "WS",
                "NEWLINE", "COMMENT", "STAR", "AT", "QUESTION"
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
                setState(71);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 8257536L) != 0)) {
                    {
                        setState(70);
                        header();
                    }
                }

                setState(73);
                match(SEPARATOR);
                setState(75);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 90052612964354L) != 0)) {
                    {
                        setState(74);
                        body();
                    }
                }

                setState(80);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == NEWLINE) {
                    {
                        {
                            setState(77);
                            match(NEWLINE);
                        }
                    }
                    setState(82);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
                setState(83);
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
                setState(92);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(85);
                            directive();
                            setState(89);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                            while (_la == WS || _la == NEWLINE) {
                                {
                                    {
                                        setState(86);
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
                                setState(91);
                                _errHandler.sync(this);
                                _la = _input.LA(1);
                            }
                        }
                    }
                    setState(94);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 8257536L) != 0));
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
            setState(102);
            _errHandler.sync(this);
            switch (_input.LA(1)) {
                case DW:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(96);
                    dwVersion();
                }
                break;
                case OUTPUT:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(97);
                    outputDirective();
                }
                break;
                case INPUT:
                    enterOuterAlt(_localctx, 3);
                {
                    setState(98);
                    inputDirective();
                }
                break;
                case NAMESPACE:
                    enterOuterAlt(_localctx, 4);
                {
                    setState(99);
                    namespaceDirective();
                }
                break;
                case VAR:
                    enterOuterAlt(_localctx, 5);
                {
                    setState(100);
                    variableDeclaration();
                }
                break;
                case FUNCTION:
                    enterOuterAlt(_localctx, 6);
                {
                    setState(101);
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
                setState(104);
                match(DW);
                setState(105);
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
                setState(107);
                match(OUTPUT);
                setState(108);
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
                setState(110);
                match(INPUT);
                setState(111);
                match(IDENTIFIER);
                setState(112);
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
                setState(114);
                match(NAMESPACE);
                setState(115);
                match(IDENTIFIER);
                setState(116);
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
                setState(118);
                match(VAR);
                setState(119);
                match(IDENTIFIER);
                setState(120);
                match(ASSIGN);
                setState(121);
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
                setState(123);
                match(FUNCTION);
                setState(124);
                match(IDENTIFIER);
                setState(125);
                match(T__0);
                setState(127);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == IDENTIFIER) {
                    {
                        setState(126);
                        functionParameters();
                    }
                }

                setState(129);
                match(T__1);
                setState(130);
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
                setState(132);
                expression();
                setState(136);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 7, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        {
                            {
                                setState(133);
                                match(NEWLINE);
                            }
                        }
                    }
                    setState(138);
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
    public static class ExpressionWrapperContext extends ExpressionContext {
        public DefaultExpressionContext defaultExpression() {
            return getRuleContext(DefaultExpressionContext.class, 0);
        }

        public ExpressionWrapperContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterExpressionWrapper(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitExpressionWrapper(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitExpressionWrapper(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ConditionalExpressionWrapperContext extends ExpressionContext {
        public ConditionalExpressionContext conditionalExpression() {
            return getRuleContext(ConditionalExpressionContext.class, 0);
        }

        public ConditionalExpressionWrapperContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener)
                ((DataWeaveListener) listener).enterConditionalExpressionWrapper(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener)
                ((DataWeaveListener) listener).exitConditionalExpressionWrapper(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitConditionalExpressionWrapper(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ExpressionContext expression() throws RecognitionException {
        ExpressionContext _localctx = new ExpressionContext(_ctx, getState());
        enterRule(_localctx, 20, RULE_expression);
        try {
            setState(141);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 8, _ctx)) {
                case 1:
                    _localctx = new ExpressionWrapperContext(_localctx);
                    enterOuterAlt(_localctx, 1);
                {
                    setState(139);
                    defaultExpression();
                }
                break;
                case 2:
                    _localctx = new ConditionalExpressionWrapperContext(_localctx);
                    enterOuterAlt(_localctx, 2);
                {
                    setState(140);
                    conditionalExpression();
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
    public static class ConditionalExpressionContext extends ParserRuleContext {
        public ConditionalExpressionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_conditionalExpression;
        }

        public ConditionalExpressionContext() {
        }

        public void copyFrom(ConditionalExpressionContext ctx) {
            super.copyFrom(ctx);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class WhenConditionContext extends ConditionalExpressionContext {
        public List<DefaultExpressionContext> defaultExpression() {
            return getRuleContexts(DefaultExpressionContext.class);
        }

        public DefaultExpressionContext defaultExpression(int i) {
            return getRuleContext(DefaultExpressionContext.class, i);
        }

        public WhenConditionContext(ConditionalExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterWhenCondition(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitWhenCondition(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitWhenCondition(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class UnlessConditionContext extends ConditionalExpressionContext {
        public List<DefaultExpressionContext> defaultExpression() {
            return getRuleContexts(DefaultExpressionContext.class);
        }

        public DefaultExpressionContext defaultExpression(int i) {
            return getRuleContext(DefaultExpressionContext.class, i);
        }

        public UnlessConditionContext(ConditionalExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterUnlessCondition(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitUnlessCondition(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitUnlessCondition(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ConditionalExpressionContext conditionalExpression() throws RecognitionException {
        ConditionalExpressionContext _localctx = new ConditionalExpressionContext(_ctx, getState());
        enterRule(_localctx, 22, RULE_conditionalExpression);
        try {
            int _alt;
            setState(163);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 11, _ctx)) {
                case 1:
                    _localctx = new WhenConditionContext(_localctx);
                    enterOuterAlt(_localctx, 1);
                {
                    setState(143);
                    defaultExpression();
                    setState(149);
                    _errHandler.sync(this);
                    _alt = 1;
                    do {
                        switch (_alt) {
                            case 1: {
                                {
                                    setState(144);
                                    match(T__2);
                                    setState(145);
                                    defaultExpression();
                                    setState(146);
                                    match(T__3);
                                    setState(147);
                                    defaultExpression();
                                }
                            }
                            break;
                            default:
                                throw new NoViableAltException(this);
                        }
                        setState(151);
                        _errHandler.sync(this);
                        _alt = getInterpreter().adaptivePredict(_input, 9, _ctx);
                    } while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER);
                }
                break;
                case 2:
                    _localctx = new UnlessConditionContext(_localctx);
                    enterOuterAlt(_localctx, 2);
                {
                    setState(153);
                    defaultExpression();
                    setState(159);
                    _errHandler.sync(this);
                    _alt = 1;
                    do {
                        switch (_alt) {
                            case 1: {
                                {
                                    setState(154);
                                    match(T__4);
                                    setState(155);
                                    defaultExpression();
                                    setState(156);
                                    match(T__3);
                                    setState(157);
                                    defaultExpression();
                                }
                            }
                            break;
                            default:
                                throw new NoViableAltException(this);
                        }
                        setState(161);
                        _errHandler.sync(this);
                        _alt = getInterpreter().adaptivePredict(_input, 10, _ctx);
                    } while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER);
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
        public InlineLambdaContext inlineLambda() {
            return getRuleContext(InlineLambdaContext.class, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public ImplicitLambdaExpressionContext implicitLambdaExpression() {
            return getRuleContext(ImplicitLambdaExpressionContext.class, 0);
        }

        public ImplicitLambdaExpressionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_implicitLambdaExpression;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener)
                ((DataWeaveListener) listener).enterImplicitLambdaExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener)
                ((DataWeaveListener) listener).exitImplicitLambdaExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitImplicitLambdaExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ImplicitLambdaExpressionContext implicitLambdaExpression() throws RecognitionException {
        ImplicitLambdaExpressionContext _localctx = new ImplicitLambdaExpressionContext(_ctx, getState());
        enterRule(_localctx, 24, RULE_implicitLambdaExpression);
        try {
            setState(171);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 12, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(165);
                    inlineLambda();
                }
                break;
                case 2:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(166);
                    expression();
                }
                break;
                case 3:
                    enterOuterAlt(_localctx, 3);
                {
                    setState(167);
                    match(T__0);
                    setState(168);
                    implicitLambdaExpression();
                    setState(169);
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
                setState(173);
                match(T__0);
                setState(174);
                functionParameters();
                setState(175);
                match(T__1);
                setState(176);
                match(ARROW);
                setState(177);
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
        enterRule(_localctx, 28, RULE_functionParameters);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(179);
                match(IDENTIFIER);
                setState(184);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == COMMA) {
                    {
                        {
                            setState(180);
                            match(COMMA);
                            setState(181);
                            match(IDENTIFIER);
                        }
                    }
                    setState(186);
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
    public static class DefaultExpressionContext extends ParserRuleContext {
        public DefaultExpressionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_defaultExpression;
        }

        public DefaultExpressionContext() {
        }

        public void copyFrom(DefaultExpressionContext ctx) {
            super.copyFrom(ctx);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class DefaultExpressionWrapperContext extends DefaultExpressionContext {
        public LogicalOrExpressionContext logicalOrExpression() {
            return getRuleContext(LogicalOrExpressionContext.class, 0);
        }

        public DefaultExpressionRestContext defaultExpressionRest() {
            return getRuleContext(DefaultExpressionRestContext.class, 0);
        }

        public DefaultExpressionWrapperContext(DefaultExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener)
                ((DataWeaveListener) listener).enterDefaultExpressionWrapper(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener)
                ((DataWeaveListener) listener).exitDefaultExpressionWrapper(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitDefaultExpressionWrapper(this);
            else return visitor.visitChildren(this);
        }
    }

    public final DefaultExpressionContext defaultExpression() throws RecognitionException {
        DefaultExpressionContext _localctx = new DefaultExpressionContext(_ctx, getState());
        enterRule(_localctx, 30, RULE_defaultExpression);
        try {
            _localctx = new DefaultExpressionWrapperContext(_localctx);
            enterOuterAlt(_localctx, 1);
            {
                setState(187);
                logicalOrExpression();
                setState(188);
                defaultExpressionRest();
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
    public static class DefaultExpressionRestContext extends ParserRuleContext {
        public DefaultExpressionRestContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_defaultExpressionRest;
        }

        public DefaultExpressionRestContext() {
        }

        public void copyFrom(DefaultExpressionRestContext ctx) {
            super.copyFrom(ctx);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class MapExpressionContext extends DefaultExpressionRestContext {
        public ImplicitLambdaExpressionContext implicitLambdaExpression() {
            return getRuleContext(ImplicitLambdaExpressionContext.class, 0);
        }

        public DefaultExpressionRestContext defaultExpressionRest() {
            return getRuleContext(DefaultExpressionRestContext.class, 0);
        }

        public MapExpressionContext(DefaultExpressionRestContext ctx) {
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
    public static class DefaultExpressionEndContext extends DefaultExpressionRestContext {
        public DefaultExpressionEndContext(DefaultExpressionRestContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterDefaultExpressionEnd(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitDefaultExpressionEnd(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitDefaultExpressionEnd(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class FilterExpressionContext extends DefaultExpressionRestContext {
        public ImplicitLambdaExpressionContext implicitLambdaExpression() {
            return getRuleContext(ImplicitLambdaExpressionContext.class, 0);
        }

        public DefaultExpressionRestContext defaultExpressionRest() {
            return getRuleContext(DefaultExpressionRestContext.class, 0);
        }

        public FilterExpressionContext(DefaultExpressionRestContext ctx) {
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
    public static class GroupByExpressionContext extends DefaultExpressionRestContext {
        public ImplicitLambdaExpressionContext implicitLambdaExpression() {
            return getRuleContext(ImplicitLambdaExpressionContext.class, 0);
        }

        public DefaultExpressionRestContext defaultExpressionRest() {
            return getRuleContext(DefaultExpressionRestContext.class, 0);
        }

        public GroupByExpressionContext(DefaultExpressionRestContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterGroupByExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitGroupByExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitGroupByExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ReplaceExpressionContext extends DefaultExpressionRestContext {
        public TerminalNode REGEX() {
            return getToken(DataWeaveParser.REGEX, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public ReplaceExpressionContext(DefaultExpressionRestContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterReplaceExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitReplaceExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitReplaceExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ConcatExpressionContext extends DefaultExpressionRestContext {
        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public ConcatExpressionContext(DefaultExpressionRestContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterConcatExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitConcatExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitConcatExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    public final DefaultExpressionRestContext defaultExpressionRest() throws RecognitionException {
        DefaultExpressionRestContext _localctx = new DefaultExpressionRestContext(_ctx, getState());
        enterRule(_localctx, 32, RULE_defaultExpressionRest);
        try {
            setState(209);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 14, _ctx)) {
                case 1:
                    _localctx = new FilterExpressionContext(_localctx);
                    enterOuterAlt(_localctx, 1);
                {
                    setState(190);
                    match(T__5);
                    setState(191);
                    implicitLambdaExpression();
                    setState(192);
                    defaultExpressionRest();
                }
                break;
                case 2:
                    _localctx = new MapExpressionContext(_localctx);
                    enterOuterAlt(_localctx, 2);
                {
                    setState(194);
                    match(T__6);
                    setState(195);
                    implicitLambdaExpression();
                    setState(196);
                    defaultExpressionRest();
                }
                break;
                case 3:
                    _localctx = new GroupByExpressionContext(_localctx);
                    enterOuterAlt(_localctx, 3);
                {
                    setState(198);
                    match(T__7);
                    setState(199);
                    implicitLambdaExpression();
                    setState(200);
                    defaultExpressionRest();
                }
                break;
                case 4:
                    _localctx = new ReplaceExpressionContext(_localctx);
                    enterOuterAlt(_localctx, 4);
                {
                    setState(202);
                    match(T__8);
                    setState(203);
                    match(REGEX);
                    setState(204);
                    match(T__9);
                    setState(205);
                    expression();
                }
                break;
                case 5:
                    _localctx = new ConcatExpressionContext(_localctx);
                    enterOuterAlt(_localctx, 5);
                {
                    setState(206);
                    match(T__10);
                    setState(207);
                    expression();
                }
                break;
                case 6:
                    _localctx = new DefaultExpressionEndContext(_localctx);
                    enterOuterAlt(_localctx, 6);
                {
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
    public static class LogicalOrExpressionContext extends ParserRuleContext {
        public List<LogicalAndExpressionContext> logicalAndExpression() {
            return getRuleContexts(LogicalAndExpressionContext.class);
        }

        public LogicalAndExpressionContext logicalAndExpression(int i) {
            return getRuleContext(LogicalAndExpressionContext.class, i);
        }

        public LogicalOrExpressionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_logicalOrExpression;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterLogicalOrExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitLogicalOrExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitLogicalOrExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    public final LogicalOrExpressionContext logicalOrExpression() throws RecognitionException {
        LogicalOrExpressionContext _localctx = new LogicalOrExpressionContext(_ctx, getState());
        enterRule(_localctx, 34, RULE_logicalOrExpression);
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                setState(211);
                logicalAndExpression();
                setState(216);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 15, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        {
                            {
                                setState(212);
                                match(T__11);
                                setState(213);
                                logicalAndExpression();
                            }
                        }
                    }
                    setState(218);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 15, _ctx);
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
    public static class LogicalAndExpressionContext extends ParserRuleContext {
        public List<EqualityExpressionContext> equalityExpression() {
            return getRuleContexts(EqualityExpressionContext.class);
        }

        public EqualityExpressionContext equalityExpression(int i) {
            return getRuleContext(EqualityExpressionContext.class, i);
        }

        public LogicalAndExpressionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_logicalAndExpression;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterLogicalAndExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitLogicalAndExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitLogicalAndExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    public final LogicalAndExpressionContext logicalAndExpression() throws RecognitionException {
        LogicalAndExpressionContext _localctx = new LogicalAndExpressionContext(_ctx, getState());
        enterRule(_localctx, 36, RULE_logicalAndExpression);
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                setState(219);
                equalityExpression();
                setState(224);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 16, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        {
                            {
                                setState(220);
                                match(T__12);
                                setState(221);
                                equalityExpression();
                            }
                        }
                    }
                    setState(226);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 16, _ctx);
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
    public static class EqualityExpressionContext extends ParserRuleContext {
        public List<RelationalExpressionContext> relationalExpression() {
            return getRuleContexts(RelationalExpressionContext.class);
        }

        public RelationalExpressionContext relationalExpression(int i) {
            return getRuleContext(RelationalExpressionContext.class, i);
        }

        public List<TerminalNode> OPERATOR_EQUALITY() {
            return getTokens(DataWeaveParser.OPERATOR_EQUALITY);
        }

        public TerminalNode OPERATOR_EQUALITY(int i) {
            return getToken(DataWeaveParser.OPERATOR_EQUALITY, i);
        }

        public EqualityExpressionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_equalityExpression;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterEqualityExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitEqualityExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitEqualityExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    public final EqualityExpressionContext equalityExpression() throws RecognitionException {
        EqualityExpressionContext _localctx = new EqualityExpressionContext(_ctx, getState());
        enterRule(_localctx, 38, RULE_equalityExpression);
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                setState(227);
                relationalExpression();
                setState(232);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 17, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        {
                            {
                                setState(228);
                                match(OPERATOR_EQUALITY);
                                setState(229);
                                relationalExpression();
                            }
                        }
                    }
                    setState(234);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 17, _ctx);
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
    public static class RelationalExpressionContext extends ParserRuleContext {
        public List<AdditiveExpressionContext> additiveExpression() {
            return getRuleContexts(AdditiveExpressionContext.class);
        }

        public AdditiveExpressionContext additiveExpression(int i) {
            return getRuleContext(AdditiveExpressionContext.class, i);
        }

        public List<TerminalNode> OPERATOR_RELATIONAL() {
            return getTokens(DataWeaveParser.OPERATOR_RELATIONAL);
        }

        public TerminalNode OPERATOR_RELATIONAL(int i) {
            return getToken(DataWeaveParser.OPERATOR_RELATIONAL, i);
        }

        public RelationalExpressionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_relationalExpression;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterRelationalExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitRelationalExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitRelationalExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    public final RelationalExpressionContext relationalExpression() throws RecognitionException {
        RelationalExpressionContext _localctx = new RelationalExpressionContext(_ctx, getState());
        enterRule(_localctx, 40, RULE_relationalExpression);
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                setState(235);
                additiveExpression();
                setState(240);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 18, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        {
                            {
                                setState(236);
                                match(OPERATOR_RELATIONAL);
                                setState(237);
                                additiveExpression();
                            }
                        }
                    }
                    setState(242);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 18, _ctx);
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
    public static class AdditiveExpressionContext extends ParserRuleContext {
        public List<MultiplicativeExpressionContext> multiplicativeExpression() {
            return getRuleContexts(MultiplicativeExpressionContext.class);
        }

        public MultiplicativeExpressionContext multiplicativeExpression(int i) {
            return getRuleContext(MultiplicativeExpressionContext.class, i);
        }

        public List<TerminalNode> OPERATOR_ADDITIVE() {
            return getTokens(DataWeaveParser.OPERATOR_ADDITIVE);
        }

        public TerminalNode OPERATOR_ADDITIVE(int i) {
            return getToken(DataWeaveParser.OPERATOR_ADDITIVE, i);
        }

        public AdditiveExpressionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_additiveExpression;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterAdditiveExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitAdditiveExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitAdditiveExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    public final AdditiveExpressionContext additiveExpression() throws RecognitionException {
        AdditiveExpressionContext _localctx = new AdditiveExpressionContext(_ctx, getState());
        enterRule(_localctx, 42, RULE_additiveExpression);
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                setState(243);
                multiplicativeExpression();
                setState(248);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 19, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        {
                            {
                                setState(244);
                                match(OPERATOR_ADDITIVE);
                                setState(245);
                                multiplicativeExpression();
                            }
                        }
                    }
                    setState(250);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 19, _ctx);
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
    public static class MultiplicativeExpressionContext extends ParserRuleContext {
        public List<TypeCoercionExpressionContext> typeCoercionExpression() {
            return getRuleContexts(TypeCoercionExpressionContext.class);
        }

        public TypeCoercionExpressionContext typeCoercionExpression(int i) {
            return getRuleContext(TypeCoercionExpressionContext.class, i);
        }

        public List<TerminalNode> OPERATOR_MULTIPLICATIVE() {
            return getTokens(DataWeaveParser.OPERATOR_MULTIPLICATIVE);
        }

        public TerminalNode OPERATOR_MULTIPLICATIVE(int i) {
            return getToken(DataWeaveParser.OPERATOR_MULTIPLICATIVE, i);
        }

        public MultiplicativeExpressionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_multiplicativeExpression;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener)
                ((DataWeaveListener) listener).enterMultiplicativeExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener)
                ((DataWeaveListener) listener).exitMultiplicativeExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitMultiplicativeExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    public final MultiplicativeExpressionContext multiplicativeExpression() throws RecognitionException {
        MultiplicativeExpressionContext _localctx = new MultiplicativeExpressionContext(_ctx, getState());
        enterRule(_localctx, 44, RULE_multiplicativeExpression);
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                setState(251);
                typeCoercionExpression();
                setState(256);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 20, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        {
                            {
                                setState(252);
                                match(OPERATOR_MULTIPLICATIVE);
                                setState(253);
                                typeCoercionExpression();
                            }
                        }
                    }
                    setState(258);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 20, _ctx);
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
    public static class TypeCoercionExpressionContext extends ParserRuleContext {
        public UnaryExpressionContext unaryExpression() {
            return getRuleContext(UnaryExpressionContext.class, 0);
        }

        public TerminalNode OPERATOR_TYPE_COERCION() {
            return getToken(DataWeaveParser.OPERATOR_TYPE_COERCION, 0);
        }

        public TypeExpressionContext typeExpression() {
            return getRuleContext(TypeExpressionContext.class, 0);
        }

        public FormatOptionContext formatOption() {
            return getRuleContext(FormatOptionContext.class, 0);
        }

        public TypeCoercionExpressionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_typeCoercionExpression;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterTypeCoercionExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitTypeCoercionExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitTypeCoercionExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    public final TypeCoercionExpressionContext typeCoercionExpression() throws RecognitionException {
        TypeCoercionExpressionContext _localctx = new TypeCoercionExpressionContext(_ctx, getState());
        enterRule(_localctx, 46, RULE_typeCoercionExpression);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(259);
                unaryExpression();
                setState(265);
                _errHandler.sync(this);
                switch (getInterpreter().adaptivePredict(_input, 22, _ctx)) {
                    case 1: {
                        setState(260);
                        match(OPERATOR_TYPE_COERCION);
                        setState(261);
                        typeExpression();
                        setState(263);
                        _errHandler.sync(this);
                        switch (getInterpreter().adaptivePredict(_input, 21, _ctx)) {
                            case 1: {
                                setState(262);
                                formatOption();
                            }
                            break;
                        }
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
    public static class FormatOptionContext extends ParserRuleContext {
        public TerminalNode LCURLY() {
            return getToken(DataWeaveParser.LCURLY, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(DataWeaveParser.IDENTIFIER, 0);
        }

        public TerminalNode COLON() {
            return getToken(DataWeaveParser.COLON, 0);
        }

        public TerminalNode STRING() {
            return getToken(DataWeaveParser.STRING, 0);
        }

        public TerminalNode RCURLY() {
            return getToken(DataWeaveParser.RCURLY, 0);
        }

        public FormatOptionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_formatOption;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterFormatOption(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitFormatOption(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitFormatOption(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FormatOptionContext formatOption() throws RecognitionException {
        FormatOptionContext _localctx = new FormatOptionContext(_ctx, getState());
        enterRule(_localctx, 48, RULE_formatOption);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(267);
                match(LCURLY);
                setState(268);
                match(IDENTIFIER);
                setState(269);
                match(COLON);
                setState(270);
                match(STRING);
                setState(271);
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
    public static class UnaryExpressionContext extends ParserRuleContext {
        public UnaryExpressionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_unaryExpression;
        }

        public UnaryExpressionContext() {
        }

        public void copyFrom(UnaryExpressionContext ctx) {
            super.copyFrom(ctx);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class PrimaryExpressionWrapperContext extends UnaryExpressionContext {
        public PrimaryExpressionContext primaryExpression() {
            return getRuleContext(PrimaryExpressionContext.class, 0);
        }

        public PrimaryExpressionWrapperContext(UnaryExpressionContext ctx) {
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
    public static class SizeOfExpressionWithParenthesesContext extends UnaryExpressionContext {
        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public SizeOfExpressionWithParenthesesContext(UnaryExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener)
                ((DataWeaveListener) listener).enterSizeOfExpressionWithParentheses(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener)
                ((DataWeaveListener) listener).exitSizeOfExpressionWithParentheses(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitSizeOfExpressionWithParentheses(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class UpperExpressionWithParenthesesContext extends UnaryExpressionContext {
        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public UpperExpressionWithParenthesesContext(UnaryExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener)
                ((DataWeaveListener) listener).enterUpperExpressionWithParentheses(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener)
                ((DataWeaveListener) listener).exitUpperExpressionWithParentheses(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitUpperExpressionWithParentheses(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class SizeOfExpressionContext extends UnaryExpressionContext {
        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public SizeOfExpressionContext(UnaryExpressionContext ctx) {
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
    public static class UpperExpressionContext extends UnaryExpressionContext {
        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public UpperExpressionContext(UnaryExpressionContext ctx) {
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
    public static class LowerExpressionContext extends UnaryExpressionContext {
        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public LowerExpressionContext(UnaryExpressionContext ctx) {
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
    public static class LowerExpressionWithParenthesesContext extends UnaryExpressionContext {
        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public LowerExpressionWithParenthesesContext(UnaryExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener)
                ((DataWeaveListener) listener).enterLowerExpressionWithParentheses(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener)
                ((DataWeaveListener) listener).exitLowerExpressionWithParentheses(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitLowerExpressionWithParentheses(this);
            else return visitor.visitChildren(this);
        }
    }

    public final UnaryExpressionContext unaryExpression() throws RecognitionException {
        UnaryExpressionContext _localctx = new UnaryExpressionContext(_ctx, getState());
        enterRule(_localctx, 50, RULE_unaryExpression);
        try {
            setState(295);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 23, _ctx)) {
                case 1:
                    _localctx = new SizeOfExpressionContext(_localctx);
                    enterOuterAlt(_localctx, 1);
                {
                    setState(273);
                    match(T__13);
                    setState(274);
                    expression();
                }
                break;
                case 2:
                    _localctx = new SizeOfExpressionWithParenthesesContext(_localctx);
                    enterOuterAlt(_localctx, 2);
                {
                    setState(275);
                    match(T__13);
                    setState(276);
                    match(T__0);
                    setState(277);
                    expression();
                    setState(278);
                    match(T__1);
                }
                break;
                case 3:
                    _localctx = new UpperExpressionContext(_localctx);
                    enterOuterAlt(_localctx, 3);
                {
                    setState(280);
                    match(T__14);
                    setState(281);
                    expression();
                }
                break;
                case 4:
                    _localctx = new UpperExpressionWithParenthesesContext(_localctx);
                    enterOuterAlt(_localctx, 4);
                {
                    setState(282);
                    match(T__14);
                    setState(283);
                    match(T__0);
                    setState(284);
                    expression();
                    setState(285);
                    match(T__1);
                }
                break;
                case 5:
                    _localctx = new LowerExpressionContext(_localctx);
                    enterOuterAlt(_localctx, 5);
                {
                    setState(287);
                    match(T__15);
                    setState(288);
                    expression();
                }
                break;
                case 6:
                    _localctx = new LowerExpressionWithParenthesesContext(_localctx);
                    enterOuterAlt(_localctx, 6);
                {
                    setState(289);
                    match(T__15);
                    setState(290);
                    match(T__0);
                    setState(291);
                    expression();
                    setState(292);
                    match(T__1);
                }
                break;
                case 7:
                    _localctx = new PrimaryExpressionWrapperContext(_localctx);
                    enterOuterAlt(_localctx, 7);
                {
                    setState(294);
                    primaryExpression(0);
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
    public static class SelectorExpressionWrapperContext extends PrimaryExpressionContext {
        public PrimaryExpressionContext primaryExpression() {
            return getRuleContext(PrimaryExpressionContext.class, 0);
        }

        public SelectorExpressionContext selectorExpression() {
            return getRuleContext(SelectorExpressionContext.class, 0);
        }

        public SelectorExpressionWrapperContext(PrimaryExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener)
                ((DataWeaveListener) listener).enterSelectorExpressionWrapper(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener)
                ((DataWeaveListener) listener).exitSelectorExpressionWrapper(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitSelectorExpressionWrapper(this);
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

    @SuppressWarnings("CheckReturnValue")
    public static class IndexIdentifierExpressionContext extends PrimaryExpressionContext {
        public TerminalNode INDEX_IDENTIFIER() {
            return getToken(DataWeaveParser.INDEX_IDENTIFIER, 0);
        }

        public IndexIdentifierExpressionContext(PrimaryExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener)
                ((DataWeaveListener) listener).enterIndexIdentifierExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener)
                ((DataWeaveListener) listener).exitIndexIdentifierExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitIndexIdentifierExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ValueIdentifierExpressionContext extends PrimaryExpressionContext {
        public TerminalNode VALUE_IDENTIFIER() {
            return getToken(DataWeaveParser.VALUE_IDENTIFIER, 0);
        }

        public ValueIdentifierExpressionContext(PrimaryExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener)
                ((DataWeaveListener) listener).enterValueIdentifierExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener)
                ((DataWeaveListener) listener).exitValueIdentifierExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitValueIdentifierExpression(this);
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
        return primaryExpression(0);
    }

    private PrimaryExpressionContext primaryExpression(int _p) throws RecognitionException {
        ParserRuleContext _parentctx = _ctx;
        int _parentState = getState();
        PrimaryExpressionContext _localctx = new PrimaryExpressionContext(_ctx, _parentState);
        PrimaryExpressionContext _prevctx = _localctx;
        int _startState = 52;
        enterRecursionRule(_localctx, 52, RULE_primaryExpression, _p);
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                setState(307);
                _errHandler.sync(this);
                switch (getInterpreter().adaptivePredict(_input, 24, _ctx)) {
                    case 1: {
                        _localctx = new LambdaExpressionContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;

                        setState(298);
                        inlineLambda();
                    }
                    break;
                    case 2: {
                        _localctx = new GroupedExpressionContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(299);
                        grouped();
                    }
                    break;
                    case 3: {
                        _localctx = new LiteralExpressionContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(300);
                        literal();
                    }
                    break;
                    case 4: {
                        _localctx = new FunctionCallExpressionContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(301);
                        functionCall();
                    }
                    break;
                    case 5: {
                        _localctx = new ArrayExpressionContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(302);
                        array();
                    }
                    break;
                    case 6: {
                        _localctx = new ObjectExpressionContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(303);
                        object();
                    }
                    break;
                    case 7: {
                        _localctx = new IdentifierExpressionContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(304);
                        match(IDENTIFIER);
                    }
                    break;
                    case 8: {
                        _localctx = new ValueIdentifierExpressionContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(305);
                        match(VALUE_IDENTIFIER);
                    }
                    break;
                    case 9: {
                        _localctx = new IndexIdentifierExpressionContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(306);
                        match(INDEX_IDENTIFIER);
                    }
                    break;
                }
                _ctx.stop = _input.LT(-1);
                setState(313);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 25, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        if (_parseListeners != null) triggerExitRuleEvent();
                        _prevctx = _localctx;
                        {
                            {
                                _localctx = new SelectorExpressionWrapperContext(new PrimaryExpressionContext(_parentctx, _parentState));
                                pushNewRecursionContext(_localctx, _startState, RULE_primaryExpression);
                                setState(309);
                                if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
                                setState(310);
                                selectorExpression();
                            }
                        }
                    }
                    setState(315);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 25, _ctx);
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
        enterRule(_localctx, 54, RULE_grouped);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(316);
                match(T__0);
                setState(317);
                expression();
                setState(318);
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

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
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
        enterRule(_localctx, 56, RULE_selectorExpression);
        try {
            setState(335);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 26, _ctx)) {
                case 1:
                    _localctx = new SingleValueSelectorContext(_localctx);
                    enterOuterAlt(_localctx, 1);
                {
                    setState(320);
                    match(DOT);
                    setState(321);
                    match(IDENTIFIER);
                }
                break;
                case 2:
                    _localctx = new MultiValueSelectorContext(_localctx);
                    enterOuterAlt(_localctx, 2);
                {
                    setState(322);
                    match(DOT);
                    setState(323);
                    match(STAR);
                    setState(324);
                    match(IDENTIFIER);
                }
                break;
                case 3:
                    _localctx = new DescendantsSelectorContext(_localctx);
                    enterOuterAlt(_localctx, 3);
                {
                    setState(325);
                    match(OPERATOR_RANGE);
                    setState(326);
                    match(IDENTIFIER);
                }
                break;
                case 4:
                    _localctx = new IndexedSelectorContext(_localctx);
                    enterOuterAlt(_localctx, 4);
                {
                    setState(327);
                    match(LSQUARE);
                    setState(328);
                    expression();
                    setState(329);
                    match(RSQUARE);
                }
                break;
                case 5:
                    _localctx = new AttributeSelectorContext(_localctx);
                    enterOuterAlt(_localctx, 5);
                {
                    setState(331);
                    match(DOT);
                    setState(332);
                    match(AT);
                    setState(333);
                    match(IDENTIFIER);
                }
                break;
                case 6:
                    _localctx = new ExistenceQuerySelectorContext(_localctx);
                    enterOuterAlt(_localctx, 6);
                {
                    setState(334);
                    match(QUESTION);
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
        enterRule(_localctx, 58, RULE_literal);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(337);
                _la = _input.LA(1);
                if (!((((_la) & ~0x3f) == 0 && ((1L << _la) & 2061617856512L) != 0))) {
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
        enterRule(_localctx, 60, RULE_array);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(339);
                match(LSQUARE);
                setState(348);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 90052612964354L) != 0)) {
                    {
                        setState(340);
                        expression();
                        setState(345);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        while (_la == COMMA) {
                            {
                                {
                                    setState(341);
                                    match(COMMA);
                                    setState(342);
                                    expression();
                                }
                            }
                            setState(347);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        }
                    }
                }

                setState(350);
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
        enterRule(_localctx, 62, RULE_object);
        int _la;
        try {
            setState(364);
            _errHandler.sync(this);
            switch (_input.LA(1)) {
                case LCURLY:
                    _localctx = new MultiKeyValueObjectContext(_localctx);
                    enterOuterAlt(_localctx, 1);
                {
                    setState(352);
                    match(LCURLY);
                    setState(353);
                    keyValue();
                    setState(358);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                    while (_la == COMMA) {
                        {
                            {
                                setState(354);
                                match(COMMA);
                                setState(355);
                                keyValue();
                            }
                        }
                        setState(360);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                    }
                    setState(361);
                    match(RCURLY);
                }
                break;
                case IDENTIFIER:
                    _localctx = new SingleKeyValueObjectContext(_localctx);
                    enterOuterAlt(_localctx, 2);
                {
                    setState(363);
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
        enterRule(_localctx, 64, RULE_keyValue);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(366);
                match(IDENTIFIER);
                setState(367);
                match(COLON);
                setState(368);
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
        enterRule(_localctx, 66, RULE_functionCall);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(370);
                match(IDENTIFIER);
                setState(371);
                match(T__0);
                setState(380);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 90052612964354L) != 0)) {
                    {
                        setState(372);
                        expression();
                        setState(377);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        while (_la == COMMA) {
                            {
                                {
                                    setState(373);
                                    match(COMMA);
                                    setState(374);
                                    expression();
                                }
                            }
                            setState(379);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        }
                    }
                }

                setState(382);
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
    public static class TypeExpressionContext extends ParserRuleContext {
        public TerminalNode COLON() {
            return getToken(DataWeaveParser.COLON, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(DataWeaveParser.IDENTIFIER, 0);
        }

        public TypeExpressionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_typeExpression;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).enterTypeExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof DataWeaveListener) ((DataWeaveListener) listener).exitTypeExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof DataWeaveVisitor)
                return ((DataWeaveVisitor<? extends T>) visitor).visitTypeExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    public final TypeExpressionContext typeExpression() throws RecognitionException {
        TypeExpressionContext _localctx = new TypeExpressionContext(_ctx, getState());
        enterRule(_localctx, 68, RULE_typeExpression);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(384);
                match(COLON);
                setState(385);
                match(IDENTIFIER);
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
            case 26:
                return primaryExpression_sempred((PrimaryExpressionContext) _localctx, predIndex);
        }
        return true;
    }

    private boolean primaryExpression_sempred(PrimaryExpressionContext _localctx, int predIndex) {
        switch (predIndex) {
            case 0:
                return precpred(_ctx, 1);
        }
        return true;
    }

    public static final String _serializedATN =
            "\u0004\u00016\u0184\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002" +
                    "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002" +
                    "\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002" +
                    "\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002" +
                    "\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f" +
                    "\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012" +
                    "\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015" +
                    "\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018" +
                    "\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b" +
                    "\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007\u001e" +
                    "\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!\u0007!\u0002\"\u0007\"\u0001" +
                    "\u0000\u0003\u0000H\b\u0000\u0001\u0000\u0001\u0000\u0003\u0000L\b\u0000" +
                    "\u0001\u0000\u0005\u0000O\b\u0000\n\u0000\f\u0000R\t\u0000\u0001\u0000" +
                    "\u0001\u0000\u0001\u0001\u0001\u0001\u0005\u0001X\b\u0001\n\u0001\f\u0001" +
                    "[\t\u0001\u0004\u0001]\b\u0001\u000b\u0001\f\u0001^\u0001\u0002\u0001" +
                    "\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0003\u0002g\b" +
                    "\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001" +
                    "\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001" +
                    "\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001" +
                    "\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0003\b\u0080\b\b\u0001" +
                    "\b\u0001\b\u0001\b\u0001\t\u0001\t\u0005\t\u0087\b\t\n\t\f\t\u008a\t\t" +
                    "\u0001\n\u0001\n\u0003\n\u008e\b\n\u0001\u000b\u0001\u000b\u0001\u000b" +
                    "\u0001\u000b\u0001\u000b\u0001\u000b\u0004\u000b\u0096\b\u000b\u000b\u000b" +
                    "\f\u000b\u0097\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b" +
                    "\u0001\u000b\u0004\u000b\u00a0\b\u000b\u000b\u000b\f\u000b\u00a1\u0003" +
                    "\u000b\u00a4\b\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0003" +
                    "\f\u00ac\b\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\u000e" +
                    "\u0001\u000e\u0001\u000e\u0005\u000e\u00b7\b\u000e\n\u000e\f\u000e\u00ba" +
                    "\t\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010\u0001" +
                    "\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001" +
                    "\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001" +
                    "\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0003\u0010\u00d2" +
                    "\b\u0010\u0001\u0011\u0001\u0011\u0001\u0011\u0005\u0011\u00d7\b\u0011" +
                    "\n\u0011\f\u0011\u00da\t\u0011\u0001\u0012\u0001\u0012\u0001\u0012\u0005" +
                    "\u0012\u00df\b\u0012\n\u0012\f\u0012\u00e2\t\u0012\u0001\u0013\u0001\u0013" +
                    "\u0001\u0013\u0005\u0013\u00e7\b\u0013\n\u0013\f\u0013\u00ea\t\u0013\u0001" +
                    "\u0014\u0001\u0014\u0001\u0014\u0005\u0014\u00ef\b\u0014\n\u0014\f\u0014" +
                    "\u00f2\t\u0014\u0001\u0015\u0001\u0015\u0001\u0015\u0005\u0015\u00f7\b" +
                    "\u0015\n\u0015\f\u0015\u00fa\t\u0015\u0001\u0016\u0001\u0016\u0001\u0016" +
                    "\u0005\u0016\u00ff\b\u0016\n\u0016\f\u0016\u0102\t\u0016\u0001\u0017\u0001" +
                    "\u0017\u0001\u0017\u0001\u0017\u0003\u0017\u0108\b\u0017\u0003\u0017\u010a" +
                    "\b\u0017\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001" +
                    "\u0018\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001" +
                    "\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001" +
                    "\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001" +
                    "\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0003\u0019\u0128" +
                    "\b\u0019\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001" +
                    "\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0003\u001a\u0134" +
                    "\b\u001a\u0001\u001a\u0001\u001a\u0005\u001a\u0138\b\u001a\n\u001a\f\u001a" +
                    "\u013b\t\u001a\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001c" +
                    "\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c" +
                    "\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c" +
                    "\u0001\u001c\u0001\u001c\u0003\u001c\u0150\b\u001c\u0001\u001d\u0001\u001d" +
                    "\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001e\u0005\u001e\u0158\b\u001e" +
                    "\n\u001e\f\u001e\u015b\t\u001e\u0003\u001e\u015d\b\u001e\u0001\u001e\u0001" +
                    "\u001e\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0005\u001f\u0165" +
                    "\b\u001f\n\u001f\f\u001f\u0168\t\u001f\u0001\u001f\u0001\u001f\u0001\u001f" +
                    "\u0003\u001f\u016d\b\u001f\u0001 \u0001 \u0001 \u0001 \u0001!\u0001!\u0001" +
                    "!\u0001!\u0001!\u0005!\u0178\b!\n!\f!\u017b\t!\u0003!\u017d\b!\u0001!" +
                    "\u0001!\u0001\"\u0001\"\u0001\"\u0001\"\u0000\u00014#\u0000\u0002\u0004" +
                    "\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"" +
                    "$&(*,.02468:<>@BD\u0000\u0002\u0001\u000012\u0002\u0000\u0019\u0019%(" +
                    "\u019a\u0000G\u0001\u0000\u0000\u0000\u0002\\\u0001\u0000\u0000\u0000" +
                    "\u0004f\u0001\u0000\u0000\u0000\u0006h\u0001\u0000\u0000\u0000\bk\u0001" +
                    "\u0000\u0000\u0000\nn\u0001\u0000\u0000\u0000\fr\u0001\u0000\u0000\u0000" +
                    "\u000ev\u0001\u0000\u0000\u0000\u0010{\u0001\u0000\u0000\u0000\u0012\u0084" +
                    "\u0001\u0000\u0000\u0000\u0014\u008d\u0001\u0000\u0000\u0000\u0016\u00a3" +
                    "\u0001\u0000\u0000\u0000\u0018\u00ab\u0001\u0000\u0000\u0000\u001a\u00ad" +
                    "\u0001\u0000\u0000\u0000\u001c\u00b3\u0001\u0000\u0000\u0000\u001e\u00bb" +
                    "\u0001\u0000\u0000\u0000 \u00d1\u0001\u0000\u0000\u0000\"\u00d3\u0001" +
                    "\u0000\u0000\u0000$\u00db\u0001\u0000\u0000\u0000&\u00e3\u0001\u0000\u0000" +
                    "\u0000(\u00eb\u0001\u0000\u0000\u0000*\u00f3\u0001\u0000\u0000\u0000," +
                    "\u00fb\u0001\u0000\u0000\u0000.\u0103\u0001\u0000\u0000\u00000\u010b\u0001" +
                    "\u0000\u0000\u00002\u0127\u0001\u0000\u0000\u00004\u0133\u0001\u0000\u0000" +
                    "\u00006\u013c\u0001\u0000\u0000\u00008\u014f\u0001\u0000\u0000\u0000:" +
                    "\u0151\u0001\u0000\u0000\u0000<\u0153\u0001\u0000\u0000\u0000>\u016c\u0001" +
                    "\u0000\u0000\u0000@\u016e\u0001\u0000\u0000\u0000B\u0172\u0001\u0000\u0000" +
                    "\u0000D\u0180\u0001\u0000\u0000\u0000FH\u0003\u0002\u0001\u0000GF\u0001" +
                    "\u0000\u0000\u0000GH\u0001\u0000\u0000\u0000HI\u0001\u0000\u0000\u0000" +
                    "IK\u00050\u0000\u0000JL\u0003\u0012\t\u0000KJ\u0001\u0000\u0000\u0000" +
                    "KL\u0001\u0000\u0000\u0000LP\u0001\u0000\u0000\u0000MO\u00052\u0000\u0000" +
                    "NM\u0001\u0000\u0000\u0000OR\u0001\u0000\u0000\u0000PN\u0001\u0000\u0000" +
                    "\u0000PQ\u0001\u0000\u0000\u0000QS\u0001\u0000\u0000\u0000RP\u0001\u0000" +
                    "\u0000\u0000ST\u0005\u0000\u0000\u0001T\u0001\u0001\u0000\u0000\u0000" +
                    "UY\u0003\u0004\u0002\u0000VX\u0007\u0000\u0000\u0000WV\u0001\u0000\u0000" +
                    "\u0000X[\u0001\u0000\u0000\u0000YW\u0001\u0000\u0000\u0000YZ\u0001\u0000" +
                    "\u0000\u0000Z]\u0001\u0000\u0000\u0000[Y\u0001\u0000\u0000\u0000\\U\u0001" +
                    "\u0000\u0000\u0000]^\u0001\u0000\u0000\u0000^\\\u0001\u0000\u0000\u0000" +
                    "^_\u0001\u0000\u0000\u0000_\u0003\u0001\u0000\u0000\u0000`g\u0003\u0006" +
                    "\u0003\u0000ag\u0003\b\u0004\u0000bg\u0003\n\u0005\u0000cg\u0003\f\u0006" +
                    "\u0000dg\u0003\u000e\u0007\u0000eg\u0003\u0010\b\u0000f`\u0001\u0000\u0000" +
                    "\u0000fa\u0001\u0000\u0000\u0000fb\u0001\u0000\u0000\u0000fc\u0001\u0000" +
                    "\u0000\u0000fd\u0001\u0000\u0000\u0000fe\u0001\u0000\u0000\u0000g\u0005" +
                    "\u0001\u0000\u0000\u0000hi\u0005\u0016\u0000\u0000ij\u0005%\u0000\u0000" +
                    "j\u0007\u0001\u0000\u0000\u0000kl\u0005\u0015\u0000\u0000lm\u0005$\u0000" +
                    "\u0000m\t\u0001\u0000\u0000\u0000no\u0005\u0013\u0000\u0000op\u0005 \u0000" +
                    "\u0000pq\u0005$\u0000\u0000q\u000b\u0001\u0000\u0000\u0000rs\u0005\u0014" +
                    "\u0000\u0000st\u0005 \u0000\u0000tu\u0005#\u0000\u0000u\r\u0001\u0000" +
                    "\u0000\u0000vw\u0005\u0011\u0000\u0000wx\u0005 \u0000\u0000xy\u0005\u0017" +
                    "\u0000\u0000yz\u0003\u0014\n\u0000z\u000f\u0001\u0000\u0000\u0000{|\u0005" +
                    "\u0012\u0000\u0000|}\u0005 \u0000\u0000}\u007f\u0005\u0001\u0000\u0000" +
                    "~\u0080\u0003\u001c\u000e\u0000\u007f~\u0001\u0000\u0000\u0000\u007f\u0080" +
                    "\u0001\u0000\u0000\u0000\u0080\u0081\u0001\u0000\u0000\u0000\u0081\u0082" +
                    "\u0005\u0002\u0000\u0000\u0082\u0083\u0003\u0014\n\u0000\u0083\u0011\u0001" +
                    "\u0000\u0000\u0000\u0084\u0088\u0003\u0014\n\u0000\u0085\u0087\u00052" +
                    "\u0000\u0000\u0086\u0085\u0001\u0000\u0000\u0000\u0087\u008a\u0001\u0000" +
                    "\u0000\u0000\u0088\u0086\u0001\u0000\u0000\u0000\u0088\u0089\u0001\u0000" +
                    "\u0000\u0000\u0089\u0013\u0001\u0000\u0000\u0000\u008a\u0088\u0001\u0000" +
                    "\u0000\u0000\u008b\u008e\u0003\u001e\u000f\u0000\u008c\u008e\u0003\u0016" +
                    "\u000b\u0000\u008d\u008b\u0001\u0000\u0000\u0000\u008d\u008c\u0001\u0000" +
                    "\u0000\u0000\u008e\u0015\u0001\u0000\u0000\u0000\u008f\u0095\u0003\u001e" +
                    "\u000f\u0000\u0090\u0091\u0005\u0003\u0000\u0000\u0091\u0092\u0003\u001e" +
                    "\u000f\u0000\u0092\u0093\u0005\u0004\u0000\u0000\u0093\u0094\u0003\u001e" +
                    "\u000f\u0000\u0094\u0096\u0001\u0000\u0000\u0000\u0095\u0090\u0001\u0000" +
                    "\u0000\u0000\u0096\u0097\u0001\u0000\u0000\u0000\u0097\u0095\u0001\u0000" +
                    "\u0000\u0000\u0097\u0098\u0001\u0000\u0000\u0000\u0098\u00a4\u0001\u0000" +
                    "\u0000\u0000\u0099\u009f\u0003\u001e\u000f\u0000\u009a\u009b\u0005\u0005" +
                    "\u0000\u0000\u009b\u009c\u0003\u001e\u000f\u0000\u009c\u009d\u0005\u0004" +
                    "\u0000\u0000\u009d\u009e\u0003\u001e\u000f\u0000\u009e\u00a0\u0001\u0000" +
                    "\u0000\u0000\u009f\u009a\u0001\u0000\u0000\u0000\u00a0\u00a1\u0001\u0000" +
                    "\u0000\u0000\u00a1\u009f\u0001\u0000\u0000\u0000\u00a1\u00a2\u0001\u0000" +
                    "\u0000\u0000\u00a2\u00a4\u0001\u0000\u0000\u0000\u00a3\u008f\u0001\u0000" +
                    "\u0000\u0000\u00a3\u0099\u0001\u0000\u0000\u0000\u00a4\u0017\u0001\u0000" +
                    "\u0000\u0000\u00a5\u00ac\u0003\u001a\r\u0000\u00a6\u00ac\u0003\u0014\n" +
                    "\u0000\u00a7\u00a8\u0005\u0001\u0000\u0000\u00a8\u00a9\u0003\u0018\f\u0000" +
                    "\u00a9\u00aa\u0005\u0002\u0000\u0000\u00aa\u00ac\u0001\u0000\u0000\u0000" +
                    "\u00ab\u00a5\u0001\u0000\u0000\u0000\u00ab\u00a6\u0001\u0000\u0000\u0000" +
                    "\u00ab\u00a7\u0001\u0000\u0000\u0000\u00ac\u0019\u0001\u0000\u0000\u0000" +
                    "\u00ad\u00ae\u0005\u0001\u0000\u0000\u00ae\u00af\u0003\u001c\u000e\u0000" +
                    "\u00af\u00b0\u0005\u0002\u0000\u0000\u00b0\u00b1\u0005\u0018\u0000\u0000" +
                    "\u00b1\u00b2\u0003\u0014\n\u0000\u00b2\u001b\u0001\u0000\u0000\u0000\u00b3" +
                    "\u00b8\u0005 \u0000\u0000\u00b4\u00b5\u0005+\u0000\u0000\u00b5\u00b7\u0005" +
                    " \u0000\u0000\u00b6\u00b4\u0001\u0000\u0000\u0000\u00b7\u00ba\u0001\u0000" +
                    "\u0000\u0000\u00b8\u00b6\u0001\u0000\u0000\u0000\u00b8\u00b9\u0001\u0000" +
                    "\u0000\u0000\u00b9\u001d\u0001\u0000\u0000\u0000\u00ba\u00b8\u0001\u0000" +
                    "\u0000\u0000\u00bb\u00bc\u0003\"\u0011\u0000\u00bc\u00bd\u0003 \u0010" +
                    "\u0000\u00bd\u001f\u0001\u0000\u0000\u0000\u00be\u00bf\u0005\u0006\u0000" +
                    "\u0000\u00bf\u00c0\u0003\u0018\f\u0000\u00c0\u00c1\u0003 \u0010\u0000" +
                    "\u00c1\u00d2\u0001\u0000\u0000\u0000\u00c2\u00c3\u0005\u0007\u0000\u0000" +
                    "\u00c3\u00c4\u0003\u0018\f\u0000\u00c4\u00c5\u0003 \u0010\u0000\u00c5" +
                    "\u00d2\u0001\u0000\u0000\u0000\u00c6\u00c7\u0005\b\u0000\u0000\u00c7\u00c8" +
                    "\u0003\u0018\f\u0000\u00c8\u00c9\u0003 \u0010\u0000\u00c9\u00d2\u0001" +
                    "\u0000\u0000\u0000\u00ca\u00cb\u0005\t\u0000\u0000\u00cb\u00cc\u0005(" +
                    "\u0000\u0000\u00cc\u00cd\u0005\n\u0000\u0000\u00cd\u00d2\u0003\u0014\n" +
                    "\u0000\u00ce\u00cf\u0005\u000b\u0000\u0000\u00cf\u00d2\u0003\u0014\n\u0000" +
                    "\u00d0\u00d2\u0001\u0000\u0000\u0000\u00d1\u00be\u0001\u0000\u0000\u0000" +
                    "\u00d1\u00c2\u0001\u0000\u0000\u0000\u00d1\u00c6\u0001\u0000\u0000\u0000" +
                    "\u00d1\u00ca\u0001\u0000\u0000\u0000\u00d1\u00ce\u0001\u0000\u0000\u0000" +
                    "\u00d1\u00d0\u0001\u0000\u0000\u0000\u00d2!\u0001\u0000\u0000\u0000\u00d3" +
                    "\u00d8\u0003$\u0012\u0000\u00d4\u00d5\u0005\f\u0000\u0000\u00d5\u00d7" +
                    "\u0003$\u0012\u0000\u00d6\u00d4\u0001\u0000\u0000\u0000\u00d7\u00da\u0001" +
                    "\u0000\u0000\u0000\u00d8\u00d6\u0001\u0000\u0000\u0000\u00d8\u00d9\u0001" +
                    "\u0000\u0000\u0000\u00d9#\u0001\u0000\u0000\u0000\u00da\u00d8\u0001\u0000" +
                    "\u0000\u0000\u00db\u00e0\u0003&\u0013\u0000\u00dc\u00dd\u0005\r\u0000" +
                    "\u0000\u00dd\u00df\u0003&\u0013\u0000\u00de\u00dc\u0001\u0000\u0000\u0000" +
                    "\u00df\u00e2\u0001\u0000\u0000\u0000\u00e0\u00de\u0001\u0000\u0000\u0000" +
                    "\u00e0\u00e1\u0001\u0000\u0000\u0000\u00e1%\u0001\u0000\u0000\u0000\u00e2" +
                    "\u00e0\u0001\u0000\u0000\u0000\u00e3\u00e8\u0003(\u0014\u0000\u00e4\u00e5" +
                    "\u0005\u001a\u0000\u0000\u00e5\u00e7\u0003(\u0014\u0000\u00e6\u00e4\u0001" +
                    "\u0000\u0000\u0000\u00e7\u00ea\u0001\u0000\u0000\u0000\u00e8\u00e6\u0001" +
                    "\u0000\u0000\u0000\u00e8\u00e9\u0001\u0000\u0000\u0000\u00e9\'\u0001\u0000" +
                    "\u0000\u0000\u00ea\u00e8\u0001\u0000\u0000\u0000\u00eb\u00f0\u0003*\u0015" +
                    "\u0000\u00ec\u00ed\u0005\u001b\u0000\u0000\u00ed\u00ef\u0003*\u0015\u0000" +
                    "\u00ee\u00ec\u0001\u0000\u0000\u0000\u00ef\u00f2\u0001\u0000\u0000\u0000" +
                    "\u00f0\u00ee\u0001\u0000\u0000\u0000\u00f0\u00f1\u0001\u0000\u0000\u0000" +
                    "\u00f1)\u0001\u0000\u0000\u0000\u00f2\u00f0\u0001\u0000\u0000\u0000\u00f3" +
                    "\u00f8\u0003,\u0016\u0000\u00f4\u00f5\u0005\u001d\u0000\u0000\u00f5\u00f7" +
                    "\u0003,\u0016\u0000\u00f6\u00f4\u0001\u0000\u0000\u0000\u00f7\u00fa\u0001" +
                    "\u0000\u0000\u0000\u00f8\u00f6\u0001\u0000\u0000\u0000\u00f8\u00f9\u0001" +
                    "\u0000\u0000\u0000\u00f9+\u0001\u0000\u0000\u0000\u00fa\u00f8\u0001\u0000" +
                    "\u0000\u0000\u00fb\u0100\u0003.\u0017\u0000\u00fc\u00fd\u0005\u001c\u0000" +
                    "\u0000\u00fd\u00ff\u0003.\u0017\u0000\u00fe\u00fc\u0001\u0000\u0000\u0000" +
                    "\u00ff\u0102\u0001\u0000\u0000\u0000\u0100\u00fe\u0001\u0000\u0000\u0000" +
                    "\u0100\u0101\u0001\u0000\u0000\u0000\u0101-\u0001\u0000\u0000\u0000\u0102" +
                    "\u0100\u0001\u0000\u0000\u0000\u0103\u0109\u00032\u0019\u0000\u0104\u0105" +
                    "\u0005\u001e\u0000\u0000\u0105\u0107\u0003D\"\u0000\u0106\u0108\u0003" +
                    "0\u0018\u0000\u0107\u0106\u0001\u0000\u0000\u0000\u0107\u0108\u0001\u0000" +
                    "\u0000\u0000\u0108\u010a\u0001\u0000\u0000\u0000\u0109\u0104\u0001\u0000" +
                    "\u0000\u0000\u0109\u010a\u0001\u0000\u0000\u0000\u010a/\u0001\u0000\u0000" +
                    "\u0000\u010b\u010c\u0005,\u0000\u0000\u010c\u010d\u0005 \u0000\u0000\u010d" +
                    "\u010e\u0005*\u0000\u0000\u010e\u010f\u0005&\u0000\u0000\u010f\u0110\u0005" +
                    "-\u0000\u0000\u01101\u0001\u0000\u0000\u0000\u0111\u0112\u0005\u000e\u0000" +
                    "\u0000\u0112\u0128\u0003\u0014\n\u0000\u0113\u0114\u0005\u000e\u0000\u0000" +
                    "\u0114\u0115\u0005\u0001\u0000\u0000\u0115\u0116\u0003\u0014\n\u0000\u0116" +
                    "\u0117\u0005\u0002\u0000\u0000\u0117\u0128\u0001\u0000\u0000\u0000\u0118" +
                    "\u0119\u0005\u000f\u0000\u0000\u0119\u0128\u0003\u0014\n\u0000\u011a\u011b" +
                    "\u0005\u000f\u0000\u0000\u011b\u011c\u0005\u0001\u0000\u0000\u011c\u011d" +
                    "\u0003\u0014\n\u0000\u011d\u011e\u0005\u0002\u0000\u0000\u011e\u0128\u0001" +
                    "\u0000\u0000\u0000\u011f\u0120\u0005\u0010\u0000\u0000\u0120\u0128\u0003" +
                    "\u0014\n\u0000\u0121\u0122\u0005\u0010\u0000\u0000\u0122\u0123\u0005\u0001" +
                    "\u0000\u0000\u0123\u0124\u0003\u0014\n\u0000\u0124\u0125\u0005\u0002\u0000" +
                    "\u0000\u0125\u0128\u0001\u0000\u0000\u0000\u0126\u0128\u00034\u001a\u0000" +
                    "\u0127\u0111\u0001\u0000\u0000\u0000\u0127\u0113\u0001\u0000\u0000\u0000" +
                    "\u0127\u0118\u0001\u0000\u0000\u0000\u0127\u011a\u0001\u0000\u0000\u0000" +
                    "\u0127\u011f\u0001\u0000\u0000\u0000\u0127\u0121\u0001\u0000\u0000\u0000" +
                    "\u0127\u0126\u0001\u0000\u0000\u0000\u01283\u0001\u0000\u0000\u0000\u0129" +
                    "\u012a\u0006\u001a\uffff\uffff\u0000\u012a\u0134\u0003\u001a\r\u0000\u012b" +
                    "\u0134\u00036\u001b\u0000\u012c\u0134\u0003:\u001d\u0000\u012d\u0134\u0003" +
                    "B!\u0000\u012e\u0134\u0003<\u001e\u0000\u012f\u0134\u0003>\u001f\u0000" +
                    "\u0130\u0134\u0005 \u0000\u0000\u0131\u0134\u0005\"\u0000\u0000\u0132" +
                    "\u0134\u0005!\u0000\u0000\u0133\u0129\u0001\u0000\u0000\u0000\u0133\u012b" +
                    "\u0001\u0000\u0000\u0000\u0133\u012c\u0001\u0000\u0000\u0000\u0133\u012d" +
                    "\u0001\u0000\u0000\u0000\u0133\u012e\u0001\u0000\u0000\u0000\u0133\u012f" +
                    "\u0001\u0000\u0000\u0000\u0133\u0130\u0001\u0000\u0000\u0000\u0133\u0131" +
                    "\u0001\u0000\u0000\u0000\u0133\u0132\u0001\u0000\u0000\u0000\u0134\u0139" +
                    "\u0001\u0000\u0000\u0000\u0135\u0136\n\u0001\u0000\u0000\u0136\u0138\u0003" +
                    "8\u001c\u0000\u0137\u0135\u0001\u0000\u0000\u0000\u0138\u013b\u0001\u0000" +
                    "\u0000\u0000\u0139\u0137\u0001\u0000\u0000\u0000\u0139\u013a\u0001\u0000" +
                    "\u0000\u0000\u013a5\u0001\u0000\u0000\u0000\u013b\u0139\u0001\u0000\u0000" +
                    "\u0000\u013c\u013d\u0005\u0001\u0000\u0000\u013d\u013e\u0003\u0014\n\u0000" +
                    "\u013e\u013f\u0005\u0002\u0000\u0000\u013f7\u0001\u0000\u0000\u0000\u0140" +
                    "\u0141\u0005)\u0000\u0000\u0141\u0150\u0005 \u0000\u0000\u0142\u0143\u0005" +
                    ")\u0000\u0000\u0143\u0144\u00054\u0000\u0000\u0144\u0150\u0005 \u0000" +
                    "\u0000\u0145\u0146\u0005\u001f\u0000\u0000\u0146\u0150\u0005 \u0000\u0000" +
                    "\u0147\u0148\u0005.\u0000\u0000\u0148\u0149\u0003\u0014\n\u0000\u0149" +
                    "\u014a\u0005/\u0000\u0000\u014a\u0150\u0001\u0000\u0000\u0000\u014b\u014c" +
                    "\u0005)\u0000\u0000\u014c\u014d\u00055\u0000\u0000\u014d\u0150\u0005 " +
                    "\u0000\u0000\u014e\u0150\u00056\u0000\u0000\u014f\u0140\u0001\u0000\u0000" +
                    "\u0000\u014f\u0142\u0001\u0000\u0000\u0000\u014f\u0145\u0001\u0000\u0000" +
                    "\u0000\u014f\u0147\u0001\u0000\u0000\u0000\u014f\u014b\u0001\u0000\u0000" +
                    "\u0000\u014f\u014e\u0001\u0000\u0000\u0000\u01509\u0001\u0000\u0000\u0000" +
                    "\u0151\u0152\u0007\u0001\u0000\u0000\u0152;\u0001\u0000\u0000\u0000\u0153" +
                    "\u015c\u0005.\u0000\u0000\u0154\u0159\u0003\u0014\n\u0000\u0155\u0156" +
                    "\u0005+\u0000\u0000\u0156\u0158\u0003\u0014\n\u0000\u0157\u0155\u0001" +
                    "\u0000\u0000\u0000\u0158\u015b\u0001\u0000\u0000\u0000\u0159\u0157\u0001" +
                    "\u0000\u0000\u0000\u0159\u015a\u0001\u0000\u0000\u0000\u015a\u015d\u0001" +
                    "\u0000\u0000\u0000\u015b\u0159\u0001\u0000\u0000\u0000\u015c\u0154\u0001" +
                    "\u0000\u0000\u0000\u015c\u015d\u0001\u0000\u0000\u0000\u015d\u015e\u0001" +
                    "\u0000\u0000\u0000\u015e\u015f\u0005/\u0000\u0000\u015f=\u0001\u0000\u0000" +
                    "\u0000\u0160\u0161\u0005,\u0000\u0000\u0161\u0166\u0003@ \u0000\u0162" +
                    "\u0163\u0005+\u0000\u0000\u0163\u0165\u0003@ \u0000\u0164\u0162\u0001" +
                    "\u0000\u0000\u0000\u0165\u0168\u0001\u0000\u0000\u0000\u0166\u0164\u0001" +
                    "\u0000\u0000\u0000\u0166\u0167\u0001\u0000\u0000\u0000\u0167\u0169\u0001" +
                    "\u0000\u0000\u0000\u0168\u0166\u0001\u0000\u0000\u0000\u0169\u016a\u0005" +
                    "-\u0000\u0000\u016a\u016d\u0001\u0000\u0000\u0000\u016b\u016d\u0003@ " +
                    "\u0000\u016c\u0160\u0001\u0000\u0000\u0000\u016c\u016b\u0001\u0000\u0000" +
                    "\u0000\u016d?\u0001\u0000\u0000\u0000\u016e\u016f\u0005 \u0000\u0000\u016f" +
                    "\u0170\u0005*\u0000\u0000\u0170\u0171\u0003\u0014\n\u0000\u0171A\u0001" +
                    "\u0000\u0000\u0000\u0172\u0173\u0005 \u0000\u0000\u0173\u017c\u0005\u0001" +
                    "\u0000\u0000\u0174\u0179\u0003\u0014\n\u0000\u0175\u0176\u0005+\u0000" +
                    "\u0000\u0176\u0178\u0003\u0014\n\u0000\u0177\u0175\u0001\u0000\u0000\u0000" +
                    "\u0178\u017b\u0001\u0000\u0000\u0000\u0179\u0177\u0001\u0000\u0000\u0000" +
                    "\u0179\u017a\u0001\u0000\u0000\u0000\u017a\u017d\u0001\u0000\u0000\u0000" +
                    "\u017b\u0179\u0001\u0000\u0000\u0000\u017c\u0174\u0001\u0000\u0000\u0000" +
                    "\u017c\u017d\u0001\u0000\u0000\u0000\u017d\u017e\u0001\u0000\u0000\u0000" +
                    "\u017e\u017f\u0005\u0002\u0000\u0000\u017fC\u0001\u0000\u0000\u0000\u0180" +
                    "\u0181\u0005*\u0000\u0000\u0181\u0182\u0005 \u0000\u0000\u0182E\u0001" +
                    "\u0000\u0000\u0000!GKPY^f\u007f\u0088\u008d\u0097\u00a1\u00a3\u00ab\u00b8" +
                    "\u00d1\u00d8\u00e0\u00e8\u00f0\u00f8\u0100\u0107\u0109\u0127\u0133\u0139" +
                    "\u014f\u0159\u015c\u0166\u016c\u0179\u017c";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}
