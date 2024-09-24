package br.unb.cic.oberon.tc

import br.unb.cic.oberon.AbstractTestSuite

import java.nio.file.{Files, Paths}
import br.unb.cic.oberon.ir.ast._
import br.unb.cic.oberon.parser.ScalaParser
import br.unb.cic.oberon.parser.Oberon2ScalaParser
import org.scalatest.funsuite.AnyFunSuite
import br.unb.cic.oberon.transformations.{CoreChecker, CoreTransformer}
import br.unb.cic.oberon.ir.ast.{OberonModule, VariableDeclaration}
import br.unb.cic.oberon.environment.Environment
import org.scalatest.flatspec.AnyFlatSpec

import scala.collection.mutable.{Map, Stack, ListBuffer}

class TypeCheckerTestSuite extends AnyFunSuite with Oberon2ScalaParser {

  test("Check expression on atomic values") {
    val atomicValues = List(
      (BoolValue(true), BooleanType),
      (IntValue(1), IntegerType),
      (RealValue(1.3), RealType),
      (CharValue('a'), CharacterType),
      (StringValue("abc"), StringType)
    )

    for ((atom, t) <- atomicValues) {
      assert(
        TypeChecker
          .checkExpression(atom)
          .runA(new Environment[Type]()) == Right(t)
      )
    }
  }

  test("Check var expression") {
    assert(
      TypeChecker
        .checkExpression(VarExpression("abc"))
        .runA(new Environment[Type]())
        .isLeft
    )

    assert(
      TypeChecker
        .checkExpression(VarExpression("abc"))
        .runA(
          new Environment[Type](
            locations = Map(
              Location(0) -> IntegerType
            ),
            global = Map(
              "abc" -> Location(0)
            )
          )
        ) == Right(IntegerType)
    )

    assert(
      TypeChecker
        .checkExpression(VarExpression("abc"))
        .runA(
          new Environment[Type](
            locations = Map(
              Location(0) -> IntegerType
            ),
            stack = Stack(
              Map(
                "abc" -> Location(0)
              )
            )
          )
        ) == Right(IntegerType)
    )
  }

  test("Check comparable operations") {
    val env = new Environment[Type]()
    val correctExpressions = List(
      GTExpression(IntValue(1), IntValue(3)),
      GTExpression(RealValue(3), RealValue(1)),
      LTExpression(IntValue(1), IntValue(3)),
      LTExpression(RealValue(3), RealValue(1)),
      GTEExpression(IntValue(1), IntValue(3)),
      GTEExpression(RealValue(3), RealValue(1)),
      LTEExpression(IntValue(1), IntValue(3)),
      LTEExpression(RealValue(3), RealValue(1))
    )

    for (exp <- correctExpressions) {
      assert(
        TypeChecker
          .checkExpression(exp)
          .runA(env) == Right(BooleanType)
      )
    }

    val wrongExpressions = List(
      GTExpression(IntValue(1), RealValue(3)),
      GTExpression(RealValue(3), IntValue(1)),
      GTExpression(BoolValue(true), BoolValue(true)),
      LTExpression(IntValue(1), RealValue(3)),
      LTExpression(RealValue(3), IntValue(1)),
      LTExpression(BoolValue(true), BoolValue(true)),
      GTEExpression(IntValue(1), RealValue(3)),
      GTEExpression(RealValue(3), IntValue(1)),
      GTEExpression(BoolValue(true), BoolValue(true)),
      LTEExpression(IntValue(1), RealValue(3)),
      LTEExpression(RealValue(3), IntValue(1)),
      LTEExpression(BoolValue(true), BoolValue(true))
    )

    for (exp <- wrongExpressions) {
      assert(
        TypeChecker
          .checkExpression(exp)
          .runA(new Environment[Type]())
          .isLeft
      )
    }

    assert(
      TypeChecker
        .checkExpression(GTExpression(VarExpression("abc"), IntValue(2)))
        .runA(
          new Environment[Type](
            locations = Map(
              Location(0) -> IntegerType
            ),
            stack = Stack(
              Map(
                "abc" -> Location(0)
              )
            )
          )
        ) == Right(BooleanType)
    )
  }

  test("Check numeric operations") {
    val env = new Environment[Type]()
    val correctExpressions = List(
      (IntValue(1), IntValue(3), IntegerType),
      (RealValue(3), RealValue(1), RealType)
    )

    for ((l, r, t) <- correctExpressions) {
      for (
        exp <- List(
          AddExpression(l, r),
          SubExpression(l, r),
          MultExpression(l, r),
          DivExpression(l, r)
        )
      ) {
        assert(
          TypeChecker
            .checkExpression(exp)
            .runA(env) == Right(t)
        )
      }
    }

    val wrongExpressions = List(
      (IntValue(1), RealValue(3)),
      (RealValue(3), IntValue(1)),
      (BoolValue(true), BoolValue(false))
    )

    for ((l, r) <- wrongExpressions) {
      for (
        exp <- List(
          AddExpression(l, r),
          SubExpression(l, r),
          MultExpression(l, r),
          DivExpression(l, r)
        )
      ) {
        assert(TypeChecker.checkExpression(exp).runA(env).isLeft)
      }
    }

    assert(
      TypeChecker
        .checkExpression(AddExpression(VarExpression("abc"), IntValue(2)))
        .runA(
          new Environment[Type](
            locations = Map(
              Location(0) -> IntegerType
            ),
            stack = Stack(
              Map(
                "abc" -> Location(0)
              )
            )
          )
        ) == Right(IntegerType)
    )

    assert(
      TypeChecker
        .checkExpression(AddExpression(VarExpression("abc"), RealValue(2)))
        .runA(
          new Environment[Type](
            locations = Map(
              Location(0) -> RealType
            ),
            stack = Stack(
              Map(
                "abc" -> Location(0)
              )
            )
          )
        ) == Right(RealType)
    )
  }

  test("Check boolean operations") {
    val env = new Environment[Type]()

    assert(
      TypeChecker
        .checkExpression(AndExpression(BoolValue(true), BoolValue(false)))
        .runA(env) == Right(BooleanType)
    )

    assert(
      TypeChecker
        .checkExpression(OrExpression(BoolValue(true), BoolValue(false)))
        .runA(env) == Right(BooleanType)
    )

    assert(
      TypeChecker
        .checkExpression(AndExpression(IntValue(1), BoolValue(false)))
        .runA(env)
        .isLeft
    )

    assert(
      TypeChecker
        .checkExpression(OrExpression(IntValue(1), IntValue(2)))
        .runA(env)
        .isLeft
    )

    assert(
      TypeChecker
        .checkExpression(AndExpression(VarExpression("abc"), BoolValue(false)))
        .runA(
          new Environment[Type](
            locations = Map(
              Location(0) -> BooleanType
            ),
            stack = Stack(
              Map(
                "abc" -> Location(0)
              )
            )
          )
        ) == Right(BooleanType)
    )
  }

  test("Check function calls") {
    val env = new Environment[Type](procedures =
      Map(
        "abc" -> Procedure(
          name = "abc",
          args = List(
            ParameterByValue("a", IntegerType),
            ParameterByReference("b", BooleanType),
            ParameterByValue("c", CharacterType)
          ),
          returnType = Some(RealType),
          constants = List(),
          variables = List(),
          stmt = SequenceStmt(List())
        )
      )
    )

    assert(
      TypeChecker
        .checkExpression(
          FunctionCallExpression(
            "abc",
            List(IntValue(1), BoolValue(false), CharValue('a'))
          )
        )
        .runA(env) == Right(RealType)
    )

    assert(
      TypeChecker
        .checkExpression(
          FunctionCallExpression(
            "cde",
            List(IntValue(1), BoolValue(false), CharValue('a'))
          )
        )
        .runA(env)
        .isLeft
    )

    assert(
      TypeChecker
        .checkExpression(
          FunctionCallExpression(
            "abc",
            List(RealValue(1.0), BoolValue(false), CharValue('a'))
          )
        )
        .runA(env)
        .isLeft
    )

    assert(
      TypeChecker
        .checkExpression(
          FunctionCallExpression(
            "abc",
            List(IntValue(1), BoolValue(false), StringValue("a"))
          )
        )
        .runA(env)
        .isLeft
    )
  }

  test("Check array definition") {
    val env = new Environment[Type]()

    assert(
      TypeChecker
        .checkExpression(
          ArrayValue(
            ListBuffer(IntValue(1), IntValue(2), IntValue(3)),
            ArrayType(3, IntegerType)
          )
        )
        .runA(env) == Right(ArrayType(3, IntegerType))
    )
  }

  test("Check array subscript") {
    val env = new Environment[Type]()
    val arr = ArrayValue(
      ListBuffer(RealValue(1.0), RealValue(2.0), RealValue(3.0)),
      ArrayType(3, RealType)
    )

    assert(
      TypeChecker
        .checkExpression(ArraySubscript(arr, IntValue(0)))
        .runA(env) == Right(RealType)
    )

    assert(
      TypeChecker
        .checkExpression(ArraySubscript(arr, RealValue(0.0)))
        .runA(env)
        .isLeft
    )

    assert(
      TypeChecker
        .checkExpression(ArraySubscript(IntValue(3), RealValue(0.0)))
        .runA(env)
        .isLeft
    )
  }

