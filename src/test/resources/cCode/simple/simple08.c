#include <stdio.h>
#include <stdbool.h>  


#define x false
#define y true
#define z (true && false)  // Operação lógica AND (&&) entre true e false

int abc;     
bool def;    

int main() {
    
    abc = 0;     
    def = false;

    
    printf("Valor de x: %s\n", x ? "true" : "false");
    printf("Valor de y: %s\n", y ? "true" : "false");
    printf("Valor de z: %s\n", z ? "true" : "false");
    printf("Valor de abc: %d\n", abc);
    printf("Valor de def: %s\n", def ? "true" : "false");

    return 0;
}
