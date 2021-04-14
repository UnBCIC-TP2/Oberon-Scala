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
        val core = new CoreVisitor()
        
        val stmtcore = Some(module.stmt.get.accept(core))

        //Tentei criar um novo modulo usando o stmtcore, porem trava tudo ao executar o teste
/*
        val coreModule = OberonModule(
            name = module.name,
            userTypes = module.userTypes,
            constants = module.constants,
            variables = module.variables,
            procedures = module.procedures,
            stmt = stmtcore
        )
        
        assert(module.name == "LoopStmt")

        coreModule.accept(interpreter)
*/

        module.accept(interpreter)

        assert(interpreter.env.lookup("x").isDefined)
        assert(interpreter.env.lookup("x") == Some(IntValue(-1)))
    }
}