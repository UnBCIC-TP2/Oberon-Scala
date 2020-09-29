#include <stdio.h>
#include <stdbool.h>

void main() {
  int x, y, z;
  scanf("%d", &x);
  for(y = x; y > 0; y++){
    z = z / (z / y); //TODO: A expressão do tipo bracket não parece ter sido implementada no parser
    y = y - 2;
  }
  printf("%d\n", z);
}