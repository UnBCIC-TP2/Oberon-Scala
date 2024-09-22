#include <math.h>
#include <stdio.h>
#include <stdbool.h>
#include <stdlib.h>


int distance, fuel, consumption;


int main() {
    distance = 800;
    fuel = 20;
    consumption = distance / fuel;
    printf("%d\n", consumption);
}
