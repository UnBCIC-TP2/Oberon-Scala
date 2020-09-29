#include <stdlib.h>
#include <stdio.h>

void main() {
  int x, y;

  scanf("%d", &x);
  scanf("%d", &y);
  while (x < y) {
    x = x * x;
  }
  printf("%d\n", x);
}