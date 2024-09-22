#include <stdio.h>
#include <stdbool.h>  


#define z (true && false || false)  // Operações lógicas AND (&&) e OR (||)

int abc;     
bool def;    

int main() {

    abc = 0;    
    def = false; 


    printf("Valor de z: %s\n", z ? "true" : "false");
    printf("Valor de abc: %d\n", abc);
    printf("Valor de def: %s\n", def ? "true" : "false");

    return 0;
}
