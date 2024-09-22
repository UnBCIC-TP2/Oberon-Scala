package br.unb.cic.oberon.transformations

import br.unb.cic.oberon.ir.ast._
import br.unb.cic.oberon.ir.common._
import br.unb.cic.oberon.ir.core._
import br.unb.cic.oberon.visitor.OberonVisitorAdapter

import scala.collection.mutable.ListBuffer
//import br.unb.cic.oberon.ir.ast.Procedure

import java.util.concurrent.atomic.AtomicInteger
import scala.collection.mutable._
import _root_.br.unb.cic.oberon.ir.core.WhileExp



object CoreMTransformer {

  def flatSequenceOfStatements(stmts: List[Statement]): List[Statement] =
  stmts.flatMap {
    case SequenceStmt(ss) => flatSequenceOfStatements(ss)
    case s => List(s)
  }



    
  def reduceToCoreExpression(
                             stmt: Statement,
                             caseIdGenerator: AtomicInteger = new AtomicInteger(0),
                             addedVariables: ArrayBuffer[VariableDeclaration] =
                             new ArrayBuffer[VariableDeclaration]
                           ): Expression = {
    stmt match {

      case SequenceStmt(stmts) =>
        stmts match {
          case Nil => throw new RuntimeException("Empty SequenceStmt")
          case single :: Nil => reduceToCoreExpression(single)
          case _ =>
            val flatStmts = flatSequenceOfStatements(stmts)
            SequenceExp(
              Tuple2(
                reduceToCoreExpression(flatStmts.head),
                reduceToCoreExpression(SequenceStmt(flatStmts.tail))
              )
            )
        }

      case LoopStmt(stmt) =>
        WhileExp(
          BoolValue(true),
          reduceToCoreExpression(stmt, caseIdGenerator, addedVariables)
        )
      case RepeatUntilStmt(condition, stmt) =>
        WhileExp(
          BoolValue(true),
            SequenceExp(
              Tuple2(
                reduceToCoreExpression(stmt, caseIdGenerator, addedVariables),
                reduceToCoreExpression(
                  IfElseStmt(condition, ExitStmt(), None),
                  caseIdGenerator,
                  addedVariables
                )
              )
          )
        )

      case ForStmt(initStmt, condition, block) =>
        SequenceExp(
          Tuple2(
            reduceToCoreExpression(initStmt, caseIdGenerator, addedVariables),
            WhileExp(
              condition,
              reduceToCoreExpression(block, caseIdGenerator, addedVariables)
            )
          )
        )
      
      case IfElseStmt(cond, thenStmt, elseStmtOpt) =>
        val thenExp = reduceToCoreExpression(thenStmt)
        val elseExp = elseStmtOpt match {
          case Some(elseStmt) => Some(reduceToCoreExpression(elseStmt))
          case None => None
        } 
        IfElseExp(cond, thenExp, elseExp)
      
      case IfElseIfStmt(condition, thenStmt, elsifStmt, elseStmt) =>
        IfElseExp(
          condition,
          reduceToCoreExpression(thenStmt, caseIdGenerator, addedVariables),
          Some(
            reduceElsifStatement(
              elsifStmt,
              elseStmt,
              caseIdGenerator,
              addedVariables
            )
          )
        )

      case CaseStmt(exp, cases, elseStmt) =>
        reduceCaseStatement(
          exp,
          cases,
          elseStmt,
          caseIdGenerator,
          addedVariables
        )
      case WhileStmt(condition, stmt) =>
        WhileExp(
          condition,
          reduceToCoreExpression(stmt, caseIdGenerator, addedVariables)
        )

      case AssertEqualStmt(left, right) =>
        AssertTrueExp(EQExpression(left, right))

      case AssertNotEqualStmt(left, right) =>
        AssertTrueExp(NEQExpression(left, right))

      case AssertError() =>
        AssertTrueExp(BoolValue(false))
      
      case AssignmentStmt(designator, exp) => AssignmentExp(designator, exp)

      case ExitStmt() => ExitExp()

      case _ => throw new RuntimeException(s"Unknown statement type: ${stmt.getClass}")
    }
    
      
  }

