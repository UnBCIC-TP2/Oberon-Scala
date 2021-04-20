#include <stdio.h>
#include <stdbool.h>

void main() {
  int x, y;

  scanf("%d", &x);
  for (y = x; y > 0; y++) {
    y = y - 2;
    printf("%d\n", y);
  }
}