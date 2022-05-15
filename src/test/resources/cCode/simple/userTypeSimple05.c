#include <stdio.h>
#include <stdbool.h>

typedef int simple[10];
struct complicated_struct {
    int variable1;
    bool variable2;
    simple variable3;
};
typedef struct complicated_struct complicated;

simple x;
simple y;
complicated z;


int main() {}