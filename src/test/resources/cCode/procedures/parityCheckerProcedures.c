#include <stdio.h>
#include <stdbool.h>


int res, oddness;

int parity(int number) {
    oddness = number % 2;
    if (oddness == 1) {
        return 1;
    } else {
        return 0;
    }
}


int main() {
    res = parity(5);
    printf("%d\n", res);
}