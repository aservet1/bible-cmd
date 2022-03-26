function httpGetAsync(theUrl, callback)
{ // thanks to https://stackoverflow.com/questions/247483/http-get-request-in-javascript
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function() {
        if (xmlHttp.readyState == 4 && xmlHttp.status == 200) {
            callback(xmlHttp.responseText)
        }
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
function postProcessBibleTextToHTMLTable__NOTREADYTOUSEYET(text) {
    // this function is not done, I've only copied the code from postProcessBibleTextToHTMLList and started to make a few small changes
    let str = '';
    str += '<table>'
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
    str += '</table>'
    return str // text
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
    return str
}
function showBibleText(text) {
    document.getElementById('contentSpot').innerHTML = postProcessBibleTextToHTMLList(text)
}
function showText(text) {
    // console.log(text)
    document.getElementById('contentSpot').innerText = text
}
function reverseString(str) {
	let rev = ""
	for (let i = str.length-1; i >= 0; --i){
		rev += str[i]
	} return rev;
}
function getBookSelectionName() {
    const radioButtons = document.querySelectorAll('input[name="book_selection"]');
    let selection
    for (const radioButton of radioButtons) {
        if (radioButton.checked) {
            selection = radioButton.value
            break
        }
    }
    return selection
}
function getFullURLFromCmd(cmd) {
    return "http://149.28.52.2:8080/api/booktext?cmd=" + (
        getBookSelectionName() + ' ' + cmd
    ).split(/\s+/).join('_')
}
function getBookText(bookrequest) {
    httpGetAsync(
        getFullURLFromCmd(bookrequest),
        showBibleText
    )
}
function validInput(text) {

}
document.getElementById('askbutton').onclick = (
    function() {
        let text = document.getElementById('asktext').value
        // console.log(text)
        // TODO: evalate text input for validity, 
        getBookText(text)
    }
)
document.getElementById('askforhelp').onclick = (
    function() {
        console.log('hello!')
        httpGetAsync(
            getFullURLFromCmd(getBookSelectionName()+' -help'),
            showText
        )
    }
)
document.getElementById('askforbooks').onclick = (
    function() {
        httpGetAsync(
            getFullURLFromCmd(getBookSelectionName()+' -l'),
            showText
        )
    }
)
