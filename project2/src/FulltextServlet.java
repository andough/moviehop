import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
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
@WebServlet(name = "FulltextServlet", urlPatterns = "/api/fulltext")
public class FulltextServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
//    @Resource(name = "jdbc/moviedb")
//    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json"); // Response mime type

		// Retrieve parameter id from url request.
		String whereclause = request.getParameter("whereclause");
		
        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            // Get a connection from dataSource

            Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
                out.println("envCtx is NULL");

            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/TestDB");

            // the following commented lines are direct connections without pooling
            //Class.forName("org.gjt.mm.mysql.Driver");
            //Class.forName("com.mysql.jdbc.Driver").newInstance();
            //Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);

            if (ds == null)
                out.println("ds is null.");

            Connection dbcon = ds.getConnection();
            if (dbcon == null)
                out.println("dbcon is null.");

            String query = "SELECT movies.title, movies.year, movies.director, movies.id AS 'movieid', GROUP_CONCAT( distinct stars.name) AS 'star', GROUP_CONCAT( distinct stars.id) AS 'starid', GROUP_CONCAT( distinct genres.name) AS 'genres', ratings.rating FROM movies  LEFT OUTER JOIN stars_in_movies ON movies.id = stars_in_movies.movieId  LEFT OUTER JOIN stars ON stars_in_movies.starId = stars.id  LEFT OUTER JOIN genres_in_movies ON movies.id = genres_in_movies.movieId  LEFT OUTER JOIN genres ON genres_in_movies.genreId = genres.id LEFT OUTER JOIN ratings ON movies.id = ratings.movieId WHERE match(title) against (? in boolean mode) GROUP BY movies.title;";
            PreparedStatement statement = dbcon.prepareStatement(query);
            statement.setString(1, whereclause);
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
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json"); // Response mime type

		// Retrieve parameter id from url request.
		String whereclause = request.getParameter("whereclause");
		
        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            // Get a connection from dataSource
        	Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
                out.println("envCtx is NULL");

            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/TestDB");

            // the following commented lines are direct connections without pooling
            //Class.forName("org.gjt.mm.mysql.Driver");
            //Class.forName("com.mysql.jdbc.Driver").newInstance();
            //Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);

            if (ds == null)
                out.println("ds is null.");

            Connection dbcon = ds.getConnection();
            if (dbcon == null)
                out.println("dbcon is null.");

            String query = "SELECT movies.title, movies.year, movies.director, movies.id AS 'movieid', GROUP_CONCAT( distinct stars.name) AS 'star', GROUP_CONCAT( distinct stars.id) AS 'starid', GROUP_CONCAT( distinct genres.name) AS 'genres', ratings.rating FROM movies  LEFT OUTER JOIN stars_in_movies ON movies.id = stars_in_movies.movieId  LEFT OUTER JOIN stars ON stars_in_movies.starId = stars.id  LEFT OUTER JOIN genres_in_movies ON movies.id = genres_in_movies.movieId  LEFT OUTER JOIN genres ON genres_in_movies.genreId = genres.id LEFT OUTER JOIN ratings ON movies.id = ratings.movieId WHERE match(title) against (? in boolean mode) GROUP BY movies.title;";
            PreparedStatement statement = dbcon.prepareStatement(query);
            statement.setString(1, whereclause);
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
