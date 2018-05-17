DELIMITER //
CREATE DEFINER=`root`@`localhost` PROCEDURE `add_star`(in _star varchar(100) ,in _birthyear integer)
BEGIN
	declare _star_id varchar(10) default "";
    declare _max_starid varchar (10);
    declare _temp_star_id int default 0;
    
	Select id into _star_id from stars where name = _star limit 1;
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
    select _star_id;
    
END
//
