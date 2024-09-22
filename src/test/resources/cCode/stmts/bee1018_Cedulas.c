#include <math.h>
#include <stdio.h>
#include <stdbool.h>
#include <stdlib.h>

typedef int intArray[7];

int i, v;
intArray banknotesValues, banknotesNeeded;
float value;


int main() {
    value = 576.73;
    i = 0;
    
    banknotesValues[0] = 100;
    banknotesValues[1] = 50;
    banknotesValues[2] = 20;
    banknotesValues[3] = 10;
    banknotesValues[4] = 5;
    banknotesValues[4] = 2;
    banknotesValues[4] = 1;

    while(i < 7) {
        banknotesNeeded[i] = (float)value/banknotesNeeded[i];
        value -= banknotesValues[i]*banknotesNeeded[i];
        i++;
    }
}
