---
comments: true
authors:
  - gr007
tags:
  - rev
  - deadface
  - rev
---
# Cereal Killer 02

## Description

> Points: 100\
> Created by: `TheZeal0t`

luciafer can be a bit of trouble sometimes, but she can put away the sugary monster cereals with the best of them! She has a favorite, too, and it is based on her favorite monster. See if you can figure it out! Select the binary for your preferred platform.

[Download file]()

## Solution

when the binary is run, it asks for Luciafer's favorite breakfast cereal.

```sh
deadface/rev/cereal_killer_2 on  master [?]
❯ ./cereal
Luciafer also loves Halloween, so she, too, LOVES SPOOKY CEREALS!
She has different favorite villain from 70-80's horror movies.
What is Luciafer's favorite breakfast cereal? adfsda
INCORRECT....: I'm afraid that is not Lucia's current favorite monster cereal.  She is kind of capricious, you know, so it changes often.
```

The following code (after some cleanup) snippets are the interesting parts from looking at it in ghidra.

```c
  puts("Luciafer also loves Halloween, so she, too, LOVES SPOOKY CEREALS!");
  puts("She has different favorite villain from 70-80\'s horror movies.");
  printf("What is Luciafer\'s favorite breakfast cereal? ");
  fgets(input,0xfff,_stdin);
  decode_str(input,63,
             "\b=3?\x1562GR\x12\x1bekHA\v<\x14\x01\x1d4A[)\x1b\x13L&\x024+\x16\x06@\x17\r8_\"\x02=\x 1c\bK5\\Hi\x0f\x13L/1\x11K-\x1aWIejS\x1c"
             ,correct);
  iVar1 = strncmp(correct,"CORRECT!!!!!",12);
  if (iVar1 == 0) {
    puts(correct);
  }
  else {
    printf("%s",
           "INCORRECT....: I\'m afraid that is not Lucia\'s current favorite monster cereal.  She is  kind of capricious, you know, so it changes often.\n"
          );
  }
```

```c
void decode_str(char *input,int len,char *flag,char *correct)

{
  int mod;
  int i;

  mod = 0;
  i = 0;
  while (i < len) {
    correct[i] = flag[i] ^ input[mod];
    i += 1;
    mod += 1;
    if (11 < mod) {
      mod = 0;
    }
  }
  correct[i] = '\0';
  return;
}
```

After analyzing the `decode_str` function, we can see that we cannot use the trick we used in `[cereal killer 1](../cereal_killer_1/index.md)` to bypass the check but we can do something pretty interesting instead. The `decode_str` takes our input and uses it as a key for rotating xor cipher to decode the flag string that has been encoded by the original key. And we can see that the first 12 characters are later compared in the main function with the string `CORRECT!!!!!`. Now, if we input our favourite cereal as `CORRECT!!!!!`, we sould get a string comparison with our cereal and `CORRECT!!!!!` in the main function.

