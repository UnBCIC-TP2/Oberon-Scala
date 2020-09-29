#include <stdlib.h>
#include <stdio.h>

void main() {
  int y, z, x;

  scanf("%d", &x);
  for (y = 0; y < x; y++) {
    y = y + 2;
    z = z + y;
  }
  printf("%d\n", z);
}