  test("Check pointer access") {
    val env = new Environment[Type](
      locations = Map(
        Location(0) -> PointerType(IntegerType)
      ),
      stack = Stack(
        Map(
          "abc" -> Location(0)
        )
      )
    )

    assert(
      TypeChecker
        .checkExpression(PointerAccessExpression("abc"))
        .runA(env) == Right(IntegerType)
    )
  }

  test("Check lambda expression") {
    val env = new Environment[Type]()

    assert(
      TypeChecker
        .checkExpression(
          LambdaExpression(
            List(
              ParameterByReference("x", IntegerType),
              ParameterByReference("y", IntegerType)
            ),
            GTExpression(VarExpression("x"), VarExpression("y"))
          )
        )
        .runA(env) == Right(BooleanType)
    )

    assert(
      TypeChecker
        .checkExpression(
          LambdaExpression(
            List(
              ParameterByReference("x", IntegerType),
              ParameterByReference("y", IntegerType)
            ),
            GTExpression(VarExpression("x"), VarExpression("z"))
          )
        )
        .runA(env)
        .isLeft
    )
  }

  test("Test expression type on simple values") {
    val env = new Environment[Type]()
    val val10 = IntValue(10)
    val bTrue = BoolValue(true)
    val bFalse = BoolValue(false)

    assert(
      TypeChecker
        .checkExpression(val10)
        .runA(env) == Right(IntegerType)
    )
    assert(
      TypeChecker
        .checkExpression(bTrue)
        .runA(env) == Right(BooleanType)
    )
    assert(
      TypeChecker
        .checkExpression(bFalse)
        .runA(env) == Right(BooleanType)
    )
  }

  test("Test expression type on add expressions") {
    val env = new Environment[Type]()
    val val10 = IntValue(10)
    val val20 = IntValue(20)
    val add01 = AddExpression(val10, val20)
    val add02 = AddExpression(val10, add01)
    val add03 = AddExpression(add01, add02)

    assert(TypeChecker.checkExpression(add01).runA(env) == Right(IntegerType))
    assert(TypeChecker.checkExpression(add02).runA(env) == Right(IntegerType))
    assert(TypeChecker.checkExpression(add03).runA(env) == Right(IntegerType))
  }

  test("Test expression type on sub expressions") {
    val env = new Environment[Type]()
    val val10 = IntValue(10)
    val val20 = IntValue(20)
    val sub01 = SubExpression(val10, val20)
    val sub02 = SubExpression(val10, sub01)
    val sub03 = SubExpression(sub01, sub02)

    assert(
      TypeChecker
        .checkExpression(sub01)
        .runA(env) == Right(IntegerType)
    )
    assert(
      TypeChecker
        .checkExpression(sub02)
        .runA(env) == Right(IntegerType)
    )
    assert(
      TypeChecker
        .checkExpression(sub03)
        .runA(env) == Right(IntegerType)
    )
  }

  test("Test expression type on div expressions") {
    val env = new Environment[Type]()
    val val10 = IntValue(10)
    val val20 = IntValue(20)
    val sub01 = DivExpression(val10, val20)
    val sub02 = DivExpression(val10, sub01)
    val sub03 = DivExpression(sub01, sub02)

    assert(
      TypeChecker
        .checkExpression(sub01)
        .runA(env) == Right(IntegerType)
    )
    assert(
      TypeChecker
        .checkExpression(sub02)
        .runA(env) == Right(IntegerType)
    )
    assert(
      TypeChecker
        .checkExpression(sub03)
        .runA(env) == Right(IntegerType)
    )
  }

  test("Test expression type on mult expressions") {
    val env = new Environment[Type]()
    val val10 = IntValue(10)
    val val20 = IntValue(20)
    val mult01 = AddExpression(val10, val20)
    val mult02 = AddExpression(val10, mult01)
    val mult03 = AddExpression(mult01, mult02)

    assert(
      TypeChecker
        .checkExpression(mult01)
        .runA(env) == Right(IntegerType)
    )
    assert(
      TypeChecker
        .checkExpression(mult02)
        .runA(env) == Right(IntegerType)
    )
    assert(
      TypeChecker
        .checkExpression(mult03)
        .runA(env) == Right(IntegerType)
    )
  }

  test("Test expression type on eq expressions") {
    val env = new Environment[Type]()
    val val10 = IntValue(10)
    val val20 = IntValue(20)
    val eq01 = EQExpression(val10, val20)

    assert(
      TypeChecker
        .checkExpression(eq01)
        .runA(env) == Right(BooleanType)
    )
  }

  test("Test expression add with boolean values") {
    val env = new Environment[Type]()
    val val10 = IntValue(10)
    val valTrue = BoolValue(true)
    val invalidAdd = AddExpression(val10, valTrue)

    assert(
      TypeChecker
        .checkExpression(invalidAdd)
        .runA(env)
        .isLeft
    )
  }

  // End

  test("Check assignment stmts") {
    // Can't test records currently

    assert(
      TypeChecker
        .checkStmt(AssignmentStmt(VarAssignment("abc"), RealValue(0.0)))
        .runA(
          new Environment[Type](
            locations = Map(
              Location(0) -> RealType
            ),
            stack = Stack(
              Map(
                "abc" -> Location(0)
              )
            )
          )
        ) == Right(NullType)
    )

    assert(
      TypeChecker
        .checkStmt(AssignmentStmt(VarAssignment("abc"), IntValue(0)))
        .runA(
          new Environment[Type](
            locations = Map(
              Location(0) -> RealType
            ),
            stack = Stack(
              Map(
                "abc" -> Location(0)
              )
            )
          )
        )
        .isLeft
    )

    assert(
      TypeChecker
        .checkStmt(
          AssignmentStmt(
            ArrayAssignment(
              ArrayValue(ListBuffer(), ArrayType(3, RealType)),
              IntValue(0)
            ),
            RealValue(0.0)
          )
        )
        .runA(new Environment[Type]()) == Right(NullType)
    )

    assert(
      TypeChecker
        .checkStmt(
          AssignmentStmt(
            ArrayAssignment(
              ArrayValue(ListBuffer(), ArrayType(3, CharacterType)),
              IntValue(0)
            ),
            RealValue(0.0)
          )
        )
        .runA(new Environment[Type]())
        .isLeft
    )

    assert(
      TypeChecker
        .checkStmt(AssignmentStmt(PointerAssignment("abc"), RealValue(0.0)))
        .runA(
          new Environment[Type](
            locations = Map(
              Location(0) -> PointerType(RealType)
            ),
            stack = Stack(
              Map(
                "abc" -> Location(0)
              )
            )
          )
        ) == Right(NullType)
    )

    assert(
      TypeChecker
        .checkStmt(AssignmentStmt(PointerAssignment("abc"), RealValue(0.0)))
        .runA(
          new Environment[Type](
            locations = Map(
              Location(0) -> PointerType(IntegerType)
            ),
            stack = Stack(
              Map(
                "abc" -> Location(0)
              )
            )
          )
        )
        .isLeft
    )
  }

  test("Check sequence stmt") {
    assert(
      TypeChecker
        .checkStmt(
          SequenceStmt(
            List(
              AssignmentStmt(VarAssignment("abc"), RealValue(0.0)),
              AssignmentStmt(VarAssignment("abc"), RealValue(1.0)),
              AssignmentStmt(VarAssignment("bcd"), IntValue(0))
            )
          )
        )
        .runA(
          new Environment[Type](
            locations = Map(
              Location(0) -> RealType,
              Location(1) -> IntegerType
            ),
            stack = Stack(
              Map(
                "abc" -> Location(0),
                "bcd" -> Location(1)
              )
            )
          )
        ) == Right(NullType)
    )

    assert(
      TypeChecker
        .checkStmt(
          SequenceStmt(
            List(
              AssignmentStmt(VarAssignment("abc"), RealValue(0.0)),
              AssignmentStmt(VarAssignment("abc"), RealValue(1.0)),
              AssignmentStmt(VarAssignment("bcd"), IntValue(0))
            )
          )
        )
        .runA(
          new Environment[Type](
            locations = Map(
              Location(0) -> RealType,
              Location(1) -> IntegerType
            ),
            stack = Stack(
              Map(
                "abc" -> Location(0)
              )
            )
          )
        )
        .isLeft
    )
  }

  test("Check read stmts") {
    assert(
      TypeChecker
        .checkStmt(ReadLongRealStmt("abc"))
        .runA(
          new Environment[Type](
            locations = Map(
              Location(0) -> RealType
            ),
            stack = Stack(
              Map(
                "abc" -> Location(0)
              )
            )
          )
        ) == Right(NullType)
    )

    assert(
      TypeChecker
        .checkStmt(ReadLongRealStmt("abc"))
        .runA(
          new Environment[Type](
            locations = Map(
              Location(0) -> RealType
            ),
            global = Map(
              "abc" -> Location(0)
            )
          )
        ) == Right(NullType)
    )

    assert(
      TypeChecker
        .checkStmt(ReadLongRealStmt("abc"))
        .runA(new Environment[Type]())
        .isLeft
    )

    assert(
      TypeChecker
        .checkStmt(ReadLongRealStmt("abc"))
        .runA(
          new Environment[Type](
            locations = Map(
              Location(0) -> IntegerType
            ),
            global = Map(
              "abc" -> Location(0)
            )
          )
        )
        .isLeft
    )
  }

