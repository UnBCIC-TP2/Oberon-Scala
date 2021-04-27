package br.unb.cic.oberon.parser

import org.antlr.v4.runtime._
import br.unb.cic.oberon.ast._
import br.unb.cic.oberon.parser.OberonParser.StatementContext
import org.antlr.stringtemplate.language.FormalArgument

import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters._

/**
 * A parser for the Oberon language using
 * the ANTLR infrastructure. The main idea of
 * this parser is to parse a Oberon source code
 * and convert it into our Oberon AST representation
 * in Scala. Here we use the ANTLR idiom for parsing
 * source code.
 *
 * @author rbonifacio
 */
object ScalaParser {
  def parse(input: String): OberonModule = {
    val charStream = new ANTLRInputStream(input)
    val lexer = new OberonLexer(charStream)
    val tokens = new CommonTokenStream(lexer)
    val parser = new OberonParser(tokens)

    val visitor = new ParserVisitor
    visitor.visitCompilationUnit(parser.compilationUnit())
    visitor.module
  }
}

class ParserVisitor {

  var module: OberonModule = _

  def visitCompilationUnit(ctx: OberonParser.CompilationUnitContext): Unit = {
    val name = ctx.name
    val constants = ctx.declarations().constant().asScala.toList.map(c => visitConstant(c))
    val variables = ctx.declarations().varDeclaration().asScala.toList.map(v => visitVariableDeclaration(v)).flatten
    val procedures = ctx.declarations().procedure().asScala.toList.map(p => visitProcedureDeclaration(p))
    val userTypes = ctx.declarations().userTypeDeclaration().asScala.toList.map(t => visitUserDefinedType(t))
    val block = visitModuleBlock(ctx.block())

    module = OberonModule(name.getText, userTypes, constants, variables, procedures, block)
  }

  /**
   * Visit a block declaration. If the block context is null,
   * we return None. Else, we return a statement with the
   * block statement.
   *
   * @param ctx block statement of an Oberon module
   * @return an abstract representation of a statement
   */
  private def visitModuleBlock(ctx: OberonParser.BlockContext) =
    if (ctx != null) {
      val stmtVisitor = new StatementVisitor()
      ctx.accept(stmtVisitor)
      Some(stmtVisitor.stmt)
    }
    else {
      None
    }

  /**
   * Visit a constant declaration.
   *
   * @param ctx constant declaration visited
   * @return a constant declaration representation
   */
  def visitConstant(ctx: OberonParser.ConstantContext): Constant = {
    val variable = ctx.constName.getText
    val v = new ExpressionVisitor()
    ctx.accept(v)
    Constant(variable, v.exp)
  }

  /**
   * Visit a variable declaration
   *
   * @param ctx variable declaration context
   * @return a variable declaration representation
   */
  def visitVariableDeclaration(ctx: OberonParser.VarDeclarationContext): List[VariableDeclaration] = {
    val variableType = visitOberonType(ctx.varType)
    ctx.vars.asScala.toList.map(v => VariableDeclaration(v.getText, variableType))
  }

  def visitProcedureDeclaration(ctx: OberonParser.ProcedureContext): Procedure = {
    val name = ctx.name.getText
    val args = ctx.formals().formalArg().asScala.toList.map(formal => visitFormalArg(formal)).flatten
    val constants = ctx.declarations().constant().asScala.toList.map(c => visitConstant(c))
    val variables = ctx.declarations().varDeclaration().asScala.toList.map(v => visitVariableDeclaration(v)).flatten
    val block = visitModuleBlock(ctx.block())

    val returnType = if (ctx.procedureType != null) Some(visitOberonType(ctx.procedureType)) else None

    Procedure(name, args, returnType, constants, variables, block.get)
  }

  def visitFormalArg(ctx: OberonParser.FormalArgContext): List[FormalArg] = {
    val visitor = new FormalArgVisitor()
    ctx.accept(visitor)
    visitor.formalArg
  }


  def visitUserDefinedType(ctx: OberonParser.UserTypeDeclarationContext): UserDefinedType = {
    val userTypeVisitor = new UserDefinedTypeVisitor()
    ctx.accept(userTypeVisitor)
    userTypeVisitor.uType
  }

  /**
   * Visit an oberon type.
   *
   * Note: this implementation uses pattern matching
   * as an expression.
   *
   * @param ctx the oberon type context
   * @return a oberon type representation.
   */
  def visitOberonType(ctx: OberonParser.OberonTypeContext): Type = {
    val typeVisitor = new OberonTypeVisitor()
    ctx.accept(typeVisitor)

    if (typeVisitor.baseType == null) UndefinedType else typeVisitor.baseType
  }

  class OberonTypeVisitor extends OberonBaseVisitor[Unit] {
    var baseType: Type = _

