#include <stdio.h>
#include <stdbool.h>

void main() {
  int x, y;

  y = 1;
  for (x = 5; x > 1; x++) {
    y = y * x;
    x = x - 1;
  }
  printf("%d\n", y);
}