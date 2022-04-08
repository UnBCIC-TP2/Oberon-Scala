#include <stdio.h>
#include <stdbool.h>


int x, y;


int main() {
    scanf("%d", &x);
    scanf("%d", &y);
    while (x < y) {
        x = x * x;
    }
    printf("%d\n", x);
}