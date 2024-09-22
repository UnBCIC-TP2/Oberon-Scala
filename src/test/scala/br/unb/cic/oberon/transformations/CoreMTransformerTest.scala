import org.scalatest.funsuite.AnyFunSuite
import br.unb.cic.oberon.ir.ast._
import br.unb.cic.oberon.ir.core._
import br.unb.cic.oberon.ir.common._
import br.unb.cic.oberon.transformations.CoreMTransformer
import java.util.concurrent.atomic.AtomicInteger
import scala.collection.mutable.ArrayBuffer

class CoreMTransformerTest extends AnyFunSuite {

  test("Test transformation of IfElseIfStmt into IfElseExp") {
    val ifStmt = IfElseIfStmt(
      EQExpression(IntValue(1), IntValue(1)),
      AssignmentStmt(VarAssignment("x"), IntValue(10)),
      List(ElseIfStmt(EQExpression(IntValue(2), IntValue(2)), AssignmentStmt(VarAssignment("x"), IntValue(20)))),
      Some(AssignmentStmt(VarAssignment("x"), IntValue(30)))
    )

    val transformedExp = CoreMTransformer.reduceToCoreExpression(ifStmt)

    assert(transformedExp.isInstanceOf[IfElseExp])
    transformedExp match {
      case IfElseExp(cond, thenExp, Some(elseExp)) =>
        assert(cond == EQExpression(IntValue(1), IntValue(1)))
        assert(thenExp.isInstanceOf[AssignmentExp])
        assert(elseExp.isInstanceOf[IfElseExp])
      case _ => fail("Transformation to IfElseExp failed")
    }
  }

  test("Test transformation of a simple WhileStmt") {
    val whileStmt = WhileStmt(
      condition = EQExpression(IntValue(1), IntValue(1)),
      stmt = AssignmentStmt(VarAssignment("x"), IntValue(10))
    )

    val transformedExp = CoreMTransformer.reduceToCoreExpression(whileStmt)

    assert(transformedExp.isInstanceOf[WhileExp])
    transformedExp match {
      case WhileExp(cond, body) =>
        assert(cond == EQExpression(IntValue(1), IntValue(1)))
        assert(body.isInstanceOf[AssignmentExp])
      case _ => fail("Transformation to WhileExp failed")
    }
  }

  test("Test transformation of a SequenceStmt into a SequenceExp") {
    val seqStmt = SequenceStmt(List(
      AssignmentStmt(VarAssignment("x"), IntValue(10)),
      AssignmentStmt(VarAssignment("y"), IntValue(20))
    ))

    val transformedExp = CoreMTransformer.reduceToCoreExpression(seqStmt)

    assert(transformedExp.isInstanceOf[SequenceExp])
  }
  
  test("Test transformation of a procedure declaration") {
    val procedure = Procedure(
      name = "testProcedure",
      args = List(),
      returnType = Some(IntegerType),
      constants = List(),
      variables = List(VariableDeclaration("x", IntegerType)),
      stmt = AssignmentStmt(VarAssignment("x"), IntValue(10))
    )

    val transformedProcedure = CoreMTransformer.reduceProcedureDeclaration(procedure, new AtomicInteger(), new ArrayBuffer())

    assert(transformedProcedure.stmt.isInstanceOf[AssignmentExp])
    assert(transformedProcedure.variables.contains(VariableDeclaration("x", IntegerType)))
  }

  test("Test transformation of a RepeatUntilStmt") {
    val repeatUntilStmt = RepeatUntilStmt(
      condition = EQExpression(IntValue(1), IntValue(1)),
      stmt = AssignmentStmt(VarAssignment("x"), IntValue(10))
    )

    val transformedExp = CoreMTransformer.reduceToCoreExpression(repeatUntilStmt)

    assert(transformedExp.isInstanceOf[WhileExp])
    transformedExp match {
      case WhileExp(cond, body) =>
        assert(cond == BoolValue(true)) 
        assert(body.isInstanceOf[SequenceExp])
      case _ => fail("Transformation to WhileExp from RepeatUntilStmt failed")
    }
  }

  test("Test transformation of an AssertEqualStmt into AssertTrueExp") {
    val assertEqualStmt = AssertEqualStmt(IntValue(10), IntValue(10))

    val transformedExp = CoreMTransformer.reduceToCoreExpression(assertEqualStmt)

    assert(transformedExp.isInstanceOf[AssertTrueExp])
    transformedExp match {
      case AssertTrueExp(expr) =>
        assert(expr == EQExpression(IntValue(10), IntValue(10)))
      case _ => fail("Transformation to AssertTrueExp failed")
    }
  }

