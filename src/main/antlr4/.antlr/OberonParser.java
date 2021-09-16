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
		T__59=60, INT=61, REAL=62, CHAR=63, TRUE=64, FALSE=65, STRING=66, Id=67, 
		WS=68, COMMENT=69, LINE_COMMENT=70;
	public static final int
		RULE_compilationUnit = 0, RULE_imports = 1, RULE_importList = 2, RULE_importModule = 3, 
		RULE_declarations = 4, RULE_userTypeDeclaration = 5, RULE_userType = 6, 
		RULE_constant = 7, RULE_varDeclaration = 8, RULE_procedure = 9, RULE_formals = 10, 
		RULE_arguments = 11, RULE_formalArg = 12, RULE_block = 13, RULE_expression = 14, 
		RULE_qualifiedName = 15, RULE_statement = 16, RULE_designator = 17, RULE_caseAlternative = 18, 
		RULE_elseIfStmt = 19, RULE_expValue = 20, RULE_intValue = 21, RULE_realValue = 22, 
		RULE_charValue = 23, RULE_stringValue = 24, RULE_boolValue = 25, RULE_oberonType = 26, 
		RULE_repl = 27;
	private static String[] makeRuleNames() {
		return new String[] {
			"compilationUnit", "imports", "importList", "importModule", "declarations", 
			"userTypeDeclaration", "userType", "constant", "varDeclaration", "procedure", 
			"formals", "arguments", "formalArg", "block", "expression", "qualifiedName", 
			"statement", "designator", "caseAlternative", "elseIfStmt", "expValue", 
			"intValue", "realValue", "charValue", "stringValue", "boolValue", "oberonType", 
			"repl"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'MODULE'", "';'", "'END'", "'.'", "'IMPORT'", "','", "':='", "'TYPE'", 
			"'CONST'", "'VAR'", "'='", "'ARRAY'", "'OF'", "'RECORD'", "'POINTER'", 
			"'TO'", "':'", "'PROCEDURE'", "'('", "')'", "'BEGIN'", "'['", "']'", 
			"'#'", "'<'", "'<='", "'>'", "'>='", "'*'", "'/'", "'&&'", "'+'", "'-'", 
			"'||'", "'::'", "'readReal'", "'readInt'", "'readChar'", "'write'", "'IF'", 
			"'THEN'", "'ELSE'", "'ELSIF'", "'WHILE'", "'DO'", "'REPEAT'", "'UNTIL'", 
			"'FOR'", "'IN'", "'..'", "'LOOP'", "'RETURN'", "'CASE'", "'|'", "'EXIT'", 
			"'INTEGER'", "'REAL'", "'CHAR'", "'BOOLEAN'", "'STRING'", null, null, 
			null, "'True'", "'False'"
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
			null, "INT", "REAL", "CHAR", "TRUE", "FALSE", "STRING", "Id", "WS", "COMMENT", 
			"LINE_COMMENT"
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
			setState(56);
			match(T__0);
			setState(57);
			((CompilationUnitContext)_localctx).name = match(Id);
			setState(58);
			match(T__1);
			setState(60);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__4) {
				{
				setState(59);
				imports();
				}
			}

			setState(62);
			declarations();
			setState(64);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__20) {
				{
				setState(63);
				block();
				}
			}

			setState(66);
			match(T__2);
			setState(67);
			match(Id);
			setState(68);
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
			setState(70);
			match(T__4);
			setState(71);
			importList();
			setState(72);
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
			setState(74);
			((ImportListContext)_localctx).importModule = importModule();
			((ImportListContext)_localctx).modules.add(((ImportListContext)_localctx).importModule);
			setState(79);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__5) {
				{
				{
				setState(75);
				match(T__5);
				setState(76);
				((ImportListContext)_localctx).importModule = importModule();
				((ImportListContext)_localctx).modules.add(((ImportListContext)_localctx).importModule);
				}
				}
				setState(81);
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
			setState(82);
			((ImportModuleContext)_localctx).module = match(Id);
			setState(85);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__6) {
				{
				setState(83);
				match(T__6);
				setState(84);
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
			setState(93);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__7) {
				{
				setState(87);
				match(T__7);
				setState(89); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(88);
					userTypeDeclaration();
					}
					}
					setState(91); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==Id );
				}
			}

			setState(101);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__8) {
				{
				setState(95);
				match(T__8);
				setState(97); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(96);
					constant();
					}
					}
					setState(99); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==Id );
				}
			}

			setState(109);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__9) {
				{
				setState(103);
				match(T__9);
				setState(105); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(104);
					varDeclaration();
					}
					}
					setState(107); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==Id );
				}
			}

			setState(114);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__17) {
				{
				{
				setState(111);
				procedure();
				}
				}
				setState(116);
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
			setState(117);
			((UserTypeDeclarationContext)_localctx).nameType = match(Id);
			setState(118);
			match(T__10);
			setState(119);
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
			setState(136);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__11:
				_localctx = new ArrayTypeDeclarationContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				{
				setState(121);
				match(T__11);
				setState(122);
				((ArrayTypeDeclarationContext)_localctx).length = match(INT);
				setState(123);
				match(T__12);
				setState(124);
				((ArrayTypeDeclarationContext)_localctx).baseType = oberonType();
				}
				}
				break;
			case T__13:
				_localctx = new RecordTypeDeclarationContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(125);
				match(T__13);
				setState(127); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(126);
					((RecordTypeDeclarationContext)_localctx).varDeclaration = varDeclaration();
					((RecordTypeDeclarationContext)_localctx).vars.add(((RecordTypeDeclarationContext)_localctx).varDeclaration);
					}
					}
					setState(129); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==Id );
				setState(131);
				match(T__2);
				}
				}
				break;
			case T__14:
				_localctx = new PointerTypeDeclarationContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				{
				setState(133);
				match(T__14);
				setState(134);
				match(T__15);
				setState(135);
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
			setState(138);
			((ConstantContext)_localctx).constName = match(Id);
			setState(139);
			match(T__10);
			setState(140);
			((ConstantContext)_localctx).exp = expression(0);
			setState(141);
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
			setState(143);
			((VarDeclarationContext)_localctx).Id = match(Id);
			((VarDeclarationContext)_localctx).vars.add(((VarDeclarationContext)_localctx).Id);
			setState(148);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__5) {
				{
				{
				setState(144);
				match(T__5);
				setState(145);
				((VarDeclarationContext)_localctx).Id = match(Id);
				((VarDeclarationContext)_localctx).vars.add(((VarDeclarationContext)_localctx).Id);
				}
				}
				setState(150);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
			setState(151);
			match(T__16);
			setState(152);
			((VarDeclarationContext)_localctx).varType = oberonType();
			setState(153);
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
			setState(155);
			match(T__17);
			setState(156);
			((ProcedureContext)_localctx).name = match(Id);
			setState(157);
			match(T__18);
			setState(159);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Id) {
				{
				setState(158);
				formals();
				}
			}

			setState(161);
			match(T__19);
			setState(164);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__16) {
				{
				setState(162);
				match(T__16);
				setState(163);
				((ProcedureContext)_localctx).procedureType = oberonType();
				}
			}

			setState(166);
			match(T__1);
			setState(167);
			declarations();
			setState(168);
			block();
			setState(169);
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
			setState(171);
			formalArg();
			setState(176);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__5) {
				{
				{
				setState(172);
				match(T__5);
				setState(173);
				formalArg();
				}
				}
				setState(178);
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
			setState(179);
			expression(0);
			setState(184);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__5) {
				{
				{
				setState(180);
				match(T__5);
				setState(181);
				expression(0);
				}
				}
				setState(186);
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
			setState(187);
			((FormalArgContext)_localctx).Id = match(Id);
			((FormalArgContext)_localctx).args.add(((FormalArgContext)_localctx).Id);
			setState(192);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__5) {
				{
				{
				setState(188);
				match(T__5);
				setState(189);
				((FormalArgContext)_localctx).Id = match(Id);
				((FormalArgContext)_localctx).args.add(((FormalArgContext)_localctx).Id);
				}
				}
				setState(194);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
			setState(195);
			match(T__16);
			setState(196);
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
			setState(198);
			match(T__20);
			setState(199);
			statement(0);
			setState(200);
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
			setState(216);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,20,_ctx) ) {
			case 1:
				{
				_localctx = new BracketsContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(203);
				match(T__18);
				setState(204);
				expression(0);
				setState(205);
				match(T__19);
				}
				break;
			case 2:
				{
				_localctx = new ValueContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(207);
				expValue();
				}
				break;
			case 3:
				{
				_localctx = new VariableContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(208);
				((VariableContext)_localctx).name = qualifiedName();
				}
				break;
			case 4:
				{
				_localctx = new FunctionCallContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(209);
				((FunctionCallContext)_localctx).name = qualifiedName();
				setState(210);
				match(T__18);
				setState(212);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (((((_la - 19)) & ~0x3f) == 0 && ((1L << (_la - 19)) & ((1L << (T__18 - 19)) | (1L << (INT - 19)) | (1L << (REAL - 19)) | (1L << (CHAR - 19)) | (1L << (TRUE - 19)) | (1L << (FALSE - 19)) | (1L << (STRING - 19)) | (1L << (Id - 19)))) != 0)) {
					{
					setState(211);
					arguments();
					}
				}

				setState(214);
				match(T__19);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(237);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,22,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(235);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
					case 1:
						{
						_localctx = new RelExpressionContext(new ExpressionContext(_parentctx, _parentState));
						((RelExpressionContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(218);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(219);
						((RelExpressionContext)_localctx).opr = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__10) | (1L << T__23) | (1L << T__24) | (1L << T__25) | (1L << T__26) | (1L << T__27))) != 0)) ) {
							((RelExpressionContext)_localctx).opr = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(220);
						((RelExpressionContext)_localctx).right = expression(4);
						}
						break;
					case 2:
						{
						_localctx = new MultExpressionContext(new ExpressionContext(_parentctx, _parentState));
						((MultExpressionContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(221);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(222);
						((MultExpressionContext)_localctx).opr = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__28) | (1L << T__29) | (1L << T__30))) != 0)) ) {
							((MultExpressionContext)_localctx).opr = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(223);
						((MultExpressionContext)_localctx).right = expression(3);
						}
						break;
					case 3:
						{
						_localctx = new AddExpressionContext(new ExpressionContext(_parentctx, _parentState));
						((AddExpressionContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(224);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(225);
						((AddExpressionContext)_localctx).opr = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__31) | (1L << T__32) | (1L << T__33))) != 0)) ) {
							((AddExpressionContext)_localctx).opr = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(226);
						((AddExpressionContext)_localctx).right = expression(2);
						}
						break;
					case 4:
						{
						_localctx = new FieldAccessContext(new ExpressionContext(_parentctx, _parentState));
						((FieldAccessContext)_localctx).exp = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(227);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(228);
						match(T__3);
						setState(229);
						((FieldAccessContext)_localctx).name = match(Id);
						}
						break;
					case 5:
						{
						_localctx = new ArraySubscriptContext(new ExpressionContext(_parentctx, _parentState));
						((ArraySubscriptContext)_localctx).arrayBase = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(230);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(231);
						match(T__21);
						setState(232);
						((ArraySubscriptContext)_localctx).index = expression(0);
						setState(233);
						match(T__22);
						}
						break;
					}
					} 
				}
				setState(239);
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
			setState(242);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
			case 1:
				{
				setState(240);
				((QualifiedNameContext)_localctx).module = match(Id);
				setState(241);
				match(T__34);
				}
				break;
			}
			setState(244);
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
			setState(356);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,30,_ctx) ) {
			case 1:
				{
				_localctx = new AssignmentStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(247);
				((AssignmentStmtContext)_localctx).var = match(Id);
				setState(248);
				match(T__6);
				setState(249);
				((AssignmentStmtContext)_localctx).exp = expression(0);
				}
				break;
			case 2:
				{
				_localctx = new EAssignmentStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(250);
				((EAssignmentStmtContext)_localctx).des = designator();
				setState(251);
				match(T__6);
				setState(252);
				((EAssignmentStmtContext)_localctx).exp = expression(0);
				}
				break;
			case 3:
				{
				_localctx = new ReadRealStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(254);
				match(T__35);
				setState(255);
				match(T__18);
				setState(256);
				((ReadRealStmtContext)_localctx).var = match(Id);
				setState(257);
				match(T__19);
				}
				break;
			case 4:
				{
				_localctx = new ReadIntStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(258);
				match(T__36);
				setState(259);
				match(T__18);
				setState(260);
				((ReadIntStmtContext)_localctx).var = match(Id);
				setState(261);
				match(T__19);
				}
				break;
			case 5:
				{
				_localctx = new ReadCharStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(262);
				match(T__37);
				setState(263);
				match(T__18);
				setState(264);
				((ReadCharStmtContext)_localctx).var = match(Id);
				setState(265);
				match(T__19);
				}
				break;
			case 6:
				{
				_localctx = new WriteStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(266);
				match(T__38);
				setState(267);
				match(T__18);
				setState(268);
				expression(0);
				setState(269);
				match(T__19);
				}
				break;
			case 7:
				{
				_localctx = new ProcedureCallContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(271);
				((ProcedureCallContext)_localctx).name = match(Id);
				setState(272);
				match(T__18);
				setState(274);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (((((_la - 19)) & ~0x3f) == 0 && ((1L << (_la - 19)) & ((1L << (T__18 - 19)) | (1L << (INT - 19)) | (1L << (REAL - 19)) | (1L << (CHAR - 19)) | (1L << (TRUE - 19)) | (1L << (FALSE - 19)) | (1L << (STRING - 19)) | (1L << (Id - 19)))) != 0)) {
					{
					setState(273);
					arguments();
					}
				}

				setState(276);
				match(T__19);
				}
				break;
			case 8:
				{
				_localctx = new IfElseStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(277);
				match(T__39);
				setState(278);
				((IfElseStmtContext)_localctx).cond = expression(0);
				setState(279);
				match(T__40);
				setState(280);
				((IfElseStmtContext)_localctx).thenStmt = statement(0);
				setState(283);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__41) {
					{
					setState(281);
					match(T__41);
					setState(282);
					((IfElseStmtContext)_localctx).elseStmt = statement(0);
					}
				}

				setState(285);
				match(T__2);
				}
				break;
			case 9:
				{
				_localctx = new IfElseIfStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(287);
				match(T__39);
				setState(288);
				((IfElseIfStmtContext)_localctx).cond = expression(0);
				setState(289);
				match(T__40);
				setState(290);
				((IfElseIfStmtContext)_localctx).thenStmt = statement(0);
				setState(293); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(291);
					match(T__42);
					setState(292);
					((IfElseIfStmtContext)_localctx).elseIfStmt = elseIfStmt();
					((IfElseIfStmtContext)_localctx).elsifs.add(((IfElseIfStmtContext)_localctx).elseIfStmt);
					}
					}
					setState(295); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__42 );
				setState(299);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__41) {
					{
					setState(297);
					match(T__41);
					setState(298);
					((IfElseIfStmtContext)_localctx).elseStmt = statement(0);
					}
				}

				setState(301);
				match(T__2);
				}
				break;
			case 10:
				{
				_localctx = new WhileStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(303);
				match(T__43);
				setState(304);
				((WhileStmtContext)_localctx).cond = expression(0);
				setState(305);
				match(T__44);
				setState(306);
				((WhileStmtContext)_localctx).stmt = statement(0);
				setState(307);
				match(T__2);
				}
				break;
			case 11:
				{
				_localctx = new RepeatUntilStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(309);
				match(T__45);
				setState(310);
				((RepeatUntilStmtContext)_localctx).stmt = statement(0);
				setState(311);
				match(T__46);
				setState(312);
				((RepeatUntilStmtContext)_localctx).cond = expression(0);
				}
				break;
			case 12:
				{
				_localctx = new ForStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(314);
				match(T__47);
				setState(315);
				((ForStmtContext)_localctx).init = statement(0);
				setState(316);
				match(T__15);
				setState(317);
				((ForStmtContext)_localctx).condition = expression(0);
				setState(318);
				match(T__44);
				setState(319);
				((ForStmtContext)_localctx).stmt = statement(0);
				setState(320);
				match(T__2);
				}
				break;
			case 13:
				{
				_localctx = new ForRangeStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(322);
				match(T__47);
				setState(323);
				((ForRangeStmtContext)_localctx).var = match(Id);
				setState(324);
				match(T__48);
				setState(325);
				((ForRangeStmtContext)_localctx).min = expression(0);
				setState(326);
				match(T__49);
				setState(327);
				((ForRangeStmtContext)_localctx).max = expression(0);
				setState(328);
				match(T__44);
				setState(329);
				((ForRangeStmtContext)_localctx).stmt = statement(0);
				setState(330);
				match(T__2);
				}
				break;
			case 14:
				{
				_localctx = new LoopStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(332);
				match(T__50);
				setState(333);
				((LoopStmtContext)_localctx).stmt = statement(0);
				setState(334);
				match(T__2);
				}
				break;
			case 15:
				{
				_localctx = new ReturnStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(336);
				match(T__51);
				setState(337);
				((ReturnStmtContext)_localctx).exp = expression(0);
				}
				break;
			case 16:
				{
				_localctx = new CaseStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(338);
				match(T__52);
				setState(339);
				((CaseStmtContext)_localctx).exp = expression(0);
				setState(340);
				match(T__12);
				setState(341);
				((CaseStmtContext)_localctx).caseAlternative = caseAlternative();
				((CaseStmtContext)_localctx).cases.add(((CaseStmtContext)_localctx).caseAlternative);
				setState(346);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__53) {
					{
					{
					setState(342);
					match(T__53);
					setState(343);
					((CaseStmtContext)_localctx).caseAlternative = caseAlternative();
					((CaseStmtContext)_localctx).cases.add(((CaseStmtContext)_localctx).caseAlternative);
					}
					}
					setState(348);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(351);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__41) {
					{
					setState(349);
					match(T__41);
					setState(350);
					((CaseStmtContext)_localctx).elseStmt = statement(0);
					}
				}

				setState(353);
				match(T__2);
				}
				break;
			case 17:
				{
				_localctx = new ExitStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(355);
				match(T__54);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(367);
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
					setState(358);
					if (!(precpred(_ctx, 16))) throw new FailedPredicateException(this, "precpred(_ctx, 16)");
					setState(361); 
					_errHandler.sync(this);
					_alt = 1;
					do {
						switch (_alt) {
						case 1:
							{
							{
							setState(359);
							match(T__1);
							setState(360);
							((SequenceStmtContext)_localctx).statement = statement(0);
							((SequenceStmtContext)_localctx).stmt.add(((SequenceStmtContext)_localctx).statement);
							}
							}
							break;
						default:
							throw new NoViableAltException(this);
						}
						setState(363); 
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,31,_ctx);
					} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
					}
					} 
				}
				setState(369);
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
			setState(380);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,33,_ctx) ) {
			case 1:
				_localctx = new VarAssignmentContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(370);
				((VarAssignmentContext)_localctx).var = match(Id);
				}
				break;
			case 2:
				_localctx = new ArrayAssignmentContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(371);
				((ArrayAssignmentContext)_localctx).array = expression(0);
				setState(372);
				match(T__21);
				setState(373);
				((ArrayAssignmentContext)_localctx).elem = expression(0);
				setState(374);
				match(T__22);
				}
				break;
			case 3:
				_localctx = new RecordAssignmentContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(376);
				((RecordAssignmentContext)_localctx).record = expression(0);
				setState(377);
				match(T__3);
				setState(378);
				((RecordAssignmentContext)_localctx).name = match(Id);
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
			setState(392);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,34,_ctx) ) {
			case 1:
				_localctx = new SimpleCaseContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(382);
				((SimpleCaseContext)_localctx).cond = expression(0);
				setState(383);
				match(T__16);
				setState(384);
				((SimpleCaseContext)_localctx).stmt = statement(0);
				}
				break;
			case 2:
				_localctx = new RangeCaseContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(386);
				((RangeCaseContext)_localctx).min = expression(0);
				setState(387);
				match(T__49);
				setState(388);
				((RangeCaseContext)_localctx).max = expression(0);
				setState(389);
				match(T__16);
				setState(390);
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
			setState(394);
			((ElseIfStmtContext)_localctx).cond = expression(0);
			setState(395);
			match(T__40);
			setState(396);
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
		public ExpValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expValue; }
	}

	public final ExpValueContext expValue() throws RecognitionException {
		ExpValueContext _localctx = new ExpValueContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_expValue);
		try {
			setState(403);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INT:
				enterOuterAlt(_localctx, 1);
				{
				setState(398);
				intValue();
				}
				break;
			case REAL:
				enterOuterAlt(_localctx, 2);
				{
				setState(399);
				realValue();
				}
				break;
			case CHAR:
				enterOuterAlt(_localctx, 3);
				{
				setState(400);
				charValue();
				}
				break;
			case STRING:
				enterOuterAlt(_localctx, 4);
				{
				setState(401);
				stringValue();
				}
				break;
			case TRUE:
			case FALSE:
				enterOuterAlt(_localctx, 5);
				{
				setState(402);
				boolValue();
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
			setState(405);
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
			setState(407);
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
			setState(409);
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
			setState(411);
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
			setState(413);
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
		enterRule(_localctx, 52, RULE_oberonType);
		try {
			setState(422);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__55:
				_localctx = new IntegerTypeContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(415);
				match(T__55);
				}
				break;
			case T__56:
				_localctx = new RealTypeContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(416);
				match(T__56);
				}
				break;
			case T__57:
				_localctx = new CharacterTypeContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(417);
				match(T__57);
				}
				break;
			case T__58:
				_localctx = new BooleanTypeContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(418);
				match(T__58);
				}
				break;
			case T__59:
				_localctx = new StringTypeContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(419);
				match(T__59);
				}
				break;
			case T__11:
			case T__13:
			case T__14:
				_localctx = new ComplexTypeContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(420);
				userType();
				}
				break;
			case Id:
				_localctx = new ReferenceTypeContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(421);
				((ReferenceTypeContext)_localctx).name = match(Id);
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
		enterRule(_localctx, 54, RULE_repl);
		try {
			setState(429);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,37,_ctx) ) {
			case 1:
				_localctx = new REPLVarDeclarationContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(424);
				varDeclaration();
				}
				break;
			case 2:
				_localctx = new REPLConstantContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(425);
				constant();
				}
				break;
			case 3:
				_localctx = new REPLExpressionContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(426);
				expression(0);
				}
				break;
			case 4:
				_localctx = new REPLStatementContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(427);
				statement(0);
				}
				break;
			case 5:
				_localctx = new REPLUserTypeDeclarationContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(428);
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
			return precpred(_ctx, 5);
		case 4:
			return precpred(_ctx, 4);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3H\u01b2\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\3\2\3\2\3\2\3\2\5\2?\n\2\3\2"+
		"\3\2\5\2C\n\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\4\3\4\3\4\7\4P\n\4\f\4"+
		"\16\4S\13\4\3\5\3\5\3\5\5\5X\n\5\3\6\3\6\6\6\\\n\6\r\6\16\6]\5\6`\n\6"+
		"\3\6\3\6\6\6d\n\6\r\6\16\6e\5\6h\n\6\3\6\3\6\6\6l\n\6\r\6\16\6m\5\6p\n"+
		"\6\3\6\7\6s\n\6\f\6\16\6v\13\6\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b"+
		"\6\b\u0082\n\b\r\b\16\b\u0083\3\b\3\b\3\b\3\b\3\b\5\b\u008b\n\b\3\t\3"+
		"\t\3\t\3\t\3\t\3\n\3\n\3\n\7\n\u0095\n\n\f\n\16\n\u0098\13\n\3\n\3\n\3"+
		"\n\3\n\3\13\3\13\3\13\3\13\5\13\u00a2\n\13\3\13\3\13\3\13\5\13\u00a7\n"+
		"\13\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\f\7\f\u00b1\n\f\f\f\16\f\u00b4"+
		"\13\f\3\r\3\r\3\r\7\r\u00b9\n\r\f\r\16\r\u00bc\13\r\3\16\3\16\3\16\7\16"+
		"\u00c1\n\16\f\16\16\16\u00c4\13\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17"+
		"\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\5\20\u00d7\n\20\3\20"+
		"\3\20\5\20\u00db\n\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20"+
		"\3\20\3\20\3\20\3\20\3\20\3\20\3\20\7\20\u00ee\n\20\f\20\16\20\u00f1\13"+
		"\20\3\21\3\21\5\21\u00f5\n\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\22"+
		"\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22"+
		"\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\5\22\u0115\n\22\3\22\3\22\3\22"+
		"\3\22\3\22\3\22\3\22\5\22\u011e\n\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22"+
		"\3\22\6\22\u0128\n\22\r\22\16\22\u0129\3\22\3\22\5\22\u012e\n\22\3\22"+
		"\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22"+
		"\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22"+
		"\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22"+
		"\7\22\u015b\n\22\f\22\16\22\u015e\13\22\3\22\3\22\5\22\u0162\n\22\3\22"+
		"\3\22\3\22\5\22\u0167\n\22\3\22\3\22\3\22\6\22\u016c\n\22\r\22\16\22\u016d"+
		"\7\22\u0170\n\22\f\22\16\22\u0173\13\22\3\23\3\23\3\23\3\23\3\23\3\23"+
		"\3\23\3\23\3\23\3\23\5\23\u017f\n\23\3\24\3\24\3\24\3\24\3\24\3\24\3\24"+
		"\3\24\3\24\3\24\5\24\u018b\n\24\3\25\3\25\3\25\3\25\3\26\3\26\3\26\3\26"+
		"\3\26\5\26\u0196\n\26\3\27\3\27\3\30\3\30\3\31\3\31\3\32\3\32\3\33\3\33"+
		"\3\34\3\34\3\34\3\34\3\34\3\34\3\34\5\34\u01a9\n\34\3\35\3\35\3\35\3\35"+
		"\3\35\5\35\u01b0\n\35\3\35\2\4\36\"\36\2\4\6\b\n\f\16\20\22\24\26\30\32"+
		"\34\36 \"$&(*,.\60\62\64\668\2\6\4\2\r\r\32\36\3\2\37!\3\2\"$\3\2BC\2"+
		"\u01dc\2:\3\2\2\2\4H\3\2\2\2\6L\3\2\2\2\bT\3\2\2\2\n_\3\2\2\2\fw\3\2\2"+
		"\2\16\u008a\3\2\2\2\20\u008c\3\2\2\2\22\u0091\3\2\2\2\24\u009d\3\2\2\2"+
		"\26\u00ad\3\2\2\2\30\u00b5\3\2\2\2\32\u00bd\3\2\2\2\34\u00c8\3\2\2\2\36"+
		"\u00da\3\2\2\2 \u00f4\3\2\2\2\"\u0166\3\2\2\2$\u017e\3\2\2\2&\u018a\3"+
		"\2\2\2(\u018c\3\2\2\2*\u0195\3\2\2\2,\u0197\3\2\2\2.\u0199\3\2\2\2\60"+
		"\u019b\3\2\2\2\62\u019d\3\2\2\2\64\u019f\3\2\2\2\66\u01a8\3\2\2\28\u01af"+
		"\3\2\2\2:;\7\3\2\2;<\7E\2\2<>\7\4\2\2=?\5\4\3\2>=\3\2\2\2>?\3\2\2\2?@"+
		"\3\2\2\2@B\5\n\6\2AC\5\34\17\2BA\3\2\2\2BC\3\2\2\2CD\3\2\2\2DE\7\5\2\2"+
		"EF\7E\2\2FG\7\6\2\2G\3\3\2\2\2HI\7\7\2\2IJ\5\6\4\2JK\7\4\2\2K\5\3\2\2"+
		"\2LQ\5\b\5\2MN\7\b\2\2NP\5\b\5\2OM\3\2\2\2PS\3\2\2\2QO\3\2\2\2QR\3\2\2"+
		"\2R\7\3\2\2\2SQ\3\2\2\2TW\7E\2\2UV\7\t\2\2VX\7E\2\2WU\3\2\2\2WX\3\2\2"+
		"\2X\t\3\2\2\2Y[\7\n\2\2Z\\\5\f\7\2[Z\3\2\2\2\\]\3\2\2\2][\3\2\2\2]^\3"+
		"\2\2\2^`\3\2\2\2_Y\3\2\2\2_`\3\2\2\2`g\3\2\2\2ac\7\13\2\2bd\5\20\t\2c"+
		"b\3\2\2\2de\3\2\2\2ec\3\2\2\2ef\3\2\2\2fh\3\2\2\2ga\3\2\2\2gh\3\2\2\2"+
		"ho\3\2\2\2ik\7\f\2\2jl\5\22\n\2kj\3\2\2\2lm\3\2\2\2mk\3\2\2\2mn\3\2\2"+
		"\2np\3\2\2\2oi\3\2\2\2op\3\2\2\2pt\3\2\2\2qs\5\24\13\2rq\3\2\2\2sv\3\2"+
		"\2\2tr\3\2\2\2tu\3\2\2\2u\13\3\2\2\2vt\3\2\2\2wx\7E\2\2xy\7\r\2\2yz\5"+
		"\16\b\2z\r\3\2\2\2{|\7\16\2\2|}\7?\2\2}~\7\17\2\2~\u008b\5\66\34\2\177"+
		"\u0081\7\20\2\2\u0080\u0082\5\22\n\2\u0081\u0080\3\2\2\2\u0082\u0083\3"+
		"\2\2\2\u0083\u0081\3\2\2\2\u0083\u0084\3\2\2\2\u0084\u0085\3\2\2\2\u0085"+
		"\u0086\7\5\2\2\u0086\u008b\3\2\2\2\u0087\u0088\7\21\2\2\u0088\u0089\7"+
		"\22\2\2\u0089\u008b\5\66\34\2\u008a{\3\2\2\2\u008a\177\3\2\2\2\u008a\u0087"+
		"\3\2\2\2\u008b\17\3\2\2\2\u008c\u008d\7E\2\2\u008d\u008e\7\r\2\2\u008e"+
		"\u008f\5\36\20\2\u008f\u0090\7\4\2\2\u0090\21\3\2\2\2\u0091\u0096\7E\2"+
		"\2\u0092\u0093\7\b\2\2\u0093\u0095\7E\2\2\u0094\u0092\3\2\2\2\u0095\u0098"+
		"\3\2\2\2\u0096\u0094\3\2\2\2\u0096\u0097\3\2\2\2\u0097\u0099\3\2\2\2\u0098"+
		"\u0096\3\2\2\2\u0099\u009a\7\23\2\2\u009a\u009b\5\66\34\2\u009b\u009c"+
		"\7\4\2\2\u009c\23\3\2\2\2\u009d\u009e\7\24\2\2\u009e\u009f\7E\2\2\u009f"+
		"\u00a1\7\25\2\2\u00a0\u00a2\5\26\f\2\u00a1\u00a0\3\2\2\2\u00a1\u00a2\3"+
		"\2\2\2\u00a2\u00a3\3\2\2\2\u00a3\u00a6\7\26\2\2\u00a4\u00a5\7\23\2\2\u00a5"+
		"\u00a7\5\66\34\2\u00a6\u00a4\3\2\2\2\u00a6\u00a7\3\2\2\2\u00a7\u00a8\3"+
		"\2\2\2\u00a8\u00a9\7\4\2\2\u00a9\u00aa\5\n\6\2\u00aa\u00ab\5\34\17\2\u00ab"+
		"\u00ac\7E\2\2\u00ac\25\3\2\2\2\u00ad\u00b2\5\32\16\2\u00ae\u00af\7\b\2"+
		"\2\u00af\u00b1\5\32\16\2\u00b0\u00ae\3\2\2\2\u00b1\u00b4\3\2\2\2\u00b2"+
		"\u00b0\3\2\2\2\u00b2\u00b3\3\2\2\2\u00b3\27\3\2\2\2\u00b4\u00b2\3\2\2"+
		"\2\u00b5\u00ba\5\36\20\2\u00b6\u00b7\7\b\2\2\u00b7\u00b9\5\36\20\2\u00b8"+
		"\u00b6\3\2\2\2\u00b9\u00bc\3\2\2\2\u00ba\u00b8\3\2\2\2\u00ba\u00bb\3\2"+
		"\2\2\u00bb\31\3\2\2\2\u00bc\u00ba\3\2\2\2\u00bd\u00c2\7E\2\2\u00be\u00bf"+
		"\7\b\2\2\u00bf\u00c1\7E\2\2\u00c0\u00be\3\2\2\2\u00c1\u00c4\3\2\2\2\u00c2"+
		"\u00c0\3\2\2\2\u00c2\u00c3\3\2\2\2\u00c3\u00c5\3\2\2\2\u00c4\u00c2\3\2"+
		"\2\2\u00c5\u00c6\7\23\2\2\u00c6\u00c7\5\66\34\2\u00c7\33\3\2\2\2\u00c8"+
		"\u00c9\7\27\2\2\u00c9\u00ca\5\"\22\2\u00ca\u00cb\7\5\2\2\u00cb\35\3\2"+
		"\2\2\u00cc\u00cd\b\20\1\2\u00cd\u00ce\7\25\2\2\u00ce\u00cf\5\36\20\2\u00cf"+
		"\u00d0\7\26\2\2\u00d0\u00db\3\2\2\2\u00d1\u00db\5*\26\2\u00d2\u00db\5"+
		" \21\2\u00d3\u00d4\5 \21\2\u00d4\u00d6\7\25\2\2\u00d5\u00d7\5\30\r\2\u00d6"+
		"\u00d5\3\2\2\2\u00d6\u00d7\3\2\2\2\u00d7\u00d8\3\2\2\2\u00d8\u00d9\7\26"+
		"\2\2\u00d9\u00db\3\2\2\2\u00da\u00cc\3\2\2\2\u00da\u00d1\3\2\2\2\u00da"+
		"\u00d2\3\2\2\2\u00da\u00d3\3\2\2\2\u00db\u00ef\3\2\2\2\u00dc\u00dd\f\5"+
		"\2\2\u00dd\u00de\t\2\2\2\u00de\u00ee\5\36\20\6\u00df\u00e0\f\4\2\2\u00e0"+
		"\u00e1\t\3\2\2\u00e1\u00ee\5\36\20\5\u00e2\u00e3\f\3\2\2\u00e3\u00e4\t"+
		"\4\2\2\u00e4\u00ee\5\36\20\4\u00e5\u00e6\f\7\2\2\u00e6\u00e7\7\6\2\2\u00e7"+
		"\u00ee\7E\2\2\u00e8\u00e9\f\6\2\2\u00e9\u00ea\7\30\2\2\u00ea\u00eb\5\36"+
		"\20\2\u00eb\u00ec\7\31\2\2\u00ec\u00ee\3\2\2\2\u00ed\u00dc\3\2\2\2\u00ed"+
		"\u00df\3\2\2\2\u00ed\u00e2\3\2\2\2\u00ed\u00e5\3\2\2\2\u00ed\u00e8\3\2"+
		"\2\2\u00ee\u00f1\3\2\2\2\u00ef\u00ed\3\2\2\2\u00ef\u00f0\3\2\2\2\u00f0"+
		"\37\3\2\2\2\u00f1\u00ef\3\2\2\2\u00f2\u00f3\7E\2\2\u00f3\u00f5\7%\2\2"+
		"\u00f4\u00f2\3\2\2\2\u00f4\u00f5\3\2\2\2\u00f5\u00f6\3\2\2\2\u00f6\u00f7"+
		"\7E\2\2\u00f7!\3\2\2\2\u00f8\u00f9\b\22\1\2\u00f9\u00fa\7E\2\2\u00fa\u00fb"+
		"\7\t\2\2\u00fb\u0167\5\36\20\2\u00fc\u00fd\5$\23\2\u00fd\u00fe\7\t\2\2"+
		"\u00fe\u00ff\5\36\20\2\u00ff\u0167\3\2\2\2\u0100\u0101\7&\2\2\u0101\u0102"+
		"\7\25\2\2\u0102\u0103\7E\2\2\u0103\u0167\7\26\2\2\u0104\u0105\7\'\2\2"+
		"\u0105\u0106\7\25\2\2\u0106\u0107\7E\2\2\u0107\u0167\7\26\2\2\u0108\u0109"+
		"\7(\2\2\u0109\u010a\7\25\2\2\u010a\u010b\7E\2\2\u010b\u0167\7\26\2\2\u010c"+
		"\u010d\7)\2\2\u010d\u010e\7\25\2\2\u010e\u010f\5\36\20\2\u010f\u0110\7"+
		"\26\2\2\u0110\u0167\3\2\2\2\u0111\u0112\7E\2\2\u0112\u0114\7\25\2\2\u0113"+
		"\u0115\5\30\r\2\u0114\u0113\3\2\2\2\u0114\u0115\3\2\2\2\u0115\u0116\3"+
		"\2\2\2\u0116\u0167\7\26\2\2\u0117\u0118\7*\2\2\u0118\u0119\5\36\20\2\u0119"+
		"\u011a\7+\2\2\u011a\u011d\5\"\22\2\u011b\u011c\7,\2\2\u011c\u011e\5\""+
		"\22\2\u011d\u011b\3\2\2\2\u011d\u011e\3\2\2\2\u011e\u011f\3\2\2\2\u011f"+
		"\u0120\7\5\2\2\u0120\u0167\3\2\2\2\u0121\u0122\7*\2\2\u0122\u0123\5\36"+
		"\20\2\u0123\u0124\7+\2\2\u0124\u0127\5\"\22\2\u0125\u0126\7-\2\2\u0126"+
		"\u0128\5(\25\2\u0127\u0125\3\2\2\2\u0128\u0129\3\2\2\2\u0129\u0127\3\2"+
		"\2\2\u0129\u012a\3\2\2\2\u012a\u012d\3\2\2\2\u012b\u012c\7,\2\2\u012c"+
		"\u012e\5\"\22\2\u012d\u012b\3\2\2\2\u012d\u012e\3\2\2\2\u012e\u012f\3"+
		"\2\2\2\u012f\u0130\7\5\2\2\u0130\u0167\3\2\2\2\u0131\u0132\7.\2\2\u0132"+
		"\u0133\5\36\20\2\u0133\u0134\7/\2\2\u0134\u0135\5\"\22\2\u0135\u0136\7"+
		"\5\2\2\u0136\u0167\3\2\2\2\u0137\u0138\7\60\2\2\u0138\u0139\5\"\22\2\u0139"+
		"\u013a\7\61\2\2\u013a\u013b\5\36\20\2\u013b\u0167\3\2\2\2\u013c\u013d"+
		"\7\62\2\2\u013d\u013e\5\"\22\2\u013e\u013f\7\22\2\2\u013f\u0140\5\36\20"+
		"\2\u0140\u0141\7/\2\2\u0141\u0142\5\"\22\2\u0142\u0143\7\5\2\2\u0143\u0167"+
		"\3\2\2\2\u0144\u0145\7\62\2\2\u0145\u0146\7E\2\2\u0146\u0147\7\63\2\2"+
		"\u0147\u0148\5\36\20\2\u0148\u0149\7\64\2\2\u0149\u014a\5\36\20\2\u014a"+
		"\u014b\7/\2\2\u014b\u014c\5\"\22\2\u014c\u014d\7\5\2\2\u014d\u0167\3\2"+
		"\2\2\u014e\u014f\7\65\2\2\u014f\u0150\5\"\22\2\u0150\u0151\7\5\2\2\u0151"+
		"\u0167\3\2\2\2\u0152\u0153\7\66\2\2\u0153\u0167\5\36\20\2\u0154\u0155"+
		"\7\67\2\2\u0155\u0156\5\36\20\2\u0156\u0157\7\17\2\2\u0157\u015c\5&\24"+
		"\2\u0158\u0159\78\2\2\u0159\u015b\5&\24\2\u015a\u0158\3\2\2\2\u015b\u015e"+
		"\3\2\2\2\u015c\u015a\3\2\2\2\u015c\u015d\3\2\2\2\u015d\u0161\3\2\2\2\u015e"+
		"\u015c\3\2\2\2\u015f\u0160\7,\2\2\u0160\u0162\5\"\22\2\u0161\u015f\3\2"+
		"\2\2\u0161\u0162\3\2\2\2\u0162\u0163\3\2\2\2\u0163\u0164\7\5\2\2\u0164"+
		"\u0167\3\2\2\2\u0165\u0167\79\2\2\u0166\u00f8\3\2\2\2\u0166\u00fc\3\2"+
		"\2\2\u0166\u0100\3\2\2\2\u0166\u0104\3\2\2\2\u0166\u0108\3\2\2\2\u0166"+
		"\u010c\3\2\2\2\u0166\u0111\3\2\2\2\u0166\u0117\3\2\2\2\u0166\u0121\3\2"+
		"\2\2\u0166\u0131\3\2\2\2\u0166\u0137\3\2\2\2\u0166\u013c\3\2\2\2\u0166"+
		"\u0144\3\2\2\2\u0166\u014e\3\2\2\2\u0166\u0152\3\2\2\2\u0166\u0154\3\2"+
		"\2\2\u0166\u0165\3\2\2\2\u0167\u0171\3\2\2\2\u0168\u016b\f\22\2\2\u0169"+
		"\u016a\7\4\2\2\u016a\u016c\5\"\22\2\u016b\u0169\3\2\2\2\u016c\u016d\3"+
		"\2\2\2\u016d\u016b\3\2\2\2\u016d\u016e\3\2\2\2\u016e\u0170\3\2\2\2\u016f"+
		"\u0168\3\2\2\2\u0170\u0173\3\2\2\2\u0171\u016f\3\2\2\2\u0171\u0172\3\2"+
		"\2\2\u0172#\3\2\2\2\u0173\u0171\3\2\2\2\u0174\u017f\7E\2\2\u0175\u0176"+
		"\5\36\20\2\u0176\u0177\7\30\2\2\u0177\u0178\5\36\20\2\u0178\u0179\7\31"+
		"\2\2\u0179\u017f\3\2\2\2\u017a\u017b\5\36\20\2\u017b\u017c\7\6\2\2\u017c"+
		"\u017d\7E\2\2\u017d\u017f\3\2\2\2\u017e\u0174\3\2\2\2\u017e\u0175\3\2"+
		"\2\2\u017e\u017a\3\2\2\2\u017f%\3\2\2\2\u0180\u0181\5\36\20\2\u0181\u0182"+
		"\7\23\2\2\u0182\u0183\5\"\22\2\u0183\u018b\3\2\2\2\u0184\u0185\5\36\20"+
		"\2\u0185\u0186\7\64\2\2\u0186\u0187\5\36\20\2\u0187\u0188\7\23\2\2\u0188"+
		"\u0189\5\"\22\2\u0189\u018b\3\2\2\2\u018a\u0180\3\2\2\2\u018a\u0184\3"+
		"\2\2\2\u018b\'\3\2\2\2\u018c\u018d\5\36\20\2\u018d\u018e\7+\2\2\u018e"+
		"\u018f\5\"\22\2\u018f)\3\2\2\2\u0190\u0196\5,\27\2\u0191\u0196\5.\30\2"+
		"\u0192\u0196\5\60\31\2\u0193\u0196\5\62\32\2\u0194\u0196\5\64\33\2\u0195"+
		"\u0190\3\2\2\2\u0195\u0191\3\2\2\2\u0195\u0192\3\2\2\2\u0195\u0193\3\2"+
		"\2\2\u0195\u0194\3\2\2\2\u0196+\3\2\2\2\u0197\u0198\7?\2\2\u0198-\3\2"+
		"\2\2\u0199\u019a\7@\2\2\u019a/\3\2\2\2\u019b\u019c\7A\2\2\u019c\61\3\2"+
		"\2\2\u019d\u019e\7D\2\2\u019e\63\3\2\2\2\u019f\u01a0\t\5\2\2\u01a0\65"+
		"\3\2\2\2\u01a1\u01a9\7:\2\2\u01a2\u01a9\7;\2\2\u01a3\u01a9\7<\2\2\u01a4"+
		"\u01a9\7=\2\2\u01a5\u01a9\7>\2\2\u01a6\u01a9\5\16\b\2\u01a7\u01a9\7E\2"+
		"\2\u01a8\u01a1\3\2\2\2\u01a8\u01a2\3\2\2\2\u01a8\u01a3\3\2\2\2\u01a8\u01a4"+
		"\3\2\2\2\u01a8\u01a5\3\2\2\2\u01a8\u01a6\3\2\2\2\u01a8\u01a7\3\2\2\2\u01a9"+
		"\67\3\2\2\2\u01aa\u01b0\5\22\n\2\u01ab\u01b0\5\20\t\2\u01ac\u01b0\5\36"+
		"\20\2\u01ad\u01b0\5\"\22\2\u01ae\u01b0\5\f\7\2\u01af\u01aa\3\2\2\2\u01af"+
		"\u01ab\3\2\2\2\u01af\u01ac\3\2\2\2\u01af\u01ad\3\2\2\2\u01af\u01ae\3\2"+
		"\2\2\u01b09\3\2\2\2(>BQW]_egmot\u0083\u008a\u0096\u00a1\u00a6\u00b2\u00ba"+
		"\u00c2\u00d6\u00da\u00ed\u00ef\u00f4\u0114\u011d\u0129\u012d\u015c\u0161"+
		"\u0166\u016d\u0171\u017e\u018a\u0195\u01a8\u01af";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}