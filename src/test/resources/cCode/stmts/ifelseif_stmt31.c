#include <stdio.h>
#include <stdbool.h>


int first, second, third, temp;


int main() {
    scanf("%d", &first);
    scanf("%d", &second);
    scanf("%d", &third);
    if (first > second) {
        temp = first;
        first = second;
        second = temp;
    } else {
        if (first > third) {
            temp = first;
            first = third;
            third = temp;
        } else {
            if (second > third) {
                temp = second;
                second = third;
                third = temp;
            }
        }
    }
    printf("%d\n", first);
    printf("%d\n", second);
    printf("%d\n", third);
}