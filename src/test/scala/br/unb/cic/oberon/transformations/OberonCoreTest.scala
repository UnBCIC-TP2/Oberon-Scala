package br.unb.cic.oberon.transformations

import br.unb.cic.oberon.parser.ScalaParser
import br.unb.cic.oberon.interpreter.Interpreter
import org.scalatest.funsuite.AnyFunSuite
import br.unb.cic.oberon.ast._
import java.nio.file.{Files, Paths}
import java.util.concurrent.locks.Condition


class CoreVisitorTest extends AnyFunSuite {
    /**  
     * Nessa classe tem q ser testado se a transformacao foi correta 
     * e se apos a transformacao os valores foram computados corretamente.
    **/

    /** ###### Loop Tests begin here ###### */
    test("Testing the loop_stmt01 evaluation after conversion to OberonCore") {
        val path = Paths.get(getClass.getClassLoader.getResource("stmts/loop_stmt01.oberon").toURI)

        assert(path != null)

        val content = String.join("\n", Files.readAllLines(path))
        val module = ScalaParser.parse(content)
        val interpreter = new Interpreter()
        val coreVisitor = new CoreVisitor()

        val coreModule = coreVisitor.transformModule(module)

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

        val coreModule = coreVisitor.transformModule(module)

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

    test("Testing the loop_stmt02 evaluation after conversion to OberonCore") {
        val path = Paths.get(getClass.getClassLoader.getResource("stmts/loop_stmt02.oberon").toURI)

        assert(path != null)

        val content = String.join("\n", Files.readAllLines(path))
        val module = ScalaParser.parse(content)
        val interpreter = new Interpreter()
        val coreVisitor = new CoreVisitor()
        
        val coreModule = coreVisitor.transformModule(module)

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
        
        val coreModule = coreVisitor.transformModule(module)

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

    test("Testing the loop_stmt03 evaluation after conversion to OberonCore") {
        val path = Paths.get(getClass.getClassLoader.getResource("stmts/loop_stmt03.oberon").toURI)

        assert(path != null)

        val content = String.join("\n", Files.readAllLines(path))
        val module = ScalaParser.parse(content)
        val interpreter = new Interpreter()
        val coreVisitor = new CoreVisitor()
        
        val coreModule = coreVisitor.transformModule(module)

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
        
        val coreModule = coreVisitor.transformModule(module)

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

        assert(stmts.head == AssignmentStmt("x",IntValue(0)))
        assert(stmts(1) == AssignmentStmt("y",IntValue(0)))
        assert(stmts(2) == WhileStmt(BoolValue(true),SequenceStmt(List(AssignmentStmt("x",AddExpression(VarExpression("x"),IntValue(1))), AssignmentStmt("i",IntValue(0)), WhileStmt(BoolValue(true), SequenceStmt(List(AssignmentStmt("y",AddExpression(VarExpression("y"),IntValue(1))), AssignmentStmt("i",AddExpression(VarExpression("i"),IntValue(1))), IfElseStmt(EQExpression(VarExpression("i"),IntValue(10)),ExitStmt(),None)))), IfElseStmt(EQExpression(VarExpression("x"),IntValue(10)),ExitStmt(),None)))))
    }

    /** ###### Loop Tests end here ###### */

    /** ###### RepeatUntil Tests begin here ###### */
    //TODO Checar negação da expressão no condicional do while
    test("Testing the RepeatUntilStmt01 evaluation after conversion to OberonCore") {
        val path = Paths.get(getClass.getClassLoader.getResource("stmts/RepeatUntilStmt01.oberon").toURI)

        assert(path != null)

        val content = String.join("\n", Files.readAllLines(path))
        val module = ScalaParser.parse(content)
        val interpreter = new Interpreter()
        val coreVisitor = new CoreVisitor()
        
        val coreModule = coreVisitor.transformModule(module)

        coreModule.accept(interpreter)

        assert(module.name == "RepeatUntilModule");
        assert(interpreter.env.lookup("x").contains(IntValue(11)));
        assert(interpreter.env.lookup("sum").contains(IntValue(55)));
    }

    test("Testing the RepeatUntilStmt01 expressions after conversion to While") {
        val path = Paths.get(getClass.getClassLoader.getResource("stmts/RepeatUntilStmt01.oberon").toURI)

        assert(path != null)

        val content = String.join("\n", Files.readAllLines(path))
        val module = ScalaParser.parse(content)
        val coreVisitor = new CoreVisitor()
        
        val coreModule = coreVisitor.transformModule(module)

        assert(coreModule.name == "RepeatUntilModule")
        assert (coreModule.stmt.isDefined)
        // assert that the main block contains a sequence of statements
        module.stmt.get match {
            case SequenceStmt(stmts) => assert(stmts.length == 4)
            case _ => fail("we are expecting three stmts in the main block")
        }
        // now we can assume that the main block contains a sequence of stmts
        val sequence = coreModule.stmt.get.asInstanceOf[SequenceStmt]
        val stmts = sequence.stmts

        assert(stmts.head == AssignmentStmt("x",IntValue(0)))
        assert(stmts(1) == AssignmentStmt("lim",IntValue(10)))
        assert(stmts(2) == WhileStmt(BoolValue(true),SequenceStmt(List(SequenceStmt(List(IfElseStmt(EQExpression(IntValue(0),VarExpression("x")),AssignmentStmt("sum",IntValue(0)),Some(AssignmentStmt("sum",AddExpression(VarExpression("sum"),VarExpression("x"))))), AssignmentStmt("x",AddExpression(VarExpression("x"),IntValue(1))))), IfElseStmt(GTExpression(VarExpression("x"),VarExpression("lim")),ExitStmt(),None)))))
        assert(stmts(3) == WriteStmt(VarExpression("sum")))
    }
    

    test("Testing the RepeatUntilStmt06 evaluation after conversion to OberonCore") {
        val path = Paths.get(getClass.getClassLoader.getResource("stmts/RepeatUntilStmt06.oberon").toURI)

        assert(path != null)

        val content = String.join("\n", Files.readAllLines(path))
        val module = ScalaParser.parse(content)
        val interpreter = new Interpreter()
        val coreVisitor = new CoreVisitor()
        
        val coreModule = coreVisitor.transformModule(module)

        coreModule.accept(interpreter)

        assert(module.name == "RepeatUntilModule");
        assert(interpreter.env.lookup("x").contains(IntValue(11)))
        assert(interpreter.env.lookup("y").contains(IntValue(40)))
    }

    test("Testing the RepeatUntilStmt06 expressions after conversion to While") {
        val path = Paths.get(getClass.getClassLoader.getResource("stmts/RepeatUntilStmt06.oberon").toURI)

        assert(path != null)

        val content = String.join("\n", Files.readAllLines(path))
        val module = ScalaParser.parse(content)
        val coreVisitor = new CoreVisitor()
        
        val coreModule = coreVisitor.transformModule(module)

        assert(coreModule.name == "RepeatUntilModule")
        assert (coreModule.stmt.isDefined)
        // assert that the main block contains a sequence of statements
        module.stmt.get match {
            case SequenceStmt(stmts) => assert(stmts.length == 4)
            case _ => fail("we are expecting three stmts in the main block")
        }
        // now we can assume that the main block contains a sequence of stmts
        val sequence = coreModule.stmt.get.asInstanceOf[SequenceStmt]
        val stmts = sequence.stmts

        assert(stmts.head == AssignmentStmt("x",IntValue(0)))
        assert(stmts(1) == AssignmentStmt("y",IntValue(0)))
        assert(stmts(2) == WhileStmt(BoolValue(true),SequenceStmt(List(SequenceStmt(List(IfElseStmt(GTExpression(VarExpression("x"),IntValue(5)),AssignmentStmt("y",AddExpression(VarExpression("y"),VarExpression("x"))),None), AssignmentStmt("x",AddExpression(VarExpression("x"),IntValue(1))))), IfElseStmt(GTExpression(VarExpression("x"),IntValue(10)),ExitStmt(),None)))))
        assert(stmts(3) == WriteStmt(VarExpression("y")))
    }

    /**RepeatUntil Test 07*/
    test("Testing the RepeatUntilStmt07 evaluation after conversion to OberonCore") {
        val path = Paths.get(getClass.getClassLoader.getResource("stmts/RepeatUntilStmt07.oberon").toURI)

        assert(path != null)

        val content = String.join("\n", Files.readAllLines(path))
        val module = ScalaParser.parse(content)
        val interpreter = new Interpreter()
        val coreVisitor = new CoreVisitor()
        
        val coreModule = coreVisitor.transformModule(module)
        
        coreModule.accept(interpreter)

        assert(module.name == "RepeatUntilModule");
        assert(interpreter.env.lookup("x").contains(IntValue(20)));
        assert(interpreter.env.lookup("y").contains(IntValue(20)));
    }

    test("Testing the RepeatUntilStmt07 expressions after conversion to While") {
        val path = Paths.get(getClass.getClassLoader.getResource("stmts/RepeatUntilStmt07.oberon").toURI)

        assert(path != null)

        val content = String.join("\n", Files.readAllLines(path))
        val module = ScalaParser.parse(content)
        val coreVisitor = new CoreVisitor()
        
        val coreModule = coreVisitor.transformModule(module)

        assert(coreModule.name == "RepeatUntilModule")
        assert (coreModule.stmt.isDefined)
        // assert that the main block contains a sequence of statements
        module.stmt.get match {
            case SequenceStmt(stmts) => assert(stmts.length == 4)
            case _ => fail("we are expecting three stmts in the main block")
        }
        // now we can assume that the main block contains a sequence of stmts
        val sequence = coreModule.stmt.get.asInstanceOf[SequenceStmt]
        val stmts = sequence.stmts

        assert(stmts.head == AssignmentStmt("x",IntValue(0)))
        assert(stmts(1) == AssignmentStmt("y",IntValue(20)))
        assert(stmts(2) == WhileStmt(BoolValue(true),SequenceStmt(List(AssignmentStmt("x",AddExpression(VarExpression("x"),IntValue(1))), IfElseStmt(EQExpression(VarExpression("x"),VarExpression("y")),ExitStmt(),None)))))
        assert(stmts(3) == WriteStmt(VarExpression("x")))
    }

    test("Testing the repeatuntil02 evaluation after conversion to OberonCore") {
        val path = Paths.get(getClass.getClassLoader.getResource("stmts/repeatuntil02.oberon").toURI)

        assert(path != null)

        val content = String.join("\n", Files.readAllLines(path))
        val module = ScalaParser.parse(content)
        val interpreter = new Interpreter()
        val coreVisitor = new CoreVisitor()
        
        val coreModule = coreVisitor.transformModule(module)

        coreModule.accept(interpreter)

        assert(module.name == "RepeatUntilModule");

        println(interpreter.env.lookup("x"))
        println(interpreter.env.lookup("y"))
        
        assert(interpreter.env.lookup("x").contains(IntValue(10)));
        assert(interpreter.env.lookup("y").contains(IntValue(19)));
    }

    test("Testing the repeatuntil02 expressions after conversion to While") {
        val path = Paths.get(getClass.getClassLoader.getResource("stmts/repeatuntil02.oberon").toURI)

        assert(path != null)

        val content = String.join("\n", Files.readAllLines(path))
        val module = ScalaParser.parse(content)
        val coreVisitor = new CoreVisitor()
        
        val coreModule = coreVisitor.transformModule(module)

        assert(coreModule.name == "RepeatUntilModule")
        assert (coreModule.stmt.isDefined)
        // assert that the main block contains a sequence of statements
        module.stmt.get match {
            case SequenceStmt(stmts) => assert(stmts.length == 5)
            case _ => fail("we are expecting three stmts in the main block")
        }
        // now we can assume that the main block contains a sequence of stmts
        val sequence = coreModule.stmt.get.asInstanceOf[SequenceStmt]
        val stmts = sequence.stmts

        assert(stmts.head == AssignmentStmt("x",IntValue(0)))
        assert(stmts(1) == AssignmentStmt("y",IntValue(0)))

        assert(stmts(2) == WhileStmt(BoolValue(true),SequenceStmt(List(SequenceStmt(List(AssignmentStmt("x",AddExpression(VarExpression("x"),IntValue(1))), WhileStmt(BoolValue(true),SequenceStmt(List(AssignmentStmt("y",AddExpression(VarExpression("y"),IntValue(1))), IfElseStmt(GTEExpression(VarExpression("y"),IntValue(10)),ExitStmt(),None)))))), IfElseStmt(GTEExpression(VarExpression("x"),IntValue(10)),ExitStmt(),None)))))
        assert(stmts(3) == WriteStmt(VarExpression("x")))
        assert(stmts(4) == WriteStmt(VarExpression("y")))
    }

    test("Testing the repeatuntil04 evaluation after conversion to OberonCore") {
        val path = Paths.get(getClass.getClassLoader.getResource("stmts/repeatuntil04.oberon").toURI)

        assert(path != null)

        val content = String.join("\n", Files.readAllLines(path))
        val module = ScalaParser.parse(content)
        val interpreter = new Interpreter()
        val coreVisitor = new CoreVisitor()
        
        val coreModule = coreVisitor.transformModule(module)

        coreModule.accept(interpreter)

        assert(module.name == "RepeatUntilModule");

        println(interpreter.env.lookup("x"))
        println(interpreter.env.lookup("y"))
        
        assert(interpreter.env.lookup("x").contains(IntValue(2)));
        assert(interpreter.env.lookup("y").contains(IntValue(2)));
    }

    test("Testing the repeatuntil04 expressions after conversion to While") {
        val path = Paths.get(getClass.getClassLoader.getResource("stmts/repeatuntil04.oberon").toURI)

        assert(path != null)

        val content = String.join("\n", Files.readAllLines(path))
        val module = ScalaParser.parse(content)
        val coreVisitor = new CoreVisitor()
        
        val coreModule = coreVisitor.transformModule(module)

        assert(coreModule.name == "RepeatUntilModule")
        assert (coreModule.stmt.isDefined)

        val sequence = coreModule.stmt.get.asInstanceOf[SequenceStmt]
        val stmts = sequence.stmts
        // assert that the main block contains a sequence of statements
        module.stmt.get match {
            case SequenceStmt(stmts) => assert(stmts.length == 3)
            case _ => fail("we are expecting three stmts in the main block")
        }
        // now we can assume that the main block contains a sequence of stmts
        assert(stmts.head == AssignmentStmt("x",IntValue(2)))
        assert(stmts(1) == AssignmentStmt("y",IntValue(2)))
        assert(stmts(2) == WriteStmt(FunctionCallExpression("power",List(VarExpression("x"), VarExpression("y")))))
    }
    /** ###### RepeatUntil Tests end here ###### */

    /** ###### For Tests begin here ###### */
    test("Testing the interpreter_stmt01 conversion to OberonCore") {
        val path = Paths.get(getClass.getClassLoader.getResource("stmts/interpreter_stmt01.oberon").toURI)

        assert(path != null)

        val content = String.join("\n", Files.readAllLines(path))
        val module = ScalaParser.parse(content)
        val interpreter = new Interpreter()
        val coreVisitor = new CoreVisitor()
        
        val coreModule = coreVisitor.transformModule(module)

        coreModule.accept(interpreter)

        assert(module.name == "SimpleModule")

        assert(interpreter.env.lookup("x") == Some(IntValue(5))) // FOR TO x
        assert(interpreter.env.lookup("y") == Some(IntValue(6))) // y = x + 1 (after last FOR)
        assert(interpreter.env.lookup("z") == Some(IntValue(15))) // z = result
    }

    test("Testing the stmt08 conversion to OberonCore") {
        val path = Paths.get(getClass.getClassLoader.getResource("stmts/stmtForCore01.oberon").toURI)

        assert(path != null)

        val content = String.join("\n", Files.readAllLines(path))
        val module = ScalaParser.parse(content)
        val interpreter = new Interpreter()
        val coreVisitor = new CoreVisitor()

        val coreModule = coreVisitor.transformModule(module)

        coreModule.accept(interpreter)

        assert(module.name == "SimpleModule")
        
        assert(interpreter.env.lookup("x") == Some(IntValue(20))) // FOR TO x
        assert(interpreter.env.lookup("k") == Some(IntValue(27))) // k = result
    }
    /** ###### For Tests end here ###### */


    /** ###### IfElseIf Tests begin here ###### */
    test("Testing the IfElseIfStmt01 conversion to OberonCore") {
        val path = Paths.get(getClass.getClassLoader.getResource("stmts/IfElseIfStmt01.oberon").toURI)

        assert(path != null)

        val content = String.join("\n", Files.readAllLines(path))
        val module = ScalaParser.parse(content)
        val interpreter = new Interpreter()
        val coreVisitor = new CoreVisitor()

        val coreModule = coreVisitor.transformModule(module)

        coreModule.accept(interpreter)

        assert(module.name == "SimpleModule")

        assert(interpreter.env.lookup("y") == Some(IntValue(1)));
    }

    test("Testing the IfElseIfStmt03 conversion to OberonCore") {
        val path = Paths.get(getClass.getClassLoader.getResource("stmts/IfElseIfStmt03.oberon").toURI)

        assert(path != null)

        val content = String.join("\n", Files.readAllLines(path))
        val module = ScalaParser.parse(content)
        val interpreter = new Interpreter()
        val coreVisitor = new CoreVisitor()
        
        val coreModule = coreVisitor.transformModule(module)
        
        coreModule.accept(interpreter)

        assert(module.name == "SimpleModule")

        assert(interpreter.env.lookup("x") == Some(IntValue(10)));
        assert(interpreter.env.lookup("y") == Some(IntValue(3)));
    }

    test("Testing the IfElseIfStmt05 conversion to OberonCore") {
        val path = Paths.get(getClass.getClassLoader.getResource("stmts/IfElseIfStmt05.oberon").toURI)

        assert(path != null)

        val content = String.join("\n", Files.readAllLines(path))
        val module = ScalaParser.parse(content)
        val interpreter = new Interpreter()
        val coreVisitor = new CoreVisitor()
        
        val coreModule = coreVisitor.transformModule(module)
        
        coreModule.accept(interpreter)

        assert(module.name == "SimpleModule")

        assert(interpreter.env.lookup("x") == Some(IntValue(55)));
        assert(interpreter.env.lookup("y") == Some(IntValue(5)));
    }
    /** ###### IfElseIf Tests begin here ###### */


    /** ###### Case Tests begin here ###### */
    test("Testing the stmt06 conversion to OberonCore") {
        val path = Paths.get(getClass.getClassLoader.getResource("stmts/stmt06.oberon").toURI)

        assert(path != null)

        val content = String.join("\n", Files.readAllLines(path))
        val module = ScalaParser.parse(content)
        val interpreter = new Interpreter()
        val coreVisitor = new CoreVisitor()
        
        val coreModule = coreVisitor.transformModule(module)

        /* TODO Reescrever o teste 18 alterando o readint para um atribuição fixa
         ou encontrar uma forma do readint funcionar

        coreModule.accept(interpreter) */
        assert(module.name == "SimpleModule")


    }

    test("Testing the stmt18 conversion to OberonCore") {
        val path = Paths.get(getClass.getClassLoader.getResource("stmts/stmt18.oberon").toURI)

        assert(path != null)

        val content = String.join("\n", Files.readAllLines(path))
        val module = ScalaParser.parse(content)
        val interpreter = new Interpreter()
        val coreVisitor = new CoreVisitor()
        
        val coreModule = coreVisitor.transformModule(module)

        /* TODO Reescrever o teste 18 alterando o readint para um atribuição fixa
         ou encontrar uma forma do readint funcionar
        
        coreModule.accept(interpreter)

        assert(module.name == "SimpleRangeCaseModule")
        assert(interpreter.env.lookup("xs") == Some(IntValue(20)));
        */
    }
    /** ###### Case Tests end here ###### */

}
