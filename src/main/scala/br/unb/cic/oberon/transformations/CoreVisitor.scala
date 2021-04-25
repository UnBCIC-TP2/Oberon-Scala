package br.unb.cic.oberon.transformations

import br.unb.cic.oberon.interpreter.Interpreter
import br.unb.cic.oberon.ast.{OberonModule, EQExpression, LTEExpression, AndExpression, SequenceStmt, BoolValue, ForStmt, LoopStmt, ExitStmt, Statement, WhileStmt, RepeatUntilStmt, CaseStmt, IfElseIfStmt, ElseIfStmt, IfElseStmt, SimpleCase, RangeCase, CaseAlternative, Expression}
import br.unb.cic.oberon.visitor.OberonVisitorAdapter
import scala.collection.mutable.ListBuffer

class CoreVisitor extends OberonVisitorAdapter {

    override type T = Statement

    override def visit(stmt: Statement): Statement = stmt match {
        // Laços de Repetição
        case SequenceStmt(stmts) => SequenceStmt(flatSequenceOfStatements(SequenceStmt(transformListStmts(stmts)).stmts))
        case LoopStmt(stmt) => WhileStmt(BoolValue(true), stmt.accept(this))
        case RepeatUntilStmt(condition, stmt) => WhileStmt(BoolValue(true), SequenceStmt(List(stmt.accept(this), IfElseStmt(condition, ExitStmt(), None))).accept((this)))
        case ForStmt(initStmt, condition, block) => SequenceStmt(List(initStmt.accept(this), WhileStmt(condition, block.accept(this))))

        // Condicionais
        case IfElseIfStmt (condition, thenStmt, elsifStmt, elseStmt) => IfElseStmt (condition, thenStmt.accept(this), Some(ifAux(elsifStmt, elseStmt)))
        case CaseStmt(exp, cases, elseStmt) => caseAux(exp, cases, elseStmt)

        case other => stmt
    }

    //TODO: Criar um visitor para Expressões. Ex: Transformar todos os x > y em y < x 
    //                                            GTExpression(left, right) => LTExpression(right, left)
                                               

    private def caseAux(exp: Expression, cases: List[CaseAlternative], elseStmt: Option[Statement]): Statement = {
        
        if (!cases.isEmpty){
            if (cases.head.isInstanceOf[SimpleCase]){
                return IfElseStmt(EQExpression(exp,     cases.head.asInstanceOf[SimpleCase].condition), cases.head.asInstanceOf[SimpleCase].stmt.accept(this), Some(caseAux(exp, cases.tail, Some(elseStmt.get.accept(this)))))
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

    // TODO: change method name to more mnemonic name such as CoreVisitor.readListStmts()
    private def transformListStmts(stmtsList: List[Statement], stmtsCore: ListBuffer[Statement] = new ListBuffer[Statement]): List[Statement] = {

        if (!stmtsList.isEmpty){
            stmtsCore += stmtsList.head.accept(this); 
            stmtsCore :: transformListStmts(stmtsList.tail, stmtsCore)
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

    def transformModule(module: OberonModule): OberonModule = {

        val stmtcore = module.stmt.get.accept(this)
        // TODO: Resolver transformação a core das procedures declaradas.


        // this might be to much hardcoded and could cause problems in case of changes to OberonModule.
        val coreModule = OberonModule(
            name = module.name,
            userTypes = module.userTypes,
            constants = module.constants,
            variables = module.variables,
            procedures = module.procedures,
            stmt = Some(stmtcore)
        )

        return  coreModule
    }

}
