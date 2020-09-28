#include <stdio.h>
#include <stdbool.h>

void main() {
  int u, v, y, z, x, w;

  scanf("%d", &x);
  v = 0;
  for (y = 0; y < x; y++){
    scanf("%d", &w);
    v = v + (w * (y + 1));
  }
  v = v / x;
  for (z = 0; z < x; z++){
    scanf("%d", &w);
    u = u + w;
  }
  u = u / x;
  printf("%d", v);
  printf("%d", u);
}