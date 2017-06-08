#!/usr/bin/env python
import sys
import os


def map():
    o = int(os.environ.get('o'))
    l = int(os.environ.get('l'))
    array = []
    for num in sys.stdin:
        num = num.strip()
        try:
            num = int(num)
            if num > o:
                continue
            else:
                array.append(num)
        except ValueError:
            continue
    array.sort(reverse=True)
    topL = l if len(array) > l else len(array)
    for i in range(0, topL):
        print array[i]


if __name__ == '__main__':
    map()
