package de.quiz.UserManager;

import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;

import de.fhwgt.quiz.application.Player;
import de.fhwgt.quiz.application.Quiz;
import de.fhwgt.quiz.error.QuizError;
import de.fhwgt.quiz.error.QuizErrorType;
import de.quiz.LoggingManager.ILoggingManager;
import de.quiz.ServiceManager.ServiceManager;
import de.quiz.Servlets.SSEServlet;
import de.quiz.User.IUser;
import de.quiz.User.User;

/**
 * This service handles the users. It should get registered with the
 * ServiceManager by default!
 * 
 * @author Patrick Na§
 */
public class UserManager implements IUserManager {

	private CopyOnWriteArrayList<IUser> activeUser = new CopyOnWriteArrayList<IUser>();

	public UserManager() {
	}

	/**
	 * Delete single user from activeUser list and quiz logic.
	 * 
	 * @param user
	 *            user which should be deleted
	 */
	public void removeActiveUser(IUser user) {
		QuizError error = new QuizError();
		if (user != null) {
			Quiz.getInstance().removePlayer(user.getPlayerObject(), error);
			if (!error.isSet()) {

				ServiceManager
						.getInstance()
						.getService(ILoggingManager.class)
						.log(user.getName()
								+ " removed because of session timeout!");
				activeUser.remove(user);
				SSEServlet.broadcast(6);

			} else {
				ServiceManager.getInstance().getService(ILoggingManager.class)
						.log(this, error);
				// if superuser left error is also set
				if (error.getStatus() == 7) {
					activeUser.remove(user);
					SSEServlet.broadcast(6);
				}
			}
		}

	}

	/**
	 * Delete all users from activeUser list and quiz logic (function for game
	 * reset).
	 * 
	 * 
	 */
	public void removeAllActiveUser() {
		for (int i = 0; i < activeUser.size(); i++) {
			removeActiveUser(activeUser.get(i));
		}
		activeUser.clear();
		ServiceManager.getInstance().getService(ILoggingManager.class)
				.log("Userliste wurde gelöscht");
	}

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
	public IUser loginUser(String name, HttpSession session, QuizError e)
			throws Exception {

		// check if session in use
		if (getUserBySession(session) != null) {
			ServiceManager.getInstance().getService(ILoggingManager.class)
					.log("User has had an existing session");
			return getUserBySession(session);

		} else {

			QuizError error = new QuizError();
			Player newPlayer = Quiz.getInstance().createPlayer(name, error);

			if (error.isSet()) {
				ServiceManager.getInstance().getService(ILoggingManager.class)
						.log(this, error);
				if (error.getDescription().equals("Username taken")) {
					e.set(QuizErrorType.USERNAME_TAKEN);
				}
				return null;

			} else {

				IUser tmpUser = new User(newPlayer.getId().toString(),
						newPlayer.getName(), session, newPlayer);
				tmpUser.setWSID(-1);
				// add to list
				activeUser.add(tmpUser);
				// ServiceManager.getInstance().getService(ILoggingManager.class)
				// .log(this, "Successfully logged in user" + name);
				return tmpUser;
			}
		}

	}

	/**
	 * Get the activeUser list.
	 * 
	 * @return CopyOnWriteArrayList with all active Users
	 */
	public CopyOnWriteArrayList<IUser> getUserList() {
		return activeUser;
	}

	/**
	 * Returns an active user by it's ID.
	 * 
	 * @param id
	 *            the requested user's ID return the user object or null
	 * @throws Exception
	 */
	public IUser getUserById(String id) throws Exception {

		// find user
		for (int i = 0; i < activeUser.size(); i++) {

			if (activeUser.get(i).getUserID().equals(id)) {

				return activeUser.get(i);
			}
		}
		throw new Exception("UserByID not found...");
	}

	/**
	 * Returns a player object if found by session.
	 * 
	 * @param session
	 *            the requested user's session return the user object or null
	 */
	public IUser getUserBySession(HttpSession session) {

		for (IUser item : activeUser) {

			if (item.getSession().equals(session)) {
				return item;

			}
		}
		// ServiceManager.getInstance().getService(ILoggingManager.class).log("No session found by getUserBySession Method");
		return null;
	}

	/**
	 * Set's the session for a user.
	 * 
	 * @param user
	 *            the user we want to add the session
	 * @param session
	 *            the session we want to add
	 * @throws Exception
	 */
	@Override
	public void setSessionForUser(IUser user, HttpSession session)
			throws Exception {

		// find user and set session
		for (int i = 0; i < activeUser.size(); i++) {

			if (activeUser.get(i).hasSameID(user)) {

				activeUser.get(i).setSession(session);
				return;
			}
		}
		throw new Exception("can't set session! User not found...");
	}

	/**
	 * Logout user. (invalidates his session)
	 * 
	 * @param session
	 *            the user's session
	 */
	@Override
	public void logoutUser(HttpSession session) throws Exception {
		if (session != null) {
			session.invalidate();

			ServiceManager
					.getInstance()
					.getService(ILoggingManager.class)
					.log(this,
							"successfully logged out user"
									+ getUserBySession(session)
											.getPlayerObject().getName());
			return;
		}
		throw new Exception("Unable to logout user!");

	}

	/**
	 * Returns a JSON with the playerlist. 
	 * 
	 * @return JSONObject Playerlist or null at failure.
	 */
	public JSONObject getPlayerList() {
		JSONObject tmpJSON = new JSONObject();
		
		for (IUser user : this.activeUser) {
			try {
				int rank = getRankingForPlayer(user.getPlayerObject());
				tmpJSON.put("name" + rank, user.getName());
				tmpJSON.put("score" + rank, user.getPlayerObject().getScore());
				tmpJSON.put("id" + rank, user.getPlayerObject().getId());
			} catch (JSONException e) {
				ServiceManager.getInstance().getService(ILoggingManager.class)
						.log("Failed to send Playerlist");
			}
			
		}
		return tmpJSON;
	}

	/**
	 * Returns the rankId for the requested Player.
	 * 
	 * @param curPlayer
	 * @return rankId
	 */
	public int getRankingForPlayer(Player curPlayer) {
		int i = 1;
		for (Player player : Quiz.getInstance().getPlayerList()) {
			if (player != curPlayer) {
				if (player.getScore() > curPlayer.getScore()) {
					i++;
				} else if (player.getScore() == curPlayer.getScore()
						&& player.getId() < curPlayer.getId()) {
					i++;
				}
			}
		}
		return i;
	}

	/**
	 * Returns the user with given websocket id.
	 * 
	 * @param id
	 * @return IUser
	 */
	public IUser getUserByWSID(int id) {

		for (IUser item : activeUser) {

			if (item.getWSID() == id) {
				return item;
			}

		}
		// ServiceManager.getInstance().getService(ILoggingManager.class).log("No user with given wsID found!");
		return null;
	}

}
