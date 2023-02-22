package br.unb.cic.oberon.codegen

import br.unb.cic.oberon.ir.ast.{Constant => ASTConstant, _}
import br.unb.cic.oberon.ir.tac._
import br.unb.cic.oberon.tc.{ExpressionTypeVisitor, TypeChecker}

object TACodeGenerator extends CodeGenerator[List[TAC]] {
  
  private var tc = new TypeChecker()
  private var expVisitor = new ExpressionTypeVisitor(tc)

  override def generateCode(module: OberonModule): List[TAC] = {
    List()
  }

  def generateProcedure() {}

  def generateBody() {}

  def generateStatement(stmt: Statement, insts: List[TAC]): List[TAC] = {
    stmt match {
      case AssignmentStmt(designator, exp) =>
        val (t, insts1) = generateExpression(exp, insts)
        designator match {
          case VarAssignment(varName) =>
            val v = Name(varName, exp.accept(expVisitor).get)
            return insts1 :+ CopyOp(t, v, "") 

          case ArrayAssignment(array, index) =>
            val (a, insts2) = generateExpression(array, insts1)
            val (i, insts3) = generateExpression(index, insts2)
            return insts3 :+ ListSet(t, i, a, "")

          case PointerAssignment(pointerName) =>
            val p = Name(pointerName, LocationType)
            return insts1 :+ SetPointer(t, p, "")

          case RecordAssignment(_,_) =>
            throw new Exception("Records não foram implementados!") 
        }

      case SequenceStmt(stmts) =>
        stmts.foldLeft(insts) {
          (acc, stm) => generateStatement(stm, acc)
        }

      case ProcedureCallStmt(name, argsExps) =>
        val (args, argInsts) = argsExps.foldLeft((List[Address](),insts)) {
          (acc, expr) => 
            val (address, ops) = TACodeGenerator.generateExpression(expr, acc._2)
            (acc._1 :+ address, ops)
        }
        val params = args.map(x => Param(x, ""))
        return argInsts ++ params :+ Call(name, args.length, "")

      case IfElseStmt(condition, thenStmt, elseStmt) =>
        val l1 = LabelGenerator.generateLabel
        val l2 = LabelGenerator.generateLabel
        condition match {
          case EQExpression(left, right) =>
            val (l, insts1) = generateExpression(left, insts)
            val (r, insts2) = generateExpression(right, insts1)
            List()

          case NEQExpression(left, right) =>
            List()

          case GTExpression(left, right) =>
            List()

          case LTExpression(left, right) =>
            List()

          case GTEExpression(left, right) =>
            List()

          case LTEExpression(left, right) =>
            List()

          case NotExpression(exp) =>
            List()

          case _ =>
            List()
        }
    }
  }

