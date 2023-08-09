---
comments: true
authors:
  - gr007
tags:
  - rev
  - '2023'
  - flaghunt
---

# Nest

## Description

Winter fades, spring awakes,
The cuckoo sings, the earth shakes,
Summer's near, but first a rest,
A nest to build, with utmost zest.

<img src="https://cdn.discordapp.com/attachments/997271582005264384/1085989730694479922/gr007_cuckoos_nest_built_with_care_has_eggs_inside_ultra_realis_2ff72d76-5200-4465-a47b-92c96ff6f4c8.png"  width="400"  height="400" alt="cuckoo_nest">

**flag format:** `coderush{fl4g}`

>Author: `gr007`

[nest](nest)

## Solution

We are given a binary ELF x64 executable. it asks for an input. presumably for the flag.
after opening the file in ghidra and renaming a couple of variables, we get the following c like source code:
```c

/* DISPLAY WARNING: Type casts are NOT being printed */

undefined8 main(void)

{
    char c;
    long in_FS_OFFSET;
    int i;
    char flag [40];
    long local_10;

    local_10 = *(in_FS_OFFSET + 40);
    read(1,flag,37);
    for (i = 0; i < 37; i += 1) {
        if (flag[i] == '\n') {
            c = '\0';
        }
        else {
            c = flag[i];
        }
        flag[i] = c;
    }
    if (flag[36] == '}' &&
        (flag[6] == 's' &&
        (flag[25] == '5' &&
        (flag[2] == 'd' &&
        (flag[27] == '1' &&
        (flag[0] == 'c' &&
        (flag[21] == 'c' &&
        (flag[17] == 'n' &&
        (flag[4] == 'r' &&
        (flag[29] == '_' &&
        (flag[20] == '_' &&
        (flag[13] == '0' &&
        (flag[32] == 'r' &&
        (flag[11] == 'c' &&
        (flag[3] == 'e' &&
        (flag[30] == '5' &&
        (flag[26] == '_' &&
        (flag[15] == '_' &&
        (flag[8] == '{' &&
        (flag[35] == 'g' &&
        (flag[28] == 'n' &&
        (flag[16] == '0' &&
        (flag[18] == 'l' &&
        (flag[24] == '3' &&
        (flag[22] == '0' &&
        (flag[23] == 'm' &&
        (flag[31] == 'p' &&
        (flag[14] == '0' &&
        (flag[1] == 'o' &&
        (flag[33] == '1' &&
        (flag[12] == 'k' &&
        (flag[10] == 'u' &&
        (flag[5] == 'u' &&
        (flag[7] == 'h' && (flag[9] == 'c' && (flag[34] == 'n' && flag[19] == 'y')))))))))))))))))))
        ))))))))))))))))) {
        puts("Yes! That\'s the saying.");
    }
    else {
        puts("No, that\'s not the saying.");
    }
    if (local_10 != *(in_FS_OFFSET + 40)) {
                    /* WARNING: Subroutine does not return */
        __stack_chk_fail();
    }
    return 0;
}


```

So, from the source code, it is obvious that the flags characters are being checked without order. we bring it in order
or just write the following c code to get the flag:
```c
int main(){
    char flag [41];
    flag[36] = '}';
    flag[6] = 's';
    flag[25] = '5';
    flag[2] = 'd';
    flag[27] = '1';
    flag[0] = 'c';
    flag[21] = 'c';
    flag[17] = 'n';
    flag[4] = 'r';
    flag[29] = '_';
    flag[20] = '_';
    flag[13] = '0';
    flag[32] = 'r';
    flag[11] = 'c';
    flag[3] = 'e';
    flag[30] = '5';
    flag[26] = '_';
    flag[15] = '_';
    flag[8] = '{';
    flag[35] = 'g';
    flag[28] = 'n';
    flag[16] = '0';
    flag[18] = 'l';
    flag[24] = '3';
    flag[22] = '0';
    flag[23] = 'm';
    flag[31] = 'p';
    flag[14] = '0';
    flag[1] = 'o';
    flag[33] = '1';
    flag[12] = 'k';
    flag[10] = 'u';
    flag[5] = 'u';
    flag[7] = 'h';
    flag[9] = 'c';
    flag[34] = 'n';
    flag[19] = 'y';
    puts(flag);
    return 0;
}

```

The flag is : `coderush{cuck00_0nly_c0m35_1n_5pr1ng}`
