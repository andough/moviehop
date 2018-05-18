


import java.util.Vector;
import java.util.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.mysql.jdbc.PreparedStatement;

public class MovieParser {
	private Vector<Movie> movieList;
	Document d;
	private HashMap<String,Integer> my_categories;
	
	public MovieParser()
	{
		movieList = new Vector<Movie>();
		my_categories = new HashMap<String, Integer>();
	}
	
	public void insertSql() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// create database connection
			Connection dbCon = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb", "root", "password");

			// Declare a new statement
			 Statement statement = dbCon.createStatement();

			PreparedStatement psInsertRecord = null;
			String sqlInsertRecord = null;

			dbCon.setAutoCommit(true);
			ResultSet rs;

			//Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb", "root", "password");
			//Statement statement = conn.createStatement();
			//sqlInsertRecord = "INSERT into movies(id,title,year,director) VALUES(?,?,?,?)";

			//add movies
			for (int i = 0; i < movieList.size(); i++) {
				String query = String.format("Select * from movies where id = '%s';", movieList.get(i).getId().replace("'", ""));
				rs = statement.executeQuery(query);
				if (rs.next())
					System.out.println("Failed to add, duplicate id: "+ movieList.get(i).toString());
				else
				{
					String updateStatement = "INSERT into movies(id,title,year,director) " + 
						"VALUES(?,?,?,?);";
					psInsertRecord = (PreparedStatement) dbCon.prepareStatement(updateStatement.replace("'", ""));
					psInsertRecord.setString(1, movieList.get(i).getId().replace("'", ""));
					psInsertRecord.setString(2, movieList.get(i).getTitle().replace("'", ""));
					psInsertRecord.setInt(3, movieList.get(i).getYear());
					psInsertRecord.setString(4, movieList.get(i).getDirector().replace("'", ""));
					psInsertRecord.executeUpdate();
					//psInsertRecord.addBatch();
					System.out.println("added movie: " + movieList.get(i).toString());
				}
			}

			String query = "Select * from genres;";
			rs = statement.executeQuery(query);
			while (rs.next())
			{
				my_categories.put(rs.getString("name"),rs.getInt("id"));
			}
			
			query = "Select id from genres order by id desc limit 1;";
			rs = statement.executeQuery(query);
			int c = 0;
			if (rs.next())
				c = rs.getInt("id");
			
			//check if genre in genres, add if not
			//add genres_in_movies
			for (int i = 0; i < movieList.size(); i++) {
				if (movieList.get(i).getCategories() != null)
					for (int x = 0; x < movieList.get(i).getCategories().size(); x++)
					{
						String cat = movieList.get(i).getCategories().get(x);
						if (my_categories.get(cat) == null)
						{
							String updateStatement = "INSERT into genres(name) VALUES(?);";
							psInsertRecord = (PreparedStatement) dbCon.prepareStatement(updateStatement);
							psInsertRecord.setString(1, cat);
							psInsertRecord.executeUpdate();
							c++;
							my_categories.put(cat,c);
							System.out.println("added genre to db: "  + cat);
						}
							
						String updateStatement = "Insert into genres_in_movies(genreId,movieId) VALUES(?,?);";
						psInsertRecord = (PreparedStatement) dbCon.prepareStatement(updateStatement);
						psInsertRecord.setInt(1,my_categories.get(cat));
						psInsertRecord.setString(2, movieList.get(i).getId());
						psInsertRecord.executeUpdate();
						System.out.println("added to genres_in_movies: " + cat + movieList.get(i).getId());
						//add to genres_in_movies
					}
			//	sqlInsertRecord = "INSERT into genres_in_movies(genreId,movieId) VALUES(?,?)";
			//	psInsertRecord = (PreparedStatement) dbCon.prepareStatement(sqlInsertRecord);
			//	Vector<String> V = movieList.get(i).getCategories();
			//	if (V != null) {
			//		for (int g = 0; g < V.size(); g++) {
			//			psInsertRecord.setString(1, V.get(g));
			//			psInsertRecord.setString(2, movieList.get(i).getId());
			//		}
			//		psInsertRecord.addBatch();
			//	}
			}
			
		//	if (psInsertRecord != null)
			//	psInsertRecord.executeBatch();
			//dbCon.commit();

			if (dbCon != null)
				dbCon.close();
			
			//if (conn != null)
			//	conn.close();
			
				

			if (psInsertRecord != null)
				psInsertRecord.close();
		} catch (Exception e) {
			System.out.println("error at insertSql: " + e);
		}
	}
	
	 public void fillList()
	 {
		 parseXml();
		 parseDocument();
		 printData();
	 }
	 
	 public void printData()
	 {
		 for (int i = 0; i <movieList.size(); i++)
		 {
			 System.out.println(movieList.get(i).toString());
		 }
	 }
	 
	 public void parseXml()
	 {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {

			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			// parse using builder to get DOM representation of the XML file
			d = db.parse("mains243.xml");

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
		NodeList nl = docEle.getElementsByTagName("film");
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {

				// get the film
				Element el = (Element) nl.item(i);
				
				// get the movie object
				Movie m = getMovie(el);
				if (m != null) {
					// get list of categories
					NodeList cats = el.getElementsByTagName("cats");
					if (cats != null && cats.getLength() > 0) {
						Element cat = (Element) cats.item(0);
						// fill movie with categories
						addCategory(cat, m);
					}
					// add it to list
					movieList.add(m);
				}
			}
		}
	}
	 
	 public void addCategory(Element cats, Movie e)
	 {
		NodeList cat = cats.getElementsByTagName("cat");
			for (int i = 0; i < cat.getLength(); i++) 
			{
				Element cate = (Element) cat.item(i);
				String category = cate.getTextContent();
				if (category != null)
					e.addCategory(category);
			}
	 }
	 
	public Movie getMovie(Element el) {
		Movie m = null;
		try{
			String id = getTextValue(el, "fid");
			String title = getTextValue(el, "t");
			Integer year = getIntValue(el, "year");
			String director = getTextValue(el, "dirn"); // first dirn is primary director from dtd

			//check for faulty data
			if (id != null && title != null && year != null && director != null)
				m = new Movie(id, title, year, director);
			else if (id == null)
				System.out.println("missing/invalid movie id");
			else if (title == null)
				System.out.println("missing/invalid movie title");
			else if (year == null)
				System.out.println("missing/invalid year");
			else if (director == null)
				System.out.println("missing/invalid director name");
		}
		catch (Exception E)
		{
			System.out.println("failed to add movie: " + E);	//dont add movie that doesnt fit format
		}
		return m;
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
        //in production application you would catch the exception
        return Integer.parseInt(getTextValue(ele, tagName));
    }
    
    public static void main(String[] args) {
        //create an instance
        MovieParser mp = new MovieParser();

        //call run example
        mp.fillList();
        mp.insertSql();
    }
}
