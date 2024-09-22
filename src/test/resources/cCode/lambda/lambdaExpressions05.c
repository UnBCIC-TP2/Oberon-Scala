#include <math.h>
#include <stdio.h>
#include <stdbool.h>
#include <stdlib.h>



#define multiply lambda_0
#define subtract lambda_1
#define x 7
#define y 3

int lambda_0(int a, int b) {
    return a * b;
}

int lambda_1(int a, int b) {
    return a - b;
}


int main() {
    multiply(x, y);
    subtract(x, y);
}
