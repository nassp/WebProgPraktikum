package de.quiz.Listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import de.fhwgt.quiz.application.Quiz;
import de.fhwgt.quiz.loader.FilesystemLoader;
import de.fhwgt.quiz.loader.LoaderException;
import de.quiz.LoggingManager.ILoggingManager;
import de.quiz.ServiceManager.ServiceManager;

/**
 * Application Lifecycle Listener implementation class
 * CatalogServletContextListener
 * 
 * @author Patrick Na§
 */
@WebListener
public class CatalogServletContextListener implements ServletContextListener {

	/**
	 * Default constructor.
	 */
	public CatalogServletContextListener() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent arg0) {

		FilesystemLoader loader = new FilesystemLoader("/de/quiz/Fragenkataloge/");

		try {
			Quiz.getInstance().initCatalogLoader(loader);
			// test if fail (initCatalogLoader doesn't throw anything)
			Quiz.getInstance().getCatalogList();

			ServiceManager.getInstance().getService(ILoggingManager.class)
					.log(this, "Instantiation of FileSystemLoader: success");
		} catch (LoaderException e) {
			ServiceManager.getInstance().getService(ILoggingManager.class)
					.log(this, "Instantiation of FileSystemLoader: fail");
		}

	}

	/**
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
	}

}