  private def reduceElsifStatement(
                                      elsifStmts: List[ElseIfStmt],
                                      elseStmt: Option[Statement],
                                      caseIdGenerator: AtomicInteger,
                                      addedVariables: ArrayBuffer[VariableDeclaration]
                                    ): Expression =
      elsifStmts match {
        case currentElsif :: Nil =>
          IfElseExp(
            currentElsif.condition,
            reduceToCoreExpression(
              currentElsif.thenStmt,
              caseIdGenerator,
              addedVariables
            ),
            elseStmt.map((stmt) =>
              reduceToCoreExpression(stmt, caseIdGenerator, addedVariables)
            )
          )
        case currentElsif :: tail =>
          IfElseExp(
            currentElsif.condition,
            reduceToCoreExpression(
              currentElsif.thenStmt,
              caseIdGenerator,
              addedVariables
            ),
            Some(
              reduceElsifStatement(
                tail,
                elseStmt.map((stmt) =>
                  CoreTransformer.reduceToCoreStatement(stmt, caseIdGenerator, addedVariables)
                ),
                caseIdGenerator,
                addedVariables
              )
            )
          )
        case Nil =>
          throw new IllegalArgumentException("elsifExp cannot be empty.")
      }
      
  private def reduceCaseStatement(
                                  exp: Expression,
                                  cases: List[CaseAlternative],
                                  elseStmt: Option[Statement],
                                  caseIdGenerator: AtomicInteger,
                                  addedVariables: ArrayBuffer[VariableDeclaration]
                                ): Expression = {
  val coreElseStmt = elseStmt.map((stmt) =>
    reduceToCoreExpression(stmt, caseIdGenerator, addedVariables)
  )

  val caseExpressionId = exp match {
    case VarExpression(name) => name
    case _ => {
      f"case_exp#${caseIdGenerator.getAndIncrement()}"
    }
  }
  val caseExpressionEvaluation =
    AssignmentExp(VarAssignment(caseExpressionId), exp)

  def casesToIfElseExp(cases: List[CaseAlternative]): IfElseExp =
    cases match {
      case SimpleCase(condition, stmt) :: Nil =>
        val newCondition =
          EQExpression(VarExpression(caseExpressionId), condition)
        IfElseExp(
          newCondition,
          reduceToCoreExpression(
            stmt,
            caseIdGenerator,
            (VariableDeclaration(
              caseExpressionId,
              IntegerType
            ) :: addedVariables.toList).to(ArrayBuffer)
          ),
          coreElseStmt
        )

      case SimpleCase(condition, stmt) :: tailCases =>
        val newCondition =
          EQExpression(VarExpression(caseExpressionId), condition)
        val newElse = Some(casesToIfElseExp(tailCases))
        IfElseExp(
          newCondition,
          reduceToCoreExpression(
            stmt,
            caseIdGenerator,
            (VariableDeclaration(
              caseExpressionId,
              IntegerType
            ) :: addedVariables.toList).to(ArrayBuffer)
          ),
          newElse
        )

      case RangeCase(min, max, stmt) :: Nil =>
        val newCondition = AndExpression(
          LTEExpression(min, VarExpression(caseExpressionId)),
          LTEExpression(VarExpression(caseExpressionId), max)
        )
        IfElseExp(
          newCondition,
          reduceToCoreExpression(
            stmt,
            caseIdGenerator,
            (VariableDeclaration(
              caseExpressionId,
              IntegerType
            ) :: addedVariables.toList).to(ArrayBuffer)
          ),
          coreElseStmt
        )

      case RangeCase(min, max, stmt) :: tailCases =>
        val newCondition = AndExpression(
          LTEExpression(min, VarExpression(caseExpressionId)),
          LTEExpression(VarExpression(caseExpressionId), max)
        )
        val newElse = Some(casesToIfElseExp(tailCases))
        IfElseExp(
          newCondition,
          reduceToCoreExpression(
            stmt,
            caseIdGenerator,
            (VariableDeclaration(
              caseExpressionId,
              IntegerType
            ) :: addedVariables.toList).to(ArrayBuffer)
          ),
          newElse
        )

      case _ => throw new RuntimeException("Invalid CaseStmt without cases")
    }

  exp match {
    case VarExpression(_) => casesToIfElseExp(cases)
    case _ =>
      // VariableDeclaration(caseExpressionId, IntegerType) +: addedVariables
      addedVariables.appendAll(
        List(VariableDeclaration(caseExpressionId, IntegerType))
      )
      SequenceExp(Tuple2(caseExpressionEvaluation, casesToIfElseExp(cases)))
  }
}
    
