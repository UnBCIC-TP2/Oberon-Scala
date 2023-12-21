#include <stdio.h>
#include <stdbool.h>


int x;


int main() {
    x = 0;
    while (true) {
        x = x + 1;
        if (x == 10) {
            break;
        }
    }
    printf("%d\n", x);
}