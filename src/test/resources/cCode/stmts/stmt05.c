#include <stdlib.h>
#include <stdio.h>

void main() {
  int x, y;

  x = 5;
  y = 100;
  while (x < y) {
    x = x * x;
  }
  printf("%d\n", x);
}