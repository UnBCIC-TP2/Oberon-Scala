package br.unb.cic.oberon.parser
import scala.util.parsing.combinator._
import scala.reflect.runtime.universe.TypeTag
import scala.reflect.runtime.universe.typeOf
import br.unb.cic.oberon.util.Resources
import br.unb.cic.oberon.ir.ast._
import scala.collection.mutable.Map

trait ParsersUtil extends JavaTokenParsers {
    // Encapsulator aggregator function
    def aggregator[T](r: T ~ List[T => T]): T = { r match { case a ~ b => (b.foldLeft(a))((acc,f) => f(acc)) } }

    // List Helper Function
    def listOpt[T](parser: Parser[List[T]]): Parser[List[T]] = opt(parser) ^^ {
        case Some(a) => a
        case None => List[T]()
    }

    // Option Helper Function
    def optSolver[T](parser: Parser[T]): Parser[Option[T]] = opt(parser) ^^ {
        case Some(a) => Option(a)
        case None => None : Option[T]
    }
}

trait BasicParsers extends ParsersUtil {
    def int: Parser[IntValue] = "-?[0-9]+".r ^^ (i => IntValue(i.toInt))
    def real: Parser[RealValue] = "-?[0-9]+\\.[0-9]+".r ^^ (i => RealValue(i.toDouble))
    def bool: Parser[BoolValue] = "(False|True)".r ^^ (i => BoolValue(i=="True"))
    def string: Parser[StringValue] = "\"[^\"]+\"".r ^^ (i => StringValue(i.substring(1, i.length()-1)))
    def char: Parser[CharValue] = ("\'[^\']\'".r) ^^ (i => CharValue(i.charAt(1)))

    def alpha: String = "[A-Za-z]"
    def digit: String = "[0-9]"
    def identifier: Parser[String] = (alpha + "(" + alpha + "|" + digit + "|_)*").r

    def typeParser: Parser[Type] = (
        "INTEGER" ^^ (_ => IntegerType)
    |   "REAL" ^^ (_ => RealType)
    |   "CHAR" ^^ (_ => CharacterType)
    |   "BOOLEAN" ^^ (_ => BooleanType)
    |   "STRING" ^^ (_ => StringType)
    |   "NIL" ^^ (_ => NullType)
    |   identifier ^^ ReferenceToUserDefinedType
    )
}

trait ExpressionParser extends BasicParsers {
    def pointerParser: Parser[Expression] = identifier <~ "^" ^^ PointerAccessExpression
    def qualifiedName: Parser[String] = identifier // TODO
    def variableParser: Parser[Expression] = qualifiedName ^^ VarExpression
    def argumentsParser: Parser[List[Expression]] = expressionParser ~ rep("," ~> expressionParser) ^^ { case a ~ b => List(a) ++ b }
    def functionParser: Parser[Expression] = qualifiedName ~ ("(" ~> opt(argumentsParser) <~ ")") ^^ {
        case name ~ None => FunctionCallExpression(name, List())
        case name ~ Some(argList) => FunctionCallExpression(name, argList)
    }
    def expValueParser: Parser[Expression] = real | int | char | string | bool | "NIL" ^^ (_ => NullValue)

    def factor: Parser[Expression] = expValueParser | pointerParser | functionParser | variableParser | "(" ~> expressionParser <~ ")" ^^ Brackets

    def complexTerm: Parser[Expression] = (
        factor ~ ("[" ~> expressionParser <~ ("]" ~ not(":="))) ^^ { case a ~ b => ArraySubscript(a, b)}
    |   factor ~ ("." ~> identifier) ^^ { case a ~ b => FieldAccessExpression(a, b)}
    |   factor
    )

    def mulExpParser: Parser[Expression => Expression] = (
        "*" ~ complexTerm ^^ { case _ ~ b => MultExpression(_, b) }
    |   "/" ~ complexTerm ^^ { case _ ~ b => DivExpression(_, b) }
    |   "&&" ~ complexTerm ^^ { case _ ~ b => AndExpression(_, b) }
    )
    def mulTerm: Parser[Expression] = complexTerm ~ rep(mulExpParser) ^^ aggregator

    def addExpParser: Parser[Expression => Expression] = (
        "+" ~ mulTerm ^^ { case _ ~ b => AddExpression(_, b) }
    |   "-" ~ mulTerm ^^ { case _ ~ b => SubExpression(_, b) }
    |   "||" ~ mulTerm ^^ { case _ ~ b => OrExpression(_, b) }
    )
    def addTerm: Parser[Expression] = mulTerm ~ rep(addExpParser) ^^ aggregator

