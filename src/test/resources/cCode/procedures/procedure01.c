#include <stdio.h>
#include <stdbool.h>

int sum(int v1, int v2) {
  return v1 + v2;
}

void main() {
  int x, y;

  scanf("%d", &x);
  scanf("%d", &y);
  printf("%d\n", sum(x, y));
}