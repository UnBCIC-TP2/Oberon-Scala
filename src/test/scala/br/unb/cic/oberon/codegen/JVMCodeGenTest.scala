package br.unb.cic.oberon.codegen

import br.unb.cic.oberon.parser.ScalaParser
import org.scalatest.funsuite.AnyFunSuite

import java.io.FileOutputStream
import java.nio.file.{Files, Paths}
import java.util.Base64

import jdk.internal.org.objectweb.asm.Opcodes._
import jdk.internal.org.objectweb.asm._

class JVMCodeGenTest extends AnyFunSuite {

  test("Generate code with fields") {
    val path = Paths.get(getClass.getClassLoader.getResource("simple/simple02.oberon").getFile)

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

 // test("Generate code of simple 03 oberon") {
 //    val path = Paths.get(getClass.getClassLoader.getResource("simple/simple03.oberon").getFile)

 //    assert(path != null)

 //    val content = String.join("\n", Files.readAllLines(path))
 //    val module = ScalaParser.parse(content)

 //    assert(module.name == "SimpleModule")
 //    assert(module.variables.size == 2)
 //    assert(module.constants.size == 3)

 //    val codeGen = JVMCodeGenerator

 //    val byteCodeAsString = codeGen.generateCode(module)

 //    val out = new FileOutputStream(createOutputFile(module.name).toFile)

 //    out.write(Base64.getDecoder.decode(byteCodeAsString))

 //    val cr = new ClassReader(Base64.getDecoder.decode(byteCodeAsString));

 //    val v = new ClassVisitorTest(ASM4);

 //    cr.accept(v, 0);

 //    assert(5 == v.numberOfTotalFields);
 //    assert(2 == v.numberOfIntegerConstants);
 //    assert(1 == v.numberOfIntegerVariables);
 //    assert(1 == v.numberOfBooleanVariables);
 //    assert(1 == v.numberOfBooleanConstants);

 //  }

 //  test("Generate code of simple 04 oberon") {
 //    val path = Paths.get(getClass.getClassLoader.getResource("simple/simple04.oberon").getFile)

 //    assert(path != null)

 //    val content = String.join("\n", Files.readAllLines(path))
 //    val module = ScalaParser.parse(content)

 //    assert(module.name == "SimpleModule")
 //    assert(module.variables.size == 2)
 //    assert(module.constants.size == 3)

 //    val codeGen = JVMCodeGenerator

 //    val byteCodeAsString = codeGen.generateCode(module)

 //    val out = new FileOutputStream(createOutputFile(module.name).toFile)

 //    out.write(Base64.getDecoder.decode(byteCodeAsString))

 //    val cr = new ClassReader(Base64.getDecoder.decode(byteCodeAsString));

 //    val v = new ClassVisitorTest(ASM4);

 //    cr.accept(v, 0);

 //    assert(5 == v.numberOfTotalFields);
 //    assert(3 == v.numberOfIntegerConstants);
 //    assert(1 == v.numberOfIntegerVariables);
 //    assert(1 == v.numberOfBooleanVariables);
 //    assert(0 == v.numberOfBooleanConstants);

 //  }


  /*
   * Creates (or override) a class file
   * @param name name of the class file
   * @return the relative Path to the class file.
   */
  def createOutputFile(name: String) = {
    val base = Paths.get("target/out/")
    Files.createDirectories(base)
    val classFile = Paths.get("target/out/" + name + ".class")
    if(Files.exists(classFile)) {
      Files.delete(classFile)
    }
    Files.createFile(classFile)
  }
}
