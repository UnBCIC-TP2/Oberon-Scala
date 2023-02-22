#include <stdio.h>
#include <stdbool.h>


int anst, bonus, ansa, ansb;
int a[3];
int b[3];


int main() {
    a[0] = 12;
    a[1] = 24;
    a[2] = 15;
    b[0] = 42;
    b[1] = 12;
    b[2] = 20;
    bonus = 5;
    anst = 0;
    ansa = a[0] + a[1] / 2;
    if (a[2] % 2 == 1) {
        ansa = ansa + bonus;
    }
    ansb = b[0] + b[1] / 2;
    if (b[2] % 2 == 1) {
        ansb = ansa + bonus;
    }
    if (ansa > ansb) {
        anst = anst + 1;
    }
    if (ansb > ansa) {
        anst = anst + 2;
    }
}