#include <stdio.h>
#include <stdbool.h>

typedef int A[10];

A a;


int main() {
    a[0] = 5;
    a[1] = 10 + 15;
    a[2] = 10 * 15;
    a[3] = 10 / 15;
    a[4] = 10 - 15;
}