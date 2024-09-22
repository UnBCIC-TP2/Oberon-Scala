#include <math.h>
#include <stdio.h>
#include <stdbool.h>
#include <stdlib.h>

bool p, q, r;


int main() {
    p = true;
    q = false;
    r = p && ~q;
    if (r == true) {
         printf("%d\n", r);
    }
}