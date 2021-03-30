package br.unb.cic.oberon.codegen

import br.unb.cic.oberon.parser.ScalaParser

object Main extends App {

	
	// Testando JVMCodeGenerator
	val module = ScalaParser.parse("MODULE SimpleModule;") // A principio não está sendo utilizado
	val codeGen = PaigeBasedGenerator()
	val generatedCCode = codeGen.generateCode(module)
	println(generatedCCode)

	// Testando CCodeGenerator
	// val CCmodule = ScalaParser.parse("MODULE SimpleModule; END SimpleModule .")
	// val CcodeGen = PaigesBasedGenerator()
	// val generatedCCodeC = CcodeGen.generateCode(CCmodule)
	// println(generatedCCodeC)

  	// println("Hello, World!")
}