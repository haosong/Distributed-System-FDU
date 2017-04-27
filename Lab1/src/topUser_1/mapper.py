#!/usr/bin/env python

import sys


def read_input(file):
    for line in file:
        yield line.split()


def main():
    data = read_input(sys.stdin)
    for words in data:
        if len(words) == 3:
            print '%s\t%s' % (words[0], words[2])


if __name__ == "__main__":
    main()
