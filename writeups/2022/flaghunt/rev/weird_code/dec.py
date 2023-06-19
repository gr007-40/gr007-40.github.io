k=4919

flag='CTF_BD{__REDACTED__}'

n=[]

for i in range(len(flag)-1):
    n.append(k^((ord(flag[i])^ord(flag[i+1]))<<4))

print('n = ',n)
