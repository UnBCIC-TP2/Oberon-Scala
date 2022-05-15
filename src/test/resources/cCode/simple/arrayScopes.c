#include <stdio.h>
#include <stdbool.h>


int table_global[2];

void Copy(int table_arg[]) {
    int table_local[2];
    table_local[0] = table_arg[0];
    table_local[1] = table_arg[1];
}


int main() {
    table_global[0] = 10;
    table_global[1] = 20;
    Copy(table_global);
}