package de.quiz.Servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import de.fhwgt.quiz.application.Player;
import de.fhwgt.quiz.application.Quiz;
import de.fhwgt.quiz.error.QuizError;
import de.quiz.LoggingManager.ILoggingManager;
import de.quiz.ServiceManager.ServiceManager;

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
		String sc ="";
		if(request.getParameter("rID")!=null){
			sc = request.getParameter("rID");		
		}
		
		if (sc.equals("1")) {
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			//JSONObject json = new JSONObject(this.getCatalogList());
			//out.print(json);
		}
	}

	/**
	 * Player Login method.
	 * 
	 * @param name
	 *            username
	 * @return long ID at success -1 at failure.
	 */
	protected long login(String name) {

		QuizError error = new QuizError();
		Player activePlayer = Quiz.getInstance().createPlayer(name, error);

		if (error.isSet()) {
			ServiceManager.getInstance().getService(ILoggingManager.class)
					.log(this, error);
			return -1;
		} else {
			ServiceManager.getInstance().getService(ILoggingManager.class)
					.log(this, "Successfully logged in user" + name);
			return activePlayer.getId();
		}
	}
}
