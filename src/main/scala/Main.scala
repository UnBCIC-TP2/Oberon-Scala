import br.unb.cic.oberon.parser.ScalaParser
import br.unb.cic.oberon.interpreter.Interpreter

object Main extends App {

  // https://stackoverflow.com/a/55032051
  def pprint(obj: Any, depth: Int = 0, paramName: Option[String] = None): Unit = {

    val indent = "  " * depth
    val prettyName = paramName.fold("")(x => s"$x: ")
    val ptype = obj match { case _: Iterable[Any] => "" case obj: Product => obj.productPrefix case _ => obj.toString }

    println(s"$indent$prettyName$ptype")

    obj match {
      case seq: Iterable[Any] =>
        seq.foreach(pprint(_, depth + 1))
      case obj: Product =>
        (obj.productIterator zip obj.productElementNames)
          .foreach { case (subObj, paramName) => pprint(subObj, depth + 1, Some(paramName)) }
      case _ =>
    }
  }

  val content = """
  MODULE import1;
  IMPORT Texts := T, Oberon, Oberon;

  VAR
    x: INTEGER;

  BEGIN
    x := 0
  END
  END import1.
  """

  val module = ScalaParser.parse(content)
  pprint(module)

  // val tc = new TypeChecker
  // val errors = tc.visit(module)
  // println(errors)

  val interpreter = new Interpreter

  module.accept(interpreter)

}