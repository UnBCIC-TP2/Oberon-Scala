#include <stdio.h>

void write_file(const char *filename, const char *content) {
    FILE *file = fopen(filename, "w");  
    if (file != NULL) {
        fprintf(file, "%s\n", content); 
        fclose(file);               
    } else {
        printf("Erro ao abrir o arquivo para escrita.\n");
    }
}

int main() {
    const char *x = "src/test/resources/stdlib/plainFile.txt";
    const char *y = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.";

    write_file(x, y);

    return 0;
}
