package br.unb.cic.oberon.parser
import br.unb.cic.oberon.util.Resources
import scala.util.parsing.combinator._
import br.unb.cic.oberon.ast._

trait BasicParsers extends JavaTokenParsers {
    def int: Parser[IntValue] = "-?[0-9]+".r ^^ (i => IntValue(i.toInt))
    def real: Parser[RealValue] = "-?[0-9]+\\.[0-9]+".r ^^ (i => RealValue(i.toDouble))
    def bool: Parser[BoolValue] = "(FALSE|TRUE)".r ^^ (i => BoolValue(i=="TRUE"))
    def string: Parser[StringValue] = "\"[^\"]+\"".r  ^^ (i => StringValue(i.substring(1, i.length()-1)))
    def char: Parser[CharValue] = ("\'[^\']\'".r)  ^^ (i => CharValue(i.charAt(1)))

    def alpha: String = "[A-Za-z]"
    def digit: Parser[String] = "[0-9]".r ^^ (i => i)
    def identifier: Parser[String] = (alpha + "(" + alpha + "|" + digit + "|_)*").r ^^ (i => i)

    def typeParser: Parser[Type] = (
        "INTEGER" ^^ (i => IntegerType)
    |   "REAL" ^^ (i => RealType)
    |   "CHAR" ^^ (i => CharacterType)
    |   "BOOLEAN" ^^ (i => BooleanType)
    |   "STRING" ^^ (i => StringType)
    |   "NIL" ^^ (i => NullType)
    |   identifier ^^ (i =>  ReferenceToUserDefinedType(i))
    )
}

trait ExpressionParser extends BasicParsers {
    def aggregator[T](r:  T ~ List[T => T]): T = { 
        r match { case a~b => (a /: b)((acc,f) => f(acc)) } 
    }

    def expressionParser: Parser[Expression] = addTerm ~ rep(relExpParser) ^^ aggregator[Expression]
    def addTerm: Parser[Expression] =  mulTerm ~ rep(addExpParser) ^^ aggregator[Expression]
    def mulTerm  : Parser[Expression] = complexTerm ~ rep(mulExpParser) ^^ aggregator[Expression]
    def complexTerm : Parser[Expression] = (
        factor ~ "[" ~ expressionParser ~ "]" ~ not(":=") ^^ { case a ~ _ ~ b ~_ ~_ => ArraySubscript(a, b)}
    |   factor ~ "." ~ identifier ^^ { case a ~ _ ~ b => FieldAccessExpression(a, b)}
    |   factor
    )
    def factor: Parser[Expression] =  (
        expValueParser 
    |   pointerParser
    |   functionParser  
    |   variableParser
    | "(" ~> expressionParser <~ ")" ^^ Brackets
    )
    def pointerParser: Parser[Expression] = identifier ~ "^" ^^ { case a ~ _ => PointerAccessExpression(a)}
    def variableParser: Parser[Expression] = qualifiedName ^^ {case a => VarExpression(a)}
    def qualifiedName: Parser[String] =  identifier

    def functionParser: Parser[Expression] = qualifiedName ~ "(" ~ opt(argumentsParser) ~ ")" ^^ {
        case name ~ _ ~ None ~ _ => FunctionCallExpression(name, List())
        case name ~ _ ~ Some(argList) ~ _ => FunctionCallExpression(name, argList)
    }
    def argumentsParser: Parser[List[Expression]] = expressionParser ~ rep(argTerm) ^^ { case a ~ b => List(a) ++ b }
    def argTerm: Parser[Expression] = "," ~ expressionParser ^^ {case _ ~ b => b}

    def relExpParser: Parser[Expression => Expression] = (
        "=" ~ addTerm ^^ { case _ ~ b => EQExpression(_, b) }
    |   "#" ~ addTerm ^^ { case _ ~ b => NEQExpression(_, b) }
    |   "<=" ~ addTerm ^^ { case _ ~ b => LTEExpression(_, b) }
    |   ">=" ~ addTerm ^^ { case _ ~ b => GTEExpression(_, b) }
    |   "<" ~ addTerm ^^ { case _ ~ b => LTExpression(_, b) }
    |   ">" ~ addTerm ^^ { case _ ~ b => GTExpression(_, b) }
    )

    def addExpParser: Parser[Expression => Expression] = (
        "+" ~ mulTerm ^^ { case _ ~ b => AddExpression(_, b) }
    |   "-" ~ mulTerm ^^ { case _ ~ b => SubExpression(_, b) }
    |   "||" ~ mulTerm ^^ { case _ ~ b => OrExpression(_, b) }
    )
    
    def mulExpParser: Parser[Expression => Expression] = (
        "*" ~ complexTerm ^^ { case _ ~ b => MultExpression(_, b) }
    |   "/" ~ complexTerm ^^ { case _ ~ b => DivExpression(_, b) }
    |   "&&" ~ complexTerm ^^ { case _ ~ b => AndExpression(_, b) }
    )

