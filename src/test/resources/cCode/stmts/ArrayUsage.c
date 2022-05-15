#include <stdio.h>
#include <stdbool.h>

typedef int A[10];

A a;


int main() {
    a[0] = 5;
    a[9] = a[0];
}