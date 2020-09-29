#include <stdio.h>
#include <stdbool.h>

void main() {
  int y, z, x;

  scanf("%d", &x);
  for (y = 0; y < x; y++) {
    y = y + 2;
    z = z + y;
  }
  printf("%d\n", z);
}