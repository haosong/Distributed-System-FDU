#!/usr/bin/env python
import sys
import os


def reduce():
    o = int(os.environ.get('o'))
    l = int(os.environ.get('l'))
    array = []
    for num in sys.stdin:
        num = num.strip()
        try:
            num = int(num)
            array.append(num)
        except ValueError:
            continue
    array.sort(reverse=True)
    topL = l if len(array) > l else len(array)
    array = array[0:topL]
    print 'o:\t%s' % o
    print 'l:\t%s' % l
    print 'sum:\t%s' % sum(array)
    print 'max:\t%s' % array[0]
    print 'window(o):'
    print array


if __name__ == '__main__':
    reduce()
