#!/usr/bin/env python

import sys


def read_mapper_output(file):
    for line in file:
        yield line.split()


def reverse_numeric(x, y):
    return int(y) - int(x)


def main():
    data = read_mapper_output(sys.stdin)
    records = []
    for words in data:
        if len(words) == 2:
            records.append((words[0], words[1]))

    records = sorted(records, key=lambda user: user[1], cmp=reverse_numeric)

    for i in range(0, 10):
        print '%s\t%s' % (records[i][0], records[i][1])


if __name__ == "__main__":
    main()
