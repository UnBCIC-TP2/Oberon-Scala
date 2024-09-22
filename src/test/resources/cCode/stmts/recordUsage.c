#include <math.h>
#include <stdio.h>
#include <stdbool.h>
#include <stdlib.h>

struct DATE_struct {
    int day, month;
};
typedef struct DATE_struct DATE;

int day_global;
DATE date1;


int main() {
    date1.day = 5;
    day_global = date1.day;
}