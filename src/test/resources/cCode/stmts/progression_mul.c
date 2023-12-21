#include <stdio.h>
#include <stdbool.h>


int an, d, n, z, pa;


int main() {
    an = 2;
    d = 3;
    z = 5;
    n = 1;
    while (true) {
        pa = an + (n - 1) * d;
        printf("%d\n", pa);
        n = n + 1;
        if (n <= z) {
            break;
        }
    }
}
