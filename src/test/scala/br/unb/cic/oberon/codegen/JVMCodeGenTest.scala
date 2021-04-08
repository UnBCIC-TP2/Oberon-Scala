package br.unb.cic.oberon.codegen

import br.unb.cic.oberon.parser.ScalaParser
import org.scalatest.funsuite.AnyFunSuite

import java.io.FileOutputStream
import java.nio.file.{Files, Paths}
import java.util.Base64

class JVMCodeGenTest extends AnyFunSuite {

  test("Generate code with fields") {
    val path = Paths.get(getClass.getClassLoader.getResource("simple/simple02.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")
    assert(module.variables.size == 2)

    val codeGen = JVMCodeGenerator

    val byteCodeAsString = codeGen.generateCode(module)

    val out = new FileOutputStream(createOutputFile(module.name).toFile)

    out.write(Base64.getDecoder.decode(byteCodeAsString))

    //TODO: seria interessante verificar algumas propriedades do
    //      arquivo gerado.
  }


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
