package de.quiz.LoggingManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.fhwgt.quiz.error.QuizError;

/**
 * this service does some basic logging, which can be reach with the browser via
 * "LOG.jsp"
 * 
 * @author Patrick Na§
 */
public class LoggingManager implements ILoggingManager {

	public LoggingManager() {
		super();
	}

	private List<String> logContainer = new ArrayList<String>();

	private String generateDateAndTimeInfo() {
		Calendar cal = Calendar.getInstance();
		cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("[EEE, d MMM HH:mm:ss] ");
		return sdf.format(cal.getTime());
	}

	/**
	 * log a string message
	 * 
	 * @param clazz
	 *            the class which invoked this call
	 * @param string
	 *            the message we want to log
	 */
	public void log(Object clazz, String string) {

		logContainer.add(generateDateAndTimeInfo()
				+ clazz.getClass().getSimpleName() + ": " + string);
	}

	/**
	 * log a string message without needed class
	 * 
	 * @param string
	 *            the message we want to log
	 */
	public void log(String string) {

		logContainer.add(generateDateAndTimeInfo() + ": " + string);
	}

	/**
	 * log an execption message
	 * 
	 * @param clazz
	 *            the class which invoked this call
	 * @param e
	 *            the exception we convert
	 */
	public void log(Object clazz, Exception e) {

		log(clazz, e.toString());
	}

	/**
	 * get a list of stored logs.
	 * 
	 * FIXME: either store in database or limit messages!
	 * 
	 * @return a list of log messages
	 */
	public List<String> getLogContainer() {

		return logContainer;
	}

	@Override
	public void log(Object clazz, QuizError e) {
		log(clazz, e.toString());
	}

}
