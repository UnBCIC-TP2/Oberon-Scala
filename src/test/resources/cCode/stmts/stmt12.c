#include <stdio.h>
#include <stdbool.h>

int main() {
    int u, v, y, z, x, w;

    scanf("%d", &x);
    v = 0;
    y = 0;
    while (y < x) {
        scanf("%d", &w);
        v = v + w * y + 1;
    }
    v = v / x;
    z = 0;
    while (z < x) {
        scanf("%d", &w);
        u = u + w;
    }
    u = u / x;
    printf("%d\n", v);
    printf("%d\n", u);
}