  test("Check write stmt") {
    val env = new Environment[Type]()

    assert(
      TypeChecker
        .checkStmt(WriteStmt(IntValue(1)))
        .runA(env) == Right(NullType)
    )
  }

  // ------

  test("Test read int statement type checker") {
    val env = new Environment[Type]()
      .setGlobalVariable("x", IntegerType)
      .setGlobalVariable("y", IntegerType)
    val read01 = ReadIntStmt("x")
    val read02 = ReadIntStmt("y")

    assert(TypeChecker.checkStmt(read01).runA(env) == Right(NullType))
    assert(TypeChecker.checkStmt(read02).runA(env) == Right(NullType))
  }

  test("Test read real statement type checker") {
    val env = new Environment[Type]().setGlobalVariable("x", RealType)
    val read01 = ReadRealStmt("x")
    val read02 = ReadRealStmt("y")

    assert(TypeChecker.checkStmt(read01).runA(env) == Right(NullType))
    assert(TypeChecker.checkStmt(read02).runA(env).isLeft)
  }

  test("Test write statement type checker") {
    val env = new Environment[Type]()
    val write01 = WriteStmt(IntValue(5))
    val write02 = WriteStmt(AddExpression(IntValue(5), BoolValue(false)))

    assert(TypeChecker.checkStmt(write01).runA(env) == Right(NullType))
    assert(TypeChecker.checkStmt(write02).runA(env).isLeft)
  }

  test("Test assignment statement type checker") {
    val env = new Environment[Type]().setGlobalVariable("x", IntegerType)
    val stmt01 = AssignmentStmt(VarAssignment("x"), IntValue(10))
    val stmt02 =
      AssignmentStmt(VarAssignment("y"), IntValue(10)) // invalid stmt
    val stmt03 = AssignmentStmt(
      VarAssignment("x"),
      AddExpression(IntValue(5), BoolValue(false))
    )

    assert(TypeChecker.checkStmt(stmt01).runA(env) == Right(NullType))
    assert(TypeChecker.checkStmt(stmt02).runA(env).isLeft)
    assert(TypeChecker.checkStmt(stmt03).runA(env).isLeft)
  }

  test("Test a sequence of statements type checker") {
    val env = new Environment[Type]().setGlobalVariable("x", IntegerType)
    val stmt01 = AssignmentStmt(VarAssignment("x"), IntValue(10))
    val stmt02 =
      AssignmentStmt(VarAssignment("y"), IntValue(10)) // invalid stmt
    val stmt03 = AssignmentStmt(
      VarAssignment("x"),
      AddExpression(IntValue(5), BoolValue(false))
    ) // invalid stmt
    val stmt04 = WriteStmt(VarExpression("x"))
    val stmt05 = WriteStmt(VarExpression("y"))

    assert(TypeChecker.checkStmt(stmt01).runA(env) == Right(NullType))
    assert(TypeChecker.checkStmt(stmt02).runA(env).isLeft)
    assert(TypeChecker.checkStmt(stmt03).runA(env).isLeft)

    val seq1 = SequenceStmt(List(stmt01, stmt04))
    val seq2 = SequenceStmt(List(stmt01, stmt05))

    assert(TypeChecker.checkStmt(seq1).runA(env) == Right(NullType))
    assert(TypeChecker.checkStmt(seq2).runA(env).isLeft)
  }

  test("Test if-else statement type checker (with invalid condition)") {
    val env = new Environment[Type]().setGlobalVariable("x", IntegerType)
    val stmt01 = AssignmentStmt(VarAssignment("x"), IntValue(10))
    val stmt02 = IfElseStmt(IntValue(10), stmt01, None)

    assert(TypeChecker.checkStmt(stmt01).runA(env) == Right(NullType))
    assert(TypeChecker.checkStmt(stmt02).runA(env).isLeft)
  }

  test("Test if-else statement type checker (with invalid then-stmt)") {
    val env = new Environment[Type]()
    val stmt01 = AssignmentStmt(VarAssignment("x"), IntValue(10))
    val stmt02 = IfElseStmt(BoolValue(true), stmt01, None)

    assert(TypeChecker.checkStmt(stmt01).runA(env).isLeft)
    assert(TypeChecker.checkStmt(stmt02).runA(env).isLeft)
  }

  test(
    "Test if-else statement type checker (with invalid then-stmt and else-stmt)"
  ) {
    val env = new Environment[Type]()
    val stmt01 = AssignmentStmt(VarAssignment("x"), IntValue(10))
    val stmt02 = AssignmentStmt(VarAssignment("y"), IntValue(10))
    val stmt03 = IfElseStmt(BoolValue(true), stmt01, Some(stmt02))

    assert(TypeChecker.checkStmt(stmt01).runA(env).isLeft)
    assert(TypeChecker.checkStmt(stmt02).runA(env).isLeft)
    assert(TypeChecker.checkStmt(stmt03).runA(env).isLeft)
  }

  test("Test if-else statement type checker") {
    val env = new Environment[Type]()
      .setGlobalVariable("x", IntegerType)
      .setGlobalVariable("y", IntegerType)
    val stmt01 = AssignmentStmt(VarAssignment("x"), IntValue(10))
    val stmt02 = AssignmentStmt(VarAssignment("y"), IntValue(10))
    val stmt03 = IfElseStmt(BoolValue(true), stmt01, Some(stmt02))

    assert(TypeChecker.checkStmt(stmt01).runA(env) == Right(NullType))
    assert(TypeChecker.checkStmt(stmt02).runA(env) == Right(NullType))
    assert(TypeChecker.checkStmt(stmt03).runA(env) == Right(NullType))
  }

  test("Test if-else-if statment type checker (invalid condition 'if')") {
    val env = new Environment[Type]()
      .setGlobalVariable("x", IntegerType)
      .setGlobalVariable("z", IntegerType)
    val stmt01 = AssignmentStmt(VarAssignment("x"), IntValue(20))
    val stmt02 = AssignmentStmt(VarAssignment("z"), IntValue(30))

    val stmt03 = ElseIfStmt(BoolValue(true), stmt02)
    val list1 = List(stmt03)

    val stmt04 = CoreTransformer.reduceToCoreStatement(
      IfElseIfStmt(IntValue(34), stmt01, list1, None)
    )

    assert(TypeChecker.checkStmt(stmt01).runA(env) == Right(NullType))
    assert(TypeChecker.checkStmt(stmt02).runA(env) == Right(NullType))
    assert(TypeChecker.checkStmt(stmt04).runA(env).isLeft)
  }

  test("Test else-if statment type checker (invalid condition 'else-if')") {
    val env = new Environment[Type]()
      .setGlobalVariable("x", IntegerType)
      .setGlobalVariable("z", IntegerType)

    val stmt01 = AssignmentStmt(VarAssignment("x"), IntValue(40))
    val stmt02 = AssignmentStmt(VarAssignment("z"), IntValue(100))

    val stmt03 = ElseIfStmt(IntValue(70), stmt02)
    val list1 = List(stmt03)

    val stmt04 = CoreTransformer.reduceToCoreStatement(
      IfElseIfStmt(BoolValue(true), stmt01, list1, None)
    )

    assert(TypeChecker.checkStmt(stmt01).runA(env) == Right(NullType))
    assert(TypeChecker.checkStmt(stmt02).runA(env) == Right(NullType))
    assert(TypeChecker.checkStmt(stmt04).runA(env).isLeft)
  }

  test(
    "Test else-if statment type checker (invalid condition list 'else-if')"
  ) {
    val env = new Environment[Type]()
      .setGlobalVariable("x", IntegerType)
      .setGlobalVariable("z", IntegerType)
    val stmt01 = AssignmentStmt(VarAssignment("x"), IntValue(40))
    val stmt02 = AssignmentStmt(VarAssignment("z"), IntValue(100))

    val stmt03 = ElseIfStmt(BoolValue(true), stmt02)
    val stmt04 = ElseIfStmt(IntValue(73), stmt02)
    val stmt05 = ElseIfStmt(IntValue(58), stmt01)
    val stmt06 = ElseIfStmt(BoolValue(false), stmt01)
    val list1 = List(stmt03, stmt04, stmt05, stmt06)

    val stmt07 = CoreTransformer.reduceToCoreStatement(
      IfElseIfStmt(BoolValue(true), stmt01, list1, None)
    )

    assert(TypeChecker.checkStmt(stmt01).runA(env) == Right(NullType))
    assert(TypeChecker.checkStmt(stmt02).runA(env) == Right(NullType))
    assert(TypeChecker.checkStmt(stmt07).runA(env).isLeft)
  }

