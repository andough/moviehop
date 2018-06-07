import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
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
import org.jasypt.util.password.StrongPasswordEncryptor;

//
@WebServlet(name = "EmployeeLogin", urlPatterns = "/api/EmployeeLogin")
public class EmployeeLogin extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
 // Create a dataSource which registered in web.xml
//    @Resource(name = "jdbc/moviedb")
//    private DataSource dataSource;
    

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	 PrintWriter out = response.getWriter();
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        /* This example only allows username/password to be test/test
        /  in the real project, you should talk to the database to verify username/password
        */
        try 
       {
            // Create a new connection to database
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


            // Declare a new statement
            String query = "SELECT * from employees where email= ?";
            PreparedStatement statement = dbcon.prepareStatement(query);
            
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            boolean success = false;
            if (rs.next()) {
                // Login success:
            	String encryptedPassword = rs.getString("password");
            	success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
            	if (success) {
            	request.getSession().setAttribute("employee", new User(username, true));
                JsonObject responseJsonObject = new JsonObject();
                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");

                response.getWriter().write(responseJsonObject.toString());
            	}
             else {
                // Login fail
                JsonObject responseJsonObject = new JsonObject();
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "invalid login");
                response.getWriter().write(responseJsonObject.toString());
             }}
            rs.close();
            statement.close();
            dbcon.close();

        	} 
        	catch (Exception ex) 
        	{

            // Output Error Massage to html
            out.println(String.format("<html><head><title>MovieDB: Error</title></head>\n<body><p>SQL error in doGet: %s</p></body></html>", ex.getMessage()));
            return;
        	}
 
    }
    
}
