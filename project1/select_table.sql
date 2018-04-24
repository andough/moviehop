SELECT movies.title, movies.year, movies.director, GROUP_CONCAT( distinct genrelist.name) As 'genres', GROUP_CONCAT(starlist.name) AS 'stars', ratings.rating FROM movies 
LEFT OUTER JOIN ratings ON movies.id = ratings.movieId
LEFT OUTER JOIN ( 
	SELECT * FROM genres_in_movies LEFT OUTER JOIN genres ON genres_in_movies.genreid = genres.id) genrelist ON movies.id = genrelist.movieId 
LEFT OUTER JOIN (
	SELECT * FROM stars_in_movies LEFT OUTER JOIN stars ON stars_in_movies.starId = stars.id) starlist ON starlist.movieId = movies.id
GROUP BY movies.id
ORDER BY ratings.rating DESC
LIMIT 20;