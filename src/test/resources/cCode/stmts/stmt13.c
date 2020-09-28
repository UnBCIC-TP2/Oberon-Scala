#include <stdio.h>

int x, y;

void main(){
  scanf("%d", &x);
  for(y=x; y<100; y++){
    y = y * y;
  }
  printf("%d", y);
}