#include <stdio.h>
#include <stdbool.h>

int main() {
    int x, y;

    scanf("%d", &x);
    scanf("%d", &y);
    while (x < y) {
        x = x * x;
    }
    printf("%d\n", x);
}