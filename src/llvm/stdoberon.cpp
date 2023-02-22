#include "stdio.h"

#define DLLEXPORT
extern "C" DLLEXPORT void write_integer(int X) {
  printf("%d\n", X);
}

extern "C" DLLEXPORT int read_integer() {
  int x;
  scanf("%d", &x);
  return x;
}
