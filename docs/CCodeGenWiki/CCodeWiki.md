# Tutorial tradução de módulos Oberon para C

<b>Objetivo:</b> Prover documentação do que foi realizado na Geração de Código C até o momento e os passos para trabalhos futuros.

<b>Documentação oficial da linguagem Oberon:</b> https://people.inf.ethz.ch/wirth/Oberon/Oberon07.Report.pdf

<b>Vídeo-tutorial de introdução a Oberon (inglês)</b>: https://www.youtube.com/watch?v=IyYRzpxJaaE

## Código Exemplo Oberon utilizando Procedures

```
MODULE Parity;

VAR
  res, oddness : INTEGER;

PROCEDURE parity(number: INTEGER) : INTEGER;
 BEGIN
   oddness := number MOD 2;
   IF (oddness = 1)
   THEN
     RETURN 1
   ELSE
     RETURN 0
   END
 END parity

BEGIN
 res := parity(5);
 write(res)
END

END Parity.
```

## Código Exemplo equivalente da tradução de Oberon para C

```

#include <stdio.h>
#include <stdbool.h>


int res, oddness;

int parity(int number) {
    oddness = number % 2;
    if (oddness == 1) {
        return 1;
    } else {
        return 0;
    }
}


int main() {
    res = parity(5);
    printf("%d\n", res);
}
```

## Como programar em Oberon

### MODULE

Primeiro é necessário criar o ```MODULE```. Preferencialmente com o mesmo nome do arquivo, mas é apenas convenção, como por exemplo: ```MODULE Parity;```.


### TYPE

Caso queira arrays ou variáveis do tipo ```RECORD```, é necessário criar o procedimento ```TYPE```, e por fim inicializar as variavéis necessárias: 

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

Para inicialização de variáveis, cria-se o procedimento ```VAR```: 

```
VAR
	banknotesValues, banknotesNeeded: intArray;
	value: INTEGER;
	i, v: INTEGER;
```

### BEGIN END

Os procedimentos ```BEGIN``` e ```END``` respectivamente começam e finalizam o algoritmo. Eles são a estrutura do módulo em Oberon, como é possível ver no Código Exemplo Oberon. O ```END```
também é utilizado para finalizar expressões como o ```FOR EACH``` e ```IF```. **Note** que a expressão ou variavel que vier antes do ```END``` **não**
pode possuir ```;``` no final, como se pode ver no Código Exemplo acima.

## Funções nativas em Oberon

```INC(x)``` Função que incrementa em 1 a variável escolhida. Exemplo: BeeBanknoteIntUser.

```DEC(x)``` Função que decrementa em 1 a variável escolhida. Exemplo: DECTest.

```ABS(x)``` Função que retorna o valor absoluto da variável. Exemplo: ABSTest.

```ODD(x)``` Função que retorna caso o valor seja ímpar. Exemplo: ODDTest.

```CEIL(x)``` Função que obtém um número inteiro maior ou igual a um determinado número, arredondando o número fornecido. Exemplo: CEILTest.

```FLOOR(x)``` Função que retorna um número float que é menor ou igual ao número float fornecido. Exemplo: FLOORTest.

```RND(x)``` Função que arredonda o número escolhido para um número inteiro. Exemplo: RNDTest.

```FLT(x)``` Função que tranforma um valor INTEGER em um valor FLOAT. Exemplo: FLTTest.

```POW(x, y)``` Função que retorna x elevado a y. Exemplo: POWTest.

```SQR(x)``` Função que retorna a raiz quadrada da variável. Exemplo: SQRTest.

```READFILE(x)``` Função que retorna em uma STRING o contéudo de um arquivo .txt. Exemplo: READFILETest.

```WRITEFILE(x, y)``` Função que escreve no arquivo com path (x) o contéudo (y). Exemplo: WRITEFILETest.

```APPENDFILE(x, y)``` Função que adiciona o mqruivo com path (x) o contéudo (y). Exemplo: APPENDFILETest.

```STRINGTOINT(x)``` Função que tranforma um valor STRING em um valor INTEGER. Exemplo: STRINGTOINTTest.

```STRINGTOREAL(x)``` Função que tranforma um valor STRING em um valor FLOAT. Exemplo: STRINGTOREALTest.

## Estrutura de um código em C
- Válida para a versão da implementação do projeto 12/2023.

### Estrutura C com Procedures
``` 
#include <stdio.h>
#include <stdbool.h>


int res, oddness;

int parity(int number) {
    oddness = number % 2;
    if (oddness == 1) {
        return 1;
    } else {
        return 0;
    }
}


int main() {
    res = parity(5);
    printf("%d\n", res);
}
```

### Estrutura C sem Procedures

```
#include <stdio.h>
#include <stdbool.h>


int number, parity;


int main() {
    number = 5;
    parity = 0;
    if (parity == 0) {
        printf("%d\n", 1);
    } else {
        printf("%d\n", 0);
    }
}
```

Devemos nos atentar a estrutura do código, suas indentações e espaços. Entre a declaração das bibliotecas e das variáveis, devemos nos atentar ao espaçamento de **2 linhas**.

```
#include <stdio.h>
#include <stdbool.h>


int number, parity;
```

Ao criar um código sem o uso de Procedures, devemos utilizar o espaçamento de **1 linha** entre a declaração de variáveis e a nossa int main().

```
int number, parity;

int main() {
	[...]
}
```

Caso utilize Procedures, a estrutura se altera, necessitando de espaçamento de **2 linhas** entre a declaração de variáveis e a procedure.

```
int number, parity;


int main() {
	[...]
}
```

## Testes realizados

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

## Bugs e peculiaridades

<ul>
    <li><b><a href="./forstmt.md">Estrutura FOR</a>: forstmt</b></li>
    <ul>
	    <li>FOR</li>
    </ul>
</ul>

- 

