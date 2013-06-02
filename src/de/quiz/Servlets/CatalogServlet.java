package de.quiz.Servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import de.fhwgt.quiz.application.Catalog;
import de.fhwgt.quiz.application.Quiz;
import de.fhwgt.quiz.loader.LoaderException;
import de.quiz.LoggingManager.ILoggingManager;
import de.quiz.ServiceManager.ServiceManager;

/**
 * Servlet implementation class CatalogServlet. This servlet handles catalogs.
 * 
 * 
 * @author Patrick Naß
 */
@WebServlet(description = "managing of catalogs", urlPatterns = { "/CatalogServlet" })
public class CatalogServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CatalogServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		ServiceManager.getInstance().getService(ILoggingManager.class)
				.log(this, "GET is not supported on this Servlet");
		response.getWriter().print("GET is not supported on this Servlet");

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String sc = "";

		if (request.getParameter("rID") != null) {
			sc = request.getParameter("rID");
		}

		// catalog request
		if (sc.equals("3")) {
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			try {
				Map<String, Catalog> catalogList = Quiz.getInstance()
						.getCatalogList();
				JSONObject answer = new JSONObject(catalogList);
				answer.put("id", 200);
				out.print(answer);

			} catch (LoaderException e3) {

				JSONObject error = new JSONObject();
				try {
					error.put("id", 255);
					ServiceManager.getInstance()
							.getService(ILoggingManager.class)
							.log("Failed sending catalog request!");

				} catch (JSONException e1) {

					try {
						error.put("id", 255);
					} catch (JSONException e2) {
						ServiceManager.getInstance()
								.getService(ILoggingManager.class)
								.log("Failed sending catalog request error!");
					}
					out.print(error);
				}
			} catch (JSONException e) {

				JSONObject error = new JSONObject();
				try {
					error.put("id", 255);
					ServiceManager.getInstance()
							.getService(ILoggingManager.class)
							.log("Failed sending catalog request!");

				} catch (JSONException e1) {

					try {
						error.put("id", 255);
					} catch (JSONException e2) {
						ServiceManager.getInstance()
								.getService(ILoggingManager.class)
								.log("Failed sending catalog request error!");
					}
					out.print(error);
				}
			}

		}

		// TODO: muss über server sent events laufen und muss gefüllt werden
		// catalog change
		if (sc.equals("5")) {
//			Quiz.getInstance().changeCatalog(player, catalogName, error)
		}

	}

}
