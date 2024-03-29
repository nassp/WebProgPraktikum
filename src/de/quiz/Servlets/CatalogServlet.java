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
import de.fhwgt.quiz.application.Quiz;
import de.fhwgt.quiz.error.QuizError;
import de.quiz.LoggingManager.ILoggingManager;
import de.quiz.ServiceManager.ServiceManager;
import de.quiz.UserManager.IUserManager;

/**
 * Servlet implementation class CatalogServlet. This servlet handles catalogs.
 * 
 * 
 * @author Patrick Na�
 */
@WebServlet(description = "manages catalogs", urlPatterns = { "/CatalogServlet" })
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
		// get is not supported
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
		
		// get JSON packet ID
		if (request.getParameter("rID") != null) {
			sc = request.getParameter("rID");
		}

		// catalog request
		if (sc.equals("3")) {
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			try {
				// get all catalogs
				Map<String, Catalog> catalogList = Quiz.getInstance()
						.getCatalogList();
				// create answer (catalogResponse)
				JSONObject answer = new JSONObject();
				answer.put("id", 4);
				int catCounter = 1;
				// adds a new catalog with the name and number of questions for each entry
				for (Map.Entry<String, Catalog> e : catalogList.entrySet()) {
					JSONObject catalog = new JSONObject();
					catalog.put("name", e.getKey());
					catalog.put("questions", e.getValue().getQuestions().size());
					answer.put("cat" + catCounter, catalog);
					catCounter++;
				}
				// send answer
				out.print(answer);

			} catch (Exception e3) {
				// inform client about error
				JSONObject error = new JSONObject();
				try {
					// create answer (errorMessage)
					error.put("id", 255);
					error.put("message",
							"Fehler beim Versenden des catalogue request");

					ServiceManager.getInstance()
							.getService(ILoggingManager.class)
							.log("Failed sending catalog request!");
					// send answer
					out.print(error);
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

		}

		// catalog change
		else if (sc.equals("5")) {
			HttpSession session = request.getSession(true);
			try {
				Player player = ServiceManager.getInstance()
						.getService(IUserManager.class)
						.getUserBySession(session).getPlayerObject();
				if (player.isSuperuser()) {
					QuizError error = new QuizError();
					Catalog cat = Quiz.getInstance().changeCatalog(player,
							request.getParameter("filename"), error);
					if (cat != null) {
						// broadcast catalogChange
						SSEServlet.broadcast(5);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

}
