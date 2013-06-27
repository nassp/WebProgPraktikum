package de.quiz.Utility;

import java.io.PrintWriter;

import javax.servlet.AsyncContext;

import org.json.JSONObject;

import de.fhwgt.quiz.application.Quiz;
import de.quiz.ServiceManager.ServiceManager;
import de.quiz.UserManager.IUserManager;

/**
 * Thread to send a message to a player. 
 * 
 * @author Harry Strauch
 */
public class ClientThread implements Runnable {
	private AsyncContext ctx;
	private int messageId;
	
	/**
	 * constructor for setting the AsyncContext and broadcast Message
	 * 
	 * @param ctx
	 *            the user to whom the message should be sent
	 * @param messageId
	 *            the ID of the broadcast to send
	 */
	public ClientThread(AsyncContext ctx, int broadcastMsg) {
		this.ctx = ctx;
		messageId = broadcastMsg;
	}

	/**
	 * @see Runnable#run()
	 */
	public void run() {
		try {
			// set response type and encoding
			ctx.getResponse().setContentType("text/event-stream");
			ctx.getResponse().setCharacterEncoding("UTF-8");
			// get the output writer
			PrintWriter out = ctx.getResponse().getWriter();
			
			// playerlist update without or with the catalogChangeEvent (for initialization of players)
			if (messageId == 6 || messageId == 65) {
				// get the playerlist
				JSONObject json = ServiceManager.getInstance()
						.getService(IUserManager.class).getPlayerList();
				int i = 0;
				// name the event and write the data as stringyfied JSON
				out.write("event: playerListEvent\n");
				out.write("data: {\n");
				out.write("data: \"id\": 6");
				// go through playerlist and get name, score and id of each player
				for (i = 1; i <= 6; i++) {
					if (json.has("name" + i)) {
						out.write(",\n");
						out.write("data: \"name" + i + "\": \""
								+ json.get("name" + i) + "\"");
					}
					if (json.has("score" + i)) {
						out.write(",\n");
						out.write("data: \"score" + i + "\": \""
								+ json.get("score" + i) + "\"");
					}
					if (json.has("id" + i)) {
						out.write(",\n");
						out.write("data: \"id" + i + "\": \""
								+ json.get("id" + i) + "\"");
					}
				}
				out.write("\n");
				out.write("data: }\n\n");
			} 
			// send gameStart to start the game
			else if (messageId == 7) {
				out.write("event: gameStartEvent\n");
				out.write("data: {\n");
				out.write("data: \"id\": 7 \n");
				out.write("data: }\n\n");
			} 
			// send error to inform client (not yet used)
			else if (messageId == 255) {
				out.write("event: errorEvent\n");
				out.write("data: {\n");
				out.write("data: \"id\": 255 ,\n");
				out.write("data: \"msg\":\"Angefragtes SSE existiert nicht\"\n");
				out.write("data: }\n\n");
			}
			// catalogChange update without or with the playerListEvent (for initialization of players)
			if (messageId == 5 || messageId == 65) {
				if (Quiz.getInstance().getCurrentCatalog() != null) {
					String catChanged = Quiz.getInstance().getCurrentCatalog()
							.getName();
					out.write("event: catalogChangeEvent\n");
					out.write("data: {\n");
					out.write("data: \"id\": 5 ,\n");
					out.write("data: \"filename\": \"" + catChanged + "\" \n");
					out.write("data: }\n\n");
				}
			}
			// flushes the stream to get the written events to the client
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
