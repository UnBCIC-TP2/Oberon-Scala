#include <stdlib.h>
#include <stdio.h>

void main() {
  int x, y, z, k;

  scanf("%d", &y);
  for (x = 0; x < y; x++) {
    for (z = 0; z < y; z++) {
      k = z + x;
    }
  }
  printf("%d\n", k);
}