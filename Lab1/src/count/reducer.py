#!/usr/bin/env python

import sys


def read_input(file):
    for line in file:
        yield line.split()


def main():
    data = read_input(sys.stdin)
    dict = {}
    for words in data:
        docId = words[0]
        doc = dict.get(docId, None)
        if (doc):
            dict[docId] = (doc[0] + int(words[1]), doc[1] + int(words[2]))
        else:
            dict[docId] = (int(words[1]), int(words[2]))

    for k, v in dict.iteritems():
        print '%s\t%.4f\t%s' % (k, float(v[0]) / float(v[1]), v[1])  # <DocId, RateAvg, UserNum>


if __name__ == "__main__":
    main()
