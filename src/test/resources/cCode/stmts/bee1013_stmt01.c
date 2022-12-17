#include <stdio.h>
#include <stdbool.h>


int a, b, c, answer;

int abs(int a) {
    if (a < 0) {
        a = a * -1;
    }
    return a;
}

int maxValue(int a, int b) {
    return a + b + abs(a - b) / 2;
}


int main() {
    a = 7;
    b = 14;
    c = 106;
    answer = maxValue(maxValue(a, b), c);
}
