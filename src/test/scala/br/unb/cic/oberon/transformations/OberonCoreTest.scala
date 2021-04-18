package br.unb.cic.oberon.transformations

import br.unb.cic.oberon.parser.ScalaParser
import br.unb.cic.oberon.interpreter.Interpreter
import org.scalatest.funsuite.AnyFunSuite
import br.unb.cic.oberon.ast._
import java.nio.file.{Files, Paths}


class CoreVisitorTest extends AnyFunSuite {
 /**  
  * Nessa classe tem q ser testado se a transformacao foi correta e se apos a transformacao os valores foram computados corretamente.
  * 
  **/
    /**Loop Test01*/
    test("Testing the loop_stmt01 evaluation after conversion to OberonCore") {
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
    }

    test("Testing the loop_stmt01 expressions after conversion to While") {
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


        assert(coreModule.name == "LoopStmt")
        assert (coreModule.stmt.isDefined)
        // assert that the main block contains a sequence of statements
        module.stmt.get match {
            case SequenceStmt(stmts) => assert(stmts.length == 2)
            case _ => fail("we are expecting two stmts in the main block")
        }

        // now we can assume that the main block contains a sequence of stmts
        val sequence = coreModule.stmt.get.asInstanceOf[SequenceStmt]
        val stmts = sequence.stmts


        assert(stmts.head == AssignmentStmt("x",IntValue(10)))
        assert(stmts(1) == WhileStmt(BoolValue(true),SequenceStmt(List(WriteStmt(VarExpression("x")), IfElseStmt(LTExpression(VarExpression("x"),IntValue(0)),ExitStmt(),None), AssignmentStmt("x",SubExpression(VarExpression("x"),IntValue(1)))))))
    }

     /**Loop Test02*/
    test("Testing the loop_stmt02 evaluation after conversion to OberonCore") {
        val path = Paths.get(getClass.getClassLoader.getResource("stmts/loop_stmt02.oberon").toURI)

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
        assert(interpreter.env.lookup("x") == Some(IntValue(6)))
        assert(interpreter.env.lookup("factorial") == Some(IntValue(120)))
    }

    test("Testing the loop_stmt02 expressions after conversion to While") {
        val path = Paths.get(getClass.getClassLoader.getResource("stmts/loop_stmt02.oberon").toURI)

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


        assert(coreModule.name == "LoopStmt")
        assert (coreModule.stmt.isDefined)
        // assert that the main block contains a sequence of statements
        module.stmt.get match {
            case SequenceStmt(stmts) => assert(stmts.length == 3)
            case _ => fail("we are expecting three stmts in the main block")
        }

        // now we can assume that the main block contains a sequence of stmts
        val sequence = coreModule.stmt.get.asInstanceOf[SequenceStmt]
        val stmts = sequence.stmts


        assert(stmts.head == AssignmentStmt("x",IntValue(2)))
        assert(stmts(1) == AssignmentStmt("factorial",IntValue(1)))
        assert(stmts(2) == WhileStmt(BoolValue(true),SequenceStmt(List(IfElseStmt(GTExpression(VarExpression("x"),IntValue(5)),ExitStmt(),None), AssignmentStmt("factorial",MultExpression(VarExpression("factorial"),VarExpression("x"))), AssignmentStmt("x",AddExpression(VarExpression("x"),IntValue(1)))))))
    }


    /**Loop Test03*/
    test("Testing the loop_stmt03 evaluation after conversion to OberonCore") {
        val path = Paths.get(getClass.getClassLoader.getResource("stmts/loop_stmt03.oberon").toURI)

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
        assert(interpreter.env.lookup("x") == Some(IntValue(10)))
        assert(interpreter.env.lookup("i") == Some(IntValue(10)))
        assert(interpreter.env.lookup("y") == Some(IntValue(100)))
    }

    test("Testing the loop_stmt03 expressions after conversion to While") {
        val path = Paths.get(getClass.getClassLoader.getResource("stmts/loop_stmt03.oberon").toURI)

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

        assert(coreModule.name == "LoopStmt")
        assert (coreModule.stmt.isDefined)
        // assert that the main block contains a sequence of statements
        module.stmt.get match {
            case SequenceStmt(stmts) => assert(stmts.length == 3)
            case _ => fail("we are expecting three stmts in the main block")
        }

        // now we can assume that the main block contains a sequence of stmts
        val sequence = coreModule.stmt.get.asInstanceOf[SequenceStmt]
        val stmts = sequence.stmts


        println(stmts(0))
        println(stmts(1))
        println(stmts(2))
        assert(stmts.head == AssignmentStmt("x",IntValue(0)))
        assert(stmts(1) == AssignmentStmt("y",IntValue(0)))
        assert(stmts(2) == WhileStmt(BoolValue(true),SequenceStmt(List(AssignmentStmt("x",AddExpression(VarExpression("x"),IntValue(1))), AssignmentStmt("i",IntValue(0)), WhileStmt(BoolValue(true), SequenceStmt(List(AssignmentStmt("y",AddExpression(VarExpression("y"),IntValue(1))), AssignmentStmt("i",AddExpression(VarExpression("i"),IntValue(1))), IfElseStmt(EQExpression(VarExpression("i"),IntValue(10)),ExitStmt(),None)))), IfElseStmt(EQExpression(VarExpression("x"),IntValue(10)),ExitStmt(),None))))
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
