BEGIN {

    booknames["gen"] = "Genesis" 
    booknames["exo"] = "Exodus" 
    booknames["lev"] = "Leviticus" 
    booknames["num"] = "Numbers" 
    booknames["deu"] = "Deuteronomy" 
    booknames["jos"] = "Joshua" 
    booknames["jdg"] = "Judges" 
    booknames["sa1"] = "1 Samuel" 
    booknames["sa2"] = "2 Samuel" 
    booknames["kg1"] = "1 Kings" 
    booknames["kg2"] = "2 Kings" 
    booknames["jer"] = "Jeremiah" 
    booknames["eze"] = "Ezekiel" 
    booknames["hos"] = "Hosea" 
    booknames["joe"] = "Joel" 
    booknames["amo"] = "Amos" 
    booknames["oba"] = "Obadiah" 
    booknames["jon"] = "Jonah" 
    booknames["mic"] = "Micah" 
    booknames["nah"] = "Nahum" 
    booknames["hab"] = "Habakkuk" 
    booknames["zep"] = "Zephaniah" 
    booknames["hag"] = "Haggai" 
    booknames["zac"] = "Zechariah" 
    booknames["mal"] = "Malachi" 
    booknames["psa"] = "Psalms" 
    booknames["pro"] = "Proverbs" 
    booknames["job"] = "Job" 
    booknames["sol"] = "Song of Solomon" 
    booknames["rut"] = "Ruth" 
    booknames["lam"] = "Lamentations" 
    booknames["ecc"] = "Ecclesiastes" 
    booknames["est"] = "Esther" 
    booknames["dan"] = "Daniel" 
    booknames["ezr"] = "Ezra" 
    booknames["neh"] = "Nehemiah" 
    booknames["ch1"] = "1 Chronicles" 
    booknames["ch2"] = "2 Chronicles" 

    FS="|"

    booknum = 0
    current_book = ""

    print "book\tabbr\tbooknum\tchapter\tverse\ttext"
}
{
    if ($1 != current_book)
        booknum += 1
    if ($1 != current_book)
        current_book = $1

    print ""(booknames[$1])"\t"$1"\t"(booknum)"\t"$2"\t"$3"\t"$4""
}

