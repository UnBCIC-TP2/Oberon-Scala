package br.unb.cic.oberon.codegen

import br.unb.cic.oberon.ir.jimple._
import br.unb.cic.oberon.parser.Oberon2ScalaParser
import org.scalatest.funsuite.AnyFunSuite

import java.nio.file.{Files, Paths}

class JimpleCodeGenTest extends AnyFunSuite with Oberon2ScalaParser {
  test("generate class declaration from simple04.oberon") {
    val path = Paths.get(
      getClass.getClassLoader.getResource("simple/simple04.oberon").toURI
    )

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = parseAbs(parse(oberonParser,content))

    assert(module.name == "SimpleModule4")

    val targetClass = ClassDeclaration(
      modifiers = List(PublicModifer),
      classType = TObject(module.name),
      superClass = TObject("java.lang.Object"),
      interfaces = List.empty[JimpleType],
      fields = List(
        Field(
          modifiers = List(PublicModifer, StaticModifier, FinalModifier),
          fieldType = TInteger,
          name = "x"
        ),
        Field(
          modifiers = List(PublicModifer, StaticModifier, FinalModifier),
          fieldType = TInteger,
          name = "y"
        ),
        Field(
          modifiers = List(PublicModifer, StaticModifier, FinalModifier),
          fieldType = TInteger,
          name = "z"
        ),
        Field(
          modifiers = List(PublicModifer, StaticModifier),
          fieldType = TInteger,
          name = "abc"
        ),
        Field(
          modifiers = List(PublicModifer, StaticModifier),
          fieldType = TBoolean,
          name = "def"
        )
      ),
      methods = List(
        Method(
          modifiers = List(PublicModifer, StaticModifier),
          returnType = TVoid,
          name = "main",
          formals = List(TArray(TString)),
          exceptions = List.empty[JimpleType],
          body = DefaultMethodBody(
            localVariableDecls =
              List(LocalVariableDeclaration(TArray(TString), "args")),
            stmts = List(
              AssignStmt(
                StaticField(FieldSignature(module.name, TInteger, "x")),
                ImmediateExpression(ImmediateValue(IntValue(5)))
              ),
              AssignStmt(
                StaticField(FieldSignature(module.name, TInteger, "y")),
                ImmediateExpression(ImmediateValue(IntValue(10)))
              ),
              AssignStmt(
                StaticField(FieldSignature(module.name, TInteger, "z")),
                PlusExpression(
                  ImmediateValue(IntValue(5)),
                  ImmediateValue(IntValue(10))
                )
              )
            ),
            catchClauses = List.empty[CatchClause]
          )
        )
      )
    )
    val jimpleClass = JimpleCodeGenerator.generateCode(module)

    assert(jimpleClass == targetClass)
  }

  test("generate class declaration from boolean5.oberon") {
    val path = Paths.get(
      getClass.getClassLoader.getResource("boolean/boolean5.oberon").toURI
    )

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = parseAbs(parse(oberonParser,content))

    assert(module.name == "SimpleModule")

    val targetClass = ClassDeclaration(
      modifiers = List(PublicModifer),
      classType = TObject(module.name),
      superClass = TObject("java.lang.Object"),
      interfaces = List.empty[JimpleType],
      fields = List(
        Field(
          modifiers = List(PublicModifer, StaticModifier),
          fieldType = TFloat,
          name = "x"
        ),
        Field(
          modifiers = List(PublicModifer, StaticModifier),
          fieldType = TFloat,
          name = "y"
        )
      ),
      methods = List(
        Method(
          modifiers = List(PublicModifer, StaticModifier),
          returnType = TVoid,
          name = "main",
          formals = List(TArray(TString)),
          exceptions = List.empty[JimpleType],
          body = DefaultMethodBody(
            localVariableDecls =
              List(LocalVariableDeclaration(TArray(TString), "args")),
            stmts = List(
              AssignStmt(
                StaticField(FieldSignature(module.name, TFloat, "x")),
                ImmediateExpression(ImmediateValue(FloatValue(3.0f)))
              ),
              AssignStmt(
                StaticField(FieldSignature(module.name, TFloat, "y")),
                ImmediateExpression(ImmediateValue(FloatValue(3.0f)))
              ),
              IfStmt(CmpEqExpression(Local("x"), Local("y")), "label0"),
              GotoStmt("label1"),
              LabelStmt("label0"),
              AssignStmt(
                StaticField(FieldSignature(module.name, TFloat, "x")),
                ImmediateExpression(ImmediateValue(FloatValue(2.0f)))
              ),
              LabelStmt("label1")
            ),
            catchClauses = List.empty[CatchClause]
          )
        )
      )
    )
    val jimpleClass = JimpleCodeGenerator.generateCode(module)

    assert(jimpleClass == targetClass)
  }

