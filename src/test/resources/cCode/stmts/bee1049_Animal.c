#include <stdio.h>
#include <stdbool.h>


char* classifiedAnimal;

char* getAnimal(char* hint1, char* hint2, char* hint3) {
    if (hint1 == "vertebrado") {
        if (hint2 == "ave") {
            if (hint3 == "carnivoro") {
                return "aguia";
            } else {
                return "pomba";
            }
        } else {
            if (hint3 == "onivoro") {
                return "homem";
            } else {
                return "vaca";
            }
        }
    } else {
        if (hint2 == "inseto") {
            if (hint3 == "hematofago") {
                return "pulga";
            } else {
                return "lagarta";
            }
        } else {
            if (hint3 == "hematofago") {
                return "sanguessuga";
            } else {
                return "minhoca";
            }
        }
    }
}


int main() {
    classifiedAnimal = getAnimal("vertebrado", "mamifero", "onivoro");
}
