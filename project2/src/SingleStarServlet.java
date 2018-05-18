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
@WebServlet(name = "SingleStarServlet", urlPatterns = "/api/single-star")
public class SingleStarServlet extends HttpServlet {
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
//			String query = "SELECT\r\n" + 
//					"	m.title,\r\n" + 
//					"    m.year,\r\n" + 
//					"    m.director,\r\n" + 
//					"    m.id AS movieId,\r\n" + 
//					"    s.id AS starId,\r\n" + 
//					"    IFNULL(s.birthYear, '') AS birthYear,\r\n" + 
//					"    s.name\r\n" + 
//					"from stars as s, stars_in_movies as sim, movies as m where m.id = sim.movieId and sim.starId = s.id and s.id = ?;";
			String query = "Select * from stars where id = ?;";
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

				String starId = rs.getString("id");
				String starName = rs.getString("name");
				String starDob = rs.getString("birthYear");
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("star_id", starId);
				jsonObject.addProperty("star_name", starName);
				jsonObject.addProperty("star_dob", starDob);
				String query1 = "SELECT\r\n" + 
						"	m.title,\r\n" + 
						"    m.year,\r\n" + 
						"    m.director,\r\n" + 
						"    m.id AS movieId,\r\n" + 
						"    s.id AS starId,\r\n" + 
						"    IFNULL(s.birthYear, '') AS birthYear,\r\n" + 
						"    s.name\r\n" + 
						"from stars as s, stars_in_movies as sim, movies as m where m.id = sim.movieId and sim.starId = s.id and s.id = ?;";
				PreparedStatement statement1 = dbcon.prepareStatement(query1);
				statement1.setString(1, id);
				ResultSet bs = statement1.executeQuery();
				while(bs.next()) {
					String movieId = bs.getString("movieId");
					String movieTitle = bs.getString("title");
					String movieYear = bs.getString("year");
					String movieDirector = bs.getString("director");
	
					// Create a JsonObject based on the data we retrieve from rs
					jsonObject.addProperty("movie_id", movieId);
					jsonObject.addProperty("movie_title", movieTitle);
					jsonObject.addProperty("movie_year", movieYear);
					jsonObject.addProperty("movie_director", movieDirector);
				}

				jsonArray.add(jsonObject);
				bs.close();
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
