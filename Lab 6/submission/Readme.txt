140050002

Change folder address containing data on line 47.

Idea Used : 

for each word, create pairs that contain ( (prefix, word) , 1) ) for all prefix with length > 3
	Combine pairs using combiner which adds frequency.
	Then sort and find 3 most occuring words and store them in listPrint

Same way for calculating following words.
	Note : for last word in line, no following word is considered