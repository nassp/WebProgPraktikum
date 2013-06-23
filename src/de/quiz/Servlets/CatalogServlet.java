package de.quiz.Servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;

import de.fhwgt.quiz.application.Catalog;
import de.fhwgt.quiz.application.Player;
import de.fhwgt.quiz.application.Question;
import de.fhwgt.quiz.application.Quiz;
import de.fhwgt.quiz.error.QuizError;
import de.fhwgt.quiz.loader.LoaderException;
import de.quiz.LoggingManager.ILoggingManager;
import de.quiz.ServiceManager.ServiceManager;
import de.quiz.UserManager.IUserManager;
import de.quiz.Utility.TimeOut;

/**
 * Servlet implementation class CatalogServlet. This servlet handles catalogs.
 * 
 * 
 * @author Patrick Naß
 */
@WebServlet(description = "managing of catalogs", urlPatterns = { "/CatalogServlet" })
public class CatalogServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CatalogServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		ServiceManager.getInstance().getService(ILoggingManager.class)
				.log(this, "GET is not supported on this Servlet");
		response.getWriter().print("GET is not supported on this Servlet");

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

		// catalog request
		if (sc.equals("3")) {
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			try {
				Map<String, Catalog> catalogList = Quiz.getInstance()
						.getCatalogList();
				JSONObject answer = new JSONObject(catalogList);
				answer.put("id", 4);
				out.print(answer);

			} catch (LoaderException e3) {

				JSONObject error = new JSONObject();
				try {
					error.put("id", 255);
					error.put("message",
							"Fehler beim Versenden des catalogue request");
					ServiceManager.getInstance()
							.getService(ILoggingManager.class)
							.log("Failed sending catalog request!");

				} catch (JSONException e1) {

					try {
						error.put("id", 255);
						error.put("message",
								"Fehler beim Versenden der catalogue request Fehler-Nachricht.");
					} catch (JSONException e2) {
						ServiceManager.getInstance()
								.getService(ILoggingManager.class)
								.log("Failed sending catalog request error!");
					}
					out.print(error);
				}
			} catch (JSONException e) {

				JSONObject error = new JSONObject();
				try {
					error.put("id", 255);
					error.put("message",
							"Fehler beim Versenden des catalogue request");
					ServiceManager.getInstance()
							.getService(ILoggingManager.class)
							.log("Failed sending catalog request!");

				} catch (JSONException e1) {

					try {
						error.put("id", 255);
						error.put("message",
								"Fehler beim Versenden der catalogue request Fehler-Nachricht.");
					} catch (JSONException e2) {
						ServiceManager.getInstance()
								.getService(ILoggingManager.class)
								.log("Failed sending catalog request error!");
					}
					out.print(error);
				}
			}

		}

		// TODO: muss über server sent events laufen und muss gefüllt werden
		// catalog change
		else if (sc.equals("5")) {
			HttpSession session = request.getSession(true);
			try {
				Player player = ServiceManager.getInstance()
						.getService(IUserManager.class)
						.getUserBySession(session).getPlayerObject();
				if (player != null) {
					System.out.println(player.isSuperuser());
				} else {
					System.out.println("Player ist null");
				}
				if(player.isSuperuser()){
					QuizError error = new QuizError();
					// error.set(QuizErrorType.NOT_SUPERUSER);
					Catalog cat = Quiz.getInstance().changeCatalog(player,
							request.getParameter("filename"), error);
					if (cat != null) {
						SSEServlet.broadcast(5);
					} else {
						System.out.println("catalog not changed");
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}

			// response.setContentType("application/json");
			// PrintWriter out = response.getWriter();
			// JSONObject json = new JSONObject();
			// String s = "";
			// try {
			// s = request.getParameter("filename");
			// json.put("id", 5);
			// json.put("filename", s);
			// } catch (JSONException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// out.print(json);
		}

		else if (sc.equals("7")) {
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			JSONObject json = new JSONObject();
			try {
				json.put("id", 7);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			out.print(json);
		}

	}

}
