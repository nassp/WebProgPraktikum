package de.quiz.LoggingManager;

import java.util.List;

import de.fhwgt.quiz.error.QuizError;
import de.quiz.ServiceManager.IService;

/**
 * this service does some basic logging, which can be reach with the browser
 * via "log.jsp"
 *
 * @author Patrick Na§
 */
public interface ILoggingManager extends IService {
    
    /**
     * log a string message
     * 
     * @param clazz	the class which invoked this call
     * @param string	the message we want to log
     */
    void log(Object clazz, String string);
    
    /**
     * log an execption message
     * 
     * @param clazz	the class which invoked this call
     * @param e		the exception we convert
     */
    void log(Object clazz, Exception e);
    
    /**
     * log a string message without needed class
     * 
     * @param string	the message we want to log
     */
    public void log(String string);
    
    /**
     * get a list of stored logs
     * 
     * @return	a list of log messages
     */
    public List<String> getLogContainer();
    
    public void log(Object clazz, QuizError e);
}
