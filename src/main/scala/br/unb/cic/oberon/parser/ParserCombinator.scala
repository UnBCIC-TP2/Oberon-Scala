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
    def expressionParser: Parser[Expression] = addTerm ~ rep(relExpParser) ^^ {case a~b => (a /: b)((acc,f) => f(acc))}
    def addTerm: Parser[Expression] =  mulTerm ~ rep(addExpParser) ^^ {case a~b => (a /: b)((acc,f) => f(acc))}
    def mulTerm  : Parser[Expression] = complexTerm ~ rep(mulExpParser) ^^ { case a~b => (a /: b)((acc,f) => f(acc))}
    def complexTerm : Parser[Expression] = (
        factor ~ "[" ~ expressionParser ~ "]" ^^ { case a ~ _ ~ b ~_ => ArraySubscript(a, b)}
    |   factor ~ "." ~ identifier ^^ { case a ~ _ ~ b => FieldAccessExpression(a, b)}
    |   factor
    )
    def factor: Parser[Expression] =  (
        expValueParser 
    |   pointerParser
    |   functionParser  
    |   variableParser
    | "(" ~> expressionParser <~ ")" ^^ {Brackets(_)}
    )
    def pointerParser: Parser[Expression] = identifier ~ "^" ^^ { case a ~ _ => PointerAccessExpression(a)}
    def variableParser: Parser[Expression] = qualifiedName ^^ {case a => VarExpression(a)}
    def qualifiedName: Parser[String] =  identifier

    def functionParser: Parser[Expression] = qualifiedName ~ "(" ~ argumentsParser ~ ")" ^^ {
        case name ~ _ ~ argList ~ _ => FunctionCallExpression(name, argList)
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

trait Oberon2ScalaParser extends ExpressionParser {

    def parseAbs[T](result: ParseResult[T]): T = {
        return result match {
            case Success(matched, _) => matched
            case Failure(msg, _)  => throw new Exception(msg)
            case Error(msg, _) => throw new Exception(msg)
        }
    }
}