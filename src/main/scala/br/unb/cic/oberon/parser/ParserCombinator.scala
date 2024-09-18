package br.unb.cic.oberon.parser

import scala.util.parsing.combinator._
import scala.reflect.runtime.universe.TypeTag
import scala.reflect.runtime.universe.typeOf
import br.unb.cic.oberon.util.Resources
import br.unb.cic.oberon.ir.ast._
import scala.collection.mutable.Map

trait ParsersUtil extends JavaTokenParsers {
  // Encapsulator aggregator function
  def aggregator[T](r: T ~ List[T => T]): T = {
    r match {
      case a ~ b => (b.foldLeft(a))((acc, f) => f(acc))
    }
  }

  // List Helper Function
  def listOpt[T](parser: Parser[List[T]]): Parser[List[T]] = opt(parser) ^^ {
    case Some(a) => a
    case None => List[T]()
  }

  // Option Helper Function
  def optSolver[T](parser: Parser[T]): Parser[Option[T]] = opt(parser) ^^ {
    case Some(a) => Option(a)
    case None => None: Option[T]
  }
}

trait BasicParsers extends ParsersUtil {

  override protected val whiteSpace = "([ \t\r\n\u000c]+|//[^\r\n]*)+|/\\*.*?\\*/".r

  def int: Parser[IntValue] = "-?[0-9]+".r ^^ (i => IntValue(i.toInt))

  def real: Parser[RealValue] = "-?[0-9]+\\.[0-9]+".r ^^ (i => RealValue(i.toDouble))

  def bool: Parser[BoolValue] = "(False|True)".r ^^ (i => BoolValue(i == "True"))

  def string: Parser[StringValue] = "\"[^\"]*\"".r ^^ (i => StringValue(i.substring(1, i.length() - 1)))

  def char: Parser[CharValue] = ("\'[^\']\'".r) ^^ (i => CharValue(i.charAt(1)))

  def alpha: String = "[A-Za-z]"

  def digit: String = "[0-9]"

  def identifier: Parser[String] = (alpha + "(" + alpha + "|" + digit + "|_)*").r


  def typeParser: Parser[Type] = (
    "INTEGER" ^^ (_ => IntegerType)
      | "REAL" ^^ (_ => RealType)
      | "CHAR" ^^ (_ => CharacterType)
      | "BOOLEAN" ^^ (_ => BooleanType)
      | "STRING" ^^ (_ => StringType)
      | "NIL" ^^ (_ => NullType)
      | identifier ^^ ReferenceToUserDefinedType
    )
}

trait CompositeParsers extends BasicParsers {

  def qualifiedName: Parser[String] = opt(identifier <~ "::") ~ identifier ^^ {
    case Some(id1) ~ id2 => id1 + "::" + id2
    case None ~ id2 => id2
  }

  // UserDefinedType
  def userTypeParser: Parser[Type] = (
    ("ARRAY" ~> int) ~ ("OF" ~> (userTypeParser | typeParser)) ^^ { case a ~ b => ArrayType(a.value, b) }
      | "RECORD" ~> multDeclarationTermParser <~ "END" ^^ RecordType
      | ("POINTER" ~ "TO") ~> (userTypeParser | typeParser) ^^ PointerType
      | ("LAMBDA" ~ "->") ~> ("(" ~> lambdaTypes <~ ")") ~ (":" ~> (userTypeParser | typeParser)) ^^ { case args ~ ret => LambdaType(args, ret) }
      | "LIST" ~> ("OF" ~> (userTypeParser | typeParser)) ^^ ListType  // Suporte para ListType
    )

  def lambdaTypes: Parser[List[Type]] = opt((userTypeParser | typeParser) ~ rep("," ~> (userTypeParser | typeParser))) ^^ {
    case Some(a ~ b) => List(a) ::: b
    case None => List[Type]()
  }

  def userTypeDeclarationTerm: Parser[UserDefinedType] = (identifier <~ "=") ~ userTypeParser ^^ { case a ~ b => UserDefinedType(a, b) }

  def userTypeDeclarationParser: Parser[List[UserDefinedType]] = "TYPE" ~> rep1(userTypeDeclarationTerm)

