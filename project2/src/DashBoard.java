import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

@WebServlet(name = "DashBoard", urlPatterns = "/api/DashBoard")
public class DashBoard extends HttpServlet {
    private static final long serialVersionUID = 1L;
	@Resource(name = "jdbc/master")
	private DataSource dataSource;

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// HttpSession session = request.getSession(); // Get a instance of current
		// session on the request
		// Map<String,Integer> previousItems = (Map<String,Integer>)
		// session.getAttribute("previousItems");
		PrintWriter out = response.getWriter();
		String title = request.getParameter("title");
		String year = request.getParameter("year");
		String director = request.getParameter("director");
		String star = request.getParameter("star");
		String birthyear = request.getParameter("birthyear");
		String genre = request.getParameter("genre");
		if (birthyear == "")
		{
			birthyear = "0";
		}

		try {

			Connection dbcon = dataSource.getConnection();
			// Declare a new statement
			CallableStatement statement = dbcon.prepareCall("{Call add_movie(?,?,?,?,?,?)}");
			statement.setString(1, title);
			statement.setString(2, year);
			statement.setString(3, director);
			statement.setString(4, star);
			statement.setString(5, birthyear);
			statement.setString(6, genre);
			String movieid = "";
			boolean hasResults = statement.execute();
			if (hasResults == true) {
				// Login success:

				// set this user into the session
				//request.getSession().setAttribute("creditCard", new User(username));
				
				JsonObject responseJsonObject = new JsonObject();
                responseJsonObject.addProperty("status2", "success");
                responseJsonObject.addProperty("message2", "success");
                String q = String.format(
    					"SELECT  id from movies where title = '%s';", title);
    			PreparedStatement bc = dbcon.prepareStatement(q);
    			ResultSet rs = bc.executeQuery();
    			if (rs.next()) {
    				movieid = rs.getString("id");
    			}
    			responseJsonObject.addProperty("movieid", movieid);
				response.getWriter().write(responseJsonObject.toString());
				bc.close();
			} else {
				// Login fail
				JsonObject responseJsonObject = new JsonObject();
				responseJsonObject.addProperty("status2", "fail");
				responseJsonObject.addProperty("message2", "invalid card");
				response.getWriter().write(responseJsonObject.toString());
			}
			statement.close();
			dbcon.close();

		} catch (Exception ex) {

			// Output Error Massage to html
			JsonObject responseJsonObject = new JsonObject();
			responseJsonObject.addProperty("status2", "fail");
			responseJsonObject.addProperty("message2", ex.getMessage());
			response.getWriter().write(responseJsonObject.toString());
			//out.println(String.format(ex.getMessage()));
			return;
		}
		}
}


