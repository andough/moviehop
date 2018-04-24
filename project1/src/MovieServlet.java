import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class MovieServlet
 */
@WebServlet("/MovieServlet")
public class MovieServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public MovieServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// change this to your own mysql username and password
		String loginUser = "root";
		String loginPasswd = "password";
		String loginUrl = "jdbc:mysql://localhost:3306/cs122B";

		// set response mime type
		response.setContentType("text/html");

		// get the printwriter for writing response
		PrintWriter out = response.getWriter();

		out.println("<html>");
		out.println("<head><link rel ='stylesheet' href = 'style.css'><title>MovieHop</title></head>");

		out.println("<body>");
		out.print("browse movies: ");
		out.println("<tr><form METHOD=\"POST\">");
		out.println("<h2 ALIGN =\"LEFT\">");
		out.println("<select name='" + "browse" + "'>");
		out.println("<td><option value = 'genre'>" + "genres </option></td>");
		out.println("<td><option value = 'title'>" + "titles </option></td>");
		out.println("</select></form></tr></h2>");	
		
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// create database connection
			Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
			// declare statement
			Statement statement = connection.createStatement();

			String query = "SELECT movies.title, movies.year, movies.director, GROUP_CONCAT( distinct genrelist.name) As 'genres', GROUP_CONCAT(starlist.name) AS 'stars', ratings.rating FROM movies \n"
					+ "LEFT OUTER JOIN ratings ON movies.id = ratings.movieId\n" + "LEFT OUTER JOIN ( \n"
					+ "	SELECT * FROM genres_in_movies LEFT OUTER JOIN genres ON genres_in_movies.genreid = genres.id) genrelist ON movies.id = genrelist.movieId \n"
					+ "LEFT OUTER JOIN (\n"
					+ "	SELECT * FROM stars_in_movies LEFT OUTER JOIN stars ON stars_in_movies.starId = stars.id) starlist ON starlist.movieId = movies.id\n"
					+ "GROUP BY movies.id\n" + "ORDER BY ratings.rating DESC\n" + "LIMIT 20;";
			statement.executeQuery("set sql_mode = '';");
			ResultSet resultSet = statement.executeQuery(query);

			out.println("<form METHOD=\"POST\">");
			out.println("<h2 ALIGN = 'RIGHT'> <input type=text name=name size=32 maxlength=80>");
			out.println("<td><button type=submit name=data value='submit'>  search </button> </td></h2>");
			out.println("</form>");
			
			out.println("<h1 ALIGN = \"CENTER\"> MovieHop top 20 movies</h1>");
			out.println("<h4><table border = '1'>");

			// add table header row
			out.println("<tr>");
			out.println("<td>Title</td>");
			out.println("<td>Year</td>");
			out.println("<td>Director</td>");
			out.println("<td>Genres</td>");
			out.println("<td>Stars</td>");
			out.println("<td>Rating</td>");
			out.println("</tr>");

			// add a row for every star result
			while (resultSet.next()) {
				// get a star from result set
				String title = resultSet.getString("title");
				String year = resultSet.getString("year");
				String director = resultSet.getString("director");
				String genres = resultSet.getString("genres");
				String stars = resultSet.getString("stars");
				float rating = resultSet.getFloat("rating");

				out.println("<tr>");
				out.println("<td>" + title + "</td>");
				out.println("<td>" + year + "</td>");
				out.println("<td>" + director + "</td>");
				out.println("<td>" + genres + "</td>");
				out.println("<td>" + stars + "</td>");
				out.println("<td>" + rating + "</td>");
				out.println("</tr>");
			}

			out.println("</table></h4>");

			out.println("</body>");

			resultSet.close();
			statement.close();
			connection.close();

		} catch (Exception e) {
			/*
			 * After you deploy the WAR file through tomcat manager webpage, there's no
			 * console to see the print messages. Tomcat append all the print messages to
			 * the file: tomcat_directory/logs/catalina.out
			 * 
			 * To view the last n lines (for example, 100 lines) of messages you can use:
			 * tail -100 catalina.out This can help you debug your program after deploying
			 * it on AWS.
			 */
			e.printStackTrace();

			out.println("<body>");
			out.println("<p>");
			out.println("Exception in doGet: " + e.getMessage());
			out.println("</p>");
			out.print("</body>");
		}

		out.println("</html>");
		out.close();

	}

}
