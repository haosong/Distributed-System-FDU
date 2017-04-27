#!/usr/bin/env python

import sys


def read_input(file):
    for line in file:
        yield line.split()


def main():
    data = read_input(sys.stdin)
    records = []
    for words in data:
        if len(words) == 3 and 0 <= int(words[2]) <= 5:
            records.append((words[0], words[1], words[2]))

    records = sorted(records, key=lambda userid: userid[0])
    records = [n for i, n in enumerate(records) if i == 0 or n[0] != records[i - 1][0] or n[1] != records[i - 1][1]]

    for words in records:
        print '%s\t%s\t%s' % (words[0], words[1], words[2])


if __name__ == "__main__":
    main()
