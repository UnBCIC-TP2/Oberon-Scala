#include <stdio.h>
#include <stdbool.h>

int main() {
  int i, n, ant, prox, soma;

  ant = 0;
  prox = 1;
  soma = 0;
  n = 7;
  i = 1;
  while (i <= n) {
    soma = prox + ant;
    ant = prox;
    prox = soma;
    i = i + 1;
  }
  printf("%d\n", ant);

	return 0; 
}