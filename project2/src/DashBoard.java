import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.sql.CallableStatement;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

@WebServlet(name = "DashBoard", urlPatterns = "/api/DashBoard")
public class DashBoard extends HttpServlet {
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// HttpSession session = request.getSession(); // Get a instance of current
		// session on the request
		// Map<String,Integer> previousItems = (Map<String,Integer>)
		// session.getAttribute("previousItems");

		String title = request.getParameter("title");
		String year = request.getParameter("year");
		String director = request.getParameter("director");
		String star = request.getParameter("star");
		String genre = request.getParameter("genre");
		PrintWriter out = response.getWriter();

		try {

			// Create a new connection to database
			Connection dbCon = dataSource.getConnection();

			// Declare a new statement
			CallableStatement statement = dbCon.prepareCall("{Call add_movie(?,?,?,?,?)}");
			statement.setString(1, title);
			statement.setString(2, year);
			statement.setString(3, director);
			statement.setString(4, star);
			statement.setString(5, genre);
			boolean hasResults = statement.execute();
			if (hasResults == true) {
				// Login success:

				// set this user into the session
				//request.getSession().setAttribute("creditCard", new User(username));
				
				JsonObject responseJsonObject = new JsonObject();
                responseJsonObject.addProperty("status2", "success");
                responseJsonObject.addProperty("message2", "success");

				response.getWriter().write(responseJsonObject.toString());
			} else {
				// Login fail
				JsonObject responseJsonObject = new JsonObject();
				responseJsonObject.addProperty("status2", "fail");
				responseJsonObject.addProperty("message2", "invalid card");
				response.getWriter().write(responseJsonObject.toString());
			}
			statement.close();
			dbCon.close();

		} catch (Exception ex) {

			// Output Error Massage to html
			JsonObject responseJsonObject = new JsonObject();
			responseJsonObject.addProperty("status2", "fail");
			responseJsonObject.addProperty("message2", ex.getMessage());
			response.getWriter().write(responseJsonObject.toString());
			//out.println(String.format(ex.getMessage()));
			return;
		}
		try {
			Connection dbCon = dataSource.getConnection();
			Statement statement = dbCon.createStatement();
			String query = "select table_name, column_name, column_type from information_schema.columns\r\n" + 
					"Where table_schema = DATABASE()\r\n" + 
					"Order by table_name, ordinal_position;";
			ResultSet rs = statement.executeQuery(query);
            JsonArray jsonArray = new JsonArray();
            while (rs.next()) {
                String table_name = rs.getString("table_name");
                String column_name = rs.getString("column_name");
                String column_type = rs.getString("column_type");
                

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("table_name", table_name);
                jsonObject.addProperty("column_name", column_name);
                jsonObject.addProperty("column_type", column_type);
                jsonArray.add(jsonObject);
            }
            
            // write JSON string to output
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

            rs.close();
            statement.close();
            dbCon.close();
			
		}
		catch(Exception ex) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", ex.getMessage());
			out.write(jsonObject.toString());

			// set reponse status to 500 (Internal Server Error)
			response.setStatus(500);
		}
		out.close();
		}
}


