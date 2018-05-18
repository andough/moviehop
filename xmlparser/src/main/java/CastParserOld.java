

import java.util.Vector;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.*;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.mysql.jdbc.PreparedStatement;

public class CastParserOld {

	private Vector<Cast> castList;
	Document d;
	private String movieInDb;
	private boolean isIndb;
	private HashMap<String,String> actorDb = new HashMap<String,String>();

	public CastParserOld() {
		castList = new Vector<Cast>();
	}

	public void insertSql() {
		try {
			movieInDb = "";
			isIndb = false;

			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// create database connection
			Connection dbCon = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb", "root", "password");
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb", "root", "password");
			Statement statement = conn.createStatement();

			PreparedStatement psInsertRecord = null;
			String sqlInsertRecord = null;

			dbCon.setAutoCommit(true);
			ResultSet rs;
			String query = "";
			
			query = "select id from stars order by id desc limit 1;";
			rs = statement.executeQuery(query);
			int largestid = 0;
			if (rs.next())
				largestid = Integer.parseInt(rs.getString("id").substring(2));
			
			query = "Select id, name from stars;";
			rs = statement.executeQuery(query);
			while (rs.next())
			{
				actorDb.put(rs.getString("name"), rs.getString("id"));
			}
			
			
			sqlInsertRecord = "INSERT into stars_in_movies(starId, movieId) VALUES(?,?)";
			psInsertRecord = (PreparedStatement) dbCon.prepareStatement(sqlInsertRecord);
			for (int i = 0; i < castList.size(); i++) {
				//update movieInDb if current movie is not same as previous
				if(!castList.get(i).getMovie().equals(movieInDb))
				{
					movieInDb = castList.get(i).getMovie();
					query = String.format("Select * from movies where id = '%s';", movieInDb);
					rs = statement.executeQuery(query);
					if (rs.next())
						isIndb = true;
					else
						isIndb = false;
				} //else we use the cached data
				
				if (isIndb)
				{
					String star = castList.get(i).getActor().replace("'", "");
					//query = String.format("Select * from stars where name = '%s';", star);
					//rs = statement.executeQuery(query);
					//if (rs.next())
					{
						//String starId = rs.getString("id");
						String starId = actorDb.get(star);
						if (starId == null)
						{
							query = "INSERT into stars(id,name) VALUES(?,?);";
							psInsertRecord = (PreparedStatement) dbCon.prepareStatement(query);
							largestid++;
							starId = "nm" + largestid;
							
							psInsertRecord.setString(1, starId);
							psInsertRecord.setString(2, star);
							psInsertRecord.executeUpdate();
							actorDb.put(star, starId);
							System.out.println("added actor to db: " + star + " " + starId);
							
						}
						sqlInsertRecord = "INSERT into stars_in_movies(starId, movieId) VALUES(?,?)";
						psInsertRecord = (PreparedStatement) dbCon.prepareStatement(sqlInsertRecord);
						psInsertRecord.setString(1, starId);
						psInsertRecord.setString(2, castList.get(i).getMovie());
						//psInsertRecord.addBatch();
						psInsertRecord.executeUpdate();
						
						
					}
					//else
						//System.out.println("failed to add, star not found in db: " + star);
				}
				else
					System.out.println("failed to add, movieId not found in db: " + movieInDb);
			}
			//if (psInsertRecord != null)
			//	psInsertRecord.executeBatch();
			//dbCon.commit();

			if (statement != null)
				statement.close();

			if (conn != null)
				conn.close();

			if (dbCon != null)
				dbCon.close();

			if (psInsertRecord != null)
				psInsertRecord.close();
		} catch (Exception e) {
			System.out.println("error at insertSql: " + e);
			e.printStackTrace();
		}
	}

	public void fillList() {
		parseXml();
		parseDocument();
		//printData();
	}

	public void printData() {
		for (int i = 0; i < castList.size(); i++) {
			System.out.println(castList.get(i).toString());
		}
	}

	public void parseXml() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {

			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			// parse using builder to get DOM representation of the XML file
			d = db.parse("casts124.xml");

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public void parseDocument() {
		Element docEle = d.getDocumentElement();

		// get a nodelist of <employee> elements
		NodeList nl = docEle.getElementsByTagName("m");
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {

				// get the film
				Element el = (Element) nl.item(i);

				// get the movie object
				Cast cast = getCast(el);
				if (cast != null) {
					castList.add(cast);
				}
			}
		}
	}

	public Cast getCast(Element el) {
		Cast c = null;
		try {
			String movieId = getTextValue(el, "f");
			String actor = getTextValue(el, "a");
			if (movieId != null && actor != null && !actor.equals("s a"))
				c = new Cast(actor, movieId);
			else if (movieId == null)
				System.out.println("missing/invalid movieId");
			else if (actor == null)
				System.out.println("missing/invalid actor name");
			else if (actor.equals("s a"))
				System.out.println("'s a' is an invalid actor name");
		} catch (Exception E) {
			System.out.println("failed to add cast: " + E); // dont add movie that doesnt fit format
		}
		return c;
	}

	private String getTextValue(Element ele, String tagName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if (nl != null && nl.getLength() > 0) {
			Element el = (Element) nl.item(0);
			textVal = el.getFirstChild().getNodeValue();
		}

		return textVal;
	}

	private int getIntValue(Element ele, String tagName) {
		// in production application you would catch the exception
		return Integer.parseInt(getTextValue(ele, tagName));
	}

	public static void main(String[] args) {
		// create an instance
		CastParserOld cp = new CastParserOld();

		// call run example
		cp.fillList();
		
		cp.insertSql();
	}
}
