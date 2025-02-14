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
            MOD = 17, OPERATOR_LOGICAL = 18, IDENTIFIER = 19, URL = 20, MEDIA_TYPE = 21, NUMBER = 22,
            STRING = 23, DATE = 24, REGEX = 25, DOT = 26, COLON = 27, COMMA = 28, LCURLY = 29, RCURLY = 30,
            LSQUARE = 31, RSQUARE = 32, SEPARATOR = 33, WS = 34, NEWLINE = 35, COMMENT = 36, OPERATOR_MATH = 37,
            OPERATOR_COMPARISON = 38, OPERATOR_BITWISE = 39, OPERATOR_CONDITIONAL = 40,
            OPERATOR_RANGE = 41, OPERATOR_CHAIN = 42, STAR = 43, DOUBLE_DOT = 44, AT = 45, QUESTION = 46,
            BUILTIN_FUNCTION = 47, INDEX_IDENTIFIER = 48, VALUE_IDENTIFIER = 49;
    public static String[] channelNames = {
            "DEFAULT_TOKEN_CHANNEL", "HIDDEN"
    };

    public static String[] modeNames = {
            "DEFAULT_MODE"
    };

    private static String[] makeRuleNames() {
        return new String[]{
                "T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "VAR", "FUNCTION",
                "INPUT", "NAMESPACE", "OUTPUT", "DW", "ASSIGN", "ARROW", "BOOLEAN", "MOD",
                "OPERATOR_LOGICAL", "IDENTIFIER", "URL", "MEDIA_TYPE", "NUMBER", "STRING",
                "DATE", "REGEX", "DOT", "COLON", "COMMA", "LCURLY", "RCURLY", "LSQUARE",
                "RSQUARE", "SEPARATOR", "WS", "NEWLINE", "COMMENT", "OPERATOR_MATH",
                "OPERATOR_COMPARISON", "OPERATOR_BITWISE", "OPERATOR_CONDITIONAL", "OPERATOR_RANGE",
                "OPERATOR_CHAIN", "STAR", "DOUBLE_DOT", "AT", "QUESTION", "BUILTIN_FUNCTION",
                "INDEX_IDENTIFIER", "VALUE_IDENTIFIER"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'('", "')'", "'filter'", "'map'", "'sizeOf'", "'upper'", "'lower'",
                "'%var'", "'%function'", "'%input'", "'%namespace'", "'%output'", "'%dw'",
                "'='", "'->'", null, "'mod'", null, null, null, null, null, null, null,
                null, "'.'", "':'", "','", "'{'", "'}'", "'['", "']'", "'---'", null,
                null, null, null, null, null, null, null, "'++'", "'*'", "'..'", "'@'",
                "'?'", null, "'$$'", "'$'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, null, null, null, null, null, null, null, "VAR", "FUNCTION", "INPUT",
                "NAMESPACE", "OUTPUT", "DW", "ASSIGN", "ARROW", "BOOLEAN", "MOD", "OPERATOR_LOGICAL",
                "IDENTIFIER", "URL", "MEDIA_TYPE", "NUMBER", "STRING", "DATE", "REGEX",
                "DOT", "COLON", "COMMA", "LCURLY", "RCURLY", "LSQUARE", "RSQUARE", "SEPARATOR",
                "WS", "NEWLINE", "COMMENT", "OPERATOR_MATH", "OPERATOR_COMPARISON", "OPERATOR_BITWISE",
                "OPERATOR_CONDITIONAL", "OPERATOR_RANGE", "OPERATOR_CHAIN", "STAR", "DOUBLE_DOT",
                "AT", "QUESTION", "BUILTIN_FUNCTION", "INDEX_IDENTIFIER", "VALUE_IDENTIFIER"
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
            "\u0004\u00001\u0186\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001" +
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
                    "+\u0002,\u0007,\u0002-\u0007-\u0002.\u0007.\u0002/\u0007/\u00020\u0007" +
                    "0\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002" +
                    "\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003" +
                    "\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004" +
                    "\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005" +
                    "\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006" +
                    "\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007" +
                    "\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001" +
                    "\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001" +
                    "\t\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001" +
                    "\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001" +
                    "\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001" +
                    "\f\u0001\f\u0001\f\u0001\f\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001" +
                    "\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001" +
                    "\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0003\u000f\u00c1\b\u000f\u0001" +
                    "\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011\u0001" +
                    "\u0011\u0001\u0011\u0001\u0011\u0003\u0011\u00cc\b\u0011\u0001\u0012\u0001" +
                    "\u0012\u0001\u0012\u0001\u0012\u0005\u0012\u00d2\b\u0012\n\u0012\f\u0012" +
                    "\u00d5\t\u0012\u0003\u0012\u00d7\b\u0012\u0001\u0013\u0004\u0013\u00da" +
                    "\b\u0013\u000b\u0013\f\u0013\u00db\u0001\u0013\u0001\u0013\u0001\u0013" +
                    "\u0001\u0013\u0001\u0013\u0004\u0013\u00e3\b\u0013\u000b\u0013\f\u0013" +
                    "\u00e4\u0001\u0014\u0004\u0014\u00e8\b\u0014\u000b\u0014\f\u0014\u00e9" +
                    "\u0001\u0014\u0001\u0014\u0004\u0014\u00ee\b\u0014\u000b\u0014\f\u0014" +
                    "\u00ef\u0001\u0015\u0004\u0015\u00f3\b\u0015\u000b\u0015\f\u0015\u00f4" +
                    "\u0001\u0015\u0001\u0015\u0004\u0015\u00f9\b\u0015\u000b\u0015\f\u0015" +
                    "\u00fa\u0003\u0015\u00fd\b\u0015\u0001\u0016\u0001\u0016\u0005\u0016\u0101" +
                    "\b\u0016\n\u0016\f\u0016\u0104\t\u0016\u0001\u0016\u0001\u0016\u0001\u0016" +
                    "\u0005\u0016\u0109\b\u0016\n\u0016\f\u0016\u010c\t\u0016\u0001\u0016\u0003" +
                    "\u0016\u010f\b\u0016\u0001\u0017\u0001\u0017\u0005\u0017\u0113\b\u0017" +
                    "\n\u0017\f\u0017\u0116\t\u0017\u0001\u0017\u0001\u0017\u0001\u0018\u0001" +
                    "\u0018\u0005\u0018\u011c\b\u0018\n\u0018\f\u0018\u011f\t\u0018\u0001\u0018" +
                    "\u0001\u0018\u0001\u0019\u0001\u0019\u0001\u001a\u0001\u001a\u0001\u001b" +
                    "\u0001\u001b\u0001\u001c\u0001\u001c\u0001\u001d\u0001\u001d\u0001\u001e" +
                    "\u0001\u001e\u0001\u001f\u0001\u001f\u0001 \u0001 \u0001 \u0001 \u0001" +
                    "!\u0004!\u0136\b!\u000b!\f!\u0137\u0001!\u0001!\u0001\"\u0004\"\u013d" +
                    "\b\"\u000b\"\f\"\u013e\u0001\"\u0001\"\u0001#\u0001#\u0001#\u0001#\u0005" +
                    "#\u0147\b#\n#\f#\u014a\t#\u0001#\u0001#\u0001$\u0001$\u0001$\u0001$\u0003" +
                    "$\u0152\b$\u0001%\u0001%\u0001%\u0001%\u0001%\u0001%\u0001%\u0001%\u0001" +
                    "%\u0003%\u015d\b%\u0001&\u0001&\u0001\'\u0001\'\u0001(\u0001(\u0001)\u0001" +
                    ")\u0001)\u0001*\u0001*\u0001+\u0001+\u0001+\u0001,\u0001,\u0001-\u0001" +
                    "-\u0001.\u0001.\u0001.\u0001.\u0001.\u0001.\u0001.\u0001.\u0001.\u0001" +
                    ".\u0001.\u0001.\u0001.\u0001.\u0001.\u0003.\u0180\b.\u0001/\u0001/\u0001" +
                    "/\u00010\u00010\u0004\u0102\u010a\u0114\u011d\u00001\u0001\u0001\u0003" +
                    "\u0002\u0005\u0003\u0007\u0004\t\u0005\u000b\u0006\r\u0007\u000f\b\u0011" +
                    "\t\u0013\n\u0015\u000b\u0017\f\u0019\r\u001b\u000e\u001d\u000f\u001f\u0010" +
                    "!\u0011#\u0012%\u0013\'\u0014)\u0015+\u0016-\u0017/\u00181\u00193\u001a" +
                    "5\u001b7\u001c9\u001d;\u001e=\u001f? A!C\"E#G$I%K&M\'O(Q)S*U+W,Y-[.]/" +
                    "_0a1\u0001\u0000\r\u0003\u0000AZ__az\u0004\u000009AZ__az\u0002\u0000A" +
                    "Zaz\u0004\u0000-9AZ__az\u0001\u0000az\u0004\u0000++-.09az\u0001\u0000" +
                    "09\u0002\u0000\t\t  \u0002\u0000\n\n\r\r\u0002\u0000++--\u0002\u0000<" +
                    "<>>\u0003\u0000&&^^||\u0002\u0000::??\u01a2\u0000\u0001\u0001\u0000\u0000" +
                    "\u0000\u0000\u0003\u0001\u0000\u0000\u0000\u0000\u0005\u0001\u0000\u0000" +
                    "\u0000\u0000\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001\u0000\u0000\u0000" +
                    "\u0000\u000b\u0001\u0000\u0000\u0000\u0000\r\u0001\u0000\u0000\u0000\u0000" +
                    "\u000f\u0001\u0000\u0000\u0000\u0000\u0011\u0001\u0000\u0000\u0000\u0000" +
                    "\u0013\u0001\u0000\u0000\u0000\u0000\u0015\u0001\u0000\u0000\u0000\u0000" +
                    "\u0017\u0001\u0000\u0000\u0000\u0000\u0019\u0001\u0000\u0000\u0000\u0000" +
                    "\u001b\u0001\u0000\u0000\u0000\u0000\u001d\u0001\u0000\u0000\u0000\u0000" +
                    "\u001f\u0001\u0000\u0000\u0000\u0000!\u0001\u0000\u0000\u0000\u0000#\u0001" +
                    "\u0000\u0000\u0000\u0000%\u0001\u0000\u0000\u0000\u0000\'\u0001\u0000" +
                    "\u0000\u0000\u0000)\u0001\u0000\u0000\u0000\u0000+\u0001\u0000\u0000\u0000" +
                    "\u0000-\u0001\u0000\u0000\u0000\u0000/\u0001\u0000\u0000\u0000\u00001" +
                    "\u0001\u0000\u0000\u0000\u00003\u0001\u0000\u0000\u0000\u00005\u0001\u0000" +
                    "\u0000\u0000\u00007\u0001\u0000\u0000\u0000\u00009\u0001\u0000\u0000\u0000" +
                    "\u0000;\u0001\u0000\u0000\u0000\u0000=\u0001\u0000\u0000\u0000\u0000?" +
                    "\u0001\u0000\u0000\u0000\u0000A\u0001\u0000\u0000\u0000\u0000C\u0001\u0000" +
                    "\u0000\u0000\u0000E\u0001\u0000\u0000\u0000\u0000G\u0001\u0000\u0000\u0000" +
                    "\u0000I\u0001\u0000\u0000\u0000\u0000K\u0001\u0000\u0000\u0000\u0000M" +
                    "\u0001\u0000\u0000\u0000\u0000O\u0001\u0000\u0000\u0000\u0000Q\u0001\u0000" +
                    "\u0000\u0000\u0000S\u0001\u0000\u0000\u0000\u0000U\u0001\u0000\u0000\u0000" +
                    "\u0000W\u0001\u0000\u0000\u0000\u0000Y\u0001\u0000\u0000\u0000\u0000[" +
                    "\u0001\u0000\u0000\u0000\u0000]\u0001\u0000\u0000\u0000\u0000_\u0001\u0000" +
                    "\u0000\u0000\u0000a\u0001\u0000\u0000\u0000\u0001c\u0001\u0000\u0000\u0000" +
                    "\u0003e\u0001\u0000\u0000\u0000\u0005g\u0001\u0000\u0000\u0000\u0007n" +
                    "\u0001\u0000\u0000\u0000\tr\u0001\u0000\u0000\u0000\u000by\u0001\u0000" +
                    "\u0000\u0000\r\u007f\u0001\u0000\u0000\u0000\u000f\u0085\u0001\u0000\u0000" +
                    "\u0000\u0011\u008a\u0001\u0000\u0000\u0000\u0013\u0094\u0001\u0000\u0000" +
                    "\u0000\u0015\u009b\u0001\u0000\u0000\u0000\u0017\u00a6\u0001\u0000\u0000" +
                    "\u0000\u0019\u00ae\u0001\u0000\u0000\u0000\u001b\u00b2\u0001\u0000\u0000" +
                    "\u0000\u001d\u00b4\u0001\u0000\u0000\u0000\u001f\u00c0\u0001\u0000\u0000" +
                    "\u0000!\u00c2\u0001\u0000\u0000\u0000#\u00cb\u0001\u0000\u0000\u0000%" +
                    "\u00d6\u0001\u0000\u0000\u0000\'\u00d9\u0001\u0000\u0000\u0000)\u00e7" +
                    "\u0001\u0000\u0000\u0000+\u00f2\u0001\u0000\u0000\u0000-\u010e\u0001\u0000" +
                    "\u0000\u0000/\u0110\u0001\u0000\u0000\u00001\u0119\u0001\u0000\u0000\u0000" +
                    "3\u0122\u0001\u0000\u0000\u00005\u0124\u0001\u0000\u0000\u00007\u0126" +
                    "\u0001\u0000\u0000\u00009\u0128\u0001\u0000\u0000\u0000;\u012a\u0001\u0000" +
                    "\u0000\u0000=\u012c\u0001\u0000\u0000\u0000?\u012e\u0001\u0000\u0000\u0000" +
                    "A\u0130\u0001\u0000\u0000\u0000C\u0135\u0001\u0000\u0000\u0000E\u013c" +
                    "\u0001\u0000\u0000\u0000G\u0142\u0001\u0000\u0000\u0000I\u0151\u0001\u0000" +
                    "\u0000\u0000K\u015c\u0001\u0000\u0000\u0000M\u015e\u0001\u0000\u0000\u0000" +
                    "O\u0160\u0001\u0000\u0000\u0000Q\u0162\u0001\u0000\u0000\u0000S\u0164" +
                    "\u0001\u0000\u0000\u0000U\u0167\u0001\u0000\u0000\u0000W\u0169\u0001\u0000" +
                    "\u0000\u0000Y\u016c\u0001\u0000\u0000\u0000[\u016e\u0001\u0000\u0000\u0000" +
                    "]\u017f\u0001\u0000\u0000\u0000_\u0181\u0001\u0000\u0000\u0000a\u0184" +
                    "\u0001\u0000\u0000\u0000cd\u0005(\u0000\u0000d\u0002\u0001\u0000\u0000" +
                    "\u0000ef\u0005)\u0000\u0000f\u0004\u0001\u0000\u0000\u0000gh\u0005f\u0000" +
                    "\u0000hi\u0005i\u0000\u0000ij\u0005l\u0000\u0000jk\u0005t\u0000\u0000" +
                    "kl\u0005e\u0000\u0000lm\u0005r\u0000\u0000m\u0006\u0001\u0000\u0000\u0000" +
                    "no\u0005m\u0000\u0000op\u0005a\u0000\u0000pq\u0005p\u0000\u0000q\b\u0001" +
                    "\u0000\u0000\u0000rs\u0005s\u0000\u0000st\u0005i\u0000\u0000tu\u0005z" +
                    "\u0000\u0000uv\u0005e\u0000\u0000vw\u0005O\u0000\u0000wx\u0005f\u0000" +
                    "\u0000x\n\u0001\u0000\u0000\u0000yz\u0005u\u0000\u0000z{\u0005p\u0000" +
                    "\u0000{|\u0005p\u0000\u0000|}\u0005e\u0000\u0000}~\u0005r\u0000\u0000" +
                    "~\f\u0001\u0000\u0000\u0000\u007f\u0080\u0005l\u0000\u0000\u0080\u0081" +
                    "\u0005o\u0000\u0000\u0081\u0082\u0005w\u0000\u0000\u0082\u0083\u0005e" +
                    "\u0000\u0000\u0083\u0084\u0005r\u0000\u0000\u0084\u000e\u0001\u0000\u0000" +
                    "\u0000\u0085\u0086\u0005%\u0000\u0000\u0086\u0087\u0005v\u0000\u0000\u0087" +
                    "\u0088\u0005a\u0000\u0000\u0088\u0089\u0005r\u0000\u0000\u0089\u0010\u0001" +
                    "\u0000\u0000\u0000\u008a\u008b\u0005%\u0000\u0000\u008b\u008c\u0005f\u0000" +
                    "\u0000\u008c\u008d\u0005u\u0000\u0000\u008d\u008e\u0005n\u0000\u0000\u008e" +
                    "\u008f\u0005c\u0000\u0000\u008f\u0090\u0005t\u0000\u0000\u0090\u0091\u0005" +
                    "i\u0000\u0000\u0091\u0092\u0005o\u0000\u0000\u0092\u0093\u0005n\u0000" +
                    "\u0000\u0093\u0012\u0001\u0000\u0000\u0000\u0094\u0095\u0005%\u0000\u0000" +
                    "\u0095\u0096\u0005i\u0000\u0000\u0096\u0097\u0005n\u0000\u0000\u0097\u0098" +
                    "\u0005p\u0000\u0000\u0098\u0099\u0005u\u0000\u0000\u0099\u009a\u0005t" +
                    "\u0000\u0000\u009a\u0014\u0001\u0000\u0000\u0000\u009b\u009c\u0005%\u0000" +
                    "\u0000\u009c\u009d\u0005n\u0000\u0000\u009d\u009e\u0005a\u0000\u0000\u009e" +
                    "\u009f\u0005m\u0000\u0000\u009f\u00a0\u0005e\u0000\u0000\u00a0\u00a1\u0005" +
                    "s\u0000\u0000\u00a1\u00a2\u0005p\u0000\u0000\u00a2\u00a3\u0005a\u0000" +
                    "\u0000\u00a3\u00a4\u0005c\u0000\u0000\u00a4\u00a5\u0005e\u0000\u0000\u00a5" +
                    "\u0016\u0001\u0000\u0000\u0000\u00a6\u00a7\u0005%\u0000\u0000\u00a7\u00a8" +
                    "\u0005o\u0000\u0000\u00a8\u00a9\u0005u\u0000\u0000\u00a9\u00aa\u0005t" +
                    "\u0000\u0000\u00aa\u00ab\u0005p\u0000\u0000\u00ab\u00ac\u0005u\u0000\u0000" +
                    "\u00ac\u00ad\u0005t\u0000\u0000\u00ad\u0018\u0001\u0000\u0000\u0000\u00ae" +
                    "\u00af\u0005%\u0000\u0000\u00af\u00b0\u0005d\u0000\u0000\u00b0\u00b1\u0005" +
                    "w\u0000\u0000\u00b1\u001a\u0001\u0000\u0000\u0000\u00b2\u00b3\u0005=\u0000" +
                    "\u0000\u00b3\u001c\u0001\u0000\u0000\u0000\u00b4\u00b5\u0005-\u0000\u0000" +
                    "\u00b5\u00b6\u0005>\u0000\u0000\u00b6\u001e\u0001\u0000\u0000\u0000\u00b7" +
                    "\u00b8\u0005t\u0000\u0000\u00b8\u00b9\u0005r\u0000\u0000\u00b9\u00ba\u0005" +
                    "u\u0000\u0000\u00ba\u00c1\u0005e\u0000\u0000\u00bb\u00bc\u0005f\u0000" +
                    "\u0000\u00bc\u00bd\u0005a\u0000\u0000\u00bd\u00be\u0005l\u0000\u0000\u00be" +
                    "\u00bf\u0005s\u0000\u0000\u00bf\u00c1\u0005e\u0000\u0000\u00c0\u00b7\u0001" +
                    "\u0000\u0000\u0000\u00c0\u00bb\u0001\u0000\u0000\u0000\u00c1 \u0001\u0000" +
                    "\u0000\u0000\u00c2\u00c3\u0005m\u0000\u0000\u00c3\u00c4\u0005o\u0000\u0000" +
                    "\u00c4\u00c5\u0005d\u0000\u0000\u00c5\"\u0001\u0000\u0000\u0000\u00c6" +
                    "\u00c7\u0005a\u0000\u0000\u00c7\u00c8\u0005n\u0000\u0000\u00c8\u00cc\u0005" +
                    "d\u0000\u0000\u00c9\u00ca\u0005o\u0000\u0000\u00ca\u00cc\u0005r\u0000" +
                    "\u0000\u00cb\u00c6\u0001\u0000\u0000\u0000\u00cb\u00c9\u0001\u0000\u0000" +
                    "\u0000\u00cc$\u0001\u0000\u0000\u0000\u00cd\u00d7\u0003_/\u0000\u00ce" +
                    "\u00d7\u0003a0\u0000\u00cf\u00d3\u0007\u0000\u0000\u0000\u00d0\u00d2\u0007" +
                    "\u0001\u0000\u0000\u00d1\u00d0\u0001\u0000\u0000\u0000\u00d2\u00d5\u0001" +
                    "\u0000\u0000\u0000\u00d3\u00d1\u0001\u0000\u0000\u0000\u00d3\u00d4\u0001" +
                    "\u0000\u0000\u0000\u00d4\u00d7\u0001\u0000\u0000\u0000\u00d5\u00d3\u0001" +
                    "\u0000\u0000\u0000\u00d6\u00cd\u0001\u0000\u0000\u0000\u00d6\u00ce\u0001" +
                    "\u0000\u0000\u0000\u00d6\u00cf\u0001\u0000\u0000\u0000\u00d7&\u0001\u0000" +
                    "\u0000\u0000\u00d8\u00da\u0007\u0002\u0000\u0000\u00d9\u00d8\u0001\u0000" +
                    "\u0000\u0000\u00da\u00db\u0001\u0000\u0000\u0000\u00db\u00d9\u0001\u0000" +
                    "\u0000\u0000\u00db\u00dc\u0001\u0000\u0000\u0000\u00dc\u00dd\u0001\u0000" +
                    "\u0000\u0000\u00dd\u00de\u0005:\u0000\u0000\u00de\u00df\u0005/\u0000\u0000" +
                    "\u00df\u00e0\u0005/\u0000\u0000\u00e0\u00e2\u0001\u0000\u0000\u0000\u00e1" +
                    "\u00e3\u0007\u0003\u0000\u0000\u00e2\u00e1\u0001\u0000\u0000\u0000\u00e3" +
                    "\u00e4\u0001\u0000\u0000\u0000\u00e4\u00e2\u0001\u0000\u0000\u0000\u00e4" +
                    "\u00e5\u0001\u0000\u0000\u0000\u00e5(\u0001\u0000\u0000\u0000\u00e6\u00e8" +
                    "\u0007\u0004\u0000\u0000\u00e7\u00e6\u0001\u0000\u0000\u0000\u00e8\u00e9" +
                    "\u0001\u0000\u0000\u0000\u00e9\u00e7\u0001\u0000\u0000\u0000\u00e9\u00ea" +
                    "\u0001\u0000\u0000\u0000\u00ea\u00eb\u0001\u0000\u0000\u0000\u00eb\u00ed" +
                    "\u0005/\u0000\u0000\u00ec\u00ee\u0007\u0005\u0000\u0000\u00ed\u00ec\u0001" +
                    "\u0000\u0000\u0000\u00ee\u00ef\u0001\u0000\u0000\u0000\u00ef\u00ed\u0001" +
                    "\u0000\u0000\u0000\u00ef\u00f0\u0001\u0000\u0000\u0000\u00f0*\u0001\u0000" +
                    "\u0000\u0000\u00f1\u00f3\u0007\u0006\u0000\u0000\u00f2\u00f1\u0001\u0000" +
                    "\u0000\u0000\u00f3\u00f4\u0001\u0000\u0000\u0000\u00f4\u00f2\u0001\u0000" +
                    "\u0000\u0000\u00f4\u00f5\u0001\u0000\u0000\u0000\u00f5\u00fc\u0001\u0000" +
                    "\u0000\u0000\u00f6\u00f8\u0005.\u0000\u0000\u00f7\u00f9\u0007\u0006\u0000" +
                    "\u0000\u00f8\u00f7\u0001\u0000\u0000\u0000\u00f9\u00fa\u0001\u0000\u0000" +
                    "\u0000\u00fa\u00f8\u0001\u0000\u0000\u0000\u00fa\u00fb\u0001\u0000\u0000" +
                    "\u0000\u00fb\u00fd\u0001\u0000\u0000\u0000\u00fc\u00f6\u0001\u0000\u0000" +
                    "\u0000\u00fc\u00fd\u0001\u0000\u0000\u0000\u00fd,\u0001\u0000\u0000\u0000" +
                    "\u00fe\u0102\u0005\"\u0000\u0000\u00ff\u0101\t\u0000\u0000\u0000\u0100" +
                    "\u00ff\u0001\u0000\u0000\u0000\u0101\u0104\u0001\u0000\u0000\u0000\u0102" +
                    "\u0103\u0001\u0000\u0000\u0000\u0102\u0100\u0001\u0000\u0000\u0000\u0103" +
                    "\u0105\u0001\u0000\u0000\u0000\u0104\u0102\u0001\u0000\u0000\u0000\u0105" +
                    "\u010f\u0005\"\u0000\u0000\u0106\u010a\u0005\'\u0000\u0000\u0107\u0109" +
                    "\t\u0000\u0000\u0000\u0108\u0107\u0001\u0000\u0000\u0000\u0109\u010c\u0001" +
                    "\u0000\u0000\u0000\u010a\u010b\u0001\u0000\u0000\u0000\u010a\u0108\u0001" +
                    "\u0000\u0000\u0000\u010b\u010d\u0001\u0000\u0000\u0000\u010c\u010a\u0001" +
                    "\u0000\u0000\u0000\u010d\u010f\u0005\'\u0000\u0000\u010e\u00fe\u0001\u0000" +
                    "\u0000\u0000\u010e\u0106\u0001\u0000\u0000\u0000\u010f.\u0001\u0000\u0000" +
                    "\u0000\u0110\u0114\u0005|\u0000\u0000\u0111\u0113\t\u0000\u0000\u0000" +
                    "\u0112\u0111\u0001\u0000\u0000\u0000\u0113\u0116\u0001\u0000\u0000\u0000" +
                    "\u0114\u0115\u0001\u0000\u0000\u0000\u0114\u0112\u0001\u0000\u0000\u0000" +
                    "\u0115\u0117\u0001\u0000\u0000\u0000\u0116\u0114\u0001\u0000\u0000\u0000" +
                    "\u0117\u0118\u0005|\u0000\u0000\u01180\u0001\u0000\u0000\u0000\u0119\u011d" +
                    "\u0005/\u0000\u0000\u011a\u011c\t\u0000\u0000\u0000\u011b\u011a\u0001" +
                    "\u0000\u0000\u0000\u011c\u011f\u0001\u0000\u0000\u0000\u011d\u011e\u0001" +
                    "\u0000\u0000\u0000\u011d\u011b\u0001\u0000\u0000\u0000\u011e\u0120\u0001" +
                    "\u0000\u0000\u0000\u011f\u011d\u0001\u0000\u0000\u0000\u0120\u0121\u0005" +
                    "/\u0000\u0000\u01212\u0001\u0000\u0000\u0000\u0122\u0123\u0005.\u0000" +
                    "\u0000\u01234\u0001\u0000\u0000\u0000\u0124\u0125\u0005:\u0000\u0000\u0125" +
                    "6\u0001\u0000\u0000\u0000\u0126\u0127\u0005,\u0000\u0000\u01278\u0001" +
                    "\u0000\u0000\u0000\u0128\u0129\u0005{\u0000\u0000\u0129:\u0001\u0000\u0000" +
                    "\u0000\u012a\u012b\u0005}\u0000\u0000\u012b<\u0001\u0000\u0000\u0000\u012c" +
                    "\u012d\u0005[\u0000\u0000\u012d>\u0001\u0000\u0000\u0000\u012e\u012f\u0005" +
                    "]\u0000\u0000\u012f@\u0001\u0000\u0000\u0000\u0130\u0131\u0005-\u0000" +
                    "\u0000\u0131\u0132\u0005-\u0000\u0000\u0132\u0133\u0005-\u0000\u0000\u0133" +
                    "B\u0001\u0000\u0000\u0000\u0134\u0136\u0007\u0007\u0000\u0000\u0135\u0134" +
                    "\u0001\u0000\u0000\u0000\u0136\u0137\u0001\u0000\u0000\u0000\u0137\u0135" +
                    "\u0001\u0000\u0000\u0000\u0137\u0138\u0001\u0000\u0000\u0000\u0138\u0139" +
                    "\u0001\u0000\u0000\u0000\u0139\u013a\u0006!\u0000\u0000\u013aD\u0001\u0000" +
                    "\u0000\u0000\u013b\u013d\u0007\b\u0000\u0000\u013c\u013b\u0001\u0000\u0000" +
                    "\u0000\u013d\u013e\u0001\u0000\u0000\u0000\u013e\u013c\u0001\u0000\u0000" +
                    "\u0000\u013e\u013f\u0001\u0000\u0000\u0000\u013f\u0140\u0001\u0000\u0000" +
                    "\u0000\u0140\u0141\u0006\"\u0000\u0000\u0141F\u0001\u0000\u0000\u0000" +
                    "\u0142\u0143\u0005/\u0000\u0000\u0143\u0144\u0005/\u0000\u0000\u0144\u0148" +
                    "\u0001\u0000\u0000\u0000\u0145\u0147\b\b\u0000\u0000\u0146\u0145\u0001" +
                    "\u0000\u0000\u0000\u0147\u014a\u0001\u0000\u0000\u0000\u0148\u0146\u0001" +
                    "\u0000\u0000\u0000\u0148\u0149\u0001\u0000\u0000\u0000\u0149\u014b\u0001" +
                    "\u0000\u0000\u0000\u014a\u0148\u0001\u0000\u0000\u0000\u014b\u014c\u0006" +
                    "#\u0000\u0000\u014cH\u0001\u0000\u0000\u0000\u014d\u0152\u0007\t\u0000" +
                    "\u0000\u014e\u0152\u0003U*\u0000\u014f\u0152\u0005/\u0000\u0000\u0150" +
                    "\u0152\u0003!\u0010\u0000\u0151\u014d\u0001\u0000\u0000\u0000\u0151\u014e" +
                    "\u0001\u0000\u0000\u0000\u0151\u014f\u0001\u0000\u0000\u0000\u0151\u0150" +
                    "\u0001\u0000\u0000\u0000\u0152J\u0001\u0000\u0000\u0000\u0153\u0154\u0005" +
                    "=\u0000\u0000\u0154\u015d\u0005=\u0000\u0000\u0155\u0156\u0005!\u0000" +
                    "\u0000\u0156\u015d\u0005=\u0000\u0000\u0157\u015d\u0007\n\u0000\u0000" +
                    "\u0158\u0159\u0005>\u0000\u0000\u0159\u015d\u0005=\u0000\u0000\u015a\u015b" +
                    "\u0005<\u0000\u0000\u015b\u015d\u0005=\u0000\u0000\u015c\u0153\u0001\u0000" +
                    "\u0000\u0000\u015c\u0155\u0001\u0000\u0000\u0000\u015c\u0157\u0001\u0000" +
                    "\u0000\u0000\u015c\u0158\u0001\u0000\u0000\u0000\u015c\u015a\u0001\u0000" +
                    "\u0000\u0000\u015dL\u0001\u0000\u0000\u0000\u015e\u015f\u0007\u000b\u0000" +
                    "\u0000\u015fN\u0001\u0000\u0000\u0000\u0160\u0161\u0007\f\u0000\u0000" +
                    "\u0161P\u0001\u0000\u0000\u0000\u0162\u0163\u0003W+\u0000\u0163R\u0001" +
                    "\u0000\u0000\u0000\u0164\u0165\u0005+\u0000\u0000\u0165\u0166\u0005+\u0000" +
                    "\u0000\u0166T\u0001\u0000\u0000\u0000\u0167\u0168\u0005*\u0000\u0000\u0168" +
                    "V\u0001\u0000\u0000\u0000\u0169\u016a\u0005.\u0000\u0000\u016a\u016b\u0005" +
                    ".\u0000\u0000\u016bX\u0001\u0000\u0000\u0000\u016c\u016d\u0005@\u0000" +
                    "\u0000\u016dZ\u0001\u0000\u0000\u0000\u016e\u016f\u0005?\u0000\u0000\u016f" +
                    "\\\u0001\u0000\u0000\u0000\u0170\u0171\u0005s\u0000\u0000\u0171\u0172" +
                    "\u0005i\u0000\u0000\u0172\u0173\u0005z\u0000\u0000\u0173\u0174\u0005e" +
                    "\u0000\u0000\u0174\u0175\u0005O\u0000\u0000\u0175\u0180\u0005f\u0000\u0000" +
                    "\u0176\u0177\u0005m\u0000\u0000\u0177\u0178\u0005a\u0000\u0000\u0178\u0180" +
                    "\u0005p\u0000\u0000\u0179\u017a\u0005f\u0000\u0000\u017a\u017b\u0005i" +
                    "\u0000\u0000\u017b\u017c\u0005l\u0000\u0000\u017c\u017d\u0005t\u0000\u0000" +
                    "\u017d\u017e\u0005e\u0000\u0000\u017e\u0180\u0005r\u0000\u0000\u017f\u0170" +
                    "\u0001\u0000\u0000\u0000\u017f\u0176\u0001\u0000\u0000\u0000\u017f\u0179" +
                    "\u0001\u0000\u0000\u0000\u0180^\u0001\u0000\u0000\u0000\u0181\u0182\u0005" +
                    "$\u0000\u0000\u0182\u0183\u0005$\u0000\u0000\u0183`\u0001\u0000\u0000" +
                    "\u0000\u0184\u0185\u0005$\u0000\u0000\u0185b\u0001\u0000\u0000\u0000\u0017" +
                    "\u0000\u00c0\u00cb\u00d3\u00d6\u00db\u00e4\u00e9\u00ef\u00f4\u00fa\u00fc" +
                    "\u0102\u010a\u010e\u0114\u011d\u0137\u013e\u0148\u0151\u015c\u017f\u0001" +
                    "\u0006\u0000\u0000";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}