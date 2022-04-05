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

    def alpha: String = "[A-z]"
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
    def expressionParser: Parser[Expression] = (
        "(" ~ expressionParser ~ ")" ^^ { case _ ~ a ~ _ => Brackets(a) }
        |   expValueParser
    // |   qualifiedNameParser ^^ (a => VarExpression())
    // |   qualifiedNameParser ~ "(" ~ opt(argumentsParser) ~ ")" ^^ {
    //         case a ~ _ ~ Some(b) ~ _ => FunctionCallExpression()
    //         case a ~ _ ~ None ~ _ => FunctionCallExpression()
    //         }
    // |   expressionParser ~ "." ~ identifier ^^ {case a ~ _ ~ b => FieldAccessExpression()]
    // |   expressionParser ~ "[" ~ expressionParser ~ "]" ^^ {case a ~ _ ~ b ~ _ => ArraySubscript()}
    // |   identifier ~ "^" ^^ PointerAccessExpression()
    // |   expressionParser ~ "(=|#|<|<=|>|>=)".r ~ expression ^^ {case a ~ "=" ~ b => } 
    // |   expressionParser ~ "(\*|/|&&)".r ~ expression
    // |   expressionParser ~ "(\+|-|\|\|)".r ~ expression
    )

    // def qualifiedNameParser: Parser[Expression]
    // def argumentsParser: Parser[List[Expression]]
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
