#include <stdio.h>
#include <stdbool.h>


int main() {
    int x, y;

    scanf("%d", &x);
    y = x;
    while (y > 0) {
        y = y - 2;
        printf("%d\n", y);
    }
}