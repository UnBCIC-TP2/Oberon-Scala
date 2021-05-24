package br.unb.cic.oberon.codegen

import java.io._
import java.nio.file.{Files, Paths}
import br.unb.cic.oberon.ast._
import br.unb.cic.oberon.parser.ScalaParser
import org.scalatest.funsuite.AnyFunSuite

class CCodeGenTest extends AnyFunSuite {

    // Tests for C code generator for stmt01.oberon - stmt16.oberon
    for (i <- (1 to 16)) {
        val stmtNumber = "%02d".format(i)

        test(s"Testing C generator for stmt$stmtNumber") {
            val oberonPath = Paths.get(
                getClass.getClassLoader
                .getResource(s"stmts/stmt$stmtNumber.oberon")
                .getFile
                .replaceFirst("\\/(.:\\/)", "$1")
            )
            assert(oberonPath != null)

            val oberonContent = String.join("\n", Files.readAllLines(oberonPath))
            val module = ScalaParser.parse(oberonContent)
            val codeGen = PaigesBasedGenerator()
            val generatedCCode = codeGen.generateCode(module)

            val cPath = Paths.get(
                getClass.getClassLoader
                .getResource(s"cCode/stmts/stmt$stmtNumber.c")
                .getFile
                .replaceFirst("\\/(.:\\/)", "$1")
            )
            assert(cPath != null)

            val cCode = String.join("\n", Files.readAllLines(cPath))
            
            assert(generatedCCode == cCode)
        }
    }


    // Tests for C code generator for procedure01.oberon - procedure04.oberon
    for (i <- 1 to 4) {
        val procedureNumber = "%02d".format(i)

        test(s"Testing C generator for procedure$procedureNumber") {
            val oberonPath = Paths.get(
                getClass.getClassLoader
                .getResource(s"procedures/procedure$procedureNumber.oberon")
                .getFile
                .replaceFirst("\\/(.:\\/)", "$1")
            )
            assert(oberonPath != null)

            val oberonContent = String.join("\n", Files.readAllLines(oberonPath))
            val module = ScalaParser.parse(oberonContent)
            val codeGen = PaigesBasedGenerator()
            val generatedCCode = codeGen.generateCode(module)

            val cPath = Paths.get(
                getClass.getClassLoader
                .getResource(s"cCode/procedures/procedure$procedureNumber.c")
                .getFile
                .replaceFirst("\\/(.:\\/)", "$1")
            )
            assert(cPath != null)

            val cCode = String.join("\n", Files.readAllLines(cPath))
            assert(generatedCCode == cCode)
        }
    }


    // Tests for C code generator for interpreter_factorial01.oberon - interpreter_factorial03.oberon
    for (i <- 1 to 3) {
        val procedureNumber = "%02d".format(i)

        test(s"Testing C generator for interpreter_factorial$procedureNumber") {
            val oberonPath = Paths.get(
                getClass.getClassLoader.getResource(
                s"procedures/interpreter_factorial$procedureNumber.oberon")
                .getFile
                .replaceFirst("\\/(.:\\/)", "$1")
            )
            assert(oberonPath != null)

            val oberonContent = String.join("\n", Files.readAllLines(oberonPath))
            val module = ScalaParser.parse(oberonContent)
            val codeGen = PaigesBasedGenerator()
            val generatedCCode = codeGen.generateCode(module)

            val cPath = Paths.get(
              getClass.getClassLoader
                .getResource(
                  s"cCode/procedures/interpreter_factorial$procedureNumber.c"
                )
                .getFile
                .replaceFirst("\\/(.:\\/)", "$1")
            )
            assert(cPath != null)

            val cCode = String.join("\n", Files.readAllLines(cPath))
            assert(generatedCCode == cCode)
        }
    }


    // Test for C code generator for interpreter_fibonacci01.oberon
    test(s"Testing C generator for interpreter_fibonacci01") {
        val oberonPath = Paths.get(
            getClass.getClassLoader
                .getResource(s"procedures/interpreter_fibonacci01.oberon")
                .getFile
                .replaceFirst("\\/(.:\\/)", "$1")
        )
        assert(oberonPath != null)

        val oberonContent = String.join("\n", Files.readAllLines(oberonPath))
        val module = ScalaParser.parse(oberonContent)
        val codeGen = PaigesBasedGenerator()
        val generatedCCode = codeGen.generateCode(module)

        val cPath = Paths.get(
            getClass.getClassLoader
                .getResource(
                    s"cCode/procedures/interpreter_fibonacci01.c"
                )
                .getFile
                .replaceFirst("\\/(.:\\/)", "$1")
        )
        assert(cPath != null)

        val cCode = String.join("\n", Files.readAllLines(cPath))
        assert(generatedCCode == cCode)
    }

