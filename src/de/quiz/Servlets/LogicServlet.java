package de.quiz.Servlets;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.ArrayList;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;

import de.fhwgt.quiz.application.Player;
import de.fhwgt.quiz.application.Quiz;
import de.fhwgt.quiz.error.QuizError;
import de.quiz.LoggingManager.ILoggingManager;
import de.quiz.ServiceManager.ServiceManager;

/**
 * WebSocketServlet implementation class PlayerServlet. 
 * This servlet handles the login process and the integration of the game logic.
 * 
 * @author Patrick Na§
 */
@WebServlet(description = "connection to game the logic", urlPatterns = { "/LogicServlet" })
public class LogicServlet extends WebSocketServlet {
	private static final long serialVersionUID = 1L;
	private static ArrayList<LoginMessageInbound> mmiList = new ArrayList<LoginMessageInbound>();

	@Override
	protected StreamInbound createWebSocketInbound(String arg0,
			HttpServletRequest arg1) {
		return new LoginMessageInbound();
	}

	private class LoginMessageInbound extends MessageInbound {
		private WsOutbound loginOutbound;

		@Override
		protected void onClose(int status) {
			ServiceManager.getInstance().getService(ILoggingManager.class)
					.log("Login client closed.");
			mmiList.remove(this);
		}

		@Override
		protected void onOpen(WsOutbound outbound) {
			try {
				this.loginOutbound = outbound;
				mmiList.add(this);
				outbound.writeTextMessage(CharBuffer.wrap("Hello world!"));
				ServiceManager.getInstance().getService(ILoggingManager.class)
						.log("Login client open.");
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
			ServiceManager.getInstance().getService(ILoggingManager.class)
			.log("Accept Message : "+ arg0);
			for (LoginMessageInbound mmib : mmiList) {
				CharBuffer buffer = CharBuffer.wrap(arg0);
				mmib.loginOutbound.writeTextMessage(buffer);
				mmib.loginOutbound.flush();
			}
		}


	}



}
