package br.unb.cic.oberon.transformations

import br.unb.cic.oberon.parser.ScalaParser
import br.unb.cic.oberon.interpreter.Interpreter
import org.scalatest.funsuite.AnyFunSuite
import br.unb.cic.oberon.ast._
import java.nio.file.{Files, Paths}


class CoreVisitorTest extends AnyFunSuite {

    /**Loop Test*/
    test("Testing the loop_stmt01 conversion to OberonCore") {
        val path = Paths.get(getClass.getClassLoader.getResource("stmts/loop_stmt01.oberon").toURI)

        assert(path != null)

        val content = String.join("\n", Files.readAllLines(path))
        val module = ScalaParser.parse(content)
        val interpreter = new Interpreter()
        val coreVisitor = new CoreVisitor()
        
        val stmtcore = module.stmt.get.accept(coreVisitor)

        val coreModule = OberonModule(
            name = module.name,
            userTypes = module.userTypes,
            constants = module.constants,
            variables = module.variables,
            procedures = module.procedures,
            stmt = Some(stmtcore)
        )
        
        coreModule.accept(interpreter)
        assert(interpreter.env.lookup("x") == Some(IntValue(-1)))
    
        //TODO 
/*
        assert(coreModule.name == "LoopStmt")
        assert (coreModule.stmt.isDefined)
        // assert that the main block contains a sequence of statements
        module.stmt.get match {
        case SequenceStmt(stmts) => assert(stmts.length == 6)
        case _ => fail("we are expecting six stmts in the main block")
        }

        // now we can assume that the main block contains a sequence of stmts
        val sequence = module.stmt.get.asInstanceOf[SequenceStmt]
        val stmts = sequence.stmts

        assert(stmts.head == AssignmentStmt("x"))
        assert(stmts(1) == 
        assert(stmts(2) ==
        assert(stmts(3) ==*/
    }

    /**RepeatUntil Test*/
    //TODO Checar negação da expressão no condicional do while
    test("Testing the RepeatUntilStmt01 conversion to OberonCore") {
        val path = Paths.get(getClass.getClassLoader.getResource("stmts/RepeatUntilStmt01.oberon").toURI)

        assert(path != null)

        val content = String.join("\n", Files.readAllLines(path))
        val module = ScalaParser.parse(content)
        val interpreter = new Interpreter()
        val coreVisitor = new CoreVisitor()
        
        val stmtcore = module.stmt.get.accept(coreVisitor)

        val coreModule = OberonModule(
            name = module.name,
            userTypes = module.userTypes,
            constants = module.constants,
            variables = module.variables,
            procedures = module.procedures,
            stmt = Some(stmtcore)
        )
        
        coreModule.accept(interpreter)

        assert(module.name == "RepeatUntilModule");
        assert(interpreter.env.lookup("x").contains(IntValue(11)));
        assert(interpreter.env.lookup("sum").contains(IntValue(55)));
    }

    
    /**For Test*/
    test("Testing the interpreter_stmt01 conversion to OberonCore") {
        val path = Paths.get(getClass.getClassLoader.getResource("stmts/interpreter_stmt01.oberon").toURI)

        assert(path != null)

        val content = String.join("\n", Files.readAllLines(path))
        val module = ScalaParser.parse(content)
        val interpreter = new Interpreter()
        val coreVisitor = new CoreVisitor()
        
        val stmtcore = module.stmt.get.accept(coreVisitor)

        val coreModule = OberonModule(
            name = module.name,
            userTypes = module.userTypes,
            constants = module.constants,
            variables = module.variables,
            procedures = module.procedures,
            stmt = Some(stmtcore)
        )
        
        coreModule.accept(interpreter)

        assert(module.name == "SimpleModule")

        assert(interpreter.env.lookup("x") == Some(IntValue(5))) // FOR TO x
        assert(interpreter.env.lookup("y") == Some(IntValue(6))) // y = x + 1 (after last FOR)
        assert(interpreter.env.lookup("z") == Some(IntValue(15))) // z = result
    }

    /**IfelseIfStmt Test*/
    test("Testing the IfElseIfStmt01 conversion to OberonCore") {
        val path = Paths.get(getClass.getClassLoader.getResource("stmts/IfElseIfStmt01.oberon").toURI)

        assert(path != null)

        val content = String.join("\n", Files.readAllLines(path))
        val module = ScalaParser.parse(content)
        val interpreter = new Interpreter()
        val coreVisitor = new CoreVisitor()
        
        val stmtcore = module.stmt.get.accept(coreVisitor)

        val coreModule = OberonModule(
            name = module.name,
            userTypes = module.userTypes,
            constants = module.constants,
            variables = module.variables,
            procedures = module.procedures,
            stmt = Some(stmtcore)
        )
        
        coreModule.accept(interpreter)

        assert(module.name == "SimpleModule")

        assert(interpreter.env.lookup("y") == Some(IntValue(1)));
    }

}