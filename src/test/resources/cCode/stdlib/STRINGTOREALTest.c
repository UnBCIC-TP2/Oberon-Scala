#include <stdio.h>
#include <stdlib.h>  

int main() {
    const char *x = "-8.0";  
    const char *w = "2.5";   
    float y, z;


    char *endptr;  
    y = strtof(x, &endptr);  
    z = strtof(w, &endptr);  


    printf("Valor de x: %s, convertido para y: %.2f\n", x, y);
    printf("Valor de w: %s, convertido para z: %.2f\n", w, z);

    return 0;
}
