// Generated from /home/munak98/Documents/Materias/TP2/Oberon-Scala/src/main/antlr4/Oberon.g4 by ANTLR 4.8
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class OberonParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

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
		T__45=46, T__46=47, T__47=48, T__48=49, T__49=50, T__50=51, T__51=52, 
		T__52=53, T__53=54, T__54=55, T__55=56, T__56=57, T__57=58, T__58=59, 
		T__59=60, T__60=61, INT=62, REAL=63, CHAR=64, TRUE=65, FALSE=66, STRING=67, 
		NIL=68, Id=69, WS=70, COMMENT=71, LINE_COMMENT=72;
	public static final int
		RULE_compilationUnit = 0, RULE_imports = 1, RULE_importList = 2, RULE_importModule = 3, 
		RULE_declarations = 4, RULE_userTypeDeclaration = 5, RULE_userType = 6, 
		RULE_constant = 7, RULE_varDeclaration = 8, RULE_procedure = 9, RULE_formals = 10, 
		RULE_arguments = 11, RULE_formalArg = 12, RULE_block = 13, RULE_expression = 14, 
		RULE_qualifiedName = 15, RULE_statement = 16, RULE_designator = 17, RULE_caseAlternative = 18, 
		RULE_elseIfStmt = 19, RULE_expValue = 20, RULE_intValue = 21, RULE_realValue = 22, 
		RULE_charValue = 23, RULE_stringValue = 24, RULE_boolValue = 25, RULE_nullValue = 26, 
		RULE_oberonType = 27, RULE_repl = 28;
	private static String[] makeRuleNames() {
		return new String[] {
			"compilationUnit", "imports", "importList", "importModule", "declarations", 
			"userTypeDeclaration", "userType", "constant", "varDeclaration", "procedure", 
			"formals", "arguments", "formalArg", "block", "expression", "qualifiedName", 
			"statement", "designator", "caseAlternative", "elseIfStmt", "expValue", 
			"intValue", "realValue", "charValue", "stringValue", "boolValue", "nullValue", 
			"oberonType", "repl"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'MODULE'", "';'", "'END'", "'.'", "'IMPORT'", "','", "':='", "'TYPE'", 
			"'CONST'", "'VAR'", "'='", "'ARRAY'", "'OF'", "'RECORD'", "'POINTER'", 
			"'TO'", "':'", "'PROCEDURE'", "'('", "')'", "'BEGIN'", "'['", "']'", 
			"'^'", "'#'", "'<'", "'<='", "'>'", "'>='", "'*'", "'/'", "'&&'", "'+'", 
			"'-'", "'||'", "'::'", "'readReal'", "'readInt'", "'readChar'", "'write'", 
			"'IF'", "'THEN'", "'ELSE'", "'ELSIF'", "'WHILE'", "'DO'", "'REPEAT'", 
			"'UNTIL'", "'FOR'", "'IN'", "'..'", "'LOOP'", "'RETURN'", "'CASE'", "'|'", 
			"'EXIT'", "'INTEGER'", "'REAL'", "'CHAR'", "'BOOLEAN'", "'STRING'", null, 
			null, null, "'True'", "'False'", null, "'NIL'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, "INT", "REAL", "CHAR", "TRUE", "FALSE", "STRING", "NIL", 
			"Id", "WS", "COMMENT", "LINE_COMMENT"
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
	public String getGrammarFileName() { return "Oberon.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public OberonParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class CompilationUnitContext extends ParserRuleContext {
		public Token name;
		public DeclarationsContext declarations() {
			return getRuleContext(DeclarationsContext.class,0);
		}
		public List<TerminalNode> Id() { return getTokens(OberonParser.Id); }
		public TerminalNode Id(int i) {
			return getToken(OberonParser.Id, i);
		}
		public ImportsContext imports() {
			return getRuleContext(ImportsContext.class,0);
		}
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public CompilationUnitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_compilationUnit; }
	}

	public final CompilationUnitContext compilationUnit() throws RecognitionException {
		CompilationUnitContext _localctx = new CompilationUnitContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_compilationUnit);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(58);
			match(T__0);
			setState(59);
			((CompilationUnitContext)_localctx).name = match(Id);
			setState(60);
			match(T__1);
			setState(62);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__4) {
				{
				setState(61);
				imports();
				}
			}

			setState(64);
			declarations();
			setState(66);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__20) {
				{
				setState(65);
				block();
				}
			}

			setState(68);
			match(T__2);
			setState(69);
			match(Id);
			setState(70);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ImportsContext extends ParserRuleContext {
		public ImportListContext importList() {
			return getRuleContext(ImportListContext.class,0);
		}
		public ImportsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_imports; }
	}

	public final ImportsContext imports() throws RecognitionException {
		ImportsContext _localctx = new ImportsContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_imports);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(72);
			match(T__4);
			setState(73);
			importList();
			setState(74);
			match(T__1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ImportListContext extends ParserRuleContext {
		public ImportModuleContext importModule;
		public List<ImportModuleContext> modules = new ArrayList<ImportModuleContext>();
		public List<ImportModuleContext> importModule() {
			return getRuleContexts(ImportModuleContext.class);
		}
		public ImportModuleContext importModule(int i) {
			return getRuleContext(ImportModuleContext.class,i);
		}
		public ImportListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_importList; }
	}

	public final ImportListContext importList() throws RecognitionException {
		ImportListContext _localctx = new ImportListContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_importList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(76);
			((ImportListContext)_localctx).importModule = importModule();
			((ImportListContext)_localctx).modules.add(((ImportListContext)_localctx).importModule);
			setState(81);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__5) {
				{
				{
				setState(77);
				match(T__5);
				setState(78);
				((ImportListContext)_localctx).importModule = importModule();
				((ImportListContext)_localctx).modules.add(((ImportListContext)_localctx).importModule);
				}
				}
				setState(83);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ImportModuleContext extends ParserRuleContext {
		public Token module;
		public Token alias;
		public List<TerminalNode> Id() { return getTokens(OberonParser.Id); }
		public TerminalNode Id(int i) {
			return getToken(OberonParser.Id, i);
		}
		public ImportModuleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_importModule; }
	}

	public final ImportModuleContext importModule() throws RecognitionException {
		ImportModuleContext _localctx = new ImportModuleContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_importModule);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(84);
			((ImportModuleContext)_localctx).module = match(Id);
			setState(87);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__6) {
				{
				setState(85);
				match(T__6);
				setState(86);
				((ImportModuleContext)_localctx).alias = match(Id);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DeclarationsContext extends ParserRuleContext {
		public List<ProcedureContext> procedure() {
			return getRuleContexts(ProcedureContext.class);
		}
		public ProcedureContext procedure(int i) {
			return getRuleContext(ProcedureContext.class,i);
		}
		public List<UserTypeDeclarationContext> userTypeDeclaration() {
			return getRuleContexts(UserTypeDeclarationContext.class);
		}
		public UserTypeDeclarationContext userTypeDeclaration(int i) {
			return getRuleContext(UserTypeDeclarationContext.class,i);
		}
		public List<ConstantContext> constant() {
			return getRuleContexts(ConstantContext.class);
		}
		public ConstantContext constant(int i) {
			return getRuleContext(ConstantContext.class,i);
		}
		public List<VarDeclarationContext> varDeclaration() {
			return getRuleContexts(VarDeclarationContext.class);
		}
		public VarDeclarationContext varDeclaration(int i) {
			return getRuleContext(VarDeclarationContext.class,i);
		}
		public DeclarationsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_declarations; }
	}

	public final DeclarationsContext declarations() throws RecognitionException {
		DeclarationsContext _localctx = new DeclarationsContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_declarations);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(95);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__7) {
				{
				setState(89);
				match(T__7);
				setState(91); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(90);
					userTypeDeclaration();
					}
					}
					setState(93); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==Id );
				}
			}

			setState(103);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__8) {
				{
				setState(97);
				match(T__8);
				setState(99); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(98);
					constant();
					}
					}
					setState(101); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==Id );
				}
			}

			setState(111);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__9) {
				{
				setState(105);
				match(T__9);
				setState(107); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(106);
					varDeclaration();
					}
					}
					setState(109); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==Id );
				}
			}

			setState(116);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__17) {
				{
				{
				setState(113);
				procedure();
				}
				}
				setState(118);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class UserTypeDeclarationContext extends ParserRuleContext {
		public Token nameType;
		public UserTypeContext baseType;
		public TerminalNode Id() { return getToken(OberonParser.Id, 0); }
		public UserTypeContext userType() {
			return getRuleContext(UserTypeContext.class,0);
		}
		public UserTypeDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_userTypeDeclaration; }
	}

	public final UserTypeDeclarationContext userTypeDeclaration() throws RecognitionException {
		UserTypeDeclarationContext _localctx = new UserTypeDeclarationContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_userTypeDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(119);
			((UserTypeDeclarationContext)_localctx).nameType = match(Id);
			setState(120);
			match(T__10);
			setState(121);
			((UserTypeDeclarationContext)_localctx).baseType = userType();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class UserTypeContext extends ParserRuleContext {
		public UserTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_userType; }
	 
		public UserTypeContext() { }
		public void copyFrom(UserTypeContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class RecordTypeDeclarationContext extends UserTypeContext {
		public VarDeclarationContext varDeclaration;
		public List<VarDeclarationContext> vars = new ArrayList<VarDeclarationContext>();
		public List<VarDeclarationContext> varDeclaration() {
			return getRuleContexts(VarDeclarationContext.class);
		}
		public VarDeclarationContext varDeclaration(int i) {
			return getRuleContext(VarDeclarationContext.class,i);
		}
		public RecordTypeDeclarationContext(UserTypeContext ctx) { copyFrom(ctx); }
	}
	public static class PointerTypeDeclarationContext extends UserTypeContext {
		public OberonTypeContext baseType;
		public OberonTypeContext oberonType() {
			return getRuleContext(OberonTypeContext.class,0);
		}
		public PointerTypeDeclarationContext(UserTypeContext ctx) { copyFrom(ctx); }
	}
	public static class ArrayTypeDeclarationContext extends UserTypeContext {
		public Token length;
		public OberonTypeContext baseType;
		public TerminalNode INT() { return getToken(OberonParser.INT, 0); }
		public OberonTypeContext oberonType() {
			return getRuleContext(OberonTypeContext.class,0);
		}
		public ArrayTypeDeclarationContext(UserTypeContext ctx) { copyFrom(ctx); }
	}

	public final UserTypeContext userType() throws RecognitionException {
		UserTypeContext _localctx = new UserTypeContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_userType);
		int _la;
		try {
			setState(138);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__11:
				_localctx = new ArrayTypeDeclarationContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				{
				setState(123);
				match(T__11);
				setState(124);
				((ArrayTypeDeclarationContext)_localctx).length = match(INT);
				setState(125);
				match(T__12);
				setState(126);
				((ArrayTypeDeclarationContext)_localctx).baseType = oberonType();
				}
				}
				break;
			case T__13:
				_localctx = new RecordTypeDeclarationContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(127);
				match(T__13);
				setState(129); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(128);
					((RecordTypeDeclarationContext)_localctx).varDeclaration = varDeclaration();
					((RecordTypeDeclarationContext)_localctx).vars.add(((RecordTypeDeclarationContext)_localctx).varDeclaration);
					}
					}
					setState(131); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==Id );
				setState(133);
				match(T__2);
				}
				}
				break;
			case T__14:
				_localctx = new PointerTypeDeclarationContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				{
				setState(135);
				match(T__14);
				setState(136);
				match(T__15);
				setState(137);
				((PointerTypeDeclarationContext)_localctx).baseType = oberonType();
				}
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ConstantContext extends ParserRuleContext {
		public Token constName;
		public ExpressionContext exp;
		public TerminalNode Id() { return getToken(OberonParser.Id, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ConstantContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constant; }
	}

	public final ConstantContext constant() throws RecognitionException {
		ConstantContext _localctx = new ConstantContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_constant);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(140);
			((ConstantContext)_localctx).constName = match(Id);
			setState(141);
			match(T__10);
			setState(142);
			((ConstantContext)_localctx).exp = expression(0);
			setState(143);
			match(T__1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VarDeclarationContext extends ParserRuleContext {
		public Token Id;
		public List<Token> vars = new ArrayList<Token>();
		public OberonTypeContext varType;
		public OberonTypeContext oberonType() {
			return getRuleContext(OberonTypeContext.class,0);
		}
		public List<TerminalNode> Id() { return getTokens(OberonParser.Id); }
		public TerminalNode Id(int i) {
			return getToken(OberonParser.Id, i);
		}
		public VarDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_varDeclaration; }
	}

	public final VarDeclarationContext varDeclaration() throws RecognitionException {
		VarDeclarationContext _localctx = new VarDeclarationContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_varDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(145);
			((VarDeclarationContext)_localctx).Id = match(Id);
			((VarDeclarationContext)_localctx).vars.add(((VarDeclarationContext)_localctx).Id);
			setState(150);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__5) {
				{
				{
				setState(146);
				match(T__5);
				setState(147);
				((VarDeclarationContext)_localctx).Id = match(Id);
				((VarDeclarationContext)_localctx).vars.add(((VarDeclarationContext)_localctx).Id);
				}
				}
				setState(152);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
			setState(153);
			match(T__16);
			setState(154);
			((VarDeclarationContext)_localctx).varType = oberonType();
			setState(155);
			match(T__1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ProcedureContext extends ParserRuleContext {
		public Token name;
		public OberonTypeContext procedureType;
		public DeclarationsContext declarations() {
			return getRuleContext(DeclarationsContext.class,0);
		}
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public List<TerminalNode> Id() { return getTokens(OberonParser.Id); }
		public TerminalNode Id(int i) {
			return getToken(OberonParser.Id, i);
		}
		public FormalsContext formals() {
			return getRuleContext(FormalsContext.class,0);
		}
		public OberonTypeContext oberonType() {
			return getRuleContext(OberonTypeContext.class,0);
		}
		public ProcedureContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_procedure; }
	}

	public final ProcedureContext procedure() throws RecognitionException {
		ProcedureContext _localctx = new ProcedureContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_procedure);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(157);
			match(T__17);
			setState(158);
			((ProcedureContext)_localctx).name = match(Id);
			setState(159);
			match(T__18);
			setState(161);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Id) {
				{
				setState(160);
				formals();
				}
			}

			setState(163);
			match(T__19);
			setState(166);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__16) {
				{
				setState(164);
				match(T__16);
				setState(165);
				((ProcedureContext)_localctx).procedureType = oberonType();
				}
			}

			setState(168);
			match(T__1);
			setState(169);
			declarations();
			setState(170);
			block();
			setState(171);
			match(Id);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FormalsContext extends ParserRuleContext {
		public List<FormalArgContext> formalArg() {
			return getRuleContexts(FormalArgContext.class);
		}
		public FormalArgContext formalArg(int i) {
			return getRuleContext(FormalArgContext.class,i);
		}
		public FormalsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formals; }
	}

	public final FormalsContext formals() throws RecognitionException {
		FormalsContext _localctx = new FormalsContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_formals);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(173);
			formalArg();
			setState(178);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__5) {
				{
				{
				setState(174);
				match(T__5);
				setState(175);
				formalArg();
				}
				}
				setState(180);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ArgumentsContext extends ParserRuleContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public ArgumentsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arguments; }
	}

	public final ArgumentsContext arguments() throws RecognitionException {
		ArgumentsContext _localctx = new ArgumentsContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_arguments);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(181);
			expression(0);
			setState(186);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__5) {
				{
				{
				setState(182);
				match(T__5);
				setState(183);
				expression(0);
				}
				}
				setState(188);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FormalArgContext extends ParserRuleContext {
		public Token Id;
		public List<Token> args = new ArrayList<Token>();
		public OberonTypeContext argType;
		public OberonTypeContext oberonType() {
			return getRuleContext(OberonTypeContext.class,0);
		}
		public List<TerminalNode> Id() { return getTokens(OberonParser.Id); }
		public TerminalNode Id(int i) {
			return getToken(OberonParser.Id, i);
		}
		public FormalArgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formalArg; }
	}

	public final FormalArgContext formalArg() throws RecognitionException {
		FormalArgContext _localctx = new FormalArgContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_formalArg);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(189);
			((FormalArgContext)_localctx).Id = match(Id);
			((FormalArgContext)_localctx).args.add(((FormalArgContext)_localctx).Id);
			setState(194);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__5) {
				{
				{
				setState(190);
				match(T__5);
				setState(191);
				((FormalArgContext)_localctx).Id = match(Id);
				((FormalArgContext)_localctx).args.add(((FormalArgContext)_localctx).Id);
				}
				}
				setState(196);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
			setState(197);
			match(T__16);
			setState(198);
			((FormalArgContext)_localctx).argType = oberonType();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BlockContext extends ParserRuleContext {
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public BlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_block; }
	}

	public final BlockContext block() throws RecognitionException {
		BlockContext _localctx = new BlockContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_block);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(200);
			match(T__20);
			setState(201);
			statement(0);
			setState(202);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExpressionContext extends ParserRuleContext {
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
	 
		public ExpressionContext() { }
		public void copyFrom(ExpressionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class PointerAccessContext extends ExpressionContext {
		public Token name;
		public TerminalNode Id() { return getToken(OberonParser.Id, 0); }
		public PointerAccessContext(ExpressionContext ctx) { copyFrom(ctx); }
	}
	public static class VariableContext extends ExpressionContext {
		public QualifiedNameContext name;
		public QualifiedNameContext qualifiedName() {
			return getRuleContext(QualifiedNameContext.class,0);
		}
		public VariableContext(ExpressionContext ctx) { copyFrom(ctx); }
	}
	public static class BracketsContext extends ExpressionContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public BracketsContext(ExpressionContext ctx) { copyFrom(ctx); }
	}
	public static class AddExpressionContext extends ExpressionContext {
		public ExpressionContext left;
		public Token opr;
		public ExpressionContext right;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public AddExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
	}
	public static class RelExpressionContext extends ExpressionContext {
		public ExpressionContext left;
		public Token opr;
		public ExpressionContext right;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public RelExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
	}
	public static class ArraySubscriptContext extends ExpressionContext {
		public ExpressionContext arrayBase;
		public ExpressionContext index;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public ArraySubscriptContext(ExpressionContext ctx) { copyFrom(ctx); }
	}
	public static class ValueContext extends ExpressionContext {
		public ExpValueContext expValue() {
			return getRuleContext(ExpValueContext.class,0);
		}
		public ValueContext(ExpressionContext ctx) { copyFrom(ctx); }
	}
	public static class FunctionCallContext extends ExpressionContext {
		public QualifiedNameContext name;
		public QualifiedNameContext qualifiedName() {
			return getRuleContext(QualifiedNameContext.class,0);
		}
		public ArgumentsContext arguments() {
			return getRuleContext(ArgumentsContext.class,0);
		}
		public FunctionCallContext(ExpressionContext ctx) { copyFrom(ctx); }
	}
	public static class FieldAccessContext extends ExpressionContext {
		public ExpressionContext exp;
		public Token name;
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode Id() { return getToken(OberonParser.Id, 0); }
		public FieldAccessContext(ExpressionContext ctx) { copyFrom(ctx); }
	}
	public static class MultExpressionContext extends ExpressionContext {
		public ExpressionContext left;
		public Token opr;
		public ExpressionContext right;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public MultExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
	}

	public final ExpressionContext expression() throws RecognitionException {
		return expression(0);
	}

	private ExpressionContext expression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExpressionContext _localctx = new ExpressionContext(_ctx, _parentState);
		ExpressionContext _prevctx = _localctx;
		int _startState = 28;
		enterRecursionRule(_localctx, 28, RULE_expression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(220);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,20,_ctx) ) {
			case 1:
				{
				_localctx = new BracketsContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(205);
				match(T__18);
				setState(206);
				expression(0);
				setState(207);
				match(T__19);
				}
				break;
			case 2:
				{
				_localctx = new ValueContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(209);
				expValue();
				}
				break;
			case 3:
				{
				_localctx = new VariableContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(210);
				((VariableContext)_localctx).name = qualifiedName();
				}
				break;
			case 4:
				{
				_localctx = new FunctionCallContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(211);
				((FunctionCallContext)_localctx).name = qualifiedName();
				setState(212);
				match(T__18);
				setState(214);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (((((_la - 19)) & ~0x3f) == 0 && ((1L << (_la - 19)) & ((1L << (T__18 - 19)) | (1L << (INT - 19)) | (1L << (REAL - 19)) | (1L << (CHAR - 19)) | (1L << (TRUE - 19)) | (1L << (FALSE - 19)) | (1L << (STRING - 19)) | (1L << (NIL - 19)) | (1L << (Id - 19)))) != 0)) {
					{
					setState(213);
					arguments();
					}
				}

				setState(216);
				match(T__19);
				}
				break;
			case 5:
				{
				_localctx = new PointerAccessContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(218);
				((PointerAccessContext)_localctx).name = match(Id);
				setState(219);
				match(T__23);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(241);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,22,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(239);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
					case 1:
						{
						_localctx = new RelExpressionContext(new ExpressionContext(_parentctx, _parentState));
						((RelExpressionContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(222);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(223);
						((RelExpressionContext)_localctx).opr = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__10) | (1L << T__24) | (1L << T__25) | (1L << T__26) | (1L << T__27) | (1L << T__28))) != 0)) ) {
							((RelExpressionContext)_localctx).opr = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(224);
						((RelExpressionContext)_localctx).right = expression(4);
						}
						break;
					case 2:
						{
						_localctx = new MultExpressionContext(new ExpressionContext(_parentctx, _parentState));
						((MultExpressionContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(225);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(226);
						((MultExpressionContext)_localctx).opr = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__29) | (1L << T__30) | (1L << T__31))) != 0)) ) {
							((MultExpressionContext)_localctx).opr = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(227);
						((MultExpressionContext)_localctx).right = expression(3);
						}
						break;
					case 3:
						{
						_localctx = new AddExpressionContext(new ExpressionContext(_parentctx, _parentState));
						((AddExpressionContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(228);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(229);
						((AddExpressionContext)_localctx).opr = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__32) | (1L << T__33) | (1L << T__34))) != 0)) ) {
							((AddExpressionContext)_localctx).opr = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(230);
						((AddExpressionContext)_localctx).right = expression(2);
						}
						break;
					case 4:
						{
						_localctx = new FieldAccessContext(new ExpressionContext(_parentctx, _parentState));
						((FieldAccessContext)_localctx).exp = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(231);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(232);
						match(T__3);
						setState(233);
						((FieldAccessContext)_localctx).name = match(Id);
						}
						break;
					case 5:
						{
						_localctx = new ArraySubscriptContext(new ExpressionContext(_parentctx, _parentState));
						((ArraySubscriptContext)_localctx).arrayBase = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(234);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(235);
						match(T__21);
						setState(236);
						((ArraySubscriptContext)_localctx).index = expression(0);
						setState(237);
						match(T__22);
						}
						break;
					}
					} 
				}
				setState(243);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,22,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class QualifiedNameContext extends ParserRuleContext {
		public Token module;
		public Token name;
		public List<TerminalNode> Id() { return getTokens(OberonParser.Id); }
		public TerminalNode Id(int i) {
			return getToken(OberonParser.Id, i);
		}
		public QualifiedNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_qualifiedName; }
	}

	public final QualifiedNameContext qualifiedName() throws RecognitionException {
		QualifiedNameContext _localctx = new QualifiedNameContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_qualifiedName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(246);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
			case 1:
				{
				setState(244);
				((QualifiedNameContext)_localctx).module = match(Id);
				setState(245);
				match(T__35);
				}
				break;
			}
			setState(248);
			((QualifiedNameContext)_localctx).name = match(Id);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StatementContext extends ParserRuleContext {
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
	 
		public StatementContext() { }
		public void copyFrom(StatementContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class LoopStmtContext extends StatementContext {
		public StatementContext stmt;
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public LoopStmtContext(StatementContext ctx) { copyFrom(ctx); }
	}
	public static class ReadIntStmtContext extends StatementContext {
		public Token var;
		public TerminalNode Id() { return getToken(OberonParser.Id, 0); }
		public ReadIntStmtContext(StatementContext ctx) { copyFrom(ctx); }
	}
	public static class IfElseStmtContext extends StatementContext {
		public ExpressionContext cond;
		public StatementContext thenStmt;
		public StatementContext elseStmt;
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public IfElseStmtContext(StatementContext ctx) { copyFrom(ctx); }
	}
	public static class SequenceStmtContext extends StatementContext {
		public List<StatementContext> stmt = new ArrayList<StatementContext>();
		public StatementContext statement;
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public SequenceStmtContext(StatementContext ctx) { copyFrom(ctx); }
	}
	public static class CaseStmtContext extends StatementContext {
		public ExpressionContext exp;
		public CaseAlternativeContext caseAlternative;
		public List<CaseAlternativeContext> cases = new ArrayList<CaseAlternativeContext>();
		public StatementContext elseStmt;
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public List<CaseAlternativeContext> caseAlternative() {
			return getRuleContexts(CaseAlternativeContext.class);
		}
		public CaseAlternativeContext caseAlternative(int i) {
			return getRuleContext(CaseAlternativeContext.class,i);
		}
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public CaseStmtContext(StatementContext ctx) { copyFrom(ctx); }
	}
	public static class EAssignmentStmtContext extends StatementContext {
		public DesignatorContext des;
		public ExpressionContext exp;
		public DesignatorContext designator() {
			return getRuleContext(DesignatorContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public EAssignmentStmtContext(StatementContext ctx) { copyFrom(ctx); }
	}
	public static class ForRangeStmtContext extends StatementContext {
		public Token var;
		public ExpressionContext min;
		public ExpressionContext max;
		public StatementContext stmt;
		public TerminalNode Id() { return getToken(OberonParser.Id, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public ForRangeStmtContext(StatementContext ctx) { copyFrom(ctx); }
	}
	public static class ReadCharStmtContext extends StatementContext {
		public Token var;
		public TerminalNode Id() { return getToken(OberonParser.Id, 0); }
		public ReadCharStmtContext(StatementContext ctx) { copyFrom(ctx); }
	}
	public static class ProcedureCallContext extends StatementContext {
		public Token name;
		public TerminalNode Id() { return getToken(OberonParser.Id, 0); }
		public ArgumentsContext arguments() {
			return getRuleContext(ArgumentsContext.class,0);
		}
		public ProcedureCallContext(StatementContext ctx) { copyFrom(ctx); }
	}
	public static class WhileStmtContext extends StatementContext {
		public ExpressionContext cond;
		public StatementContext stmt;
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public WhileStmtContext(StatementContext ctx) { copyFrom(ctx); }
	}
	public static class ExitStmtContext extends StatementContext {
		public ExitStmtContext(StatementContext ctx) { copyFrom(ctx); }
	}
	public static class RepeatUntilStmtContext extends StatementContext {
		public StatementContext stmt;
		public ExpressionContext cond;
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public RepeatUntilStmtContext(StatementContext ctx) { copyFrom(ctx); }
	}
	public static class ReadRealStmtContext extends StatementContext {
		public Token var;
		public TerminalNode Id() { return getToken(OberonParser.Id, 0); }
		public ReadRealStmtContext(StatementContext ctx) { copyFrom(ctx); }
	}
	public static class WriteStmtContext extends StatementContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public WriteStmtContext(StatementContext ctx) { copyFrom(ctx); }
	}
	public static class AssignmentStmtContext extends StatementContext {
		public Token var;
		public ExpressionContext exp;
		public TerminalNode Id() { return getToken(OberonParser.Id, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public AssignmentStmtContext(StatementContext ctx) { copyFrom(ctx); }
	}
	public static class ForStmtContext extends StatementContext {
		public StatementContext init;
		public ExpressionContext condition;
		public StatementContext stmt;
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ForStmtContext(StatementContext ctx) { copyFrom(ctx); }
	}
	public static class ReturnStmtContext extends StatementContext {
		public ExpressionContext exp;
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ReturnStmtContext(StatementContext ctx) { copyFrom(ctx); }
	}
	public static class IfElseIfStmtContext extends StatementContext {
		public ExpressionContext cond;
		public StatementContext thenStmt;
		public ElseIfStmtContext elseIfStmt;
		public List<ElseIfStmtContext> elsifs = new ArrayList<ElseIfStmtContext>();
		public StatementContext elseStmt;
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public List<ElseIfStmtContext> elseIfStmt() {
			return getRuleContexts(ElseIfStmtContext.class);
		}
		public ElseIfStmtContext elseIfStmt(int i) {
			return getRuleContext(ElseIfStmtContext.class,i);
		}
		public IfElseIfStmtContext(StatementContext ctx) { copyFrom(ctx); }
	}

	public final StatementContext statement() throws RecognitionException {
		return statement(0);
	}

	private StatementContext statement(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		StatementContext _localctx = new StatementContext(_ctx, _parentState);
		StatementContext _prevctx = _localctx;
		int _startState = 32;
		enterRecursionRule(_localctx, 32, RULE_statement, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(360);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,30,_ctx) ) {
			case 1:
				{
				_localctx = new AssignmentStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(251);
				((AssignmentStmtContext)_localctx).var = match(Id);
				setState(252);
				match(T__6);
				setState(253);
				((AssignmentStmtContext)_localctx).exp = expression(0);
				}
				break;
			case 2:
				{
				_localctx = new EAssignmentStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(254);
				((EAssignmentStmtContext)_localctx).des = designator();
				setState(255);
				match(T__6);
				setState(256);
				((EAssignmentStmtContext)_localctx).exp = expression(0);
				}
				break;
			case 3:
				{
				_localctx = new ReadRealStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(258);
				match(T__36);
				setState(259);
				match(T__18);
				setState(260);
				((ReadRealStmtContext)_localctx).var = match(Id);
				setState(261);
				match(T__19);
				}
				break;
			case 4:
				{
				_localctx = new ReadIntStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(262);
				match(T__37);
				setState(263);
				match(T__18);
				setState(264);
				((ReadIntStmtContext)_localctx).var = match(Id);
				setState(265);
				match(T__19);
				}
				break;
			case 5:
				{
				_localctx = new ReadCharStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(266);
				match(T__38);
				setState(267);
				match(T__18);
				setState(268);
				((ReadCharStmtContext)_localctx).var = match(Id);
				setState(269);
				match(T__19);
				}
				break;
			case 6:
				{
				_localctx = new WriteStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(270);
				match(T__39);
				setState(271);
				match(T__18);
				setState(272);
				expression(0);
				setState(273);
				match(T__19);
				}
				break;
			case 7:
				{
				_localctx = new ProcedureCallContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(275);
				((ProcedureCallContext)_localctx).name = match(Id);
				setState(276);
				match(T__18);
				setState(278);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (((((_la - 19)) & ~0x3f) == 0 && ((1L << (_la - 19)) & ((1L << (T__18 - 19)) | (1L << (INT - 19)) | (1L << (REAL - 19)) | (1L << (CHAR - 19)) | (1L << (TRUE - 19)) | (1L << (FALSE - 19)) | (1L << (STRING - 19)) | (1L << (NIL - 19)) | (1L << (Id - 19)))) != 0)) {
					{
					setState(277);
					arguments();
					}
				}

				setState(280);
				match(T__19);
				}
				break;
			case 8:
				{
				_localctx = new IfElseStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(281);
				match(T__40);
				setState(282);
				((IfElseStmtContext)_localctx).cond = expression(0);
				setState(283);
				match(T__41);
				setState(284);
				((IfElseStmtContext)_localctx).thenStmt = statement(0);
				setState(287);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__42) {
					{
					setState(285);
					match(T__42);
					setState(286);
					((IfElseStmtContext)_localctx).elseStmt = statement(0);
					}
				}

				setState(289);
				match(T__2);
				}
				break;
			case 9:
				{
				_localctx = new IfElseIfStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(291);
				match(T__40);
				setState(292);
				((IfElseIfStmtContext)_localctx).cond = expression(0);
				setState(293);
				match(T__41);
				setState(294);
				((IfElseIfStmtContext)_localctx).thenStmt = statement(0);
				setState(297); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(295);
					match(T__43);
					setState(296);
					((IfElseIfStmtContext)_localctx).elseIfStmt = elseIfStmt();
					((IfElseIfStmtContext)_localctx).elsifs.add(((IfElseIfStmtContext)_localctx).elseIfStmt);
					}
					}
					setState(299); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__43 );
				setState(303);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__42) {
					{
					setState(301);
					match(T__42);
					setState(302);
					((IfElseIfStmtContext)_localctx).elseStmt = statement(0);
					}
				}

				setState(305);
				match(T__2);
				}
				break;
			case 10:
				{
				_localctx = new WhileStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(307);
				match(T__44);
				setState(308);
				((WhileStmtContext)_localctx).cond = expression(0);
				setState(309);
				match(T__45);
				setState(310);
				((WhileStmtContext)_localctx).stmt = statement(0);
				setState(311);
				match(T__2);
				}
				break;
			case 11:
				{
				_localctx = new RepeatUntilStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(313);
				match(T__46);
				setState(314);
				((RepeatUntilStmtContext)_localctx).stmt = statement(0);
				setState(315);
				match(T__47);
				setState(316);
				((RepeatUntilStmtContext)_localctx).cond = expression(0);
				}
				break;
			case 12:
				{
				_localctx = new ForStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(318);
				match(T__48);
				setState(319);
				((ForStmtContext)_localctx).init = statement(0);
				setState(320);
				match(T__15);
				setState(321);
				((ForStmtContext)_localctx).condition = expression(0);
				setState(322);
				match(T__45);
				setState(323);
				((ForStmtContext)_localctx).stmt = statement(0);
				setState(324);
				match(T__2);
				}
				break;
			case 13:
				{
				_localctx = new ForRangeStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(326);
				match(T__48);
				setState(327);
				((ForRangeStmtContext)_localctx).var = match(Id);
				setState(328);
				match(T__49);
				setState(329);
				((ForRangeStmtContext)_localctx).min = expression(0);
				setState(330);
				match(T__50);
				setState(331);
				((ForRangeStmtContext)_localctx).max = expression(0);
				setState(332);
				match(T__45);
				setState(333);
				((ForRangeStmtContext)_localctx).stmt = statement(0);
				setState(334);
				match(T__2);
				}
				break;
			case 14:
				{
				_localctx = new LoopStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(336);
				match(T__51);
				setState(337);
				((LoopStmtContext)_localctx).stmt = statement(0);
				setState(338);
				match(T__2);
				}
				break;
			case 15:
				{
				_localctx = new ReturnStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(340);
				match(T__52);
				setState(341);
				((ReturnStmtContext)_localctx).exp = expression(0);
				}
				break;
			case 16:
				{
				_localctx = new CaseStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(342);
				match(T__53);
				setState(343);
				((CaseStmtContext)_localctx).exp = expression(0);
				setState(344);
				match(T__12);
				setState(345);
				((CaseStmtContext)_localctx).caseAlternative = caseAlternative();
				((CaseStmtContext)_localctx).cases.add(((CaseStmtContext)_localctx).caseAlternative);
				setState(350);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__54) {
					{
					{
					setState(346);
					match(T__54);
					setState(347);
					((CaseStmtContext)_localctx).caseAlternative = caseAlternative();
					((CaseStmtContext)_localctx).cases.add(((CaseStmtContext)_localctx).caseAlternative);
					}
					}
					setState(352);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(355);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__42) {
					{
					setState(353);
					match(T__42);
					setState(354);
					((CaseStmtContext)_localctx).elseStmt = statement(0);
					}
				}

				setState(357);
				match(T__2);
				}
				break;
			case 17:
				{
				_localctx = new ExitStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(359);
				match(T__55);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(371);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,32,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new SequenceStmtContext(new StatementContext(_parentctx, _parentState));
					((SequenceStmtContext)_localctx).stmt.add(_prevctx);
					pushNewRecursionContext(_localctx, _startState, RULE_statement);
					setState(362);
					if (!(precpred(_ctx, 16))) throw new FailedPredicateException(this, "precpred(_ctx, 16)");
					setState(365); 
					_errHandler.sync(this);
					_alt = 1;
					do {
						switch (_alt) {
						case 1:
							{
							{
							setState(363);
							match(T__1);
							setState(364);
							((SequenceStmtContext)_localctx).statement = statement(0);
							((SequenceStmtContext)_localctx).stmt.add(((SequenceStmtContext)_localctx).statement);
							}
							}
							break;
						default:
							throw new NoViableAltException(this);
						}
						setState(367); 
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,31,_ctx);
					} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
					}
					} 
				}
				setState(373);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,32,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class DesignatorContext extends ParserRuleContext {
		public DesignatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_designator; }
	 
		public DesignatorContext() { }
		public void copyFrom(DesignatorContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class RecordAssignmentContext extends DesignatorContext {
		public ExpressionContext record;
		public Token name;
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode Id() { return getToken(OberonParser.Id, 0); }
		public RecordAssignmentContext(DesignatorContext ctx) { copyFrom(ctx); }
	}
	public static class PointerAssignmentContext extends DesignatorContext {
		public Token pointer;
		public TerminalNode Id() { return getToken(OberonParser.Id, 0); }
		public PointerAssignmentContext(DesignatorContext ctx) { copyFrom(ctx); }
	}
	public static class VarAssignmentContext extends DesignatorContext {
		public Token var;
		public TerminalNode Id() { return getToken(OberonParser.Id, 0); }
		public VarAssignmentContext(DesignatorContext ctx) { copyFrom(ctx); }
	}
	public static class ArrayAssignmentContext extends DesignatorContext {
		public ExpressionContext array;
		public ExpressionContext elem;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public ArrayAssignmentContext(DesignatorContext ctx) { copyFrom(ctx); }
	}

	public final DesignatorContext designator() throws RecognitionException {
		DesignatorContext _localctx = new DesignatorContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_designator);
		try {
			setState(386);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,33,_ctx) ) {
			case 1:
				_localctx = new VarAssignmentContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(374);
				((VarAssignmentContext)_localctx).var = match(Id);
				}
				break;
			case 2:
				_localctx = new ArrayAssignmentContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(375);
				((ArrayAssignmentContext)_localctx).array = expression(0);
				setState(376);
				match(T__21);
				setState(377);
				((ArrayAssignmentContext)_localctx).elem = expression(0);
				setState(378);
				match(T__22);
				}
				break;
			case 3:
				_localctx = new RecordAssignmentContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(380);
				((RecordAssignmentContext)_localctx).record = expression(0);
				setState(381);
				match(T__3);
				setState(382);
				((RecordAssignmentContext)_localctx).name = match(Id);
				}
				break;
			case 4:
				_localctx = new PointerAssignmentContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(384);
				((PointerAssignmentContext)_localctx).pointer = match(Id);
				setState(385);
				match(T__23);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CaseAlternativeContext extends ParserRuleContext {
		public CaseAlternativeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_caseAlternative; }
	 
		public CaseAlternativeContext() { }
		public void copyFrom(CaseAlternativeContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class RangeCaseContext extends CaseAlternativeContext {
		public ExpressionContext min;
		public ExpressionContext max;
		public StatementContext stmt;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public RangeCaseContext(CaseAlternativeContext ctx) { copyFrom(ctx); }
	}
	public static class SimpleCaseContext extends CaseAlternativeContext {
		public ExpressionContext cond;
		public StatementContext stmt;
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public SimpleCaseContext(CaseAlternativeContext ctx) { copyFrom(ctx); }
	}

	public final CaseAlternativeContext caseAlternative() throws RecognitionException {
		CaseAlternativeContext _localctx = new CaseAlternativeContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_caseAlternative);
		try {
			setState(398);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,34,_ctx) ) {
			case 1:
				_localctx = new SimpleCaseContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(388);
				((SimpleCaseContext)_localctx).cond = expression(0);
				setState(389);
				match(T__16);
				setState(390);
				((SimpleCaseContext)_localctx).stmt = statement(0);
				}
				break;
			case 2:
				_localctx = new RangeCaseContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(392);
				((RangeCaseContext)_localctx).min = expression(0);
				setState(393);
				match(T__50);
				setState(394);
				((RangeCaseContext)_localctx).max = expression(0);
				setState(395);
				match(T__16);
				setState(396);
				((RangeCaseContext)_localctx).stmt = statement(0);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ElseIfStmtContext extends ParserRuleContext {
		public ExpressionContext cond;
		public StatementContext stmt;
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public ElseIfStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_elseIfStmt; }
	}

	public final ElseIfStmtContext elseIfStmt() throws RecognitionException {
		ElseIfStmtContext _localctx = new ElseIfStmtContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_elseIfStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(400);
			((ElseIfStmtContext)_localctx).cond = expression(0);
			setState(401);
			match(T__41);
			setState(402);
			((ElseIfStmtContext)_localctx).stmt = statement(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExpValueContext extends ParserRuleContext {
		public IntValueContext intValue() {
			return getRuleContext(IntValueContext.class,0);
		}
		public RealValueContext realValue() {
			return getRuleContext(RealValueContext.class,0);
		}
		public CharValueContext charValue() {
			return getRuleContext(CharValueContext.class,0);
		}
		public StringValueContext stringValue() {
			return getRuleContext(StringValueContext.class,0);
		}
		public BoolValueContext boolValue() {
			return getRuleContext(BoolValueContext.class,0);
		}
		public NullValueContext nullValue() {
			return getRuleContext(NullValueContext.class,0);
		}
		public ExpValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expValue; }
	}

	public final ExpValueContext expValue() throws RecognitionException {
		ExpValueContext _localctx = new ExpValueContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_expValue);
		try {
			setState(410);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INT:
				enterOuterAlt(_localctx, 1);
				{
				setState(404);
				intValue();
				}
				break;
			case REAL:
				enterOuterAlt(_localctx, 2);
				{
				setState(405);
				realValue();
				}
				break;
			case CHAR:
				enterOuterAlt(_localctx, 3);
				{
				setState(406);
				charValue();
				}
				break;
			case STRING:
				enterOuterAlt(_localctx, 4);
				{
				setState(407);
				stringValue();
				}
				break;
			case TRUE:
			case FALSE:
				enterOuterAlt(_localctx, 5);
				{
				setState(408);
				boolValue();
				}
				break;
			case NIL:
				enterOuterAlt(_localctx, 6);
				{
				setState(409);
				nullValue();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IntValueContext extends ParserRuleContext {
		public TerminalNode INT() { return getToken(OberonParser.INT, 0); }
		public IntValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_intValue; }
	}

	public final IntValueContext intValue() throws RecognitionException {
		IntValueContext _localctx = new IntValueContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_intValue);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(412);
			match(INT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RealValueContext extends ParserRuleContext {
		public TerminalNode REAL() { return getToken(OberonParser.REAL, 0); }
		public RealValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_realValue; }
	}

	public final RealValueContext realValue() throws RecognitionException {
		RealValueContext _localctx = new RealValueContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_realValue);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(414);
			match(REAL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CharValueContext extends ParserRuleContext {
		public TerminalNode CHAR() { return getToken(OberonParser.CHAR, 0); }
		public CharValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_charValue; }
	}

	public final CharValueContext charValue() throws RecognitionException {
		CharValueContext _localctx = new CharValueContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_charValue);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(416);
			match(CHAR);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StringValueContext extends ParserRuleContext {
		public TerminalNode STRING() { return getToken(OberonParser.STRING, 0); }
		public StringValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stringValue; }
	}

	public final StringValueContext stringValue() throws RecognitionException {
		StringValueContext _localctx = new StringValueContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_stringValue);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(418);
			match(STRING);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BoolValueContext extends ParserRuleContext {
		public TerminalNode TRUE() { return getToken(OberonParser.TRUE, 0); }
		public TerminalNode FALSE() { return getToken(OberonParser.FALSE, 0); }
		public BoolValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_boolValue; }
	}

	public final BoolValueContext boolValue() throws RecognitionException {
		BoolValueContext _localctx = new BoolValueContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_boolValue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(420);
			_la = _input.LA(1);
			if ( !(_la==TRUE || _la==FALSE) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NullValueContext extends ParserRuleContext {
		public TerminalNode NIL() { return getToken(OberonParser.NIL, 0); }
		public NullValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nullValue; }
	}

	public final NullValueContext nullValue() throws RecognitionException {
		NullValueContext _localctx = new NullValueContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_nullValue);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(422);
			match(NIL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class OberonTypeContext extends ParserRuleContext {
		public OberonTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_oberonType; }
	 
		public OberonTypeContext() { }
		public void copyFrom(OberonTypeContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class IntegerTypeContext extends OberonTypeContext {
		public IntegerTypeContext(OberonTypeContext ctx) { copyFrom(ctx); }
	}
	public static class StringTypeContext extends OberonTypeContext {
		public StringTypeContext(OberonTypeContext ctx) { copyFrom(ctx); }
	}
	public static class BooleanTypeContext extends OberonTypeContext {
		public BooleanTypeContext(OberonTypeContext ctx) { copyFrom(ctx); }
	}
	public static class CharacterTypeContext extends OberonTypeContext {
		public CharacterTypeContext(OberonTypeContext ctx) { copyFrom(ctx); }
	}
	public static class RealTypeContext extends OberonTypeContext {
		public RealTypeContext(OberonTypeContext ctx) { copyFrom(ctx); }
	}
	public static class ComplexTypeContext extends OberonTypeContext {
		public UserTypeContext userType() {
			return getRuleContext(UserTypeContext.class,0);
		}
		public ComplexTypeContext(OberonTypeContext ctx) { copyFrom(ctx); }
	}
	public static class ReferenceTypeContext extends OberonTypeContext {
		public Token name;
		public TerminalNode Id() { return getToken(OberonParser.Id, 0); }
		public ReferenceTypeContext(OberonTypeContext ctx) { copyFrom(ctx); }
	}

	public final OberonTypeContext oberonType() throws RecognitionException {
		OberonTypeContext _localctx = new OberonTypeContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_oberonType);
		try {
			setState(431);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__56:
				_localctx = new IntegerTypeContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(424);
				match(T__56);
				}
				break;
			case T__57:
				_localctx = new RealTypeContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(425);
				match(T__57);
				}
				break;
			case T__58:
				_localctx = new CharacterTypeContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(426);
				match(T__58);
				}
				break;
			case T__59:
				_localctx = new BooleanTypeContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(427);
				match(T__59);
				}
				break;
			case T__60:
				_localctx = new StringTypeContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(428);
				match(T__60);
				}
				break;
			case Id:
				_localctx = new ReferenceTypeContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(429);
				((ReferenceTypeContext)_localctx).name = match(Id);
				}
				break;
			case T__11:
			case T__13:
			case T__14:
				_localctx = new ComplexTypeContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(430);
				userType();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ReplContext extends ParserRuleContext {
		public ReplContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_repl; }
	 
		public ReplContext() { }
		public void copyFrom(ReplContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class REPLVarDeclarationContext extends ReplContext {
		public VarDeclarationContext varDeclaration() {
			return getRuleContext(VarDeclarationContext.class,0);
		}
		public REPLVarDeclarationContext(ReplContext ctx) { copyFrom(ctx); }
	}
	public static class REPLConstantContext extends ReplContext {
		public ConstantContext constant() {
			return getRuleContext(ConstantContext.class,0);
		}
		public REPLConstantContext(ReplContext ctx) { copyFrom(ctx); }
	}
	public static class REPLExpressionContext extends ReplContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public REPLExpressionContext(ReplContext ctx) { copyFrom(ctx); }
	}
	public static class REPLStatementContext extends ReplContext {
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public REPLStatementContext(ReplContext ctx) { copyFrom(ctx); }
	}
	public static class REPLUserTypeDeclarationContext extends ReplContext {
		public UserTypeDeclarationContext userTypeDeclaration() {
			return getRuleContext(UserTypeDeclarationContext.class,0);
		}
		public REPLUserTypeDeclarationContext(ReplContext ctx) { copyFrom(ctx); }
	}

	public final ReplContext repl() throws RecognitionException {
		ReplContext _localctx = new ReplContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_repl);
		try {
			setState(438);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,37,_ctx) ) {
			case 1:
				_localctx = new REPLVarDeclarationContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(433);
				varDeclaration();
				}
				break;
			case 2:
				_localctx = new REPLConstantContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(434);
				constant();
				}
				break;
			case 3:
				_localctx = new REPLExpressionContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(435);
				expression(0);
				}
				break;
			case 4:
				_localctx = new REPLStatementContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(436);
				statement(0);
				}
				break;
			case 5:
				_localctx = new REPLUserTypeDeclarationContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(437);
				userTypeDeclaration();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 14:
			return expression_sempred((ExpressionContext)_localctx, predIndex);
		case 16:
			return statement_sempred((StatementContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expression_sempred(ExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 3);
		case 1:
			return precpred(_ctx, 2);
		case 2:
			return precpred(_ctx, 1);
		case 3:
			return precpred(_ctx, 6);
		case 4:
			return precpred(_ctx, 5);
		}
		return true;
	}
	private boolean statement_sempred(StatementContext _localctx, int predIndex) {
		switch (predIndex) {
		case 5:
			return precpred(_ctx, 16);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3J\u01bb\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\3\2\3\2\3\2\3\2\5\2"+
		"A\n\2\3\2\3\2\5\2E\n\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\4\3\4\3\4\7\4"+
		"R\n\4\f\4\16\4U\13\4\3\5\3\5\3\5\5\5Z\n\5\3\6\3\6\6\6^\n\6\r\6\16\6_\5"+
		"\6b\n\6\3\6\3\6\6\6f\n\6\r\6\16\6g\5\6j\n\6\3\6\3\6\6\6n\n\6\r\6\16\6"+
		"o\5\6r\n\6\3\6\7\6u\n\6\f\6\16\6x\13\6\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b"+
		"\3\b\3\b\6\b\u0084\n\b\r\b\16\b\u0085\3\b\3\b\3\b\3\b\3\b\5\b\u008d\n"+
		"\b\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\7\n\u0097\n\n\f\n\16\n\u009a\13\n\3"+
		"\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\5\13\u00a4\n\13\3\13\3\13\3\13\5\13"+
		"\u00a9\n\13\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\f\7\f\u00b3\n\f\f\f\16"+
		"\f\u00b6\13\f\3\r\3\r\3\r\7\r\u00bb\n\r\f\r\16\r\u00be\13\r\3\16\3\16"+
		"\3\16\7\16\u00c3\n\16\f\16\16\16\u00c6\13\16\3\16\3\16\3\16\3\17\3\17"+
		"\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\5\20\u00d9"+
		"\n\20\3\20\3\20\3\20\3\20\5\20\u00df\n\20\3\20\3\20\3\20\3\20\3\20\3\20"+
		"\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\7\20\u00f2\n\20"+
		"\f\20\16\20\u00f5\13\20\3\21\3\21\5\21\u00f9\n\21\3\21\3\21\3\22\3\22"+
		"\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22"+
		"\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\5\22\u0119"+
		"\n\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\5\22\u0122\n\22\3\22\3\22\3\22"+
		"\3\22\3\22\3\22\3\22\3\22\6\22\u012c\n\22\r\22\16\22\u012d\3\22\3\22\5"+
		"\22\u0132\n\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22"+
		"\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22"+
		"\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22"+
		"\3\22\3\22\3\22\3\22\7\22\u015f\n\22\f\22\16\22\u0162\13\22\3\22\3\22"+
		"\5\22\u0166\n\22\3\22\3\22\3\22\5\22\u016b\n\22\3\22\3\22\3\22\6\22\u0170"+
		"\n\22\r\22\16\22\u0171\7\22\u0174\n\22\f\22\16\22\u0177\13\22\3\23\3\23"+
		"\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\5\23\u0185\n\23\3\24"+
		"\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\5\24\u0191\n\24\3\25\3\25"+
		"\3\25\3\25\3\26\3\26\3\26\3\26\3\26\3\26\5\26\u019d\n\26\3\27\3\27\3\30"+
		"\3\30\3\31\3\31\3\32\3\32\3\33\3\33\3\34\3\34\3\35\3\35\3\35\3\35\3\35"+
		"\3\35\3\35\5\35\u01b2\n\35\3\36\3\36\3\36\3\36\3\36\5\36\u01b9\n\36\3"+
		"\36\2\4\36\"\37\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62"+
		"\64\668:\2\6\4\2\r\r\33\37\3\2 \"\3\2#%\3\2CD\2\u01e7\2<\3\2\2\2\4J\3"+
		"\2\2\2\6N\3\2\2\2\bV\3\2\2\2\na\3\2\2\2\fy\3\2\2\2\16\u008c\3\2\2\2\20"+
		"\u008e\3\2\2\2\22\u0093\3\2\2\2\24\u009f\3\2\2\2\26\u00af\3\2\2\2\30\u00b7"+
		"\3\2\2\2\32\u00bf\3\2\2\2\34\u00ca\3\2\2\2\36\u00de\3\2\2\2 \u00f8\3\2"+
		"\2\2\"\u016a\3\2\2\2$\u0184\3\2\2\2&\u0190\3\2\2\2(\u0192\3\2\2\2*\u019c"+
		"\3\2\2\2,\u019e\3\2\2\2.\u01a0\3\2\2\2\60\u01a2\3\2\2\2\62\u01a4\3\2\2"+
		"\2\64\u01a6\3\2\2\2\66\u01a8\3\2\2\28\u01b1\3\2\2\2:\u01b8\3\2\2\2<=\7"+
		"\3\2\2=>\7G\2\2>@\7\4\2\2?A\5\4\3\2@?\3\2\2\2@A\3\2\2\2AB\3\2\2\2BD\5"+
		"\n\6\2CE\5\34\17\2DC\3\2\2\2DE\3\2\2\2EF\3\2\2\2FG\7\5\2\2GH\7G\2\2HI"+
		"\7\6\2\2I\3\3\2\2\2JK\7\7\2\2KL\5\6\4\2LM\7\4\2\2M\5\3\2\2\2NS\5\b\5\2"+
		"OP\7\b\2\2PR\5\b\5\2QO\3\2\2\2RU\3\2\2\2SQ\3\2\2\2ST\3\2\2\2T\7\3\2\2"+
		"\2US\3\2\2\2VY\7G\2\2WX\7\t\2\2XZ\7G\2\2YW\3\2\2\2YZ\3\2\2\2Z\t\3\2\2"+
		"\2[]\7\n\2\2\\^\5\f\7\2]\\\3\2\2\2^_\3\2\2\2_]\3\2\2\2_`\3\2\2\2`b\3\2"+
		"\2\2a[\3\2\2\2ab\3\2\2\2bi\3\2\2\2ce\7\13\2\2df\5\20\t\2ed\3\2\2\2fg\3"+
		"\2\2\2ge\3\2\2\2gh\3\2\2\2hj\3\2\2\2ic\3\2\2\2ij\3\2\2\2jq\3\2\2\2km\7"+
		"\f\2\2ln\5\22\n\2ml\3\2\2\2no\3\2\2\2om\3\2\2\2op\3\2\2\2pr\3\2\2\2qk"+
		"\3\2\2\2qr\3\2\2\2rv\3\2\2\2su\5\24\13\2ts\3\2\2\2ux\3\2\2\2vt\3\2\2\2"+
		"vw\3\2\2\2w\13\3\2\2\2xv\3\2\2\2yz\7G\2\2z{\7\r\2\2{|\5\16\b\2|\r\3\2"+
		"\2\2}~\7\16\2\2~\177\7@\2\2\177\u0080\7\17\2\2\u0080\u008d\58\35\2\u0081"+
		"\u0083\7\20\2\2\u0082\u0084\5\22\n\2\u0083\u0082\3\2\2\2\u0084\u0085\3"+
		"\2\2\2\u0085\u0083\3\2\2\2\u0085\u0086\3\2\2\2\u0086\u0087\3\2\2\2\u0087"+
		"\u0088\7\5\2\2\u0088\u008d\3\2\2\2\u0089\u008a\7\21\2\2\u008a\u008b\7"+
		"\22\2\2\u008b\u008d\58\35\2\u008c}\3\2\2\2\u008c\u0081\3\2\2\2\u008c\u0089"+
		"\3\2\2\2\u008d\17\3\2\2\2\u008e\u008f\7G\2\2\u008f\u0090\7\r\2\2\u0090"+
		"\u0091\5\36\20\2\u0091\u0092\7\4\2\2\u0092\21\3\2\2\2\u0093\u0098\7G\2"+
		"\2\u0094\u0095\7\b\2\2\u0095\u0097\7G\2\2\u0096\u0094\3\2\2\2\u0097\u009a"+
		"\3\2\2\2\u0098\u0096\3\2\2\2\u0098\u0099\3\2\2\2\u0099\u009b\3\2\2\2\u009a"+
		"\u0098\3\2\2\2\u009b\u009c\7\23\2\2\u009c\u009d\58\35\2\u009d\u009e\7"+
		"\4\2\2\u009e\23\3\2\2\2\u009f\u00a0\7\24\2\2\u00a0\u00a1\7G\2\2\u00a1"+
		"\u00a3\7\25\2\2\u00a2\u00a4\5\26\f\2\u00a3\u00a2\3\2\2\2\u00a3\u00a4\3"+
		"\2\2\2\u00a4\u00a5\3\2\2\2\u00a5\u00a8\7\26\2\2\u00a6\u00a7\7\23\2\2\u00a7"+
		"\u00a9\58\35\2\u00a8\u00a6\3\2\2\2\u00a8\u00a9\3\2\2\2\u00a9\u00aa\3\2"+
		"\2\2\u00aa\u00ab\7\4\2\2\u00ab\u00ac\5\n\6\2\u00ac\u00ad\5\34\17\2\u00ad"+
		"\u00ae\7G\2\2\u00ae\25\3\2\2\2\u00af\u00b4\5\32\16\2\u00b0\u00b1\7\b\2"+
		"\2\u00b1\u00b3\5\32\16\2\u00b2\u00b0\3\2\2\2\u00b3\u00b6\3\2\2\2\u00b4"+
		"\u00b2\3\2\2\2\u00b4\u00b5\3\2\2\2\u00b5\27\3\2\2\2\u00b6\u00b4\3\2\2"+
		"\2\u00b7\u00bc\5\36\20\2\u00b8\u00b9\7\b\2\2\u00b9\u00bb\5\36\20\2\u00ba"+
		"\u00b8\3\2\2\2\u00bb\u00be\3\2\2\2\u00bc\u00ba\3\2\2\2\u00bc\u00bd\3\2"+
		"\2\2\u00bd\31\3\2\2\2\u00be\u00bc\3\2\2\2\u00bf\u00c4\7G\2\2\u00c0\u00c1"+
		"\7\b\2\2\u00c1\u00c3\7G\2\2\u00c2\u00c0\3\2\2\2\u00c3\u00c6\3\2\2\2\u00c4"+
		"\u00c2\3\2\2\2\u00c4\u00c5\3\2\2\2\u00c5\u00c7\3\2\2\2\u00c6\u00c4\3\2"+
		"\2\2\u00c7\u00c8\7\23\2\2\u00c8\u00c9\58\35\2\u00c9\33\3\2\2\2\u00ca\u00cb"+
		"\7\27\2\2\u00cb\u00cc\5\"\22\2\u00cc\u00cd\7\5\2\2\u00cd\35\3\2\2\2\u00ce"+
		"\u00cf\b\20\1\2\u00cf\u00d0\7\25\2\2\u00d0\u00d1\5\36\20\2\u00d1\u00d2"+
		"\7\26\2\2\u00d2\u00df\3\2\2\2\u00d3\u00df\5*\26\2\u00d4\u00df\5 \21\2"+
		"\u00d5\u00d6\5 \21\2\u00d6\u00d8\7\25\2\2\u00d7\u00d9\5\30\r\2\u00d8\u00d7"+
		"\3\2\2\2\u00d8\u00d9\3\2\2\2\u00d9\u00da\3\2\2\2\u00da\u00db\7\26\2\2"+
		"\u00db\u00df\3\2\2\2\u00dc\u00dd\7G\2\2\u00dd\u00df\7\32\2\2\u00de\u00ce"+
		"\3\2\2\2\u00de\u00d3\3\2\2\2\u00de\u00d4\3\2\2\2\u00de\u00d5\3\2\2\2\u00de"+
		"\u00dc\3\2\2\2\u00df\u00f3\3\2\2\2\u00e0\u00e1\f\5\2\2\u00e1\u00e2\t\2"+
		"\2\2\u00e2\u00f2\5\36\20\6\u00e3\u00e4\f\4\2\2\u00e4\u00e5\t\3\2\2\u00e5"+
		"\u00f2\5\36\20\5\u00e6\u00e7\f\3\2\2\u00e7\u00e8\t\4\2\2\u00e8\u00f2\5"+
		"\36\20\4\u00e9\u00ea\f\b\2\2\u00ea\u00eb\7\6\2\2\u00eb\u00f2\7G\2\2\u00ec"+
		"\u00ed\f\7\2\2\u00ed\u00ee\7\30\2\2\u00ee\u00ef\5\36\20\2\u00ef\u00f0"+
		"\7\31\2\2\u00f0\u00f2\3\2\2\2\u00f1\u00e0\3\2\2\2\u00f1\u00e3\3\2\2\2"+
		"\u00f1\u00e6\3\2\2\2\u00f1\u00e9\3\2\2\2\u00f1\u00ec\3\2\2\2\u00f2\u00f5"+
		"\3\2\2\2\u00f3\u00f1\3\2\2\2\u00f3\u00f4\3\2\2\2\u00f4\37\3\2\2\2\u00f5"+
		"\u00f3\3\2\2\2\u00f6\u00f7\7G\2\2\u00f7\u00f9\7&\2\2\u00f8\u00f6\3\2\2"+
		"\2\u00f8\u00f9\3\2\2\2\u00f9\u00fa\3\2\2\2\u00fa\u00fb\7G\2\2\u00fb!\3"+
		"\2\2\2\u00fc\u00fd\b\22\1\2\u00fd\u00fe\7G\2\2\u00fe\u00ff\7\t\2\2\u00ff"+
		"\u016b\5\36\20\2\u0100\u0101\5$\23\2\u0101\u0102\7\t\2\2\u0102\u0103\5"+
		"\36\20\2\u0103\u016b\3\2\2\2\u0104\u0105\7\'\2\2\u0105\u0106\7\25\2\2"+
		"\u0106\u0107\7G\2\2\u0107\u016b\7\26\2\2\u0108\u0109\7(\2\2\u0109\u010a"+
		"\7\25\2\2\u010a\u010b\7G\2\2\u010b\u016b\7\26\2\2\u010c\u010d\7)\2\2\u010d"+
		"\u010e\7\25\2\2\u010e\u010f\7G\2\2\u010f\u016b\7\26\2\2\u0110\u0111\7"+
		"*\2\2\u0111\u0112\7\25\2\2\u0112\u0113\5\36\20\2\u0113\u0114\7\26\2\2"+
		"\u0114\u016b\3\2\2\2\u0115\u0116\7G\2\2\u0116\u0118\7\25\2\2\u0117\u0119"+
		"\5\30\r\2\u0118\u0117\3\2\2\2\u0118\u0119\3\2\2\2\u0119\u011a\3\2\2\2"+
		"\u011a\u016b\7\26\2\2\u011b\u011c\7+\2\2\u011c\u011d\5\36\20\2\u011d\u011e"+
		"\7,\2\2\u011e\u0121\5\"\22\2\u011f\u0120\7-\2\2\u0120\u0122\5\"\22\2\u0121"+
		"\u011f\3\2\2\2\u0121\u0122\3\2\2\2\u0122\u0123\3\2\2\2\u0123\u0124\7\5"+
		"\2\2\u0124\u016b\3\2\2\2\u0125\u0126\7+\2\2\u0126\u0127\5\36\20\2\u0127"+
		"\u0128\7,\2\2\u0128\u012b\5\"\22\2\u0129\u012a\7.\2\2\u012a\u012c\5(\25"+
		"\2\u012b\u0129\3\2\2\2\u012c\u012d\3\2\2\2\u012d\u012b\3\2\2\2\u012d\u012e"+
		"\3\2\2\2\u012e\u0131\3\2\2\2\u012f\u0130\7-\2\2\u0130\u0132\5\"\22\2\u0131"+
		"\u012f\3\2\2\2\u0131\u0132\3\2\2\2\u0132\u0133\3\2\2\2\u0133\u0134\7\5"+
		"\2\2\u0134\u016b\3\2\2\2\u0135\u0136\7/\2\2\u0136\u0137\5\36\20\2\u0137"+
		"\u0138\7\60\2\2\u0138\u0139\5\"\22\2\u0139\u013a\7\5\2\2\u013a\u016b\3"+
		"\2\2\2\u013b\u013c\7\61\2\2\u013c\u013d\5\"\22\2\u013d\u013e\7\62\2\2"+
		"\u013e\u013f\5\36\20\2\u013f\u016b\3\2\2\2\u0140\u0141\7\63\2\2\u0141"+
		"\u0142\5\"\22\2\u0142\u0143\7\22\2\2\u0143\u0144\5\36\20\2\u0144\u0145"+
		"\7\60\2\2\u0145\u0146\5\"\22\2\u0146\u0147\7\5\2\2\u0147\u016b\3\2\2\2"+
		"\u0148\u0149\7\63\2\2\u0149\u014a\7G\2\2\u014a\u014b\7\64\2\2\u014b\u014c"+
		"\5\36\20\2\u014c\u014d\7\65\2\2\u014d\u014e\5\36\20\2\u014e\u014f\7\60"+
		"\2\2\u014f\u0150\5\"\22\2\u0150\u0151\7\5\2\2\u0151\u016b\3\2\2\2\u0152"+
		"\u0153\7\66\2\2\u0153\u0154\5\"\22\2\u0154\u0155\7\5\2\2\u0155\u016b\3"+
		"\2\2\2\u0156\u0157\7\67\2\2\u0157\u016b\5\36\20\2\u0158\u0159\78\2\2\u0159"+
		"\u015a\5\36\20\2\u015a\u015b\7\17\2\2\u015b\u0160\5&\24\2\u015c\u015d"+
		"\79\2\2\u015d\u015f\5&\24\2\u015e\u015c\3\2\2\2\u015f\u0162\3\2\2\2\u0160"+
		"\u015e\3\2\2\2\u0160\u0161\3\2\2\2\u0161\u0165\3\2\2\2\u0162\u0160\3\2"+
		"\2\2\u0163\u0164\7-\2\2\u0164\u0166\5\"\22\2\u0165\u0163\3\2\2\2\u0165"+
		"\u0166\3\2\2\2\u0166\u0167\3\2\2\2\u0167\u0168\7\5\2\2\u0168\u016b\3\2"+
		"\2\2\u0169\u016b\7:\2\2\u016a\u00fc\3\2\2\2\u016a\u0100\3\2\2\2\u016a"+
		"\u0104\3\2\2\2\u016a\u0108\3\2\2\2\u016a\u010c\3\2\2\2\u016a\u0110\3\2"+
		"\2\2\u016a\u0115\3\2\2\2\u016a\u011b\3\2\2\2\u016a\u0125\3\2\2\2\u016a"+
		"\u0135\3\2\2\2\u016a\u013b\3\2\2\2\u016a\u0140\3\2\2\2\u016a\u0148\3\2"+
		"\2\2\u016a\u0152\3\2\2\2\u016a\u0156\3\2\2\2\u016a\u0158\3\2\2\2\u016a"+
		"\u0169\3\2\2\2\u016b\u0175\3\2\2\2\u016c\u016f\f\22\2\2\u016d\u016e\7"+
		"\4\2\2\u016e\u0170\5\"\22\2\u016f\u016d\3\2\2\2\u0170\u0171\3\2\2\2\u0171"+
		"\u016f\3\2\2\2\u0171\u0172\3\2\2\2\u0172\u0174\3\2\2\2\u0173\u016c\3\2"+
		"\2\2\u0174\u0177\3\2\2\2\u0175\u0173\3\2\2\2\u0175\u0176\3\2\2\2\u0176"+
		"#\3\2\2\2\u0177\u0175\3\2\2\2\u0178\u0185\7G\2\2\u0179\u017a\5\36\20\2"+
		"\u017a\u017b\7\30\2\2\u017b\u017c\5\36\20\2\u017c\u017d\7\31\2\2\u017d"+
		"\u0185\3\2\2\2\u017e\u017f\5\36\20\2\u017f\u0180\7\6\2\2\u0180\u0181\7"+
		"G\2\2\u0181\u0185\3\2\2\2\u0182\u0183\7G\2\2\u0183\u0185\7\32\2\2\u0184"+
		"\u0178\3\2\2\2\u0184\u0179\3\2\2\2\u0184\u017e\3\2\2\2\u0184\u0182\3\2"+
		"\2\2\u0185%\3\2\2\2\u0186\u0187\5\36\20\2\u0187\u0188\7\23\2\2\u0188\u0189"+
		"\5\"\22\2\u0189\u0191\3\2\2\2\u018a\u018b\5\36\20\2\u018b\u018c\7\65\2"+
		"\2\u018c\u018d\5\36\20\2\u018d\u018e\7\23\2\2\u018e\u018f\5\"\22\2\u018f"+
		"\u0191\3\2\2\2\u0190\u0186\3\2\2\2\u0190\u018a\3\2\2\2\u0191\'\3\2\2\2"+
		"\u0192\u0193\5\36\20\2\u0193\u0194\7,\2\2\u0194\u0195\5\"\22\2\u0195)"+
		"\3\2\2\2\u0196\u019d\5,\27\2\u0197\u019d\5.\30\2\u0198\u019d\5\60\31\2"+
		"\u0199\u019d\5\62\32\2\u019a\u019d\5\64\33\2\u019b\u019d\5\66\34\2\u019c"+
		"\u0196\3\2\2\2\u019c\u0197\3\2\2\2\u019c\u0198\3\2\2\2\u019c\u0199\3\2"+
		"\2\2\u019c\u019a\3\2\2\2\u019c\u019b\3\2\2\2\u019d+\3\2\2\2\u019e\u019f"+
		"\7@\2\2\u019f-\3\2\2\2\u01a0\u01a1\7A\2\2\u01a1/\3\2\2\2\u01a2\u01a3\7"+
		"B\2\2\u01a3\61\3\2\2\2\u01a4\u01a5\7E\2\2\u01a5\63\3\2\2\2\u01a6\u01a7"+
		"\t\5\2\2\u01a7\65\3\2\2\2\u01a8\u01a9\7F\2\2\u01a9\67\3\2\2\2\u01aa\u01b2"+
		"\7;\2\2\u01ab\u01b2\7<\2\2\u01ac\u01b2\7=\2\2\u01ad\u01b2\7>\2\2\u01ae"+
		"\u01b2\7?\2\2\u01af\u01b2\7G\2\2\u01b0\u01b2\5\16\b\2\u01b1\u01aa\3\2"+
		"\2\2\u01b1\u01ab\3\2\2\2\u01b1\u01ac\3\2\2\2\u01b1\u01ad\3\2\2\2\u01b1"+
		"\u01ae\3\2\2\2\u01b1\u01af\3\2\2\2\u01b1\u01b0\3\2\2\2\u01b29\3\2\2\2"+
		"\u01b3\u01b9\5\22\n\2\u01b4\u01b9\5\20\t\2\u01b5\u01b9\5\36\20\2\u01b6"+
		"\u01b9\5\"\22\2\u01b7\u01b9\5\f\7\2\u01b8\u01b3\3\2\2\2\u01b8\u01b4\3"+
		"\2\2\2\u01b8\u01b5\3\2\2\2\u01b8\u01b6\3\2\2\2\u01b8\u01b7\3\2\2\2\u01b9"+
		";\3\2\2\2(@DSY_agioqv\u0085\u008c\u0098\u00a3\u00a8\u00b4\u00bc\u00c4"+
		"\u00d8\u00de\u00f1\u00f3\u00f8\u0118\u0121\u012d\u0131\u0160\u0165\u016a"+
		"\u0171\u0175\u0184\u0190\u019c\u01b1\u01b8";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}