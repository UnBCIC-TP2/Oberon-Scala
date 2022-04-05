#include <stdio.h>
#include <stdbool.h>


int main() {
    int x, y, z;

    scanf("%d", &x);
    y = 0;
    while (y < x) {
        z = z + y;
    }
    printf("%d\n", z);
}