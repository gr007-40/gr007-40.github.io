#include <stdio.h>
#include <stdlib.h>

const int secret_len = 23;

void preserve(char* secret_ingredient) {
    for (int i = 0; i < secret_len; i++) {
        printf("%c", secret_ingredient[i]);
    }
    free(secret_ingredient);
}

void wait(char* cake) {
    for (int i = 0; i < secret_len; i++) {
        cake[i] = 'a' + (cake[i] - 84) % 26;
    }
}

void seperate_and_dwadle(char* cake) {
    for (int i = 0; i < secret_len / 2; i++) {
        for (int j = secret_len - 1; j >= 0; j--) {
            int k = (j + secret_len - 1) % secret_len;
            cake[j] ^= cake[k];
            cake[k] ^= cake[j];
            cake[j] ^= cake[k];
        }
    }
}

void defrost_and_dake(char* cake) {
    for (int i = 0; i < (2 << secret_len); i++) {
        for (int j = secret_len - 1; j >= 0; j--) {
            cake[j] ^= cake[(j + secret_len - 1) % secret_len];
        }
    }
}

char* cake_a_bake(char* cake) {
    void (*recipe[3])(char*) = {defrost_and_dake, seperate_and_dwadle, wait};
    for (int i = 0; i < 2; i++) {
        (*recipe[i])(cake);
    }
    char* leet_cake = (char*)malloc(24 * sizeof(char));
    leet_cake[0] = '1';
    leet_cake[1] = '3';
    leet_cake[2] = '4';
    leet_cake[3] = '5';
    leet_cake[4] = '7';
    leet_cake[5] = '0';
    leet_cake[6] = '!';
    leet_cake[7] = '@';
    leet_cake[8] = '_';
    leet_cake[24] = '\0';
    wait(leet_cake);
    puts(leet_cake);
    return cake;
}

char* get_a_cake() {
    char* some_cake = (char*)malloc(sizeof(char) * 23);
    some_cake[0] = 0x3c;
    some_cake[1] = 0x4e;
    some_cake[2] = 0x14;
    some_cake[3] = 0x70;
    some_cake[4] = 0x6e;
    some_cake[5] = 0x42;
    some_cake[6] = 0x70;
    some_cake[7] = 0x10;
    some_cake[8] = 0x29;
    some_cake[9] = 0x0e;
    some_cake[10] = 0x37;
    some_cake[11] = 0x6e;
    some_cake[12] = 0x11;
    some_cake[13] = 0x13;
    some_cake[14] = 0x54;
    some_cake[15] = 0x74;
    some_cake[16] = 0x27;
    some_cake[17] = 0x02;
    some_cake[18] = 0x54;
    some_cake[19] = 0x33;
    some_cake[20] = 0x68;
    some_cake[21] = 0x35;
    some_cake[22] = 0x6a;
    return some_cake;
}

int main() {
    char* cake =
        get_a_cake();  // get the cake from serve_the_cake(cake) function
    char* secret_ingredient = cake_a_bake(cake);
    preserve(secret_ingredient);
}