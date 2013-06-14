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
import de.quiz.UserManager.IUserManager;

/**
 * Servlet implementation class PlayerServlet
 */
@WebServlet(description = "handles everything which has to do with players", urlPatterns = { "/SSEServlet" })
public class SSEServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static CopyOnWriteArrayList<ClientConnection> clientConArr = new CopyOnWriteArrayList<ClientConnection>();
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
		
		addClientConnection(req,res);
		System.out.println("Start broadcast");
		for (ClientConnection clientCon : clientConArr) {
			sendMsg(clientCon,6);
		}
	}

	public static void sendMsg(final ClientConnection clientCon , int msg)
			throws ServletException, IOException {
		// create the async context, otherwise getAsyncContext() will be
		// null
		HttpServletRequest req = clientCon.getRequest();
		HttpServletResponse res = clientCon.getResponse();
		PrintWriter out = res.getWriter();
		System.out.println("bla: "+out);
		System.out.println("response status: "+res.getStatus());
		final AsyncContext ctx;
		if(req.getAsyncContext()==null){
			ctx = req.startAsync();
			ctx.setTimeout(10000);
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
				clientConArr.remove(clientCon);
				
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
	public static boolean addClientConnection(HttpServletRequest request, HttpServletResponse response) {
		for (ClientConnection clientCon : clientConArr) {
			if (clientCon.getRequest() == request) {
				System.out.println("fehler");
				return false;
			}
		}
		ClientConnection clientCon = new ClientConnection(0, request,
				response);
		clientConArr.add(clientCon);
		return true;
	}
	public static boolean broadcast(int msg) throws ServletException, IOException {
		System.out.println("Start broadcast");
		for (ClientConnection clientCon : clientConArr) {
			sendMsg(clientCon,msg);
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
 * ClientConnection clientCon : clientConArr) { if (clientCon.getRequest()!=
 * null && clientCon.getResponse()!=null){ clientCon.getAsyncCo().notify(); } }
 * } public static boolean addClientConnection(int clientId, HttpServletRequest
 * request, HttpServletResponse response) { for (ClientConnection clientCon :
 * clientConArr) { if(clientCon.getRequest()==request){
 * System.out.println("fehler"); return false; } } ClientConnection clientCon =
 * new ClientConnection(clientId,request,response); clientConArr.add(clientCon);
 * return true; } public static boolean removeClientConnectionById(int clientId)
 * { for (ClientConnection clientCon : clientConArr) {
 * if(clientCon.getClientId()==clientId){ clientConArr.remove(clientCon); return
 * true; } } return false; } public static boolean
 * removeClientConnectionByRequest(HttpServletRequest request) { for
 * (ClientConnection clientCon : clientConArr) {
 * if(clientCon.getRequest()==request){ clientConArr.remove(clientCon); return
 * true; } } return false; } }
 */
