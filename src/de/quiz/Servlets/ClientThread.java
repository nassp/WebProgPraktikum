package de.quiz.Servlets;

import java.io.PrintWriter;

import javax.servlet.AsyncContext;

import org.json.JSONObject;

import de.fhwgt.quiz.application.Quiz;
import de.quiz.ServiceManager.ServiceManager;
import de.quiz.UserManager.IUserManager;

class ClientThread implements Runnable {
	private AsyncContext ctx;
	private int messageId;

	ClientThread(AsyncContext ctx, int broadcastMsg) {
		this.ctx = ctx;
		messageId = broadcastMsg;
	}

	public void run() {
		try {

			ctx.getResponse().setContentType("text/event-stream");
			ctx.getResponse().setCharacterEncoding("UTF-8");
			PrintWriter out = ctx.getResponse().getWriter();
			if (messageId == 6 || messageId == 65) {
				JSONObject json = ServiceManager.getInstance()
						.getService(IUserManager.class).getPlayerList();
				int i = 0;
				out.write("event: playerListEvent\n");
				out.write("data: {\n");
				out.write("data: \"id\": 6");
				for (i = 0; i < 6; i++) {
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
			} else if (messageId == 7) {
				out.write("event: gameStartEvent\n");
				out.write("data: {\n");
				out.write("data: \"id\": 7 \n");
				out.write("data: }\n\n");
			} else if (messageId == 255) {
				out.write("event: errorEvent\n");
				out.write("data: {\n");
				out.write("data: \"id\": 255 ,\n");
				out.write("data: \"msg\":\"Angefragtes SSE existiert nicht\"\n");
				out.write("data: }\n\n");
			}
			if (messageId == 5 || messageId == 65) {
				if (Quiz.getInstance().getCurrentCatalog() != null) {
					String catChanged = Quiz.getInstance().getCurrentCatalog()
							.getName();
					out.write("event: catalogChangeEvent\n");
					out.write("data: {\n");
					out.write("data: \"id\": 5 ,\n");
					out.write("data: \"filename\": \"" + catChanged + "\" \n");
					out.write("data: }\n\n");
					System.out.println("catChanged: " + catChanged);
				}
			}
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Problem processing task");
		}
	}
}
