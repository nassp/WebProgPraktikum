package de.quiz.Servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import de.fhwgt.quiz.application.Quiz;
import de.fhwgt.quiz.error.QuizError;
import de.quiz.LoggingManager.ILoggingManager;
import de.quiz.ServiceManager.IService;
import de.quiz.ServiceManager.ServiceManager;
import de.quiz.User.IUser;
import de.quiz.User.User;
import de.quiz.UserManager.IUserManager;

/**
 * Servlet implementation class PlayerServlet
 */
@WebServlet(description = "handles everything which has to do with players", urlPatterns = { "/SSEServlet" })
public class SSEServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static CopyOnWriteArrayList<IUser> userList = new CopyOnWriteArrayList<IUser>();
	// private static CopyOnWriteArrayList<AsyncContext> asyncArr = new
	// CopyOnWriteArrayList<AsyncContext>();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SSEServlet() {
		super();
	}

	public synchronized void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		//bradcast
		String userID = req.getParameter("rID");
		if(userID!= null){
			addUser(userID,req,res);
			res.setContentType("application/json");
			res.getWriter().print("Event Stream wurde gestartet.");
			System.out.println("playerID ="+req.getParameter("rID"));
		}else {
			for (IUser user : userList) {
				AsyncContext ctx;
				if(user.getAsyncCo()==null){
					ctx = user.getRequest().startAsync();
					ctx.setTimeout(200000000);
					user.setAsyncCo(ctx);
				}else{
					ctx = user.getAsyncCo();
				}
				System.out.println("!Send Broadcast to "+user.getRequest()+"!");
				sendMsg(user,6);
			}
		}

		
	}
	public synchronized void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
	}

	public synchronized static void sendMsg(final IUser user , int msg)
			throws ServletException, IOException {
		System.out.println("bla:"+user.getRequest());
		final AsyncContext ctx = user.getAsyncCo();

		

		// attach listener to respond to lifecycle events
		ctx.addListener(new AsyncListener() {
			public void onComplete(AsyncEvent event) throws IOException {
				System.out.println("onComplete called");
			}

			public void onTimeout(AsyncEvent event) throws IOException {
				System.out.println("onTimeout called");
				//userList.remove(user);
				
			}

			public void onError(AsyncEvent event) throws IOException {
				System.out.println("onError called");
				//userList.remove(user);
			}

			public void onStartAsync(AsyncEvent event) throws IOException {
				System.out.println("onStartAsync called");
			}
		});

		// spawn task in a background thread
		ClientThread clientThread = new ClientThread(ctx,msg);
		ctx.start(clientThread);

	}
	public synchronized static boolean addUser(String userID, HttpServletRequest request, HttpServletResponse response) {
		for (IUser user : userList) {
			/*if(user.getUserID().equals(userID)&& user.getRequest() != request){
				System.out.println("User "+user.getRequest()+" removed");
			}*/
			if (user.getRequest().equals(request)) {
				return false; 
			}
		}
		IUser tmpUser = new User(userID);
		tmpUser.setRequest(request);
		tmpUser.setResponse(response);
		System.out.println("Client wird hinzugefügt");
		userList.add(tmpUser);
		return true;
	}
	public synchronized static boolean broadcast(int msg) throws ServletException, IOException {
		System.out.println("Start broadcast");
		for (IUser user : userList) {
			sendMsg(user,msg);
		}
		return true;
	}

}
