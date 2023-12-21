#include <stdio.h>
#include <stdbool.h>


int i, answer, n, m;
int a[3];
int cnt[11];


int main() {
    a[0] = 5;
    a[1] = 8;
    a[2] = 3;
    cnt[0] = 0;
    cnt[1] = 0;
    cnt[2] = 0;
    cnt[3] = 0;
    cnt[4] = 0;
    cnt[5] = 0;
    cnt[6] = 0;
    cnt[7] = 0;
    cnt[8] = 0;
    cnt[9] = 0;
    i = 0;
    n = 10;
    m = 3;
    answer = n;
    while (true) {
        cnt[a[i]] = cnt[a[i]] + 1;
        if (cnt[a[i]] == 1) {
            answer = answer - 1;
        }
        i = i + 1;
        if (i == 3) {
            break;
        }
    }
}