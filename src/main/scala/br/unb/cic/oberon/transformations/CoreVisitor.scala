package br.unb.cic.oberon.transformations

import br.unb.cic.oberon.ast.{SequenceStmt, BoolValue, ForStmt, LoopStmt, Statement, WhileStmt, RepeatUntilStmt, CaseStmt, IfElseIfStmt, ElseIfStmt, IfElseStmt}
import br.unb.cic.oberon.visitor.OberonVisitorAdapter

class CoreVisitor extends OberonVisitorAdapter {

    override type T = Statement

    override def visit(stmt: Statement): Statement = stmt match {
        // Laços de Repetição
        case LoopStmt(stmt) => WhileStmt(BoolValue(true), stmt)
        //case RepeatUntilStmt(condition, stmt) => SequenceStmt(stmt, WhileStmt(condition, stmt))
        //case ForStmt(init, condition, block) => SequenceStmt(init, WhileStmt(condition, block)) // init = init_stmt

        // Condicionais
        //case ElseIfStmt(condition, thenStmt) =>  IfElseStmt(condition, thenStmt, None) // É necessário?
        //case IfElseIfStmt(condition, thenStmt, elseifStmt, elseStmt) => IfElseStmt(condition, SequenceStmt(thenStmt, elseifStmt), elseStmt)
        // case CaseStmt(exp, cases, elseStmt) => IfElseStmt(exp, cases, )

    }
 
}