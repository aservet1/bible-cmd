#pragma once

typedef struct {
    int number;
    char *name;
    char *abbr;
} bible_book;

typedef struct {
    int book;
    int chapter;
    int verse;
    char *text;
} bible_verse;

extern bible_verse bible_verses[];

extern int bible_verses_length;

extern bible_book bible_books[];

extern int bible_books_length;
