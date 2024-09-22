#include <math.h>
#include <stdio.h>
#include <stdbool.h>
#include <stdlib.h>


int x, y;

int sum(int v1, int v2) {
    return v1 + v2;
}


int main() {
    scanf("%d", &x);
    scanf("%d", &y);
    printf("%d\n", sum(x, y));
}