import os
import re
import sys
import json
import subprocess

# code adapted from https://stackoverflow.com/questions/13398261/how-to-run-a-subprocess-with-python-wait-for-it-to-exit-and-get-the-full-stdout
def shell_exec_get_stdout_as_string(cmd):
    process = subprocess.Popen(
        cmd,
        shell=True,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE
    )
    result = ''
    # wait for the process to terminate
    result = [ bytes.decode(line) for line in process.stdout ]
    # print(result)
    errorcode = process.returncode
    if errorcode:
        print("error code is not None! errorcode = " + errorcode)
    return ''.join(result)

def usage_string():
    return (
        "usage: python3 wordcount.py -p|--path-to-book-binary PATH [ -f|--filter-stopwords stopwords.txt ] [BOOK_SELECTIONS...]\n" +
        "\tBOOK_SELECTIONS is a list of standard book selections, i.e. 'mark 1:1-15' or 'luke' or 'Obadiah 1:2,4,6,8'\n" +
        "\tPATH is the path to your book binary, i.e. './bookcmds/kjv' or '/home/username/biblecmds/non-english-versions/vulgate'\n" +
        "\t\tIf you already have the binary in your system path and you use it as a global command, you can do that as well, i.e. 'kjv'"
    )
def usage_abort():
    print(usage_string())
    exit(2)

args = sys.argv
stopwords = None
path_to_book_binary = None
i = 1 # skip the first arg
while i < (len(args)):
    if args[i] == "-f" or args[i] == "--filter-stopwords":
        args[i] = None
        i += 1
        if not i < len(args):
            usage_abort()
        stopwords = args[i]
        args[i] = None
    elif args[i] == "-p" or args[i] == "--path-to-book-binary":
        args[i] = None
        i += 1
        if not i < len(args):
            usage_abort()
        path_to_book_binary = args[i]
        args[i] = None

    i += 1

if not path_to_book_binary:
    usage_abort()

args = [ arg for arg in args if arg ] # remove the 'None' values, if any

if len(args) == 1:
    book_selections = [ book_selection.strip() for book_selection in sys.stdin.readlines() ]
else:
    book_selections = args[1:]

if stopwords:
    with open(stopwords) as fp:
        stopwords = { word for word in re.split("\s+",fp.read()) if len(word) }
else:
    stopwords = {}

words_to_keep_capitalized = [
    "LORD"
    # add more as you find them
    # also consider a more modular way to keep this list
]

wordcount_results = {}
for book_selection in book_selections:
    text = shell_exec_get_stdout_as_string(path_to_book_binary + " " + book_selection)
    words = []
    for line in text.strip().split("\n"):
        for word in re.split(
            "\s+",
            re.sub(
                "[^\w\s]",
                "",
                line.split("\t")[-1]
            )
        ):
            if not len(word):
                continue
            word = word if word in words_to_keep_capitalized else word.lower()
            words.append(word)
    wordcount = {}
    for word in words:
        if word in stopwords:
            continue
        if word in wordcount.keys():
            wordcount[word] += 1
        else:
            wordcount[word] = 1
    wordcount_results[book_selection] = wordcount

print(json.dumps(wordcount_results))




