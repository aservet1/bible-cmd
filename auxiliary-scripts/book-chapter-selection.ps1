$BooksFile = '.\apocrypha.tsv'
$BookName  = '2 Macabees'
$Chapters  = @(4,5,6)

$head = @('name','abbr','book','chapter','verse','text' -join "`t") # tsv file format doesnt have headers right now...

$head + (get-Content $BooksFile) | ConvertFrom-Csv -Delimiter "`t" | ? {
    $_.name -eq $BookName
} | foreach-Object {
    $_ | select-Object -Property Name,Chapter,Verse,Text
} | where-Object {
    $Chapters -contains $_.chapter
} | select-Object -Property chapter,verse,text | Format-Table -Wrap

