package de.quiz.Servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
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
	//private static CopyOnWriteArrayList<AsyncContext> asyncArr = new CopyOnWriteArrayList<AsyncContext>();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SSEServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		broadcast(6);
	}
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
	}

	public static void broadcast(final int messageId){
		System.out.println("Start Broadcast: "+messageId);
		//final Asyn
		final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);

		
		//executorService.scheduleWithFixedDelay(new ServerSendEvent(asyncContext.getResponse()), 0, 2,TimeUnit.SECONDS)
		for (final ClientConnection clientCon : clientConArr) {
			if (clientCon.getRequest()!= null && clientCon.getResponse()!=null){
				HttpServletRequest request = clientCon.getRequest();
				HttpServletResponse response = clientCon.getResponse();
				AsyncContext asyncTmp = clientCon.getAsyncCo();
				if(asyncTmp ==null){
					try{
						asyncTmp =  request.startAsync(request, response);
						asyncTmp.setTimeout(0); 
						//clientCon.setAsyncCo(asyncTmp);
					} catch (NullPointerException e) {
						e.printStackTrace();
						asyncTmp = clientCon.getAsyncCo();
					}
				}
				final AsyncContext ctx = asyncTmp;
				
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
						//ctx.complete();
						return;
					}
					
				});
				//asyncTmp.complete();
				executorService.shutdown();
			}
		}
	}
	public static boolean addClientConnection(int clientId, HttpServletRequest request, HttpServletResponse response) {
		for (ClientConnection clientCon : clientConArr) {
			if(clientCon.getRequest()==request){
				return false;
			}
		}
		ClientConnection clientCon = new ClientConnection(clientId,request,response);
		clientConArr.add(clientCon);
		return true;
	}
	public static boolean removeClientConnectionById(int clientId) {
		for (ClientConnection clientCon : clientConArr) {
			if(clientCon.getClientId()==clientId){
				clientConArr.remove(clientCon);
				return true;
			}
		}
		return false;
	}
	public static boolean removeClientConnectionByRequest(HttpServletRequest request) {
		for (ClientConnection clientCon : clientConArr) {
			if(clientCon.getRequest()==request){
				clientConArr.remove(clientCon);
				return true;
			}
		}
		return false;
	}
}
