package de.quiz.Servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.servlet.AsyncContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
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
 * Servlet implementation class PlayerServlet
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
		if (request.getParameter("rID") != null) {
			sc = request.getParameter("rID");
		}
		// login request
		if (sc.equals("1")) {
			response.setContentType("application/json");
			HttpSession session = request.getSession(true);
			PrintWriter out = response.getWriter();
			IUser tmpUser;

			try {

				// create user
				tmpUser = ServiceManager.getInstance()
						.getService(IUserManager.class)
						.loginUser(request.getParameter("name"), session);

				// create answer
				JSONObject obj = new JSONObject();

				obj.put("id", 2);
				obj.put("userID", tmpUser.getUserID());

				// send answer
				out.print(obj);

				ServiceManager
						.getInstance()
						.getService(ILoggingManager.class)
						.log("Successfully logged in User with ID: "
								+ tmpUser.getUserID() + " and name: "
								+ tmpUser.getName());
			} catch (Exception e) {
				e.printStackTrace();
				// create answer
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

				ServiceManager.getInstance().getService(ILoggingManager.class)
						.log("User login failed!");
			}
		}
		// playerlist
		if (sc.equals("6")) {
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();

			try {

			} catch (Exception e) {

				// create answer
				JSONObject error = new JSONObject();

				try {
					error.put("id", 255);
					error.put("message",
							"Fehler beim Versenden der Spielerliste.");
				} catch (JSONException e1) {
					ServiceManager.getInstance()
							.getService(ILoggingManager.class)
							.log("Failed sending playerlist error!");
				}

				// send answer
				out.print(error);

				ServiceManager.getInstance().getService(ILoggingManager.class)
						.log("Failed sending Playerlist!");
			}
		}

		// start game
		if (sc.equals("7")) {
			SSEServlet.broadcast(7);
			response.setContentType("application/json");

			PrintWriter out = response.getWriter();
			QuizError error = new QuizError();
			Quiz.getInstance().startGame(
					ServiceManager.getInstance().getService(IUserManager.class)
							.getUserBySession(request.getSession())
							.getPlayerObject(), error);
			if (error.isSet()) {

				// create answer
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

				ServiceManager.getInstance().getService(ILoggingManager.class)
						.log("Failed starting game!");
			} else {

				// create answer
				JSONObject answer = new JSONObject();

				try {
					answer.put("id", 200);
					answer.put("CatalogName", Quiz.getInstance()
							.getCurrentCatalog());
				} catch (JSONException e) {

					// create answer
					JSONObject errorA = new JSONObject();

					try {
						errorA.put("id", 255);
						errorA.put("message",
								"Fehler beim Senden der Start Game message.");
					} catch (JSONException e1) {
						ServiceManager.getInstance()
								.getService(ILoggingManager.class)
								.log("Failed sending start game succeed!");
					}

					// send answer
					out.print(error);
				}

				// send answer
				out.print(answer);

				ServiceManager.getInstance().getService(ILoggingManager.class)
						.log("Started game!");
			}
		}
	}
}
