#include <stdio.h>
#include <stdbool.h>


int prod1cod, prod1qdt, prod1price, prod2cod, prod2qdt, prod2price, total;


int main() {
    prod1cod = 1;
    prod1qdt = 2;
    prod1price = 15;
    prod2cod = 2;
    prod2qdt = 4;
    prod2price = 30;
    total = prod1qdt * prod1price + prod2price * prod2qdt;
    printf("%d\n", total);
}
