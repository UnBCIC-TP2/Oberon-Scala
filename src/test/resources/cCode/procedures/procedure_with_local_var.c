#include <math.h>
#include <stdio.h>
#include <stdbool.h>
#include <stdlib.h>


int global;

int Double(int argument) {
    int local;
    local = argument * 2;
    return local;
}


int main() {
    global = 10;
    global = Double(global);
}