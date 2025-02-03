// Generated from src/main/java/dataweave/parser/DataWeave.g4 by ANTLR 4.13.2
package dataweave.parser;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class DataWeaveLexer extends Lexer {
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
            STAR = 30, DOUBLE_DOT = 31, AT = 32, QUESTION = 33, OPERATOR_MATH = 34, OPERATOR_COMPARISON = 35,
            OPERATOR_LOGICAL = 36, OPERATOR_BITWISE = 37, OPERATOR_CONDITIONAL = 38, OPERATOR_RANGE = 39,
            OPERATOR_CHAIN = 40, BUILTIN_FUNCTION = 41;
    public static String[] channelNames = {
            "DEFAULT_TOKEN_CHANNEL", "HIDDEN"
    };

    public static String[] modeNames = {
            "DEFAULT_MODE"
    };

    private static String[] makeRuleNames() {
        return new String[]{
                "T__0", "T__1", "VAR", "FUNCTION", "INPUT", "NAMESPACE", "OUTPUT", "DW",
                "ASSIGN", "ARROW", "BOOLEAN", "IDENTIFIER", "URL", "MEDIA_TYPE", "NUMBER",
                "STRING", "DATE", "REGEX", "DOT", "COLON", "COMMA", "LCURLY", "RCURLY",
                "LSQUARE", "RSQUARE", "SEPARATOR", "WS", "NEWLINE", "COMMENT", "STAR",
                "DOUBLE_DOT", "AT", "QUESTION", "OPERATOR_MATH", "OPERATOR_COMPARISON",
                "OPERATOR_LOGICAL", "OPERATOR_BITWISE", "OPERATOR_CONDITIONAL", "OPERATOR_RANGE",
                "OPERATOR_CHAIN", "BUILTIN_FUNCTION"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'('", "')'", "'%var'", "'%function'", "'%input'", "'%namespace'",
                "'%output'", "'%dw'", "'='", "'->'", null, null, null, null, null, null,
                null, null, "'.'", "':'", "','", "'{'", "'}'", "'['", "']'", "'---'",
                null, null, null, "'*'", "'..'", "'@'", "'?'", null, null, null, null,
                null, null, "'++'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, null, null, "VAR", "FUNCTION", "INPUT", "NAMESPACE", "OUTPUT",
                "DW", "ASSIGN", "ARROW", "BOOLEAN", "IDENTIFIER", "URL", "MEDIA_TYPE",
                "NUMBER", "STRING", "DATE", "REGEX", "DOT", "COLON", "COMMA", "LCURLY",
                "RCURLY", "LSQUARE", "RSQUARE", "SEPARATOR", "WS", "NEWLINE", "COMMENT",
                "STAR", "DOUBLE_DOT", "AT", "QUESTION", "OPERATOR_MATH", "OPERATOR_COMPARISON",
                "OPERATOR_LOGICAL", "OPERATOR_BITWISE", "OPERATOR_CONDITIONAL", "OPERATOR_RANGE",
                "OPERATOR_CHAIN", "BUILTIN_FUNCTION"
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


    public DataWeaveLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
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
    public String[] getChannelNames() {
        return channelNames;
    }

    @Override
    public String[] getModeNames() {
        return modeNames;
    }

    @Override
    public ATN getATN() {
        return _ATN;
    }

    public static final String _serializedATN =
            "\u0004\u0000)\u014d\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001" +
                    "\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004" +
                    "\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007" +
                    "\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b" +
                    "\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002" +
                    "\u000f\u0007\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002" +
                    "\u0012\u0007\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002" +
                    "\u0015\u0007\u0015\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002" +
                    "\u0018\u0007\u0018\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002" +
                    "\u001b\u0007\u001b\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002" +
                    "\u001e\u0007\u001e\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!\u0007" +
                    "!\u0002\"\u0007\"\u0002#\u0007#\u0002$\u0007$\u0002%\u0007%\u0002&\u0007" +
                    "&\u0002\'\u0007\'\u0002(\u0007(\u0001\u0000\u0001\u0000\u0001\u0001\u0001" +
                    "\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001" +
                    "\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001" +
                    "\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001" +
                    "\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001" +
                    "\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001" +
                    "\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001" +
                    "\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001" +
                    "\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\t\u0001" +
                    "\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001" +
                    "\n\u0001\n\u0003\n\u0093\b\n\u0001\u000b\u0001\u000b\u0005\u000b\u0097" +
                    "\b\u000b\n\u000b\f\u000b\u009a\t\u000b\u0001\f\u0004\f\u009d\b\f\u000b" +
                    "\f\f\f\u009e\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0004\f\u00a6\b\f" +
                    "\u000b\f\f\f\u00a7\u0001\r\u0004\r\u00ab\b\r\u000b\r\f\r\u00ac\u0001\r" +
                    "\u0001\r\u0004\r\u00b1\b\r\u000b\r\f\r\u00b2\u0001\u000e\u0004\u000e\u00b6" +
                    "\b\u000e\u000b\u000e\f\u000e\u00b7\u0001\u000e\u0001\u000e\u0004\u000e" +
                    "\u00bc\b\u000e\u000b\u000e\f\u000e\u00bd\u0003\u000e\u00c0\b\u000e\u0001" +
                    "\u000f\u0001\u000f\u0005\u000f\u00c4\b\u000f\n\u000f\f\u000f\u00c7\t\u000f" +
                    "\u0001\u000f\u0001\u000f\u0001\u000f\u0005\u000f\u00cc\b\u000f\n\u000f" +
                    "\f\u000f\u00cf\t\u000f\u0001\u000f\u0003\u000f\u00d2\b\u000f\u0001\u0010" +
                    "\u0001\u0010\u0005\u0010\u00d6\b\u0010\n\u0010\f\u0010\u00d9\t\u0010\u0001" +
                    "\u0010\u0001\u0010\u0001\u0011\u0001\u0011\u0005\u0011\u00df\b\u0011\n" +
                    "\u0011\f\u0011\u00e2\t\u0011\u0001\u0011\u0001\u0011\u0001\u0012\u0001" +
                    "\u0012\u0001\u0013\u0001\u0013\u0001\u0014\u0001\u0014\u0001\u0015\u0001" +
                    "\u0015\u0001\u0016\u0001\u0016\u0001\u0017\u0001\u0017\u0001\u0018\u0001" +
                    "\u0018\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u001a\u0004" +
                    "\u001a\u00f9\b\u001a\u000b\u001a\f\u001a\u00fa\u0001\u001a\u0001\u001a" +
                    "\u0001\u001b\u0004\u001b\u0100\b\u001b\u000b\u001b\f\u001b\u0101\u0001" +
                    "\u001b\u0001\u001b\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0005" +
                    "\u001c\u010a\b\u001c\n\u001c\f\u001c\u010d\t\u001c\u0001\u001c\u0001\u001c" +
                    "\u0001\u001d\u0001\u001d\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001f" +
                    "\u0001\u001f\u0001 \u0001 \u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0003" +
                    "!\u0120\b!\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001" +
                    "\"\u0001\"\u0003\"\u012b\b\"\u0001#\u0001#\u0001#\u0001#\u0001#\u0003" +
                    "#\u0132\b#\u0001$\u0001$\u0001%\u0001%\u0001&\u0001&\u0001\'\u0001\'\u0001" +
                    "\'\u0001(\u0001(\u0001(\u0001(\u0001(\u0001(\u0001(\u0001(\u0001(\u0001" +
                    "(\u0001(\u0001(\u0001(\u0001(\u0001(\u0003(\u014c\b(\u0004\u00c5\u00cd" +
                    "\u00d7\u00e0\u0000)\u0001\u0001\u0003\u0002\u0005\u0003\u0007\u0004\t" +
                    "\u0005\u000b\u0006\r\u0007\u000f\b\u0011\t\u0013\n\u0015\u000b\u0017\f" +
                    "\u0019\r\u001b\u000e\u001d\u000f\u001f\u0010!\u0011#\u0012%\u0013\'\u0014" +
                    ")\u0015+\u0016-\u0017/\u00181\u00193\u001a5\u001b7\u001c9\u001d;\u001e" +
                    "=\u001f? A!C\"E#G$I%K&M\'O(Q)\u0001\u0000\r\u0003\u0000AZ__az\u0004\u0000" +
                    "09AZ__az\u0002\u0000AZaz\u0004\u0000-9AZ__az\u0001\u0000az\u0004\u0000" +
                    "++-.09az\u0001\u000009\u0002\u0000\t\t  \u0002\u0000\n\n\r\r\u0002\u0000" +
                    "++--\u0002\u0000<<>>\u0003\u0000&&^^||\u0002\u0000::??\u0167\u0000\u0001" +
                    "\u0001\u0000\u0000\u0000\u0000\u0003\u0001\u0000\u0000\u0000\u0000\u0005" +
                    "\u0001\u0000\u0000\u0000\u0000\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001" +
                    "\u0000\u0000\u0000\u0000\u000b\u0001\u0000\u0000\u0000\u0000\r\u0001\u0000" +
                    "\u0000\u0000\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0011\u0001\u0000" +
                    "\u0000\u0000\u0000\u0013\u0001\u0000\u0000\u0000\u0000\u0015\u0001\u0000" +
                    "\u0000\u0000\u0000\u0017\u0001\u0000\u0000\u0000\u0000\u0019\u0001\u0000" +
                    "\u0000\u0000\u0000\u001b\u0001\u0000\u0000\u0000\u0000\u001d\u0001\u0000" +
                    "\u0000\u0000\u0000\u001f\u0001\u0000\u0000\u0000\u0000!\u0001\u0000\u0000" +
                    "\u0000\u0000#\u0001\u0000\u0000\u0000\u0000%\u0001\u0000\u0000\u0000\u0000" +
                    "\'\u0001\u0000\u0000\u0000\u0000)\u0001\u0000\u0000\u0000\u0000+\u0001" +
                    "\u0000\u0000\u0000\u0000-\u0001\u0000\u0000\u0000\u0000/\u0001\u0000\u0000" +
                    "\u0000\u00001\u0001\u0000\u0000\u0000\u00003\u0001\u0000\u0000\u0000\u0000" +
                    "5\u0001\u0000\u0000\u0000\u00007\u0001\u0000\u0000\u0000\u00009\u0001" +
                    "\u0000\u0000\u0000\u0000;\u0001\u0000\u0000\u0000\u0000=\u0001\u0000\u0000" +
                    "\u0000\u0000?\u0001\u0000\u0000\u0000\u0000A\u0001\u0000\u0000\u0000\u0000" +
                    "C\u0001\u0000\u0000\u0000\u0000E\u0001\u0000\u0000\u0000\u0000G\u0001" +
                    "\u0000\u0000\u0000\u0000I\u0001\u0000\u0000\u0000\u0000K\u0001\u0000\u0000" +
                    "\u0000\u0000M\u0001\u0000\u0000\u0000\u0000O\u0001\u0000\u0000\u0000\u0000" +
                    "Q\u0001\u0000\u0000\u0000\u0001S\u0001\u0000\u0000\u0000\u0003U\u0001" +
                    "\u0000\u0000\u0000\u0005W\u0001\u0000\u0000\u0000\u0007\\\u0001\u0000" +
                    "\u0000\u0000\tf\u0001\u0000\u0000\u0000\u000bm\u0001\u0000\u0000\u0000" +
                    "\rx\u0001\u0000\u0000\u0000\u000f\u0080\u0001\u0000\u0000\u0000\u0011" +
                    "\u0084\u0001\u0000\u0000\u0000\u0013\u0086\u0001\u0000\u0000\u0000\u0015" +
                    "\u0092\u0001\u0000\u0000\u0000\u0017\u0094\u0001\u0000\u0000\u0000\u0019" +
                    "\u009c\u0001\u0000\u0000\u0000\u001b\u00aa\u0001\u0000\u0000\u0000\u001d" +
                    "\u00b5\u0001\u0000\u0000\u0000\u001f\u00d1\u0001\u0000\u0000\u0000!\u00d3" +
                    "\u0001\u0000\u0000\u0000#\u00dc\u0001\u0000\u0000\u0000%\u00e5\u0001\u0000" +
                    "\u0000\u0000\'\u00e7\u0001\u0000\u0000\u0000)\u00e9\u0001\u0000\u0000" +
                    "\u0000+\u00eb\u0001\u0000\u0000\u0000-\u00ed\u0001\u0000\u0000\u0000/" +
                    "\u00ef\u0001\u0000\u0000\u00001\u00f1\u0001\u0000\u0000\u00003\u00f3\u0001" +
                    "\u0000\u0000\u00005\u00f8\u0001\u0000\u0000\u00007\u00ff\u0001\u0000\u0000" +
                    "\u00009\u0105\u0001\u0000\u0000\u0000;\u0110\u0001\u0000\u0000\u0000=" +
                    "\u0112\u0001\u0000\u0000\u0000?\u0115\u0001\u0000\u0000\u0000A\u0117\u0001" +
                    "\u0000\u0000\u0000C\u011f\u0001\u0000\u0000\u0000E\u012a\u0001\u0000\u0000" +
                    "\u0000G\u0131\u0001\u0000\u0000\u0000I\u0133\u0001\u0000\u0000\u0000K" +
                    "\u0135\u0001\u0000\u0000\u0000M\u0137\u0001\u0000\u0000\u0000O\u0139\u0001" +
                    "\u0000\u0000\u0000Q\u014b\u0001\u0000\u0000\u0000ST\u0005(\u0000\u0000" +
                    "T\u0002\u0001\u0000\u0000\u0000UV\u0005)\u0000\u0000V\u0004\u0001\u0000" +
                    "\u0000\u0000WX\u0005%\u0000\u0000XY\u0005v\u0000\u0000YZ\u0005a\u0000" +
                    "\u0000Z[\u0005r\u0000\u0000[\u0006\u0001\u0000\u0000\u0000\\]\u0005%\u0000" +
                    "\u0000]^\u0005f\u0000\u0000^_\u0005u\u0000\u0000_`\u0005n\u0000\u0000" +
                    "`a\u0005c\u0000\u0000ab\u0005t\u0000\u0000bc\u0005i\u0000\u0000cd\u0005" +
                    "o\u0000\u0000de\u0005n\u0000\u0000e\b\u0001\u0000\u0000\u0000fg\u0005" +
                    "%\u0000\u0000gh\u0005i\u0000\u0000hi\u0005n\u0000\u0000ij\u0005p\u0000" +
                    "\u0000jk\u0005u\u0000\u0000kl\u0005t\u0000\u0000l\n\u0001\u0000\u0000" +
                    "\u0000mn\u0005%\u0000\u0000no\u0005n\u0000\u0000op\u0005a\u0000\u0000" +
                    "pq\u0005m\u0000\u0000qr\u0005e\u0000\u0000rs\u0005s\u0000\u0000st\u0005" +
                    "p\u0000\u0000tu\u0005a\u0000\u0000uv\u0005c\u0000\u0000vw\u0005e\u0000" +
                    "\u0000w\f\u0001\u0000\u0000\u0000xy\u0005%\u0000\u0000yz\u0005o\u0000" +
                    "\u0000z{\u0005u\u0000\u0000{|\u0005t\u0000\u0000|}\u0005p\u0000\u0000" +
                    "}~\u0005u\u0000\u0000~\u007f\u0005t\u0000\u0000\u007f\u000e\u0001\u0000" +
                    "\u0000\u0000\u0080\u0081\u0005%\u0000\u0000\u0081\u0082\u0005d\u0000\u0000" +
                    "\u0082\u0083\u0005w\u0000\u0000\u0083\u0010\u0001\u0000\u0000\u0000\u0084" +
                    "\u0085\u0005=\u0000\u0000\u0085\u0012\u0001\u0000\u0000\u0000\u0086\u0087" +
                    "\u0005-\u0000\u0000\u0087\u0088\u0005>\u0000\u0000\u0088\u0014\u0001\u0000" +
                    "\u0000\u0000\u0089\u008a\u0005t\u0000\u0000\u008a\u008b\u0005r\u0000\u0000" +
                    "\u008b\u008c\u0005u\u0000\u0000\u008c\u0093\u0005e\u0000\u0000\u008d\u008e" +
                    "\u0005f\u0000\u0000\u008e\u008f\u0005a\u0000\u0000\u008f\u0090\u0005l" +
                    "\u0000\u0000\u0090\u0091\u0005s\u0000\u0000\u0091\u0093\u0005e\u0000\u0000" +
                    "\u0092\u0089\u0001\u0000\u0000\u0000\u0092\u008d\u0001\u0000\u0000\u0000" +
                    "\u0093\u0016\u0001\u0000\u0000\u0000\u0094\u0098\u0007\u0000\u0000\u0000" +
                    "\u0095\u0097\u0007\u0001\u0000\u0000\u0096\u0095\u0001\u0000\u0000\u0000" +
                    "\u0097\u009a\u0001\u0000\u0000\u0000\u0098\u0096\u0001\u0000\u0000\u0000" +
                    "\u0098\u0099\u0001\u0000\u0000\u0000\u0099\u0018\u0001\u0000\u0000\u0000" +
                    "\u009a\u0098\u0001\u0000\u0000\u0000\u009b\u009d\u0007\u0002\u0000\u0000" +
                    "\u009c\u009b\u0001\u0000\u0000\u0000\u009d\u009e\u0001\u0000\u0000\u0000" +
                    "\u009e\u009c\u0001\u0000\u0000\u0000\u009e\u009f\u0001\u0000\u0000\u0000" +
                    "\u009f\u00a0\u0001\u0000\u0000\u0000\u00a0\u00a1\u0005:\u0000\u0000\u00a1" +
                    "\u00a2\u0005/\u0000\u0000\u00a2\u00a3\u0005/\u0000\u0000\u00a3\u00a5\u0001" +
                    "\u0000\u0000\u0000\u00a4\u00a6\u0007\u0003\u0000\u0000\u00a5\u00a4\u0001" +
                    "\u0000\u0000\u0000\u00a6\u00a7\u0001\u0000\u0000\u0000\u00a7\u00a5\u0001" +
                    "\u0000\u0000\u0000\u00a7\u00a8\u0001\u0000\u0000\u0000\u00a8\u001a\u0001" +
                    "\u0000\u0000\u0000\u00a9\u00ab\u0007\u0004\u0000\u0000\u00aa\u00a9\u0001" +
                    "\u0000\u0000\u0000\u00ab\u00ac\u0001\u0000\u0000\u0000\u00ac\u00aa\u0001" +
                    "\u0000\u0000\u0000\u00ac\u00ad\u0001\u0000\u0000\u0000\u00ad\u00ae\u0001" +
                    "\u0000\u0000\u0000\u00ae\u00b0\u0005/\u0000\u0000\u00af\u00b1\u0007\u0005" +
                    "\u0000\u0000\u00b0\u00af\u0001\u0000\u0000\u0000\u00b1\u00b2\u0001\u0000" +
                    "\u0000\u0000\u00b2\u00b0\u0001\u0000\u0000\u0000\u00b2\u00b3\u0001\u0000" +
                    "\u0000\u0000\u00b3\u001c\u0001\u0000\u0000\u0000\u00b4\u00b6\u0007\u0006" +
                    "\u0000\u0000\u00b5\u00b4\u0001\u0000\u0000\u0000\u00b6\u00b7\u0001\u0000" +
                    "\u0000\u0000\u00b7\u00b5\u0001\u0000\u0000\u0000\u00b7\u00b8\u0001\u0000" +
                    "\u0000\u0000\u00b8\u00bf\u0001\u0000\u0000\u0000\u00b9\u00bb\u0005.\u0000" +
                    "\u0000\u00ba\u00bc\u0007\u0006\u0000\u0000\u00bb\u00ba\u0001\u0000\u0000" +
                    "\u0000\u00bc\u00bd\u0001\u0000\u0000\u0000\u00bd\u00bb\u0001\u0000\u0000" +
                    "\u0000\u00bd\u00be\u0001\u0000\u0000\u0000\u00be\u00c0\u0001\u0000\u0000" +
                    "\u0000\u00bf\u00b9\u0001\u0000\u0000\u0000\u00bf\u00c0\u0001\u0000\u0000" +
                    "\u0000\u00c0\u001e\u0001\u0000\u0000\u0000\u00c1\u00c5\u0005\"\u0000\u0000" +
                    "\u00c2\u00c4\t\u0000\u0000\u0000\u00c3\u00c2\u0001\u0000\u0000\u0000\u00c4" +
                    "\u00c7\u0001\u0000\u0000\u0000\u00c5\u00c6\u0001\u0000\u0000\u0000\u00c5" +
                    "\u00c3\u0001\u0000\u0000\u0000\u00c6\u00c8\u0001\u0000\u0000\u0000\u00c7" +
                    "\u00c5\u0001\u0000\u0000\u0000\u00c8\u00d2\u0005\"\u0000\u0000\u00c9\u00cd" +
                    "\u0005\'\u0000\u0000\u00ca\u00cc\t\u0000\u0000\u0000\u00cb\u00ca\u0001" +
                    "\u0000\u0000\u0000\u00cc\u00cf\u0001\u0000\u0000\u0000\u00cd\u00ce\u0001" +
                    "\u0000\u0000\u0000\u00cd\u00cb\u0001\u0000\u0000\u0000\u00ce\u00d0\u0001" +
                    "\u0000\u0000\u0000\u00cf\u00cd\u0001\u0000\u0000\u0000\u00d0\u00d2\u0005" +
                    "\'\u0000\u0000\u00d1\u00c1\u0001\u0000\u0000\u0000\u00d1\u00c9\u0001\u0000" +
                    "\u0000\u0000\u00d2 \u0001\u0000\u0000\u0000\u00d3\u00d7\u0005|\u0000\u0000" +
                    "\u00d4\u00d6\t\u0000\u0000\u0000\u00d5\u00d4\u0001\u0000\u0000\u0000\u00d6" +
                    "\u00d9\u0001\u0000\u0000\u0000\u00d7\u00d8\u0001\u0000\u0000\u0000\u00d7" +
                    "\u00d5\u0001\u0000\u0000\u0000\u00d8\u00da\u0001\u0000\u0000\u0000\u00d9" +
                    "\u00d7\u0001\u0000\u0000\u0000\u00da\u00db\u0005|\u0000\u0000\u00db\"" +
                    "\u0001\u0000\u0000\u0000\u00dc\u00e0\u0005/\u0000\u0000\u00dd\u00df\t" +
                    "\u0000\u0000\u0000\u00de\u00dd\u0001\u0000\u0000\u0000\u00df\u00e2\u0001" +
                    "\u0000\u0000\u0000\u00e0\u00e1\u0001\u0000\u0000\u0000\u00e0\u00de\u0001" +
                    "\u0000\u0000\u0000\u00e1\u00e3\u0001\u0000\u0000\u0000\u00e2\u00e0\u0001" +
                    "\u0000\u0000\u0000\u00e3\u00e4\u0005/\u0000\u0000\u00e4$\u0001\u0000\u0000" +
                    "\u0000\u00e5\u00e6\u0005.\u0000\u0000\u00e6&\u0001\u0000\u0000\u0000\u00e7" +
                    "\u00e8\u0005:\u0000\u0000\u00e8(\u0001\u0000\u0000\u0000\u00e9\u00ea\u0005" +
                    ",\u0000\u0000\u00ea*\u0001\u0000\u0000\u0000\u00eb\u00ec\u0005{\u0000" +
                    "\u0000\u00ec,\u0001\u0000\u0000\u0000\u00ed\u00ee\u0005}\u0000\u0000\u00ee" +
                    ".\u0001\u0000\u0000\u0000\u00ef\u00f0\u0005[\u0000\u0000\u00f00\u0001" +
                    "\u0000\u0000\u0000\u00f1\u00f2\u0005]\u0000\u0000\u00f22\u0001\u0000\u0000" +
                    "\u0000\u00f3\u00f4\u0005-\u0000\u0000\u00f4\u00f5\u0005-\u0000\u0000\u00f5" +
                    "\u00f6\u0005-\u0000\u0000\u00f64\u0001\u0000\u0000\u0000\u00f7\u00f9\u0007" +
                    "\u0007\u0000\u0000\u00f8\u00f7\u0001\u0000\u0000\u0000\u00f9\u00fa\u0001" +
                    "\u0000\u0000\u0000\u00fa\u00f8\u0001\u0000\u0000\u0000\u00fa\u00fb\u0001" +
                    "\u0000\u0000\u0000\u00fb\u00fc\u0001\u0000\u0000\u0000\u00fc\u00fd\u0006" +
                    "\u001a\u0000\u0000\u00fd6\u0001\u0000\u0000\u0000\u00fe\u0100\u0007\b" +
                    "\u0000\u0000\u00ff\u00fe\u0001\u0000\u0000\u0000\u0100\u0101\u0001\u0000" +
                    "\u0000\u0000\u0101\u00ff\u0001\u0000\u0000\u0000\u0101\u0102\u0001\u0000" +
                    "\u0000\u0000\u0102\u0103\u0001\u0000\u0000\u0000\u0103\u0104\u0006\u001b" +
                    "\u0000\u0000\u01048\u0001\u0000\u0000\u0000\u0105\u0106\u0005/\u0000\u0000" +
                    "\u0106\u0107\u0005/\u0000\u0000\u0107\u010b\u0001\u0000\u0000\u0000\u0108" +
                    "\u010a\b\b\u0000\u0000\u0109\u0108\u0001\u0000\u0000\u0000\u010a\u010d" +
                    "\u0001\u0000\u0000\u0000\u010b\u0109\u0001\u0000\u0000\u0000\u010b\u010c" +
                    "\u0001\u0000\u0000\u0000\u010c\u010e\u0001\u0000\u0000\u0000\u010d\u010b" +
                    "\u0001\u0000\u0000\u0000\u010e\u010f\u0006\u001c\u0000\u0000\u010f:\u0001" +
                    "\u0000\u0000\u0000\u0110\u0111\u0005*\u0000\u0000\u0111<\u0001\u0000\u0000" +
                    "\u0000\u0112\u0113\u0005.\u0000\u0000\u0113\u0114\u0005.\u0000\u0000\u0114" +
                    ">\u0001\u0000\u0000\u0000\u0115\u0116\u0005@\u0000\u0000\u0116@\u0001" +
                    "\u0000\u0000\u0000\u0117\u0118\u0005?\u0000\u0000\u0118B\u0001\u0000\u0000" +
                    "\u0000\u0119\u0120\u0007\t\u0000\u0000\u011a\u0120\u0003;\u001d\u0000" +
                    "\u011b\u0120\u0005/\u0000\u0000\u011c\u011d\u0005m\u0000\u0000\u011d\u011e" +
                    "\u0005o\u0000\u0000\u011e\u0120\u0005d\u0000\u0000\u011f\u0119\u0001\u0000" +
                    "\u0000\u0000\u011f\u011a\u0001\u0000\u0000\u0000\u011f\u011b\u0001\u0000" +
                    "\u0000\u0000\u011f\u011c\u0001\u0000\u0000\u0000\u0120D\u0001\u0000\u0000" +
                    "\u0000\u0121\u0122\u0005=\u0000\u0000\u0122\u012b\u0005=\u0000\u0000\u0123" +
                    "\u0124\u0005!\u0000\u0000\u0124\u012b\u0005=\u0000\u0000\u0125\u012b\u0007" +
                    "\n\u0000\u0000\u0126\u0127\u0005>\u0000\u0000\u0127\u012b\u0005=\u0000" +
                    "\u0000\u0128\u0129\u0005<\u0000\u0000\u0129\u012b\u0005=\u0000\u0000\u012a" +
                    "\u0121\u0001\u0000\u0000\u0000\u012a\u0123\u0001\u0000\u0000\u0000\u012a" +
                    "\u0125\u0001\u0000\u0000\u0000\u012a\u0126\u0001\u0000\u0000\u0000\u012a" +
                    "\u0128\u0001\u0000\u0000\u0000\u012bF\u0001\u0000\u0000\u0000\u012c\u012d" +
                    "\u0005a\u0000\u0000\u012d\u012e\u0005n\u0000\u0000\u012e\u0132\u0005d" +
                    "\u0000\u0000\u012f\u0130\u0005o\u0000\u0000\u0130\u0132\u0005r\u0000\u0000" +
                    "\u0131\u012c\u0001\u0000\u0000\u0000\u0131\u012f\u0001\u0000\u0000\u0000" +
                    "\u0132H\u0001\u0000\u0000\u0000\u0133\u0134\u0007\u000b\u0000\u0000\u0134" +
                    "J\u0001\u0000\u0000\u0000\u0135\u0136\u0007\f\u0000\u0000\u0136L\u0001" +
                    "\u0000\u0000\u0000\u0137\u0138\u0003=\u001e\u0000\u0138N\u0001\u0000\u0000" +
                    "\u0000\u0139\u013a\u0005+\u0000\u0000\u013a\u013b\u0005+\u0000\u0000\u013b" +
                    "P\u0001\u0000\u0000\u0000\u013c\u013d\u0005s\u0000\u0000\u013d\u013e\u0005" +
                    "i\u0000\u0000\u013e\u013f\u0005z\u0000\u0000\u013f\u0140\u0005e\u0000" +
                    "\u0000\u0140\u0141\u0005O\u0000\u0000\u0141\u014c\u0005f\u0000\u0000\u0142" +
                    "\u0143\u0005m\u0000\u0000\u0143\u0144\u0005a\u0000\u0000\u0144\u014c\u0005" +
                    "p\u0000\u0000\u0145\u0146\u0005f\u0000\u0000\u0146\u0147\u0005i\u0000" +
                    "\u0000\u0147\u0148\u0005l\u0000\u0000\u0148\u0149\u0005t\u0000\u0000\u0149" +
                    "\u014a\u0005e\u0000\u0000\u014a\u014c\u0005r\u0000\u0000\u014b\u013c\u0001" +
                    "\u0000\u0000\u0000\u014b\u0142\u0001\u0000\u0000\u0000\u014b\u0145\u0001" +
                    "\u0000\u0000\u0000\u014cR\u0001\u0000\u0000\u0000\u0016\u0000\u0092\u0098" +
                    "\u009e\u00a7\u00ac\u00b2\u00b7\u00bd\u00bf\u00c5\u00cd\u00d1\u00d7\u00e0" +
                    "\u00fa\u0101\u010b\u011f\u012a\u0131\u014b\u0001\u0006\u0000\u0000";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}