#include <stdio.h>
#include <stdbool.h>

void main() {
  int i, n, ant, prox, soma;

  ant = 0;
  prox = 1;
  soma = 0;
  n = 7;
  for (i = 1; i <= n; i++) {
    soma = prox + ant;
    ant = prox;
    prox = soma;
    i = i + 1;
  }
  printf("%d\n", ant);
}