#include <stdio.h>
#include <stdbool.h>


int i, answer, n, m;
int a[6];
int cnt[5];


int main() {
    a[0] = 3;
    a[1] = 3;
    a[2] = 2;
    a[3] = 3;
    a[4] = 3;
    a[5] = 3;
    cnt[0] = 0;
    cnt[1] = 0;
    cnt[2] = 0;
    cnt[3] = 0;
    cnt[4] = 0;
    i = 0;
    n = 5;
    m = 6;
    answer = n;
    while (true) {
        cnt[a[i]] = cnt[a[i]] + 1;
        if (cnt[a[i]] == 1) {
            answer = answer - 1;
        }
        i = i + 1;
        if (i == 6) {
            break;
        }
    }
}
