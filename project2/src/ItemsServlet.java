import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

// Declaring a WebServlet called ItemServlet, which maps to url "/items"
@WebServlet(name = "ItemServlet", urlPatterns = "/items")

public class ItemsServlet extends HttpServlet {
	
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws IOException {

        HttpSession session = request.getSession(); // Get a instance of current session on the request
        ArrayList<String> previousItems = (ArrayList<String>) session.getAttribute("previousItems"); // Retrieve data named "previousItems" from session

        // If "previousItems" is not found on session, means this is a new user, thus we create a new previousItems ArrayList for the user
        if (previousItems == null) {
            previousItems = new ArrayList<>();
            session.setAttribute("previousItems", previousItems); // Add the newly created ArrayList to session, so that it could be retrieved next time

        }
        String newItem = request.getParameter("newItem"); // Get parameter that sent by GET request url
        String clear = request.getParameter("clear");
        String removeItem = request.getParameter("removeItem");
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
        synchronized (previousItems) {
            if (newItem != null) {
                previousItems.add(newItem); // Add the new item to the previousItems ArrayList
            }
            
            if (removeItem != null) {
                previousItems.remove(removeItem); // remove the new item from the previousItems ArrayList
            }

            // Display the current previousItems ArrayList
            if(clear != null)
            	if(clear.equals("true"))
            		previousItems.clear();
            if (previousItems.size() == 0) {
                out.println("<i>No items</i>");
            } else {
            	out.println("<table BORDER=\"1\" style=\"width:100%\">");
            	out.println("<th><center> Title </center></th> <th><center> Cost </center>"
            			+ "</th> <th> <center> Remove Item </center></th>");
                for (Object previousItem : previousItems) {
                    out.println("<tr><td>" + previousItem + "</td>");
                    out.println("<td> 10 </td>");
                    out.println("<td><a href=\"items?removeItem=" + previousItem + 
                    		"\"><input type=\"submit\" value=\"remove\"></a></td></tr>");
                }
                out.println("<a href=\"items?clear=true\">\r\n" + 
                		"	<p> <button type=\"button\" class=\"btn btn-primary\">clear cart</button> </p>\r\n" + 
                		"</a>");
                out.println("</table>");
            }
        }
        //out.println("<input type=\"submit\" value=\"remove\">");
        out.println("</body></html>");
    }
}
