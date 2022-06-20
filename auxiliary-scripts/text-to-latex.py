#!/usr/bin/env python3
from sys import argv

print(r'\documentclass{article}'           )
print(r'\begin{document}'                  )
print(                                     )
print(r'\makeatletter'                     )
print(r'\renewcommand{\@seccntformat}[1]{}')
print(r'\makeatother'                      )
print(                                     )

with open(argv[1]) as fp:
    lines = [ line.strip() for line in fp.readlines() ]

currently_printing_list = False
current_book = current_chapter = None
for line in lines:
    book, chverse, text = line.split('\t')
    chapter, verse = chverse.split(':')

    if book != current_book:
        if currently_printing_list:
            print("\t" + r'\end{enumerate}')
            currently_printing_list = False

        current_book = book
        print(r'\section{'+book+'}')

    if chapter != current_chapter:
        if currently_printing_list:
            print("\t" + r'\end{enumerate}')
            currently_printing_list = False

        current_chapter = chapter
        print(r'\subsection{Chapter ' + chapter + '}')
        print('\t' + r'\begin{enumerate}')
        currently_printing_list = True

    print("\t\t\\item [" + verse + "] " + text)

if currently_printing_list:
    print("\t" + r'\end{enumerate}')
    currently_printing_list = False

print(r"\end{document}")

