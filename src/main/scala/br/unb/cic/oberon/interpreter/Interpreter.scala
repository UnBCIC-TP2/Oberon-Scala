package br.unb.cic.oberon.interpreter

import java.io.{ByteArrayOutputStream, OutputStream, PrintStream}

import scala.reflect.runtime.{universe => ru}
import scala.reflect.runtime.universe._

import br.unb.cic.oberon.ast._
import br.unb.cic.oberon.environment.Environment
import br.unb.cic.oberon.util.Values
import br.unb.cic.oberon.visitor.OberonVisitorAdapter
import br.unb.cic.oberon.external.External


import scala.io.StdIn

/**
 * The interpreter visitor first updates the
 * environment with the global constants, variables,
 * and procedures; after that, it visits the
 * main program statement.
 *
 * It uses an additional visitor (EvalExpressionVisitor) to
 * compute the value of the different expressions.
 *
 * We assume the program is well-typed, otherwise,
 * a runtime exception might be thrown.
 */
class Interpreter extends OberonVisitorAdapter {
  type T = Unit

  var exit = false
  val env = new Environment[Expression]()
  var printStream : PrintStream = new PrintStream(System.out)

  override def visit(module: OberonModule): Unit = {
    // set up the global declarations
    module.constants.foreach(c => c.accept(this))
    module.variables.foreach(v => v.accept(this))
    module.procedures.foreach(p => p.accept(this))
    module.userTypes.foreach(userType => userType.accept(this))

    // execute the statement, if it is defined. remember,
    // module.stmt is an Option[Statement].
    if (module.stmt.isDefined) {
      module.stmt.get.accept(this)
    }
  }

  override def visit(constant: br.unb.cic.oberon.ast.Constant): Unit = {
    env.setGlobalVariable(constant.name, constant.exp)
  }

  override def visit(variable: VariableDeclaration): Unit = {
    env.setGlobalVariable(variable.name, Undef())
  }

  override def visit(userType: UserDefinedType): Unit = {
    env.addUserDefinedType(userType)
  }

  override def visit(procedure: Procedure): Unit = {
    env.declareProcedure(procedure)
  }

  // 	assert(stmts(1) == EAssignmentStmt(ArrayAssignment(VarExpression("array"), IntValue(0)), VarExpression("x")))


  override def visit(stmt: Statement): Unit = {
    // we first check if we achieved a return stmt.
    // if so, we should not execute any other statement
    // of a sequence of stmts. Whenever we achieve a
    // return stmt, we associate in the local variables
    // the name "return" to the return value.
    if (exit || env.lookup(Values.ReturnKeyWord).isDefined) {
      return
    }
    // otherwise, we pattern-match on the current stmt.
    stmt match {
      case EAssignmentStmt(indexDesignator, exp) => {
        indexDesignator match {
          case ArrayAssignment(arrayExpression, indexExpression) => {
            env.reassignArray(arrayExpression.asInstanceOf[VarExpression].name, evalExpression(indexExpression).asInstanceOf[IntValue].value, evalExpression(exp))
          }
          case RecordAssignment(record, atrib) => ???
          case _ => ???
        }
      }
      case AssignmentStmt(name, exp) => env.setVariable(name, evalExpression(exp))
      case SequenceStmt(stmts) => stmts.foreach(s => s.accept(this))
      case ReadIntStmt(name) => env.setVariable(name, IntValue(StdIn.readLine().toInt))
      case WriteStmt(exp) => printStream.println(evalExpression(exp))
      case IfElseStmt(condition, thenStmt, elseStmt) => if (evalCondition(condition)) thenStmt.accept(this) else if (elseStmt.isDefined) elseStmt.get.accept(this)
      case IfElseIfStmt(condition, thenStmt, listOfElseIf, elseStmt) => checkIfElseIfStmt(condition, thenStmt, listOfElseIf, elseStmt)
      case WhileStmt(condition, whileStmt) => while (evalCondition(condition)) whileStmt.accept(this)
      case RepeatUntilStmt(condition, repeatUntilStmt) => do (repeatUntilStmt.accept(this)) while (!evalCondition(condition))
      case ForStmt(init, condition, block) => init.accept(this); while (evalCondition(condition)) block.accept(this)
      case LoopStmt(stmt) => while(!exit) { stmt.accept(this) }; exit = false
      case ExitStmt() => exit = true
      case CaseStmt(exp, cases, elseStmt) => checkCaseStmt(exp, cases, elseStmt)
      case ReturnStmt(exp: Expression) => setReturnExpression(evalExpression(exp))
      case ProcedureCallStmt(name, args) =>
        // we evaluate the "args" in the current
        // environment.

        print("HERE")
        print(args)
        val actualArguments = args.map(a => evalExpression(a))



        env.push() // after that, we can "push", to indicate a procedure call.

        visitProcedureCall(name, actualArguments) // then we execute the procedure.
        env.pop() // and we pop, to indicate that a procedure finished its execution.
    }
  }

