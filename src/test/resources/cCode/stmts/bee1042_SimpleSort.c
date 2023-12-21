#include <stdio.h>
#include <stdbool.h>


int x, y, Z;
int input[3];
int input2[3];
int ans[6];


int main() {
    x = 0;
    y = 0;
    input[0] = 7;
    input[1] = 21;
    input[2] = -14;
    input2[0] = 7;
    input2[1] = 21;
    input2[2] = -14;
    while (true) {
        x = 0;
        while (true) {
            if (input[x] > input[x + 1]) {
                Z = input[x];
                input[x] = input[x + 1];
                input[x + 1] = Z;
            }
            x = x + 1;
            if (x == 2) {
                break;
            }
        }
        y = y + 1;
        if (y == 2) {
            break;
        }
    }
    ans[0] = input[0];
    ans[1] = input[1];
    ans[2] = input[2];
    ans[3] = input2[0];
    ans[4] = input2[1];
    ans[5] = input2[2];
}
