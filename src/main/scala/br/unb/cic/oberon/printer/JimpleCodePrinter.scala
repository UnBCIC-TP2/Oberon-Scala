package br.unb.cic.oberon.printer

import br.unb.cic.oberon.codegen.JimpleCodeGenerator

import br.unb.cic.oberon.ir.ast.{
  AddExpression => OberonAddExpression,
  AndExpression => OberonAndExpression,
  BoolValue => OberonBoolValue,
  CharValue => OberonCharValue,
  DivExpression => OberonDivExpression,
  IntValue => OberonIntValue,
  ModExpression => OberonModExpression,
  MultExpression => OberonMultExpression,
  NotExpression => OberonNotExpression,
  NullValue => OberonNullValue,
  OrExpression => OberonOrExpression,
  RealValue => OberonRealValue,
  StringValue => OberonStringValue,
  SubExpression => OberonSubExpression,
  _
}
import br.unb.cic.oberon.ir.jimple._
import br.unb.cic.oberon.tc.{
  ExpressionTypeChecker,
  TypeChecker
  }
import br.unb.cic.oberon.transformations.CoreChecker

import scala.collection.mutable.ListBuffer
import scala.util.matching.Regex

import org.typelevel.paiges.Doc
import org.typelevel.paiges.Doc._

object JimpleCodePrinter {
  private val indentSize: Doc = Doc.spaces(4)
  private val doubleIndentSize: Doc = Doc.spaces(8)
  private val twoLines: Doc = line * 2

  private def generateDoc(module: OberonModule): String = {
    val fields = JimpleCodeGenerator.generateFields(module)
    val methodSignatures = JimpleCodeGenerator.generateMethodSignatures(module)
    val methodsString = JimpleCodeGenerator.generateMethods(module, fields, methodSignatures).toString()
    
    var ConditionPrintClinit: Boolean = false

    val moduleName: Doc = Doc.text(module.name)
    val MainHeader = text("Public Class ") + moduleName + text(" extends java.lang.Object") / Doc.char('{')

    // Main initialization
    val HasVoid = FindInString("TVoid,main,", methodsString)

    val MainTitle =
      if (HasVoid) {
          indentSize + text("public void <init>()") / indentSize + Doc.char('{') / doubleIndentSize +
          moduleName + text(" r0;") + twoLines + doubleIndentSize + text("r0 := @this: ") + moduleName + text(";") +
          twoLines + doubleIndentSize + text("specialinvoke r0.<java.lang.Object: void <init>()>();") +
          twoLines + doubleIndentSize + text("return;") + indentSize
      } else {
          text("")
      }

    // Instantiate variables and consts
    var VariablesAndConstants: Doc = Doc.text("")
    var variablesAndConstant: Doc = Doc.text("")
    for (elemento <- fields) {
      val name : Doc = text(elemento.name)
      val tipo : Doc = text(generateTypeString(elemento.fieldType))

      if ((elemento.modifiers).toString() == "List(PublicModifer, StaticModifier, FinalModifier)") {
          variablesAndConstant = text("Public static final ") + tipo + space + name
          if (ConditionPrintClinit == (false)) {
          ConditionPrintClinit = true
          }
      } else {
          variablesAndConstant = text("Public ") + tipo + space + name
      }

      VariablesAndConstants = VariablesAndConstants + indentSize + variablesAndConstant + text(";") + Doc.line
    }

    // Go through methods and find list of expressions
    val ExpressionsListTuple = findExpressions(methodsString)

    // Method instantiation
    var MainMethods: Doc = Doc.text("")

    if (ConditionPrintClinit == true) {
    MainMethods = MainMethods + twoLines + indentSize + text("public static void <clinit>()") / indentSize + Doc.char('{')

    for(tuple <- ExpressionsListTuple) {
      var ExpressionDoc = defineStaticExpressionToDoc(tuple)
      if (ExpressionDoc == Doc.text("")) {

      } else {
      MainMethods = MainMethods / doubleIndentSize + text("<") + moduleName + text(": ") + ExpressionDoc + Doc.line
      }
    }

    MainMethods = MainMethods + Doc.line + doubleIndentSize + text("return;") / indentSize + Doc.char('}')
    }

    val jimpleCode = MainHeader / VariablesAndConstants / MainTitle / indentSize + Doc.char('}') + MainMethods / Doc.char('}')
    jimpleCode.render(1000)
  }