  private def checkIfElseIfStmt(condition: Expression, thenStmt: Statement, listOfElseIf: List[ElseIfStmt], elseStmt: Option[Statement]): Unit = {
    var matched = false
    var i = 0

    if (evalCondition(condition)) thenStmt.accept(this)


    else {

      while (i < listOfElseIf.size && !matched) {
        listOfElseIf(i) match {
          case ElseIfStmt(condition, stmt) => if (evalCondition(condition)) {
            stmt.accept(this)
            matched = true
          }
        }
        i += 1
      }

      if (!matched && elseStmt.isDefined) elseStmt.get.accept(this)
    }
  }

  private def checkCaseStmt(exp: Expression, cases: List[CaseAlternative], elseStmt: Option[Statement]): Unit = {
    var v = evalExpression(exp)
    var matched = false
    var i = 0
    while (i < cases.size && !matched) {
      cases(i) match {
        case RangeCase(min, max, stmt) =>
          if ((evalCaseAlt(v) >= evalCaseAlt(min)) && (evalCaseAlt(v) <= evalCaseAlt(max))) {
            stmt.accept(this)
            matched = true
          }
        case SimpleCase(condition, stmt) =>
          if (v == evalExpression(condition)) {
            stmt.accept(this)
            matched = true
          }
      }
      i += 1
    }
    if (!matched && elseStmt.isDefined) {
      elseStmt.get.accept(this)
    }
  }

  /*
   * process the ReturnStmt(exp) statement.
   * In this case, we just create a new entry
   * in the local variables, assigning
   * "return" -> exp.
   */
  private def setReturnExpression(exp: Expression): Unit =
    env.setLocalVariable(Values.ReturnKeyWord, exp)

  def visitProcedureCall(name: String, args: List[Expression]): Unit = {
    val procedure = env.findProcedure(name)

    // println("HEREEEEEEEE")
    // print(procedure)

    if(procedure.isInstanceOf[ProcedureDeclaration]) {
      updateEnvironmentWithProcedureCall(procedure.asInstanceOf[ProcedureDeclaration], args)
      procedure.asInstanceOf[ProcedureDeclaration].stmt.accept(this)
    } else {
      
      updateEnvironmentWithProcedureCall(procedure.asInstanceOf[ExternalProcedureDeclaration], args)
    }
  }

  def updateEnvironmentWithProcedureCall(procedure: Procedure, args: List[Expression]): Unit = {
    procedure match {
      case ProcedureDeclaration(_, _, _, _, _, _) => {
        val proc = procedure.asInstanceOf[ProcedureDeclaration]
        proc.args.map(formal => formal.name)
          .zip(args)
          .foreach(pair => env.setLocalVariable(pair._1, pair._2))

        proc.constants.foreach(c => env.setLocalVariable(c.name, c.exp))
        proc.variables.foreach(v => env.setLocalVariable(v.name, Undef()))
      }
      case ExternalProcedureDeclaration(_, _, _) => {
        //println("HEREEEEEE")

        val proc = procedure.asInstanceOf[ExternalProcedureDeclaration]
        proc.args.map(formal => formal.name)
          .zip(args)
          .foreach(pair => env.setLocalVariable(pair._1, pair._2))
        
     }
    }
  }

  def evalCondition(expression: Expression): Boolean = {
    val evalVisitor = new EvalExpressionVisitor(this)
    expression.accept(evalVisitor).asInstanceOf[BoolValue].value
  }

  def evalExpression(expression: Expression): Expression = {
    val evalVisitor = new EvalExpressionVisitor(this)
    expression.accept(evalVisitor)
  }

  def evalCaseAlt(expression: Expression): Integer = {
    val evalVisitor = new EvalExpressionVisitor(interpreter = this)
    expression.accept(evalVisitor).asInstanceOf[IntValue].value
  }

  /*
   * This method is mostly useful for testing purpose.
   * That is, here we are considering testability as a
   * design concern.
   */
  def setGlobalVariable(name: String, exp: Expression): Unit = {
    env.setGlobalVariable(name, exp)
  }

  /*
   * the same here.
   */
  def setLocalVariable(name: String, exp: Expression): Unit = {
    env.setLocalVariable(name, exp)
  }

  def setTestEnvironment() = {
    printStream = new PrintStream(new NullPrintStream())
  }
}

class EvalExpressionVisitor(val interpreter: Interpreter) extends OberonVisitorAdapter {
  type T = Expression

