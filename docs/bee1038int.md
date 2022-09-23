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

## Funcionalidades testadas
### Inicialização de arrays de inteiro

<b>Arrays</b> precisam ter seu tipo declarado antes de serem inicializados. Para tanto, é necessário utilizar a sintaxe <i>typeName = ARRAY X OF Y</i>, onde typeName é o nome do tipo, X é a quantidade de elementos no array e Y é o tipo dos elementos.

```
TYPE
	intArray = ARRAY 5 OF INTEGER
```
Com o tipo definido, o array pode ser inicializado partindo do tipo typeName.

```
VAR
	banknotesValues: intArray;
```

### Aritmética com array de inteiro

Nesse teste utilizou-se multiplicação de elementos do array.
<br>"value := banknotesValues[x] * y"