    def reduceProcedureDeclaration(
                                            procedure: Procedure[Statement],
                                            caseIdGenerator: AtomicInteger,
                                            addedVariables: ArrayBuffer[VariableDeclaration]
                                          ): Procedure[Expression] = {
      Procedure(
        name = procedure.name,
        args = procedure.args,
        returnType = procedure.returnType,
        constants = procedure.constants,
        variables = procedure.variables,
        stmt =
          reduceToCoreExpression(procedure.stmt, caseIdGenerator, addedVariables)
      )
    }

    private def transformTestListStatement(listTest: List[Test], caseIdGenerator: AtomicInteger,
                                           addedVariables: ArrayBuffer[VariableDeclaration]): List[TestCore] = {

      var listTestCore = ListBuffer[TestCore]()

      for (test <- listTest) {
        val coreExp = reduceToCoreExpression(test.stmt, caseIdGenerator, addedVariables)
        listTestCore += TestCore(modifier = test.modifier,
          name = test.name,
          description = test.description,
          constants = test.constants,
          variables = test.variables ++ addedVariables,
          exp = coreExp)
      }
      listTestCore.toList
    }


    def reduceOberonModule(
                            module: OberonModule,
                            caseIdGenerator: AtomicInteger = new AtomicInteger(0),
                            addedVariables: ArrayBuffer[VariableDeclaration] =
                            new ArrayBuffer[VariableDeclaration]
                          ): CoreModule = {
      // É possível remover essa val?
      val stmtcore =
        reduceToCoreExpression(module.stmt.get, caseIdGenerator, addedVariables)

      val testList = transformTestListStatement(module.tests, caseIdGenerator, addedVariables)
      CoreModule(
        name = module.name,
        submodules = module.submodules,
        constants = module.constants,
        variables = module.variables ++ addedVariables,
        procedures = module.procedures.map(
          reduceProcedureDeclaration(_, caseIdGenerator, addedVariables)
        ),
        tests = testList,
        exp = Some(stmtcore)
      )
    }
  }


object CoreChecker {
    def stmtCheck(stmt: Statement): Boolean =
      stmt match {
        case SequenceStmt(stmts) =>
          stmts.map(s => stmtCheck(s)).foldLeft(true)(_ && _)
        case LoopStmt(stmt) => false
        case RepeatUntilStmt(condition, stmt) => false
        case ForStmt(initStmt, condition, block) => false
        case WhileStmt(_, whileStmt) => stmtCheck(whileStmt)
        case CaseStmt(exp, cases, elseStmt) => false
        case IfElseIfStmt(condition, thenStmt, elsifStmt, elseStmt) => false
        case IfElseStmt(_, thenStmt, Some(elseStmt)) =>
          stmtCheck(thenStmt) && stmtCheck(elseStmt)
        case _ => true
      }
    

    def checkModule(module: OberonModule): Boolean =
      stmtCheck(module.stmt.get) && module.procedures
        .map(p => stmtCheck(p.stmt))
        .foldLeft(true)(_ && _)

  }