  //Funções auxiliares para o Pretty Print

  def defineStaticExpressionToDoc(element: (Any, Any)) : Doc ={
    var envio:Doc = Doc.text("")

    val expressionImm: Regex = """\(AssignStmtInt\((\w+)\),ImmediateExpressionInt\((\d+)\)""".r
    val expressionPlu: Regex = """\(AssignStmtInt\((\w+)\),PlusExpression\((\d+),(\d+)\)\)""".r
    val expressionBoo: Regex = """\(AssignStmtBoolean\((\w+)\),BooleanExpression\((\w+)\)\)""".r
    val expressionMul: Regex = """\(AssignStmtInt\((\w+)\),MultExpression\((\d+),(\d+)\)\)""".r

    val secondElement = element._2.toString

    if (secondElement.contains("ImmediateExpressionInt")) {
      // Extrair os valores usando expressões regulares
      val extractedValues: Option[(String, Int)] = expressionImm.findFirstMatchIn(element.toString()).map { matchResult =>
        (matchResult.group(1), matchResult.group(2).toInt)
      }
      // Imprimir os valores extraídos ou tratar caso não tenham sido encontrados
      extractedValues match {
        case Some((variable, intValue)) =>
          envio = envio + Doc.text(s"int $variable> = $intValue;")
        case None => Doc.text("")
      }
    } else if(secondElement.contains("PlusExpression")) {
      // Extrair os valores usando expressões regulares
      val extractedValues: Option[(String, Int, Int)] = expressionPlu.findFirstMatchIn(element.toString()).map { matchResult =>
        (matchResult.group(1), matchResult.group(2).toInt, matchResult.group(3).toInt)
      }
      // Imprimir os valores extraídos ou tratar caso não tenham sido encontrados
      extractedValues match {
        case Some((variable, intValue, intValue2)) =>
          val soma: Int = intValue + intValue2
          envio = envio + Doc.text(s"int $variable> = $soma;")
        case None => Doc.text("")
      }
    } else if(secondElement.contains("BooleanExpression")){

      // Extrair os valores usando expressões regulares
      val extractedValues: Option[(String, String)] = expressionBoo.findFirstMatchIn(element.toString()).map { matchResult =>
        (matchResult.group(1), matchResult.group(2))
      }
      // Imprimir os valores extraídos ou tratar caso não tenham sido encontrados
      extractedValues match {
        case Some((variable, boolean)) =>
          if (boolean == ("true")) {
            envio = envio + Doc.text(s"boolean $variable> = 1;")
          }else if(boolean == ("false")){
            envio = envio + Doc.text(s"boolean $variable> = 0;")
          }
        case None => Doc.text("")
      }
    } else if(secondElement.contains("MultExpression")) {
      // Extrair os valores usando expressões regulares
      val extractedValues: Option[(String, Int, Int)] = expressionMul.findFirstMatchIn(element.toString()).map { matchResult =>
        (matchResult.group(1), matchResult.group(2).toInt, matchResult.group(3).toInt)
      }
      // Imprimir os valores extraídos ou tratar caso não tenham sido encontrados
      extractedValues match {
        case Some((variable, intValue, intValue2)) =>
          val mult: Int = intValue * intValue2
          envio = envio + Doc.text(s"int $variable> = $mult;")
        case None => Doc.text("")
      }
    }

    envio
  }

  // Procura se há uma determinada string dentro de outra string
  def FindInString(queroAchar: String, stringCompleta: String): Boolean = {
    val regex = queroAchar.r
    val result = regex.findFirstIn(stringCompleta).isDefined

    result
  }

  // Gera o tipo utilizado pelo Pretty Print, recebendo como input um Tipo Jimple
  def generateTypeString(value: JimpleType): String = value match{
    case TInteger => "int"
    case TBoolean => "boolean"
    case TFloat => "float"
    case TCharacter => "char"
    case TString => "string"
    case TNull => "null"
    case TVoid => "void"
    case TUnknown => "unknown"
    // TODO: Implement array type matching
    // case TArray[JimpleType] => s"${generateTypeString(JimpleType)}[]"
    case other => extractTypeString(other.toString)
  }

