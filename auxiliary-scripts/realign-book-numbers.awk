# assumes the TSV format for books that I've been using
# goes through the third column (book number) and sets
# it to be whatever the books are in ascending order

# this is for the common use case where you edit the TSV file
# to change the placement of the book, like when I moved the
# Gospel of Peter from the top to the bottom of my apocrypha
# collection; it was originally numbered book 1, but I wanted
# all of the Old testament Apocrypha to be at the
# top, so I put this one at the bottom of the series. This
# script prevents the tedious task of manually rewriting column 3
# whenever I change the book order

BEGIN { n = 0; currentbook = ""; FS = "\t" }

{
	if (currentbook != $1)
		n = n + 1;
		currentbook = $1;

	print $1"\t"$2"\t"n"\t"$4"\t"$5"\t"$6
}
