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
  while (true) {
    r = r * b;
    e = e - 1;
    if (e <= 1) {
      break;
    }
  }
  return r;
}

int main() {
  int x, y;

  x = 2;
  y = 2;
  printf("%d\n", power(x, y));

	return 0; 
}