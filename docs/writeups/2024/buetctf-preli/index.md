---
comments: true
authors:
  - gr007
tags:
  - rev
  - buetctf
---

# BUETCTF 2024 preli

## Baby rev

> Difficulty: easy
> [chal](./rev/baby_rev/chal)

5 directories, 3 files
First let's see what the binary does by running it.

```sh
❯ ./chal
FLAG: asdfasdf

❯ ltrace ./chal
printf("FLAG: ")                                                                                   = 6
fgets(FLAG: asdfasdf
"asdfasdf\n", 33, 0x7921be6488e0)                                                            = 0x7ffd63ba3660
strcspn("asdfasdf\n", "\n")                                                                        = 8
+++ exited (status 255) +++

```

So, from the ltrace, it is clear that the flag is being taken as input and something happens and we get an exit with 255 status code.
Let's load the binary in ghidra and see what happens.

After analysis is complete and a few renaming, we can see the following main function:

```c
/* DISPLAY WARNING: Type casts are NOT being printed */

long main(void)

{
  int iVar1;
  size_t sVar2;
  long ret;
  long in_FS_OFFSET;
  char input [40];
  long local_10;
  
  local_10 = *(in_FS_OFFSET + 0x28);
  printf("FLAG: ");
  fgets(input,0x21,stdin);
  sVar2 = strcspn(input,"\n");
  input[sVar2] = '\0';
  iVar1 = is_valid_flag(input);
  if (iVar1 == 0) {
    ret = 0xffffffff;
  }
  else {
    ret = 0;
  }
  if (local_10 != *(in_FS_OFFSET + 0x28)) {
                    /* WARNING: Subroutine does not return */
    __stack_chk_fail();
  }
  return ret;
}
```

Let's look inside `is_valid_flag` function.

```c
/* DISPLAY WARNING: Type casts are NOT being printed */

long is_valid_flag(char *input)

{
  long ret;
  long in_FS_OFFSET;
  uint i;
  char obf [32];
  
  obf[0] = 'B';
  obf[1] = 'T';
  obf[2] = 'G';
  obf[3] = 'W';
  obf[4] = 'G';
  obf[5] = 'Q';
  obf[6] = '@';
  obf[7] = '|';
  obf[8] = 'b';
  obf[9] = '{';
  obf[10] = 'd';
  obf[11] = '}';
  obf[12] = 'j';
  obf[13] = 'K';
  obf[14] = ':';
  obf[15] = '@';
  obf[16] = 'W';
  obf[17] = '#';
  obf[18] = 'j';
  obf[19] = 'j';
  obf[20] = 'Q';
  obf[21] = '&';
  obf[22] = 'T';
  obf[23] = '\\';
  obf[24] = '*';
  obf[25] = 'r';
  obf[26] = 'J';
  obf[27] = 'R';
  obf[28] = 'K';
  obf[29] = 'p';
  obf[30] = 'm';
  obf[31] = 'b';
  i = 0;
  do {
    if (0x1f < i) {
      ret = 1;
LAB_00101204:
      if (*(in_FS_OFFSET + 0x28) != *(in_FS_OFFSET + 0x28)) {
                    /* WARNING: Subroutine does not return */
        __stack_chk_fail();
      }
      return ret;
    }
    if ((input[i] ^ i) != obf[i]) {
      ret = 0;
      goto LAB_00101204;
    }
    i = i + 1;
  } while( true );
}
```

We can see a very easy obfuscation is carried out inside a do-while loop. (It's actually a for loop. Ghidra just fails to recognize it as such.)
We start from i=0 and go all the way up to 0x1f. We xor the character in index i of our input with the index and compare it with the obfuscated flag's corresponding character. If they are not equal, we return 0 (false) and if all of the checks pass, we return 1 (true).

We can now reverse the logic and from the obfuscated flag, we can get the flag. We can do it because of xor's property. i.e, if a^b = c then a = b^c and also, b = a^c.

The [solution](./rev/baby_rev/sol.c):

```c
#include <stdio.h>

int main(){
  char obf [32];
  
  obf[0] = 'B';
  obf[1] = 'T';
  obf[2] = 'G';
  obf[3] = 'W';
  obf[4] = 'G';
  obf[5] = 'Q';
  obf[6] = '@';
  obf[7] = '|';
  obf[8] = 'b';
  obf[9] = '{';
  obf[10] = 'd';
  obf[11] = '}';
  obf[12] = 'j';
  obf[13] = 'K';
  obf[14] = ':';
  obf[15] = '@';
  obf[16] = 'W';
  obf[17] = '#';
  obf[18] = 'j';
  obf[19] = 'j';
  obf[20] = 'Q';
  obf[21] = '&';
  obf[22] = 'T';
  obf[23] = '\\';
  obf[24] = '*';
  obf[25] = 'r';
  obf[26] = 'J';
  obf[27] = 'R';
  obf[28] = 'K';
  obf[29] = 'p';
  obf[30] = 'm';
  obf[31] = 'b';
  for(int i = 0; i < 32; i++){
    printf("%c", obf[i] ^ i);
  }
}
```

We run it to get flag:

```sh
❯ ./sol
BUETCTF{jrnvfF4OG2xyE3BK2kPIWms}
```

flag: `BUETCTF{jrnvfF4OG2xyE3BK2kPIWms}`
