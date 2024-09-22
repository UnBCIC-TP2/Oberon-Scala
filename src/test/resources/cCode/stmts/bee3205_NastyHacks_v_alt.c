#include <stdio.h>
#include <stdbool.h>


int i, temp, temp2, answer;
int a[3];
int b[3];
int c[3];
int ans[3];


int main() {
    a[0] = 0;
    a[1] = 100;
    a[2] = 100;
    b[0] = 100;
    b[1] = 130;
    b[2] = 70;
    c[0] = 70;
    c[1] = 30;
    c[2] = 20;
    ans[0] = 0;
    ans[1] = 0;
    ans[2] = 0;
    i = 0;
    temp = 0;
    answer = 0;
    while (true) {
        temp = b[i] - c[i];
        temp2 = temp - a[i];
        answer = answer + temp2;
        i = i + 1;
        if (i == 3) {
            break;
        }
    }
}
