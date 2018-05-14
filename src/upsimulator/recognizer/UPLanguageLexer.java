package upsimulator.recognizer;

// Generated from UPLanguage.g4 by ANTLR 4.7.1
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class UPLanguageLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, T__23=24, 
		T__24=25, T__25=26, T__26=27, T__27=28, T__28=29, T__29=30, T__30=31, 
		T__31=32, T__32=33, T__33=34, T__34=35, T__35=36, T__36=37, T__37=38, 
		T__38=39, T__39=40, T__40=41, T__41=42, T__42=43, T__43=44, T__44=45, 
		T__45=46, T__46=47, Integer=48, Letters=49, Name=50, COMMENT=51, WS=52;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
		"T__9", "T__10", "T__11", "T__12", "T__13", "T__14", "T__15", "T__16", 
		"T__17", "T__18", "T__19", "T__20", "T__21", "T__22", "T__23", "T__24", 
		"T__25", "T__26", "T__27", "T__28", "T__29", "T__30", "T__31", "T__32", 
		"T__33", "T__34", "T__35", "T__36", "T__37", "T__38", "T__39", "T__40", 
		"T__41", "T__42", "T__43", "T__44", "T__45", "T__46", "Integer", "Letters", 
		"Name", "COMMENT", "WS"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'Environment'", "'{'", "'}'", "'RuleSet'", "'Membrane'", "'extends'", 
		"','", "'Property'", "';'", "'='", "'+'", "'-'", "'Tunnel'", "'['", "']'", 
		"'Object'", "'^'", "'Rule'", "'/'", "'->'", "'|'", "'&'", "'divide'", 
		"'('", "')'", "':'", "'dissolve'", "'<'", "'>'", "'in'", "'out'", "'here'", 
		"'go'", "'all'", "'random'", "'%'", "'*'", "'&&'", "'||'", "'>='", "'=='", 
		"'!='", "'<='", "'\u2260'", "'@'", "'!'", "'?'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		"Integer", "Letters", "Name", "COMMENT", "WS"
	};
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


	public UPLanguageLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "UPLanguage.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\66\u014d\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t"+
		"+\4,\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64"+
		"\t\64\4\65\t\65\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3"+
		"\3\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6"+
		"\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3"+
		"\t\3\t\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3\16\3\16\3\16\3\16\3"+
		"\16\3\16\3\16\3\17\3\17\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3"+
		"\22\3\22\3\23\3\23\3\23\3\23\3\23\3\24\3\24\3\25\3\25\3\25\3\26\3\26\3"+
		"\27\3\27\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\31\3\31\3\32\3\32\3\33\3"+
		"\33\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\35\3\35\3\36\3\36\3"+
		"\37\3\37\3\37\3 \3 \3 \3 \3!\3!\3!\3!\3!\3\"\3\"\3\"\3#\3#\3#\3#\3$\3"+
		"$\3$\3$\3$\3$\3$\3%\3%\3&\3&\3\'\3\'\3\'\3(\3(\3(\3)\3)\3)\3*\3*\3*\3"+
		"+\3+\3+\3,\3,\3,\3-\3-\3.\3.\3/\3/\3\60\3\60\3\61\6\61\u011d\n\61\r\61"+
		"\16\61\u011e\3\62\6\62\u0122\n\62\r\62\16\62\u0123\3\63\6\63\u0127\n\63"+
		"\r\63\16\63\u0128\3\64\3\64\3\64\3\64\7\64\u012f\n\64\f\64\16\64\u0132"+
		"\13\64\3\64\5\64\u0135\n\64\3\64\3\64\3\64\3\64\3\64\7\64\u013c\n\64\f"+
		"\64\16\64\u013f\13\64\3\64\3\64\5\64\u0143\n\64\3\64\3\64\3\65\6\65\u0148"+
		"\n\65\r\65\16\65\u0149\3\65\3\65\3\u013d\2\66\3\3\5\4\7\5\t\6\13\7\r\b"+
		"\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26"+
		"+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$G%I&K\'M(O)Q*S"+
		"+U,W-Y.[/]\60_\61a\62c\63e\64g\65i\66\3\2\7\3\2\62;\4\2C\\c|\6\2\62;C"+
		"\\aac|\4\2\f\f\17\17\5\2\13\f\17\17\"\"\2\u0154\2\3\3\2\2\2\2\5\3\2\2"+
		"\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21"+
		"\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2"+
		"\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3"+
		"\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3"+
		"\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3"+
		"\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2"+
		"\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2"+
		"Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3"+
		"\2\2\2\2g\3\2\2\2\2i\3\2\2\2\3k\3\2\2\2\5w\3\2\2\2\7y\3\2\2\2\t{\3\2\2"+
		"\2\13\u0083\3\2\2\2\r\u008c\3\2\2\2\17\u0094\3\2\2\2\21\u0096\3\2\2\2"+
		"\23\u009f\3\2\2\2\25\u00a1\3\2\2\2\27\u00a3\3\2\2\2\31\u00a5\3\2\2\2\33"+
		"\u00a7\3\2\2\2\35\u00ae\3\2\2\2\37\u00b0\3\2\2\2!\u00b2\3\2\2\2#\u00b9"+
		"\3\2\2\2%\u00bb\3\2\2\2\'\u00c0\3\2\2\2)\u00c2\3\2\2\2+\u00c5\3\2\2\2"+
		"-\u00c7\3\2\2\2/\u00c9\3\2\2\2\61\u00d0\3\2\2\2\63\u00d2\3\2\2\2\65\u00d4"+
		"\3\2\2\2\67\u00d6\3\2\2\29\u00df\3\2\2\2;\u00e1\3\2\2\2=\u00e3\3\2\2\2"+
		"?\u00e6\3\2\2\2A\u00ea\3\2\2\2C\u00ef\3\2\2\2E\u00f2\3\2\2\2G\u00f6\3"+
		"\2\2\2I\u00fd\3\2\2\2K\u00ff\3\2\2\2M\u0101\3\2\2\2O\u0104\3\2\2\2Q\u0107"+
		"\3\2\2\2S\u010a\3\2\2\2U\u010d\3\2\2\2W\u0110\3\2\2\2Y\u0113\3\2\2\2["+
		"\u0115\3\2\2\2]\u0117\3\2\2\2_\u0119\3\2\2\2a\u011c\3\2\2\2c\u0121\3\2"+
		"\2\2e\u0126\3\2\2\2g\u0142\3\2\2\2i\u0147\3\2\2\2kl\7G\2\2lm\7p\2\2mn"+
		"\7x\2\2no\7k\2\2op\7t\2\2pq\7q\2\2qr\7p\2\2rs\7o\2\2st\7g\2\2tu\7p\2\2"+
		"uv\7v\2\2v\4\3\2\2\2wx\7}\2\2x\6\3\2\2\2yz\7\177\2\2z\b\3\2\2\2{|\7T\2"+
		"\2|}\7w\2\2}~\7n\2\2~\177\7g\2\2\177\u0080\7U\2\2\u0080\u0081\7g\2\2\u0081"+
		"\u0082\7v\2\2\u0082\n\3\2\2\2\u0083\u0084\7O\2\2\u0084\u0085\7g\2\2\u0085"+
		"\u0086\7o\2\2\u0086\u0087\7d\2\2\u0087\u0088\7t\2\2\u0088\u0089\7c\2\2"+
		"\u0089\u008a\7p\2\2\u008a\u008b\7g\2\2\u008b\f\3\2\2\2\u008c\u008d\7g"+
		"\2\2\u008d\u008e\7z\2\2\u008e\u008f\7v\2\2\u008f\u0090\7g\2\2\u0090\u0091"+
		"\7p\2\2\u0091\u0092\7f\2\2\u0092\u0093\7u\2\2\u0093\16\3\2\2\2\u0094\u0095"+
		"\7.\2\2\u0095\20\3\2\2\2\u0096\u0097\7R\2\2\u0097\u0098\7t\2\2\u0098\u0099"+
		"\7q\2\2\u0099\u009a\7r\2\2\u009a\u009b\7g\2\2\u009b\u009c\7t\2\2\u009c"+
		"\u009d\7v\2\2\u009d\u009e\7{\2\2\u009e\22\3\2\2\2\u009f\u00a0\7=\2\2\u00a0"+
		"\24\3\2\2\2\u00a1\u00a2\7?\2\2\u00a2\26\3\2\2\2\u00a3\u00a4\7-\2\2\u00a4"+
		"\30\3\2\2\2\u00a5\u00a6\7/\2\2\u00a6\32\3\2\2\2\u00a7\u00a8\7V\2\2\u00a8"+
		"\u00a9\7w\2\2\u00a9\u00aa\7p\2\2\u00aa\u00ab\7p\2\2\u00ab\u00ac\7g\2\2"+
		"\u00ac\u00ad\7n\2\2\u00ad\34\3\2\2\2\u00ae\u00af\7]\2\2\u00af\36\3\2\2"+
		"\2\u00b0\u00b1\7_\2\2\u00b1 \3\2\2\2\u00b2\u00b3\7Q\2\2\u00b3\u00b4\7"+
		"d\2\2\u00b4\u00b5\7l\2\2\u00b5\u00b6\7g\2\2\u00b6\u00b7\7e\2\2\u00b7\u00b8"+
		"\7v\2\2\u00b8\"\3\2\2\2\u00b9\u00ba\7`\2\2\u00ba$\3\2\2\2\u00bb\u00bc"+
		"\7T\2\2\u00bc\u00bd\7w\2\2\u00bd\u00be\7n\2\2\u00be\u00bf\7g\2\2\u00bf"+
		"&\3\2\2\2\u00c0\u00c1\7\61\2\2\u00c1(\3\2\2\2\u00c2\u00c3\7/\2\2\u00c3"+
		"\u00c4\7@\2\2\u00c4*\3\2\2\2\u00c5\u00c6\7~\2\2\u00c6,\3\2\2\2\u00c7\u00c8"+
		"\7(\2\2\u00c8.\3\2\2\2\u00c9\u00ca\7f\2\2\u00ca\u00cb\7k\2\2\u00cb\u00cc"+
		"\7x\2\2\u00cc\u00cd\7k\2\2\u00cd\u00ce\7f\2\2\u00ce\u00cf\7g\2\2\u00cf"+
		"\60\3\2\2\2\u00d0\u00d1\7*\2\2\u00d1\62\3\2\2\2\u00d2\u00d3\7+\2\2\u00d3"+
		"\64\3\2\2\2\u00d4\u00d5\7<\2\2\u00d5\66\3\2\2\2\u00d6\u00d7\7f\2\2\u00d7"+
		"\u00d8\7k\2\2\u00d8\u00d9\7u\2\2\u00d9\u00da\7u\2\2\u00da\u00db\7q\2\2"+
		"\u00db\u00dc\7n\2\2\u00dc\u00dd\7x\2\2\u00dd\u00de\7g\2\2\u00de8\3\2\2"+
		"\2\u00df\u00e0\7>\2\2\u00e0:\3\2\2\2\u00e1\u00e2\7@\2\2\u00e2<\3\2\2\2"+
		"\u00e3\u00e4\7k\2\2\u00e4\u00e5\7p\2\2\u00e5>\3\2\2\2\u00e6\u00e7\7q\2"+
		"\2\u00e7\u00e8\7w\2\2\u00e8\u00e9\7v\2\2\u00e9@\3\2\2\2\u00ea\u00eb\7"+
		"j\2\2\u00eb\u00ec\7g\2\2\u00ec\u00ed\7t\2\2\u00ed\u00ee\7g\2\2\u00eeB"+
		"\3\2\2\2\u00ef\u00f0\7i\2\2\u00f0\u00f1\7q\2\2\u00f1D\3\2\2\2\u00f2\u00f3"+
		"\7c\2\2\u00f3\u00f4\7n\2\2\u00f4\u00f5\7n\2\2\u00f5F\3\2\2\2\u00f6\u00f7"+
		"\7t\2\2\u00f7\u00f8\7c\2\2\u00f8\u00f9\7p\2\2\u00f9\u00fa\7f\2\2\u00fa"+
		"\u00fb\7q\2\2\u00fb\u00fc\7o\2\2\u00fcH\3\2\2\2\u00fd\u00fe\7\'\2\2\u00fe"+
		"J\3\2\2\2\u00ff\u0100\7,\2\2\u0100L\3\2\2\2\u0101\u0102\7(\2\2\u0102\u0103"+
		"\7(\2\2\u0103N\3\2\2\2\u0104\u0105\7~\2\2\u0105\u0106\7~\2\2\u0106P\3"+
		"\2\2\2\u0107\u0108\7@\2\2\u0108\u0109\7?\2\2\u0109R\3\2\2\2\u010a\u010b"+
		"\7?\2\2\u010b\u010c\7?\2\2\u010cT\3\2\2\2\u010d\u010e\7#\2\2\u010e\u010f"+
		"\7?\2\2\u010fV\3\2\2\2\u0110\u0111\7>\2\2\u0111\u0112\7?\2\2\u0112X\3"+
		"\2\2\2\u0113\u0114\7\u2262\2\2\u0114Z\3\2\2\2\u0115\u0116\7B\2\2\u0116"+
		"\\\3\2\2\2\u0117\u0118\7#\2\2\u0118^\3\2\2\2\u0119\u011a\7A\2\2\u011a"+
		"`\3\2\2\2\u011b\u011d\t\2\2\2\u011c\u011b\3\2\2\2\u011d\u011e\3\2\2\2"+
		"\u011e\u011c\3\2\2\2\u011e\u011f\3\2\2\2\u011fb\3\2\2\2\u0120\u0122\t"+
		"\3\2\2\u0121\u0120\3\2\2\2\u0122\u0123\3\2\2\2\u0123\u0121\3\2\2\2\u0123"+
		"\u0124\3\2\2\2\u0124d\3\2\2\2\u0125\u0127\t\4\2\2\u0126\u0125\3\2\2\2"+
		"\u0127\u0128\3\2\2\2\u0128\u0126\3\2\2\2\u0128\u0129\3\2\2\2\u0129f\3"+
		"\2\2\2\u012a\u012b\7\61\2\2\u012b\u012c\7\61\2\2\u012c\u0130\3\2\2\2\u012d"+
		"\u012f\n\5\2\2\u012e\u012d\3\2\2\2\u012f\u0132\3\2\2\2\u0130\u012e\3\2"+
		"\2\2\u0130\u0131\3\2\2\2\u0131\u0134\3\2\2\2\u0132\u0130\3\2\2\2\u0133"+
		"\u0135\7\17\2\2\u0134\u0133\3\2\2\2\u0134\u0135\3\2\2\2\u0135\u0136\3"+
		"\2\2\2\u0136\u0143\7\f\2\2\u0137\u0138\7\61\2\2\u0138\u0139\7,\2\2\u0139"+
		"\u013d\3\2\2\2\u013a\u013c\13\2\2\2\u013b\u013a\3\2\2\2\u013c\u013f\3"+
		"\2\2\2\u013d\u013e\3\2\2\2\u013d\u013b\3\2\2\2\u013e\u0140\3\2\2\2\u013f"+
		"\u013d\3\2\2\2\u0140\u0141\7,\2\2\u0141\u0143\7\61\2\2\u0142\u012a\3\2"+
		"\2\2\u0142\u0137\3\2\2\2\u0143\u0144\3\2\2\2\u0144\u0145\b\64\2\2\u0145"+
		"h\3\2\2\2\u0146\u0148\t\6\2\2\u0147\u0146\3\2\2\2\u0148\u0149\3\2\2\2"+
		"\u0149\u0147\3\2\2\2\u0149\u014a\3\2\2\2\u014a\u014b\3\2\2\2\u014b\u014c"+
		"\b\65\2\2\u014cj\3\2\2\2\f\2\u011e\u0123\u0126\u0128\u0130\u0134\u013d"+
		"\u0142\u0149\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}