  test("generate class declaration from userTypeSimple05.oberon") {
    val path = Paths.get(
      getClass.getClassLoader
        .getResource("simple/userTypeSimple05.oberon")
        .toURI
    )

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = parseAbs(parse(oberonParser,content))

    assert(module.name == "UserTypeModule")

    val targetClass = ClassDeclaration(
      modifiers = List(PublicModifer),
      classType = TObject(module.name),
      superClass = TObject("java.lang.Object"),
      interfaces = List.empty[JimpleType],
      fields = List(
        Field(
          modifiers = List(PublicModifer, StaticModifier),
          fieldType = TArray(TInteger),
          name = "x"
        ),
        Field(
          modifiers = List(PublicModifer, StaticModifier),
          fieldType = TArray(TInteger),
          name = "y"
        ),
        Field(
          modifiers = List(PublicModifer, StaticModifier),
          fieldType = TObject("complicated"),
          name = "z"
        )
      ),
      methods = List(
        Method(
          modifiers = List(PublicModifer, StaticModifier),
          returnType = TVoid,
          name = "main",
          formals = List(TArray(TString)),
          exceptions = List.empty[JimpleType],
          body = DefaultMethodBody(
            localVariableDecls =
              List(LocalVariableDeclaration(TArray(TString), "args")),
            stmts = List.empty[JimpleStatement],
            catchClauses = List.empty[CatchClause]
          )
        )
      )
    )
    val jimpleClass = JimpleCodeGenerator.generateCode(module)

    assert(jimpleClass == targetClass)
  }

  test("generate class declaration from ArrayAssignmentStmt03.oberon") {
    val path = Paths.get(
      getClass.getClassLoader
        .getResource("stmts/ArrayAssignmentStmt03.oberon")
        .toURI
    )

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = parseAbs(parse(oberonParser,content))

    assert(module.name == "ArrayAssignmentStmt03")

    val targetClass = ClassDeclaration(
      modifiers = List(PublicModifer),
      classType = TObject(module.name),
      superClass = TObject("java.lang.Object"),
      interfaces = List.empty[JimpleType],
      fields = List(
        Field(
          modifiers = List(PublicModifer, StaticModifier),
          fieldType = TArray(TInteger),
          name = "array"
        ),
        Field(
          modifiers = List(PublicModifer, StaticModifier),
          fieldType = TArray(TInteger),
          name = "outroarray"
        )
      ),
      methods = List(
        Method(
          modifiers = List(PublicModifer, StaticModifier),
          returnType = TVoid,
          name = "main",
          formals = List(TArray(TString)),
          exceptions = List.empty[JimpleType],
          body = DefaultMethodBody(
            localVariableDecls =
              List(LocalVariableDeclaration(TArray(TString), "args")),
            stmts = List(
              AssignStmt(
                ArrayRef("array", ImmediateValue(IntValue(0))),
                ImmediateExpression(ImmediateValue(IntValue(10)))
              ),
              AssignStmt(
                ArrayRef("array", ImmediateValue(IntValue(1))),
                ImmediateExpression(ImmediateValue(IntValue(20)))
              ),
              AssignStmt(
                ArrayRef("array", ImmediateValue(IntValue(2))),
                ImmediateExpression(ImmediateValue(IntValue(30)))
              ),
              AssignStmt(
                ArrayRef("outroarray", ImmediateValue(IntValue(0))),
                ImmediateExpression(ImmediateValue(IntValue(1)))
              ),
              AssignStmt(
                ArrayRef("outroarray", ImmediateValue(IntValue(1))),
                ImmediateExpression(ImmediateValue(IntValue(5)))
              )
            ),
            catchClauses = List.empty[CatchClause]
          )
        )
      )
    )
    val jimpleClass = JimpleCodeGenerator.generateCode(module)

    assert(jimpleClass == targetClass)
  }

  test("generate class declaration from recordAssignmentStmt01.oberon") {
    val path = Paths.get(
      getClass.getClassLoader
        .getResource("stmts/recordAssignmentStmt01.oberon")
        .toURI
    )

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = parseAbs(parse(oberonParser,content))

    assert(module.name == "SimpleModule")

    val targetClass = ClassDeclaration(
      modifiers = List(PublicModifer),
      classType = TObject(module.name),
      superClass = TObject("java.lang.Object"),
      interfaces = List.empty[JimpleType],
      fields = List(
        Field(
          modifiers = List(PublicModifer, StaticModifier),
          fieldType = TObject("date"),
          name = "d1"
        )
      ),
      methods = List(
        Method(
          modifiers = List(PublicModifer, StaticModifier),
          returnType = TVoid,
          name = "main",
          formals = List(TArray(TString)),
          exceptions = List.empty[JimpleType],
          body = DefaultMethodBody(
            localVariableDecls =
              List(LocalVariableDeclaration(TArray(TString), "args")),
            stmts = List(
              AssignStmt(
                FieldRef("d1", FieldSignature("date", TObject("date"), "day")),
                ImmediateExpression(ImmediateValue(IntValue(5)))
              )
            ),
            catchClauses = List.empty[CatchClause]
          )
        )
      )
    )
    val jimpleClass = JimpleCodeGenerator.generateCode(module)

    assert(jimpleClass == targetClass)
  }
}
