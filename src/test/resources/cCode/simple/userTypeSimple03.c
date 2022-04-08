#include <stdio.h>
#include <stdbool.h>

typedef bool test_array[5];
struct tipo1_struct {
    int num;
    test_array numum;
};
typedef struct tipo1_struct tipo1;
struct tipo2_struct {
    tipo1 num_record;
};
typedef struct tipo2_struct tipo2;



int main() {}