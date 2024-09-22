package br.unb.cic.oberon.codegen

import br.unb.cic.oberon.ir.ast._
import br.unb.cic.oberon.transformations.CoreChecker
import org.typelevel.paiges.Doc
import org.typelevel.paiges.Doc._
import scala.math._

import scala.collection.mutable.Queue

class NotOberonCoreException(s: String) extends Exception(s) {}

abstract class CCodeGenerator extends CodeGenerator[String] {}

case class PaigesBasedGenerator() extends CCodeGenerator {
  val indentSize: Int = 4
  val twoLines: Doc = line * 2
  var declVars: List[VariableDeclaration] = List()
  var userTypes: List[UserDefinedType] = List()
  var allocatedVars: Queue[String] = Queue()
  
  var lambda_index = 0
  var lambdaFunctions: List[Doc] = List()

  override def generateCode(module: OberonModule): String = {

    if (module.stmt.isDefined && !CoreChecker.checkModule(module))
      throw new NotOberonCoreException(
        "Não podemos compilar módulo que não seja OberonCore"
      )

    declVars = module.variables
    userTypes = module.userTypes
    
    val mainHeader =
      text("#include <math.h>") / text("#include <stdio.h>") / text("#include <stdbool.h>") / text("#include <stdlib.h>") + twoLines
    val procedureDocs = module.procedures.map(procedure =>
      generateProcedure(procedure, module.userTypes)
    )
    val mainDefines = generateConstants(module.constants)
    val userDefinedTypes = generateUserDefinedTypes(module.userTypes)
    val globalVars = declareVars(declVars, userTypes, 0)
    val mainBody = module.stmt match {
      case Some(stmt) =>
        text("int main() {") / generateStmt(
          stmt,
          indentSize
        ) + freeVars(indentSize) + Doc.char('}')
      case None => text("int main() {}")
    }
    //precisa estar após o processamento de mainBody para que lambdaFunctions receba os valores das lambdas
    val allProcedures = lambdaFunctions ++ procedureDocs
    val mainProcedures =
      if (allProcedures.nonEmpty)
        intercalate(twoLines, allProcedures) + twoLines
      else empty

    val CCode =
      mainHeader + userDefinedTypes / globalVars / mainDefines + mainProcedures / mainBody
    CCode.render(60)
  }

  def generateProcedure(
      procedure: Procedure,
      userTypes: List[UserDefinedType]
  ): Doc = {
    val returnType = procedure.returnType match {
      case Some(varType) => getCType(varType, userTypes)
      case None          => "void"
    }

    val args = procedure.args.map {
      case ParameterByValue(name, argumentType) =>
        text(convertVariable(name, argumentType, userTypes, isArgument = true))
      case ParameterByReference(name, argumentType) =>
        text(convertVariable(name, argumentType, userTypes, isArgument = true))
    }

    val procedureDeclarations =
      declareVars(procedure.variables, List(), indentSize)
    val procedureArgs = intercalate(Doc.char(',') + space, args)
    val procedureName = text(s"$returnType ${procedure.name}(")
    val procedureHeader =
      procedureArgs.tightBracketBy(procedureName, Doc.char(')'))
    val procedureBody = generateStmt(procedure.stmt)


    procedureHeader + space + Doc.char(
      '{'
    ) / procedureDeclarations + procedureBody +
      Doc.char('}')
  }

  def declareVars(
      variables: List[VariableDeclaration],
      userTypes: List[UserDefinedType],
      localIndent: Int
  ): Doc = {

    var basicVariablesDoc = empty
    for (varType <- List(IntegerType, RealType, BooleanType, StringType)) {
      val variablesOfType = variables
        .filter(_.variableType == varType)
        .map(variable => variable.name)
      if (variablesOfType.nonEmpty) {
        val CVarType = getCType(varType, userTypes)
        val varNames = variablesOfType.mkString(", ")
        val newDeclarationLine = textln(localIndent, s"$CVarType $varNames;")
        basicVariablesDoc = basicVariablesDoc + newDeclarationLine
      }
    }

    var userVariablesDoc = empty

    for (variable <- variables) {
      variable.variableType match {
        case ReferenceToUserDefinedType(userTypeName) =>
          userVariablesDoc += textln(
            localIndent,
            s"$userTypeName ${variable.name};"
          )

        case ArrayType(length, innerType) =>
          val variableType: String = getCType(innerType, userTypes)
          userVariablesDoc += textln(
            localIndent,
            s"$variableType ${variable.name}[$length];"
          )

        case PointerType(t) =>
          val pointerVariableType: String = getPointerType(t, userTypes)
          userVariablesDoc += textln(
            localIndent,
            s"$pointerVariableType ${variable.name};"
          )

        case _ => ()
      }
    }

    basicVariablesDoc + userVariablesDoc
  }

  def generateUserDefinedTypes(userTypes: List[UserDefinedType]): Doc = {

    var generatedDoc: Doc = empty

    for (userType <- userTypes) {
      generatedDoc += (userType.baseType match {
        case ArrayType(length, innerType) =>
          val variableType: String = innerType match {
            case ReferenceToUserDefinedType(name) => name
            case _ => getCType(innerType, userTypes)
          }

          textln(s"typedef $variableType ${userType.name}[$length];")
        case RecordType(variables) =>
          val structName = userType.name
          text(s"struct ${structName}_struct {") /
            declareVars(variables, userTypes, indentSize) +
            textln("};") +
            textln(s"typedef struct ${structName}_struct $structName;")
        case _ => throw new Exception("Non-exhaustive match in case statement.")
      })
    }
    generatedDoc
  }

  def stringToType(
      typeAsString: String,
      userTypes: List[UserDefinedType]
  ): Type = {
    for (userType <- userTypes) {
      if (userType.name == typeAsString) {
        return userType.baseType
      }
    }
    throw new Exception(s"Type not defined: $typeAsString")
  }

  def getCType(variableType: Type, userTypes: List[UserDefinedType]): String = {
    variableType match {
      case IntegerType    => "int"
      case BooleanType    => "bool"
      case CharacterType  => "char"
      case RealType       => "float"
      case StringType     => "char*"

      case ReferenceToUserDefinedType(name) =>
        val userType = stringToType(name, userTypes)
        userType match {
          case RecordType(_) => s"$name"
          case _ =>
            throw new Exception("Non-exhaustive match in case statement.")
        }
      case _ => throw new Exception("Non-exhaustive match in case statement.")
    }
  }

  def getPointerType(pointerType: Type, userTypes: List[UserDefinedType]): String = {
    pointerType match {
      case IntegerType   => "int*"
      case BooleanType   => "bool*"
      case CharacterType => "char*"
      case RealType      => "float*"
      case StringType    => "char**"

      case ReferenceToUserDefinedType(name) =>
        val userType = stringToType(name, userTypes)
        userType match {
          case RecordType(_) => s"${name}*"
          case ArrayType(_, _)  => s"${name}*" 
          case _ =>
              throw new Exception("Non-exaustive match in case statement.")
        }

      case _ => throw new Exception("Non-supported pointer type.")
    }
  }

  def convertVariable(
      name: String,
      varType: Type,
      userTypes: List[UserDefinedType],
      isArgument: Boolean = false
  ): String = {
    varType match {
      case ArrayType(length, arrayVarType) =>
        val innerCType = getCType(arrayVarType, userTypes)
        if (isArgument) {
          s"$innerCType $name[]"
        } else {
          s"$innerCType $name[$length]"
        }

      case _ =>
        val CType = getCType(varType, userTypes)
        s"$CType $name"
    }
  }

  def generateConstants(constants: List[Constant]): Doc = {
    val constantsDeclaration = constants.map { constant =>
      text(s"#define ${constant.name} ${genExp(constant.exp)}")
    }
    if (constantsDeclaration.nonEmpty)
      intercalate(line, constantsDeclaration) + twoLines
    else
      empty
  }

  def generateStmt(statement: Statement, indent: Int = indentSize): Doc = {

    statement match {
      case SequenceStmt(stmts) =>
        val multipleStmts = stmts.map(stmt => generateStmt(stmt, indent))
        intercalate(empty, multipleStmts)
      case ReadIntStmt(varName) =>
        textln(indent, s"""scanf("%d", &$varName);""")
      case WriteStmt(expression) =>
        textln(indent, s"""printf("%d\\n", ${genExp(expression)});""")
      case NewStmt(varName) =>
        val varType = declVars.find(_.name == varName)

        varType match {
          case Some(variableDeclaration) => 
            val pointerType = variableDeclaration.variableType match {
              case PointerType(t) =>
                getCType(t, userTypes)
              case _ => 
                throw new Exception("Non-exaustive match.")
            }

            // enqueue variables to be free
            allocatedVars.enqueue(variableDeclaration.name)
            
            textln(indent, s"""$varName = (${pointerType}*)malloc(sizeof(${pointerType}));""")
          case None => 
            throw new Exception("Variable declaration not found.")
        }
      case ProcedureCallStmt(name, args) =>
        genProcedureCallStmt(name, args, indent)
      case IfElseStmt(condition, thenStmt, elseStmt) =>
        val ifCond =
          textln(indent, s"if (${genExp(condition)}) {") +
            generateStmt(thenStmt, indent + indentSize) +
            indentation(indent) + text("}")
        val elseCond = elseStmt match {
          case Some(stmt) =>
            textln(" else {") + generateStmt(
              stmt,
              indent + indentSize
            ) + textln(indent, "}")
          case None => line
        }
        ifCond + elseCond
      case WhileStmt(condition, stmt) =>
        textln(indent, s"while (${genExp(condition)}) {") +
          generateStmt(stmt, indent + indentSize) +
          textln(indent, "}")
      case ReturnStmt(exp) =>
        textln(indent, s"return ${genExp(exp)};")
      case ForStmt(init, condition, stmt) =>
        textln(indent, s"for ($init; ${genExp(condition)}; $stmt) {") +
          generateStmt(stmt, indent + indentSize) +
          textln(indent, "}")

      case AssignmentStmt(designator, exp) =>
        designator match {
          case VarAssignment(varName) =>
            textln(indent, s"$varName = ${genExp(exp)};")
          case ArrayAssignment(array, elem) =>
            val varName = genExp(array)
            val index = genExp(elem)
            val value = genExp(exp)
            textln(indent, s"$varName[$index] = $value;")
          case RecordAssignment(record, atrib) =>
            val structName = genExp(record)
            val value = genExp(exp)
            textln(indent, s"$structName.$atrib = $value;")
          case PointerAssignment(pointerName) =>             
            textln(indent, s"*$pointerName = ${genExp(exp)};")
          case _ =>
            throw new Exception("Non-exhaustive match in case statement.")
        }

      case ExitStmt() =>
        textln(indent, "break;")

      case _ => empty
    }
  }

  def genExp(exp: Expression): String = {
    exp match {
      case IntValue(v)    => v.toString
      // round up to 2 decimal digits
      // case RealValue(v)   => BigDecimal(v).setScale(1, BigDecimal.RoundingMode.HALF_UP).toString
      case RealValue(v)   => v.toString
      case Brackets(exp)  => s"( ${genExp(exp)} )"
      case BoolValue(v)   => if (v) "true" else "false"
      case StringValue(v) => s""""${v}""""
      case Undef()        => "undefined"
      case VarExpression("N") => "n <= N"
      case VarExpression(name) => name
      case FunctionCallExpression(name, args) =>
        name match {
          case "ODD" =>
            s"${genExp(args.head)} % 2 == 1"
          case "ABS" =>
            s"abs(${genExp(args.head)})"
          case "CEIL" => 
            s"ceil(${genExp(args.head)})"
          case "RND" =>
            s"round(${genExp(args.head)})"
          case "FLT" =>
            s"(float)${genExp(args.head)}"
          case "POW" =>
            s"pow(${genExp(args.head)}, ${genExp(args(1))})"
          case "SQR" =>
            s"sqrt(${genExp(args.head)})"
          // stringtoint
          // stringtoreal
          case _ =>
            val expressions = args.map(arg => text(genExp(arg)))
            val functionArgs = intercalate(Doc.char(',') + space, expressions)
            functionArgs
              .tightBracketBy(
                text(name + '('),
                Doc.char(')')
              )
              .render(10000)
        }
      case LambdaExpression(args, exp) =>
            
            val functionName = s"lambda_${lambda_index}"
            lambda_index += 1

            val inferredReturnType = args.map {
                case ParameterByValue(_, argumentType) => argumentType
                case ParameterByReference(_, argumentType) => argumentType
              } match {
                case argTypes if argTypes.contains(RealType) => RealType
                case argTypes if argTypes.forall(_ == IntegerType) => IntegerType
                case argTypes if argTypes.contains(BooleanType) => BooleanType
                case _ => IntegerType // ou outro tipo padrão se necessário
              }

            //considera que nao será utilizado constants e variables nas lambdas,somente argumentos
            val procedure = Procedure(
              name = functionName,
              args = args,
              returnType = Some(inferredReturnType),
              constants = List(),             
              variables = List(),             
              stmt = ReturnStmt(exp),         
            )

            val procedureDoc = generateProcedure(procedure, List())
            
            lambdaFunctions = lambdaFunctions :+ procedureDoc

            val functionArgs = args.map(arg => genExp(VarExpression(arg.name))).mkString(", ")
            //s"$functionName($functionArgs)"
            s"$functionName"

      case EQExpression(left, right)   => s"${genExp(left)} == ${genExp(right)}"
      case NEQExpression(left, right)  => s"${genExp(left)} != ${genExp(right)}"
      case GTExpression(left, right)   => s"${genExp(left)} > ${genExp(right)}"
      case LTExpression(left, right)   => s"${genExp(left)} < ${genExp(right)}"
      case GTEExpression(left, right)  => s"${genExp(left)} >= ${genExp(right)}"
      case LTEExpression(left, right)  => s"${genExp(left)} <= ${genExp(right)}"
      
      case AddExpression(left, right) =>
        val currentPrecedence = 3
        val leftExp = genExpWithParens(left, currentPrecedence)
        val rightExp = genExpWithParens(right, currentPrecedence)
        s"$leftExp + $rightExp"
        
      case SubExpression(left, right) =>
        val currentPrecedence = 3
        val leftExp = genExpWithParens(left, currentPrecedence)
        val rightExp = genExpWithParens(right, currentPrecedence)
        s"$leftExp - $rightExp"
        
      case MultExpression(left, right) =>
        val currentPrecedence = 2
        val leftExp = genExpWithParens(left, currentPrecedence)
        val rightExp = genExpWithParens(right, currentPrecedence)
        s"$leftExp * $rightExp"
        
      case DivExpression(left, right) =>
        val currentPrecedence = 2
        val leftExp = genExpWithParens(left, currentPrecedence)
        val rightExp = genExpWithParens(right, currentPrecedence)
        s"$leftExp / $rightExp"
        
      case ModExpression(left, right) =>
        val currentPrecedence = 2
        val leftExp = genExpWithParens(left, currentPrecedence)
        val rightExp = genExpWithParens(right, currentPrecedence)
        s"$leftExp % $rightExp"
      
      case AndExpression(left, right) =>
        val currentPrecedence = 4
        val leftExp = genExpWithParens(left, currentPrecedence)
        val rightExp = genExpWithParens(right, currentPrecedence)
        s"$leftExp && $rightExp"

      case OrExpression(left, right) =>
        val currentPrecedence = 5
        val leftExp = genExpWithParens(left, currentPrecedence)
        val rightExp = genExpWithParens(right, currentPrecedence)
        s"$leftExp || $rightExp"

      case NotExpression(exp) => 
        val currentPrecedence = 1
        s"!${genExpWithParens(exp, currentPrecedence)}"

      case FieldAccessExpression(exp, name) => s"${genExp(exp)}.$name"
      case ArraySubscript(arrayBase, index) =>
        val arrayName = genExp(arrayBase)
        val arrayIndex = genExp(index)
        s"$arrayName[$arrayIndex]"
      case PointerAccessExpression(name) => s"*$name"
      case _ => throw new Exception("expression not found")
    }
  }

  // https://en.cppreference.com/w/c/language/operator_precedence
  def precedence(exp: Expression): Int = exp match {
    // lower number means higher precedence
    case NotExpression(_)                                                 => 1
    case MultExpression(_, _) | DivExpression(_, _) | ModExpression(_, _) => 2
    case AddExpression(_, _) | SubExpression(_, _)                        => 3
    case AndExpression(_, _)                                              => 4
    case OrExpression(_, _)                                               => 5

    case _                                                                => 0
  }

  def genExpWithParens(exp: Expression, parentPrecedence: Int): String = {
    val expPrecedence = precedence(exp)
    val expStr = genExp(exp)
    
    if (expPrecedence > parentPrecedence) s"($expStr)" else expStr
  }

  def genProcedureCallStmt(
      name: String,
      args: List[Expression],
      indent: Int
  ): Doc = {
    name match {
      case "INC" =>
        genInc(args, "+", indent)
      case "DEC" =>
        genInc(args, "-", indent)
      case _ =>
        val expressions = args.map(arg => text(genExp(arg)))
        val functionArgs = intercalate(Doc.char(',') + space, expressions)
        functionArgs.tightBracketBy(
          indentation(indent) + text(name + '('),
          text(");")
        ) + line
    }
  }

  def freeVars(indentSize: Int): Doc = {
    
    if(!allocatedVars.isEmpty){
      var freeStmts = Doc.text("")
      
      while(!allocatedVars.isEmpty) {
        var freeVarStmt = allocatedVars.dequeue() 
        freeStmts = freeStmts + textln(indentSize, s"free($freeVarStmt);")
      }

      textln("") + freeStmts
    } else {
      Doc.text("")
    }
  }

  def genInc(args: List[Expression], signal: String, indent: Int): Doc = {
    if (args.length == 1) {
      indentation(indent) + textln(
        s"${genExp(args.head)} = ${genExp(args.head)} ${signal} 1;"
      )
    } else {
      indentation(indent) + textln(
        s"${genExp(args.head)} = ${genExp(args.head)} ${signal} ${genExp(args(1))};"
      )
    }
  }

  def textln(str: String): Doc = text(str) + line

  def textln(indentSize: Int, str: String): Doc =
    indentation(indentSize) + text(str) + line

  def indentation(size: Int = indentSize): Doc =
    intercalate(empty, List.fill(size)(space))

}

class PPrintBasedGenerator extends CCodeGenerator {
  override def generateCode(module: OberonModule): String = ???
}