  test("Test else-if statment type checker (invalid then-stmt 'else-if')") {
    val env = new Environment[Type]().setGlobalVariable("x", IntegerType)
    val stmt01 = AssignmentStmt(VarAssignment("x"), IntValue(40))
    val stmt02 = AssignmentStmt(VarAssignment("z"), IntValue(100))

    val stmt03 = ElseIfStmt(BoolValue(true), stmt02)
    val list1 = List(stmt03)

    val stmt04 = CoreTransformer.reduceToCoreStatement(
      IfElseIfStmt(BoolValue(true), stmt01, list1, None)
    )

    assert(TypeChecker.checkStmt(stmt01).runA(env) == Right(NullType))
    assert(TypeChecker.checkStmt(stmt02).runA(env).isLeft)
    assert(TypeChecker.checkStmt(stmt04).runA(env).isLeft)
  }

  test("Test if-else-if statment type checker (invalid else-stmt)") {
    val env = new Environment[Type]()
      .setGlobalVariable("x", IntegerType)
      .setGlobalVariable("z", IntegerType)
    val stmt01 = AssignmentStmt(VarAssignment("x"), IntValue(40))
    val stmt02 = AssignmentStmt(VarAssignment("z"), IntValue(100))
    val stmt03 = AssignmentStmt(VarAssignment("w"), IntValue(20))

    val stmt04 = ElseIfStmt(BoolValue(true), stmt02)
    val list1 = List(stmt04)

    val stmt05 = CoreTransformer.reduceToCoreStatement(
      IfElseIfStmt(BoolValue(true), stmt01, list1, Some(stmt03))
    )

    assert(TypeChecker.checkStmt(stmt01).runA(env) == Right(NullType))
    assert(TypeChecker.checkStmt(stmt02).runA(env) == Right(NullType))
    assert(TypeChecker.checkStmt(stmt03).runA(env).isLeft)
    assert(TypeChecker.checkStmt(stmt05).runA(env).isLeft)
  }

  test(
    "Test if-else-if statment type checker (invalid then-stmt, 'else-if' then-stmt, 'else-if' invalid condition and else-stmt)"
  ) {
    val env = new Environment[Type]()
    val stmt01 = AssignmentStmt(VarAssignment("x"), IntValue(40))
    val stmt02 = AssignmentStmt(VarAssignment("z"), IntValue(100))
    val stmt03 = AssignmentStmt(VarAssignment("w"), IntValue(20))

    val stmt04 = ElseIfStmt(IntValue(56), stmt02)
    val stmt05 = ElseIfStmt(IntValue(79), stmt01)
    val stmt06 = ElseIfStmt(BoolValue(true), stmt02)
    val list1 = List(stmt04, stmt05, stmt06)

    val stmt07 = CoreTransformer.reduceToCoreStatement(
      IfElseIfStmt(BoolValue(true), stmt01, list1, Some(stmt03))
    )

    assert(TypeChecker.checkStmt(stmt01).runA(env).isLeft)
    assert(TypeChecker.checkStmt(stmt02).runA(env).isLeft)
    assert(TypeChecker.checkStmt(stmt03).runA(env).isLeft)
    assert(TypeChecker.checkStmt(stmt07).runA(env).isLeft)
  }

  test("Test if-else-if statment type checker") {
    val env = new Environment[Type]()
      .setGlobalVariable("x", IntegerType)
      .setGlobalVariable("y", IntegerType)
    val stmt01 = AssignmentStmt(VarAssignment("x"), IntValue(15))
    val stmt02 = AssignmentStmt(VarAssignment("y"), IntValue(5))

    val stmt03 = ElseIfStmt(BoolValue(true), stmt02)
    val list1 = List(stmt03)

    val stmt04 = CoreTransformer.reduceToCoreStatement(
      IfElseIfStmt(BoolValue(true), stmt01, list1, None)
    )

    assert(TypeChecker.checkStmt(stmt01).runA(env) == Right(NullType))
    assert(TypeChecker.checkStmt(stmt02).runA(env) == Right(NullType))
    assert(TypeChecker.checkStmt(stmt04).runA(env) == Right(NullType))
  }

  test("Test while statement type checker (with invalid condition)") {
    val env = new Environment[Type]().setGlobalVariable("x", IntegerType)
    val stmt01 = AssignmentStmt(VarAssignment("x"), IntValue(10))
    val stmt02 = WhileStmt(IntValue(10), stmt01)

    assert(TypeChecker.checkStmt(stmt01).runA(env) == Right(NullType))
    assert(TypeChecker.checkStmt(stmt02).runA(env).isLeft)
  }

  test("Test while statement type checker (with invalid stmt)") {
    val env = (new Environment[Type]())
    val stmt01 = AssignmentStmt(VarAssignment("x"), IntValue(10))
    val stmt02 = WhileStmt(BoolValue(true), stmt01)

    assert(TypeChecker.checkStmt(stmt01).runA(env).isLeft)
    assert(TypeChecker.checkStmt(stmt02).runA(env).isLeft)
  }

  test("Test while statement type checker") {
    val env = (new Environment[Type]()).setGlobalVariable("x", IntegerType)
    val stmt01 = AssignmentStmt(VarAssignment("x"), IntValue(10))
    val stmt02 = WhileStmt(BoolValue(true), stmt01)

    assert(TypeChecker.checkStmt(stmt01).runA(env) == Right(NullType))
    assert(TypeChecker.checkStmt(stmt02).runA(env) == Right(NullType))
  }

  test("Test for statement type checker (with invalid init)") {
    val env = new Environment[Type]().setGlobalVariable("y", IntegerType)
    val stmt01 = AssignmentStmt(VarAssignment("x"), IntValue(10))
    val stmt02 = AssignmentStmt(VarAssignment("y"), IntValue(10))
    val stmt03 = CoreTransformer.reduceToCoreStatement(
      ForStmt(stmt01, BoolValue(true), stmt02)
    )

    assert(TypeChecker.checkStmt(stmt01).runA(env).isLeft)
    assert(TypeChecker.checkStmt(stmt02).runA(env) == Right(NullType))
    assert(TypeChecker.checkStmt(stmt03).runA(env).isLeft)
  }

  test("Test for statement type checker (with invalid condition)") {
    val env = new Environment[Type]()
      .setGlobalVariable("x", IntegerType)
      .setGlobalVariable("y", IntegerType)
    val stmt01 = AssignmentStmt(VarAssignment("x"), IntValue(10))
    val stmt02 = AssignmentStmt(VarAssignment("y"), IntValue(10))

    val stmt03 = CoreTransformer.reduceToCoreStatement(
      ForStmt(stmt01, IntValue(10), stmt02)
    )

    assert(TypeChecker.checkStmt(stmt01).runA(env) == Right(NullType))
    assert(TypeChecker.checkStmt(stmt02).runA(env) == Right(NullType))
    assert(TypeChecker.checkStmt(stmt03).runA(env).isLeft)
  }

  test("Test for statement type checker (with invalid stmt)") {
    val env = new Environment[Type]().setGlobalVariable("x", IntegerType)
    val stmt01 = AssignmentStmt(VarAssignment("x"), IntValue(10))
    val stmt02 = AssignmentStmt(VarAssignment("y"), IntValue(100))

    val stmt03 = CoreTransformer.reduceToCoreStatement(
      ForStmt(stmt01, BoolValue(true), stmt02)
    )

    assert(TypeChecker.checkStmt(stmt01).runA(env) == Right(NullType))
    assert(TypeChecker.checkStmt(stmt02).runA(env).isLeft)
    assert(TypeChecker.checkStmt(stmt03).runA(env).isLeft)
  }

  test("Test for statement type checker") {
    val env = new Environment[Type]()
      .setGlobalVariable("x", IntegerType)
      .setGlobalVariable("y", IntegerType)
    val stmt01 = AssignmentStmt(VarAssignment("x"), IntValue(0))
    val stmt02 = AssignmentStmt(VarAssignment("y"), IntValue(10))

    val stmt03 = CoreTransformer.reduceToCoreStatement(
      ForStmt(stmt01, BoolValue(true), stmt02)
    )

    assert(TypeChecker.checkStmt(stmt01).runA(env) == Right(NullType))
    assert(TypeChecker.checkStmt(stmt02).runA(env) == Right(NullType))
    assert(TypeChecker.checkStmt(stmt03).runA(env) == Right(NullType))
  }

  // Começo

  test(
    "Test switch-case statement type checker RangeCase (invalid case01 min expression) "
  ) {
    val env = new Environment[Type]()
      .setGlobalVariable("x", IntegerType)
      .setGlobalVariable("y", IntegerType)

    val stmt01 = AssignmentStmt(VarAssignment("x"), IntValue(10))
    val stmt02 = AssignmentStmt(VarAssignment("y"), IntValue(15))

    val caseElse = AssignmentStmt(VarAssignment("x"), IntValue(20))

    val case01 = RangeCase(BoolValue(false), IntValue(20), stmt01)
    val case02 = RangeCase(IntValue(21), IntValue(30), stmt02)

    val cases = List(case01, case02)

    val stmt03 = CaseStmt(IntValue(11), cases, Some(caseElse))

    val testModule = OberonModule(
      name = "switch-case-test",
      submodules = Set(),
      userTypes = Nil,
      constants = Nil,
      variables = List(
        VariableDeclaration("x", IntegerType),
        VariableDeclaration("y", IntegerType)
      ),
      procedures = Nil,
      tests = Nil,
      stmt = Some(stmt03)
    )

    val testModuleCore = CoreTransformer.reduceOberonModule(testModule)

    assert(TypeChecker.checkStmt(stmt01).runA(env).isRight)
    assert(TypeChecker.checkStmt(stmt02).runA(env).isRight)
    assert(TypeChecker.checkStmt(caseElse).runA(env).isRight)
    assert(TypeChecker.checkModule(testModuleCore).runA(env).isLeft)
  }

