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

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
	private static final long serialVersionUID = 2L;

	// Create a dataSource which registered in web.xml
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("application/json"); // Response mime type

		// Retrieve parameter id from url request.
		String id = request.getParameter("id");

		// Output stream to STDOUT
		PrintWriter out = response.getWriter();

		try {
			// Get a connection from dataSource
			Connection dbcon = dataSource.getConnection();

			// Construct a query with parameter represented by "?"
			String query = "SELECT   \r\n" + 
					"            				movies.title,   \r\n" + 
					"            				movies.year,   \r\n" + 
					"            				movies.director,   \r\n" + 
					"							#stars_in_movies.starId,  \r\n" + 
					"							GROUP_CONCAT( distinct stars.name) AS 'star',   \r\n" + 
					"							GROUP_CONCAT(stars.id) AS 'starid',\r\n" + 
					"							movies.id AS 'movieid',\r\n" + 
					"            		        #genres_in_movies.genreId,  \r\n" + 
					"            		        GROUP_CONCAT( distinct genres.name) AS 'genres'\r\n" + 
					"            		          \r\n" + 
					"            		FROM movies  \r\n" + 
					"            		LEFT OUTER JOIN stars_in_movies ON movies.id = stars_in_movies.movieId  \r\n" + 
					"            		LEFT OUTER JOIN stars ON stars_in_movies.starId = stars.id  \r\n" + 
					"            		LEFT OUTER JOIN genres_in_movies ON movies.id = genres_in_movies.movieId  \r\n" + 
					"            		LEFT OUTER JOIN genres ON genres_in_movies.genreId = genres.id \r\n" + 
					"            		Where movies.id = ? \r\n" + 
					"					GROUP BY   \r\n" + 
					"            						movies.id;";

			// Declare our statement
			PreparedStatement statement = dbcon.prepareStatement(query);

			// Set the parameter represented by "?" in the query to the id we get from url,
			// num 1 indicates the first "?" in the query
			statement.setString(1, id);

			// Perform the query
			ResultSet rs = statement.executeQuery();

			JsonArray jsonArray = new JsonArray();

			// Iterate through each row of rs
			while (rs.next()) {

				String title = rs.getString("title");
				String year = rs.getString("year");
				String director = rs.getString("director");
				String star = rs.getString("star");
				String starid = rs.getString("starid");
				String movieid = rs.getString("movieid");
				String genres = rs.getString("genres");

				// Create a JsonObject based on the data we retrieve from rs

				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("title", title);
				jsonObject.addProperty("year", year);
				jsonObject.addProperty("director", director);
				jsonObject.addProperty("star", star);
				jsonObject.addProperty("starid", starid);
				jsonObject.addProperty("movieid", movieid);
				jsonObject.addProperty("genres", genres);

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
