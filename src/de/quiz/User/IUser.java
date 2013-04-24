package de.quiz.User;


import javax.servlet.http.HttpSession;



/**
 * this class is the interface for the UserConfig
 * 
 * @author Patrick Na§
 */
public interface IUser {
    
    /*##################################
     * setter
     *##################################*/
    
    /**
     * set user id
     * 
     * @param id	the user's id/alias
     */
    public void setID(String id);
    
    
    /**
     * set user session
     * 
     * @param session	the user's session
     */
    public void setSession(HttpSession session);
    
    /**
     * set the name
     * 
     * @param	_firstname	the user's name
     */
    public void setName (String _firstname);
    
    
    /*##################################
     * getter
     *##################################*/
    

    
    /**
     * getter for session
     */
    public HttpSession getSession(); 
    
    /**
     * getter for the user ID
     * 
     */
    public String getUserID ();
    
    /**
     * getter for the user's name
     * 
     */
    public String getName ();
    
    
    
    /**
     * checks if two user objects have the same ID
     */
    public boolean hasSameID(Object object);
}
