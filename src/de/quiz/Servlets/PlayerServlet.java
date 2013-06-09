package de.quiz.Servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.servlet.AsyncContext;
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
	private static CopyOnWriteArrayList<AsyncContext> asyncArr = new CopyOnWriteArrayList<AsyncContext>();
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
				SSEServlet.addClientConnection(Integer.valueOf(tmpUser.getUserID()), request, response);
				//Spielerliste broadcasten
				//SSEServlet.broadcast(6);
				
				ServiceManager
						.getInstance()
						.getService(ILoggingManager.class)
						.log("Successfully logged in User with ID: "
								+ tmpUser.getUserID() + " and name: "
								+ tmpUser.getName());
			} catch (Exception e) {

				// create answer
				JSONObject error = new JSONObject();

				try {
					error.put("id", 255);
					error.put("message", "Fehler beim Einloggen. Das tut uns leid :(");
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
			

			//SSEServlet.broadcast(6);

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
					error.put("message", "Fehler beim Versenden der Spielerliste.");
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
		if (sc.equals("7")){ //&& request.getParameter("uID").equals("0")) {
			System.out.println("hallo!!!!!!!");
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
					errorA.put("message", "Das Spiel konnte nicht gestartet werden.");
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
						errorA.put("message", "Fehler beim Senden der Start Game message.");
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
	public static void broadcast(final int messageId){
		System.out.println("Start Broadcast: "+messageId);
		//final Asyn
		final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);
		//executorService.scheduleWithFixedDelay(new ServerSendEvent(asyncContext.getResponse()), 0, 2,TimeUnit.SECONDS)
		for (final AsyncContext ctx : asyncArr) {
			executorService.execute(new Runnable() {
				public void run() {
					try {
						if(ctx!= null){
							System.out.println("Send Broadcast to "+ctx);
							ctx.getResponse().setContentType("text/event-stream");
							ctx.getResponse().setCharacterEncoding("UTF-8");
							PrintWriter out = ctx.getResponse().getWriter();
							if(messageId==6){
								 JSONObject json = ServiceManager.getInstance().getService(IUserManager.class).getPlayerList();
								 int i = 0;
								 out.write("event: playerListEvent\n");
								 out.write("data: {\n");
								 out.write("data: \"id\": 6");
								 for(i=0;i<6;i++){
									 if(json.has("name"+i)){
										 out.write(",\n");
										 out.write("data: \"name"+i+"\": \""+json.get("name"+i)+"\"");
									 }
								 }
								 out.write("\n");
								 out.write("data: }\n\n");
								 out.flush();
							}else if(messageId ==7){
								//System.out.println(ServiceManager.getInstance().getService(Quiz.class).getPlayerList());
								//String catChanged = Quiz.getInstance().getCurrentCatalog().getName();
								out.write("event: gameStartEvent\n");
								out.write("data: {\n");
								out.write("data: \"id\": 7 \n");
								out.write("data: }\n\n");
								out.flush(); 
							}else if(messageId ==5){
								//System.out.println(ServiceManager.getInstance().getService(Quiz.class).getPlayerList());
								String catChanged = Quiz.getInstance().getCurrentCatalog().getName();
								out.write("event: catalogChangeEvent\n");
								out.write("data: {\n");
								out.write("data: \"id\": 5 ,\n");
								out.write("data: \"filename\": \""+catChanged+"\" \n");
								out.write("data: }\n\n");
								out.flush();
							}else if(messageId == 255){
								out.write("event: errorEvent\n");
								out.write("data: {\n");
								out.write("data: \"id\": 255 ,\n");
								out.write("data: \"msg\":\"Angefragtes SSE existiert nicht\"\n");
								out.write("data: }\n\n");
								out.flush();
							}
						}
					}catch (Exception e){
						e.printStackTrace();
						System.out.println("Broadcast fehlgeschlagen");
					}
				}

			});
		}
		
		executorService.shutdown();
	}

}
