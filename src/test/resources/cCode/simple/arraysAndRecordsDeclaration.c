#include <stdio.h>
#include <stdbool.h>

typedef int Grades[5];
struct Student_struct {
    int id;
    Grades grades;
};
typedef struct Student_struct Student;
typedef Student Students[5];
struct Classroom_struct {
    int room_id;
    Students students;
};
typedef struct Classroom_struct Classroom;

int number;
Grades standalone_grades;
int array_ints[10];
Student fulano;
Classroom tp2;


int main() {}