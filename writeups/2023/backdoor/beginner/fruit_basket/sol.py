#!/usr/bin/env python

from ctypes import CDLL

from pwn import remote, gdb, context, log, p64, process, u64

context.log_level = "debug"
context.terminal = ["tmux", "split-window", "-h"]

fruits = b"\x08\x20\x10\x00\x00\x00\x00\x00\x0e\x20\x10\x00\x00\x00\x00\x00\x15\x20\x10\x00\x00\x00\x00\x00\x1b\x20\x10\x00\x00\x00\x00\x00\x22\x20\x10\x00\x00\x00\x00\x00\x2c\x20\x10\x00\x00\x00\x00\x00\x37\x20\x10\x00\x00\x00\x00\x00\x3d\x20\x10\x00\x00\x00\x00\x00\x42\x20\x10\x00\x00\x00\x00\x00\x4d\x20\x10\x00\x00\x00\x00\x00"
fruits = [u64(fruits[i : i + 8]) for i in range(0, len(fruits), 8)]

fruit_basket = {
    0x00102008: b"Apple",
    0x0010200E: b"Orange",
    0x00102015: b"Mango",
    0x0010201B: b"Banana",
    0x00102022: b"Pineapple",
    0x0010202C: b"Watermelon",
    0x00102037: b"Guava",
    0x0010203D: b"Kiwi",
    0x00102042: b"Strawberry",
    0x0010204D: b"Peach",
}

libc = CDLL("libc.so.6")
p = process("./chal")
# p = remote("34.70.212.151", 8006)
seed = libc.time(0)
libc.srand(seed)
log.info(f"using {seed=}")

# gdb.attach(
#     p,
#     """
#     set follow-fork-mode child
#     break *(main+318)
#     continue
#     """,
# )

for i in range(50):
    rin = libc.rand() % len(fruits)
    fruit = fruit_basket[fruits[rin]]
    log.info(f"rand = {rin}, fruit = {fruit}")
    try:
        p.sendlineafter(b"Your guess : ", fruit)
    except:  # noqa: E722
        break

p.interactive()
