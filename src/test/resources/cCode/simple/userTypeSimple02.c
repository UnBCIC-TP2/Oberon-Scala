#include <stdio.h>
#include <stdbool.h>

struct HALLS_struct {
    int matricula;
    bool integrante;
};
typedef struct HALLS_struct HALLS;
typedef HALLS HALLS_array[9];

int x;


int main() {
    scanf("%d", &x);
}