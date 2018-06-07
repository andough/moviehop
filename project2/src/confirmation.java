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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

@WebServlet(name = "confirmation", urlPatterns = "/confirmation")
public class confirmation extends HttpServlet{
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession(); // Get a instance of current session on the request
		Map<String, Integer> previousItems = (Map<String, Integer>) session.getAttribute("previousItems");

		String customerId = request.getParameter("customerId");

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String title = "confirmation";
		String docType = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\n";
		out.println(String.format(
				"%s<html>\n<head><title>%s</title><link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\">\r\n"
						+ "\r\n" + "<!-- jQuery library -->\r\n"
						+ "<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js\"></script>\r\n"
						+ "\r\n" + "<!-- Latest compiled JavaScript -->\r\n"
						+ "<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js\"></script> \r\n"
						+ "	<link rel=\"import\" href=\"bootstrap.html\"></head>\n<body bgcolor=\"#FDF5E6\">\n",
				docType, title, title));

		try {
			out.println("<center><h2>Review Your Order</h2></center>");
			// Create a new connection to database
			Connection dbCon = dataSource.getConnection();

			// Declare a new statement

			synchronized (previousItems) {

				if (previousItems.size() == 0) {
					out.println("<i>No items</i>");
				} else {
					out.println("<br><div align=\"center\"><table BORDER=\"1\" style=\"width:60%\">");
					out.println("<th><center> Title </center></th> <th><center> count </center></th>");

					Iterator it = previousItems.entrySet().iterator();

					while (it.hasNext()) {
						Map.Entry pair = (Map.Entry) it.next();
						String previousItem = (String) pair.getKey();
						Integer count = (Integer) pair.getValue();

						String query = String.format(
								"SELECT movies.id, movies.title from movies where movies.id = '%s';", previousItem);
						PreparedStatement statement = dbCon.prepareStatement(query);
						
						ResultSet rs = statement.executeQuery();

						if (rs.next()) {
							out.println("<tr><td>" + rs.getString("movies.title") + "</td><td>" + count + "</td></tr>");
						}
						rs.close();
						statement.close();
					}
					
					out.println("</table></div>");
					
					//
	                out.println("<center><a href=\"placedOrder\">\r\n" + 
	                		"<br><p> <button type=\"button\" class=\"btn btn-primary\">confirm order</button> </p></a></center>");
				}
				
			}
        	dbCon.close();
        	out.close();
		} catch (Exception e) {
		}

		out.println("</body></html>");

	}
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

		doGet(request, response);

	}
}