import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import org.jasypt.util.password.StrongPasswordEncryptor;
/*
 * We create a separate android login Servlet here because
 *   the recaptcha secret key for web and android are different.
 * 
 */
@WebServlet(name = "AndroidLoginServlet", urlPatterns = "/api/android-login")
public class AndroidLoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    public AndroidLoginServlet() {
        super();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        System.out.println(username + " " + password);
        
        //String gRecaptchaResponse = request.getParameter("g-recaptcha-response");

        Map<String, String[]> map = request.getParameterMap();
        for (String key: map.keySet()) {
            System.out.println(key);
            System.out.println(map.get(key)[0]);
        }
        
        // verify recaptcha first
       /* try {
            RecaptchaVerifyUtils.verify(gRecaptchaResponse, RecaptchaConstants.ANDROID_SECRET_KEY);
        } catch (Exception e) {
            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", e.getMessage());
            response.getWriter().write(responseJsonObject.toString());
            return;
        }
        
        // then verify username / password
        JsonObject loginResult = LoginVerifyUtils.verifyUsernamePassword(username, password);
        
        if (loginResult.get("status").getAsString().equals("success")) {
            // login success
            request.getSession().setAttribute("user", new User(username));
            response.getWriter().write(loginResult.toString());
        } else {
            response.getWriter().write(loginResult.toString());
        }*/
        
        try 
       {
        	//Connection dbcon = dataSource.getConnection();
        	//RecaptchaVerifyUtils.verify(gRecaptchaResponse, RecaptchaConstants.ANDROID_SECRET_KEY);
        	Class.forName("com.mysql.jdbc.Driver").newInstance();
            // Create a new connection to database
            Connection dbcon = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb","root","password");
        	//Connection dbcon = dataSource.getConnection();
        	
            // Declare a new statement
            String query = String.format("SELECT * from customers where email='%s'", username);
            PreparedStatement statement = dbcon.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            boolean success = false;
            if (rs.next()) {
            	String encryptedPassword = rs.getString("password");
            	success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
            	if (success)
            	{

            	

                request.getSession().setAttribute("user", new User(username));

                JsonObject responseJsonObject = new JsonObject(); //
                //responseJsonObject.addProperty("status", "success"); //
               // responseJsonObject.addProperty("message", "success"); //
                //responseJsonObject.addProperty("user",username); //
                response.getWriter().write(username);
            	System.out.println("match");
            	}
            	else {
            		 System.out.println("no match");
                     // Login fail
                     response.getWriter().write("invalid login");
            	}
            }
             else {
            	 System.out.println("no match");
                // Login fail
                response.getWriter().write("invalid login");
             }
            rs.close();
            statement.close();
            dbcon.close();

        	} 
        	catch (Exception ex) 
        	{System.out.println("exception: " + ex);}

    }

}
