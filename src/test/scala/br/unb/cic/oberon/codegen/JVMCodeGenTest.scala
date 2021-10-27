package br.unb.cic.oberon.codegen

import br.unb.cic.oberon.parser.ScalaParser
import org.scalatest.funsuite.AnyFunSuite

import java.io.FileOutputStream
import java.nio.file.{Files, Paths}
import java.util.Base64

import jdk.internal.org.objectweb.asm.Opcodes._
import jdk.internal.org.objectweb.asm._
import java.io.File

import br.unb.cic.oberon.interpreter._
import br.unb.cic.oberon.ast._
import java.io.PrintStream

class JVMCodeGenTest extends AnyFunSuite {

  test("Generate code with fields of simple01.oberon") {
    val path = Paths.get(getClass.getClassLoader.getResource("simple/simple01.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule1")
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

    assert(module.name == "SimpleModule2")
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

    assert(module.name == "SimpleModule3")
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

    assert(module.name == "SimpleModule4")
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

    assert(module.name == "SimpleModule5")
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

    assert(module.name == "SimpleModule6")
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

    assert(module.name == "SimpleModule7")
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

    assert(module.name == "SimpleModule8")
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

    assert(module.name == "SimpleModule9")
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

    assert(module.name == "SimpleModule10")
    assert(module.variables.size == 1)
    assert(module.constants.size == 0)

    val codeGen = JVMCodeGenerator

    val byteCodeAsString = codeGen.generateCode(module)

    val out = new FileOutputStream(createOutputFile(module.name).toFile)

    out.write(Base64.getDecoder.decode(byteCodeAsString))

    val cr = new ClassReader(Base64.getDecoder.decode(byteCodeAsString));

    val v = new ClassVisitorTest(ASM4);

    cr.accept(v, 0);

    assert(2 == v.numberOfTotalFields);
    assert(0 == v.numberOfIntegerConstants);
    assert(0 == v.numberOfIntegerVariables);
    assert(0 == v.numberOfBooleanVariables);
    assert(0 == v.numberOfBooleanConstants);
  }

  test("Generate bytecode from records"){

    val path = Paths.get(getClass.getClassLoader.getResource("records/record_1_simples.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "simple_record")
    assert(module.variables.size == 2)
    assert(module.constants.size == 0)
    assert(module.userTypes.size == 2)

    val codeGen = GenerateClassFromRecord
    // var recordClassString = new String;

    // val record_name_and_bytecode
    // usa pattern matching para obter um record 
    //  (o único record do arquivo oberon, ou o último, caso tenha mais de um record)
    // module.userTypes.foreach((userDefinedTypes : UserDefinedType) =>
    //   userDefinedTypes match {
    //     case r: RecordType => val record_name_and_bytecode = codeGen.generateCode(r);
    //     case a: ArrayType => null;
    //   }
    // )

    var record_name_and_bytecode = codeGen.generateCode(module.userTypes.head.asInstanceOf[RecordType])

    val bytecode = record_name_and_bytecode._2

    val out = new FileOutputStream(createOutputFile(record_name_and_bytecode._1).toFile)

    out.write(bytecode)

    val cr = new ClassReader(bytecode);

    val v = new ClassVisitorTest(ASM4);

    // record dentro de record
    // fazer mais testes (checar differentes atributos)
    // resolver target;out -> done

    cr.accept(v, 0);

    assert(3 == v.numberOfTotalFields);
    assert(2 == v.numberOfIntegerVariables);
    assert(1 == v.numberOfBooleanVariables);

  }
  /*
   * Creates (or override) a class file
   * @param name name of the class file
   * @return the relative Path to the class file.
   */
  def createOutputFile(name: String) = {
    val base = Paths.get("target" + File.separator + "out")
    Files.createDirectories(base)
    val classFile = Paths.get("target" + File.separator +  "out" + File.separator + name + ".class")
    if(Files.exists(classFile)) {
      Files.delete(classFile)
    }
    Files.createFile(classFile)
  }
}
