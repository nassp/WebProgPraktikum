package de.quiz.User;



import javax.servlet.http.HttpSession;

/**
 * this class saves the userdata at register as preparation for the DBM to write
 * it into the database.
 * 
 * @author Patrick Na§
 */
public class User implements IUser {

	private HttpSession session;
	private String userID;
	private String name;
	//TODO: WebsocketID fehlt noch


	/**
	 * default constructor of UserConfig object
	 * 
	 */
	public User() {
	}

	/**
	 * constructor of UserConfig object
	 * 
	 */
	public User(String id) {

		this.name = id;
	}

	@Override
	public void setID(String id) {

		this.userID = id;
	}

	/**
	 * constructor of UserConfig object
	 * 
	 */
	public User(String id, String name, HttpSession session) {

		this.userID = id;
		this.session = session;
	}

	/*
	 * ################################## setter
	 * ##################################
	 */

	/**
	 * set user session
	 * 
	 * @param session
	 *            the user's session
	 */
	public void setSession(HttpSession session) {

		this.session = session;
	}

	/**
	 * set the name
	 * 
	 * @param _name
	 *            the user's name
	 */
	public void setName(String _name) {
		name = _name;
	}

	/*
	 * ################################## getter
	 * ##################################
	 */

	/**
	 * getter for session
	 */
	public HttpSession getSession() {
		return session;
	}

	/**
	 * getter for the user ID
	 * 
	 */
	public String getUserID() {
		return userID;
	}

	/**
	 * getter for the user's firstname
	 * 
	 */
	public String getName() {
		return name;
	}

	// methods

	/**
	 * checks if all fields are equal in order to check User objects for
	 * equality
	 */
	public boolean equals(Object object) {
		IUser user = (IUser) object;

		if (this.name.equals(user.getName()) &&

		this.userID.equals(user.getUserID())) {
			return true;
		}
		return false;
	}

	/**
	 * checks if two user objects have the same ID
	 */
	public boolean hasSameID(Object object) {

		IUser user = (IUser) object;

		if (this.userID.equals(user.getUserID())) {
			return true;
		}
		return false;
	}
}
