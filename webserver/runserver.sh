#!/bin/bash

set -e
set -x

pkgname=bibleServer

if [ "$1" = "--rebuild" ] || [ "$1" = "-r" ] ; then
    rm -rfv $pkgname
    javac src/* -d .
fi

mkdir -p "./server-logs"
java $pkgname.BibleServer --port 8080 --web-root "./public" \
    | tee "./server-logs/server_log_started_on_$( date | tr ' ' '-' ).log"
