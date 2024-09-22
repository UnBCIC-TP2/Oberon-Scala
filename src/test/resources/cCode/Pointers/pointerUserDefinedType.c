#include <math.h>
#include <stdio.h>
#include <stdbool.h>
#include <stdlib.h>

struct myType_struct {
    int size;
    bool active;
};
typedef struct myType_struct myType;

myType* p;
myType x;


int main() {
    p = (myType*)malloc(sizeof(myType));
    x.size = 10;
    x.active = true;
    *p = x;

    free(p);
}
