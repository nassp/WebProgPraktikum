package de.quiz.Listener;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import de.quiz.ServiceManager.ServiceManager;
import de.quiz.User.IUser;
import de.quiz.UserManager.IUserManager;

/**
 * Application Lifecycle Listener implementation class SessionListener
 * 
 */
@WebListener
public class SessionListener implements HttpSessionListener {

	/**
	 * Default constructor.
	 */
	public SessionListener() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpSessionListener#sessionCreated(HttpSessionEvent)
	 */
	public void sessionCreated(HttpSessionEvent arg0) {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpSessionListener#sessionDestroyed(HttpSessionEvent)
	 */
	public void sessionDestroyed(HttpSessionEvent arg0) {
		IUser tmpUser = ServiceManager.getInstance()
				.getService(IUserManager.class)
				.getUserBySession(arg0.getSession());
		// session could be already destroyed
		if (tmpUser != null)
		ServiceManager.getInstance().getService(IUserManager.class)
				.removeActiveUser(tmpUser);
		// debug
		System.out.println("Session Destroyed");

	}

}
