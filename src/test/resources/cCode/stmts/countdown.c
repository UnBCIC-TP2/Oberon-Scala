#include <stdio.h>
#include <stdbool.h>


int counter;


int main() {
    counter = 10;
    while (true) {
        counter = counter - 1;
        if (counter >= 0) {
            break;
        }
    }
    printf("%d\n", counter);
}