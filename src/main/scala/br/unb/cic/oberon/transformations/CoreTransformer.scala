package br.unb.cic.oberon.transformations

import br.unb.cic.oberon.ast._
import br.unb.cic.oberon.visitor.OberonVisitorAdapter
import scala.collection.mutable.ListBuffer
import br.unb.cic.oberon.ast.Procedure

class CoreVisitor extends OberonVisitorAdapter {

  override type T = Statement

  override def visit(stmt: Statement): Statement = stmt match {
    case SequenceStmt(stmts) =>
      SequenceStmt(flatSequenceOfStatements(SequenceStmt(transformListStmts(stmts)).stmts))

    case LoopStmt(stmt) =>
      WhileStmt(BoolValue(true), stmt.accept(this))

    case RepeatUntilStmt(condition, stmt) =>
      WhileStmt(BoolValue(true), SequenceStmt(List(stmt.accept(this), IfElseStmt(condition, ExitStmt(), None))).accept(this))

    case ForStmt(initStmt, condition, block) =>
      SequenceStmt(List(initStmt.accept(this), WhileStmt(condition, block.accept(this))))

    case IfElseIfStmt (condition, thenStmt, elsifStmt, elseStmt) =>
      IfElseStmt (condition, thenStmt.accept(this), Some(ifAux(elsifStmt, elseStmt)))

    case CaseStmt(exp, cases, elseStmt) => caseAux(exp, cases, elseStmt)

    case _ => stmt
  }

  private def caseAux(exp: Expression, cases: List[CaseAlternative], elseStmt: Option[Statement]): Statement = {

    if (cases.nonEmpty){
      if (cases.head.isInstanceOf[SimpleCase]){
        return IfElseStmt(EQExpression(exp, cases.head.asInstanceOf[SimpleCase].condition), cases.head.asInstanceOf[SimpleCase].stmt.accept(this), Some(caseAux(exp, cases.tail, Some(elseStmt.get.accept(this)))))
      }
      else{
        return IfElseStmt(AndExpression(LTEExpression(cases.head.asInstanceOf[RangeCase].min, exp), LTEExpression(exp, cases.head.asInstanceOf[RangeCase].max)), cases.head.asInstanceOf[RangeCase].stmt.accept(this), Some(caseAux(exp, cases.tail, Some(elseStmt.get.accept(this)))))
      }
    }else{
      return elseStmt.get.accept(this)
    }
  }

  private def ifAux (elsifStmt: List[ElseIfStmt], elseStmt: Option[Statement]): Statement = { // falta arruamr a entrada da função com os tipos
    if (elsifStmt.tail.isEmpty){ // termina a recursividade
      return IfElseStmt(elsifStmt.head.condition, elsifStmt.head.asInstanceOf[ElseIfStmt].thenStmt.accept(this), Some(elseStmt.get.accept((this))))
    }
    else{
      return IfElseStmt (elsifStmt.head.condition, elsifStmt.head.asInstanceOf[ElseIfStmt].thenStmt.accept(this), Some(ifAux(elsifStmt.tail, Some(elseStmt.get.accept(this)))))
    }
  }

  private def transformListStmts(stmtsList: List[Statement], stmtsCore: ListBuffer[Statement] = new ListBuffer[Statement]): List[Statement] = {

    if (!stmtsList.isEmpty){
      stmtsCore += stmtsList.head.accept(this);
      stmtsCore :: transformListStmts(stmtsList.tail, stmtsCore)
    }
    else {
      return Nil
    }

    stmtsCore.toList
  }

  private def transformProcedureListStatement(listProcedures: List[Procedure]): List[Procedure] = {

    var listProceduresCore = ListBuffer[Procedure]()

    for (procedure <- listProcedures){
      listProceduresCore += Procedure(name = procedure.name,
        args = procedure.args,
        referenceMap = procedure.referenceMap,
        returnType = procedure.returnType,
        constants = procedure.constants,
        variables = procedure.variables,
        stmt = procedure.stmt.accept(this))
    }
    listProceduresCore.toList
  }

  def flatSequenceOfStatements(stmts: List[Statement]): List[Statement] =
    stmts match {
      case SequenceStmt(ss) :: rest => flatSequenceOfStatements(ss) ++ flatSequenceOfStatements(rest)
      case s :: rest => s :: flatSequenceOfStatements(rest)
      case Nil => List()
    }

  def transformModule(module: OberonModule): OberonModule = {

    val stmtcore = module.stmt.get.accept(this)
    val stmtprocedureList = transformProcedureListStatement(module.procedures)

     val coreModule = OberonModule(
      name = module.name,
      submodules = module.submodules,
      userTypes = module.userTypes,
      constants = module.constants,
      variables = module.variables,
      procedures = stmtprocedureList,
      stmt = Some(stmtcore)
    )

    coreModule
  }

}

object CoreChecker extends OberonVisitorAdapter {
  private var isCore = true

  override type T = Unit

  override def visit(stmt: Statement): Unit = {

    if (!isCore) {
      return
    }

    stmt match {
      case SequenceStmt(stmts) => transformListStmts(stmts)
      case LoopStmt(stmt) => isCore = false
      case RepeatUntilStmt(condition, stmt) => isCore = false
      case ForStmt(initStmt, condition, block) => isCore = false

      // Condicionais
      case IfElseIfStmt (condition, thenStmt, elsifStmt, elseStmt) => isCore = false
      case CaseStmt(exp, cases, elseStmt) => isCore = false

      // Outros casos com SequenceStmt
      case WhileStmt(condition, whileStmt) => whileStmt.accept(this)
      case IfElseStmt(condition, thenStmt, elseStmt) => thenStmt.accept(this);
        elseStmt match {
          case Some(f) => Some(elseStmt.get.accept((this)))
          case None => ()
        }

      case _ => ()
    }
  }

  private def transformListStmts(stmtsList: List[Statement], stmtsCore: ListBuffer[Statement] = new ListBuffer[Statement]): Unit = {
    if (stmtsList.nonEmpty){
      stmtsList.head.accept(this);
      transformListStmts(stmtsList.tail)
    }
  }

  private def checkProcedureStmts(listProcedures: List[Procedure]): Unit = {
    for (procedure <- listProcedures){
      procedure.stmt.accept(this)
    }
  }

  def isModuleCore(module: OberonModule): Boolean = {
    isCore = true
    module.stmt.get.accept(this)
    if (!isCore){
      isCore
    }
    else{
      module.procedures.foreach{x : Procedure => x.stmt.accept(this)}
      isCore
    }
  }
}
