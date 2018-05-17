/**
 * This User class only has the username field in this example.
 * <p>
 * However, in the real project, this User class can contain many more things,
 * for example, the user's shopping cart items.
 */
public class User {

    private final String username;
    boolean isEmployee;

    public User(String username) {
        this.username = username;
        isEmployee = false;
        
    }
    public User(String username, boolean isEmployee) {
        this.username = username;
        this.isEmployee = isEmployee;

    }

    public String getUsername() {
        return this.username;
    }
    public boolean getEmployee() {
    	return isEmployee;
    }
}
