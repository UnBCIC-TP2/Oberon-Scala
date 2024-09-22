#include <math.h>
#include <stdio.h>
#include <stdbool.h>
#include <stdlib.h>


int averageSpeed, time, liters;


int main() {
    averageSpeed = 80;
    time = 10;
    liters = averageSpeed * time / 12;
    printf("%d\n", liters);
}
