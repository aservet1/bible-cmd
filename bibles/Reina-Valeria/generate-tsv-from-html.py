import os

with open('book-list.csv') as fp: # consider just hard coding the book list in here
	books = { line.strip().split(',')[0] : line.strip().split(',')[1] for line in fp.readlines() }

allBooksDir = './Books'

for bookNum in sorted(os.listdir(allBooksDir)):
	bookName = books[bookNum]
	bookDir = os.path.join(allBooksDir,bookNum)
	bookFiles = [ os.path.join(bookDir,bookFile) for bookFile in os.listdir(bookDir) if bookFile.endswith('.htm') or bookFile.endswith('html') ]

