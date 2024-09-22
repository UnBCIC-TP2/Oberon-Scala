#include <math.h>
#include <stdio.h>
#include <stdbool.h>
#include <stdlib.h>


int numero;


int main() {
    numero = 11;
    if (numero >= 10 && numero <= 20 && (numero == 5 || numero == 10)) {
        printf("%d\n", 1);
    } else {
        if (!(numero == 5 || numero == 10)) {
            printf("%d\n", 0);
        }
    }
}