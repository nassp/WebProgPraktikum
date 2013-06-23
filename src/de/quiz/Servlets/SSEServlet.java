package de.quiz.Servlets;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.quiz.User.IUser;
import de.quiz.User.User;

/**
 * Servlet implementation class SSEServlet
 */
@WebServlet(description = "handles everything which has to do with players", urlPatterns = { "/SSEServlet" })
public class SSEServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	// user array to save user connections
	private static CopyOnWriteArrayList<IUser> userArr = new CopyOnWriteArrayList<IUser>();

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
		addIUser(req.getParameter("uID"), req, res);

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
	 * create and add user to user array
	 * 
	 * @param userID
	 *            the user ID to create the user with
	 * @param request
	 *            the user request to create the user with
	 * @param response
	 *            the user response to create the user with
	 * @return boolean true if user wasn't already in user array, false
	 *         otherwise
	 */
	private static boolean addIUser(String userID, HttpServletRequest request,
			HttpServletResponse response) {
		// check if user request or user ID is already in user array
		for (IUser user : userArr) {
			if (user.getRequest() == request || user.getUserID() == userID) {
				return false;
			}
		}

		// create new user
		IUser user = new User();
		user.setID(userID);
		user.setRequest(request);
		user.setResponse(response);

		// add user to user array
		userArr.add(user);

		return true;
	}

	/**
	 * delete user with given ID from user array
	 * 
	 * @param userID
	 *            the ID of the user who should be deleted
	 * @return boolean true if user with given ID was in user array, false
	 *         otherwise
	 */
	public static boolean removeIUser(String userID) {
		for (int i = 0; i < userArr.size(); i++) {
			if (userArr.get(i) != null) {
				if (userArr.get(i).getUserID() == userID) {
					userArr.remove(i);

					// refresh client playerlist
					broadcast(6);
					return true;
				}
			}
		}
		// false if user with given ID not in user array
		return false;
	}

	/**
	 * clear the user array (use for game reset)
	 */
	public static void clearUserArr() {
		userArr.clear();
	}

	/**
	 * broadcast a message with given ID to all users in user array
	 * 
	 * @param msg
	 *            the message which should be broadcasted
	 */
	public static void broadcast(int msg) {
		// send Message to all users in user array
		for (IUser user : userArr) {
			try {
				sendMsg(user, msg);
			} catch (ServletException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}