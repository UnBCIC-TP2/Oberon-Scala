#include <stdio.h>
#include <stdbool.h>

void main() {
  int xs;

  scanf("%d", &xs);
  switch (xs) {
  case 1:
    xs = 5;
    break;
  case 2:
    xs = 10;
    break;
  case 3:
    xs = 20;
    break;
  case 4:
    xs = 40;
    break;
  default:
    xs = 0;
    break;
  }
  printf("%d\n", xs);
}