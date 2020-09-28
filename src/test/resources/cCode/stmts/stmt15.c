#include <stdio.h>

int x, y, z;

void main () {
  scanf("%d", &x);
  for(y = 0; y < x; y++){
    scanf("%d", &z);
    z = z / (y * x);
  }
  printf("%d", z);
}