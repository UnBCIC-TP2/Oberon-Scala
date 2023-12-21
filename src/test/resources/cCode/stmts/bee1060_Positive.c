#include <stdio.h>
#include <stdbool.h>


int i;
float ans, v;
float a[6];


int main() {
    i = 0;
    ans = 0;
    a[0] = 7;
    a[1] = -5;
    a[2] = 6;
    a[3] = -3.4000000953674316;
    a[4] = 4.599999904632568;
    a[5] = 12;
    while (true) {
        if (a[i] >= 0) {
            ans = ans + 1;
        }
        i = i + 1;
        if (i == 6) {
            break;
        }
    }
}