    test("Testing C generator for stmt01 with 4 spaces indent ") {
        val oberonPath = Paths.get(
            getClass.getClassLoader
                .getResource("stmts/stmt01.oberon")
                .getFile
                .replaceFirst("\\/(.:\\/)", "$1")
        )
        assert(oberonPath != null)

        val oberonContent = String.join("\n", Files.readAllLines(oberonPath))
        val module = ScalaParser.parse(oberonContent)
        val codeGen = PaigesBasedGenerator(4)
        val generatedCCode = codeGen.generateCode(module)

        val cPath = Paths.get(
          getClass.getClassLoader
              .getResource("cCode/stmts/stmt01_4spaces.c")
              .getFile
              .replaceFirst("\\/(.:\\/)", "$1")
        )
        assert(cPath != null)
        val cCode = String.join("\n", Files.readAllLines(cPath))
        assert(generatedCCode == cCode)
    }

    // TODO:  Os RepeatUntil não estão gerando o "break" para sair do while, no código em C.
    ignore("First RepeatUntil Test") {
        val oberonPath = Paths.get(
            getClass.getClassLoader
            .getResource("stmts/repeatuntil.oberon")
            .getFile
            .replaceFirst("\\/(.:\\/)", "$1")
        )
        assert(oberonPath != null)

        val oberonContent = String.join("\n", Files.readAllLines(oberonPath))
        val module = ScalaParser.parse(oberonContent)
        val codeGen = PaigesBasedGenerator()
        val generatedCCode = codeGen.generateCode(module)

        val cPath = Paths.get(
            getClass.getClassLoader.getResource("cCode/stmts/repeatuntil.c")
            .getFile
            .replaceFirst("\\/(.:\\/)", "$1")
        )
        assert(cPath != null)
        val cCode = String.join("\n", Files.readAllLines(cPath))

        assert(generatedCCode == cCode)
    }

    // TODO
    ignore("RepeatUntil Test with just one loop") {
        val oberonPath = Paths.get(
            getClass.getClassLoader.getResource("stmts/repeatuntil01.oberon")
            .getFile
            .replaceFirst("\\/(.:\\/)", "$1")
        )
        assert(oberonPath != null)

        val oberonContent = String.join("\n", Files.readAllLines(oberonPath))
        val module = ScalaParser.parse(oberonContent)
        val codeGen = PaigesBasedGenerator()
        val generatedCCode = codeGen.generateCode(module)

        val cPath = Paths.get(
            getClass.getClassLoader
            .getResource("cCode/stmts/repeatuntil01.c")
            .getFile
            .replaceFirst("\\/(.:\\/)", "$1")
        )
        assert(cPath != null)
        val cCode = String.join("\n", Files.readAllLines(cPath))

        assert(generatedCCode == cCode)
    }

    // TODO
    ignore("RepeatUntil Nested") {
        val oberonPath = Paths.get(
            getClass.getClassLoader
            .getResource("stmts/repeatuntil02.oberon")
            .getFile
            .replaceFirst("\\/(.:\\/)", "$1")
        )
        assert(oberonPath != null)

        val oberonContent = String.join("\n", Files.readAllLines(oberonPath))
        val module = ScalaParser.parse(oberonContent)
        val codeGen = PaigesBasedGenerator()
        val generatedCCode = codeGen.generateCode(module)

        val cPath = Paths.get(
            getClass.getClassLoader
            .getResource("cCode/stmts/repeatuntil02.c")
            .getFile
            .replaceFirst("\\/(.:\\/)", "$1")
        )
        assert(cPath != null)
        val cCode = String.join("\n", Files.readAllLines(cPath))

        assert(generatedCCode == cCode)
    }

    // TODO
    ignore("RepeatUntil Compound Exit Condition") {
        val oberonPath = Paths.get(
            getClass.getClassLoader
            .getResource("stmts/repeatuntil03.oberon")
            .getFile
            .replaceFirst("\\/(.:\\/)", "$1")
        )
        assert(oberonPath != null)

        val oberonContent = String.join("\n", Files.readAllLines(oberonPath))
        val module = ScalaParser.parse(oberonContent)
        val codeGen = PaigesBasedGenerator()
        val generatedCCode = codeGen.generateCode(module)

        val cPath = Paths.get(
            getClass.getClassLoader
            .getResource("cCode/stmts/repeatuntil03.c")
            .getFile
            .replaceFirst("\\/(.:\\/)", "$1")
        )
        assert(cPath != null)
        val cCode = String.join("\n", Files.readAllLines(cPath))

        assert(generatedCCode == cCode)
    }

    // TODO
      ignore("RepeatUntil In Procedure") {
        val oberonPath = Paths.get(
            getClass.getClassLoader
            .getResource("stmts/repeatuntil04.oberon")
            .getFile
            .replaceFirst("\\/(.:\\/)", "$1")

        )
        assert(oberonPath != null)

        val oberonContent = String.join("\n", Files.readAllLines(oberonPath))
        val module = ScalaParser.parse(oberonContent)
        val codeGen = PaigesBasedGenerator()
        val generatedCCode = codeGen.generateCode(module)

        val cPath = Paths.get(
            getClass.getClassLoader
            .getResource("cCode/stmts/repeatuntil04.c")
            .getFile
            .replaceFirst("\\/(.:\\/)", "$1")
        )
        assert(cPath != null)
        val cCode = String.join("\n", Files.readAllLines(cPath))

        assert(generatedCCode == cCode)
    }

