#include <stdio.h>
#include <stdbool.h>

void main() {
  int x, y;

  scanf("%d", &x);
  for (y = x; y < 100; y++) {
    y = y * y;
  }
  printf("%d\n", y);
}