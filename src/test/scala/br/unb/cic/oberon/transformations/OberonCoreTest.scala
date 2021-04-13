package br.unb.cic.oberon.transformations

import br.unb.cic.oberon.parser.ScalaParser
import br.unb.cic.oberon.interpreter.Interpreter
import org.scalatest.funsuite.AnyFunSuite
import br.unb.cic.oberon.ast._
import java.nio.file.{Files, Paths}


class CoreVisitorTest extends AnyFunSuite {

    /**LoopStmt Test*/
    test("Test LoopStmt from Oberon Core") {
        val path = Paths.get(getClass.getClassLoader.getResource("stmts/loop_stmt01.oberon").toURI)

        assert(path != null)

        val content = String.join("\n", Files.readAllLines(path))
        val module = ScalaParser.parse(content)
        val interpreter = new Interpreter()
        val core = CoreVisitor.visit(module)

        core.accept(interpreter)   

        assert(interpreter.env.lookup("x").isDefined)
        assert(interpreter.env.lookup("x") == Some(IntValue(-1)))
    }
}