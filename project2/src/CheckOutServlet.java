import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

@WebServlet(name = "CheckOutServlet", urlPatterns = "/api/CheckOut")
public class CheckOutServlet extends HttpServlet {
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// HttpSession session = request.getSession(); // Get a instance of current
		// session on the request
		// Map<String,Integer> previousItems = (Map<String,Integer>)
		// session.getAttribute("previousItems");

		String id = request.getParameter("creditid");
		String expiration = request.getParameter("expiration");
		String first = request.getParameter("first");
		String last = request.getParameter("last");

		PrintWriter out = response.getWriter();

		try {

			// Create a new connection to database
			Connection dbCon = dataSource.getConnection();

			// Declare a new statement
			Statement statement = dbCon.createStatement();
			String query = String.format(
					"SELECT creditcards.id, creditcards.expiration, creditcards.firstName, creditcards.lastName"
					+ " from creditcards where creditcards.id = '%s' and creditcards.expiration = '%s' and creditcards.firstName = '%s'"
					+ "and creditcards.lastName = '%s';", id,expiration,first,last);
			ResultSet rs = statement.executeQuery(query);
			if (rs.next()) {
				// Login success:

				// set this user into the session
				//request.getSession().setAttribute("creditCard", new User(username));
				
				JsonObject responseJsonObject = new JsonObject();
                responseJsonObject.addProperty("status2", "success");
                responseJsonObject.addProperty("message2", "success");
				responseJsonObject.addProperty("creditid", id);
				responseJsonObject.addProperty("expiration", expiration);
				responseJsonObject.addProperty("first", first);
				responseJsonObject.addProperty("last", last);

				response.getWriter().write(responseJsonObject.toString());
			} else {
				// Login fail
				JsonObject responseJsonObject = new JsonObject();
				responseJsonObject.addProperty("status2", "fail");
				responseJsonObject.addProperty("message2", "invalid card");
				response.getWriter().write(responseJsonObject.toString());
			}
			rs.close();
			statement.close();
			dbCon.close();

		} catch (Exception ex) {

			// Output Error Massage to html
			out.println(String.format(
					"<html><head><title>MovieDB: Error</title></head>\n<body><p>SQL error in doGet: %s</p></body></html>",
					ex.getMessage()));
			return;
		}
	}

}
