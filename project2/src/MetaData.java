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
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

@WebServlet(name = "MetaData", urlPatterns = "/api/MetaData")
public class MetaData extends HttpServlet {
    private static final long serialVersionUID = 1L;
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		try {
			Connection dbCon = dataSource.getConnection();
			Statement statement = dbCon.createStatement();
			String query = "select table_name, column_name, column_type from information_schema.columns\r\n" + 
					"Where table_schema = DATABASE()\r\n" + 
					"Order by table_name, ordinal_position;";
			ResultSet rs = statement.executeQuery(query);
            JsonArray jsonArray = new JsonArray();
            while (rs.next()) {
                String table_name = rs.getString("table_name");
                String column_name = rs.getString("column_name");
                String column_type = rs.getString("column_type");
                

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("table_name", table_name);
                jsonObject.addProperty("column_name", column_name);
                jsonObject.addProperty("column_type", column_type);
                jsonArray.add(jsonObject);
            }
            
            // write JSON string to output
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

            rs.close();
            statement.close();
            dbCon.close();
			
		}
		catch(Exception ex) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", ex.getMessage());
			out.write(jsonObject.toString());

			// set reponse status to 500 (Internal Server Error)
			response.setStatus(500);
		}
		out.close();
	}
}