#include <stdio.h>
#include <stdbool.h>

void main() {
  int y, z, x;

  scanf("%d", &x);
  for (y = 0; y < x; y++) {
    scanf("%d", &z);
    z = z / (y + 1);
    printf("%d\n", z);
  }
}