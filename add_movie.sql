DELIMITER //
CREATE DEFINER=`root`@`localhost` PROCEDURE `add_movie`(in _title varchar(100), 
in _year integer, _director varchar(100), _star varchar(100),in _birthyear integer, _genre varchar(32))
BEGIN
	#declare variable
    declare _genres_id int default 0;
    declare _star_id varchar(10) default "";
    declare _max_starid varchar (10);
    declare _temp_star_id int default 0;
    declare _movie_id varchar(10) default "";
    declare _max_movie_id varchar(10);
    declare _temp_movie_id int default 0;
    
    #find existing IDs
    Select id into _genres_id from genres where name =  _genre;
    Select id into _star_id from stars where name = _star limit 1;
    #Select id into _movie_id from movies where title = _title and year = _year and director = _director;
    
    #create new IDs if not found
    #Select id into _max_starid from stars order by id desc limit 1;
    
    if _genres_id = 0 then
		Select max(id) + 1 into _genres_id from genres;
        
        INSERT INTO genres
			(`id`,
			`name`)
			VALUES
			(_genres_id,
			_genre);
	end if;
    
	if _star_id = "" then
		#Select id into _max_starid from stars order by id desc limit 1;
        Select substring(id, 1, 2) into _max_starid from stars order by id desc limit 1;
        Select substring(id, 3, 10) + 1 into _temp_star_id from stars order by id desc limit 1;
        select concat(_max_starid, _temp_star_id) into _star_id;
        INSERT INTO stars
			(`id`,
			`name`,
            `birthYear`)
			VALUES
			(_star_id,
			_star,
            _birthYear);
	end if;
    
    if _movie_id = "" then
		#Select id into _max_starid from stars order by id desc limit 1;
        Select substring(id, 1, 2) into _max_movie_id from movies order by id desc limit 1;
        Select substring(id, 3, 10) + 1 into _temp_movie_id from movies order by id desc limit 1;
        select concat(_max_movie_id, _temp_movie_id) into _movie_id;
        
        INSERT INTO movies
			(`id`,
			`title`,
			`year`,
			`director`)
			VALUES
			(_movie_id,
			_title,
			_year,
			_director);
    end if;
    
    #check if stars_in_movies relationship exists
    set @recCount = 
		(select count(*) from stars_in_movies
		where 
			starId = _star_id
		and	movieId = _movie_id);
    
    if @recCount = 0 then
		INSERT INTO stars_in_movies
			(`starId`,
			`movieId`)
			VALUES
			(_star_id,
			_movie_id);
    end if;
    
	#check if genres_in_movies relationship exists
    set @recCount = 
		(select count(*) from genres_in_movies
		where 
			genreId = _genres_id
		and	movieId = _movie_id);
    
    if @recCount = 0 then
		INSERT INTO genres_in_movies
			(`genreId`,
			`movieId`)
			VALUES
			(_genres_id,
			_movie_id);
    end if;
    
    select _star_id, _max_starid, _genres_id, _temp_star_id, _movie_id, _max_movie_id, _temp_movie_id;
END
//