  test("Test transformation of an AssertNotEqualStmt into AssertTrueExp") {
    val assertNotEqualStmt = AssertNotEqualStmt(IntValue(10), IntValue(5))

    val transformedExp = CoreMTransformer.reduceToCoreExpression(assertNotEqualStmt)

    assert(transformedExp.isInstanceOf[AssertTrueExp])
    transformedExp match {
      case AssertTrueExp(expr) =>
        assert(expr == NEQExpression(IntValue(10), IntValue(5)))
      case _ => fail("Transformation to AssertTrueExp failed")
    }
  }

  test("Test transformation of a ForStmt into a SequenceExp") {
    val forStmt = ForStmt(
      init = AssignmentStmt(VarAssignment("i"), IntValue(0)),
      condition = LTEExpression(VarExpression("i"), IntValue(10)),
      stmt = AssignmentStmt(VarAssignment("i"), AddExpression(VarExpression("i"), IntValue(1)))
    )

    val transformedExp = CoreMTransformer.reduceToCoreExpression(forStmt)

    assert(transformedExp.isInstanceOf[SequenceExp])
    transformedExp match {
      case SequenceExp((initExp, whileExp)) =>
        assert(initExp.isInstanceOf[AssignmentExp])
        assert(whileExp.isInstanceOf[WhileExp])
        whileExp match {
          case WhileExp(cond, body) =>
            assert(cond == LTEExpression(VarExpression("i"), IntValue(10)))
            assert(body.isInstanceOf[AssignmentExp])
          case _ => fail("Transformation of WhileExp inside ForStmt failed")
        }
      case _ => fail("Transformation of ForStmt into SequenceExp failed")
    }
  }

  test("Test transformation of a CaseStmt into IfElseExp (No VarExp)") {
    val caseStmt = CaseStmt(
  exp = AddExpression(IntValue(1), IntValue(2)), 
  cases = List(
    SimpleCase(IntValue(1), AssignmentStmt(VarAssignment("y"), IntValue(10))),
    RangeCase(IntValue(2), IntValue(5), AssignmentStmt(VarAssignment("y"), IntValue(20)))
  ),
  elseStmt = Some(AssignmentStmt(VarAssignment("y"), IntValue(30)))
)

    val transformedExp = CoreMTransformer.reduceToCoreExpression(caseStmt)

    assert(transformedExp.isInstanceOf[SequenceExp])
    transformedExp match {
      case SequenceExp((assignmentExp, ifElseExp)) =>
        assert(assignmentExp.isInstanceOf[AssignmentExp])
        assert(ifElseExp.isInstanceOf[IfElseExp])
      case _ => fail("Transformation of CaseStmt into SequenceExp failed")
    }
  }

  test("Test transformation of a CaseStmt into IfElseExp (VarExp)") {
    val caseStmt = CaseStmt(
  exp = VarExpression("x"), 
  cases = List(
    SimpleCase(IntValue(1), AssignmentStmt(VarAssignment("y"), IntValue(10))),
    RangeCase(IntValue(2), IntValue(5), AssignmentStmt(VarAssignment("y"), IntValue(20)))
  ),
  elseStmt = Some(AssignmentStmt(VarAssignment("y"), IntValue(30)))
)

    val transformedExp = CoreMTransformer.reduceToCoreExpression(caseStmt)

    assert(transformedExp.isInstanceOf[IfElseExp])
    transformedExp match{
      case IfElseExp(condition, thenExp, elseExp) =>
        assert(condition.isInstanceOf[EQExpression])
        condition match{
          case EQExpression(left, right) => 
            assert(left == VarExpression("x"))
            assert(right == IntValue(1))
          case _ => fail("No EQExpression found")  
        }
        assert(thenExp == AssignmentExp(VarAssignment("y"),IntValue(10)))

        elseExp match {
        case Some(exp) =>
          assert(exp.isInstanceOf[IfElseExp]) 
          exp match {
            case IfElseExp(innerCondition, innerThenExp, innerElseExp) =>
              assert(innerCondition.isInstanceOf[AndExpression])
              assert(innerThenExp == AssignmentExp(VarAssignment("y"), IntValue(20)))

              innerElseExp match {
                case Some(finalElseExp) =>
                  assert(finalElseExp == AssignmentExp(VarAssignment("y"), IntValue(30)))
                case None => fail("No elseStmt for final transformation")
              }
            case _ => fail("Expected an IfElseExp in elseExp")
          }
        case None => fail("No elseExp found")
      }
      case _ => fail("Transformation of CaseStmt(VarExp) into IfElseExp failed")
    }
  }

