# Teste Progression
<b>Situação:</b> Falhou

<b>Objetivo:</b> Testar laços de repetição e incremento.

<b>Funcionalidades usadas:</b> While e INC.

## Descrição da falha

O Parser não consegue identificar uma expressão válida ao utilizar parenteses como operador de precedência.

## Trecho da falha

```
pa := an + (n - 1) * d;
```

<details>
<p>
<summary><b><u>Módulo Oberon</u></b></summary>
<pre>
<code>
MODULE pa;

VAR
    an, d, n, z, pa : INTEGER;

BEGIN
    an := 2;
    d := 3;
    z := 5;
    n := 1;
    REPEAT
        pa := an + (n - 1) * d;
        write(pa);
        INC(n)
    UNTIL (n <= z)

END

END pa.
</code>
</pre>
</details>



