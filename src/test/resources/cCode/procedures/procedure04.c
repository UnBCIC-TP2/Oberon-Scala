#include <stdio.h>
#include <stdbool.h>

int sum(int v1, int v2) {
  return v1 + v2;
}

void print(int v1) {
  printf("%d\n", v1);
}

void main() {
  int x, y;

  scanf("%d", &x);
  scanf("%d", &y);
  print(sum(x, y));
}