package br.unb.cic.oberon.codegen

import br.unb.cic.oberon.ir.ast.{Constant => ASTConstant, _}
import br.unb.cic.oberon.ir.tac._
import br.unb.cic.oberon.tc.{TypeChecker}
import br.unb.cic.oberon.environment.Environment

object TACodeGenerator extends CodeGenerator[List[TAC]] {

  var env = new Environment[Type]()

  private val typeByteSize: Map[Type, Int] =
    Map(IntegerType -> 4, RealType -> 4)

  override def generateCode(module: OberonModule): List[TAC] = {
    load_vars(module.variables, module.constants)
    module.stmt match {
      case Some(stm) => generateStatement(stm, List())

      case None => List()
    }
  }

  def generateStatement(stmt: Statement, insts: List[TAC]): List[TAC] = {
    stmt match {
      case AssignmentStmt(designator, exp) =>
        val (t, insts1) = generateExpression(exp, insts)
        designator match {
          case VarAssignment(varName) =>
            val v = Name(varName, checkExpression(exp))
            insts1 :+ CopyOp(t, v, "")
          case ArrayAssignment(array, index) =>
            val (a, insts2) = generateExpression(array, insts1)
            val offset = getArrayOffset(a, index)
            insts2 :+ ArraySet(t, offset, a, "")
          case PointerAssignment(pointerName) =>
            val p = Name(pointerName, LocationType)
            insts1 :+ SetPointer(t, p, "")
          case RecordAssignment(record, field) =>
            val (name: Name, insts2: List[TAC]) =
              generateExpression(record, insts1)
            val offset = getRecordOffset(name, field)
            insts2 :+ RecordSet(t, offset, name, "")
        }
      case SequenceStmt(stmts) =>
        stmts.foldLeft(insts) { (acc, stm) =>
          generateStatement(stm, acc)
        }
      case ProcedureCallStmt(name, argsExps) => {
        val argsTAC = argsExps.map(exp => generateExpression(exp, List()))
        val TACops = argsTAC.flatMap { case (_, tac: List[TAC]) =>
          tac
        }
        val param = argsTAC.map(_._1).map {
          case t: Temporary => (List[TAC](), Param(t, ""))
          case name1: Name =>
            val t = new Temporary(name1.t)
            (List(MoveOp(name1, t, "")), Param(t, ""))

          case const: Constant =>
            val t = new Temporary(const.t)
            (List(MoveOp(const, t, "")), Param(t, ""))

        }
        val paramops = param.map(_._1)
        val paramops2 = paramops.flatten
        val param2 = param.map(_._2)

        insts ++ TACops ++ paramops2 ++ param2 :+ Call(
          name,
          argsExps.length,
          ""
        )

      }
      case IfElseStmt(condition, thenStmt, elseStmt) =>
        val l1 = LabelGenerator.generateLabel
        val l2 = if (elseStmt.isDefined) LabelGenerator.generateLabel else ""
        condition match {
          case EQExpression(left, right) =>
            val (l, insts1) = generateExpression(left, insts)
            val (r, insts2) = generateExpression(right, insts1)
            generateIfStatement(
              l1,
              l2,
              NeqJump(l, r, l1, ""),
              thenStmt,
              elseStmt,
              insts2
            )

          case NEQExpression(left, right) =>
            val (l, insts1) = generateExpression(left, insts)
            val (r, insts2) = generateExpression(right, insts1)
            generateIfStatement(
              l1,
              l2,
              EqJump(l, r, l1, ""),
              thenStmt,
              elseStmt,
              insts2
            )

          case GTExpression(left, right) =>
            val (l, insts1) = generateExpression(left, insts)
            val (r, insts2) = generateExpression(right, insts1)
            generateIfStatement(
              l1,
              l2,
              LTEJump(l, r, l1, ""),
              thenStmt,
              elseStmt,
              insts2
            )

          case LTExpression(left, right) =>
            val (l, insts1) = generateExpression(left, insts)
            val (r, insts2) = generateExpression(right, insts1)
            generateIfStatement(
              l1,
              l2,
              GTEJump(l, r, l1, ""),
              thenStmt,
              elseStmt,
              insts2
            )

          case GTEExpression(left, right) =>
            val (l, insts1) = generateExpression(left, insts)
            val (r, insts2) = generateExpression(right, insts1)
            generateIfStatement(
              l1,
              l2,
              LTJump(l, r, l1, ""),
              thenStmt,
              elseStmt,
              insts2
            )

          case LTEExpression(left, right) =>
            val (l, insts1) = generateExpression(left, insts)
            val (r, insts2) = generateExpression(right, insts1)
            generateIfStatement(
              l1,
              l2,
              GTJump(l, r, l1, ""),
              thenStmt,
              elseStmt,
              insts2
            )

          case NotExpression(exp) =>
            val (t, insts1) = generateExpression(exp, insts)
            generateIfStatement(
              l1,
              l2,
              JumpTrue(t, l1, ""),
              thenStmt,
              elseStmt,
              insts1
            )

          case _ =>
            val (t, insts1) = generateExpression(condition, insts)
            generateIfStatement(
              l1,
              l2,
              JumpFalse(t, l1, ""),
              thenStmt,
              elseStmt,
              insts1
            )
        }

      case WhileStmt(condition, stmt) =>
        val l1 = LabelGenerator.generateLabel
        val l2 = LabelGenerator.generateLabel
        val insts1 = insts :+ Jump(l1, "") :+ NOp(l2)
        val insts2 = generateStatement(stmt, insts1) :+ NOp(l1)

        condition match {
          case EQExpression(left, right) =>
            val (l, insts3) = generateExpression(left, insts2)
            val (r, insts4) = generateExpression(right, insts3)
            return insts4 :+ EqJump(l, r, l2, "")

          case NEQExpression(left, right) =>
            val (l, insts3) = generateExpression(left, insts2)
            val (r, insts4) = generateExpression(right, insts3)
            return insts4 :+ NeqJump(l, r, l2, "")

          case GTExpression(left, right) =>
            val (l, insts3) = generateExpression(left, insts2)
            val (r, insts4) = generateExpression(right, insts3)
            return insts4 :+ GTEJump(l, r, l2, "")

          case LTExpression(left, right) =>
            val (l, insts3) = generateExpression(left, insts2)
            val (r, insts4) = generateExpression(right, insts3)
            return insts4 :+ LTEJump(l, r, l2, "")

          case GTEExpression(left, right) =>
            val (l, insts3) = generateExpression(left, insts2)
            val (r, insts4) = generateExpression(right, insts3)
            return insts4 :+ GTEJump(l, r, l2, "")

          case LTEExpression(left, right) =>
            val (l, insts3) = generateExpression(left, insts2)
            val (r, insts4) = generateExpression(right, insts3)
            return insts4 :+ LTEJump(l, r, l2, "")

          case NotExpression(exp) =>
            val (t, insts3) = generateExpression(exp, insts2)
            return insts3 :+ JumpFalse(t, l2, "")

          case _ =>
            val (t, insts3) = generateExpression(condition, insts2)
            return insts3 :+ JumpTrue(t, l2, "")
        }

      case ReadLongRealStmt(varName) =>
        return insts :+ ReadLongReal(Name(varName, RealType), "")

      case ReadRealStmt(varName) =>
        return insts :+ ReadReal(Name(varName, RealType), "")

      case ReadLongIntStmt(varName) =>
        return insts :+ ReadLongInt(Name(varName, IntegerType), "")

      case ReadIntStmt(varName) =>
        return insts :+ ReadInt(Name(varName, IntegerType), "")

      case ReadShortIntStmt(varName) =>
        return insts :+ ReadShortInt(Name(varName, IntegerType), "")

      case ReadCharStmt(varName) =>
        return insts :+ ReadChar(Name(varName, StringType), "")

      case WriteStmt(expression) =>
        val (t, insts1) = generateExpression(expression, insts)
        return insts1 :+ Write(t, "")

      case ReturnStmt(expr) =>
        val (t, insts1) = generateExpression(expr, insts)
        return insts1 :+ Return(t, "")

      case ExitStmt() =>
        return insts :+ Exit("")
      case NewStmt(varName) =>
        val (variable: Address, insts1) =
          generateExpression(VarExpression(varName), insts)
        insts1 :+ New(variable, "")
      case MetaStmt(_) =>
        throw new Exception("MetaStmt não foi implementado")
    }
  }

