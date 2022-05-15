#include <stdio.h>
#include <stdbool.h>

typedef int A[10];
typedef int B[15];

A a;
B b;


int main() {
    a[0] = 5;
    a[1] = 10;
    b[0] = 10;
    a[2] = 10 + 15;
}