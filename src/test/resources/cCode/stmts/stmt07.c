#include <stdio.h>
#include <stdbool.h>

int main() {
  int x, y, z;

  scanf("%d", &x);
  z = 0;
  y = 0;
  while (y < x) {
    z = z + y;
    y = y + 1;
  }
  printf("%d\n", z);

	return 0; 
}