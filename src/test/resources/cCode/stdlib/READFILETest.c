#include <stdio.h>
#include <stdlib.h>

#define BUFFER_SIZE 256  // Tamanho do buffer para leitura de linhas


char *read_file(const char *filename) {
    FILE *file = fopen(filename, "r");  
    if (file == NULL) {
        return NULL;  
    }

    
    char *content = malloc(BUFFER_SIZE * sizeof(char));
    if (content == NULL) {
        fclose(file);
        return NULL;  
    }

    
    size_t total_read = 0;
    while (fgets(content + total_read, BUFFER_SIZE, file) != NULL) {
        total_read += strlen(content + total_read);
    }

    fclose(file);  
    return content;  
}

int main() {
    const char *x = "src/test/resources/stdlib/plainFile.txt";
    char *y;

    
    y = read_file(x);
    if (y != NULL) {
        printf("Conteúdo do arquivo:\n%s", y);
        free(y); 
    } else {
        printf("Erro ao ler o arquivo.\n");
    }

    return 0;
}
