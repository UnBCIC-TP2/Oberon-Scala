#include <stdio.h>
#include <stdbool.h> 


#define x (5 + 10 * 3)     // Multiplicação tem precedência sobre a soma
#define y (5 + 10 * 3 / 5) // Multiplicação e divisão têm precedência sobre a soma

int abc;    
bool def;   

int main() {

    abc = 0;    
    def = false; 


    printf("Valor de x: %d\n", x);
    printf("Valor de y: %d\n", y);
    printf("Valor de abc: %d\n", abc);
    printf("Valor de def: %s\n", def ? "true" : "false");

    return 0;
}