  test("Test transformation of a Procedure with constants and variables") {
    val procedure = Procedure(
      name = "testProcedureWithVars",
      args = List(),
      returnType = Some(IntegerType),
      constants = List(Constant("TEN", IntValue(10))),
      variables = List(VariableDeclaration("x", IntegerType), VariableDeclaration("y", IntegerType)),
      stmt = AssignmentStmt(VarAssignment("x"), IntValue(10))
    )

    val transformedProcedure = CoreMTransformer.reduceProcedureDeclaration(procedure, new AtomicInteger(), new ArrayBuffer())

    assert(transformedProcedure.stmt.isInstanceOf[AssignmentExp])
    assert(transformedProcedure.constants.contains(Constant("TEN", IntValue(10))))
    assert(transformedProcedure.variables.contains(VariableDeclaration("x", IntegerType)))
    assert(transformedProcedure.variables.contains(VariableDeclaration("y", IntegerType)))
  }

  test("Test transformation of a procedure with return value and no args") {
    val procedure = Procedure(
      name = "testReturnProcedure",
      args = List(),
      returnType = Some(IntegerType),
      constants = List(),
      variables = List(VariableDeclaration("x", IntegerType)),
      stmt = AssignmentStmt(VarAssignment("x"), IntValue(100))
    )

    val transformedProcedure = CoreMTransformer.reduceProcedureDeclaration(procedure, new AtomicInteger(), new ArrayBuffer())

    assert(transformedProcedure.returnType.contains(IntegerType))
    assert(transformedProcedure.stmt.isInstanceOf[AssignmentExp])
    assert(transformedProcedure.variables.contains(VariableDeclaration("x", IntegerType)))
  }

  test("Test transformation of an OberonModule into its Core representation") {
  val oberonModule = OberonModule(
    name = "TestModule",
    submodules = Set("SubModule1", "SubModule2"),
    userTypes = List(), 
    constants = List(Constant("PI", RealValue(3.14))),
    variables = List(VariableDeclaration("x", IntegerType)),
    procedures = List(
      Procedure(
        name = "simpleProcedure",
        args = List(),
        returnType = Some(IntegerType),
        constants = List(),
        variables = List(VariableDeclaration("y", IntegerType)),
        stmt = AssignmentStmt(VarAssignment("y"), IntValue(10))
      )
    ),
    tests = List(
      Test(
        modifier = "public",
        name = "test1",
        description = StringValue("Test description"),
        constants = List(Constant("A", IntValue(1))),
        variables = List(VariableDeclaration("z", BooleanType)),
        stmt = AssignmentStmt(VarAssignment("x"), IntValue(42))
      )
    ),
    stmt = Some(AssignmentStmt(VarAssignment("x"), IntValue(42)))
  )

  val transformedModule = CoreMTransformer.reduceOberonModule(oberonModule)

  //verificar se as informações foram preservadas
  assert(transformedModule.name == "TestModule")
  assert(transformedModule.submodules == Set("SubModule1", "SubModule2"))
  assert(transformedModule.constants == List(Constant("PI", RealValue(3.14))))
  assert(transformedModule.variables == List(VariableDeclaration("x", IntegerType)))
  transformedModule.exp match {
    case Some(exp) =>
      assert(exp.isInstanceOf[AssignmentExp])
      exp match {
        case AssignmentExp(VarAssignment("x"), IntValue(42)) =>
          succeed 
        case _ => fail("A expressão principal não foi transformada corretamente")
      }
    case None => fail("Nenhuma expressão principal encontrada")
  }

  assert(transformedModule.procedures.size == 1)

  val transformedProcedure = transformedModule.procedures.head
  assert(transformedProcedure.name == "simpleProcedure")
  assert(transformedProcedure.returnType.contains(IntegerType))
  
  assert(transformedProcedure.variables == List(VariableDeclaration("y", IntegerType)))

  transformedProcedure.stmt match {
    case AssignmentExp(VarAssignment("y"), IntValue(10)) =>
      succeed 
    case _ => fail("A declaração do procedimento não foi transformada corretamente")
  }

  // Verificar se os testes foram transformados corretamente
  assert(transformedModule.tests.size == 1)
  
  val transformedTest = transformedModule.tests.head
  assert(transformedTest.modifier == "public")
  assert(transformedTest.name == "test1")
  assert(transformedTest.description == StringValue("Test description"))
  assert(transformedTest.constants == List(Constant("A", IntValue(1))))
  assert(transformedTest.variables == List(VariableDeclaration("z", BooleanType)))

  transformedTest.exp match {
    case AssignmentExp(VarAssignment("x"), IntValue(42)) =>
      succeed 
    case _ => fail("A expressão do teste não foi transformada corretamente")
  }
}



}
