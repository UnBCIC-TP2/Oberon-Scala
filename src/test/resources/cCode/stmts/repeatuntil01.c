#include <stdio.h>
#include <stdbool.h>

int main() {
  int x;

  x = 20;
  while (true) {
    x = x + 1;
    if (x >= 10) {
      break;
    }
  }
  printf("%d\n", x);

	return 0; 
}