  test(
    "Test switch-case statement type checker RangeCase (invalid case02 min expression) "
  ) {
    val env = new Environment[Type]()
      .setGlobalVariable("x", IntegerType)
      .setGlobalVariable("y", IntegerType)

    val stmt01 = AssignmentStmt(VarAssignment("x"), IntValue(10))
    val stmt02 = AssignmentStmt(VarAssignment("y"), IntValue(15))

    val caseElse = AssignmentStmt(VarAssignment("x"), IntValue(20))

    val case01 = RangeCase(IntValue(21), IntValue(20), stmt01)
    val case02 = RangeCase(BoolValue(false), IntValue(30), stmt02)

    val cases = List(case01, case02)

    val stmt03 = CaseStmt(IntValue(11), cases, Some(caseElse))

    val testModule = OberonModule(
      name = "switch-case-test",
      submodules = Set(),
      userTypes = Nil,
      constants = Nil,
      variables = List(
        VariableDeclaration("x", IntegerType),
        VariableDeclaration("y", IntegerType)
      ),
      procedures = Nil,
      tests = Nil,
      stmt = Some(stmt03)
    )

    val testModuleCore = CoreTransformer.reduceOberonModule(testModule)

    assert(TypeChecker.checkStmt(stmt01).runA(env).isRight)
    assert(TypeChecker.checkStmt(stmt02).runA(env).isRight)
    assert(TypeChecker.checkStmt(caseElse).runA(env).isRight)
    assert(TypeChecker.checkModule(testModuleCore).runA(env).isLeft)
  }

  test(
    "Test switch-case statement type checker RangeCase (invalid case01 and case02 min expression) "
  ) {
    val env = new Environment[Type]()
      .setGlobalVariable("x", IntegerType)
      .setGlobalVariable("y", IntegerType)

    val stmt01 = AssignmentStmt(VarAssignment("x"), IntValue(10))
    val stmt02 = AssignmentStmt(VarAssignment("y"), IntValue(15))

    val caseElse = AssignmentStmt(VarAssignment("x"), IntValue(20))

    val case01 = RangeCase(BoolValue(false), IntValue(20), stmt01)
    val case02 = RangeCase(BoolValue(false), IntValue(30), stmt02)

    val cases = List(case01, case02)

    val stmt03 = CaseStmt(IntValue(11), cases, Some(caseElse))

    val testModule = OberonModule(
      name = "switch-case-test",
      submodules = Set(),
      userTypes = Nil,
      constants = Nil,
      variables = List(
        VariableDeclaration("x", IntegerType),
        VariableDeclaration("y", IntegerType)
      ),
      procedures = Nil,
      tests = Nil,
      stmt = Some(stmt03)
    )

    val testModuleCore = CoreTransformer.reduceOberonModule(testModule)

    assert(TypeChecker.checkStmt(stmt01).runA(env).isRight)
    assert(TypeChecker.checkStmt(stmt02).runA(env).isRight)
    assert(TypeChecker.checkStmt(caseElse).runA(env).isRight)
    assert(TypeChecker.checkModule(testModuleCore).runA(env).isLeft)
  }

  test(
    "Test switch-case statement type checker RangeCase (invalid case01 and case02 max expression) "
  ) {
    val env = new Environment[Type]()
      .setGlobalVariable("x", IntegerType)
      .setGlobalVariable("y", IntegerType)

    val stmt01 = AssignmentStmt(VarAssignment("x"), IntValue(10))
    val stmt02 = AssignmentStmt(VarAssignment("y"), IntValue(15))

    val caseElse = AssignmentStmt(VarAssignment("x"), IntValue(20))

    val case01 = RangeCase(IntValue(20), BoolValue(false), stmt01)
    val case02 = RangeCase(IntValue(30), BoolValue(false), stmt02)

    val cases = List(case01, case02)

    val stmt03 = CaseStmt(IntValue(11), cases, Some(caseElse))

    val testModule = OberonModule(
      name = "switch-case-test",
      submodules = Set(),
      userTypes = Nil,
      constants = Nil,
      variables = List(
        VariableDeclaration("x", IntegerType),
        VariableDeclaration("y", IntegerType)
      ),
      procedures = Nil,
      tests = Nil,
      stmt = Some(stmt03)
    )

    val testModuleCore = CoreTransformer.reduceOberonModule(testModule)

    assert(TypeChecker.checkStmt(stmt01).runA(env).isRight)
    assert(TypeChecker.checkStmt(stmt02).runA(env).isRight)
    assert(TypeChecker.checkStmt(caseElse).runA(env).isRight)
    assert(TypeChecker.checkModule(testModuleCore).runA(env).isLeft)
  }

  test(
    "Test switch-case statement type checker RangeCase (invalid CaseStmt exp) "
  ) {
    val env = new Environment[Type]()
      .setGlobalVariable("x", IntegerType)
      .setGlobalVariable("y", IntegerType)

    val stmt01 = AssignmentStmt(VarAssignment("x"), IntValue(10))
    val stmt02 = AssignmentStmt(VarAssignment("y"), IntValue(15))

    val caseElse = AssignmentStmt(VarAssignment("x"), IntValue(20))

    val case01 = RangeCase(IntValue(20), IntValue(30), stmt01)
    val case02 = RangeCase(IntValue(30), IntValue(40), stmt02)

    val cases = List(case01, case02)

    val stmt03 = CaseStmt(Undef(), cases, Some(caseElse))

    val testModule = OberonModule(
      name = "switch-case-test",
      submodules = Set(),
      userTypes = Nil,
      constants = Nil,
      variables = List(
        VariableDeclaration("x", IntegerType),
        VariableDeclaration("y", IntegerType)
      ),
      procedures = Nil,
      tests = Nil,
      stmt = Some(stmt03)
    )

    val testModuleCore = CoreTransformer.reduceOberonModule(testModule)

    assert(TypeChecker.checkStmt(stmt01).runA(env).isRight)
    assert(TypeChecker.checkStmt(stmt02).runA(env).isRight)
    assert(TypeChecker.checkStmt(caseElse).runA(env).isRight)
    assert(TypeChecker.checkModule(testModuleCore).runA(env).isLeft)
  }

  ignore("Test switch-case statement type checker SimpleCase (Boolean cases)") {
    val env = new Environment[Type]().setGlobalVariable("x", IntegerType)

    val stmt01 = AssignmentStmt(VarAssignment("x"), IntValue(10))

    val caseElse = AssignmentStmt(VarAssignment("x"), IntValue(20))

    val case01 = SimpleCase(BoolValue(true), stmt01)
    val case02 = SimpleCase(BoolValue(false), stmt01)
    val cases = List(case01, case02)

    val stmt02 = CaseStmt(BoolValue(true), cases, Some(caseElse))

    val testModule = OberonModule(
      name = "switch-case-test",
      submodules = Set(),
      userTypes = Nil,
      constants = Nil,
      variables = List(VariableDeclaration("x", IntegerType)),
      procedures = Nil,
      tests = Nil,
      stmt = Some(stmt02)
    )

    val testModuleCore = CoreTransformer.reduceOberonModule(testModule)

    assert(TypeChecker.checkStmt(stmt01).runA(env).isRight)
    assert(TypeChecker.checkStmt(caseElse).runA(env).isRight)
    assert(TypeChecker.checkModule(testModuleCore).runA(env).isRight)
  }

  test(
    "Test switch-case statement type checker SimpleCase (invalid case02 condition)"
  ) {
    val env = new Environment[Type]().setGlobalVariable("x", IntegerType)

    val stmt01 = AssignmentStmt(VarAssignment("x"), IntValue(10))

    val caseElse = AssignmentStmt(VarAssignment("x"), IntValue(20))

    val case01 = SimpleCase(IntValue(10), stmt01)
    val case02 = SimpleCase(BoolValue(false), stmt01)
    val cases = List(case01, case02)

    val stmt02 = CaseStmt(IntValue(10), cases, Some(caseElse))

    val testModule = OberonModule(
      name = "switch-case-test",
      submodules = Set(),
      userTypes = Nil,
      constants = Nil,
      variables = List(VariableDeclaration("x", IntegerType)),
      procedures = Nil,
      tests = Nil,
      stmt = Some(stmt02)
    )

    val testModuleCore = CoreTransformer.reduceOberonModule(testModule)

    assert(TypeChecker.checkStmt(stmt01).runA(env).isRight)
    assert(TypeChecker.checkStmt(caseElse).runA(env).isRight)
    assert(TypeChecker.checkModule(testModuleCore).runA(env).isLeft)
  }

