#include <stdio.h>
#include <stdlib.h>
#include <string.h>

typedef struct {
    char nome[100];  
    int idade;
} Aluno;

typedef float Notas[3];

int main() {
  
    Aluno *a = (Aluno *)malloc(sizeof(Aluno));
    Notas *b = (Notas *)malloc(sizeof(Notas));

    
    strcpy(a->nome, "Manuela");  
    a->idade = 23;

    (*b)[0] = 9.5;
    (*b)[2] = 9.0;

   
    printf("Nome: %s\n", a->nome);
    printf("Idade: %d\n", a->idade);
    printf("Nota 1: %.1f\n", (*b)[0]);
    printf("Nota 3: %.1f\n", (*b)[2]);


    free(a);
    free(b);

    return 0;
}
