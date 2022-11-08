# Oberon Tutorial

<b>Objetivo:</b> Prover tutoriais para aprender a linguagem Oberon, com foco na prática através de testes unitários.

<b>Documentação oficial da linguagem:</b> https://people.inf.ethz.ch/wirth/Oberon/Oberon07.Report.pdf

<b>Vídeo-tutorial de introdução a Oberon (inglês)</b>: https://www.youtube.com/watch?v=IyYRzpxJaaE

## Código Exemplo

```
MODULE BeeBanknoteInt;


TYPE
	intArray = ARRAY 7 OF INTEGER

VAR
	banknotesValues, banknotesNeeded: intArray;
	value: INTEGER;
	i, v: INTEGER;

BEGIN
	value := 576.73;
	i := 0;

	banknotesValues[0] := 100;
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

## Como programar em Oberon

### MODULE

Primeiro é necessário criar o ```MODULE``` . Com o mesmo nome do arquivo em está o algoritmo. como ```MODULE BeeBanknoteInt;```

### TYPE

Caso queira arrays ou variavéis tipo ```RECORD``` é necessário criar o procedimento ```TYPE``` e iniciliazar as variavéis necessárias. como 

```
TYPE
	realArray = ARRAY 12 OF REAL
  intArray = ARRAY 12 OF INTEGER
  type4 = RECORD
    v1, v2: INTEGER;
  END
  p : POINTER TO INTEGER;
```

### VAR

Para inicialização de variavéis, cria-se o procedimento ```VAR```. Como em 

```
VAR
	banknotesValues, banknotesNeeded: intArray;
	value: INTEGER;
	i, v: INTEGER;
