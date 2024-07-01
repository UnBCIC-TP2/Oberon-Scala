#include <stdio.h>
#include <stdbool.h>

typedef int A[3];

int answer, i;
A a;


int main() {
    a[0] = 0;
    a[1] = 3;
    a[2] = -4;
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