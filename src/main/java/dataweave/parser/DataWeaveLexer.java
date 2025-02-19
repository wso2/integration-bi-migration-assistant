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
            T__0 = 1, T__1 = 2, T__2 = 3, T__3 = 4, T__4 = 5, T__5 = 6, T__6 = 7, VAR = 8, FUNCTION = 9,
            INPUT = 10, NAMESPACE = 11, OUTPUT = 12, DW = 13, ASSIGN = 14, ARROW = 15, BOOLEAN = 16,
            OPERATOR_LOGICAL = 17, OPERATOR_COMPARISON = 18, OPERATOR_MATH = 19, OPERATOR_RANGE = 20,
            OPERATOR_CHAIN = 21, IDENTIFIER = 22, URL = 23, MEDIA_TYPE = 24, NUMBER = 25, STRING = 26,
            DATE = 27, REGEX = 28, DOT = 29, COLON = 30, COMMA = 31, LCURLY = 32, RCURLY = 33, LSQUARE = 34,
            RSQUARE = 35, SEPARATOR = 36, WS = 37, NEWLINE = 38, COMMENT = 39, STAR = 40, AT = 41,
            QUESTION = 42, BUILTIN_FUNCTION = 43, INDEX_IDENTIFIER = 44, VALUE_IDENTIFIER = 45;
    public static String[] channelNames = {
            "DEFAULT_TOKEN_CHANNEL", "HIDDEN"
    };

    public static String[] modeNames = {
            "DEFAULT_MODE"
    };

    private static String[] makeRuleNames() {
        return new String[]{
                "T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "VAR", "FUNCTION",
                "INPUT", "NAMESPACE", "OUTPUT", "DW", "ASSIGN", "ARROW", "BOOLEAN", "OPERATOR_LOGICAL",
                "OPERATOR_COMPARISON", "OPERATOR_MATH", "OPERATOR_RANGE", "OPERATOR_CHAIN",
                "IDENTIFIER", "URL", "MEDIA_TYPE", "NUMBER", "STRING", "DATE", "REGEX",
                "DOT", "COLON", "COMMA", "LCURLY", "RCURLY", "LSQUARE", "RSQUARE", "SEPARATOR",
                "WS", "NEWLINE", "COMMENT", "STAR", "AT", "QUESTION", "BUILTIN_FUNCTION",
                "INDEX_IDENTIFIER", "VALUE_IDENTIFIER"
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
            "\u0004\u0000-\u0176\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001" +
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
                    "&\u0002\'\u0007\'\u0002(\u0007(\u0002)\u0007)\u0002*\u0007*\u0002+\u0007" +
                    "+\u0002,\u0007,\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001" +
                    "\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001" +
                    "\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001" +
                    "\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001" +
                    "\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001" +
                    "\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001" +
                    "\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b" +
                    "\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001" +
                    "\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001" +
                    "\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001" +
                    "\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001" +
                    "\u000b\u0001\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\r\u0001\r\u0001" +
                    "\u000e\u0001\u000e\u0001\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0001" +
                    "\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0003" +
                    "\u000f\u00b9\b\u000f\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001" +
                    "\u0010\u0003\u0010\u00c0\b\u0010\u0001\u0011\u0001\u0011\u0001\u0011\u0001" +
                    "\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0003" +
                    "\u0011\u00cb\b\u0011\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001" +
                    "\u0012\u0001\u0012\u0003\u0012\u00d3\b\u0012\u0001\u0013\u0001\u0013\u0001" +
                    "\u0013\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0015\u0001\u0015\u0001" +
                    "\u0015\u0001\u0015\u0005\u0015\u00df\b\u0015\n\u0015\f\u0015\u00e2\t\u0015" +
                    "\u0003\u0015\u00e4\b\u0015\u0001\u0016\u0004\u0016\u00e7\b\u0016\u000b" +
                    "\u0016\f\u0016\u00e8\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001" +
                    "\u0016\u0004\u0016\u00f0\b\u0016\u000b\u0016\f\u0016\u00f1\u0001\u0017" +
                    "\u0004\u0017\u00f5\b\u0017\u000b\u0017\f\u0017\u00f6\u0001\u0017\u0001" +
                    "\u0017\u0004\u0017\u00fb\b\u0017\u000b\u0017\f\u0017\u00fc\u0001\u0018" +
                    "\u0004\u0018\u0100\b\u0018\u000b\u0018\f\u0018\u0101\u0001\u0018\u0001" +
                    "\u0018\u0004\u0018\u0106\b\u0018\u000b\u0018\f\u0018\u0107\u0003\u0018" +
                    "\u010a\b\u0018\u0001\u0019\u0001\u0019\u0005\u0019\u010e\b\u0019\n\u0019" +
                    "\f\u0019\u0111\t\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0005\u0019" +
                    "\u0116\b\u0019\n\u0019\f\u0019\u0119\t\u0019\u0001\u0019\u0003\u0019\u011c" +
                    "\b\u0019\u0001\u001a\u0001\u001a\u0005\u001a\u0120\b\u001a\n\u001a\f\u001a" +
                    "\u0123\t\u001a\u0001\u001a\u0001\u001a\u0001\u001b\u0001\u001b\u0005\u001b" +
                    "\u0129\b\u001b\n\u001b\f\u001b\u012c\t\u001b\u0001\u001b\u0001\u001b\u0001" +
                    "\u001c\u0001\u001c\u0001\u001d\u0001\u001d\u0001\u001e\u0001\u001e\u0001" +
                    "\u001f\u0001\u001f\u0001 \u0001 \u0001!\u0001!\u0001\"\u0001\"\u0001#" +
                    "\u0001#\u0001#\u0001#\u0001$\u0004$\u0143\b$\u000b$\f$\u0144\u0001$\u0001" +
                    "$\u0001%\u0004%\u014a\b%\u000b%\f%\u014b\u0001%\u0001%\u0001&\u0001&\u0001" +
                    "&\u0001&\u0005&\u0154\b&\n&\f&\u0157\t&\u0001&\u0001&\u0001\'\u0001\'" +
                    "\u0001(\u0001(\u0001)\u0001)\u0001*\u0001*\u0001*\u0001*\u0001*\u0001" +
                    "*\u0001*\u0001*\u0001*\u0001*\u0001*\u0001*\u0001*\u0001*\u0001*\u0003" +
                    "*\u0170\b*\u0001+\u0001+\u0001+\u0001,\u0001,\u0004\u010f\u0117\u0121" +
                    "\u012a\u0000-\u0001\u0001\u0003\u0002\u0005\u0003\u0007\u0004\t\u0005" +
                    "\u000b\u0006\r\u0007\u000f\b\u0011\t\u0013\n\u0015\u000b\u0017\f\u0019" +
                    "\r\u001b\u000e\u001d\u000f\u001f\u0010!\u0011#\u0012%\u0013\'\u0014)\u0015" +
                    "+\u0016-\u0017/\u00181\u00193\u001a5\u001b7\u001c9\u001d;\u001e=\u001f" +
                    "? A!C\"E#G$I%K&M\'O(Q)S*U+W,Y-\u0001\u0000\u000b\u0002\u0000<<>>\u0002" +
                    "\u0000++--\u0003\u0000AZ__az\u0004\u000009AZ__az\u0002\u0000AZaz\u0004" +
                    "\u0000-9AZ__az\u0001\u0000az\u0004\u0000++-.09az\u0001\u000009\u0002\u0000" +
                    "\t\t  \u0002\u0000\n\n\r\r\u0192\u0000\u0001\u0001\u0000\u0000\u0000\u0000" +
                    "\u0003\u0001\u0000\u0000\u0000\u0000\u0005\u0001\u0000\u0000\u0000\u0000" +
                    "\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b" +
                    "\u0001\u0000\u0000\u0000\u0000\r\u0001\u0000\u0000\u0000\u0000\u000f\u0001" +
                    "\u0000\u0000\u0000\u0000\u0011\u0001\u0000\u0000\u0000\u0000\u0013\u0001" +
                    "\u0000\u0000\u0000\u0000\u0015\u0001\u0000\u0000\u0000\u0000\u0017\u0001" +
                    "\u0000\u0000\u0000\u0000\u0019\u0001\u0000\u0000\u0000\u0000\u001b\u0001" +
                    "\u0000\u0000\u0000\u0000\u001d\u0001\u0000\u0000\u0000\u0000\u001f\u0001" +
                    "\u0000\u0000\u0000\u0000!\u0001\u0000\u0000\u0000\u0000#\u0001\u0000\u0000" +
                    "\u0000\u0000%\u0001\u0000\u0000\u0000\u0000\'\u0001\u0000\u0000\u0000" +
                    "\u0000)\u0001\u0000\u0000\u0000\u0000+\u0001\u0000\u0000\u0000\u0000-" +
                    "\u0001\u0000\u0000\u0000\u0000/\u0001\u0000\u0000\u0000\u00001\u0001\u0000" +
                    "\u0000\u0000\u00003\u0001\u0000\u0000\u0000\u00005\u0001\u0000\u0000\u0000" +
                    "\u00007\u0001\u0000\u0000\u0000\u00009\u0001\u0000\u0000\u0000\u0000;" +
                    "\u0001\u0000\u0000\u0000\u0000=\u0001\u0000\u0000\u0000\u0000?\u0001\u0000" +
                    "\u0000\u0000\u0000A\u0001\u0000\u0000\u0000\u0000C\u0001\u0000\u0000\u0000" +
                    "\u0000E\u0001\u0000\u0000\u0000\u0000G\u0001\u0000\u0000\u0000\u0000I" +
                    "\u0001\u0000\u0000\u0000\u0000K\u0001\u0000\u0000\u0000\u0000M\u0001\u0000" +
                    "\u0000\u0000\u0000O\u0001\u0000\u0000\u0000\u0000Q\u0001\u0000\u0000\u0000" +
                    "\u0000S\u0001\u0000\u0000\u0000\u0000U\u0001\u0000\u0000\u0000\u0000W" +
                    "\u0001\u0000\u0000\u0000\u0000Y\u0001\u0000\u0000\u0000\u0001[\u0001\u0000" +
                    "\u0000\u0000\u0003]\u0001\u0000\u0000\u0000\u0005_\u0001\u0000\u0000\u0000" +
                    "\u0007f\u0001\u0000\u0000\u0000\tj\u0001\u0000\u0000\u0000\u000bq\u0001" +
                    "\u0000\u0000\u0000\rw\u0001\u0000\u0000\u0000\u000f}\u0001\u0000\u0000" +
                    "\u0000\u0011\u0082\u0001\u0000\u0000\u0000\u0013\u008c\u0001\u0000\u0000" +
                    "\u0000\u0015\u0093\u0001\u0000\u0000\u0000\u0017\u009e\u0001\u0000\u0000" +
                    "\u0000\u0019\u00a6\u0001\u0000\u0000\u0000\u001b\u00aa\u0001\u0000\u0000" +
                    "\u0000\u001d\u00ac\u0001\u0000\u0000\u0000\u001f\u00b8\u0001\u0000\u0000" +
                    "\u0000!\u00bf\u0001\u0000\u0000\u0000#\u00ca\u0001\u0000\u0000\u0000%" +
                    "\u00d2\u0001\u0000\u0000\u0000\'\u00d4\u0001\u0000\u0000\u0000)\u00d7" +
                    "\u0001\u0000\u0000\u0000+\u00e3\u0001\u0000\u0000\u0000-\u00e6\u0001\u0000" +
                    "\u0000\u0000/\u00f4\u0001\u0000\u0000\u00001\u00ff\u0001\u0000\u0000\u0000" +
                    "3\u011b\u0001\u0000\u0000\u00005\u011d\u0001\u0000\u0000\u00007\u0126" +
                    "\u0001\u0000\u0000\u00009\u012f\u0001\u0000\u0000\u0000;\u0131\u0001\u0000" +
                    "\u0000\u0000=\u0133\u0001\u0000\u0000\u0000?\u0135\u0001\u0000\u0000\u0000" +
                    "A\u0137\u0001\u0000\u0000\u0000C\u0139\u0001\u0000\u0000\u0000E\u013b" +
                    "\u0001\u0000\u0000\u0000G\u013d\u0001\u0000\u0000\u0000I\u0142\u0001\u0000" +
                    "\u0000\u0000K\u0149\u0001\u0000\u0000\u0000M\u014f\u0001\u0000\u0000\u0000" +
                    "O\u015a\u0001\u0000\u0000\u0000Q\u015c\u0001\u0000\u0000\u0000S\u015e" +
                    "\u0001\u0000\u0000\u0000U\u016f\u0001\u0000\u0000\u0000W\u0171\u0001\u0000" +
                    "\u0000\u0000Y\u0174\u0001\u0000\u0000\u0000[\\\u0005(\u0000\u0000\\\u0002" +
                    "\u0001\u0000\u0000\u0000]^\u0005)\u0000\u0000^\u0004\u0001\u0000\u0000" +
                    "\u0000_`\u0005f\u0000\u0000`a\u0005i\u0000\u0000ab\u0005l\u0000\u0000" +
                    "bc\u0005t\u0000\u0000cd\u0005e\u0000\u0000de\u0005r\u0000\u0000e\u0006" +
                    "\u0001\u0000\u0000\u0000fg\u0005m\u0000\u0000gh\u0005a\u0000\u0000hi\u0005" +
                    "p\u0000\u0000i\b\u0001\u0000\u0000\u0000jk\u0005s\u0000\u0000kl\u0005" +
                    "i\u0000\u0000lm\u0005z\u0000\u0000mn\u0005e\u0000\u0000no\u0005O\u0000" +
                    "\u0000op\u0005f\u0000\u0000p\n\u0001\u0000\u0000\u0000qr\u0005u\u0000" +
                    "\u0000rs\u0005p\u0000\u0000st\u0005p\u0000\u0000tu\u0005e\u0000\u0000" +
                    "uv\u0005r\u0000\u0000v\f\u0001\u0000\u0000\u0000wx\u0005l\u0000\u0000" +
                    "xy\u0005o\u0000\u0000yz\u0005w\u0000\u0000z{\u0005e\u0000\u0000{|\u0005" +
                    "r\u0000\u0000|\u000e\u0001\u0000\u0000\u0000}~\u0005%\u0000\u0000~\u007f" +
                    "\u0005v\u0000\u0000\u007f\u0080\u0005a\u0000\u0000\u0080\u0081\u0005r" +
                    "\u0000\u0000\u0081\u0010\u0001\u0000\u0000\u0000\u0082\u0083\u0005%\u0000" +
                    "\u0000\u0083\u0084\u0005f\u0000\u0000\u0084\u0085\u0005u\u0000\u0000\u0085" +
                    "\u0086\u0005n\u0000\u0000\u0086\u0087\u0005c\u0000\u0000\u0087\u0088\u0005" +
                    "t\u0000\u0000\u0088\u0089\u0005i\u0000\u0000\u0089\u008a\u0005o\u0000" +
                    "\u0000\u008a\u008b\u0005n\u0000\u0000\u008b\u0012\u0001\u0000\u0000\u0000" +
                    "\u008c\u008d\u0005%\u0000\u0000\u008d\u008e\u0005i\u0000\u0000\u008e\u008f" +
                    "\u0005n\u0000\u0000\u008f\u0090\u0005p\u0000\u0000\u0090\u0091\u0005u" +
                    "\u0000\u0000\u0091\u0092\u0005t\u0000\u0000\u0092\u0014\u0001\u0000\u0000" +
                    "\u0000\u0093\u0094\u0005%\u0000\u0000\u0094\u0095\u0005n\u0000\u0000\u0095" +
                    "\u0096\u0005a\u0000\u0000\u0096\u0097\u0005m\u0000\u0000\u0097\u0098\u0005" +
                    "e\u0000\u0000\u0098\u0099\u0005s\u0000\u0000\u0099\u009a\u0005p\u0000" +
                    "\u0000\u009a\u009b\u0005a\u0000\u0000\u009b\u009c\u0005c\u0000\u0000\u009c" +
                    "\u009d\u0005e\u0000\u0000\u009d\u0016\u0001\u0000\u0000\u0000\u009e\u009f" +
                    "\u0005%\u0000\u0000\u009f\u00a0\u0005o\u0000\u0000\u00a0\u00a1\u0005u" +
                    "\u0000\u0000\u00a1\u00a2\u0005t\u0000\u0000\u00a2\u00a3\u0005p\u0000\u0000" +
                    "\u00a3\u00a4\u0005u\u0000\u0000\u00a4\u00a5\u0005t\u0000\u0000\u00a5\u0018" +
                    "\u0001\u0000\u0000\u0000\u00a6\u00a7\u0005%\u0000\u0000\u00a7\u00a8\u0005" +
                    "d\u0000\u0000\u00a8\u00a9\u0005w\u0000\u0000\u00a9\u001a\u0001\u0000\u0000" +
                    "\u0000\u00aa\u00ab\u0005=\u0000\u0000\u00ab\u001c\u0001\u0000\u0000\u0000" +
                    "\u00ac\u00ad\u0005-\u0000\u0000\u00ad\u00ae\u0005>\u0000\u0000\u00ae\u001e" +
                    "\u0001\u0000\u0000\u0000\u00af\u00b0\u0005t\u0000\u0000\u00b0\u00b1\u0005" +
                    "r\u0000\u0000\u00b1\u00b2\u0005u\u0000\u0000\u00b2\u00b9\u0005e\u0000" +
                    "\u0000\u00b3\u00b4\u0005f\u0000\u0000\u00b4\u00b5\u0005a\u0000\u0000\u00b5" +
                    "\u00b6\u0005l\u0000\u0000\u00b6\u00b7\u0005s\u0000\u0000\u00b7\u00b9\u0005" +
                    "e\u0000\u0000\u00b8\u00af\u0001\u0000\u0000\u0000\u00b8\u00b3\u0001\u0000" +
                    "\u0000\u0000\u00b9 \u0001\u0000\u0000\u0000\u00ba\u00bb\u0005a\u0000\u0000" +
                    "\u00bb\u00bc\u0005n\u0000\u0000\u00bc\u00c0\u0005d\u0000\u0000\u00bd\u00be" +
                    "\u0005o\u0000\u0000\u00be\u00c0\u0005r\u0000\u0000\u00bf\u00ba\u0001\u0000" +
                    "\u0000\u0000\u00bf\u00bd\u0001\u0000\u0000\u0000\u00c0\"\u0001\u0000\u0000" +
                    "\u0000\u00c1\u00c2\u0005=\u0000\u0000\u00c2\u00cb\u0005=\u0000\u0000\u00c3" +
                    "\u00c4\u0005!\u0000\u0000\u00c4\u00cb\u0005=\u0000\u0000\u00c5\u00cb\u0007" +
                    "\u0000\u0000\u0000\u00c6\u00c7\u0005>\u0000\u0000\u00c7\u00cb\u0005=\u0000" +
                    "\u0000\u00c8\u00c9\u0005<\u0000\u0000\u00c9\u00cb\u0005=\u0000\u0000\u00ca" +
                    "\u00c1\u0001\u0000\u0000\u0000\u00ca\u00c3\u0001\u0000\u0000\u0000\u00ca" +
                    "\u00c5\u0001\u0000\u0000\u0000\u00ca\u00c6\u0001\u0000\u0000\u0000\u00ca" +
                    "\u00c8\u0001\u0000\u0000\u0000\u00cb$\u0001\u0000\u0000\u0000\u00cc\u00d3" +
                    "\u0007\u0001\u0000\u0000\u00cd\u00d3\u0003O\'\u0000\u00ce\u00d3\u0005" +
                    "/\u0000\u0000\u00cf\u00d0\u0005m\u0000\u0000\u00d0\u00d1\u0005o\u0000" +
                    "\u0000\u00d1\u00d3\u0005d\u0000\u0000\u00d2\u00cc\u0001\u0000\u0000\u0000" +
                    "\u00d2\u00cd\u0001\u0000\u0000\u0000\u00d2\u00ce\u0001\u0000\u0000\u0000" +
                    "\u00d2\u00cf\u0001\u0000\u0000\u0000\u00d3&\u0001\u0000\u0000\u0000\u00d4" +
                    "\u00d5\u0005.\u0000\u0000\u00d5\u00d6\u0005.\u0000\u0000\u00d6(\u0001" +
                    "\u0000\u0000\u0000\u00d7\u00d8\u0005+\u0000\u0000\u00d8\u00d9\u0005+\u0000" +
                    "\u0000\u00d9*\u0001\u0000\u0000\u0000\u00da\u00e4\u0003W+\u0000\u00db" +
                    "\u00e4\u0003Y,\u0000\u00dc\u00e0\u0007\u0002\u0000\u0000\u00dd\u00df\u0007" +
                    "\u0003\u0000\u0000\u00de\u00dd\u0001\u0000\u0000\u0000\u00df\u00e2\u0001" +
                    "\u0000\u0000\u0000\u00e0\u00de\u0001\u0000\u0000\u0000\u00e0\u00e1\u0001" +
                    "\u0000\u0000\u0000\u00e1\u00e4\u0001\u0000\u0000\u0000\u00e2\u00e0\u0001" +
                    "\u0000\u0000\u0000\u00e3\u00da\u0001\u0000\u0000\u0000\u00e3\u00db\u0001" +
                    "\u0000\u0000\u0000\u00e3\u00dc\u0001\u0000\u0000\u0000\u00e4,\u0001\u0000" +
                    "\u0000\u0000\u00e5\u00e7\u0007\u0004\u0000\u0000\u00e6\u00e5\u0001\u0000" +
                    "\u0000\u0000\u00e7\u00e8\u0001\u0000\u0000\u0000\u00e8\u00e6\u0001\u0000" +
                    "\u0000\u0000\u00e8\u00e9\u0001\u0000\u0000\u0000\u00e9\u00ea\u0001\u0000" +
                    "\u0000\u0000\u00ea\u00eb\u0005:\u0000\u0000\u00eb\u00ec\u0005/\u0000\u0000" +
                    "\u00ec\u00ed\u0005/\u0000\u0000\u00ed\u00ef\u0001\u0000\u0000\u0000\u00ee" +
                    "\u00f0\u0007\u0005\u0000\u0000\u00ef\u00ee\u0001\u0000\u0000\u0000\u00f0" +
                    "\u00f1\u0001\u0000\u0000\u0000\u00f1\u00ef\u0001\u0000\u0000\u0000\u00f1" +
                    "\u00f2\u0001\u0000\u0000\u0000\u00f2.\u0001\u0000\u0000\u0000\u00f3\u00f5" +
                    "\u0007\u0006\u0000\u0000\u00f4\u00f3\u0001\u0000\u0000\u0000\u00f5\u00f6" +
                    "\u0001\u0000\u0000\u0000\u00f6\u00f4\u0001\u0000\u0000\u0000\u00f6\u00f7" +
                    "\u0001\u0000\u0000\u0000\u00f7\u00f8\u0001\u0000\u0000\u0000\u00f8\u00fa" +
                    "\u0005/\u0000\u0000\u00f9\u00fb\u0007\u0007\u0000\u0000\u00fa\u00f9\u0001" +
                    "\u0000\u0000\u0000\u00fb\u00fc\u0001\u0000\u0000\u0000\u00fc\u00fa\u0001" +
                    "\u0000\u0000\u0000\u00fc\u00fd\u0001\u0000\u0000\u0000\u00fd0\u0001\u0000" +
                    "\u0000\u0000\u00fe\u0100\u0007\b\u0000\u0000\u00ff\u00fe\u0001\u0000\u0000" +
                    "\u0000\u0100\u0101\u0001\u0000\u0000\u0000\u0101\u00ff\u0001\u0000\u0000" +
                    "\u0000\u0101\u0102\u0001\u0000\u0000\u0000\u0102\u0109\u0001\u0000\u0000" +
                    "\u0000\u0103\u0105\u0005.\u0000\u0000\u0104\u0106\u0007\b\u0000\u0000" +
                    "\u0105\u0104\u0001\u0000\u0000\u0000\u0106\u0107\u0001\u0000\u0000\u0000" +
                    "\u0107\u0105\u0001\u0000\u0000\u0000\u0107\u0108\u0001\u0000\u0000\u0000" +
                    "\u0108\u010a\u0001\u0000\u0000\u0000\u0109\u0103\u0001\u0000\u0000\u0000" +
                    "\u0109\u010a\u0001\u0000\u0000\u0000\u010a2\u0001\u0000\u0000\u0000\u010b" +
                    "\u010f\u0005\"\u0000\u0000\u010c\u010e\t\u0000\u0000\u0000\u010d\u010c" +
                    "\u0001\u0000\u0000\u0000\u010e\u0111\u0001\u0000\u0000\u0000\u010f\u0110" +
                    "\u0001\u0000\u0000\u0000\u010f\u010d\u0001\u0000\u0000\u0000\u0110\u0112" +
                    "\u0001\u0000\u0000\u0000\u0111\u010f\u0001\u0000\u0000\u0000\u0112\u011c" +
                    "\u0005\"\u0000\u0000\u0113\u0117\u0005\'\u0000\u0000\u0114\u0116\t\u0000" +
                    "\u0000\u0000\u0115\u0114\u0001\u0000\u0000\u0000\u0116\u0119\u0001\u0000" +
                    "\u0000\u0000\u0117\u0118\u0001\u0000\u0000\u0000\u0117\u0115\u0001\u0000" +
                    "\u0000\u0000\u0118\u011a\u0001\u0000\u0000\u0000\u0119\u0117\u0001\u0000" +
                    "\u0000\u0000\u011a\u011c\u0005\'\u0000\u0000\u011b\u010b\u0001\u0000\u0000" +
                    "\u0000\u011b\u0113\u0001\u0000\u0000\u0000\u011c4\u0001\u0000\u0000\u0000" +
                    "\u011d\u0121\u0005|\u0000\u0000\u011e\u0120\t\u0000\u0000\u0000\u011f" +
                    "\u011e\u0001\u0000\u0000\u0000\u0120\u0123\u0001\u0000\u0000\u0000\u0121" +
                    "\u0122\u0001\u0000\u0000\u0000\u0121\u011f\u0001\u0000\u0000\u0000\u0122" +
                    "\u0124\u0001\u0000\u0000\u0000\u0123\u0121\u0001\u0000\u0000\u0000\u0124" +
                    "\u0125\u0005|\u0000\u0000\u01256\u0001\u0000\u0000\u0000\u0126\u012a\u0005" +
                    "/\u0000\u0000\u0127\u0129\t\u0000\u0000\u0000\u0128\u0127\u0001\u0000" +
                    "\u0000\u0000\u0129\u012c\u0001\u0000\u0000\u0000\u012a\u012b\u0001\u0000" +
                    "\u0000\u0000\u012a\u0128\u0001\u0000\u0000\u0000\u012b\u012d\u0001\u0000" +
                    "\u0000\u0000\u012c\u012a\u0001\u0000\u0000\u0000\u012d\u012e\u0005/\u0000" +
                    "\u0000\u012e8\u0001\u0000\u0000\u0000\u012f\u0130\u0005.\u0000\u0000\u0130" +
                    ":\u0001\u0000\u0000\u0000\u0131\u0132\u0005:\u0000\u0000\u0132<\u0001" +
                    "\u0000\u0000\u0000\u0133\u0134\u0005,\u0000\u0000\u0134>\u0001\u0000\u0000" +
                    "\u0000\u0135\u0136\u0005{\u0000\u0000\u0136@\u0001\u0000\u0000\u0000\u0137" +
                    "\u0138\u0005}\u0000\u0000\u0138B\u0001\u0000\u0000\u0000\u0139\u013a\u0005" +
                    "[\u0000\u0000\u013aD\u0001\u0000\u0000\u0000\u013b\u013c\u0005]\u0000" +
                    "\u0000\u013cF\u0001\u0000\u0000\u0000\u013d\u013e\u0005-\u0000\u0000\u013e" +
                    "\u013f\u0005-\u0000\u0000\u013f\u0140\u0005-\u0000\u0000\u0140H\u0001" +
                    "\u0000\u0000\u0000\u0141\u0143\u0007\t\u0000\u0000\u0142\u0141\u0001\u0000" +
                    "\u0000\u0000\u0143\u0144\u0001\u0000\u0000\u0000\u0144\u0142\u0001\u0000" +
                    "\u0000\u0000\u0144\u0145\u0001\u0000\u0000\u0000\u0145\u0146\u0001\u0000" +
                    "\u0000\u0000\u0146\u0147\u0006$\u0000\u0000\u0147J\u0001\u0000\u0000\u0000" +
                    "\u0148\u014a\u0007\n\u0000\u0000\u0149\u0148\u0001\u0000\u0000\u0000\u014a" +
                    "\u014b\u0001\u0000\u0000\u0000\u014b\u0149\u0001\u0000\u0000\u0000\u014b" +
                    "\u014c\u0001\u0000\u0000\u0000\u014c\u014d\u0001\u0000\u0000\u0000\u014d" +
                    "\u014e\u0006%\u0000\u0000\u014eL\u0001\u0000\u0000\u0000\u014f\u0150\u0005" +
                    "/\u0000\u0000\u0150\u0151\u0005/\u0000\u0000\u0151\u0155\u0001\u0000\u0000" +
                    "\u0000\u0152\u0154\b\n\u0000\u0000\u0153\u0152\u0001\u0000\u0000\u0000" +
                    "\u0154\u0157\u0001\u0000\u0000\u0000\u0155\u0153\u0001\u0000\u0000\u0000" +
                    "\u0155\u0156\u0001\u0000\u0000\u0000\u0156\u0158\u0001\u0000\u0000\u0000" +
                    "\u0157\u0155\u0001\u0000\u0000\u0000\u0158\u0159\u0006&\u0000\u0000\u0159" +
                    "N\u0001\u0000\u0000\u0000\u015a\u015b\u0005*\u0000\u0000\u015bP\u0001" +
                    "\u0000\u0000\u0000\u015c\u015d\u0005@\u0000\u0000\u015dR\u0001\u0000\u0000" +
                    "\u0000\u015e\u015f\u0005?\u0000\u0000\u015fT\u0001\u0000\u0000\u0000\u0160" +
                    "\u0161\u0005s\u0000\u0000\u0161\u0162\u0005i\u0000\u0000\u0162\u0163\u0005" +
                    "z\u0000\u0000\u0163\u0164\u0005e\u0000\u0000\u0164\u0165\u0005O\u0000" +
                    "\u0000\u0165\u0170\u0005f\u0000\u0000\u0166\u0167\u0005m\u0000\u0000\u0167" +
                    "\u0168\u0005a\u0000\u0000\u0168\u0170\u0005p\u0000\u0000\u0169\u016a\u0005" +
                    "f\u0000\u0000\u016a\u016b\u0005i\u0000\u0000\u016b\u016c\u0005l\u0000" +
                    "\u0000\u016c\u016d\u0005t\u0000\u0000\u016d\u016e\u0005e\u0000\u0000\u016e" +
                    "\u0170\u0005r\u0000\u0000\u016f\u0160\u0001\u0000\u0000\u0000\u016f\u0166" +
                    "\u0001\u0000\u0000\u0000\u016f\u0169\u0001\u0000\u0000\u0000\u0170V\u0001" +
                    "\u0000\u0000\u0000\u0171\u0172\u0005$\u0000\u0000\u0172\u0173\u0005$\u0000" +
                    "\u0000\u0173X\u0001\u0000\u0000\u0000\u0174\u0175\u0005$\u0000\u0000\u0175" +
                    "Z\u0001\u0000\u0000\u0000\u0017\u0000\u00b8\u00bf\u00ca\u00d2\u00e0\u00e3" +
                    "\u00e8\u00f1\u00f6\u00fc\u0101\u0107\u0109\u010f\u0117\u011b\u0121\u012a" +
                    "\u0144\u014b\u0155\u016f\u0001\u0006\u0000\u0000";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}