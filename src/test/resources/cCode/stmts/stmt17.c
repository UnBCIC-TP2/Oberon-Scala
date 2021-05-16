#include <stdio.h>
#include <stdbool.h>

void main() {
  int x, y;

  scanf("%d", &x);
  if (x < 5) {
    y = 1;
  }
  else if (x < 7) {
    y = 2;
  }
  else if (x < 9) {
    y = 3;
  }
  else {
    y = 4;
  }
  printf("%d\n", y);
}