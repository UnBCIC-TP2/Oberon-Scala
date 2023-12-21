package br.unb.cic.oberon.transformations

import shapeless._
import poly._
import br.unb.cic.oberon.ir.ast._
import br.unb.cic.oberon.visitor.OberonVisitorAdapter

import scala.collection.mutable.ListBuffer
import br.unb.cic.oberon.ir.ast.Procedure

import java.util.concurrent.atomic.AtomicInteger
import scala.collection.mutable._

object CoreTransformer {

  def reduceToCoreStatement(
                             stmt: Statement,
                             caseIdGenerator: AtomicInteger = new AtomicInteger(0),
                             addedVariables: ArrayBuffer[VariableDeclaration] =
                             new ArrayBuffer[VariableDeclaration]
                           ): Statement = {
    stmt match {
      case SequenceStmt(stmts) =>
        SequenceStmt(
          flatSequenceOfStatements(
            SequenceStmt(
              stmts.map((stmt) =>
                reduceToCoreStatement(stmt, caseIdGenerator, addedVariables)
              )
            ).stmts
          )
        )

      case LoopStmt(stmt) =>
        WhileStmt(
          BoolValue(true),
          reduceToCoreStatement(stmt, caseIdGenerator, addedVariables)
        )
      // condition --> expression
      case RepeatUntilStmt(condition, stmt) =>
        WhileStmt(
          BoolValue(true),
          reduceToCoreStatement(
            SequenceStmt(
              List(
                reduceToCoreStatement(stmt, caseIdGenerator, addedVariables),
                reduceToCoreStatement(
                  IfElseStmt(reduceExpressionToProcedure(condition), ExitStmt(), None),
                  caseIdGenerator,
                  addedVariables
                )
              )
            )
          )
        )
      // condition --> expression
      case ForStmt(initStmt, condition, block) =>
        SequenceStmt(
          List(
            reduceToCoreStatement(initStmt, caseIdGenerator, addedVariables),
            WhileStmt(
              reduceExpressionToProcedure(condition),
              reduceToCoreStatement(block, caseIdGenerator, addedVariables)
            )
          )
        )
      // condition --> expression
      case IfElseIfStmt(condition, thenStmt, elsifStmt, elseStmt) =>
        IfElseStmt(
          reduceExpressionToProcedure(condition),
          reduceToCoreStatement(thenStmt, caseIdGenerator, addedVariables),
          Some(
            reduceElsifStatement(
              elsifStmt,
              elseStmt,
              caseIdGenerator,
              addedVariables
            )
          )
        )

      // exp --> expression
      case CaseStmt(exp, cases, elseStmt) =>
        reduceCaseStatement(
          reduceExpressionToProcedure(exp),
          cases,
          elseStmt,
          caseIdGenerator,
          addedVariables
        )

      // condition --> expressioin
      case WhileStmt(condition, stmt) =>
        WhileStmt(
          reduceExpressionToProcedure(condition),
          reduceToCoreStatement(stmt, caseIdGenerator, addedVariables)
        )
      // left, right --> expression
      case AssertEqualStmt(left, right) =>
        AssertTrueStmt(EQExpression(reduceExpressionToProcedure(left), reduceExpressionToProcedure(right)))
      
      // left, right --> expression
      case AssertNotEqualStmt(left, right) =>
        AssertTrueStmt(NEQExpression(reduceExpressionToProcedure(left), reduceExpressionToProcedure(right)))

      case AssertError() =>
        AssertTrueStmt(BoolValue(false))

      case _ => stmt
    }
  }

