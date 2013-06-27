package de.quiz.UserManager;

import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import de.fhwgt.quiz.application.Player;
import de.fhwgt.quiz.error.QuizError;
import de.quiz.ServiceManager.IService;
import de.quiz.User.IUser;

/**
 * This service handles the users. It should get registered with the
 * ServiceManager by default!
 * 
 * @author Patrick Na§
 */
public interface IUserManager extends IService {

	/**
	 * Creates IUser object and adds player to actual gameplay
	 * 
	 * @param name
	 *            the user's name
	 * @param session
	 *            the user's session
	 * @param e
	 *            QuizError exception
	 * @throws Exception
	 */
	public IUser loginUser(String name, HttpSession session, QuizError error)
			throws Exception;

	/**
	 * Logout user. (invalidates his session)
	 * 
	 * @param session
	 *            the user's session
	 */
	public void logoutUser(HttpSession session) throws Exception;

	/**
	 * Get the activeUser list.
	 * 
	 * @return CopyOnWriteArrayList with all active Users
	 */
	public CopyOnWriteArrayList<IUser> getUserList();

	/**
	 * Get a user by his ID.
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
	 * Set's the session for a user.
	 * 
	 * @param user
	 *            the user we want to add the session
	 * @param session
	 *            the session we want to add
	 * @throws Exception
	 */
	public void setSessionForUser(IUser user, HttpSession session)
			throws Exception;

	/**
	 * Returns a JSON with the playerlist.
	 * 
	 * @return JSONObject Playerlist or null at failure.
	 */
	public JSONObject getPlayerList();

	/**
	 * Delete single user from activeUser list and quiz logic.
	 * 
	 * @param user
	 *            the user we want to add the session
	 */
	public void removeActiveUser(IUser user);

	/**
	 * Delete all users from activeUser list and quiz logic (function for game
	 * reset).
	 * 
	 */
	public void removeAllActiveUser();

	/**
	 * Returns the rankId for the requested Player.
	 * 
	 * @param curPlayer
	 *            the player whose ranking we want
	 * @return rankId
	 */
	public int getRankingForPlayer(Player curPlayer);

	/**
	 * Returns the user with given websocket id.
	 * 
	 * @param id
	 * @return IUser
	 */
	public IUser getUserByWSID(int id);

}