    def expValueParser: Parser[Expression] = (
        real
    |   int
    |   char
    |   string
    |   bool
    |   "NIL" ^^ (i => NullValue)
    )
}


trait StatementParser extends ExpressionParser {
  def multStatementParser: Parser[Statement] = stmtSequenceParser ^^ {case a => {
      if(a.length > 1){
          SequenceStmt(a)
      }
      else
          a(0)
    }}

  def designator: Parser[AssignmentAlternative] = (
      expressionParser ~ "[" ~ expressionParser ~ "]" ^^ { case a ~ _ ~ b ~ _ => ArrayAssignment(a, b)}
  |   expressionParser ~ "." ~ identifier ^^ { case a ~ _ ~ b => RecordAssignment(a, b)}
  |   identifier ~ "^" ^^ { case a ~ _ => PointerAssignment(a)}
  |   identifier ^^ {case a => VarAssignment(a)}
  )

  def elseIfStmtParser: Parser[ElseIfStmt] = expressionParser ~ "THEN" ~ statementParser ^^ {
      case cond ~ _ ~ stmt => ElseIfStmt(cond, stmt)
  }

  def caseAlternativeParser: Parser[CaseAlternative] = (
      expressionParser ~ ':' ~ statementParser ^^ { case cond ~ _ ~ stmt => SimpleCase(cond, stmt) }
  |   expressionParser ~ ".." ~ expressionParser ~ ':' ~ statementParser ^^ { 
          case min ~ _ ~ max ~ _ ~ stmt => RangeCase(min, max, stmt)
      }
  );

  def stmtSequenceParser: Parser[List[Statement]] = statementParser ~ rep(stmtSequenceTerm) ^^ { case a ~ b => List(a) ++ b }
      
  def stmtSequenceTerm: Parser[Statement] = ";" ~ statementParser ^^ {case _ ~ b => b}

  def statementParser: Parser[Statement] = (
      identifier ~ ":=" ~ expressionParser ^^ { case id ~ _ ~ expression => AssignmentStmt(id, expression)}
  |   designator ~ ":=" ~ expressionParser ^^ { case des ~ _ ~ expression => EAssignmentStmt(des, expression)}
  |   "readReal" ~ '(' ~ identifier ~ ')' ^^ { case _ ~ _ ~ id ~ _ => ReadRealStmt(id) }
  |   "readInt" ~ '(' ~ identifier ~ ')' ^^ { case _ ~ _ ~ id ~ _ => ReadIntStmt(id) }
  |   "readChar" ~ '(' ~ identifier ~ ')' ^^ { case _ ~ _ ~ id ~ _ => ReadCharStmt(id) }
  |   "write" ~ '(' ~ expressionParser ~ ')' ^^ { case _ ~ _ ~ exp ~ _ => WriteStmt(exp) }
  |   identifier ~ '(' ~ opt(argumentsParser) ~ ')' ^^ { 
      case id ~ _ ~ None ~ _ => ProcedureCallStmt(id, List())
      case id ~ _ ~ Some(args) ~ _ => ProcedureCallStmt(id, args)
  }
  | "IF" ~ expressionParser ~ "THEN" ~ statementParser ~ opt("ELSE" ~> statementParser) ~ "END" ^^ {
      case _ ~ cond ~ _ ~ stmt ~ None ~ _ => IfElseStmt(cond, stmt, None : Option[Statement])
      case _ ~ cond ~ _ ~ stmt ~ Some(elseStmt) ~ _ => IfElseStmt(cond, stmt, Option(elseStmt))
  }
  | "IF" ~ expressionParser ~ "THEN" ~ statementParser ~ rep1("ELSIF" ~> elseIfStmtParser) ~ opt("ELSE" ~> statementParser) ~ "END" ^^ {
      case _ ~ cond ~ _ ~ stmt ~ elseifs ~ None ~ _ => IfElseIfStmt(cond, stmt, elseifs, None : Option[Statement])
      case _ ~ cond ~ _ ~ stmt ~ elseifs ~ Some(elseStmt) ~ _ => IfElseIfStmt(cond, stmt, elseifs, Option(elseStmt))
  }
  | "WHILE" ~ expressionParser ~ "DO" ~ statementParser ~ "END" ^^ { 
      case _ ~ cond ~ _ ~ stmt ~ _ => WhileStmt(cond, stmt)
  }
  | "REPEAT" ~ statementParser ~ "UNTIL" ~ expressionParser ^^ { 
      case _ ~ stmt ~ _ ~ cond => RepeatUntilStmt(cond, stmt)
  }
  | "FOR" ~ statementParser ~ "TO" ~ expressionParser ~ "DO" ~ statementParser ~ "END" ^^ {
      case _ ~ indexes ~ _ ~ cond ~ _ ~ stmt ~ _ => ForStmt(indexes, cond, stmt)
  }
  | "LOOP" ~ statementParser ~ "END" ^^ { case _ ~ stmt ~ _ => LoopStmt(stmt) }
  | "RETURN" ~ expressionParser ^^ { case _ ~ exp => ReturnStmt(exp) }
  | "CASE" ~ expressionParser ~ "OF" ~ caseAlternativeParser ~ rep("|" ~> caseAlternativeParser) ~ opt("ELSE" ~> statementParser) ~ "END" ^^ {
      case _ ~ exp ~ _ ~ case1 ~ cases ~ None ~ _ => CaseStmt(exp, List(case1) ++ cases, None : Option[Statement])
      case _ ~ exp ~ _ ~ case1 ~ cases ~ Some(stmt) ~ _ => CaseStmt(exp, List(case1) ++ cases, Option(stmt))
  }
  | "EXIT" ^^ { case _ => ExitStmt() }
  // | "FOR" ~ identifier ~ "IN" ~ expressionParser ~ ".." ~ expressionParser ~ "DO" ~ statementParser ~ "END" {
  //     case _ ~ id ~ _ ~ min ~ _ ~ max ~ _ ~ stmt => ForRangeStmt(id, min, max, stmt)
  // }
  // | stmt += statement (';' stmt += statement)+                                                                                 #SequenceStmt
  );
}

