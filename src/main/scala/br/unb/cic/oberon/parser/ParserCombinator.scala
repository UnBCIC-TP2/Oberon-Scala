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
        def expValueParser: Parser[Expression] = (
        int
    |   real
    |   char
    |   string
    |   bool
    |   "NIL" ^^ (i => NullValue)
    )
    def expressionParser: Parser[Expression] = term ~ rep(boolExpParser) ^^ 
        {case a~b => (a /: b)((acc,f) => f(acc))}

    def eqParser: Parser[Expression => Expression] =  "=" ~ term ^^ { case _ ~ b => EQExpression(_, b) }
    def neqParser: Parser[Expression => Expression] =  "#" ~ term ^^ { case _ ~ b => NEQExpression(_, b) }
    def lteParser: Parser[Expression => Expression] =  "<=" ~ term ^^ { case _ ~ b => LTEExpression(_, b) }
    def gteParser: Parser[Expression => Expression] =  ">=" ~ term ^^ { case _ ~ b => GTEExpression(_, b) }
    def ltParser: Parser[Expression => Expression] =  "<" ~ term ^^ { case _ ~ b => LTExpression(_, b) }
    def gtParser: Parser[Expression => Expression] =  ">" ~ term ^^ { case _ ~ b => GTExpression(_, b) }

   def boolExpParser: Parser[Expression => Expression] = (
        eqParser
    |   neqParser
    |   lteParser
    |   gteParser
    |   ltParser
    |   gteParser
    )

    def term: Parser[Expression] =  term2 ~ rep(plus | minus | or) ^^ 
        {case a~b => (a /: b)((acc,f) => f(acc))}
    def plus  : Parser[Expression => Expression] = "+" ~ term2 ^^ { case _ ~ b => AddExpression(_, b) }
    def minus : Parser[Expression => Expression] = "-" ~ term2 ^^ { case _ ~ b => SubExpression(_, b) }
    def or : Parser[Expression => Expression] = "||" ~ term2 ^^ { case _ ~ b => OrExpression(_, b) }
    def term2  : Parser[Expression] = factor ~ rep(times | divide | and) ^^ { case a~b => (a /: b)((acc,f) => f(acc))}
    def times : Parser[Expression => Expression] =  "*" ~ factor ^^ { case _ ~ b => MultExpression(_, b) }
    def divide : Parser[Expression => Expression] =  "/" ~ factor ^^ { case _ ~ b => DivExpression(_, b) }
    def and : Parser[Expression => Expression] =  "&&" ~ factor ^^ { case _ ~ b => AndExpression(_, b) }

    def factor: Parser[Expression] = expValueParser | "(" ~> expressionParser <~ ")" ^^ {Brackets(_)}
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