  test(
    "Test switch-case statement type checker SimpleCase (invalid case01 and case02 condition)"
  ) {
    val env = new Environment[Type]().setGlobalVariable("x", IntegerType)

    val stmt01 = AssignmentStmt(VarAssignment("x"), IntValue(10))

    val caseElse = AssignmentStmt(VarAssignment("x"), IntValue(20))

    val case01 = SimpleCase(Undef(), stmt01)
    val case02 = SimpleCase(Undef(), stmt01)
    val cases = List(case01, case02)

    val stmt02 = CaseStmt(IntValue(10), cases, Some(caseElse))

    val testModule = OberonModule(
      name = "switch-case-test",
      submodules = Set(),
      userTypes = Nil,
      constants = Nil,
      variables = List(VariableDeclaration("x", IntegerType)),
      procedures = Nil,
      tests = Nil,
      stmt = Some(stmt02)
    )

    val testModuleCore = CoreTransformer.reduceOberonModule(testModule)

    assert(TypeChecker.checkStmt(stmt01).runA(env).isRight)
    assert(TypeChecker.checkStmt(caseElse).runA(env).isRight)
    assert(TypeChecker.checkModule(testModuleCore).runA(env).isLeft)
  }

  test("Test switch-case statement type checker RangeCase") {
    val env = new Environment[Type]()
      .setGlobalVariable("x", IntegerType)
      .setGlobalVariable("y", IntegerType)

    val stmt01 = AssignmentStmt(VarAssignment("x"), IntValue(10))
    val stmt02 = AssignmentStmt(VarAssignment("y"), IntValue(15))

    val caseElse = AssignmentStmt(VarAssignment("x"), IntValue(20))

    val case01 = RangeCase(IntValue(10), IntValue(20), stmt01)
    val case02 = RangeCase(IntValue(21), IntValue(30), stmt02)

    val cases = List(case01, case02)

    val stmt03 = CaseStmt(IntValue(11), cases, Some(caseElse))

    val testModule = OberonModule(
      name = "switch-case-test",
      submodules = Set(),
      userTypes = Nil,
      constants = Nil,
      variables = List(
        VariableDeclaration("x", IntegerType),
        VariableDeclaration("y", IntegerType)
      ),
      procedures = Nil,
      tests = Nil,
      stmt = Some(stmt03)
    )

    val testModuleCore = CoreTransformer.reduceOberonModule(testModule)

    assert(TypeChecker.checkStmt(stmt01).runA(env).isRight)
    assert(TypeChecker.checkStmt(stmt02).runA(env).isRight)
    assert(TypeChecker.checkStmt(caseElse).runA(env).isRight)
    assert(TypeChecker.checkModule(testModuleCore).runA(env).isRight)
  }

  test("Test switch-case statement type checker SimpleCase") {
    val env = new Environment[Type]().setGlobalVariable("x", IntegerType)

    val stmt01 = AssignmentStmt(VarAssignment("x"), IntValue(10))

    val caseElse = AssignmentStmt(VarAssignment("x"), IntValue(20))

    val case01 = SimpleCase(IntValue(10), stmt01)
    val case02 = SimpleCase(IntValue(20), stmt01)
    val cases = List(case01, case02)

    val stmt02 = CaseStmt(IntValue(10), cases, Some(caseElse))

    val testModule = OberonModule(
      name = "switch-case-test",
      submodules = Set(),
      userTypes = Nil,
      constants = Nil,
      variables = List(VariableDeclaration("x", IntegerType)),
      procedures = Nil,
      tests = Nil,
      stmt = Some(stmt02)
    )

    val testModuleCore = CoreTransformer.reduceOberonModule(testModule)

    assert(TypeChecker.checkStmt(stmt01).runA(env).isRight)
    assert(TypeChecker.checkStmt(caseElse).runA(env).isRight)
    assert(TypeChecker.checkModule(testModuleCore).runA(env).isRight)
  }

  /*
   * the following test cases read an oberon module with the
   * factorial procedure.
   */
  test("Test invalid procedure declaration") {
    val module = parseResource("procedures/procedure04.oberon")

    assert(module.name == "SimpleModule")

    assert(module.procedures.size == 2)
    assert(module.stmt.isDefined)
  }

  test("Test the type checker of a valid Repeat statement") {
    val env = new Environment[Type]().setGlobalVariable("x", IntegerType)

    val condition = LTExpression(VarExpression("x"), IntValue(10))
    val stmt01 = ReadIntStmt("x")
    val repeatStmt =
      CoreTransformer.reduceToCoreStatement(RepeatUntilStmt(condition, stmt01))

    assert(TypeChecker.checkStmt(stmt01).runA(env).isRight)
    assert(TypeChecker.checkStmt(repeatStmt).runA(env).isRight)
  }

  test("Test the type checker of a valid Repeat statement 2") {
    val env = new Environment[Type]().setGlobalVariable("x", IntegerType)

    val condition = EQExpression(VarExpression("x"), IntValue(0))
    val stmt01 = ReadIntStmt("x")
    val repeatStmt =
      CoreTransformer.reduceToCoreStatement(RepeatUntilStmt(condition, stmt01))

    assert(TypeChecker.checkStmt(stmt01).runA(env).isRight)
    assert(TypeChecker.checkStmt(repeatStmt).runA(env).isRight)
  }

  test("Test the type checker of a valid Repeat statement 3") {
    val env = new Environment[Type]()
    val stmt01 = AssignmentStmt(VarAssignment("x"), IntValue(10))

    val stmt02 = CoreTransformer.reduceToCoreStatement(
      RepeatUntilStmt(BoolValue(true), stmt01)
    )

    assert(TypeChecker.checkStmt(stmt01).runA(env).isLeft)
    assert(TypeChecker.checkStmt(stmt02).runA(env).isLeft)
  }

  test("Test a invalid Repeat statement in the type checker") {
    val env = new Environment[Type]()

    val stmt01 = AssignmentStmt(VarAssignment("x"), IntValue(10))
    val stmt02 = ReadIntStmt("x")
    val stmt03 = IfElseStmt(BoolValue(false), stmt01, Some(stmt02))
    val stmt04 = AssignmentStmt(VarAssignment("x"), IntValue(20))
    val stmt05 = SequenceStmt(List(stmt01, stmt02, stmt03, stmt04))
    val stmt06 = CoreTransformer.reduceToCoreStatement(
      RepeatUntilStmt(BoolValue(true), stmt05)
    )

    assert(TypeChecker.checkStmt(stmt01).runA(env).isLeft)
    assert(TypeChecker.checkStmt(stmt02).runA(env).isLeft)
    assert(TypeChecker.checkStmt(stmt03).runA(env).isLeft)
    assert(TypeChecker.checkStmt(stmt04).runA(env).isLeft)
    assert(TypeChecker.checkStmt(stmt05).runA(env).isLeft)
    assert(TypeChecker.checkStmt(stmt06).runA(env).isLeft)
  }

  test("Test the type checker of a valid Repeat statement 4") {
    val env = new Environment[Type]().setGlobalVariable("x", IntegerType)

    val condition = AndExpression(
      GTEExpression(VarExpression("x"), IntValue(1)),
      LTEExpression(VarExpression("x"), IntValue(10))
    )
    val stmt01 = ReadIntStmt("x")
    val repeatStmt =
      CoreTransformer.reduceToCoreStatement(RepeatUntilStmt(condition, stmt01))

    assert(TypeChecker.checkStmt(stmt01).runA(env).isRight)
    assert(TypeChecker.checkStmt(repeatStmt).runA(env).isRight)

  }

  test("Test a valid Repeat statement, with nested Repeat statements") {
    val env = new Environment[Type]().setGlobalVariable("x", IntegerType)

    val stmt01 = AssignmentStmt(VarAssignment("x"), IntValue(10))
    val repeatStmt01 = CoreTransformer.reduceToCoreStatement(
      RepeatUntilStmt(BoolValue(true), stmt01)
    )
    val repeatStmt02 = CoreTransformer.reduceToCoreStatement(
      RepeatUntilStmt(BoolValue(true), repeatStmt01)
    )
    val repeatStmt03 = CoreTransformer.reduceToCoreStatement(
      RepeatUntilStmt(BoolValue(true), repeatStmt02)
    )
    val repeatStmt04 = CoreTransformer.reduceToCoreStatement(
      RepeatUntilStmt(BoolValue(true), repeatStmt03)
    )

    val allStmts =
      List(stmt01, repeatStmt01, repeatStmt02, repeatStmt03, repeatStmt04)

    allStmts.foreach(stmt => {
      assert(TypeChecker.checkStmt(stmt).runA(env).isRight)
    })
  }

