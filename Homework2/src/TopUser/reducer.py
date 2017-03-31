#!/usr/bin/env python

from operator import itemgetter
import sys

N = 10 # Top K
topList = [(0, "0")] * N # Initialize Top-N List
current_doc = None
current_count = 0
docid = None

def addRecord(current_doc, count):
    if (topList[N - 1][0] < count):
        topList.pop()
        topList.append((count, current_doc))
        topList.sort()
        topList.reverse()

def printList():
    for record in topList:
        print '%s\t%s' %(record[1], record[0])

for line in sys.stdin:
    record = line.strip()
    docid, count = record.split('\t')
    
    try:
        count = int(count)
    except ValueError:
        continue

    if current_doc == docid:
        current_count += count
    else:
        if current_doc:
            addRecord(current_doc, current_count)  
        current_count = count
        current_doc = docid

if current_doc == docid:
    addRecord(current_doc, current_count)

printList()
