#include <math.h>
#include <stdio.h>
#include <stdbool.h>
#include <stdlib.h>


int c;
int* a;
int* b;


int main() {
    a = (int*)malloc(sizeof(int));
    b = (int*)malloc(sizeof(int));
    *a = 7;
    *b = 12;
    c = (*a + *b) * (*b - *a);

    free(a);
    free(b);
}
