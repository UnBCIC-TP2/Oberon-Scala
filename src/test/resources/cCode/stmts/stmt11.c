#include <stdio.h>
#include <stdbool.h>

int main() {
    int y, z, x;

    scanf("%d", &x);
    y = 0;
    while (y < x) {
        scanf("%d", &z);
        z = z / y + 1;
        printf("%d\n", z);
    }
}