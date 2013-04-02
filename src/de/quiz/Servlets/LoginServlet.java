package de.quiz.Servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.fhwgt.quiz.application.Player;
import de.fhwgt.quiz.application.Quiz;
import de.fhwgt.quiz.error.QuizError;
import de.quiz.LoggingManager.ILoggingManager;
import de.quiz.ServiceManager.ServiceManager;

/**
 * Servlet implementation class LoginServlet
 * this servlet handels the login process and the integration of the game logic
 * 
 * @author Patrick Na§
 */
@WebServlet(description = "handles the login and integrates the game logic", urlPatterns = { "/LoginServlet" })
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	
	/**
	 * Player Login method
	 * 
	 * @param name			username
	 * @return boolean		true for success, false for failure
	 */
	protected boolean login(String name) {
		
		QuizError error = new QuizError();
		Player ActivePlayer = Quiz.getInstance().createPlayer(name, error);
		
		if(error.isSet()) {
			ServiceManager.getInstance().getService(ILoggingManager.class).log(this, error);
			return false;
		}else {
			ServiceManager.getInstance().getService(ILoggingManager.class).log(this, "Successfully logged in user" + name);
			return true;
		}
	}

}
