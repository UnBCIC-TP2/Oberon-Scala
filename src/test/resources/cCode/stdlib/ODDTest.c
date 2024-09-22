#include <stdio.h>
#include <stdbool.h>  

bool is_odd(int number) {
    return number % 2 != 0;  
}

int main() {
    int x = 10;
    int y = 11;
    bool z, w;

   
    z = is_odd(x);
    w = is_odd(y);

   
    printf("Valor de x: %d, é ímpar: %s\n", x, z ? "true" : "false");
    printf("Valor de y: %d, é ímpar: %s\n", y, w ? "true" : "false");

    return 0;
}
