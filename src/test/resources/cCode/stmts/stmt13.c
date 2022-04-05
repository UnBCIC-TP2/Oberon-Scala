#include <stdio.h>
#include <stdbool.h>


int main() {
    int x, y;

    scanf("%d", &x);
    y = x;
    while (y < 100) {
        y = y * y;
    }
    printf("%d\n", y);
}