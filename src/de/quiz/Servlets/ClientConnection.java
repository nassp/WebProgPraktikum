package de.quiz.Servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;

import de.fhwgt.quiz.application.Quiz;
import de.fhwgt.quiz.error.QuizError;
import de.quiz.LoggingManager.ILoggingManager;
import de.quiz.ServiceManager.ServiceManager;
import de.quiz.User.IUser;
import de.quiz.UserManager.IUserManager;

/**
 * Container for saving Client response, requests and IDs
 */

public class ClientConnection {
	//private static final long serialVersionUID = 1L;
	private int clientId;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private AsyncContext asyncCo;
	
	public ClientConnection(int clientIdP,HttpServletRequest requestP, HttpServletResponse responseP) {
		clientId = clientIdP;
		request = requestP;
		response = responseP;
		asyncCo = null;
	}
	public void setAsyncCo(AsyncContext asyncCoP){
		asyncCo = asyncCoP;
		return;
	}
	public AsyncContext getAsyncCo(){
		return asyncCo;
	}
	public HttpServletRequest getRequest() {
		return request;
	}
	public HttpServletResponse getResponse() {
		return response;
	}
	public int getClientId() {
		return clientId;
	}

}
