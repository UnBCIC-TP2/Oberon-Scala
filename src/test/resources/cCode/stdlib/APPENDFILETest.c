#include <stdio.h>
#include <string.h>

void write_file(const char *filename, const char *content) {
    FILE *file = fopen(filename, "w");  
    if (file != NULL) {
        fprintf(file, "%s\n", content);  
        fclose(file);                  
}

void append_file(const char *filename, const char *content) {
    FILE *file = fopen(filename, "a");  
    if (file != NULL) {
        fprintf(file, "%s\n", content);  
        fclose(file);                  
    }
}

void read_file(const char *filename) {
    FILE *file = fopen(filename, "r");  
    char line[256];                    

    if (file != NULL) {
        printf("Conteúdo do arquivo %s:\n", filename);
        while (fgets(line, sizeof(line), file)) {
            printf("%s", line); 
        }
        fclose(file);  
    }
}

int main() {
    const char *x = "src/test/resources/stdlib/plainFile.txt";
    const char *y = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.";
    const char *z = "Testando append";
    
 
    write_file(x, y);
    
  
    append_file(x, z);
    
   
    read_file(x);

    return 0;
}
