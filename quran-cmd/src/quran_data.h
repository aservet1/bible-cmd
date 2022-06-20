#pragma once

typedef struct {
    int number;
    char *name;
    char *abbr;
} quran_book;

typedef struct {
    int surah;
    int ayat;
    char *text;
} quran_verse;

extern quran_verse quran_verses[];

extern int quran_verses_length;
