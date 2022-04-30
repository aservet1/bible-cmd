#!/bin/bash

set -e
set -x

pkgname=bibleServer

rm -rfv $pkgname
javac src/* -d .
mkdir -p "./server-logs"
java $pkgname.BibleServer -p 8080 -r "./public" \
    | tee "./server-logs/server_log_started_on_$( date | tr ' ' '-' ).log"
