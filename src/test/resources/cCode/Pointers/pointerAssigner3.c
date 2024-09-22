#include <stdio.h>
#include <stdlib.h>

typedef float *PointerA;

int main() {

    float *a = (float *)malloc(sizeof(float));
    PointerA *b = (PointerA *)malloc(sizeof(PointerA));
    int **c = (int **)malloc(sizeof(int *));


    *a = 10.5; 
    *b = a;     


    printf("Valor de a: %.1f\n", *a);
    printf("Valor apontado por b (a): %.1f
