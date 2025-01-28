// Generated from src/main/java/dataweave/parser/DataWeave.g4 by ANTLR 4.13.2
package dataweave.parser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.RuntimeMetaData;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.VocabularyImpl;
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
            T__0 = 1, T__1 = 2, T__2 = 3, VAR = 4, FUNCTION = 5, INPUT = 6, NAMESPACE = 7, OUTPUT = 8,
            DW = 9, ASSIGN = 10, ARROW = 11, BOOLEAN = 12, IDENTIFIER = 13, NUMBER = 14, STRING = 15,
            DATE = 16, OPERATOR = 17, DOT = 18, COLON = 19, COMMA = 20, LCURLY = 21, RCURLY = 22,
            LSQUARE = 23, RSQUARE = 24, SEPARATOR = 25, NEWLINE = 26, WS = 27, COMMENT = 28, REGEX = 29;
    public static String[] channelNames = {
            "DEFAULT_TOKEN_CHANNEL", "HIDDEN"
    };

    public static String[] modeNames = {
            "DEFAULT_MODE"
    };

    private static String[] makeRuleNames() {
        return new String[]{
                "T__0", "T__1", "T__2", "VAR", "FUNCTION", "INPUT", "NAMESPACE", "OUTPUT",
                "DW", "ASSIGN", "ARROW", "BOOLEAN", "IDENTIFIER", "NUMBER", "STRING",
                "DATE", "OPERATOR", "DOT", "COLON", "COMMA", "LCURLY", "RCURLY", "LSQUARE",
                "RSQUARE", "SEPARATOR", "NEWLINE", "WS", "COMMENT", "REGEX"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'/'", "'('", "')'", "'%var'", "'%function'", "'%input'", "'%namespace'",
                "'%output'", "'%dw'", "'='", "'->'", null, null, null, null, null, null,
                "'.'", "':'", "','", "'{'", "'}'", "'['", "']'", "'---'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, null, null, null, "VAR", "FUNCTION", "INPUT", "NAMESPACE", "OUTPUT",
                "DW", "ASSIGN", "ARROW", "BOOLEAN", "IDENTIFIER", "NUMBER", "STRING",
                "DATE", "OPERATOR", "DOT", "COLON", "COMMA", "LCURLY", "RCURLY", "LSQUARE",
                "RSQUARE", "SEPARATOR", "NEWLINE", "WS", "COMMENT", "REGEX"
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
            "\u0004\u0000\u001d\u00e6\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002" +
                    "\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002" +
                    "\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002" +
                    "\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002" +
                    "\u000b\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e" +
                    "\u0002\u000f\u0007\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011" +
                    "\u0002\u0012\u0007\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014" +
                    "\u0002\u0015\u0007\u0015\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017" +
                    "\u0002\u0018\u0007\u0018\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a" +
                    "\u0002\u001b\u0007\u001b\u0002\u001c\u0007\u001c\u0001\u0000\u0001\u0000" +
                    "\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003" +
                    "\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004" +
                    "\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004" +
                    "\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005" +
                    "\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006" +
                    "\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006" +
                    "\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007" +
                    "\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001" +
                    "\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b" +
                    "\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b" +
                    "\u0003\u000b}\b\u000b\u0001\f\u0001\f\u0005\f\u0081\b\f\n\f\f\f\u0084" +
                    "\t\f\u0001\r\u0004\r\u0087\b\r\u000b\r\f\r\u0088\u0001\r\u0001\r\u0004" +
                    "\r\u008d\b\r\u000b\r\f\r\u008e\u0003\r\u0091\b\r\u0001\u000e\u0001\u000e" +
                    "\u0005\u000e\u0095\b\u000e\n\u000e\f\u000e\u0098\t\u000e\u0001\u000e\u0001" +
                    "\u000e\u0001\u000e\u0005\u000e\u009d\b\u000e\n\u000e\f\u000e\u00a0\t\u000e" +
                    "\u0001\u000e\u0003\u000e\u00a3\b\u000e\u0001\u000f\u0001\u000f\u0005\u000f" +
                    "\u00a7\b\u000f\n\u000f\f\u000f\u00aa\t\u000f\u0001\u000f\u0001\u000f\u0001" +
                    "\u0010\u0001\u0010\u0001\u0010\u0003\u0010\u00b1\b\u0010\u0001\u0011\u0001" +
                    "\u0011\u0001\u0012\u0001\u0012\u0001\u0013\u0001\u0013\u0001\u0014\u0001" +
                    "\u0014\u0001\u0015\u0001\u0015\u0001\u0016\u0001\u0016\u0001\u0017\u0001" +
                    "\u0017\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0019\u0004" +
                    "\u0019\u00c6\b\u0019\u000b\u0019\f\u0019\u00c7\u0001\u0019\u0001\u0019" +
                    "\u0001\u001a\u0004\u001a\u00cd\b\u001a\u000b\u001a\f\u001a\u00ce\u0001" +
                    "\u001a\u0001\u001a\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0005" +
                    "\u001b\u00d7\b\u001b\n\u001b\f\u001b\u00da\t\u001b\u0001\u001b\u0001\u001b" +
                    "\u0001\u001c\u0001\u001c\u0005\u001c\u00e0\b\u001c\n\u001c\f\u001c\u00e3" +
                    "\t\u001c\u0001\u001c\u0001\u001c\u0004\u0096\u009e\u00a8\u00e1\u0000\u001d" +
                    "\u0001\u0001\u0003\u0002\u0005\u0003\u0007\u0004\t\u0005\u000b\u0006\r" +
                    "\u0007\u000f\b\u0011\t\u0013\n\u0015\u000b\u0017\f\u0019\r\u001b\u000e" +
                    "\u001d\u000f\u001f\u0010!\u0011#\u0012%\u0013\'\u0014)\u0015+\u0016-\u0017" +
                    "/\u00181\u00193\u001a5\u001b7\u001c9\u001d\u0001\u0000\u0006\u0003\u0000" +
                    "AZ__az\u0004\u000009AZ__az\u0001\u000009\u0003\u0000*+--//\u0002\u0000" +
                    "\n\n\r\r\u0003\u0000\t\n\r\r  \u00f3\u0000\u0001\u0001\u0000\u0000\u0000" +
                    "\u0000\u0003\u0001\u0000\u0000\u0000\u0000\u0005\u0001\u0000\u0000\u0000" +
                    "\u0000\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001\u0000\u0000\u0000\u0000" +
                    "\u000b\u0001\u0000\u0000\u0000\u0000\r\u0001\u0000\u0000\u0000\u0000\u000f" +
                    "\u0001\u0000\u0000\u0000\u0000\u0011\u0001\u0000\u0000\u0000\u0000\u0013" +
                    "\u0001\u0000\u0000\u0000\u0000\u0015\u0001\u0000\u0000\u0000\u0000\u0017" +
                    "\u0001\u0000\u0000\u0000\u0000\u0019\u0001\u0000\u0000\u0000\u0000\u001b" +
                    "\u0001\u0000\u0000\u0000\u0000\u001d\u0001\u0000\u0000\u0000\u0000\u001f" +
                    "\u0001\u0000\u0000\u0000\u0000!\u0001\u0000\u0000\u0000\u0000#\u0001\u0000" +
                    "\u0000\u0000\u0000%\u0001\u0000\u0000\u0000\u0000\'\u0001\u0000\u0000" +
                    "\u0000\u0000)\u0001\u0000\u0000\u0000\u0000+\u0001\u0000\u0000\u0000\u0000" +
                    "-\u0001\u0000\u0000\u0000\u0000/\u0001\u0000\u0000\u0000\u00001\u0001" +
                    "\u0000\u0000\u0000\u00003\u0001\u0000\u0000\u0000\u00005\u0001\u0000\u0000" +
                    "\u0000\u00007\u0001\u0000\u0000\u0000\u00009\u0001\u0000\u0000\u0000\u0001" +
                    ";\u0001\u0000\u0000\u0000\u0003=\u0001\u0000\u0000\u0000\u0005?\u0001" +
                    "\u0000\u0000\u0000\u0007A\u0001\u0000\u0000\u0000\tF\u0001\u0000\u0000" +
                    "\u0000\u000bP\u0001\u0000\u0000\u0000\rW\u0001\u0000\u0000\u0000\u000f" +
                    "b\u0001\u0000\u0000\u0000\u0011j\u0001\u0000\u0000\u0000\u0013n\u0001" +
                    "\u0000\u0000\u0000\u0015p\u0001\u0000\u0000\u0000\u0017|\u0001\u0000\u0000" +
                    "\u0000\u0019~\u0001\u0000\u0000\u0000\u001b\u0086\u0001\u0000\u0000\u0000" +
                    "\u001d\u00a2\u0001\u0000\u0000\u0000\u001f\u00a4\u0001\u0000\u0000\u0000" +
                    "!\u00b0\u0001\u0000\u0000\u0000#\u00b2\u0001\u0000\u0000\u0000%\u00b4" +
                    "\u0001\u0000\u0000\u0000\'\u00b6\u0001\u0000\u0000\u0000)\u00b8\u0001" +
                    "\u0000\u0000\u0000+\u00ba\u0001\u0000\u0000\u0000-\u00bc\u0001\u0000\u0000" +
                    "\u0000/\u00be\u0001\u0000\u0000\u00001\u00c0\u0001\u0000\u0000\u00003" +
                    "\u00c5\u0001\u0000\u0000\u00005\u00cc\u0001\u0000\u0000\u00007\u00d2\u0001" +
                    "\u0000\u0000\u00009\u00dd\u0001\u0000\u0000\u0000;<\u0005/\u0000\u0000" +
                    "<\u0002\u0001\u0000\u0000\u0000=>\u0005(\u0000\u0000>\u0004\u0001\u0000" +
                    "\u0000\u0000?@\u0005)\u0000\u0000@\u0006\u0001\u0000\u0000\u0000AB\u0005" +
                    "%\u0000\u0000BC\u0005v\u0000\u0000CD\u0005a\u0000\u0000DE\u0005r\u0000" +
                    "\u0000E\b\u0001\u0000\u0000\u0000FG\u0005%\u0000\u0000GH\u0005f\u0000" +
                    "\u0000HI\u0005u\u0000\u0000IJ\u0005n\u0000\u0000JK\u0005c\u0000\u0000" +
                    "KL\u0005t\u0000\u0000LM\u0005i\u0000\u0000MN\u0005o\u0000\u0000NO\u0005" +
                    "n\u0000\u0000O\n\u0001\u0000\u0000\u0000PQ\u0005%\u0000\u0000QR\u0005" +
                    "i\u0000\u0000RS\u0005n\u0000\u0000ST\u0005p\u0000\u0000TU\u0005u\u0000" +
                    "\u0000UV\u0005t\u0000\u0000V\f\u0001\u0000\u0000\u0000WX\u0005%\u0000" +
                    "\u0000XY\u0005n\u0000\u0000YZ\u0005a\u0000\u0000Z[\u0005m\u0000\u0000" +
                    "[\\\u0005e\u0000\u0000\\]\u0005s\u0000\u0000]^\u0005p\u0000\u0000^_\u0005" +
                    "a\u0000\u0000_`\u0005c\u0000\u0000`a\u0005e\u0000\u0000a\u000e\u0001\u0000" +
                    "\u0000\u0000bc\u0005%\u0000\u0000cd\u0005o\u0000\u0000de\u0005u\u0000" +
                    "\u0000ef\u0005t\u0000\u0000fg\u0005p\u0000\u0000gh\u0005u\u0000\u0000" +
                    "hi\u0005t\u0000\u0000i\u0010\u0001\u0000\u0000\u0000jk\u0005%\u0000\u0000" +
                    "kl\u0005d\u0000\u0000lm\u0005w\u0000\u0000m\u0012\u0001\u0000\u0000\u0000" +
                    "no\u0005=\u0000\u0000o\u0014\u0001\u0000\u0000\u0000pq\u0005-\u0000\u0000" +
                    "qr\u0005>\u0000\u0000r\u0016\u0001\u0000\u0000\u0000st\u0005t\u0000\u0000" +
                    "tu\u0005r\u0000\u0000uv\u0005u\u0000\u0000v}\u0005e\u0000\u0000wx\u0005" +
                    "f\u0000\u0000xy\u0005a\u0000\u0000yz\u0005l\u0000\u0000z{\u0005s\u0000" +
                    "\u0000{}\u0005e\u0000\u0000|s\u0001\u0000\u0000\u0000|w\u0001\u0000\u0000" +
                    "\u0000}\u0018\u0001\u0000\u0000\u0000~\u0082\u0007\u0000\u0000\u0000\u007f" +
                    "\u0081\u0007\u0001\u0000\u0000\u0080\u007f\u0001\u0000\u0000\u0000\u0081" +
                    "\u0084\u0001\u0000\u0000\u0000\u0082\u0080\u0001\u0000\u0000\u0000\u0082" +
                    "\u0083\u0001\u0000\u0000\u0000\u0083\u001a\u0001\u0000\u0000\u0000\u0084" +
                    "\u0082\u0001\u0000\u0000\u0000\u0085\u0087\u0007\u0002\u0000\u0000\u0086" +
                    "\u0085\u0001\u0000\u0000\u0000\u0087\u0088\u0001\u0000\u0000\u0000\u0088" +
                    "\u0086\u0001\u0000\u0000\u0000\u0088\u0089\u0001\u0000\u0000\u0000\u0089" +
                    "\u0090\u0001\u0000\u0000\u0000\u008a\u008c\u0005.\u0000\u0000\u008b\u008d" +
                    "\u0007\u0002\u0000\u0000\u008c\u008b\u0001\u0000\u0000\u0000\u008d\u008e" +
                    "\u0001\u0000\u0000\u0000\u008e\u008c\u0001\u0000\u0000\u0000\u008e\u008f" +
                    "\u0001\u0000\u0000\u0000\u008f\u0091\u0001\u0000\u0000\u0000\u0090\u008a" +
                    "\u0001\u0000\u0000\u0000\u0090\u0091\u0001\u0000\u0000\u0000\u0091\u001c" +
                    "\u0001\u0000\u0000\u0000\u0092\u0096\u0005\"\u0000\u0000\u0093\u0095\t" +
                    "\u0000\u0000\u0000\u0094\u0093\u0001\u0000\u0000\u0000\u0095\u0098\u0001" +
                    "\u0000\u0000\u0000\u0096\u0097\u0001\u0000\u0000\u0000\u0096\u0094\u0001" +
                    "\u0000\u0000\u0000\u0097\u0099\u0001\u0000\u0000\u0000\u0098\u0096\u0001" +
                    "\u0000\u0000\u0000\u0099\u00a3\u0005\"\u0000\u0000\u009a\u009e\u0005\'" +
                    "\u0000\u0000\u009b\u009d\t\u0000\u0000\u0000\u009c\u009b\u0001\u0000\u0000" +
                    "\u0000\u009d\u00a0\u0001\u0000\u0000\u0000\u009e\u009f\u0001\u0000\u0000" +
                    "\u0000\u009e\u009c\u0001\u0000\u0000\u0000\u009f\u00a1\u0001\u0000\u0000" +
                    "\u0000\u00a0\u009e\u0001\u0000\u0000\u0000\u00a1\u00a3\u0005\'\u0000\u0000" +
                    "\u00a2\u0092\u0001\u0000\u0000\u0000\u00a2\u009a\u0001\u0000\u0000\u0000" +
                    "\u00a3\u001e\u0001\u0000\u0000\u0000\u00a4\u00a8\u0005|\u0000\u0000\u00a5" +
                    "\u00a7\t\u0000\u0000\u0000\u00a6\u00a5\u0001\u0000\u0000\u0000\u00a7\u00aa" +
                    "\u0001\u0000\u0000\u0000\u00a8\u00a9\u0001\u0000\u0000\u0000\u00a8\u00a6" +
                    "\u0001\u0000\u0000\u0000\u00a9\u00ab\u0001\u0000\u0000\u0000\u00aa\u00a8" +
                    "\u0001\u0000\u0000\u0000\u00ab\u00ac\u0005|\u0000\u0000\u00ac \u0001\u0000" +
                    "\u0000\u0000\u00ad\u00ae\u0005+\u0000\u0000\u00ae\u00b1\u0005+\u0000\u0000" +
                    "\u00af\u00b1\u0007\u0003\u0000\u0000\u00b0\u00ad\u0001\u0000\u0000\u0000" +
                    "\u00b0\u00af\u0001\u0000\u0000\u0000\u00b1\"\u0001\u0000\u0000\u0000\u00b2" +
                    "\u00b3\u0005.\u0000\u0000\u00b3$\u0001\u0000\u0000\u0000\u00b4\u00b5\u0005" +
                    ":\u0000\u0000\u00b5&\u0001\u0000\u0000\u0000\u00b6\u00b7\u0005,\u0000" +
                    "\u0000\u00b7(\u0001\u0000\u0000\u0000\u00b8\u00b9\u0005{\u0000\u0000\u00b9" +
                    "*\u0001\u0000\u0000\u0000\u00ba\u00bb\u0005}\u0000\u0000\u00bb,\u0001" +
                    "\u0000\u0000\u0000\u00bc\u00bd\u0005[\u0000\u0000\u00bd.\u0001\u0000\u0000" +
                    "\u0000\u00be\u00bf\u0005]\u0000\u0000\u00bf0\u0001\u0000\u0000\u0000\u00c0" +
                    "\u00c1\u0005-\u0000\u0000\u00c1\u00c2\u0005-\u0000\u0000\u00c2\u00c3\u0005" +
                    "-\u0000\u0000\u00c32\u0001\u0000\u0000\u0000\u00c4\u00c6\u0007\u0004\u0000" +
                    "\u0000\u00c5\u00c4\u0001\u0000\u0000\u0000\u00c6\u00c7\u0001\u0000\u0000" +
                    "\u0000\u00c7\u00c5\u0001\u0000\u0000\u0000\u00c7\u00c8\u0001\u0000\u0000" +
                    "\u0000\u00c8\u00c9\u0001\u0000\u0000\u0000\u00c9\u00ca\u0006\u0019\u0000" +
                    "\u0000\u00ca4\u0001\u0000\u0000\u0000\u00cb\u00cd\u0007\u0005\u0000\u0000" +
                    "\u00cc\u00cb\u0001\u0000\u0000\u0000\u00cd\u00ce\u0001\u0000\u0000\u0000" +
                    "\u00ce\u00cc\u0001\u0000\u0000\u0000\u00ce\u00cf\u0001\u0000\u0000\u0000" +
                    "\u00cf\u00d0\u0001\u0000\u0000\u0000\u00d0\u00d1\u0006\u001a\u0000\u0000" +
                    "\u00d16\u0001\u0000\u0000\u0000\u00d2\u00d3\u0005/\u0000\u0000\u00d3\u00d4" +
                    "\u0005/\u0000\u0000\u00d4\u00d8\u0001\u0000\u0000\u0000\u00d5\u00d7\b" +
                    "\u0004\u0000\u0000\u00d6\u00d5\u0001\u0000\u0000\u0000\u00d7\u00da\u0001" +
                    "\u0000\u0000\u0000\u00d8\u00d6\u0001\u0000\u0000\u0000\u00d8\u00d9\u0001" +
                    "\u0000\u0000\u0000\u00d9\u00db\u0001\u0000\u0000\u0000\u00da\u00d8\u0001" +
                    "\u0000\u0000\u0000\u00db\u00dc\u0006\u001b\u0000\u0000\u00dc8\u0001\u0000" +
                    "\u0000\u0000\u00dd\u00e1\u0005/\u0000\u0000\u00de\u00e0\t\u0000\u0000" +
                    "\u0000\u00df\u00de\u0001\u0000\u0000\u0000\u00e0\u00e3\u0001\u0000\u0000" +
                    "\u0000\u00e1\u00e2\u0001\u0000\u0000\u0000\u00e1\u00df\u0001\u0000\u0000" +
                    "\u0000\u00e2\u00e4\u0001\u0000\u0000\u0000\u00e3\u00e1\u0001\u0000\u0000" +
                    "\u0000\u00e4\u00e5\u0005/\u0000\u0000\u00e5:\u0001\u0000\u0000\u0000\u000f" +
                    "\u0000|\u0082\u0088\u008e\u0090\u0096\u009e\u00a2\u00a8\u00b0\u00c7\u00ce" +
                    "\u00d8\u00e1\u0001\u0006\u0000\u0000";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}
