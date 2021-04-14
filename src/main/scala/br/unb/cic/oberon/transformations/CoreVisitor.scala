package br.unb.cic.oberon.transformations

import br.unb.cic.oberon.ast.{AssignmentStmt, SequenceStmt, BoolValue, ForStmt, LoopStmt, Statement, WhileStmt, RepeatUntilStmt, CaseStmt, IfElseIfStmt, ElseIfStmt, IfElseStmt}
import br.unb.cic.oberon.visitor.OberonVisitorAdapter
import br.unb.cic.oberon.ast.OberonModule

import scala.collection.mutable.ListBuffer
import br.unb.cic.oberon.ast.ExitStmt

class CoreVisitor extends OberonVisitorAdapter {

    override type T = Statement

    override def visit(stmt: Statement): Statement = stmt match {
        // Laços de Repetição
        
        case SequenceStmt(stmts) => SequenceStmt(coreStmts(stmts))
        case LoopStmt(stmt) => WhileStmt(BoolValue(true), stmt)
        
        case other => stmt
        //case RepeatUntilStmt(condition, stmt) => SequenceStmt(stmt, WhileStmt(condition, stmt))
        //case ForStmt(init, condition, block) => SequenceStmt(init, WhileStmt(condition, block)) // init = init_stmt

        // Condicionais
        //case ElseIfStmt(condition, thenStmt) =>  IfElseStmt(condition, thenStmt, None) // É necessário?
        //case IfElseIfStmt(condition, thenStmt, elseifStmt, elseStmt) => IfElseStmt(condition, SequenceStmt(thenStmt, elseifStmt), elseStmt)
        // case CaseStmt(exp, cases, elseStmt) => IfElseStmt(exp, cases, )

    }

    def coreStmts(stmts: List[Statement]): List[Statement] = {
        val core = new CoreVisitor()
        var coreStmts = new ListBuffer[Statement]()
        
        for (i <- stmts){
            coreStmts += i.accept(core)
        }

        return coreStmts.toList
    }
 
}