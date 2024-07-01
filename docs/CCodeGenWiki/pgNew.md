# Teste pgNew
<b>Situação:</b> Falhou.

<b>Objetivo:</b> Testar FOR e POW.

<b>Funcionalidades usadas:</b> POW e FOR.

## Descrição da falha

O Parser não consegue identificar uma expressão válida devido a falta da "stdlib.h". O módulo Oberon não está encontrando a função POW implementada. O While não está sendo traduzido corretamente para C.

## Trecho da falha

```
p := POW(r, n - 1);
```

<details>
<p>
<summary><b><u>Módulo Oberon</u></b></summary>
<pre>
<code>
MODULE pgNew;

VAR
 pg, a1, r, n, N, p : INTEGER;

BEGIN
 a1 := 2;
 r := 3;
 N := 5;
 FOR n:= 0 TO N DO
  p := POW(r, n - 1);
  pg = a1 * p;
  write(pg);
  n := n + 1
END

END pgNew.

</code>
</pre>
</details>

![Descritivo da falha](image-1.png)