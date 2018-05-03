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
import java.util.*;

@WebServlet(name = "CheckOutServlet", urlPatterns = "/CheckOut")
public class CheckOutServlet extends HttpServlet{
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;
	
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws IOException {
    	HttpSession session = request.getSession(); // Get a instance of current session on the request
        Map<String,Integer> previousItems = (Map<String,Integer>) session.getAttribute("previousItems");
    }

}