    override def visitIntegerType(ctx: OberonParser.IntegerTypeContext): Unit =
      baseType = IntegerType

    override def visitBooleanType(ctx: OberonParser.BooleanTypeContext): Unit = {
      baseType = BooleanType
    }

    override def visitReferenceType(ctx: OberonParser.ReferenceTypeContext): Unit = {
      val nameType = ctx.name.getText
      baseType = ReferenceToUserDefinedType(nameType)
    }
  }

  class FormalArgVisitor() extends OberonBaseVisitor[Unit] {
    var formalArg: List[FormalArg] = _

    override def visitValueArguments(ctx: OberonParser.ValueArgumentsContext): Unit = {
      val argType = visitOberonType(ctx.argType)
      formalArg = ctx.args.asScala.toList.map(arg => ValueArguments(arg.getText, argType))
    }

    override def visitReferenceArguments(ctx: OberonParser.ReferenceArgumentsContext): Unit = {
      val argType = visitOberonType(ctx.argType)
      formalArg = ctx.args.asScala.toList.map(arg => ReferenceArguments(arg.getText, argType))
    }
  }

  /* A visitor for parsing expressions */
  class ExpressionVisitor() extends OberonBaseVisitor[Unit] {
    var exp: Expression = _

    override def visitIntValue(ctx: OberonParser.IntValueContext): Unit =
      exp = IntValue(ctx.getText.toInt)

    override def visitBoolValue(ctx: OberonParser.BoolValueContext): Unit =
      exp = BoolValue(ctx.getText == "True")

    override def visitFieldAccess(ctx: OberonParser.FieldAccessContext): Unit = {
      val visitor = new ExpressionVisitor()

      ctx.expression().accept(visitor)
      val parentExpression = visitor.exp

      val expressionName = ctx.name.getText

      exp = FieldAccessExpression(parentExpression, expressionName)
    }

    override def visitArraySubscript(ctx: OberonParser.ArraySubscriptContext): Unit = {
      ctx.arrayBase.accept(this)
      val arrayBase = exp

      ctx.index.accept(this)
      val index = exp

      exp = ArraySubscript(arrayBase, index)
    }

    override def visitRelExpression(ctx: OberonParser.RelExpressionContext): Unit =
      visitBinExpression(ctx.left, ctx.right, expression(ctx.opr.getText))

    override def visitAddExpression(ctx: OberonParser.AddExpressionContext): Unit =
      visitBinExpression(ctx.left, ctx.right, expression(ctx.opr.getText))

    override def visitMultExpression(ctx: OberonParser.MultExpressionContext): Unit =
      visitBinExpression(ctx.left, ctx.right, expression(ctx.opr.getText))

    override def visitVariable(ctx: OberonParser.VariableContext): Unit =
      exp = VarExpression(ctx.getText)

    override def visitBrackets(ctx: OberonParser.BracketsContext): Unit = {
      ctx.expression().accept(this)
    }

    override def visitFunctionCall(ctx: OberonParser.FunctionCallContext): Unit = {
      val name = ctx.name.getText
      val args = new ListBuffer[Expression]

      ctx.arguments().expression().forEach(e => {
        e.accept(this)
        args += exp
      })
      exp = FunctionCallExpression(name, args.toList)
    }

    private def expression(opr: String): (Expression, Expression) => Expression =
      opr match {
        case "=" => EQExpression
        case "#" => NEQExpression
        case ">" => GTExpression
        case "<" => LTExpression
        case ">=" => GTEExpression
        case "<=" => LTEExpression
        case "+" => AddExpression
        case "-" => SubExpression
        case "*" => MultExpression
        case "/" => DivExpression
        case "&&" => AndExpression
        case "||" => OrExpression
      }

    /*
     * The "ugly", though necessary code for visiting binary expressions.
     */
    private def visitBinExpression(left: OberonParser.ExpressionContext, right: OberonParser.ExpressionContext, constructor: (Expression, Expression) => Expression) {
      left.accept(this) // first visit the left hand side of an expression.
      val lhs = exp // assign the result to the value lhs
      right.accept(this) // second, visit the right hand side of an expression
      val rhs = exp // assign the result to the value rhs
      exp = constructor(lhs, rhs) // assign the result to exp, using the 'constructor' to set the actual expression
    }
  }

  /* a visitor for parsing statements */
  class StatementVisitor extends OberonBaseVisitor[Unit] {
    var stmt: Statement = _

    override def visitAssignmentStmt(ctx: OberonParser.AssignmentStmtContext): Unit = {
      val varName = ctx.`var`.getText
      val visitor = new ExpressionVisitor()
      ctx.exp.accept(visitor)
      stmt = AssignmentStmt(varName, visitor.exp)
    }

