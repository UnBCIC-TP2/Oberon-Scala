#include <stdio.h>
#include <stdbool.h>

void main() {
  int x, y, z;
  scanf("%d", &x);
  for(y = 0; y < x; y++){
    scanf("%d", &z);
    z = z / (y * x); //TODO: A expressão do tipo bracket não parece ter sido implementada no parser
  }
  printf("%d\n", z);
}