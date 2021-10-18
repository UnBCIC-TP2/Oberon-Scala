package br.unb.cic.oberon.codegen

import br.unb.cic.oberon.ast.{Constant, OberonModule, Procedure, VariableDeclaration}
import br.unb.cic.oberon.parser.ScalaParser

import java.io.FileOutputStream
import java.nio.file.{Files, Paths}
import java.util.Base64
import jdk.internal.org.objectweb.asm.Opcodes._
import jdk.internal.org.objectweb.asm._
import java.io.File


/**
 * The base class for code generation test.
 * You should extend from this class to get base testing methods.
 */
class JVMCodeGenTest extends CodeGenTest {
  /**
   * Gets Oberon module from given oberon file.
   *
   * @param filePath oberon file target path of the current test.
   *
   * @return the resulting module for the given target oberon file.
   */
  def getOberonModuleFromFile(filePath: String): OberonModule = {
    val path = Paths.get(getClass.getClassLoader.getResource(filePath).toURI)
    assert(path != null)
    ScalaParser.parse(String.join("\n", Files.readAllLines(path)))
  }

  /**
   * Creates (or override) a class file
   * @param name name of the class file
   * @return the relative Path to the class file.
   */
  def createOutputFile(name: String) = {
    Files.createDirectories(Paths.get("target" + File.pathSeparator + "out"))
    val classFile = Paths.get("target" + File.pathSeparator +  "out" + File.pathSeparator + name + ".class")
    if(Files.exists(classFile)) Files.delete(classFile)
    Files.createFile(classFile)
  }

  def getVisitModule(module: OberonModule): ClassVisitorTest = {
    val byteCodeAsString = JVMCodeGenerator.generateCode(module)

    val out = new FileOutputStream(createOutputFile(module.name).toFile)
    out.write(Base64.getDecoder.decode(byteCodeAsString))

    val cr = new ClassReader(Base64.getDecoder.decode(byteCodeAsString));
    val v = new ClassVisitorTest(ASM4)
//    val s = new String(Base64.getDecoder.decode(byteCodeAsString), StandardCharsets.UTF_8)
    cr.accept(v, 0)
    v
  }

  override def assertWithJVMCodeGenerated(codeTest: ModuleTest): Unit = {
    
		val module = codeTest.module
    
		val visitor = getVisitModule(codeTest.module)

    assert(module.name == codeTest.moduleName)

    assert(module.constants.size == visitor.constants.size)
    assert(module.variables.size == visitor.variables.size)
    assert(module.procedures.size == visitor.procedures.size)

    module.constants.foreach((c: Constant) => assert(visitor.constants.contains(c.name)))
    module.variables.foreach((v: VariableDeclaration) => assert(visitor.variables.contains(v.name)))
    module.procedures.foreach((p: Procedure) => assert(visitor.procedures.contains(p.name)))
  }

  override def testGenerator(testName: String, codeTest: ModuleTest): Unit = {
    codeTest.module = getOberonModuleFromFile(codeTest.filePath)

    test(testName) {
      assertWithJVMCodeGenerated(codeTest)
    }

  }
}


class SimpleModule01CodeGenTest extends JVMCodeGenTest {
  val codeTest = new ModuleTest("simple/simple01.oberon", "SimpleModule1")

  testGenerator("Generate code with fields of simple01.oberon", codeTest)
}


class SimpleModule02CodeGenTest extends JVMCodeGenTest {
  val codeTest = new ModuleTest("simple/simple02.oberon", "SimpleModule2")

  testGenerator("Generate code with fields of simple02.oberon", codeTest)
}


class SimpleModule03CodeGenTest extends JVMCodeGenTest {
  val codeTest = new ModuleTest("simple/simple03.oberon", "SimpleModule3")

  testGenerator("Generate code with fields of simple03.oberon", codeTest)
}


class SimpleModule04CodeGenTest extends JVMCodeGenTest {
  val codeTest = new ModuleTest("simple/simple04.oberon", "SimpleModule4")

  testGenerator("Generate code of simple04.oberon", codeTest)
}


class SimpleModule05CodeGenTest extends JVMCodeGenTest {
  val codeTest = new ModuleTest("simple/simple05.oberon", "SimpleModule5")

  testGenerator("Generate code of simple05.oberon", codeTest)
}


class SimpleModule06CodeGenTest extends JVMCodeGenTest {
  val codeTest = new ModuleTest("simple/simple06.oberon", "SimpleModule6")

  testGenerator("Generate code of simple06.oberon", codeTest)
}

class SimpleModule07CodeGenTest extends JVMCodeGenTest {
  val codeTest = new ModuleTest("simple/simple07.oberon", "SimpleModule7")

  testGenerator("Generate code of simple07.oberon", codeTest)
}

class SimpleModule08CodeGenTest extends JVMCodeGenTest {
  val codeTest = new ModuleTest("simple/simple08.oberon", "SimpleModule8")

  testGenerator("Generate code of simple08.oberon", codeTest)
}

class SimpleModule09CodeGenTest extends JVMCodeGenTest {
  val codeTest = new ModuleTest("simple/simple09.oberon", "SimpleModule9")

  testGenerator("Generate code of simple09.oberon", codeTest)
}

class SimpleModule10CodeGenTest extends JVMCodeGenTest {
  val codeTest = new ModuleTest("simple/simple10.oberon", "SimpleModule10")

  testGenerator("Generate code of simple10.oberon", codeTest)
}

class ComplexModuleCodeGenTest extends JVMCodeGenTest {
  val codeTest = new ModuleTest("procedures/procedure05.oberon", "ComplexModule")

  testGenerator("Generate code of procedure05.oberon", codeTest)
}

