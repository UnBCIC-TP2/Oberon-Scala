#include <stdio.h>
#include <stdbool.h>  


#define x 5
#define y 10
#define z true  // "True" em Oberon corresponde a "true" em C

int abc;     
bool def;   

int main() {
   
    abc = 0;     
    def = false; 

 
    printf("Valor de x: %d\n", x);
    printf("Valor de y: %d\n", y);
    printf("Valor de z: %s\n", z ? "true" : "false");
    printf("Valor de abc: %d\n", abc);
    printf("Valor de def: %s\n", def ? "true" : "false");

    return 0;
}
