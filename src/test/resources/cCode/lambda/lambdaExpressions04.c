#include <math.h>
#include <stdio.h>
#include <stdbool.h>
#include <stdlib.h>



#define x lambda_0
#define y lambda_1
#define z 10
#define w 5

int lambda_0(int a) {
    return a + 10;
}

int lambda_1(int a, int b) {
    return a + b;
}


int main() {
    x(z);
    y(z, w);
}
