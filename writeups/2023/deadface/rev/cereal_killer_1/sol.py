#!/usr/bin/env python

cereal = '\x4d\x79\x7c\x70\x7b\x80\x47\x52\x59\x5c\x4c\x4e\x4c\x59'

cereal = ''.join(chr(ord(c)-ord('\a')) for c in cereal)

print(cereal)
