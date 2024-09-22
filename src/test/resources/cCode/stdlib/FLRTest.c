#include <stdio.h>
#include <math.h>  

int main() {
    double x, y;
    int z, w;

  
    w = (int)50.1;  
    x = 10.8;
    y = floor(x);   
    z = floor((double)w);  


    printf("Valor de x: %.1f\n", x);
    printf("Valor de y (FLOOR de x): %.1f\n", y);
    printf("Valor de w: %d\n", w);
    printf("Valor de z (FLOOR de w): %d\n", z);

    return 0;
}
