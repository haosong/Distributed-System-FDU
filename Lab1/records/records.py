#!/usr/bin/env python

import random

print "Generating records ..."
f = open('records.txt', 'w')
for i in range(0, 1000000):
    f.write("%s\t%s\t%d\n" % (random.randint(1, 20000), random.randint(1, 1000), random.randint(1, 5)))
f.close()
print "Generated records: ./records.txt !"
