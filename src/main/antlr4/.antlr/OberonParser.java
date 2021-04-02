// Generated from /home/pedro/Desktop/UnB/TP2/Oberon-Scala/src/main/antlr4/Oberon.g4 by ANTLR 4.8
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
		T__52=53, INT=54, TRUE=55, FALSE=56, Id=57, WS=58, COMMENT=59, LINE_COMMENT=60;
	public static final int
		RULE_compilationUnit = 0, RULE_declarations = 1, RULE_userTypeDeclaration = 2, 
		RULE_constant = 3, RULE_varDeclaration = 4, RULE_ffi = 5, RULE_procedure = 6, 
		RULE_formals = 7, RULE_arguments = 8, RULE_formalArg = 9, RULE_block = 10, 
		RULE_expression = 11, RULE_statement = 12, RULE_designator = 13, RULE_caseAlternative = 14, 
		RULE_elseIfStmt = 15, RULE_intValue = 16, RULE_boolValue = 17, RULE_oberonType = 18;
	private static String[] makeRuleNames() {
		return new String[] {
			"compilationUnit", "declarations", "userTypeDeclaration", "constant", 
			"varDeclaration", "ffi", "procedure", "formals", "arguments", "formalArg", 
			"block", "expression", "statement", "designator", "caseAlternative", 
			"elseIfStmt", "intValue", "boolValue", "oberonType"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'MODULE'", "';'", "'END'", "'.'", "'TYPE'", "'CONST'", "'VAR'", 
			"'='", "'ARRAY'", "'OF'", "'RECORD'", "','", "':'", "'FOREIGN IMPORT'", 
			"'('", "')'", "'PROCEDURE'", "'BEGIN'", "'['", "']'", "'#'", "'<'", "'<='", 
			"'>'", "'>='", "'*'", "'/'", "'&&'", "'+'", "'-'", "'||'", "':='", "'readInt'", 
			"'write'", "'IF'", "'THEN'", "'ELSE'", "'ELSIF'", "'WHILE'", "'DO'", 
			"'REPEAT'", "'UNTIL'", "'FOR'", "'TO'", "'IN'", "'..'", "'LOOP'", "'RETURN'", 
			"'CASE'", "'|'", "'EXIT'", "'INTEGER'", "'BOOLEAN'", null, "'True'", 
			"'False'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, "INT", "TRUE", "FALSE", "Id", "WS", 
			"COMMENT", "LINE_COMMENT"
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
			setState(38);
			match(T__0);
			setState(39);
			((CompilationUnitContext)_localctx).name = match(Id);
			setState(40);
			match(T__1);
			setState(41);
			declarations();
			setState(43);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__17) {
				{
				setState(42);
				block();
				}
			}

			setState(45);
			match(T__2);
			setState(46);
			match(Id);
			setState(47);
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
		enterRule(_localctx, 2, RULE_declarations);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(55);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__4) {
				{
				setState(49);
				match(T__4);
				setState(51); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(50);
					userTypeDeclaration();
					}
					}
					setState(53); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==Id );
				}
			}

			setState(63);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__5) {
				{
				setState(57);
				match(T__5);
				setState(59); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(58);
					constant();
					}
					}
					setState(61); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==Id );
				}
			}

			setState(71);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__6) {
				{
				setState(65);
				match(T__6);
				setState(67); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(66);
					varDeclaration();
					}
					}
					setState(69); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==Id );
				}
			}

			setState(76);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__16) {
				{
				{
				setState(73);
				procedure();
				}
				}
				setState(78);
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
		public UserTypeDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_userTypeDeclaration; }
	 
		public UserTypeDeclarationContext() { }
		public void copyFrom(UserTypeDeclarationContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class RecordTypeDeclarationContext extends UserTypeDeclarationContext {
		public Token nameType;
		public VarDeclarationContext varDeclaration;
		public List<VarDeclarationContext> vars = new ArrayList<VarDeclarationContext>();
		public TerminalNode Id() { return getToken(OberonParser.Id, 0); }
		public List<VarDeclarationContext> varDeclaration() {
			return getRuleContexts(VarDeclarationContext.class);
		}
		public VarDeclarationContext varDeclaration(int i) {
			return getRuleContext(VarDeclarationContext.class,i);
		}
		public RecordTypeDeclarationContext(UserTypeDeclarationContext ctx) { copyFrom(ctx); }
	}
	public static class ArrayTypeDeclarationContext extends UserTypeDeclarationContext {
		public Token nameType;
		public Token length;
		public OberonTypeContext vartype;
		public TerminalNode Id() { return getToken(OberonParser.Id, 0); }
		public TerminalNode INT() { return getToken(OberonParser.INT, 0); }
		public OberonTypeContext oberonType() {
			return getRuleContext(OberonTypeContext.class,0);
		}
		public ArrayTypeDeclarationContext(UserTypeDeclarationContext ctx) { copyFrom(ctx); }
	}

	public final UserTypeDeclarationContext userTypeDeclaration() throws RecognitionException {
		UserTypeDeclarationContext _localctx = new UserTypeDeclarationContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_userTypeDeclaration);
		int _la;
		try {
			setState(95);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				_localctx = new ArrayTypeDeclarationContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(79);
				((ArrayTypeDeclarationContext)_localctx).nameType = match(Id);
				setState(80);
				match(T__7);
				{
				setState(81);
				match(T__8);
				setState(82);
				((ArrayTypeDeclarationContext)_localctx).length = match(INT);
				setState(83);
				match(T__9);
				setState(84);
				((ArrayTypeDeclarationContext)_localctx).vartype = oberonType();
				}
				}
				break;
			case 2:
				_localctx = new RecordTypeDeclarationContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(85);
				((RecordTypeDeclarationContext)_localctx).nameType = match(Id);
				setState(86);
				match(T__7);
				{
				setState(87);
				match(T__10);
				setState(89); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(88);
					((RecordTypeDeclarationContext)_localctx).varDeclaration = varDeclaration();
					((RecordTypeDeclarationContext)_localctx).vars.add(((RecordTypeDeclarationContext)_localctx).varDeclaration);
					}
					}
					setState(91); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==Id );
				setState(93);
				match(T__2);
				}
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
		enterRule(_localctx, 6, RULE_constant);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(97);
			((ConstantContext)_localctx).constName = match(Id);
			setState(98);
			match(T__7);
			setState(99);
			((ConstantContext)_localctx).exp = expression(0);
			setState(100);
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
		enterRule(_localctx, 8, RULE_varDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(102);
			((VarDeclarationContext)_localctx).Id = match(Id);
			((VarDeclarationContext)_localctx).vars.add(((VarDeclarationContext)_localctx).Id);
			setState(107);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__11) {
				{
				{
				setState(103);
				match(T__11);
				setState(104);
				((VarDeclarationContext)_localctx).Id = match(Id);
				((VarDeclarationContext)_localctx).vars.add(((VarDeclarationContext)_localctx).Id);
				}
				}
				setState(109);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
			setState(110);
			match(T__12);
			setState(111);
			((VarDeclarationContext)_localctx).varType = oberonType();
			setState(112);
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

	public static class FfiContext extends ParserRuleContext {
		public Token name;
		public OberonTypeContext procedureType;
		public TerminalNode Id() { return getToken(OberonParser.Id, 0); }
		public FormalsContext formals() {
			return getRuleContext(FormalsContext.class,0);
		}
		public OberonTypeContext oberonType() {
			return getRuleContext(OberonTypeContext.class,0);
		}
		public FfiContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ffi; }
	}

	public final FfiContext ffi() throws RecognitionException {
		FfiContext _localctx = new FfiContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_ffi);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(114);
			match(T__13);
			setState(115);
			((FfiContext)_localctx).name = match(Id);
			setState(116);
			match(T__14);
			setState(118);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Id) {
				{
				setState(117);
				formals();
				}
			}

			setState(120);
			match(T__15);
			setState(123);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__12) {
				{
				setState(121);
				match(T__12);
				setState(122);
				((FfiContext)_localctx).procedureType = oberonType();
				}
			}

			setState(125);
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
		enterRule(_localctx, 12, RULE_procedure);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(127);
			match(T__16);
			setState(128);
			((ProcedureContext)_localctx).name = match(Id);
			setState(129);
			match(T__14);
			setState(131);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Id) {
				{
				setState(130);
				formals();
				}
			}

			setState(133);
			match(T__15);
			setState(136);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__12) {
				{
				setState(134);
				match(T__12);
				setState(135);
				((ProcedureContext)_localctx).procedureType = oberonType();
				}
			}

			setState(138);
			match(T__1);
			setState(139);
			declarations();
			setState(140);
			block();
			setState(141);
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
		enterRule(_localctx, 14, RULE_formals);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(143);
			formalArg();
			setState(148);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__11) {
				{
				{
				setState(144);
				match(T__11);
				setState(145);
				formalArg();
				}
				}
				setState(150);
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
		enterRule(_localctx, 16, RULE_arguments);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(151);
			expression(0);
			setState(156);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__11) {
				{
				{
				setState(152);
				match(T__11);
				setState(153);
				expression(0);
				}
				}
				setState(158);
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
		enterRule(_localctx, 18, RULE_formalArg);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(159);
			((FormalArgContext)_localctx).Id = match(Id);
			((FormalArgContext)_localctx).args.add(((FormalArgContext)_localctx).Id);
			setState(164);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__11) {
				{
				{
				setState(160);
				match(T__11);
				setState(161);
				((FormalArgContext)_localctx).Id = match(Id);
				((FormalArgContext)_localctx).args.add(((FormalArgContext)_localctx).Id);
				}
				}
				setState(166);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
			setState(167);
			match(T__12);
			setState(168);
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
		enterRule(_localctx, 20, RULE_block);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(170);
			match(T__17);
			setState(171);
			statement(0);
			setState(172);
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
		public Token name;
		public TerminalNode Id() { return getToken(OberonParser.Id, 0); }
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
	public static class IntegerValueContext extends ExpressionContext {
		public IntValueContext intValue() {
			return getRuleContext(IntValueContext.class,0);
		}
		public IntegerValueContext(ExpressionContext ctx) { copyFrom(ctx); }
	}
	public static class BooleanValueContext extends ExpressionContext {
		public BoolValueContext boolValue() {
			return getRuleContext(BoolValueContext.class,0);
		}
		public BooleanValueContext(ExpressionContext ctx) { copyFrom(ctx); }
	}
	public static class FunctionCallContext extends ExpressionContext {
		public Token name;
		public TerminalNode Id() { return getToken(OberonParser.Id, 0); }
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
		int _startState = 22;
		enterRecursionRule(_localctx, 22, RULE_expression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(188);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
			case 1:
				{
				_localctx = new BracketsContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(175);
				match(T__14);
				setState(176);
				expression(0);
				setState(177);
				match(T__15);
				}
				break;
			case 2:
				{
				_localctx = new IntegerValueContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(179);
				intValue();
				}
				break;
			case 3:
				{
				_localctx = new BooleanValueContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(180);
				boolValue();
				}
				break;
			case 4:
				{
				_localctx = new VariableContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(181);
				((VariableContext)_localctx).name = match(Id);
				}
				break;
			case 5:
				{
				_localctx = new FunctionCallContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(182);
				((FunctionCallContext)_localctx).name = match(Id);
				setState(183);
				match(T__14);
				setState(185);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__14) | (1L << INT) | (1L << TRUE) | (1L << FALSE) | (1L << Id))) != 0)) {
					{
					setState(184);
					arguments();
					}
				}

				setState(187);
				match(T__15);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(209);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,21,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(207);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,20,_ctx) ) {
					case 1:
						{
						_localctx = new RelExpressionContext(new ExpressionContext(_parentctx, _parentState));
						((RelExpressionContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(190);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(191);
						((RelExpressionContext)_localctx).opr = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__7) | (1L << T__20) | (1L << T__21) | (1L << T__22) | (1L << T__23) | (1L << T__24))) != 0)) ) {
							((RelExpressionContext)_localctx).opr = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(192);
						((RelExpressionContext)_localctx).right = expression(4);
						}
						break;
					case 2:
						{
						_localctx = new MultExpressionContext(new ExpressionContext(_parentctx, _parentState));
						((MultExpressionContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(193);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(194);
						((MultExpressionContext)_localctx).opr = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__25) | (1L << T__26) | (1L << T__27))) != 0)) ) {
							((MultExpressionContext)_localctx).opr = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(195);
						((MultExpressionContext)_localctx).right = expression(3);
						}
						break;
					case 3:
						{
						_localctx = new AddExpressionContext(new ExpressionContext(_parentctx, _parentState));
						((AddExpressionContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(196);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(197);
						((AddExpressionContext)_localctx).opr = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__28) | (1L << T__29) | (1L << T__30))) != 0)) ) {
							((AddExpressionContext)_localctx).opr = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(198);
						((AddExpressionContext)_localctx).right = expression(2);
						}
						break;
					case 4:
						{
						_localctx = new FieldAccessContext(new ExpressionContext(_parentctx, _parentState));
						((FieldAccessContext)_localctx).exp = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(199);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(200);
						match(T__3);
						setState(201);
						((FieldAccessContext)_localctx).name = match(Id);
						}
						break;
					case 5:
						{
						_localctx = new ArraySubscriptContext(new ExpressionContext(_parentctx, _parentState));
						((ArraySubscriptContext)_localctx).arrayBase = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(202);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(203);
						match(T__18);
						setState(204);
						((ArraySubscriptContext)_localctx).index = expression(0);
						setState(205);
						match(T__19);
						}
						break;
					}
					} 
				}
				setState(211);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,21,_ctx);
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
		int _startState = 24;
		enterRecursionRule(_localctx, 24, RULE_statement, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(314);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,28,_ctx) ) {
			case 1:
				{
				_localctx = new AssignmentStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(213);
				((AssignmentStmtContext)_localctx).var = match(Id);
				setState(214);
				match(T__31);
				setState(215);
				((AssignmentStmtContext)_localctx).exp = expression(0);
				}
				break;
			case 2:
				{
				_localctx = new EAssignmentStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(216);
				((EAssignmentStmtContext)_localctx).des = designator();
				setState(217);
				match(T__31);
				setState(218);
				((EAssignmentStmtContext)_localctx).exp = expression(0);
				}
				break;
			case 3:
				{
				_localctx = new ReadIntStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(220);
				match(T__32);
				setState(221);
				match(T__14);
				setState(222);
				((ReadIntStmtContext)_localctx).var = match(Id);
				setState(223);
				match(T__15);
				}
				break;
			case 4:
				{
				_localctx = new WriteStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(224);
				match(T__33);
				setState(225);
				match(T__14);
				setState(226);
				expression(0);
				setState(227);
				match(T__15);
				}
				break;
			case 5:
				{
				_localctx = new ProcedureCallContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(229);
				((ProcedureCallContext)_localctx).name = match(Id);
				setState(230);
				match(T__14);
				setState(232);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__14) | (1L << INT) | (1L << TRUE) | (1L << FALSE) | (1L << Id))) != 0)) {
					{
					setState(231);
					arguments();
					}
				}

				setState(234);
				match(T__15);
				}
				break;
			case 6:
				{
				_localctx = new IfElseStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(235);
				match(T__34);
				setState(236);
				((IfElseStmtContext)_localctx).cond = expression(0);
				setState(237);
				match(T__35);
				setState(238);
				((IfElseStmtContext)_localctx).thenStmt = statement(0);
				setState(241);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__36) {
					{
					setState(239);
					match(T__36);
					setState(240);
					((IfElseStmtContext)_localctx).elseStmt = statement(0);
					}
				}

				setState(243);
				match(T__2);
				}
				break;
			case 7:
				{
				_localctx = new IfElseIfStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(245);
				match(T__34);
				setState(246);
				((IfElseIfStmtContext)_localctx).cond = expression(0);
				setState(247);
				match(T__35);
				setState(248);
				((IfElseIfStmtContext)_localctx).thenStmt = statement(0);
				setState(251); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(249);
					match(T__37);
					setState(250);
					((IfElseIfStmtContext)_localctx).elseIfStmt = elseIfStmt();
					((IfElseIfStmtContext)_localctx).elsifs.add(((IfElseIfStmtContext)_localctx).elseIfStmt);
					}
					}
					setState(253); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__37 );
				setState(257);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__36) {
					{
					setState(255);
					match(T__36);
					setState(256);
					((IfElseIfStmtContext)_localctx).elseStmt = statement(0);
					}
				}

				setState(259);
				match(T__2);
				}
				break;
			case 8:
				{
				_localctx = new WhileStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(261);
				match(T__38);
				setState(262);
				((WhileStmtContext)_localctx).cond = expression(0);
				setState(263);
				match(T__39);
				setState(264);
				((WhileStmtContext)_localctx).stmt = statement(0);
				setState(265);
				match(T__2);
				}
				break;
			case 9:
				{
				_localctx = new RepeatUntilStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(267);
				match(T__40);
				setState(268);
				((RepeatUntilStmtContext)_localctx).stmt = statement(0);
				setState(269);
				match(T__41);
				setState(270);
				((RepeatUntilStmtContext)_localctx).cond = expression(0);
				}
				break;
			case 10:
				{
				_localctx = new ForStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(272);
				match(T__42);
				setState(273);
				((ForStmtContext)_localctx).init = statement(0);
				setState(274);
				match(T__43);
				setState(275);
				((ForStmtContext)_localctx).condition = expression(0);
				setState(276);
				match(T__39);
				setState(277);
				((ForStmtContext)_localctx).stmt = statement(0);
				setState(278);
				match(T__2);
				}
				break;
			case 11:
				{
				_localctx = new ForRangeStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(280);
				match(T__42);
				setState(281);
				((ForRangeStmtContext)_localctx).var = match(Id);
				setState(282);
				match(T__44);
				setState(283);
				((ForRangeStmtContext)_localctx).min = expression(0);
				setState(284);
				match(T__45);
				setState(285);
				((ForRangeStmtContext)_localctx).max = expression(0);
				setState(286);
				match(T__39);
				setState(287);
				((ForRangeStmtContext)_localctx).stmt = statement(0);
				setState(288);
				match(T__2);
				}
				break;
			case 12:
				{
				_localctx = new LoopStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(290);
				match(T__46);
				setState(291);
				((LoopStmtContext)_localctx).stmt = statement(0);
				setState(292);
				match(T__2);
				}
				break;
			case 13:
				{
				_localctx = new ReturnStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(294);
				match(T__47);
				setState(295);
				((ReturnStmtContext)_localctx).exp = expression(0);
				}
				break;
			case 14:
				{
				_localctx = new CaseStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(296);
				match(T__48);
				setState(297);
				((CaseStmtContext)_localctx).exp = expression(0);
				setState(298);
				match(T__9);
				setState(299);
				((CaseStmtContext)_localctx).caseAlternative = caseAlternative();
				((CaseStmtContext)_localctx).cases.add(((CaseStmtContext)_localctx).caseAlternative);
				setState(304);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__49) {
					{
					{
					setState(300);
					match(T__49);
					setState(301);
					((CaseStmtContext)_localctx).caseAlternative = caseAlternative();
					((CaseStmtContext)_localctx).cases.add(((CaseStmtContext)_localctx).caseAlternative);
					}
					}
					setState(306);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(309);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__36) {
					{
					setState(307);
					match(T__36);
					setState(308);
					((CaseStmtContext)_localctx).elseStmt = statement(0);
					}
				}

				setState(311);
				match(T__2);
				}
				break;
			case 15:
				{
				_localctx = new ExitStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(313);
				match(T__50);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(325);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,30,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new SequenceStmtContext(new StatementContext(_parentctx, _parentState));
					((SequenceStmtContext)_localctx).stmt.add(_prevctx);
					pushNewRecursionContext(_localctx, _startState, RULE_statement);
					setState(316);
					if (!(precpred(_ctx, 14))) throw new FailedPredicateException(this, "precpred(_ctx, 14)");
					setState(319); 
					_errHandler.sync(this);
					_alt = 1;
					do {
						switch (_alt) {
						case 1:
							{
							{
							setState(317);
							match(T__1);
							setState(318);
							((SequenceStmtContext)_localctx).statement = statement(0);
							((SequenceStmtContext)_localctx).stmt.add(((SequenceStmtContext)_localctx).statement);
							}
							}
							break;
						default:
							throw new NoViableAltException(this);
						}
						setState(321); 
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,29,_ctx);
					} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
					}
					} 
				}
				setState(327);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,30,_ctx);
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
		enterRule(_localctx, 26, RULE_designator);
		try {
			setState(338);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,31,_ctx) ) {
			case 1:
				_localctx = new VarAssignmentContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(328);
				((VarAssignmentContext)_localctx).var = match(Id);
				}
				break;
			case 2:
				_localctx = new ArrayAssignmentContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(329);
				((ArrayAssignmentContext)_localctx).array = expression(0);
				setState(330);
				match(T__18);
				setState(331);
				((ArrayAssignmentContext)_localctx).elem = expression(0);
				setState(332);
				match(T__19);
				}
				break;
			case 3:
				_localctx = new RecordAssignmentContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(334);
				((RecordAssignmentContext)_localctx).record = expression(0);
				setState(335);
				match(T__3);
				setState(336);
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
		enterRule(_localctx, 28, RULE_caseAlternative);
		try {
			setState(350);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,32,_ctx) ) {
			case 1:
				_localctx = new SimpleCaseContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(340);
				((SimpleCaseContext)_localctx).cond = expression(0);
				setState(341);
				match(T__12);
				setState(342);
				((SimpleCaseContext)_localctx).stmt = statement(0);
				}
				break;
			case 2:
				_localctx = new RangeCaseContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(344);
				((RangeCaseContext)_localctx).min = expression(0);
				setState(345);
				match(T__45);
				setState(346);
				((RangeCaseContext)_localctx).max = expression(0);
				setState(347);
				match(T__12);
				setState(348);
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
		enterRule(_localctx, 30, RULE_elseIfStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(352);
			((ElseIfStmtContext)_localctx).cond = expression(0);
			setState(353);
			match(T__35);
			setState(354);
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

	public static class IntValueContext extends ParserRuleContext {
		public TerminalNode INT() { return getToken(OberonParser.INT, 0); }
		public IntValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_intValue; }
	}

	public final IntValueContext intValue() throws RecognitionException {
		IntValueContext _localctx = new IntValueContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_intValue);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(356);
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
		enterRule(_localctx, 34, RULE_boolValue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(358);
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
	public static class BooleanTypeContext extends OberonTypeContext {
		public BooleanTypeContext(OberonTypeContext ctx) { copyFrom(ctx); }
	}
	public static class ReferenceTypeContext extends OberonTypeContext {
		public Token name;
		public TerminalNode Id() { return getToken(OberonParser.Id, 0); }
		public ReferenceTypeContext(OberonTypeContext ctx) { copyFrom(ctx); }
	}

	public final OberonTypeContext oberonType() throws RecognitionException {
		OberonTypeContext _localctx = new OberonTypeContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_oberonType);
		try {
			setState(363);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__51:
				_localctx = new IntegerTypeContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(360);
				match(T__51);
				}
				break;
			case T__52:
				_localctx = new BooleanTypeContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(361);
				match(T__52);
				}
				break;
			case Id:
				_localctx = new ReferenceTypeContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(362);
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

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 11:
			return expression_sempred((ExpressionContext)_localctx, predIndex);
		case 12:
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
			return precpred(_ctx, 14);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3>\u0170\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\3\2\3\2\3\2\3\2\3\2\5\2.\n\2\3\2\3\2\3\2\3\2\3\3"+
		"\3\3\6\3\66\n\3\r\3\16\3\67\5\3:\n\3\3\3\3\3\6\3>\n\3\r\3\16\3?\5\3B\n"+
		"\3\3\3\3\3\6\3F\n\3\r\3\16\3G\5\3J\n\3\3\3\7\3M\n\3\f\3\16\3P\13\3\3\4"+
		"\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\6\4\\\n\4\r\4\16\4]\3\4\3\4\5\4b"+
		"\n\4\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\7\6l\n\6\f\6\16\6o\13\6\3\6\3\6\3"+
		"\6\3\6\3\7\3\7\3\7\3\7\5\7y\n\7\3\7\3\7\3\7\5\7~\n\7\3\7\3\7\3\b\3\b\3"+
		"\b\3\b\5\b\u0086\n\b\3\b\3\b\3\b\5\b\u008b\n\b\3\b\3\b\3\b\3\b\3\b\3\t"+
		"\3\t\3\t\7\t\u0095\n\t\f\t\16\t\u0098\13\t\3\n\3\n\3\n\7\n\u009d\n\n\f"+
		"\n\16\n\u00a0\13\n\3\13\3\13\3\13\7\13\u00a5\n\13\f\13\16\13\u00a8\13"+
		"\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r"+
		"\3\r\3\r\5\r\u00bc\n\r\3\r\5\r\u00bf\n\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3"+
		"\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\7\r\u00d2\n\r\f\r\16\r\u00d5\13"+
		"\r\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3"+
		"\16\3\16\3\16\3\16\3\16\3\16\3\16\5\16\u00eb\n\16\3\16\3\16\3\16\3\16"+
		"\3\16\3\16\3\16\5\16\u00f4\n\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16"+
		"\6\16\u00fe\n\16\r\16\16\16\u00ff\3\16\3\16\5\16\u0104\n\16\3\16\3\16"+
		"\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16"+
		"\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16"+
		"\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\7\16"+
		"\u0131\n\16\f\16\16\16\u0134\13\16\3\16\3\16\5\16\u0138\n\16\3\16\3\16"+
		"\3\16\5\16\u013d\n\16\3\16\3\16\3\16\6\16\u0142\n\16\r\16\16\16\u0143"+
		"\7\16\u0146\n\16\f\16\16\16\u0149\13\16\3\17\3\17\3\17\3\17\3\17\3\17"+
		"\3\17\3\17\3\17\3\17\5\17\u0155\n\17\3\20\3\20\3\20\3\20\3\20\3\20\3\20"+
		"\3\20\3\20\3\20\5\20\u0161\n\20\3\21\3\21\3\21\3\21\3\22\3\22\3\23\3\23"+
		"\3\24\3\24\3\24\5\24\u016e\n\24\3\24\2\4\30\32\25\2\4\6\b\n\f\16\20\22"+
		"\24\26\30\32\34\36 \"$&\2\6\4\2\n\n\27\33\3\2\34\36\3\2\37!\3\29:\2\u0193"+
		"\2(\3\2\2\2\49\3\2\2\2\6a\3\2\2\2\bc\3\2\2\2\nh\3\2\2\2\ft\3\2\2\2\16"+
		"\u0081\3\2\2\2\20\u0091\3\2\2\2\22\u0099\3\2\2\2\24\u00a1\3\2\2\2\26\u00ac"+
		"\3\2\2\2\30\u00be\3\2\2\2\32\u013c\3\2\2\2\34\u0154\3\2\2\2\36\u0160\3"+
		"\2\2\2 \u0162\3\2\2\2\"\u0166\3\2\2\2$\u0168\3\2\2\2&\u016d\3\2\2\2()"+
		"\7\3\2\2)*\7;\2\2*+\7\4\2\2+-\5\4\3\2,.\5\26\f\2-,\3\2\2\2-.\3\2\2\2."+
		"/\3\2\2\2/\60\7\5\2\2\60\61\7;\2\2\61\62\7\6\2\2\62\3\3\2\2\2\63\65\7"+
		"\7\2\2\64\66\5\6\4\2\65\64\3\2\2\2\66\67\3\2\2\2\67\65\3\2\2\2\678\3\2"+
		"\2\28:\3\2\2\29\63\3\2\2\29:\3\2\2\2:A\3\2\2\2;=\7\b\2\2<>\5\b\5\2=<\3"+
		"\2\2\2>?\3\2\2\2?=\3\2\2\2?@\3\2\2\2@B\3\2\2\2A;\3\2\2\2AB\3\2\2\2BI\3"+
		"\2\2\2CE\7\t\2\2DF\5\n\6\2ED\3\2\2\2FG\3\2\2\2GE\3\2\2\2GH\3\2\2\2HJ\3"+
		"\2\2\2IC\3\2\2\2IJ\3\2\2\2JN\3\2\2\2KM\5\16\b\2LK\3\2\2\2MP\3\2\2\2NL"+
		"\3\2\2\2NO\3\2\2\2O\5\3\2\2\2PN\3\2\2\2QR\7;\2\2RS\7\n\2\2ST\7\13\2\2"+
		"TU\78\2\2UV\7\f\2\2Vb\5&\24\2WX\7;\2\2XY\7\n\2\2Y[\7\r\2\2Z\\\5\n\6\2"+
		"[Z\3\2\2\2\\]\3\2\2\2][\3\2\2\2]^\3\2\2\2^_\3\2\2\2_`\7\5\2\2`b\3\2\2"+
		"\2aQ\3\2\2\2aW\3\2\2\2b\7\3\2\2\2cd\7;\2\2de\7\n\2\2ef\5\30\r\2fg\7\4"+
		"\2\2g\t\3\2\2\2hm\7;\2\2ij\7\16\2\2jl\7;\2\2ki\3\2\2\2lo\3\2\2\2mk\3\2"+
		"\2\2mn\3\2\2\2np\3\2\2\2om\3\2\2\2pq\7\17\2\2qr\5&\24\2rs\7\4\2\2s\13"+
		"\3\2\2\2tu\7\20\2\2uv\7;\2\2vx\7\21\2\2wy\5\20\t\2xw\3\2\2\2xy\3\2\2\2"+
		"yz\3\2\2\2z}\7\22\2\2{|\7\17\2\2|~\5&\24\2}{\3\2\2\2}~\3\2\2\2~\177\3"+
		"\2\2\2\177\u0080\7\4\2\2\u0080\r\3\2\2\2\u0081\u0082\7\23\2\2\u0082\u0083"+
		"\7;\2\2\u0083\u0085\7\21\2\2\u0084\u0086\5\20\t\2\u0085\u0084\3\2\2\2"+
		"\u0085\u0086\3\2\2\2\u0086\u0087\3\2\2\2\u0087\u008a\7\22\2\2\u0088\u0089"+
		"\7\17\2\2\u0089\u008b\5&\24\2\u008a\u0088\3\2\2\2\u008a\u008b\3\2\2\2"+
		"\u008b\u008c\3\2\2\2\u008c\u008d\7\4\2\2\u008d\u008e\5\4\3\2\u008e\u008f"+
		"\5\26\f\2\u008f\u0090\7;\2\2\u0090\17\3\2\2\2\u0091\u0096\5\24\13\2\u0092"+
		"\u0093\7\16\2\2\u0093\u0095\5\24\13\2\u0094\u0092\3\2\2\2\u0095\u0098"+
		"\3\2\2\2\u0096\u0094\3\2\2\2\u0096\u0097\3\2\2\2\u0097\21\3\2\2\2\u0098"+
		"\u0096\3\2\2\2\u0099\u009e\5\30\r\2\u009a\u009b\7\16\2\2\u009b\u009d\5"+
		"\30\r\2\u009c\u009a\3\2\2\2\u009d\u00a0\3\2\2\2\u009e\u009c\3\2\2\2\u009e"+
		"\u009f\3\2\2\2\u009f\23\3\2\2\2\u00a0\u009e\3\2\2\2\u00a1\u00a6\7;\2\2"+
		"\u00a2\u00a3\7\16\2\2\u00a3\u00a5\7;\2\2\u00a4\u00a2\3\2\2\2\u00a5\u00a8"+
		"\3\2\2\2\u00a6\u00a4\3\2\2\2\u00a6\u00a7\3\2\2\2\u00a7\u00a9\3\2\2\2\u00a8"+
		"\u00a6\3\2\2\2\u00a9\u00aa\7\17\2\2\u00aa\u00ab\5&\24\2\u00ab\25\3\2\2"+
		"\2\u00ac\u00ad\7\24\2\2\u00ad\u00ae\5\32\16\2\u00ae\u00af\7\5\2\2\u00af"+
		"\27\3\2\2\2\u00b0\u00b1\b\r\1\2\u00b1\u00b2\7\21\2\2\u00b2\u00b3\5\30"+
		"\r\2\u00b3\u00b4\7\22\2\2\u00b4\u00bf\3\2\2\2\u00b5\u00bf\5\"\22\2\u00b6"+
		"\u00bf\5$\23\2\u00b7\u00bf\7;\2\2\u00b8\u00b9\7;\2\2\u00b9\u00bb\7\21"+
		"\2\2\u00ba\u00bc\5\22\n\2\u00bb\u00ba\3\2\2\2\u00bb\u00bc\3\2\2\2\u00bc"+
		"\u00bd\3\2\2\2\u00bd\u00bf\7\22\2\2\u00be\u00b0\3\2\2\2\u00be\u00b5\3"+
		"\2\2\2\u00be\u00b6\3\2\2\2\u00be\u00b7\3\2\2\2\u00be\u00b8\3\2\2\2\u00bf"+
		"\u00d3\3\2\2\2\u00c0\u00c1\f\5\2\2\u00c1\u00c2\t\2\2\2\u00c2\u00d2\5\30"+
		"\r\6\u00c3\u00c4\f\4\2\2\u00c4\u00c5\t\3\2\2\u00c5\u00d2\5\30\r\5\u00c6"+
		"\u00c7\f\3\2\2\u00c7\u00c8\t\4\2\2\u00c8\u00d2\5\30\r\4\u00c9\u00ca\f"+
		"\7\2\2\u00ca\u00cb\7\6\2\2\u00cb\u00d2\7;\2\2\u00cc\u00cd\f\6\2\2\u00cd"+
		"\u00ce\7\25\2\2\u00ce\u00cf\5\30\r\2\u00cf\u00d0\7\26\2\2\u00d0\u00d2"+
		"\3\2\2\2\u00d1\u00c0\3\2\2\2\u00d1\u00c3\3\2\2\2\u00d1\u00c6\3\2\2\2\u00d1"+
		"\u00c9\3\2\2\2\u00d1\u00cc\3\2\2\2\u00d2\u00d5\3\2\2\2\u00d3\u00d1\3\2"+
		"\2\2\u00d3\u00d4\3\2\2\2\u00d4\31\3\2\2\2\u00d5\u00d3\3\2\2\2\u00d6\u00d7"+
		"\b\16\1\2\u00d7\u00d8\7;\2\2\u00d8\u00d9\7\"\2\2\u00d9\u013d\5\30\r\2"+
		"\u00da\u00db\5\34\17\2\u00db\u00dc\7\"\2\2\u00dc\u00dd\5\30\r\2\u00dd"+
		"\u013d\3\2\2\2\u00de\u00df\7#\2\2\u00df\u00e0\7\21\2\2\u00e0\u00e1\7;"+
		"\2\2\u00e1\u013d\7\22\2\2\u00e2\u00e3\7$\2\2\u00e3\u00e4\7\21\2\2\u00e4"+
		"\u00e5\5\30\r\2\u00e5\u00e6\7\22\2\2\u00e6\u013d\3\2\2\2\u00e7\u00e8\7"+
		";\2\2\u00e8\u00ea\7\21\2\2\u00e9\u00eb\5\22\n\2\u00ea\u00e9\3\2\2\2\u00ea"+
		"\u00eb\3\2\2\2\u00eb\u00ec\3\2\2\2\u00ec\u013d\7\22\2\2\u00ed\u00ee\7"+
		"%\2\2\u00ee\u00ef\5\30\r\2\u00ef\u00f0\7&\2\2\u00f0\u00f3\5\32\16\2\u00f1"+
		"\u00f2\7\'\2\2\u00f2\u00f4\5\32\16\2\u00f3\u00f1\3\2\2\2\u00f3\u00f4\3"+
		"\2\2\2\u00f4\u00f5\3\2\2\2\u00f5\u00f6\7\5\2\2\u00f6\u013d\3\2\2\2\u00f7"+
		"\u00f8\7%\2\2\u00f8\u00f9\5\30\r\2\u00f9\u00fa\7&\2\2\u00fa\u00fd\5\32"+
		"\16\2\u00fb\u00fc\7(\2\2\u00fc\u00fe\5 \21\2\u00fd\u00fb\3\2\2\2\u00fe"+
		"\u00ff\3\2\2\2\u00ff\u00fd\3\2\2\2\u00ff\u0100\3\2\2\2\u0100\u0103\3\2"+
		"\2\2\u0101\u0102\7\'\2\2\u0102\u0104\5\32\16\2\u0103\u0101\3\2\2\2\u0103"+
		"\u0104\3\2\2\2\u0104\u0105\3\2\2\2\u0105\u0106\7\5\2\2\u0106\u013d\3\2"+
		"\2\2\u0107\u0108\7)\2\2\u0108\u0109\5\30\r\2\u0109\u010a\7*\2\2\u010a"+
		"\u010b\5\32\16\2\u010b\u010c\7\5\2\2\u010c\u013d\3\2\2\2\u010d\u010e\7"+
		"+\2\2\u010e\u010f\5\32\16\2\u010f\u0110\7,\2\2\u0110\u0111\5\30\r\2\u0111"+
		"\u013d\3\2\2\2\u0112\u0113\7-\2\2\u0113\u0114\5\32\16\2\u0114\u0115\7"+
		".\2\2\u0115\u0116\5\30\r\2\u0116\u0117\7*\2\2\u0117\u0118\5\32\16\2\u0118"+
		"\u0119\7\5\2\2\u0119\u013d\3\2\2\2\u011a\u011b\7-\2\2\u011b\u011c\7;\2"+
		"\2\u011c\u011d\7/\2\2\u011d\u011e\5\30\r\2\u011e\u011f\7\60\2\2\u011f"+
		"\u0120\5\30\r\2\u0120\u0121\7*\2\2\u0121\u0122\5\32\16\2\u0122\u0123\7"+
		"\5\2\2\u0123\u013d\3\2\2\2\u0124\u0125\7\61\2\2\u0125\u0126\5\32\16\2"+
		"\u0126\u0127\7\5\2\2\u0127\u013d\3\2\2\2\u0128\u0129\7\62\2\2\u0129\u013d"+
		"\5\30\r\2\u012a\u012b\7\63\2\2\u012b\u012c\5\30\r\2\u012c\u012d\7\f\2"+
		"\2\u012d\u0132\5\36\20\2\u012e\u012f\7\64\2\2\u012f\u0131\5\36\20\2\u0130"+
		"\u012e\3\2\2\2\u0131\u0134\3\2\2\2\u0132\u0130\3\2\2\2\u0132\u0133\3\2"+
		"\2\2\u0133\u0137\3\2\2\2\u0134\u0132\3\2\2\2\u0135\u0136\7\'\2\2\u0136"+
		"\u0138\5\32\16\2\u0137\u0135\3\2\2\2\u0137\u0138\3\2\2\2\u0138\u0139\3"+
		"\2\2\2\u0139\u013a\7\5\2\2\u013a\u013d\3\2\2\2\u013b\u013d\7\65\2\2\u013c"+
		"\u00d6\3\2\2\2\u013c\u00da\3\2\2\2\u013c\u00de\3\2\2\2\u013c\u00e2\3\2"+
		"\2\2\u013c\u00e7\3\2\2\2\u013c\u00ed\3\2\2\2\u013c\u00f7\3\2\2\2\u013c"+
		"\u0107\3\2\2\2\u013c\u010d\3\2\2\2\u013c\u0112\3\2\2\2\u013c\u011a\3\2"+
		"\2\2\u013c\u0124\3\2\2\2\u013c\u0128\3\2\2\2\u013c\u012a\3\2\2\2\u013c"+
		"\u013b\3\2\2\2\u013d\u0147\3\2\2\2\u013e\u0141\f\20\2\2\u013f\u0140\7"+
		"\4\2\2\u0140\u0142\5\32\16\2\u0141\u013f\3\2\2\2\u0142\u0143\3\2\2\2\u0143"+
		"\u0141\3\2\2\2\u0143\u0144\3\2\2\2\u0144\u0146\3\2\2\2\u0145\u013e\3\2"+
		"\2\2\u0146\u0149\3\2\2\2\u0147\u0145\3\2\2\2\u0147\u0148\3\2\2\2\u0148"+
		"\33\3\2\2\2\u0149\u0147\3\2\2\2\u014a\u0155\7;\2\2\u014b\u014c\5\30\r"+
		"\2\u014c\u014d\7\25\2\2\u014d\u014e\5\30\r\2\u014e\u014f\7\26\2\2\u014f"+
		"\u0155\3\2\2\2\u0150\u0151\5\30\r\2\u0151\u0152\7\6\2\2\u0152\u0153\7"+
		";\2\2\u0153\u0155\3\2\2\2\u0154\u014a\3\2\2\2\u0154\u014b\3\2\2\2\u0154"+
		"\u0150\3\2\2\2\u0155\35\3\2\2\2\u0156\u0157\5\30\r\2\u0157\u0158\7\17"+
		"\2\2\u0158\u0159\5\32\16\2\u0159\u0161\3\2\2\2\u015a\u015b\5\30\r\2\u015b"+
		"\u015c\7\60\2\2\u015c\u015d\5\30\r\2\u015d\u015e\7\17\2\2\u015e\u015f"+
		"\5\32\16\2\u015f\u0161\3\2\2\2\u0160\u0156\3\2\2\2\u0160\u015a\3\2\2\2"+
		"\u0161\37\3\2\2\2\u0162\u0163\5\30\r\2\u0163\u0164\7&\2\2\u0164\u0165"+
		"\5\32\16\2\u0165!\3\2\2\2\u0166\u0167\78\2\2\u0167#\3\2\2\2\u0168\u0169"+
		"\t\5\2\2\u0169%\3\2\2\2\u016a\u016e\7\66\2\2\u016b\u016e\7\67\2\2\u016c"+
		"\u016e\7;\2\2\u016d\u016a\3\2\2\2\u016d\u016b\3\2\2\2\u016d\u016c\3\2"+
		"\2\2\u016e\'\3\2\2\2$-\679?AGIN]amx}\u0085\u008a\u0096\u009e\u00a6\u00bb"+
		"\u00be\u00d1\u00d3\u00ea\u00f3\u00ff\u0103\u0132\u0137\u013c\u0143\u0147"+
		"\u0154\u0160\u016d";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}