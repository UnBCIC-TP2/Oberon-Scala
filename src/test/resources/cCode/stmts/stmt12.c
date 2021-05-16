#include <stdio.h>
#include <stdbool.h>

void main() {
  int u, v, y, z, x, w;

  scanf("%d", &x);
  v = 0;
  for (y = 0; y < x; y++) {
    scanf("%d", &w);
    v = v + (w * (y + 1)); //TODO: A expressão do tipo bracket não parece ter sido implementada no parser
  }
  v = v / x;
  for (z = 0; z < x; z++) {
    scanf("%d", &w);
    u = u + w;
  }
  u = u / x;
  printf("%d\n", v);
  printf("%d\n", u);
}