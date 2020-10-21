#include <stdio.h>
#include <stdbool.h>

#define limit 10

int calcmult(int i, int base) {
  return i * base;
}

void main() {
  int base, count, mult;

  scanf("%d", &base);
  count = 1;
  while (count < limit) {
    mult = calcmult(count, base);
    printf("%d\n", mult);
  }
}