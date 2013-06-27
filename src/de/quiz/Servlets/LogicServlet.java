package de.quiz.Servlets;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;
import org.json.JSONException;
import org.json.JSONObject;

import de.fhwgt.quiz.application.Player;
import de.fhwgt.quiz.application.Question;
import de.fhwgt.quiz.application.Quiz;
import de.fhwgt.quiz.error.QuizError;
import de.quiz.LoggingManager.ILoggingManager;
import de.quiz.ServiceManager.ServiceManager;
import de.quiz.User.IUser;
import de.quiz.UserManager.IUserManager;
import de.quiz.Utility.TimeOut;

/**
 * WebSocketServlet implementation class LogicServlet. This servlet handles the
 * ingame process.
 * 
 * @author Patrick Na§
 */
@WebServlet(description = "handles the ingame process", urlPatterns = { "/LogicServlet" })
public class LogicServlet extends WebSocketServlet {
	private static final long serialVersionUID = 1L;
	private static CopyOnWriteArrayList<LogicMessageInbound> myInList = new CopyOnWriteArrayList<LogicMessageInbound>();
	private final AtomicInteger connectionIds = new AtomicInteger(0);
	private boolean gameStarted = false;

	@Override
	protected StreamInbound createWebSocketInbound(String arg0,
			HttpServletRequest arg1) {
		return new LogicMessageInbound(connectionIds.incrementAndGet(),
				arg1.getSession());
	}

	/**
	 * Implements the MessageInbound. (connection of sockets and textmessaging)
	 * 
	 * @author Patrick Na§
	 */
	private class LogicMessageInbound extends MessageInbound {
		private WsOutbound myOutbound;

		private Question currentQuestion;

		private final int playerID;
		private final HttpSession playerSession;
		private boolean superUser = false;

		// constructor
		private LogicMessageInbound(int id, HttpSession session) {
			this.playerID = id;
			this.playerSession = session;
		}

		/**
		 * if the websocket is closed checks whether the superuser left or if
		 * too few players are left to play and then sends messages. After that
		 * the player is remove from the playerlist.
		 * 
		 * @param status
		 */
		@Override
		protected void onClose(int status) {
			// If the socket is not closed because of the superuser or too few
			// players. Checks if it is the superuser or too few players and
			// sends a broadcast. But only if the game has started.
			if (status != 255) {

				// superuser or too few Players
				if ((superUser) || (myInList.size() < 3 && gameStarted)) {
					broadcast(255);
					gameStarted = false;
				}
			}
			ServiceManager.getInstance().getService(IUserManager.class)
					.removeActiveUser(this.getUserObject());

			// if the userObject is not null deletes the player.
			if (this.getUserObject() != null)
				this.getUserObject().setWSID(-1);
			myInList.remove(this);

			if (myInList.size() < 1) {
				// delete all active players from the list.
				ServiceManager.getInstance().getService(IUserManager.class)
						.removeAllActiveUser();
				gameStarted = false;
			}
			ServiceManager.getInstance().getService(ILoggingManager.class)
					.log("Login client closed. PlayerID: " + (playerID - 1));
		}

		/**
		 * On opening of the websocket myOutbound is set to the WsOutbound. The
		 * WebsocketID is set and the player added to the list.
		 * 
		 * @param outbound
		 */
		@Override
		protected void onOpen(WsOutbound outbound) {

			this.myOutbound = outbound;

			// sets the websocketID an adds the player to the list.
			ServiceManager.getInstance().getService(IUserManager.class)
					.getUserBySession(playerSession).setWSID(playerID);
			myInList.add(this);

			ServiceManager.getInstance().getService(ILoggingManager.class)
					.log("Login client open with ID: " + playerID);

			// if player is the superUser sets the superUser boolean
			if (getUserObject().getPlayerObject().isSuperuser()) {
				superUser = true;
			}
		}

		/**
		 * not supported
		 * 
		 * @param arg0
		 */
		@Override
		protected void onBinaryMessage(ByteBuffer arg0) throws IOException {
			// this application does not expect binary data
			throw new UnsupportedOperationException(
					"Binary message not supported.");
		}