    def relExpParser: Parser[Expression => Expression] = (
        "=" ~ addTerm ^^ { case _ ~ b => EQExpression(_, b) }
    |   "#" ~ addTerm ^^ { case _ ~ b => NEQExpression(_, b) }
    |   "<=" ~ addTerm ^^ { case _ ~ b => LTEExpression(_, b) }
    |   ">=" ~ addTerm ^^ { case _ ~ b => GTEExpression(_, b) }
    |   "<" ~ addTerm ^^ { case _ ~ b => LTExpression(_, b) }
    |   ">" ~ addTerm ^^ { case _ ~ b => GTExpression(_, b) }
    )

    
    def expressionParser: Parser[Expression] = addTerm ~ rep(relExpParser) ^^ aggregator
}


trait StatementParser extends ExpressionParser {
    def designator: Parser[Designator] = (
        expressionParser ~ ("[" ~> expressionParser <~ "]") ^^ { case a ~ b => ArrayAssignment(a, b)}
    |   expressionParser ~ ("." ~> identifier) ^^ { case a ~ b => RecordAssignment(a, b)}
    |   identifier <~ "^" ^^ PointerAssignment
    |   identifier ^^ VarAssignment
    )

    def elseIfStmtParser: Parser[ElseIfStmt] = (expressionParser <~ "THEN") ~ statementParser ^^ { case cond ~ stmt => ElseIfStmt(cond, stmt) }

    def caseAlternativeParser: Parser[CaseAlternative] = (
        (expressionParser <~ ':') ~ statementParser ^^ { case cond ~ stmt => SimpleCase(cond, stmt) }
    |   (expressionParser <~ "..") ~ (expressionParser <~ ':') ~ statementParser ^^ { case min ~ max ~ stmt => RangeCase(min, max, stmt) }
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
    |   "readReal" ~> ('(' ~> identifier <~ ')') ^^ ReadRealStmt
    |   "readInt" ~> ('(' ~> identifier <~ ')') ^^ ReadIntStmt
    |   "readChar" ~> ('(' ~> identifier <~ ')') ^^ ReadCharStmt
    |   "write" ~> ('(' ~> expressionParser <~ ')') ^^ WriteStmt
    |   identifier ~ ('(' ~> listOpt(argumentsParser) <~ ')') ^^ { case id ~ args => ProcedureCallStmt(id, args) }
    |   ("IF" ~> expressionParser <~ "THEN") ~ statementParser ~ optSolver("ELSE" ~> statementParser) <~ "END" ^^ 
        { case cond ~ stmt ~ elseStmt => IfElseStmt(cond, stmt, elseStmt) }
    |   ("IF" ~> expressionParser <~ "THEN") ~ statementParser ~ rep1("ELSIF" ~> elseIfStmtParser) ~ optSolver("ELSE" ~> statementParser) <~ "END" ^^ 
        { case cond ~ stmt ~ elseifs ~ elseStmt => IfElseIfStmt(cond, stmt, elseifs, elseStmt) }
    |   "WHILE" ~> expressionParser ~ ("DO" ~> statementParser <~ "END") ^^ { case cond ~ stmt => WhileStmt(cond, stmt) }
    |   "REPEAT" ~> (statementParser <~ "UNTIL") ~ expressionParser ^^ { case stmt ~ cond => RepeatUntilStmt(cond, stmt) }
    |   "FOR" ~> statementParser ~ ("TO" ~> expressionParser <~ "DO") ~ statementParser <~ "END" ^^ 
        { case indexes ~ cond ~ stmt => ForStmt(indexes, cond, stmt) }
    |   ("FOR" ~> identifier <~ "IN") ~ expressionParser ~ (".." ~> expressionParser <~ "DO") ~ statementParser <~ "END" ^^
        { case id ~ min ~ max ~ stmt => buildForRangeStmt(id, min, max, stmt) }
    |   "LOOP" ~> statementParser <~ "END" ^^ LoopStmt
    |   "RETURN" ~> expressionParser ^^ ReturnStmt
    |   "CASE" ~> expressionParser ~ ("OF" ~> caseAlternativeParser) ~ rep("|" ~> caseAlternativeParser) ~ optSolver("ELSE" ~> statementParser) <~ "END" ^^ 
        { case exp ~ case1 ~ cases ~ stmt => CaseStmt(exp, List(case1) ++ cases, stmt) }
    |   "EXIT" ^^ { _ => ExitStmt() }
    );

    // Final Multiple Statements Parser
    def multStatementParser: Parser[Statement] = (statementParser ~ rep(";" ~> statementParser) ^^ 
        { case a ~ b => List(a) ++ b } ^^ {
            case a :: Nil => a
            case a :: b => SequenceStmt(a :: b)
			case Nil => SequenceStmt(Nil)
        }
    );
}

