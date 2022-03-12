#!/bin/bash

bookfile=$1
awk -f ./data/generate.awk $bookfile > data/bible_data.c
exe=$( echo $bookfile | sed 's|^.*/||;s|\..*$||' )
make
mkdir -p bin
mv bible bin/$exe

