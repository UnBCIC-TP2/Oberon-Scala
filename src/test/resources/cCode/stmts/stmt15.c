#include <stdio.h>
#include <stdbool.h>

void main() {
  int y, z, x;

  scanf("%d", &x);
  for (y = 0; y < x; y++) {
    scanf("%d", &z);
    z = z / (y * x);
  }
  printf("%d\n", z);
}