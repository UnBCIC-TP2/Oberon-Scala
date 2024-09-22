#include <math.h>
#include <stdio.h>
#include <stdbool.h>
#include <stdlib.h>


int x, y;
int* p;


int main() {
    p = (int*)malloc(sizeof(int));
    *p = 10;
    x = 100;
    y = 1000;

    free(p);
}
