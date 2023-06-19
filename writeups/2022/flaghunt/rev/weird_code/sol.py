k = 4919

n = [4679, 4631, 4775, 4839, 4951, 4295, 4487, 4519, 4439, 4839, 4231, 5095, 5959, 5623, 4855, 5127, 5751, 5703, 6103]

flag = 'C'

for i, item in enumerate(n):
    flag += chr((k ^ item) >> 4 ^ ord(flag[i]))

print(flag)
