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
        case LoopStmt(stmt) => WhileStmt(BoolValue(true), stmt)
        case RepeatUntilStmt(condition, stmt) => WhileStmt(BoolValue(true), SequenceStmt(List(stmt, IfElseStmt(condition, ExitStmt(), None))))
        case ForStmt(init, condition, block) => SequenceStmt(List(init, WhileStmt(condition, block))) // init = init_stmt

        // Condicionais
        case ElseIfStmt(condition, thenStmt) =>  IfElseStmt(condition, thenStmt, None) // É necessário?
        //case IfElseIfStmt(condition, thenStmt, elseifStmt, elseStmt) => IfElseStmt(condition, thenStmt, Some(coreIfElseIf(coreStmts(elseifStmt), elseStmt)))
        //case IfElseIfStmt(condition, thenStmt, elseifStmt, elseStmt) => IfElseStmt(condition, thenStmt, Some(coreIfElse(elseifStmt, elseStmt)))
        // case CaseStmt(exp, cases, elseStmt) => IfElseStmt(exp, cases, )

        //case WhileStmt(condition, stmt) => LoopStmt(IfElseStmt(condition, ExitStmt(), Some(stmt)))
        case other => stmt
    }

    //TODO
/*
    def coreIfElse(elseIfStmt: List[Statement], elseStmt: Option[Statement]): Statement = {
        case elseIfStmt.tail.isEmpty => return IfElseStmt(elseIfStmt.head.condition, elseIfStmt.head.thenStmt, elseStmt)
        case other => return IfElseStmt(elseIfStmt.head.condtion, elseIfStmt.head.thenStmt, coreIfElse(elseIfStmt.tail, elseStmt))
    }
*/
    def coreStmts(stmts: List[Statement]): List[Statement] = {
        val coreVisitor = new CoreVisitor()
        var coreStmts = new ListBuffer[Statement]()
        
        for (i <- stmts) coreStmts += i.accept(coreVisitor)

        return coreStmts.toList
    }

    
 
}