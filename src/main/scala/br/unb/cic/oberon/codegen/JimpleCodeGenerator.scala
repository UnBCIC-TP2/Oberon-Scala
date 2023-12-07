package br.unb.cic.oberon.codegen

import br.unb.cic.oberon.ir.ast.{AddExpression => OberonAddExpression, AndExpression => OberonAndExpression, BoolValue => OberonBoolValue, CharValue => OberonCharValue, DivExpression => OberonDivExpression, IntValue => OberonIntValue, ModExpression => OberonModExpression, MultExpression => OberonMultExpression, NotExpression => OberonNotExpression, NullValue => OberonNullValue, OrExpression => OberonOrExpression, RealValue => OberonRealValue, StringValue => OberonStringValue, SubExpression => OberonSubExpression, _}
import br.unb.cic.oberon.ir.jimple
import br.unb.cic.oberon.ir.jimple._
import br.unb.cic.oberon.tc.{ExpressionTypeChecker, TypeChecker}
import br.unb.cic.oberon.transformations.CoreChecker

import scala.collection.mutable.ListBuffer
import org.typelevel.paiges.Doc
import org.typelevel.paiges.Doc._

import scala.util.matching.Regex


object JimpleCodeGenerator extends CodeGenerator[String] {
  val indentSize:Doc = Doc.spaces(4)
  val doubleIndentSize:Doc = Doc.spaces(8)
  val twoLines: Doc = line * 2