trait OberonParserFull extends StatementParser {
    
    def userTypeParser: Parser[Type] = (
        "ARRAY" ~ int ~ "OF" ~ (typeParser | userTypeParser) ^^ { case _ ~ a ~ _ ~ b => ArrayType(a.value, b)} 
    |   "RECORD" ~> varDeclarationParser <~ "END" ^^ RecordType
    |   ("POINTER" ~ "TO") ~> (typeParser | userTypeParser) ^^ PointerType
    )
    def userTypeDeclarationTerm: Parser[UserDefinedType] = identifier ~ "=" ~ userTypeParser ^^ { case a ~ _ ~ b => UserDefinedType(a, b) }
    def userTypeDeclarationParser: Parser[List[UserDefinedType]] = opt("TYPE" ~> rep1(userTypeDeclarationTerm)) ^^ {
        case Some(a) => a
        case None => List[UserDefinedType]()
    }

    def constantParserTerm: Parser[Constant] = identifier ~ "=" ~ expressionParser ^^ { case a ~ _ ~ b => Constant(a, b)} 
    def constantParser: Parser[List[Constant]] = opt("CONST" ~> rep1(constantParserTerm)) ^^ {
        case Some(a) => a
        case None => List[Constant]()
    }

    def varListParser: Parser[List[String]] = identifier ~ rep("," ~> identifier) ^^ { case a ~ b => List(a) ++ b  }
    def varDeclarationParserTerm: Parser[List[VariableDeclaration]] =  varListParser ~ ":" ~ (typeParser | userTypeParser) ^^ {
        case varList ~ _ ~ varType => varList.map(VariableDeclaration(_, varType))
    }
    def varDeclarationParser: Parser[List[VariableDeclaration]] = opt("VAR" ~> rep1(varDeclarationParserTerm)) ^^ {
        case Some(a) => a.flatten
        case None => List[VariableDeclaration]()
    }

    def procedureParser: Parser[List[Procedure]] = "PROCEDURE" ^^ (_ => List[Procedure]())
    
    class DeclarationProps(val userTypes: List[UserDefinedType], val constants: List[Constant], val variables: List[VariableDeclaration], val procedures: List[Procedure])
    def declarationsParser: Parser[DeclarationProps] =
        userTypeDeclarationParser ~ constantParser ~ varDeclarationParser ~ procedureParser ^^ {
            case userTypes ~ constants ~ vars ~ procedures => new DeclarationProps(userTypes, constants, vars, procedures)
        }

    def blockParser: Parser[Statement] = "BEGIN" ~ multStatementParser ~ "END" ^^ { case _ ~ stmt ~ _ => stmt }
    def importParser: Parser[Set[String]] = opt("IMPORT" ~> rep(identifier)) ^^ {
        case Some(a) => a.toSet
        case None => Set[String]()
    }
    def moduleParser: Parser[OberonModule] = "MODULE" ~ identifier ~ ";" ~ importParser ~ declarationsParser ~ blockParser ~ "END" ~ identifier ~ "." ^^ {
        case _ ~ name ~ _ ~  imports ~ declarations ~ statements ~ _ ~ _ ~ _  => OberonModule(
            name,
            imports,
            declarations.userTypes,
            declarations.constants,
            declarations.variables,
            declarations.procedures,
            Option(statements)
    )}
    
    def oberonParser: Parser[OberonModule] = moduleParser
}


trait Oberon2ScalaParser extends OberonParserFull {

    def parseResource(resource: String): OberonModule = {
        return parseAbs(parse(oberonParser, Resources.getContent(resource))) 
    }

    def parseAbs[T](result: ParseResult[T]): T = {
        return result match {
            case Success(matched, _) => matched
            case Failure(msg, _)  => throw new Exception(msg)
            case Error(msg, _) => throw new Exception(msg)
        }
    }
}