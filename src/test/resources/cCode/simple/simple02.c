#include <stdio.h>
#include <stdbool.h>  

#define x 5  // Constante

int abc;     
bool def;    

int main() {
    
    abc = 0;     
    def = false;


    printf("Valor de x: %d\n", x);
    printf("Valor de abc: %d\n", abc);
    printf("Valor de def: %s\n", def ? "true" : "false");

    return 0;
}
