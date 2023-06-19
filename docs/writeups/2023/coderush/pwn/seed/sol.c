#include <stdio.h>
#include <stdlib.h>

int main() {
    srand(rand());
    for (int i = 0; i < 100; i += 1) {
        printf("%d\n", rand()%100000);
    }
    return 0;
}