package de.quiz.Servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import de.fhwgt.quiz.application.Catalog;
import de.fhwgt.quiz.application.Quiz;
import de.fhwgt.quiz.loader.LoaderException;
import de.quiz.LoggingManager.ILoggingManager;
import de.quiz.ServiceManager.ServiceManager;

/**
 * Servlet implementation class CatalogServlet This servlet handles catalogs.
 * 
 * 
 * @author Patrick Na�
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
		// TODO Auto-generated method stub

		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException {
		String sc ="";

		if(request.getParameter("rID")!=null){
			sc = request.getParameter("rID");		
		}
		
		if (sc.equals("3")) {
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			JSONObject json = new JSONObject(this.getCatalogList());
			out.print(json);
			//ServiceManager.getInstance().getService(ILoggingManager.class).log("Send CatalogList");
		}

	}

	/**
	 * Returns a map object with available catalogs.
	 * 
	 * @return map <String, Catalog>
	 */
	protected Map<String, Catalog> getCatalogList() {
		try {
			return Quiz.getInstance().getCatalogList();
		} catch (LoaderException e) {
			ServiceManager.getInstance().getService(ILoggingManager.class)
					.log(this, "Failed fetching catalog list");
			return null;
		}

	}
	
	/**
	 * Returns a catalog with given name
	 * 
	 * @param String Name of the catalog.
	 * @return Catalog Catalog object or null.
	 */
	protected Catalog getCatalogByName (String name) {
		try {
			return Quiz.getInstance().getCatalogByName(name);
		} catch (LoaderException e) {
			return null;
		}
	}
	
	
}
