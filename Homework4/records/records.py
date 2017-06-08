#!/usr/bin/env python
import random

print("Generating records ...")
f = open('records.txt', 'w')
for i in range(0, 10000):
    f.write("%s\n" % random.randint(1, 1000000))
f.close()
print("Generated records: ./records.txt !")