  def generateExpression(expr: Expression, insts: List[TAC]): (Address, List[TAC]) = {
    expr match {

      case Brackets(exp) =>
        return generateExpression(exp, insts)

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

      case VarExpression(name) =>
        return (Name(name, expr.accept(expVisitor).get), insts)

      case AddExpression(left, right) =>
        val (t, l, r, insts2) = generateBinaryExpression(left, right, insts, expr.accept(expVisitor).get)
        return (t, insts2 :+ AddOp(l, r, t, ""))

      case SubExpression(left, right) =>
        val (t, l, r, insts2) = generateBinaryExpression(left, right, insts, expr.accept(expVisitor).get)
        return (t, insts2 :+ SubOp(l, r, t, ""))

      case MultExpression(left, right) =>
        val (t, l, r, insts2) = generateBinaryExpression(left, right, insts, expr.accept(expVisitor).get)
        return (t, insts2 :+ MulOp(l, r, t, ""))

      case DivExpression(left, right) =>
        val (t, l, r, insts2) = generateBinaryExpression(left, right, insts, expr.accept(expVisitor).get)
        return (t, insts2 :+ DivOp(l, r, t, ""))
        
      case AndExpression(left, right) =>
        val (t, l, r, insts2) = generateBinaryExpression(left, right, insts, BooleanType)
        return (t, insts2 :+ AndOp(l, r, t, ""))

      case OrExpression(left, right) =>
        val (t, l, r, insts2) = generateBinaryExpression(left, right, insts, BooleanType)
        return (t, insts2 :+ OrOp(l, r, t, ""))

      case ModExpression(left, right) =>
        val (t, l, r, insts2) = generateBinaryExpression(left, right, insts, IntegerType)
        return (t, insts2 :+ RemOp(l, r, t, ""))

      case NotExpression(exp) =>
        val (a, insts1) = generateExpression(exp, insts)
        val t = new Temporary(BooleanType)
        return (t, insts1 :+ NotOp(a, t, ""))

      case EQExpression(left, right) =>
        val (temps, l, r, insts2) = generateComparisonExpression(expr, left, right, insts)
        val (t0, t1) = (temps(0), temps(1))
        return (t1, insts2 :+ SubOp(l, r, t0, "") :+ SLTUOp(t0, Constant("1", IntegerType), t1, ""))

      case NEQExpression(left, right) =>
        val (temps, l, r, insts2) = generateComparisonExpression(expr, left, right, insts)
        val (t0, t1) = (temps(0), temps(1))
        return (t1, insts2 :+ SubOp(l, r, t0, "") :+ SLTUOp(Constant("0", IntegerType), t0, t1, ""))

      case GTExpression(left, right) =>
        val (temps, l, r, insts2) = generateComparisonExpression(expr, left, right, insts)
        val t = temps(0)
        return (t, insts2 :+ SLTOp(r, l, t, ""))

      case LTExpression(left, right) =>
        val (temps, l, r, insts2) = generateComparisonExpression(expr, left, right, insts)
        val t = temps(0)
        return (t, insts2 :+ SLTOp(l, r, t, ""))

      case GTEExpression(left, right) =>
        val (temps, l, r, insts2) = generateComparisonExpression(expr, left, right, insts)
        val (t0, t1) = (temps(0), temps(1))
        return (t1, insts2 :+ SLTOp(l, r, t0, "") :+ NotOp(t0, t1, ""))

      case LTEExpression(left, right) =>
        val (temps, l, r, insts2) = generateComparisonExpression(expr, left, right, insts)
        val (t0, t1) = (temps(0), temps(1))
        return (t1, insts2 :+ SLTOp(r, l, t0, "") :+ NotOp(t0, t1, ""))

//TODO generateProcedure e gerar o Map funcs
//      case FunctionCallExpression(name, args) =>
//        val (args, argInsts) = exp.args.foldLeft((List[Address](),insts)) {
//          (acc, expr) => 
//            val (address, ops) = TACodeGenerator.generateExpression(expr, acc._2)
//            (acc._1 :+ address, ops)
//        }
//        val params = args.map(x => Param(x, ""))
//        //talvez mudar o funcs.get(name) para stack
//        return (funcs.get(name), argInsts ++ params :+ Call(name, args.length))

      case ArraySubscript(array, index) =>
        val (a, insts1) = generateExpression(array, insts)
        val (i, insts2) = generateExpression(index, insts1)
        val t = new Temporary(expr.accept(expVisitor).get)
        return (t, insts2 :+ ListGet(a, i, t, ""))

      case PointerAccessExpression(name) =>
        val p = Name(name, LocationType)
        val t = new Temporary(expr.accept(expVisitor).get)
        return (t, insts :+ GetValue(p, t, ""))

      case FieldAccessExpression(exp, name) =>
        throw new Exception("FieldAccessExpression não foi implementada!")
    }
  }

  private def generateBinaryExpression(left: Expression, right: Expression, insts: List[TAC], exprType: Type): (Address, Address, Address, List[TAC]) = {
    val (l, insts1) = generateExpression(left, insts)
    val (r, insts2) = generateExpression(right, insts1)
    val t = new Temporary(exprType)
    (t, l, r, insts2)
  }

  private def generateComparisonExpression(expr: Expression, left: Expression, right: Expression, insts: List[TAC]): (List[Address], Address, Address, List[TAC]) = {
    val (l, insts1) = generateExpression(left, insts)
    val (r, insts2) = generateExpression(right, insts1)
    val temps = expr match {
      case GTExpression(_,_) | LTExpression(_,_) => List(new Temporary(BooleanType))
      case other => List(new Temporary(BooleanType), new Temporary(BooleanType))
      }
    (temps, l, r, insts2)
  }

  private def generateIfStatement(condition: Expression, thenStmt: Statement, elseStmt: Option[Statement], insts: List[TAC]): List[TAC] = {
    List()
  } 


  //somente para testes
  def load_vars(vars: List[VariableDeclaration], consts: List[ASTConstant] = List()): Unit = {
    OberonModule("test", Set(), List(), consts, vars, List(), None).accept(tc)
  }

  //somente para testes
  def reset(): Unit = {
    tc = new TypeChecker()
    expVisitor = new ExpressionTypeVisitor(tc)
    Temporary.reset
  }
}

object LabelGenerator {
  var counter = 0

  def generateLabel(): String = {
    val label = "L" + counter.toString
    counter += 1
    label
  }

  def reset(): Unit = {
    counter = 0
  }
}
