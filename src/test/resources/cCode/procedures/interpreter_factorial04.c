#include <stdio.h>
#include <stdbool.h>


int x, y;


int main() {
    y = 1;
    x = 4;
    while (x > 1) {
        if (1 <= x && x <= 4) {
            y = y * x;
        }
        x = x - 1;
    }
    printf("%d\n", y);
}