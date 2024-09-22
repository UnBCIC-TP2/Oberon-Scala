#include <stdio.h>

#define SIZE 10  // Definindo o tamanho do array como constante

typedef int simple[SIZE];  


void initialize_array(simple pogArray) {
    int i = 0;

    do {
        pogArray[i] = i * 5;
        i++;
    } while (i < SIZE);
}

int main() {
    simple pogArray;  


    initialize_array(pogArray);

  
    for (int i = 0; i < SIZE; i++) {
        printf("pogArray[%d] = %d\n", i, pogArray[i]);
    }

    return 0;
}
