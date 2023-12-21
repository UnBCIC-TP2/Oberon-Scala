# Teste boolNeg
<b>Situação:</b> Falhou.

<b>Objetivo:</b> Testar negação de expressões.

<b>Funcionalidades usadas:</b> AND, NOT, IF e BOOLEAN.

## Descrição da falha

O estágio atual da implementação não permite o uso de variáveis do tipo BOOLEAN.

## Trecho da falha

```
p, q, r : BOOLEAN;
```

<details>
<p>
<summary><b><u>Módulo Oberon</u></b></summary>
<pre>
<code>
MODULE boolNeg;

VAR
    p, q, r : BOOLEAN;
BEGIN
 p := TRUE;
 q := FALSE;
 p := p && ~q;
 IF (r = FALSE)
 THEN
    write(r)
 END
END

END boolNeg.
</code>
</pre>
</details>