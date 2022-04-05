#!/bin/bash

function usage() {
    echo "usage: $0 <directory name where books are kept, each in their own directory {01..66} where each directory has {1..nVerses}.htm>"
}

function remove_leading_zeroes() {
    cmd="sed 's/^0*//'"
    if [ -z $1 ]; then
        cmd="cat | $cmd"     # pipe stdin to the sed command
    else
        cmd="echo $1 | $cmd" # echo arg 1 to the sed command 
    fi
    eval $cmd
}

# sets the $books variable
source book-dict.sh

[ -z $1 ] && usage $0 && exit 2

bookDir=$1

# find $bookDir | grep '\.htm$'

n=10
book=${books[$n]}

echo "'$n' '$book'"

for bookNum in "${!books[@]}"
do
  echo "bookNum  : $bookNum"
  echo "value: ${books[$bookNum]}"
done


# cat Books/01/2.htm | grep 'class="verse"' | sed 's/<span[^>]*>//' | sed 's|<br />||' | sed 's/^[^0-9]*//' | sed 's|\s*</span>|\t|'
