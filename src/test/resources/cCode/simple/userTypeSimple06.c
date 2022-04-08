#include <stdio.h>
#include <stdbool.h>

typedef int cheesewithbread[10];
struct cheesewithoutbread_struct {
    int var1;
    cheesewithbread var2;
};
typedef struct cheesewithoutbread_struct cheesewithoutbread;
typedef cheesewithoutbread cheesewithhalfabread[100000];



int main() {}