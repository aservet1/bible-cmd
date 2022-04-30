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

args = sys.argv

if len(args) == 1:
    book_selections = [ book_selection.strip() for book_selection in sys.stdin.readlines() ]
else:
    book_selections = args[1:]

words_to_keep_capitalized = [
    "LORD"
    # add more as you find them
    # also consider a more modular way to keep this list
]

wordcount_results = {}
path_to_book_binary = "./bin/kjv" # TODO: parameterize this (requires arg parsing where you pick out flagged values while maintaining not flagged values)
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
        if word in wordcount.keys():
            wordcount[word] += 1
        else:
            wordcount[word] = 1
    wordcount_results[book_selection] = wordcount

print(json.dumps(wordcount_results))




