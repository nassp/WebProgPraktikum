package de.quiz.Servlets;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletException;
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
 * in game process.
 * 
 * @author Patrick Na§
 */
@WebServlet(description = "connection to game the logic", urlPatterns = { "/LogicServlet" })
public class LogicServlet extends WebSocketServlet {
	private static final long serialVersionUID = 1L;
	private static CopyOnWriteArrayList<LogicMessageInbound> myInList = new CopyOnWriteArrayList<LogicMessageInbound>();
	private final AtomicInteger connectionIds = new AtomicInteger(0);
	private boolean superUserGone = false;
	private boolean gameStarted = false;

	@Override
	protected StreamInbound createWebSocketInbound(String arg0,
			HttpServletRequest arg1) {
		return new LogicMessageInbound(connectionIds.incrementAndGet(),
				arg1.getSession());
	}

	private class LogicMessageInbound extends MessageInbound {
		private WsOutbound myOutbound;

		private Question currentQuestion;

		private final int playerID;
		private final HttpSession playerSession;
		private boolean superUser = false;

		private LogicMessageInbound(int id, HttpSession session) {
			this.playerID = id;
			this.playerSession = session;
		}

		@Override
		protected void onClose(int status) {
			if(status!=255){
				if ((superUser) || (myInList.size()<3 && gameStarted)) {
					broadcast(255);
					gameStarted=false;
				}
			}
			ServiceManager.getInstance().getService(IUserManager.class)
					.removeActiveUser(this.getUserObject());

			if (this.getUserObject() != null)
				this.getUserObject().setWSID(-1);
			myInList.remove(this);
			if (myInList.size() < 1) {
				// alle Aktiven User aus Liste löschen
				ServiceManager.getInstance().getService(IUserManager.class)
						.removeAllActiveUser();
				gameStarted=false;
			}
			ServiceManager.getInstance().getService(ILoggingManager.class)
					.log("Login client closed. PlayerID: " + (playerID - 1));
		}

		@Override
		protected void onOpen(WsOutbound outbound) {

			this.myOutbound = outbound;

			ServiceManager.getInstance().getService(IUserManager.class)
					.getUserBySession(playerSession).setWSID(playerID);
			myInList.add(this);
			ServiceManager.getInstance().getService(ILoggingManager.class)
					.log("Login client open.");

			ServiceManager.getInstance().getService(ILoggingManager.class)
					.log("Login client open. " + playerID);

			if (getUserObject().getPlayerObject().isSuperuser()) {
				superUser = true;
				System.out.println("SuperUser ist true!");
			}
		}

		@Override
		protected void onBinaryMessage(ByteBuffer arg0) throws IOException {
			// this application does not expect binary data
			throw new UnsupportedOperationException(
					"Binary message not supported.");
		}

		@Override
		protected void onTextMessage(CharBuffer arg0) throws IOException {
			String jsonString = arg0.toString();
			JSONObject cases = null;
			try {
				cases = new JSONObject(jsonString);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			System.out.println(cases);

			try {
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
		 * for future use if a broadcast should be necessary
		 * 
		 * @param message
		 */
		private void broadcast(int msgID) {
			for (LogicMessageInbound connection : myInList) {
				try {
					if (connection.getUserObject() != null) {
						int ranking = ServiceManager
								.getInstance()
								.getService(IUserManager.class)
								.getRankingForPlayer(
										connection.getUserObject()
												.getPlayerObject());
						String meins = "";
						if (msgID == 12) {
							meins = "{\"id\": \"12\", \"ranking\": \""
									+ ranking + "\"}";
							gameStarted=false;
						} else if (msgID == 255){
							meins = "{\"id\": \"255\", \"message\": \"Es sind nicht mehr genügend Spieler vorhanden oder der Superuser hat sich abgemeldet. Das Spiel wird abgebrochen.\"}";
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
		 * Returns the user object which belongs to this MessageInbound
		 * 
		 * @return IUser
		 */
		public IUser getUserObject() {
			return ServiceManager.getInstance().getService(IUserManager.class)
					.getUserByWSID(playerID);
		}

		private void tooFewPlayers() {

			if (myInList.size() < 3) {
				for (LogicMessageInbound connection : myInList) {
					try {

						// Hier muss eine 3 stehen da die Funktion ja im
						// sich
						// schließenden Client (der noch mitgezählt wird)
						// aufgerufen
						// wird.

						if (connection.getUserObject() != null) {
							String meins = "{\"id\": \"255\", \"message\": \"Es sind nicht mehr genügend Spieler vorhanden. Das Spiel wird abgebrochen.\"}";
							CharBuffer buffer = CharBuffer.wrap(meins);
							connection.getWsOutbound().writeTextMessage(buffer);
						}

					} catch (IOException ignore) {
						// Ignore
					}
				}

			}
		}

		private void superUserLeft() {
			if (!superUserGone) {
				for (LogicMessageInbound connection : myInList) {
					try {
						String meins = "{\"id\": \"255\", \"message\": \"Der Spielleiter hat das Spiel verlassen. Bitte melden sie sich erneut an.\"}";
						CharBuffer buffer = CharBuffer.wrap(meins);
						connection.getWsOutbound().writeTextMessage(buffer);
					} catch (IOException ignore) {
						// Ignore
					}
					ServiceManager.getInstance().getService(IUserManager.class)
							.removeActiveUser(connection.getUserObject());
					connection.getUserObject().setWSID(-1);
					myInList.remove(connection);
					ServiceManager
							.getInstance()
							.getService(ILoggingManager.class)
							.log("Login client closed. PlayerID: "
									+ (playerID - 1));

				}
				superUserGone = true;
			}
		}

		private void onCase8() {
			gameStarted = true;
			QuizError error = new QuizError();
			TimeOut t = new TimeOut(this.myOutbound, this.getUserObject()
					.getPlayerObject());
			currentQuestion = Quiz.getInstance().requestQuestion(
					this.getUserObject().getPlayerObject(), t, error);
			if (currentQuestion != null) {

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

				System.out.println(this.getUserObject().getPlayerObject()
						.getName()
						+ ": Frage wurde versendet!");

			} else {

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

				for (Player p : Quiz.getInstance().getPlayerList()) {
					if (!p.isDone()) {
						return;
					}
				}

				broadcast(12);
			}
		}

		private void onCase10(String answer) {

			System.out.println("Case: 10");
			QuizError error = new QuizError();
			Quiz.getInstance().answerQuestion(
					this.getUserObject().getPlayerObject(), new Long(answer),
					error);
			System.out.println("Frage beantworten klappt!");
			SSEServlet.broadcast(6);

			System.out.println("SSE Broadcast klappt!");
			if (currentQuestion != null) {
				System.out.println("CurrentQuestion ist nicht null!");
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
			} else {
				System.out.println("Wie kommt das?");
			}

		}

	}

	/**
	 * Returns the stream object found with the given user id
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
