#include <stdio.h>
#include <stdbool.h>

int power(int b, int e) {
  int r;
  r = b;
  if (b < 0 | e < 0) {
    return 0;
  }
  if (e == 0) {
    return 1;
  }
  do {
    r = r * b;
    e = e - 1;
  } while (!(e <= 1));
  return r;
}

void main() {
  int x, y;

  x = 2;
  y = 2;
  printf("%d\n", power(x, y));
}