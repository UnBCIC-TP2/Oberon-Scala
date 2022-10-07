# Teste stringToInt
<b>Situação:</b> Sucesso

<b>Objetivo:</b> Tranformar um valor String em um valor Integer.

<b>Funcionalidades usadas:</b> stringToInt.

## Descrição do problema

Com a possibilidade de entrada de usuário por meio do READFILE(), necessitamos do retorno das variavéis se String para Integer.
src\main\scala\br\unb\cic\oberon\stdlib\StandardLibrary.scala - File com a implementação em Scala.
src\test\resources\stdlib\STRINGTOINTTest.oberon - Arquivo de teste.

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
  y := STRINGTOINT(x);
  z := STRINGTOINT(w)
END
END STRINGTOINTTest.
```

<details>
<p>
<summary><b><u>Teste unitário (em Scala)</u></b></summary>
<pre>
<code>
   test(testName = "Test for the STRINGTOINT function"){
    val module = ScalaParser.parseResource("stdlib/STRINGTOINTTest.oberon")

    assert(module.name == "STRINGTOINTTest")


    module.accept(interpreter)

    assert(interpreter.env.lookup("y") == Some(IntValue(-8)))
    assert(interpreter.env.lookup("z") == Some(IntValue(2)))
  }
</code>
</pre>
</details>

## Funcionalidades testadas
### stringToInt

Documentação já feita no teste [bee1049](bee1018user.md#stringtoint).
