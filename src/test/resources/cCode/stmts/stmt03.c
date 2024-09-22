#include <math.h>
#include <stdio.h>
#include <stdbool.h>
#include <stdlib.h>


int x, max;


int main() {
    scanf("%d", &x);
    scanf("%d", &max);
    if (x > max) {
        max = x;
        x = 0;
    }
    printf("%d\n", max);
}