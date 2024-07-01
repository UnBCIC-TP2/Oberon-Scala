#include <stdio.h>
#include <stdbool.h>


int x, y;


int main() {
    x = 0;
    y = 0;
    while (true) {
        x = x + 1;
        y = y + 1;
        if (x >= 10 && y >= 20) {
            break;
        }
    }
    printf("%d\n", x);
    printf("%d\n", y);
}