#include <stdio.h>
#include <stdbool.h>


int x, y, z;


int main() {
    scanf("%d", &x);
    y = x;
    while (y > 0) {
        z = z + z / y;
        y = y - 2;
    }
    printf("%d\n", z);
}