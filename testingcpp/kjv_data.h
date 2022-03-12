#pragma once
#include <vector>
// #include <string>

struct kjv_book {
    int number;
    const char* name;
    const char* abbr;
};

struct kjv_verse {
    int book;
    int chapter;
    int verse;
    const char* text;
};

extern std::vector<kjv_verse> kjv_verses;
extern std::vector<kjv_book>  kjv_books;
