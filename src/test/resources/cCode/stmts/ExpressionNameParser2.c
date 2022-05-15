#include <stdio.h>
#include <stdbool.h>

struct type4_struct {
    int v1, v2;
};
typedef struct type4_struct type4;

int result;
type4 t4;


int main() {
    result = 1 + t4.v1;
    printf("%d\n", result);
}