```

### BEGIN END

Os dois procedimentos ```BEGIN``` e ```END```. começam e finalizam o algoritmo, respectivamente. Como é possível ver no código exemplo. O ```END```
também é utilizado para finalizar expressões como o ```FOR EACH```. Note que a expressão ou variavel que vier antes do ```END``` não
pode possuir ```;``` no final, como se pode ver no Código-exemplo acima.

## Funções nativas em Oberon

```INC(x)``` Função que incrementa em 1 a variavél escolhida. Exemplo: BeeBanknoteIntUser.

```DEC(x)``` Função que decrementa em 1 a variavél escolhida. Exemplo: DECTest.

```ABS(x)``` Função que retorna o valor absoluto da variavél. Exemplo: ABSTest.

```ODD(x)``` Função que retorna caso o valor seja ímpar. Exemplo: ODDTest.

```CEIL(x)``` Função que obtém um número inteiro maior ou igual a um determinado número, arredondando o número fornecido. Exemplo: CEILTest.

```FLOOR(x)``` Função que retorna um número float que é menor ou igual ao número float fornecido. Exemplo: FLOORTest.

```RND(x)``` Função que arredonda o número escolhido para um número inteiro. Exemplo: RNDTest.

```FLT(x)``` Função que tranforma um valor INTEGER em um valor FLOAT. Exemplo: FLTTest.

```POW(x, y)``` Função que retorna x elevado a y. Exemplo: POWTest.

```SQR(x)``` Função que retorna a raiz quadrada da variavél. Exemplo: SQRTest.

```READFILE(x)``` Função que retorna em uma STRING o contéudo de um arquivo .txt. Exemplo: READFILETest.

```WRITEFILE(x, y)``` Função que escreve no arquivo com path (x) o contéudo (y). Exemplo: WRITEFILETest.

```APPENDFILE(x, y)``` Função que adiciona o mqruivo com path (x) o contéudo (y). Exemplo: APPENDFILETest.

```STRINGTOINT(x)``` Função que tranforma um valor STRING em um valor INTEGER. Exemplo: STRINGTOINTTest.

```STRINGTOREAL(x)``` Função que tranforma um valor STRING em um valor FLOAT. Exemplo: STRINGTOREALTest.

## Testes feitos

Lista de testes unitários feitos e documentados em forma de tutorial da linguagem Oberon.

(Como subitens estão as funcionalidades testadas em cada teste.)

<ul>
	<li><b><a href="./bee1013.md">bee1013</a>: O Maior</b></li>
    <ul>
	    <li>Procedure</li>
	    <li>Nested Procedure</li>
    </ul>
	<li><b><a href="./bee1018.md">bee1018</a>: Cédulas</b></li>
    <ul>
	    <li>FOREACH</li>
	    <li>Inicialização de arrays</li>
	    <li>FLOOR</li>
	    <li>INC</li>
    </ul>
	<li><b><a href="./bee1018user.md">bee1018user</a>: Cédulas (User Input)</b></li>
    <ul>
	    <li>Todas do bee1018</li>
	    <li>READFILE</li>
	    <li>STRINGTOINT</li>
    </ul>
	<li><b><a href="./bee1021.md">bee1021</a>: Notas e Moedas</b></li>
    <ul>
	    <li>FOREACH</li>
	    <li>Inicialização de arrays</li>
	    <li>FLOOR</li>
	    <li>INC</li>
    </ul>
	<li><b><a href="./bee1029.md">bee1029</a>: Fibonacci, Quantas Chamadas?</b></li>
    <ul>
	    <li>Procedure</li>
	    <li>FOR</li>
	    <li>Comparador de igualdade</li>
	    <li>Inicialização de arrays</li>
    </ul>
	<li><b><a href="./bee1038int.md">bee1038</a>: Snack (Inteiros)</b></li>
    <ul>
	    <li>Inicialização de arrays</li>
	    <li>Aritmética com arrays de inteiros</li>
    </ul>
	<li><b><a href="./bee1038real.md">bee1038</a>: Snack (Reais)</b></li>
    <ul>
	    <li>Inicialização de arrays</li>
	    <li>Aritmética com arrays de reais</li>
    </ul>
	<li><b><a href="./bee1042.md">bee1042</a>: Simple Sort</b></li>
    <ul>
	    <li>REPEAT</li>
    </ul>
	<li><b><a href="./bee1049.md">bee1049</a>: Animal</b></li>
    <ul>
	    <li>IF/ELSE</li>
	    <li>Comparador de igualdade</li>
	    <li>Procedure</li>
    </ul>
	<li><b><a href="./bee1060.md">bee1060</a>: Positive</b></li>
    <ul>
	    <li>FOREACH</li>
    </ul>
	<li><b><a href="./bee1061.md">bee1061</a>: Event Time</b></li>
    <ul>
	    <li>IF/ELSE</li>
    </ul>
	<li><b><a href="./bee1071.md">bee1071</a>: Sum</b></li>
    <ul>
	    <li>REPEAT</li>
    </ul>
	<li><b><a href="./bee1079.md">bee1079</a>: Average</b></li>
    <ul>
	    <li>REPEAT</li>
    </ul>
	<li><b><a href="./bee2057.md">bee2057</a>: Fuso Horário</b></li>
    <ul>
	    <li>FOREACH</li>
	    <li>Inicialização de arrays</li>
	    <li>MOD</li>
    </ul>
	<li><b><a href="./bee2221.md">bee2221</a>: Pokemons Battle</b></li>
    <ul>
	    <li>IF/ELSE</li>
	    <li>ODD</li>
    </ul>
	<li><b><a href="./bee2779.md">bee2779</a>: Álbum da Copa</b></li>
    <ul>
	    <li>FOREACH</li>
	    <li>Inicialização de arrays</li>
	    <li>IF/ELSE</li>
    </ul>
	<li><b><a href="./bee3205valt.md">bee3205valt</a>: Nasty Hacks (versão alterada)</b></li>
    <ul>
	    <li>FOREACH</li>
	    <li>Inicialização de arrays</li>
	    <li>FLOOR</li>
	    <li>INC</li>
    </ul>
	<li><b><a href="./stringToInt.md">Nova função</a>: stringToInt</b></li>
    <ul>
	    <li>STRINGTOINT</li>
    </ul>
	<li><b><a href="./stringToReal.md">Nova função</a>: stringToReal</b></li>
    <ul>
	    <li>STRINGTOREAL</li>
    </ul>
</ul> 
