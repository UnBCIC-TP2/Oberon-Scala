# Teste bee1038: Snack (utilizando REAL ARRAY)
<b>Situação:</b> Sucesso

<b>Objetivo:</b> Testar manipulação de arrays de real.

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
1 2</p>
</td>
<td>
<p>
7 </p>
</td>
</tr>
</tbody>
</table>

## Teste implementado

```
MODULE bee1038real;

TYPE
    realArray = ARRAY 5 OF REAL

VAR
    banknotesValues: realArray;
    value: INTEGER;
    x, y: INTEGER;

BEGIN
    x := 1;
    y := 3;

    banknotesValues[0] := 2.5;
    banknotesValues[1] := 3.5;
    banknotesValues[2] := 4.5;
    banknotesValues[3] := 5.5;
    banknotesValues[4] := 6.5;

    value := banknotesValues[x] * y

END

END bee1038real.
```

<details>
<p>
<summary><b><u>Teste unitário (em Scala)</u></b></summary>
<pre>
<code>
  test(testName = "Testing bee1038 with REAL: Sample Test 1"){
    val module = ScalaParser.parseResource("stmts/bee1038_Snack_real.oberon")

    assert(module.name == "bee1038real")

    module.accept(interpreter)

    assert(interpreter.env.lookup("value") == Some(RealValue(10.5)))
  }
</code>
</pre>
</details>

## Funcionalidades testadas
### Inicialização de arrays de real

Documentação já feita no teste [bee1018](bee1018.md#arrays).

### Aritmética com array de real

Nesse teste utilizou-se multiplicação de elementos do array.
<br>"value := banknotesValues[x] * y"