    private def reduceCaseStatement(
                                     exp: Expression,
                                     cases: List[CaseAlternative],
                                     elseStmt: Option[Statement],
                                     caseIdGenerator: AtomicInteger,
                                     addedVariables: ArrayBuffer[VariableDeclaration]
                                   ): Statement = {
      val coreElseStmt = elseStmt.map((stmt) =>
        reduceToCoreStatement(stmt, caseIdGenerator, addedVariables)
      )

      val caseExpressionId = exp match {
        case VarExpression(name) => name
        case _ => {
          f"case_exp#${caseIdGenerator.getAndIncrement()}"
        }
      }
      val caseExpressionEvaluation =
        AssignmentStmt(VarAssignment(caseExpressionId), exp)

      def casesToIfElseStmt(cases: List[CaseAlternative]): IfElseStmt =
        cases match {
          case SimpleCase(condition, stmt) :: Nil =>
            val newCondition =
              EQExpression(VarExpression(caseExpressionId), condition)
            IfElseStmt(
              newCondition,
              reduceToCoreStatement(
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
            val newElse = Some(casesToIfElseStmt(tailCases))
            IfElseStmt(
              newCondition,
              reduceToCoreStatement(
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
            IfElseStmt(
              newCondition,
              reduceToCoreStatement(
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
            val newElse = Some(casesToIfElseStmt(tailCases))
            IfElseStmt(
              newCondition,
              reduceToCoreStatement(
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
        case VarExpression(_) => casesToIfElseStmt(cases)
        case _ =>
          // VariableDeclaration(caseExpressionId, IntegerType) +: addedVariables
          addedVariables.appendAll(
            List(VariableDeclaration(caseExpressionId, IntegerType))
          )
          SequenceStmt(List(caseExpressionEvaluation, casesToIfElseStmt(cases)))
      }
    }

    private def reduceElsifStatement(
                                      elsifStmts: List[ElseIfStmt],
                                      elseStmt: Option[Statement],
                                      caseIdGenerator: AtomicInteger,
                                      addedVariables: ArrayBuffer[VariableDeclaration]
                                    ): Statement =
      elsifStmts match {
        // currentElsif.condition --> expression
        case currentElsif :: Nil =>
          IfElseStmt(
            reduceExpressionToProcedure(currentElsif.condition),
            reduceToCoreStatement(
              currentElsif.thenStmt,
              caseIdGenerator,
              addedVariables
            ),
            elseStmt.map((stmt) =>
              reduceToCoreStatement(stmt, caseIdGenerator, addedVariables)
            )
          )
        // currentElsif.condition --> expression        
        case currentElsif :: tail =>
          IfElseStmt(
            reduceExpressionToProcedure(currentElsif.condition),
            reduceToCoreStatement(
              currentElsif.thenStmt,
              caseIdGenerator,
              addedVariables
            ),
            Some(
              reduceElsifStatement(
                tail,
                elseStmt.map((stmt) =>
                  reduceToCoreStatement(stmt, caseIdGenerator, addedVariables)
                ),
                caseIdGenerator,
                addedVariables
              )
            )
          )
        case Nil =>
          throw new IllegalArgumentException("elsifStmts cannot be empty.")
      }

    private def reduceProcedureDeclaration(
                                            procedure: Procedure,
                                            caseIdGenerator: AtomicInteger,
                                            addedVariables: ArrayBuffer[VariableDeclaration]
                                          ): Procedure = {
      Procedure(
        name = procedure.name,
        args = procedure.args,
        returnType = procedure.returnType,
        constants = procedure.constants,
        variables = procedure.variables,
        stmt =
          reduceToCoreStatement(procedure.stmt, caseIdGenerator, addedVariables)
      )
    }

    private def transformTestListStatement(listTest: List[Test], caseIdGenerator: AtomicInteger,
                                           addedVariables: ArrayBuffer[VariableDeclaration]): List[Test] = {

      var listTestCore = ListBuffer[Test]()

      for (test <- listTest) {
        val coreStmt = reduceToCoreStatement(test.stmt, caseIdGenerator, addedVariables)
        listTestCore += Test(modifier = test.modifier,
          name = test.name,
          description = test.description,
          constants = test.constants,
          variables = test.variables ++ addedVariables,
          stmt = coreStmt)
      }
      listTestCore.toList
    }

    def flatSequenceOfStatements(stmts: List[Statement]): List[Statement] =
      stmts.flatMap {
        case SequenceStmt(ss) => flatSequenceOfStatements(ss)
        case s => List(s)
      }

    def reduceOberonModule(
                            module: OberonModule,
                            caseIdGenerator: AtomicInteger = new AtomicInteger(0),
                            addedVariables: ArrayBuffer[VariableDeclaration] =
                            new ArrayBuffer[VariableDeclaration]
                          ): OberonModule = {
      // É possível remover essa val?
      val stmtcore =
        reduceToCoreStatement(module.stmt.get, caseIdGenerator, addedVariables)

      val testList = transformTestListStatement(module.tests, caseIdGenerator, addedVariables)
      OberonModule(
        name = module.name,
        submodules = module.submodules,
        userTypes = module.userTypes,
        constants = module.constants,
        variables = module.variables ++ addedVariables,
        procedures = module.procedures.map(
          reduceProcedureDeclaration(_, caseIdGenerator, addedVariables)
        ),
        tests = testList,
        stmt = Some(stmtcore)
      )
    }

    def reduceExpressionToProcedure(exp: Expression): Expression = {
      object polyExp extends Poly1 {
        implicit def lambdaCase =  at[LambdaExpression]{ case LambdaExpression(lis_args, expression) => 
          Procedure(
            name = "lambda",
            args = lis_args,
            returnType = None,
            constants = List(),
            variables = List(),
            stmt = ReturnStmt(expression)
          )
        }
      }
      
      return everywhere(polyExp)(exp)
    }

    // TODO Alterar a função para lidar com expressões dentro de enxpressões
    // TODO Ver artigo compartilhado pelo professor no teams
    def reduceExpressionToProcedure2(exp: Expression): Expression = exp match {
        case LambdaExpression(lis_args, expression) => 
          Procedure(
            name = "lambda",
            args = lis_args,
            returnType = None,
            constants = List(),
            variables = List(),
            stmt = ReturnStmt(expression)
          )
        
        case Brackets(exp) => Brackets(reduceExpressionToProcedure(exp))
        
        case ArraySubscript(exp1, exp2) => ArraySubscript(reduceExpressionToProcedure(exp1), reduceExpressionToProcedure(exp2))
        
        case FieldAccessExpression(exp, name) => FieldAccessExpression(reduceExpressionToProcedure(exp), name)
        
        case EQExpression(exp1, exp2) => EQExpression(reduceExpressionToProcedure(exp1), reduceExpressionToProcedure(exp2))
        
        case NEQExpression(exp1, exp2) => NEQExpression(reduceExpressionToProcedure(exp1), reduceExpressionToProcedure(exp2))
        
        case GTExpression(exp1, exp2) => GTExpression(reduceExpressionToProcedure(exp1), reduceExpressionToProcedure(exp2))
        
        case LTExpression(exp1, exp2) => LTExpression(reduceExpressionToProcedure(exp1), reduceExpressionToProcedure(exp2))
        
        case GTEExpression(exp1, exp2) => GTEExpression(reduceExpressionToProcedure(exp1), reduceExpressionToProcedure(exp2))
        
        case LTEExpression(exp1, exp2) => LTEExpression(reduceExpressionToProcedure(exp1), reduceExpressionToProcedure(exp2))
        
        case AddExpression(exp1, exp2) => AndExpression(reduceExpressionToProcedure(exp1), reduceExpressionToProcedure(exp2))
        
        case SubExpression(exp1, exp2) => SubExpression(reduceExpressionToProcedure(exp1), reduceExpressionToProcedure(exp2))
        
        case MultExpression(exp1, exp2) => MultExpression(reduceExpressionToProcedure(exp1), reduceExpressionToProcedure(exp2))
        
        case DivExpression(exp1, exp2) => DivExpression(reduceExpressionToProcedure(exp1), reduceExpressionToProcedure(exp2))
        
        case OrExpression(exp1, exp2) => OrExpression(reduceExpressionToProcedure(exp1), reduceExpressionToProcedure(exp2))
        
        case AndExpression(exp1, exp2) => AndExpression(reduceExpressionToProcedure(exp1), reduceExpressionToProcedure(exp2))
        
        case ModExpression(exp1, exp2) => ModExpression(reduceExpressionToProcedure(exp1), reduceExpressionToProcedure(exp2))
        
        case NotExpression(exp1, exp2) => NotExpression(reduceExpressionToProcedure(exp1), reduceExpressionToProcedure(exp2))
          
        case _ => exp
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
