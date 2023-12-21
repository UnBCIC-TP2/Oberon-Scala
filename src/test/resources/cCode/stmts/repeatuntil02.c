#include <stdio.h>
#include <stdbool.h>


int x, y;


int main() {
    x = 0;
    y = 0;
    while (true) {
        x = x + 1;
        while (true) {
            y = y + 1;
            if (y >= 10) {
                break;
            }
        }
        if (x >= 10) {
            break;
        }
    }
    printf("%d\n", x);
    printf("%d\n", y);
}