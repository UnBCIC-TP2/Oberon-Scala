#include <stdio.h>
#include <stdbool.h>

int main() {
    int y, z, x;

    scanf("%d", &x);
    y = 0;
    while (y < x) {
        y = y + 2;
        z = z + y;
    }
    printf("%d\n", z);
}