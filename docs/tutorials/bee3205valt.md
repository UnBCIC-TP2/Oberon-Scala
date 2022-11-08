# Teste bee3205: Nasty Hacks (versão alterada)
<b>Situação:</b> Sucesso

<b>Objetivo:</b> Testar forEach com valores inteiros.

<b>Funcionalidades usadas:</b> forEach, inicialização de arrays, FLOOR, INC.

## Descrição no beecrowd

<b>Link:</b> [3205 - Nasty Hacks (versão alterada)](https://www.beecrowd.com.br/judge/en/problems/view/3205)

<b>Problema:</b> Leia 3 linhas com 3 números inteiros em cada. A primeira linha representa o custo para anunciar o i-ézimo produto, a segunda linha representa o dinheiro ganho
pela venda do i-ézimo produto e a terceira linha representa o custo para o i-ézimo produto. Imprima na tela o lucro no final da venda de todos os produtos.

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
0 100 100<br>
100 30 70<br>
70 30 20<br>
</p>
</td>
<td>
<p>
-20
</td>
</tr>
</tbody>
</table>

## Teste implementado

```
MODULE bee3205valt;

VAR
    a : ARRAY 3 OF INTEGER;
    b : ARRAY 3 OF INTEGER;
    c : ARRAY 3 OF INTEGER;
    ans : ARRAY 3 OF INTEGER;
    i, temp, temp2, answer : INTEGER;

BEGIN
    a[0] := 0;
    a[1] := 100;
    a[2] := 100;

    b[0] := 100;
    b[1] := 130;
    b[2] := 70;

    c[0] := 70;
    c[1] := 30;
    c[2] := 20;

    ans[0] := 0;
    ans[1] := 0;
    ans[2] := 0;

    i := 0;
    temp := 0;
    answer := 0;

    FOREACH v IN a
        temp := b[i] - c[i];
        temp2 := temp - v;
        answer := answer + temp2;
        INC(i)
    END
END

END bee3205valt.
```

<details>
<p>
<summary><b><u>Teste unitário (em Scala)</u></b></summary>
<pre>
<code>
    test(testName = "Testing altered version of bee3205: Sample Test 1"){
    val module = ScalaParser.parseResource("stmts/bee3205_NastyHacks_v_alt.oberon")

    assert(module.name == "bee3205valt")

    module.accept(interpreter)

    assert(interpreter.env.lookup("answer") == Some(IntValue(-20)))
  }
</code>
</pre>
</details>

## Funcionalidades testadas
### Inicialização de arrays

Documentação já feita no teste [bee1018](bee1018.md#arrays).

### forEach

Documentação já feita no teste [bee1018](bee1018.md#foreach).

### INC

Documentação já feita no teste [bee1018](bee1018.md#inc).
