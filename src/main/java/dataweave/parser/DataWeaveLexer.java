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
            T__0 = 1, T__1 = 2, T__2 = 3, T__3 = 4, T__4 = 5, T__5 = 6, VAR = 7, FUNCTION = 8, INPUT = 9,
            NAMESPACE = 10, OUTPUT = 11, DW = 12, ASSIGN = 13, ARROW = 14, BOOLEAN = 15, IDENTIFIER = 16,
            URL = 17, MEDIA_TYPE = 18, NUMBER = 19, STRING = 20, DATE = 21, REGEX = 22, DOT = 23,
            COLON = 24, COMMA = 25, LCURLY = 26, RCURLY = 27, LSQUARE = 28, RSQUARE = 29, SEPARATOR = 30,
            WS = 31, NEWLINE = 32, COMMENT = 33, STAR = 34, DOUBLE_DOT = 35, AT = 36, QUESTION = 37,
            OPERATOR_MATH = 38, OPERATOR_COMPARISON = 39, OPERATOR_LOGICAL = 40, OPERATOR_BITWISE = 41,
            OPERATOR_CONDITIONAL = 42, OPERATOR_RANGE = 43, OPERATOR_CHAIN = 44, BUILTIN_FUNCTION = 45,
            INDEX_IDENTIFIER = 46, VALUE_IDENTIFIER = 47;
    public static String[] channelNames = {
            "DEFAULT_TOKEN_CHANNEL", "HIDDEN"
    };

    public static String[] modeNames = {
            "DEFAULT_MODE"
    };

    private static String[] makeRuleNames() {
        return new String[]{
                "T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "VAR", "FUNCTION", "INPUT",
                "NAMESPACE", "OUTPUT", "DW", "ASSIGN", "ARROW", "BOOLEAN", "IDENTIFIER",
                "URL", "MEDIA_TYPE", "NUMBER", "STRING", "DATE", "REGEX", "DOT", "COLON",
                "COMMA", "LCURLY", "RCURLY", "LSQUARE", "RSQUARE", "SEPARATOR", "WS",
                "NEWLINE", "COMMENT", "STAR", "DOUBLE_DOT", "AT", "QUESTION", "OPERATOR_MATH",
                "OPERATOR_COMPARISON", "OPERATOR_LOGICAL", "OPERATOR_BITWISE", "OPERATOR_CONDITIONAL",
                "OPERATOR_RANGE", "OPERATOR_CHAIN", "BUILTIN_FUNCTION", "INDEX_IDENTIFIER",
                "VALUE_IDENTIFIER"
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
            "\u0004\u0000/\u0179\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001" +
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
                    "+\u0002,\u0007,\u0002-\u0007-\u0002.\u0007.\u0001\u0000\u0001\u0000\u0001" +
                    "\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001" +
                    "\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001" +
                    "\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001" +
                    "\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001" +
                    "\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001" +
                    "\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001" +
                    "\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001" +
                    "\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001" +
                    "\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001" +
                    "\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b" +
                    "\u0001\u000b\u0001\f\u0001\f\u0001\r\u0001\r\u0001\r\u0001\u000e\u0001" +
                    "\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001" +
                    "\u000e\u0001\u000e\u0003\u000e\u00b6\b\u000e\u0001\u000f\u0001\u000f\u0001" +
                    "\u000f\u0001\u000f\u0005\u000f\u00bc\b\u000f\n\u000f\f\u000f\u00bf\t\u000f" +
                    "\u0003\u000f\u00c1\b\u000f\u0001\u0010\u0004\u0010\u00c4\b\u0010\u000b" +
                    "\u0010\f\u0010\u00c5\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001" +
                    "\u0010\u0004\u0010\u00cd\b\u0010\u000b\u0010\f\u0010\u00ce\u0001\u0011" +
                    "\u0004\u0011\u00d2\b\u0011\u000b\u0011\f\u0011\u00d3\u0001\u0011\u0001" +
                    "\u0011\u0004\u0011\u00d8\b\u0011\u000b\u0011\f\u0011\u00d9\u0001\u0012" +
                    "\u0004\u0012\u00dd\b\u0012\u000b\u0012\f\u0012\u00de\u0001\u0012\u0001" +
                    "\u0012\u0004\u0012\u00e3\b\u0012\u000b\u0012\f\u0012\u00e4\u0003\u0012" +
                    "\u00e7\b\u0012\u0001\u0013\u0001\u0013\u0005\u0013\u00eb\b\u0013\n\u0013" +
                    "\f\u0013\u00ee\t\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0005\u0013" +
                    "\u00f3\b\u0013\n\u0013\f\u0013\u00f6\t\u0013\u0001\u0013\u0003\u0013\u00f9" +
                    "\b\u0013\u0001\u0014\u0001\u0014\u0005\u0014\u00fd\b\u0014\n\u0014\f\u0014" +
                    "\u0100\t\u0014\u0001\u0014\u0001\u0014\u0001\u0015\u0001\u0015\u0005\u0015" +
                    "\u0106\b\u0015\n\u0015\f\u0015\u0109\t\u0015\u0001\u0015\u0001\u0015\u0001" +
                    "\u0016\u0001\u0016\u0001\u0017\u0001\u0017\u0001\u0018\u0001\u0018\u0001" +
                    "\u0019\u0001\u0019\u0001\u001a\u0001\u001a\u0001\u001b\u0001\u001b\u0001" +
                    "\u001c\u0001\u001c\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001" +
                    "\u001e\u0004\u001e\u0120\b\u001e\u000b\u001e\f\u001e\u0121\u0001\u001e" +
                    "\u0001\u001e\u0001\u001f\u0004\u001f\u0127\b\u001f\u000b\u001f\f\u001f" +
                    "\u0128\u0001\u001f\u0001\u001f\u0001 \u0001 \u0001 \u0001 \u0005 \u0131" +
                    "\b \n \f \u0134\t \u0001 \u0001 \u0001!\u0001!\u0001\"\u0001\"\u0001\"" +
                    "\u0001#\u0001#\u0001$\u0001$\u0001%\u0001%\u0001%\u0001%\u0001%\u0001" +
                    "%\u0003%\u0147\b%\u0001&\u0001&\u0001&\u0001&\u0001&\u0001&\u0001&\u0001" +
                    "&\u0001&\u0003&\u0152\b&\u0001\'\u0001\'\u0001\'\u0001\'\u0001\'\u0003" +
                    "\'\u0159\b\'\u0001(\u0001(\u0001)\u0001)\u0001*\u0001*\u0001+\u0001+\u0001" +
                    "+\u0001,\u0001,\u0001,\u0001,\u0001,\u0001,\u0001,\u0001,\u0001,\u0001" +
                    ",\u0001,\u0001,\u0001,\u0001,\u0001,\u0003,\u0173\b,\u0001-\u0001-\u0001" +
                    "-\u0001.\u0001.\u0004\u00ec\u00f4\u00fe\u0107\u0000/\u0001\u0001\u0003" +
                    "\u0002\u0005\u0003\u0007\u0004\t\u0005\u000b\u0006\r\u0007\u000f\b\u0011" +
                    "\t\u0013\n\u0015\u000b\u0017\f\u0019\r\u001b\u000e\u001d\u000f\u001f\u0010" +
                    "!\u0011#\u0012%\u0013\'\u0014)\u0015+\u0016-\u0017/\u00181\u00193\u001a" +
                    "5\u001b7\u001c9\u001d;\u001e=\u001f? A!C\"E#G$I%K&M\'O(Q)S*U+W,Y-[.]/" +
                    "\u0001\u0000\r\u0003\u0000AZ__az\u0004\u000009AZ__az\u0002\u0000AZaz\u0004" +
                    "\u0000-9AZ__az\u0001\u0000az\u0004\u0000++-.09az\u0001\u000009\u0002\u0000" +
                    "\t\t  \u0002\u0000\n\n\r\r\u0002\u0000++--\u0002\u0000<<>>\u0003\u0000" +
                    "&&^^||\u0002\u0000::??\u0195\u0000\u0001\u0001\u0000\u0000\u0000\u0000" +
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
                    "\u0001\u0000\u0000\u0000\u0000Y\u0001\u0000\u0000\u0000\u0000[\u0001\u0000" +
                    "\u0000\u0000\u0000]\u0001\u0000\u0000\u0000\u0001_\u0001\u0000\u0000\u0000" +
                    "\u0003a\u0001\u0000\u0000\u0000\u0005c\u0001\u0000\u0000\u0000\u0007g" +
                    "\u0001\u0000\u0000\u0000\tn\u0001\u0000\u0000\u0000\u000bt\u0001\u0000" +
                    "\u0000\u0000\rz\u0001\u0000\u0000\u0000\u000f\u007f\u0001\u0000\u0000" +
                    "\u0000\u0011\u0089\u0001\u0000\u0000\u0000\u0013\u0090\u0001\u0000\u0000" +
                    "\u0000\u0015\u009b\u0001\u0000\u0000\u0000\u0017\u00a3\u0001\u0000\u0000" +
                    "\u0000\u0019\u00a7\u0001\u0000\u0000\u0000\u001b\u00a9\u0001\u0000\u0000" +
                    "\u0000\u001d\u00b5\u0001\u0000\u0000\u0000\u001f\u00c0\u0001\u0000\u0000" +
                    "\u0000!\u00c3\u0001\u0000\u0000\u0000#\u00d1\u0001\u0000\u0000\u0000%" +
                    "\u00dc\u0001\u0000\u0000\u0000\'\u00f8\u0001\u0000\u0000\u0000)\u00fa" +
                    "\u0001\u0000\u0000\u0000+\u0103\u0001\u0000\u0000\u0000-\u010c\u0001\u0000" +
                    "\u0000\u0000/\u010e\u0001\u0000\u0000\u00001\u0110\u0001\u0000\u0000\u0000" +
                    "3\u0112\u0001\u0000\u0000\u00005\u0114\u0001\u0000\u0000\u00007\u0116" +
                    "\u0001\u0000\u0000\u00009\u0118\u0001\u0000\u0000\u0000;\u011a\u0001\u0000" +
                    "\u0000\u0000=\u011f\u0001\u0000\u0000\u0000?\u0126\u0001\u0000\u0000\u0000" +
                    "A\u012c\u0001\u0000\u0000\u0000C\u0137\u0001\u0000\u0000\u0000E\u0139" +
                    "\u0001\u0000\u0000\u0000G\u013c\u0001\u0000\u0000\u0000I\u013e\u0001\u0000" +
                    "\u0000\u0000K\u0146\u0001\u0000\u0000\u0000M\u0151\u0001\u0000\u0000\u0000" +
                    "O\u0158\u0001\u0000\u0000\u0000Q\u015a\u0001\u0000\u0000\u0000S\u015c" +
                    "\u0001\u0000\u0000\u0000U\u015e\u0001\u0000\u0000\u0000W\u0160\u0001\u0000" +
                    "\u0000\u0000Y\u0172\u0001\u0000\u0000\u0000[\u0174\u0001\u0000\u0000\u0000" +
                    "]\u0177\u0001\u0000\u0000\u0000_`\u0005(\u0000\u0000`\u0002\u0001\u0000" +
                    "\u0000\u0000ab\u0005)\u0000\u0000b\u0004\u0001\u0000\u0000\u0000cd\u0005" +
                    "m\u0000\u0000de\u0005a\u0000\u0000ef\u0005p\u0000\u0000f\u0006\u0001\u0000" +
                    "\u0000\u0000gh\u0005s\u0000\u0000hi\u0005i\u0000\u0000ij\u0005z\u0000" +
                    "\u0000jk\u0005e\u0000\u0000kl\u0005O\u0000\u0000lm\u0005f\u0000\u0000" +
                    "m\b\u0001\u0000\u0000\u0000no\u0005u\u0000\u0000op\u0005p\u0000\u0000" +
                    "pq\u0005p\u0000\u0000qr\u0005e\u0000\u0000rs\u0005r\u0000\u0000s\n\u0001" +
                    "\u0000\u0000\u0000tu\u0005l\u0000\u0000uv\u0005o\u0000\u0000vw\u0005w" +
                    "\u0000\u0000wx\u0005e\u0000\u0000xy\u0005r\u0000\u0000y\f\u0001\u0000" +
                    "\u0000\u0000z{\u0005%\u0000\u0000{|\u0005v\u0000\u0000|}\u0005a\u0000" +
                    "\u0000}~\u0005r\u0000\u0000~\u000e\u0001\u0000\u0000\u0000\u007f\u0080" +
                    "\u0005%\u0000\u0000\u0080\u0081\u0005f\u0000\u0000\u0081\u0082\u0005u" +
                    "\u0000\u0000\u0082\u0083\u0005n\u0000\u0000\u0083\u0084\u0005c\u0000\u0000" +
                    "\u0084\u0085\u0005t\u0000\u0000\u0085\u0086\u0005i\u0000\u0000\u0086\u0087" +
                    "\u0005o\u0000\u0000\u0087\u0088\u0005n\u0000\u0000\u0088\u0010\u0001\u0000" +
                    "\u0000\u0000\u0089\u008a\u0005%\u0000\u0000\u008a\u008b\u0005i\u0000\u0000" +
                    "\u008b\u008c\u0005n\u0000\u0000\u008c\u008d\u0005p\u0000\u0000\u008d\u008e" +
                    "\u0005u\u0000\u0000\u008e\u008f\u0005t\u0000\u0000\u008f\u0012\u0001\u0000" +
                    "\u0000\u0000\u0090\u0091\u0005%\u0000\u0000\u0091\u0092\u0005n\u0000\u0000" +
                    "\u0092\u0093\u0005a\u0000\u0000\u0093\u0094\u0005m\u0000\u0000\u0094\u0095" +
                    "\u0005e\u0000\u0000\u0095\u0096\u0005s\u0000\u0000\u0096\u0097\u0005p" +
                    "\u0000\u0000\u0097\u0098\u0005a\u0000\u0000\u0098\u0099\u0005c\u0000\u0000" +
                    "\u0099\u009a\u0005e\u0000\u0000\u009a\u0014\u0001\u0000\u0000\u0000\u009b" +
                    "\u009c\u0005%\u0000\u0000\u009c\u009d\u0005o\u0000\u0000\u009d\u009e\u0005" +
                    "u\u0000\u0000\u009e\u009f\u0005t\u0000\u0000\u009f\u00a0\u0005p\u0000" +
                    "\u0000\u00a0\u00a1\u0005u\u0000\u0000\u00a1\u00a2\u0005t\u0000\u0000\u00a2" +
                    "\u0016\u0001\u0000\u0000\u0000\u00a3\u00a4\u0005%\u0000\u0000\u00a4\u00a5" +
                    "\u0005d\u0000\u0000\u00a5\u00a6\u0005w\u0000\u0000\u00a6\u0018\u0001\u0000" +
                    "\u0000\u0000\u00a7\u00a8\u0005=\u0000\u0000\u00a8\u001a\u0001\u0000\u0000" +
                    "\u0000\u00a9\u00aa\u0005-\u0000\u0000\u00aa\u00ab\u0005>\u0000\u0000\u00ab" +
                    "\u001c\u0001\u0000\u0000\u0000\u00ac\u00ad\u0005t\u0000\u0000\u00ad\u00ae" +
                    "\u0005r\u0000\u0000\u00ae\u00af\u0005u\u0000\u0000\u00af\u00b6\u0005e" +
                    "\u0000\u0000\u00b0\u00b1\u0005f\u0000\u0000\u00b1\u00b2\u0005a\u0000\u0000" +
                    "\u00b2\u00b3\u0005l\u0000\u0000\u00b3\u00b4\u0005s\u0000\u0000\u00b4\u00b6" +
                    "\u0005e\u0000\u0000\u00b5\u00ac\u0001\u0000\u0000\u0000\u00b5\u00b0\u0001" +
                    "\u0000\u0000\u0000\u00b6\u001e\u0001\u0000\u0000\u0000\u00b7\u00c1\u0003" +
                    "[-\u0000\u00b8\u00c1\u0003].\u0000\u00b9\u00bd\u0007\u0000\u0000\u0000" +
                    "\u00ba\u00bc\u0007\u0001\u0000\u0000\u00bb\u00ba\u0001\u0000\u0000\u0000" +
                    "\u00bc\u00bf\u0001\u0000\u0000\u0000\u00bd\u00bb\u0001\u0000\u0000\u0000" +
                    "\u00bd\u00be\u0001\u0000\u0000\u0000\u00be\u00c1\u0001\u0000\u0000\u0000" +
                    "\u00bf\u00bd\u0001\u0000\u0000\u0000\u00c0\u00b7\u0001\u0000\u0000\u0000" +
                    "\u00c0\u00b8\u0001\u0000\u0000\u0000\u00c0\u00b9\u0001\u0000\u0000\u0000" +
                    "\u00c1 \u0001\u0000\u0000\u0000\u00c2\u00c4\u0007\u0002\u0000\u0000\u00c3" +
                    "\u00c2\u0001\u0000\u0000\u0000\u00c4\u00c5\u0001\u0000\u0000\u0000\u00c5" +
                    "\u00c3\u0001\u0000\u0000\u0000\u00c5\u00c6\u0001\u0000\u0000\u0000\u00c6" +
                    "\u00c7\u0001\u0000\u0000\u0000\u00c7\u00c8\u0005:\u0000\u0000\u00c8\u00c9" +
                    "\u0005/\u0000\u0000\u00c9\u00ca\u0005/\u0000\u0000\u00ca\u00cc\u0001\u0000" +
                    "\u0000\u0000\u00cb\u00cd\u0007\u0003\u0000\u0000\u00cc\u00cb\u0001\u0000" +
                    "\u0000\u0000\u00cd\u00ce\u0001\u0000\u0000\u0000\u00ce\u00cc\u0001\u0000" +
                    "\u0000\u0000\u00ce\u00cf\u0001\u0000\u0000\u0000\u00cf\"\u0001\u0000\u0000" +
                    "\u0000\u00d0\u00d2\u0007\u0004\u0000\u0000\u00d1\u00d0\u0001\u0000\u0000" +
                    "\u0000\u00d2\u00d3\u0001\u0000\u0000\u0000\u00d3\u00d1\u0001\u0000\u0000" +
                    "\u0000\u00d3\u00d4\u0001\u0000\u0000\u0000\u00d4\u00d5\u0001\u0000\u0000" +
                    "\u0000\u00d5\u00d7\u0005/\u0000\u0000\u00d6\u00d8\u0007\u0005\u0000\u0000" +
                    "\u00d7\u00d6\u0001\u0000\u0000\u0000\u00d8\u00d9\u0001\u0000\u0000\u0000" +
                    "\u00d9\u00d7\u0001\u0000\u0000\u0000\u00d9\u00da\u0001\u0000\u0000\u0000" +
                    "\u00da$\u0001\u0000\u0000\u0000\u00db\u00dd\u0007\u0006\u0000\u0000\u00dc" +
                    "\u00db\u0001\u0000\u0000\u0000\u00dd\u00de\u0001\u0000\u0000\u0000\u00de" +
                    "\u00dc\u0001\u0000\u0000\u0000\u00de\u00df\u0001\u0000\u0000\u0000\u00df" +
                    "\u00e6\u0001\u0000\u0000\u0000\u00e0\u00e2\u0005.\u0000\u0000\u00e1\u00e3" +
                    "\u0007\u0006\u0000\u0000\u00e2\u00e1\u0001\u0000\u0000\u0000\u00e3\u00e4" +
                    "\u0001\u0000\u0000\u0000\u00e4\u00e2\u0001\u0000\u0000\u0000\u00e4\u00e5" +
                    "\u0001\u0000\u0000\u0000\u00e5\u00e7\u0001\u0000\u0000\u0000\u00e6\u00e0" +
                    "\u0001\u0000\u0000\u0000\u00e6\u00e7\u0001\u0000\u0000\u0000\u00e7&\u0001" +
                    "\u0000\u0000\u0000\u00e8\u00ec\u0005\"\u0000\u0000\u00e9\u00eb\t\u0000" +
                    "\u0000\u0000\u00ea\u00e9\u0001\u0000\u0000\u0000\u00eb\u00ee\u0001\u0000" +
                    "\u0000\u0000\u00ec\u00ed\u0001\u0000\u0000\u0000\u00ec\u00ea\u0001\u0000" +
                    "\u0000\u0000\u00ed\u00ef\u0001\u0000\u0000\u0000\u00ee\u00ec\u0001\u0000" +
                    "\u0000\u0000\u00ef\u00f9\u0005\"\u0000\u0000\u00f0\u00f4\u0005\'\u0000" +
                    "\u0000\u00f1\u00f3\t\u0000\u0000\u0000\u00f2\u00f1\u0001\u0000\u0000\u0000" +
                    "\u00f3\u00f6\u0001\u0000\u0000\u0000\u00f4\u00f5\u0001\u0000\u0000\u0000" +
                    "\u00f4\u00f2\u0001\u0000\u0000\u0000\u00f5\u00f7\u0001\u0000\u0000\u0000" +
                    "\u00f6\u00f4\u0001\u0000\u0000\u0000\u00f7\u00f9\u0005\'\u0000\u0000\u00f8" +
                    "\u00e8\u0001\u0000\u0000\u0000\u00f8\u00f0\u0001\u0000\u0000\u0000\u00f9" +
                    "(\u0001\u0000\u0000\u0000\u00fa\u00fe\u0005|\u0000\u0000\u00fb\u00fd\t" +
                    "\u0000\u0000\u0000\u00fc\u00fb\u0001\u0000\u0000\u0000\u00fd\u0100\u0001" +
                    "\u0000\u0000\u0000\u00fe\u00ff\u0001\u0000\u0000\u0000\u00fe\u00fc\u0001" +
                    "\u0000\u0000\u0000\u00ff\u0101\u0001\u0000\u0000\u0000\u0100\u00fe\u0001" +
                    "\u0000\u0000\u0000\u0101\u0102\u0005|\u0000\u0000\u0102*\u0001\u0000\u0000" +
                    "\u0000\u0103\u0107\u0005/\u0000\u0000\u0104\u0106\t\u0000\u0000\u0000" +
                    "\u0105\u0104\u0001\u0000\u0000\u0000\u0106\u0109\u0001\u0000\u0000\u0000" +
                    "\u0107\u0108\u0001\u0000\u0000\u0000\u0107\u0105\u0001\u0000\u0000\u0000" +
                    "\u0108\u010a\u0001\u0000\u0000\u0000\u0109\u0107\u0001\u0000\u0000\u0000" +
                    "\u010a\u010b\u0005/\u0000\u0000\u010b,\u0001\u0000\u0000\u0000\u010c\u010d" +
                    "\u0005.\u0000\u0000\u010d.\u0001\u0000\u0000\u0000\u010e\u010f\u0005:" +
                    "\u0000\u0000\u010f0\u0001\u0000\u0000\u0000\u0110\u0111\u0005,\u0000\u0000" +
                    "\u01112\u0001\u0000\u0000\u0000\u0112\u0113\u0005{\u0000\u0000\u01134" +
                    "\u0001\u0000\u0000\u0000\u0114\u0115\u0005}\u0000\u0000\u01156\u0001\u0000" +
                    "\u0000\u0000\u0116\u0117\u0005[\u0000\u0000\u01178\u0001\u0000\u0000\u0000" +
                    "\u0118\u0119\u0005]\u0000\u0000\u0119:\u0001\u0000\u0000\u0000\u011a\u011b" +
                    "\u0005-\u0000\u0000\u011b\u011c\u0005-\u0000\u0000\u011c\u011d\u0005-" +
                    "\u0000\u0000\u011d<\u0001\u0000\u0000\u0000\u011e\u0120\u0007\u0007\u0000" +
                    "\u0000\u011f\u011e\u0001\u0000\u0000\u0000\u0120\u0121\u0001\u0000\u0000" +
                    "\u0000\u0121\u011f\u0001\u0000\u0000\u0000\u0121\u0122\u0001\u0000\u0000" +
                    "\u0000\u0122\u0123\u0001\u0000\u0000\u0000\u0123\u0124\u0006\u001e\u0000" +
                    "\u0000\u0124>\u0001\u0000\u0000\u0000\u0125\u0127\u0007\b\u0000\u0000" +
                    "\u0126\u0125\u0001\u0000\u0000\u0000\u0127\u0128\u0001\u0000\u0000\u0000" +
                    "\u0128\u0126\u0001\u0000\u0000\u0000\u0128\u0129\u0001\u0000\u0000\u0000" +
                    "\u0129\u012a\u0001\u0000\u0000\u0000\u012a\u012b\u0006\u001f\u0000\u0000" +
                    "\u012b@\u0001\u0000\u0000\u0000\u012c\u012d\u0005/\u0000\u0000\u012d\u012e" +
                    "\u0005/\u0000\u0000\u012e\u0132\u0001\u0000\u0000\u0000\u012f\u0131\b" +
                    "\b\u0000\u0000\u0130\u012f\u0001\u0000\u0000\u0000\u0131\u0134\u0001\u0000" +
                    "\u0000\u0000\u0132\u0130\u0001\u0000\u0000\u0000\u0132\u0133\u0001\u0000" +
                    "\u0000\u0000\u0133\u0135\u0001\u0000\u0000\u0000\u0134\u0132\u0001\u0000" +
                    "\u0000\u0000\u0135\u0136\u0006 \u0000\u0000\u0136B\u0001\u0000\u0000\u0000" +
                    "\u0137\u0138\u0005*\u0000\u0000\u0138D\u0001\u0000\u0000\u0000\u0139\u013a" +
                    "\u0005.\u0000\u0000\u013a\u013b\u0005.\u0000\u0000\u013bF\u0001\u0000" +
                    "\u0000\u0000\u013c\u013d\u0005@\u0000\u0000\u013dH\u0001\u0000\u0000\u0000" +
                    "\u013e\u013f\u0005?\u0000\u0000\u013fJ\u0001\u0000\u0000\u0000\u0140\u0147" +
                    "\u0007\t\u0000\u0000\u0141\u0147\u0003C!\u0000\u0142\u0147\u0005/\u0000" +
                    "\u0000\u0143\u0144\u0005m\u0000\u0000\u0144\u0145\u0005o\u0000\u0000\u0145" +
                    "\u0147\u0005d\u0000\u0000\u0146\u0140\u0001\u0000\u0000\u0000\u0146\u0141" +
                    "\u0001\u0000\u0000\u0000\u0146\u0142\u0001\u0000\u0000\u0000\u0146\u0143" +
                    "\u0001\u0000\u0000\u0000\u0147L\u0001\u0000\u0000\u0000\u0148\u0149\u0005" +
                    "=\u0000\u0000\u0149\u0152\u0005=\u0000\u0000\u014a\u014b\u0005!\u0000" +
                    "\u0000\u014b\u0152\u0005=\u0000\u0000\u014c\u0152\u0007\n\u0000\u0000" +
                    "\u014d\u014e\u0005>\u0000\u0000\u014e\u0152\u0005=\u0000\u0000\u014f\u0150" +
                    "\u0005<\u0000\u0000\u0150\u0152\u0005=\u0000\u0000\u0151\u0148\u0001\u0000" +
                    "\u0000\u0000\u0151\u014a\u0001\u0000\u0000\u0000\u0151\u014c\u0001\u0000" +
                    "\u0000\u0000\u0151\u014d\u0001\u0000\u0000\u0000\u0151\u014f\u0001\u0000" +
                    "\u0000\u0000\u0152N\u0001\u0000\u0000\u0000\u0153\u0154\u0005a\u0000\u0000" +
                    "\u0154\u0155\u0005n\u0000\u0000\u0155\u0159\u0005d\u0000\u0000\u0156\u0157" +
                    "\u0005o\u0000\u0000\u0157\u0159\u0005r\u0000\u0000\u0158\u0153\u0001\u0000" +
                    "\u0000\u0000\u0158\u0156\u0001\u0000\u0000\u0000\u0159P\u0001\u0000\u0000" +
                    "\u0000\u015a\u015b\u0007\u000b\u0000\u0000\u015bR\u0001\u0000\u0000\u0000" +
                    "\u015c\u015d\u0007\f\u0000\u0000\u015dT\u0001\u0000\u0000\u0000\u015e" +
                    "\u015f\u0003E\"\u0000\u015fV\u0001\u0000\u0000\u0000\u0160\u0161\u0005" +
                    "+\u0000\u0000\u0161\u0162\u0005+\u0000\u0000\u0162X\u0001\u0000\u0000" +
                    "\u0000\u0163\u0164\u0005s\u0000\u0000\u0164\u0165\u0005i\u0000\u0000\u0165" +
                    "\u0166\u0005z\u0000\u0000\u0166\u0167\u0005e\u0000\u0000\u0167\u0168\u0005" +
                    "O\u0000\u0000\u0168\u0173\u0005f\u0000\u0000\u0169\u016a\u0005m\u0000" +
                    "\u0000\u016a\u016b\u0005a\u0000\u0000\u016b\u0173\u0005p\u0000\u0000\u016c" +
                    "\u016d\u0005f\u0000\u0000\u016d\u016e\u0005i\u0000\u0000\u016e\u016f\u0005" +
                    "l\u0000\u0000\u016f\u0170\u0005t\u0000\u0000\u0170\u0171\u0005e\u0000" +
                    "\u0000\u0171\u0173\u0005r\u0000\u0000\u0172\u0163\u0001\u0000\u0000\u0000" +
                    "\u0172\u0169\u0001\u0000\u0000\u0000\u0172\u016c\u0001\u0000\u0000\u0000" +
                    "\u0173Z\u0001\u0000\u0000\u0000\u0174\u0175\u0005$\u0000\u0000\u0175\u0176" +
                    "\u0005$\u0000\u0000\u0176\\\u0001\u0000\u0000\u0000\u0177\u0178\u0005" +
                    "$\u0000\u0000\u0178^\u0001\u0000\u0000\u0000\u0017\u0000\u00b5\u00bd\u00c0" +
                    "\u00c5\u00ce\u00d3\u00d9\u00de\u00e4\u00e6\u00ec\u00f4\u00f8\u00fe\u0107" +
                    "\u0121\u0128\u0132\u0146\u0151\u0158\u0172\u0001\u0006\u0000\u0000";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}