  test("Test a invalid Repeat statement, with nested Repeat statements") {
    val env = new Environment[Type]()

    val stmt01 = AssignmentStmt(VarAssignment("x"), IntValue(10))
    val repeatStmt01 = CoreTransformer.reduceToCoreStatement(
      RepeatUntilStmt(BoolValue(true), stmt01)
    )
    val repeatStmt02 = CoreTransformer.reduceToCoreStatement(
      RepeatUntilStmt(BoolValue(true), repeatStmt01)
    )
    val repeatStmt03 = CoreTransformer.reduceToCoreStatement(
      RepeatUntilStmt(BoolValue(true), repeatStmt02)
    )
    val repeatStmt04 = CoreTransformer.reduceToCoreStatement(
      RepeatUntilStmt(BoolValue(true), repeatStmt03)
    )

    val allStmts = List(repeatStmt01, repeatStmt02, repeatStmt03, repeatStmt04)

    allStmts.foreach(stmt => {
      assert(TypeChecker.checkStmt(stmt).runA(env).isLeft)
    })
  }

  test("Test a valid Repeat statement, with a boolean variable") {
    val env = new Environment[Type]().setGlobalVariable("flag", BooleanType)

    val boolVar = VarExpression("flag")
    val stmt01 = AssignmentStmt(VarAssignment("flag"), BoolValue(true))
    val repeatStmt =
      CoreTransformer.reduceToCoreStatement(RepeatUntilStmt(boolVar, stmt01))

    assert(TypeChecker.checkStmt(repeatStmt).runA(env).isRight)
  }

  test("Test a valid Repeat statement, with a sequence of statements") {
    val env = new Environment[Type]().setGlobalVariable("x", IntegerType)

    val stmt01 = AssignmentStmt(VarAssignment("x"), BoolValue(false))
    val repeatStmt = CoreTransformer.reduceToCoreStatement(
      RepeatUntilStmt(BoolValue(true), stmt01)
    )
    val stmt02 = SequenceStmt(List(stmt01, repeatStmt, stmt01, repeatStmt))

    assert(TypeChecker.checkStmt(stmt02).runA(env).isRight)
  }

  test("Test a invalid Repeat statement, with a sequence of statements") {
    val env = new Environment[Type]()

    val stmt01 = AssignmentStmt(VarAssignment("x"), BoolValue(false))
    val repeatStmt = CoreTransformer.reduceToCoreStatement(
      RepeatUntilStmt(BoolValue(true), stmt01)
    )
    val stmt02 = SequenceStmt(List(stmt01, repeatStmt, stmt01, repeatStmt))

    assert(TypeChecker.checkStmt(stmt02).runA(env).isLeft)
  }

  test("Test a loop statement, from loop_stmt03") {
    val path = Paths.get(
      getClass.getClassLoader.getResource("stmts/loop_stmt03.oberon").toURI
    )

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = parseAbs(parse(oberonParser, content))

    assert(module.name == "LoopStmt")

    val env = new Environment[Type]()

    assert(
      TypeChecker
        .checkModule(CoreTransformer.reduceOberonModule(module))
        .runA(env)
        .isRight
    )
  }

  test("Test assignment to pointer value") {
    val module = parseResource("stmts/tc_PointerAccessStmt.oberon")
    val env = new Environment[Type]()

    assert(
      TypeChecker
        .checkModule(CoreTransformer.reduceOberonModule(module))
        .runA(env)
        .isRight
    )
  }

  test("Test arithmetic operation with pointers") {
    val module = parseResource("stmts/tc_PointerOperation.oberon")
    val env = new Environment[Type]()

    assert(
      TypeChecker
        .checkModule(CoreTransformer.reduceOberonModule(module))
        .runA(env)
        .isRight
    )
  }

  test("Test incorrect assignment between pointer and simple type variable") {
    val module = parseResource("stmts/tc_PointerAssignmentWrong.oberon")
    val env = new Environment[Type]()

    assert(
      TypeChecker
        .checkModule(CoreTransformer.reduceOberonModule(module))
        .runA(env)
        .isLeft
    )
  }

  test("Test incorrect assignment between pointer and arithmetic operation") {
    val module = parseResource("stmts/tc_PointerOperationWrong.oberon")
    val env = new Environment[Type]()

    assert(
      TypeChecker
        .checkModule(CoreTransformer.reduceOberonModule(module))
        .runA(env)
        .isLeft
    )
  }

  test("Test assignment of NullValue to pointer") {
    val module = parseResource("stmts/tc_PointerNull.oberon")
    val env = new Environment[Type]()
    val reduced = CoreTransformer.reduceOberonModule(module)

    println(reduced)

    assert(
      TypeChecker
        .checkModule(CoreTransformer.reduceOberonModule(module))
        .runA(env) == Right(())
    )
  }

  test("Test array subscript") {
    val env = new Environment[Type]()
      .setGlobalVariable("arr", ArrayType(1, IntegerType))
    val stmt = WriteStmt(ArraySubscript(VarExpression("arr"), IntValue(0)))

    assert(TypeChecker.checkStmt(stmt).runA(env).isRight)
  }

  test("Test array subscript, expression of wrong type") {
    val env = new Environment[Type]().setGlobalVariable("arr", IntegerType)
    val stmt = WriteStmt(ArraySubscript(VarExpression("arr"), IntValue(0)))

    assert(TypeChecker.checkStmt(stmt).runA(env).isLeft)
  }

  test("Test array subscript, index of wrong type") {
    val env = new Environment[Type]()
      .setGlobalVariable("arr", ArrayType(1, IntegerType))
    val stmt = WriteStmt(ArraySubscript(VarExpression("arr"), BoolValue(false)))

    assert(TypeChecker.checkStmt(stmt).runA(env).isLeft)
  }

  test("Test array subscript, expression is ArrayValue") {
    val env = new Environment[Type]()
    val stmt =
      WriteStmt(
        ArraySubscript(
          ArrayValue(ListBuffer(IntValue(0)), ArrayType(1, IntegerType)),
          IntValue(0)
        )
      )

    assert(TypeChecker.checkStmt(stmt).runA(env).isRight)
  }

  test("Test array subscript, expression is empty ArrayValue") {
    val env = new Environment[Type]()
    val stmt =
      WriteStmt(
        ArraySubscript(
          ArrayValue(ListBuffer(), ArrayType(0, IntegerType)),
          IntValue(0)
        )
      )

    assert(TypeChecker.checkStmt(stmt).runA(env).isRight)
  }

  test("Test function call") {
    val env = new Environment[Type]().declareProcedure(
      Procedure(
        name = "proc",
        args = Nil,
        returnType = None,
        constants = Nil,
        variables = Nil,
        stmt = WriteStmt(IntValue(0))
      )
    )
    val stmt = WriteStmt(FunctionCallExpression("proc", Nil))

    assert(TypeChecker.checkStmt(stmt).runA(env).isRight)
  }

  test("Test function call with args and return type") {
    val env = new Environment[Type]()
      .setGlobalVariable("x", IntegerType)
      .declareProcedure(
        Procedure(
          name = "proc",
          args = List(
            ParameterByValue("x", IntegerType),
            ParameterByReference("y", BooleanType)
          ),
          returnType = Some(IntegerType),
          constants = Nil,
          variables = Nil,
          stmt = IfElseStmt(
            VarExpression("y"),
            ReturnStmt(AddExpression(VarExpression("x"), IntValue(1))),
            Some(ReturnStmt(AddExpression(VarExpression("x"), IntValue(-1))))
          )
        )
      )
    val stmt = AssignmentStmt(
      VarAssignment("x"),
      FunctionCallExpression("proc", List(IntValue(5), BoolValue(true)))
    )

    assert(TypeChecker.checkStmt(stmt).runA(env).isRight)
  }

  test("Test function call with one argument") {
    val env = new Environment[Type]().declareProcedure(
      Procedure(
        name = "proc",
        args = List(ParameterByValue("x", IntegerType)),
        returnType = None,
        constants = Nil,
        variables = Nil,
        stmt = WriteStmt(IntValue(0))
      )
    )
    val stmt = WriteStmt(
      FunctionCallExpression("proc", List(IntValue(5)))
    )

    assert(TypeChecker.checkStmt(stmt).runA(env).isRight)
  }

  test("Test function call with return type") {
    val env = new Environment[Type]()
      .setGlobalVariable("s", StringType)
      .declareProcedure(
        Procedure(
          name = "proc",
          args = Nil,
          returnType = Some(StringType),
          constants = Nil,
          variables = Nil,
          stmt = ReturnStmt(StringValue("ret"))
        )
      )
    val stmt = AssignmentStmt(
      VarAssignment("s"),
      FunctionCallExpression("proc", Nil)
    )

    assert(TypeChecker.checkStmt(stmt).runA(env).isRight)
  }

  test("Test function call, wrong args") {
    val env = new Environment[Type]()
      .setGlobalVariable("x", IntegerType)
      .declareProcedure(
        Procedure(
          name = "proc",
          args = List(
            ParameterByValue("x", IntegerType),
            ParameterByReference("y", BooleanType)
          ),
          returnType = Some(IntegerType),
          constants = Nil,
          variables = Nil,
          stmt = IfElseStmt(
            VarExpression("y"),
            ReturnStmt(AddExpression(VarExpression("x"), IntValue(1))),
            Some(ReturnStmt(AddExpression(VarExpression("x"), IntValue(-1))))
          )
        )
      )
    val stmt = AssignmentStmt(
      VarAssignment("x"),
      FunctionCallExpression("proc", List(IntValue(5), IntValue(0)))
    )

    assert(TypeChecker.checkStmt(stmt).runA(env).isLeft)
  }

