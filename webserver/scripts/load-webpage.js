function httpGetAsync(theUrl, callback)
{ // thanks to https://stackoverflow.com/questions/247483/http-get-request-in-javascript
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function() { 
        if (xmlHttp.readyState == 4 && xmlHttp.status == 200)
            callback(xmlHttp.responseText);
    }
    xmlHttp.open("GET", theUrl, true); // true for asynchronous 
    xmlHttp.send(null);
}
function parseBookText(bookText) {
    return bookText.split('\n').reduce(
        (parsedBook, line) => {
            // bookText line format: 'Book\tChapter:Verse\tVerseText'
            let parts   = line.split('\t')
            let chverse = parts[1].split(':')
            let book    = parts[0]
            let chapter = chverse[0]
            let verse   = chverse[1]
            let verseText = parts[2]
            if (!parsedBook[book])          { parsedBook[book]           =  {} }
            if (!parsedBook[book][chapter]) { parsedBook[book][chapter]  =  {} }
            parsedBook[book][chapter][verse] = verseText
            return parsedBook
        }, {}
    )
}
function postProcessBibleTextToHTMLList(text) {
    let str = '';
    str += '<ul>'
    for (const [book, chapters] of Object.entries(parseBookText(text))) {
        str += '<li><b>' + book + '</b>'
        str += '<ul>'
        for (const [chapter,verses] of Object.entries(chapters)) {
            str += '<li><b>Chapter ' + chapter + ':</b>'
            str += '<ul>'
            for (const [verse,verseText] of Object.entries(verses)) {
                str += '<li>'
                str += '<b>'+verse+'.</b> ' + verseText
                str += '</li>'
            }
            str += '</ul>'
            str += '</li>'
        }
        str += '</ul>'
        str += '</li>'
    }
    str += '</ul>'
    return str // text
}
function showBibleText(text) {
    document.getElementById('contentSpot').innerHTML = postProcessBibleTextToHTMLList(text)
}
function reverseString(str) {
	let rev = ""
	for (let i = str.length-1; i >= 0; --i){
		rev += str[i]
	} return rev;
}
function getCmd(cmd) {
    httpGetAsync(
        "http://149.28.52.2:8080/api/booktext?cmd=" + cmd.split(/\s+/).join('_'),
        showBibleText
    )
}
getCmd('kjv mark 16:1,2,4,8')
