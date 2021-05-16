#include <stdio.h>
#include <stdbool.h>

void main() {
  int x, y;

  x = 5;
  y = 100;
  while (x < y) {
    x = x * x;
  }
  printf("%d\n", x);
}