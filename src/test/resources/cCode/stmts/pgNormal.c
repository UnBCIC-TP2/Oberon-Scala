#include <stdio.h>
#include <stdbool.h>


int pg, a1, r, N, n;


int main() {
    a1 = 2;
    r = 3;
    N = 5;
    n = 0;
    while (n <= N) {
         pg = a1 * pow(r, n - 1);
         printf("%d\n", pg);
         n = n + 1;
    }
}