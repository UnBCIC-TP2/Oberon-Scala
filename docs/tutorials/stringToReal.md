# Teste stringToReal
<b>Situação:</b> Sucesso

<b>Objetivo:</b> Tranformar um valor String em um valor Real.

<b>Funcionalidades usadas:</b> stringToReal.

## Descrição do problema

Com a possibilidade de entrada de usuário por meio do READFILE(), necessitamos do retorno das variavéis de String para Real.
src\main\scala\br\unb\cic\oberon\stdlib\StandardLibrary.scala - File com a implementação em Scala.
src\test\resources\stdlib\STRINGTOREALTest.oberon - Arquivo de teste.

## Código-exemplo

```
MODULE STRINGTOINTTest;

VAR
  x : STRING;
  y : INTEGER;
  z : INTEGER;
  w : STRING;

BEGIN
  w := "2";
  x := "-8";
  y := STRINGTOREAL(x);
  z := STRINGTOREAL(w)
END
END STRINGTOINTTest.
```


<details>
<p>
<summary><b><u>Teste unitário (em Scala)</u></b></summary>
<pre>
<code>
  test(testName = "Test for the STRINGTOREAL function"){
    val module = ScalaParser.parseResource("stdlib/STRINGTOREALTest.oberon")

    assert(module.name == "STRINGTOREALTest")


    module.accept(interpreter)

    assert(interpreter.env.lookup("y") == Some(RealValue(-8.0)))
    assert(interpreter.env.lookup("z") == Some(RealValue(2.5)))
  }
</code>
</pre>
</details>

## Funcionalidades testadas
<a name="stringtoreal"></a>
### STRINGTOREAL

Função em Oberon, implementado em Scala, que retorna um Real a partir de uma String.
