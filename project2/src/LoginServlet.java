import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

//
@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String email = "";
		String mypassword = "";

		/*
		 * This example only allows username/password to be test/test / in the real
		 * project, you should talk to the database to verify username/password
		 */
		try {
			// Get a connection from dataSource
			Connection dbcon = dataSource.getConnection();

			// Declare our statement
			Statement statement = dbcon.createStatement();

			String query = "SELECT customers.email, customers.password" + "FROM CUSTOMERS" + "WHERE customers.email = '"
					+ username + "' and customers.password = '" + password + "';";

			// Perform the query
			ResultSet rs = statement.executeQuery(query);
			while (rs.next()) {
	                email = rs.getString("email");
	                mypassword = rs.getString("password");
			}
			if (username.equals("anteater") && password.equals("123456")) {
				// Login success:

				// set this user into the session
				request.getSession().setAttribute("user", new User(username));

				JsonObject responseJsonObject = new JsonObject();
				responseJsonObject.addProperty("status", "success");
				responseJsonObject.addProperty("message", "success");

				response.getWriter().write(responseJsonObject.toString());
			} else {
				// Login fail
				JsonObject responseJsonObject = new JsonObject();
				responseJsonObject.addProperty("status", "fail");
				if (!username.equals("anteater")) {
					responseJsonObject.addProperty("message", "user " + username + " doesn't exist");
				} else if (!password.equals("123456")) {
					responseJsonObject.addProperty("message", "incorrect password");
				}
				response.getWriter().write(responseJsonObject.toString());
			}
			  rs.close();
	           statement.close();
	           dbcon.close();

		} catch (Exception e) {
		}
	}
}