  // Caso o tipo seja diferente dos demais, usa essa função para retornar ele mesmo
  def extractTypeString(str: String): String = {
    val pattern: Regex = "TObject\\((.*)\\)".r
    str match {
      case pattern(content) => content
      case _ => str
    }
  }

  // Pega uma lista de qualquer tipo e transforma em uma lista de Tuplas
  def listToTuples(list: List[Any]): List[(Any,Any)] = {
    list.grouped(2).collect {
      case List(x, y) => (x, y)
    }.toList
  }

  // Procura as expressões solicitadas nos métodos jimple
  def findExpressions(text: String): List[(Any,Any)] = {

    val inputString = text

    // Definindo case classes para representar as correspondências encontradas
    case class ImmediateExpressionInt(value: Any)
    case class AssignStmtInt(fieldName: String)
    case class PlusExpression(value1: Any, value2: Any)
    case class AssignStmtBoolean(fieldName: String)
    case class BooleanExpression(fieldName: String)
    case class MultExpression(value1: Any, value2: Any)

    // Definindo os padrões de regex para as correspondências
    val expressionImmediateInt: Regex = """ImmediateExpression\(ImmediateValue\(IntValue\((\d+)\)""".r
    val expressionAssignInt: Regex = """AssignStmt\(StaticField\(FieldSignature\([^,]+,TInteger,(\w+)\)""".r
    val expressionPlus: Regex = """PlusExpression\(ImmediateValue\(IntValue\((\d+)\)\),ImmediateValue\(IntValue\((\d+)\)""".r
    val expressionAssignBoolean: Regex = """AssignStmt\(StaticField\(FieldSignature\([^,]+,TBoolean,(\w+)\)""".r
    val expressionBoolean: Regex = """ImmediateExpression\(ImmediateValue\(BooleanValue\((\w+)\)""".r
    val expressionMult: Regex = """MultExpression\(ImmediateValue\(IntValue\((\d+)\)\),ImmediateValue\(IntValue\((\d+)\)""".r

    // Função para converter o valor correspondente para o caso apropriado
    def parseMatchedValue(matchedValue: String): Any = matchedValue.toInt

    // Função para mapear o padrão de regex para o caso apropriado
    def mapMatchedValue(expr: String, pos: Int): (Any, Int) = expr match {
      case expressionImmediateInt(value) => (ImmediateExpressionInt(parseMatchedValue(value)), pos)
      case expressionAssignInt(fieldName) => (AssignStmtInt(fieldName), pos)
      case expressionPlus(value1, value2) => (PlusExpression(parseMatchedValue(value1), parseMatchedValue(value2)), pos)
      case expressionAssignBoolean(fieldName) => (AssignStmtBoolean(fieldName),pos)
      case expressionBoolean(fieldName) => (BooleanExpression(fieldName),pos)
      case expressionMult(value1, value2) => (MultExpression(parseMatchedValue(value1), parseMatchedValue(value2)), pos)
    }

    // Encontrando todas as correspondências na string e armazenando-as em uma lista junto com suas posições
    val allExpressionsWithPos: List[(Any, Int)] = (
      expressionImmediateInt.findAllMatchIn(inputString).map(m => mapMatchedValue(m.matched, m.start)) ++
        expressionAssignInt.findAllMatchIn(inputString).map(m => mapMatchedValue(m.matched, m.start)) ++
        expressionPlus.findAllMatchIn(inputString).map(m => mapMatchedValue(m.matched, m.start)) ++
        expressionAssignBoolean.findAllMatchIn(inputString).map(m => mapMatchedValue(m.matched, m.start)) ++
        expressionBoolean.findAllMatchIn(inputString).map(m => mapMatchedValue(m.matched, m.start)) ++
        expressionMult.findAllMatchIn(inputString).map(m => mapMatchedValue(m.matched, m.start))).toList

    // Classificando a lista com base nas posições das correspondências na string original
    val sortedExpressions = allExpressionsWithPos.sortBy(_._2)

    var tempList: List[Any] = List.empty

    // Imprimindo as correspondências na ordem em que aparecem na string original
    sortedExpressions.foreach { case (expr, _) =>
      tempList = tempList :+ (expr)
    }

    val unrankedList: List[(Any, Any)] = listToTuples(tempList).reverse

    unrankedList
  }
}