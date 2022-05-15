#include <stdio.h>
#include <stdbool.h>


int x, max;


int main() {
    scanf("%d", &x);
    scanf("%d", &max);
    if (x > max) {
        max = x;
    }
    printf("%d\n", max);
}