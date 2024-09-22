#include <stdio.h>
#include <stdbool.h>

bool p, q, r;


int main() {
    p = true;
    q = false;
    r = p && ~q;
    if (r == true) {
         printf("%d\n", r);
    }
}