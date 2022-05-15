#include <stdio.h>
#include <stdbool.h>


int x, y;


int main() {
    x = 5;
    y = 100;
    while (x < y) {
        x = x * x;
    }
    printf("%d\n", x);
}