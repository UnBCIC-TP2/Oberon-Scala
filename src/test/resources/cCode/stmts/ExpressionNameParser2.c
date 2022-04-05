#include <stdio.h>
#include <stdbool.h>

struct type4 {
    int v1, v2;
};

int main() {
    int result;
    struct type4 t4;

    result = 1 + t4.v1;
    printf("%d\n", result);
}