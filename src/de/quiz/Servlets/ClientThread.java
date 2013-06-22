package de.quiz.Servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
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
import de.quiz.ServiceManager.IService;
import de.quiz.ServiceManager.ServiceManager;
import de.quiz.User.IUser;
import de.quiz.UserManager.IUserManager;

class ClientThread implements Runnable {
	private AsyncContext ctx;
	private int messageId;
	ClientThread(AsyncContext ctx, int broadcastMsg){
		this.ctx = ctx;
		messageId = broadcastMsg;
	}
	public void run() {
			try {

				System.out.println("Send Broadcast to " + ctx.getRequest());
				ctx.getResponse().setContentType("text/event-stream");
				ctx.getResponse().setCharacterEncoding("UTF-8");
				PrintWriter out = ctx.getResponse().getWriter();
				if (messageId == 6) {
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
							out.write("data: \"score" + i  + "\": \""
									+ json.get("score" + i) + "\"");
						}
						if (json.has("id" + i)) {
							out.write(",\n");
							out.write("data: \"id" + i  + "\": \""
									+ json.get("id" + i) + "\"");
						}
					}
					out.write("\n");
					out.write("data: }\n\n");
					out.flush();
				} else if (messageId == 7) {
					// System.out.println(ServiceManager.getInstance().getService(Quiz.class).getPlayerList());
					// String catChanged =
					// Quiz.getInstance().getCurrentCatalog().getName();
					out.write("event: gameStartEvent\n");
					out.write("data: {\n");
					out.write("data: \"id\": 7 \n");
					out.write("data: }\n\n");
					out.flush();
				} else if (messageId == 5) {
					// System.out.println(ServiceManager.getInstance().getService(Quiz.class).getPlayerList());
					String catChanged = Quiz.getInstance().getCurrentCatalog()
							.getName();
					out.write("event: catalogChangeEvent\n");
					out.write("data: {\n");
					out.write("data: \"id\": 5 ,\n");
					out.write("data: \"filename\": \"" + catChanged + "\" \n");
					out.write("data: }\n\n");
					out.flush();
				} else if (messageId == 255) {
					out.write("event: errorEvent\n");
					out.write("data: {\n");
					out.write("data: \"id\": 255 ,\n");
					out.write("data: \"msg\":\"Angefragtes SSE existiert nicht\"\n");
					out.write("data: }\n\n");
					out.flush();
				}
				// ctx.getResponse().getWriter().write(
				// MessageFormat.format("<h1>Processing task in bgt_id:[{0}]</h1>",
				// Thread.currentThread().getId()));
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Problem processing task");
			}
			//ctx.complete();
//			try {
//				this.wait(6000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
	}
}
