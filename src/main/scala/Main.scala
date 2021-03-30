package br.unb.cic.oberon.codegen

import java.nio.file.{Files, Paths}
import br.unb.cic.oberon.parser.ScalaParser

object Main extends App {
	// val oberonPath = Paths.get(
	//     getClass.getClassLoader
	//       .getResource(s"stmts/stmt01.oberon")
	//       .getFile
	//       .replaceFirst("\\/(.:\\/)", "$1")
	// )
	// assert(oberonPath != null)

	// val oberonContent = String.join("\n", Files.readAllLines(oberonPath))
	val module = ScalaParser.parse("MODULE SimpleModule;")
	val codeGen = PaigeBasedGenerator()
	val generatedCCode = codeGen.generateCode(module)
	println(generatedCCode)
  	println("Hello, World!")
}