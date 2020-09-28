#include <stdio.h>
#include <stdbool.h>

int x, y;

void main () {
  scanf("%d", &x);
  for(y = x; y > 0; y++){
    y = y - 2;
    printf("%d", y);
  }
}