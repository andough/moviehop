

public class Actor {
	private String id;
	private String name;
	private Integer dob;
	
	public Actor() {}
	
	public Actor(String id, String name, Integer dob)
	{
		this.id = id;
		this.name = name;
		this.dob = dob;
	}
	
	public void setId(String id)
	{
		this.id = id;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public void setDob(Integer dob)
	{
		this.dob = dob;
	}
	
	public String getId()
	{
		return id;
	}
	
	public String getName()
	{
		return name;
	}
	
	public Integer getDob()
	{
		return dob;
	}
	
	public String toString()
	{
		StringBuffer s = new StringBuffer();
		s.append(" id: " + id);
		s.append(" name: " + name);
		s.append(" dob: " + dob);
		return s.toString();
	}

}
