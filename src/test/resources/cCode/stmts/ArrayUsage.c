#include <math.h>
#include <stdio.h>
#include <stdbool.h>
#include <stdlib.h>

typedef int A[10];

A a;


int main() {
    a[0] = 5;
    a[9] = a[0];
}