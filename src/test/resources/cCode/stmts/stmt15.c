#include <math.h>
#include <stdio.h>
#include <stdbool.h>
#include <stdlib.h>


int y, z, x;


int main() {
    scanf("%d", &x);
    y = 0;
    while (y < x) {
        scanf("%d", &z);
        z = z / y * x;
    }
    printf("%d\n", z);
}