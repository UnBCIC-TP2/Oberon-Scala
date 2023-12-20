#include <stdio.h>
#include <stdbool.h>


int i, answer, n, m;
int a[4];
int cnt[4];


int main() {
    a[0] = 2;
    a[1] = 1;
    a[2] = 3;
    a[3] = 3;
    cnt[0] = 0;
    cnt[1] = 0;
    cnt[2] = 0;
    cnt[3] = 0;
    i = 0;
    n = 3;
    m = 4;
    answer = n;
    while (true) {
        cnt[a[i]] = cnt[a[i]] + 1;
        if (cnt[a[i]] == 1) {
            answer = answer - 1;
        }
        i = i + 1;
        if (i == 4) {
            break;
        }
    }
}