    override def visitEAssignmentStmt(ctx: OberonParser.EAssignmentStmtContext): Unit = {
      val visitor = new ExpressionVisitor()
      ctx.exp.accept(visitor)

      val EAssignmentVisitor = new AssignmentAlternativeVisitor()

      ctx.designator.accept(EAssignmentVisitor)
      val designator = EAssignmentVisitor.assignmentAlt

      stmt = EAssignmentStmt(designator, visitor.exp)
    }

    override def visitSequenceStmt(ctx: OberonParser.SequenceStmtContext): Unit = {
      val stmts = new ListBuffer[Statement]

      ctx.statement().asScala.toList.foreach(s => {
        s.accept(this)
        stmts += stmt
      })
      stmt = SequenceStmt(flatSequenceOfStatements(stmts.toList))
    }

    def flatSequenceOfStatements(stmts: List[Statement]): List[Statement] =
      stmts match {
        case SequenceStmt(ss) :: rest => flatSequenceOfStatements(ss) ++ flatSequenceOfStatements(rest)
        case s :: rest => s :: flatSequenceOfStatements(rest)
        case Nil => List()
      }

    override def visitReadIntStmt(ctx: OberonParser.ReadIntStmtContext): Unit = {
      val varName = ctx.`var`.getText
      stmt = ReadIntStmt(varName)
    }

    override def visitWriteStmt(ctx: OberonParser.WriteStmtContext): Unit = {
      val visitor = new ExpressionVisitor()
      ctx.expression().accept(visitor)
      stmt = WriteStmt(visitor.exp)
    }

    override def visitProcedureCall(ctx: OberonParser.ProcedureCallContext): Unit = {
      val name = ctx.name.getText
      val args = new ListBuffer[Expression]
      val visitor = new ExpressionVisitor()

      ctx.arguments().expression().asScala.map(e => {
        e.accept(visitor)
        args += visitor.exp
      })
      stmt = ProcedureCallStmt(name, args.toList)
    }

    override def visitIfElseStmt(ctx: OberonParser.IfElseStmtContext): Unit = {
      val visitor = new ExpressionVisitor()

      ctx.expression().accept(visitor)
      val condition = visitor.exp

      ctx.thenStmt.accept(this)
      val thenStmt = stmt

      val elseStmt = if (ctx.elseStmt != null) {
        ctx.elseStmt.accept(this)
        Some(stmt)
      } else None

      stmt = IfElseStmt(condition, thenStmt, elseStmt)
    }

    override def visitIfElseIfStmt(ctx: OberonParser.IfElseIfStmtContext): Unit = {
      val visitor = new ExpressionVisitor()

      ctx.expression().accept(visitor)
      val condition = visitor.exp
      ctx.thenStmt.accept(this)
      val thenStmt = stmt

      val elsifStmt = new ListBuffer[ElseIfStmt]
      ctx.elsifs.asScala.toList.foreach(e => {
        e.expression().accept(visitor)
        val elsifCondition = visitor.exp
        e.stmt.accept(this)
        val elsifThenStmt = stmt
        elsifStmt += ElseIfStmt(elsifCondition, elsifThenStmt)
      })

      val elseStmt = if (ctx.elseStmt != null) {
        ctx.elseStmt.accept(this)
        Some(stmt)
      } else None

      stmt = IfElseIfStmt(condition, thenStmt, elsifStmt.toList, elseStmt)
    }

    override def visitCaseStmt(ctx: OberonParser.CaseStmtContext): Unit = {
      val expressionVisitor = new ExpressionVisitor()

      ctx.expression().accept(expressionVisitor)
      val caseExp = expressionVisitor.exp

      val caseVisitor = new CaseAlternativeVisitor()

      val cases = new ListBuffer[CaseAlternative]
      ctx.cases.asScala.toList.foreach(e => {
        e.accept(caseVisitor)
        cases += caseVisitor.caseAlt
      })

      val elseStmt = if (ctx.elseStmt != null) {
        ctx.elseStmt.accept(this)
        Some(stmt)
      } else None

      stmt = CaseStmt(caseExp, cases.toList, elseStmt)
    }

    override def visitWhileStmt(ctx: OberonParser.WhileStmtContext): Unit = {
      val visitor = new ExpressionVisitor()

      ctx.expression().accept(visitor)
      val condition = visitor.exp

      ctx.stmt.accept(this)
      val whileStmt = stmt

      stmt = WhileStmt(condition, whileStmt)
    }

    override def visitRepeatUntilStmt(ctx: OberonParser.RepeatUntilStmtContext): Unit = {
      val visitor = new ExpressionVisitor()

      ctx.expression().accept(visitor)
      val condition = visitor.exp

      ctx.stmt.accept(this)
      val repeatUntilStmt = stmt

      stmt = RepeatUntilStmt(condition, repeatUntilStmt)
    }

