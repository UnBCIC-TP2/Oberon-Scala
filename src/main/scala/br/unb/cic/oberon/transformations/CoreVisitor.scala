package br.unb.cic.oberon.transformations

import br.unb.cic.oberon.interpreter.Interpreter
import br.unb.cic.oberon.ast.{SequenceStmt, BoolValue, ForStmt, LoopStmt, Statement, WhileStmt, RepeatUntilStmt, CaseStmt, IfElseIfStmt, ElseIfStmt, IfElseStmt, AndExpression}
import br.unb.cic.oberon.visitor.OberonVisitorAdapter
import br.unb.cic.oberon.ast.OberonModule
import scala.collection.mutable.ListBuffer
import br.unb.cic.oberon.ast.ExitStmt
import org.scalactic.Bool

class CoreVisitor extends OberonVisitorAdapter {

    override type T = Statement

    override def visit(stmt: Statement): Statement = stmt match {
        // Laços de Repetição

        case SequenceStmt(stmts) => SequenceStmt(coreStmts(stmts))
        case LoopStmt(stmt) => WhileStmt(BoolValue(true), stmt.accept(this))
        case RepeatUntilStmt(condition, stmt) => WhileStmt(BoolValue(true), SequenceStmt(List(stmt.accept(this), IfElseStmt(condition, ExitStmt(), None))))
        case ForStmt(init, condition, block) => SequenceStmt(List(init, WhileStmt(condition, block.accept(this)))) // init = init_stmt

        // Condicionais
        case ElseIfStmt(condition, thenStmt) =>  IfElseStmt(condition, thenStmt, None) // É necessário?
        //case IfElseIfStmt(condition, thenStmt, elseifStmt, elseStmt) => IfElseStmt(condition, thenStmt, Some(coreIfElseIf(coreStmts(elseifStmt), elseStmt)))
        //case IfElseIfStmt(condition, thenStmt, elseifStmt, elseStmt) => IfElseStmt(condition, thenStmt, Some(coreIfElse(elseifStmt, elseStmt)))
        // case CaseStmt(exp, cases, elseStmt) => IfElseStmt(exp, cases, )

        //case WhileStmt(condition, stmt) => LoopStmt(IfElseStmt(condition, ExitStmt(), Some(stmt)))
        case other => stmt
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

    // TODO: add entrypoint method to users such as CoreVisitor.transform() which receives the whole Module

}
