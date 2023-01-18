package br.unb.cic.oberon.codegen

import br.unb.cic.oberon.ir.ast.{Constant => ASTConstant, _}
import br.unb.cic.oberon.ir.tac._
import br.unb.cic.oberon.tc.{ExpressionTypeVisitor, TypeChecker}

object TACodeGenerator extends CodeGenerator[List[TAC]] {
  
  val visitor = new ExpressionTypeVisitor(new TypeChecker())

  override def generateCode(module: OberonModule): List[TAC] = {
    List()
  }

  def generateProcedure() {}

  def generateBody() {}

  def generateExpression(expr: Expression, insts: List[TAC]): (Address, List[TAC]) = {
    expr match {//usar visitExpression do tc para ver as expressoes

      case IntValue(value) =>
        return (Constant(value.toString, IntegerType), insts)

      case RealValue(value) =>
        return (Constant(value.toString, RealType), insts)

      case CharValue(value) =>
        return (Constant(value.toString, CharacterType), insts)

      case BoolValue(value) =>
        return (Constant(value.toString, BooleanType), insts)

      case StringValue(value) =>
        return (Constant(value, StringType), insts)

      case NullValue =>
        return (Constant("Null", NullType), insts)

      //TODO PROCURAR TIPO DA VARIAVEL
      //case VarExpression(name) =>
      //  return (Name(name, tipo), insts)

      case AddExpression(left, right) =>
        val (l, insts1) = generateExpression(left, insts)
        val (r, insts2) = generateExpression(right, insts1)
        val t = new Temporary(expr.accept(visitor).get)
        return (t, insts2 :+ AddOp(l, r, t, ""))

      case SubExpression(left, right) =>
        val (l, insts1) = generateExpression(left, insts)
        val (r, insts2) = generateExpression(right, insts1)
        val t = new Temporary(expr.accept(visitor).get)
        return (t, insts2 :+ SubOp(l, r, t, ""))

      case MultExpression(left, right) =>
        val (l, insts1) = generateExpression(left, insts)
        val (r, insts2) = generateExpression(right, insts1)
        val t = new Temporary(expr.accept(visitor).get)
        return (t, insts2 :+ MulOp(l, r, t, ""))

      case DivExpression(left, right) =>
        val (l, insts1) = generateExpression(left, insts)
        val (r, insts2) = generateExpression(right, insts1)
        val t = new Temporary(expr.accept(visitor).get)
        return (t, insts2 :+ DivOp(l, r, t, ""))
        
      case AndExpression(left, right) =>
        val (l, insts1) = generateExpression(left, insts)
        val (r, insts2) = generateExpression(right, insts1)
        val t = new Temporary(expr.accept(visitor).get)
        return (t, insts2 :+ AndOp(l, r, t, ""))

      case OrExpression(left, right) =>
        val (l, insts1) = generateExpression(left, insts)
        val (r, insts2) = generateExpression(right, insts1)
        val t = new Temporary(expr.accept(visitor).get)
        return (t, insts2 :+ OrOp(l, r, t, ""))

      //case FunctionCallExpression(name, args) =>
        //exp.args.foldLeft((List[Address](),insts)) {(acc, expr) => 
          //val (address, ops) = TACodeGenerator.generateExpression(expr, acc._2); (acc._1 :+ address, ops) }
    }
  }

  def generateStatement() {}
}
