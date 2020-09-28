#include <stdlib.h>
#include <stdio.h>

void main() {
  int y, z;

  for (y = 0; y < 10; y++) {
    z = z + y;
  }
  printf("%d\n", z);
}