#!/usr/bin/env python

import sys


def read_input(file):
    for line in file:
        yield line.split()


def main():
    data = read_input(sys.stdin)
    records = []
    for words in data:
        if len(words) == 3:
            records.append((words[0], words[1]))

    records = sorted(records, key=lambda rate: rate[1], reverse=True)

    for i in range(0, 10):
        print '%s\t%s' % (records[i][0], records[i][1])


if __name__ == "__main__":
    main()
