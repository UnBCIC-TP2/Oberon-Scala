#include <stdio.h>
#include <stdbool.h>

struct date_struct {
    int day, month;
};
typedef struct date_struct date;

date d1;


int main() {
    d1.day = 5;
}