  override def visit(exp: Expression): Expression = exp match {
    case Brackets(expression) => expression.accept(this)
    case IntValue(v) => IntValue(v)
    case BoolValue(v) => BoolValue(v)
    case Undef() => Undef()
    case VarExpression(name) => interpreter.env.lookup(name).get
    case EQExpression(left, right) => binExpression(left, right, (v1: Value[Int], v2: Value[Int]) => BoolValue(v1.value == v2.value))
    case NEQExpression(left, right) => binExpression(left, right, (v1: Value[Int], v2: Value[Int]) => BoolValue(v1.value != v2.value))
    case GTExpression(left, right) => binExpression(left, right, (v1: Value[Int], v2: Value[Int]) => BoolValue(v1.value > v2.value))
    case LTExpression(left, right) => binExpression(left, right, (v1: Value[Int], v2: Value[Int]) => BoolValue(v1.value < v2.value))
    case GTEExpression(left, right) => binExpression(left, right, (v1: Value[Int], v2: Value[Int]) => BoolValue(v1.value >= v2.value))
    case LTEExpression(left, right) => binExpression(left, right, (v1: Value[Int], v2: Value[Int]) => BoolValue(v1.value <= v2.value))
    case AddExpression(left, right) => binExpression(left, right, (v1: Value[Int], v2: Value[Int]) => IntValue(v1.value + v2.value))
    case SubExpression(left, right) => binExpression(left, right, (v1: Value[Int], v2: Value[Int]) => IntValue(v1.value - v2.value))
    case MultExpression(left, right) => binExpression(left, right, (v1: Value[Int], v2: Value[Int]) => IntValue(v1.value * v2.value))
    case DivExpression(left, right) => binExpression(left, right, (v1: Value[Int], v2: Value[Int]) => IntValue(v1.value / v2.value))
    case AndExpression(left, right) => binExpression(left, right, (v1: Value[Boolean], v2: Value[Boolean]) => BoolValue(v1.value && v2.value))
    case OrExpression(left, right) => binExpression(left, right, (v1: Value[Boolean], v2: Value[Boolean]) => BoolValue(v1.value || v2.value))
    case FunctionCallExpression(name, args) => {
      val actualArguments = args.map(a => a.accept(this))

      interpreter.env.push()

      val exp = visitFunctionCall(name, actualArguments)
      interpreter.env.pop()
      exp
    } 

  }

  def getTypeTag[T: ru.TypeTag](obj: T) = ru.typeTag[T]

  def visitFunctionCall(name: String, args: List[Expression]): Expression = {

    interpreter.visitProcedureCall(name, args)
    val returnValue = interpreter.env.lookup(Values.ReturnKeyWord)

    //assert(returnValue.isDefined) // a function call must set a local variable with the "return" expression

    if(returnValue.isDefined) {
      return returnValue.get
    } else {
      // TODO: tratar erros e generalizar para todas as funções
      val procedure = interpreter.env.findProcedure(name).asInstanceOf[ExternalProcedureDeclaration]

      val external = new External()

      external.run.div(3, 5) // para carregar a lib

      val rm = ru.runtimeMirror(getClass.getClassLoader)
      val instanceMirror = rm.reflect(external._lib)
      val tipo = getTypeTag(external._lib).tpe
      val methodSymbol = tipo.member(TermName(name)).asMethod // a definição do método 
      val method = instanceMirror.reflectMethod(methodSymbol) // é o método em C (já pode ser chamado)
      val Retorno = procedure.returnType.get // o tipo de retorno daquele procedimento


      val parametros = methodSymbol.typeSignature.toString().split(":")

      assert(parametros.length-1 == args.length) 

      var ans: Int = -1
      if(args.length == 1) 
        ans = method(args(0).accept(this).asInstanceOf[Value[Int]].value).asInstanceOf[Int]
      else if(args.length == 2)
        ans = method(args(0).accept(this).asInstanceOf[Value[Int]].value, args(1).accept(this).asInstanceOf[Value[Int]].value).asInstanceOf[Int]
      else 
        ans = method().asInstanceOf[Int]

      return IntValue(ans)
    }
  }

  /**
   * Eval a binary expression.
   *
   * @param left  the left expression
   * @param right the right expression
   * @param fn    a function that constructs an expression. Here we
   *              are using again a high-order function. We assign to
   *              the "result" visitor attribute the value we compute
   *              after applying this function.
   * @tparam T a type parameter to set the function fn correctly.
   */
  def binExpression[T](left: Expression, right: Expression, fn: (Value[T], Value[T]) => Expression): Expression = {
    val v1 = left.accept(this).asInstanceOf[Value[T]]
    val v2 = right.accept(this).asInstanceOf[Value[T]]
    fn(v1, v2)
  }

}

class NullPrintStream extends PrintStream(new NullByteArrayOutputStream) { }

class NullByteArrayOutputStream extends ByteArrayOutputStream {
  override def writeTo(o: OutputStream) {}
}