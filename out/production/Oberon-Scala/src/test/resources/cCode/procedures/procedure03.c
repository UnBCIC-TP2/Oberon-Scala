#include <stdio.h>
#include <stdbool.h>

int factorial(int i) {
  if (i == 1) {
    return 1;
  }
  return i * factorial(i - 1);
}

void main() {
  int res;

  res = factorial(5);
  printf("%d\n", res);
}