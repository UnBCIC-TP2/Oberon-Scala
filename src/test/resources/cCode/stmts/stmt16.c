#include <stdio.h>
#include <stdbool.h>

int main() {
    int x, y, z;

    scanf("%d", &x);
    y = x;
    while (y > 0) {
        z = z + z / y;
        y = y - 2;
    }
    printf("%d\n", z);
}