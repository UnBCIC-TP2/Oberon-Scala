#include <stdio.h>
#include <stdbool.h>

int main() {
    int x, max;

    scanf("%d", &x);
    scanf("%d", &max);
    if (x > max) {
        max = x;
    }
    printf("%d\n", max);
}