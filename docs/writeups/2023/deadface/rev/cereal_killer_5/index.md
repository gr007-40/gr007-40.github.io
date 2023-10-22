---
comments: true
authors:
  - gr007
tags:
  - rev
  - deadface
  - rev
---
# Cereal Killer 05

## Description

> Points: 50\
> Created by: `TheZeal0t`

We think Dr. Geschichter of Lytton Labs likes to use his favorite monster cereal as a password for ALL of his accounts! See if you can figure out what it is, and keep it handy! Choose one of the binaries to work with.

Enter the answer as flag{WHATEVER-IT-IS}.

[Download file](./cereal)

## Solution

When the binary is run, it askes for Dr. Geschichter's favourite cereal and entity.

```sh
deadface/rev/cereal_killer_5 on  master [!?]
❯ ./cereal
Dr. Geschichter, just because he is evil, doesn't mean he doesn't have a favorite cereal.
Please enter the passphrase, which is based off his favorite cereal and entity: adsfadff
notf1aq{you-guessed-it---this-is-not-the-f1aq}
```

I don't know if it is the intended solution, I just ran the program in gdb, interrupted it when it asked for input and search for any string having `flag{}` and there it was, the flag.

```sh
deadface/rev/cereal_killer_5 on  master [!?]
❯ gdb cereal
GNU gdb (GDB) 13.2
Copyright (C) 2023 Free Software Foundation, Inc.
License GPLv3+: GNU GPL version 3 or later <http://gnu.org/licenses/gpl.html>
This is free software: you are free to change and redistribute it.
There is NO WARRANTY, to the extent permitted by law.
Type "show copying" and "show warranty" for details.
[ Legend: Modified register | Code | Heap | Stack | String ]
────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────── registers ────
$eax   : 0xfffffe00
$ebx   : 0x0
$ecx   : 0x5655a5b0  →  0x00000000
$edx   : 0x400
$esp   : 0xffffc2c0  →  0xffffc328  →  0xffffc998  →  0xffffcc38  →  0x00000000
$ebp   : 0xffffc328  →  0xffffc998  →  0xffffcc38  →  0x00000000
$esi   : 0xf7e20e34  →  "L\r""
$edi   : 0xf7e207c8  →  0x00000000
$eip   : 0xf7fc7579  →  <__kernel_vsyscall+9> pop ebp
$eflags: [ZERO carry PARITY adjust sign trap INTERRUPT direction overflow resume virtualx86 identification]
$cs: 0x23 $ss: 0x2b $ds: 0x2b $es: 0x2b $fs: 0x00 $gs: 0x63
────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────── stack ────
0xffffc2c0│+0x0000: 0xffffc328  →  0xffffc998  →  0xffffcc38  →  0x00000000      ← $esp
0xffffc2c4│+0x0004: 0x00000400
0xffffc2c8│+0x0008: 0x5655a5b0  →  0x00000000
0xffffc2cc│+0x000c: 0xf7d1e0d7  →  0xfff0003d ("="?)
0xffffc2d0│+0x0010: 0xf7e215c0  →  0xfbad2288
0xffffc2d4│+0x0014: 0xf7e20e34  →  "L\r""
0xffffc2d8│+0x0018: 0xffffc328  →  0xffffc998  →  0xffffcc38  →  0x00000000
0xffffc2dc│+0x001c: 0xf7c827b2  →  <_IO_file_underflow+674> add esp, 0x10
──────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────── code:x86:32 ────
   0xf7fc7573 <__kernel_vsyscall+3> mov    ebp, esp
   0xf7fc7575 <__kernel_vsyscall+5> sysenter
   0xf7fc7577 <__kernel_vsyscall+7> int    0x80
 → 0xf7fc7579 <__kernel_vsyscall+9> pop    ebp
   0xf7fc757a <__kernel_vsyscall+10> pop    edx
   0xf7fc757b <__kernel_vsyscall+11> pop    ecx
   0xf7fc757c <__kernel_vsyscall+12> ret
   0xf7fc757d <__kernel_vsyscall+13> int3
   0xf7fc757e                  nop
──────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────── threads ────
[#0] Id 1, Name: "cereal", stopped 0xf7fc7579 in __kernel_vsyscall (), reason: SIGINT
────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────── trace ────
[#0] 0xf7fc7579 → __kernel_vsyscall()
[#1] 0xf7d1e0d7 → read()
[#2] 0xf7c82690 → _IO_file_underflow()
[#3] 0xf7c84f1a → _IO_default_uflow()
[#4] 0xf7c62b63 → add esp, 0x10
[#5] 0xf7c56df9 → __isoc99_scanf()
[#6] 0x5655637a → main()
─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────
gef➤  grep flag{
[+] Searching 'flag{' in memory
[+] In '[stack]'(0xfffdc000-0xffffe000), permission=rw-
  0xffffca0b - 0xffffca2b  →   "flag{XENO-DO-DO-DO-DO-DO-DOOOOO}"
gef➤
```
flag: `flag{XENO-DO-DO-DO-DO-DO-DOOOOO}`
