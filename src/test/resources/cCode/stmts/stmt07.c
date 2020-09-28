#include <stdlib.h>
#include <stdio.h>

void main() {
  int x, y, z;

  scanf("%d", &x);
  for (y = 0; y < x; y++) {
    z = z + y;
  }
  printf("%d\n", z);
}