#include <stdio.h>
#include <stdbool.h>


int xs;


int main() {
    scanf("%d", &xs);
    if (xs == 1) {
        xs = 5;
    } else {
        if (xs == 2) {
            xs = 10;
        } else {
            if (xs == 3) {
                xs = 20;
            } else {
                if (xs == 4) {
                    xs = 40;
                } else {
                    xs = 0;
                }
            }
        }
    }
    printf("%d\n", xs);
}