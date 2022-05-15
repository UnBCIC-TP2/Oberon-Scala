#include <stdio.h>
#include <stdbool.h>


int x, y, z;


int main() {
    scanf("%d", &x);
    y = 0;
    while (y < x) {
        z = z + y;
    }
    printf("%d\n", z);
}