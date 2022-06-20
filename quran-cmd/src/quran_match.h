#pragma once

#include "bible_config.h"
#include "bible_ref.h"

typedef struct {
    int start;
    int end;
} bible_range;

typedef struct {
    int current;
    int next_match;
    bible_range matches[2];
} bible_next_data;

int
bible_next_verse(const bible_ref *ref, const bible_config *config, bible_next_data *next);