```sh
[ Legend: Modified register | Code | Heap | Stack | String ]
───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────── registers ────
$eax   : 0xffffbc2c  →  0x6d61724b ("Kram"?)
$ebx   : 0x56558ff4  →  <_GLOBAL_OFFSET_TABLE_+0> call 0x86559037
$ecx   : 0x4e
$edx   : 0x3f
$esp   : 0xffffac10  →  0xffffbc2c  →  0x6d61724b ("Kram"?)
$ebp   : 0xffffcc38  →  0x00000000
$esi   : 0xffffcd0c  →  0xffffcf45  →  "VIMRUNTIME=/usr/share/nvim/runtime"
$edi   : 0xf7ffcb80  →  0x00000000
$eip   : 0x56556301  →  <main+202> call 0x56556080 <strncmp@plt>
$eflags: [zero carry parity ADJUST SIGN trap INTERRUPT direction overflow resume virtualx86 identification]
$cs: 0x23 $ss: 0x2b $ds: 0x2b $es: 0x2b $fs: 0x00 $gs: 0x63
───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────── stack ────
0xffffac10│+0x0000: 0xffffbc2c  →  0x6d61724b    ← $esp
0xffffac14│+0x0004: 0x56557187  →  "CORRECT!!!!!"
0xffffac18│+0x0008: 0x00000c ("
                               "?)
0xffffac1c│+0x000c: 0xffffbc2c  →  0x6d61724b
0xffffac20│+0x0010: 0x56557008  →  "INCORRECT....: I'm afraid that is not Lucia's curr[...]"
0xffffac24│+0x0014: 0x56557094  →   or BYTE PTR ds:0x36153f33, bh
0xffffac28│+0x0018: 0x00000000
0xffffac2c│+0x001c: "CORRECT!!!!!\n"
─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────── code:x86:32 ────
   0x565562f9 <main+194>       push   eax
   0x565562fa <main+195>       lea    eax, [ebp-0x100c]
   0x56556300 <main+201>       push   eax
 → 0x56556301 <main+202>       call   0x56556080 <strncmp@plt>
   ↳  0x56556080 <strncmp@plt+0>  jmp    DWORD PTR [ebx+0x1c]
      0x56556086 <strncmp@plt+6>  push   0x20
      0x5655608b <strncmp@plt+11> jmp    0x56556030
      0x56556090 <_start+0>       endbr32
      0x56556094 <_start+4>       xor    ebp, ebp
      0x56556096 <_start+6>       pop    esi
─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────── arguments (guessed) ────
strncmp@plt (
   [sp + 0x0] = 0xffffbc2c → 0x6d61724b,
   [sp + 0x4] = 0x56557187 → "CORRECT!!!!!"
)
─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────── threads ────
[#0] Id 1, Name: "cereal", stopped 0x56556301 in main (), reason: BREAKPOINT
───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────── trace ────
[#0] 0x56556301 → main()
────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────
gef➤  x/s 0xffffbc2c
0xffffbc2c:     "KramPuffs3:D(\a\023YyWU<\025`z\bX\\\036tGw\1777'a6,{\020pPx_\\j\024}iHL\\\036}tR\037\f;vhD)\034N"
gef➤
```

```sh
[ Legend: Modified register | Code | Heap | Stack | String ]
───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────── registers ────
$eax   : 0xffffbc2c  →  "CORRECT!!!!! : flag{GramPa-KRAMpus-Is-Comin-For-Da[...]"
$ebx   : 0x56558ff4  →  <_GLOBAL_OFFSET_TABLE_+0> call 0x86559037
$ecx   : 0x7d
$edx   : 0x3f
$esp   : 0xffffac10  →  0xffffbc2c  →  "CORRECT!!!!! : flag{GramPa-KRAMpus-Is-Comin-For-Da[...]"
$ebp   : 0xffffcc38  →  0x00000000
$esi   : 0xffffcd0c  →  0xffffcf45  →  "VIMRUNTIME=/usr/share/nvim/runtime"
$edi   : 0xf7ffcb80  →  0x00000000
$eip   : 0x56556301  →  <main+202> call 0x56556080 <strncmp@plt>
$eflags: [zero carry parity ADJUST SIGN trap INTERRUPT direction overflow resume virtualx86 identification]
$cs: 0x23 $ss: 0x2b $ds: 0x2b $es: 0x2b $fs: 0x00 $gs: 0x63
───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────── stack ────
0xffffac10│+0x0000: 0xffffbc2c  →  "CORRECT!!!!! : flag{GramPa-KRAMpus-Is-Comin-For-Da[...]"     ← $esp
0xffffac14│+0x0004: 0x56557187  →  "CORRECT!!!!!"
0xffffac18│+0x0008: 0x00000c ("
                               "?)
0xffffac1c│+0x000c: 0xffffbc2c  →  "CORRECT!!!!! : flag{GramPa-KRAMpus-Is-Comin-For-Da[...]"
0xffffac20│+0x0010: 0x56557008  →  "INCORRECT....: I'm afraid that is not Lucia's curr[...]"
0xffffac24│+0x0014: 0x56557094  →   or BYTE PTR ds:0x36153f33, bh
0xffffac28│+0x0018: 0x00000000
0xffffac2c│+0x001c: "KramPuffs3:D\n"
─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────── code:x86:32 ────
   0x565562f9 <main+194>       push   eax
   0x565562fa <main+195>       lea    eax, [ebp-0x100c]
   0x56556300 <main+201>       push   eax
 → 0x56556301 <main+202>       call   0x56556080 <strncmp@plt>
   ↳  0x56556080 <strncmp@plt+0>  jmp    DWORD PTR [ebx+0x1c]
      0x56556086 <strncmp@plt+6>  push   0x20
      0x5655608b <strncmp@plt+11> jmp    0x56556030
      0x56556090 <_start+0>       endbr32
      0x56556094 <_start+4>       xor    ebp, ebp
      0x56556096 <_start+6>       pop    esi
─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────── arguments (guessed) ────
strncmp@plt (
   [sp + 0x0] = 0xffffbc2c → "CORRECT!!!!! : flag{GramPa-KRAMpus-Is-Comin-For-Da[...]",
   [sp + 0x4] = 0x56557187 → "CORRECT!!!!!"
)
─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────── threads ────
[#0] Id 1, Name: "cereal", stopped 0x56556301 in main (), reason: BREAKPOINT
───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────── trace ────
[#0] 0x56556301 → main()
────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────
gef➤  c
Continuing.
CORRECT!!!!! : flag{GramPa-KRAMpus-Is-Comin-For-Da-Bad-Kids!!!}
[Inferior 1 (process 11203) exited normally]
gef➤
```

flag: `flag{GramPa-KRAMpus-Is-Comin-For-Da-Bad-Kids!!!}`

