package de.quiz.UserManager;

import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import de.quiz.ServiceManager.IService;
import de.quiz.User.IUser;



/**
 * this service handles the users. It should get registered with the
 * ServiceManager by default!
 * 
 * @author Patrick Na§
 */
public interface IUserManager extends IService {

    /**
     * login user
     * 
     * @param id
     *            the user's ID
     * @param session
     *            the user's session
     */
    public IUser loginUser(String name, HttpSession session) throws Exception;
    
    /**
     * logout user
     * 
     * @param session
     *            the user's session
     */
    public void logoutUser(HttpSession session) throws Exception;

    /**
     * get a user by his ID
     * 
     * @param id
     *            the requested user's ID return the user object or null
     */
    public IUser getUserById(String id) throws Exception;

    /**
     * check if the users session timed out!
     * 
     * @param session
     *            the requested user's session return the user object or null
     */
    public IUser getUserBySession(HttpSession session);


    /**
     * set's the session for a user
     * 
     * @param user
     *            the user we want to add the session
     * @param session
     *            the session we want to add
     * @throws Exception
     */
    public void setSessionForUser(IUser user, HttpSession session) throws Exception;

	/**
	 * Returns a JSON with the playerlist. THIS METHOD HAS TO BE CALLED FROM THE SERVER SEND EVENTS!!!
	 * 
	 * @return JSONObject Playerlist or null at failure.
	 */
	public JSONObject getPlayerList();
	
	/**
	 * delete single user from activeUser list and invalidates its session
	 * 
	 * @param user
	 */
	public void removeActiveUser(IUser user);

}
