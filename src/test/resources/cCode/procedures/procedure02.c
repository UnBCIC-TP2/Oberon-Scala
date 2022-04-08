#include <stdio.h>
#include <stdbool.h>


int base, count, mult;

#define limit 10

int calcmult(int i, int base) {
    return i * base;
}


int main() {
    scanf("%d", &base);
    count = 1;
    while (count < limit) {
        mult = calcmult(count, base);
        printf("%d\n", mult);
    }
}