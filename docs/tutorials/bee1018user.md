# Teste bee1018: Cédulas com User Input
<b>Situação:</b> Sucesso

<b>Objetivo:</b> Testar forEach com valor inteiro lido com READFILE() em um arquivo .txt.

<b>Funcionalidades usadas:</b> forEach, inicialização de arrays, FLOOR, INC, READFILE(), STRINGTOINT.

## Descrição no beecrowd

<b>Link:</b> [1018 - Cédulas](https://www.beecrowd.com.br/judge/pt/problems/view/1018)

<b>Problema:</b> Leia um valor inteiro. A seguir, calcule o menor número de notas possíveis (cédulas) no qual o valor pode ser decomposto. As notas consideradas são de 100, 50, 20, 10, 5, 2 e 1. A seguir mostre o valor lido e a relação de notas necessárias.

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
576</p>
</td>
<td>
<p>
576<br>
5 nota(s) de R$ 100,00<br>
1 nota(s) de R$ 50,00<br>
1 nota(s) de R$ 20,00<br>
0 nota(s) de R$ 10,00<br>
1 nota(s) de R$ 5,00<br>
0 nota(s) de R$ 2,00<br>
1 nota(s) de R$ 1,00</p>
</td>
</tr>
</tbody>
</table>

## Código-exemplo

```
MODULE BeeBanknoteIntUser;


TYPE
	intArray = ARRAY 7 OF INTEGER

VAR
	banknotesValues, banknotesNeeded: intArray;
	value, valueUserInt1, valueUserInt2: INTEGER;
	valueUser: STRING;
	i, v: INTEGER;

BEGIN
	value := 576.73;
	i := 0;

	valueUser := READFILE("src\test\resources\userInput\beecrowdint");
	
	valueUserInt1 := STRINGTOINT(valueUser);
	
	banknotesValues[0] := valueUserInt1;
	banknotesValues[1] := 50;
	banknotesValues[2] := 20;
	banknotesValues[3] := 10;
	banknotesValues[4] := 5;
	banknotesValues[5] := 2;
	banknotesValues[6] := 1;

	FOREACH v IN banknotesValues
		banknotesNeeded[i] := FLOOR(value/v);
		value := value - v*banknotesNeeded[i];
		INC(i)
	END
END

END BeeBanknoteInt.
```

<details>
<p>
<summary><b><u>Teste unitário (em Scala)</u></b></summary>
<pre>
<code>
  test("BeeCrowd test of INTEGER banknotes with (User Input)") {
    val module = ScalaParser.parseResource("stmts/BeeBanknoteIntUser.oberon")

    assert(module.stmt.isDefined)

    assert(module.name == "BeeBanknoteIntUser")

    module.accept(interpreter)

    assert(interpreter.env.lookup("i") == Some(IntValue(7)))

    assert(evalArraySubscript("banknotesNeeded", 0) == IntValue(5))
    assert(evalArraySubscript("banknotesNeeded", 1) == IntValue(1))
    assert(evalArraySubscript("banknotesNeeded", 2) == IntValue(1))
    assert(evalArraySubscript("banknotesNeeded", 3) == IntValue(0))
    assert(evalArraySubscript("banknotesNeeded", 4) == IntValue(1))
    assert(evalArraySubscript("banknotesNeeded", 5) == IntValue(0))
    assert(evalArraySubscript("banknotesNeeded", 6) == IntValue(1))

  }
</code>
</pre>
</details>

## Funcionalidades testadas
### Inicialização de arrays
Documentação já feita no teste [bee1018](bee1018.md#arrays).

### forEach
Documentação já feita no teste [bee1018](bee1018.md#foreach).

### FLOOR
Documentação já feita no teste [bee1018](bee1018.md#floor).

### INC
Documentação já feita no teste [bee1018](bee1018.md#inc).

<a name="readfile"></a>
### READFILE

É um procedimento básico que lê a partir de um .txt e retorna uma String.

```
		valueUser := READFILE("src\test\resources\userInput\beecrowdint");
```

<a name="stringtoint"></a>
### STRINGTOINT

É um procedimento básico que tranforma uma String em Integer.

```
		valueUserInt1 := STRINGTOINT(valueUser);
```
