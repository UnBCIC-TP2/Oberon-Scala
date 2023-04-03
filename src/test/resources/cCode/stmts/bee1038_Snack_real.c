#include <stdio.h>
#include <stdbool.h>

typedef float realArray[5];

int x, y;
float value;
realArray banknotesValues;


int main() {
    x = 1;
    y = 3;
    banknotesValues[0] = 2.5;
    banknotesValues[1] = 3.5;
    banknotesValues[2] = 4.5;
    banknotesValues[3] = 5.5;
    banknotesValues[4] = 6.5;
    value = banknotesValues[x] * y;
}
