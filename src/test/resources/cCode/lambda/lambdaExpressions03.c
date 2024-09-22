#include <math.h>
#include <stdio.h>
#include <stdbool.h>
#include <stdlib.h>

typedef int (*ld)(int);


int lambda_0(int a) {
    return a + 10;
}

int main() {

    ld x;


    x = lambda_0;

}
