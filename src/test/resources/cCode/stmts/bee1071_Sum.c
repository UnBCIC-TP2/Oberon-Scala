#include <stdio.h>
#include <stdbool.h>


int input1, input2, ans, x, y, aux;


int main() {
    input1 = 15;
    input2 = 12;
    ans = 0;
    x = input1;
    y = input2 + 1;
    if (input2 > input1) {
        x = input2;
        y = input1;
    }
    while (true) {
        x = x - 1;
        aux = x - x / 2 * 2;
        if (aux == 1) {
            ans = ans + x;
        }
        if (x == y) {
            break;
        }
    }
}
