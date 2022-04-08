#include <stdio.h>
#include <stdbool.h>


int y, z;


int main() {
    y = 0;
    while (y < 10) {
        z = z + y;
    }
    printf("%d\n", z);
}