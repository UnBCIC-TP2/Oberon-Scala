#include <stdio.h>
#include <stdbool.h>

void main() {
  int y, z, x;

  scanf("%d", &x);
  for (y = 0; y < x; y++) {
    scanf("%d", &z);
    z = z / (y + 1); //TODO: A expressão do tipo bracket não parece ter sido implementada no parser
    printf("%d\n", z);
  }
}