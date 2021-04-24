package br.unb.cic.oberon.transformations

import br.unb.cic.oberon.interpreter.Interpreter
import br.unb.cic.oberon.ast.{OberonModule, EQExpression, LTEExpression, AndExpression, SequenceStmt, BoolValue, ForStmt, LoopStmt, ExitStmt, Statement, WhileStmt, RepeatUntilStmt, CaseStmt, IfElseIfStmt, ElseIfStmt, IfElseStmt, SimpleCase, RangeCase, CaseAlternative, Expression}
import br.unb.cic.oberon.visitor.OberonVisitorAdapter
import scala.collection.mutable.ListBuffer

class CoreVisitor extends OberonVisitorAdapter {

    override type T = Statement

    override def visit(stmt: Statement): Statement = stmt match {
        // Laços de Repetição
        case SequenceStmt(stmts) => SequenceStmt(flatSequenceOfStatements(SequenceStmt(coreStmts(stmts)).stmts))
        case LoopStmt(stmt) => WhileStmt(BoolValue(true), stmt.accept(this))
        case RepeatUntilStmt(condition, stmt) => WhileStmt(BoolValue(true), SequenceStmt(List(stmt.accept(this), IfElseStmt(condition, ExitStmt(), None))))
        case ForStmt(initStmt, condition, block) => SequenceStmt(List(initStmt, WhileStmt(condition, block.accept(this))))

        // Condicionais
        case IfElseIfStmt (condition, thenStmt, elsifStmt, elseStmt) => IfElseStmt (condition, thenStmt, Some(ifAux(elsifStmt, elseStmt)))
        case CaseStmt(exp, cases, elseStmt) => caseAux(exp, cases, elseStmt)

        case other => stmt
    }

    def caseAux(exp: Expression, cases: List[CaseAlternative], elseStmt: Option[Statement]): Statement = {
        
        if (!cases.isEmpty){
            if (cases.head.isInstanceOf[SimpleCase]){
                return IfElseStmt(EQExpression(cases.head.asInstanceOf[SimpleCase].condition, exp), cases.head.asInstanceOf[SimpleCase].stmt, Some(caseAux(exp, cases.tail, elseStmt)))
            }
            else{
                return IfElseStmt(AndExpression(LTEExpression(cases.head.asInstanceOf[RangeCase].min, exp), LTEExpression(exp, cases.head.asInstanceOf[RangeCase].max)), cases.head.asInstanceOf[RangeCase].stmt, Some(caseAux(exp, cases.tail, elseStmt)))
            }
        }else{
            return elseStmt.get.accept(this)
        }
    }

    def ifAux (elsifStmt: List[ElseIfStmt], elseStmt: Option[Statement]): Statement = { // falta arruamr a entrada da função com os tipos
        if (elsifStmt.tail.isEmpty){ // termina a recursividade
            return IfElseStmt(elsifStmt.head.condition, elsifStmt.head.asInstanceOf[ElseIfStmt].thenStmt, elseStmt)
        }
        else{
            return IfElseStmt (elsifStmt.head.condition, elsifStmt.head.asInstanceOf[ElseIfStmt].thenStmt, Some(ifAux(elsifStmt.tail, elseStmt)))
        }
    }

    // TODO: change method name to more mnemonic name such as CoreVisitor.readListStmts()
    def coreStmts(stmtsList: List[Statement], stmtsCore: ListBuffer[Statement] = new ListBuffer[Statement]): List[Statement] = {

        if (!stmtsList.isEmpty){
            stmtsCore += stmtsList.head.accept(this); 
            stmtsCore :: coreStmts(stmtsList.tail, stmtsCore)
        }
        else {
            return Nil
        }

        return stmtsCore.toList
    }

    def flatSequenceOfStatements(stmts: List[Statement]): List[Statement] =
      stmts match {
        case SequenceStmt(ss) :: rest => flatSequenceOfStatements(ss) ++ flatSequenceOfStatements(rest)
        case s :: rest => s :: flatSequenceOfStatements(rest)
        case Nil => List()
      }

    // TODO: add entrypoint method to users such as CoreVisitor.transform() which receives the whole Module

}