  def generateExpression(
      expr: Expression,
      insts: List[TAC]
  ): (Address, List[TAC]) = {

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
        return (Name(name, checkExpression(expr)), insts)

      case AddExpression(left, right) =>
        val (t, l, r, insts2) =
          generateBinaryExpression(left, right, insts, checkExpression(expr))
        return (t, insts2 :+ AddOp(l, r, t, ""))

      case SubExpression(left, right) =>
        val (t, l, r, insts2) =
          generateBinaryExpression(left, right, insts, checkExpression(expr))
        return (t, insts2 :+ SubOp(l, r, t, ""))

      case MultExpression(left, right) =>
        val (t, l, r, insts2) =
          generateBinaryExpression(left, right, insts, checkExpression(expr))
        return (t, insts2 :+ MulOp(l, r, t, ""))

      case DivExpression(left, right) =>
        val (t, l, r, insts2) =
          generateBinaryExpression(left, right, insts, checkExpression(expr))

        return (t, insts2 :+ DivOp(l, r, t, ""))

      case AndExpression(left, right) =>
        val (t, l, r, insts2) =
          generateBinaryExpression(left, right, insts, checkExpression(expr))
        return (t, insts2 :+ AndOp(l, r, t, ""))

      case OrExpression(left, right) =>
        val (t, l, r, insts2) =
          generateBinaryExpression(left, right, insts, checkExpression(expr))
        return (t, insts2 :+ OrOp(l, r, t, ""))

      case ModExpression(left, right) =>
        val (t, l, r, insts2) =
          generateBinaryExpression(left, right, insts, checkExpression(expr))
        return (t, insts2 :+ RemOp(l, r, t, ""))

      case NotExpression(exp) =>
        val (a, insts1) = generateExpression(exp, insts)
        val t = new Temporary(BooleanType)
        return (t, insts1 :+ NotOp(a, t, ""))

      case EQExpression(left, right) =>
        val (temps, l, r, insts2) =
          generateComparisonExpression(expr, left, right, insts)
        val (t0, t1) = (temps(0), temps(1))
        return (
          t1,
          insts2 :+ SubOp(l, r, t0, "") :+ SLTUOp(
            t0,
            Constant("1", IntegerType),
            t1,
            ""
          )
        )

      case NEQExpression(left, right) =>
        val (temps, l, r, insts2) =
          generateComparisonExpression(expr, left, right, insts)
        val (t0, t1) = (temps(0), temps(1))
        return (
          t1,
          insts2 :+ SubOp(l, r, t0, "") :+ SLTUOp(
            Constant("0", IntegerType),
            t0,
            t1,
            ""
          )
        )

      case GTExpression(left, right) =>
        val (temps, l, r, insts2) =
          generateComparisonExpression(expr, left, right, insts)
        val t = temps(0)
        return (t, insts2 :+ SLTOp(r, l, t, ""))

      case LTExpression(left, right) =>
        val (temps, l, r, insts2) =
          generateComparisonExpression(expr, left, right, insts)
        val t = temps(0)
        return (t, insts2 :+ SLTOp(l, r, t, ""))

      case GTEExpression(left, right) =>
        val (temps, l, r, insts2) =
          generateComparisonExpression(expr, left, right, insts)
        val (t0, t1) = (temps(0), temps(1))
        return (t1, insts2 :+ SLTOp(l, r, t0, "") :+ NotOp(t0, t1, ""))

      case LTEExpression(left, right) =>
        val (temps, l, r, insts2) =
          generateComparisonExpression(expr, left, right, insts)
        val (t0, t1) = (temps(0), temps(1))
        return (t1, insts2 :+ SLTOp(r, l, t0, "") :+ NotOp(t0, t1, ""))

// No final não conseguimos implementar a geração de procedures.
//      case FunctionCallExpression(name, args) =>
//        val (args, argInsts) = argsExps.foldLeft((List[Address](),insts)) {
//          (acc, expr) =>
//            val (address, ops) = TACodeGenerator.generateExpression(expr, acc._2)
//            (acc._1 :+ address, ops)
//        }
//        val params = args.map(x => Param(x, ""))
//        return (funcs.get(name), argInsts ++ params :+ Call(name, args.length), "")

      case ArraySubscript(array, index) =>
        val (a, insts1) = generateExpression(array, insts)
        val (i, insts2) = generateExpression(index, insts1)
        val t = new Temporary(checkExpressionSafe(expr))
        return (t, insts2 :+ ArrayGet(a, i, t, ""))
      case PointerAccessExpression(name) =>
        val p = Name(name, LocationType)
        val t = new Temporary(checkExpressionSafe(expr))
        return (t, insts :+ GetValue(p, t, ""))

      case FieldAccessExpression(record, field) =>
        val (name: Name, insts1: List[TAC]) = generateExpression(record, insts)
        val offset = getRecordOffset(name, field)
        val fieldtype = getFieldType(name, field)
        val t = new Temporary(fieldtype)
        (t, insts1 :+ RecordGet(name, offset, t, ""))
    }
  }

  private def generateBinaryExpression(
      left: Expression,
      right: Expression,
      insts: List[TAC],
      exprType: Type
  ): (Address, Address, Address, List[TAC]) = {
    val (l, insts1) = generateExpression(left, insts)
    val (r, insts2) = generateExpression(right, insts1)
    val t = new Temporary(exprType)
    (t, l, r, insts2)
  }

  private def generateComparisonExpression(
      expr: Expression,
      left: Expression,
      right: Expression,
      insts: List[TAC]
  ): (List[Address], Address, Address, List[TAC]) = {
    val (l, insts1) = generateExpression(left, insts)
    val (r, insts2) = generateExpression(right, insts1)
    val temps = expr match {
      case GTExpression(_, _) | LTExpression(_, _) =>
        List(new Temporary(BooleanType))
      case other => List(new Temporary(BooleanType), new Temporary(BooleanType))
    }
    (temps, l, r, insts2)
  }

  private def generateIfStatement(
      l1: String,
      l2: String,
      condition: TAC,
      thenStmt: Statement,
      elseStmt: Option[Statement],
      insts: List[TAC]
  ): List[TAC] = {
    val ops =
      if (elseStmt.isDefined) List(Jump(l2, ""), NOp(l1)) else List(NOp(l1))
    val insts1 = generateStatement(thenStmt, insts :+ condition) ++ ops
    elseStmt match {
      case Some(stm) => generateStatement(stm, insts1) :+ NOp(l2)

      case None => insts1
    }
  }

  private def getArrayOffset(array: Address, index: Expression): Address = {
    val arrayType = array match {
      case Name(_, ArrayType(_, baseType)) => baseType
    }
    val index1 = index match {
      case IntValue(value) => value
    }

    val offset = typeByteSize.getOrElse(arrayType, 0) * index1

    Constant(offset.toString, IntegerType)

  }

  private def getRecordOffset(record: Name, field: String): Constant = {
    val variables: List[VariableDeclaration] =
      record.t.asInstanceOf[RecordType].variables
    val targetIndex: Int = variables.indexWhere(_.name == field)
    val variables2: List[VariableDeclaration] = variables.take(targetIndex + 1)

    val offset: Int = variables2.map {
      case VariableDeclaration(_, ArrayType(size, vartype)) =>
        size * typeByteSize.getOrElse(vartype, 0)
      case VariableDeclaration(_, vartype) => typeByteSize.getOrElse(vartype, 0)
    }.sum

    Constant(offset.toString, IntegerType)
  }

  private def getFieldType(record: Name, field: String): Type = {
    val variables: List[VariableDeclaration] =
      record.t.asInstanceOf[RecordType].variables
    variables.find(_.name == field).map(_.variableType) match {
      case Some(ty) => ty
      case None =>
        throw new IllegalArgumentException("All variables need a Type")
    }
  }

  private def checkExpression(exp: Expression) =
    TypeChecker.checkExpression(exp).run(env) match {
      case Right((s, t)) =>
        env.baseType(t) match {
          case Some(t) => t
          case None =>
            throw new Exception(
              s"TypeCheckerError: Error while trying to convert to base type."
            )
        }
      case Left(err) => throw new Exception(s"TypeCheckerError: ${err}")
    }

  private def checkExpressionSafe(exp: Expression) =
    TypeChecker.checkExpression(exp).run(env) match {
      case Right((s, t)) =>
        env.baseType(t) match {
          case Some(t) => t
          case None =>
            throw new Exception(
              s"TypeCheckerError: Error while trying to convert to base type."
            )
        }
      case Left(err) => NullType
    }

  def load_vars(
      vars: List[VariableDeclaration],
      consts: List[ASTConstant] = List()
  ): Unit = {
    TypeChecker
      .checkModule(
        OberonModule("test", Set(), List(), consts, vars, List(), List(), None)
      )
      .runS(env) match {
      case Right(s)  => { this.env = s }
      case Left(err) => throw new Exception(s"TypeCheckerError: ${err}")
    }
  }

  def load_userTypes_and_vars(
      userTypes: List[UserDefinedType],
      vars: List[VariableDeclaration],
      consts: List[ASTConstant] = List()
  ): Unit = {
    TypeChecker
      .checkModule(
        OberonModule(
          "test",
          Set(),
          userTypes,
          consts,
          vars,
          List(),
          List(),
          None
        )
      )
      .runS(env) match {
      case Right(s)  => { this.env = s }
      case Left(err) => throw new Exception(s"TypeCheckerError: ${err}")
    }
  }

  def reset(): Unit = {
    this.env = new Environment[Type]()
    Temporary.reset
    LabelGenerator.reset
  }
}

object LabelGenerator {
  var counter = 0

  def generateLabel(): String = {
    val label = ".L" + counter.toString + ":"
    counter += 1
    label
  }

  def reset(): Unit = {
    counter = 0
  }
}
