---
comments: true
authors:
  - gr007
tags:
  - rev
  - flaghunt
  - '2022'
  - xor
  - python
---

# Weird Code

## Description

Can you find the flag from the [file](./chal.txt) attached?

??? file_contents
    ```
      3           0 LOAD_CONST               1 (4919)
                  2 STORE_FAST               0 (k)

      4           4 LOAD_CONST               2 ('CTF_BD{__REDACTED__}')
                  6 STORE_FAST               1 (flag)

      5           8 BUILD_LIST               0
                 10 STORE_FAST               2 (n)

      6          12 LOAD_GLOBAL              0 (range)
                 14 LOAD_GLOBAL              1 (len)
                 16 LOAD_FAST                1 (flag)
                 18 CALL_FUNCTION            1
                 20 LOAD_CONST               3 (1)
                 22 BINARY_SUBTRACT
                 24 CALL_FUNCTION            1
                 26 GET_ITER
            >>   28 FOR_ITER                46 (to 76)
                 30 STORE_FAST               3 (i)

      7          32 LOAD_FAST                2 (n)
                 34 LOAD_METHOD              2 (append)
          k       36 LOAD_FAST                0 (k)
                 38 LOAD_GLOBAL              3 (ord)
                 40 LOAD_FAST                1 (flag)
                 42 LOAD_FAST                3 (i)
                 44 BINARY_SUBSCR
                 46 CALL_FUNCTION            1
                 48 LOAD_GLOBAL              3 (ord)
                 50 LOAD_FAST                1 (flag)
                 52 LOAD_FAST                3 (i)
                 54 LOAD_CONST               3 (1)
                 56 BINARY_ADD
                 58 BINARY_SUBSCR
                 60 CALL_FUNCTION            1
                 62 BINARY_XOR
                 64 LOAD_CONST               4 (4)
                 66 BINARY_LSHIFT
                 68 BINARY_XOR
                 70 CALL_METHOD              1
                 72 POP_TOP
                 74 JUMP_ABSOLUTE           28

      8     >>   76 LOAD_GLOBAL              4 (print)
                 78 LOAD_CONST               5 ('n = ')
                 80 LOAD_FAST                2 (n)
                 82 CALL_FUNCTION            2
                 84 POP_TOP
                 86 LOAD_CONST               0 (None)
                 88 RETURN_VALUE

    n =  [4679, 4631, 4775, 4839, 4951, 4295, 4487, 4519, 4439, 4839, 4231, 5095, 5959, 5623, 4855, 5127, 5751, 5703, 6103]
    ```


## Solution

The goal is to reverse engineer the flag from the output list `n`. The sauce is to understand what each instruction do and decompile the source into python code ourselves. Most of the instructions are easily understandable and most of us become confused about what `BINARY_SUBSCR` does and how `CALL_FUNCTION` works.

`BINARY_SUBSCR`: It stands for `binary subscript`. It does the job of index in a list. It takes in two parameters: the `name of the list` and the `index` of the item.

`CALL_FUNCTION`: It calls the function on top of the stack with the parameters passed to it in the stack. An example would be a fast way to learn. Consider offset 14-18 in the source code.

```
             14 LOAD_GLOBAL              1 (len)
             16 LOAD_FAST                1 (flag)
             18 CALL_FUNCTION            1
```

The `len` function is loaded on the stack first then it's parameter `flag`. So, the overall call stands for `len(flag)`. This is also true for operations involving more than one operands or parameters like `XOR` `ADD` `SUBTRACT`, etc. Similarly, we keep decompiling the source. You should decompile the rest by yourselves before continuing.

??? Decompiled_source
    ```python
    k = 4919

    flag = 'CTF_BD{__REDACTED__}'

    n = []

    for i in range(len(flag)-1):
        n.append(k ^ ((ord(flag[i]) ^ ord(flag[i + 1])) << 4))

    print('n = ',n)
    ```

??? Solution
    ```python
    k = 4919

    n = [4679, 4631, 4775, 4839, 4951, 4295, 4487, 4519, 4439, 4839, 4231, 5095, 5959, 5623, 4855, 5127, 5751, 5703, 6103]

    flag = 'C'

    for i in range(len(n)):
        flag += chr((k ^ n[i]) >> 4 ^ ord(flag[i]))

    print(flag)
    ```

flag: `CTF_BD{Py_Byt3_C0d3}`

