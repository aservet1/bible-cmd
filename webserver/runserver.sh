#!/bin/bash

set -e
set -x

pkgname=bibleServer

if [ "$1" = "--rebuild" ] || [ "$1" = "-r" ] ; then
    rm -rfv $pkgname
    javac src/* -d .
fi

mkdir -p "./server-logs"
java $pkgname.BibleServer -p 8080 -r "./public" \
    | tee "./server-logs/server_log_started_on_$( date | tr ' ' '-' ).log"