  override def generateCode(module: OberonModule): String = {

    if (module.stmt.isDefined && !CoreChecker.checkModule(module))
      throw new NotOberonCoreException("Não podemos compilar módulo que não seja OberonCore")

    val fields = generateFields(module)
    val methodSignatures = generateMethodSignatures(module)
    val methods = generateMethods(module, fields, methodSignatures)


    ClassDeclaration(
      modifiers = List(PublicModifer),
      classType = TObject(module.name),
      superClass = TObject("java.lang.Object"),
      interfaces = List.empty[JimpleType],
      fields = fields,
      methods = methods
    )


    val methodsString = generateMethods(module, fields, methodSignatures).toString()
    var ConditionPrintClinit: Boolean = false

    //Tudo que estiver comentado, foi implementado pelo Grupo 06 do 2023.1

    // Cabeçalho
    val ModuleName: Doc = Doc.text(module.name)
    val MainHeader = text("Public Class ") + ModuleName + text(" extends java.lang.Object") / Doc.char('{')

    //Inicialização da Main

    val HasVoid = FindInString("TVoid,main,", methodsString)

    val MainTitle =
      if (HasVoid) {
        indentSize + text("public void <init>()") / indentSize + Doc.char('{') / doubleIndentSize +
          ModuleName + text(" r0;") + twoLines + doubleIndentSize + text("r0 := @this: ") + ModuleName + text(";") +
          twoLines + doubleIndentSize + text("specialinvoke r0.<java.lang.Object: void <init>()>();") +
          twoLines + doubleIndentSize + text("return;") + indentSize
      }else{
        text("")
      }

    // Instanciação de variaveis
    var VariablesAndConstants: Doc = Doc.text("")
    var variablesAndConstant: Doc = Doc.text("")

    for (elemento <- fields) {
      val name : Doc = text(elemento.name)
      val tipo : Doc = text(generateTypeString(elemento.fieldType))

      if ((elemento.modifiers).toString() == "List(PublicModifer, StaticModifier, FinalModifier)"){
        variablesAndConstant = text("Public static final ") + tipo + space + name
        if(ConditionPrintClinit == (false)){
          ConditionPrintClinit = true
        }
      } else {
        variablesAndConstant = text("Public ") + tipo + space + name
      }
      VariablesAndConstants = VariablesAndConstants + indentSize + variablesAndConstant + text(";") + Doc.line
    }

    // Percorre pelos metódos e retorna uma lista de expressões

    val ExpressionsListTuple = findExpressions(methodsString)


    // Instanciação de Métodos

    var MainMethods: Doc = Doc.text("")

    if(ConditionPrintClinit == true){
      MainMethods = MainMethods + twoLines + indentSize + text("public static void <clinit>()") / indentSize + Doc.char('{')

      for(tuple <- ExpressionsListTuple) {
        var ExpressionDoc = defineStaticExpressionToDoc(tuple)
        if (ExpressionDoc == Doc.text("")) {

        } else {
          MainMethods = MainMethods / doubleIndentSize + text("<") + ModuleName + text(": ") + ExpressionDoc + Doc.line
        }
      }

      MainMethods = MainMethods + Doc.line + doubleIndentSize + text("return;") / indentSize + Doc.char('}')
    }

    // Pretty Print Final

    val Jcode = MainHeader / VariablesAndConstants / MainTitle / indentSize + Doc.char('}') + MainMethods / Doc.char('}')
    Jcode.render(1000)
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
      }

    }else if(secondElement.contains("BooleanExpression")){

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
      }

    }else if(secondElement.contains("MultExpression")) {

      // Extrair os valores usando expressões regulares
      val extractedValues: Option[(String, Int, Int)] = expressionMul.findFirstMatchIn(element.toString()).map { matchResult =>
        (matchResult.group(1), matchResult.group(2).toInt, matchResult.group(3).toInt)
      }
      // Imprimir os valores extraídos ou tratar caso não tenham sido encontrados
      extractedValues match {
        case Some((variable, intValue, intValue2)) =>
          val mult: Int = intValue * intValue2
          envio = envio + Doc.text(s"int $variable> = $mult;")
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

    val unrakedList: List[(Any, Any)] = listToTuples(tempList).reverse

    unrakedList
  }

  // -------------------------------------------------------------------------------------

  def generateConstants(module: OberonModule): List[Field] = {
    val visitor = new ExpressionTypeChecker(new TypeChecker())

    module.constants.map(constant =>
      Field(
        modifiers = List(PublicModifer, StaticModifier, FinalModifier),
        fieldType = jimpleType(visitor.checkExpression(constant.exp), module),
        name = constant.name
      )
    )
  }

  def generateVariables(module: OberonModule): List[Field] =
    module.variables.map(variable =>
      Field(
        modifiers = List(PublicModifer, StaticModifier),
        fieldType = jimpleType(variable.variableType, module),
        name = variable.name
      )
    )

  def generateFields(module: OberonModule) =
    generateConstants(module) ::: generateVariables(module)

  def generateUserDefinedTypes(module: OberonModule): List[JimpleType] =
    module.userTypes.map(userType =>
      jimpleUserDefinedType(userType.name, module)
    )

  def generateMethodSignatures(module: OberonModule): List[MethodSignature] =
    module.procedures.map(procedure =>
      MethodSignature(
        className = module.name,
        returnType = jimpleType(procedure.returnType, module),
        methodName = procedure.name,
        formals =
          procedure.args.map(arg => jimpleType(arg.argumentType, module))
      )
    )

  def generateConstantAssignments(
      module: OberonModule,
      fields: List[Field],
      methodSignatures: List[MethodSignature]
  ): List[JimpleStatement] =
    module.constants.map(constant =>
      AssignStmt(
        StaticField(
          FieldSignature(
            module.name,
            fields.find(field => field.name == constant.name).get.fieldType,
            constant.name
          )
        ),
        jimpleExpression(constant.exp, module, fields, methodSignatures)
      )
    )

  def generateMethods(
      module: OberonModule,
      fields: List[Field],
      methodSignatures: List[MethodSignature]
  ): List[Method] =
    module.procedures.map(procedure =>
      jimpleMethod(procedure, module, fields, methodSignatures)
    ) :+ Method(
      modifiers = List(PublicModifer, StaticModifier),
      returnType = TVoid,
      name = "main",
      formals = List(TArray(TString)),
      exceptions = List.empty[JimpleType],
      body = DefaultMethodBody(
        localVariableDecls =
          List(LocalVariableDeclaration(TArray(TString), "args")),
        stmts = generateConstantAssignments(
          module,
          fields,
          methodSignatures
        ) ::: jimpleStatement(module.stmt, module, fields, methodSignatures, 0),
        catchClauses = List.empty[CatchClause]
      )
    )

  def jimpleMethod(
      procedure: Procedure,
      module: OberonModule,
      fields: List[Field],
      methodSignatures: List[MethodSignature]
  ): Method = Method(
    modifiers = List(PublicModifer, StaticModifier),
    returnType = jimpleType(procedure.returnType, module),
    name = procedure.name,
    formals = procedure.args.map(arg => jimpleType(arg.argumentType, module)),
    exceptions = List.empty[JimpleType],
    body = jimpleMethodBody(procedure, module, fields, methodSignatures)
  )

  // FIXME: statements don't work with local variables
  def jimpleMethodBody(
      procedure: Procedure,
      module: OberonModule,
      fields: List[Field],
      methodSignatures: List[MethodSignature]
  ): JimpleMethodBody = {
    val localVariableDecls = procedure.args.map(arg =>
      LocalVariableDeclaration(jimpleType(arg.argumentType, module), arg.name)
    )
    DefaultMethodBody(
      localVariableDecls = localVariableDecls,
      stmts = jimpleStatement(
        procedure.stmt,
        module,
        fields,
        methodSignatures,
        indexOffset = 0
      ),
      catchClauses = List.empty[CatchClause]
    )
  }

  def jimpleStatement(
      oberonStmt: Option[Statement],
      module: OberonModule,
      fields: List[Field],
      methodSignatures: List[MethodSignature],
      indexOffset: Int
  ): List[JimpleStatement] = oberonStmt match {
    case Some(someStmt) =>
      jimpleStatement(someStmt, module, fields, methodSignatures, indexOffset)
    case None => List.empty[JimpleStatement]
  }

  def jimpleStatement(
      oberonStmt: Statement,
      module: OberonModule,
      fields: List[Field],
      methodSignatures: List[MethodSignature],
      indexOffset: Int
  ): List[JimpleStatement] = oberonStmt match {
    case AssignmentStmt(designator, exp) =>
      jimpleAssignment(designator, exp, module, fields, methodSignatures)

    case SequenceStmt(stmts) => {
      var index = indexOffset
      var jimpleStmts = List.empty[JimpleStatement]

      for (stmt <- stmts) {
        jimpleStmts = jimpleStmts ::: jimpleStatement(
          stmt,
          module,
          fields,
          methodSignatures,
          index
        )
        index += calculateIndexOffset(stmt)
      }
      jimpleStmts
    }
    case IfElseStmt(condition, thenStmt, elseStmt) =>
      jimpleIfStatement(
        condition,
        thenStmt,
        elseStmt,
        module,
        fields,
        methodSignatures,
        indexOffset
      )
    case WhileStmt(condition, stmt) =>
      jimpleWhileStatement(
        condition,
        stmt,
        module,
        fields,
        methodSignatures,
        indexOffset
      )

    case _ => throw new Exception("Non-exhaustive match in case statement.")
  }

  def jimpleAssignment(
      designator: Designator,
      exp: Expression,
      module: OberonModule,
      fields: List[Field],
      methodSignatures: List[MethodSignature],
      methodBody: Option[DefaultMethodBody] = None
  ): List[JimpleStatement] = designator match {
    case VarAssignment(varName) =>
      List(
        AssignStmt(
          StaticField(
            FieldSignature(
              module.name,
              fields.find(field => field.name == varName).get.fieldType,
              varName
            )
          ),
          jimpleExpression(exp, module, fields, methodSignatures)
        )
      )
    case ArrayAssignment(array, index) =>
      array match {
        case VarExpression(arrayName) =>
          List(
            AssignStmt(
              ArrayRef(arrayName, jimpleImmediate(index)),
              jimpleExpression(exp, module, fields, methodSignatures)
            )
          )
        case _ => throw new Exception("Array must be a field reference.")
      }
    case RecordAssignment(record, field) =>
      record match {
        case VarExpression(recordName) => {
          val recordType =
            fields.find(field => field.name == recordName).get.fieldType
          val recordClassName = recordType.asInstanceOf[TObject].name

          List(
            AssignStmt(
              FieldRef(
                recordName,
                FieldSignature(recordClassName, recordType, field)
              ),
              jimpleExpression(exp, module, fields, methodSignatures)
            )
          )
        }
        case _ => throw new Exception("Record must be a field reference.")
      }

    case PointerAssignment(_) =>
      throw new Exception(
        "Pointers are not yet supported by Jimple code generation."
      )
    case _ => throw new Exception("Non-exhaustive match in case statement.")
  }

  def jimpleIfStatement(
      condition: Expression,
      thenStmt: Statement,
      elseStmt: Option[Statement],
      module: OberonModule,
      fields: List[Field],
      methodSignatures: List[MethodSignature],
      indexOffset: Int
  ): List[JimpleStatement] = {
    var index = indexOffset
    val ifLabel = s"label${index}"
    val endIfLabel = s"label${index + 1}"
    val buffer = ListBuffer[JimpleStatement]()

    index += 2

    buffer += IfStmt(
      jimpleExpression(condition, module, fields, methodSignatures),
      ifLabel
    )
    buffer ++= jimpleStatement(
      elseStmt,
      module,
      fields,
      methodSignatures,
      index
    )
    index += calculateIndexOffset(elseStmt)
    buffer += GotoStmt(endIfLabel)
    buffer += LabelStmt(ifLabel)
    buffer ++= jimpleStatement(
      thenStmt,
      module,
      fields,
      methodSignatures,
      index
    )
    index += calculateIndexOffset(thenStmt)
    buffer += LabelStmt(endIfLabel)

    buffer.result()
  }

  def jimpleWhileStatement(
      condition: Expression,
      stmt: Statement,
      module: OberonModule,
      fields: List[Field],
      methodSignatures: List[MethodSignature],
      indexOffset: Int
  ): List[JimpleStatement] = {
    var index = indexOffset
    val whileLabel = s"label${index}"
    val endWhileLabel = s"label${index + 1}"
    val buffer = ListBuffer[JimpleStatement]()

    index += 2

    buffer += LabelStmt(whileLabel)
    buffer += IfStmt(
      jimpleExpression(condition, module, fields, methodSignatures),
      endWhileLabel
    )
    buffer ++= jimpleStatement(stmt, module, fields, methodSignatures, index)
    index += calculateIndexOffset(stmt)
    buffer += GotoStmt(whileLabel)
    buffer += LabelStmt(endWhileLabel)

    buffer.result()
  }

  def jimpleExpression(
      oberonExpression: Expression,
      module: OberonModule,
      fields: List[Field],
      methodSignatures: List[MethodSignature]
  ): JimpleExpression = oberonExpression match {
    case value: Value => ImmediateExpression(jimpleImmediate(value))
    case Brackets(exp) =>
      jimpleExpression(exp, module, fields, methodSignatures)

    case ArraySubscript(arrayBase, index) =>
      arrayBase match {
        case VarExpression(name) =>
          ArraySubscriptExpression(name, jimpleImmediate(index))
        case _ => throw new Exception("Non-exhaustive match in case statement.")
      }
    case VarExpression(name) => {
      val fieldType = fields.find(field => field.name == name).get.fieldType
      FieldRefExpression(module.name, fieldType, name)
    }

    case EQExpression(left, right) =>
      CmpEqExpression(jimpleImmediate(left), jimpleImmediate(right))
    case NEQExpression(left, right) =>
      CmpNeExpression(jimpleImmediate(left), jimpleImmediate(right))
    case GTExpression(left, right) =>
      CmpGtExpression(jimpleImmediate(left), jimpleImmediate(right))
    case LTExpression(left, right) =>
      CmpLtExpression(jimpleImmediate(left), jimpleImmediate(right))
    case GTEExpression(left, right) =>
      CmpGeExpression(jimpleImmediate(left), jimpleImmediate(right))
    case LTEExpression(left, right) =>
      CmpLeExpression(jimpleImmediate(left), jimpleImmediate(right))

    case OberonAddExpression(left, right) =>
      PlusExpression(jimpleImmediate(left), jimpleImmediate(right))
    case OberonSubExpression(left, right) =>
      MinusExpression(jimpleImmediate(left), jimpleImmediate(right))
    case OberonMultExpression(left, right) =>
      MultExpression(jimpleImmediate(left), jimpleImmediate(right))
    case OberonDivExpression(left, right) =>
      DivExpression(jimpleImmediate(left), jimpleImmediate(right))
    case OberonModExpression(left, right) =>
      RemainderExpression(jimpleImmediate(left), jimpleImmediate(right))

    case OberonAndExpression(left, right) =>
      AndExpression(jimpleImmediate(left), jimpleImmediate(right))
    case OberonOrExpression(left, right) =>
      OrExpression(jimpleImmediate(left), jimpleImmediate(right))
    case OberonNotExpression(exp) => NegExpression(jimpleImmediate(exp))

    case PointerAccessExpression(_) =>
      throw new Exception(
        "Pointers are not yet supported by Jimple code generation."
      )

    case _ => throw new Exception("Non-exhaustive match in case statement.")
  }

  def jimpleImmediate(oberonExpression: Expression): JimpleImmediate =
    oberonExpression match {
      case value: Value        => ImmediateValue(jimpleValue(value))
      case VarExpression(name) => Local(name)

      case _ => throw new Exception("Non-exhaustive match in case statement.")
    }

  def jimpleValue(oberonValue: Value): JimpleValue = oberonValue match {
    case OberonIntValue(int)       => IntValue(int)
    case OberonBoolValue(bool)     => BooleanValue(bool)
    case OberonRealValue(real)     => FloatValue(real.toFloat)
    case OberonCharValue(char)     => StringValue(char.toString)
    case OberonStringValue(string) => StringValue(string)
    case OberonNullValue           => NullValue

    case _ => throw new Exception("Non-exhaustive match in case statement.")
  }

  def jimpleType(oberonType: Option[Type], module: OberonModule): JimpleType =
    oberonType match {
      case Some(someType) => jimpleType(someType, module)
      case None           => TVoid
    }

  def jimpleType(oberonType: Type, module: OberonModule): JimpleType =
    oberonType match {
      case IntegerType   => TInteger
      case RealType      => TFloat
      case BooleanType   => TBoolean
      case CharacterType => TCharacter
      case StringType    => TString
      case UndefinedType => TUnknown
      case NullType      => TNull

      case ArrayType(_, baseType) => TArray(jimpleType(baseType, module))

      case ReferenceToUserDefinedType(name) =>
        jimpleUserDefinedType(name, module)
      case PointerType(_) =>
        throw new Exception(
          "Pointers are not yet supported by Jimple code generation."
        )

      case _ => throw new Exception("Non-exhaustive match in case statement.")
    }

  def jimpleUserDefinedType(name: String, module: OberonModule): JimpleType =
    jimpleUserDefinedType(
      module.userTypes.find(userType => userType.name == name).get,
      module
    )

  def jimpleUserDefinedType(
      userType: UserDefinedType,
      module: OberonModule
  ): JimpleType = userType.baseType match {
    case RecordType(_)          => TObject(userType.name)
    case ArrayType(_, baseType) => TArray(jimpleType(baseType, module))

    case _ => throw new Exception("Non-exhaustive match in case statement.")
  }

  def calculateIndexOffset(oberonStmt: Option[Statement]): Int =
    oberonStmt match {
      case Some(someStmt) => calculateIndexOffset(someStmt)
      case None           => 0
    }

  def calculateIndexOffset(oberonStmt: Statement): Int = oberonStmt match {
    case SequenceStmt(stmts)  => stmts.map(calculateIndexOffset).sum
    case AssignmentStmt(_, _) => 0
    case IfElseStmt(_, thenStmt, elseStmt) =>
      calculateIndexOffset(thenStmt) + calculateIndexOffset(elseStmt) + 2
    case WhileStmt(_, stmt) => calculateIndexOffset(stmt) + 2

    case _ => throw new Exception("Non-exhaustive match in case statement.")
  }
}
