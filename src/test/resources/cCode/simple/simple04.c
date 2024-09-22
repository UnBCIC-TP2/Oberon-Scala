#include <stdio.h>
#include <stdbool.h>  


#define x 5
#define y 10
#define z (5 + 10)  // Expressão constante

int abc;    
bool def;  

int main() {

    abc = 0;    
    def = false; 

 
    printf("Valor de x: %d\n", x);
    printf("Valor de y: %d\n", y);
    printf("Valor de z: %d\n", z);
    printf("Valor de abc: %d\n", abc);
    printf("Valor de def: %s\n", def ? "true" : "false");

    return 0;
}