    // Tests for IfElseIf 
    test("Testing C generator for stmt30") {
        val oberonPath = Paths.get(
            getClass.getClassLoader
            .getResource("stmts/stmt30.oberon")
            .getFile
            .replace("/C:/", "C:/")
        )
        assert(oberonPath != null)

        val oberonContent = String.join("\n", Files.readAllLines(oberonPath))
        val module = ScalaParser.parse(oberonContent)
        val codeGen = PaigesBasedGenerator()
        val generatedCCode = codeGen.generateCode(module)

        val cPath = Paths.get(
            getClass.getClassLoader
            .getResource("cCode/stmts/stmt17.c")
            .getFile
            .replace("/C:/", "C:/")
        )
        assert(cPath != null)
        val cCode = String.join("\n", Files.readAllLines(cPath))
        assert(generatedCCode == cCode)
    }

    // TODO: esse teste pulou na minha máquina, não consegui gerar o arquivo .c
    test("Testing C generator for ifelseif_stmt31") {
        val oberonPath = Paths.get(
            getClass.getClassLoader
            .getResource("stmts/ifelseif_stmt31.oberon")
            .getFile
            .replace("/C:/", "C:/")
        )
        assert(oberonPath != null)

        val oberonContent = String.join("\n", Files.readAllLines(oberonPath))
        val module = ScalaParser.parse(oberonContent)
        val codeGen = PaigesBasedGenerator()
        val generatedCCode = codeGen.generateCode(module)

        val cPath = Paths.get(
            getClass.getClassLoader
            .getResource("cCode/stmts/stmt18.c")
            .getFile
            .replace("/C:/", "C:/")
        )
        assert(cPath != null)
        val cCode = String.join("\n", Files.readAllLines(cPath))
/*
        val pw = new PrintWriter(new File(s"hello18.txt" ))
        pw.write(generatedCCode)
        pw.close
*/
        assert(generatedCCode == cCode)
    }

    test("Testing C generator for stmt32") {
        val oberonPath = Paths.get(
            getClass.getClassLoader
            .getResource("stmts/ifelseif_stmt32.oberon")
            .getFile
            .replace("/C:/", "C:/")
        )
        assert(oberonPath != null)

        val oberonContent = String.join("\n", Files.readAllLines(oberonPath))
        val module = ScalaParser.parse(oberonContent)
        val codeGen = PaigesBasedGenerator()
        val generatedCCode = codeGen.generateCode(module)

        val cPath = Paths.get(
            getClass.getClassLoader
            .getResource("cCode/stmts/stmt19.c")
            .getFile
            .replace("/C:/", "C:/")
        )
        assert(cPath != null)
        val cCode = String.join("\n", Files.readAllLines(cPath))
        assert(generatedCCode == cCode)
    }

    test("Testing C generator for stmt33") {
        val oberonPath = Paths.get(
            getClass.getClassLoader
            .getResource("stmts/ifelseif_stmt33.oberon")
            .getFile
            .replace("/C:/", "C:/")
        )
        assert(oberonPath != null)

        val oberonContent = String.join("\n", Files.readAllLines(oberonPath))
        val module = ScalaParser.parse(oberonContent)
        val codeGen = PaigesBasedGenerator()
        val generatedCCode = codeGen.generateCode(module)

        val cPath = Paths.get(
            getClass.getClassLoader
            .getResource("cCode/stmts/stmt20.c")
            .getFile
            .replace("/C:/", "C:/")
        )
        assert(cPath != null)
        val cCode = String.join("\n", Files.readAllLines(cPath))
        assert(generatedCCode == cCode)
    }

    //TODO
    ignore("Testing C generator for stmt34") {
        val oberonPath = Paths.get(
            getClass.getClassLoader
            .getResource("stmts/stmt34.oberon")
            .getFile
            .replace("/C:/", "C:/")
        )
        assert(oberonPath != null)

        val oberonContent = String.join("\n", Files.readAllLines(oberonPath))
        val module = ScalaParser.parse(oberonContent)
        val codeGen = PaigesBasedGenerator()
        val generatedCCode = codeGen.generateCode(module)

        val cPath = Paths.get(
            getClass.getClassLoader
            .getResource("cCode/stmts/stmt21.c")
            .getFile
            .replace("/C:/", "C:/")
        )
        assert(cPath != null)
        val cCode = String.join("\n", Files.readAllLines(cPath))
/*
        val pw = new PrintWriter(new File(s"hello21.txt" ))
        pw.write(generatedCCode)
        pw.close
*/
        assert(generatedCCode == cCode)
    }
}
