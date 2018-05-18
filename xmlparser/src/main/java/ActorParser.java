

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.mysql.jdbc.PreparedStatement;

public class ActorParser {
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
    private String id;
    private Integer idInt;
	
	private Vector<Actor> actorList;
	Document d;
	
	public ActorParser()
	{
		actorList = new Vector<Actor>();
		id = "AA0";
		idInt = 0;
	}
	
	public void insertSql()
	{
		 try {
	     		Class.forName("com.mysql.jdbc.Driver").newInstance();
	     		// create database connection
	     		Connection dbCon = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb", "root", "password");
				
				PreparedStatement psInsertRecord = null;
				String sqlInsertRecord = null;
				
				dbCon.setAutoCommit(false);
				
				
				sqlInsertRecord = "INSERT into stars(id, name, birthYear) VALUES(?,?,?)";
				psInsertRecord = (PreparedStatement) dbCon.prepareStatement(sqlInsertRecord);
				
				for (int i = 0; i < actorList.size(); i++)
				{
					psInsertRecord.setString(1, actorList.get(i).getId());
					psInsertRecord.setString(2, actorList.get(i).getName());
					if(actorList.get(i).getDob() != null)
						psInsertRecord.setInt(3, actorList.get(i).getDob());
					else
						psInsertRecord.setInt(3, 0);
					psInsertRecord.addBatch();
				}
				
				psInsertRecord.executeBatch();
				dbCon.commit();
				
	     		if (dbCon != null)
	     			dbCon.close();
	     		
	     		if(psInsertRecord !=null)
	     			psInsertRecord.close();
		 }
		 catch(Exception e)
		 {
			 System.out.println("error at insertSql: " + e);
		 }
	}

	 public void fillList()
	 {
		 getId();
		 idInt= Integer.parseInt(id.substring(2));
		 parseXml();
		 parseDocument();
		 printData();
	 }
	 
	 public void getId()
	 {
		 try {
     		Class.forName("com.mysql.jdbc.Driver").newInstance();
     		// create database connection
     		Connection dbCon = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb", "root", "password");

			// Declare a new statement
			Statement statement = dbCon.createStatement();
			 
     		String query = "SELECT id from stars ORDER BY stars.id desc LIMIT 1";
     		// execute query
     		ResultSet resultSet = statement.executeQuery(query);
     		if(resultSet.next())
     		{
     			id = resultSet.getString("id");
     		}
     		
     		if (dbCon != null)
     			dbCon.close();
		 }
		 catch(Exception E) {System.out.println("couldnt connect " + E);}
		 
	 }
	 
	 public void printData()
	 {
		 for (int i = 0; i <actorList.size(); i++)
		 {
			 System.out.println(actorList.get(i).toString());
		 }
	 }
	 
	 public void parseXml()
	 {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {

			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			// parse using builder to get DOM representation of the XML file
			d = db.parse("actors63.xml");

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	 
	 public void parseDocument()
	 {
		Element docEle = d.getDocumentElement();

		// get a nodelist of <employee> elements
		NodeList nl = docEle.getElementsByTagName("actor");
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {

				// get the film
				Element el = (Element) nl.item(i);
				
				// get the movie object
				Actor actor = getActor(el);
				if (actor != null) {
					actorList.add(actor);
				}
			}
		}
	}
	 
	public Actor getActor(Element el) {
		Actor c = null;
		try{
			idInt++;
			id = ("nm" + idInt);
			String name = getTextValue(el, "stagename");
			Integer dob = getIntValue(el, "dob");
			if (name != null)
				c = new Actor(id, name, dob);
			else
				System.out.println("Failed to add Star: " + name + " DOB: " + dob);
		}
		catch (Exception E)
		{
			System.out.println("failed to add cast: " + E);	//dont add movie that doesnt fit format
		}
		return c;
	}

	private String getTextValue(Element ele, String tagName) {
		try{
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if (nl != null & nl.getLength() > 0) 
		{
			Element el = (Element) nl.item(0);
			textVal = el.getFirstChild().getNodeValue();
		}
		return textVal;
		} catch (Exception e) {}
		return null;
	}
	
   private Integer getIntValue(Element ele, String tagName) {
       //in production application you would catch the exception
	   String s = getTextValue(ele, tagName);
	   if (s == null)
	   {
		   return null;}
	   else
       return Integer.parseInt(getTextValue(ele, tagName));
   }
   
   public static void main(String[] args) {
       //create an instance
       ActorParser ap = new ActorParser();

       //call run example
       ap.fillList();
       ap.insertSql();
   }
}
