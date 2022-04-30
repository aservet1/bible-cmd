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
function parseBookText_KeepOrderAndUniqueness(bookTextSelection) {
    const parsedBookTextSelection = []
    let currentBook = null;
    let parsedSelectionForCurrentBook = {}
    for(const lineText of bookText.split('\n')) {

        // todo: validate line format with regex
 
        const lineParts   = lineText.split('\t')
        const chverse = lineParts[1].split(':')

        const line = {
            book :  lineParts[0],
            chapter :   chverse[0],
            verseNumber :   chverse[1],
            verseText : lineParts[2]
        }
        
        if (line.book !== currentBook) {
            parsedBookTextSelection.append({
                book: currentBook,
                verses: currentBookVerses
            }) 
            currentBook = line.book
        } else {
            verses.append({
                verseNumber: line.verseNumber,
                verseText = line.verseText
            })
        }
    }
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
            str += `<li><b>Chapter ${chapter}:</b>`
            str += '<ol style="font-weight: bolder">'
            for (const [verse,verseText] of Object.entries(verses)) {
                str += `<li value=${verse}> <span style="font-weight: normal"> ${verseText} </span> </li>`
            }
            str += '</ol>'
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
        cmd .split(';')
	    	.map(e => getBookSelectionName() + ' ' + e.trim())
	    .join(';')
	//getBookSelectionName() + ' ' + cmd
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
        showText('waiting for content...')
        let text = document.getElementById('asktext').value
        // console.log(text)
        // TODO: evalate text input for validity, 
        getBookText(text)
    }
)
document.getElementById('askforhelp').onclick = (
    function() {
        showText('hello!')
        httpGetAsync(
            getFullURLFromCmd(getBookSelectionName()+' -help'),
            showText
        )
    }
)
document.getElementById('askforbooks').onclick = (
    function() {
        showText('waiting for books...')
        httpGetAsync(
            getFullURLFromCmd(getBookSelectionName()+' -l'),
            showText
        )
    }
)
document.getElementById("kjv_radio_btn").checked = true;
document.getElementById('asktext').onkeypress = (
	function(e) {
		if (e.which == 13) {
			showText('waiting for content...')
			getBookText(document.getElementById('asktext').value)
		}
		// TODO: if you hit 'esc' key, unfocus from the text box
	}
)
