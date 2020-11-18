#include <stdio.h>
#include <stdbool.h>

void main() {
  int x;

  x = 20;
  do {
    x = x + 1;
  } while (!(x >= 10));
  printf("%d\n", x);
}