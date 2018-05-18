package movieHopClasses;

import java.util.Vector;

public class Movie {
	private String id;
	private String title;
	private int year;
	private String director;
	private String directorId;
	private Vector<String> categories;
	
	public Movie() {categories = new Vector<String>();}
	
	public Movie (String id, String title, int year, String director){
		this.id = id;
		this.title = title;
		this.year = year;
		this.director = director;
		categories = new Vector<String>();
	}
	
	public Movie (String id, String title, int year, String director, String directorId){
		this.id = id;
		this.title = title;
		this.year = year;
		this.director = director;
		this.directorId = directorId;
		categories = new Vector<String>();
	}
	
	public void setId(String id)
	{
		this.id = id;
	}
	
	public void setTitle(String title)
	{
		this.title = title;
	}
	
	public void setYear(int year)
	{
		this.year = year;
	}
	
	public void setDirector(String director)
	{
		this.director = director;
	}
	
	public void setDirectorId(String directorId)
	{
		this.directorId = directorId;
	}
	
	public void addCategory(String category)
	{
		categories.add(category);
	}
	
	public String getId()
	{
		return id;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public int getYear()
	{
		return year;
	}
	
	public String getDirector()
	{
		return director;
	}
	
	public String getDirectorId()
	{
		return directorId;
	}
	
	public Vector<String> getCategories()
	{
		return categories;
	}
	
	public String toString()
	{
		StringBuffer s = new StringBuffer();
		s.append(" ID: " + getId());
		s.append(" Title: " + getTitle());
		s.append(" Year: " + getYear());
		s.append(" Director: " + getDirector());
		//s.append(" DirectorId: " + getDirectorId());
		if (categories != null && categories.size() > 0)
			for (int i = 0; i < categories.size(); i++)
				s.append(" c: " + categories.get(i));
		return s.toString();
	}

}
