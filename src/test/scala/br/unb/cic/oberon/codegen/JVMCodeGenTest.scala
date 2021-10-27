package br.unb.cic.oberon.codegen

import br.unb.cic.oberon.parser.ScalaParser
import org.scalatest.funsuite.AnyFunSuite

import java.io.FileOutputStream
import java.nio.file.{Files, Paths}
import java.util.Base64

import jdk.internal.org.objectweb.asm.Opcodes._
import jdk.internal.org.objectweb.asm._
import java.io.File

import br.unb.cic.oberon.ast._

import org.objectweb.asm
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Type._

class JVMCodeGenTest extends AnyFunSuite {
  test("Generate code with fields of simple01.oberon") {
    val path = Paths.get(getClass.getClassLoader.getResource("simple/simple01.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")
    assert(module.variables.size == 0)
    assert(module.constants.size == 1)

    val codeGen = JVMCodeGenerator

    val byteCodeAsString = codeGen.generateCode(module)

    val out = new FileOutputStream(createOutputFile(module.name).toFile)

    out.write(Base64.getDecoder.decode(byteCodeAsString))

    val cr = new ClassReader(Base64.getDecoder.decode(byteCodeAsString));

    val v = new ClassVisitorTest(ASM4);

    cr.accept(v, 0);

    assert(1 == v.numberOfTotalFields);
    assert(1 == v.numberOfIntegerConstants);
    assert(0 == v.numberOfIntegerVariables);
    assert(0 == v.numberOfBooleanVariables);
    assert(0 == v.numberOfBooleanConstants);
  }

  test("Generate code with fields of simple02.oberon") {
    val path = Paths.get(getClass.getClassLoader.getResource("simple/simple02.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")
    assert(module.variables.size == 2)
    assert(module.constants.size == 1)

    val codeGen = JVMCodeGenerator

    val byteCodeAsString = codeGen.generateCode(module)

    val out = new FileOutputStream(createOutputFile(module.name).toFile)

    out.write(Base64.getDecoder.decode(byteCodeAsString))

    val cr = new ClassReader(Base64.getDecoder.decode(byteCodeAsString));

    val v = new ClassVisitorTest(ASM4);

    cr.accept(v, 0);

    assert(3 == v.numberOfTotalFields);
    assert(1 == v.numberOfIntegerConstants);
    assert(1 == v.numberOfIntegerVariables);
    assert(1 == v.numberOfBooleanVariables);
    assert(0 == v.numberOfBooleanConstants);
  }

 test("Generate code with fields of simple03.oberon") {
    val path = Paths.get(getClass.getClassLoader.getResource("simple/simple03.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")
    assert(module.variables.size == 2)
    assert(module.constants.size == 3)

    val codeGen = JVMCodeGenerator

    val byteCodeAsString = codeGen.generateCode(module)

    val out = new FileOutputStream(createOutputFile(module.name).toFile)

    out.write(Base64.getDecoder.decode(byteCodeAsString))

    val cr = new ClassReader(Base64.getDecoder.decode(byteCodeAsString));

    val v = new ClassVisitorTest(ASM4);

    cr.accept(v, 0);

    assert(5 == v.numberOfTotalFields);
    assert(2 == v.numberOfIntegerConstants);
    assert(1 == v.numberOfIntegerVariables);
    assert(1 == v.numberOfBooleanVariables);
    assert(1 == v.numberOfBooleanConstants);
  }

  test("Generate code of simple04.oberon") {
    val path = Paths.get(getClass.getClassLoader.getResource("simple/simple04.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")
    assert(module.variables.size == 2)
    assert(module.constants.size == 3)

    val codeGen = JVMCodeGenerator

    val byteCodeAsString = codeGen.generateCode(module)

    val out = new FileOutputStream(createOutputFile(module.name).toFile)

    out.write(Base64.getDecoder.decode(byteCodeAsString))

    val cr = new ClassReader(Base64.getDecoder.decode(byteCodeAsString));

    val v = new ClassVisitorTest(ASM4);

    cr.accept(v, 0);

    assert(5 == v.numberOfTotalFields);
    assert(3 == v.numberOfIntegerConstants);
    assert(1 == v.numberOfIntegerVariables);
    assert(1 == v.numberOfBooleanVariables);
    assert(0 == v.numberOfBooleanConstants);
  }

  test("Generate code of simple05.oberon") {
    val path = Paths.get(getClass.getClassLoader.getResource("simple/simple05.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")
    assert(module.variables.size == 2)
    assert(module.constants.size == 1)

    val codeGen = JVMCodeGenerator

    val byteCodeAsString = codeGen.generateCode(module)

    val out = new FileOutputStream(createOutputFile(module.name).toFile)

    out.write(Base64.getDecoder.decode(byteCodeAsString))

    val cr = new ClassReader(Base64.getDecoder.decode(byteCodeAsString));

    val v = new ClassVisitorTest(ASM4);

    cr.accept(v, 0);

    assert(3 == v.numberOfTotalFields);
    assert(1 == v.numberOfIntegerConstants);
    assert(1 == v.numberOfIntegerVariables);
    assert(1 == v.numberOfBooleanVariables);
    assert(0 == v.numberOfBooleanConstants);
  }

  test("Generate code of simple06.oberon") {
    val path = Paths.get(getClass.getClassLoader.getResource("simple/simple06.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")
    assert(module.variables.size == 2)
    assert(module.constants.size == 1)

    val codeGen = JVMCodeGenerator

    val byteCodeAsString = codeGen.generateCode(module)

    val out = new FileOutputStream(createOutputFile(module.name).toFile)

    out.write(Base64.getDecoder.decode(byteCodeAsString))

    val cr = new ClassReader(Base64.getDecoder.decode(byteCodeAsString));

    val v = new ClassVisitorTest(ASM4);

    cr.accept(v, 0);

    assert(3 == v.numberOfTotalFields);
    assert(1 == v.numberOfIntegerConstants);
    assert(1 == v.numberOfIntegerVariables);
    assert(1 == v.numberOfBooleanVariables);
    assert(0 == v.numberOfBooleanConstants);
  }

  test("Generate code of simple07.oberon") {
    val path = Paths.get(getClass.getClassLoader.getResource("simple/simple07.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")
    assert(module.variables.size == 2)
    assert(module.constants.size == 2)

    val codeGen = JVMCodeGenerator

    val byteCodeAsString = codeGen.generateCode(module)

    val out = new FileOutputStream(createOutputFile(module.name).toFile)

    out.write(Base64.getDecoder.decode(byteCodeAsString))

    val cr = new ClassReader(Base64.getDecoder.decode(byteCodeAsString));

    val v = new ClassVisitorTest(ASM4);

    cr.accept(v, 0);

    assert(4 == v.numberOfTotalFields);
    assert(2 == v.numberOfIntegerConstants);
    assert(1 == v.numberOfIntegerVariables);
    assert(1 == v.numberOfBooleanVariables);
    assert(0 == v.numberOfBooleanConstants);
  }

  test("Generate code of simple08.oberon") {
    val path = Paths.get(getClass.getClassLoader.getResource("simple/simple08.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")
    assert(module.variables.size == 2)
    assert(module.constants.size == 3)

    val codeGen = JVMCodeGenerator

    val byteCodeAsString = codeGen.generateCode(module)

    val out = new FileOutputStream(createOutputFile(module.name).toFile)

    out.write(Base64.getDecoder.decode(byteCodeAsString))

    val cr = new ClassReader(Base64.getDecoder.decode(byteCodeAsString));

    val v = new ClassVisitorTest(ASM4);

    cr.accept(v, 0);

    assert(5 == v.numberOfTotalFields);
    assert(0 == v.numberOfIntegerConstants);
    assert(1 == v.numberOfIntegerVariables);
    assert(1 == v.numberOfBooleanVariables);
    assert(3 == v.numberOfBooleanConstants);
  }

  test("Generate code of simple09.oberon") {
    val path = Paths.get(getClass.getClassLoader.getResource("simple/simple09.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")
    assert(module.variables.size == 2)
    assert(module.constants.size == 1)

    val codeGen = JVMCodeGenerator

    val byteCodeAsString = codeGen.generateCode(module)

    val out = new FileOutputStream(createOutputFile(module.name).toFile)

    out.write(Base64.getDecoder.decode(byteCodeAsString))

    val cr = new ClassReader(Base64.getDecoder.decode(byteCodeAsString));

    val v = new ClassVisitorTest(ASM4);

    cr.accept(v, 0);

    assert(3 == v.numberOfTotalFields);
    assert(0 == v.numberOfIntegerConstants);
    assert(1 == v.numberOfIntegerVariables);
    assert(1 == v.numberOfBooleanVariables);
    assert(1 == v.numberOfBooleanConstants);
  }

  test("Generate code of simple10.oberon") {
    val path = Paths.get(getClass.getClassLoader.getResource("simple/simple10.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")
    assert(module.variables.size == 1)
    assert(module.constants.size == 0)

    val codeGen = JVMCodeGenerator

    val byteCodeAsString = codeGen.generateCode(module)

    val out = new FileOutputStream(createOutputFile(module.name).toFile)

    out.write(Base64.getDecoder.decode(byteCodeAsString))

    val cr = new ClassReader(Base64.getDecoder.decode(byteCodeAsString));

    val v = new ClassVisitorTest(ASM4);

    cr.accept(v, 0);

    assert(1 == v.numberOfTotalFields);
    assert(0 == v.numberOfIntegerConstants);
    assert(1 == v.numberOfIntegerVariables);
    assert(0 == v.numberOfBooleanVariables);
    assert(0 == v.numberOfBooleanConstants);
  }

  test("Generate integer expression") {
    val cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
    cw.visit(V1_5, ACC_PUBLIC, "test", null, "java/lang/Object", null);

    visitExpressionMethod(IntValue(-1), cw, "int1", INT_TYPE)
    visitExpressionMethod(IntValue(1), cw, "int2", INT_TYPE)
    visitExpressionMethod(IntValue(10), cw, "int3", INT_TYPE)

    cw.visitEnd()

    val b = cw.toByteArray()

    val stubClassLoader = new StubClassLoader()
    val c = stubClassLoader.getClass("test", b)

    assert(-1 == c.getDeclaredMethod("int1").invoke(null))
    assert(1 == c.getDeclaredMethod("int2").invoke(null))
    assert(10 == c.getDeclaredMethod("int3").invoke(null))
  }

  test("Generate real expression") {
    val cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
    cw.visit(V1_5, ACC_PUBLIC, "test", null, "java/lang/Object", null);

    visitExpressionMethod(RealValue(0.0), cw, "real1", DOUBLE_TYPE)
    visitExpressionMethod(RealValue(1.0), cw, "real2", DOUBLE_TYPE)
    visitExpressionMethod(RealValue(1.5), cw, "real3", DOUBLE_TYPE)

    cw.visitEnd()

    val b = cw.toByteArray()

    val stubClassLoader = new StubClassLoader()
    val c = stubClassLoader.getClass("test", b)

    assert(0.0 == c.getDeclaredMethod("real1").invoke(null))
    assert(1.0 == c.getDeclaredMethod("real2").invoke(null))
    assert(1.5 == c.getDeclaredMethod("real3").invoke(null))
  }

  test("Generate char expression") {
    val cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
    cw.visit(V1_5, ACC_PUBLIC, "test", null, "java/lang/Object", null);

    visitExpressionMethod(CharValue('a'), cw, "charExpression", CHAR_TYPE)

    cw.visitEnd()

    val b = cw.toByteArray()

    val stubClassLoader = new StubClassLoader()
    val c = stubClassLoader.getClass("test", b)

    assert('a' == c.getDeclaredMethod("charExpression").invoke(null))
  }

  test("Generate boolean expression") {
    val cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
    cw.visit(V1_5, ACC_PUBLIC, "test", null, "java/lang/Object", null);

    visitExpressionMethod(BoolValue(true), cw, "trueExpression", returnType = BOOLEAN_TYPE)
    visitExpressionMethod(BoolValue(false), cw, "falseExpression", returnType = BOOLEAN_TYPE)

    cw.visitEnd()

    val b = cw.toByteArray()

    val stubClassLoader = new StubClassLoader()
    val c = stubClassLoader.getClass("test", b)

    assert(true == c.getDeclaredMethod("trueExpression").invoke(null))
    assert(false == c.getDeclaredMethod("falseExpression").invoke(null))
  }

  test("Generate string expression") {
    val cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
    cw.visit(V1_5, ACC_PUBLIC, "test", null, "java/lang/Object", null);

    visitExpressionMethod(StringValue("Lorem ipsum"), cw, "stringExpression", asm.Type.getType(classOf[String]))

    cw.visitEnd()

    val b = cw.toByteArray()

    val stubClassLoader = new StubClassLoader()
    val c = stubClassLoader.getClass("test", b)

    assert("Lorem ipsum" == c.getDeclaredMethod("stringExpression").invoke(null))
  }

  test("Generate constant var expression") {
    val constant = Constant("x", IntValue(5))
    val module = OberonModule("test", null, null, List(constant), List(), List(), None)
    val codeGen = JVMCodeGenerator

    val cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
    cw.visit(V1_5, ACC_PUBLIC, "test", null, "java/lang/Object", null);
    codeGen.generateConstants(module.constants, cw)

    val mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "varExpression", "()I", null, null)

    mv.visitCode()

    codeGen.generateExpression(VarExpression("x"), mv, module)

    mv.visitInsn(IRETURN)

    mv.visitMaxs(0, 0) 
    mv.visitEnd()

    cw.visitEnd()

    val b = cw.toByteArray()

    val stubClassLoader = new StubClassLoader()
    val c = stubClassLoader.getClass("test", b)

    assert(5 == c.getDeclaredMethod("varExpression").invoke(null))
  }

  test("Generate EQ expression") {
    val cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
    cw.visit(V1_5, ACC_PUBLIC, "test", null, "java/lang/Object", null);

    visitExpressionMethod(LTExpression(IntValue(10), IntValue(5)), cw, "greaterMethod", BOOLEAN_TYPE)
    visitExpressionMethod(EQExpression(IntValue(10), IntValue(10)), cw, "equalMethod", BOOLEAN_TYPE)
    visitExpressionMethod(EQExpression(IntValue(5), IntValue(10)), cw, "lessMethod", BOOLEAN_TYPE)

    cw.visitEnd()

    val b = cw.toByteArray()

    val stubClassLoader = new StubClassLoader()
    val c = stubClassLoader.getClass("test", b)

    assert(false == c.getDeclaredMethod("greaterMethod").invoke(null))
    assert(true == c.getDeclaredMethod("equalMethod").invoke(null))
    assert(false == c.getDeclaredMethod("lessMethod").invoke(null))
  }

  test("Generate NEQ expression") {
    val cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
    cw.visit(V1_5, ACC_PUBLIC, "test", null, "java/lang/Object", null);

    visitExpressionMethod(NEQExpression(IntValue(10), IntValue(9)), cw, "greaterMethod", BOOLEAN_TYPE)
    visitExpressionMethod(NEQExpression(IntValue(10), IntValue(10)), cw, "equalMethod", BOOLEAN_TYPE)
    visitExpressionMethod(NEQExpression(IntValue(9), IntValue(10)), cw, "lessMethod", BOOLEAN_TYPE)

    cw.visitEnd()

    val b = cw.toByteArray()

    val stubClassLoader = new StubClassLoader()
    val c = stubClassLoader.getClass("test", b)

    assert(true == c.getDeclaredMethod("greaterMethod").invoke(null))
    assert(false == c.getDeclaredMethod("equalMethod").invoke(null))
    assert(true == c.getDeclaredMethod("lessMethod").invoke(null))
  }

   test("Generate GT expression") {
    val cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
    cw.visit(V1_5, ACC_PUBLIC, "test", null, "java/lang/Object", null);

    visitExpressionMethod(GTExpression(IntValue(10), IntValue(9)), cw, "greaterMethod", BOOLEAN_TYPE)
    visitExpressionMethod(GTExpression(IntValue(10), IntValue(10)), cw, "equalMethod", BOOLEAN_TYPE)
    visitExpressionMethod(GTExpression(IntValue(9), IntValue(10)), cw, "lessMethod", BOOLEAN_TYPE)

    cw.visitEnd()

    val b = cw.toByteArray()

    val stubClassLoader = new StubClassLoader()
    val c = stubClassLoader.getClass("test", b)

    assert(true == c.getDeclaredMethod("greaterMethod").invoke(null))
    assert(false == c.getDeclaredMethod("equalMethod").invoke(null))
    assert(false == c.getDeclaredMethod("lessMethod").invoke(null))
  }

  test("Generate LT expression") {
    val cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
    cw.visit(V1_5, ACC_PUBLIC, "test", null, "java/lang/Object", null);

    visitExpressionMethod(LTExpression(IntValue(10), IntValue(5)), cw, "greaterMethod", BOOLEAN_TYPE)
    visitExpressionMethod(LTExpression(IntValue(5), IntValue(5)), cw, "equalMethod", BOOLEAN_TYPE)
    visitExpressionMethod(LTExpression(IntValue(5), IntValue(10)), cw, "lessMethod", BOOLEAN_TYPE)

    cw.visitEnd()

    val b = cw.toByteArray()

    val stubClassLoader = new StubClassLoader()
    val c = stubClassLoader.getClass("test", b)

    assert(false == c.getDeclaredMethod("greaterMethod").invoke(null))
    assert(false == c.getDeclaredMethod("equalMethod").invoke(null))
    assert(true == c.getDeclaredMethod("lessMethod").invoke(null))
  }

  test("Generate GTE expression") {
    val cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
    cw.visit(V1_5, ACC_PUBLIC, "test", null, "java/lang/Object", null);

    visitExpressionMethod(GTEExpression(IntValue(10), IntValue(9)), cw, "greaterMethod", BOOLEAN_TYPE)
    visitExpressionMethod(GTEExpression(IntValue(10), IntValue(10)), cw, "equalMethod", BOOLEAN_TYPE)
    visitExpressionMethod(GTEExpression(IntValue(9), IntValue(10)), cw, "lessMethod", BOOLEAN_TYPE)

    cw.visitEnd()

    val b = cw.toByteArray()

    val stubClassLoader = new StubClassLoader()
    val c = stubClassLoader.getClass("test", b)

    assert(true == c.getDeclaredMethod("greaterMethod").invoke(null))
    assert(true == c.getDeclaredMethod("equalMethod").invoke(null))
    assert(false == c.getDeclaredMethod("lessMethod").invoke(null))
  }

  test("Generate LTE expression") {
    val cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
    cw.visit(V1_5, ACC_PUBLIC, "test", null, "java/lang/Object", null);

    visitExpressionMethod(LTEExpression(IntValue(10), IntValue(9)), cw, "greaterMethod", BOOLEAN_TYPE)
    visitExpressionMethod(LTEExpression(IntValue(10), IntValue(10)), cw, "equalMethod", BOOLEAN_TYPE)
    visitExpressionMethod(LTEExpression(IntValue(9), IntValue(10)), cw, "lessMethod", BOOLEAN_TYPE)

    cw.visitEnd()

    val b = cw.toByteArray()

    val stubClassLoader = new StubClassLoader()
    val c = stubClassLoader.getClass("test", b)

    assert(false == c.getDeclaredMethod("greaterMethod").invoke(null))
    assert(true == c.getDeclaredMethod("equalMethod").invoke(null))
    assert(true == c.getDeclaredMethod("lessMethod").invoke(null))
  }
  

  test("Generate Add expression") {
    val cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
    cw.visit(V1_5, ACC_PUBLIC, "test", null, "java/lang/Object", null);

    visitExpressionMethod(AddExpression(IntValue(4), IntValue(5)), cw, "add1", INT_TYPE)
    visitExpressionMethod(AddExpression(IntValue(3), IntValue(-1)), cw, "add2", INT_TYPE)
    visitExpressionMethod(AddExpression(IntValue(-1), IntValue(-2)), cw, "add3", INT_TYPE)

    cw.visitEnd()

    val b = cw.toByteArray()

    val stubClassLoader = new StubClassLoader()
    val c = stubClassLoader.getClass("test", b)

    assert(9 == c.getDeclaredMethod("add1").invoke(null))
    assert(2 == c.getDeclaredMethod("add2").invoke(null))
    assert(-3 == c.getDeclaredMethod("add3").invoke(null))
  }

   test("Generate Sub expression") {
    val cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
    cw.visit(V1_5, ACC_PUBLIC, "test", null, "java/lang/Object", null);

    visitExpressionMethod(SubExpression(IntValue(4), IntValue(2)), cw, "sub1", INT_TYPE)
    visitExpressionMethod(SubExpression(IntValue(3), IntValue(-1)), cw, "sub2", INT_TYPE)
    visitExpressionMethod(SubExpression(IntValue(-1), IntValue(-2)), cw, "sub3", INT_TYPE)

    cw.visitEnd()

    val b = cw.toByteArray()

    val stubClassLoader = new StubClassLoader()
    val c = stubClassLoader.getClass("test", b)

    assert(2 == c.getDeclaredMethod("sub1").invoke(null))
    assert(4 == c.getDeclaredMethod("sub2").invoke(null))
    assert(1 == c.getDeclaredMethod("sub3").invoke(null))
  }

   test("Generate Mult expression") {
    val cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
    cw.visit(V1_5, ACC_PUBLIC, "test", null, "java/lang/Object", null);

    visitExpressionMethod(MultExpression(IntValue(4), IntValue(5)), cw, "mult1", INT_TYPE)
    visitExpressionMethod(MultExpression(IntValue(2), IntValue(10)), cw, "mult2", INT_TYPE)
    visitExpressionMethod(MultExpression(IntValue(3), IntValue(7)), cw, "mult3", INT_TYPE)

    cw.visitEnd()

    val b = cw.toByteArray()

    val stubClassLoader = new StubClassLoader()
    val c = stubClassLoader.getClass("test", b)

    assert(20 == c.getDeclaredMethod("mult1").invoke(null))
    assert(20 == c.getDeclaredMethod("mult2").invoke(null))
    assert(21 == c.getDeclaredMethod("mult3").invoke(null))
  }

  test("Generate Div expression") {
    val cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
    cw.visit(V1_5, ACC_PUBLIC, "test", null, "java/lang/Object", null);

    visitExpressionMethod(DivExpression(IntValue(1), IntValue(2)), cw, "div1", INT_TYPE)
    visitExpressionMethod(DivExpression(IntValue(4), IntValue(2)), cw, "div2", INT_TYPE)
    visitExpressionMethod(DivExpression(IntValue(9), IntValue(3)), cw, "div3", INT_TYPE)

    cw.visitEnd()

    val b = cw.toByteArray()

    val stubClassLoader = new StubClassLoader()
    val c = stubClassLoader.getClass("test", b)

    assert(0 == c.getDeclaredMethod("div1").invoke(null))
    assert(2 == c.getDeclaredMethod("div2").invoke(null))
    assert(3 == c.getDeclaredMethod("div3").invoke(null))
  }

  test("Generate And expression") {
    val cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
    cw.visit(V1_5, ACC_PUBLIC, "test", null, "java/lang/Object", null);

    visitExpressionMethod(AndExpression(BoolValue(true), BoolValue(true)), cw, "and1", BOOLEAN_TYPE)
    visitExpressionMethod(AndExpression(BoolValue(true), BoolValue(false)), cw, "and2", BOOLEAN_TYPE)
    visitExpressionMethod(AndExpression(BoolValue(false), BoolValue(true)), cw, "and3", BOOLEAN_TYPE)
    visitExpressionMethod(AndExpression(BoolValue(false), BoolValue(false)), cw, "and4", BOOLEAN_TYPE)

    cw.visitEnd()

    val b = cw.toByteArray()

    val stubClassLoader = new StubClassLoader()
    val c = stubClassLoader.getClass("test", b)

    assert(true == c.getDeclaredMethod("and1").invoke(null))
    assert(false == c.getDeclaredMethod("and2").invoke(null))
    assert(false == c.getDeclaredMethod("and3").invoke(null))
    assert(false == c.getDeclaredMethod("and4").invoke(null))
  }

  test("Generate Or expression") {
    val cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
    cw.visit(V1_5, ACC_PUBLIC, "test", null, "java/lang/Object", null);

    visitExpressionMethod(OrExpression(BoolValue(true), BoolValue(true)), cw, "or1", BOOLEAN_TYPE)
    visitExpressionMethod(OrExpression(BoolValue(true), BoolValue(false)), cw, "or2", BOOLEAN_TYPE)
    visitExpressionMethod(OrExpression(BoolValue(false), BoolValue(true)), cw, "or3", BOOLEAN_TYPE)
    visitExpressionMethod(OrExpression(BoolValue(false), BoolValue(false)), cw, "or4", BOOLEAN_TYPE)

    cw.visitEnd()

    val b = cw.toByteArray()

    val stubClassLoader = new StubClassLoader()
    val c = stubClassLoader.getClass("test", b)

    assert(true == c.getDeclaredMethod("or1").invoke(null))
    assert(true == c.getDeclaredMethod("or2").invoke(null))
    assert(true == c.getDeclaredMethod("or3").invoke(null))
    assert(false == c.getDeclaredMethod("or4").invoke(null))
  }
  
  /*
   * Creates (or override) a class file
   * @param name name of the class file
   * @return the relative Path to the class file.
   */
  def createOutputFile(name: String) = {
    val base = Paths.get("target" + File.pathSeparator + "out")
    Files.createDirectories(base)
    val classFile = Paths.get("target" + File.pathSeparator +  "out" + File.pathSeparator + name + ".class")
    if(Files.exists(classFile)) {
      Files.delete(classFile)
    }
    Files.createFile(classFile)
  }

  /**
   * Create a method with an expression that returns it's result
   */
  def visitExpressionMethod(expression: Expression, cw: ClassWriter, name: String, returnType: asm.Type): Unit = {
    val codeGen = JVMCodeGenerator

    val mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, name, "()" + returnType.getDescriptor(), null, null)
    mv.visitCode()
    codeGen.generateExpression(expression, mv, null)

    mv.visitInsn(returnType match {
      case BOOLEAN_TYPE => IRETURN
      case CHAR_TYPE => IRETURN
      case INT_TYPE => IRETURN
      case DOUBLE_TYPE => DRETURN
      case FLOAT_TYPE => FRETURN
      case VOID_TYPE => RETURN
      // FIXME: change _ to a matcher for OBJECT_TYPE
      case _ => ARETURN
    })

    mv.visitMaxs(0, 0) 
    mv.visitEnd()
  }
}
