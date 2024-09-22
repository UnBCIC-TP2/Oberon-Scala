#include <stdio.h>
#include <stdbool.h> 


#define z (5 + 10 * 3)  // Expressão com precedência matemática (multiplicação antes da soma)

int abc;    
bool def;   

int main() {
 
    abc = 0;     
    def = false; 


    printf("Valor de z: %d\n", z);
    printf("Valor de abc: %d\n", abc);
    printf("Valor de def: %s\n", def ? "true" : "false");

    return 0;
}
