# Teste bee1038: Snack (utilizando INTEGER ARRAY)
<b>Situação:</b> Sucesso

<b>Objetivo:</b> Testar manipulação de arrays de inteiro.

<b>Funcionalidades usadas:</b> inicialização e aritmética de array.

## Descrição no beecrowd

<b>Link:</b> [1018 - Cédulas](https://www.beecrowd.com.br/judge/en/problems/view/1038)

<b>Problema:</b> The input file contains two integer numbers X and Y. X is the product code according to the array in the code and Y is the quantity of this item. The 
output is the value to be paid.

<table>
<thead>
<tr>
  <td><b>Exemplo de Entrada</b></td>
  <td><b>Exemplo de Saída</b></td>
</tr>
</thead>
<tbody>
<tr>
<td class="division">
<p>
2 3</p>
</td>
<td>
<p>
12 </p>
</td>
</tr>
</tbody>
</table>

## Teste implementado

```
MODULE bee1038int;

TYPE
    intArray = ARRAY 5 OF INTEGER

VAR
    banknotesValues: intArray;
    value: INTEGER;
    x, y: INTEGER;

BEGIN
    x := 1;
    y := 3;

    banknotesValues[0] := 2;
    banknotesValues[1] := 3;
    banknotesValues[2] := 4;
    banknotesValues[3] := 5;
    banknotesValues[4] := 6;

    value := banknotesValues[x] * y

END

END bee1038int.
```

<details>
<p>
<summary><b><u>Teste unitário (em Scala)</u></b></summary>
<pre>
<code>
  test(testName = "Testing bee1038 with INTEGER: Sample Test 1"){
    val module = ScalaParser.parseResource("stmts/bee1038_Snack_int.oberon")

    assert(module.name == "bee1038int")

    module.accept(interpreter)

    assert(interpreter.env.lookup("value") == Some(IntValue(9)))
  }
</code>
</pre>
</details>

## Funcionalidades testadas
### Inicialização de arrays de inteiro

Documentação já feita no teste [bee1018](bee1018.md#arrays).

### Aritmética com array de inteiro

Nesse teste utilizou-se multiplicação de elementos do array.
<br>"value := banknotesValues[x] * y"
