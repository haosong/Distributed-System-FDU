#!/usr/bin/env python

from operator import itemgetter
import sys

N = 10 # Top K
topList = [(0, "0")] * N # Initialize Top-N List
current_doc = None
current_count = 0
star_sum = 0
docid = None

def addRecord(current_doc, rating):
    if (topList[N - 1][0] < rating):
        topList.pop()
        topList.append((rating, current_doc))
        topList.sort()
        topList.reverse()

def printList():
    for record in topList:
        print '%s\t%.3f' %(record[1], record[0])

for line in sys.stdin:
    record = line.strip()
    docid, star = record.split('\t')
    
    try:
        star = float(star)
    except ValueError:
        continue

    if current_doc == docid:
        current_count += 1
        star_sum += star
    else:
        if current_doc:
            addRecord(current_doc, star_sum / current_count)  
        current_count = 1
        star_sum = star
        current_doc = docid

if current_doc == docid:
    addRecord(current_doc, star_sum / current_count)

printList()
