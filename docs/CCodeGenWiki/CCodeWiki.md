# Tutorial tradução de módulos Oberon para C

<b>Objetivo:</b> Prover documentação do que foi realizado na Geração de Código C até o momento e os passos para trabalhos futuros.

<b>Documentação oficial da linguagem Oberon:</b> https://people.inf.ethz.ch/wirth/Oberon/Oberon07.Report.pdf

<b>Vídeo-tutorial de introdução a Oberon (inglês)</b>: https://www.youtube.com/watch?v=IyYRzpxJaaE

## A tradução de código
A geração de código em C na implementação atual, é feita a partir de um módulo Oberon. O módulo é traduzido para C, e então compilado e executado. O resultado da execução é comparado com o resultado esperado, e então o teste é considerado bem sucedido ou falhou. O resultado da execução dos testes é a comparação entre a tradução do módulo Oberon e a tradução do código em C equivalente a ele. Logo, é necessário ter ambos os códigos (Oberon e C) para a comparação. 

Atente-se que ambos os códigos, Oberon e C, devem estar em pastas equivalentes, para que a comparação seja realizada com sucesso. Caso o módulo Oberon esteja na pasta stmts, o código C equivalente deve estar na pasta stmts no diretório referente a códigos em C.

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
	<li><b><a href="./parityCheckerProcedures.md">parityCheckerProcedures</a>: Par ou ímpar</b></li>
    <ul>
	    <li>Procedure</li>
	    <li>MOD</li>
    </ul>
	<li><b><a href="./parityCheckerSimple">parityCheckerSimple</a>: Par ou ímpar</b></li>
    <ul>
	    <li>IF</li>
    </ul>
	<li><b><a href="./Progression.md">Progression</a>: Progressão Aritmética</b></li>
    <ul>
	    <li>REPEAT</li>
	    <li>INC</li>
    </ul>
	<li><b><a href="./Countdown.md">Countdown</a>: Contagem decrescente</b></li>
    <ul>
	    <li>REPEAT</li>
    </ul>
	<li><b><a href="./beecrowd1017.md">beecrowd1017</a>: Consumo médio</b></li>
    <ul>
	    <li>Operadores matemáticos</li>
    </ul>
	<li><b><a href="./beecrowd1005.md">beecrowd1005</a>: Média aritmética simples</b></li>
    <ul>
	    <li>Operadores matemáticos</li>
    </ul>
	<li><b><a href="./beecrowd1008.md">beecrowd1008</a>: Salário</b></li>
    <ul>
		<li>Operadores matemáticos</li>
    </ul>
	<li><b><a href="./beecrowd1020.md">beecrowd1020</a>: Cálculo de anos</b></li>
    <ul>
	    <li>Operadores matemáticos</li>
    </ul>
	<li><b><a href="./beecrowd1014.md">beecrowd1014</a>: Consumo de combustível</b></li>
    <ul>
	    <li>Divisão</li>
    </ul>
	<li><b><a href="./beecrowd1010.md">beecrowd1010</a>: Cálculo de valor de produtos</b></li>
    <ul>
	    <li>Multiplas variáveis com multiplificação</li>
    </ul>
	<li><b><a href="./forstmt.md">forstmt</a>: For Statement</b></li>
    <ul>
	    <li>FOR</li>
    </ul>
	<li><b><a href="./Logic.md">Logic</a>: Lógica AND OR</b></li>
    <ul>
	    <li>Operadores Lógicos AND e OR</li>
    </ul>
	<li><b><a href="./LogicNot.md">LogicNot</a>: Lógica AND e UNEQUAL</b></li>
    <ul>
	    <li>Operadores Lógicos AND e UNEQUAL</li>
    </ul>
	<li><b><a href="./pgNormal.md">pgNormal</a>: Progressão Geométrica Expressão com +3 variáveis</b></li>
    <ul>
	    <li>POW</li>
		<li>FOR</li>
    </ul>
	<li><b><a href="./pgNew.md">pgNew</a>: Progressão Geométrica</b></li>
    <ul>
	    <li>IF/ELSE</li>
    </ul>
	<li><b><a href="./boolNeg.md">boolNeg</a>: Negação Lógica</b></li>
    <ul>
	    <li>Operadores Lógicos</li>
		<li>AND</li>
		<li>NOT</li>
		<li>IF</li>
		<li>BOOLEAN</li>
    </ul>
	<li><b><a href="./Progression_mul.md">Progression_mul</a>: Progressão Aritmética com diversas variáveis</b></li>
    <ul>
	    <li>Operadores Lógicos</li>
		<li>AND</li>
		<li>NOT</li>
		<li>IF</li>
		<li>BOOLEAN</li>
    </ul>
</ul> 

## Bugs e peculiaridades

<ul>
    <li><b><a href="./forstmt.md">Estrutura FOR</a>: Peculiaridade For</b></li>
    <ul>
	    <li>O FOR em Oberon se traduz em While em C. </li>
    </ul>
	<li><b><a href="./pgNormal.md">pgNormal</a> e <a href="./pgNew.md">pgNew</a>: POW e FOR</b></li>
    <ul>
	    <li>O while quando traduzido a partir de um FOR, ele não respeita a condição. A função POW não é encontrada no Oberon Module.</li>
    </ul>
	<li><b><a href="./Progression_mul.md">Progressão Aritmética</a>: Parenteses</b></li>
    <ul>
	    <li>O uso de parenteses para determinar precedência causa a falha no teste.</li>
    </ul>
</ul>

## Correções realizadas na versão 12/2023
- Refatoração na tradução do CCodeGen.scala
  - Correção de bugs (break e indentação).
  - Otimização de código 
    - Alteração na utilização das listas (lista[0] -> lista.head).
- Refatoração de 18 testes afetados por bugs no CCodeGen.scala
- Corrigida a formatação de código em C.

