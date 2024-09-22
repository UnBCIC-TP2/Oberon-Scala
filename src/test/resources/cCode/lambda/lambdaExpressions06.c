#include <math.h>
#include <stdio.h>
#include <stdbool.h>
#include <stdlib.h>



#define divide lambda_0
#define addReal lambda_1
#define x 10.5
#define y 2.5

float lambda_0(float a, float b) {
    return a / b;
}

float lambda_1(float a, float b) {
    return a + b;
}


int main() {
    divide(x, y);
    addReal(x, y);
}
