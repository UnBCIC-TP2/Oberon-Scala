#include <stdio.h>
#include <stdlib.h>  

int main() {
    const char *x = "-8";   
    const char *w = "2";   
    int y, z;


    char *endptr;  
    y = (int)strtol(x, &endptr, 10);  
    z = (int)strtol(w, &endptr, 10);  


    printf("Valor de x: %s, convertido para y: %d\n", x, y);
    printf("Valor de w: %s, convertido para z: %d\n", w, z);

    return 0;
}
