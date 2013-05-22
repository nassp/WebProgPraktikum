package de.quiz.Servlets;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;

import de.quiz.LoggingManager.ILoggingManager;
import de.quiz.ServiceManager.ServiceManager;
import de.quiz.Utility.HTMLFilter;

/**
 * WebSocketServlet implementation class LogicServlet. This servlet handles the
 * in game process.
 * 
 * @author Patrick Na§
 */
@WebServlet(description = "connection to game the logic", urlPatterns = { "/LogicServlet" })
public class LogicServlet extends WebSocketServlet {
	private static final long serialVersionUID = 1L;
	private static ArrayList<LogicMessageInbound> myInList = new ArrayList<LogicMessageInbound>();
	private final AtomicInteger connectionIds = new AtomicInteger(0);

	@Override
	protected StreamInbound createWebSocketInbound(String arg0,
			HttpServletRequest arg1) {
		return new LogicMessageInbound(connectionIds.incrementAndGet());

	}

	private class LogicMessageInbound extends MessageInbound {
		private WsOutbound myOutbound;

		private final int playerID;


		private LogicMessageInbound(int id) {
			this.playerID = id;
		}

		@Override
		protected void onClose(int status) {
//			this.getUserObject().setWSID(-1);
			myInList.remove(this);
			ServiceManager.getInstance().getService(ILoggingManager.class)
					.log("Login client closed.");
		}

		@Override
		protected void onOpen(WsOutbound outbound) {
			try {
				this.myOutbound = outbound;
//				ServiceManager.getInstance().getService(IUserManager.class)
//				.getUserBySession(userSession).setWSID(playerID);
				myInList.add(this);
				outbound.writeTextMessage(CharBuffer
						.wrap("Connection successfully opened!"));
				ServiceManager.getInstance().getService(ILoggingManager.class)
						.log("Login client open.");
//				this.broadcast(this.getUserObject().getName() + " has joined the game!");
			} catch (IOException e) {
				ServiceManager.getInstance().getService(ILoggingManager.class)
						.log("Login client opening failed.");
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

			String filteredMessage = String.format("%s",
					HTMLFilter.filter(arg0.toString()));
			ServiceManager.getInstance().getService(ILoggingManager.class)
					.log("Accept Message : " + filteredMessage);
			CharBuffer buffer = CharBuffer.wrap(filteredMessage);
			this.myOutbound.writeTextMessage(buffer);
			this.myOutbound.flush();

		}

		/**
		 * for future use if a broadcast should be necessary
		 * 
		 * @param message
		 */
		private void broadcast(String message) {
			for (LogicMessageInbound connection : myInList) {
				try {
					CharBuffer buffer = CharBuffer.wrap(message);
					connection.getWsOutbound().writeTextMessage(buffer);
				} catch (IOException ignore) {
					// Ignore
				}
			}
		}

//		/**
//		 * Returns the user object which belongs to this MessageInbound
//		 * 
//		 * @return IUser
//		 */
//		private IUser getUserObject() {
//			return ServiceManager.getInstance().getService(IUserManager.class)
//					.getUserByWSID(playerID);
//		}

	}

}
