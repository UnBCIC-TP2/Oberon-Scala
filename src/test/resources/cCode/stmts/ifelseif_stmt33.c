#include <stdio.h>
#include <stdbool.h>


int x, y, gt;


int main() {
    scanf("%d", &x);
    scanf("%d", &y);
    if (x > y) {
        gt = 2;
    } else {
        if (x < y) {
            gt = 1;
        } else {
            gt = 0;
        }
    }
    printf("%d\n", gt);
}