    override def visitForStmt(ctx: OberonParser.ForStmtContext): Unit = {
      val visitor = new ExpressionVisitor()

      ctx.init.accept(this)
      val init = stmt

      ctx.expression().accept(visitor)
      val condition = visitor.exp

      ctx.stmt.accept(this)
      val block = stmt

      stmt = ForStmt(init, condition, block)
    }

    override def visitForRangeStmt(ctx: OberonParser.ForRangeStmtContext): Unit = {
      val visitor = new ExpressionVisitor()

      val varName = ctx.`var`.getText
      val variable = VarExpression(varName)

      ctx.min.accept(visitor)
      val rangeMin = visitor.exp

      ctx.max.accept(visitor)
      val rangeMax = visitor.exp

      ctx.stmt.accept(this)
      val block = stmt

      // Instantiating the values for the basic ForStmt

      // var := rangeMin
      val init = AssignmentStmt(variable.name, rangeMin)

      // var <= rangeMax
      val condition = LTEExpression(variable, rangeMax)

      // var := var + 1
      val accumulator = AssignmentStmt(variable.name, AddExpression(variable, IntValue(1)))

      // stmt; var := var + 1
      val realBlock = SequenceStmt(List(block, accumulator))

      stmt = ForStmt(init, condition, realBlock)
    }

    override def visitReturnStmt(ctx: OberonParser.ReturnStmtContext): Unit = {
      val visitor = new ExpressionVisitor()
      ctx.exp.accept(visitor)
      stmt = ReturnStmt(visitor.exp)
    }
  }

  class CaseAlternativeVisitor extends OberonBaseVisitor[Unit] {
    var caseAlt: CaseAlternative = _

    override def visitSimpleCase(ctx: OberonParser.SimpleCaseContext): Unit = {
      val expVisitor = new ExpressionVisitor()

      ctx.expression().accept(expVisitor)
      val condition = expVisitor.exp

      val stmtVisitor = new StatementVisitor()

      ctx.stmt.accept(stmtVisitor)
      val stmt = stmtVisitor.stmt

      caseAlt = SimpleCase(condition, stmt)
    }

    override def visitRangeCase(ctx: OberonParser.RangeCaseContext): Unit = {
      var expressionVisitor = new ExpressionVisitor()
      ctx.min.accept(expressionVisitor)
      var min = expressionVisitor.exp

      ctx.max.accept(expressionVisitor)
      var max = expressionVisitor.exp

      var statementVisitor = new StatementVisitor()
      ctx.stmt.accept(statementVisitor)
      var stmt = statementVisitor.stmt

      caseAlt = RangeCase(min, max, stmt)
    }
  }

  class AssignmentAlternativeVisitor extends OberonBaseVisitor[Unit] {
    var assignmentAlt: AssignmentAlternative = _

    override def visitVarAssignment(ctx: OberonParser.VarAssignmentContext): Unit = {
      val varName = ctx.`var`.getText
      assignmentAlt = VarAssignment(varName)
    }

    override def visitArrayAssignment(ctx: OberonParser.ArrayAssignmentContext): Unit = {
      val expressionVisitor = new ExpressionVisitor()
      ctx.array.accept(expressionVisitor)
      val array = expressionVisitor.exp

      ctx.elem.accept(expressionVisitor)
      val elem = expressionVisitor.exp


      assignmentAlt = ArrayAssignment(array, elem)
    }

    override def visitRecordAssignment(ctx: OberonParser.RecordAssignmentContext): Unit = {
      var expressionVisitor = new ExpressionVisitor()
      ctx.record.accept(expressionVisitor)
      val record = expressionVisitor.exp
      val atrib = ctx.name.getText

      assignmentAlt = RecordAssignment(record, atrib)
    }
  }

  class UserDefinedTypeVisitor extends OberonBaseVisitor[Unit] {
    var uType: UserDefinedType = _

    override def visitRecordTypeDeclaration(ctx: OberonParser.RecordTypeDeclarationContext): Unit = {
      val variablesList = new ListBuffer[VariableDeclaration]
      val name = ctx.nameType.getText

      ctx.vars.asScala.toList.foreach(variable => {
        val varList = visitVariableDeclaration(variable)

        varList.foreach(v => {
          variablesList += v
        })
      })

      uType = RecordType(name, variablesList.toList)
    }

    override def visitArrayTypeDeclaration(ctx: OberonParser.ArrayTypeDeclarationContext): Unit = {
      val typeVisitor = new ParserVisitor()

      val name = ctx.nameType.getText
      val length = ctx.length.getText.toInt
      val baseType = typeVisitor.visitOberonType(ctx.vartype)

      uType = ArrayType(name, length, baseType)
    }
  }
}
