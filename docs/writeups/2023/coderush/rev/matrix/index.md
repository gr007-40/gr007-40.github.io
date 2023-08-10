---
comments: true
authors:
  - gr007
tags:
  - rev
  - flaghunt
---
# Matrix

## Description

Matrix is inevitable. Escape the `Matrix`.

>Author: `gr007`

[matrix](matrix)

## Solution

We are given the `matrix` binary that asks for the one last thing that we have to say. Presumably that has to be the
flag in order to escape the matrix.
We open the binary in ghidra and look at what the source code looks like:

The following part of code is interesting. Because this part is our goal:
```c
    f = true;
    for (j = 0; j < 40; j += 1) {
        sum = 0;
        for (k = 0; k < 32; k += 1) {
            sum += flag[k] * matrix[k + j * 32];
        }
        f = f & sum == B[j];
    }
    if (f) {
        puts("You sure can escape the matrix");
    }
    else {
        puts("You shall never escape the matrix");
    }
```

So, what is happening is that the flag that is input is being taken as if it were `X` in a matrix multiplication
equation for `AX = B`. We have `B` matrix with which the result is being compared with. and also the A matrix with which
our flag is being multiplied with. Now, we can simply do `X = A^(-1)*B` to get X. But there are only 32 characters in
the flag but 40 values in `B` and the matrix `A` is `40x32`. We can simply opt out any 8 row from both `A` and `B` and
get the flag in 32 characters.
The following python file calculates the flag:
[`sol.py`](sol.py)

flag: `coderush{s0_d0_y0u_l1k3_m4tr1x?}`
