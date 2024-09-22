#include <stdio.h>
#include <stdbool.h>


int pg, a1, r, N, n, p;


int main() {
    a1 = 2;
    r = 3;
    N = 5;
    n = 0;

    while (n <= N) {
         p = pow(r, n - 1);
         pg = a1 * p;
         printf("%d\n", pg);
         n = n + 1;
    }
}