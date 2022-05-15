#include <stdio.h>
#include <stdbool.h>


int x, y;

int sum(int v1, int v2) {
    return v1 + v2;
}

void print(int v1) {
    printf("%d\n", v1);
}


int main() {
    scanf("%d", &x);
    scanf("%d", &y);
    print(sum(x, y));
}