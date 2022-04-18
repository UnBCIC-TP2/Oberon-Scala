package br.unb.cic.oberon.parser
import br.unb.cic.oberon.util.Resources
import scala.util.parsing.combinator._
import br.unb.cic.oberon.ast._

trait BasicParsers extends JavaTokenParsers {
    def int: Parser[IntValue] = "-?[0-9]+".r <~ not('.') ^^ (i => IntValue(i.toInt))
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
    // |   qualifiedNameParser ^^ (a => VarExpression())
    // |   qualifiedNameParser ~ "(" ~ opt(argumentsParser) ~ ")" ^^ {
    //         case a ~ _ ~ Some(b) ~ _ => FunctionCallExpression()
    //         case a ~ _ ~ None ~ _ => FunctionCallExpression()
    //         }
    // |   expressionParser ~ "." ~ identifier ^^ {case a ~ _ ~ b => FieldAccessExpression()]
    // |   expressionParser ~ "[" ~ expressionParser ~ "]" ^^ { case a ~ _ ~ b ~ _ => ArraySubscript(a, b) }
    // |   identifier ~ "^" ^^ PointerAccessExpression()
    // |   expressionParser ~ "(=|#|<|<=|>|>=)".r ~ expression ^^ {case a ~ "=" ~ b => } 
    // |   expressionParser ~ "(\+|-|\|\|)".r ~ expression
        multParser
    |    "(" ~> expressionParser <~ ")" ^^ { Brackets(_) }
    |   expValueParser
    )

    def multParser: Parser[Expression] = (
        factor ~ rep(times | divide | and) ^^ { case left ~ right => (left /: right)((acc,f) => f(acc)) }
    )

    def times : Parser[Expression => Expression] =  "*" ~ factor ^^ { case _ ~ b => MultExpression(_, b) }
    def divide : Parser[Expression => Expression] =  "/" ~ factor ^^ { case _ ~ b => DivExpression(_, b) }
    def and : Parser[Expression => Expression] =  "&&" ~ factor ^^ { case _ ~ b => AndExpression(_, b) }

    def factor: Parser[Expression] = expValueParser | "(" ~> expressionParser <~ ")" ^^ { Brackets(_) }


    // def qualifiedNameParser: Parser[Expression]
    // def argumentsParser: Parser[List[Expression]]
    def expValueParser: Parser[Expression] = (
        int
    |   real
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
