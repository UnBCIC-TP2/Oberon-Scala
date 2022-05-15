#include <stdio.h>
#include <stdbool.h>


int y, z, x;


int main() {
    scanf("%d", &x);
    y = 0;
    while (y < x) {
        scanf("%d", &z);
        z = z / y + 1;
        printf("%d\n", z);
    }
}