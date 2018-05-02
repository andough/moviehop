import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.*;

// Declaring a WebServlet called ItemServlet, which maps to url "/items"
@WebServlet(name = "ItemServlet", urlPatterns = "/items")

public class ItemsServlet extends HttpServlet {
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;
	
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws IOException {

        HttpSession session = request.getSession(); // Get a instance of current session on the request
        Map<String,Integer> previousItems = (Map<String,Integer>) session.getAttribute("previousItems"); // Retrieve data named "previousItems" from session

        // If "previousItems" is not found on session, means this is a new user, thus we create a new previousItems ArrayList for the user
        if (previousItems == null) {
            previousItems = new HashMap<String,Integer>();
            session.setAttribute("previousItems", previousItems); // Add the newly created ArrayList to session, so that it could be retrieved next time

        }
        String newItem = request.getParameter("newItem"); // Get parameter that sent by GET request url
        String clear = request.getParameter("clear");
        String removeItem = request.getParameter("removeItem");
        String removeAll = request.getParameter("removeAll");
        User username = (User)session.getAttribute("user");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String title = "Shopping Cart";
        String docType =
                "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\n";
        out.println(String.format("%s<html>\n<head><title>%s</title><link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\">\r\n" + 
        		"\r\n" + 
        		"<!-- jQuery library -->\r\n" + 
        		"<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js\"></script>\r\n" + 
        		"\r\n" + 
        		"<!-- Latest compiled JavaScript -->\r\n" + 
        		"<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js\"></script> \r\n" + 
        		"	<link rel=\"import\" href=\"bootstrap.html\"></head>\n<body bgcolor=\"#FDF5E6\">\n"
        		+ "<p><div align=\"right\"> account: "+ username.getUsername() +"</div></p><h1>%s <span class=\"glyphicon glyphicon-shopping-cart\"></span></h1>", docType, title, title));
        // In order to prevent multiple clients, requests from altering previousItems ArrayList at the same time, we lock the ArrayList while updating

        Integer i = 1;
        synchronized (previousItems) {
            if (newItem != null) {
				if(previousItems.containsKey(newItem))
            		i += (Integer)previousItems.get(newItem);
            	previousItems.put(newItem,i); // Add the new item to the previousItems ArrayList
            }
            
            Integer r = 0;
            if (removeItem != null && previousItems.containsKey(removeItem)) {
                r = (Integer)previousItems.get(removeItem) -1;
                if (r > 0)
                	previousItems.put(removeItem, r);
                else
                	previousItems.remove(removeItem);
                // remove the new item from the previousItems ArrayList
            }
            
            if(removeAll != null)
            	previousItems.remove(removeAll);

            // Display the current previousItems ArrayList
            if(clear != null)
            	if(clear.equals("true"))
            		previousItems.clear();
            if (previousItems.size() == 0) {
                out.println("<i>No items</i>");
            } else {
            	out.println("<table BORDER=\"1\" style=\"width:100%\">");
            	out.println("<th><center> ID </center></th>"
            			+ "<th><center> Title </center></th> <th><center> count </center></th>"
            			+ "<th> <center> Remove One </center></th>"
            			+ " <th> <center> Remove All </center></th>");
            	Iterator it = previousItems.entrySet().iterator();
            	
            	try {
                // Create a new connection to database
                Connection dbCon = dataSource.getConnection();

                // Declare a new statement
                Statement statement = dbCon.createStatement();
            		
            	while (it.hasNext()) {
                //for (Object previousItem : previousItems) {
            		Map.Entry pair = (Map.Entry)it.next();
            		String previousItem = (String)pair.getKey();
            		Integer count = (Integer)pair.getValue();
            		
            		String query = String.format("SELECT movies.id, movies.title from movies where movies.id = '%s';", previousItem);
                    ResultSet rs = statement.executeQuery(query);	
                    if (rs.next())
                    {
            		
                    out.println("<tr><td>" + previousItem + "</td>");
                    out.println("<td>"+ rs.getString("movies.title") +"</td><td>"+ count +"</td>");
                    out.println("<td><a href=\"items?removeItem=" + previousItem + "\">"
                    		+ "<input type=\"submit\" value=\"remove\"></a></td>");
                    out.println("<td><a href=\"items?removeAll=" + previousItem + "\">"
                    		+ "<input type=\"submit\" value=\"remove All\"></a></td></tr>");
                    }
                }
                out.println("<a href=\"items?clear=true\">\r\n" + 
                		"	<p> <button type=\"button\" class=\"btn btn-primary\">clear cart</button> </p>\r\n" + 
                		"</a>");
                out.println("</table>");
            	}
            	catch(Exception e) {}
            }
        }
        //out.println("<input type=\"submit\" value=\"remove\">");
        out.println("</body></html>");
    }
}
