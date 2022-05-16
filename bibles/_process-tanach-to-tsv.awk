BEGIN {
    # book names manually retreieved from
    #   https://www.chabad.org/library/bible_cdo/aid/63255/jewish/The-Bible-with-Rashi.htm
	fullnames["gen"] = "Genesis"
	fullnames["exo"] = "Exodus"
	fullnames["lev"] = "Leviticus"
	fullnames["num"] = "Numbers"
	fullnames["deu"] = "Deuteronomy"
	fullnames["jos"] = "Joshua"
	fullnames["jdg"] = "Judges"
	fullnames["sa1"] = "1 Samuel"
	fullnames["sa2"] = "2 Samuel"
	fullnames["kg1"] = "1 Kings"
	fullnames["kg2"] = "2 Kings"
	fullnames["isa"] = "Isaiah"
	fullnames["jer"] = "Jeremiah"
	fullnames["eze"] = "Ezekiel"
	fullnames["hos"] = "Hosea"
	fullnames["joe"] = "Joel"
	fullnames["amo"] = "Amos"
	fullnames["oba"] = "Obadiah"
	fullnames["jon"] = "Jonah"
	fullnames["mic"] = "Micah"
	fullnames["nah"] = "Nahum"
	fullnames["hab"] = "Habakkuk"
	fullnames["zep"] = "Zephaniah"
	fullnames["hag"] = "Haggai"
	fullnames["zac"] = "Zechariah"
	fullnames["mal"] = "Malachi"
	fullnames["psa"] = "Psalms"
	fullnames["pro"] = "Proverbs"
	fullnames["job"] = "Job"
	fullnames["sol"] = "Song of Songs"
	fullnames["rut"] = "Ruth"
	fullnames["lam"] = "Lamentations"
	fullnames["ecc"] = "Ecclesiastes"
	fullnames["est"] = "Esther"
	fullnames["dan"] = "Daniel"
	fullnames["ezr"] = "Ezra"
	fullnames["neh"] = "Nehemiah"
	fullnames["ch1"] = "1 Chronicles"
	fullnames["ch2"] = "2 Chronicles"
    bookNumber = 0
    currentBook = ""
    print "name\tabbr\tbookNum\tchapter\tverse\ttext"
}
{
    # expected line format:
    #   bookAbbr|chapter|verse|text
    FS="|"
    bookAbbr = $1
    bookName = fullnames[bookAbbr]
    chapter  = $2
    verse    = $3
    text     = $4
    if (currentBook != bookName) {
        currentBook = bookName
        bookNumber = bookNumber + 1
    }
    print bookName"\t"bookAbbr"\t"bookNumber"\t"chapter"\t"verse"\t"text
}
END {


}
