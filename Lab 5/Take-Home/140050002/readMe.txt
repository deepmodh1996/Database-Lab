
pre requisites :

1. javax.json-1.0.jar must be added to classpath and build path for json objects. which can be downloaded below.
	http://www.java2s.com/Code/JarDownload/javax.json/javax.json-1.0.jar.zip
2. there must be necessary tables in database. (regnDates, password as discribed in previous lab problem statements.)
	example : regnDates table
		CREATE TABLE regnDates (
		    year	numeric(4,0),
		    semester	varchar(6),
		    startTS   timestamp,
		    endTS timestamp
		);
3. add all files.

How to run :

1. run login.html.
2. enter id and password.
3. for part 1,2 and 3 : click on register button.
   for part 4 click on browse courses link on user home.