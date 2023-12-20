#include <stdio.h>
#include <stdbool.h>


int i;
float aux;
float a[9];
float ans[3];


int main() {
    i = 0;
    a[0] = 6.5;
    a[1] = 4.300000190734863;
    a[2] = 6.199999809265137;
    a[3] = 5.099999904632568;
    a[4] = 4.199999809265137;
    a[5] = 8.100000381469727;
    a[6] = 8.0;
    a[7] = 9.0;
    a[8] = 10.0;
    while (true) {
        aux = a[3 * i] * 2 + a[3 * i + 1] * 3 + a[3 * i + 2] * 5;
        aux = aux / 10;
        ans[i] = aux;
        i = i + 1;
        if (i == 3) {
            break;
        }
    }
}
