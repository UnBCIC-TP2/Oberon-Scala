#include <stdio.h>
#include <stdbool.h>

struct history {
    int last, actual, next;
};

int main() {
    struct history years;

    years.last = 2019;
    years.actual = 2020;
    years.next = 2021;
}