trait OberonParserFull extends StatementParser {
    // UserDefinedType
    def userTypeParser: Parser[Type] = (
        ("ARRAY" ~> int) ~ ("OF" ~> (typeParser | userTypeParser)) ^^ { case a ~ b => ArrayType(a.value, b)} 
    |   "RECORD" ~> varDeclarationParser <~ "END" ^^ RecordType
    |   ("POINTER" ~ "TO") ~> (typeParser | userTypeParser) ^^ PointerType
    )
    def userTypeDeclarationTerm: Parser[UserDefinedType] = (identifier <~ "=") ~ userTypeParser ^^ { case a ~ b => UserDefinedType(a, b) }
    def userTypeDeclarationParser: Parser[List[UserDefinedType]] = "TYPE" ~> rep1(userTypeDeclarationTerm)

    // Constant

    def constantParserTerm: Parser[Constant] = (identifier <~ "=") ~ expressionParser ^^ { case a ~ b => Constant(a, b) } 
    def constantParser: Parser[List[Constant]] = "CONST" ~> rep1(constantParserTerm <~ ";")

    // VariableDeclaration
    def varListParser: Parser[List[String]] = identifier ~ rep("," ~> identifier) ^^ { case a ~ b => List(a) ++ b }
    def varDeclarationParserTerm: Parser[List[VariableDeclaration]] = (varListParser <~ ":") ~ (typeParser | userTypeParser) ^^ 
        { case varList ~ varType => varList.map(VariableDeclaration(_, varType)) }
    def varDeclarationParser: Parser[List[VariableDeclaration]] = "VAR" ~> rep1(varDeclarationParserTerm <~ ";") ^^ { a => a.flatten }

    // Procedure 

    def procedureParser: Parser[Procedure] = 
    "PROCEDURE" ~ identifier ~ ("(" ~> formalArgs <~ ")") ~ procedureTypeParser ~ ";" ~ listOpt(constantParser) ~ listOpt(varDeclarationParser) ~ ("BEGIN" ~> multStatementParser <~ "END") ~ identifier ^^
    {   case _ ~ name ~ args ~ procedureType ~ _ ~ constants ~ variables ~ statements ~ endName => {
            if(name != endName) throw new Exception(s"Procedure name ($name) doesn't match the end identifier ($endName)")
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

    def procedureTypeParser: Parser[Option[Type]]= opt(":" ~> (typeParser | userTypeParser)) ^^ {
        case Some(procedureType) => Option(procedureType)
        case None => None: Option[Type]
    }

    def formalArgs: Parser[List[FormalArg]] = opt(formalArg ~ rep("," ~> formalArg)) ^^ {
        case Some(a ~ b) => a ::: b.flatten
        case None => List[FormalArg]()
    }

    def formalArg: Parser[List[FormalArg]] = (
        identifier ~ rep("," ~> identifier) ~ ":" ~ (typeParser | userTypeParser) ^^ {
            case head ~ tail ~ _ ~ argType => {
                val args = List(head) ++ tail
                args.map((x: String) => ParameterByValue(x, argType))
            }
        }
    |   "VAR" ~ identifier ~ rep(("," ~ "VAR") ~> identifier) ~ ":" ~ (typeParser | userTypeParser) ^^ {
            case _ ~ head ~ tail ~ _ ~ argType => {
                val args = List(head) ++ tail
                args.map((x: String) => ParameterByReference(x, argType))
            }
        }
    )

    // Final Parsers

    def importParser: Parser[Set[String]] = listOpt("IMPORT" ~> rep(identifier <~ ";")) ^^ { a => a.toSet }
    
    class DeclarationProps(val userTypes: List[UserDefinedType], val constants: List[Constant], val variables: List[VariableDeclaration], val procedures: List[Procedure])
    def declarationsParser: Parser[DeclarationProps] =
        listOpt(userTypeDeclarationParser) ~ listOpt(constantParser) ~ listOpt(varDeclarationParser) ~ listOpt(rep(procedureParser)) ^^ 
        { case userTypes ~ constants ~ vars ~ procedures => new DeclarationProps(userTypes, constants, vars, procedures) }
    
    def blockParser: Parser[Option[Statement]] = optSolver("BEGIN" ~> multStatementParser <~ "END")

    def oberonParser: Parser[OberonModule] = ("MODULE" ~> identifier <~ ";") ~ importParser ~ declarationsParser ~ blockParser <~ ("END" ~ identifier ~ ".") ^^ {
        case name ~ imports ~ declarations ~ statements => 
            OberonModule(
                name, imports,
                declarations.userTypes,
                declarations.constants,
                declarations.variables,
                declarations.procedures,
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