		/**
		 * on getting a text message converts it to a jsonString. Decides then
		 * the case and calls the respective function.
		 * 
		 * @param arg0
		 */
		@Override
		protected void onTextMessage(CharBuffer arg0) throws IOException {
			String jsonString = arg0.toString();
			JSONObject cases = null;
			try {
				cases = new JSONObject(jsonString);

				if (cases.getString("id").equals("8")) {
					this.onCase8();
				}

				else if (cases.getString("id").equals("10")) {
					this.onCase10(cases.getString("answered"));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		/**
		 * Broadcast method.
		 * 
		 * @param msgID
		 */
		private void broadcast(int msgID) {
			// goes through all connections in the playerlist
			for (LogicMessageInbound connection : myInList) {
				try {
					if (connection.getUserObject() != null) {
						// ranking of a player from the list
						int ranking = ServiceManager
								.getInstance()
								.getService(IUserManager.class)
								.getRankingForPlayer(
										connection.getUserObject()
												.getPlayerObject());
						String meins = "";
						// sends his ranking to the player
						if (msgID == 12) {
							meins = "{\"id\": \"12\", \"ranking\": \""
									+ ranking + "\"}";
							gameStarted = false;
							// on error sends a respective message to the
							// players
						} else if (msgID == 255) {
							meins = "{\"id\": \"255\", \"message\": \"Es sind nicht mehr genügend Spieler vorhanden oder der Superuser hat sich abgemeldet. Das Spiel wird abgebrochen.\"}";
							// if this is the current connection, closes the
							// connection in another way
							if (!(this.equals(connection))) {
								connection.onClose(255);
							}
						} else {
							meins = "{\"id\": \"255\", \"message\": \"undefined broadcast.\"}";
						}
						CharBuffer buffer = CharBuffer.wrap(meins);
						connection.getWsOutbound().writeTextMessage(buffer);
					}
				} catch (IOException ignore) {
					// Ignore
				}
			}
		}

		/**
		 * Returns the user object which belongs to this MessageInbound.
		 * 
		 * @return IUser
		 */
		public IUser getUserObject() {
			return ServiceManager.getInstance().getService(IUserManager.class)
					.getUserByWSID(playerID);
		}

		/**
		 * Method for requestQuestion (case8).
		 */
		private void onCase8() {
			gameStarted = true;
			QuizError error = new QuizError();
			// creates a timeout implementing the TimerTask
			TimeOut t = new TimeOut(this.myOutbound, this.getUserObject()
					.getPlayerObject());
			// sets the current Question
			currentQuestion = Quiz.getInstance().requestQuestion(
					this.getUserObject().getPlayerObject(), t, error);
			if (currentQuestion != null) {

				// sets the currentQuestion and index of the correct answer in
				// the timeout object
				t.setThisQuestion(currentQuestion);
				t.setIndex(currentQuestion.getCorrectIndex());

				long timeout = currentQuestion.getTimeout();
				String question = currentQuestion.getQuestion();
				String answer1 = currentQuestion.getAnswerList().get(0);
				String answer2 = currentQuestion.getAnswerList().get(1);
				String answer3 = currentQuestion.getAnswerList().get(2);
				String answer4 = currentQuestion.getAnswerList().get(3);

				String meins = "{\"id\": \"9\", \"timeout\": \"" + timeout
						+ "\", \"question\": \"" + question
						+ "\", \"answer1\": \"" + answer1
						+ "\", \"answer2\": \"" + answer2
						+ "\", \"answer3\": \"" + answer3
						+ "\", \"answer4\": \"" + answer4 + "\"}";
				CharBuffer buffer = CharBuffer.wrap(meins);

				try {
					this.myOutbound.writeTextMessage(buffer);
					this.myOutbound.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {

				// case 9 with everything set to zero signalises the end of the
				// catalog
				String meins = "{\"id\": \"9\", \"timeout\": \"" + 0
						+ "\", \"question\": \"" + 0 + "\", \"answer1\": \""
						+ 0 + "\", \"answer2\": \"" + 0 + "\", \"answer3\": \""
						+ 0 + "\", \"answer4\": \"" + 0 + "\"}";
				CharBuffer buffer = CharBuffer.wrap(meins);

				try {
					this.myOutbound.writeTextMessage(buffer);
					this.myOutbound.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				Quiz.getInstance().setDone(
						this.getUserObject().getPlayerObject());

				// if all players are done sends a broadcast with the end game
				// message
				for (Player p : Quiz.getInstance().getPlayerList()) {
					if (!p.isDone()) {
						return;
					}
				}

				broadcast(12);
			}
		}

		/**
		 * Method for answerQuestion (case10).
		 * 
		 * @param answer
		 *            the player's answer
		 */
		private void onCase10(String answer) {

			// answers the question and broadcasts the new playerlist
			QuizError error = new QuizError();
			Quiz.getInstance().answerQuestion(
					this.getUserObject().getPlayerObject(), new Long(answer),
					error);
			SSEServlet.broadcast(6);

			if (currentQuestion != null) {

				String meins = "{\"id\": \"11\", \"answer\": \""
						+ String.valueOf(currentQuestion.getCorrectIndex())
						+ "\"}";
				CharBuffer buffer = CharBuffer.wrap(meins);
				try {
					this.myOutbound.writeTextMessage(buffer);
					this.myOutbound.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}

	/**
	 * Returns the stream object found with the given user id.
	 * 
	 * @param id
	 * @return LogicMessageInbound
	 */
	public LogicMessageInbound getStreamByUserID(int id) {
		for (LogicMessageInbound connection : myInList) {
			if (connection.playerID == id)
				return connection;
		}
		return null;
	}

}
