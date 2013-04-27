package de.quiz.UserManager;

import java.util.ArrayList;

import javax.servlet.http.HttpSession;

import de.fhwgt.quiz.application.Player;
import de.fhwgt.quiz.application.Quiz;
import de.fhwgt.quiz.error.QuizError;
import de.quiz.LoggingManager.ILoggingManager;
import de.quiz.ServiceManager.ServiceManager;
import de.quiz.User.IUser;
import de.quiz.User.User;




/**
 * this service handles the users. It should get registered with the
 * ServiceManager by default!
 * 
 * @author Patrick Na�
 */
public class UserManager implements IUserManager {

    private ArrayList<IUser> activeUser = new ArrayList<IUser>();

    public UserManager() {
    }

    /**
     * delete single user from activeUser list and invalidates its session
     * 
     * @param user
     */
    private void removeActiveUser(IUser user) {

	for (int i = 0; i < activeUser.size(); i++) {

	    if (activeUser.get(i).getUserID().equals(user.getUserID())) {

		// remove from list is sufficient
		activeUser.remove(user);
	    }
	}
    }



    /**
     * check user data and set session ID
     * 
     * @param id
     *            the user's ID
     * @param session
     *            the user's session
     * @throws Exception
     */
    public long loginUser(String name, HttpSession session) throws Exception {

    	// check if session in use -> delete old user entry
    	if (isUserSessionValid(session) != null) {
			ServiceManager.getInstance().getService(ILoggingManager.class).log("User has had an existing session while tried to login");
	    	removeActiveUser(isUserSessionValid(session));
		}
    	//TODO
		QuizError error = new QuizError();
		Player activePlayer = Quiz.getInstance().createPlayer(name, error);

		if (error.isSet()) {
			ServiceManager.getInstance().getService(ILoggingManager.class)
					.log(this, error);
			return -1;
			
		} else {
			ServiceManager.getInstance().getService(ILoggingManager.class)
					.log(this, "Successfully logged in user" + name);
			IUser tmpUser = new User(activePlayer.getId().toString(), activePlayer.getName(), session);
			// add to list
			activeUser.add(tmpUser);
			return activePlayer.getId();
		}

		
	    // store session
	    //this.setSessionForUser(tmpUser, session);


    }



    /**
     * returns an active user by it's ID
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
     * check if the users session timed out!
     * 
     * @param session
     *            the requested user's session return the user object or null
     */
    public IUser isUserSessionValid(HttpSession session) {

	IUser tmpUser = null;
	for (IUser item : activeUser) {
	    
	    if (item.getSession().equals(session)) {
		tmpUser = item;
		try{
		    tmpUser.getSession().getLastAccessedTime();
		}catch (IllegalStateException e){
		    ServiceManager.getInstance().getService(ILoggingManager.class).log(this, e);
		    activeUser.remove(tmpUser);
		}
		return tmpUser;
	    }
	}
	//ServiceManager.getInstance().getService(ILoggingManager.class).log("No session found by getUserBySession Method");
	return null;
    }
    

    
    /**
     * set's the session for a user
     * 
     * @param user
     *            the user we want to add the session
     * @param session
     *            the session we want to add
     * @throws Exception
     */
    @Override
    public void setSessionForUser(IUser user, HttpSession session) throws Exception {

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
     * logout user
     * 
     * @param session
     *            the user's session
     */
    @Override
    public void logoutUser(HttpSession session) throws Exception {
	
	for(IUser user : activeUser){
	    if(user.getSession().equals(session)){
		user.getSession().invalidate();
		activeUser.remove(user);
		ServiceManager.getInstance().getService(ILoggingManager.class).log(this, "successfully logged out user");
		return;
	    }
	}
	throw new Exception("unable to logout user");
	
    }

}