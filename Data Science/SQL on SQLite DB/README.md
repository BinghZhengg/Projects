# Repository: template.project2
> 
**Completed Mar 2, 2022**

### Database

Schema of SQLite database stored in the file `cs1656-public.db`:
* Actors (aid, fname, lname, gender)  
* Movies (mid, title, year, rank)  
* Directors (did, fname, lname)  
* Cast (aid, mid, role)  
* Movie_Director (did, mid)  

### Query Tasks

* **[1]** List all actors first and last name w/ appearances in at least one film both in the 80s (1980-1990, both ends inclusive) and the 21st century (>=2000). Sort alphabetically last name then first name.

* **[2]** List all the movies (title, year) released in the same year `"Rogue One: A Star Wars Story"` but had a better rank attribute value. Sort title alphabetically.  

* **[3]** List all the actors (first and last name) who played in a Star Wars movie in decreasing order of how many Star Wars movies they appeared in. If an actor plays multiple roles in the same movie, count as one movie. If there is a tie, use actor's last and first name to generate a full sorted order. Sort by descending number of movies, the actor's last name and first name alphabetically.  

* **[4]** Find the actor(s) (first and last name) who only acted in films released before 1980. Sort alphabetically by the actor's last and first name.  

* **[5]** List the top 10 directors in descending number of films they directed (first name, last name, number of films directed). Sort alphabetically by descending number of films, the actor's last name and first name alphabetically.  

* **[6]** Find the top 10 movies with the largest cast in descending order (title, number of cast members). Show all movies in case of a tie.  

* **[7]** Find the movie(s) whose cast has more actresses than actors.  Show the title, number of actresses, and number of actors in the results. Sort alphabetically by movie title.   

* **[8]** Find all the actors who have worked with at least 7 different directors. Self-directing doesn't count but count all directors in a movie towards the threshold of 7 directors. Show the actor's first, last name, and the number of directors he/she has worked with. Sort in decreasing number of directors.

* **[9]** Count the movies an actor appeared in his/her debut year whose name begins with a D. Show the actor's first and last name and the count. Sort by decreasing order of the count, then first and last name.  

* **[10]** Find instances of nepotism between actors and directors (same last name, different first name). Show the last name and the title of the movie sorted alphabetically by last name then movie title.  

* **[11]** List all actors whose Bacon number is 2 (first name, last name). Sort results by the last and first name.

* **[12]** Assume that the popularity of an actor is reflected by the average rank of all the movies he/she has acted in. Find top 20 most popular actors (in descreasing order of popularity). List actor's first/last name, the total number of movies he/she has acted, and his/her popularity score. Ignore ties at the number 20 spot.  


