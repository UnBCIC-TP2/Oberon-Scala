#include <stdio.h>
#include <stdbool.h>


int a, b, c;


int main() {
    scanf("%d", &a);
    scanf("%d", &b);
    scanf("%d", &c);
    if (a > b) {
        printf("%d\n", a);
    } else {
        if (b > a) {
            printf("%d\n", b);
        } else {
            if (a > c) {
                printf("%d\n", c);
            } else {
                printf("%d\n", c);
            }
        }
    }
}