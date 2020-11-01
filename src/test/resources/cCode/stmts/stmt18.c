#include <stdio.h>

int main() {
    int first, second, temp;
    scanf("%d", &first);
    scanf("%d", &second);

    if( first > second){
        temp = first;
        first = second;
        second = temp;
    }
}