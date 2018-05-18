

public class Cast {
	private String actor;
	private String movieId;
	public Cast()
	{
		
	}
	
	public Cast(String actor, String movieId)
	{
		this.actor = actor;
		this.movieId = movieId;
	}
	
	public void setActor(String actor)
	{
		this.actor = actor;
	}
	
	public void setMovie(String MovieId)
	{
		this.movieId = movieId;
	}
	
	public String getActor()
	{
		return actor;
	}
	
	public String getMovie()
	{
		return movieId;
	}
	
	public String toString()
	{
		StringBuffer s = new StringBuffer();
		s.append(" Actor: " + actor);
		s.append(" Movie: " + movieId);
		return s.toString();
	}
	
}
