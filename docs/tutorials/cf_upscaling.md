# Teste codeforces_upscaling: Upscaling (adaptado)
<b>Situação:</b> Sucesso

<b>Objetivo:</b> Manipulação de um vetor de caracteres.

<b>Funcionalidades usadas:</b> while, for, CHR, INC.

## Descrição no codeforces

<b>Link:</b> [B. Upscaling](https://codeforces.com/contest/1950/problem/B)

<b>Problema:</b> You are given an integer n. Output a 2n×2n checkerboard made of 2×2 squares alternating '#' and '.', with the top-left cell being '#'.

<img src="https://espresso.codeforces.com/51f8f6d3cb1f2f2902c6a38cfb4b9719aa0d5eca.png" alt="checkerboard">

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
4</p>
</td>
<td>
<p>
##..##..
	
##..##..

..##..##

..##..##

##..##..

##..##..

..##..##

..##..##
</p>
</td>
</tr>
</tbody>
</table>

## Código-exemplo

```
MODULE codeforces_Upscaling;

VAR
    s1, s2 : ARRAY 8 OF CHAR ;
    new_line : CHAR;
    n, i, length, aux : INTEGER;

BEGIN
    
    new_line := CHR(10);
    n := 4;
    length := 2*n;
    aux := 0;
    i := 0;
    WHILE (aux < length) DO
        IF (i MOD 2 = 0) THEN
            s1[aux] := '#';
            s1[aux+1] := '#';
            s2[aux] := '.';
            s2[aux+1] := '.';
            aux := aux + 2
        ELSE
            s1[aux] := '.';
            s1[aux+1] := '.';
            s2[aux] := '#';
            s2[aux+1] := '#';
            aux := aux + 2
        END;
        INC(i)
    END;

    FOR i := 0 TO i < length DO
        IF(i MOD 2 = 0) THEN
            write(s1);
            write(new_line);
            write(s1)
        ELSE
            write(s2);
            write(new_line);
            write(s2)
        END;
        INC(i)
    END
END

END codeforces_Upscaling.
```

<details>
<p>
<summary><b><u>Teste unitário (em Scala)</u></b></summary>
<pre>
<code>
  test("Testing interpreter on codeforces_Upscaling program") {
    val module = parseResource("challenges/codeforces_Upscaling.oberon")

    
    val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "codeforces_Upscaling")

    val result = interpreter.run(coreModule)

    
    assert(result.lookup("length") == Some(IntValue(8)))
    assert(result.lookup("aux") == Some(IntValue(8)))
    assert(result.lookup("new_line") == Some(CharValue('\n')))
    assert(evalArraySubscript(result, "s1", 0) == CharValue('#'))
    assert(evalArraySubscript(result, "s1", 1) == CharValue('#'))
    assert(evalArraySubscript(result, "s1", 2) == CharValue('.'))
    assert(evalArraySubscript(result, "s1", 3) == CharValue('.'))
    assert(evalArraySubscript(result, "s1", 4) == CharValue('#'))
    assert(evalArraySubscript(result, "s1", 5) == CharValue('#'))
    assert(evalArraySubscript(result, "s1", 6) == CharValue('.'))
    assert(evalArraySubscript(result, "s1", 7) == CharValue('.'))
    //s1 é a string ##..##..
    assert(evalArraySubscript(result, "s2", 0) == CharValue('.'))
    assert(evalArraySubscript(result, "s2", 1) == CharValue('.'))
    assert(evalArraySubscript(result, "s2", 2) == CharValue('#'))
    assert(evalArraySubscript(result, "s2", 3) == CharValue('#'))
    assert(evalArraySubscript(result, "s2", 4) == CharValue('.'))
    assert(evalArraySubscript(result, "s2", 5) == CharValue('.'))
    assert(evalArraySubscript(result, "s2", 6) == CharValue('#'))
    assert(evalArraySubscript(result, "s2", 7) == CharValue('#'))
    //s2 é a string ..##..##
  }
</code>
</pre>
</details>

## Funcionalidades testadas
<a name="while"></a>
### while

Loop principal, o qual faz o preenchimento certo das strings para exibição do tabuleiro.

```
    WHILE (aux < length) DO
        IF (i MOD 2 = 0) THEN
            s1[aux] := '#';
            s1[aux+1] := '#';
            s2[aux] := '.';
            s2[aux+1] := '.';
            aux := aux + 2
        ELSE
            s1[aux] := '.';
            s1[aux+1] := '.';
            s2[aux] := '#';
            s2[aux+1] := '#';
            aux := aux + 2
        END;
        INC(i)
    END;
```


<a name="for"></a>
### for

Utilizado apenas para exibir a resposta do problema.

```
	FOR i := 0 TO i < length DO
        IF(i MOD 2 = 0) THEN
            write(s1);
            write(new_line);
            write(s1)
        ELSE
            write(s2);
            write(new_line);
            write(s2)
        END;
        INC(i)
    END
```


<a name="chr"></a>
### CHR

É um procedimento novo, previsto na documentação do Niklaus Wirth, onde é recebido um valor inteiro, sendo este o código ASCII do caractere,
e retorna o caractere desejado.

```
		new_line := CHR(10);
```


<a name="inc"></a>
### INC

É um procedimento básico da linguagem, que incrementa uma variável inteira. É bastante útil para ser usada em loops, em especial no forLoop, onde não foi definida uma variável padrão que retorna o número da iteração sendo realizada.

```
		INC(i)
```

## Código C

Código o qual utilizei como base para fazer esta versão em Oberon:

```
/*
https://codeforces.com/contest/1950/problem/B
*/

#include <stdio.h>

void null_string(char str[]){ /*faz a string ficar nula*/
    int i = 0;
    while(i<=41){
        str[i] = '\0';
        i++;
    }
}


int main()
{
    int t, n, i, aux, size;
    scanf("%d", &t);
    
    char s1[41];
    char s2[41]; /*essas strings vao ter como tamanho o dobro de N
                * como N vai até 20, elas podem ter até 40 caracteres.
                * daí eu acrescento mais 1 caractere para conter o
                * caractere de fim de linha "\0".
                */
    
    while(t--){
        null_string(s1);
        null_string(s2);
        scanf("%d", &n);
        aux = 2*n;
        i = 0;
        size = 0;
        while(size < aux){
            if(i%2==0){
                s1[size] = '#';
                s1[size+1] = '#';
                s2[size] = '.';
                s2[size+1] = '.';
                size += 2;/*nr de caracteres da string*/
            }
            else{
                s1[size] = '.';
                s1[size+1] = '.';
                s2[size] = '#';
                s2[size+1] = '#';
                size += 2;/*nr de caracteres da string*/
            }
            i++;
        }
        for(i=0; i<n; i++){
            if(i%2==0)
                printf("%s\n%s\n",s1,s1);
            else
                printf("%s\n%s\n",s2,s2);
        }
    }

    return 0;
}
```
