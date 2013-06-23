package de.quiz.UserManager;

import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import de.fhwgt.quiz.application.Player;
import de.fhwgt.quiz.error.QuizError;
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
     * @return IUser The user with the requested session
     */
    public IUser loginUser(String name, HttpSession session, QuizError error) throws Exception;
    
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
     * @return IUser The user with the requested id
     */
    public IUser getUserById(String id) throws Exception;

    /**
     * Returns a player object if found by session.
     * 
     * @param session
     *            the requested user's session return the user object or null
     * @return IUser The user with the requested session
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
     *            the user we want to add the session
	 */
	public void removeActiveUser(IUser user);
	
	/**
	 * deletes all user from activeUser list and invalidates their sessions (function for game reset)
	 * 
	 */
	public void removeAllActiveUser();
	
	/**
	 * Returns the rankId for the requested Player
	 * 
	 * @param curPlayer
     *            the player whose ranking we want
	 * @return rankId 
	 */
	public int getRankingForPlayer(Player curPlayer);
	
	/**
	 * Returns the user with given websocket id
	 * 
	 * @param id
	 * @return IUser
	 */
	public IUser getUserByWSID(int id);

}