  test("Test function call, less args than needed") {
    val env = new Environment[Type]()
      .setGlobalVariable("x", IntegerType)
      .declareProcedure(
        Procedure(
          name = "proc",
          args = List(
            ParameterByValue("x", IntegerType),
            ParameterByReference("y", BooleanType)
          ),
          returnType = Some(IntegerType),
          constants = Nil,
          variables = Nil,
          stmt = IfElseStmt(
            VarExpression("y"),
            ReturnStmt(AddExpression(VarExpression("x"), IntValue(1))),
            Some(ReturnStmt(AddExpression(VarExpression("x"), IntValue(-1))))
          )
        )
      )
    val stmt = AssignmentStmt(
      VarAssignment("x"),
      FunctionCallExpression("proc", List(IntValue(0)))
    )

    assert(TypeChecker.checkStmt(stmt).runA(env).isLeft)
  }

  test("Test function call, wrong args and return type") {
    val env = new Environment[Type]()
      .setGlobalVariable("x", IntegerType)
      .declareProcedure(
        Procedure(
          name = "proc",
          args = List(
            ParameterByValue("x", IntegerType),
            ParameterByReference("y", BooleanType)
          ),
          returnType = Some(IntegerType),
          constants = Nil,
          variables = Nil,
          stmt = IfElseStmt(
            VarExpression("y"),
            ReturnStmt(AddExpression(VarExpression("x"), IntValue(1))),
            Some(ReturnStmt(AddExpression(VarExpression("x"), IntValue(-1))))
          )
        )
      )
    val stmt = AssignmentStmt(
      VarAssignment("x"),
      FunctionCallExpression("proc", List(IntValue(5), VarExpression("404")))
    )

    assert(TypeChecker.checkStmt(stmt).runA(env).isLeft)
  }

  test("Test EAssignment") {
    val env = new Environment[Type]()
      .addUserDefinedType(
        UserDefinedType(
          "customType",
          RecordType(List(VariableDeclaration("x1", RealType)))
        )
      )
      .setGlobalVariable("x", IntegerType)
      .setGlobalVariable("b", PointerType(BooleanType))
      .setGlobalVariable("arr", ArrayType(3, CharacterType))
      .setGlobalVariable(
        "rec",
        RecordType(List(VariableDeclaration("x", StringType)))
      )
      .setGlobalVariable(
        "userDefType",
        ReferenceToUserDefinedType("customType")
      )

    val stmt01 = new AssignmentStmt(VarAssignment("x"), IntValue(0))
    val stmt02 = new AssignmentStmt(PointerAssignment("b"), BoolValue(false))
    val stmt03 = new AssignmentStmt(
      ArrayAssignment(VarExpression("arr"), IntValue(0)),
      CharValue('a')
    )
    val stmt04 = new AssignmentStmt(
      RecordAssignment(VarExpression("rec"), "x"),
      StringValue("teste")
    )
    val stmt05 = new AssignmentStmt(
      RecordAssignment(VarExpression("userDefType"), "x1"),
      RealValue(6.9)
    )

    val stmts = SequenceStmt(List(stmt01, stmt02, stmt03, stmt04))

    assert(TypeChecker.checkStmt(stmts).runA(env) == Right(NullType))
  }

  test("Test EAssignment, PointerAssignment, missing variable") {
    val env = new Environment[Type]()
    val stmt = AssignmentStmt(PointerAssignment("b"), BoolValue(false))

    assert(TypeChecker.checkStmt(stmt).runA(env).isLeft)
  }

  test("Test EAssignment, PointerAssignment, left side not PointerType") {
    val env = new Environment[Type]().setGlobalVariable("b", IntegerType)
    val stmt = AssignmentStmt(PointerAssignment("b"), BoolValue(false))

    assert(TypeChecker.checkStmt(stmt).runA(env).isLeft)
  }

  test("Test EAssignment, PointerAssignment, invalid left side Type") {
    val env =
      new Environment[Type]().setGlobalVariable("b", PointerType(UndefinedType))
    val stmt = AssignmentStmt(PointerAssignment("b"), BoolValue(false))

    assert(TypeChecker.checkStmt(stmt).runA(env).isLeft)
  }

  test("Test EAssignment, PointerAssignment, wrong right side Type") {
    val env =
      new Environment[Type]().setGlobalVariable("b", PointerType(IntegerType))
    val stmt = AssignmentStmt(PointerAssignment("b"), BoolValue(false))

    assert(TypeChecker.checkStmt(stmt).runA(env).isLeft)
  }

  test("Test EAssignment, ArrayAssignment, wrong array type") {
    val env = new Environment[Type]().setGlobalVariable("arr", IntegerType)
    val stmt = AssignmentStmt(
      ArrayAssignment(VarExpression("arr"), IntValue(0)),
      CharValue('a')
    )

    assert(TypeChecker.checkStmt(stmt).runA(env).isLeft)
  }

  test("Test EAssignment, ArrayAssignment, wrong index type") {
    val env = new Environment[Type]()
      .setGlobalVariable("arr", ArrayType(3, CharacterType))
    val stmt = AssignmentStmt(
      ArrayAssignment(VarExpression("arr"), BoolValue(true)),
      CharValue('a')
    )

    assert(TypeChecker.checkStmt(stmt).runA(env).isLeft)
  }

  test("Test EAssignment, ArrayAssignment, missing array type") {
    val env = new Environment[Type]()
    val stmt = AssignmentStmt(
      ArrayAssignment(VarExpression("arr"), IntValue(0)),
      CharValue('a')
    )

    assert(TypeChecker.checkStmt(stmt).runA(env).isLeft)
  }

  test("Test EAssignment, ArrayAssignment, missing index type") {
    val env = new Environment[Type]()
      .setGlobalVariable("arr", ArrayType(3, CharacterType))
    val stmt = AssignmentStmt(
      ArrayAssignment(VarExpression("arr"), VarExpression("i")),
      CharValue('a')
    )

    assert(TypeChecker.checkStmt(stmt).runA(env).isLeft)
  }

  test("Test EAssignment, ArrayAssignment, wrong array element type") {
    val env = new Environment[Type]()
      .setGlobalVariable("arr", ArrayType(3, IntegerType))
    val stmt = AssignmentStmt(
      ArrayAssignment(VarExpression("arr"), IntValue(0)),
      CharValue('a')
    )

    assert(TypeChecker.checkStmt(stmt).runA(env).isLeft)
  }

  test("Test EAssignment, RecordAssignment(RecordType), missing attribute") {
    val env = new Environment[Type]().setGlobalVariable(
      "rec",
      RecordType(List(VariableDeclaration("x", StringType)))
    )
    val stmt = AssignmentStmt(
      RecordAssignment(VarExpression("rec"), "404"),
      StringValue("teste")
    )

    assert(TypeChecker.checkStmt(stmt).runA(env).isLeft)
  }

  test("Test EAssignment, RecordAssignment(RecordType), wrong attribute type") {
    val env = new Environment[Type]().setGlobalVariable(
      "rec",
      RecordType(List(VariableDeclaration("x", StringType)))
    )
    val stmt = AssignmentStmt(
      RecordAssignment(VarExpression("rec"), "x"),
      IntValue(8)
    )

    assert(TypeChecker.checkStmt(stmt).runA(env).isLeft)
  }

  test(
    "Test EAssignment, RecordAssignment(ReferenceToUserDefinedType), missing custom type"
  ) {
    val env = new Environment[Type]().setGlobalVariable(
      "userDefType",
      ReferenceToUserDefinedType("customType")
    )
    val stmt = AssignmentStmt(
      RecordAssignment(VarExpression("userDefType"), "x"),
      RealValue(3.0)
    )

    assert(TypeChecker.checkStmt(stmt).runA(env).isLeft)
  }

  test(
    "Test EAssignment, RecordAssignment(ReferenceToUserDefinedType), missing attribute type"
  ) {
    val env = new Environment[Type]()
      .addUserDefinedType(
        UserDefinedType(
          "customType",
          RecordType(List(VariableDeclaration("x1", RealType)))
        )
      )
      .setGlobalVariable(
        "userDefType",
        ReferenceToUserDefinedType("customType")
      )
    val stmt = AssignmentStmt(
      RecordAssignment(VarExpression("userDefType"), "404"),
      RealValue(3.0)
    )

    assert(TypeChecker.checkStmt(stmt).runA(env).isLeft)
  }

  test(
    "Test EAssignment, RecordAssignment(ReferenceToUserDefinedType), wrong attribute type"
  ) {
    val env = new Environment[Type]()
      .addUserDefinedType(
        UserDefinedType(
          "customType",
          RecordType(List(VariableDeclaration("x1", RealType)))
        )
      )
      .setGlobalVariable(
        "userDefType",
        ReferenceToUserDefinedType("customType")
      )
    val stmt = AssignmentStmt(
      RecordAssignment(VarExpression("userDefType"), "x1"),
      IntValue(3)
    )

    assert(TypeChecker.checkStmt(stmt).runA(env).isLeft)
  }
}
