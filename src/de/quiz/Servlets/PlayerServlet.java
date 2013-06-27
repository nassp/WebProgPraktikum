package de.quiz.Servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;

import de.fhwgt.quiz.application.Quiz;
import de.fhwgt.quiz.error.QuizError;
import de.quiz.LoggingManager.ILoggingManager;
import de.quiz.ServiceManager.ServiceManager;
import de.quiz.User.IUser;
import de.quiz.UserManager.IUserManager;

/**
 * Servlet implementation class PlayerServlet. This Servlet handles everything
 * that has to do with players.
 * 
 * @author Patrick Na§
 */
@WebServlet(description = "handles everything which has to do with players", urlPatterns = { "/PlayerServlet" })
public class PlayerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PlayerServlet() {
		super();

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// get is not supported
		ServiceManager.getInstance().getService(ILoggingManager.class)
				.log("GET is not supported by this Servlet");
		response.getWriter().print("GET is not supported by this Servlet");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String sc = "";
		response.setContentType("application/json");

		// get JSON packet ID
		if (request.getParameter("rID") != null) {
			sc = request.getParameter("rID");
		}

		// login request
		if (sc.equals("1")) {
			HttpSession session = request.getSession(true);
			PrintWriter out = response.getWriter();
			IUser tmpUser;

			try {

				QuizError error = new QuizError();

				// create user
				tmpUser = ServiceManager
						.getInstance()
						.getService(IUserManager.class)
						.loginUser(request.getParameter("name"), session, error);
				// catches an error if set 
				if (error.isSet()) {
					// QuizErrorType: Username taken
					JSONObject errors = new JSONObject();

					// create answer (errorMessage)
					try {
						errors.put("id", 255);
						errors.put("message", error.getDescription());
					} catch (JSONException e1) {
						ServiceManager.getInstance()
								.getService(ILoggingManager.class)
								.log("Failed sending login error!");
					}

					// send answer
					out.print(errors);
					// log the login fail
					ServiceManager.getInstance()
							.getService(ILoggingManager.class)
							.log("User login failed!");
					return;

				} else {

					// create answer (loginResponse)
					JSONObject obj = new JSONObject();

					obj.put("id", 2);
					obj.put("userID", tmpUser.getUserID());

					// send answer
					out.print(obj);
					// log the user login
					ServiceManager
							.getInstance()
							.getService(ILoggingManager.class)
							.log("Successfully logged in User with ID: "
									+ tmpUser.getUserID() + " and name: "
									+ tmpUser.getName());
				}
			} catch (Exception e) {
				e.printStackTrace();
				// create answer (errorMessage)
				JSONObject error = new JSONObject();

				try {
					error.put("id", 255);
					error.put("message",
							"Fehler beim Einloggen. Das tut uns leid :(");
				} catch (JSONException e1) {
					ServiceManager.getInstance()
							.getService(ILoggingManager.class)
							.log("Failed sending login error!");
				}

				// send answer
				out.print(error);
				// log the login fail
				ServiceManager.getInstance().getService(ILoggingManager.class)
						.log("User login failed!");
			}
		}

		// start game
		if (sc.equals("7")) {

			PrintWriter out = response.getWriter();
			QuizError error = new QuizError();
			// try to start the game
			Quiz.getInstance().startGame(
					ServiceManager.getInstance().getService(IUserManager.class)
							.getUserBySession(request.getSession())
							.getPlayerObject(), error);
			// catches an error if set
			if (error.isSet()) {

				// create answer (errorMessage)
				JSONObject errorA = new JSONObject();

				try {
					errorA.put("id", 255);
					errorA.put("message",
							"Das Spiel konnte nicht gestartet werden.");
				} catch (JSONException e1) {
					ServiceManager.getInstance()
							.getService(ILoggingManager.class)
							.log("Failed sending start game error!");
				}

				// send answer
				out.print(error);
				// log failed gameStart
				ServiceManager.getInstance().getService(ILoggingManager.class)
						.log("Failed starting game!");
			} else {
				// broadcast gameStart with SSE
				SSEServlet.broadcast(7);
				// log successful gameStart
				ServiceManager.getInstance().getService(ILoggingManager.class)
						.log("Started game!");
			}
		}
	}
}
