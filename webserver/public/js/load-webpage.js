var CURRENT_SELECTION = null;
const SERVER_URL = "http://149.28.52.2:8080"

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
function parseBibleText(bookText) {
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
function parseBookText_KeepOrderAndUniqueness(bookTextSelection) { // TODO: finish this function
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
                verseText: line.verseText
            })
        }
    }
}
function postProcessBibleTextToHTMLTable(text) {
    return null
}
function parsedBibleTextToHTMLList(parsedBookText) {
    let str = '';
    str += '<ul>'
    for (const [book, chapters] of Object.entries( parsedBookText )) {
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
function renderBibleSelection(selection) {
    document.getElementById('contentSpot').innerHTML = parsedBibleTextToHTMLList(selection)
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
    return SERVER_URL + "/api/booktext?cmd=" + (
        cmd .split(';')
	    	.map(e => getBookSelectionName() + ' ' + e.trim())
	    .join(';')
	//getBookSelectionName() + ' ' + cmd
    ).split(/\s+/).join('_')
}
function getBookText(bookrequest) {
    httpGetAsync(
        getFullURLFromCmd(bookrequest),
        (text) => {
			CURRENT_SELECTION = parseBibleText(text);
			renderBibleSelection(CURRENT_SELECTION);
		}
    )
}
function validInput(text) {

}
function filterVerses(selection, pattern) {
	if (!pattern)                    return selection
	if (pattern === null)            return selection
	if (pattern.trim().length === 0) return selection
	const regex = new RegExp(pattern, "g")
	const filteredSelection = {}
	for (const [book, chapters] of Object.entries( selection )) {
 		for (const [chapter,verses] of Object.entries(chapters)) {
 			for (const [verse,verseText] of Object.entries(verses)) {
				if (verseText.match(regex)) {
					if (!filteredSelection[book])          { filteredSelection[book]          = {} }
					if (!filteredSelection[book][chapter]) { filteredSelection[book][chapter] = {} }
					filteredSelection[book][chapter][verse] = verseText
				}
			}
		}
	}
	return filteredSelection
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
document.getElementById('askbutton_pdf').onclick = (
	function() {
		const url      = getFullURLFromCmd(document.getElementById('asktext').value).replace("/api/booktext","/api/latexdoc")
		const tempLink = document.createElement('a')
		tempLink.href  = url
		tempLink.click()
		tempLink.parentNode.removeChild(tempLink)
	}
)
document.getElementById('filterbutton').onclick = (
    function() {
        let pattern = document.getElementById('filtertext').value
	// console.log(pattern)
        renderBibleSelection( filterVerses(CURRENT_SELECTION, pattern) )
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
document.getElementById("nasb_radio_btn").checked = true;

var HISTORY_INDEX = 0
const HISTORY = []
const HISTORY_MAX_SIZE = 256
document.getElementById('asktext').onkeydown = (
	// https://tutorial.eyehunts.com/js/javascript-keycode-list-event-which-event-key-event-code-values/
	// left arrow	37	ArrowLeft	ArrowLeft
	// up arrow	38	ArrowUp	ArrowUp
	// right arrow	39	ArrowRight	ArrowRight
	// down arrow	40	ArrowDown	ArrowDown
	function(e) {
		if (e.which == 37) { // left arrow
		}
		if (e.which == 39) { // right arrow
		}
		if (e.which == 38) { // up arrow
			HISTORY_INDEX++
			console.log(HISTORY_INDEX)
		}
		if (e.which == 40) { // down arrow
			HISTORY_INDEX--
			console.log(HISTORY_INDEX)
		}
	}
)
document.getElementById('asktext').onkeypress = (
	function(e) {
		if (e.which == 13) { // enter
			showText('waiting for content...')
			getBookText(document.getElementById('asktext').value)
		}
		// TODO: if you hit 'esc' key, unfocus from the text box
	}
)
