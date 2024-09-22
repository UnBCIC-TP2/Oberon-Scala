#include <math.h>
#include <stdio.h>
#include <stdbool.h>
#include <stdlib.h>

struct history_struct {
    int last, actual, next;
};
typedef struct history_struct history;

history years;


int main() {
    years.last = 2019;
    years.actual = 2020;
    years.next = 2021;
}