  def declarationTermParser: Parser[List[VariableDeclaration]] = (varListParser <~ ":") ~ (userTypeParser | typeParser) ^^ { case varList ~ varType => varList.map(VariableDeclaration(_, varType)) }

  def multDeclarationTermParser: Parser[List[VariableDeclaration]] = rep1(declarationTermParser <~ ";") ^^ { a => a.flatten }


  // VariableDeclaration
  def varListParser: Parser[List[String]] = identifier ~ rep("," ~> identifier) ^^ { case a ~ b => List(a) ++ b }

  def varDeclarationParser: Parser[List[VariableDeclaration]] = "VAR" ~> multDeclarationTermParser

  def module: Parser[String] = identifier ~ opt(":=" ~> identifier) ^^ {
    case mod ~ Some(a) => mod //+ ":=" + a
    case mod ~ None => mod
  }

  def formalArgs: Parser[List[FormalArg]] = opt(formalArg ~ rep("," ~> formalArg)) ^^ {
    case Some(a ~ b) => a ::: b.flatten
    case None => List[FormalArg]()
  }

  def formalArg: Parser[List[FormalArg]] = (
    identifier ~ rep("," ~> identifier) ~ ":" ~ (userTypeParser | typeParser) ^^ {
      case head ~ tail ~ _ ~ argType => {
        val args = List(head) ++ tail
        args.map((x: String) => ParameterByValue(x, argType))
      }
    }
      | "VAR" ~ identifier ~ rep(("," ~ "VAR") ~> identifier) ~ ":" ~ (userTypeParser | typeParser) ^^ {
      case _ ~ head ~ tail ~ _ ~ argType => {
        val args = List(head) ++ tail
        args.map((x: String) => ParameterByReference(x, argType))
      }
    }
    )

}

trait ExpressionParser extends CompositeParsers {
  def consExpressionParser: Parser[Expression] = {"CONS" ~> "(" ~> expressionParser <~ ")" ^^ {case head => ConsExpression(head)}} // Create a list with the head as the only element}

  def concatExpressionParser: Parser[Expression] = "CONCAT" ~> ("(" ~> expressionParser ~ ("," ~> expressionParser) <~ ")") ^^ {case left ~ right => ConcatExpression(left, right)}

  def removeExpressionParser: Parser[Expression] = "REMOVE" ~> ("(" ~> expressionParser ~ ("," ~> expressionParser) <~ ")") ^^ {case item ~ list => RemoveExpression(item, list)}

  def lenExpressionParser: Parser[Expression] = "LEN" ~> ("(" ~> expressionParser <~ ")") ^^ LenExpression

  def pointerParser: Parser[Expression] = identifier <~ "^" ^^ PointerAccessExpression

  def variableParser: Parser[Expression] = qualifiedName ^^ VarExpression

  def argumentsParser: Parser[List[Expression]] = expressionParser ~ rep("," ~> expressionParser) ^^ { case a ~ b => List(a) ++ b }

  def functionParser: Parser[Expression] = qualifiedName ~ ("(" ~> opt(argumentsParser) <~ ")") ^^ {
    case name ~ None => FunctionCallExpression(name, List())
    case name ~ Some(argList) => FunctionCallExpression(name, argList)
  }

  def lambdaExpParser: Parser[Expression] = ("(" ~> opt(formalArgs) <~ ")") ~ ("=>" ~> expressionParser) ^^ {
    case Some(a) ~ b => LambdaExpression(a, b)
    case None ~ b => LambdaExpression(List(), b)
  }

  def expValueParser: Parser[Expression] = real | int | char | string | bool | "NIL" ^^ (_ => NullValue)

  def fieldAccessTerm: Parser[Expression => Expression] = "." ~ identifier ^^ { case _ ~ b => FieldAccessExpression(_, b) }

  def factor: Parser[Expression] = removeExpressionParser | concatExpressionParser | consExpressionParser | lenExpressionParser | expValueParser | pointerParser | functionParser | variableParser | lambdaExpParser | "(" ~> expressionParser <~ ")"

