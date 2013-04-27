package de.quiz.Servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import de.fhwgt.quiz.application.Catalog;
import de.fhwgt.quiz.application.Player;
import de.fhwgt.quiz.application.Quiz;
import de.fhwgt.quiz.error.QuizError;
import de.fhwgt.quiz.loader.LoaderException;
import de.quiz.LoggingManager.ILoggingManager;
import de.quiz.ServiceManager.ServiceManager;
import de.quiz.UserManager.IUserManager;

/**
 * Servlet implementation class PlayerServlet
 */
@WebServlet(description = "handles everything which has to do with players", urlPatterns = { "/PlayerServlet" })
public class PlayerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private ArrayList<String> PlayerList;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PlayerServlet() {
        super();
        this.PlayerList = new ArrayList<String>();
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
			response.setContentType("text/plain");
			PrintWriter out = response.getWriter();
			HttpSession session = request.getSession(true);
			long userID;
			try {
				userID = ServiceManager.getInstance().getService(IUserManager.class).loginUser(request.getParameter("name"), session);
				//out.print(userID);
				this.PlayerList.add(request.getParameter("name"));
				ServiceManager.getInstance().getService(ILoggingManager.class).log("Successfully logged in User with ID: "+userID);;
			} catch (Exception e) {
				ServiceManager.getInstance().getService(ILoggingManager.class).log("User login failed!");
			}
			//Hier Playerlist senden
				response.setContentType("application/json");
				JSONObject json = new JSONObject(this.getPlayerList());
				out.print(json);
			//ServiceManager.getInstance().getService(ILoggingManager.class).log("Send Playerthings");
		}
	}
	protected Map<String, String> getPlayerList() {
		Map<String, String> playerMap = new HashMap<String, String>();
		int i = 0;
		for (String player : this.PlayerList) {
			playerMap.put("name"+i, player);
			i++;
		}
		return playerMap;
	}

}