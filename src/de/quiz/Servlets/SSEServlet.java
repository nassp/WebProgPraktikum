package de.quiz.Servlets;

import java.io.IOException;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.quiz.ServiceManager.ServiceManager;
import de.quiz.User.IUser;
import de.quiz.UserManager.IUserManager;
import de.quiz.Utility.ClientThread;

/**
 * Servlet implementation class SSEServlet
 */
@WebServlet(description = "handles broadcasts which are event-triggered", urlPatterns = { "/SSEServlet" })
public class SSEServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

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
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		// add player to broadcast-clients (userArr)
		try {
			addIUser(req.getParameter("uID"), req, res);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// broadcast Playerlist and CatalogChange
		SSEServlet.broadcast(65);
	}

	/**
	 * create clientThread to send a message to user
	 * 
	 * @param user
	 *            the user to whom the message should be sent
	 * @param msg
	 *            the ID of the message to send
	 * @throws ServletException
	 * @throws IOException
	 */
	private static void sendMsg(final IUser user, int msg)
			throws ServletException, IOException {

		final AsyncContext ctx;

		// Check if AsyncContext already started
		if (user.getRequest().getAsyncContext() == null) {
			// create new AsyncContext for user
			ctx = user.getRequest().startAsync();
			ctx.setTimeout(0);
		} else {
			// get AsyncContext for user
			ctx = user.getRequest().getAsyncContext();
		}

		// spawn broadcast task in a background thread
		ClientThread clientThread = new ClientThread(ctx, msg);
		ctx.start(clientThread);
	}

	/**
	 * add request and response to user with given ID in userlist
	 * 
	 * @param userID
	 *            the user ID to create the user with
	 * @param request
	 *            the user request to create the user with
	 * @param response
	 *            the user response to create the user with
	 * @return boolean true if user wasn't already in user array, false
	 *         otherwise
	 * @throws Exception
	 */
	private static boolean addIUser(String userID, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// get IUser with given ID from IUserManager
		IUser user = ServiceManager.getInstance()
				.getService(IUserManager.class).getUserById(userID);
		// add request and response if not already set
		if (user.getRequest() == null || user.getResponse() == null) {
			user.setRequest(request);
			user.setResponse(response);
			return true;
		}
		return false;
	}

	/**
	 * broadcast a message with given ID to all users in user array
	 * 
	 * @param msg
	 *            the message which should be broadcasted
	 */
	public static void broadcast(int msg) {
		// send Message to all users in user list
		for (IUser user : ServiceManager.getInstance()
				.getService(IUserManager.class).getUserList()) {
			try {
				if (user.getRequest() != null) {
					sendMsg(user, msg);
				}
			} catch (ServletException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}