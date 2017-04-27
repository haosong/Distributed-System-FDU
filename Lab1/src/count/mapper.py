#!/usr/bin/env python

import sys


def read_input(file):
    for line in file:
        yield line.split()


def main():
    data = read_input(sys.stdin)
    dict = {}
    for words in data:
        docId = words[1]
        doc = dict.get(docId, None)
        if (doc):
            dict[docId] = (doc[0] + int(words[2]), doc[1] + 1)
        else:
            dict[docId] = (int(words[2]), 1)

    for k, v in dict.iteritems():
        print '%s\t%s\t%s' % (k, v[0], v[1])  # <DocId, RateSum, UserNum>


if __name__ == "__main__":
    main()
