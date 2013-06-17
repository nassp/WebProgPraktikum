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
	private static CopyOnWriteArrayList<IUser> userArr = new CopyOnWriteArrayList<IUser>();
	private static CopyOnWriteArrayList<AsyncContext> asyncArr = new CopyOnWriteArrayList<AsyncContext>();

	// private static CopyOnWriteArrayList<AsyncContext> asyncArr = new
	// CopyOnWriteArrayList<AsyncContext>();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SSEServlet() {
		super();
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		//bradcast
		
		addIUser(req,res);
		System.out.println("Start broadcast");
		for (IUser user : userArr) {
			sendMsg(user,6);
		}
	}

	public static void sendMsg(final IUser user , int msg)
			throws ServletException, IOException {
		// create the async context, otherwise getAsyncContext() will be
		// null
		HttpServletRequest req = user.getRequest();
		HttpServletResponse res = user.getResponse();
		PrintWriter out = res.getWriter();
		System.out.println("bla: "+out);
		System.out.println("response status: "+res.getStatus());
		final AsyncContext ctx;
		if(req.getAsyncContext()==null){
			ctx = req.startAsync();
			ctx.setTimeout(0);
		}else{
			ctx = req.getAsyncContext();
		}
		
			

		// attach listener to respond to lifecycle events of this
		// AsyncContext
		ctx.addListener(new AsyncListener() {
			public void onComplete(AsyncEvent event) throws IOException {
				System.out.println("onComplete called");
			}

			public void onTimeout(AsyncEvent event) throws IOException {
				System.out.println("onTimeout called");
				//ctx.complete();
				userArr.remove(user);
				
			}

			public void onError(AsyncEvent event) throws IOException {
				System.out.println("onError called");
			}

			public void onStartAsync(AsyncEvent event) throws IOException {
				System.out.println("onStartAsync called");
			}
		});
		// ExecutorService exec1 = Executors.newCachedThreadPool();

		// exec1.shutdown();
		// spawn some task in a background thread
		ClientThread clientThread = new ClientThread(ctx,msg);
		ctx.start(clientThread);

	}
	public static boolean addIUser(HttpServletRequest request, HttpServletResponse response) {
		for (IUser user : userArr) {
			if (user.getRequest() == request) {
				System.out.println("fehler");
				return false;
			}
		}
		IUser user = new User();
		user.setRequest(request);
		user.setResponse(response);
		userArr.add(user);
		return true;
	}
	public static boolean broadcast(int msg) throws ServletException, IOException {
		System.out.println("Start broadcast");
		for (IUser user : userArr) {
			sendMsg(user,msg);
		}
		return true;
	}

}

/**
 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
 *      response)
 */
/*
 * protected void doGet(HttpServletRequest request, HttpServletResponse
 * response) throws ServletException, IOException { broadcast(6); } protected
 * void doPost(HttpServletRequest request, HttpServletResponse response) throws
 * ServletException, IOException {
 * 
 * }
 * 
 * public static void broadcast(final int messageId){
 * System.out.println("Start Broadcast: "+messageId); for (final
 * IUser user : userArr) { if (user.getRequest()!=
 * null && user.getResponse()!=null){ user.getAsyncCo().notify(); } }
 * } public static boolean addIUser(int clientId, HttpServletRequest
 * request, HttpServletResponse response) { for (IUser user :
 * userArr) { if(user.getRequest()==request){
 * System.out.println("fehler"); return false; } } IUser user =
 * new IUser(clientId,request,response); userArr.add(user);
 * return true; } public static boolean removeIUserById(int clientId)
 * { for (IUser user : userArr) {
 * if(user.getClientId()==clientId){ userArr.remove(user); return
 * true; } } return false; } public static boolean
 * removeIUserByRequest(HttpServletRequest request) { for
 * (IUser user : userArr) {
 * if(user.getRequest()==request){ userArr.remove(user); return
 * true; } } return false; } }
 */
