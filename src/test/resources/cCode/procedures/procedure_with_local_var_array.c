#include <stdio.h>
#include <stdbool.h>


int global[1];

int Double(int argument[]) {
    int local[1];
    local[0] = argument[0] * 2;
    return local[0];
}


int main() {
    global[0] = 10;
    global[0] = Double(global);
    printf("%d\n", global[0]);
}