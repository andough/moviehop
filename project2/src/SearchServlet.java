import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;


// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "SearchServlet", urlPatterns = "/api/search")
public class SearchServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json"); // Response mime type

		// Retrieve parameter id from url request.
		String whereclause = request.getParameter("whereclause");
		
        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            // Declare our statement
            //Statement statement = dbcon.createStatement();

//            String query = "SELECT \r\n" + 
//            		"		movies.title, \r\n" + 
//            		"		movies.year, \r\n" + 
//            		"		movies.director, \r\n" + 
//            		"		movies.id AS 'movieid', \r\n" + 
//            		"       GROUP_CONCAT( distinct stars.name) AS 'star', \r\n" + 
//            		"       GROUP_CONCAT( distinct stars.id) AS 'starid', \r\n" + 
//            		"        GROUP_CONCAT( distinct genres.name) AS 'genres', \r\n" + 
//            		"        ratings.rating \r\n" + 
//            		"        \r\n" + 
//            		"FROM movies\r\n" + 
//            		"LEFT OUTER JOIN stars_in_movies ON movies.id = stars_in_movies.movieId\r\n" + 
//            		"LEFT OUTER JOIN stars ON stars_in_movies.starId = stars.id\r\n" + 
//            		"LEFT OUTER JOIN genres_in_movies ON movies.id = genres_in_movies.movieId\r\n" + 
//            		"LEFT OUTER JOIN genres ON genres_in_movies.genreId = genres.id\r\n" +
//            		"LEFT OUTER JOIN ratings ON movies.id = ratings.movieId\r\n" +
//            		"WHERE " + whereclause +
//            		" GROUP BY \r\n" + 
//            				"		movies.title; \r\n";
            String query = String.format("SELECT  movies.title, movies.year, movies.director, movies.id AS 'movieid', GROUP_CONCAT( distinct stars.name) AS 'star', GROUP_CONCAT( distinct stars.id) AS 'starid', GROUP_CONCAT( distinct genres.name) AS 'genres', ratings.rating FROM movies  LEFT OUTER JOIN stars_in_movies ON movies.id = stars_in_movies.movieId  LEFT OUTER JOIN stars ON stars_in_movies.starId = stars.id  LEFT OUTER JOIN genres_in_movies ON movies.id = genres_in_movies.movieId  LEFT OUTER JOIN genres ON genres_in_movies.genreId = genres.id LEFT OUTER JOIN ratings ON movies.id = ratings.movieId WHERE %s GROUP BY movies.title;" , whereclause );
            PreparedStatement statement = dbcon.prepareStatement(query);
            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                String title = rs.getString("title");
                String year = rs.getString("year");
                String star = rs.getString("star");
                String director = rs.getString("director");
                String starid = rs.getString("starid");
                String genres = rs.getString("genres");
                String rating = rs.getString("rating");
                String movieid = rs.getString("movieid");
                

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("title", title);
                jsonObject.addProperty("year", year);
                jsonObject.addProperty("star", star);
                jsonObject.addProperty("director", director);
                jsonObject.addProperty("starid", starid);
                jsonObject.addProperty("genres", genres);
                jsonObject.addProperty("rating", rating);
                jsonObject.addProperty("movieid", movieid);
                jsonArray.add(jsonObject);
            }
            
            // write JSON string to output
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

            rs.close();
            statement.close();
            dbcon.close();
        } catch (Exception e) {
        	
			// write error message JSON object to output
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());

			// set reponse status to 500 (Internal Server Error)
			response.setStatus(500);

        }
        out.close();

    }
}
