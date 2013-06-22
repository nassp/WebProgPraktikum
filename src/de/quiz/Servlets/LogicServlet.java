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

		private LogicMessageInbound(int id, HttpSession session) {
			this.playerID = id;
			this.playerSession = session;
		}

		@Override
		protected void onClose(int status) {
			IUser tmp = this.getUserObject();
			if (tmp != null)
				this.getUserObject().setWSID(-1);
			myInList.remove(this);
			ServiceManager.getInstance().getService(ILoggingManager.class)
					.log("Login client closed.");
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
		}

		@Override
		protected void onBinaryMessage(ByteBuffer arg0) throws IOException {
			// this application does not expect binary data
			throw new UnsupportedOperationException(
					"Binary message not supported.");
		}

		@Override
		protected void onTextMessage(CharBuffer arg0) throws IOException {

			System.out.println(this.getUserObject().getPlayerObject().getName()
					+ "LogicServlet: Case: " + arg0.toString());

			if (arg0.toString().equals("8")) {
				this.onCase8();
			}

			else if (arg0.toString().equals("100")) {
				this.onCase10("0");
			}

			else if (arg0.toString().equals("101")) {
				this.onCase10("1");
			}

			else if (arg0.toString().equals("102")) {
				this.onCase10("2");
			}

			else if (arg0.toString().equals("103")) {
				this.onCase10("3");
			}

		}

		/**
		 * for future use if a broadcast should be necessary
		 * 
		 * @param message
		 */
		private void broadcastGameEnd() {
			for (LogicMessageInbound connection : myInList) {
				try {
					int ranking = ServiceManager
							.getInstance()
							.getService(IUserManager.class)
							.getRankingForPlayer(
									connection.getUserObject()
											.getPlayerObject());
					String meins = "{\"id\": \"12\", \"ranking\": \"" + ranking
							+ "\"}";

					CharBuffer buffer = CharBuffer.wrap(meins);
					connection.getWsOutbound().writeTextMessage(buffer);
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

		private void onCase8() {
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

				// CharBuffer buffer = CharBuffer.wrap("9");
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
				System.out.println("Unerwarteter Fehler!");
				Quiz.getInstance().setDone(
						this.getUserObject().getPlayerObject());

				for (Player p : Quiz.getInstance().getPlayerList()) {
					if (!p.isDone()) {
						return;
					} else {
						broadcastGameEnd();
					}
				}
			}
		}

		private void onCase10(String answer) {

			System.out.println("Case: 10");
			QuizError error = new QuizError();
			Quiz.getInstance().answerQuestion(
					this.getUserObject().getPlayerObject(), new Long(answer),
					error);
			try {
				SSEServlet.broadcast(6);
			} catch (ServletException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
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