  def complexTerm: Parser[Expression] = (
    "~" ~> factor ^^ NotExpression
      | factor ~ ("[" ~> expressionParser <~ ("]" ~ not(":="))) ^^ { case a ~ b => ArraySubscript(a, b) }
      | factor ~ rep1(fieldAccessTerm) ^^ aggregator
      | factor

    )

  def mulExpParser: Parser[Expression => Expression] = (
    "*" ~ complexTerm ^^ { case _ ~ b => MultExpression(_, b) }
      | "/" ~ complexTerm ^^ { case _ ~ b => DivExpression(_, b) }
      | "MOD" ~ complexTerm ^^ { case _ ~ b => ModExpression(_, b) }
    )

  def mulTerm: Parser[Expression] = complexTerm ~ rep(mulExpParser) ^^ aggregator

  def addExpParser: Parser[Expression => Expression] = (
    "+" ~ mulTerm ^^ { case _ ~ b => AddExpression(_, b) }
      | "-" ~ mulTerm ^^ { case _ ~ b => SubExpression(_, b) }
    )

  def addTerm: Parser[Expression] = mulTerm ~ rep(addExpParser) ^^ aggregator

  def relExpParser: Parser[Expression => Expression] = (
    "=" ~ addTerm ^^ { case _ ~ b => EQExpression(_, b) }
      | "#" ~ addTerm ^^ { case _ ~ b => NEQExpression(_, b) }
      | "<=" ~ addTerm ^^ { case _ ~ b => LTEExpression(_, b) }
      | ">=" ~ addTerm ^^ { case _ ~ b => GTEExpression(_, b) }
      | "<" ~ addTerm ^^ { case _ ~ b => LTExpression(_, b) }
      | ">" ~ addTerm ^^ { case _ ~ b => GTExpression(_, b) }
    )

  def relTerm: Parser[Expression] = addTerm ~ rep(relExpParser) ^^ aggregator

  def logExpParser: Parser[Expression => Expression] = (
    "&&" ~ relTerm ^^ { case _ ~ b => AndExpression(_, b) }
      | "||" ~ relTerm ^^ { case _ ~ b => OrExpression(_, b) }
    )

  def expressionParser: Parser[Expression] = relTerm ~ rep(logExpParser) ^^ aggregator
}


trait StatementParser extends ExpressionParser {
  def designator: Parser[Designator] = (
    expressionParser ~ ("[" ~> expressionParser <~ "]") ^^ { case a ~ b => ArrayAssignment(a, b) }
      | factor ~ ("." ~> identifier) ^^ { case a ~ b => RecordAssignment(a, b) }
      | identifier <~ "^" ^^ PointerAssignment
      | identifier ^^ VarAssignment
    )

  def elseIfStmtParser: Parser[ElseIfStmt] = (expressionParser <~ "THEN") ~ multStatementParser ^^ { case cond ~ stmt => ElseIfStmt(cond, stmt) }

  def caseAlternativeParser: Parser[CaseAlternative] = (
    (expressionParser <~ ":") ~ multStatementParser ^^ { case cond ~ stmt => SimpleCase(cond, stmt) }
      | (expressionParser <~ "..") ~ (expressionParser <~ ":") ~ multStatementParser ^^ { case min ~ max ~ stmt => RangeCase(min, max, stmt) }
    );

  def buildForRangeStmt(id: String, min: Expression, max: Expression, stmt: Statement): Statement = {
    val variable = VarExpression(id)
    val init = AssignmentStmt(VarAssignment(id), min)
    val condition = LTEExpression(variable, max)
    val accumulator = AssignmentStmt(VarAssignment(id), AddExpression(variable, IntValue(1)))
    val realBlock = SequenceStmt(List(stmt, accumulator))
    ForStmt(init, condition, realBlock)
  }

