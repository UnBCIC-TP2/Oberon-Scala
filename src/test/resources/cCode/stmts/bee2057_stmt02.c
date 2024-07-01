#include <stdio.h>
#include <stdbool.h>

typedef int A[3];

int answer, i;
A a;


int main() {
    a[0] = 22;
    a[1] = 6;
    a[2] = -2;
    i = 0;
    answer = 0;
    while (true) {
        answer = answer + a[i];
        i = i + 1;
        if (i == 3) {
            break;
        }
    }
    answer = answer % 24;
}