#include <math.h>
#include <stdio.h>
#include <stdbool.h>
#include <stdlib.h>


int x, y;


int main() {
    scanf("%d", &x);
    y = x;
    while (y < 100) {
        y = y * y;
    }
    printf("%d\n", y);
}