  def statementParser: Parser[Statement] = (
    designator ~ (":=" ~> expressionParser) ^^ { case des ~ expression => AssignmentStmt(des, expression) }
      | "readReal" ~> ("(" ~> identifier <~ ")") ^^ ReadRealStmt
      | "readLongReal" ~> ("(" ~> identifier <~ ")") ^^ ReadLongRealStmt
      | "readLongInt" ~> ("(" ~> identifier <~ ")") ^^ ReadLongIntStmt
      | "readInt" ~> ("(" ~> identifier <~ ")") ^^ ReadIntStmt
      | "readShortInt" ~> ("(" ~> identifier <~ ")") ^^ ReadShortIntStmt
      | "readChar" ~> ("(" ~> identifier <~ ")") ^^ ReadCharStmt
      | "write" ~> ("(" ~> expressionParser <~ ")") ^^ WriteStmt
      | "assert" ~> ("(" ~> expressionParser <~ ")") ^^ AssertTrueStmt
      | "assert_eq" ~> (("(" ~> expressionParser) ~ ("," ~> expressionParser <~ ")")) ^^ { case exp1 ~ exp2 => AssertEqualStmt(exp1, exp2) }
      | "assert_ne" ~> (("(" ~> expressionParser) ~ ("," ~> expressionParser <~ ")")) ^^ { case exp1 ~ exp2 => AssertNotEqualStmt(exp1, exp2) }
      | "assert_error" ~> ("(" ~> opt(multStatementParser | expressionParser) <~ ")") ^^ {
      case None => AssertError()
      case Some(_) => throw new Exception("assert_error is a reserved word that receives no arguments")
    }
      | "NEW" ~> ("(" ~> identifier <~ ")") ^^ NewStmt
      | ("IF" ~> expressionParser <~ "THEN") ~ multStatementParser ~ optSolver("ELSE" ~> multStatementParser) <~ "END" ^^ { case cond ~ stmt ~ elseStmt => IfElseStmt(cond, stmt, elseStmt) }
      | ("IF" ~> expressionParser <~ "THEN") ~ multStatementParser ~ rep1("ELSIF" ~> elseIfStmtParser) ~ optSolver("ELSE" ~> multStatementParser) <~ "END" ^^ { case cond ~ stmt ~ elseifs ~ elseStmt => IfElseIfStmt(cond, stmt, elseifs, elseStmt) }
      | "WHILE" ~> expressionParser ~ ("DO" ~> multStatementParser <~ "END") ^^ { case cond ~ stmt => WhileStmt(cond, stmt) }
      | "REPEAT" ~> (multStatementParser <~ "UNTIL") ~ expressionParser ^^ { case stmt ~ cond => RepeatUntilStmt(cond, stmt) }
      | "FOR" ~> statementParser ~ ("TO" ~> expressionParser <~ "DO") ~ multStatementParser <~ "END" ^^ { case indexes ~ cond ~ stmt => ForStmt(indexes, cond, stmt) }
      | ("FOR" ~> identifier <~ "IN") ~ expressionParser ~ (".." ~> expressionParser <~ "DO") ~ multStatementParser <~ "END" ^^ { case id ~ min ~ max ~ stmt => buildForRangeStmt(id, min, max, stmt) }
      | ("FOREACH" ~> identifier <~ "IN") ~ expressionParser ~ multStatementParser <~ "END" ^^ { case id ~ exp ~ stmt => ForEachStmt(id, exp, stmt) }
      | "LOOP" ~> multStatementParser <~ "END" ^^ LoopStmt
      | "RETURN" ~> expressionParser ^^ ReturnStmt
      | "CASE" ~> expressionParser ~ ("OF" ~> caseAlternativeParser) ~ rep("|" ~> caseAlternativeParser) ~ optSolver("ELSE" ~> statementParser) <~ "END" ^^ { case exp ~ case1 ~ cases ~ stmt => CaseStmt(exp, List(case1) ++ cases, stmt) }
      | identifier ~ ("(" ~> listOpt(argumentsParser) <~ ")") ^^ { case id ~ args => ProcedureCallStmt(id, args) }
      | "EXIT" ^^ { _ => ExitStmt() }
    );

  // Final Multiple Statements Parser
  def multStatementParser: Parser[Statement] = (statementParser ~ rep(";" ~> statementParser) ^^ { case a ~ b => List(a) ++ b } ^^ {
    case a :: Nil => a
    case a :: b => SequenceStmt(a :: b)
    case Nil => SequenceStmt(Nil)
  }
    );
}

