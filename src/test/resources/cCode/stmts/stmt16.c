#include <stdio.h>

int x, y, z;

void main () {
  scanf("%d", &x);
  for(y = x; y > 0; y++){
    z = z / (z / y);
    y = y - 2;
  }
  printf("%d", z);
}