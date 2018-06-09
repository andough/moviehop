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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

@WebServlet(name = "InsertStar", urlPatterns = "/api/InsertStar")
public class InsertStar extends HttpServlet {
    private static final long serialVersionUID = 1L;
	@Resource(name = "jdbc/master")
	private DataSource dataSource;

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// HttpSession session = request.getSession(); // Get a instance of current
		// session on the request
		// Map<String,Integer> previousItems = (Map<String,Integer>)
		// session.getAttribute("previousItems");
		String star1 = request.getParameter("star1");
		String birthyear1 = request.getParameter("birthyear1");
		String starid = "";
		if (birthyear1 == "")
		{
			birthyear1 = "0";
		}
		try {

			// Create a new connection to database
			Connection dbCon = dataSource.getConnection();
			// Declare a new statement
			CallableStatement statement = dbCon.prepareCall("{CALL add_star(?,?)}");
			statement.setString(1, star1);
			statement.setString(2, birthyear1);
			Boolean rs = statement.execute();
			if (rs) {
				// Login success:

				// set this user into the session
				//request.getSession().setAttribute("creditCard", new User(username));
				
				JsonObject responseJsonObject = new JsonObject();
                responseJsonObject.addProperty("status2", "success");
                responseJsonObject.addProperty("message2", "success");
                String q = String.format(
    					"SELECT id from stars where name = '%s';", star1);
    			PreparedStatement bc = dbCon.prepareStatement(q);
    			ResultSet rs1 = bc.executeQuery();
    			if (rs1.next()) {
    				starid = rs1.getString("id");
    			}
    			responseJsonObject.addProperty("starid", starid);
				response.getWriter().write(responseJsonObject.toString());
				bc.close();
			} else {
				// Login fail
				JsonObject responseJsonObject = new JsonObject();
				responseJsonObject.addProperty("status2", "fail");
				responseJsonObject.addProperty("message2", "fail");
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
		}
}