trait OberonParserFull extends StatementParser {


  // Constant
  def constantParserTerm: Parser[Constant] = (identifier <~ "=") ~ expressionParser ^^ { case a ~ b => Constant(a, b) }

  def constantParser: Parser[List[Constant]] = "CONST" ~> rep1(constantParserTerm <~ ";")

  // Procedure

  def procedureParser: Parser[Procedure] =
    "PROCEDURE" ~ identifier ~ ("(" ~> formalArgs <~ ")") ~ procedureTypeParser ~ ";" ~ listOpt(constantParser) ~ listOpt(varDeclarationParser) ~ ("BEGIN" ~> multStatementParser <~ "END") ~ identifier ^^ { case _ ~ name ~ args ~ procedureType ~ _ ~ constants ~ variables ~ statements ~ endName => {
      if (name != endName) throw new Exception(s"Procedure name ($name) doesn't match the end identifier ($endName)")
      Procedure(
        name,
        args,
        procedureType,
        constants,
        variables,
        statements
      )
    }
    }

  def procedureTypeParser: Parser[Option[Type]] = opt(":" ~> (userTypeParser | typeParser)) ^^ {
    case Some(procedureType) => Option(procedureType)
    case None => None: Option[Type]
  }

  def testMod: Parser[String] = "TEST" | "IGNORE"

  def testParser: Parser[Test] = {
    testMod ~ identifier ~ ("(" ~> string <~ ")") ~ ";" ~ listOpt(constantParser) ~ listOpt(varDeclarationParser) ~ ("BEGIN" ~> multStatementParser <~ "END") ~ identifier ^^ {
      case mod ~ name ~ description ~ _ ~ constants ~ variables ~ statements ~ endName => {
        if (name != endName) throw new Exception(s"Procedure name ($name) doesn't match the end identifier ($endName)")
        Test(mod,
          name,
          description,
          constants,
          variables,
          statements
        )
      }
    }
  }


  // Final Parsers

  def importsParser: Parser[Set[String]] = opt(importParser ~ rep("," ~> importParser)) ^^ { //a => a.toSet
    case Some(a ~ b) => (a ::: b.flatten).toSet
    case None => Set[String]()
  }

  def importParser: Parser[List[String]] = (
    "IMPORT" ~> module ~ rep("," ~> module) <~ ";" ^^ {
      case head ~ tail => List(head) ++ tail
    }
    )

  class DeclarationProps(val userTypes: List[UserDefinedType], val constants: List[Constant], val variables: List[VariableDeclaration], val procedures: List[Procedure], val tests: List[Test])

  def declarationsParser: Parser[DeclarationProps] =
    listOpt(userTypeDeclarationParser) ~ listOpt(constantParser) ~ listOpt(varDeclarationParser) ~ listOpt(rep(procedureParser)) ~ listOpt(rep(testParser)) ^^ { case userTypes ~ constants ~ vars ~ procedures ~ tests => new DeclarationProps(userTypes, constants, vars, procedures, tests) }

  def blockParser: Parser[Option[Statement]] = optSolver("BEGIN" ~> multStatementParser <~ "END")

  def oberonParser: Parser[OberonModule] = ("MODULE" ~> identifier <~ ";") ~ importsParser ~ declarationsParser ~ blockParser <~ ("END" ~ identifier ~ ".") ^^ {
    case name ~ imports ~ declarations ~ statements =>
      OberonModule(
        name, imports,
        declarations.userTypes,
        declarations.constants,
        declarations.variables,
        declarations.procedures,
        declarations.tests,
        statements
      )
  }
}


trait Oberon2ScalaParser extends OberonParserFull {
  def parseResource(resource: String): OberonModule = {
    return parseAbs(parse(oberonParser, Resources.getContent(resource)))
  }

  def parseAbs[T](result: ParseResult[T]): T = {
    return result match {
      case Success(matched, _) => matched
      case Failure(msg, _) => throw new Exception(msg)
      case Error(msg, _) => throw new Exception(msg)
    }
  }
}