#include <stdio.h>
#include <stdbool.h>

typedef int intArray[5];

int value, x, y;
intArray banknotesValues;


int main() {
    x = 1;
    y = 3;
    banknotesValues[0] = 2;
    banknotesValues[1] = 3;
    banknotesValues[2] = 4;
    banknotesValues[3] = 5;
    banknotesValues[4] = 6;
    value = banknotesValues[x] * y;
}
