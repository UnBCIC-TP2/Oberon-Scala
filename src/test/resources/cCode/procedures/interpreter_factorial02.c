#include <stdio.h>
#include <stdbool.h>

int main() {
  int x, y;

  y = 1;
  x = 1;
  while (x > 1) {
    y = y * x;
    x = x - 1;
  }
  printf("%d\n", y);

	return 0; 
}